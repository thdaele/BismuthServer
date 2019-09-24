package si.bismuth.mixins;

import si.bismuth.utils.Profiler;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;

@Mixin(World.class)
public abstract class WorldMixin {
	@Shadow
	@Final
	public WorldProvider provider;
	@Shadow
	@Final
	public List<TileEntity> loadedTileEntityList;

	@Shadow
	public abstract boolean isBlockLoaded(BlockPos pos);

	@Shadow
	public abstract Chunk getChunk(BlockPos pos);

	private String worldName;
	private Iterator myIterator;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void setWorldName(ISaveHandler saveHandlerIn, WorldInfo info, WorldProvider providerIn, net.minecraft.profiler.Profiler profilerIn, boolean client, CallbackInfo ci) {
		this.worldName = this.provider.getDimensionType().getName();
	}

	@Inject(method = "updateEntities", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V", args = "ldc=remove"))
	private void onRemoveEntities(CallbackInfo ci) {
		Profiler.start_section(this.worldName, "entities");
	}

	@Inject(method = "updateEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getRidingEntity()Lnet/minecraft/entity/Entity;"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void onGetRidingEntity(CallbackInfo ci, int i, Entity entity) {
		Profiler.start_entity_section(this.worldName, entity);
	}

	@Inject(method = "updateEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;endSection()V", ordinal = 1))
	private void postUpdateEntities(CallbackInfo ci) {
		Profiler.end_current_entity_section();
	}

	@Inject(method = "updateEntities", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V", args = "ldc=blockEntities"))
	private void onStartBlockEntities(CallbackInfo ci) {
		Profiler.end_current_section();
		Profiler.start_section(this.worldName, "tileentities");
	}

	@Inject(method = "updateEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/tileentity/TileEntity;isInvalid()Z", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
	private void keepACopy(CallbackInfo ci, Iterator iterator, TileEntity entity) {
		this.myIterator = iterator;
		Profiler.start_entity_section(this.worldName, entity);
	}

	@Redirect(method = "updateEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/tileentity/TileEntity;isInvalid()Z", ordinal = 1))
	private boolean onRemoveBlockEntity(TileEntity tileEntity) {
		if (tileEntity.isInvalid()) {
			this.myIterator.remove();
			this.loadedTileEntityList.remove(tileEntity);

			if (this.isBlockLoaded(tileEntity.getPos())) {
				this.getChunk(tileEntity.getPos()).removeTileEntity(tileEntity.getPos());
			}
		}

		Profiler.end_current_entity_section();
		return false;
	}

	@Inject(method = "updateEntities", at = @At("RETURN"))
	private void onEndUpdateEntities(CallbackInfo ci) {
		Profiler.end_current_section();
	}
}

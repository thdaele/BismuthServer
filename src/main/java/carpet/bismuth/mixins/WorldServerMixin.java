package carpet.bismuth.mixins;

import carpet.bismuth.interfaces.IWorldServer;
import carpet.bismuth.utils.CarpetProfiler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldServer.class)
public abstract class WorldServerMixin extends World implements IWorldServer {
	protected WorldServerMixin(ISaveHandler ish, WorldInfo wi, WorldProvider wp, Profiler p, boolean b) {
		super(ish, wi, wp, p, b);
	}

	@Shadow
	protected abstract boolean isChunkLoaded(int x, int y, boolean allowEmpty);

	@Shadow
	@Final
	private static Logger LOGGER;
	private String worldName;
	private Entity myEntity;

	@Override
	public boolean isChunkLoadedC(int x, int z, boolean allowEmpty) {
		return this.isChunkLoaded(x, z, allowEmpty);
	}

	@Inject(method = "canAddEntity", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V", ordinal = 0, remap = false))
	private void keepACopy(Entity entityIn, CallbackInfoReturnable<Boolean> cir) {
		this.myEntity = entityIn;
	}

	@Redirect(method = "canAddEntity", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V", ordinal = 0, remap = false))
	private void addLocationToUUIDSpam(Logger logger, String message, Object p0, Object p1) {
		LOGGER.warn("Keeping entity {} that already exists with UUID {} at {} in {}", EntityList.getKey(this.myEntity), this.myEntity.getUniqueID(), this.myEntity.getPosition(), this.worldName);
	}

	@Inject(method = "<init>", at = @At("RETURN"))
	private void onInit(MinecraftServer server, ISaveHandler saveHandlerIn, WorldInfo info, int dimensionId, Profiler profilerIn, CallbackInfo ci) {
		this.worldName = this.provider.getDimensionType().getName();
	}

	@Inject(method = "tick", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/profiler/Profiler;startSection(Ljava/lang/String;)V", args = "ldc=mobSpawner"))
	private void onMobSpawning(CallbackInfo ci) {
		CarpetProfiler.start_section(this.worldName, "spawning");
	}

	@Inject(method = "tick", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V", args = "ldc=chunkSource"))
	private void onChunkSource(CallbackInfo ci) {
		CarpetProfiler.end_current_section();
	}

	@Inject(method = "tick", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V", args = "ldc=tickPending"))
	private void onTickPending(CallbackInfo ci) {
		CarpetProfiler.start_section(this.worldName, "tickupdates");
	}

	@Inject(method = "tick", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V", args = "ldc=tickBlocks"))
	private void onTickBlocks(CallbackInfo ci) {
		CarpetProfiler.end_current_section();
	}

	@Inject(method = "tick", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V", args = "ldc=chunkMap"))
	private void onChunkMap(CallbackInfo ci) {
		CarpetProfiler.start_section(this.worldName, "chunkmap");
	}

	@Inject(method = "tick", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V", args = "ldc=village"))
	private void onVillage(CallbackInfo ci) {
		CarpetProfiler.end_current_section();
		CarpetProfiler.start_section(this.worldName, "villages");
	}

	@Inject(method = "tick", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V", args = "ldc=portalForcer"))
	private void onTeleporter(CallbackInfo ci) {
		CarpetProfiler.end_current_section();
		CarpetProfiler.start_section(this.worldName, "portals");
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldServer;sendQueuedBlockEvents()V"))
	private void preSendQueuedBlockEvents(CallbackInfo ci) {
		CarpetProfiler.end_current_section();
		CarpetProfiler.start_section(this.worldName, "blockevents");
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldServer;sendQueuedBlockEvents()V", shift = At.Shift.AFTER))
	private void postSendQueuedBlockEvents(CallbackInfo ci) {
		CarpetProfiler.end_current_section();
	}

	@Inject(method = "updateBlocks", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V", args = "ldc=checkNextLight"))
	private void onCheckNextLight(CallbackInfo ci) {
		CarpetProfiler.start_section(this.worldName, "checknextlight");
	}

	@Inject(method = "updateBlocks", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V", args = "ldc=tickChunk"))
	private void onTickChunk(CallbackInfo ci) {
		CarpetProfiler.end_current_section();
		CarpetProfiler.start_section(this.worldName, "tickchunk");
	}

	@Inject(method = "updateBlocks", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V", args = "ldc=thunder"))
	private void onThunder(CallbackInfo ci) {
		CarpetProfiler.end_current_section();
		CarpetProfiler.start_section(this.worldName, "thunder");
	}

	@Inject(method = "updateBlocks", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V", args = "ldc=iceandsnow"))
	private void onIceAndSnow(CallbackInfo ci) {
		CarpetProfiler.end_current_section();
		CarpetProfiler.start_section(this.worldName, "iceandsnow");
	}

	@Inject(method = "updateBlocks", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V", args = "ldc=thunder"))
	private void onRandomTick(CallbackInfo ci) {
		CarpetProfiler.end_current_section();
		CarpetProfiler.start_section(this.worldName, "randomticks");
	}

	@Inject(method = "updateBlocks", slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;randomTick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Ljava/util/Random;)V")), at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;endSection()V", ordinal = 1))
	private void endMethod(CallbackInfo ci) {
		CarpetProfiler.end_current_section();
	}
}

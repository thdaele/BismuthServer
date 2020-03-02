package si.bismuth.movablete.mixins;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nullable;

@Mixin(Chunk.class)
public abstract class MixinChunk {
	@Shadow
	@Nullable
	public abstract TileEntity getTileEntity(BlockPos pos, Chunk.EnumCreateEntityType mode);

	@Redirect(method = "setBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;getTileEntity(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/chunk/Chunk$EnumCreateEntityType;)Lnet/minecraft/tileentity/TileEntity;", ordinal = 1))
	private TileEntity worldGetTileEntity(Chunk chunk, BlockPos pos, Chunk.EnumCreateEntityType mode) {
		return this.getTileEntity(pos, mode);
	}
}

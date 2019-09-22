package carpet.bismuth.mixins;

import net.minecraft.block.BlockGrassPath;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockGrassPath.class)
public abstract class BlockGrassPathMixin {
	@Redirect(method = "updateBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockFarmland;turnToDirt(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V"))
	private static void revertHax(World world, BlockPos pos) {
		world.setBlockState(pos, Blocks.DIRT.getDefaultState());
	}
}

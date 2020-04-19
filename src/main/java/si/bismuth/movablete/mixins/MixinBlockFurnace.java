package si.bismuth.movablete.mixins;

import net.minecraft.block.BlockFurnace;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockFurnace.class)
public abstract class MixinBlockFurnace {
	@Inject(method = "onBlockAdded", at = @At("HEAD"), cancellable = true)
	private void fixFurnaceRotation(World world, BlockPos pos, IBlockState state, CallbackInfo ci) {
		ci.cancel();
	}
}

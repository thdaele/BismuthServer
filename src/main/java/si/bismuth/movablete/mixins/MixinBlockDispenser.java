package si.bismuth.movablete.mixins;

import net.minecraft.block.BlockDispenser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockDispenser.class)
public abstract class MixinBlockDispenser {
	@Inject(method = "onBlockAdded", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockDispenser;setDefaultDirection(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)V"), cancellable = true)
	private void fixDispenserDropperRotation(World world, BlockPos pos, IBlockState state, CallbackInfo ci) {
		ci.cancel();
	}
}

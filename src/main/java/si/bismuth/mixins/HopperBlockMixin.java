package si.bismuth.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HopperBlock.class)
public class HopperBlockMixin {
    @Inject(method = "neighborChanged", at = @At("TAIL"))
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos neighborPos, CallbackInfo ci) {
        world.notifyBlockChanged(pos, state, state, 0);
    }
}
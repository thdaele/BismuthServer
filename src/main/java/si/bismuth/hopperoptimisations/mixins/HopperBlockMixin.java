package si.bismuth.hopperoptimisations.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import si.bismuth.hopperoptimisations.HopperSettings;
import si.bismuth.hopperoptimisations.utils.IHopper;

@Mixin(BlockHopper.class)
public class HopperBlockMixin {
	@Inject(method = "neighborChanged", at = @At(value = "HEAD"))
	private void updateBlockEntity(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, CallbackInfo ci) {
		if (HopperSettings.inventoryCheckOnBlockUpdate) {
			TileEntity hopper = world.getTileEntity(pos);
			if (hopper instanceof IHopper)
				((IHopper) hopper).onBlockUpdate();
		}
	}
}

package si.bismuth.hopperoptimisations.mixins;

import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockDropper;
import net.minecraft.block.BlockSourceImpl;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import si.bismuth.hopperoptimisations.HopperSettings;
import si.bismuth.hopperoptimisations.utils.InventoryOptimizer;
import si.bismuth.hopperoptimisations.utils.OptimizedInventory;

@Mixin(BlockDropper.class)
public abstract class DropperBlockMixin extends BlockDispenser {
	@Inject(method = "dispense", at = @At(value = "INVOKE", target = "Lnet/minecraft/dispenser/IBehaviorDispenseItem;dispense(Lnet/minecraft/dispenser/IBlockSource;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
	private void notifyInventoryDecr1(World worldIn, BlockPos pos, CallbackInfo ci, BlockSourceImpl impl, TileEntityDispenser dispenser, int i) {
		if (HopperSettings.optimizedInventories && dispenser instanceof OptimizedInventory) {
			InventoryOptimizer opt = ((OptimizedInventory) dispenser).getOptimizer();
			if (opt != null) opt.onItemStackCountChanged(i, -1);
		}
	}
}

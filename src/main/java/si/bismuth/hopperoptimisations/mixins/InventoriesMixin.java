package si.bismuth.hopperoptimisations.mixins;

import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import si.bismuth.hopperoptimisations.HopperSettings;
import si.bismuth.hopperoptimisations.utils.InventoryListOptimized;
import si.bismuth.hopperoptimisations.utils.InventoryOptimizer;

import java.util.List;

@Mixin(ItemStackHelper.class)
public class InventoriesMixin {
	@Inject(method = "getAndSplit", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;splitStack(I)Lnet/minecraft/item/ItemStack;", shift = At.Shift.AFTER))
	private static void notifyOptimizedInventoryAboutChangedItemStack(List<ItemStack> list_1, int int_1, int int_2, CallbackInfoReturnable<ItemStack> cir) {
		if (HopperSettings.optimizedInventories && list_1 instanceof InventoryListOptimized) {
			InventoryOptimizer opt = ((InventoryListOptimized) list_1).getOrRemoveOptimizer();
			if (opt != null) {
				opt.onItemStackCountChanged(int_1, -int_2);
			}
		}
	}
}

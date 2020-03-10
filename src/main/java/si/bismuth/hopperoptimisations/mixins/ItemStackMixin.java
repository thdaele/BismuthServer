package si.bismuth.hopperoptimisations.mixins;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import si.bismuth.hopperoptimisations.HopperSettings;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
	//Optimization: As ItemStack already caches whether it is empty, actually use the cached value.
	@Shadow(aliases = "isEmpty")
	private boolean empty;
	@Shadow(aliases = "stackSize")
	private int count;
	@Shadow
	@Final
	private Item item;

	@Inject(method = "isEmpty", at = @At(value = "HEAD"), cancellable = true)
	private void returnCachedEmpty(CallbackInfoReturnable<Boolean> cir) {
		if (HopperSettings.optimizedItemStackEmptyCheck) {
			cir.setReturnValue(this.empty);
		}
	}

	@Redirect(method = "updateEmptyState", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z"))
	private boolean isEmptyRecalculate(ItemStack itemStack) {
		return (this.item == null || this.item == Items.AIR || this.count <= 0);
	}
}


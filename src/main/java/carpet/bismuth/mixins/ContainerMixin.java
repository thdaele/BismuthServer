package carpet.bismuth.mixins;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Container.class)
abstract class ContainerMixin {
	@Shadow
	public abstract Slot getSlot(int slotId);

	@Shadow
	public abstract ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player);

	@Shadow
	public abstract void detectAndSendChanges();

	@Inject(method = "slotClick", at = @At("HEAD"), cancellable = true)
	private void onSlotClick(int slotId, int dragType, ClickType clickType, EntityPlayer player, CallbackInfoReturnable<ItemStack> cir) {
		if (slotId == 0 && dragType == 1 && clickType == ClickType.THROW && this.getSlot(slotId) instanceof SlotCrafting) {
			final SlotCrafting slot = (SlotCrafting) this.getSlot(slotId);
			while (slot.getHasStack()) {
				this.slotClick(slotId, 0, ClickType.THROW, player);
			}
			this.detectAndSendChanges();
		}
	}
}

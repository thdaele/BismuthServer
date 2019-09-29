package si.bismuth.mixins;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Container.class)
public abstract class ContainerMixin {
	@Shadow
	public List<Slot> inventorySlots;

	@Redirect(method = "slotClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/Slot;getHasStack()Z", ordinal = 0), slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/inventory/ClickType;THROW:Lnet/minecraft/inventory/ClickType;", opcode = Opcodes.GETSTATIC)))
	private boolean onSlotClick(Slot slot, int slotId, int dragType, ClickType clickType, EntityPlayer player) {
		if (slot != null && slot.getHasStack() && slot.canTakeStack(player)) {
			final ItemStack stack = slot.decrStackSize(slot.getStack().getCount());
			slot.onTake(player, stack);
			player.dropItem(stack, true);
		}

		return false;
	}

	@Inject(method = "slotClick", at = @At("HEAD"), cancellable = true)
	private void craftingHax(int slotId, int dragType, ClickType clickType, EntityPlayer player, CallbackInfoReturnable<ItemStack> cir) {
		if (clickType == ClickType.THROW && player.inventory.getItemStack().isEmpty() && slotId >= 0) {
			ItemStack itemStack = ItemStack.EMPTY;
			final Slot slot = this.inventorySlots.get(slotId);
			if (slot != null && slot.canTakeStack(player)) {
				if (slotId == 0 && dragType == 1) {
					ItemStack itemStackDropAll = this.dropAllCrafting(player, slotId, this.inventorySlots);
					while (!itemStackDropAll.isEmpty() && ItemStack.areItemsEqual(slot.getStack(), itemStackDropAll)) {
						itemStack = itemStackDropAll.copy();
						itemStackDropAll = this.dropAllCrafting(player, slotId, this.inventorySlots);
					}

					cir.setReturnValue(itemStack);
					cir.cancel();
				}
			}
		}
	}

	private ItemStack dropAllCrafting(EntityPlayer playerIn, int index, List<Slot> inventorySlotsParam) {
		ItemStack itemstack = ItemStack.EMPTY;
		final Slot slot = inventorySlotsParam.get(index);
		if (slot != null && slot.getHasStack()) {
			final ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			if (index == 0) {
				playerIn.dropItem(itemstack, true);
				itemstack1.setCount(0);
				slot.onSlotChange(itemstack1, itemstack);
			}

			if (itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}

			ItemStack itemstack2 = slot.onTake(playerIn, itemstack1);
			if (index == 0) {
				playerIn.dropItem(itemstack2, false);
			}
		}

		return itemstack;
	}
}

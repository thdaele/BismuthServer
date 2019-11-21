package si.bismuth.mixins;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.objectweb.asm.Opcodes;
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
	private boolean onSlotClick(Slot slot, int id, int dragType, ClickType clickType, EntityPlayer player) {
		if (slot != null && slot.getHasStack() && slot.canTakeStack(player)) {
			final ItemStack stack = slot.decrStackSize(slot.getStack().getCount());
			slot.onTake(player, stack);
			player.dropItem(stack, true);
		}

		return false;
	}

	@Inject(method = "slotClick", at = @At("HEAD"), cancellable = true)
	private void craftingHax(int id, int dragType, ClickType clickType, EntityPlayer player, CallbackInfoReturnable<ItemStack> cir) {
		if (clickType == ClickType.THROW && player.inventory.getItemStack().isEmpty() && id >= 0) {
			ItemStack stack = ItemStack.EMPTY;
			final Slot slot = this.inventorySlots.get(id);
			if (slot != null && slot.canTakeStack(player)) {
				if (id == 0 && dragType == 1) {
					ItemStack dropAll = this.dropAllCrafting(player, id, this.inventorySlots);
					while (!dropAll.isEmpty() && ItemStack.areItemsEqual(slot.getStack(), dropAll)) {
						stack = dropAll.copy();
						dropAll = this.dropAllCrafting(player, id, this.inventorySlots);
					}

					cir.setReturnValue(stack);
					cir.cancel();
				}
			}
		}
	}

	private ItemStack dropAllCrafting(EntityPlayer player, int index, List<Slot> inventorySlotsParam) {
		ItemStack itemstack = ItemStack.EMPTY;
		final Slot slot = inventorySlotsParam.get(index);
		if (slot != null && slot.getHasStack()) {
			final ItemStack slotStack = slot.getStack();
			itemstack = slotStack.copy();
			if (index == 0) {
				player.dropItem(itemstack, true);
				slotStack.setCount(0);
				slot.onSlotChange(slotStack, itemstack);
			}

			if (slotStack.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}

			final ItemStack stackOnTake = slot.onTake(player, slotStack);
			if (index == 0) {
				player.dropItem(stackOnTake, false);
			}
		}

		return itemstack;
	}
}

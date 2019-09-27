package si.bismuth.mixins;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.stats.StatList;
import net.minecraft.util.NonNullList;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Container.class)
public abstract class ContainerMixin {
	@Shadow
	public abstract void detectAndSendChanges();

	@Redirect(method = "slotClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/Slot;getHasStack()Z", ordinal = 0), slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/inventory/ClickType;THROW:Lnet/minecraft/inventory/ClickType;", opcode = Opcodes.GETSTATIC)))
	private boolean onSlotClick(Slot slot, int slotId, int dragType, ClickType clickType, EntityPlayer player) {
		if (slot != null && slot.getHasStack() && slot.canTakeStack(player)) {
			if (slotId == 0 && slot instanceof SlotCrafting) {
				if (dragType == 0) {
					this.dropStack(slot, player);
				} else {
					this.dropFullStack((SlotCrafting) slot, player);
				}
			} else {
				this.dropStack(slot, player);
			}

			this.detectAndSendChanges();
		}

		return false;
	}

	private void dropStack(Slot slot, EntityPlayer player) {
		final ItemStack stack = slot.decrStackSize(slot.getStack().getCount());
		slot.onTake(player, stack);
		player.dropItem(stack, true);
	}

	private void dropFullStack(SlotCrafting slot, EntityPlayer player) {
		final InventoryCrafting matrix = ((ISlotCraftingMixin) slot).getCraftMatrix();
		NonNullList<ItemStack> list = CraftingManager.getRemainingItems(matrix, player.world);
		int amount = 64;
		for (int i = 0; i < list.size(); i++) {
			final ItemStack stack = matrix.getStackInSlot(i);
			if (!stack.isEmpty()) {
				amount = Math.min(amount, stack.getCount());
			}
		}

		final ItemStack stack = slot.getStack();
		final int originalAmount = stack.getCount();
		final int craftedAmount = originalAmount * amount;
		stack.setCount(craftedAmount);
		slot.onTake(player, stack);
		stack.setCount(originalAmount);
		for (int i = 0; i < amount; i++) {
			player.dropItem(stack, true);
		}

		player.addStat(StatList.getCraftStats(stack.getItem()), craftedAmount);
		for (int i = 0; i < list.size(); i++) {
			matrix.decrStackSize(i, amount);
		}
	}
}

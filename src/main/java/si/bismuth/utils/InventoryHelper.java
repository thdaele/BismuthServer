package si.bismuth.utils;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// Stolen from/based on code from https://github.com/kyrptonaught/Inventory-Sorter
public class InventoryHelper {
	public static void sortInv(IInventory inv, int startSlot, int invSize) {
		final List<ItemStack> stacks = new ArrayList<>();
		for (int i = 0; i < invSize; i++) {
			addStackWithMerge(stacks, inv.getStackInSlot(startSlot + i));
		}

		if (stacks.isEmpty()) {
			return;
		}

		stacks.sort(Comparator.comparing(SortCases::getStringForSort));
		for (int i = 0; i < invSize; i++) {
			inv.setInventorySlotContents(startSlot + i, i < stacks.size() ? stacks.get(i) : ItemStack.EMPTY);
		}

		inv.markDirty();
	}

	private static void addStackWithMerge(List<ItemStack> stacks, ItemStack newStack) {
		if (newStack.isEmpty()) {
			return;
		}

		if (newStack.isStackable() && newStack.getCount() != newStack.getMaxStackSize()) {
			for (int j = stacks.size() - 1; j >= 0; j--) {
				final ItemStack oldStack = stacks.get(j);
				if (canMergeItems(newStack, oldStack)) {
					combineStacks(newStack, oldStack);
					if (oldStack.isEmpty()) {
						stacks.remove(j);
					}
				}
			}
		}

		stacks.add(newStack);
	}

	private static void combineStacks(ItemStack first, ItemStack second) {
		if (first.getMaxStackSize() >= first.getCount() + second.getCount()) {
			first.grow(second.getCount());
			second.setCount(0);
		}

		final int maxInsertAmount = Math.min(first.getMaxStackSize() - first.getCount(), second.getCount());
		first.grow(maxInsertAmount);
		second.shrink(maxInsertAmount);
	}

	private static boolean canMergeItems(ItemStack first, ItemStack second) {
		if (!first.isStackable() || !second.isStackable()) {
			return false;
		}

		if (first.getCount() >= first.getMaxStackSize() || second.getCount() >= second.getMaxStackSize()) {
			return false;
		}

		return ItemStack.areItemsEqual(first, second);
	}

	public static boolean areItemStacksEqualIgnoringCount(ItemStack first, ItemStack second) {
		if (first.isEmpty() && second.isEmpty()) {
			return true;
		} else {
			return !first.isEmpty() && !second.isEmpty() && isItemStackEqualIgnoringCount(first, second);
		}
	}

	/**
	 * compares ItemStack argument to the instance ItemStack; returns true if both ItemStacks are equal
	 */
	private static boolean isItemStackEqualIgnoringCount(ItemStack first, ItemStack second) {
		if (first.getItem() != second.getItem()) {
			return false;
		} else if (first.getItemDamage() != second.getItemDamage()) {
			return false;
		} else if (first.getTagCompound() == null && second.getTagCompound() != null) {
			return false;
		} else {
			return first.getTagCompound() == null || first.getTagCompound().equals(second.getTagCompound());
		}
	}
}

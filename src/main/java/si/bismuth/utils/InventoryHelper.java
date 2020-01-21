package si.bismuth.utils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import si.bismuth.MCServer;
import si.bismuth.network.BisPacketSearchForItem;

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
		} else if (first.getItem() != second.getItem()) {
			return false;
		} else if (first.getItemDamage() != second.getItemDamage()) {
			return false;
		} else {
			return first.getTagCompound() == null || first.getTagCompound().equals(second.getTagCompound());
		}
	}

	public static void processFindItem(EntityPlayerMP player, ItemStack stack) {
		if (stack.isEmpty()) {
			return;
		}

		final int range = 16;
		final NonNullList<BlockPos> positions = NonNullList.create();
		for (int i = -range; i <= range; i++) {
			for (int j = -range; j <= range; j++) {
				for (int k = -range; k <= range; k++) {
					final int x = i + player.getPosition().getX();
					final int y = j + player.getPosition().getY();
					final int z = k + player.getPosition().getZ();
					final BlockPos pos = new BlockPos(x, y, z);
					IInventory container = TileEntityHopper.getInventoryAtPosition(player.world, x, y, z);
					if (player.world.getTileEntity(pos) instanceof TileEntityEnderChest) {
						container = player.getInventoryEnderChest();
					}

					//noinspection ConstantConditions
					if (container == null) {
						continue;
					}

					for (int s = 0; s < container.getSizeInventory(); s++) {
						final ItemStack stackInSlot = container.getStackInSlot(s);
						if (InventoryHelper.areItemStacksEqualIgnoringCount(stack, stackInSlot)) {
							positions.add(pos);
							break;
						} else if (Block.getBlockFromItem(stackInSlot.getItem()) instanceof BlockShulkerBox) {
							final NBTTagCompound tag = stackInSlot.getTagCompound();
							if (tag != null && tag.hasKey("BlockEntityTag", 10)) {
								final NBTTagList list = tag.getCompoundTag("BlockEntityTag").getTagList("Items", 10);
								for (int b = 0; b < list.tagCount(); b++) {
									final NBTTagCompound compound = list.getCompoundTagAt(b);
									if (InventoryHelper.areItemStacksEqualIgnoringCount(stack, new ItemStack(compound))) {
										positions.add(pos);
										break;
									}
								}
							}
						}
					}
				}
			}
		}

		MCServer.pcm.sendPacketToPlayer(player, new BisPacketSearchForItem(positions));
	}
}

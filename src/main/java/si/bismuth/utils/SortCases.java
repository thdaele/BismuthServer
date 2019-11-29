package si.bismuth.utils;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Stolen from/based on code from https://github.com/kyrptonaught/Inventory-Sorter
public class SortCases {
	public static String getStringForSort(ItemStack stack) {
		return specialCases(stack);
	}

	private static String specialCases(ItemStack stack) {
		if (stack.getCount() != stack.getMaxStackSize()) {
			return stackSize(stack);
		}

		if (stack.getItem() instanceof ItemEnchantedBook) {
			return enchantedBookNameCase(stack);
		}

		if (stack.getItem() instanceof ItemTool) {
			return toolDuribilityCase(stack);
		}

		return stack.getItem().toString();
	}

	private static String stackSize(ItemStack stack) {
		return stack.getItem().toString() + stack.getCount();
	}

	private static String enchantedBookNameCase(ItemStack stack) {
		final NBTTagList enchants = ItemEnchantedBook.getEnchantments(stack);
		final List<String> names = new ArrayList<>();
		for (int i = 0; i < enchants.tagCount(); i++) {
			final NBTTagCompound enchantTag = enchants.getCompoundTagAt(i);
			final Enchantment enchant = Enchantment.getEnchantmentByLocation(enchantTag.getString("id"));
			if (enchant != null) {
				names.add(enchant.getTranslatedName(enchants.getCompoundTagAt(i).getInteger("lvl")));
			}
		}

		Collections.sort(names);
		final String enchantNames = StringUtils.join(names, " ");
		return stack.getItem().toString() + " " + enchants.tagCount() + " " + enchantNames;
	}

	private static String toolDuribilityCase(ItemStack stack) {
		return stack.getItem().toString() + stack.getItemDamage();
	}
}

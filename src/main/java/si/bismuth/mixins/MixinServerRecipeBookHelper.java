package si.bismuth.mixins;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ServerRecipeBookHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import si.bismuth.utils.IRecipeBookItemDuper;

@Mixin(ServerRecipeBookHelper.class)
public abstract class MixinServerRecipeBookHelper {
	@Shadow
	private InventoryCraftResult field_194335_f;

	@Shadow
	private InventoryCrafting field_194336_g;

	@Inject(method = "func_194327_a", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ServerRecipeBookHelper;func_194329_b()V", shift = At.Shift.AFTER))
	private void dupingBug(EntityPlayerMP player, IRecipe recipe, boolean b, CallbackInfo ci) {
		this.craftingWindowDupingBugAddedBack(player);
	}

	private void craftingWindowDupingBugAddedBack(EntityPlayerMP player) {
		final int slot = ((IRecipeBookItemDuper) player).getDupeItem();
		if (slot == Integer.MIN_VALUE || slot == -1) {
			return;
		}

		final ItemStack dupeItem = player.inventory.getStackInSlot(slot);
		if (dupeItem.isEmpty()) {
			return;
		}

		int size = dupeItem.getCount();
		for (int j = 0; j < this.field_194336_g.getSizeInventory(); ++j) {
			final ItemStack itemstack = this.field_194336_g.getStackInSlot(j);
			if (!itemstack.isEmpty()) {
				size += itemstack.getCount();
				itemstack.setCount(0);
			}
		}

		dupeItem.setCount(size);
		this.field_194335_f.clear();
		((IRecipeBookItemDuper) player).clearDupeItem();
	}
}

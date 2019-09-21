package carpet.bismuth.mixins;

import carpet.bismuth.utils.IRecipeBookItemDuper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InventoryPlayer.class)
public abstract class InventoryPlayerMixin {
	@Shadow
	public EntityPlayer player;

	@Shadow
	public abstract int storeItemStack(ItemStack itemStackIn);

	@Shadow
	public abstract int getFirstEmptyStack();

	@Shadow
	protected abstract int addResource(int p_191973_1_, ItemStack p_191973_2_);

	/**
	 * @author nessie
	 */
	@Overwrite
	private int storePartialItemStack(ItemStack itemStackIn) {
		int i = this.storeItemStack(itemStackIn);

		if (i == -1) {
			i = this.getFirstEmptyStack();
			((IRecipeBookItemDuper) this.player).dupeItem(i);
		}

		return i == -1 ? itemStackIn.getCount() : this.addResource(i, itemStackIn);
	}

	@Inject(method = "add", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/InventoryPlayer;getFirstEmptyStack()I", shift = At.Shift.AFTER))
	private void megaDupe(int i, ItemStack item, CallbackInfoReturnable<Boolean> cir) {
		((IRecipeBookItemDuper) this.player).dupeItem(i);
	}
}

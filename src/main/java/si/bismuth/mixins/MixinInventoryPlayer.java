package si.bismuth.mixins;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import si.bismuth.utils.IRecipeBookItemDuper;

@Mixin(InventoryPlayer.class)
public abstract class MixinInventoryPlayer {
	@Shadow
	public EntityPlayer player;

	@Inject(method = "storePartialItemStack", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/player/InventoryPlayer;getFirstEmptyStack()I"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void storePartialItemStack(ItemStack stack, CallbackInfoReturnable<Integer> cir, int i) {
		((IRecipeBookItemDuper) this.player).dupeItem(i);
	}

	@Inject(method = "add", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/InventoryPlayer;getFirstEmptyStack()I", shift = At.Shift.AFTER))
	private void megaDupe(int i, ItemStack item, CallbackInfoReturnable<Boolean> cir) {
		((IRecipeBookItemDuper) this.player).dupeItem(i);
	}
}

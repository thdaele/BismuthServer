package si.bismuth.mixins;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import si.bismuth.utils.IRecipeBookItemDuper;

@Mixin(EntityItem.class)
public abstract class MixinEntityItem implements IRecipeBookItemDuper {
	@Shadow
	public abstract ItemStack getItem();

	@Inject(method = "onCollideWithPlayer", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/item/EntityItem;pickupDelay:I", opcode = Opcodes.GETFIELD))
	private void startDupe(EntityPlayer entityIn, CallbackInfo ci) {
		((IRecipeBookItemDuper) entityIn).dupeItemScan(true);
	}

	@Inject(method = "onCollideWithPlayer", at = @At("RETURN"))
	private void endDupe(EntityPlayer entityIn, CallbackInfo ci) {
		((IRecipeBookItemDuper) entityIn).dupeItemScan(false);
	}

	@Inject(method = "searchForOtherItemsNearby", at = @At("HEAD"), cancellable = true)
	private void preSearchForOtherItemsNearby(CallbackInfo ci) {
		final ItemStack stack = this.getItem();
		if (stack.getCount() >= stack.getMaxStackSize()) {
			ci.cancel();
		}
	}

	@Inject(method = "combineItems", at = @At("HEAD"), cancellable = true)
	private void combineItems(EntityItem other, CallbackInfoReturnable<Boolean> cir) {
		final ItemStack stack = this.getItem();
		if (stack.getCount() >= stack.getMaxStackSize()) {
			cir.setReturnValue(false);
		}
	}
}

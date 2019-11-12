package si.bismuth.mixins;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import si.bismuth.utils.IRecipeBookItemDuper;

@Mixin(EntityItem.class)
public abstract class EntityItemMixin implements IRecipeBookItemDuper {
	@Inject(method = "onCollideWithPlayer", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/item/EntityItem;pickupDelay:I", opcode = Opcodes.GETFIELD))
	private void startDupe(EntityPlayer entityIn, CallbackInfo ci) {
		((IRecipeBookItemDuper) entityIn).dupeItemScan(true);
	}

	@Inject(method = "onCollideWithPlayer", at = @At("RETURN"))
	private void endDupe(EntityPlayer entityIn, CallbackInfo ci) {
		((IRecipeBookItemDuper) entityIn).dupeItemScan(false);
	}
}

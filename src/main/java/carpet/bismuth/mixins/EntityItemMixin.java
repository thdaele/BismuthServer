package carpet.bismuth.mixins;

import carpet.bismuth.utils.IRecipeBookItemDuper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityItem.class)
public abstract class EntityItemMixin extends Entity implements IRecipeBookItemDuper {
	public EntityItemMixin(World worldIn) {
		super(worldIn);
	}

	@Shadow
	public abstract ItemStack getItem();

	@Inject(method = "combineItems", at = @At(value = "RETURN", ordinal = 7), cancellable = true)
	private void stackShulkerboxes(EntityItem other, CallbackInfoReturnable<Boolean> cir) {
		final ItemStack itemstack = this.getItem();
		final ItemStack itemstack1 = other.getItem();
		if (itemstack1.getItem() instanceof ItemShulkerBox && itemstack.getItem() instanceof ItemShulkerBox) {
			if (!itemstack1.hasTagCompound() && !itemstack.hasTagCompound()) {
				itemstack1.grow(itemstack.getCount());
				other.setPickupDelay(Math.max(((IEntityItemMixin) other).getPickupDelay(), ((IEntityItemMixin) this).getPickupDelay()));
				((IEntityItemMixin) other).setAge(Math.min(((IEntityItemMixin) other).getAge(), ((IEntityItemMixin) this).getAge()));
				other.setItem(itemstack1);
				this.setDead();
				cir.setReturnValue(true);
			}
		}
	}

	@Inject(method = "onCollideWithPlayer", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/item/EntityItem;pickupDelay:I", opcode = Opcodes.GETFIELD))
	private void test(EntityPlayer entityIn, CallbackInfo ci) {
		if (entityIn instanceof EntityPlayerMP) {
			((IRecipeBookItemDuper) entityIn).dupeItemScan(true);
		}
	}

	@Inject(method = "onCollideWithPlayer", at = @At("RETURN"))
	private void test1(EntityPlayer entityIn, CallbackInfo ci) {
		if (entityIn instanceof EntityPlayerMP) {
			((IRecipeBookItemDuper) entityIn).dupeItemScan(false);
		}
	}
}

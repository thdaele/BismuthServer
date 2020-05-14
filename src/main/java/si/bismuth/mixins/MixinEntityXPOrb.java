package si.bismuth.mixins;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityXPOrb.class)
public abstract class MixinEntityXPOrb extends Entity implements si.bismuth.utils.IEntityXPOrb {
	@Unique
	private static final double LIFETIME_CONSTANT = 1D / Math.log(16D);
	@Unique
	private IntArrayList xpValues = new IntArrayList();
	@Unique
	private int delayBeforeCombine = 25;
	@Unique
	private int maxAge;

	public MixinEntityXPOrb(World world) {
		super(world);
	}

	@Shadow
	public int delayBeforeCanPickup;

	@Shadow
	private int xpValue;

	@Shadow
	protected abstract int durabilityToXp(int durability);

	@Shadow
	protected abstract int xpToDurability(int xp);

	@Inject(method = "<init>(Lnet/minecraft/world/World;DDDI)V", at = @At("RETURN"))
	private void onInit(World worldIn, double x, double y, double z, int expValue, CallbackInfo ci) {
		this.xpValues.add(expValue);
	}

	/**
	 * @author nessie
	 * @reason pretty invasive mixin so much simpler to overwrite
	 */
	@Overwrite
	public void onCollideWithPlayer(EntityPlayer player) {
		if (this.delayBeforeCanPickup == 0 && player.xpCooldown == 0) {
			player.xpCooldown = 2;
			final ItemStack stack = EnchantmentHelper.getEnchantedItem(Enchantments.MENDING, player);
			int xp = 0;
			// hackfix to prevent OOB exception
			if (!this.xpValues.isEmpty()) {
				xp = this.xpValues.removeInt(this.xpValues.size() - 1);
			}

			if (!stack.isEmpty() && stack.isItemDamaged()) {
				final int i = Math.min(this.xpToDurability(xp), stack.getItemDamage());
				xp -= this.durabilityToXp(i);
				stack.setItemDamage(stack.getItemDamage() - i);
			}

			if (xp > 0) {
				player.addExperience(xp);
			}

			if (this.xpValues.isEmpty()) {
				player.onItemPickup(this, 1);
				this.setDead();
			} else {
				this.world.playSound(null, this.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.35F + 0.9F);
				this.resetAge();
			}
		}
	}

	@Inject(method = "onUpdate", at = @At("HEAD"))
	private void onOrbUpdate(CallbackInfo ci) {
		if (--delayBeforeCombine > 0) {
			this.searchAndCombine();
		}
	}

	@Unique
	private void searchAndCombine() {
		this.delayBeforeCombine = 50;
		for (EntityXPOrb orb : this.world.getEntitiesWithinAABB(EntityXPOrb.class, this.getEntityBoundingBox().grow(0.5D))) {
			this.combineOrbs(orb);
		}
	}

	@Unique
	private void combineOrbs(EntityXPOrb orb) {
		if ((EntityXPOrb) (Object) this == orb) {
			return;
		}

		if (orb.isEntityAlive()) {
			final si.bismuth.utils.IEntityXPOrb iorb = ((si.bismuth.utils.IEntityXPOrb) orb);
			this.xpValue += orb.getXpValue();
			this.xpValues.addAll(iorb.getXpValues());
			orb.setDead();
			this.resetAge();
		}
	}

	@Unique
	private void resetAge() {
		((IEntityXPOrb) this).setXpOrbAge(0);
		this.maxAge = (int) (6000 * (1 + LIFETIME_CONSTANT * Math.log(this.xpValues.size())));
	}

	@ModifyConstant(method = "onUpdate", constant = @Constant(intValue = 6000))
	private int getMaxAge(int value) {
		return this.maxAge;
	}

	@Inject(method = "writeEntityToNBT", at = @At("RETURN"))
	private void onWriteEntityToNBT(NBTTagCompound compound, CallbackInfo ci) {
		compound.setIntArray("xpValues", this.xpValues.toIntArray());
		compound.setInteger("maxAge", this.maxAge);
	}

	@Inject(method = "readEntityFromNBT", at = @At("RETURN"))
	private void readEntityFromNBT(NBTTagCompound compound, CallbackInfo ci) {
		this.xpValues = new IntArrayList(compound.getIntArray("xpValues"));
		this.maxAge = compound.getInteger("maxAge");
	}

	@Override
	public IntArrayList getXpValues() {
		return this.xpValues;
	}
}

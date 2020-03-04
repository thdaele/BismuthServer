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
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import si.bismuth.MCServer;

import java.util.Arrays;

@Mixin(EntityXPOrb.class)
public abstract class MixinEntityXPOrb extends Entity implements si.bismuth.utils.IEntityXPOrb {
	@Unique
	IntArrayList xpValues = new IntArrayList();
	@Unique
	int delayBeforeCombine = 25;

	@Shadow
	public int delayBeforeCanPickup;

	@Shadow
	private int xpValue;

	public MixinEntityXPOrb(World world) {
		super(world);
	}

	@Shadow
	protected abstract int durabilityToXp(int durability);

	@Shadow
	protected abstract int xpToDurability(int xp);

	@Inject(method = "<init>(Lnet/minecraft/world/World;DDDI)V", at = @At("RETURN"))
	private void onInit(World worldIn, double x, double y, double z, int expValue, CallbackInfo ci) {
		this.xpValues.add(expValue);
	}

	@Inject(method = "onUpdate", at = @At("HEAD"))
	private void onOrbUpdate(CallbackInfo ci) {
		if (--delayBeforeCombine > 0) {
			this.searchAndCombine();
		}
	}

	private void searchAndCombine() {
		this.delayBeforeCombine = 50;
		for (EntityXPOrb orb : this.world.getEntitiesWithinAABB(EntityXPOrb.class, this.getEntityBoundingBox().grow(0.5D))) {
			this.combineOrbs(orb);
		}

		this.resetAge();
	}

	private void combineOrbs(EntityXPOrb orb) {
		if ((EntityXPOrb) (Object) this == orb) {
			return;
		}

		if (orb.isEntityAlive()) {
			final si.bismuth.utils.IEntityXPOrb iorb = ((si.bismuth.utils.IEntityXPOrb) orb);
			this.xpValue += orb.getXpValue();
			this.xpValues.addAll(iorb.getXpValues());

			orb.setDead();
		}
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
			try {
				xp = this.xpValues.removeInt(this.xpValues.size() - 1);
			} catch (ArrayIndexOutOfBoundsException e) {
				MCServer.bot.sendToDiscord(xp + " " + this.xpValues.size() + " " + Arrays.toString(this.xpValues.elements()));
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

	@Inject(method = "writeEntityToNBT", at = @At("RETURN"))
	private void onWriteEntityToNBT(NBTTagCompound compound, CallbackInfo ci) {
		compound.setIntArray("xpValues", this.xpValues.toIntArray());
	}

	@Inject(method = "readEntityFromNBT", at = @At("RETURN"))
	private void readEntityFromNBT(NBTTagCompound compound, CallbackInfo ci) {
		this.xpValues = new IntArrayList(compound.getIntArray("xpValues"));
	}

	private void resetAge() {
		((IEntityXPOrb) this).setXpOrbAge(0);
	}

	@Override
	public IntArrayList getXpValues() {
		return this.xpValues;
	}
}

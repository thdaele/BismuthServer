package carpet.bismuth.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityLivingBase.class)
public abstract class EntityLivingBaseMixin extends Entity {
	public EntityLivingBaseMixin(World worldIn) {
		super(worldIn);
	}

	@Redirect(method = "renderBrokenItemStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;playSound(Lnet/minecraft/util/SoundEvent;FF)V"))
	private void fixToolBreakEffect(EntityLivingBase entityLivingBase, SoundEvent soundIn, float volume, float pitch) {
		this.world.playSound((EntityPlayer) (Object) this, this.posX, this.posY, this.posZ, soundIn, this.getSoundCategory(), volume, pitch);
	}
}

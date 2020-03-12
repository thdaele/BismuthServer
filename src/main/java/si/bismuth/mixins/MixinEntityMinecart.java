package si.bismuth.mixins;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EntityMinecart.class)
public abstract class MixinEntityMinecart {
	@ModifyConstant(method = "moveAlongTrack", constant = @Constant(classValue = EntityLivingBase.class, ordinal = 0))
	private Class<? extends EntityLivingBase> mc64836(Object value, Class<EntityLivingBase> clazz) {
		return EntityPlayer.class;
	}
}

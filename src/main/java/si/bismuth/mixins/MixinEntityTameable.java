package si.bismuth.mixins;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import si.bismuth.MCServer;

@Mixin(EntityTameable.class)
public abstract class MixinEntityTameable {
	@Redirect(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;sendMessage(Lnet/minecraft/util/text/ITextComponent;)V"))
	private void sendMessage(EntityLivingBase entity, ITextComponent component) {
		MCServer.server.getPlayerList().sendMessage(component);
		MCServer.bot.sendDeathMessage(component);
	}
}

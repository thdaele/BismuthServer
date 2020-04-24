package si.bismuth.mixins;

import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import si.bismuth.MCServer;

@Mixin(PlayerAdvancements.class)
public abstract class MixinPlayerAdvancements {
	@Redirect(method = "grantCriterion", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/management/PlayerList;sendMessage(Lnet/minecraft/util/text/ITextComponent;)V"))
	private void onSendAdvancement(PlayerList list, ITextComponent component) {
		list.sendMessage(component);
		MCServer.bot.sendAdvancementMessage(component);
	}
}

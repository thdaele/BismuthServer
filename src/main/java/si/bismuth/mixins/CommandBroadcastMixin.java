package si.bismuth.mixins;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.server.CommandBroadcast;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import si.bismuth.MCServer;

@Mixin(CommandBroadcast.class)
public abstract class CommandBroadcastMixin {
	@Inject(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/management/PlayerList;sendMessage(Lnet/minecraft/util/text/ITextComponent;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void onSay(MinecraftServer server, ICommandSender sender, String[] args, CallbackInfo ci, ITextComponent component) {
		final ITextComponent text = new TextComponentTranslation("chat.type.announcement", sender.getDisplayName(), component);
		MCServer.bot.sendToDiscord(text.getUnformattedText());
	}
}

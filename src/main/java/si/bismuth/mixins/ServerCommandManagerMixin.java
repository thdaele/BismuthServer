package si.bismuth.mixins;

import si.bismuth.commands.CommandLog;
import si.bismuth.commands.CommandPlayer;
import si.bismuth.commands.CommandStackBoxes;
import si.bismuth.commands.CommandTick;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommandListener;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerCommandManager.class)
abstract class ServerCommandManagerMixin extends CommandHandler implements ICommandListener {
	@Inject(method = "<init>", at = @At("RETURN"))
	private void onCtor(MinecraftServer server, CallbackInfo ci) {
		this.registerCommand(new CommandLog());
		this.registerCommand(new CommandPlayer());
		this.registerCommand(new CommandStackBoxes());
		this.registerCommand(new CommandTick());
	}
}

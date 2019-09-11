package carpet.bismuth.mixins;

import carpet.bismuth.commands.CommandCarpet;
import carpet.bismuth.commands.CommandLog;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommandListener;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerCommandManager.class)
public abstract class ServerCommandManagerMixin extends CommandHandler implements ICommandListener
{
    @Inject(method = "<init>", at = @At("RETURN"))
    private void onCtor(MinecraftServer p_i46985_1_, CallbackInfo ci)
    {
        this.registerCommand(new CommandCarpet());
        this.registerCommand(new CommandLog());
    }
}

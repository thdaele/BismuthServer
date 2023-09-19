package si.bismuth.scoreboard.mixins;

import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.source.CommandResults;
import net.minecraft.server.command.source.CommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import si.bismuth.scoreboard.IScoreboard;
import si.bismuth.scoreboard.LongScore;

@Mixin(CommandResults.class)
public class CommandResultsMixin {
    @Inject(method = "add", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/Scoreboard;getScore(Ljava/lang/String;Lnet/minecraft/scoreboard/ScoreObjective;)Lnet/minecraft/scoreboard/ScoreboardScore;"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    public void setCommandStatForSender(MinecraftServer server, CommandSource source, CommandResults.Type type, int result, CallbackInfo ci, String lastSource, CommandSource wrapper, String playerName, String lastObjective, Scoreboard scoreboard, ScoreboardObjective objective) {
        IScoreboard iscoreboard = (IScoreboard) scoreboard;
        LongScore longScore = iscoreboard.getLongScore(playerName, objective);
        longScore.set(result);
        ci.cancel();
    }
}

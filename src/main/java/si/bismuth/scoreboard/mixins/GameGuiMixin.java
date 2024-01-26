package si.bismuth.scoreboard.mixins;

import net.minecraft.client.gui.GameGui;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardScore;
import net.minecraft.scoreboard.team.Team;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import si.bismuth.scoreboard.IScoreboard;

import java.util.Collection;

@Mixin(GameGui.class)
public class GameGuiMixin {
    @Redirect(method = "renderScoreboardObjective", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/Scoreboard;getScores(Lnet/minecraft/scoreboard/ScoreboardObjective;)Ljava/util/Collection;"))
    Collection<ScoreboardScore> renderScoreboardObjective(Scoreboard instance, ScoreboardObjective objective) {
        return ((IScoreboard) instance).bismuthServer$getScores(objective);
    }

//    @ModifyArg(method = "renderScoreboardObjective", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/team/Team;getMemberDisplayName(Lnet/minecraft/scoreboard/team/AbstractTeam;Ljava/lang/String;)Ljava/lang/String;"), index = 1)
    @ModifyVariable(method = "renderScoreboardObjective", at = @At("STORE"), ordinal = 0)
    private String injected1(String string) {
//        long score = ((IScoreboardScore) scoreboardScore).bismuthServer$getLongScore();
//
//        return 0;
        return value;
    }

    @ModifyVariable(method = "renderScoreboardObjective", at = @At("STORE"), ordinal = 2)
    private String injected2(String string) {
//        long score = ((IScoreboardScore) scoreboardScore).bismuthServer$getLongScore();
//
//        return 0;
    }
}

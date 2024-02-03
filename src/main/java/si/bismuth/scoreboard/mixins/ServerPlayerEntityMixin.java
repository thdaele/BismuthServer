package si.bismuth.scoreboard.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardScore;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import si.bismuth.scoreboard.IScoreboardScore;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Redirect(method = "updateScores", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/ScoreboardScore;set(I)V"))
    private void updateScores(ScoreboardScore instance, int score, @Local ScoreboardObjective scoreboardObjective) {
        ((IScoreboardScore) instance).bismuthServer$setLongScore((long) score, instance.getOwner(), scoreboardObjective);
    }

    @Redirect(method = "clearStat", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/ScoreboardScore;set(I)V"))
    private void clearStat(ScoreboardScore instance, int score, @Local ScoreboardObjective scoreboardObjective) {
        ((IScoreboardScore) instance).bismuthServer$setLongScore((long) score, instance.getOwner(), scoreboardObjective);
    }
}

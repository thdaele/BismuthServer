package si.bismuth.scoreboard.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardScore;
import net.minecraft.server.command.TriggerCommand;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import si.bismuth.scoreboard.IScoreboardScore;

@Mixin(TriggerCommand.class)
public class TriggerCommandMixin {
    // TODO in same method try to parse a long should be easier with the mixinextras
    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/ScoreboardScore;set(I)V"))
    private void run(ScoreboardScore instance, int score, @Local ServerPlayerEntity player, @Local ScoreboardObjective scoreboardObjective) {
        String owner = player.getName();
        ((IScoreboardScore) instance).bismuthServer$setLongScore((long) score, owner, scoreboardObjective);
    }
}

package si.bismuth.scoreboard.mixins;

import net.minecraft.scoreboard.ScoreboardScore;
import net.minecraft.server.command.TriggerCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import si.bismuth.scoreboard.IScoreboardScore;

@Mixin(TriggerCommand.class)
public class TriggerCommandMixin {
    // TODO in same method try to parse a long should be easier with the mixinextras
    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/ScoreboardScore;set(I)V"))
    private void run(ScoreboardScore instance, int score) {
        ((IScoreboardScore) instance).bismuthServer$setLongScore((long) score);
    }
}

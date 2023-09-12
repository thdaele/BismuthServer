package si.bismuth.scoreboard.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.world.gen.loot.RandomNumberBounds;
import net.minecraft.world.gen.loot.conditions.EntityScoresCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import si.bismuth.scoreboard.IScoreboard;

@Mixin(EntityScoresCondition.class)
public class EntityScoresConditionMixin {
    // TODO insert in randomValueRange or some shit to make this more clean
    public boolean isInRange(long score, RandomNumberBounds range) {
        return (float)score <= range.getMax() && (float)score >= range.getMin();
    }


    @Inject(method = "hasScore", at = @At(value = "HEAD"), cancellable = true)
    void entityScoreMatch(Entity entity, Scoreboard scoreboard, String objectiveName, RandomNumberBounds score, CallbackInfoReturnable<Boolean> cir) {
        ScoreboardObjective objective = scoreboard.getObjective(objectiveName);
        if (objective == null) {
            cir.setReturnValue(Boolean.FALSE);
        } else {
            String ownerName = entity instanceof ServerPlayerEntity
                    ? entity.getName()
                    : entity.getScoreboardName();
            if (!scoreboard.hasScore(ownerName, objective)) {
                cir.setReturnValue(Boolean.FALSE);
            } else {
                isInRange(((IScoreboard)scoreboard).getLongScore(ownerName, objective).get(), score);
            }
        }
    }
}

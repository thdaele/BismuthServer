package si.bismuth.scoreboard.mixins;

import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.criterion.ScoreboardCriterion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import si.bismuth.scoreboard.IScoreboard;
import si.bismuth.scoreboard.LongScore;

import java.util.*;

@Mixin(Scoreboard.class)
public abstract class ScoreboardMixin implements IScoreboard {
    @Unique
    private final Map<String, Map<ScoreboardObjective, LongScore>> longScores = new HashMap<>();

    @Shadow @Final
    private Map<String, ScoreboardObjective> objectivesByName;

    @Shadow @Final
    private Map<ScoreboardCriterion, List<ScoreboardObjective>> objectivesByCreaterion;

    @Shadow
    public abstract void onScoresRemoved(String scoreName);

    @Shadow
    public abstract void onScoreRemoved(String scoreName, ScoreboardObjective objective);

    @Shadow
    public abstract void setDisplayObjective(int slot, ScoreboardObjective objective);

    @Shadow
    public abstract ScoreboardObjective getDisplayObjective(int slotIn);

    @Shadow
    public abstract void onObjectiveRemoved(ScoreboardObjective objective);


    @Override
    public LongScore bismuthServer$getLongScore(String owner, ScoreboardObjective objective) {
        if (owner.length() > 40) {
            throw new IllegalArgumentException("The player name '" + owner + "' is too long!");
        } else {
            Map<ScoreboardObjective, LongScore> scores = this.longScores.computeIfAbsent(owner, k -> new HashMap<>());

            LongScore score = (LongScore) ((Map<?, ?>)scores).get(objective);
            if (score == null) {
                score = new LongScore(this, objective, owner);
                scores.put(objective, score);
            }

            return score;
        }
    }

    @Override
    public Collection<LongScore> bismuthServer$getLongScores(ScoreboardObjective objective) {
        List<LongScore> collectedScores = new ArrayList<>();

        for (Map<ScoreboardObjective, LongScore> scores : this.longScores.values()) {
            LongScore score = scores.get(objective);
            if (score != null) {
                collectedScores.add(score);
            }
        }

        collectedScores.sort(LongScore.SCORE_COMPARATOR);
        return collectedScores;
    }

    @Override
    public Collection<LongScore> bismuthServer$getLongScores() {
        Collection<Map<ScoreboardObjective, LongScore>> scoreMaps = this.longScores.values();
        List<LongScore> collectedScores = new ArrayList<>();

        for (Map<ScoreboardObjective, LongScore> scores : scoreMaps) {
            collectedScores.addAll(scores.values());
        }

        return collectedScores;
    }

    @Override
    public Map<ScoreboardObjective, LongScore> bismuthServer$getLongScores(String owner) {
        Map<ScoreboardObjective, LongScore> scores = this.longScores.get(owner);
        if (scores == null) {
            scores = new HashMap<>();
        }

        return scores;
    }

    @Inject(method = "hasScore", at = @At(value = "HEAD"), cancellable = true)
    void entityHasObjective(String owner, ScoreboardObjective objective, CallbackInfoReturnable<Boolean> cir) {
        Map<ScoreboardObjective, LongScore> scores = this.longScores.get(owner);

        if (scores == null)
        {
            cir.setReturnValue(Boolean.FALSE);
        }
        else
        {
            cir.setReturnValue(scores.containsKey(objective));
        }
    }

    @Inject(method = "removeScore", at = @At(value = "HEAD"), cancellable = true)
    void removeObjectiveFromEntity(String owner, ScoreboardObjective objective, CallbackInfo ci) {
        if (objective == null)
        {
            Map<ScoreboardObjective, LongScore> scores = this.longScores.remove(owner);

            if (scores != null)
            {
                this.onScoresRemoved(owner);
            }
        }
        else
        {
            Map<ScoreboardObjective, LongScore> scores = this.longScores.get(owner);

            if (scores != null)
            {
                LongScore score = scores.remove(objective);

                if (scores.size() < 1)
                {
                    Map<ScoreboardObjective, LongScore> removedScores = this.longScores.remove(owner);

                    if (removedScores != null)
                    {
                        this.onScoresRemoved(owner);
                    }
                }
                else if (score != null)
                {
                    this.onScoreRemoved(owner, objective);
                }
            }
        }
        ci.cancel();
    }

    @Inject(method = "removeObjective", at = @At(value = "HEAD"), cancellable = true)
    void removeObjective(ScoreboardObjective objective, CallbackInfo ci) {
        this.objectivesByName.remove(objective.getName());

        for (int slot = 0; slot < 19; ++slot)
        {
            if (this.getDisplayObjective(slot) == objective)
            {
                this.setDisplayObjective(slot, null);
            }
        }

        List<ScoreboardObjective> objectives = this.objectivesByCreaterion.get(objective.getCriterion());

        if (objectives != null)
        {
            objectives.remove(objective);
        }

        for (Map<ScoreboardObjective, LongScore> scores : this.longScores.values())
        {
            scores.remove(objective);
        }

        this.onObjectiveRemoved(objective);
        ci.cancel();
    }
}

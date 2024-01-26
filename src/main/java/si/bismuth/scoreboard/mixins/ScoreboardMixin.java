package si.bismuth.scoreboard.mixins;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardScore;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import si.bismuth.scoreboard.IScoreboard;

import java.util.Collection;
import java.util.List;

import static si.bismuth.utils.ScoreboardHelper.getUpperScoreboardScoreName;
import static si.bismuth.utils.ScoreboardHelper.upperScoreboardScorePrefix;

@Mixin(Scoreboard.class)
public abstract class ScoreboardMixin implements IScoreboard {
    @Shadow
    public abstract ScoreboardScore getScore(String owner, ScoreboardObjective objective);

    @Shadow
    public abstract Collection<ScoreboardScore> getScores(ScoreboardObjective objective);

    @Shadow
    public abstract Collection<ScoreboardScore> getScores();

    @Override
    public Long bismuthServer$getLongScore(String owner, ScoreboardObjective objective) {
        // TODO make the lower bits contain a reference to the higher_bits inside the ScoreboardScore (dunno how that works in java)
        ScoreboardScore lower_score = this.getScore(owner, objective);
        ScoreboardScore higher_score = this.getScore(getUpperScoreboardScoreName(owner), objective);

        int lower_bits = lower_score.get();
        int higher_bits = higher_score.get();

        return ((long) higher_bits) << 32 & lower_bits;
    }

    @Override
    public Collection<ScoreboardScore> bismuthServer$getScores(ScoreboardObjective objective) {
        Collection<ScoreboardScore> collection = this.getScores(objective);

        collection.removeIf(scoreboardScore -> scoreboardScore.getOwner().startsWith(upperScoreboardScorePrefix));
        return collection;
    }

    @Override
    public Collection<ScoreboardScore> bismuthServer$getScores() {
        Collection<ScoreboardScore> collection = this.getScores();

        collection.removeIf(scoreboardScore -> scoreboardScore.getOwner().startsWith(upperScoreboardScorePrefix));
        return collection;
    }
}

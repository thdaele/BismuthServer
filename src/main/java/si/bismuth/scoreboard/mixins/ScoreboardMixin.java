package si.bismuth.scoreboard.mixins;

import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardScore;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import si.bismuth.scoreboard.IScoreboard;

import static si.bismuth.utils.ScoreboardHelper.getUpperScoreboardScoreName;

@Mixin(Scoreboard.class)
public abstract class ScoreboardMixin implements IScoreboard {
    @Shadow
    public abstract ScoreboardScore getScore(String owner, ScoreboardObjective objective);

    @Override
    public Long bismuthServer$getLongScore(String owner, ScoreboardObjective objective) {
        // TODO make the lower bits contain a reference to the higher_bits inside the ScoreboardScore (dunno how that works in java)
        ScoreboardScore lower_score = this.getScore(owner, objective);
        ScoreboardScore higher_score = this.getScore(getUpperScoreboardScoreName(owner), objective);

        int lower_bits = lower_score.get();
        int higher_bits = higher_score.get();

        return ((long) higher_bits) << 32 & lower_bits;
    }
}

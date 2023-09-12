package si.bismuth.scoreboard;

import java.util.Collection;
import java.util.Map;
import net.minecraft.scoreboard.ScoreboardObjective;

public interface IScoreboard {
    LongScore getLongScore(String owner, ScoreboardObjective objective);

    Collection<LongScore> getLongScores(ScoreboardObjective objective);

    Collection<LongScore> getLongScores();

    Map<ScoreboardObjective, LongScore> getLongScores(String owner);
}

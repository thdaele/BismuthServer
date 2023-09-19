package si.bismuth.scoreboard;

import java.util.Collection;
import java.util.Map;
import net.minecraft.scoreboard.ScoreboardObjective;

public interface IScoreboard {
    LongScore bismuthServer$getLongScore(String owner, ScoreboardObjective objective);

    Collection<LongScore> bismuthServer$getLongScores(ScoreboardObjective objective);

    Collection<LongScore> bismuthServer$getLongScores();

    Map<ScoreboardObjective, LongScore> bismuthServer$getLongScores(String owner);
}

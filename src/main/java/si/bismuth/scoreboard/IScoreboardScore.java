package si.bismuth.scoreboard;

import net.minecraft.scoreboard.ScoreboardObjective;

public interface IScoreboardScore {
    Long bismuthServer$getLongScore();
    void bismuthServer$setLongScore(Long value, String owner, ScoreboardObjective objective);
    void bismuthServer$createUpperScore(String owner, ScoreboardObjective objective);
}

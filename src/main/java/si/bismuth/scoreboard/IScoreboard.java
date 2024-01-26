package si.bismuth.scoreboard;

import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardScore;

import java.util.Collection;

public interface IScoreboard {
    Long bismuthServer$getLongScore(String owner, ScoreboardObjective objective);
    public Collection<ScoreboardScore> bismuthServer$getScores(ScoreboardObjective objective);
    public Collection<ScoreboardScore> bismuthServer$getScores();


}

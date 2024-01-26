package si.bismuth.scoreboard;

import net.minecraft.scoreboard.ScoreboardObjective;

public interface IScoreboard {
    Long bismuthServer$getLongScore(String owner, ScoreboardObjective objective);
}

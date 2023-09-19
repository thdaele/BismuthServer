package si.bismuth.scoreboard;

import net.minecraft.scoreboard.ScoreboardObjective;

import java.util.Comparator;

public class LongScore {
    public static final Comparator<LongScore> SCORE_COMPARATOR = new Comparator<LongScore>() {
        public int compare(LongScore score1, LongScore score2) {
            if (score1.get() > score2.get()) {
                return 1;
            } else {
                return score1.get() < score2.get() ? -1 : score2.getOwner().compareToIgnoreCase(score1.getOwner());
            }
        }
    };
    private final IScoreboard scoreboard;
    private final ScoreboardObjective objective;
    private final String owner;
    private long score;
    private boolean locked;
    private boolean forceUpdate;

    public LongScore(IScoreboard scoreboard, ScoreboardObjective objective, String owner) {
        this.scoreboard = scoreboard;
        this.objective = objective;
        this.owner = owner;
        this.forceUpdate = true;
    }

    public void increase(long amount) {
        if (this.objective.getCriterion().isReadOnly()) {
            throw new IllegalStateException("Cannot modify read-only score");
        } else {
            this.set(this.get() + amount);
            if (!"Total".equals(owner)){
                final IScoreboard scoreboard = this.scoreboard;
                final LongScore totalScore = scoreboard.bismuthServer$getLongScore("Total", objective);
                if (totalScore.get() > -1){
                    totalScore.increase(amount);
                }
            }
        }
    }

    public void decrease(long amount) {
        if (this.objective.getCriterion().isReadOnly()) {
            throw new IllegalStateException("Cannot modify read-only score");
        } else {
            this.set(this.get() - amount);
        }
    }

    public void increment() {
        if (this.objective.getCriterion().isReadOnly()) {
            throw new IllegalStateException("Cannot modify read-only score");
        } else {
            this.increase(1);
        }
    }

    public long get() {
        return this.score;
    }

    public void set(long score) {
        long prev = this.score;
        this.score = score;
        if (prev != score || this.forceUpdate) {
            //TODO Calculate the change and apply that to the total score
            this.forceUpdate = false;
            IServerScoreboard scoreboard = (IServerScoreboard) this.getScoreboard();
            scoreboard.bismuthServer$onScoreUpdated(this);
        }
    }

    public ScoreboardObjective getObjective() {
        return this.objective;
    }

    public String getOwner() {
        return this.owner;
    }

    public IScoreboard getScoreboard() {
        return this.scoreboard;
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}

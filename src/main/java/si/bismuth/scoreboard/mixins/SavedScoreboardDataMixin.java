package si.bismuth.scoreboard.mixins;

import com.google.common.collect.Maps;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.scoreboard.SavedScoreboardData;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardScore;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

import static si.bismuth.utils.ScoreboardHelper.getUpperScoreboardScoreName;
import static si.bismuth.utils.ScoreboardHelper.upperScoreboardScorePrefix;

@Mixin(SavedScoreboardData.class)
public class SavedScoreboardDataMixin {
    @Shadow
    private Scoreboard scoreboard;

    @Inject(method="setScoreboard", at=@At(value="TAIL", target="Lnet/minecraft/scoreboard/ScoreboardSaveData;readFromNBT(Lnet/minecraft/nbt/NBTTagCompound;)V"))
    private void updateTotals(CallbackInfo ci){
        // TODO update to make sure it still works with the 64bit scoreboard
        final Map<ScoreboardObjective, Long> totalsMap = Maps.newHashMap();

        for (ScoreboardScore score : scoreboard.getScores()){
            if (!"Total".equals(score.getOwner())){
                totalsMap.put(score.getObjective(), totalsMap.getOrDefault(score.getObjective(), (long) 0) + score.get());
            }
        }

        for (ScoreboardObjective objective : totalsMap.keySet()){
            long total = totalsMap.get(objective);

            if (total > Integer.MAX_VALUE){
                total = -1;
            }

            scoreboard.getScore("Total", objective).set((int) total);
        }
    }

    /**
     * @author thdaele
     * @reason Force storing the 64bit scoreboard as single long
     */
    @Overwrite
    protected NbtList scoresToNbt() {
        NbtList nbtList = new NbtList();

        for(ScoreboardScore scoreboardScore : this.scoreboard.getScores()) {
            if (scoreboardScore.getOwner().startsWith(upperScoreboardScorePrefix)) {
                continue;
            }
            if (scoreboardScore.getObjective() != null) {
                ScoreboardObjective objective = scoreboardScore.getObjective();
                // TODO improve this to use a reference in ScoreboardScore to upper_score
                ScoreboardScore higher_score = this.scoreboard.getScore(getUpperScoreboardScoreName(scoreboardScore.getOwner()), objective);
                int lower_bits = scoreboardScore.get();
                int higher_bits = higher_score.get();

                long score = ((long) higher_bits) << 32 & lower_bits;
                NbtCompound nbtCompound = new NbtCompound();
                nbtCompound.putString("Name", scoreboardScore.getOwner());
                nbtCompound.putString("Objective", scoreboardScore.getObjective().getName());
                nbtCompound.putLong("Score", score);
                nbtCompound.putBoolean("Locked", scoreboardScore.isLocked());
                nbtList.add(nbtCompound);
            }
        }
        return nbtList;
    }

    /**
     * @author thdaele
     * @reason Force storing the 64bit scoreboard as single long
     */
    @Overwrite
    protected void readScoresFromNbt(NbtList nbt) {
        for(int i = 0; i < nbt.size(); ++i) {
            NbtCompound nbtCompound = nbt.getCompound(i);
            ScoreboardObjective scoreboardObjective = this.scoreboard.getObjective(nbtCompound.getString("Objective"));
            String string = nbtCompound.getString("Name");
            if (string.length() > 40) {
                string = string.substring(0, 40);
            }

            long score = nbtCompound.getLong("Score");
            // Lower bits
            ScoreboardScore lower_score = this.scoreboard.getScore(string, scoreboardObjective);
            lower_score.set((int) score);
            if (nbtCompound.contains("Locked")) {
                lower_score.setLocked(nbtCompound.getBoolean("Locked"));
            }
            // Upper bits
            ScoreboardScore upper_score = this.scoreboard.getScore(getUpperScoreboardScoreName(string), scoreboardObjective);
            upper_score.set((int)(score >> 32));
            if (nbtCompound.contains("Locked")) {
                upper_score.setLocked(nbtCompound.getBoolean("Locked"));
            }
        }
    }
}

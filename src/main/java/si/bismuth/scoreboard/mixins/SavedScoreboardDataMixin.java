package si.bismuth.scoreboard.mixins;

import com.google.common.collect.Maps;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalLongRef;
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
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import si.bismuth.scoreboard.IScoreboard;
import si.bismuth.scoreboard.IScoreboardScore;

import java.util.Collection;
import java.util.Map;

@Mixin(SavedScoreboardData.class)
public abstract class SavedScoreboardDataMixin {
    @Shadow
    private Scoreboard scoreboard;

    @Inject(method = "setScoreboard", at = @At("TAIL"))
    private void updateTotals(CallbackInfo ci){
        final Map<ScoreboardObjective, Long> totalsMap = Maps.newHashMap();

        for (ScoreboardScore score : ((IScoreboard) scoreboard).bismuthServer$getScores()){
            if (!"Total".equals(score.getOwner())){
                totalsMap.put(score.getObjective(), totalsMap.getOrDefault(score.getObjective(), (long) 0) + ((IScoreboardScore) score).bismuthServer$getLongScore());
            }
        }

        for (ScoreboardObjective objective : totalsMap.keySet()){
            long total = totalsMap.get(objective);

            ((IScoreboardScore) scoreboard.getScore("Total", objective)).bismuthServer$setLongScore(total, "Total", objective);
        }
    }

    /**
     * @author thdaele
     * @reason Force storing the 64bit scoreboard as single long
     */
//    @Overwrite
//    protected NbtList scoresToNbt() {
//        NbtList nbtList = new NbtList();
//
//        // TODO convert this to redirect
//        for(ScoreboardScore scoreboardScore : ((IScoreboard) this.scoreboard).bismuthServer$getScores()) {
//            if (scoreboardScore.getObjective() != null) {
//                NbtCompound nbtCompound = new NbtCompound();
//                nbtCompound.putString("Name", scoreboardScore.getOwner());
//                nbtCompound.putString("Objective", scoreboardScore.getObjective().getName());
//                // TODO convert this to redirect
//                nbtCompound.putLong("Score", ((IScoreboardScore) scoreboardScore).bismuthServer$getLongScore());
//                nbtCompound.putBoolean("Locked", scoreboardScore.isLocked());
//                nbtList.add(nbtCompound);
//            }
//        }
//        return nbtList;
//    }

    // The two following mixins replace the previous overwrite

    @Redirect(method = "scoresToNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/Scoreboard;getScores()Ljava/util/Collection;"))
    private Collection<ScoreboardScore> getScores(Scoreboard instance) {
        return ((IScoreboard) instance).bismuthServer$getScores();
    }

    @Redirect(method = "scoresToNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NbtCompound;putInt(Ljava/lang/String;I)V"))
    private void get(NbtCompound instance, String key, int value, @Local ScoreboardScore scoreboardScore) {
        instance.putLong(key, ((IScoreboardScore) scoreboardScore).bismuthServer$getLongScore());
    }

    /**
     * @author thdaele
     * @reason Force storing the 64bit scoreboard as single long
     */
//    @Overwrite
//    protected void readScoresFromNbt(NbtList nbt) {
//        for(int i = 0; i < nbt.size(); ++i) {
//            NbtCompound nbtCompound = nbt.getCompound(i);
//            ScoreboardObjective scoreboardObjective = this.scoreboard.getObjective(nbtCompound.getString("Objective"));
//            String string = nbtCompound.getString("Name");
//            if (string.length() > 40) {
//                string = string.substring(0, 40);
//            }
//
//            long score = nbtCompound.getLong("Score");
//
//            // Lower bits
//            ScoreboardScore scoreboardScore = this.scoreboard.getScore(string, scoreboardObjective);
//            scoreboardScore.setLocked(nbtCompound.getBoolean("Locked"));
//            ((IScoreboardScore) scoreboardScore).bismuthServer$setLongScore(score, string, scoreboardObjective);
//        }
//    }

    @Inject(method = "readScoresFromNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/Scoreboard;getScore(Ljava/lang/String;Lnet/minecraft/scoreboard/ScoreboardObjective;)Lnet/minecraft/scoreboard/ScoreboardScore;"))
    private void LongNBT(NbtList nbt, CallbackInfo ci, @Local NbtCompound nbtCompound, @Share("scoreValue") LocalLongRef argRef) {
        long score = nbtCompound.getLong("Score");
        argRef.set(score);
    }

    @WrapOperation(method = "readScoresFromNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/ScoreboardScore;set(I)V"))
    private void setScoreboard(ScoreboardScore instance, int score, Operation<Void> original, @Share("scoreValue") LocalLongRef argRef, @Local String owner, @Local ScoreboardObjective scoreboardObjective) {
        ((IScoreboardScore) instance).bismuthServer$setLongScore(argRef.get(), owner, scoreboardObjective);
    }
}

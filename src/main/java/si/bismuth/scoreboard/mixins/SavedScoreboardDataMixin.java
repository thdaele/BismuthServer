package si.bismuth.scoreboard.mixins;

import com.google.common.collect.Maps;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.scoreboard.SavedScoreboardData;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import si.bismuth.scoreboard.IScoreboard;
import si.bismuth.scoreboard.IScoreboardSaveData;
import si.bismuth.scoreboard.LongScore;

import java.util.Collection;
import java.util.Map;

@Mixin(SavedScoreboardData.class)
public class SavedScoreboardDataMixin implements IScoreboardSaveData {
    @Shadow
    private Scoreboard scoreboard;

    @Inject(method="setScoreboard", at=@At(value="TAIL", target="Lnet/minecraft/scoreboard/SavedScoreboardData;readNbt(Lnet/minecraft/nbt/NbtCompound;)V"))
    private void updateTotals(CallbackInfo ci){
        final Map<ScoreboardObjective, Long> totalsMap = Maps.<ScoreboardObjective, Long>newHashMap();
        IScoreboard scoreboard = (IScoreboard) this.scoreboard;

        for (LongScore longScore : scoreboard.bismuthServer$getLongScores()){
            if (!"Total".equals(longScore.getOwner())){
                totalsMap.put(longScore.getObjective(), totalsMap.getOrDefault(longScore.getObjective(), 0L) + longScore.get());
            }
        }

        for (ScoreboardObjective objective : totalsMap.keySet()){
            long total = totalsMap.get(objective);
            scoreboard.bismuthServer$getLongScore("Total", objective).set(total);
        }
    }

    @Redirect(method = "readNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/SavedScoreboardData;readScoresFromNbt(Lnet/minecraft/nbt/NbtList;)V"))
    private void readFromNBT(SavedScoreboardData instance, NbtList p_readScores_1_) {
        IScoreboardSaveData scoreboardSaveData = (IScoreboardSaveData) instance;
        scoreboardSaveData.bismuthServer$readScores(p_readScores_1_);
    }

    @Redirect(method = "writeNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/SavedScoreboardData;scoresToNbt()Lnet/minecraft/nbt/NbtList;"))
    private NbtList writeToNBT(SavedScoreboardData instance) {
        IScoreboardSaveData scoreboardSaveData = (IScoreboardSaveData) instance;
        return scoreboardSaveData.bismuthServer$scoresToNbt();
    }

    @Override
    public void bismuthServer$readScores(NbtList p_readScores_1_) {
        for(int lvt_2_1_ = 0; lvt_2_1_ < p_readScores_1_.size(); ++lvt_2_1_) {
            NbtCompound lvt_3_1_ = p_readScores_1_.getCompound(lvt_2_1_);
            ScoreboardObjective lvt_4_1_ = this.scoreboard.getObjective(lvt_3_1_.getString("Objective"));
            String lvt_5_1_ = lvt_3_1_.getString("Name");
            if (lvt_5_1_.length() > 40) {
                lvt_5_1_ = lvt_5_1_.substring(0, 40);
            }

            IScoreboard scoreboard = (IScoreboard) this.scoreboard;
            LongScore lvt_6_1_ = scoreboard.bismuthServer$getLongScore(lvt_5_1_, lvt_4_1_);
            lvt_6_1_.set(lvt_3_1_.getLong("Score"));
            if (lvt_3_1_.contains("Locked")) {
                lvt_6_1_.setLocked(lvt_3_1_.getBoolean("Locked"));
            }
        }
    }

    @Override
    public NbtList bismuthServer$scoresToNbt() {
        NbtList lvt_1_1_ = new NbtList();
        IScoreboard scoreboard = (IScoreboard) this.scoreboard;
        Collection<LongScore> lvt_2_1_ = scoreboard.bismuthServer$getLongScores();

        for (LongScore lvt_4_1_ : lvt_2_1_) {
            if (lvt_4_1_.getObjective() != null) {
                NbtCompound lvt_5_1_ = new NbtCompound();
                lvt_5_1_.putString("Name", lvt_4_1_.getOwner());
                lvt_5_1_.putString("Objective", lvt_4_1_.getObjective().getName());
                lvt_5_1_.putLong("Score", lvt_4_1_.get());
                lvt_5_1_.putBoolean("Locked", lvt_4_1_.isLocked());
                lvt_1_1_.add(lvt_5_1_);
            }
        }
        return lvt_1_1_;
    }
}

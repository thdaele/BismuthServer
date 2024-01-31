package si.bismuth.scoreboard.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardScore;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import si.bismuth.scoreboard.IScoreboardScore;

import static si.bismuth.utils.ScoreboardHelper.getUpperScoreboardScoreName;

@Mixin(ScoreboardScore.class)
public abstract class ScoreboardScoreMixin implements IScoreboardScore {
    @Shadow @Final
    private Scoreboard scoreboard;
    @Shadow @Final
    private ScoreboardObjective objective;
    @Shadow @Final
    private String owner;

    @Unique
    private ScoreboardScore upperScore = null;

    @Shadow
    public abstract int get();

    @Shadow
    public abstract void set(int score);

    @Shadow public abstract String getOwner();

    @Shadow public abstract ScoreboardObjective getObjective();

    @Inject(method= "increase", at = @At("RETURN"))
    private void increaseTotal(int amount, CallbackInfo ci){
        if (!"Total".equals(owner)) {
            ScoreboardScore totalScore = scoreboard.getScore("Total", objective);
            totalScore.increase(amount);
        }
    }

    @Redirect(method = "increase", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/ScoreboardScore;set(I)V"))
    private void increase(ScoreboardScore instance, int score, @Local(argsOnly = true) int amount) {
        this.bismuthServer$setLongScore(this.bismuthServer$getLongScore() + (long) amount, this.getOwner(), this.getObjective());
    }

    @Redirect(method = "decrease", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/ScoreboardScore;set(I)V"))
    private void decrease(ScoreboardScore instance, int score, @Local(argsOnly = true) int amount) {
        this.bismuthServer$setLongScore(this.bismuthServer$getLongScore() - (long) amount, this.getOwner(), this.getObjective());
    }

    @Inject(method = "setLocked", at = @At("TAIL"))
    private void setLocked(boolean locked, CallbackInfo ci) {
        if (upperScore != null) {
            upperScore.setLocked(locked);
        }
    }

    public Long bismuthServer$getLongScore() {
        int lowerBits = this.get();
        if (upperScore == null) {
            bismuthServer$createUpperScore(owner, objective);
        }
        int higher_bits = upperScore.get();

        return (((long) higher_bits) << 32) | (lowerBits & 0xffffffffL);
    }

    public void bismuthServer$setLongScore(Long value, String owner, ScoreboardObjective objective) {
        this.set(value.intValue());

        if (upperScore == null) {
            bismuthServer$createUpperScore(owner, objective);
        }
        upperScore.set((int)(value >> 32));
    }

    public void bismuthServer$createUpperScore(String owner, ScoreboardObjective objective) {
        upperScore = this.scoreboard.getScore(getUpperScoreboardScoreName(owner), objective);
    }
}

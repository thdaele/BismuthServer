package si.bismuth.scoreboard.mixins;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.scoreboard.*;
import net.minecraft.scoreboard.criterion.ScoreboardCriterion;
import net.minecraft.scoreboard.team.Team;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.server.stat.ServerPlayerStats;
import net.minecraft.stat.Stat;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import si.bismuth.scoreboard.IScoreboard;
import si.bismuth.scoreboard.LongScore;

import java.util.Collection;
import java.util.Iterator;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    private ServerPlayerEntityMixin(World world, GameProfile profile) {
        super(world, profile);
    }

    @Shadow @Final private ServerPlayerStats stats;

    @Shadow
    public abstract boolean isSpectator();

    /**
     * @author brrr
     */
    @Overwrite
    private void updateScores(ScoreboardCriterion criterion, int value) {
        Collection<ScoreboardObjective> objectives = this.getScoreboard().getObjectives(criterion);

        for (ScoreboardObjective objective : objectives) {
            LongScore score = ((IScoreboard) this.getScoreboard()).getLongScore(this.getName(), objective);
            score.set(value);
        }
    }

    //TODO For some reason it can't capture the collection like wtf currently using above overwrite
//    @Inject(method = "updateScores", at = @At(value = "INVOKE", target = "Ljava/util/Collection;iterator()Ljava/util/Iterator;"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
//    private void updateScorePoints(IScoreCriteria p_updateScorePoints_1_, int p_updateScorePoints_2_, CallbackInfo ci, Collection<ScoreObjective> lvt_3_1_) {
//        for (ScoreObjective lvt_5_1_ : lvt_3_1_) {
//            LongScore lvt_6_1_ = ((IScoreboard) this.getWorldScoreboard()).getOrCreateScore(this.getName(), lvt_5_1_);
//            lvt_6_1_.setScorePoints(p_updateScorePoints_2_);
//        }
//        ci.cancel();
//    }

    //TODO conflicts with collection when using CAPTURE_FAILHARD
    @Inject(method = "onKilled", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/entity/living/player/ServerPlayerEntity;getScoreboard()Lnet/minecraft/scoreboard/Scoreboard;"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onDeath(DamageSource p_onDeath_1_, CallbackInfo ci, boolean lvt_2_1_, Collection<ScoreboardObjective> lvt_3_2_, Iterator<ScoreboardObjective> var4, ScoreboardObjective lvt_5_1_) {
        LongScore lvt_6_1_ = ((IScoreboard) this.getScoreboard()).getLongScore(this.getName(), lvt_5_1_);
        lvt_6_1_.increment();
    }

    @Redirect(method = "onKilled", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/Scoreboard;getScore(Ljava/lang/String;Lnet/minecraft/scoreboard/ScoreboardObjective;)Lnet/minecraft/scoreboard/ScoreboardScore;"))
    private ScoreboardScore onDeath2(Scoreboard instance, String p_getOrCreateScore_1_, ScoreboardObjective p_getOrCreateScore_2_) {
        // Return dummy score that will be garbage collected later on to prevent filling up the old scoreboard datastructure
        return new ScoreboardScore(instance, p_getOrCreateScore_2_, p_getOrCreateScore_1_);
    }

    @Redirect(method = "m_0114784", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/Scoreboard;getScore(Ljava/lang/String;Lnet/minecraft/scoreboard/ScoreboardObjective;)Lnet/minecraft/scoreboard/ScoreboardScore;"))
    private ScoreboardScore awardKillScore(Scoreboard instance, String p_getOrCreateScore_1_, ScoreboardObjective p_getOrCreateScore_2_) {
        ((IScoreboard) instance).getLongScore(p_getOrCreateScore_1_, p_getOrCreateScore_2_).increment();
        // Return dummy score that will be garbage collected later on to prevent filling up the old scoreboard datastructure
        return new ScoreboardScore(instance, p_getOrCreateScore_2_, p_getOrCreateScore_1_);
    }

    @Inject(method = "m_0114784", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/entity/living/player/ServerPlayerEntity;getScoreboard()Lnet/minecraft/scoreboard/Scoreboard;", ordinal = 2), locals = LocalCapture.CAPTURE_FAILHARD)
    private void awardTeamKillScores(Entity p_awardTeamKillScores_1_, CallbackInfoReturnable<Collection<ScoreboardObjective>> cir, String lvt_2_1_, Team lvt_3_1_, int lvt_4_1_, Iterator<ScoreboardObjective> var5, ScoreboardObjective lvt_6_1_) {
        LongScore longScore = ((IScoreboard) this.getScoreboard()).getLongScore(this.getName(), lvt_6_1_);
        longScore.increment();
    }

    @Redirect(method = "m_0114784", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/Scoreboard;getScore(Ljava/lang/String;Lnet/minecraft/scoreboard/ScoreboardObjective;)Lnet/minecraft/scoreboard/ScoreboardScore;"))
    private ScoreboardScore awardTeamKillScores2(Scoreboard instance, String p_getOrCreateScore_1_, ScoreboardObjective p_getOrCreateScore_2_) {
        // Return dummy score that will be garbage collected later on to prevent filling up the old scoreboard datastructure
        return new ScoreboardScore(instance, p_getOrCreateScore_2_, p_getOrCreateScore_1_);
    }

    @Redirect(method = "incrementStat", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/Scoreboard;getScore(Ljava/lang/String;Lnet/minecraft/scoreboard/ScoreboardObjective;)Lnet/minecraft/scoreboard/ScoreboardScore;"))
    private ScoreboardScore addStat(Scoreboard instance, String p_getOrCreateScore_1_, ScoreboardObjective p_getOrCreateScore_2_) {
        ((IScoreboard) instance).getLongScore(p_getOrCreateScore_1_, p_getOrCreateScore_2_).increment();
        // Return dummy score that will be garbage collected later on to prevent filling up the old scoreboard datastructure
        return new ScoreboardScore(instance, p_getOrCreateScore_2_, p_getOrCreateScore_1_);
    }

    @Inject(method = "updateScores", at = @At(value = "HEAD"), cancellable = true)
    void updateScorePoints(ScoreboardCriterion criterion, int value, CallbackInfo ci) {
        for(ScoreboardObjective objective : this.getScoreboard().getObjectives(criterion)) {
            LongScore score = ((IScoreboard)this.getScoreboard()).getLongScore(this.getName(), objective);
            score.set(value);
        }
        ci.cancel();
    }

    @Inject(method = "clearStat", at = @At(value = "HEAD"), cancellable = true)
    void takeStat(Stat stat, CallbackInfo ci) {
        if (stat != null) {
            this.stats.set(this, stat, 0);

            for(ScoreboardObjective objective : this.getScoreboard().getObjectives(stat.getCriterion())) {
                ((IScoreboard)this.getScoreboard()).getLongScore(this.getName(), objective).set(0);
            }

        }
        ci.cancel();
    }
}

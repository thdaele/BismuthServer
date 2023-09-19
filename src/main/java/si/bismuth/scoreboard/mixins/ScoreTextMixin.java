package si.bismuth.scoreboard.mixins;

import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.text.ScoreText;
import net.minecraft.text.StringUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import si.bismuth.scoreboard.IScoreboard;
import si.bismuth.scoreboard.LongScore;

@Mixin(ScoreText.class)
public abstract class ScoreTextMixin {
    @Shadow @Final private String owner;
    @Shadow @Final private String objective;
    @Shadow private String value;

    @Shadow
    public abstract void setValue(String p_setValue_1_);

    @Inject(method = "resolve", at = @At(value = "HEAD"), cancellable = true)
    void resolve(CommandSource source, CallbackInfo ci) {
        MinecraftServer server = source.getServer();
        if (server != null && server.hasGameDirectory() && StringUtils.isStringEmpty(this.value)) {
            Scoreboard scoreboard = server.getWorld(0).getScoreboard();
            ScoreboardObjective objective = scoreboard.getObjective(this.objective);
            if (scoreboard.hasScore(this.owner, objective)) {
                LongScore score = ((IScoreboard)scoreboard).getLongScore(this.owner, objective);
                this.setValue(String.format("%d", score.get()));
            } else {
                this.value = "";
            }
        }
        ci.cancel();
    }
}

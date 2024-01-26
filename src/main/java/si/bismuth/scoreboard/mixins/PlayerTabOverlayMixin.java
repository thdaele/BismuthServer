package si.bismuth.scoreboard.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.overlay.PlayerTabOverlay;
import net.minecraft.client.network.PlayerInfo;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import si.bismuth.scoreboard.IScoreboard;

@Mixin(PlayerTabOverlay.class)
public class PlayerTabOverlayMixin {
//    @Shadow @Final
//    private Minecraft minecraft;

    @ModifyVariable(method = "renderDisplayScore", at = @At("STORE"), ordinal = 1)
    private String injected(String original, @Local(argsOnly = true) ScoreboardObjective displayObjective, @Local(ordinal = 0, argsOnly = true) String owner) {
        IScoreboard scoreboard = (IScoreboard) displayObjective.getScoreboard();
        return Formatting.YELLOW + "" + scoreboard.bismuthServer$getLongScore(owner, displayObjective);
    }

    // The one above is a bit clearner bc doesn't duplicate code, I should test if it actually works tho

//    @Inject(method = "renderDisplayScore", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/TextRenderer;drawWithShadow(Ljava/lang/String;FFI)I", ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD)
//    private void renderDisplayScore(ScoreboardObjective displayObjective, int width, String owner, int x, int y, PlayerInfo player, CallbackInfo ci, int i, String string2) {
//        IScoreboard scoreboard = (IScoreboard) displayObjective.getScoreboard();
//        string2 = Formatting.YELLOW + "" + scoreboard.bismuthServer$getLongScore(owner, displayObjective);
//        this.minecraft.textRenderer.drawWithShadow(string2, (float)(y - this.minecraft.textRenderer.getWidth(string2)), (float)width, 16777215);
//    }
}

package si.bismuth.scoreboard.mixins;

import net.minecraft.client.gui.overlay.PlayerTabOverlay;
import net.minecraft.client.network.PlayerInfo;
import net.minecraft.client.network.handler.ClientPlayNetworkHandler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;

@Mixin(PlayerTabOverlay.class)
public class PlayerTabOverlayMixin {
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/ScoreboardScore;get()I"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void render(int width, Scoreboard scoreboard, ScoreboardObjective displayObjective, CallbackInfo ci, ClientPlayNetworkHandler clientPlayNetworkHandler, List list, int i, int j, Iterator var8, PlayerInfo playerInfo, int k) {
    }
}

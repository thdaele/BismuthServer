package si.bismuth.stats.mixins.StatsScreen;

import net.minecraft.client.gui.screen.StatsScreen;
import net.minecraft.stat.Stat;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import si.bismuth.stats.IPlayerStats;
import si.bismuth.stats.IStat;

@Mixin(targets = "net/minecraft/client/gui/screen/StatsScreen$GeneralStatsListWidget")
public class GeneralStatsListWidgetMixin {
    @Shadow @Final
    StatsScreen parent;

    @Redirect(method = "renderEntry", at = @At(value = "INVOKE", target = "Lnet/minecraft/stat/Stat;format(I)Ljava/lang/String;"))
    public String renderEntry(Stat instance, int value) {
        return ((IStat) instance).bismuthServer$longFormat(((IPlayerStats) parent.stats).bismuthServer$getLongStat(instance));
    }
}

package si.bismuth.stats.mixins.StatsScreen;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalLongRef;
import net.minecraft.client.gui.screen.StatsScreen;
import net.minecraft.stat.PlayerStats;
import net.minecraft.stat.Stat;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import si.bismuth.stats.IPlayerStats;

@Mixin(targets = "net/minecraft/client/gui/screen/StatsScreen$EntityStatsListWidget")
public class EntityStatsListWidgetMixin {
    @Shadow @Final
    StatsScreen parent;

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/stat/PlayerStats;get(Lnet/minecraft/stat/Stat;)I"))
    public int init(PlayerStats instance, Stat stat) {
        return ((IPlayerStats) parent.stats).bismuthServer$getLongStat(stat) > 0L ? 1 : 0;
    }

    @Redirect(method = "renderEntry", at = @At(value = "INVOKE", target = "Lnet/minecraft/stat/PlayerStats;get(Lnet/minecraft/stat/Stat;)I", ordinal = 0))
    public int renderEntry1(PlayerStats instance, Stat stat, @Share("i") LocalLongRef iRef) {
        long i = ((IPlayerStats) parent.stats).bismuthServer$getLongStat(stat);
        iRef.set(i);
        return i > 0L ? 1 : 0;
    }

    @Redirect(method = "renderEntry", at = @At(value = "INVOKE", target = "Lnet/minecraft/stat/PlayerStats;get(Lnet/minecraft/stat/Stat;)I", ordinal = 1))
    public int renderEntry2(PlayerStats instance, Stat stat, @Share("j") LocalLongRef jRef) {
        long j = ((IPlayerStats) parent.stats).bismuthServer$getLongStat(stat);
        jRef.set(j);
        return j > 0L ? 1 : 0;
    }

    @ModifyArg(method = "renderEntry", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resource/language/I18n;translate(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;", ordinal = 1), index = 1)
    public Object[] renderEntry3(Object[] args, @Share("i") LocalLongRef iRef) {
        args[0] = iRef.get();
        return args;
    }

    @ModifyArg(method = "renderEntry", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resource/language/I18n;translate(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;", ordinal = 2), index = 1)
    public Object[] renderEntry4(Object[] args, @Share("i") LocalLongRef iRef) {
        args[1] = iRef.get();
        return args;
    }
}

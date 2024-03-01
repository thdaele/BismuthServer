package si.bismuth.stats.mixins.StatsScreen;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.screen.StatsScreen;
import net.minecraft.stat.ItemStat;
import net.minecraft.stat.PlayerStats;
import net.minecraft.stat.Stat;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import si.bismuth.BismuthClient;
import si.bismuth.BismuthServer;
import si.bismuth.stats.IPlayerStats;

@Mixin(StatsScreen.ItemStatsListWidget.class)
class ItemStatsListWidgetMixin {
    @Shadow @Final
    StatsScreen f_2839217;

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/stat/PlayerStats;get(Lnet/minecraft/stat/Stat;)I")
    )
    public int init(PlayerStats instance, Stat stat) {
        return ((IPlayerStats) f_2839217.stats).bismuthServer$getLongStat(stat) > 0L ? 1 : 0;
    }

    @ModifyConstant(method = "*", constant = @Constant(intValue = 115))
    private int injected1(int value) {
        return 140;
    }

    @ModifyConstant(method = "*", constant = @Constant(intValue = 165))
    private int injected2(int value) {
        return 240;
    }

    @ModifyConstant(method = "*", constant = @Constant(intValue = 215))
    private int injected3(int value) {
        return 340;
    }

    @ModifyConstant(method = "*", constant = @Constant(intValue = 265))
    private int injected4(int value) {
        return 440;
    }

    @ModifyConstant(method = "*", constant = @Constant(intValue = 315))
    private int injected5(int value) {
        return 540;
    }
}

@Mixin(targets = "net/minecraft/client/gui/screen/StatsScreen$ItemStatsListWidget$1")
class ItemStatsListWidgetStatComparatorMixin {
    @Shadow @Final
    StatsScreen f_5153485;

    @Shadow @Final
    StatsScreen.ItemStatsListWidget f_7207520;

    @Inject(method = "compare(Lnet/minecraft/stat/ItemStat;Lnet/minecraft/stat/ItemStat;)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/stat/PlayerStats;get(Lnet/minecraft/stat/Stat;)I"), cancellable = true)
    public void compare(ItemStat itemStat, ItemStat itemStat2, CallbackInfoReturnable<Integer> cir, @Local(ordinal = 0) Stat stat, @Local(ordinal = 1) Stat stat2, @Local(ordinal = 0) int i, @Local(ordinal = 1) int j) {
        BismuthClient.log.log(Level.WARN, "Yeeet");
        long k = ((IPlayerStats) f_5153485.stats).bismuthServer$getLongStat(stat);
        long l = ((IPlayerStats) f_5153485.stats).bismuthServer$getLongStat(stat2);
        if (k != l) {
            int order = (k - l) > 0 ? 1 : -1;
            cir.setReturnValue(order * f_7207520.f_2401102);
            cir.cancel();
            return;
        }

        cir.setReturnValue(i - j);
        cir.cancel();
    }
}

package si.bismuth.stats.mixins;

import net.minecraft.client.tutorial.CraftPlanksTutorialStep;
import net.minecraft.stat.PlayerStats;
import net.minecraft.stat.Stat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import si.bismuth.stats.IPlayerStats;

@Mixin(CraftPlanksTutorialStep.class)
public class CraftPlanksTutorialStepMixin {
    @Redirect(method = "wasPreviouslyCompleted", at = @At(value = "INVOKE", target = "Lnet/minecraft/stat/PlayerStats;get(Lnet/minecraft/stat/Stat;)I"))
    private static int wasPreviouslyCompleted(PlayerStats instance, Stat stat) {
        return ((IPlayerStats) instance).bismuthServer$getLongStat(stat) > 0L ? 1 : 0;
    }
}

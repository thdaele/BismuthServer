package si.bismuth.stats.mixins;

import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.server.stat.ServerPlayerStats;
import net.minecraft.stat.Stat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import si.bismuth.stats.IPlayerStats;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Redirect(method = "clearStat", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/stat/ServerPlayerStats;set(Lnet/minecraft/entity/living/player/PlayerEntity;Lnet/minecraft/stat/Stat;I)V"))
    public void clearStat(ServerPlayerStats instance, PlayerEntity player, Stat stat, int value) {
        ((IPlayerStats) instance).bismuthServer$setLongStat(player, stat, 0);
    }

    @Redirect(method = "incrementStat", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/stat/ServerPlayerStats;increment(Lnet/minecraft/entity/living/player/PlayerEntity;Lnet/minecraft/stat/Stat;I)V"))
    public void incrementStat(ServerPlayerStats instance, PlayerEntity player, Stat stat, int amount) {
        ((IPlayerStats) instance).bismuthServer$longIncrement(player, stat, amount);
    }
}

package si.bismuth.stats.mixins;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.server.stat.ServerPlayerStats;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatCounter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import si.bismuth.BismuthServer;
import si.bismuth.network.client.StatisticsPacket;
import si.bismuth.stats.IPlayerStats;
import si.bismuth.stats.IStatCounter;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static net.ornithemc.osl.networking.api.server.ServerPlayNetworking.canSend;

@Mixin(ServerPlayerStats.class)
public abstract class ServerPlayerStatsMixin extends PlayerStatsMixin {
    @Shadow
    private int lastUpdate;

    @Shadow @Final
    private Set<Stat> dirtyStats;

    @Shadow
    protected abstract Set<Stat> takeDirty();

    @Override
    public void bismuthServer$setLongStat(PlayerEntity player, Stat stat, long value) {
        super.bismuthServer$setLongStat(player, stat, value);
        this.dirtyStats.add(stat);
    }

    @Inject(method = "sendStats", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Maps;newHashMap()Ljava/util/HashMap;", remap = false), cancellable = true)
    public void sendStats(ServerPlayerEntity player, CallbackInfo ci, @Local int i) {
        if (canSend(player, "Bis|Stats")) {
            Map<Stat, Long> map = Maps.newHashMap();
            if (i - this.lastUpdate > 300) {
                this.lastUpdate = i;

                for(Stat stat : this.takeDirty()) {
                    map.put(stat, ((IPlayerStats) this).bismuthServer$getLongStat(stat));
                }
            }
            BismuthServer.networking.sendPacket(player, new StatisticsPacket(map));

            ci.cancel();
        }
        // If player doesn't have the 64bit support we resort to the normal sending of the packets
    }

    @Redirect(method = "sendStats", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/stat/ServerPlayerStats;get(Lnet/minecraft/stat/Stat;)I"))
    public int sendStatsVanilla(ServerPlayerStats instance, Stat stat) {
        // Cast all the long stats to int
        return (int) ((IPlayerStats) instance).bismuthServer$getLongStat(stat);
    }

    @Redirect(method = "deserialize", at = @At(value = "INVOKE", target = "Lnet/minecraft/stat/StatCounter;setValue(I)V", ordinal = 0))
    public void deserialize1(StatCounter statCounter, int value, @Local Entry<String, JsonElement> entry) {
        ((IStatCounter) statCounter).bismuthServer$setLongValue(entry.getValue().getAsLong());
    }

    @Redirect(method = "deserialize", at = @At(value = "INVOKE", target = "Lnet/minecraft/stat/StatCounter;setValue(I)V", ordinal = 1))
    public void deserialize2(StatCounter statCounter, int value, @Local(ordinal = 1) JsonObject jsonObject2) {
        ((IStatCounter) statCounter).bismuthServer$setLongValue(jsonObject2.getAsJsonPrimitive("value").getAsLong());
    }

    @Redirect(method = "serialize", at = @At(value = "INVOKE", target = "Lcom/google/gson/JsonObject;addProperty(Ljava/lang/String;Ljava/lang/Number;)V", ordinal = 0, remap = false))
    private static void serialize1(JsonObject jsonObject, String property, Number value, @Local Entry<Stat, StatCounter> entry) {
        jsonObject.addProperty("value", ((IStatCounter) entry.getValue()).bismuthServer$getLongValue());
    }

    @Redirect(method = "serialize", at = @At(value = "INVOKE", target = "Lcom/google/gson/JsonObject;addProperty(Ljava/lang/String;Ljava/lang/Number;)V", ordinal = 1, remap = false))
    private static void serialize2(JsonObject jsonObject, String property, Number value, @Local Entry<Stat, StatCounter> entry) {
        jsonObject.addProperty(entry.getKey().key, ((IStatCounter) entry.getValue()).bismuthServer$getLongValue());
    }
}

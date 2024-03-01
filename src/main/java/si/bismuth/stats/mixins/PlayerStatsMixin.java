package si.bismuth.stats.mixins;

import com.google.common.collect.Maps;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.stat.PlayerStats;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatCounter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import si.bismuth.stats.IPlayerStats;
import si.bismuth.stats.IStatCounter;

import java.util.Map;

@Mixin(PlayerStats.class)
public class PlayerStatsMixin implements IPlayerStats {
    @Shadow @Final
    protected Map<Stat, StatCounter> counters = Maps.<Stat, StatCounter>newConcurrentMap();

    public void bismuthServer$longIncrement(PlayerEntity player, Stat stat, long amount) {
        this.bismuthServer$setLongStat(player, stat, this.bismuthServer$getLongStat(stat) + amount);
    }

    public void bismuthServer$setLongStat(PlayerEntity player, Stat stat, long value) {
        IStatCounter statCounter = (IStatCounter) this.counters.get(stat);
        if (statCounter == null) {
            statCounter = (IStatCounter) new StatCounter();
            this.counters.put(stat, (StatCounter) statCounter);
        }

        statCounter.bismuthServer$setLongValue(value);
    }

    public long bismuthServer$getLongStat(Stat stat) {
        IStatCounter statCounter = (IStatCounter) this.counters.get(stat);
        return statCounter == null ? 0 : statCounter.bismuthServer$getLongValue();
    }
}

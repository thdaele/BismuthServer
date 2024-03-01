package si.bismuth.stats;

import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.stat.Stat;

public interface IPlayerStats {
    void bismuthServer$longIncrement(PlayerEntity player, Stat stat, long amount);

    void bismuthServer$setLongStat(PlayerEntity player, Stat stat, long value);

    long bismuthServer$getLongStat(Stat stat);
}

package si.bismuth.stats.mixins;

import net.minecraft.stat.StatCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import si.bismuth.stats.IStatCounter;

@Mixin(StatCounter.class)
public class StatCounterMixin implements IStatCounter {
    @Unique
    private long value;

    public Long bismuthServer$getLongValue() {
        return value;
    }

    public void bismuthServer$setLongValue(Long value) {
        this.value = value;
    }
}

package si.bismuth.stats.mixins;

import net.minecraft.stat.Stat;
import net.minecraft.stat.StatFormatter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import si.bismuth.stats.IStat;
import si.bismuth.stats.LongStatFormatter;

@Mixin(Stat.class)
public class StatMixin implements IStat {
    @Shadow @Final
    private StatFormatter formatter;
    @Override
    public String bismuthServer$longFormat(long value) {
        return ((LongStatFormatter) this.formatter).bismuthServer$format(value);
    }
}

@Mixin(targets = "net/minecraft/stat/Stat$1")
class NumberFormatterMixin implements LongStatFormatter {
    @Override
    public String bismuthServer$format(long value) {
        return Stat.NUMBER_FORMAT.format(value);
    }
}

@Mixin(targets = "net/minecraft/stat/Stat$2")
class TimeFormatterMixin implements LongStatFormatter {
    @Override
    public String bismuthServer$format(long value) {
        double d = (double)value / 20.0;
        double e = d / 60.0;
        double f = e / 60.0;
        double g = f / 24.0;
        double h = g / 365.0;
        if (h > 0.5) {
            return Stat.DECIMAL_FORMAT.format(h) + " y";
        } else if (g > 0.5) {
            return Stat.DECIMAL_FORMAT.format(g) + " d";
        } else if (f > 0.5) {
            return Stat.DECIMAL_FORMAT.format(f) + " h";
        } else {
            return e > 0.5 ? Stat.DECIMAL_FORMAT.format(e) + " m" : d + " s";
        }
    }
}

@Mixin(targets = "net/minecraft/stat/Stat$3")
class DistanceFormatterMixin implements LongStatFormatter {
    @Override
    public String bismuthServer$format(long value) {
        double d = (double)value / 100.0;
        double e = d / 1000.0;
        if (e > 0.5) {
            return Stat.DECIMAL_FORMAT.format(e) + " km";
        } else {
            return d > 0.5 ? Stat.DECIMAL_FORMAT.format(d) + " m" : value + " cm";
        }
    }
}

@Mixin(targets = "net/minecraft/stat/Stat$4")
class DivideByTenFormatterMixin implements LongStatFormatter {
    @Override
    public String bismuthServer$format(long value) {
        return Stat.DECIMAL_FORMAT.format((double)value * 0.1);
    }
}

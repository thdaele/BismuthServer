package si.bismuth.scoreboard.mixins;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.command.TargetSelector;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import si.bismuth.scoreboard.IScoreboard;
import si.bismuth.scoreboard.LongScore;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Mixin(TargetSelector.class)
public class TargetSelectorMixin {
    @Unique
    private static long getLong(String string, long defaultValue) {
        try {
            return Long.parseLong(string);
        } catch (Throwable var3) {
            return defaultValue;
        }
    }

    @Unique
    private static Map<String, Long> getLongScoreMap(Map<String, String> args) {
        Map<String, Long> map = Maps.newHashMap();

        for(String arg : args.keySet()) {
            if (arg.startsWith("score_") && arg.length() > "score_".length()) {
                args.put(arg.substring("score_".length()), String.valueOf(getLong(args.get(arg), 1L)));
            }
        }

        return map;
    }

    @Inject(method = "getScoreboardPredicates", at = @At(value = "HEAD"), cancellable = true)
    private static void getScoreboardPredicates(CommandSource source, Map<String, String> args, CallbackInfoReturnable<List<Predicate<Entity>>> cir) {
        final Map<String, Long> map = getLongScoreMap(args);
        cir.setReturnValue ((List)(map.isEmpty() ? Collections.emptyList() : Lists.newArrayList(new Predicate[]{new Predicate<Entity>() {
            public boolean apply(@Nullable Entity entity) {
                if (entity == null) {
                    return false;
                } else {
                    Scoreboard scoreboard = source.getServer().getWorld(0).getScoreboard();
                    Iterator var3 = map.entrySet().iterator();

                    Map.Entry entry;
                    boolean bl;
                    long i;
                    do {
                        if (!var3.hasNext()) {
                            return true;
                        }

                        entry = (Map.Entry)var3.next();
                        String string = (String)entry.getKey();
                        bl = false;
                        if (string.endsWith("_min") && string.length() > 4) {
                            bl = true;
                            string = string.substring(0, string.length() - 4);
                        }

                        ScoreboardObjective scoreboardObjective = scoreboard.getObjective(string);
                        if (scoreboardObjective == null) {
                            return false;
                        }

                        String string2 = entity instanceof ServerPlayerEntity ? entity.getName() : entity.getScoreboardName();
                        if (!scoreboard.hasScore(string2, scoreboardObjective)) {
                            return false;
                        }

                        LongScore scoreboardScore = ((IScoreboard)scoreboard).bismuthServer$getLongScore(string2, scoreboardObjective);
                        i = scoreboardScore.get();
                        if (i < (Integer)entry.getValue() && bl) {
                            return false;
                        }
                    } while(i <= (Integer)entry.getValue() || bl);

                    return false;
                }
            }
        }})));
    }
}

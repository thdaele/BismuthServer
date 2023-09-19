package si.bismuth.scoreboard.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.criterion.ScoreboardCriterion;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.AbstractCommand;
import net.minecraft.server.command.TriggerCommand;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import si.bismuth.scoreboard.IScoreboard;
import si.bismuth.scoreboard.LongScore;

@Mixin(TriggerCommand.class)
public abstract class TriggerCommandMixin extends AbstractCommand {
    @Inject(method = "run", at = @At(value = "HEAD"), cancellable = true)
    void execute(MinecraftServer server, CommandSource source, String[] args, CallbackInfo ci) throws CommandException {
        if (args.length < 3) {
            throw new IncorrectUsageException("commands.trigger.usage", new Object[0]);
        } else {
            ServerPlayerEntity lvt_4_1_;
            if (source instanceof ServerPlayerEntity) {
                lvt_4_1_ = (ServerPlayerEntity)source;
            } else {
                Entity lvt_5_1_ = source.asEntity();
                if (!(lvt_5_1_ instanceof ServerPlayerEntity)) {
                    throw new CommandException("commands.trigger.invalidPlayer", new Object[0]);
                }

                lvt_4_1_ = (ServerPlayerEntity)lvt_5_1_;
            }

            Scoreboard lvt_5_2_ = server.getWorld(0).getScoreboard();
            ScoreboardObjective lvt_6_1_ = lvt_5_2_.getObjective(args[0]);
            if (lvt_6_1_ != null && lvt_6_1_.getCriterion() == ScoreboardCriterion.TRIGGER) {
                long lvt_7_1_ = parseLong(args[2]);
                if (!lvt_5_2_.hasScore(lvt_4_1_.getName(), lvt_6_1_)) {
                    throw new CommandException("commands.trigger.invalidObjective", new Object[]{args[0]});
                } else {
                    LongScore lvt_8_1_ = ((IScoreboard)lvt_5_2_).bismuthServer$getLongScore(lvt_4_1_.getName(), lvt_6_1_);
                    if (lvt_8_1_.isLocked()) {
                        throw new CommandException("commands.trigger.disabled", new Object[]{args[0]});
                    } else {
                        if ("set".equals(args[1])) {
                            lvt_8_1_.set(lvt_7_1_);
                        } else {
                            if (!"add".equals(args[1])) {
                                throw new CommandException("commands.trigger.invalidMode", new Object[]{args[1]});
                            }

                            lvt_8_1_.increase(lvt_7_1_);
                        }

                        lvt_8_1_.setLocked(true);
                        if (lvt_4_1_.interactionManager.isCreative()) {
                            sendSuccess(
                                    source, this, "commands.trigger.success", new Object[]{args[0], args[1], args[2]}
                            );
                        }

                    }
                }
            } else {
                throw new CommandException("commands.trigger.invalidObjective", new Object[]{args[0]});
            }
        }
        ci.cancel();
    }
}

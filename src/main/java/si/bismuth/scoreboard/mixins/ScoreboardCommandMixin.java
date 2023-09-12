package si.bismuth.scoreboard.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.SnbtParser;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.AbstractCommand;
import net.minecraft.server.command.ScoreboardCommand;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.exception.CommandSyntaxException;
import net.minecraft.server.command.source.CommandResults;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.text.Formatting;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import si.bismuth.scoreboard.IScoreboard;
import si.bismuth.scoreboard.LongScore;

import java.util.Collection;
import java.util.Map;

@Mixin(ScoreboardCommand.class)
public abstract class ScoreboardCommandMixin extends AbstractCommand {
    @Shadow
    protected abstract Scoreboard getScoreboard(MinecraftServer server);

    @Shadow
    protected abstract ScoreboardObjective getObjective(String name, boolean write, MinecraftServer server);

    @Inject(method = "listPlayers", at = @At(value = "HEAD"), cancellable = true)
    void listPlayers(CommandSource source, String[] args, int index, MinecraftServer server, CallbackInfo ci) throws CommandException {
        Scoreboard scoreboard = this.getScoreboard(server);
        if (args.length > index) {
            String name = parseEntityName(server, source, args[index]);
            Map<ScoreboardObjective, LongScore> scores = ((IScoreboard)scoreboard).getLongScores(name);
            source.addResult(CommandResults.Type.QUERY_RESULT, scores.size());
            if (scores.isEmpty()) {
                throw new CommandException("commands.scoreboard.players.list.player.empty", new Object[]{name});
            }

            TranslatableText message = new TranslatableText(
                    "commands.scoreboard.players.list.player.count", scores.size(), name
            );
            message.getStyle().setColor(Formatting.DARK_GREEN);
            source.sendMessage(message);

            for(LongScore score : scores.values()) {
                source.sendMessage(
                        new TranslatableText(
                                "commands.scoreboard.players.list.player.entry",
                                score.get(), score.getObjective().getDisplayName(), score.getObjective().getName()
                        )
                );
            }
        } else {
            Collection<String> owners = scoreboard.getScoreOwners();
            source.addResult(CommandResults.Type.QUERY_RESULT, owners.size());
            if (owners.isEmpty()) {
                throw new CommandException("commands.scoreboard.players.list.empty", new Object[0]);
            }

            TranslatableText message = new TranslatableText("commands.scoreboard.players.list.count", new Object[]{owners.size()});
            message.getStyle().setColor(Formatting.DARK_GREEN);
            source.sendMessage(message);
            source.sendMessage(new LiteralText(listArgs(owners.toArray())));
        }
        ci.cancel();
    }

    @Inject(method = "setScore", at = @At(value = "HEAD"), cancellable = true)
    void addPlayerScore(CommandSource source, String[] args, int index, MinecraftServer server, CallbackInfo ci) throws CommandException {
        String lvt_5_1_ = args[index - 1];
        int lvt_6_1_ = index;
        String lvt_7_1_ = parseEntityName(server, source, args[index++]);
        if (lvt_7_1_.length() > 40) {
            throw new CommandSyntaxException("commands.scoreboard.players.name.tooLong", new Object[]{lvt_7_1_, 40});
        } else {
            ScoreboardObjective lvt_8_1_ = this.getObjective(args[index++], true, server);
            long lvt_9_1_ = "set".equalsIgnoreCase(lvt_5_1_)
                    ? parseLong(args[index++])
                    : parseLong(args[index++], 0L, Long.MAX_VALUE);
            if (args.length > index) {
                Entity lvt_10_1_ = parseEntity(server, source, args[lvt_6_1_]);

                try {
                    NbtCompound lvt_11_1_ = SnbtParser.parse(parseString(args, index));
                    NbtCompound lvt_12_1_ = getEntityNbt(lvt_10_1_);
                    if (!NbtUtils.matches(lvt_11_1_, lvt_12_1_, true)) {
                        throw new CommandException("commands.scoreboard.players.set.tagMismatch", new Object[]{lvt_7_1_});
                    }
                } catch (NbtException var13) {
                    throw new CommandException("commands.scoreboard.players.set.tagError", new Object[]{var13.getMessage()});
                }
            }

            Scoreboard lvt_10_2_ = this.getScoreboard(server);
            LongScore lvt_11_3_ = ((IScoreboard)lvt_10_2_).getLongScore(lvt_7_1_, lvt_8_1_);
            if ("set".equalsIgnoreCase(lvt_5_1_)) {
                lvt_11_3_.set(lvt_9_1_);
            } else if ("add".equalsIgnoreCase(lvt_5_1_)) {
                lvt_11_3_.increase(lvt_9_1_);
            } else {
                lvt_11_3_.decrease(lvt_9_1_);
            }

            sendSuccess(
                    source, this, "commands.scoreboard.players.set.success", new Object[]{lvt_8_1_.getName(), lvt_7_1_, lvt_11_3_.get()}
            );
        }
        ci.cancel();
    }

    @Inject(method = "testScore", at = @At(value = "HEAD"), cancellable = true)
    void testPlayerScore(CommandSource source, String[] args, int index, MinecraftServer server, CallbackInfo ci) throws CommandException {
        Scoreboard lvt_5_1_ = this.getScoreboard(server);
        String lvt_6_1_ = parseEntityName(server, source, args[index++]);
        if (lvt_6_1_.length() > 40) {
            throw new CommandSyntaxException("commands.scoreboard.players.name.tooLong", new Object[]{lvt_6_1_, 40});
        } else {
            ScoreboardObjective lvt_7_1_ = this.getObjective(args[index++], false, server);
            if (!lvt_5_1_.hasScore(lvt_6_1_, lvt_7_1_)) {
                throw new CommandException("commands.scoreboard.players.test.notFound", new Object[]{lvt_7_1_.getName(), lvt_6_1_});
            } else {
                long lvt_8_1_ = args[index].equals("*")
                        ? Long.MIN_VALUE
                        : parseLong(args[index]);
                ++index;
                long lvt_9_1_ = index < args.length && !args[index].equals("*")
                        ? parseLong(args[index], lvt_8_1_, Long.MAX_VALUE)
                        : Long.MAX_VALUE;
                LongScore lvt_10_1_ = ((IScoreboard)lvt_5_1_).getLongScore(lvt_6_1_, lvt_7_1_);
                if (lvt_10_1_.get() >= lvt_8_1_ && lvt_10_1_.get() <= lvt_9_1_) {
                    sendSuccess(
                            source, this, "commands.scoreboard.players.test.success", new Object[]{lvt_10_1_.get(), lvt_8_1_, lvt_9_1_}
                    );
                } else {
                    throw new CommandException("commands.scoreboard.players.test.failed", new Object[]{lvt_10_1_.get(), lvt_8_1_, lvt_9_1_});
                }
            }
        }
        ci.cancel();
    }

    @Inject(method = "modifyScore", at = @At(value = "HEAD"), cancellable = true)
    void applyPlayerOperation(CommandSource source, String[] args, int index, MinecraftServer server, CallbackInfo ci) throws CommandException {
        Scoreboard lvt_5_1_ = this.getScoreboard(server);
        String lvt_6_1_ = parseEntityName(server, source, args[index++]);
        ScoreboardObjective lvt_7_1_ = this.getObjective(args[index++], true, server);
        String lvt_8_1_ = args[index++];
        String lvt_9_1_ = parseEntityName(server, source, args[index++]);
        ScoreboardObjective lvt_10_1_ = this.getObjective(args[index], false, server);
        if (lvt_6_1_.length() > 40) {
            throw new CommandSyntaxException("commands.scoreboard.players.name.tooLong", new Object[]{lvt_6_1_, 40});
        } else if (lvt_9_1_.length() > 40) {
            throw new CommandSyntaxException("commands.scoreboard.players.name.tooLong", new Object[]{lvt_9_1_, 40});
        } else {
            LongScore lvt_11_1_ = ((IScoreboard)lvt_5_1_).getLongScore(lvt_6_1_, lvt_7_1_);
            if (!lvt_5_1_.hasScore(lvt_9_1_, lvt_10_1_)) {
                throw new CommandException("commands.scoreboard.players.operation.notFound", new Object[]{lvt_10_1_.getName(), lvt_9_1_});
            } else {
                LongScore lvt_12_1_ = ((IScoreboard)lvt_5_1_).getLongScore(lvt_9_1_, lvt_10_1_);
                if ("+=".equals(lvt_8_1_)) {
                    lvt_11_1_.set(lvt_11_1_.get() + lvt_12_1_.get());
                } else if ("-=".equals(lvt_8_1_)) {
                    lvt_11_1_.set(lvt_11_1_.get() - lvt_12_1_.get());
                } else if ("*=".equals(lvt_8_1_)) {
                    lvt_11_1_.set(lvt_11_1_.get() * lvt_12_1_.get());
                } else if ("/=".equals(lvt_8_1_)) {
                    if (lvt_12_1_.get() != 0) {
                        lvt_11_1_.set(lvt_11_1_.get() / lvt_12_1_.get());
                    }
                } else if ("%=".equals(lvt_8_1_)) {
                    if (lvt_12_1_.get() != 0) {
                        lvt_11_1_.set(lvt_11_1_.get() % lvt_12_1_.get());
                    }
                } else if ("=".equals(lvt_8_1_)) {
                    lvt_11_1_.set(lvt_12_1_.get());
                } else if ("<".equals(lvt_8_1_)) {
                    lvt_11_1_.set(Math.min(lvt_11_1_.get(), lvt_12_1_.get()));
                } else if (">".equals(lvt_8_1_)) {
                    lvt_11_1_.set(Math.max(lvt_11_1_.get(), lvt_12_1_.get()));
                } else {
                    if (!"><".equals(lvt_8_1_)) {
                        throw new CommandException("commands.scoreboard.players.operation.invalidOperation", new Object[]{lvt_8_1_});
                    }

                    long lvt_13_1_ = lvt_11_1_.get();
                    lvt_11_1_.set(lvt_12_1_.get());
                    lvt_12_1_.set(lvt_13_1_);
                }

                sendSuccess(source, this, "commands.scoreboard.players.operation.success", new Object[0]);
            }
        }
        ci.cancel();
    }
}

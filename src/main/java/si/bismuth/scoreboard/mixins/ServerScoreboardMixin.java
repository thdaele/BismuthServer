package si.bismuth.scoreboard.mixins;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.ScoreboardDisplayS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardObjectiveS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardScoreS2CPacket;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardScore;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.scoreboard.ServerScoreboard;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import si.bismuth.BismuthServer;
import si.bismuth.network.server.UpdateScorePacket;
import si.bismuth.scoreboard.IScoreboard;
import si.bismuth.scoreboard.IServerScoreboard;
import si.bismuth.scoreboard.LongScore;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Mixin(ServerScoreboard.class)
public abstract class ServerScoreboardMixin extends Scoreboard implements IServerScoreboard {
    @Shadow @Final private MinecraftServer server;
    @Shadow @Final private Set<ScoreboardObjective> displayedObjectives;

    @Shadow protected abstract void markDirty();

    @Override
    public void bismuthServer$onScoreUpdated(LongScore longScore) {
        if (this.displayedObjectives.contains(longScore.getObjective())) {
            ScoreboardScore score = new ScoreboardScore((Scoreboard) longScore.getScoreboard(), longScore.getObjective(), longScore.getOwner());
            score.set((int)score.get());
            this.server.getPlayerManager().sendPacket(new ScoreboardScoreS2CPacket(score));

            BismuthServer.networking.sendPacket(new UpdateScorePacket(longScore));
        }
        this.markDirty();
    }

    /**
     * @author agressive in making 64 bit scoreboards work, greets dragonbabyfly
     */
    @Overwrite
    public List<Packet<?>> createStartDisplayingObjectivePackets(ScoreboardObjective objective) {
        List<Packet<?>> packets = new ArrayList<>();
        packets.add(new ScoreboardObjectiveS2CPacket(objective, 0));

        for(int slot = 0; slot < 19; ++slot) {
            if (this.getDisplayObjective(slot) == objective) {
                packets.add(new ScoreboardDisplayS2CPacket(slot, objective));
            }
        }

        IScoreboard scoreboard = (IScoreboard) this;
        for (LongScore longScore : scoreboard.bismuthServer$getLongScores(objective)) {
            BismuthServer.networking.sendPacket(new UpdateScorePacket(longScore));
            ScoreboardScore score = new ScoreboardScore((Scoreboard) longScore.getScoreboard(), longScore.getObjective(), longScore.getOwner());
            score.set((int)longScore.get());
            packets.add(new ScoreboardScoreS2CPacket(score));
        }

        return packets;
    }
}

package si.bismuth.network.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import si.bismuth.scoreboard.IScoreboard;
import si.bismuth.scoreboard.LongScore;

import java.io.IOException;

public class UpdateScorePacket implements ClientPacket {
    private String name = "";
    private String objectiveName = "";
    private long value;

    public UpdateScorePacket() {
    }

    @Override
	public void read(PacketByteBuf buffer) throws IOException {
		this.name = buffer.readString(40);
        this.objectiveName = buffer.readString(16);
        this.value = buffer.readVarLong();
	}

	@Override
	public void write(PacketByteBuf buffer) throws IOException {
		// noop
	}

	@Override
	public String getChannel() {
		return "Bis|updateScore";
	}

	@Override
	public void handle(Minecraft minecraft) {
        Scoreboard scoreboard = minecraft.world.getScoreboard();
        ScoreboardObjective objective = scoreboard.getObjective(objectiveName);
        LongScore score = ((IScoreboard) scoreboard).bismuthServer$getLongScore(name, objective);
        score.set(value);
	}
}

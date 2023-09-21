package si.bismuth.network.client;

import java.io.IOException;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import si.bismuth.BismuthClient;
import si.bismuth.scoreboard.IScoreboard;
import si.bismuth.scoreboard.LongScore;

public class ScorePacket implements ClientPacket {
	private String owner = "";
	private String objectiveName = "";
	private long score;

	public ScorePacket() {
	}

	public ScorePacket(LongScore longScore) {
		this.owner = longScore.getOwner();
		this.objectiveName = longScore.getObjective().getName();
		this.score = longScore.get();
	}

	@Override
	public void read(PacketByteBuf buffer) throws IOException {
		this.owner = buffer.readString(40);
		this.objectiveName = buffer.readString(16);
		this.score = buffer.readVarLong();
	}

	@Override
	public void write(PacketByteBuf buffer) throws IOException {
		buffer.writeString(this.owner);
		buffer.writeString(this.objectiveName);
		buffer.writeVarLong(this.score);
	}

	@Override
	public String getChannel() {
		return "Bis|Score";
	}

	@Override
	public void handle() {
		Scoreboard scoreboard = BismuthClient.minecraft.world.getScoreboard();
		ScoreboardObjective objective = scoreboard.getObjective(objectiveName);
		LongScore longScore = ((IScoreboard) scoreboard).bismuthServer$getLongScore(owner, objective);
		longScore.set(score);
	}
}

package si.bismuth.network;

import si.bismuth.scoreboard.LongScore;

import java.io.IOException;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;

public class UpdateScorePacket implements BisPacket {
    private String name = "";
    private String objective = "";
    private long value;

    public UpdateScorePacket() {
    }

    public UpdateScorePacket(LongScore longScore) {
        this.name = longScore.getOwner();
        this.objective = longScore.getObjective().getName();
        this.value = longScore.get();
    }

    @Override
	public void read(PacketByteBuf buffer) throws IOException {
		this.name = buffer.readString(40);
        this.objective = buffer.readString(16);
        this.value = buffer.readVarLong();
	}

	@Override
	public void write(PacketByteBuf buffer) throws IOException {
		buffer.writeString(this.name);
        buffer.writeString(this.objective);
        buffer.writeVarLong(this.value);
	}

	@Override
	public String getChannel() {
		return "Bis|updateScore";
	}

	@Override
	public void handle(ServerPlayerEntity player) {
		// noop
	}
}

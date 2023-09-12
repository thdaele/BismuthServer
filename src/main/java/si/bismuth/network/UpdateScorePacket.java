package si.bismuth.network;

import si.bismuth.scoreboard.LongScore;

import java.io.IOException;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;

@PacketChannelName("updateScore")
public class UpdateScorePacket extends BisPacket {
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
    public void writePacketData() {
        final PacketByteBuf buf = this.getPacketBuffer();
        buf.writeString(this.name);
        buf.writeString(this.objective);
        buf.writeVarLong(this.value);

    }

    @Override
    public void readPacketData(PacketByteBuf buf) throws IOException {
        this.name = buf.readString(40);
        this.objective = buf.readString(16);
        this.value = buf.readVarLong();
    }

    @Override
    public void processPacket(ServerPlayerEntity player) {

    }
}

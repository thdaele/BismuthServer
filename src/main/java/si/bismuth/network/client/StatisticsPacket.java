package si.bismuth.network.client;

import com.google.common.collect.Maps;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import si.bismuth.network.BisPacket;

import java.io.IOException;
import java.util.Map;

public class StatisticsPacket implements BisPacket {
    private Map<Stat, Long> stats;

    public StatisticsPacket() {
    }

    public StatisticsPacket(Map<Stat, Long> stats) {
        this.stats = stats;
    }

    @Override
    public String getChannel() {
        return "Bis|Stats";
    }

    @Override
    public void read(PacketByteBuf buffer) throws IOException {
        int i = buffer.readVarInt();
        this.stats = Maps.newHashMap();

        for(int j = 0; j < i; ++j) {
            Stat stat = Stats.byKey(buffer.readString(32767));
            long k = buffer.readVarLong();
            if (stat != null) {
                this.stats.put(stat, k);
            }
        }
    }

    @Override
    public void write(PacketByteBuf buffer) throws IOException {
        buffer.writeVarInt(this.stats.size());

        for(Map.Entry<Stat, Long> entry : this.stats.entrySet()) {
            buffer.writeString(entry.getKey().key);
            buffer.writeVarLong(entry.getValue());
        }
    }

    public Map<Stat, Long> getStats() {
        return this.stats;
    }
}

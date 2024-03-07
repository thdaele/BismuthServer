package si.bismuth.network.client;

import net.minecraft.client.gui.screen.menu.StatsListener;
import net.minecraft.stat.Stat;
import net.minecraft.util.math.BlockPos;
import si.bismuth.BismuthClient;
import si.bismuth.client.ParticleShowItems;
import si.bismuth.stats.IPlayerStats;

import java.util.Map;
import java.util.Random;

public class ClientNetworkHandler {
    private static final Random rng = new Random();
    public static void handleItemLocation(ItemLocationsPacket packet) {
        BismuthClient.minecraft.player.closeMenu();
        for (BlockPos pos : packet.getPositions()) {
            for (int i = 0; i < 20; i++) {
                BismuthClient.minecraft.particleManager.addParticle(new ParticleShowItems(BismuthClient.minecraft.player.world, pos.getX() + rng.nextDouble(), pos.getY() + rng.nextDouble(), pos.getZ() + rng.nextDouble(), 0D, 0D, 0D, 2F));
            }
        }
    }

    public static void handleInventoryContents(InventoryContentsPacket packet) {
        // TODO use data for something ig
    }

    public static void handleStatistics(StatisticsPacket packet) {
        for(Map.Entry<Stat, Long> entry : packet.getStats().entrySet()) {
            Stat stat = entry.getKey();
            long i = entry.getValue();
            ((IPlayerStats) BismuthClient.minecraft.player.getStats()).bismuthServer$setLongStat(BismuthClient.minecraft.player, stat, i);
        }

        if (BismuthClient.minecraft.screen instanceof StatsListener) {
            ((StatsListener)BismuthClient.minecraft.screen).m_6496620();
        }
    }
}

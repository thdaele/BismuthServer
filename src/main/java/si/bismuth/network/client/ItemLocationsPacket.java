package si.bismuth.network.client;

import java.io.IOException;
import java.util.Random;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.math.BlockPos;
import si.bismuth.BismuthClient;
import si.bismuth.client.ParticleShowItems;

public class ItemLocationsPacket implements ClientPacket {

	private static final Random rng = new Random();

	private DefaultedList<BlockPos> positions;

	public ItemLocationsPacket() {
	}

	public ItemLocationsPacket(DefaultedList<BlockPos> positions) {
		this.positions = positions;
	}

	@Override
	public void read(PacketByteBuf buffer) throws IOException {
		final int size = buffer.readVarInt();
		this.positions = DefaultedList.of(size, BlockPos.ORIGIN);
		for (int i = 0; i < size; i++) {
			this.positions.set(i, buffer.readBlockPos());
		}
	}

	@Override
	public void write(PacketByteBuf buffer) throws IOException {
		buffer.writeVarInt(this.positions.size());
		for (BlockPos pos : this.positions) {
			buffer.writeBlockPos(pos);
		}
	}

	@Override
	public String getChannel() {
		return "Bis|ItemLoc";
	}

	@Override
	public void handle() {
		BismuthClient.minecraft.player.closeMenu();
		for (BlockPos pos : this.positions) {
			for (int i = 0; i < 20; i++) {
				BismuthClient.minecraft.particleManager.addParticle(new ParticleShowItems(BismuthClient.minecraft.player.world, pos.getX() + rng.nextDouble(), pos.getY() + rng.nextDouble(), pos.getZ() + rng.nextDouble(), 0D, 0D, 0D, 2F));
			}
		}
	}
}

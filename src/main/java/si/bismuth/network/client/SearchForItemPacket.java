package si.bismuth.network.client;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.math.BlockPos;
import si.bismuth.client.ParticleShowItems;

import java.io.IOException;
import java.util.Random;

public class SearchForItemPacket implements ClientPacket {
	private ItemStack stack;
	private DefaultedList<BlockPos> result;
	private final static Random rng = new Random();

	public SearchForItemPacket() {
		// noop
	}

	public SearchForItemPacket(ItemStack stack) {
		this.stack = stack;
	}

	@Override
	public void read(PacketByteBuf buffer) throws IOException {
		final int size = buffer.readVarInt();
		this.result = DefaultedList.of(size, BlockPos.ORIGIN);
		for (int i = 0; i < size; i++) {
			this.result.set(i, buffer.readBlockPos());
		}
	}

	@Override
	public void write(PacketByteBuf buffer) throws IOException {
		buffer.writeItemStack(this.stack);
	}

	@Override
	public String getChannel() {
		return "Bis|searchforitem";
	}

	@Override
	public void handle(Minecraft minecraft) {
		minecraft.player.closeMenu();
		for (BlockPos pos : this.result) {
			for (int i = 0; i < 20; i++) {
				minecraft.particleManager.addParticle(new ParticleShowItems(minecraft.player.world, pos.getX() + rng.nextDouble(), pos.getY() + rng.nextDouble(), pos.getZ() + rng.nextDouble(), 0D, 0D, 0D, 2F));
			}
		}
	}
}

package si.bismuth.network.server;

import java.io.IOException;

import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.math.BlockPos;
import si.bismuth.BismuthServer;
import si.bismuth.network.client.InventoryContentsPacket;

public class QueryInventoryPacket implements ServerPacket {

	private BlockPos pos;

	public QueryInventoryPacket() {
	}

	public QueryInventoryPacket(BlockPos pos) {
		this.pos = pos;
	}

	@Override
	public void read(PacketByteBuf buffer) throws IOException {
		this.pos = buffer.readBlockPos();
	}

	@Override
	public void write(PacketByteBuf buffer) throws IOException {
		buffer.writeBlockPos(this.pos);
	}

	@Override
	public String getChannel() {
		return "Bis|InvQuery";
	}

	@Override
	public void handle(ServerPlayerEntity player) {
		final Inventory inventory = HopperBlockEntity.getInventoryAt(player.world, this.pos.getX(), this.pos.getY(), this.pos.getZ());
		// silence inspection since it falsely claims that container cannot be null. :(
		// noinspection ConstantConditions
		if (inventory == null) {
			return;
		}

		final DefaultedList<ItemStack> contents = DefaultedList.of(inventory.getSize(), ItemStack.EMPTY);
		for (int i = 0; i < inventory.getSize(); i++) {
			contents.set(i, inventory.getStack(i));
		}

		BismuthServer.networking.sendPacket(player, new InventoryContentsPacket(contents));
	}
}

package si.bismuth.network.server;

import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.math.BlockPos;
import si.bismuth.BismuthServer;

import java.io.IOException;

public class GetInventoryPacket implements ServerPacket {
	private BlockPos pos;
	private DefaultedList<ItemStack> result;

	public GetInventoryPacket() {
		// noop
	}

	public GetInventoryPacket(DefaultedList<ItemStack> listIn) {
		this.result = listIn;
	}

	@Override
	public void read(PacketByteBuf buffer) throws IOException {
		this.pos = buffer.readBlockPos();
	}

	@Override
	public void write(PacketByteBuf buffer) throws IOException {
		buffer.writeVarInt(this.result.size());
		for (ItemStack stack : this.result) {
			buffer.writeItemStack(stack);
		}
	}

	@Override
	public String getChannel() {
		return "Bis|getinventory";
	}

	@Override
	public void handle(ServerPlayerEntity player) {
		final Inventory container = HopperBlockEntity.getInventoryAt(player.world, this.pos.getX(), this.pos.getY(), this.pos.getZ());
		// silence inspection since it falsely claims that container cannot be null. :(
		// noinspection ConstantConditions
		if (container == null) {
			return;
		}

		final DefaultedList<ItemStack> inventory = DefaultedList.of(container.getSize(), ItemStack.EMPTY);
		for (int i = 0; i < container.getSize(); i++) {
			inventory.set(i, container.getStack(i));
		}

		BismuthServer.networking.sendPacket(player, new GetInventoryPacket(inventory));
	}
}

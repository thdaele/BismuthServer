package si.bismuth.network.server;

import java.io.IOException;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import si.bismuth.utils.InventoryHelper;

public class FindItemPacket implements ServerPacket {

	private ItemStack stack;

	public FindItemPacket() {
	}

	public FindItemPacket(ItemStack stack) {
		this.stack = stack;
	}

	@Override
	public void read(PacketByteBuf buffer) throws IOException {
		this.stack = buffer.readItemStack();
	}

	@Override
	public void write(PacketByteBuf buffer) throws IOException {
		buffer.writeItemStack(this.stack);
	}

	@Override
	public String getChannel() {
		return "Bis|FindItem";
	}

	@Override
	public void handle(ServerPlayerEntity player) {
		InventoryHelper.processFindItem(player, this.stack);
	}
}

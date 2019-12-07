package si.bismuth.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import si.bismuth.MCServer;

@PacketChannelName("getinventory")
public class BisPacketGetInventory extends BisPacket {
	private BlockPos pos;
	private NonNullList<ItemStack> result;

	public BisPacketGetInventory() {
		// noop
	}

	public BisPacketGetInventory(NonNullList<ItemStack> listIn) {
		this.result = listIn;
	}

	@Override
	public void writePacketData() {
		final PacketBuffer buf = this.getPacketBuffer();
		buf.writeVarInt(this.result.size());
		for (ItemStack stack : this.result) {
			buf.writeItemStack(stack);
		}
	}

	@Override
	public void readPacketData(PacketBuffer buf) {
		this.pos = buf.readBlockPos();
	}

	@Override
	public void processPacket(EntityPlayerMP player) {
		final IInventory container = TileEntityHopper.getInventoryAtPosition(player.world, this.pos.getX(), this.pos.getY(), this.pos.getZ());
		// silence inspection since it falsely claims that container cannot be null. :(
		// noinspection ConstantConditions
		if (container == null) {
			return;
		}

		final NonNullList<ItemStack> inventory = NonNullList.withSize(container.getSizeInventory(), ItemStack.EMPTY);
		for (int i = 0; i < container.getSizeInventory(); i++) {
			inventory.set(i, container.getStackInSlot(i));
		}

		MCServer.pcm.sendPacketToPlayer(player, new BisPacketGetInventory(inventory));
	}
}

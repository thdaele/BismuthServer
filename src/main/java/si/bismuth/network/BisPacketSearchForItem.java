package si.bismuth.network;

import net.minecraft.block.Block;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import si.bismuth.MCServer;

import java.io.IOException;

@PacketChannelName("searchforitem")
public class BisPacketSearchForItem extends BisPacket {
	private ItemStack stack;
	private NonNullList<BlockPos> result;

	public BisPacketSearchForItem() {
		// noop
	}

	public BisPacketSearchForItem(NonNullList<BlockPos> listIn) {
		this.result = listIn;
	}

	@Override
	public void writePacketData() {
		final PacketBuffer buf = this.getPacketBuffer();
		buf.writeVarInt(this.result.size());
		for (BlockPos pos : this.result) {
			buf.writeBlockPos(pos);
		}
	}

	@Override
	public void readPacketData(PacketBuffer buf) throws IOException {
		this.stack = buf.readItemStack();
	}

	@Override
	public void processPacket(EntityPlayerMP player) {
		final int range = 8;
		final NonNullList<BlockPos> positions = NonNullList.create();
		for (int i = -range; i <= range; i++) {
			for (int j = -range; j <= range; j++) {
				for (int k = -range; k <= range; k++) {
					final int x = i + player.getPosition().getX();
					final int y = j + player.getPosition().getY();
					final int z = k + player.getPosition().getZ();
					final IInventory container = TileEntityHopper.getInventoryAtPosition(player.world, x, y, z);
					// silence inspection since it falsely claims that container cannot be null. :(
					//noinspection ConstantConditions
					if (container == null)
						continue;
					for (int s = 0; s < container.getSizeInventory(); s++) {
						final ItemStack stackInSlot = container.getStackInSlot(s);
						if (stackInSlot.isItemEqual(this.stack)) {
							positions.add(new BlockPos(x, y, z));
							break;
						} else if (Block.getBlockFromItem(stackInSlot.getItem()) instanceof BlockShulkerBox) {
							final NBTTagCompound tag = stackInSlot.getTagCompound();
							if (tag != null) {
								final NBTTagList list = tag.getTagList("Items", 9);
								for (int b = 0; b < list.tagCount(); b++) {
									final NBTTagCompound compound = list.getCompoundTagAt(b);
									if (new ItemStack(compound).isItemEqual(this.stack)) {
										positions.add(new BlockPos(x, y, z));
										break;
									}
								}
							}
						}
					}
				}
			}
		}

		MCServer.pcm.sendPacketToPlayer(player, new BisPacketSearchForItem(positions));
	}
}

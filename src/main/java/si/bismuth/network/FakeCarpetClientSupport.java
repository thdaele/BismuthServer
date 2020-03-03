package si.bismuth.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import si.bismuth.MCServer;

import java.util.Arrays;

@PacketChannelName(value = "carpet:client", isCustom = true)
public class FakeCarpetClientSupport extends BisPacket {
	public FakeCarpetClientSupport() {
		// noop
	}

	@Override
	public void writePacketData() {
		final PacketBuffer buf = this.getPacketBuffer();
		final NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("carpetVersion", "bismuth");
		nbt.setFloat("tickrate", 20F);
		final NBTTagCompound ctrlQCrafting = new NBTTagCompound();
		ctrlQCrafting.setString("rule", "ctrlQCrafting");
		ctrlQCrafting.setString("current", "true");
		ctrlQCrafting.setString("default", "true");
		ctrlQCrafting.setBoolean("isfloat", false);
		final NBTTagList carpetRules = new NBTTagList();
		carpetRules.appendTag(ctrlQCrafting);
		nbt.setTag("ruleList", carpetRules);
		buf.writeInt(1);
		buf.writeCompoundTag(nbt);
	}

	@Override
	public void readPacketData(PacketBuffer buf) {
		System.out.println(Arrays.toString(buf.readByteArray()));
		// noop
	}

	@Override
	public void processPacket(EntityPlayerMP player) {
		MCServer.pcm.sendPacketToPlayer(player, new FakeCarpetClientSupport());
	}
}

package si.bismuth.network.server;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import si.bismuth.BismuthServer;

import java.io.IOException;
import java.util.Arrays;

public class FakeCarpetClientSupport implements ServerPacket {
	public FakeCarpetClientSupport() {
		// noop
	}

	@Override
	public void read(PacketByteBuf buffer) throws IOException {
		System.out.println(Arrays.toString(buffer.readByteArray()));
		// noop
	}

	@Override
	public void write(PacketByteBuf buffer) throws IOException {
		final NbtCompound nbt = new NbtCompound();
		nbt.putString("carpetVersion", "bismuth");
		nbt.putFloat("tickrate", 20F);
		final NbtCompound ctrlQCrafting = new NbtCompound();
		ctrlQCrafting.putString("rule", "ctrlQCrafting");
		ctrlQCrafting.putString("current", "true");
		ctrlQCrafting.putString("default", "true");
		ctrlQCrafting.putBoolean("isfloat", false);
		final NbtList carpetRules = new NbtList();
		carpetRules.add(ctrlQCrafting);
		nbt.put("ruleList", carpetRules);
		buffer.writeInt(1);
		buffer.writeNbtCompound(nbt);
	}

	@Override
	public String getChannel() {
		return "carpet:client";
	}

	@Override
	public void handle(ServerPlayerEntity player) {
		BismuthServer.networking.sendPacket(player, new FakeCarpetClientSupport());
	}
}

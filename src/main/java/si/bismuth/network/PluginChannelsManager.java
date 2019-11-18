package si.bismuth.network;

import com.google.common.collect.Lists;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.play.server.SPacketCustomPayload;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PluginChannelsManager {
	private static final String CHANNEL_SEPARATOR = "\u0000";
	private static final String REGISTER_CHANNELS = "REGISTER";
	private final Map<UUID, List<String>> channelList = new HashMap<>();
	private final List<String> allChannels = new ArrayList<>();

	public void sendRegisterToPlayer(EntityPlayerMP player) {
		final String channels = StringUtils.join(this.getAllChannels(), CHANNEL_SEPARATOR);
		System.out.println(channels);
		final SPacketCustomPayload packet = new SPacketCustomPayload(REGISTER_CHANNELS, new PacketBuffer(Unpooled.buffer()).writeString(channels));
		player.connection.sendPacket(packet);
	}

	public void processIncoming(EntityPlayer player, CPacketCustomPayload packet) {
		final UUID uuid = player.getUniqueID();
		final String channel = packet.getChannelName();
		final PacketBuffer data = packet.getBufferData();
		data.resetReaderIndex();

		if (channel.equals(REGISTER_CHANNELS)) {
			final List<String> incomingChannels = this.getChannelsFromBuffer(data);
			System.out.println("REGISTER: " + StringUtils.join(incomingChannels, " "));
			this.addChannelsForPlayer(uuid, incomingChannels);
		} else if (this.getChannelsForPlayer(uuid).contains(channel)) {
			// TODO
		}
	}

	private List<String> getChannelsFromBuffer(PacketBuffer data) {
		final byte[] bytes = new byte[data.readableBytes()];
		data.readBytes(bytes);
		final String channels = new String(bytes, StandardCharsets.UTF_8);
		return Lists.newArrayList(channels.split(CHANNEL_SEPARATOR));
	}

	private void addChannelsForPlayer(UUID player, List<String> channels) {
		this.getChannelsForPlayer(player).addAll(channels);
	}

	private List<String> getChannelsForPlayer(UUID player) {
		final Map<UUID, List<String>> channelMap = this.channelList;
		if (!channelMap.containsKey(player)) {
			channelMap.put(player, new ArrayList<>());
		}

		return channelMap.get(player);
	}

	private List<String> getAllChannels() {
		return this.allChannels;
	}
}

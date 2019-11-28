package si.bismuth.network;

import com.google.common.collect.Lists;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.play.server.SPacketCustomPayload;
import org.apache.commons.lang3.StringUtils;
import si.bismuth.MCServer;

import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PluginChannelsManager {
	private static final String CHANNEL_SEPARATOR = "\u0000";
	private static final String REGISTER_CHANNELS = "REGISTER";
	private final Map<String, Class<? extends BisPacket>> allChannels = new HashMap<>();
	private final Map<UUID, List<String>> channelList = new HashMap<>();

	public PluginChannelsManager() {
		this.registerPacket(BisPacketSort.class);
	}

	private void registerPacket(Class<? extends BisPacket> clazz) {
		if (!clazz.isAnnotationPresent(PacketChannelName.class)) {
			MCServer.LOG.error("Packet {} lacks plugin channel annotation.", clazz.getSimpleName());
			return;
		}

		try {
			clazz.getMethod("setChannelName", String.class).invoke(null, clazz.getDeclaredAnnotation(PacketChannelName.class).value());
			final String channel = (String) clazz.getMethod("getChannelName").invoke(null);
			if (this.allChannels.containsKey(channel)) {
				MCServer.LOG.error("Packet {} attempted to register packet on channel '{}' but it already exists!", clazz.getSimpleName(), channel);
			} else {
				this.allChannels.put(channel, clazz);
			}
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	public void sendRegisterToPlayer(EntityPlayerMP player) {
		final String channels = StringUtils.join(this.allChannels.keySet(), CHANNEL_SEPARATOR);
		final SPacketCustomPayload packet = new SPacketCustomPayload(REGISTER_CHANNELS, new PacketBuffer(Unpooled.buffer()).writeString(channels));
		player.connection.sendPacket(packet);
	}

	public void sendPacketToPlayer(EntityPlayerMP player, BisPacket packet) {
		if (this.getChannelsForPlayer(player.getUniqueID()).contains(BisPacket.getChannelName())) {
			packet.writePacketData();
			player.connection.sendPacket(new SPacketCustomPayload(BisPacket.getChannelName(), packet.getPacketBuffer()));
		}
	}

	public void processIncoming(EntityPlayer player, CPacketCustomPayload packetIn) {
		final UUID uuid = player.getUniqueID();
		final String channel = packetIn.getChannelName();
		final PacketBuffer data = packetIn.getBufferData();
		data.resetReaderIndex();

		if (channel.equals(REGISTER_CHANNELS)) {
			final List<String> incomingChannels = this.getChannelsFromBuffer(data);
			this.addChannelsForPlayer(uuid, incomingChannels);
		} else if (this.getChannelsForPlayer(uuid).contains(channel)) {
			try {
				final BisPacket packet = this.allChannels.get(channel).newInstance();
				packet.readPacketData(data);
				packet.processPacket(player);
			} catch (IllegalAccessException | InstantiationException e) {
				e.printStackTrace();
			}
		} else {
			// debug
			MCServer.LOG.debug("Received on unregistered channel '{}' for player '{}'!", channel, player.getName());
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
		final Map<UUID, List<String>> channels = this.channelList;
		if (!channels.containsKey(player)) {
			channels.put(player, new ArrayList<>());
		}

		return channels.get(player);
	}
}

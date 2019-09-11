package carpet.bismuth.patches;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.server.MinecraftServer;

public class NetHandlerPlayServerFake extends NetHandlerPlayServer
{
    public NetHandlerPlayServerFake(MinecraftServer server, NetworkManager nm, EntityPlayerMP playerIn)
    {
        super(server, nm, playerIn);
    }

    public void sendPacket(final Packet<?> packetIn)
    {
    }

    public void disconnect(String reason)
    {
    }
}




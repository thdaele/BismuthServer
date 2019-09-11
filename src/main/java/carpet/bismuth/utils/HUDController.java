package carpet.bismuth.utils;

import carpet.bismuth.logging.LoggerRegistry;
import carpet.bismuth.logging.logHelpers.PacketCounter;
import carpet.bismuth.mixins.ISPacketPlayerListHeaderFooterMixin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketPlayerListHeaderFooter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import java.util.*;

public class HUDController
{
    public static Map<EntityPlayer, List<ITextComponent>> player_huds = new HashMap<>();
    
    public static void addMessage(EntityPlayer player, ITextComponent hudMessage)
    {
        if (!player_huds.containsKey(player))
        {
            player_huds.put(player, new ArrayList<>());
        }
        else
        {
            player_huds.get(player).add(new TextComponentString("\n"));
        }
        player_huds.get(player).add(hudMessage);
    }
    
    public static void clear_player(EntityPlayer player)
    {
        SPacketPlayerListHeaderFooter packet = new SPacketPlayerListHeaderFooter();
        ((ISPacketPlayerListHeaderFooterMixin) packet).setHeader(new TextComponentString(""));
        ((ISPacketPlayerListHeaderFooterMixin) packet).setFooter(new TextComponentString(""));
        ((EntityPlayerMP) player).connection.sendPacket(packet);
    }
    
    
    public static void update_hud(MinecraftServer server)
    {
        if (server.getTickCounter() % 20 != 0)
            return;
        
        player_huds.clear();
        
        if (LoggerRegistry.__tps)
            log_tps(server);
        
        if (LoggerRegistry.__packets)
            LoggerRegistry.getLogger("packets").log(() -> packetCounter(), "TOTAL_IN", PacketCounter.totalIn, "TOTAL_OUT", PacketCounter.totalOut);
        
        for (EntityPlayer player : player_huds.keySet())
        {
            SPacketPlayerListHeaderFooter packet = new SPacketPlayerListHeaderFooter();
            ((ISPacketPlayerListHeaderFooterMixin) packet).setHeader(new TextComponentString(""));
            ((ISPacketPlayerListHeaderFooterMixin) packet).setFooter(Messenger.m(null, player_huds.get(player).toArray(new Object[0])));
            ((EntityPlayerMP) player).connection.sendPacket(packet);
        }
    }
    
    private static void log_tps(MinecraftServer server)
    {
        double MSPT = MathHelper.average(server.tickTimeArray) * 1.0E-6D;
        double TPS = 1000.0D / Math.max(50, MSPT);
        String color = Messenger.heatmap_color(MSPT, 50);
        ITextComponent[] message = new ITextComponent[]{Messenger.m(null, "g TPS: ", String.format(Locale.US, "%s %.1f", color, TPS), "g  MSPT: ", String.format(Locale.US, "%s %.1f", color, MSPT))};
        LoggerRegistry.getLogger("tps").log(() -> message, "MSPT", MSPT, "TPS", TPS);
    }
    
    private static ITextComponent[] packetCounter()
    {
        ITextComponent[] ret = new ITextComponent[]{Messenger.m(null, "w I/" + PacketCounter.totalIn + " O/" + PacketCounter.totalOut),};
        PacketCounter.reset();
        return ret;
    }
}

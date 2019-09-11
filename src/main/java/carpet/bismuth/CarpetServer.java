package carpet.bismuth;

import carpet.bismuth.logging.LoggerRegistry;
import carpet.bismuth.utils.HUDController;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CarpetServer // static for now - easier to handle all around the code, its one anyways
{
    public static MinecraftServer minecraft_server;
    
    public static void init(MinecraftServer server) //aka constructor of this static singleton class
    {
        minecraft_server = server;
    }
    
    public static void onServerLoaded(MinecraftServer server)
    {
        CarpetSettings.applySettingsFromConf(server);
        LoggerRegistry.initLoggers(server);
    }
    
    public static void tick(MinecraftServer server)
    {
        // TickSpeed.tick(server);
        HUDController.update_hud(server);
    }
    
    public static void playerConnected(EntityPlayerMP player)
    {
        LoggerRegistry.playerConnected(player);
    }
    
    public static void playerDisconnected(EntityPlayerMP player)
    {
        LoggerRegistry.playerDisconnected(player);
    }
}

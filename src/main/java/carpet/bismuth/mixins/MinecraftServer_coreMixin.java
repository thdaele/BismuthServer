package carpet.bismuth.mixins;

import carpet.bismuth.CarpetServer;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.world.WorldType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.net.Proxy;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServer_coreMixin
{
    @Shadow
    public abstract void logInfo(String s);
    
    @Inject(method = "run", at = @At("HEAD"))
    private void helloWorld(CallbackInfo ci)
    {
        this.logInfo("Hello, world!");
    }
    
    @Inject(method = "<init>", at = @At("RETURN"))
    private void onCtor(File p_i47054_1_, Proxy p_i47054_2_, DataFixer p_i47054_3_, YggdrasilAuthenticationService p_i47054_4_, MinecraftSessionService p_i47054_5_, GameProfileRepository p_i47054_6_, PlayerProfileCache p_i47054_7_, CallbackInfo ci)
    {
        CarpetServer.init((MinecraftServer)(Object) this);
    }
    
    @Inject(method = "loadAllWorlds", at = @At("HEAD"))
    private void onLoadAllWorlds(String saveName, String worldNameIn, long seed, WorldType type, String generatorOptions, CallbackInfo ci)
    {
        CarpetServer.onServerLoaded((MinecraftServer) (Object) this);
    }
    
    @Inject(
            method = "tick",
            at = @At(value = "FIELD", ordinal = 0, shift = At.Shift.AFTER,
                    target = "Lnet/minecraft/server/MinecraftServer;tickCounter:I")
    )
    private void onTick(CallbackInfo ci)
    {
        CarpetServer.tick((MinecraftServer)(Object)this);
    }
}

package carpet.bismuth.mixins;

import carpet.bismuth.helpers.EntityPlayerActionPack;
import carpet.bismuth.interfaces.IEntityPlayerMP;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerMP.class)
public abstract class EntityPlayerMPMixin implements IEntityPlayerMP
{
    private EntityPlayerActionPack actionPack;
    
    @Override
    public EntityPlayerActionPack getActionPack()
    {
        return this.actionPack;
    }
    
    @Inject(method = "<init>", at = @At("RETURN"))
    private void onCtor(MinecraftServer server, WorldServer worldIn, GameProfile profile, PlayerInteractionManager interactionManagerIn, CallbackInfo ci)
    {
        actionPack = new EntityPlayerActionPack((EntityPlayerMP)(Object)this);
    }
    
    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void onOnUpdate(CallbackInfo ci)
    {
        actionPack.onUpdate();
    }
}

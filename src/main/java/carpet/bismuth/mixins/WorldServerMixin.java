package carpet.bismuth.mixins;

import carpet.bismuth.interfaces.IWorldServer;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(WorldServer.class)
public abstract class WorldServerMixin extends World implements IWorldServer
{
    @Shadow protected abstract boolean isChunkLoaded(int p_isChunkLoaded_1_, int p_isChunkLoaded_2_, boolean p_isChunkLoaded_3_);
    
    protected WorldServerMixin(ISaveHandler p_i45749_1_, WorldInfo p_i45749_2_, WorldProvider p_i45749_3_, Profiler p_i45749_4_, boolean p_i45749_5_)
    {
        super(p_i45749_1_, p_i45749_2_, p_i45749_3_, p_i45749_4_, p_i45749_5_);
    }
    
    @Override
    public boolean isChunkLoadedC(int x, int z, boolean allowEmpty)
    {
        return this.isChunkLoaded(x, z, allowEmpty);
    }
}

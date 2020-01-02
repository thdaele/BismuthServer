package si.bismuth.mixins;

import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(WorldServer.class)
public interface IWorldServerMixin {
	@Invoker
	boolean getIsChunkLoaded(int x, int z, boolean allowEmpty);
}

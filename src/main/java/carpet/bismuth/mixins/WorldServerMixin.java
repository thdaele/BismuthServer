package carpet.bismuth.mixins;

import carpet.bismuth.interfaces.IWorldServer;
import carpet.bismuth.utils.CarpetProfiler;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldServer.class)
public abstract class WorldServerMixin extends World implements IWorldServer {
	protected WorldServerMixin(ISaveHandler ish, WorldInfo wi, WorldProvider wp, Profiler p, boolean b) {
		super(ish, wi, wp, p, b);
	}

	@Shadow
	protected abstract boolean isChunkLoaded(int x, int y, boolean allowEmpty);

	private String worldName;

	@Override
	public boolean isChunkLoadedC(int x, int z, boolean allowEmpty) {
		return this.isChunkLoaded(x, z, allowEmpty);
	}

	@Inject(method = "<init>", at = @At("RETURN"))
	private void onInit(MinecraftServer server, ISaveHandler saveHandlerIn, WorldInfo info, int dimensionId, Profiler profilerIn, CallbackInfo ci) {
		this.worldName = this.provider.getDimensionType().getName();
	}

	@Inject(method = "tick", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/profiler/Profiler;startSection(Ljava/lang/String;)V", args = "ldc=mobSpawner"))
	private void onMobSpawning(CallbackInfo ci) {
		CarpetProfiler.start_section(worldName, "spawning");
	}

	@Inject(method = "tick", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V", args = "ldc=chunkSource"))
	private void onChunkSource(CallbackInfo ci) {
		CarpetProfiler.end_current_section();
	}

	@Inject(method = "tick", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V", args = "ldc=tickPending"))
	private void onTickPending(CallbackInfo ci) {
		CarpetProfiler.start_section(worldName, "blocks");
	}


	@Inject(method = "tick", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V", args = "ldc=chunkMap"))
	private void onChunkMap(CallbackInfo ci) {
		CarpetProfiler.end_current_section();
	}
}

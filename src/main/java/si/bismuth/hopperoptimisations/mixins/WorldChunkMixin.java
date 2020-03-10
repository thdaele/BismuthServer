package si.bismuth.hopperoptimisations.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import si.bismuth.hopperoptimisations.utils.EntityHopperInteraction;

@Mixin(Chunk.class)
public abstract class WorldChunkMixin {
	@Shadow
	private boolean loaded;

	@Inject(method = "addEntity(Lnet/minecraft/entity/Entity;)V", at = @At(value = "RETURN"))
	private void notifyHoppersOfNewEntity(Entity entity, CallbackInfo ci) {
		if (this.loaded) { //don't do anything if this chunk is not ticked yet. Neighboring hoppers don't have caches yet anyway. //deadlock without this line on world load!
			EntityHopperInteraction.notifyHoppersOfNewOrTeleportedEntity(entity);
		}
	}
}

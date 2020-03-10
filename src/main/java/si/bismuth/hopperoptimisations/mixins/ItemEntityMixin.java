package si.bismuth.hopperoptimisations.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityItem.class)
public abstract class ItemEntityMixin extends Entity {
	public ItemEntityMixin(World world) {
		super(world);
	}

    /* //replaced with code in EntityMixin
    @Inject(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V", shift = At.Shift.BEFORE))
    private void rememberNearbyHoppers(CallbackInfo ci) {
        if (this.world.isClient) return;
        EntityHopperInteraction.findHoppers = HopperSettings.optimizedEntityHopperInteraction;
    }*/

    /* //replaced with code in EntityMixin
    @Inject(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V", shift = At.Shift.AFTER))
    private void notifyHoppersOfExistence(CallbackInfo ci) {
        if (this.world.isClient || !HopperSettings.optimizedEntityHopperInteraction) return;
        EntityHopperInteraction.notifyHoppers(this);
    }*/

    /* //replaced with code in WorldChunkMixin, also handles lazy entities.
    @Inject(method = "tick()V", at = @At(value = "HEAD"))
    private void notifyHoppersOfExistenceOnFirstTick(CallbackInfo ci) {
        if (!this.world.isClient && firstUpdate && HopperSettings.optimizedEntityHopperInteraction) //if this doesn't happen and the item never moves, a hopper won't find it
            EntityHopperInteraction.findAndNotifyHoppers(this);
    }*/


	// {@link #si.bismuth.hopperoptimisations.mixins.EntityMixin}
    /*
	@Redirect(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;doesNotCollide(Lnet/minecraft/entity/Entity;)Z"))
	private boolean doNotCheckEntities(World world, Entity itemEntity) {
		if (!HopperSettings.simplifiedItemElevatorCheck) {
			return itemEntity.world.doesNotCollide(itemEntity);
		}

		//only do block collisions, shulkers, minecarts and boats no push out items or have the "item elevator" effect
		return itemEntity.world.getBlockCollisions(itemEntity, itemEntity.getBoundingBox()).allMatch(VoxelShape::isEmpty);
	}*/
}

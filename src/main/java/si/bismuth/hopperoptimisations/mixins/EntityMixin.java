package si.bismuth.hopperoptimisations.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import si.bismuth.hopperoptimisations.HopperSettings;
import si.bismuth.hopperoptimisations.utils.EntityHopperInteraction;

@Mixin(Entity.class)
public abstract class EntityMixin {
	@Shadow
	public World world;

	@Inject(method = "move", at = @At(value = "HEAD"))
	private void rememberNearbyHoppers(MoverType type, double x, double y, double z, CallbackInfo ci) {
		if (!this.world.isRemote && EntityHopperInteraction.canInteractWithHopper(this)) {
			EntityHopperInteraction.findHoppers = HopperSettings.optimizedEntityHopperInteraction;
		}
	}

	@Inject(method = "move", at = @At(value = "RETURN"))
	private void notifyHoppersOfExistence(CallbackInfo ci) {
		if (EntityHopperInteraction.findHoppers && !this.world.isRemote && HopperSettings.optimizedEntityHopperInteraction) {
			EntityHopperInteraction.notifyHoppersObj(this);
		}
	}

	// TODO: consider removal
	/*@Redirect(method = "pushOutOfBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;collidesWithAnyBlock(Lnet/minecraft/util/math/AxisAlignedBB;)Z"))
	private boolean doNotCheckEntities(World world, AxisAlignedBB bbox) {
		//noinspection ConstantConditions
		if (!HopperSettings.simplifiedItemElevatorCheck || !((Entity) (Object) this instanceof EntityItem)) {
			return this.world.collidesWithAnyBlock(this.getEntityBoundingBox());
		}

		//only do block collisions, shulkers, minecarts and boats no push out items or have the "item elevator" effect
		return this.world.getBlockCollisions(itemEntity, itemEntity.getBoundingBox()).allMatch(VoxelShape::isEmpty);
	}*/
}

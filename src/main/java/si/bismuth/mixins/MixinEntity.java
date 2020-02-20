package si.bismuth.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import si.bismuth.utils.BlockRotator;

@Mixin(Entity.class)
public abstract class MixinEntity {
	// @formatter:off
	@Shadow public double posX;
	@Shadow public double posY;
	@Shadow public double posZ;
	@Shadow public float rotationYaw;
	@Shadow public abstract AxisAlignedBB getEntityBoundingBox();
	@Shadow protected abstract boolean shouldSetPosAfterLoading();
	@Shadow public abstract void setEntityBoundingBox(AxisAlignedBB bb);
	@Shadow public abstract void setPosition(double x, double y, double z);
	@Shadow protected abstract NBTTagList newDoubleNBTList(double... numbers);
	// @formatter:on

	@Inject(method = "getHorizontalFacing", at = @At("HEAD"), cancellable = true)
	private void onGetHorizontalFacing(CallbackInfoReturnable<EnumFacing> cir) {
		if (BlockRotator.flippinEligibility((Entity) (Object) this)) {
			cir.setReturnValue(EnumFacing.byHorizontalIndex(MathHelper.floor((double) (this.rotationYaw * 1F / 90F) + 0.5D) & 3).getOpposite());
		}
	}

	@Inject(method = "writeToNBT", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;setUniqueId(Ljava/lang/String;Ljava/util/UUID;)V", shift = At.Shift.AFTER, ordinal = 0))
	private void AABBNBT(NBTTagCompound compound, CallbackInfoReturnable<NBTTagCompound> cir) {
		final AxisAlignedBB bb = this.getEntityBoundingBox();
		compound.setTag("AABB", this.newDoubleNBTList(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ));
	}

	@Redirect(method = "readFromNBT", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;shouldSetPosAfterLoading()Z"))
	private boolean readAABBNBT(Entity entity, NBTTagCompound compound) {
		if (this.shouldSetPosAfterLoading()) {
			this.setPosition(this.posX, this.posY, this.posZ);
		}

		if (compound.hasKey("AABB", 9)) {
			final NBTTagList bb = compound.getTagList("AABB", 6);
			this.setEntityBoundingBox(new AxisAlignedBB(bb.getDoubleAt(0), bb.getDoubleAt(1), bb.getDoubleAt(2), bb.getDoubleAt(3), bb.getDoubleAt(4), bb.getDoubleAt(5)));
		}

		return false;
	}
}

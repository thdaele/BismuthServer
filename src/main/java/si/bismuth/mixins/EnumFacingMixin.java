package si.bismuth.mixins;

import si.bismuth.utils.BlockRotator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnumFacing.class)
public abstract class EnumFacingMixin {
	@Inject(method = "getDirectionFromEntityLiving", at = @At(value = "FIELD", target = "Lnet/minecraft/util/EnumFacing;UP:Lnet/minecraft/util/EnumFacing;", shift = At.Shift.BEFORE), cancellable = true)
	private static void onGetDirectionFromEntityLiving1(BlockPos pos, EntityLivingBase placer, CallbackInfoReturnable<EnumFacing> cir) {
		if (BlockRotator.flippinEligibility(placer)) {
			cir.setReturnValue(EnumFacing.DOWN);
		}
	}

	@Inject(method = "getDirectionFromEntityLiving", at = @At(value = "FIELD", target = "Lnet/minecraft/util/EnumFacing;DOWN:Lnet/minecraft/util/EnumFacing;", shift = At.Shift.BEFORE), cancellable = true)
	private static void onGetDirectionFromEntityLiving2(BlockPos pos, EntityLivingBase placer, CallbackInfoReturnable<EnumFacing> cir) {
		if (BlockRotator.flippinEligibility(placer)) {
			cir.setReturnValue(EnumFacing.DOWN);
		}
	}
}

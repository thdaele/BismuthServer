package si.bismuth.mixins;

import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntityPiston.class)
public abstract class MixinTileEntityPiston extends TileEntity {
	@Shadow
	private float progress;
	@Shadow
	private float lastProgress;

	@Inject(method = "readFromNBT", at = @At(value = "FIELD", target = "Lnet/minecraft/tileentity/TileEntityPiston;lastProgress:F", opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER))
	private void readPistonSerialization(NBTTagCompound compound, CallbackInfo ci) {
		if (compound.hasKey("lastProgress", 5)) {
			this.lastProgress = this.progress;
		}
	}

	@Redirect(method = "writeToNBT", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;setFloat(Ljava/lang/String;F)V"))
	private void writePistonSerialization(NBTTagCompound compound, String key, float value) {
		compound.setFloat("progress", this.progress);
		compound.setFloat("lastProgress", this.lastProgress);
	}

	@Inject(method = "update", at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z"))
	private void onUpdate(CallbackInfo ci) {
		final IBlockState state = this.world.getBlockState(this.pos);
		this.world.notifyBlockUpdate(pos.offset(state.getValue(BlockPistonExtension.FACING).getOpposite()), state, state, 0);
	}
}

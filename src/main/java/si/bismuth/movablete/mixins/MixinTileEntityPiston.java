package si.bismuth.movablete.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import si.bismuth.movablete.ITileEntityPiston;

@Mixin(TileEntityPiston.class)
public abstract class MixinTileEntityPiston extends TileEntity implements ITileEntityPiston {
	@Shadow
	private IBlockState pistonState;
	@Unique
	private TileEntity carriedTileEntity;

	@Inject(method = "clearPistonTileEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/tileentity/TileEntityPiston;invalidate()V", shift = At.Shift.AFTER), cancellable = true)
	private void clearPistonTileEntityTE(CallbackInfo ci) {
		ci.cancel();
		final Block block = this.world.getBlockState(this.pos).getBlock();
		if (block == Blocks.PISTON_EXTENSION) {
			this.placeBlock();
		} else if (this.carriedTileEntity != null && block == Blocks.AIR) {
			this.placeBlock();
			this.world.setBlockToAir(this.pos);
		}
	}

	@Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z"), cancellable = true)
	private void updateTE(CallbackInfo ci) {
		ci.cancel();
		this.placeBlock();
	}

	@Inject(method = "readFromNBT", at = @At("RETURN"))
	private void readFromNBTTE(NBTTagCompound compound, CallbackInfo ci) {
		if (compound.hasKey("carriedTileEntity", 10)) {
			final Block block = this.pistonState.getBlock();
			if (block instanceof ITileEntityProvider) {
				this.carriedTileEntity = ((ITileEntityProvider) block).createNewTileEntity(this.world, block.getMetaFromState(this.pistonState));
			}

			if (this.carriedTileEntity != null) {
				this.carriedTileEntity.readFromNBT(compound.getCompoundTag("carriedTileEntity"));
			}
		}
	}

	@Inject(method = "writeToNBT", at = @At("RETURN"))
	private void writeToNBTTE(NBTTagCompound compound, CallbackInfoReturnable<NBTTagCompound> cir) {
		if (this.carriedTileEntity != null) {
			compound.setTag("carriedTileEntity", this.carriedTileEntity.writeToNBT(new NBTTagCompound()));
		}
	}

	@Unique
	@Override
	public void setCarriedTileEntity(TileEntity te) {
		this.carriedTileEntity = te;
	}

	@Unique
	private void placeBlock() {
		final Block block = this.pistonState.getBlock();
		this.world.setBlockState(this.pos, this.pistonState, 18);
		if (this.carriedTileEntity != null) {
			this.world.removeTileEntity(this.pos);
			this.carriedTileEntity.validate();
			this.world.setTileEntity(this.pos, this.carriedTileEntity);
		}

		this.world.notifyNeighborsRespectDebug(this.pos, Blocks.PISTON_EXTENSION, true);
		if (this.pistonState.hasComparatorInputOverride()) {
			this.world.updateComparatorOutputLevel(this.pos, block);
		}

		this.world.updateObservingBlocksAt(this.pos, block);
		this.world.neighborChanged(this.pos, block, this.pos);
	}
}

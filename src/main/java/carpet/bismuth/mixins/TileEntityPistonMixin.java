package carpet.bismuth.mixins;

import carpet.bismuth.utils.ITileEntityPiston;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TileEntityPiston.class)
public abstract class TileEntityPistonMixin extends TileEntity implements ITileEntityPiston
{
    @Shadow
    private IBlockState pistonState;
    @Shadow private float lastProgress;
    @Shadow private float progress;
    private TileEntity carriedTileEntity;

    @Inject(
            method = "update",
            at = @At(value = "INVOKE", shift = At.Shift.BEFORE,
                    target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z")
    )
    private void onUpdate(CallbackInfo ci)
    {
        IBlockState state = this.world.getBlockState(this.pos);
        this.world.notifyBlockUpdate(pos.offset(state.getValue(BlockPistonExtension.FACING).getOpposite()), state, state, 0);
    }

    @Override
    public void setCarriedTileEntity(TileEntity tileEntity) {
        this.carriedTileEntity = tileEntity;
        if (this.carriedTileEntity != null) {
            this.carriedTileEntity.setPos(this.pos);
        }
    }

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

    private void placeBlock() {
        // Workaround for the direction change caused by BlockDispenser.onBlockAdded();
        final Block block = this.pistonState.getBlock();
        if (block instanceof BlockDispenser || block instanceof BlockFurnace) {
            this.world.setBlockState(this.pos, this.pistonState, 18);
        }
        // Workaround is just placing the block twice. This should not cause any problems, but is ugly code

        this.world.setBlockState(this.pos, this.pistonState, 18);  //Flag 18 => No block updates, TileEntity has to be placed first
        if (!this.world.isRemote) {
            if (this.carriedTileEntity != null) {
                this.world.removeTileEntity(this.pos);
                this.carriedTileEntity.validate();
                this.world.setTileEntity(this.pos, this.carriedTileEntity);
            }

            // Update neighbors, comparators and observers now (same order as setBlockState would have if flag was set to 3 (default))
            // This should not change piston behavior for vanilla-pushable blocks at all
            this.world.notifyNeighborsRespectDebug(pos, Blocks.PISTON_EXTENSION, true);
            if (this.pistonState.hasComparatorInputOverride()) {
                this.world.updateComparatorOutputLevel(pos, this.pistonState.getBlock());
            }

            this.world.updateObservingBlocksAt(pos, this.pistonState.getBlock());
        }

        this.world.neighborChanged(this.pos, this.pistonState.getBlock(), this.pos);
    }

    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z"), cancellable = true)
    private void updateTE(CallbackInfo ci) {
        this.placeBlock();
        ci.cancel();
    }

    @Inject(method = "readFromNBT", at = @At("RETURN"))
    private void readFromNBTTE(NBTTagCompound compound, CallbackInfo ci) {
        if (compound.hasKey("carriedTileEntity", 10)) {
            final Block block = this.pistonState.getBlock();
            if (block instanceof ITileEntityProvider) {
                this.carriedTileEntity = ((ITileEntityProvider) block).createNewTileEntity(this.world, block.getMetaFromState(this.pistonState));
                if (this.carriedTileEntity != null) { // Can actually be null, as BlockPistonMoving.createNewTileEntity(...) returns null
                    this.carriedTileEntity.readFromNBT(compound.getCompoundTag("carriedTileEntity"));
                }
            }
        }
    }

    @Inject(method = "writeToNBT", at = @At("RETURN"))
    private void writeToNBTTE(NBTTagCompound compound, CallbackInfoReturnable<NBTTagCompound> cir) {
        if (this.carriedTileEntity != null) {
            compound.setTag("carriedTileEntity", this.carriedTileEntity.writeToNBT(new NBTTagCompound()));
        }
    }

    @Inject(method = "readFromNBT", at = @At(value = "FIELD", target = "Lnet/minecraft/tileentity/TileEntityPiston;lastProgress:F", opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER))
    private void readPistonSerialization(NBTTagCompound compound, CallbackInfo ci) {
        if(compound.hasKey("lastProgress", 5)) {
            this.lastProgress = this.progress;
        }
    }

    @Redirect(method = "writeToNBT", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;setFloat(Ljava/lang/String;F)V"))
    private void writePistonSerialization(NBTTagCompound compound, String key, float value) {
        compound.setFloat("progress", this.progress);
        compound.setFloat("lastProgress", this.lastProgress);
    }
}

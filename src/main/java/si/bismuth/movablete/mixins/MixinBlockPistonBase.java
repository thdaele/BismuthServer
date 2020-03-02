package si.bismuth.movablete.mixins;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.state.BlockPistonStructureHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import si.bismuth.movablete.ITileEntityPiston;

import java.util.List;

@Mixin(BlockPistonBase.class)
public abstract class MixinBlockPistonBase {
	@Unique
	private List<TileEntity> tileEntitiesList;

	@Redirect(method = "canPush", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;hasTileEntity()Z"))
	private static boolean canPushTE(Block block) {
		return block.hasTileEntity() && !isPushableTileEntityBlock(block);
	}

	private static boolean isPushableTileEntityBlock(Block block) {
		return block != Blocks.ENDER_CHEST && block != Blocks.ENCHANTING_TABLE && block != Blocks.END_GATEWAY
				&& block != Blocks.END_PORTAL && block != Blocks.MOB_SPAWNER && block != Blocks.PISTON_EXTENSION;
	}

	@Inject(method = "doMove", at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Ljava/util/List;size()I", remap = false, ordinal = 4), locals = LocalCapture.CAPTURE_FAILHARD)
	private void doMoveTE(World world, BlockPos p, EnumFacing d, boolean e, CallbackInfoReturnable<Boolean> cir, BlockPistonStructureHelper bpsh, List<BlockPos> list) {
		this.tileEntitiesList = Lists.newArrayList();
		for (BlockPos pos : list) {
			final TileEntity te = world.getTileEntity(pos);
			this.tileEntitiesList.add(te);
			if (te != null) {
				world.removeTileEntity(pos);
				te.markDirty();
			}
		}

	}

	@Inject(method = "doMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setTileEntity(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/tileentity/TileEntity;)V", shift = At.Shift.AFTER, ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
	private void setTileEntityTE(World world, BlockPos p, EnumFacing d, boolean ex, CallbackInfoReturnable<Boolean> cir, BlockPistonStructureHelper bpsh, List<BlockPos> list, List<IBlockState> lbs, List<BlockPos> lbp, int i, IBlockState[] abs, EnumFacing enumfacing, int l, BlockPos pos) {
		final TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityPiston) {
			((ITileEntityPiston) te).setCarriedTileEntity(tileEntitiesList.get(l));
		}
	}
}

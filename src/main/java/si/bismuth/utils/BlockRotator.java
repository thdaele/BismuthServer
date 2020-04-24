package si.bismuth.utils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockEndRod;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockGlazedTerracotta;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockObserver;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockRedstoneDiode;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

public class BlockRotator {
	private static final List<String> UUIDS = Arrays.asList("c36e8cbd-b090-47b7-8166-bab6985e4382", "73a0e9c7-d30f-43dd-b820-55c72c62a6f7", "78bbb591-b677-41cc-9d60-194fcb2e422c", "8a45fff7-0335-4928-a98c-dcc8b1ad5193");

	public static boolean flipBlockWithCactus(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!playerIn.capabilities.allowEdit || !player_holds_cactus_mainhand(playerIn)) {
			return false;
		}

		if (UUIDS.contains(String.valueOf(EntityPlayer.getUUID(playerIn.getGameProfile())))) return false;
		return flip_block(worldIn, pos, state, facing, hitX, hitY, hitZ);
	}

	private static boolean flip_block(World worldIn, BlockPos pos, IBlockState state, EnumFacing facing, float hitX, float hitY, float hitZ) {
		Block block = state.getBlock();
		if ((block instanceof BlockGlazedTerracotta) || (block instanceof BlockRedstoneDiode) || (block instanceof BlockRailBase) || (block instanceof BlockTrapDoor) || (block instanceof BlockLever) || (block instanceof BlockFenceGate)) {
			worldIn.setBlockState(pos, state.withRotation(Rotation.CLOCKWISE_90), 130);
		} else if ((block instanceof BlockObserver) || (block instanceof BlockEndRod)) {
			worldIn.setBlockState(pos, state.withProperty(BlockDirectional.FACING, state.getValue(BlockDirectional.FACING).getOpposite()), 130);
		} else if (block instanceof BlockDispenser) {
			worldIn.setBlockState(pos, state.withProperty(BlockDispenser.FACING, state.getValue(BlockDispenser.FACING).getOpposite()), 130);
		} else if (block instanceof BlockPistonBase) {
			if (!(state.getValue(BlockPistonBase.EXTENDED)))
				worldIn.setBlockState(pos, state.withProperty(BlockDirectional.FACING, state.getValue(BlockDirectional.FACING).getOpposite()), 130);
		} else if (block instanceof BlockSlab) {
			if (!((BlockSlab) block).isDouble()) {
				if (state.getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.TOP) {
					worldIn.setBlockState(pos, state.withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.BOTTOM), 130);
				} else {
					worldIn.setBlockState(pos, state.withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.TOP), 130);
				}
			}
		} else if (block instanceof BlockHopper) {
			if (state.getValue(BlockHopper.FACING) != EnumFacing.DOWN) {
				worldIn.setBlockState(pos, state.withProperty(BlockHopper.FACING, state.getValue(BlockHopper.FACING).rotateY()), 130);
			}
		} else if (block instanceof BlockStairs) {
			if ((facing == EnumFacing.UP && hitY == 1.0f) || (facing == EnumFacing.DOWN && hitY == 0.0f)) {
				if (state.getValue(BlockStairs.HALF) == BlockStairs.EnumHalf.TOP) {
					worldIn.setBlockState(pos, state.withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.BOTTOM), 130);
				} else {
					worldIn.setBlockState(pos, state.withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP), 130);
				}
			} else {
				boolean turn_right;
				if (facing == EnumFacing.NORTH) {
					turn_right = (hitX <= 0.5);
				} else if (facing == EnumFacing.SOUTH) {
					turn_right = !(hitX <= 0.5);
				} else if (facing == EnumFacing.EAST) {
					turn_right = (hitZ <= 0.5);
				} else if (facing == EnumFacing.WEST) {
					turn_right = !(hitZ <= 0.5);
				} else {
					return false;
				}
				if (turn_right) {
					worldIn.setBlockState(pos, state.withRotation(Rotation.COUNTERCLOCKWISE_90), 130);
				} else {
					worldIn.setBlockState(pos, state.withRotation(Rotation.CLOCKWISE_90), 130);
				}
			}
		} else {
			return false;
		}
		worldIn.markBlockRangeForRenderUpdate(pos, pos);
		return true;
	}

	private static boolean player_holds_cactus_mainhand(EntityPlayer playerIn) {
		return (!playerIn.getHeldItemMainhand().isEmpty() && playerIn.getHeldItemMainhand().getItem() instanceof ItemBlock && ((ItemBlock) (playerIn.getHeldItemMainhand().getItem())).getBlock() == Blocks.CACTUS);
	}

	public static boolean flippinEligibility(Entity entity) {
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			if (UUIDS.contains(String.valueOf(EntityPlayer.getUUID(player.getGameProfile())))) return false;
			return (!player.getHeldItemOffhand().isEmpty() && player.getHeldItemOffhand().getItem() instanceof ItemBlock && ((ItemBlock) (player.getHeldItemOffhand().getItem())).getBlock() == Blocks.CACTUS);
		}
		return false;
	}
}

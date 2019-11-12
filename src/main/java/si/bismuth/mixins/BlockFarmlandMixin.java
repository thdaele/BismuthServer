package si.bismuth.mixins;

import net.minecraft.block.BlockFarmland;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(BlockFarmland.class)
public abstract class BlockFarmlandMixin {
	/**
	 * @author nessie
	 * @reason Unfixes MC-120444
	 */
	@Overwrite
	protected static void turnToDirt(World world, BlockPos pos) {
		final IBlockState state = Blocks.DIRT.getDefaultState();
		world.setBlockState(pos, state);
		final AxisAlignedBB bb = state.getCollisionBoundingBox(world, pos).offset(pos);

		for (final Entity entity : world.getEntitiesWithinAABBExcludingEntity(null, bb)) {
			entity.setPosition(entity.posX, bb.maxY, entity.posZ);
		}
	}
}

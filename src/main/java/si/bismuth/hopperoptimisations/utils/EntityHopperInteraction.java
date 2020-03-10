package si.bismuth.hopperoptimisations.utils;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import si.bismuth.hopperoptimisations.HopperSettings;

import java.util.ArrayList;
import java.util.List;

public class EntityHopperInteraction extends Validator<Boolean> {
	public static final List<BlockPos> hopperLocationsToNotify = new ArrayList<>();
	//used to track when the rule was changed, incrementing makes all cached optimization states invalid
	public static int ruleUpdates = 0;
	public static boolean findHoppers = false;
	public static boolean searchedForHoppers = false;

	public static void notifyHoppersObj(Object object) {
		if (object instanceof Entity) notifyHoppers((Entity) object);
	}

	public static void notifyHoppers(Entity targetEntity) {
		if (!searchedForHoppers) {
			if (targetEntity.prevPosX != targetEntity.posX || targetEntity.prevPosY != targetEntity.posY || targetEntity.prevPosZ != targetEntity.posZ)
				findAndNotifyHoppers(targetEntity);
			findHoppers = false;
		} else {
			for (BlockPos pos : hopperLocationsToNotify) {
				TileEntity hopper = targetEntity.world.getTileEntity(pos);
				if (hopper instanceof TileEntityHopper) {
					((IHopper) hopper).notifyOfNearbyEntity(targetEntity);
				}
			}
			hopperLocationsToNotify.clear();
			findHoppers = false;
			searchedForHoppers = false;
		}
	}

	public static void findAndNotifyHoppers(Entity targetEntity) {
		searchedForHoppers = true;
		findHoppers = true;

		final AxisAlignedBB box = targetEntity.getEntityBoundingBox();
		int minX, maxX, minY, maxY, minZ, maxZ;
		minX = (int) Math.floor(box.minX) - 1;
		minY = (int) Math.floor(box.minY) - 1;
		minZ = (int) Math.floor(box.minZ) - 1;
		maxX = (int) Math.ceil(box.maxX);
		maxY = (int) Math.ceil(box.maxY);
		maxZ = (int) Math.ceil(box.maxZ);

		BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
		for (int x = minX; x <= maxX; ++x)
			for (int y = minY; y <= maxY; ++y)
				for (int z = minZ; z <= maxZ; ++z) {
					blockPos.setPos(x, y, z);
					IBlockState blockState = targetEntity.world.getBlockState(blockPos);
					if (blockState.getBlock() == Blocks.HOPPER) {
						hopperLocationsToNotify.add(blockPos.toImmutable());
					}
				}

		notifyHoppers(targetEntity);
	}

	public static boolean canInteractWithHopper(Object object) {
		return object instanceof EntityItem || object instanceof IInventory;
	}

	public static void notifyHoppersOfNewOrTeleportedEntity(Entity entity) {
		if (HopperSettings.optimizedEntityHopperInteraction && entity.isEntityAlive()) {
			//when rememberHoppers is true, we are already checking for hoppers, so calling it would be redundant
			//only call for entity types that hoppers can interact with
			if (!EntityHopperInteraction.findHoppers && (canInteractWithHopper(entity)))
				EntityHopperInteraction.findAndNotifyHoppers(entity);
		}
	}

	@Override
	public Boolean validate(ServerCommandSource source, ParsedRule<Boolean> rule, Boolean newValue, String previous) {
		if (ruleUpdates != -1)
			++ruleUpdates;
		return newValue;
	}
}

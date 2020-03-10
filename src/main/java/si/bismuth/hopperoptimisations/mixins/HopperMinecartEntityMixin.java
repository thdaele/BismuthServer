package si.bismuth.hopperoptimisations.mixins;

import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraft.inventory.IInventory;
import org.spongepowered.asm.mixin.Mixin;
import si.bismuth.hopperoptimisations.HopperSettings;
import si.bismuth.hopperoptimisations.utils.IHopper;
import si.bismuth.hopperoptimisations.utils.InventoryOptimizer;

@Mixin(EntityMinecartHopper.class)
public class HopperMinecartEntityMixin implements IHopper {
	//Duplicated code from HopperBlockEntityMixin, don't know where else to store those fields:
	//Fields for optimizedInventories
	private int this_lastChangeCount_Extract;
	private InventoryOptimizer previousExtract;
	private int previousExtract_lastChangeCount;
	private boolean previousExtract_causeMarkDirty;

	/**
	 * Checks whether the last item extract attempt was with the same inventory as the current one AND
	 * since before the last item transfer attempt the hopper's inventory and the other inventory did not change.
	 * Requires optimizedInventories.
	 *
	 * @param thisOpt  InventoryOptimizer of this hopper
	 * @param other    Inventory interacted with
	 * @param otherOpt InventoryOptimizer of other
	 *                 <p>
	 *                 Side effect: Sends comparator updates that would be sent on normal failed transfers.
	 * @return Whether the current item transfer attempt is known to fail.
	 */
	public boolean tryShortcutFailedExtract(InventoryOptimizer thisOpt, IInventory other, InventoryOptimizer otherOpt) {
		int thisChangeCount = thisOpt.getInventoryChangeCount();
		int otherChangeCount = otherOpt.getInventoryChangeCount();
		if (this_lastChangeCount_Extract != thisChangeCount || otherOpt != previousExtract || previousExtract_lastChangeCount != otherChangeCount) {
			this_lastChangeCount_Extract = thisChangeCount;
			previousExtract = otherOpt;
			previousExtract_lastChangeCount = otherChangeCount;
			previousExtract_causeMarkDirty = false;
			return false;
		}
		if (previousExtract_causeMarkDirty && !HopperSettings.failedTransferNoComparatorUpdates)
			IHopper.markDirtyLikeHopperWould(other, otherOpt, null); //failed transfers sometimes cause comparator updates

		return true;
	}

	/**
	 * Checks whether the last item insert attempt was with the same inventory as the current one AND
	 * since before the last item transfer attempt the hopper's inventory and the other inventory did not change.
	 * Requires optimizedInventories.
	 *
	 * @param thisOpt  InventoryOptimizer of this hopper
	 * @param other    Inventory interacted with
	 * @param otherOpt InventoryOptimizer of other
	 *                 <p>
	 *                 Side effect: Sends comparator updates that would be sent on normal failed transfers.
	 * @return Whether the current item transfer attempt is known to fail.
	 */
	public boolean tryShortcutFailedInsert(InventoryOptimizer thisOpt, IInventory other, InventoryOptimizer otherOpt) {
		return false; //hopper minecarts are not transferring items out. This won't break mods that implement it though.
	}


	public void setMarkOtherDirty() {
		this.previousExtract_causeMarkDirty = true;
	}
}

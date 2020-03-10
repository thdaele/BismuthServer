package si.bismuth.hopperoptimisations.mixins;

import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.ILockableContainer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import si.bismuth.hopperoptimisations.HopperSettings;
import si.bismuth.hopperoptimisations.utils.DoubleInventoryOptimizer;
import si.bismuth.hopperoptimisations.utils.InventoryOptimizer;
import si.bismuth.hopperoptimisations.utils.OptimizedInventory;

import javax.annotation.Nullable;

@Mixin(InventoryLargeChest.class)
public abstract class DoubleInventoryMixin implements OptimizedInventory {
	@Final
	@Shadow(aliases = "upperChest")
	private ILockableContainer first;
	@Final
	@Shadow(aliases = "lowerChest")
	private ILockableContainer second;
	//Invalidate this DoubleInventory when one half is invalidated.
	//This wasn't necessary in vanilla, because the DoubleInventory object was recreated every time the doublechest was accessed.
	private int firstInvalidCount;
	private int secondInvalidCount;
	private boolean invalid; //If true, this inventory will not be cached and will not be reused from a cache.

	private DoubleInventoryOptimizer optimizer; //Make sure this is only used when both of its halfs have optimizers

	@Inject(method = "<init>", at = @At(value = "RETURN"))
	private void initValidityCheck(String nameIn, ILockableContainer upperChestIn, ILockableContainer lowerChestIn, CallbackInfo ci) {
		if (!HopperSettings.optimizedInventories) {
			invalid = true;
			return;
		}
		if (upperChestIn == lowerChestIn) {
			invalid = true;
			return;
		}

		if (!(upperChestIn instanceof TileEntityChest) || !(lowerChestIn instanceof TileEntityChest) ||
				!(upperChestIn instanceof OptimizedInventory) || !(lowerChestIn instanceof OptimizedInventory)) {
			invalid = true;
			return;
		}
		firstInvalidCount = ((OptimizedInventory) upperChestIn).getInvalidCount();
		secondInvalidCount = ((OptimizedInventory) lowerChestIn).getInvalidCount();
		invalid = (firstInvalidCount == -1 || secondInvalidCount == -1);
	}

    /*public void setInvalid(){
        this.invalid = true;
    }*/

	private DoubleInventoryOptimizer getCreateOrRemoveOptimizer() {
		if (!HopperSettings.optimizedInventories) { //Remove first's and second's optimizers
			this.invalidateOptimizer();
			return this.optimizer;
		}

		if (this.optimizer == null) {
			if (((OptimizedInventory) first).getOptimizer() == null || ((OptimizedInventory) second).getOptimizer() == null) {
				System.out.println("Bad initialisation of OptimizedInventory's stacklist! Skipping optmizations!");
				return null;
			}
			this.optimizer = new DoubleInventoryOptimizer((OptimizedInventory) first, (OptimizedInventory) second);
		} else if (this.optimizer.isInvalid()) {
			this.invalidateOptimizer();
		}
		return this.optimizer;
	}

	@Override
	@Nullable
	public InventoryOptimizer getOptimizer() {
		return mayHaveOptimizer() ? getCreateOrRemoveOptimizer() : null;
	}

	@Override
	public void invalidateOptimizer() {
		if (this.first == null) {
			System.out.println("Double Inventory with empty first half!");
		} else if (this.first instanceof OptimizedInventory) {
			((OptimizedInventory) this.first).invalidateOptimizer();
		}
		if (this.second == null) {
			System.out.println("Double Inventory with empty second half!");
		} else if (this.second instanceof OptimizedInventory) {
			((OptimizedInventory) this.second).invalidateOptimizer();
		}
		if (this.optimizer != null)
			this.optimizer.setInvalid();
		this.optimizer = null;
	}

	@Override
	public boolean mayHaveOptimizer() {
		return this.first instanceof OptimizedInventory && ((OptimizedInventory) this.first).mayHaveOptimizer()
				&& this.second instanceof OptimizedInventory && ((OptimizedInventory) this.second).mayHaveOptimizer();
	}

	//This doesn't get called on the cached object, because opening an inventory creates a new Double Inventory Object.
    /*@Inject(method = "onInvOpen(Lnet/minecraft/entity/player/PlayerEntity;)V", at = @At(value = "HEAD"))
    private void onInventoryOpened(PlayerEntity playerEntity_1, CallbackInfo ci) {
        if (!playerEntity_1.isSpectator())
            invalidateOptimizer();
    }*/


	//Allows caching the inventory safely
	public boolean isStillValid() {
		return !this.invalid && !(this.invalid = firstInvalidCount != ((OptimizedInventory) first).getInvalidCount()) &&
				!(this.invalid = secondInvalidCount != ((OptimizedInventory) second).getInvalidCount());
	}

}

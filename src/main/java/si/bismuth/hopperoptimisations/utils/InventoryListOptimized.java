package si.bismuth.hopperoptimisations.utils;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import org.apache.commons.lang3.Validate;
import si.bismuth.hopperoptimisations.HopperSettings;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class InventoryListOptimized extends NonNullList<ItemStack> {
	private InventoryOptimizer optimizer = null;
	private int sizeOverride = -1; //used for Minecart Inventories to pretend to be small, just like they do

	private InventoryListOptimized() {
		super();
	}

	public InventoryListOptimized(List<ItemStack> list_1, @Nullable ItemStack object_1) {
		super(list_1, object_1);
	}

	public static NonNullList<ItemStack> of() {
		return new InventoryListOptimized();
	}

	public static NonNullList<ItemStack> ofSize(int int_1, ItemStack object_1) {
		Validate.notNull(object_1);
		ItemStack[] objects_1 = new ItemStack[int_1];
		Arrays.fill(objects_1, object_1);
		return new InventoryListOptimized(Arrays.asList(objects_1), object_1);
	}

	public InventoryOptimizer getCreateOrRemoveOptimizer(IInventory inventory) {
		if (!HopperSettings.optimizedInventories) return this.optimizer = null;

		if (this.optimizer == null) {
			this.optimizer = new InventoryOptimizer(this, inventory);
		}
		if (this.optimizer.isInvalid()) {
			this.optimizer = null;
		}
		return this.optimizer;
	}

	public InventoryOptimizer getOrRemoveOptimizer() {
		if (!HopperSettings.optimizedInventories || this.optimizer == null || this.optimizer.isInvalid())
			return this.optimizer = null;
		return optimizer;
	}

	public void invalidateOptimizer() {
		if (this.optimizer != null)
			this.optimizer.setInvalid();
		this.optimizer = null;
	}

	public ItemStack set(int slotIndex, ItemStack newStack) {
		ItemStack prevStack = super.set(slotIndex, newStack);
		if (HopperSettings.optimizedInventories) {
			InventoryOptimizer opt = this.getOrRemoveOptimizer();
			if (opt != null) opt.update(slotIndex, prevStack);
		} else invalidateOptimizer();
		return prevStack;
	}

    /*
    //Debug code to see who causes trouble with item stacks
    public E get(int int_1) {
        if(printStackTrace)
            new UnsupportedOperationException().printStackTrace();
        Object e = super.get(int_1);
        if(printStackTrace)
            System.out.println(e.toString());
        return (E)e;
    }
    */

	public void add(int int_1, ItemStack object_1) {
		if (HopperSettings.debugOptimizedInventories && HopperSettings.optimizedInventories)
			throw new UnsupportedOperationException("Won't resize optimized inventory!");
		else
			super.add(int_1, object_1);
	}

	public ItemStack remove(int int_1) {
		if (HopperSettings.debugOptimizedInventories && HopperSettings.optimizedInventories)
			throw new UnsupportedOperationException("Won't resize optimized inventory!");
		else
			return super.remove(int_1);
	}

	public void clear() {
		this.invalidateOptimizer();
		super.clear();
	}

	public void setSize(int size) {
		sizeOverride = size;
	}

	@Override
	public int size() {
		if (sizeOverride >= 0 && HopperSettings.optimizedInventories)
			return sizeOverride;
		return super.size();
	}
}

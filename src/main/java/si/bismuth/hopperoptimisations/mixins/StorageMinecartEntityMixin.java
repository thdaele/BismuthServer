package si.bismuth.hopperoptimisations.mixins;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import si.bismuth.hopperoptimisations.utils.InventoryListOptimized;
import si.bismuth.hopperoptimisations.utils.InventoryOptimizer;
import si.bismuth.hopperoptimisations.utils.OptimizedInventory;

import javax.annotation.Nullable;

@Mixin(EntityMinecartContainer.class)
public abstract class StorageMinecartEntityMixin extends EntityMinecart implements OptimizedInventory {
	private boolean initialized;
	//Redirects and Injects to replace the inventory with an optimized Inventory
	@Shadow(aliases = "minecartContainerItems")
	private NonNullList<ItemStack> inventory;
	private int viewerCount;

	public StorageMinecartEntityMixin(World worldIn) {
		super(worldIn);
	}


	@Inject(method = "<init>(Lnet/minecraft/world/World;)V", at = @At("RETURN"))
	private void createInventory(World world, CallbackInfo ci) {
		NonNullList<ItemStack> ret = InventoryListOptimized.ofSize(36, ItemStack.EMPTY);
		((InventoryListOptimized) ret).setSize(this.getSizeInventory()); //Storage Minecarts pretend to have smaller inventories
		this.inventory = ret;
	}

	@Inject(method = "<init>(Lnet/minecraft/world/World;DDD)V", at = @At("RETURN"))
	private void createInventory1(World world, double x, double y, double z, CallbackInfo ci) {
		NonNullList<ItemStack> ret = InventoryListOptimized.ofSize(36, ItemStack.EMPTY);
		((InventoryListOptimized) ret).setSize(this.getSizeInventory()); //Storage Minecarts pretend to have smaller inventories
		this.inventory = ret;
	}

	@Redirect(method = "readEntityFromNBT", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/NonNullList;withSize(ILjava/lang/Object;)Lnet/minecraft/util/NonNullList;"))
	private NonNullList<ItemStack> createInventory2(int int_1, Object object_1) {
		NonNullList<ItemStack> ret = InventoryListOptimized.ofSize(int_1, (ItemStack) object_1);
		((InventoryListOptimized) ret).setSize(this.getSizeInventory());
		return ret;
	}

	@Nullable
	public InventoryOptimizer getOptimizer() {
		return mayHaveOptimizer() && inventory instanceof InventoryListOptimized ? ((InventoryListOptimized) inventory).getCreateOrRemoveOptimizer(this) : null;
	}

	@Override
	public void invalidateOptimizer() {
		if (inventory instanceof InventoryListOptimized) ((InventoryListOptimized) inventory).invalidateOptimizer();
	}

    /*
    public void onInvOpen(PlayerEntity playerEntity_1) {
        if (!playerEntity_1.isSpectator()) {
            if (Settings.playerInventoryDeoptimization)
                invalidateOptimizer();
            viewerCount++;
        }
    }

    public void onInvClose(PlayerEntity playerEntity_1) {
        if (!playerEntity_1.isSpectator()) {
            viewerCount--;
            if (Settings.playerInventoryDeoptimization) {
                if (viewerCount < 0) {
                    System.out.println("StorageMinecartEntityMixin: (Inventory-)viewerCount inconsistency detected, might affect performance of optimizedInventories!");
                    viewerCount = 0;
                }
            }
        }
    }*/

	@Override
	public boolean mayHaveOptimizer() {
		return !this.world.isRemote;// && (!Settings.playerInventoryDeoptimization || viewerCount <= 0);
	}

    /* //replaced with code in EntityMixin
    @Override
    public void tick() {
        super.tick();
        if (!this.world.isClient && (this.prevX != this.getX() || this.prevY != this.getY() || this.prevZ != this.getZ() || !initialized)) {
            EntityHopperInteraction.findAndNotifyHoppers(this);
            initialized = true;
        }
    }*/
}

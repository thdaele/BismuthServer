package si.bismuth.hopperoptimisations.mixins;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.NonNullList;
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
import java.util.Arrays;

@Mixin(TileEntityDispenser.class)
public abstract class DispenserBlockEntityMixin extends TileEntityLockableLoot implements OptimizedInventory {
	private int viewerCount = 0;
	@Shadow(aliases = "stacks")
	private NonNullList<ItemStack> inventory;

	//Redirects and Injects to replace the inventory with an optimized Inventory
	@Inject(method = "<init>", at = @At(value = "RETURN"))
	private void createInventory(CallbackInfo ci) {
		this.inventory = InventoryListOptimized.ofSize(9, ItemStack.EMPTY);
	}

	// TODO handle later
	@Inject(method = "setInvStackList", at = @At("RETURN"))
	private void onSetStackList(NonNullList<ItemStack> stackList, CallbackInfo ci) {
		if (!(inventory instanceof InventoryListOptimized))
			inventory = new InventoryListOptimized(Arrays.asList((ItemStack[]) inventory.toArray()), ItemStack.EMPTY);
	}

	@Redirect(method = "readFromNBT", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/NonNullList;withSize(ILjava/lang/Object;)Lnet/minecraft/util/NonNullList;"))
	private NonNullList<ItemStack> createInventory2(int int_1, Object object_1) {
		return InventoryListOptimized.ofSize(int_1, (ItemStack) object_1);
	}

	@Nullable
	public InventoryOptimizer getOptimizer() {
		return mayHaveOptimizer() && this.inventory instanceof InventoryListOptimized ? ((InventoryListOptimized) this.inventory).getCreateOrRemoveOptimizer(this) : null;
	}

	@Override
	public void invalidateOptimizer() {
		if (this.inventory instanceof InventoryListOptimized) {
			((InventoryListOptimized) this.inventory).invalidateOptimizer();
		}
	}

	//@Inject(method = "onInvOpen(Lnet/minecraft/entity/player/PlayerEntity;)V", at = @At(value = "HEAD"))
    /*
    public void onInvOpen(PlayerEntity playerEntity_1) {
        if (!playerEntity_1.isSpectator()) {
            viewerCount++;
            if (Settings.playerInventoryDeoptimization)
                invalidateOptimizer();
        }
    }*/
/*
    public void onInvClose(PlayerEntity playerEntity_1) {
        if (!playerEntity_1.isSpectator()) {
            viewerCount--;
            if (Settings.playerInventoryDeoptimization && viewerCount < 0) {
                System.out.println("Dropper/Dispenser viewer count inconsistency, might affect performance of optimizedInventories!");
                viewerCount = 0;
            }
        }
    }*/

	@Override
	public boolean mayHaveOptimizer() {
		return this.world != null && !this.world.isRemote;// && (!Settings.playerInventoryDeoptimization || viewerCount <= 0);
	}

}

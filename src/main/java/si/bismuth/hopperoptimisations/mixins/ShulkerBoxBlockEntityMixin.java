package si.bismuth.hopperoptimisations.mixins;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.tileentity.TileEntityShulkerBox;
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

@Mixin(TileEntityShulkerBox.class)
public abstract class ShulkerBoxBlockEntityMixin extends TileEntityLockableLoot implements OptimizedInventory {
	@Shadow(aliases = "items")
	private NonNullList<ItemStack> inventory;
	@Shadow(aliases = "openCount")
	private int viewerCount;

	//Redirects and Injects to replace the inventory with an optimized Inventory
	@Redirect(method = "<init>(Lnet/minecraft/item/EnumDyeColor;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/NonNullList;withSize(ILjava/lang/Object;)Lnet/minecraft/util/NonNullList;"))
	private NonNullList<ItemStack> createInventory(int int_1, Object object_1) {
		return InventoryListOptimized.ofSize(int_1, (ItemStack) object_1);
	}

	// TODO: where is this??
	@Inject(method = "setInvStackList", at = @At("RETURN"))
	private void onSetStackList(NonNullList<ItemStack> stackList, CallbackInfo ci) {
		if (!(inventory instanceof InventoryListOptimized))
			inventory = new InventoryListOptimized(Arrays.asList((ItemStack[]) inventory.toArray()), ItemStack.EMPTY);
	}

	@Redirect(method = "loadFromNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/NonNullList;withSize(ILjava/lang/Object;)Lnet/minecraft/util/NonNullList;"))
	private NonNullList<ItemStack> createInventory2(int int_1, Object object_1) {
		return InventoryListOptimized.ofSize(int_1, (ItemStack) object_1);
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
    @Inject(method = "onInvOpen(Lnet/minecraft/entity/player/PlayerEntity;)V", at = @At(value = "HEAD"))
    private void onInvOpened(PlayerEntity playerEntity_1, CallbackInfo ci) {
        if (Settings.playerInventoryDeoptimization && !playerEntity_1.isSpectator())
            invalidateOptimizer();
    }*/

	@Override
	public boolean mayHaveOptimizer() {
		return this.world != null && !this.world.isRemote;// && (!Settings.playerInventoryDeoptimization || viewerCount <= 0);
	}
}

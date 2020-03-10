package si.bismuth.hopperoptimisations.mixins;

import net.minecraft.block.BlockChest;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import si.bismuth.hopperoptimisations.utils.InventoryListOptimized;
import si.bismuth.hopperoptimisations.utils.InventoryOptimizer;
import si.bismuth.hopperoptimisations.utils.OptimizedInventory;

import javax.annotation.Nullable;
import java.util.Arrays;

@Mixin(TileEntityChest.class)
public abstract class ChestBlockEntityMixin extends TileEntityLockableLoot implements OptimizedInventory, ITickable {
	@Shadow
	private NonNullList<ItemStack> chestContents;
	private int invalidCount;

	// Redirects and Injects to replace the inventory with an optimized Inventory
	@Inject(method = "<init>(Lnet/minecraft/block/BlockChest$Type;)V", at = @At("RETURN"))
	private void createInventory(BlockChest.Type typeIn, CallbackInfo ci) {
		this.chestContents = NonNullList.withSize(27, ItemStack.EMPTY);
	}

	@Inject(method = "getItems", at = @At("RETURN"))
	private void onSetStackList(CallbackInfoReturnable<NonNullList<ItemStack>> cir) {
		if (!(this.chestContents instanceof InventoryListOptimized))
			this.chestContents = new InventoryListOptimized(Arrays.asList((ItemStack[]) chestContents.toArray()), ItemStack.EMPTY);
	}

	@Redirect(method = "readFromNBT", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/NonNullList;withSize(ILjava/lang/Object;)Lnet/minecraft/util/NonNullList;"))
	private NonNullList<ItemStack> createInventory2(int int_1, Object object_1) {
		return InventoryListOptimized.ofSize(int_1, (ItemStack) object_1);
	}

	@Nullable
	public InventoryOptimizer getOptimizer() {
		return mayHaveOptimizer() && chestContents instanceof InventoryListOptimized ? ((InventoryListOptimized) chestContents).getCreateOrRemoveOptimizer(this) : null;
	}

	@Override
	public void invalidateOptimizer() {
		if (chestContents instanceof InventoryListOptimized)
			((InventoryListOptimized) chestContents).invalidateOptimizer();
	}

	/*
	@Inject(method = "onInvOpen(Lnet/minecraft/entity/player/PlayerEntity;)V", at = @At(value = "HEAD"))
	private void onInventoryOpened(PlayerEntity playerEntity_1, CallbackInfo ci) {
		if (HopperSettings.playerInventoryDeoptimization && !playerEntity_1.isSpectator())
			invalidateOptimizer();
	}*/

	@Override
	public boolean mayHaveOptimizer() {
		return this.world != null && !this.world.isRemote;// && (!HopperSettings.playerInventoryDeoptimization || viewerCount <= 0);
	}


	// Making sure that DoubleInventories don't act on invalid chest halfs using counter comparison.
	public void markRemoved() {
		this.invalidCount++;
		if (invalidCount == 0) invalidCount = -1;
		super.invalidate();
	}

	public int getInvalidCount() {
		return invalidCount;
	}
}

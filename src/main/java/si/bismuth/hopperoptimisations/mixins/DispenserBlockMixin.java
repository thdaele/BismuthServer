package si.bismuth.hopperoptimisations.mixins;

import net.minecraft.block.BlockDispenser;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityDispenser;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockDispenser.class)
public abstract class DispenserBlockMixin {
	@Redirect(method = "dispense", at = @At(value = "INVOKE", target = "Lnet/minecraft/tileentity/TileEntityDispenser;getStackInSlot(I)Lnet/minecraft/item/ItemStack;"))
	private ItemStack getItemStackCopy(TileEntityDispenser dispenserBlockEntity, int slot) {
		//Sometimes dispense behaviors change items, sometimes they don't.
		//When the stack is placed in the inventory, the optimizer is notified of any change.
		//Using a copied stack makes sure that a dispense behavior changing the stack isn't going unnoticed.
		return dispenserBlockEntity.getStackInSlot(slot).copy();
	}
}

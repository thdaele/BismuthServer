package carpet.bismuth.mixins;

import carpet.bismuth.CarpetSettings;
import carpet.bismuth.helpers.CtrlQCrafting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Container.class)
public abstract class ContainerMixin
{
    @Shadow
    public List<Slot> inventorySlots;
    
    @Inject(method = "slotClick", at = @At("HEAD"), cancellable = true)
    private void onSlotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player, CallbackInfoReturnable<ItemStack> cir)
    {
        if (clickTypeIn == ClickType.THROW && CarpetSettings.ctrlQCraftingFix && player.inventory.getItemStack().isEmpty() && slotId >= 0)
        {
            ItemStack itemStack = ItemStack.EMPTY;
            Slot slot = inventorySlots.get(slotId);
            if (slot != null && slot.canTakeStack(player))
            {
                if (slotId == 0 && dragType == 1)
                {
                    ItemStack itemStackDropAll = CtrlQCrafting.dropAllCrafting(player, slotId, inventorySlots);
                    while (!itemStackDropAll.isEmpty() && ItemStack.areItemsEqual(slot.getStack(), itemStackDropAll))
                    {
                        itemStack = itemStackDropAll.copy();
                        itemStackDropAll = CtrlQCrafting.dropAllCrafting(player, slotId, inventorySlots);
                    }
                    cir.setReturnValue(itemStack);
                    cir.cancel();
                }
            }
        }
    }
}

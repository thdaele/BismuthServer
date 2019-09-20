package carpet.bismuth.mixins;

import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemStack.class)
public interface IItemStackMixin {
	@Accessor
	void setStackSize(int size);
}

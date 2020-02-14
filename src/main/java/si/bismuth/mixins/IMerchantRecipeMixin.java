package si.bismuth.mixins;

import net.minecraft.village.MerchantRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MerchantRecipe.class)
public interface IMerchantRecipeMixin {
	@Accessor
	void setToolUses(int value);
}

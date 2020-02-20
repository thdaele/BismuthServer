package si.bismuth.mixins;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(ContainerMerchant.class)
public abstract class MixinContainerMerchant {
	private final Random rand = new Random();

	@Shadow
	@Final
	private IMerchant merchant;

	@Inject(method = "onContainerClosed", at = @At("HEAD"))
	private void unlockRecipes(EntityPlayer player, CallbackInfo ci) {
		final MerchantRecipeList recipes = this.merchant.getRecipes(player);
		if (recipes == null) {
			return;
		}

		boolean shouldUnlock = true;
		for (MerchantRecipe recipe : recipes) {
			if (!recipe.isRecipeDisabled()) {
				shouldUnlock = false;
			}
		}

		if (shouldUnlock) {
			for (MerchantRecipe recipe : recipes) {
				recipe.increaseMaxTradeUses(this.rand.nextInt(6) + this.rand.nextInt(6) + 2);
			}
		}
	}
}

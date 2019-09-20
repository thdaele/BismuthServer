package carpet.bismuth.mixins;

import carpet.bismuth.utils.IRecipeBookItemDuper;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerMP.class)
public abstract class EntityPlayerMPMixin implements IRecipeBookItemDuper {
	private int dupe;
	private boolean scanForDuping;

	@Override
	public void clearDupeItem() {
		this.dupe = Integer.MIN_VALUE;
	}

	@Override
	public void dupeItem(int slot) {
		if (this.scanForDuping) {
			this.dupe = slot;
		}
	}

	@Override
	public int getDupeItem() {
		return this.dupe;
	}

	@Override
	public void dupeItemScan(boolean s) {
		this.scanForDuping = s;
	}

	@Inject(method = "onUpdate", at = @At("RETURN"))
	private void postOnUpdate(CallbackInfo ci) {
		this.clearDupeItem();
	}
}

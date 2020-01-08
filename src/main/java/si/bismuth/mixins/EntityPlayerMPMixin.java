package si.bismuth.mixins;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import si.bismuth.MCServer;
import si.bismuth.utils.IRecipeBookItemDuper;

@Mixin(EntityPlayerMP.class)
public abstract class EntityPlayerMPMixin extends EntityPlayer implements IRecipeBookItemDuper {
	private int dupe;
	private boolean scanForDuping;

	public EntityPlayerMPMixin(World worldIn, GameProfile gameProfileIn) {
		super(worldIn, gameProfileIn);
	}

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

	@Redirect(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/management/PlayerList;sendMessage(Lnet/minecraft/util/text/ITextComponent;)V"))
	private void sendMessage(PlayerList list, ITextComponent component) {
		list.sendMessage(component);
		MCServer.bot.sendDeathmessage(component);
		MCServer.LOG.info("Player {} died at {} {} {} in {}", this.getName(), this.posX, this.posY, this.posZ, this.world.provider.getDimensionType().getName());
	}
}

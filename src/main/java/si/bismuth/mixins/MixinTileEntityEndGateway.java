package si.bismuth.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntityEndGateway;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import si.bismuth.MCServer;
import si.bismuth.commands.CommandAllowGateway;

@Mixin(TileEntityEndGateway.class)
public abstract class MixinTileEntityEndGateway extends TileEntityEndPortal {
	@Unique
	private static final ITextComponent DENY = new TextComponentString("Stop going through ungenerated end gateways.").setStyle(new Style().setColor(TextFormatting.DARK_RED));

	@Shadow
	private BlockPos exitPortal;

	@Shadow
	protected abstract void findExitPortal();

	@Inject(method = "teleportEntity", at = @At("HEAD"), cancellable = true)
	private void preventPeopleFromEnteringEndGatewayNearSpawn(Entity entity, CallbackInfo ci) {
		boolean shouldBlockTeleport = false;
		if (this.exitPortal == null && this.getPos().distanceSq(0D, 75D, 0D) < 16384D) {
			shouldBlockTeleport = true;
			if (entity instanceof EntityPlayer) {
				final EntityPlayer player = (EntityPlayer) entity;
				if (CommandAllowGateway.canEnterPortal) {
					shouldBlockTeleport = false;
					this.findExitPortal();
					CommandAllowGateway.canEnterPortal = false;
					MCServer.bot.sendToDiscord("**" + player.getName() + " generated gateway " + this.pos + "**");
				} else {
					if (this.world.getTotalWorldTime() % 20L == 0L) {
						player.sendMessage(DENY);
					}
				}
			}
		}

		if (shouldBlockTeleport) {
			ci.cancel();
		}
	}
}

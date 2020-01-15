package si.bismuth.mixins;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.play.client.CPacketPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import si.bismuth.MCServer;

@Mixin(NetHandlerPlayServer.class)
public abstract class NetHandlerPlayServerMixin {
	@Shadow
	public EntityPlayerMP player;

	@Inject(method = "processCustomPayload", at = @At(value = "TAIL"))
	private void onProcessCustomPayload(CPacketCustomPayload packet, CallbackInfo ci) {
		MCServer.pcm.processIncoming(this.player, packet);
	}

	@Redirect(method = "processPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayerMP;isInvulnerableDimensionChange()Z"))
	private boolean preventPlayerMovedWronglyOrTooQuickly(EntityPlayerMP player) {
		return true;
	}

	@Inject(method = "processPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetHandlerPlayServer;disconnect(Lnet/minecraft/util/text/ITextComponent;)V"))
	private void debugPlayerBeingKicked(CPacketPlayer packet, CallbackInfo ci) {
		if (Double.isNaN(packet.getX(0D)) || Double.isNaN(packet.getY(0D)) || Double.isNaN(packet.getZ(0D))) {
			MCServer.bot.sendToDiscord(String.format("Detected and adjusted: %s (%s %s %s) %s %s (%s %s %s)", this.player.getName(), packet.getX(0D), packet.getY(0D), packet.getZ(0D), packet.getPitch(0F), packet.getYaw(0F), this.player.posX, this.player.posY, this.player.posZ));
			this.player.connection.setPlayerLocation(this.player.posX, this.player.posY, this.player.posZ, this.player.rotationYaw, this.player.rotationPitch);
		}
	}
}

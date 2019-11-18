package si.bismuth.mixins;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import si.bismuth.MCServer;

@Mixin(NetHandlerPlayServer.class)
public abstract class NetHandlerPlayServerMixin {
	@Shadow
	public EntityPlayerMP player;

	@Inject(method = "processCustomPayload", at = @At(value = "TAIL"))
	private void onProcessCustomPayload(CPacketCustomPayload packet, CallbackInfo ci) {
		MCServer.channelManager.processIncoming(this.player, packet);
	}
}

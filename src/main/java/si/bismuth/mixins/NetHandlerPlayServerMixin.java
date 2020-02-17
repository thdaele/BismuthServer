package si.bismuth.mixins;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import org.objectweb.asm.Opcodes;
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

	@Redirect(method = "processPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetHandlerPlayServer;disconnect(Lnet/minecraft/util/text/ITextComponent;)V"))
	private void debugPlayerBeingKicked(NetHandlerPlayServer handler, ITextComponent component) {
		handler.player.sendStatusMessage(new TextComponentString("If you're OptiFine F4 or later, disable Fast Math!"), true);
		this.player.setPositionAndUpdate(this.player.posX, this.player.posY, this.player.posZ);
	}

	@Redirect(method = "processEntityAction", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/EntityPlayerMP;motionY:D", opcode = Opcodes.GETFIELD))
	private double mc111444(EntityPlayerMP player) {
		return -1D;
	}
}

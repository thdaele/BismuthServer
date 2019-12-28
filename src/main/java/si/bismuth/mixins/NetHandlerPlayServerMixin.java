package si.bismuth.mixins;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketVehicleMove;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
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

	/**
	 * @author nessie
	 * @reason simplest fix
	 */
	@Overwrite
	private static boolean isMovePlayerPacketInvalid(CPacketPlayer packet) {
		return false;
	}

	/**
	 * @author nessie
	 * @reason simplest fix
	 */
	@Overwrite
	private static boolean isMoveVehiclePacketInvalid(CPacketVehicleMove packet) {
		return false;
	}

	@Redirect(method = "processUseEntity", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/network/NetHandlerPlayServer;player:Lnet/minecraft/entity/player/EntityPlayerMP;", ordinal = 0), slice = @Slice(from = @At(value = "FIELD", opcode = Opcodes.GETSTATIC, target = "Lnet/minecraft/network/play/client/CPacketUseEntity$Action;ATTACK:Lnet/minecraft/network/play/client/CPacketUseEntity$Action;")))
	private EntityPlayerMP preventPlayerAttackedInvalidEntity(NetHandlerPlayServer handler) {
		return null;
	}
}

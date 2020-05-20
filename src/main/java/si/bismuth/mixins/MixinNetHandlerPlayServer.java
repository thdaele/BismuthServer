package si.bismuth.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
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
public abstract class MixinNetHandlerPlayServer {
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
		handler.player.sendStatusMessage(new TextComponentString("If you're using OptiFine F4 or later, disable Fast Math!"), true);
		this.player.setPositionAndUpdate(this.player.posX, this.player.posY, this.player.posZ);
	}

	@Redirect(method = "processEntityAction", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/EntityPlayerMP;motionY:D", opcode = Opcodes.GETFIELD))
	private double mc111444(EntityPlayerMP player) {
		return -1D;
	}

	@Redirect(method = "processUseEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayerMP;canEntityBeSeen(Lnet/minecraft/entity/Entity;)Z"))
	private boolean mc107103(EntityPlayerMP player, Entity entity) {
		return true;
	}

	@Redirect(method = "processUseEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayerMP;interactOn(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/EnumHand;)Lnet/minecraft/util/EnumActionResult;"))
	private EnumActionResult onRightClickVillager(EntityPlayerMP player, Entity entity, EnumHand hand) {
		if (this.player.isSneaking() && entity instanceof EntityVillager) {
			final EntityVillager villager = (EntityVillager) entity;
			final InventoryBasic inventory = villager.getVillagerInventory();
			final StringBuilder builder = new StringBuilder();
			for (int i = 0; i < inventory.getSizeInventory(); i++) {
				final ItemStack stack = inventory.getStackInSlot(i);
				if (!stack.isEmpty()) {
					builder.append(stack.getCount()).append(" ").append(stack.getDisplayName()).append(" ");
				}
			}

			final String output = builder.toString();
			if (!output.isEmpty()) {
				final ITextComponent message = new TextComponentString(builder.toString());
				final SPacketTitle packetOut = new SPacketTitle(SPacketTitle.Type.ACTIONBAR, message);
				this.player.connection.sendPacket(packetOut);
				return EnumActionResult.PASS;
			}
		}

		return player.interactOn(entity, hand);
	}
}

package si.bismuth.hopperoptimisations.mixins;

import net.minecraft.command.server.CommandTeleport;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import si.bismuth.hopperoptimisations.utils.EntityHopperInteraction;

@Mixin(CommandTeleport.class)
public class TeleportCommandMixin {
	@Redirect(method = "doTeleport", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setRotationYawHead(F)V", ordinal = 1))
	private static void notifyHoppersAndSetHeadYaw(Entity entity, float headYaw) {
		entity.setRotationYawHead(headYaw);
		EntityHopperInteraction.notifyHoppersOfNewOrTeleportedEntity(entity);
	}
}

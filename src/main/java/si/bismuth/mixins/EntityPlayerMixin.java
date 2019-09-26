package si.bismuth.mixins;

import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin {
	@Redirect(method = "onLivingUpdate", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/EntityPlayer;fallDistance:F", opcode = Opcodes.GETFIELD))
	private float neverDismountParrots(EntityPlayer entityPlayer) {
		return 0F;
	}

	@Redirect(method = "attackEntityFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;spawnShoulderEntities()V"))
	private void neverEverDismountParrots(EntityPlayer entityPlayer) {
		// noop
	}
}

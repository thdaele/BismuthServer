package si.bismuth.ebictranslononation.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Entity.class)
public abstract class MixinEntity {
	@ModifyVariable(method = "move", ordinal = 0, at = @At("HEAD"), argsOnly = true)
	private MoverType mc124718(MoverType type) {
		if (type == MoverType.PISTON) {
			type = MoverType.SELF;
		}

		return type;
	}
}

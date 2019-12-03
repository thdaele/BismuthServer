package si.bismuth.mixins;

import net.minecraft.entity.item.EntityXPOrb;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityXPOrb.class)
public interface IEntityXPOrbMixin {
	@Accessor
	void setXpOrbAge(int age);
}

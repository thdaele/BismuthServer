package si.bismuth.mixins;

import net.minecraft.entity.item.EntityItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityItem.class)
public interface IEntityItemMixin {
	@Accessor
	int getPickupDelay();

	@Accessor
	int getAge();

	@Accessor
	void setAge(int age);
}

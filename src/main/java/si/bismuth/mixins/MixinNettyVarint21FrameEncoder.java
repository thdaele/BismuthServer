package si.bismuth.mixins;

import net.minecraft.network.NettyVarint21FrameEncoder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(NettyVarint21FrameEncoder.class)
public abstract class MixinNettyVarint21FrameEncoder {
	@ModifyConstant(method = "encode", constant = @Constant(intValue = 3))
	private int fixBookBan(int value) {
		return Integer.MAX_VALUE;
	}
}

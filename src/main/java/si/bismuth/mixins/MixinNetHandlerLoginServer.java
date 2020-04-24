package si.bismuth.mixins;

import net.minecraft.server.network.NetHandlerLoginServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(NetHandlerLoginServer.class)
public abstract class MixinNetHandlerLoginServer {
	@ModifyConstant(method = "update", constant = @Constant(intValue = 600))
	private int disableLoginTimeout(int value) {
		return -1;
	}
}

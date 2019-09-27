package si.bismuth.mixins;

import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RConConsoleSource.class)
public abstract class RConConsoleSourceMixin {
	@Shadow
	@Final
	private StringBuffer buffer;

	@Inject(method = "sendMessage", at = @At("RETURN"))
	private void mc7569(ITextComponent component, CallbackInfo ci) {
		this.buffer.append('\n');
	}
}

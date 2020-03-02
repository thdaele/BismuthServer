package si.bismuth.mixins;

import com.google.common.collect.Maps;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.HashMap;

@Mixin(Chunk.class)
public abstract class MixinChunk {
	@Redirect(method = "<init>(Lnet/minecraft/world/World;II)V", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Maps;newHashMap()Ljava/util/HashMap;", opcode = Opcodes.INVOKESTATIC, remap = false))
	private HashMap<BlockPos, TileEntity> reloadUpdateOrderFix() {
		return Maps.newLinkedHashMap();
	}
}

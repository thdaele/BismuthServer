package si.bismuth.mixins;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeForestMutated;
import net.minecraft.world.gen.feature.WorldGenBirchTree;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(WorldGenBirchTree.class)
public class WorldGenBirchTreeMixin {
	@Shadow
	@Final
	private boolean useExtraRandomHeight;
	private World worldCopy;
	private Random randCopy;
	private BlockPos posCopy;

	@Inject(method = "generate", at = @At("HEAD"))
	private void onGenerate(World world, Random rand, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		this.worldCopy = world;
		this.randCopy = rand;
		this.posCopy = pos;
	}

	@Redirect(method = "generate", at = @At(value = "FIELD", target = "Lnet/minecraft/world/gen/feature/WorldGenBirchTree;useExtraRandomHeight:Z", opcode = Opcodes.GETFIELD))
	private boolean shouldThisBeSuperBirch(WorldGenBirchTree gen) {
		return this.useExtraRandomHeight || this.worldCopy.getBiome(this.posCopy) instanceof BiomeForestMutated && this.randCopy.nextBoolean();
	}
}

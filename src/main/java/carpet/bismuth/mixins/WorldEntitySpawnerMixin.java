package carpet.bismuth.mixins;

import carpet.bismuth.utils.SpawnReporter;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(WorldEntitySpawner.class)
abstract class WorldEntitySpawnerMixin {
	@Shadow
	@Final
	private static int MOB_COUNT_DIV;

	@Inject(method = "findChunksForSpawning", at = @At(value = "FIELD", target = "Lnet/minecraft/world/WorldEntitySpawner;MOB_COUNT_DIV:I", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
	private void getMobcaps(WorldServer worldServerIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs, boolean spawnOnSetTickRate, CallbackInfoReturnable<Integer> cir, int i, int j4, BlockPos blockpos1, EnumCreatureType[] var8, int var9, int var10, EnumCreatureType enumcreaturetype, int k4) {
		int l4 = enumcreaturetype.getMaxNumberOfCreature() * i / MOB_COUNT_DIV;
		SpawnReporter.mobcaps.get(worldServerIn.provider.getDimensionType().getId()).put(enumcreaturetype, new Tuple<>(k4, l4));
	}
}

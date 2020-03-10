package si.bismuth.hopperoptimisations.mixins;

import hopperOptimizations.utils.EntityHopperInteraction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import si.bismuth.hopperoptimisations.HopperSettings;

@Mixin(targets = "net.minecraft.world.CollisionView$1") //Spliterator Subclass
public class CollisionView$1Mixin {
	private boolean notifyHoppers; //every call newly created with false, temporary var to eliminate check in the loop

	@Redirect(method = "tryAdvance(Ljava/util/function/Consumer;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/BlockView;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
	private IBlockState getBlockState_rememberHoppers(BlockView blockView, BlockPos blockPos) {
		IBlockState blockState = blockView.getBlockState(blockPos);
		if (!notifyHoppers) return blockState;

		EntityHopperInteraction.searchedForHoppers = true;
		if (blockState.getBlock() == Blocks.HOPPER)
			EntityHopperInteraction.hopperLocationsToNotify.add(blockPos.toImmutable());

		return blockState;
	}

	@Redirect(method = "tryAdvance(Ljava/util/function/Consumer;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getBoundingBox()Lnet/minecraft/util/math/Box;", ordinal = 0))
	private Box isClient_SpaghettiCall(Entity entity) {
		//not have to check this condition on every advance -> only on the first
		//can only set notify hoppers when entity is not null, which is intended
		World world = entity.getEntityWorld();
		notifyHoppers = HopperSettings.optimizedEntityHopperInteraction && EntityHopperInteraction.findHoppers && world != null && !world.isRemote;

		return entity.getBoundingBox();
	}
}

package si.bismuth.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import si.bismuth.utils.BlockRotator;

@Mixin(PlayerInteractionManager.class)
public abstract class MixinPlayerInteractionManager {
	@Shadow
	public World world;
	@Shadow
	public EntityPlayerMP player;

	@Inject(method = "onBlockClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;sendBlockBreakProgress(ILnet/minecraft/util/math/BlockPos;I)V"))
	private void notifyUpdate(BlockPos pos, EnumFacing face, CallbackInfo ci) {
		this.player.connection.sendPacket(new SPacketBlockChange(this.world, pos));
	}

	@Redirect(method = "processRightClickBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;onBlockActivated(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/EnumHand;Lnet/minecraft/util/EnumFacing;FFF)Z"))
	private boolean onProcessRightClickBlock(Block block, World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing face, float hitX, float hitY, float hitZ) {
		final boolean flipped = BlockRotator.flipBlockWithCactus(world, pos, state, player, face, hitX, hitY, hitZ);
		if (flipped) {
			return true;
		}

		return state.getBlock().onBlockActivated(world, pos, state, player, hand, face, hitX, hitY, hitZ);
	}
}

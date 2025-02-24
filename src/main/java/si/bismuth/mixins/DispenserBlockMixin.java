package si.bismuth.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.state.BlockState;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DispenserBlock.class)
public class DispenserBlockMixin {
    @Inject(method = "neighborChanged", at = @At("TAIL"))
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos neighborPos, CallbackInfo ci) {
        List<ServerPlayerEntity> players = world.getServer().getPlayerManager().getAll();
        if (players != null) {
            for (ServerPlayerEntity player : players) {
                player.networkHandler.sendPacket(new BlockUpdateS2CPacket(world, pos));
            }
        }
    }
}

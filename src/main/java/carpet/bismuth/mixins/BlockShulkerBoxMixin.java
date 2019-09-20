package carpet.bismuth.mixins;

import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BlockShulkerBox.class)
public abstract class BlockShulkerBoxMixin {
	private NBTTagCompound mynbttagcompound1;

	@Inject(method = "breakBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;setTag(Ljava/lang/String;Lnet/minecraft/nbt/NBTBase;)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
	private void keepACopy(World worldIn, BlockPos pos, IBlockState state, CallbackInfo ci, TileEntity te, TileEntityShulkerBox sb, ItemStack is, NBTTagCompound tag, NBTTagCompound tag1) {
		this.mynbttagcompound1 = tag1;
	}

	@Redirect(method = "breakBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;setTagCompound(Lnet/minecraft/nbt/NBTTagCompound;)V"))
	private void test(ItemStack itemStack, NBTTagCompound nbt) {
		if(this.mynbttagcompound1.getSize() > 0) {
			itemStack.setTagCompound(nbt);
		}
	}
}

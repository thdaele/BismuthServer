package si.bismuth.commands;

import com.google.common.collect.ImmutableList;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import si.bismuth.utils.InventoryHelper;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandSearchForItem extends CommandBismuthBase {
	@Override
	public String getName() {
		return "searchforitem";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/searchforitem [item] [damage] [nbt]";
	}

	public List<String> getAliases() {
		return ImmutableList.of("find", "finditem", "searchforitem");
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (!(sender instanceof EntityPlayerMP)) {
			throw new CommandException("Nonplayer " + sender + " tried to run /searchforitem!");
		}

		if (args.length == 0) {
			InventoryHelper.processFindItem((EntityPlayerMP) sender, ((EntityPlayerMP) sender).getHeldItemMainhand());
			return;
		}

		final Item item = getItemByText(sender, args[0]);
		final int damage = args.length >= 2 ? parseInt(args[1]) : 0;
		final ItemStack stack = new ItemStack(item, 1, damage);

		if (args.length >= 3) {
			final String s = buildString(args, 2);

			try {
				stack.setTagCompound(JsonToNBT.getTagFromJson(s));
			} catch (NBTException exception) {
				throw new CommandException("Data tag parsing failed: " + exception.getMessage());
			}
		}

		InventoryHelper.processFindItem((EntityPlayerMP) sender, stack);
	}

	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
		return args.length == 1 ? getListOfStringsMatchingLastWord(args, Item.REGISTRY.getKeys()) : Collections.emptyList();
	}
}

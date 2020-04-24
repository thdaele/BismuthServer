package si.bismuth.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class CommandDisplayItem extends CommandBismuthBase {
	@Override
	public String getName() {
		return "displayitem";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return this.getName();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (!(sender instanceof EntityPlayerMP)) {
			throw new CommandException("Unknown " + sender.getName() + " tried to run " + this.getName() + "!");
		}

		final ItemStack stack = ((EntityPlayerMP) sender).getHeldItemMainhand();
		if (!stack.isEmpty()) {
			final ITextComponent component = new TextComponentTranslation("chat.type.text", sender.getDisplayName(), stack.getTextComponent());
			server.getPlayerList().sendMessage(component, false);
		}
	}
}

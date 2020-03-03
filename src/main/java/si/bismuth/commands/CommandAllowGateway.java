package si.bismuth.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandAllowGateway extends CommandBase {
	public static boolean canEnterPortal = false;

	@Override
	public String getName() {
		return "allowgateway";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return this.getName();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
		canEnterPortal = !canEnterPortal;
		if (canEnterPortal) {
			sender.sendMessage(new TextComponentString("You may now enter an ungenerated gateway near spawn."
					+ " Will automatically disable itself once a player goes through such a portal."
					+ " Run command again to toggle off."));
		} else {
			sender.sendMessage(new TextComponentString("Blocked ungenerated gateways near spawn."));
		}
	}
}

package iblis.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CommandSetAttribute extends CommandBase {

	@Override
	public String getName() {
		return "setattribute";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return getName() + " attribute value";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length < 2) {
			throw new WrongUsageException(getUsage(sender), new Object[0]);
		} else {
			Entity command_sender = sender.getCommandSenderEntity();
			if (command_sender instanceof EntityPlayerMP) {
				EntityPlayerMP player = (EntityPlayerMP) command_sender;
				player.getAttributeMap().getAttributeInstanceByName(args[0]).setBaseValue(Double.parseDouble(args[1]));
			}
		}
	}

}

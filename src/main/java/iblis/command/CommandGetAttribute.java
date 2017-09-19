package iblis.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandGetAttribute extends CommandBase {

	@Override
	public String getName() {
		return "getattribute";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return getName();
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		Entity command_sender = sender.getCommandSenderEntity();
		if (args.length < 1) {
			throw new WrongUsageException(getUsage(sender), new Object[0]);
		} else if (!(command_sender instanceof EntityPlayerMP))
			return;
		EntityPlayerMP player = (EntityPlayerMP) command_sender;
		double value = player.getAttributeMap().getAttributeInstanceByName(args[0]).getBaseValue();
		command_sender.sendMessage(new TextComponentString("Attribute base value: " + value));
	}
}

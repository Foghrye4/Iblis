package iblis.command;

import java.util.Iterator;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;

public class CommandShowNBT extends CommandBase {

	@Override
	public String getName() {
		return "shownbt";
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
		if (!(command_sender instanceof EntityPlayerMP))
			return;
		EntityPlayerMP player = (EntityPlayerMP) command_sender;
		ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null)
			return;
		Iterator<String> iterator = tag.getKeySet().iterator();
		while (iterator.hasNext()) {
			String entry = iterator.next();
			if (tag.getTag(entry) instanceof NBTTagCompound) {
				command_sender.sendMessage(new TextComponentString("NBTtag '" + entry + "':"));
				NBTTagCompound ct = tag.getCompoundTag(entry);
				if (ct != null && ct.getKeySet() != null && !ct.getKeySet().isEmpty()) {
					command_sender.sendMessage(new TextComponentString(" -NBT compound tag subkeys:"));
					Iterator<?> stIterator = ct.getKeySet().iterator();
					while (stIterator.hasNext()) {
						String entry2 = (String) stIterator.next();
						String value = ct.getString(entry2);
						if(value.isEmpty())
							value = String.valueOf(ct.getInteger(entry2));
						command_sender
								.sendMessage(new TextComponentString("    " + entry2 + "=" + value));
					}
				}
			}else if (tag.getTag(entry) instanceof NBTTagList) {
				command_sender.sendMessage(new TextComponentString("NBTtagList '" + entry + "':"));
				NBTTagList list = tag.getTagList(entry, 10);
				for(int i=0;i<list.tagCount();i++){
					NBTTagCompound ct = list.getCompoundTagAt(i);
					if (ct != null && ct.getKeySet() != null && !ct.getKeySet().isEmpty()) {
						command_sender.sendMessage(new TextComponentString(" -NBT compound tag subkeys:"));
						Iterator<?> stIterator = ct.getKeySet().iterator();
						while (stIterator.hasNext()) {
							String entry2 = (String) stIterator.next();
							String value = ct.getString(entry2);
							if(value.isEmpty())
								value = String.valueOf(ct.getInteger(entry2));
							command_sender
									.sendMessage(new TextComponentString("    " + entry2 + "=" + value));
						}
					}
				}

			} else {
				String value = tag.getString(entry);
				if(value.isEmpty())
					value = String.valueOf(tag.getFloat(entry));
				command_sender.sendMessage(new TextComponentString(" " + entry + "=" + value));
			}
		}
	}
}

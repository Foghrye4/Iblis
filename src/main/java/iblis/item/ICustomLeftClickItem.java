package iblis.item;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public interface ICustomLeftClickItem {

	public void onLeftClick(World world, EntityPlayerMP player, EnumHand mainHand);
}

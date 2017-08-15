package iblis.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerDeathEventHandler {

	@SubscribeEvent
	public void onPlayerDeath(LivingDeathEvent event) {
		if(event.getEntityLiving() instanceof EntityPlayer){
			EntityPlayer player = (EntityPlayer) event.getEntityLiving();
			EntityPlayerZombie playerZombie = new EntityPlayerZombie(player);
			player.world.spawnEntity(playerZombie);
		}
			
	}
	
}
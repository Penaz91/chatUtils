package smallutils.chatutils;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLogoutListener implements Listener{
	@EventHandler
	public void onPlayerLogout(PlayerQuitEvent event){
		main.timeList.remove(event.getPlayer().getName());
	}
}

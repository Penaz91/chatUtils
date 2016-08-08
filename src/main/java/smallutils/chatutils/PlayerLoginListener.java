package smallutils.chatutils;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerLoginListener implements Listener{
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event){
		main.timeList.put(event.getPlayer().getName(), java.lang.System.currentTimeMillis());
	}
}

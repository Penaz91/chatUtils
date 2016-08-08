package smallutils.chatutils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener{
	@EventHandler
	public void onPlayerMessage(AsyncPlayerChatEvent event){
		if (main.config.getBoolean("chatCooldown")){
			if (!main.timeList.containsKey(event.getPlayer().getName())){
				main.timeList.put(event.getPlayer().getName(), java.lang.System.currentTimeMillis());
			}
			if (java.lang.System.currentTimeMillis() - main.timeList.get(event.getPlayer().getName()) < main.chatCooldown){
				event.setCancelled(true);
				event.getPlayer().sendMessage(main.defaultPrefix + "Please wait at least " + main.chatCooldown/1000 + "s between each message.");
				return;
			}else{
				main.timeList.put(event.getPlayer().getName(), java.lang.System.currentTimeMillis());
			}
		}
		if (main.LockDown && !(event.getPlayer().hasPermission("ChatUtils.bypassld"))){
			if (!(event.getPlayer().getName().equalsIgnoreCase(main.Locker))){
				event.setCancelled(true);
				event.getPlayer().sendMessage(main.defaultPrefix + "Chat is in Lockdown, only staff can talk");
			}
		}
		if (main.config.getBoolean("capsDetection")){
			double perc = 0;
			double num=0, den=0;
			char[] msg = event.getMessage().replaceAll("\\s+", "").toCharArray();
			if (msg.length < main.config.getInt("minCapsSize")){
				return;
			}
			for (char ch: msg){
				if (Character.isUpperCase(ch)){
					num++;
				}
				den++;
			}
			perc = (num/den) * 100;
			if (perc > main.config.getInt("capsPercentage")){
				if (main.config.getBoolean("capsReplacement")){
					String newmsg = event.getMessage().toLowerCase();
					event.setMessage(newmsg);
				}
				event.getPlayer().sendMessage(main.defaultPrefix + "Please don't overuse Capital letters in your messages");
			}
		}
		if (main.config.getBoolean("stringReplacement")){
			String message = event.getMessage();
			for (String key: main.replacers.keySet())
				if (message.indexOf(key) > -1){
					message = message.replaceAll(key, main.replacers.get(key));
				}
			event.setMessage(message);
		}
		if (main.config.getBoolean("mentions")){
			String message = event.getMessage();
			int index = message.indexOf("@");
			if (index > -1){
				int firstSpace = message.substring(index).indexOf(" ");
				if (firstSpace == -1){
					firstSpace = message.length();
				}
				String name = message.substring(index+1, firstSpace);
				@SuppressWarnings("deprecation")
				Player pl = Bukkit.getPlayer(name);
				try{
					pl.playSound(pl.getLocation(), Sound.valueOf(main.config.getString("notificationSound")), 50.0F, 50.0F);
				}catch (NullPointerException e){
					Bukkit.getLogger().info("[ChatUtils] Couldn't send Mention Sound Effect");
				}
			}
		}
	}
}

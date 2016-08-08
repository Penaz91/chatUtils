package smallutils.chatutils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class main extends JavaPlugin{
	public static boolean LockDown = false;
	public static String Locker = "";
	public static Configuration config = null;
	public static Map<String, String> replacers = new HashMap<String, String>();
	public static Map<String, Long> timeList = new HashMap<String, Long>();
	public static int chatCooldown = 0;
	public static String defaultPrefix = ChatColor.GOLD + "[" + ChatColor.DARK_PURPLE + "ChatUtils" + ChatColor.GOLD + "] ";
	public static String defaultPermissionsMsg = "You don't have permission to use this feature";
	public static List<String> patterns = new ArrayList<String>();
	@Override
	public void onEnable(){
		File f = getDataFolder();
		if (!f.exists()){
			f.mkdir();
			saveResource("config.yml", false);
		}
		getServer().getPluginManager().registerEvents(new ChatListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerLoginListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerLogoutListener(), this);
		config = this.getConfig();
		if (config.getBoolean("stringReplacement")){
			ConfigurationSection sec = config.getConfigurationSection("stringList");
			if (sec!=null){
				replacers.clear();
				for (String key: sec.getKeys(false)){
					if (key != null){
						replacers.put(key, config.getString("stringList."+key));
					}
				}
			}
			getLogger().info("[ChatUtils] Replacers enabled: " + replacers.size() + " keys found");
		}
		if (config.getBoolean("chatCooldown")){
			chatCooldown = config.getInt("cooldownMillis");
		}
	}
	@Override
	public void onDisable(){
		HandlerList.unregisterAll();
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if (cmd.getName().equalsIgnoreCase("chatutils")){
			if (args.length == 0){
				sender.sendMessage(ChatColor.GOLD + "-------<->-------ChatUtils-------<->-------");
				sender.sendMessage(ChatColor.GOLD + "A plugin by: " + ChatColor.RED + "Penaz");
				sender.sendMessage(ChatColor.GOLD + "-------------------<->-------------------");
				sender.sendMessage(ChatColor.GOLD + "Usage:");
				sender.sendMessage(ChatColor.GOLD + "- " + ChatColor.DARK_PURPLE + "/cu" + ChatColor.GRAY + " cc" + ChatColor.GOLD + " - Clear Chat");
				sender.sendMessage(ChatColor.GOLD + "- " + ChatColor.DARK_PURPLE + "/cu" + ChatColor.GRAY + " ld" + ChatColor.GOLD + " - Clear Lockdown");
				sender.sendMessage(ChatColor.GOLD + "- " + ChatColor.DARK_PURPLE + "@<player>" + ChatColor.GOLD + " Mention a player to have them hear a sound");
				sender.sendMessage(ChatColor.GOLD + "- " + ChatColor.DARK_PURPLE + "!<keyword>" + ChatColor.GOLD + " Use keywords that get replaced, like !website");
				sender.sendMessage(" ");
				sender.sendMessage(ChatColor.GOLD + "This plugin also has Capital letters limitation and message cooldowns to limit spam, check config.yml!");
				sender.sendMessage(ChatColor.GOLD + "-------------------<->-------------------");
				return true;
			}else{
				if (args[0].equalsIgnoreCase("cc")){
					if (sender.hasPermission("chatUtils.cc")){
						for (Player p: Bukkit.getOnlinePlayers()){
							if (!p.hasPermission("chatutils.bypasscc")){
								for (int i=0; i<120; i++){
									p.sendMessage(" ");
								}
							}
						}
						Bukkit.getServer().broadcastMessage(defaultPrefix + "Chat has been cleared by " + sender.getName());
						return true;
					}else{
						sender.sendMessage(defaultPrefix + defaultPermissionsMsg);
						return true;
					}
				}
				if (args[0].equalsIgnoreCase("ld")){
					if (sender.hasPermission("chatUtils.ld")){
						LockDown = !LockDown;
						if (LockDown){
							Locker = sender.getName();
							Bukkit.getServer().broadcastMessage(defaultPrefix + "Chat has been put in lockdown by " + sender.getName());
						}else{
							Bukkit.getServer().broadcastMessage(defaultPrefix + sender.getName() + " Has lifted the chat Lockdown");
						}
						return true;
					}else{
						sender.sendMessage(defaultPrefix + defaultPermissionsMsg);
						return true;
					}
				}
			}
		}
		return false;
	}
}

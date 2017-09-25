package smallutils.chatutils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

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
	public static List<String> broadcasts = null;
	public static long broadcastDelay = 0;
	public static String divider = null;
	public static BukkitTask broadcastTask = null;
	public static boolean randomizedBroadcasts = false;
	public static String broadcastLabel = null;
	public static Random rng = new Random();
	public static int index = -1;
	
	public boolean prepareConfiguration(){
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
		if (config.getBoolean("autoMessages")){
			broadcasts = config.getStringList("messageList");
			divider = colorize(config.getString("divider"));
			randomizedBroadcasts = config.getBoolean("randomizedBroadcasts");
			broadcastLabel = colorize(config.getString("broadcastLabel"));
			broadcastDelay = config.getLong("autoMessagesDelay");
			broadcastTask=Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable(){
				public void run(){
					if (randomizedBroadcasts){
						index = rng.nextInt(broadcasts.size());
					}else{
						if (index < broadcasts.size()-1){
							index++;
						}else{
							index=0;
						}
					}
					if (!divider.equals("")){
						getServer().broadcastMessage(divider);
					}
					getServer().broadcastMessage(broadcastLabel + colorize(broadcasts.get(index)));
					if (!divider.equals("")){
						getServer().broadcastMessage(divider);
					}
				}
			}, 20*broadcastDelay, 20*broadcastDelay);
		}
		return true;
	}
	
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
		prepareConfiguration();
	}
	
	public boolean reloadConfigCommand(){
		reloadConfig();
		broadcastTask.cancel();
		return prepareConfiguration();
	}
	
	@Override
	public void onDisable(){
		getLogger().info("Disabling ChatUtils");
		HandlerList.unregisterAll();
		broadcastTask.cancel();
		getLogger().info("ChatUtils Disabled");
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
				if (args[0].equalsIgnoreCase("reload")){
					if (sender.hasPermission("chatutils.reload")){
						reloadConfigCommand();
					}else{
						sender.sendMessage(defaultPrefix + defaultPermissionsMsg);
					}
					return true;
				}
				if (args[0].equalsIgnoreCase("cc")){
					if (sender.hasPermission("chatUtils.cc")){
						for (Player p: Bukkit.getOnlinePlayers()){
							if (!p.hasPermission("chatutils.bypasscc")){
								for (int i=0; i<config.getInt("linesCleared"); i++){
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
				if (args[0].equalsIgnoreCase("kw")||args[0].equalsIgnoreCase("keywords")){
					if (sender.hasPermission("chatUtils.keywords")){
						sender.sendMessage(defaultPrefix + "These are the keywords available:");
						for (Entry<String, String> item:replacers.entrySet()){
							sender.sendMessage(ChatColor.GOLD + item.getKey()+" -> " + item.getValue());
						}
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
	
	private static String colorize(String phrase) {
		/*
		 * Simple method to replace the & color codes with the respective colors.
		 */
		phrase = phrase.replaceAll("&0", ChatColor.BLACK + "");
		phrase = phrase.replaceAll("&1", ChatColor.DARK_BLUE + "");
		phrase = phrase.replaceAll("&2", ChatColor.DARK_GREEN + "");
		phrase = phrase.replaceAll("&3", ChatColor.DARK_AQUA + "");
		phrase = phrase.replaceAll("&4", ChatColor.DARK_RED + "");
		phrase = phrase.replaceAll("&5", ChatColor.DARK_PURPLE + "");
		phrase = phrase.replaceAll("&6", ChatColor.GOLD + "");
		phrase = phrase.replaceAll("&7", ChatColor.GRAY + "");
		phrase = phrase.replaceAll("&8", ChatColor.DARK_GRAY+ "");
		phrase = phrase.replaceAll("&9", ChatColor.BLUE + "");
		phrase = phrase.replaceAll("&a", ChatColor.GREEN + "");
		phrase = phrase.replaceAll("&b", ChatColor.AQUA + "");
		phrase = phrase.replaceAll("&c", ChatColor.RED + "");
		phrase = phrase.replaceAll("&d", ChatColor.LIGHT_PURPLE + "");
		phrase = phrase.replaceAll("&e", ChatColor.YELLOW + "");
		phrase = phrase.replaceAll("&f", ChatColor.WHITE + "");
		phrase = phrase.replaceAll("&k", ChatColor.MAGIC + "");
		phrase = phrase.replaceAll("&l", ChatColor.BOLD + "");
		phrase = phrase.replaceAll("&o", ChatColor.ITALIC + "");
		phrase = phrase.replaceAll("&n", ChatColor.UNDERLINE + "");
		phrase = phrase.replaceAll("&m", ChatColor.STRIKETHROUGH + "");
		phrase = phrase.replaceAll("&r", ChatColor.RESET + "");
		return phrase;
	}
}

package com.tashctf;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
	
	public RoryUtil roryUtilities = new RoryUtil();
	public FireworkEffectPlayer fplayer = new FireworkEffectPlayer();
	Main plugin = this;
	
	@Override
	public void onEnable(){
		getLogger().info("Capture The Item Ver0.01 Enabled");
		getServer().getPluginManager().registerEvents(this, this);
		roryUtilities.loadConfiguration(plugin);
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){

			@Override
			public void run() {
				int cakeDetected = 0;
				//Bukkit.getServer().broadcastMessage("scheduler running");
				for(Entity e: Bukkit.getWorld(plugin.getConfig().getString("data.world.gameworld")).getEntities()){
        			
					if(e.getType().equals(EntityType.PLAYER)){
        				for(ItemStack i: ((Player) e).getInventory().getContents()){
        					if(i != null){
        						if(i.getType().equals(Material.CAKE)){
            						cakeDetected++;
            						
            					}
        					}
        					
        				}
        			}
					if(e.getType().equals(EntityType.DROPPED_ITEM)){
        				if(((Item) e).getItemStack().getType().equals(Material.CAKE)){
        					cakeDetected++;
        				}
        			}
        			
        		}
				
				if(roryUtilities.glitchCatch(cakeDetected)){
					Bukkit.getServer().broadcastMessage("glitches eleminated, this is genuine, there are " + cakeDetected);	
				}
				Bukkit.getServer().broadcastMessage("cakes " + cakeDetected);
			}
			
		}, 40, 4);
	}
	
	@Override
	public void onDisable(){
		getLogger().info("Capture The Item Ver0.01 DISABLED");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		
		if (cmd.getName().equalsIgnoreCase("settp")) {
			if (args[0].equalsIgnoreCase("here")){
				roryUtilities.storeLocation(plugin, ((Entity) sender).getLocation(), args[1]);
			}
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("tpto")) {
			Location location = roryUtilities.readLocation(plugin, args[0]);
			((Entity) sender).teleport(location);
			return true;
		}
		
		
		return false; 
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event){
		String playerCountry = null;
		//int playerTimezone = (Integer) null;
		Player player = event.getPlayer();
		player.sendMessage(ChatColor.BLUE + plugin.getConfig().getString("data.text.welcome"));
		player.teleport(roryUtilities.readLocation(plugin, "joinspawn"));
		String playerIp = player.getAddress().getHostString();
		event.getPlayer().sendMessage(playerIp);
		if ((plugin.getConfig().getString("data.user." + player.getUniqueId().toString() + ".name") == null)){
			player.sendMessage(ChatColor.AQUA + "As this is your first time joining we are currently saving your data for later use.");
			roryUtilities.storeGeodata(player, plugin);
		}
		playerCountry = plugin.getConfig().getString("data.user." + player.getUniqueId().toString() + ".country");
		//playerTimezone = plugin.getConfig().getInt("data.user." + player.getUniqueId().toString() + ".timezone");
		String playerTime = RoryUtil.ipToTime(playerIp);
		event.getPlayer().sendMessage(ChatColor.BLUE + "We see you are from " + playerCountry + " and your timezone will be changed acordingly (" + playerTime + ").");
		try {
			fplayer.playFirework(player.getWorld(), player.getLocation(), FireworkUtil.getSpawnEffect());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@EventHandler
    public void onItemDespawn(ItemDespawnEvent event) {
        if (event.getEntityType() == EntityType.DROPPED_ITEM) {
        	if(event.getEntity().getItemStack().getType() == Material.CAKE){
        		int cakes = 0;
        		for(Entity e: event.getLocation().getWorld().getEntities()){
        			if(e.getType().equals(EntityType.DROPPED_ITEM)){
        				if(((Item) e).getItemStack().getType().equals(Material.CAKE)){
        					cakes++;
        				}
        			}
        		}
        		if(cakes == 1){
        			event.getEntity().setTicksLived(1);
                    event.setCancelled(true);
        		}
        		if(cakes >= 2){
					for(Entity entity: event.getLocation().getWorld().getEntities()){
	        			if(entity.getType().equals(EntityType.DROPPED_ITEM)){
	        				if(((Item) entity).getItemStack().getType().equals(Material.CAKE)){
	        					entity.remove();
	        				}
	        			}
					}
				}
        	}
        }
    }
	
	
}

package com.tashctf;

import java.io.BufferedReader;
import java.io.IOException; 
import java.io.InputStreamReader; 
import java.net.MalformedURLException; 
import java.net.URL; 
import java.net.URLConnection; 
import java.text.DateFormat; 
import java.text.SimpleDateFormat; 
import java.util.Calendar; 
import java.util.HashMap; 
import java.util.TimeZone; 

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject; 
import org.json.simple.JSONValue; 

public class RoryUtil {
	public void loadConfiguration(Main plugin){
	     plugin.getConfig().addDefault("data.text.welcome", "Welcome to Tash Gaming's capture the flag!");
	     plugin.getConfig().addDefault("data.location.joinspawn", "Location to spawn players when they join the server");
	     plugin.getConfig().addDefault("data.world.gameworld", "World");
	     plugin.getConfig().options().copyDefaults(true);
	     plugin.saveConfig();
	}
	public void storeLocation(Main plugin, Location loc, String path){
		plugin.getConfig().set("data.location." + path + ".world", loc.getWorld().getName());
		plugin.getConfig().set("data.location." + path + ".x", loc.getX());
		plugin.getConfig().set("data.location." + path + ".y", loc.getY());
		plugin.getConfig().set("data.location." + path + ".z", loc.getZ());
		plugin.getConfig().set("data.location." + path + ".pitch", loc.getPitch());
		plugin.getConfig().set("data.location." + path + ".yaw", loc.getYaw());
		plugin.saveConfig();
		plugin.reloadConfig();
	}
	
	public Location readLocation(Main plugin, String path){
		String world = plugin.getConfig().getString("data.location." + path + ".world");
		Double x = plugin.getConfig().getDouble("data.location." + path + ".x");
		Double y = plugin.getConfig().getDouble("data.location." + path + ".y");
		Double z = plugin.getConfig().getDouble("data.location." + path + ".z");
		double pitch1 = plugin.getConfig().getDouble("data.location." + path + ".pitch");
		double yaw1 = plugin.getConfig().getDouble("data.location." + path + ".yaw");
		Float pitch2 = new Float(pitch1);
		Float yaw2 = new Float(yaw1);
		Location location = new Location(plugin.getServer().getWorld(world), x, y, z, yaw2, pitch2);
		return location;
	}
	
	public void storeGeodata(Player player, Main plugin){
		String playerUUID = player.getUniqueId().toString();
		String playerName = player.getName();
		String playerIP = player.getAddress().getHostString();
		String playerCountry = getCountryName(playerIP);
		int playerTimezone = getTimezone(playerIP);
		plugin.getConfig().set("data.user." + playerUUID + ".name", playerName);
		plugin.getConfig().set("data.user." + playerUUID + ".uuid", playerUUID);
		plugin.getConfig().set("data.user." + playerUUID + ".ip", playerIP);
		plugin.getConfig().set("data.user." + playerUUID + ".country", playerCountry);
		plugin.getConfig().set("data.user." + playerUUID + ".timezone", playerTimezone);
		plugin.saveConfig();
		plugin.reloadConfig();
	}
	
	int previous = 10000;
	public boolean glitchCatch(int value){
		if(value == previous){
			previous = 10000;
			if(value > 1){
				return true;
			}
		}
		if(value != previous){
			previous = value;
			if(value > 1){
				return false;
			}
		}
		return false;
	}
	
	static HashMap<String,JSONObject> ipStorage = new HashMap<String,JSONObject>(); 
	public static String ipToTime(String ip){ 
		int offset = 0;
		if (ipStorage.containsKey(ip)){ 
			String timeZoneString = (String) ipStorage.get(ip).get("timeZone");
			if(timeZoneString != null && timeZoneString.length() > 3){
				offset = Integer.parseInt(timeZoneString.substring(0,timeZoneString.length()-3));
			}
		} 
		else { 
			String url = "http://api.ipinfodb.com/v3/ip-city/?key=d7859a91e5346872d0378a2674821fbd60bc07ed63684c3286c083198f024138&ip="+ip+"&format=json";
			JSONObject object = stringToJSON(getUrlSource(url));
			String timezone = (String) object.get("timeZone");
			if (timezone != null && timezone.length() > 3){
				offset = Integer.parseInt(timezone.substring(0,timezone.length()-3));
				ipStorage.put(ip,object);
			} else {
				return "Error: Cannot parse time";
			}
		}
		Calendar time = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		time.add(Calendar.HOUR_OF_DAY,offset);
		DateFormat formatter = new SimpleDateFormat("EEEEEE hh:mm");
		formatter.setCalendar(time);
		String date = formatter.format(time.getTime());
		DateFormat formatter2 = new SimpleDateFormat("aa");
		formatter2.setCalendar(time);
		date += formatter2.format(time.getTime()).toLowerCase();
		return date;
	}
	
	public static int getTimezone(String ip){
		int offset = 0;
		if (ipStorage.containsKey(ip)){ 
			String timeZoneString = (String) ipStorage.get(ip).get("timeZone");
			if(timeZoneString != null && timeZoneString.length() > 3){
				offset = Integer.parseInt(timeZoneString.substring(0,timeZoneString.length()-3));
			}
		} 
		else { 
			String url = "http://api.ipinfodb.com/v3/ip-city/?key=d7859a91e5346872d0378a2674821fbd60bc07ed63684c3286c083198f024138&ip="+ip+"&format=json";
			JSONObject object = stringToJSON(getUrlSource(url));
			String timezone = (String) object.get("timeZone");
			if (timezone != null && timezone.length() > 3){
				offset = Integer.parseInt(timezone.substring(0,timezone.length()-3));
				ipStorage.put(ip,object);
			} else {
				return 0;
			}
		}
		return offset;
	}
	
	public static String getCityName(String ip){
		JSONObject obj=null;
		if (ipStorage.containsKey(ip)){
			obj = ipStorage.get(ip);
		} else {
			String url = "http://api.ipinfodb.com/v3/ip-city/?key=d7859a91e5346872d0378a2674821fbd60bc07ed63684c3286c083198f024138&ip="+ip+"&format=json";
			JSONObject object = stringToJSON(getUrlSource(url));
			obj = object;
			ipStorage.put(ip,object);
		}
		return (String) obj.get("cityName");
	}
	public static String getStateName(String ip){
		JSONObject obj=null;
		if (ipStorage.containsKey(ip)){
			obj = ipStorage.get(ip);
		} else {
			String url = "http://api.ipinfodb.com/v3/ip-city/?key=d7859a91e5346872d0378a2674821fbd60bc07ed63684c3286c083198f024138&ip="+ip+"&format=json";
			JSONObject object = stringToJSON(getUrlSource(url));
			obj = object;
			ipStorage.put(ip,object);
		}
		return (String) obj.get("regionName");
	}
	public static String getCountryName(String ip){
		JSONObject obj=null;
		if (ipStorage.containsKey(ip)){
			obj = ipStorage.get(ip);
		} else {
			String url = "http://api.ipinfodb.com/v3/ip-city/?key=d7859a91e5346872d0378a2674821fbd60bc07ed63684c3286c083198f024138&ip="+ip+"&format=json";
			JSONObject object = stringToJSON(getUrlSource(url));
			obj = object;
			ipStorage.put(ip,object);
		}
		String country = (String) obj.get("countryName");
		if (country.contains(",")){
			country = country.split(",")[0];
		}
		return country;
	}
	public static String getCountryCode(String ip){
		JSONObject obj=null;
		if (ipStorage.containsKey(ip)){
			obj = ipStorage.get(ip);
		} else {
			String url = "http://api.ipinfodb.com/v3/ip-city/?key=d7859a91e5346872d0378a2674821fbd60bc07ed63684c3286c083198f024138&ip="+ip+"&format=json";
			JSONObject object = stringToJSON(getUrlSource(url));
			obj = object;
			ipStorage.put(ip,object);
		}
		String country = (String) obj.get("countryCode");
		return country;
	}
	public static JSONObject stringToJSON(String json){
		return (JSONObject) JSONValue.parse(json);
	}
	private static String getUrlSource(String url){
		URL url2 = null;
		try {
			url2 = new URL(url);
		} catch (MalformedURLException e) {
		}
		URLConnection yc = null;
		try {
			yc = url2.openConnection();
		} catch (IOException e) {
		}
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(
			yc.getInputStream(), "UTF-8"));
		} catch (IOException e) {
		}
		String inputLine;
		StringBuilder a = new StringBuilder();
		try {
			while ((inputLine = in.readLine()) != null)
			a.append(inputLine);
		} catch (IOException e) {
		}
		try {
			in.close();
		} catch (IOException e) {
		}
		return a.toString();
	}
}

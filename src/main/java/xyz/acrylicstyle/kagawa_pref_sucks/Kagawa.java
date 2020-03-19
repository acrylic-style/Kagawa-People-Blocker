package xyz.acrylicstyle.kagawa_pref_sucks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONObject;
import util.CollectionList;
import util.JSONAPI;

import java.util.*;

public class Kagawa extends JavaPlugin implements Listener {
    public static String api_key = null;
    public static CollectionList<UUID> bannedPlayers = new CollectionList<>();

    @Override
    public void onEnable() {
        api_key = this.getConfig().getString("api_key");
        Bukkit.getPluginManager().registerEvents(this, this);
        Calendar date = new GregorianCalendar();
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        new Timer().schedule(new TimerTask() { @Override public void run() { bannedPlayers.clear(); } }, date.getTimeInMillis()-System.currentTimeMillis());
    }

    @EventHandler
    public void onAsyncPlayerPreLogin(final AsyncPlayerPreLoginEvent e) {
        if (bannedPlayers.contains(e.getUniqueId())) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "1時間経過したため今日はもう参加できません。");
            return;
        }
        if (api_key != null) {
            JSONObject response = new JSONAPI("http://api.ipstack.com/" + e.getAddress().getHostAddress() + "?access_key=" + api_key).call().getResponse();
            if (response.getString("country_code").equals("JP")) {
                if (response.getString("region_name").equals("Kagawa")) {
                    new BukkitRunnable() {
                        public void run() {
                            if (Bukkit.getPlayer(e.getUniqueId()).isOnline())
                                Bukkit.getPlayer(e.getUniqueId()).kickPlayer(ChatColor.RED + "1時間経過したため今日はもう遊べません。");
                            bannedPlayers.add(e.getUniqueId());
                        }
                    }.runTaskLater(this, 20*60*60); // 1 hour
                }
            }
        }
    }
}

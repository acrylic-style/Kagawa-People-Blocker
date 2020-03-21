package xyz.acrylicstyle.kagawa_pref_sucks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONObject;
import util.JSONAPI;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Kagawa extends JavaPlugin implements Listener {

    private static final String BANNED_MESSAGE = ChatColor.RED + "1時間経過したため今日はもう遊べません。";

    private String apiKey = null;

    private BukkitRunnable task = null;
    private int today = LocalDateTime.now().getDayOfYear(); // 1 - 366
    private final Set<UUID> bannedPlayers = ConcurrentHashMap.newKeySet();
    private final Map<UUID, Long> onlineKagawaPlayers = new ConcurrentHashMap<>();

    @Override
    public void onEnable() {
        this.apiKey = this.getConfig().getString("api_key");

        if (this.task != null) this.task.cancel();

        // Ticks per 1 sec
        this.task = new BukkitRunnable() {
            @Override
            public void run() {
                final long now = System.currentTimeMillis();
                onlineKagawaPlayers.entrySet().stream()
                        .filter(entry -> now - entry.getValue() > 60 * 60 * 1000)
                        .map(entry -> Bukkit.getPlayer(entry.getKey())) // Never null, because offline player's id will be removed when player quit.
                        .forEach(p -> {
                            p.kickPlayer(BANNED_MESSAGE);
                            bannedPlayers.add(p.getUniqueId());
                        });

                final int newDate = LocalDateTime.now().getDayOfYear();

                if (today != newDate) { // If date changed
                    bannedPlayers.clear();
                }

                today = newDate;
            }
        };
        this.task.runTaskTimer(this, 0, 20);

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }
    }

    @EventHandler
    public void onAsyncPlayerPreLogin(final AsyncPlayerPreLoginEvent e) {
        final UUID id = e.getUniqueId();
        if (this.bannedPlayers.contains(id)) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, BANNED_MESSAGE);
            return;
        }

        if (this.apiKey == null) return;

        final JSONObject response = new JSONAPI("http://api.ipstack.com/" + e.getAddress().getHostAddress() + "?access_key=" + this.apiKey).call().getResponse();

        if (response.getString("country_code").equals("JP")
                && response.getString("region_name").equals("Kagawa")) {
            this.onlineKagawaPlayers.put(id, System.currentTimeMillis());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onKick(final PlayerKickEvent e) {
        this.onlineKagawaPlayers.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent e) {
        this.onlineKagawaPlayers.remove(e.getPlayer().getUniqueId());
    }
}

package com.cavetale.sidebar;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public final class Sessions implements Listener {
    private final SidebarPlugin plugin;
    private final Map<UUID, Session> sessions = new HashMap<>();

    protected void enable() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        for (Player player : Bukkit.getOnlinePlayers()) {
            enter(player);
        }
        Bukkit.getScheduler().runTaskTimer(plugin, this::tick, 0L, 1L);
    }

    protected void disable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            exit(player);
        }
    }

    public Session get(Player player) {
        return sessions.get(player.getUniqueId());
    }

    private void enter(Player player) {
        Session session = new Session(plugin, player);
        session.enable(player);
        sessions.put(session.uuid, session);
    }

    private void exit(Player player) {
        Session session = sessions.remove(player);
        if (session != null) {
            session.disable(player);
        }
    }

    private void tick() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Session session = get(player);
            if (session != null) session.tick(player);
        }
    }

    @EventHandler
    void onPlayerJoin(PlayerJoinEvent event) {
        enter(event.getPlayer());
    }

    @EventHandler
    void onPlayerQuit(PlayerQuitEvent event) {
        exit(event.getPlayer());
    }
}

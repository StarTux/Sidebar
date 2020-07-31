package com.cavetale.sidebar;

import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
final class EventListener implements Listener {
    private final SidebarPlugin plugin;

    void register() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    void onPlayerJoin(PlayerJoinEvent event) {
        plugin.sessions.enter(event.getPlayer());
    }

    @EventHandler
    void onPlayerQuit(PlayerQuitEvent event) {
        plugin.sessions.exit(event.getPlayer());
    }
}

package com.cavetale.sidebar;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class SidebarPlugin extends JavaPlugin {
    final EventListener eventListener = new EventListener(this);
    final Sessions sessions = new Sessions(this);

    @Override
    public void onEnable() {
        eventListener.register();
        getServer().getScheduler().runTaskTimer(this, this::onTick, 0, 1);
        new SidebarCommand(this).enable();
        for (Player player : getServer().getOnlinePlayers()) {
            sessions.enter(player);
        }
    }

    @Override
    public void onDisable() {
        for (Player player : getServer().getOnlinePlayers()) {
            sessions.exit(player);
        }
    }

    void onTick() {
        for (Player player : getServer().getOnlinePlayers()) {
            sessions.tick(player);
        }
    }

    public boolean hasPermission(Player player) {
        return player.hasPermission("sidebar.sidebar");
    }
}

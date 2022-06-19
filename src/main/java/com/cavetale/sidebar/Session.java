package com.cavetale.sidebar;

import com.cavetale.core.event.hud.PlayerHudEntry;
import com.cavetale.core.event.hud.PlayerHudEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public final class Session {
    private final SidebarPlugin plugin;
    protected final UUID uuid;
    private final Sidebar sidebar = new Sidebar();
    private final PlayerListHeader header = new PlayerListHeader();
    private final PlayerListFooter footer = new PlayerListFooter();
    private final PlayerBossBar bossbar = new PlayerBossBar();

    protected Session(final SidebarPlugin plugin, final Player player) {
        this.plugin = plugin;
        this.uuid = player.getUniqueId();
    }

    protected void enable(Player player) {
        sidebar.enable(player);
        header.enable(player);
        footer.enable(player);
        bossbar.enable(player);
    }

    protected void disable(Player player) {
        sidebar.disable(player);
        header.disable(player);
        footer.disable(player);
        bossbar.disable(player);
    }

    protected void tick(Player player) {
        PlayerHudEvent playerHudEvent = new PlayerHudEvent(player);
        playerHudEvent.callEvent();
        if (sidebar.isVisible()) {
            List<PlayerHudEntry> entries = new ArrayList<>();
            // Call legacy event
            PlayerSidebarEvent playerSidebarEvent = new PlayerSidebarEvent(player);
            playerSidebarEvent.callEvent();
            entries.addAll(playerSidebarEvent.entries);
            entries.addAll(playerHudEvent.getSidebar());
            sidebar.loadEntries(entries);
        }
        header.loadEntries(player, playerHudEvent.getHeader());
        footer.loadEntries(player, playerHudEvent.getFooter());
        bossbar.loadEntries(player, playerHudEvent.getBossbar());
    }
}

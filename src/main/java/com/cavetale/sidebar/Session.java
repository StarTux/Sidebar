package com.cavetale.sidebar;

import com.cavetale.core.event.hud.PlayerHudEntry;
import com.cavetale.core.event.hud.PlayerHudEvent;
import com.cavetale.core.event.hud.PlayerHudPriority;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

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
        List<PlayerHudEntry> headerList = new ArrayList<>();
        headerList.addAll(playerHudEvent.getHeader());
        if (!plugin.sessions.serverTimeComponent.isEmpty()) {
            headerList.add(new PlayerHudEntry(PlayerHudPriority.LOWEST.value, plugin.sessions.serverTimeComponent));
        }
        header.loadEntries(player, headerList);
        List<PlayerHudEntry> footerList = new ArrayList<>();
        footerList.addAll(playerHudEvent.getFooter());
        if (!player.getWorld().getGameRuleValue(GameRule.REDUCED_DEBUG_INFO)) {
            Location loc = player.getLocation();
            final int x = loc.getBlockX();
            final int y = loc.getBlockY();
            final int z = loc.getBlockZ();
            final float yaw = loc.getYaw();
            final String facing;
            if (yaw < -157.5f) {
                facing = "N";
            } else if (yaw < -112.5f) {
                facing = "NE";
            } else if (yaw < -67.5f) {
                facing = "E";
            } else if (yaw < -22.5f) {
                facing = "SE";
            } else if (yaw < 22.5f) {
                facing = "S";
            } else if (yaw < 67.5f) {
                facing = "SW";
            } else if (yaw < 112.5f) {
                facing = "W";
            } else if (yaw < 157.5f) {
                facing = "NW";
            } else {
                facing = "N";
            }
            footerList.add(new PlayerHudEntry(PlayerHudPriority.DEFAULT.value,
                                              List.of(text(x + " " + y + " " + z + " " + facing, GRAY))));
        }
        footer.loadEntries(player, footerList);
        bossbar.loadEntries(player, playerHudEvent.getBossbar());
    }
}

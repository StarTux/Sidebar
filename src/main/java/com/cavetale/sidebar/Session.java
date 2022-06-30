package com.cavetale.sidebar;

import com.cavetale.core.event.hud.PlayerHudEntry;
import com.cavetale.core.event.hud.PlayerHudEvent;
import com.cavetale.core.event.hud.PlayerHudPriority;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");

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
        headerList.add(new PlayerHudEntry(PlayerHudPriority.LOWEST.value,
                                          List.of(text(TIME_FORMAT.format(new Date()), GRAY))));
        header.loadEntries(player, headerList);
        List<PlayerHudEntry> footerList = new ArrayList<>();
        footerList.addAll(playerHudEvent.getFooter());
        if (!player.getWorld().getGameRuleValue(GameRule.REDUCED_DEBUG_INFO)) {
            Location loc = player.getLocation();
            final int x = loc.getBlockX();
            final int y = loc.getBlockY();
            final int z = loc.getBlockZ();
            footerList.add(new PlayerHudEntry(PlayerHudPriority.DEFAULT.value,
                                              List.of(text(x + " " + y + " " + z, GRAY))));
        }
        footer.loadEntries(player, footerList);
        bossbar.loadEntries(player, playerHudEvent.getBossbar());
    }
}

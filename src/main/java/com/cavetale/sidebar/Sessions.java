package com.cavetale.sidebar;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class Sessions {
    private final SidebarPlugin plugin;
    private final Map<UUID, Sidebar> sidebars = new HashMap<>();

    public Sidebar get(Player player) {
        return sidebars.get(player.getUniqueId());
    }

    /**
     * Get or create.
     */
    public Sidebar of(Player player) {
        return sidebars.computeIfAbsent(player.getUniqueId(), u -> {
                Sidebar sidebar = new Sidebar(plugin, player);
                sidebar.open(player);
                return sidebar;
            });
    }

    void clear(Player player) {
        sidebars.remove(player.getUniqueId());
    }

    /**
     * Player joins or is online while plugin loads.
     */
    void enter(Player player) {
        if (!plugin.hasPermission(player)) return;
        Sidebar sidebar = get(player);
        if (sidebar != null) {
            sidebar.close(player);
        }
        sidebar = of(player);
    }

    /**
     * Player quits or is online while plugin unloads.
     */
    void exit(Player player) {
        Sidebar sidebar = get(player);
        if (sidebar != null) {
            sidebar.close(player);
            clear(player);
        }
    }

    void tick(Player player) {
        Sidebar sidebar = get(player);
        if (sidebar == null) {
            if (!plugin.hasPermission(player)) return;
            sidebar = of(player);
        } else if (!plugin.hasPermission(player)) {
            sidebar.close(player);
            clear(player);
            return;
        }
        if (!sidebar.canSee()) return;
        PlayerSidebarEvent event = new PlayerSidebarEvent(plugin, player);
        plugin.getServer().getPluginManager().callEvent(event);
        List<Entry> entries = event.entries;
        if (entries.isEmpty()) {
            sidebar.reset();
            sidebar.update();
            return;
        }
        Collections.sort(entries);
        int lineCount = entries.stream().mapToInt(Entry::getLineCount).sum();
        boolean doInsertBreaks = lineCount + entries.size() - 1 <= 15;
        sidebar.clear();
        for (int i = 0; i < entries.size(); i += 1) {
            Entry entry = entries.get(i);
            if (doInsertBreaks && i > 0) {
                sidebar.newLine(Component.empty());
            }
            for (Component line : entry.lines) {
                sidebar.newLine(line);
            }
        }
        sidebar.update();
    }
}

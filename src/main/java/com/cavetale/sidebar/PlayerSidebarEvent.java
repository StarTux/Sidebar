package com.cavetale.sidebar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

@RequiredArgsConstructor
public final class PlayerSidebarEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    final List<Entry> entries = new ArrayList<>();
    @Getter final SidebarPlugin sidebarPlugin;
    @Getter final Player player;

    public void addLines(@NonNull JavaPlugin plugin, @NonNull Priority priority, @NonNull List<String> strings) {
        if (strings.isEmpty()) {
            throw new IllegalArgumentException("strings is empty");
        }
        List<Component> lines = new ArrayList<>(strings.size());
        for (String string : strings) {
            lines.add(Component.text(string));
        }
        entries.add(new Entry(plugin, priority, lines));
    }

    public void addLines(@NonNull JavaPlugin plugin, @NonNull Priority priority, @NonNull String... lines) {
        addLines(plugin, priority, Arrays.asList(lines));
    }

    public void add(@NonNull JavaPlugin plugin, @NonNull Priority priority, @NonNull List<Component> lines) {
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("lines is empty");
        }
        entries.add(new Entry(plugin, priority, lines));
    }

    public void add(@NonNull JavaPlugin plugin, @NonNull Priority priority, @NonNull Component... lines) {
        add(plugin, priority, Arrays.asList(lines));
    }

    public Sidebar getSidebar() {
        return sidebarPlugin.sessions.get(player);
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}

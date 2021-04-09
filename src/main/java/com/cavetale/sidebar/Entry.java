package com.cavetale.sidebar;

import java.util.List;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.plugin.java.JavaPlugin;

@RequiredArgsConstructor
public final class Entry implements Comparable<Entry> {
    final JavaPlugin plugin;
    final Priority priority;
    final List<Component> lines;

    @Override
    public int compareTo(Entry other) {
        int v = priority.compareTo(other.priority);
        if (v != 0) return v;
        return Integer.compare(plugin.hashCode(), other.plugin.hashCode());
    }

    public int getLineCount() {
        return lines.size();
    }
}

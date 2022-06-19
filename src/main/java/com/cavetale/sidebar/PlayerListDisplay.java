package com.cavetale.sidebar;

import com.cavetale.core.event.hud.PlayerHudEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.join;
import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.JoinConfiguration.separator;

abstract class PlayerListDisplay {
    private Component display = empty();

    protected void enable(Player player) { }

    protected void disable(Player player) {
        updateDisplay(player, empty());
    }

    protected void loadEntries(Player player, List<PlayerHudEntry> entries) {
        if (entries.isEmpty()) {
            updateDisplay(player, empty());
            return;
        }
        Collections.sort(entries);
        List<Component> lines = new ArrayList<>();
        for (PlayerHudEntry entry : entries) {
            lines.addAll(entry.getLines());
        }
        updateDisplay(player, join(separator(newline()), lines));
    }

    private void updateDisplay(Player player, Component newDisplay) {
        if (display.equals(newDisplay)) return;
        display = newDisplay;
        send(player, display);
    }

    protected abstract void send(Player player, Component message);
}

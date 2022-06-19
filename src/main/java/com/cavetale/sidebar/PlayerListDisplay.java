package com.cavetale.sidebar;

import com.cavetale.core.event.hud.PlayerHudEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.join;
import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.JoinConfiguration.separator;

@Getter
abstract class PlayerListDisplay {
    private List<Component> lines = new ArrayList<>();
    private Component display = empty();

    protected void enable(Player player) { }

    protected void disable(Player player) {
        updateDisplay(player, List.of());
    }

    protected void loadEntries(Player player, List<PlayerHudEntry> entries) {
        if (entries.isEmpty()) {
            updateDisplay(player, List.of());
            return;
        }
        Collections.sort(entries);
        List<Component> newLines = new ArrayList<>();
        for (PlayerHudEntry entry : entries) {
            newLines.addAll(entry.getLines());
        }
        updateDisplay(player, newLines);
    }

    private void updateDisplay(Player player, List<Component> newLines) {
        this.lines = newLines;
        Component newDisplay = join(separator(newline()), lines);
        if (display.equals(newDisplay)) return;
        display = newDisplay;
        send(player, display);
    }

    protected abstract void send(Player player, Component message);
}

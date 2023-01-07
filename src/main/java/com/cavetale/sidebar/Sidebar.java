package com.cavetale.sidebar;

import com.cavetale.core.event.hud.PlayerHudEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import static com.cavetale.core.font.Unicode.tiny;
import static java.awt.Color.HSBtoRGB;
import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.TextColor.color;

/**
 * The actual sidebar for one player.
 */
@Getter
public final class Sidebar {
    protected Scoreboard scoreboard;
    protected Objective objective;
    private final List<SidebarLine> lines = new ArrayList<>();
    private int cursor = 0;
    private boolean visible = true;
    private int ticks;
    private int colorIndex;

    protected void enable(Player player) {
        this.scoreboard = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
        this.objective = scoreboard.registerNewObjective("sidebar", "dummy", empty(), RenderType.INTEGER);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        Team team = scoreboard.registerNewTeam("Sidebar");
        team.addEntry(player.getName());
        if (!player.getScoreboard().equals(scoreboard)) {
            player.setScoreboard(scoreboard);
        }
    }

    protected void disable(Player player) {
        if (player.getScoreboard().equals(scoreboard)) {
            player.setScoreboard(Bukkit.getServer().getScoreboardManager().getMainScoreboard());
        }
    }

    public void setCursor(final int newCursor) {
        if (newCursor < 0 || newCursor > lines.size()) {
            throw new IllegalStateException("Sidebar: setCursor: " + newCursor
                                            + "/" + lines.size());
        }
        cursor = newCursor;
    }

    public void newLine(@NonNull Component display) {
        if (cursor < 0 || cursor > lines.size()) {
            throw new IllegalStateException("Sidebar cursor: " + cursor
                                            + "/" + lines.size());
        }
        SidebarLine line;
        if (cursor == lines.size()) {
            if (lines.size() >= 15) {
                return;
            }
            line = new SidebarLine(this, lines.size());
            lines.add(line);
            line.enable();
        } else {
            line = lines.get(cursor);
        }
        cursor += 1;
        line.now = display;
    }

    public void setLine(final int lineNumber, @NonNull Component display) {
        if (lineNumber < 0 || lineNumber >= lines.size()) {
            return;
        }
        lines.get(lineNumber).now = display;
    }

    public int countLines() {
        return lines.size();
    }

    public void setTitle(@NonNull Component component) {
        objective.displayName(component);
    }

    /**
     * Clear all lines.
     */
    public void clear() {
        for (SidebarLine line : lines) {
            line.now = empty();
        }
        cursor = 0;
    }

    public void reset() {
        for (SidebarLine line : lines) {
            line.disable();
        }
        lines.clear();
        cursor = 0;
    }

    public void show() {
        visible = true;
    }

    public void hide() {
        visible = false;
        reset();
    }

    /**
     * Update all visible lines.
     */
    public void update() {
        if (lines.isEmpty()) return;
        int index = lines.size() - 1;
        if (lines.get(index).isEmpty()) {
            lines.get(index).disable();
            lines.remove(index);
            if (cursor == index) {
                cursor -= 1;
            }
        }
        for (SidebarLine line : lines) {
            line.update();
        }
        if (ticks % 10 == 0) {
            colorIndex += 1;
            if (colorIndex >= 256) colorIndex = 0;
            final float hue = (float) colorIndex / 256.0f;
            final int rgb = 0xFFFFFF & HSBtoRGB(hue, 1.0f, 1.0f);
            setTitle(text(tiny("sidebar"), color(rgb)));
        }
        ticks += 1;
    }

    /**
     * Accept a new set of entries.
     */
    protected void loadEntries(List<PlayerHudEntry> entries) {
        if (entries.isEmpty()) {
            reset();
            return;
        }
        Collections.sort(entries);
        int lineCount = 0;
        for (PlayerHudEntry entry : entries) {
            lineCount += entry.getLineCount();
        }
        clear();
        for (int i = 0; i < entries.size(); i += 1) {
            PlayerHudEntry entry = entries.get(i);
            if (i > 0 && i < entries.size() - 1) {
                newLine(empty());
            }
            for (Component line : entry.getLines()) {
                newLine(line);
            }
        }
        update();
    }
}

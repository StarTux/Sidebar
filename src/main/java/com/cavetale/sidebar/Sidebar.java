package com.cavetale.sidebar;

import com.cavetale.core.event.hud.PlayerHudEntry;
import com.cavetale.core.font.DefaultFont;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import static net.kyori.adventure.text.Component.empty;

/**
 * The actual sidebar for one player.
 */
@Getter
public final class Sidebar {
    public static final int MAX_LINES = 15;
    protected Scoreboard scoreboard;
    protected Objective objective;
    private final List<SidebarLine> lines = new ArrayList<>();
    private int cursor = 0;
    private boolean visible = true;
    private int ticks;
    private int colorIndex;

    protected void enable(Player player) {
        this.scoreboard = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
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
        removeObjective();
    }

    /**
     * Accept a new set of entries.
     */
    protected void update(List<PlayerHudEntry> entries) {
        if (entries.isEmpty()) {
            removeObjective();
            reset();
            return;
        }
        if (objective == null) {
            objective = scoreboard.registerNewObjective("sidebar", Criteria.DUMMY, DefaultFont.CAVETALE.asComponent(), RenderType.INTEGER);
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }
        assert objective != null;
        Collections.sort(entries);
        int lineCount = 0;
        for (PlayerHudEntry entry : entries) {
            lineCount += entry.getLineCount();
        }
        clear();
        for (int i = 0; i < entries.size(); i += 1) {
            PlayerHudEntry entry = entries.get(i);
            if (i > 0) newLine(empty());
            for (Component line : entry.getLines()) {
                newLine(line);
            }
        }
        // Update all visible lines
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
        ticks += 1;
    }

    private void removeObjective() {
        if (objective == null) return;
        objective.unregister();
        objective = null;
    }
}

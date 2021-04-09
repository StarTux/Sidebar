package com.cavetale.sidebar;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

/**
 * The actual sidebar for one player.
 */
@Getter
public final class Sidebar {
    static final String CHARS = "0123456789abcdef";
    private final SidebarPlugin plugin;
    private final Scoreboard scoreboard;
    private final Objective objective;
    private final List<Line> lines;
    private int cursor = 0;
    private boolean warned = false;
    private final UUID playerUuid;
    private final String playerName;
    private boolean visible = true;
    @Setter private boolean debug = false;
    private int ticks;
    private int colorIndex;

    public Sidebar(@NonNull final SidebarPlugin plugin, final Player player) {
        this.plugin = plugin;
        this.playerUuid = player.getUniqueId();
        this.playerName = player.getName();
        scoreboard = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
        objective = scoreboard.registerNewObjective("sidebar", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        lines = new ArrayList<>();
        Team team = scoreboard.registerNewTeam("sidebar");
        team.addPlayer(player);
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(playerUuid);
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
        Line line;
        if (cursor == lines.size()) {
            if (lines.size() >= 15) {
                if (!warned) {
                    warned = true;
                    plugin.getLogger().warning(playerName + ": Sidebar line count exceeds 15!");
                }
                return;
            }
            line = new Line(cursor);
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
            plugin.getLogger().warning("Sidebar setLine index out of bounds:"
                                       + lineNumber + "/" + lines.size());
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

    public void clear() {
        for (Line line : lines) {
            line.now = Component.empty();
        }
        cursor = 0;
    }

    public void reset() {
        for (Line line : lines) {
            line.disable();
        }
        lines.clear();
        cursor = 0;
    }

    public void open(@NonNull Player player) {
        if (!player.getScoreboard().equals(scoreboard)) {
            player.setScoreboard(scoreboard);
        }
    }

    public void close(@NonNull Player player) {
        if (player.getScoreboard().equals(scoreboard)) {
            player.setScoreboard(Bukkit.getServer().getScoreboardManager()
                                 .getMainScoreboard());
        }
    }

    public boolean canSee() {
        return visible;
    }

    public void show() {
        visible = true;
    }

    public void hide() {
        visible = false;
        reset();
    }

    public void update() {
        if (!visible || lines.isEmpty()) return;
        int index = lines.size() - 1;
        if (lines.get(index).isEmpty()) {
            lines.get(index).disable();
            lines.remove(index);
            if (cursor == index) {
                cursor -= 1;
            }
        }
        for (Line line : lines) {
            line.update();
        }
        if (ticks % 10 == 0) {
            colorIndex += 1;
            if (colorIndex >= 256) colorIndex = 0;
            int rgb = 0xFFFFFF & Color.HSBtoRGB(((float) colorIndex) / 256.0f, 1.0f, 1.0f);
            setTitle(Component.text("/sidebar").color(TextColor.color(rgb)));
        }
        ticks += 1;
    }

    @RequiredArgsConstructor @Getter
    public final class Line {
        final int index;
        String name;
        Team team = null;
        Component old;
        Component now;

        void enable() {
            name = "" + ChatColor.COLOR_CHAR + CHARS.charAt(index);
            team = scoreboard.getTeam(name);
            objective.getScore(name).setScore(1);
            if (team == null) {
                team = scoreboard.registerNewTeam(name);
            }
            team.addEntry(name);
            now = Component.empty();
            team.prefix(now);
            old = now;
        }

        void disable() {
            team.unregister();
            team = null;
            scoreboard.resetScores(name);
        }

        boolean isEmpty() {
            return Component.empty().equals(now);
        }

        void update() {
            if (old.equals(now)) return;
            if (debug) {
                getPlayer().sendMessage(Component.text("Sidebar update line #" + index + ": ").append(now));
            }
            old = now;
            team.prefix(now);
        }
    }
}

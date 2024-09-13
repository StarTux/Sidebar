package com.cavetale.sidebar;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Team;
import static io.papermc.paper.scoreboard.numbers.NumberFormat.blank;

@Getter
@RequiredArgsConstructor
public final class SidebarLine {
    public static final String COLOR_CHAR = "\u00A7";
    private static final String CHARS = "0123456789abcdef";
    protected final Sidebar sidebar;
    protected final int index;
    protected String name;
    protected Team team = null;
    protected Component old;
    protected Component now;

    public void enable() {
        if (index >= CHARS.length()) return;
        name = index < CHARS.length()
            ? "" + COLOR_CHAR + CHARS.charAt(index)
            : "";
        team = sidebar.scoreboard.getTeam(name);
        final Score score = sidebar.objective.getScore(name);
        score.setScore(1);
        score.numberFormat(blank());
        if (team == null) {
            team = sidebar.scoreboard.registerNewTeam(name);
        }
        team.addEntry(name);
        now = Component.empty();
        team.prefix(now);
        old = now;
    }

    public void disable() {
        if (index >= CHARS.length()) return;
        team.unregister();
        team = null;
        sidebar.scoreboard.resetScores(name);
    }

    public boolean isEmpty() {
        return Component.empty().equals(now);
    }

    public void update() {
        if (index >= CHARS.length()) return;
        if (old.equals(now)) return;
        old = now;
        team.prefix(now);
    }
}

package com.cavetale.sidebar;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Team;

@RequiredArgsConstructor @Getter
public final class SidebarLine {
    private static final String CHARS = "0123456789abcdef";
    protected final Sidebar sidebar;
    protected final int index;
    protected String name;
    protected Team team = null;
    protected Component old;
    protected Component now;

    public void enable() {
        name = "" + ChatColor.COLOR_CHAR + CHARS.charAt(index);
        team = sidebar.scoreboard.getTeam(name);
        sidebar.objective.getScore(name).setScore(1);
        if (team == null) {
            team = sidebar.scoreboard.registerNewTeam(name);
        }
        team.addEntry(name);
        now = Component.empty();
        team.prefix(now);
        old = now;
    }

    public void disable() {
        team.unregister();
        team = null;
        sidebar.scoreboard.resetScores(name);
    }

    public boolean isEmpty() {
        return Component.empty().equals(now);
    }

    public void update() {
        if (old.equals(now)) return;
        old = now;
        team.prefix(now);
    }
}

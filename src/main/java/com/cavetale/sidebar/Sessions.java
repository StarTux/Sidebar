package com.cavetale.sidebar;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import static com.cavetale.core.font.Unicode.tiny;
import static com.cavetale.core.util.CamelCase.toCamelCase;
import static net.kyori.adventure.text.Component.join;
import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.JoinConfiguration.noSeparators;
import static net.kyori.adventure.text.format.NamedTextColor.*;

@RequiredArgsConstructor
public final class Sessions implements Listener {
    private final SidebarPlugin plugin;
    private final Map<UUID, Session> sessions = new HashMap<>();
    protected List<Component> serverTimeComponent = List.of();

    protected void enable() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        for (Player player : Bukkit.getOnlinePlayers()) {
            enter(player);
        }
        Bukkit.getScheduler().runTaskTimer(plugin, this::tick, 0L, 1L);
    }

    protected void disable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            exit(player);
        }
    }

    public Session get(Player player) {
        return sessions.get(player.getUniqueId());
    }

    private void enter(Player player) {
        Session session = new Session(plugin, player);
        session.enable(player);
        sessions.put(session.uuid, session);
    }

    private void exit(Player player) {
        Session session = sessions.remove(player);
        if (session != null) {
            session.disable(player);
        }
    }

    private static String padZeros(int in) {
        return in < 10 ? "0" + in : "" + in;
    }

    private void tick() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Session session = get(player);
            if (session != null) session.tick(player);
        }
        LocalDateTime date = LocalDateTime.now();
        serverTimeComponent = List.of(join(noSeparators(),
                                           text(tiny("time "), GRAY),
                                           text(padZeros(date.getHour())),
                                           text(":", GRAY),
                                           text(padZeros(date.getMinute())),
                                           text(":", GRAY),
                                           text(padZeros(date.getSecond())),
                                           space(),
                                           text(toCamelCase(" ", date.getMonth()) + " " + date.getDayOfMonth(), GRAY)));
    }

    @EventHandler
    void onPlayerJoin(PlayerJoinEvent event) {
        enter(event.getPlayer());
    }

    @EventHandler
    void onPlayerQuit(PlayerQuitEvent event) {
        exit(event.getPlayer());
    }
}

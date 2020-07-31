package com.cavetale.sidebar;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class SidebarCommand implements TabExecutor {
    private final SidebarPlugin plugin;

    void enable() {
        plugin.getCommand("sidebar").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Player expected");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 0) return false;
        return onCommand(player, args[0], Arrays.copyOfRange(args, 1, args.length));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 0) return null;
        if (args.length == 1) {
            return Stream.of("toggle", "on", "off")
                .filter(a -> a.startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    boolean onCommand(Player player, String cmd, String[] args) {
        switch (cmd) {
        case "toggle": {
            if (args.length > 0) return false;
            Sidebar sidebar = plugin.sessions.of(player);
            if (!sidebar.canSee(player)) {
                sidebar.open(player);
                player.sendMessage(ChatColor.GREEN + "Sidebar enabled.");
            } else {
                sidebar.close(player);
                player.sendMessage(ChatColor.RED + "Sidebar disabled.");
            }
            return true;
        }
        case "on": {
            if (args.length > 0) return false;
            Sidebar sidebar = plugin.sessions.of(player);
            if (sidebar.canSee(player)) {
                sidebar.open(player);
                player.sendMessage(ChatColor.GREEN + "Sidebar enabled.");
            } else {
                player.sendMessage(ChatColor.GREEN + "Already enabled.");
            }
            return true;
        }
        case "off": {
            if (args.length > 0) return false;
            Sidebar sidebar = plugin.sessions.of(player);
            if (sidebar.canSee(player)) {
                sidebar.close(player);
                player.sendMessage(ChatColor.RED + "Sidebar disabled.");
            } else {
                player.sendMessage(ChatColor.RED + "Already disabled.");
            }
            return true;
        }
        default: return false;
        }
    }
}

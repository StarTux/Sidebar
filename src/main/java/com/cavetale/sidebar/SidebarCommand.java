package com.cavetale.sidebar;

import com.cavetale.core.command.CommandNode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class SidebarCommand implements TabExecutor {
    private final SidebarPlugin plugin;
    private CommandNode rootNode;

    void enable() {
        rootNode = new CommandNode("sidebar");
        rootNode.addChild("toggle").denyTabCompletion()
            .description("Toggle sidebar")
            .playerCaller(this::toggle);
        rootNode.addChild("on").denyTabCompletion()
            .description("Enable sidebar")
            .playerCaller(this::on);
        rootNode.addChild("off").denyTabCompletion()
            .description("Disable sidebar")
            .playerCaller(this::off);
        plugin.getCommand("sidebar").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        return rootNode.call(sender, command, alias, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return rootNode.complete(sender, command, alias, args);
    }

    boolean toggle(Player player, String[] args) {
        if (args.length != 0) return false;
        Sidebar sidebar = plugin.sessions.of(player);
        if (!sidebar.canSee()) {
            sidebar.show();
            player.sendMessage(ChatColor.GREEN + "Sidebar enabled.");
        } else {
            sidebar.hide();
            player.sendMessage(ChatColor.RED + "Sidebar disabled.");
        }
        return true;
    }

    boolean on(Player player, String[] args) {
        if (args.length != 0) return false;
        Sidebar sidebar = plugin.sessions.of(player);
        if (!sidebar.canSee()) {
            sidebar.show();
            player.sendMessage(ChatColor.GREEN + "Sidebar enabled.");
        } else {
            player.sendMessage(ChatColor.GREEN + "Already enabled.");
        }
        return true;
    }

    boolean off(Player player, String[] args) {
        if (args.length != 0) return false;
        Sidebar sidebar = plugin.sessions.of(player);
        if (sidebar.canSee()) {
            sidebar.hide();
            player.sendMessage(ChatColor.RED + "Sidebar disabled.");
        } else {
            player.sendMessage(ChatColor.RED + "Already disabled.");
        }
        return true;
    }
}

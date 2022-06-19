package com.cavetale.sidebar;

import com.cavetale.core.command.CommandNode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

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

    private void toggle(Player player) {
        Sidebar sidebar = plugin.sessions.get(player).getSidebar();
        if (!sidebar.isVisible()) {
            sidebar.show();
            player.sendMessage(text("Sidebar enabled", GREEN));
        } else {
            sidebar.hide();
            player.sendMessage(text("Sidebar disabled", YELLOW));
        }
    }

    private void on(Player player) {
        Sidebar sidebar = plugin.sessions.get(player).getSidebar();
        if (!sidebar.isVisible()) {
            sidebar.show();
            player.sendMessage(text("Sidebar enabled", GREEN));
        } else {
            player.sendMessage(text("Already enabled", RED));
        }
    }

    private void off(Player player) {
        Sidebar sidebar = plugin.sessions.get(player).getSidebar();
        if (sidebar.isVisible()) {
            sidebar.hide();
            player.sendMessage(text("Sidebar disabled", YELLOW));
        } else {
            player.sendMessage(text("Already disabled", RED));
        }
    }
}

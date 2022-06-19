package com.cavetale.sidebar;

import com.cavetale.core.command.CommandNode;
import com.cavetale.core.command.CommandWarn;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class SidebarAdminCommand implements TabExecutor {
    private final SidebarPlugin plugin;
    private CommandNode rootNode;

    public SidebarAdminCommand enable() {
        rootNode = new CommandNode("sidebaradmin");
        rootNode.addChild("view").arguments("<player>")
            .description("View someone's sidebar content")
            .senderCaller(this::view);
        plugin.getCommand("sidebaradmin").setExecutor(this);
        return this;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        return rootNode.call(sender, command, alias, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return rootNode.complete(sender, command, alias, args);
    }

    private boolean view(CommandSender sender, String[] args) {
        if (args.length != 1) return false;
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) throw new CommandWarn("Player not found: " + args[0]);
        Session session = plugin.sessions.get(target);
        if (session == null) throw new CommandWarn(target.getName() + " does not have a session!");
        Sidebar sidebar = session.getSidebar();
        List<SidebarLine> lines = sidebar.getLines();
        sender.sendMessage(Component.text(target.getName() + " has " + lines.size() + " line(s)").color(TextColor.color(0xFFFF00)));
        for (SidebarLine line : lines) {
            sender.sendMessage(Component.text("Line #" + line.getIndex() + ": ").append(line.getNow()));
        }
        return true;
    }
}

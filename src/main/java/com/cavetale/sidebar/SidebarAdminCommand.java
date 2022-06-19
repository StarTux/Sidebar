package com.cavetale.sidebar;

import com.cavetale.core.command.CommandNode;
import com.cavetale.core.command.CommandWarn;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import static net.kyori.adventure.text.Component.join;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.JoinConfiguration.noSeparators;
import static net.kyori.adventure.text.format.NamedTextColor.*;

@RequiredArgsConstructor
public final class SidebarAdminCommand implements TabExecutor {
    private final SidebarPlugin plugin;
    private CommandNode rootNode;

    public SidebarAdminCommand enable() {
        rootNode = new CommandNode("sidebaradmin");
        rootNode.addChild("view").arguments("<player>")
            .description("View player HUD")
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

    private Session requireSession(String arg) {
        Player target = Bukkit.getPlayerExact(arg);
        if (target == null) throw new CommandWarn("Player not found: " + arg);
        Session session = plugin.sessions.get(target);
        if (session == null) throw new CommandWarn(target.getName() + " does not have a session!");
        return session;
    }

    private boolean view(CommandSender sender, String[] args) {
        if (args.length != 1) return false;
        Session session = requireSession(args[0]);
        List<Component> lines;
        lines = new ArrayList<>();
        for (SidebarLine sidebarLine : session.getSidebar().getLines()) {
            lines.add(sidebarLine.getNow());
        }
        sender.sendMessage(text("Sidebar " + lines.size() + " lines", AQUA));
        Component prefix = text("- ", DARK_GRAY);
        for (Component line : lines) {
            sender.sendMessage(join(noSeparators(), prefix, line));
        }
        lines = session.getHeader().getLines();
        sender.sendMessage(text("Header " + lines.size() + " lines", AQUA));
        for (Component line : lines) {
            sender.sendMessage(join(noSeparators(), prefix, line));
        }
        lines = session.getFooter().getLines();
        sender.sendMessage(text("Footer " + lines.size() + " lines", AQUA));
        for (Component line : lines) {
            sender.sendMessage(join(noSeparators(), prefix, line));
        }
        if (session.getBossbar().isShown()) {
            sender.sendMessage(text("Boss Bar", AQUA));
            sender.sendMessage(join(noSeparators(), prefix, session.getBossbar().getContent().getTitle()));
        } else {
            sender.sendMessage(text("Boss Bar Empty", RED));
        }
        return true;
    }
}

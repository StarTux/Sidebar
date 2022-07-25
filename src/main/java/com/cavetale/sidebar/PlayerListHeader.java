package com.cavetale.sidebar;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import static net.kyori.adventure.text.Component.join;
import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.JoinConfiguration.noSeparators;

public final class PlayerListHeader extends PlayerListDisplay {
    @Override
    protected void send(Player player, Component message) {
        player.sendPlayerListHeader(join(noSeparators(), newline(), message));
    }
}
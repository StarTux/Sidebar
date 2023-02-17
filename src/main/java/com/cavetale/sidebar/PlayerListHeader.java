package com.cavetale.sidebar;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public final class PlayerListHeader extends PlayerListDisplay {
    @Override
    protected void send(Player player, Component message) {
        player.sendPlayerListHeader(message);
    }
}

package com.cavetale.sidebar;

import com.cavetale.core.event.hud.PlayerHudPriority;

public enum Priority {
    HIGHEST(PlayerHudPriority.HIGHEST),
    HIGH(PlayerHudPriority.HIGH),
    DEFAULT(PlayerHudPriority.DEFAULT),
    LOW(PlayerHudPriority.LOW),
    LOWEST(PlayerHudPriority.LOWEST);

    public final int value;

    Priority(final PlayerHudPriority prio) {
        this.value = prio.value;
    }
}

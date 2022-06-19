package com.cavetale.sidebar;

import org.bukkit.plugin.java.JavaPlugin;

public final class SidebarPlugin extends JavaPlugin {
    protected final Sessions sessions = new Sessions(this);
    protected final SidebarCommand sidebarCommand = new SidebarCommand(this);
    protected final SidebarAdminCommand sidebarAdminCommand = new SidebarAdminCommand(this);

    @Override
    public void onEnable() {
        sessions.enable();
        sidebarCommand.enable();
        sidebarAdminCommand.enable();
    }
}

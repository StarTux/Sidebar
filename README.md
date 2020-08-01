# Sidebar

Provide a per-player sidebar for plugins to display messages in.

## Interface

The plugin calls an event once per tick for every player. Client
plugins should listen to said event and add their lines to the
sidebar.

```java
import com.cavetale.sidebar.PlayerSidebarEvent;
import com.cavetale.sidebar.Priority;
import org.bukkit.event.Listener;

class EventListener implements Listener {
    @EventHandler
    void onPlayerSidebar(PlayerSidebarEvent event) {
        event.addLines(plugin, Priority.DEFAULT, "Hello World");
    }
}
```

### Priority

Priority determines how high the message will be displayed in relation
to other messages.

```java
public enum Priority {
    HIGHEST,
    HIGH,
    DEFAULT,
    LOW,
    LOWEST;
}
```

## Commands

Players may disable the sidebar any time.

- `/sidebar off`
- `/sidebar on`
- `/sidebar toggle`

## Permissions

- `sidebar.sidebar` Show the sidebar and gain access to the `/sidebar` command.
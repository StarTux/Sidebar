package com.cavetale.sidebar;

import com.cavetale.core.event.hud.PlayerBossBarEntry;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.entity.Player;
import static net.kyori.adventure.text.Component.empty;

@Getter
public final class PlayerBossBar {
    private boolean shown;
    private BossBar bossbar = BossBar.bossBar(empty(), 1.0f, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS);
    private PlayerBossBarEntry content = null;

    protected void enable(Player player) { }

    protected void disable(Player player) {
        if (shown) hide(player);
    }

    protected void loadEntries(Player player, List<PlayerBossBarEntry> entries) {
        if (entries.isEmpty()) {
            content = null;
            if (shown) hide(player);
            return;
        }
        Collections.sort(entries);
        PlayerBossBarEntry newContent = entries.get(0);
        if (content == null || !content.isSimilar(newContent)) {
            content = newContent;
            bossbar.progress(Math.max(0f, Math.min(1f, content.getProgress())));
            bossbar.name(content.getTitle());
            bossbar.color(content.getColor());
            bossbar.overlay(content.getOverlay());
            bossbar.flags(content.getFlags());
        }
        if (!shown) show(player);
    }

    private void show(Player player) {
        shown = true;
        player.showBossBar(bossbar);
    }

    private void hide(Player player) {
        shown = false;
        player.hideBossBar(bossbar);
    }
}

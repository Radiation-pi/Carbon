package net.draycia.carbon.listeners;

import me.clip.placeholderapi.PlaceholderAPI;
import net.draycia.carbon.events.ChatFormatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class PlaceholderHandler implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPapiPlaceholder(ChatFormatEvent event) {
        event.setFormat(PlaceholderAPI.setPlaceholders(event.getUser().asOfflinePlayer(), event.getFormat()));
    }

}
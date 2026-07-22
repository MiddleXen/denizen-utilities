package com.isnsest.denizenutilities.bridges.LiteBans;

import com.isnsest.denizenutilities.DenizenUtilities;
import com.isnsest.denizenutilities.bridges.LiteBans.events.*;
import litebans.api.Entry;
import litebans.api.Events;
import org.bukkit.Bukkit;

public class LiteBansListener extends Events.Listener {

    private void runSync(Runnable task) {
        Bukkit.getScheduler().runTask(DenizenUtilities.instance, task);
    }

    @Override
    public void broadcastSent(String message, String type) {
        runSync(() -> LiteBansBroadcastScriptEvent.instance.fire(message, type));
    }

    @Override
    public void entryAdded(Entry entry) {
        runSync(() -> {
            switch (entry.getType()) {
                case "ban" -> PlayerBansScriptEvent.instance.fire(entry);
                case "mute" -> PlayerMutesScriptEvent.instance.fire(entry);
                case "kick" -> PlayerKicksScriptEvent.instance.fire(entry);
                case "warn" -> PlayerWarnsScriptEvent.instance.fire(entry);
            }
        });
    }

    @Override
    public void entryRemoved(Entry entry) {
        runSync(() -> {
            switch (entry.getType()) {
                case "ban" -> PlayerUnbansScriptEvent.instance.fire(entry);
                case "mute" -> PlayerUnmutesScriptEvent.instance.fire(entry);
            }
        });
    }
}

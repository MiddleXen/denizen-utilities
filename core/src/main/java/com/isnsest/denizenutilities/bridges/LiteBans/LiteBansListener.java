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
                case "ban" -> PlayerBannedScriptEvent.instance.fire(entry);
                case "mute" -> PlayerMutedScriptEvent.instance.fire(entry);
                case "kick" -> PlayerKickedScriptEvent.instance.fire(entry);
                case "warn" -> PlayerWarnedScriptEvent.instance.fire(entry);
            }
        });
    }

    @Override
    public void entryRemoved(Entry entry) {
        runSync(() -> {
            switch (entry.getType()) {
                case "ban" -> PlayerUnbannedScriptEvent.instance.fire(entry);
                case "mute" -> PlayerUnmutedScriptEvent.instance.fire(entry);
            }
        });
    }
}

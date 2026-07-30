package com.isnsest.denizenutilities.bridges.LiteBans;

import com.isnsest.denizenutilities.DenizenUtilities;
import com.isnsest.denizenutilities.bridges.LiteBans.events.LiteBansBansPlayerScriptEvent;
import com.isnsest.denizenutilities.bridges.LiteBans.events.LiteBansKicksPlayerScriptEvent;
import com.isnsest.denizenutilities.bridges.LiteBans.events.LiteBansMutesPlayerScriptEvent;
import com.isnsest.denizenutilities.bridges.LiteBans.events.LiteBansSendsBroadcastScriptEvent;
import com.isnsest.denizenutilities.bridges.LiteBans.events.LiteBansUnbansPlayerScriptEvent;
import com.isnsest.denizenutilities.bridges.LiteBans.events.LiteBansUnmutesPlayerScriptEvent;
import com.isnsest.denizenutilities.bridges.LiteBans.events.LiteBansWarnsPlayerScriptEvent;
import litebans.api.Entry;
import litebans.api.Events;
import org.bukkit.Bukkit;

public class LiteBansListener extends Events.Listener {

    @Override
    public void broadcastSent(String message, String type) {
        runSync(() -> LiteBansSendsBroadcastScriptEvent.instance.fire(message, type));
    }

    @Override
    public void entryAdded(Entry entry) {
        switch (entry.getType()) {
            case "ban" -> runSync(() -> LiteBansBansPlayerScriptEvent.instance.fire(entry));
            case "mute" -> runSync(() -> LiteBansMutesPlayerScriptEvent.instance.fire(entry));
            case "kick" -> runSync(() -> LiteBansKicksPlayerScriptEvent.instance.fire(entry));
            case "warn" -> runSync(() -> LiteBansWarnsPlayerScriptEvent.instance.fire(entry));
        }
    }

    @Override
    public void entryRemoved(Entry entry) {
        switch (entry.getType()) {
            case "ban" -> runSync(() -> LiteBansUnbansPlayerScriptEvent.instance.fire(entry));
            case "mute" -> runSync(() -> LiteBansUnmutesPlayerScriptEvent.instance.fire(entry));
        }
    }

    private void runSync(Runnable task) {
        Bukkit.getScheduler().runTask(DenizenUtilities.instance, task);
    }
}

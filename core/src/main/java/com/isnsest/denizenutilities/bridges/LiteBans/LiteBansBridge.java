package com.isnsest.denizenutilities.bridges.LiteBans;

import com.denizenscript.denizencore.events.ScriptEvent;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.isnsest.denizenutilities.bridges.LiteBans.events.LiteBansBansPlayerScriptEvent;
import com.isnsest.denizenutilities.bridges.LiteBans.events.LiteBansKicksPlayerScriptEvent;
import com.isnsest.denizenutilities.bridges.LiteBans.events.LiteBansMutesPlayerScriptEvent;
import com.isnsest.denizenutilities.bridges.LiteBans.events.LiteBansSendsBroadcastScriptEvent;
import com.isnsest.denizenutilities.bridges.LiteBans.events.LiteBansUnbansPlayerScriptEvent;
import com.isnsest.denizenutilities.bridges.LiteBans.events.LiteBansUnmutesPlayerScriptEvent;
import com.isnsest.denizenutilities.bridges.LiteBans.events.LiteBansWarnsPlayerScriptEvent;
import litebans.api.Events;

public class LiteBansBridge {

    private static LiteBansListener listener;

    public static void register() {
        listener = new LiteBansListener();
        Events.get().register(listener);

        // Events
        ScriptEvent.registerScriptEvent(LiteBansBansPlayerScriptEvent.class);
        ScriptEvent.registerScriptEvent(LiteBansUnbansPlayerScriptEvent.class);
        ScriptEvent.registerScriptEvent(LiteBansMutesPlayerScriptEvent.class);
        ScriptEvent.registerScriptEvent(LiteBansUnmutesPlayerScriptEvent.class);
        ScriptEvent.registerScriptEvent(LiteBansKicksPlayerScriptEvent.class);
        ScriptEvent.registerScriptEvent(LiteBansWarnsPlayerScriptEvent.class);
        ScriptEvent.registerScriptEvent(LiteBansSendsBroadcastScriptEvent.class);
        //

        Debug.log("denizen-utilities", "LiteBans bridge initialized.");
    }
}

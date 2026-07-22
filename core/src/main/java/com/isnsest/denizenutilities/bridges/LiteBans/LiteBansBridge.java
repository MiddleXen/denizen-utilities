package com.isnsest.denizenutilities.bridges.LiteBans;

import com.denizenscript.denizencore.events.ScriptEvent;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.isnsest.denizenutilities.bridges.LiteBans.events.*;
import litebans.api.Events;

public class LiteBansBridge {

    private static final LiteBansListener listener = new LiteBansListener();

    public static void register() {
        Events.get().register(listener);

        // Events
        ScriptEvent.registerScriptEvent(PlayerBansScriptEvent.class);
        ScriptEvent.registerScriptEvent(PlayerUnbansScriptEvent.class);
        ScriptEvent.registerScriptEvent(PlayerMutesScriptEvent.class);
        ScriptEvent.registerScriptEvent(PlayerUnmutesScriptEvent.class);
        ScriptEvent.registerScriptEvent(PlayerKicksScriptEvent.class);
        ScriptEvent.registerScriptEvent(PlayerWarnsScriptEvent.class);
        ScriptEvent.registerScriptEvent(LiteBansBroadcastScriptEvent.class);
        //

        Debug.log("denizen-utilities", "LiteBans bridge initialized.");
    }
}

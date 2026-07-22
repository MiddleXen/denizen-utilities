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
        ScriptEvent.registerScriptEvent(PlayerBannedScriptEvent.class);
        ScriptEvent.registerScriptEvent(PlayerUnbannedScriptEvent.class);
        ScriptEvent.registerScriptEvent(PlayerMutedScriptEvent.class);
        ScriptEvent.registerScriptEvent(PlayerUnmutedScriptEvent.class);
        ScriptEvent.registerScriptEvent(PlayerKickedScriptEvent.class);
        ScriptEvent.registerScriptEvent(PlayerWarnedScriptEvent.class);
        ScriptEvent.registerScriptEvent(LiteBansBroadcastScriptEvent.class);
        //

        Debug.log("denizen-utilities", "LiteBans bridge initialized.");
    }
}

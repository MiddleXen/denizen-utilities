package com.isnsest.denizenutilities.bridges.BetterModel.events;

import com.denizenscript.denizencore.events.ScriptEvent;
import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.bukkit.BetterModelBukkit;
import kr.toxicity.model.api.event.PluginStartReloadEvent;
import org.bukkit.event.Listener;

public class BMStartReloadScriptEvent extends ScriptEvent implements Listener {

    // <--[event]
    // @Events
    // bm start reload
    //
    // @Group denizen-utilities
    //
    // @Triggers when the BetterModel plugin begins the reload process.
    //
    // @Plugin denizen-utilities, BetterModel
    //
    // -->

    public static BMStartReloadScriptEvent instance;

    public BMStartReloadScriptEvent() {
        instance = this;
        registerCouldMatcher("bm start reload");
        BetterModel.eventBus().subscribe(BetterModelBukkit.platform(), PluginStartReloadEvent.class, event -> {
            instance.fire();
        });
    }
}

package com.isnsest.denizenutilities.bridges.BetterModel.events;

import com.denizenscript.denizencore.events.ScriptEvent;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.BetterModelPlatform;
import kr.toxicity.model.api.bukkit.BetterModelBukkit;
import kr.toxicity.model.api.event.PluginEndReloadEvent;
import org.bukkit.event.Listener;

public class BMEndReloadScriptEvent extends ScriptEvent implements Listener {

    // <--[event]
    // @Events
    // bm end reload
    //
    // @Group denizen-utilities
    //
    // @Triggers when the BetterModel plugin finishes reloading models and generating the resource pack.
    //
    // @Plugin denizen-utilities, BetterModel
    //
    // @Context
    // <context.result> returns a MapTag containing the reload details.
    // -->

    public static BMEndReloadScriptEvent instance;

    public PluginEndReloadEvent event;

    public BMEndReloadScriptEvent() {
        instance = this;
        registerCouldMatcher("bm end reload");
        BetterModel.eventBus().subscribe(BetterModelBukkit.platform(), PluginEndReloadEvent.class, event -> {
            instance.event = event;
            instance.fire();
        });
    }

    @Override
    public ObjectTag getContext(String name) {
        if (name.equals("result")) {
            BetterModelPlatform.ReloadResult result = event.result();
            return switch (result) {
                case BetterModelPlatform.ReloadResult.Success ignored -> new ElementTag("SUCCESS");
                case BetterModelPlatform.ReloadResult.Failure ignored -> new ElementTag("FAILURE");
                case BetterModelPlatform.ReloadResult.OnReload ignored -> new ElementTag("RELOAD");
            };
        }
        return super.getContext(name);
    }
}

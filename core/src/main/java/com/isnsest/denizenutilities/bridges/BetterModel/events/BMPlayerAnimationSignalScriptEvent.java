package com.isnsest.denizenutilities.bridges.BetterModel.events;

import com.denizenscript.denizen.events.BukkitScriptEvent;
import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizen.utilities.implementation.BukkitScriptEntryData;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.scripts.ScriptEntryData;
import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.bukkit.BetterModelBukkit;
import kr.toxicity.model.api.bukkit.platform.BukkitPlayer;
import kr.toxicity.model.api.event.AnimationSignalEvent;
import kr.toxicity.model.api.event.ModelEventListener;
import org.bukkit.entity.Player;

public class BMPlayerAnimationSignalScriptEvent extends BukkitScriptEvent {

    // <--[event]
    // @Events
    // bm player animation signal
    //
    // @Group BetterModel
    //
    // @Switch name:<name> to only process if the signal name matches.
    //
    // @Triggers when an animation script emits a personal signal to a player.
    //
    // @Context
    // <context.name> returns the name of the signal.
    //
    // @Player Always.
    // -->

    private ModelEventListener subscription;

    public Player player;
    public String signal;

    public BMPlayerAnimationSignalScriptEvent() {
        registerCouldMatcher("bm player animation signal");
        registerSwitches("name");
    }

    @Override
    public boolean matches(ScriptPath path) {
        if (!runGenericSwitchCheck(path, "name", signal)) {
            return false;
        }
        return super.matches(path);
    }

    @Override
    public ScriptEntryData getScriptEntryData() {
        return new BukkitScriptEntryData(new PlayerTag(player), null);
    }

    @Override
    public ObjectTag getContext(String name) {
        if (name.equals("name")) {
            return new ElementTag(signal);
        }
        return super.getContext(name);
    }

    @Override
    public void init() {
        if (subscription == null) {
            subscription = BetterModel.eventBus().subscribe(BetterModelBukkit.platform(), AnimationSignalEvent.class, event -> {
                if (!(event.player() instanceof BukkitPlayer eventPlayer)) return;

                BMPlayerAnimationSignalScriptEvent scriptEvent = (BMPlayerAnimationSignalScriptEvent) clone();

                scriptEvent.player = eventPlayer.source();
                scriptEvent.signal = event.signal();
                scriptEvent.fire();
            });
        }
    }

    @Override
    public void destroy() {
        if (subscription != null) {
            subscription.unregister();
            subscription = null;
        }
    }
}
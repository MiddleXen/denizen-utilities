package com.isnsest.denizenutilities.bridges.BetterModel.events;

import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizen.utilities.implementation.BukkitScriptEntryData;
import com.denizenscript.denizencore.events.ScriptEvent;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.scripts.ScriptEntryData;
import com.isnsest.denizenutilities.bridges.BetterModel.objects.BMActiveModelTag;
import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.bone.RenderedBone;
import kr.toxicity.model.api.bukkit.BetterModelBukkit;
import kr.toxicity.model.api.bukkit.platform.BukkitEntity;
import kr.toxicity.model.api.event.ModelEventListener;
import kr.toxicity.model.api.event.hitbox.HitBoxInteractAtEvent;
import kr.toxicity.model.api.tracker.EntityTracker;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class BMModelInteractScriptEvent extends ScriptEvent implements Listener {

    // <--[event]
    // @Events
    // bm player model interact
    //
    // @Switch name:<name> to only run the event if the BetterModel model name matches the given matcher.
    //
    // @Group denizen-utilities
    //
    // @Triggers when a player interacts with a BetterModel model hitbox.
    //
    // @Plugin denizen-utilities, BetterModel
    //
    // @Context
    // <context.model> returns the BMActiveModelTag that was interacted with.
    // <context.hand> returns the hand used to interact.
    //
    // @Player Always.
    // -->

    private ModelEventListener subscription;

    public HitBoxInteractAtEvent event;
    public BMActiveModelTag model;

    public BMModelInteractScriptEvent() {
        registerCouldMatcher("bm player model interact");
        registerSwitches("name");
    }

    @Override
    public boolean matches(ScriptPath path) {
        if (model == null || !runGenericSwitchCheck(path, "name", model.getTracker().name())) {
            return false;
        }
        return super.matches(path);
    }

    @Override
    public ScriptEntryData getScriptEntryData() {
        Player player = (Player) ((BukkitEntity) event.getWho()).source();
        return new BukkitScriptEntryData(new PlayerTag(player), null);
    }

    @Override
    public ObjectTag getContext(String name) {
        return switch (name) {
            case "model" -> model;
            case "hand" -> new ElementTag(event.getHand().name());
            default -> super.getContext(name);
        };
    }

    @Override
    public void init() {
        if (subscription == null) {
            subscription = BetterModel.eventBus().subscribe(BetterModelBukkit.platform(), HitBoxInteractAtEvent.class, event -> {
                this.event = event;

                RenderedBone bone = event.getHitBox().positionSource();
                for (EntityTracker tracker : event.getHitBox().registry().orElseThrow().trackers()) {
                    if (tracker.bones().contains(bone)) {
                        this.model = new BMActiveModelTag(tracker);
                        break;
                    }
                }

                fire();
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

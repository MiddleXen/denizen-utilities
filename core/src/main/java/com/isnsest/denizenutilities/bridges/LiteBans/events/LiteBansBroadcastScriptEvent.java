package com.isnsest.denizenutilities.bridges.LiteBans.events;

import com.denizenscript.denizen.utilities.implementation.BukkitScriptEntryData;
import com.denizenscript.denizencore.events.ScriptEvent;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.scripts.ScriptEntryData;
import org.bukkit.event.Listener;

public class LiteBansBroadcastScriptEvent extends ScriptEvent implements Listener {

    // <--[event]
    // @Events
    // litebans sent broadcast
    //
    // @Group denizen-utilities
    //
    // @Triggers when LiteBans sends a broadcast message to permitted players.
    //
    // @Context
    // <context.message> returns the broadcast message.
    // <context.type> returns the broadcast type (e.g. ban, mute, kick), if available.
    //
    // @Plugin denizen-utilities, LiteBans
    //
    // -->

    public static LiteBansBroadcastScriptEvent instance;

    public String message;
    public String type;

    public LiteBansBroadcastScriptEvent() {
        instance = this;
        registerCouldMatcher("litebans sent broadcast");
    }

    @Override
    public ScriptEntryData getScriptEntryData() {
        return new BukkitScriptEntryData(null, null);
    }

    @Override
    public ObjectTag getContext(String name) {
        return switch (name) {
            case "message" -> new ElementTag(message);
            case "type" -> type == null ? null : new ElementTag(type);
            default -> super.getContext(name);
        };
    }

    public void fire(String message, String type) {
        this.message = message;
        this.type = type;
        super.fire();
    }
}

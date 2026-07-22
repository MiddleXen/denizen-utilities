package com.isnsest.denizenutilities.bridges.LiteBans.events;

import com.denizenscript.denizen.utilities.implementation.BukkitScriptEntryData;
import com.denizenscript.denizencore.events.ScriptEvent;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.scripts.ScriptEntryData;
import com.isnsest.denizenutilities.bridges.LiteBans.LiteBansUtils;
import litebans.api.Entry;
import org.bukkit.event.Listener;

public class PlayerKicksScriptEvent extends ScriptEvent implements Listener {

    // <--[event]
    // @Events
    // litebans kicks player
    //
    // @Group denizen-utilities
    //
    // @Triggers when a player is kicked via LiteBans.
    //
    // @Context
    // <context.reason> returns the kick reason.
    // <context.executor> returns the name of the staff member who issued the kick.
    // <context.executor_uuid> returns the UUID of the staff member who issued the kick, if available.
    // <context.ip> returns the IP address of the player, if available.
    // <context.server_scope> returns the server scope of the kick.
    // <context.id> returns the database ID of the kick entry.
    // <context.uuid> returns the UUID of the kicked player, if available.
    // <context.random_id> returns the random ID of the entry.
    //
    // @Player When the kick targets a UUID.
    //
    // @Plugin denizen-utilities, LiteBans
    //
    // -->

    public static PlayerKicksScriptEvent instance;

    public Entry entry;

    public PlayerKicksScriptEvent() {
        instance = this;
        registerCouldMatcher("litebans kicks player");
    }

    @Override
    public ScriptEntryData getScriptEntryData() {
        return new BukkitScriptEntryData(LiteBansUtils.getPlayerTag(entry), null);
    }

    @Override
    public ObjectTag getContext(String name) {
        ObjectTag context = LiteBansUtils.getEntryContext(entry, name);
        return context != null ? context : super.getContext(name);
    }

    public void fire(Entry entry) {
        this.entry = entry;
        super.fire();
    }
}

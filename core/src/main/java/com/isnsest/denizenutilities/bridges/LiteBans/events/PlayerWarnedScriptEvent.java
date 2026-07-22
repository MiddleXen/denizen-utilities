package com.isnsest.denizenutilities.bridges.LiteBans.events;

import com.denizenscript.denizen.utilities.implementation.BukkitScriptEntryData;
import com.denizenscript.denizencore.events.ScriptEvent;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.scripts.ScriptEntryData;
import com.isnsest.denizenutilities.bridges.LiteBans.LiteBansUtils;
import litebans.api.Entry;
import org.bukkit.event.Listener;

public class PlayerWarnedScriptEvent extends ScriptEvent implements Listener {

    // <--[event]
    // @Events
    // player warned
    //
    // @Group denizen-utilities
    //
    // @Triggers when a player receives a warning via LiteBans.
    //
    // @Context
    // <context.reason> returns the warning reason.
    // <context.executor> returns the name of the staff member who issued the warning.
    // <context.executor_uuid> returns the UUID of the staff member who issued the warning, if available.
    // <context.server_scope> returns the server scope of the warning.
    // <context.id> returns the database ID of the warning entry.
    // <context.uuid> returns the UUID of the warned player, if available.
    // <context.random_id> returns the random ID of the entry.
    // <context.template_name> returns the template name used, if any.
    // <context.has_template> returns whether a template was used.
    //
    // @Player When the warning targets a UUID.
    //
    // @Plugin denizen-utilities, LiteBans
    //
    // -->

    public static PlayerWarnedScriptEvent instance;

    public Entry entry;

    public PlayerWarnedScriptEvent() {
        instance = this;
        registerCouldMatcher("player warned");
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

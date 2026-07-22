package com.isnsest.denizenutilities.bridges.LiteBans.events;

import com.denizenscript.denizen.utilities.implementation.BukkitScriptEntryData;
import com.denizenscript.denizencore.events.ScriptEvent;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.scripts.ScriptEntryData;
import com.isnsest.denizenutilities.bridges.LiteBans.LiteBansUtils;
import litebans.api.Entry;
import org.bukkit.event.Listener;

public class PlayerMutesScriptEvent extends ScriptEvent implements Listener {

    // <--[event]
    // @Events
    // litebans mutes player
    //
    // @Group denizen-utilities
    //
    // @Triggers when a player is muted via LiteBans.
    //
    // @Context
    // <context.reason> returns the mute reason.
    // <context.executor> returns the name of the staff member who issued the mute.
    // <context.executor_uuid> returns the UUID of the staff member who issued the mute, if available.
    // <context.duration> returns the mute duration in milliseconds, or -1 if permanent.
    // <context.duration_string> returns a human-readable duration string.
    // <context.permanent> returns whether the mute is permanent.
    // <context.ip> returns the IP address if this is an IP mute.
    // <context.ip_ban> returns whether this is an IP mute.
    // <context.server_scope> returns the server scope of the mute.
    // <context.id> returns the database ID of the mute entry.
    // <context.uuid> returns the UUID of the muted player, if available.
    // <context.random_id> returns the random ID of the entry.
    // <context.template_name> returns the template name used, if any.
    // <context.has_template> returns whether a template was used.
    //
    // @Player When the mute targets a UUID.
    //
    // @Plugin denizen-utilities, LiteBans
    //
    // -->

    public static PlayerMutesScriptEvent instance;

    public Entry entry;

    public PlayerMutesScriptEvent() {
        instance = this;
        registerCouldMatcher("litebans mutes player");
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

package com.isnsest.denizenutilities.bridges.LiteBans.events;

import com.denizenscript.denizen.utilities.implementation.BukkitScriptEntryData;
import com.denizenscript.denizencore.events.ScriptEvent;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.scripts.ScriptEntryData;
import com.isnsest.denizenutilities.bridges.LiteBans.LiteBansUtils;
import litebans.api.Entry;
import org.bukkit.event.Listener;

public class PlayerBannedScriptEvent extends ScriptEvent implements Listener {

    // <--[event]
    // @Events
    // player banned
    //
    // @Group denizen-utilities
    //
    // @Triggers when a player is banned via LiteBans.
    //
    // @Context
    // <context.reason> returns the ban reason.
    // <context.executor> returns the name of the staff member who issued the ban.
    // <context.executor_uuid> returns the UUID of the staff member who issued the ban, if available.
    // <context.duration> returns the ban duration in milliseconds, or -1 if permanent.
    // <context.duration_string> returns a human-readable duration string.
    // <context.permanent> returns whether the ban is permanent.
    // <context.ip> returns the IP address if this is an IP ban.
    // <context.ip_ban> returns whether this is an IP ban.
    // <context.server_scope> returns the server scope of the ban.
    // <context.id> returns the database ID of the ban entry.
    // <context.uuid> returns the UUID of the banned player, if available.
    // <context.random_id> returns the random ID of the entry.
    // <context.template_name> returns the template name used, if any.
    // <context.has_template> returns whether a template was used.
    //
    // @Player When the ban targets a UUID.
    //
    // @Plugin denizen-utilities, LiteBans
    //
    // -->

    public static PlayerBannedScriptEvent instance;

    public Entry entry;

    public PlayerBannedScriptEvent() {
        instance = this;
        registerCouldMatcher("player banned");
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

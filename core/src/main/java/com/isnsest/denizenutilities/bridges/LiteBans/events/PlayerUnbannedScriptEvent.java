package com.isnsest.denizenutilities.bridges.LiteBans.events;

import com.denizenscript.denizen.utilities.implementation.BukkitScriptEntryData;
import com.denizenscript.denizencore.events.ScriptEvent;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.scripts.ScriptEntryData;
import com.isnsest.denizenutilities.bridges.LiteBans.LiteBansUtils;
import litebans.api.Entry;
import org.bukkit.event.Listener;

public class PlayerUnbannedScriptEvent extends ScriptEvent implements Listener {

    // <--[event]
    // @Events
    // player unbanned
    //
    // @Group denizen-utilities
    //
    // @Triggers when a player is unbanned via LiteBans.
    //
    // @Context
    // <context.reason> returns the original ban reason.
    // <context.executor> returns the name of the staff member who originally issued the ban.
    // <context.executor_uuid> returns the UUID of the staff member who originally issued the ban, if available.
    // <context.removed_by> returns the name of the staff member who removed the ban.
    // <context.removal_reason> returns the reason for removing the ban, if provided.
    // <context.ip> returns the IP address if this was an IP ban.
    // <context.ip_ban> returns whether this was an IP ban.
    // <context.server_scope> returns the server scope of the ban.
    // <context.id> returns the database ID of the ban entry.
    // <context.uuid> returns the UUID of the unbanned player, if available.
    // <context.random_id> returns the random ID of the entry.
    //
    // @Player When the ban targeted a UUID.
    //
    // @Plugin denizen-utilities, LiteBans
    //
    // -->

    public static PlayerUnbannedScriptEvent instance;

    public Entry entry;

    public PlayerUnbannedScriptEvent() {
        instance = this;
        registerCouldMatcher("player unbanned");
    }

    @Override
    public ScriptEntryData getScriptEntryData() {
        return new BukkitScriptEntryData(LiteBansUtils.getPlayerTag(entry), null);
    }

    @Override
    public ObjectTag getContext(String name) {
        ObjectTag context = LiteBansUtils.getRemovalContext(entry, name);
        if (context != null) {
            return context;
        }
        context = LiteBansUtils.getEntryContext(entry, name);
        return context != null ? context : super.getContext(name);
    }

    public void fire(Entry entry) {
        this.entry = entry;
        super.fire();
    }
}

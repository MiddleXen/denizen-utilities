package com.isnsest.denizenutilities.bridges.DiscordSRV;

import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizencore.events.ScriptEvent;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.isnsest.denizenutilities.bridges.DiscordSRV.events.PlayerLinksDiscordAccountScriptEvent;
import com.isnsest.denizenutilities.bridges.DiscordSRV.events.PlayerUnlinksDiscordAccountScriptEvent;
import github.scarsz.discordsrv.DiscordSRV;

public class DiscordSRVBridge {

    private static final DiscordSRVListener discordSRVListener = new DiscordSRVListener();

    public static void register() {

        DiscordSRV.api.subscribe(discordSRVListener);

        registerTags();

        // Events
        ScriptEvent.registerScriptEvent(PlayerLinksDiscordAccountScriptEvent.class);
        ScriptEvent.registerScriptEvent(PlayerUnlinksDiscordAccountScriptEvent.class);
        //

        Debug.log("denizen-utilities", "DiscordSRV bridge initialized.");
    }

    public static void registerTags() {
        // <--[tag]
        // @attribute <PlayerTag.discord_id>
        // @returns ElementTag
        // @plugin denizen-utilities, DiscordSRV
        // @description
        // Returns the Discord ID associated with the Minecraft player's account via DiscordSRV.
        // Returns null if the player has not linked their Discord account.
        //
        // @Usage
        // Use to get the current player's Discord ID.
        // - narrate "Your Discord ID is <player.discord_id>"
        // -->
        PlayerTag.tagProcessor.registerTag(ElementTag.class, "discord_id", (attribute, object) -> {
            String id = DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(object.getUUID());
            return id == null ? null : new ElementTag(id);
        });
    }
}

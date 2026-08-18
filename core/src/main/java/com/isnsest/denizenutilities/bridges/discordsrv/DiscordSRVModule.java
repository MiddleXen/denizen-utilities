package com.isnsest.denizenutilities.bridges.discordsrv;

import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizencore.events.ScriptEvent;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.isnsest.denizenutilities.bridges.BridgeModule;
import com.isnsest.denizenutilities.bridges.discordsrv.events.PlayerLinksDiscordAccountScriptEvent;
import com.isnsest.denizenutilities.bridges.discordsrv.events.PlayerUnlinksDiscordAccountScriptEvent;
import github.scarsz.discordsrv.DiscordSRV;

public class DiscordSRVModule implements BridgeModule {

    @Override
    public String getPluginName() {
        return "DiscordSRV";
    }

    @Override
    public void register() {
        DiscordSRV.api.subscribe(new DiscordSRVListener());

        registerTags();

        // Events
        ScriptEvent.registerScriptEvent(PlayerLinksDiscordAccountScriptEvent.class);
        ScriptEvent.registerScriptEvent(PlayerUnlinksDiscordAccountScriptEvent.class);

        Debug.log("denizen-utilities", "DiscordSRV bridge initialized.");
    }

    private static void registerTags() {
        // <--[tag]
        // @attribute <PlayerTag.discord_id>
        // @returns ElementTag
        // @plugin denizen-utilities, DiscordSRV
        // @description
        // Returns the Discord ID associated with the Minecraft player's account via DiscordSRV.
        // Returns null if the player has not linked their Discord account.
        // -->
        PlayerTag.tagProcessor.registerTag(ElementTag.class, "discord_id", (_, object) -> {
            String id = DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(object.getUUID());
            return id == null ? null : new ElementTag(id);
        });
    }
}
package com.isnsest.denizenutilities.bridges.SkinsRestorer;

import com.denizenscript.denizencore.DenizenCore;
import com.denizenscript.denizencore.events.ScriptEvent;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.isnsest.denizenutilities.bridges.SkinsRestorer.commands.SkinCommand;
import com.isnsest.denizenutilities.bridges.SkinsRestorer.events.PlayerSkinApplyEvent;
import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;

public class SkinsRestorerBridge {

    private static SkinsRestorer skinsRestorerAPI;

    public static SkinsRestorer getAPI() {
        return skinsRestorerAPI;
    }

    public static void register() {
        skinsRestorerAPI = SkinsRestorerProvider.get();

        // Events
        ScriptEvent.registerScriptEvent(PlayerSkinApplyEvent.class);
        //

        // Commands
        DenizenCore.commandRegistry.registerCommand(SkinCommand.class);
        //

        Debug.log("denizen-utilities", "SkinsRestorer bridge initialized.");
    }
}

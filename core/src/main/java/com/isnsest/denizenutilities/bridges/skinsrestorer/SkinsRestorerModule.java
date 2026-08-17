package com.isnsest.denizenutilities.bridges.skinsrestorer;

import com.denizenscript.denizencore.DenizenCore;
import com.denizenscript.denizencore.events.ScriptEvent;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.isnsest.denizenutilities.bridges.BridgeModule;
import com.isnsest.denizenutilities.bridges.skinsrestorer.commands.SkinCommand;
import com.isnsest.denizenutilities.bridges.skinsrestorer.events.PlayerSkinApplyEvent;
import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;

public class SkinsRestorerModule implements BridgeModule {

    @Override
    public String getPluginName() {
        return "SkinsRestorer";
    }

    @Override
    public void register() {

        // Events
        ScriptEvent.registerScriptEvent(PlayerSkinApplyEvent.class);
        //

        // Commands
        DenizenCore.commandRegistry.registerCommand(SkinCommand.class);
        //

        Debug.log("denizen-utilities", "SkinsRestorer bridge initialized.");
    }

    public static SkinsRestorer getAPI() {
        return SkinsRestorerProvider.get();
    }
}

package com.isnsest.denizenutilities.extensions.properties;

import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizen.tags.BukkitTagContext;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.scripts.ScriptRegistry;
import com.isnsest.denizenutilities.extensions.containers.DialogScriptContainer;

public class PlayerExtensions {

    public static void register() {
        // <--[mechanism]
        // @object PlayerTag
        // @name show_dialog
        // @input ElementTag
        // @plugin denizen-utilities, Paper
        // @description
        // Opens a dialog UI for the player using the specified dialog script.
        // -->
        PlayerTag.registerOnlineOnlyMechanism("show_dialog", ElementTag.class, (object, mechanism, input) -> {
            BukkitTagContext context = (BukkitTagContext) mechanism.context;
            context.player = new PlayerTag(object.getPlayerEntity());
            DialogScriptContainer container = ScriptRegistry.getScriptContainer(input.asString());
            if (container == null) {
                mechanism.echoError("Invalid dialog script: '" + input.asString() + "'");
                return;
            }
            container.showTo(object.getPlayerEntity().getConnection(), context);
        });

        // <--[mechanism]
        // @object PlayerTag
        // @name close_dialog
        // @input None
        // @plugin denizen-utilities, Paper
        // @description
        // Closes the player's current dialog UI.
        // -->
        PlayerTag.registerOnlineOnlyMechanism("close_dialog", (object, _) -> {
            object.getPlayerEntity().closeDialog();
        });
    }
}

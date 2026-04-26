package com.isnsest.denizen.Denizen.properties;

import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.scripts.ScriptRegistry;
import com.isnsest.denizen.Denizen.containers.DialogScriptContainer;
import com.isnsest.denizen.DenizenUtilities;

public class PlayerExtensions {

    public static void register() {
        if (DenizenUtilities.supportsPaper) {
            // <--[mechanism]
            // @object PlayerTag
            // @name show_dialog
            // @input ElementTag
            // @plugin denizen-utilities, Paper
            // @description
            // Opens a dialog UI for the player using the specified dialog script.
            // -->
            PlayerTag.registerOnlineOnlyMechanism("show_dialog", ElementTag.class, (object, mechanism, input) -> {
                DialogScriptContainer container = ScriptRegistry.getScriptContainer(input.asString());
                if (container == null) {
                    mechanism.echoError("Invalid dialog script: '" + input.asString() + "'");
                    return;
                }
                container.showTo(object.getPlayerEntity(), mechanism.context);
            });

            // <--[mechanism]
            // @object PlayerTag
            // @name close_dialog
            // @input None
            // @plugin denizen-utilities, Paper
            // @description
            // Closes the player's current dialog UI.
            // -->
            PlayerTag.registerOnlineOnlyMechanism("close_dialog", (object, mechanism) -> {
                object.getPlayerEntity().closeDialog();
            });
        }
    }
}

package com.isnsest.denizenutilities.extensions.commands;

import com.denizenscript.denizen.utilities.Utilities;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.objects.core.ScriptTag;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.commands.generator.*;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import com.denizenscript.denizencore.utilities.DefinitionProvider;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.isnsest.denizenutilities.extensions.containers.DialogScriptContainer;

import java.util.List;

public class ShowDialogCommand extends AbstractCommand {

    public ShowDialogCommand() {
        setName("showdialog");
        setSyntax("showdialog <dialog> def:<ListTag>");
        setRequiredArguments(1, 2);
        autoCompile();
    }

    // <--[command]
    // @Name showdialog
    // @Syntax showdialog [<dialog>] (def:<ListTag>)
    // @Required 1
    // @Maximum 2
    // @Short Opens a custom Paper UI dialog for a player.
    // @Group UI
    //
    // @Description
    // This command opens a custom Paper-native dialog window for the player attached to the script queue.
    // The dialog properties and layout must be defined within a 'dialog' script container.
    //
    // @Usage
    // Use to open a simple dialog script with no definitions.
    // - showdialog MySimpleDialog
    //
    // @Usage
    // Use to pass parameter definitions (like quest IDs or reward amounts) into the dialog.
    // - showdialog QuestConfirmDialog def:quest_01|1000
    // -->

    public static void autoExecute(ScriptEntry scriptEntry,
                                   @ArgName("dialog") @ArgLinear ScriptTag dialog,
                                   @ArgName("def") @ArgPrefixed @ArgDefaultNull ListTag definitions) {
        if (!(dialog.getContainer() instanceof DialogScriptContainer container)) {
            Debug.echoError("Invalid dialog script: '" + dialog.getName() + "'.");
            return;
        }
        if (definitions != null) {
            DefinitionProvider provider = scriptEntry.context.definitionProvider;
            List<String> definition_names = null;
            if (container.contains("definitions", String.class)) {
                String str = container.getString("definitions");
                definition_names = CoreUtilities.split(str, '|');
            }
            int x = 1;
            for (ObjectTag definition : definitions.objectForms) {
                String name = definition_names != null && definition_names.size() >= x ? definition_names.get(x - 1).trim() : String.valueOf(x);
                int squareBracket = name.indexOf('[');
                if (squareBracket != -1) {
                    name = name.substring(0, squareBracket).trim();
                }
                provider.addDefinition(name, definition);
                x++;
            }
        }
        container.showTo(Utilities.getEntryPlayer(scriptEntry).getPlayerEntity(), scriptEntry.context);
    }
}
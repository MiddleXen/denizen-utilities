package com.isnsest.denizenutilities.bridges.BetterModel.commands;

import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.commands.generator.*;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.isnsest.denizenutilities.bridges.BetterModel.BetterModelUtils;
import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.bukkit.platform.BukkitAdapter;
import kr.toxicity.model.api.data.renderer.ModelRenderer;
import kr.toxicity.model.api.tracker.TrackerModifier;

public class BMModelCommand extends AbstractCommand {

    public BMModelCommand() {
        setName("bmmodel");
        setSyntax("bmmodel [entity:<entity>] [model:<model>] (remove)");
        setRequiredArguments(2, 3);
        autoCompile();
    }

    // <--[command]
    // @Name bmmodel
    // @Syntax bmmodel [entity:<entity>] [model:<model>] (remove)
    // @Required 2
    // @Maximum 3
    // @Short Adds or removes a BetterModel from an entity.
    // @Group denizen-utilities
    // @Plugin denizen-utilities, BetterModel
    //
    // @Description
    // Attaches a specific BetterModel to an entity or removes an existing one.
    // An entity can have multiple models attached at once.
    //
    // @Usage
    // Use to add a 'dragon' model to an entity.
    // - bmmodel entity:<context.entity> model:dragon
    //
    // @Usage
    // Use to remove a model from an entity.
    // - bmmodel entity:<context.entity> model:dragon remove
    // -->

    @Override
    public void addCustomTabCompletions(TabCompletionsBuilder tab) {
        tab.addWithPrefix("model:", BetterModel.models().stream().map(ModelRenderer::name).toList());
        tab.add("remove");
    }

    public static void autoExecute(ScriptEntry scriptEntry,
                                   @ArgName("entity") @ArgPrefixed EntityTag entity,
                                   @ArgName("model") @ArgPrefixed ElementTag model,
                                   @ArgName("remove") boolean remove) {
        if (BetterModel.model(model.asString()).isEmpty()) {
            Debug.echoError(scriptEntry, "Model '" + model.asString() + "' is not found.");
            return;
        } else if (remove) {
            BetterModelUtils.remove(entity.getBukkitEntity(), model.asString());
            return;
        }
        BetterModel.model(model.asString())
                .map(renderer -> renderer.create(BukkitAdapter.adapt(entity.getBukkitEntity()), TrackerModifier.DEFAULT, e -> {}));
    }
}
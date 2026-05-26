package com.isnsest.denizenutilities.bridges.BetterModel.commands;

import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;
import com.denizenscript.denizencore.scripts.commands.generator.*;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.animation.AnimationIterator;
import kr.toxicity.model.api.animation.AnimationModifier;
import kr.toxicity.model.api.bukkit.platform.BukkitAdapter;
import kr.toxicity.model.api.data.renderer.ModelRenderer;
import kr.toxicity.model.api.util.function.BooleanConstantSupplier;
import org.bukkit.entity.Player;

import java.util.List;

import static com.isnsest.denizenutilities.bridges.BetterModel.BetterModelUtils.parseLoop;

public class BMLimbCommand extends AbstractCommand {

    // <--[command]
    // @Name bmlimb
    // @Syntax bmlimb [target:<entity>] [model:<limb_model>] [animation:<animation>] (loop:<mode>) (override)
    // @Required 3
    // @Maximum 4
    // @Short Manages BetterModel limb animations for a player.
    // @Group denizen-utilities
    // @Plugin denizen-utilities, BetterModel
    //
    // @Description
    // Plays a specific limb animation for a player or NPC.
    // Available loop modes: PLAY_ONCE, LOOP, HOLD_ON_LAST.
    //
    // @Usage
    // Use to play a waving animation on a player's arm.
    // - bmlimb target:<player> model:player_arm animation:wave loop:PLAY_ONCE
    // -->

    public BMLimbCommand() {
        setName("bmlimb");
        setSyntax("bmlimb [target:<player>] [model:<BMModelTag>] [animation:<animation>] (loop:<ONCE|LOOP|HOLD>) (override)");
        setRequiredArguments(3, 4);
        autoCompile();
    }

    @Override
    public void addCustomTabCompletions(TabCompletionsBuilder tab) {
        tab.addWithPrefix("model:", BetterModel.limbs().stream().map(ModelRenderer::name).toList());
        tab.addWithPrefix("loop:", List.of("PLAY_ONCE", "LOOP", "HOLD_ON_LAST"));
    }

    public static void autoExecute(ScriptEntry scriptEntry,
                                   @ArgName("target") @ArgPrefixed EntityTag entityTag,
                                   @ArgName("model") @ArgPrefixed ElementTag modelName,
                                   @ArgName("animation") @ArgPrefixed ElementTag animationName,
                                   @ArgName("override") boolean override,
                                   @ArgName("loop") @ArgDefaultText("PLAY_ONCE") @ArgPrefixed ElementTag loopMode) {

        String model = modelName.asString();
        String animation = animationName.asString();

        if (BetterModel.limb(model).isEmpty()) {
            Debug.echoError(scriptEntry, "Limb model '" + model + "' not found.");
            return;
        }

        if (!(entityTag.getBukkitEntity() instanceof Player player)) {
            Debug.echoError(scriptEntry, "Target is not a PLAYER/NPC.");
            return;
        }

        AnimationIterator.Type type = parseLoop(loopMode.asString());

        AnimationModifier modifier = AnimationModifier.builder()
                .type(type)
                .override(override ? true : null)
                .build();

        BetterModel.platform().modelManager().animate(BukkitAdapter.adapt(player), model, animation, modifier);
    }
}
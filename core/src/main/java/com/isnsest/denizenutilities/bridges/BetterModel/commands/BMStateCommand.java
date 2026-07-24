package com.isnsest.denizenutilities.bridges.BetterModel.commands;

import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;
import com.denizenscript.denizencore.scripts.commands.generator.*;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import kr.toxicity.model.api.animation.AnimationIterator;
import kr.toxicity.model.api.animation.AnimationModifier;
import kr.toxicity.model.api.animation.AnimationOverrideState;
import kr.toxicity.model.api.bone.RenderedBone;
import kr.toxicity.model.api.data.blueprint.BlueprintAnimation;
import kr.toxicity.model.api.tracker.EntityTracker;
import kr.toxicity.model.api.util.function.BonePredicate;
import kr.toxicity.model.api.util.function.FloatSupplier;
import com.isnsest.denizenutilities.bridges.BetterModel.objects.BMActiveModelTag;

import java.util.List;
import java.util.function.Predicate;

import static com.isnsest.denizenutilities.bridges.BetterModel.BetterModelUtils.parseLoop;

public class BMStateCommand extends AbstractCommand {

    // <--[command]
    // @Name bmstate
    // @Syntax bmstate [model:<BMModelTag>] [state:<animation>] (bones:<list>) (loop:<mode>) (speed:<#.#>) (override) (remove)
    // @Required 2
    // @Maximum 8
    // @Short Manages animation states for specific bones in a BetterModel.
    // @Group denizen-utilities
    // @Plugin denizen-utilities, BetterModel
    //
    // @Description
    // Starts, stops, or modifies animations for a specific model.
    // You can target specific bones using the 'bones' argument; otherwise, the animation applies to the entire model.
    // Available loop modes: PLAY_ONCE, LOOP, HOLD_ON_LAST.
    // Use the 'remove' switch to stop the specified animation (or all animations if no state is provided).
    //
    // @Usage
    // Use to start a looping 'walk' animation on a model at 1.2x speed.
    // - bmstate model:<[my_model]> state:walk loop:LOOP speed:1.2
    //
    // @Usage
    // Use to play an animation only on the model's 'head' and 'waist' bones.
    // - bmstate model:<[my_model]> state:nod bones:head|waist loop:PLAY_ONCE
    //
    // @Usage
    // Use to stop the 'walk' animation on a model.
    // - bmstate model:<[my_model]> state:walk remove
    // -->

    public BMStateCommand() {
        setName("bmstate");
        setSyntax("bmstate [model:<BMModelTag>] [state:<animation>] (bones:<list>) (loop:<PLAY_ONCE|LOOP|HOLD>) (speed:<#.#>) (override) (remove)");
        setRequiredArguments(2, 7);
        autoCompile();
    }

    @Override
    public void addCustomTabCompletions(TabCompletionsBuilder tab) {
        tab.addWithPrefix("loop:", List.of("PLAY_ONCE", "LOOP", "HOLD_ON_LAST"));
        tab.add("override");
    }

    public static void autoExecute(ScriptEntry scriptEntry,
                                   @ArgName("model") @ArgPrefixed BMActiveModelTag modelTag,
                                   @ArgName("state") @ArgPrefixed @ArgDefaultNull ElementTag animation,
                                   @ArgName("bones") @ArgPrefixed @ArgDefaultNull ListTag bones,
                                   @ArgName("loop") @ArgDefaultText("PLAY_ONCE") @ArgPrefixed ElementTag loopMode,
                                   @ArgName("speed") @ArgDefaultText("1.0") @ArgPrefixed ElementTag speed,
                                   @ArgName("override") boolean override,
                                   @ArgName("remove") boolean remove) {

        EntityTracker tracker = modelTag.getTracker();

        if (tracker == null || tracker.isClosed()) {
            Debug.echoError(scriptEntry, "Model tracker is inactive or the entity has been removed.");
            return;
        }

        Predicate<RenderedBone> boneFilter = (bones == null || bones.isEmpty())
                ? (b -> true)
                : (b -> bones.contains(b.name().name()));

        BonePredicate bonePredicate = BonePredicate.of(BonePredicate.TRUE.applyAtChildren(), boneFilter);

        if (remove) {
            handleRemove(tracker, animation, bonePredicate);
            return;
        }

        if (animation == null) {
            Debug.echoError(scriptEntry, "The 'state:' argument is required to start an animation.");
            return;
        }

        BlueprintAnimation blueprint = tracker.renderer().animation(animation.asString()).orElse(null);
        if (blueprint == null) {
            Debug.echoError(scriptEntry, "Animation '" + animation.asString() + "' was not found in model '" + tracker.name() + "'.");
            return;
        }

        AnimationIterator.Type type = parseLoop(loopMode.asString());
        AnimationModifier modifier = AnimationModifier.builder()
                .type(type)
                .speed(FloatSupplier.of(speed.asFloat()))
                .override(override ? true : null)
                .build();

        if (bones == null || bones.isEmpty()) {
            tracker.animate(blueprint, modifier);
        } else {
            tracker.getPipeline().matchAnimation((bone, _) -> {
                String name = bone.name().name();

                if (bones.contains(name)) {
                    bone.addAnimation(AnimationOverrideState.MATCHED, blueprint, modifier, () -> {});
                    return true;
                }

                return false;
            });
        }
    }

    private static void handleRemove(EntityTracker tracker, ElementTag animation, Predicate<RenderedBone> predicate) {
        if (animation == null) {
            tracker.renderer().animations().keySet().forEach(anim -> tracker.stopAnimation(predicate, anim));
        } else {
            tracker.stopAnimation(predicate, animation.asString());
        }
    }
}
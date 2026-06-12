package com.isnsest.denizenutilities.bridges.BetterModel;

import com.isnsest.denizenutilities.bridges.BetterModel.events.BMAnimationSignalScriptEvent;
import com.isnsest.denizenutilities.bridges.BetterModel.objects.BMActiveModelTag;
import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.script.AnimationScript;
import kr.toxicity.model.api.tracker.EntityTracker;

public class BMScriptManager {

    // <--[language]
    // @name BetterModel Animation Scripts
    // @group BetterModel
    // @description
    // BetterModel allows you to trigger Denizen logic directly from Blockbench animations
    // by placing keyframes on the "Instructions" (Effects) track.
    //
    // To use this, add a keyframe to the "Instructions" track and set its script field
    // using the "denizen:" prefix.
    //
    // Format: denizen:signal_name{metadata}
    //
    // When the animation reaches this keyframe, it will fire the global script event:
    // "on bm animation signal".
    //
    // METADATA:
    // You can pass parameters inside curly braces {}. These will be available
    // directly in the event context.
    // Example: denizen:strike{damage=5;type=fire}
    // In your script, you can use <context.damage> to get '5' or <context.type> to get 'fire'.
    //
    // CONTEXTS:
    // <context.name> returns the name of the signal (e.g., 'strike').
    // <context.model> returns the BMActiveModelTag that triggered the signal.
    // <context.[key]> returns any metadata value defined in the braces {}.
    //
    // NOTE: This system is for global server-side logic. For per-player visual signals
    // (using the built-in "signal:" prefix), use the "bm player animation signal" event.
    // -->
    public static void register() {
        BetterModel.platform().scriptManager().addBuilder("denizen", data -> {
            final String signal = data.args();
            final var meta = data.metadata();

            if (signal == null || signal.isEmpty()) return AnimationScript.EMPTY;

            return AnimationScript.of(true, tracker -> {
                if (!(tracker instanceof EntityTracker entityTracker)) return;

                BMAnimationSignalScriptEvent event = (BMAnimationSignalScriptEvent) BMAnimationSignalScriptEvent.instance.clone();
                event.model = new BMActiveModelTag(entityTracker);
                event.signal = signal;
                event.metadata = meta;

                event.fire();
            });
        });
    }

}

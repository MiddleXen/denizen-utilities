package com.isnsest.denizenutilities.bridges.BetterModel.events;

import com.denizenscript.denizencore.events.ScriptEvent;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.isnsest.denizenutilities.bridges.BetterModel.objects.BMActiveModelTag;
import kr.toxicity.model.api.script.ScriptBuilder;

public class BMAnimationSignalScriptEvent extends ScriptEvent {

    // <--[event]
    // @Events
    // bm animation signal
    //
    // @Group BetterModel
    //
    // @Switch name:<name> to only process if the signal name matches.
    //
    // @Triggers when a BetterModel animation reaches a script with the 'denizen:' prefix.
    //
    // @Context
    // <context.name> returns the name of the signal (the argument provided after 'denizen:').
    // <context.model> returns the BMActiveModelTag that triggered this signal.
    // <context.(key)> returns the value of any metadata key defined in the Blockbench script braces {}.
    //
    // -->

    public static BMAnimationSignalScriptEvent instance;

    public BMActiveModelTag model;
    public String signal;
    public ScriptBuilder.ScriptMetaData metadata;

    public BMAnimationSignalScriptEvent() {
        instance = this;
        registerCouldMatcher("bm animation signal");
        registerSwitches("name");
    }

    @Override
    public boolean matches(ScriptPath path) {
        if (!runGenericSwitchCheck(path, "name", signal)) {
            return false;
        }
        return super.matches(path);
    }

    @Override
    public ObjectTag getContext(String name) {
        return switch (name) {
            case "name" -> new ElementTag(signal);
            case "model" -> model;
            default -> {
                String value = metadata.asString(name);
                yield value != null ? new ElementTag(value) : null;
            }
        };
    }
}
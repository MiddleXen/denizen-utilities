package com.isnsest.denizenutilities.bridges.BetterModel.objects;

import com.denizenscript.denizencore.objects.Fetchable;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.DurationTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.tags.ObjectTagProcessor;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.data.renderer.ModelRenderer;

// <--[ObjectType]
// @name BMModelTag
// @prefix bmmodel
// @base ElementTag
// @format The identity format for BMModelTag is the name of the model.
// For example: 'bmmodel@demon_knight'.
//
// @plugin denizen-utilities, BetterModel
// @description
// A BMModelTag represents a BetterModel blueprint (the raw model data from the files).
// It can be used to retrieve information about a model without needing to spawn it.
//
// -->
public class BMModelTag implements ObjectTag {

    @Fetchable("bmmodel")
    public static BMModelTag valueOf(String string, TagContext context) {
        if (string == null || string.isEmpty()) return null;

        final String name = CoreUtilities.toLowerCase(string).startsWith("bmmodel@")
                ? string.substring(8)
                : string;

        return BetterModel.model(name)
                .or(() -> BetterModel.limb(name))
                .map(BMModelTag::new)
                .orElse(null);
    }

    public static boolean matches(String input) {
        return input != null && input.startsWith("bmmodel@");
    }

    private final ModelRenderer model;
    private String prefix = "bmmodel";

    public BMModelTag(ModelRenderer model) {
        this.model = model;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public ObjectTag setPrefix(String s) {
        this.prefix = s;
        return this;
    }

    @Override
    public boolean isUnique() {
        return false;
    }

    @Override
    public String identify() {
        return "bmmodel@" + model.name();
    }

    @Override
    public String identifySimple() {
        return identify();
    }

    @Override
    public Object getJavaObject() {
        return model;
    }

    @Override
    public String toString() {
        return identify();
    }

    public static final ObjectTagProcessor<BMModelTag> tagProcessor = new ObjectTagProcessor<>();

    public static void register() {

        // <--[tag]
        // @attribute <BMModelTag.name>
        // @returns ElementTag
        // @description
        // Returns the name of the model.
        // -->
        tagProcessor.registerTag(ElementTag.class, "name", (_, object) -> new ElementTag(object.model.name()));

        // <--[tag]
        // @attribute <BMModelTag.type>
        // @returns ElementTag
        // @description
        // Returns the template type of the model.
        // Possible values: PLAYER or GENERAL.
        // -->
        tagProcessor.registerTag(ElementTag.class, "type", (_, object) -> new ElementTag(object.model.type().name()));

        // <--[tag]
        // @attribute <BMModelTag.animations>
        // @returns ListTag<ElementTag>
        // @description
        // Returns a list of all animation names available for this model.
        // -->
        tagProcessor.registerTag(ListTag.class, "animations", (_, object) -> {
            return new ListTag(object.model.animations().keySet());
        });

        // <--[tag]
        // @attribute <BMModelTag.animation_duration[<name>]>
        // @returns DurationTag
        // @description
        // Returns the duration of a specific animation for this model.
        // -->
        tagProcessor.registerTag(DurationTag.class, ElementTag.class, "animation_duration", (_, object, input) -> {
            return object.model.animation(input.asString())
                    .map(anim -> new DurationTag(anim.length()))
                    .orElse(null);
        });
    }

    @Override
    public ObjectTag getObjectAttribute(Attribute attribute) {
        return tagProcessor.getObjectAttribute(this, attribute);
    }
}
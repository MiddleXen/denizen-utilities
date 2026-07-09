package com.isnsest.denizenutilities.extensions.properties;

import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.core.MapTag;
import com.denizenscript.denizencore.utilities.text.StringHolder;
import com.isnsest.denizenutilities.nms.NMSHandler;

import java.util.Map;

import static com.denizenscript.denizen.objects.BiomeTag.tagProcessor;

public class BiomeExtensions {

    public static void register() {
        // <--[tag]
        // @attribute <BiomeTag.attribute[<name>]>
        // @returns ObjectTag
        // @plugin denizen-utilities
        // @description
        // Returns the value of the specified environment attribute for this biome.
        //
        // See: <@link url https://minecraft.wiki/w/Environment_attribute>
        //
        // Example: <player.location.biome.attribute[sky_color]>
        // -->
        tagProcessor.registerTag(ObjectTag.class, ElementTag.class, "attribute", (_, object, input) ->
                NMSHandler.biomeHelper.getAttribute(object.getBiome(), input.asString()));

        // <--[mechanism]
        // @object BiomeTag
        // @name attribute
        // @input MapTag
        // @plugin denizen-utilities
        // @description
        // Sets one or more environment attributes for this biome.
        // The input must be a MapTag where the keys are the attribute names and the values are the new data.
        //
        // See: <@link url https://minecraft.wiki/w/Environment_attribute>
        //
        // Example:
        // # Changes the plains biome to have a red sky and green fog.
        // - adjust <biome[plains]> attribute:[sky_color=red;fog_color=<color[green]>]
        // -->
        tagProcessor.registerMechanism("attribute", false, MapTag.class, (object, _, input) -> {
            for (Map.Entry<StringHolder, ObjectTag> entry : input.entrySet()) {
                NMSHandler.biomeHelper.setAttribute(object.getBiome(), entry.getKey().str, entry.getValue());
            }
        });
    }
}

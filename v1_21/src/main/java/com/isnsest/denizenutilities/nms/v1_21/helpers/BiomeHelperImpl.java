package com.isnsest.denizenutilities.nms.v1_21.helpers;

import com.denizenscript.denizen.nms.abstracts.BiomeNMS;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ColorTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.isnsest.denizenutilities.nms.helpers.BiomeHelper;
import com.denizenscript.denizen.nms.v1_21.impl.BiomeNMSImpl;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributes;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class BiomeHelperImpl implements BiomeHelper {

    public static final Map<String, EnvironmentAttribute<?>> ATTRIBUTE_CACHE = new HashMap<>();

    static {
        try {
            for (Field field : EnvironmentAttributes.class.getFields()) {
                if (field.getType().equals(EnvironmentAttribute.class)) {
                    field.setAccessible(true);
                    EnvironmentAttribute<?> attribute = (EnvironmentAttribute<?>) field.get(null);
                    if (attribute != null) {
                        ATTRIBUTE_CACHE.put(field.getName().toUpperCase(), attribute);
                    }
                }
            }
        } catch (Exception e) {
            Debug.echoError(e);
        }
    }

    @Override
    public void setAttribute(BiomeNMS biomeNMS, String name, ObjectTag value) {
        EnvironmentAttribute<?> attribute = ATTRIBUTE_CACHE.get(name.toUpperCase());
        if (attribute == null) {
            Debug.echoError("Environment attribute '" + name + "' does not exist.");
            return;
        }

        var defaultValue = attribute.defaultValue();
        String expectedTypeName = defaultValue.getClass().getSimpleName();

        final ElementTag elementTag = value.asElement();
        Object object = switch (defaultValue) {
            case Integer ignored -> {
                expectedTypeName = "ColorTag";
                var color = ColorTag.valueOf(elementTag.asString(), null);
                yield color != null ? color.asARGB() : null;
            }
            case Float ignored -> elementTag.isFloat() ? elementTag.asFloat() : null;
            case Boolean ignored -> elementTag.isBoolean() ? elementTag.asBoolean() : null;
            default -> null;
        };

        if (object == null) {
            Debug.echoError("Invalid value format for attribute '" + name + "'. Expected type: "
                    + expectedTypeName + ", but got: '" + value + "'");
            return;
        }

        object = ((EnvironmentAttribute) attribute).sanitizeValue(object);
        ((BiomeNMSImpl) biomeNMS).setEnvironmentAttribute((EnvironmentAttribute) attribute, object);
    }

    @Override
    public ObjectTag getAttribute(BiomeNMS biomeNMS, String name) {
        EnvironmentAttribute<?> attribute = ATTRIBUTE_CACHE.get(name.toUpperCase());
        if (attribute == null) {
            return null;
        }

        Object value = ((BiomeNMSImpl) biomeNMS).getEnvironmentAttribute(attribute);
        if (value == null) {
            return null;
        }

        return switch (value) {
            case Integer integer -> ColorTag.fromARGB(integer);
            case Float f -> new ElementTag(f);
            case Boolean b -> new ElementTag(b);
            default -> null;
        };
    }
}
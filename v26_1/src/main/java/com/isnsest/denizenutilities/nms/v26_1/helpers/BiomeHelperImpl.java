package com.isnsest.denizenutilities.nms.v26_1.helpers;

import com.denizenscript.denizen.nms.v26_1.impl.BiomeNMSImpl;
import com.isnsest.denizenutilities.extensions.helpers.BiomeHelper;
import net.minecraft.world.attribute.EnvironmentAttributes;

public class BiomeHelperImpl implements BiomeHelper {

    @Override
    public int getSkyColor(Object nmsBiome) {
        return ((BiomeNMSImpl) nmsBiome).getEnvironmentAttribute(EnvironmentAttributes.SKY_COLOR);
    }

    @Override
    public void setSkyColor(Object nmsBiome, int color) {
        ((BiomeNMSImpl) nmsBiome).setEnvironmentAttribute(EnvironmentAttributes.SKY_COLOR, color);
    }

    @Override
    public int getSkyLightColor(Object nmsBiome) {
        return ((BiomeNMSImpl) nmsBiome).getEnvironmentAttribute(EnvironmentAttributes.SKY_LIGHT_COLOR);
    }

    @Override
    public void setSkyLightColor(Object nmsBiome, int color) {
        ((BiomeNMSImpl) nmsBiome).setEnvironmentAttribute(EnvironmentAttributes.SKY_LIGHT_COLOR, color);
    }
}
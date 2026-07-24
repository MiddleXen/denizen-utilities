package com.isnsest.denizenutilities.nms.helpers;

import com.denizenscript.denizen.nms.abstracts.BiomeNMS;
import com.denizenscript.denizencore.objects.ObjectTag;

public interface BiomeHelper {
    void setAttribute(BiomeNMS biomeNMS, String name, ObjectTag value);
    ObjectTag getAttribute(BiomeNMS nmsBiome, String name);
}

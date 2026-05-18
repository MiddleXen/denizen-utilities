package com.isnsest.denizenutilities.nms;

import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.isnsest.denizenutilities.extensions.helpers.BiomeHelper;
import org.bukkit.Bukkit;

public abstract class NMSHandler {

    public static NMSHandler instance;

    public static Object entityHelper;
    public static BiomeHelper biomeHelper;

    public static boolean initialize() {
        String bukkitVersion = Bukkit.getBukkitVersion();
        String packageVersion;

        if (bukkitVersion.contains("26.1")) {
            packageVersion = "v26_1";
        } else if (bukkitVersion.contains("1.21")) {
            packageVersion = "v1_21";
        } else {
            Debug.log("denizen-utilities", "This server version is not supported!");
            return false;
        }

        try {
            Class<?> clazz = Class.forName("com.isnsest.denizenutilities.nms." + packageVersion + ".Handler");

            if (NMSHandler.class.isAssignableFrom(clazz)) {
                instance = (NMSHandler) clazz.getDeclaredConstructor().newInstance();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public abstract void patchEntityHelper();
}
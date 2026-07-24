package com.isnsest.denizenutilities;

import com.denizenscript.denizen.paper.PaperModule;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

public abstract class Compatibility {

    private static Compatibility instance;

    public static void init() {
        try {
            Class<?> clazz = Class.forName("com.isnsest.denizenutilities.CompatibilityImpl");
            Object obj = clazz.getDeclaredConstructor().newInstance();

            clazz.getMethod("init").invoke(obj);
            instance = (Compatibility) obj;
        } catch (Exception ignored) {}
    }

    public static Compatibility get() {
        return instance;
    }

    public abstract Component parse(String text);

    public static Component defaultParse(String text) {
        return PaperModule.parseFormattedText(text, ChatColor.WHITE);
    }
}
package com.isnsest.denizenutilities.bridges.BetterModel;

import kr.toxicity.model.api.animation.AnimationIterator;
import kr.toxicity.model.api.bone.RenderedBone;
import kr.toxicity.model.api.util.TransformedItemStack;

import java.lang.reflect.Field;

public class BetterModelUtils {

    private static final Field ITEM_STACK_FIELD;

    static {
        Field f = null;
        try {
            f = RenderedBone.class.getDeclaredField("itemStack");
            f.setAccessible(true);
        } catch (NoSuchFieldException ignored) {}
        ITEM_STACK_FIELD = f;
    }

    public static TransformedItemStack getTransform(RenderedBone bone) {
        try {
            if (ITEM_STACK_FIELD != null) {
                return (TransformedItemStack) ITEM_STACK_FIELD.get(bone);
            }
        } catch (IllegalAccessException ignored) {}
        return TransformedItemStack.empty();
    }

    public static AnimationIterator.Type parseLoop(String mode) {
        return switch (mode.toUpperCase()) {
            case "LOOP" -> AnimationIterator.Type.LOOP;
            case "HOLD", "HOLD_ON_LAST" -> AnimationIterator.Type.HOLD_ON_LAST;
            default -> AnimationIterator.Type.PLAY_ONCE;
        };
    }

}

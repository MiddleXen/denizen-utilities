package com.isnsest.denizenutilities.bridges.BetterModel;

import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.bone.RenderedBone;
import kr.toxicity.model.api.tracker.EntityTracker;
import kr.toxicity.model.api.tracker.EntityTrackerRegistry;
import kr.toxicity.model.api.util.TransformedItemStack;
import org.bukkit.entity.Entity;

import java.lang.reflect.Field;
import java.util.Optional;

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

    public static void remove(Entity entity, String modelId) {
        Optional<EntityTrackerRegistry> registry = BetterModel.registry(entity.getUniqueId());
        if (registry.isPresent()) {
            EntityTracker tracker = registry.get().tracker(modelId);
            if (tracker != null) {
                tracker.close();
            }
        }
    }

}

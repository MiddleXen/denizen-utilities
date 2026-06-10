package com.isnsest.denizenutilities.bridges.BetterModel;

import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.isnsest.denizenutilities.DenizenUtilities;
import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.animation.AnimationIterator;
import kr.toxicity.model.api.bone.BoneRenderContext;
import kr.toxicity.model.api.bone.RenderedBone;
import kr.toxicity.model.api.bukkit.platform.BukkitAdapter;
import kr.toxicity.model.api.data.renderer.RenderSource;
import kr.toxicity.model.api.profile.ModelProfile;
import kr.toxicity.model.api.skin.SkinData;
import kr.toxicity.model.api.tracker.Tracker;
import kr.toxicity.model.api.util.TransformedItemStack;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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

    //
     //
    //

    public static void changeSkin(@NotNull Tracker tracker, @NotNull ObjectTag object) {
        changeSkin(tracker, object, null);
    }

    public static void changeSkin(@NotNull Tracker tracker, @NotNull ObjectTag object, @Nullable RenderedBone bone) {
        ModelProfile.Uncompleted uncompleted = null;

        if (object instanceof PlayerTag player) {
            uncompleted = ModelProfile.of(BukkitAdapter.adapt(player.getPlayerEntity())).asUncompleted();
        } else {
            try {
                UUID uuid = UUID.fromString(object.toString());
                uncompleted = ModelProfile.of(uuid);
            } catch (Exception ignored) {
                // Ignored.
            }
        }

        if (uncompleted != null) {
            CompletableFuture<? extends SkinData> future = BetterModel.platform().skinManager().complete(uncompleted);
            if (future.isDone()) {
                changeSkinWithProfile(tracker, future.join(), bone);
            } else {
                future.thenAccept(skin -> {
                    Bukkit.getScheduler().runTask(DenizenUtilities.instance, () -> changeSkinWithProfile(tracker, skin, bone));
                });
            }
        }
    }

    private static void changeSkinWithProfile(@NotNull Tracker tracker, @NotNull SkinData skinData, RenderedBone bone) {
        if (tracker.isClosed()) {
            return;
        }

        RenderSource<?> source = tracker.getPipeline().getSource();
        BoneRenderContext boneRenderContext = new BoneRenderContext(source, skinData);

        if (bone != null) {
            for (RenderedBone childBone : bone.flattenBones()) {
                childBone.updateItem(boneRenderContext);
            }
            tracker.forceUpdate(true);
            return;
        }

        for (RenderedBone _bone : tracker.bones()) {
            _bone.updateItem(boneRenderContext);
        }

        tracker.forceUpdate(true);
    }

}

package com.isnsest.denizenutilities.bridges.BetterModel.objects;

import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizen.objects.ItemTag;
import com.denizenscript.denizen.objects.LocationTag;
import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizencore.objects.Adjustable;
import com.denizenscript.denizencore.objects.Fetchable;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.*;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.tags.ObjectTagProcessor;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import com.isnsest.denizenutilities.bridges.BetterModel.BetterModelUtils;
import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.animation.RunningAnimation;
import kr.toxicity.model.api.bone.RenderedBone;
import kr.toxicity.model.api.bukkit.platform.BukkitAdapter;
import kr.toxicity.model.api.bukkit.platform.BukkitPlayer;
import kr.toxicity.model.api.platform.PlatformBillboard;
import kr.toxicity.model.api.tracker.EntityTracker;
import kr.toxicity.model.api.tracker.TrackerUpdateAction;
import kr.toxicity.model.api.util.TransformedItemStack;
import kr.toxicity.model.api.util.function.BonePredicate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.joml.Quaternionf;

import java.util.Optional;
import java.util.UUID;
import java.util.function.UnaryOperator;

import static com.isnsest.denizenutilities.bridges.BetterModel.BetterModelUtils.changeSkin;

// <--[ObjectType]
// @name BMActiveModelTag
// @prefix bmactivemodel
// @base ElementTag
// @ExampleTagBase player.model[steve]
// @format
// The identity format for a BMActiveModelTag is the UUID of the entity and the model name, separated by a slash.
//
// @plugin denizen-utilities, BetterModel
// @description
// A BMActiveModelTag represents an active BetterModel tracker instance.
// To modify specific parts, use <@link tag BMActiveModelTag.bone>.
//
// -->
public class BMActiveModelTag implements ObjectTag, Adjustable {

    @Fetchable("bmmodel")
    public static BMActiveModelTag valueOf(String string, TagContext context) {
        if (string == null || string.isEmpty()) return null;
        if (CoreUtilities.toLowerCase(string).startsWith("bmactivemodel@")) {
            string = string.substring("bmactivemodel@".length());
        }
        String[] split = string.split("/", 2);
        if (split.length < 2) return null;
        try {
            UUID uuid = UUID.fromString(split[0]);
            String modelName = split[1];
            return BetterModel.registry(uuid)
                    .flatMap(reg -> Optional.ofNullable(reg.tracker(modelName)))
                    .map(BMActiveModelTag::new)
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean matches(String input) {
        return input != null && input.startsWith("bmactivemodel@");
    }

    private final EntityTracker tracker;
    private String prefix = "bmactivemodel";

    public BMActiveModelTag(EntityTracker tracker) {
        this.tracker = tracker;
    }

    public EntityTracker getTracker() {
        return tracker;
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
        return true;
    }

    @Override
    public String identify() {
        return "bmactivemodel@" + getTracker().registry().uuid() + "/" + getTracker().name();
    }

    @Override
    public String identifySimple() {
        return identify();
    }

    @Override
    public Object getJavaObject() {
        return tracker;
    }

    @Override
    public String toString() {
        return identify();
    }

    public static final ObjectTagProcessor<BMActiveModelTag> tagProcessor = new ObjectTagProcessor<>();

    public static void register() {

        // <--[tag]
        // @attribute <BMActiveModelTag.name>
        // @returns ElementTag
        // @plugin denizen-utilities, BetterModel
        // @description
        // Returns the name of the model.
        // -->
        tagProcessor.registerTag(ElementTag.class, "name", (_, object) -> new ElementTag(object.tracker.name()));

        // <--[tag]
        // @attribute <BMActiveModelTag.bone[<bone_name>]>
        // @returns BMBoneTag
        // @plugin denizen-utilities, BetterModel
        // @description
        // Returns a specific bone from this model.
        // -->
        tagProcessor.registerTag(BMBoneTag.class, ElementTag.class, "bone", (_, object, input) -> {
            RenderedBone bone = object.tracker.bone(input.toString());
            return bone == null ? null : new BMBoneTag(object.tracker, bone);
        });

        // <--[tag]
        // @attribute <BMActiveModelTag.bones>
        // @returns MapTag
        // @plugin denizen-utilities, BetterModel
        // @description
        // Returns a list of all bones in this model tracker.
        // -->
        tagProcessor.registerTag(MapTag.class, "bones", (_, object) -> {
            MapTag mapTag = new MapTag();
            for (RenderedBone bone : object.getTracker().bones()) {
                mapTag.putObject(bone.name().name(), new BMBoneTag(object.getTracker(), bone));
            }
            return mapTag;
        });

        // <--[tag]
        // @attribute <BMActiveModelTag.type>
        // @returns ElementTag
        // @description
        // Returns the type of the model.
        // Possible values: PLAYER or GENERAL.
        // -->
        tagProcessor.registerTag(ElementTag.class, "type", (_, object) -> new ElementTag(object.tracker.getPipeline().getParent().type().name()));

        // <--[tag]
        // @attribute <BMActiveModelTag.running_animation>
        // @returns ElementTag
        // @plugin denizen-utilities, BetterModel
        // @description
        // Returns the name of the animation currently running on the model.
        // Add .type to get the loop mode (e.g., play_once, loop, hold_on_last).
        // -->
        tagProcessor.registerTag(ElementTag.class, "running_animation", (attribute, object) -> {
            RunningAnimation runningAnimation = object.tracker.getPipeline().runningAnimation();
            if (runningAnimation == null) { return null; }
            if (attribute.startsWith("type", 2)) {
                attribute.fulfill(1);
                return new ElementTag(CoreUtilities.toLowerCase(runningAnimation.type().name()));
            }
            return new ElementTag(runningAnimation.name());
        });

        // <--[tag]
        // @attribute <BMActiveModelTag.animation_duration[<animation>]>
        // @returns DurationTag
        // @plugin denizen-utilities, BetterModel
        // @description
        // Returns the total duration of the specified animation for this model.
        // -->
        tagProcessor.registerTag(DurationTag.class, ElementTag.class, "animation_duration", (_, object, input) -> {
            return object.tracker.renderer().animation(input.asString())
                    .map(anim -> new DurationTag(anim.length()))
                    .orElse(null);
        });

        // <--[tag]
        // @attribute <BMActiveModelTag.animations>
        // @returns ListTag<ElementTag>
        // @plugin denizen-utilities, BetterModel
        // @description
        // Returns a list of all available animation names configured for this model.
        // -->
        tagProcessor.registerTag(ListTag.class, "animations", (_, object) -> {
            return new ListTag(object.tracker.renderer().animations().keySet());
        });

        // <--[tag]
        // @attribute <BMActiveModelTag.viewers>
        // @returns ListTag(PlayerTag)
        // @plugin denizen-utilities, BetterModel
        // @description
        // Returns a list of all players who are currently viewing this model.
        // -->
        tagProcessor.registerTag(ListTag.class, "viewers", (_, object) -> {
            ListTag list = new ListTag();
            object.getTracker().getPipeline().viewedPlayer().forEach(handler -> {
                if (handler.player() instanceof BukkitPlayer bukkitPlayer) {
                    list.addObject(new PlayerTag(bukkitPlayer.source()));
                }
            });
            return list;
        });

        // <--[tag]
        // @attribute <BMActiveModelTag.entity>
        // @returns EntityTag
        // @plugin denizen-utilities, BetterModel
        // @description
        // Returns the underlying Bukkit entity that this active model is attached to.
        // -->
        tagProcessor.registerTag(EntityTag.class, "entity", (_, object) -> {
            Entity entity = Bukkit.getEntity(object.tracker.registry().entity().uuid());
            return new EntityTag(entity);
        });

        // --- Mechanisms ---

        // <--[mechanism]
        // @object BMActiveModelTag
        // @name billboard
        // @plugin denizen-utilities, BetterModel
        // @input ElementTag
        // @description
        // Globally sets the billboard mode for ALL bones in the model (CENTER, VERTICAL, HORIZONTAL).
        // -->
        tagProcessor.registerMechanism("billboard", false, ElementTag.class, (object, mechanism, input) -> {
            try {
                object.tracker.update(TrackerUpdateAction.billboard(PlatformBillboard.valueOf(input.asString().toUpperCase())));
            } catch (IllegalArgumentException e) {
                mechanism.echoError("Invalid billboard mode: " + input.asString());
            }
        });

        // <--[mechanism]
        // @object BMActiveModelTag
        // @name view_range
        // @plugin denizen-utilities, BetterModel
        // @input ElementTag(Decimal)
        // @description
        // Globally sets the view range for ALL bones in this model.
        // -->
        tagProcessor.registerMechanism("view_range", false, ElementTag.class, (object, mechanism, input) -> {
            if (mechanism.requireFloat()) {
                object.tracker.update(TrackerUpdateAction.viewRange(input.asFloat() * 64));
            }
        });

        // <--[mechanism]
        // @object BMActiveModelTag
        // @name glow
        // @plugin denizen-utilities, BetterModel
        // @input ElementTag(Boolean)
        // @description
        // Globally sets whether the ENTIRE model should glow.
        // -->
        tagProcessor.registerMechanism("glow", false, ElementTag.class, (object, mechanism, input) -> {
            if (mechanism.requireBoolean()) {
                object.tracker.update(TrackerUpdateAction.glow(input.asBoolean()));
            }
        });

        // <--[mechanism]
        // @object BMActiveModelTag
        // @name glow_color
        // @plugin denizen-utilities, BetterModel
        // @input ColorTag
        // @description
        // Globally sets the glow color for ALL bones in the model.
        // -->
        tagProcessor.registerMechanism("glow_color", false, ColorTag.class, (object, _, input) -> {
            int color = (input.red << 16) | (input.green << 8) | (input.blue);
            object.tracker.update(TrackerUpdateAction.glowColor(color));
        });

        // <--[mechanism]
        // @object BMActiveModelTag
        // @name tint
        // @plugin denizen-utilities, BetterModel
        // @input ColorTag
        // @description
        // Globally sets the tint color for ALL bones in the model.
        // -->
        tagProcessor.registerMechanism("tint", false, ColorTag.class, (object, _, input) -> {
            int color = (input.red << 16) | (input.green << 8) | (input.blue);
            object.tracker.update(TrackerUpdateAction.tint(color));
        });

        // <--[mechanism]
        // @object BMActiveModelTag
        // @name visible
        // @plugin denizen-utilities, BetterModel
        // @input ElementTag(Boolean)
        // @description
        // Globally toggles the visibility of the ENTIRE model.
        // -->
        tagProcessor.registerMechanism("visible", false, ElementTag.class, (object, mechanism, input) -> {
            if (mechanism.requireBoolean()) {
                object.tracker.update(TrackerUpdateAction.togglePart(input.asBoolean()));
            }
        });

        // <--[mechanism]
        // @object BMActiveModelTag
        // @name rotation
        // @plugin denizen-utilities, BetterModel
        // @input QuaternionTag
        // @description
        // Sets a custom rotation modifier for all bones.
        // -->
        tagProcessor.registerMechanism("rotation", false, QuaternionTag.class, (object, _, input) -> {
            object.tracker.getPipeline().addLocalRotModifier(BonePredicate.TRUE, _ ->
                    new Quaternionf(input.x, input.y, input.z, input.w).conjugate()
            );
        });

        // <--[mechanism]
        // @object BMActiveModelTag
        // @name item
        // @plugin denizen-utilities, BetterModel
        // @input ItemTag
        // @description
        // Globally overrides the item displayed on ALL bones in the model.
        // -->
        tagProcessor.registerMechanism("item", false, ItemTag.class, (object, _, input) -> {
            updateBones(object, t -> TransformedItemStack.of(t.position(), t.offset(), t.scale(), BukkitAdapter.adapt(input.getItemStack())));
        });

        // <--[mechanism]
        // @object BMActiveModelTag
        // @name scale
        // @plugin denizen-utilities, BetterModel
        // @input LocationTag
        // @description
        // Globally overrides the scale for ALL bones in the model.
        // -->
        tagProcessor.registerMechanism("scale", false, LocationTag.class, (object, _, input) -> {
            updateBones(object, t -> TransformedItemStack.of(t.position(), t.offset(), input.toVector().toVector3f(), t.itemStack()));
        });

        // <--[mechanism]
        // @object BMActiveModelTag
        // @name translation
        // @plugin denizen-utilities, BetterModel
        // @input LocationTag
        // @description
        // Globally overrides the translation for ALL bones in the model.
        // -->
        tagProcessor.registerMechanism("translation", false, LocationTag.class, (object, _, input) -> {
            updateBones(object, t -> TransformedItemStack.of(input.toVector().toVector3f(), t.offset(), t.scale(), t.itemStack()));
        });

        // <--[mechanism]
        // @object BMActiveModelTag
        // @name hide_from
        // @plugin denizen-utilities, BetterModel
        // @input ListTag(PlayerTag)
        // @description
        // Hides the entire model from the specified list of players.
        // -->
        tagProcessor.registerMechanism("hide_from", false, ListTag.class, (object, mechanism, input) -> {
            EntityTracker tracker = object.getTracker();
            for (PlayerTag pTag : input.filter(PlayerTag.class, mechanism.context)) {
                Player p = pTag.getPlayerEntity();
                tracker.hide(BukkitAdapter.adapt(p));
            }
        });

        // <--[mechanism]
        // @object BMActiveModelTag
        // @name show_to
        // @plugin denizen-utilities, BetterModel
        // @input ListTag(PlayerTag)
        // @description
        // Makes the model visible again to the specified list of players if it was previously hidden.
        // -->
        tagProcessor.registerMechanism("show_to", false, ListTag.class, (object, mechanism, input) -> {
            EntityTracker tracker = object.getTracker();
            for (PlayerTag pTag : input.filter(PlayerTag.class, mechanism.context)) {
                Player p = pTag.getPlayerEntity();
                tracker.show(BukkitAdapter.adapt(p));
            }
        });

        // <--[mechanism]
        // @object BMActiveModelTag
        // @name skin
        // @plugin denizen-utilities, BetterModel
        // @input ElementTag
        // @description
        // Changes the skin of the active model to the player skin associated with the specified UUID or PlayerTag.
        // This is especially useful when working with limb models.
        // -->
        tagProcessor.registerMechanism("skin", false, ObjectTag.class, (object, _, input) -> {
            changeSkin(object.tracker, input);
        });

        // <--[mechanism]
        // @object BMActiveModelTag
        // @name force_update
        // @plugin denizen-utilities, BetterModel
        // @input None
        // @description
        // Manually forces a full synchronization update (bones, metadata, and hitboxes) for the model.
        // -->
        tagProcessor.registerMechanism("force_update", false, (object, _) -> {
            object.getTracker().forceUpdate(true);
        });
    }

    private static void updateBones(BMActiveModelTag object, UnaryOperator<TransformedItemStack> mapper) {
        object.tracker.bones().forEach(bone -> {
            TransformedItemStack current = BetterModelUtils.getTransform(bone);
            bone.itemStack(b -> true, mapper.apply(current));
        });
    }

    @Override
    public ObjectTag getObjectAttribute(Attribute attribute) {
        return tagProcessor.getObjectAttribute(this, attribute);
    }

    @Override
    public void adjust(Mechanism mechanism) {
        tagProcessor.processMechanism(this, mechanism);
    }

    @Override
    public void applyProperty(Mechanism mechanism) {
        adjust(mechanism);
    }
}
package com.isnsest.denizenutilities.bridges.BetterModel.objects;

import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizen.objects.ItemTag;
import com.denizenscript.denizen.objects.LocationTag;
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
import kr.toxicity.model.api.bone.RenderedBone;
import kr.toxicity.model.api.bukkit.platform.BukkitAdapter;
import kr.toxicity.model.api.bukkit.platform.BukkitEntity;
import kr.toxicity.model.api.bukkit.platform.BukkitItemStack;
import kr.toxicity.model.api.bukkit.platform.BukkitLocation;
import kr.toxicity.model.api.nms.HitBox;
import kr.toxicity.model.api.platform.PlatformBillboard;
import kr.toxicity.model.api.tracker.EntityTracker;
import kr.toxicity.model.api.tracker.TrackerUpdateAction;
import kr.toxicity.model.api.util.TransformedItemStack;
import kr.toxicity.model.api.util.function.BonePredicate;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.isnsest.denizenutilities.bridges.BetterModel.BetterModelUtils.changeSkin;

// <--[ObjectType]
// @name BMBoneTag
// @prefix bmbone
// @base ElementTag
// @format
// The identity format for BMBoneTag is the UUID of the entity, the active model tracker name, and the bone name, separated by slashes.
// For example: 'bmbone@01234567-89ab-cdef-0123-456789abcdef/my_model/head'.
//
// @plugin denizen-utilities, BetterModel
// @description
// A BMBoneTag represents a specific bone within an active BetterModel tracker.
// This object allows manipulating rendering properties (glow, billboard, visibility),
// changing displayed items, and managing passengers if the bone is marked as a seat.
//
// -->

public class BMBoneTag implements ObjectTag, Adjustable {

    private final RenderedBone bone;
    private final EntityTracker tracker;
    public final String name;
    private final BonePredicate bonePredicate;

    private String prefix = "bmbone";

    public BMBoneTag(EntityTracker tracker, RenderedBone bone) {
        this.tracker = tracker;
        this.bone = bone;
        this.name = bone.name().name();
        this.bonePredicate = BonePredicate.name(name).withChildren();
    }

    @Fetchable("bmbone")
    public static BMBoneTag valueOf(String string, TagContext context) {
        if (string == null || string.isEmpty()) return null;
        if (string.startsWith("bmbone@")) {
            string = string.substring("bmbone@".length());
        }

        List<String> parts = CoreUtilities.split(string, '/');
        if (parts.size() < 3) return null;

        try {
            UUID uuid = UUID.fromString(parts.get(0));
            String trackerName = parts.get(1);
            String boneName = parts.get(2);
            return BetterModel.registry(uuid)
                    .flatMap(reg -> Optional.ofNullable(reg.tracker(trackerName)))
                    .flatMap(tracker -> {
                        RenderedBone bone = tracker.bone(boneName);
                        return bone != null ? Optional.of(new BMBoneTag(tracker, bone)) : Optional.empty();
                    })
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean matches(String input) {
        return input != null && input.startsWith("bmbone@");
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
        return "bmbone@" + tracker.sourceEntity().uuid() + "/" + tracker.name() + "/" + name;
    }

    @Override
    public String identifySimple() {
        return identify();
    }

    @Override
    public Object getJavaObject() {
        return bone;
    }

    @Override
    public String toString() {
        return identify();
    }

    public static final ObjectTagProcessor<BMBoneTag> tagProcessor = new ObjectTagProcessor<>();

    public static void register() {

        // <--[tag]
        // @attribute <BMBoneTag.name>
        // @returns ElementTag
        // @plugin denizen-utilities, BetterModel
        // @description
        // Returns the name of the bone.
        // -->
        tagProcessor.registerTag(ElementTag.class, "name", (_, object) -> new ElementTag(object.name));

        // <--[tag]
        // @attribute <BMBoneTag.location>
        // @returns LocationTag
        // @plugin denizen-utilities, BetterModel
        // @description
        // Returns the current world location of the bone.
        // -->
        tagProcessor.registerTag(LocationTag.class, "location", (_, object) -> {
            Vector3f relativePos = object.bone.hitBoxPosition();
            Location boneLocation = ((BukkitLocation) object.tracker.location().add(relativePos.x, relativePos.y, relativePos.z)).source();
            return new LocationTag(boneLocation);
        });

        // <--[tag]
        // @attribute <BMBoneTag.euler>
        // @returns LocationTag
        // @plugin denizen-utilities, BetterModel
        // @description
        // Returns the rotation of the bone as Euler angles.
        // -->
        tagProcessor.registerTag(LocationTag.class, "euler", (_, object) -> {
            Vector3f wR = object.bone.worldRotation();
            return new LocationTag(Vector.fromJOML(wR));
        });

        // <--[tag]
        // @attribute <BMBoneTag.passengers>
        // @returns ListTag(EntityTag)
        // @plugin denizen-utilities, BetterModel
        // @description
        // Returns a list of entities mounted on this bone.
        // -->
        tagProcessor.registerTag(ListTag.class, "passengers", (_, object) -> {
            HitBox hitBox = object.bone.getHitBox();
            if (hitBox == null) {
                return new ListTag();
            }
            Entity entity = ((BukkitEntity) hitBox.source()).source();
            ListTag list = new ListTag();
            for (Entity e : entity.getPassengers()) {
                list.addObject(new EntityTag(e));
            }
            return list;
        });

        // <--[tag]
        // @attribute <BMBoneTag.item>
        // @returns ItemTag
        // @plugin denizen-utilities, BetterModel
        // @description
        // Returns the ItemTag currently displayed on this bone.
        // -->
        tagProcessor.registerTag(ItemTag.class, "item", (_, object) -> {
            var transformed = BetterModelUtils.getTransform(object.bone).itemStack();
            var itemStack = ((BukkitItemStack) transformed).source();
            return new ItemTag(itemStack);
        });



        // --- Mechanisms ---



        // <--[mechanism]
        // @object BMBoneTag
        // @name billboard
        // @plugin denizen-utilities, BetterModel
        // @input ElementTag
        // @description
        // Sets the billboard mode for the bone. (e.g. CENTER, VERTICAL, HORIZONTAL).
        // -->
        tagProcessor.registerMechanism("billboard", false, ElementTag.class, (object, mechanism, input) -> {
            try {
                TrackerUpdateAction.Billboard action = TrackerUpdateAction.billboard(PlatformBillboard.valueOf(input.asString().toUpperCase()));
                object.tracker.update(action, object.bonePredicate);
            } catch (IllegalArgumentException e) {
                mechanism.echoError("Invalid billboard mode: " + input.asString());
            }
        });

        // <--[mechanism]
        // @object BMBoneTag
        // @name view_range
        // @plugin denizen-utilities, BetterModel
        // @input ElementTag(Decimal)
        // @description
        // Sets the view range for this bone.
        // -->
        tagProcessor.registerMechanism("view_range", false, ElementTag.class, (object, mechanism, input) -> {
            if (mechanism.requireFloat()) {
                TrackerUpdateAction.ViewRange action = TrackerUpdateAction.viewRange(input.asFloat());
                object.tracker.update(action, object.bonePredicate);
            }
        });

        // <--[mechanism]
        // @object BMBoneTag
        // @name glow
        // @plugin denizen-utilities, BetterModel
        // @input ElementTag(Boolean)
        // @description
        // Sets whether the bone should glow.
        // -->
        tagProcessor.registerMechanism("glow", false, ElementTag.class, (object, mechanism, input) -> {
            if (mechanism.requireBoolean()) {
                TrackerUpdateAction.Glow action = TrackerUpdateAction.glow(input.asBoolean());
                object.tracker.update(action, object.bonePredicate);
            }
        });

        // <--[mechanism]
        // @object BMBoneTag
        // @name glow_color
        // @plugin denizen-utilities, BetterModel
        // @input ColorTag
        // @description
        // Sets the glow color for the bone.
        // -->
        tagProcessor.registerMechanism("glow_color", false, ColorTag.class, (object, _, input) -> {
            int color = (input.red << 16) | (input.green << 8) | (input.blue);
            TrackerUpdateAction.GlowColor action = TrackerUpdateAction.glowColor(color);
            object.tracker.update(action, object.bonePredicate);
        });

        // <--[mechanism]
        // @object BMBoneTag
        // @name tint
        // @plugin denizen-utilities, BetterModel
        // @input ColorTag
        // @description
        // Sets the tint color for the bone.
        // -->
        tagProcessor.registerMechanism("tint", false, ColorTag.class, (object, _, input) -> {
            int color = (input.red << 16) | (input.green << 8) | (input.blue);
            TrackerUpdateAction.Tint action = TrackerUpdateAction.tint(color);
            object.tracker.update(action, object.bonePredicate);
        });

        // <--[mechanism]
        // @object BMBoneTag
        // @name visible
        // @plugin denizen-utilities, BetterModel
        // @input ElementTag(Boolean)
        // @description
        // Sets whether the bone is visible.
        // -->
        tagProcessor.registerMechanism("visible", false, ElementTag.class, (object, mechanism, input) -> {
            if (mechanism.requireBoolean()) {
                TrackerUpdateAction.TogglePart action = TrackerUpdateAction.togglePart(input.asBoolean());
                object.tracker.update(action, object.bonePredicate);
            }
        });

        // <--[mechanism]
        // @object BMBoneTag
        // @name rotation
        // @plugin denizen-utilities, BetterModel
        // @input QuaternionTag
        // @description
        // Sets a custom rotation modifier for the bone.
        // -->
        tagProcessor.registerMechanism("rotation", false, QuaternionTag.class, (object, _, input) -> {
            object.tracker.getPipeline().addLocalRotModifier(object.bonePredicate, _ -> {
                return new Quaternionf(input.x, input.y, input.z, input.w).conjugate();
            });
        });

        // <--[mechanism]
        // @object BMBoneTag
        // @name mount
        // @plugin denizen-utilities, BetterModel
        // @input EntityTag
        // @description
        // Mounts an entity onto this bone.
        // -->
        tagProcessor.registerMechanism("mount", false, EntityTag.class, (object, mechanism, input) -> {
            HitBox hitBox = object.bone.getHitBox();
            if (hitBox == null) {
                mechanism.echoError("Bone '" + object.name + "' does not have a hitbox/seat. Check your model tags.");
                return;
            }
            hitBox.mount(BukkitAdapter.adapt(input.getBukkitEntity()));
        });

        // <--[mechanism]
        // @object BMBoneTag
        // @name dismount
        // @plugin denizen-utilities, BetterModel
        // @input EntityTag
        // @description
        // Dismounts a specific entity from this bone.
        // -->
        tagProcessor.registerMechanism("dismount", false, EntityTag.class, (object, _, input) -> {
            HitBox hitBox = object.bone.getHitBox();
            if (hitBox != null) {
                hitBox.dismount(BukkitAdapter.adapt(input.getBukkitEntity()));
            }
        });

        // <--[mechanism]
        // @object BMBoneTag
        // @name dismount_all
        // @plugin denizen-utilities, BetterModel
        // @input None
        // @description
        // Dismounts all entities from this bone.
        // -->
        tagProcessor.registerMechanism("dismount_all", false, (object, _) -> {
            HitBox hitBox = object.bone.getHitBox();
            if (hitBox != null) {
                hitBox.dismountAll();
            }
        });

        // <--[mechanism]
        // @object BMBoneTag
        // @name item
        // @plugin denizen-utilities, BetterModel
        // @input ItemTag
        // @description
        // Changes the item display of this bone.
        // -->
        tagProcessor.registerMechanism("item", false, ItemTag.class, (object, _, input) -> {
            TransformedItemStack transformed = BetterModelUtils.getTransform(object.bone);
            TransformedItemStack result = TransformedItemStack.of(transformed.position(), transformed.offset(), transformed.scale(), BukkitAdapter.adapt(input.getItemStack()));
            object.bone.itemStack(_ -> true, result);
            object.tracker.forceUpdate(true);
        });

        // <--[mechanism]
        // @object BMBoneTag
        // @name scale
        // @plugin denizen-utilities, BetterModel
        // @input LocationTag
        // @description
        // Sets the scale of the bone's item.
        // -->
        tagProcessor.registerMechanism("scale", false, LocationTag.class, (object, _, input) -> {
            TransformedItemStack transformed = BetterModelUtils.getTransform(object.bone);
            TransformedItemStack result = TransformedItemStack.of(transformed.position(), transformed.offset(), input.toVector().toVector3f(), transformed.itemStack());
            object.bone.itemStack(_ -> true, result);
        });

        // <--[mechanism]
        // @object BMBoneTag
        // @name translation
        // @plugin denizen-utilities, BetterModel
        // @input LocationTag
        // @description
        // Sets the translation of the bone's item.
        // -->
        tagProcessor.registerMechanism("translation", false, LocationTag.class, (object, _, input) -> {
            TransformedItemStack transformed = BetterModelUtils.getTransform(object.bone);
            TransformedItemStack result = TransformedItemStack.of(input.toVector().toVector3f(), transformed.offset(), transformed.scale(), transformed.itemStack());
            object.bone.itemStack(_ -> true, result);
        });

        // <--[mechanism]
        // @object BMBoneTag
        // @name brightness
        // @plugin denizen-utilities, BetterModel
        // @input MapTag
        // @description
        // A map of the bone's display entity's brightness override, containing "block" and "sky" keys, each with a brightness level between 0 and 15.
        // -->
        tagProcessor.registerMechanism("brightness", false, MapTag.class, (object, mechanism, input) -> {
            int block = input.getElement("block", "-1").asInt();
            int sky = input.getElement("sky", "-1").asInt();
            if (block < 0 || block > 15) {
                mechanism.echoError("Invalid 'block' brightness, must be a number between 0 and 15.");
                return;
            }
            if (sky < 0 || sky > 15) {
                mechanism.echoError("Invalid 'sky' brightness, must be a number between 0 and 15.");
                return;
            }
            TrackerUpdateAction.Brightness action = TrackerUpdateAction.brightness(block, sky);
            object.tracker.update(action, object.bonePredicate);
        });

        // <--[mechanism]
        // @object BMBoneTag
        // @name skin
        // @plugin denizen-utilities, BetterModel
        // @input ElementTag
        // @description
        // Changes the skin of the specified bone to the player skin associated with the specified UUID or PlayerTag.
        // This is especially useful when working with limb models.
        // -->
        tagProcessor.registerMechanism("skin", false, ObjectTag.class, (object, _, input) -> {
            changeSkin(object.tracker, input, object.bone);
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
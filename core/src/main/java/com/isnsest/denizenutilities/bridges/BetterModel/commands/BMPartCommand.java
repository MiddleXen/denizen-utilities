package com.isnsest.denizenutilities.bridges.BetterModel.commands;

import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;
import com.denizenscript.denizencore.scripts.commands.generator.ArgName;
import com.denizenscript.denizencore.scripts.commands.generator.ArgPrefixed;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.bone.BoneRenderContext;
import kr.toxicity.model.api.bone.RenderedBone;
import kr.toxicity.model.api.bukkit.platform.BukkitAdapter;
import kr.toxicity.model.api.data.renderer.RenderSource;
import kr.toxicity.model.api.player.PlayerLimb;
import kr.toxicity.model.api.tracker.EntityTracker;
import com.isnsest.denizenutilities.bridges.BetterModel.objects.BMActiveModelTag;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class BMPartCommand extends AbstractCommand {

    // <--[command]
    // @Name bmpart
    // @Syntax bmpart [model:<BMModelTag>] [bone:<bone>] [part:<limb_name>] [from:<player>]
    // @Required 4
    // @Maximum 4
    // @Short Applies a skin limb from an online player to a specific model bone.
    // @Group denizen-utilities
    // @Plugin denizen-utilities, BetterModel
    //
    // @Description
    // Applies a specific limb texture from a player's skin to a bone in your BetterModel.
    // The 'bone' is the destination bone in your model (e.g., head, chest, waist, right_arm).
    // The 'part' is the source limb taken from the player's skin. You can find the list of valid limb names
    // by checking the template player models provided in your 'BetterModel/players' folder.
    // The 'from' argument must be an online player who provides the skin source.
    //
    // @Usage
    // Use to apply the current player's head skin to the model's head bone.
    // - bmpart model:<[my_model]> bone:head part:HEAD from:<player>
    //
    // @Usage
    // Use to apply a specific online player's torso skin to the model's chest bone.
    // - bmpart model:<[my_model]> bone:chest part:TORSO from:<player[PlayerName]>
    // -->

    public BMPartCommand() {
        setName("bmpart");
        setSyntax("bmpart [model:<model>] [bone:<bone>] [part:<limb_name>] [from:<player>]");
        setRequiredArguments(4, 4);
        autoCompile();
    }

    public void addCustomTabCompletions(TabCompletionsBuilder tab) {
        tab.addWithPrefix("part:", Arrays.stream(PlayerLimb.values())
                .map(limb -> limb.name().toLowerCase())
                .toList());
    }

    public static void autoExecute(ScriptEntry scriptEntry,
                                   @ArgName("model") @ArgPrefixed BMActiveModelTag model,
                                   @ArgName("bone") @ArgPrefixed ElementTag boneName,
                                   @ArgName("part") @ArgPrefixed ElementTag partName,
                                   @ArgName("from") @ArgPrefixed PlayerTag skinSource) {

        Player player = skinSource.getPlayerEntity();
        if (player == null) return;

        EntityTracker tracker = model.getTracker();
        if (tracker == null) {
            Debug.echoError("Model not found!");
            return;
        }

        RenderedBone bone = tracker.bone(boneName.asString());
        if (bone == null) {
            Debug.echoError("Bone '" + boneName.asString() + "' not found!");
            return;
        }

        PlayerLimb targetLimb;
        try {
            targetLimb = PlayerLimb.valueOf(partName.asString().toUpperCase());
        } catch (Exception e) {
            Debug.echoError("Invalid part name: " + partName.asString());
            return;
        }

        var api = BetterModel.platform();
        var profile = api.nms().profile(BukkitAdapter.adapt(player));

        api.skinManager().complete(profile.asUncompleted()).thenAccept(skinData -> {
            Bukkit.getScheduler().runTask(JavaPlugin.getProvidingPlugin(BMPartCommand.class), () -> {

                var adaptedSource = api.nms().adapt(BukkitAdapter.adapt(player));
                BoneRenderContext ctx = new BoneRenderContext(RenderSource.of(adaptedSource), skinData);

                bone.setItemMapper(targetLimb.getItemMapper());
                bone.updateItem(ctx);

                tracker.forceUpdate(true);
                Debug.echoDebug(scriptEntry, "Successfully applied skin!");
            });
        });
    }
}
package com.isnsest.denizenutilities.bridges.SkinsRestorer.commands;

import com.denizenscript.denizen.utilities.Utilities;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.commands.generator.*;
import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.isnsest.denizenutilities.bridges.SkinsRestorer.SkinsRestorerUtils;
import net.skinsrestorer.api.property.SkinIdentifier;
import net.skinsrestorer.api.property.SkinProperty;
import org.bukkit.entity.Player;

import java.util.List;

public class SkinCommand extends AbstractCommand {

    public SkinCommand() {
        setName("skin");
        setSyntax("skin [<name>/<url>/<texture>] (<player>|...)");
        setRequiredArguments(1, 2);
        isProcedural = false;
        autoCompile();
        registerTags();
    }

    // <--[command]
    // @Name skin
    // @Syntax skin [<name>/<url>/<texture>] (<player>|...)
    // @Required 1
    // @Maximum 2
    // @Short Sets a player's skin using SkinsRestorer.
    // @Group Player
    //
    // @Description
    // Changes the skin of the specified player(s).
    // You can provide a skin name, a URL,
    // or raw texture data in 'value;signature' format.
    // If no targets are specified, the player attached to the script queue will be used.
    //
    // @Usage
    // Use to set the current player's skin by name.
    // - skin Notch
    //
    // @Usage
    // Use to set a specific player's skin by URL.
    // - skin https://minesk.in/7db... targets:<[some_player]>
    //
    // @Usage
    // Use to set a skin for multiple players using raw texture data.
    // - skin <player.skin_blob> targets:<server.online_players>
    // -->

    public static void autoExecute(ScriptEntry scriptEntry,
                                   @ArgName("skin") @ArgLinear String skin,
                                   @ArgName("targets") @ArgLinear @ArgDefaultNull @ArgSubType(PlayerTag.class) List<PlayerTag> targets) {
        if (targets == null) {
            if (!Utilities.entryHasPlayer(scriptEntry)) {
                Debug.echoError("Must specify target(s).");
                return;
            }
             targets = List.of(Utilities.getEntryPlayer(scriptEntry));
        }

        for (PlayerTag playerTag : targets) {
            Player player = playerTag.getPlayerEntity();
            if (skin.startsWith("http")) {
                SkinsRestorerUtils.setSkinFromUrl(player, skin);
            } else if (skin.contains(";")) {
                SkinsRestorerUtils.setSkinByTexture(player, skin);
            } else {
                SkinsRestorerUtils.setSkinByName(player, skin);
            }
        }
    }

    public static void registerTags() {
        // <--[tag]
        // @attribute <PlayerTag.skin_url>
        // @returns ElementTag
        // @description
        // Returns the URL of the player's current skin.
        // -->
        PlayerTag.tagProcessor.registerTag(ElementTag.class, "skin_url", (attribute, object) -> {
            SkinProperty property = SkinsRestorerUtils.getPlayerSkin(object.getPlayerEntity());
            return new ElementTag(SkinsRestorerUtils.getTextureUrl(property));
        });

        // <--[tag]
        // @attribute <PlayerTag.skin_type>
        // @returns ElementTag
        // @description
        // Returns the skin type of the player.
        // CUSTOM: Skin linked to a custom value and signature.
        // LEGACY: Skin linked to an old value and signature from pre-v15 versions.
        // PLAYER: Skin linked to a player by UUID.
        // URL: Skin linked to a URL (cannot be updated).
        // -->
        PlayerTag.tagProcessor.registerTag(ElementTag.class, "skin_type", (attribute, object) -> {
            SkinIdentifier identifier = SkinIdentifier.ofPlayer(object.getUUID());
            return new ElementTag(identifier.getSkinType());
        });
    }
}
package com.isnsest.denizenutilities.bridges.SkinsRestorer;

import com.denizenscript.denizencore.utilities.debugging.Debug;
import net.skinsrestorer.api.PropertyUtils;
import net.skinsrestorer.api.connections.MineSkinAPI;
import net.skinsrestorer.api.connections.model.MineSkinResponse;
import net.skinsrestorer.api.exception.DataRequestException;
import net.skinsrestorer.api.exception.MineSkinException;
import net.skinsrestorer.api.property.InputDataResult;
import net.skinsrestorer.api.property.SkinProperty;
import net.skinsrestorer.api.property.SkinVariant;
import net.skinsrestorer.api.storage.PlayerStorage;
import net.skinsrestorer.api.storage.SkinStorage;
import org.bukkit.entity.Player;

import java.util.Optional;

import static com.isnsest.denizenutilities.bridges.SkinsRestorer.SkinsRestorerBridge.getAPI;

public class SkinsRestorerUtils {

    public static InputDataResult getSkinByName(String skinName) {
        SkinStorage skinStorage = getAPI().getSkinStorage();
        Optional<InputDataResult> result = Optional.empty();
        try {
            result = skinStorage.findOrCreateSkinData(skinName);
        }
        catch (DataRequestException | MineSkinException e) {
            Debug.echoError("Failed to fetch skin: " + e.getMessage());
        }
        return result.orElse(null);
    }

    public static SkinProperty getSkinFromUrl(String url) {
        MineSkinAPI mineSkinAPI = getAPI().getMineSkinAPI();
        MineSkinResponse response = null;
        try {
            response = mineSkinAPI.genSkin(url, SkinVariant.CLASSIC);
        } catch (DataRequestException | MineSkinException e) {
            Debug.echoError("Failed to fetch skin: " + e.getMessage());
        }
        return response != null ? response.getProperty() : null;
    }

    public static void setSkinByName(Player player, String skinName) {
        PlayerStorage playerStorage = getAPI().getPlayerStorage();
        InputDataResult result = getSkinByName(skinName);
        playerStorage.setSkinIdOfPlayer(
                player.getUniqueId(),
                result.getIdentifier()
        );
        try {
            getAPI().getSkinApplier(Player.class).applySkin(player);
        } catch (DataRequestException e) {
            Debug.echoError("Failed to fetch skin: " + e.getMessage());
        }
    }

    public static void setSkinFromUrl(Player player, String url) {
        SkinProperty skinProperty = getSkinFromUrl(url);
        getAPI().getSkinApplier(Player.class).applySkin(player, skinProperty);
    }

    public static void setSkinByTexture(Player player, String texture) {
        String[] data = texture.split(";");
        SkinProperty skinProperty = SkinProperty.of(data[0], data[1]);
        getAPI().getSkinApplier(Player.class).applySkin(player, skinProperty);
    }

    public static SkinProperty getPlayerSkin(Player player) {
        PlayerStorage playerStorage = getAPI().getPlayerStorage();
        Optional<SkinProperty> property = Optional.empty();
        try {
            property = playerStorage.getSkinForPlayer(
                    player.getUniqueId(),
                    player.getName()
            );
        } catch (DataRequestException e) {
            Debug.echoError("Failed to fetch skin: " + e.getMessage());
        }
        return property.orElse(null);
    }

    public static String getTextureUrl(SkinProperty skinProperty) {
        return PropertyUtils.getSkinTextureUrl(skinProperty);
    }

}

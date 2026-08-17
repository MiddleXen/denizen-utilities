package com.isnsest.denizenutilities.bridges.viaversion;

import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.isnsest.denizenutilities.bridges.BridgeModule;
import com.isnsest.denizenutilities.extensions.objects.ConnectionTag;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;

public class ViaVersionModule implements BridgeModule {

    @Override
    public String getPluginName() {
        return "ViaVersion";
    }

    @Override
    public void register() {
        registerTags();
        Debug.log("denizen-utilities", "ViaVersion bridge initialized.");
    }

    private static void registerTags() {
        // <--[tag]
        // @attribute <ConnectionTag.viaversion_protocol>
        // @returns ElementTag(Number)
        // @plugin denizen-utilities, ViaVersion
        // @description
        // Returns the protocol version number of the player's client.
        // See <@link url https://wiki.vg/Protocol_version_numbers> as a reference list.
        // See also <@link tag ConnectionTag.viaversion_version>
        // -->
        ConnectionTag.tagProcessor.registerTag(ElementTag.class, "viaversion_protocol", (_, object) ->
                new ElementTag(Via.getAPI().getPlayerVersion(object.getUUID())));

        // <--[tag]
        // @attribute <ConnectionTag.viaversion_version>
        // @returns ElementTag
        // @plugin denizen-utilities, ViaVersion
        // @description
        // Returns the player's client version ("1.19.4", "1.18.2"...).
        // See also <@link tag ConnectionTag.viaversion_protocol>
        // -->
        ConnectionTag.tagProcessor.registerTag(ElementTag.class, "viaversion_version", (_, object) -> {
            int version = Via.getAPI().getPlayerVersion(object.getUUID());
            return new ElementTag(ProtocolVersion.getProtocol(version).getName(), true);
        });
    }
}
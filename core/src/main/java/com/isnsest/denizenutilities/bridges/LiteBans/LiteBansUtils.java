package com.isnsest.denizenutilities.bridges.LiteBans;

import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import litebans.api.Entry;

import java.util.UUID;

public class LiteBansUtils {

    public static PlayerTag getPlayerTag(Entry entry) {
        String uuid = entry.getUuid();
        if (uuid == null || uuid.isEmpty()) {
            return null;
        }
        return new PlayerTag(UUID.fromString(uuid));
    }

    public static ObjectTag getEntryContext(Entry entry, String name) {
        return switch (name) {
            case "reason" -> new ElementTag(entry.getReason());
            case "executor" -> new ElementTag(entry.getExecutorName());
            case "executor_uuid" -> entry.getExecutorUUID() == null ? null : new ElementTag(entry.getExecutorUUID().toString());
            case "duration" -> new ElementTag(entry.getDuration());
            case "duration_string" -> new ElementTag(entry.getDurationString());
            case "permanent" -> new ElementTag(entry.isPermanent());
            case "ip" -> entry.getIp() == null ? null : new ElementTag(entry.getIp());
            case "ip_ban" -> new ElementTag(entry.isIpban());
            case "server_scope" -> new ElementTag(entry.getServerScope());
            case "id" -> new ElementTag(entry.getId());
            case "uuid" -> entry.getUuid() == null ? null : new ElementTag(entry.getUuid());
            case "type" -> new ElementTag(entry.getType());
            case "random_id" -> new ElementTag(entry.getRandomID());
            case "template_name" -> new ElementTag(entry.getTemplateName());
            case "has_template" -> new ElementTag(entry.hasTemplate());
            default -> null;
        };
    }

    public static ObjectTag getRemovalContext(Entry entry, String name) {
        return switch (name) {
            case "removed_by" -> new ElementTag(entry.getRemovedByName());
            case "removal_reason" -> entry.getRemovalReason() == null ? null : new ElementTag(entry.getRemovalReason());
            default -> null;
        };
    }
}
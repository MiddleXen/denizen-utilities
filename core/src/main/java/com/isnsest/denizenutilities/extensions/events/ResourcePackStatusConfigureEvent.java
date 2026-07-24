package com.isnsest.denizenutilities.extensions.events;

import com.denizenscript.denizencore.events.ScriptEvent;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.isnsest.denizenutilities.extensions.objects.ConnectionTag;
import io.papermc.paper.connection.PlayerConfigurationConnection;

public class ResourcePackStatusConfigureEvent extends ScriptEvent {

    // <--[event]
    // @Events
    // connection resource pack status
    //
    // @Group denizen-utilities
    //
    // @Triggers когда завершается загрузка (успехом или ошибкой) ресурс-пака на этапе конфигурации.
    //
    // @Context
    // <context.connection> возвращает ConnectionTag.
    // <context.status> возвращает ElementTag ('SUCCESS' или 'FAILED').
    //
    // @Determine
    // "KICK:" + ElementTag чтобы кикнуть игрока с сервера.
    //
    // @Plugin denizen-utilities
    //
    // -->

    public static ResourcePackStatusConfigureEvent instance;

    public PlayerConfigurationConnection connection;
    public String status;

    public ResourcePackStatusConfigureEvent() {
        instance = this;
        registerCouldMatcher("connection resource pack status");
    }

    @Override
    public ObjectTag getContext(String name) {
        return switch (name) {
            case "connection" -> new ConnectionTag(connection);
            case "status" -> new ElementTag(status);
            default -> super.getContext(name);
        };
    }
}
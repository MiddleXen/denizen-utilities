package com.isnsest.denizenutilities.extensions.events;

import com.denizenscript.denizencore.events.ScriptEvent;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.JavaReflectedObjectTag;
import com.destroystokyo.paper.event.player.PlayerConnectionCloseEvent;
import com.isnsest.denizenutilities.DenizenUtilities;
import com.isnsest.denizenutilities.extensions.objects.ConnectionTag;
import io.papermc.paper.connection.PlayerConfigurationConnection;
import io.papermc.paper.event.connection.configuration.AsyncPlayerConnectionConfigureEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class PlayerConnectionConfigureEvent extends ScriptEvent implements Listener {

    // <--[event]
    // @Events
    // player connection configure
    //
    // @Group Paper
    //
    // @Triggers when a player's connection is being configured (Paper specific).
    //
    // @Context
    // <context.connection> returns the ConnectionTag.
    //
    // @Determine
    // "WAIT" to delay the configuration process for up to 1 minute.
    //
    // @Plugin denizen-utilities, Paper
    //
    // -->

    public static final Map<UUID, CompletableFuture<Boolean>> awaitingResponse = new ConcurrentHashMap<>();

    public AsyncPlayerConnectionConfigureEvent event;
    public String determination = null;

    public PlayerConnectionConfigureEvent() {
        registerCouldMatcher("player connection configure");
        this.<PlayerConnectionConfigureEvent, ObjectTag>registerDetermination(null, ObjectTag.class, (evt, _, output) -> {
            evt.determination = output.toString();
        });
    }

    @Override
    public void init() {
        Bukkit.getPluginManager().registerEvents(this, DenizenUtilities.instance);
    }

    @Override
    public void destroy() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public ObjectTag getContext(String name) {
        return switch (name) {
            case "connection" -> new ConnectionTag(event.getConnection());
            case "reflect_event" -> new JavaReflectedObjectTag(event);
            default -> super.getContext(name);
        };
    }

    @EventHandler
    public void onPlayerConfigure(AsyncPlayerConnectionConfigureEvent event) {
        PlayerConfigurationConnection connection = event.getConnection();
        UUID uniqueId = connection.getProfile().getId();
        if (uniqueId == null) {
            return;
        }

        ConnectionTag.activeConnections.put(uniqueId, event.getConnection());

        PlayerConnectionConfigureEvent altEvent = (PlayerConnectionConfigureEvent) this.clone();

        altEvent.event = event;
        altEvent.determination = null;
        String determination = ((PlayerConnectionConfigureEvent) altEvent.fire()).determination;

        if ("WAIT".equalsIgnoreCase(determination)) {
            CompletableFuture<Boolean> response = new CompletableFuture<>();
            response.completeOnTimeout(false, 1, TimeUnit.MINUTES);

            awaitingResponse.put(uniqueId, response);

            Audience audience = connection.getAudience();

            if (!response.join()) {
                audience.closeDialog();
                connection.disconnect(Component.empty());
            }

            awaitingResponse.remove(uniqueId);
        }
    }

    @EventHandler
    void onConnectionClose(PlayerConnectionCloseEvent event) {
        awaitingResponse.remove(event.getPlayerUniqueId());
    }
}

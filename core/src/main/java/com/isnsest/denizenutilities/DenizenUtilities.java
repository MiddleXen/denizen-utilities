package com.isnsest.denizenutilities;

import com.denizenscript.denizencore.DenizenCore;
import com.denizenscript.denizencore.events.ScriptEvent;
import com.denizenscript.denizencore.objects.ObjectFetcher;
import com.isnsest.denizenutilities.extensions.commands.ShowDialogCommand;
import com.isnsest.denizenutilities.extensions.containers.DialogScriptContainer;
import com.isnsest.denizenutilities.extensions.events.PlayerConnectionConfigureEvent;
import com.isnsest.denizenutilities.extensions.events.PlayerCustomClickScriptEvent;
import com.isnsest.denizenutilities.extensions.objects.ConnectionTag;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import com.isnsest.denizenutilities.nms.NMSHandler;
import com.denizenscript.denizencore.scripts.ScriptRegistry;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.isnsest.denizenutilities.extensions.properties.BiomeExtensions;
import com.isnsest.denizenutilities.extensions.properties.PlayerExtensions;

import java.util.List;

public class DenizenUtilities extends JavaPlugin {

    public static DenizenUtilities instance;

    Metrics metrics = new Metrics(this, 29915);

    private void register() {
        PlayerExtensions.register();
        BiomeExtensions.register();

        ScriptRegistry._registerType("dialog", DialogScriptContainer.class);

        // Commands
        DenizenCore.commandRegistry.registerCommand(ShowDialogCommand.class);
        //

        // Events
        ScriptEvent.registerScriptEvent(PlayerConnectionConfigureEvent.class);
        ScriptEvent.registerScriptEvent(PlayerCustomClickScriptEvent.class);
        //

        // Objects
        ObjectFetcher.registerWithObjectFetcher(ConnectionTag.class, ConnectionTag.tagProcessor).setAsNOtherCode();
        //
    }

    private void registerBridges() {
        List<String> bridges = List.of("SkinsRestorer", "BetterModel", "DiscordSRV", "LiteBans");
        for (String name : bridges) {
            if (Bukkit.getPluginManager().isPluginEnabled(name)) {
                try {
                    String className = "com.isnsest.denizenutilities.bridges." + name + "." + name + "Bridge";
                    Class<?> clazz = Class.forName(className);

                    clazz.getDeclaredMethod("register").invoke(null);
                } catch (Exception ignored) {
                }
            }
        }
    }

    private void registerMetrics() {
        metrics.addCustomChart(new Metrics.SimplePie("Denizen", () -> {
            var plugin = Bukkit.getPluginManager().getPlugin("Denizen");
            return plugin != null ? plugin.getPluginMeta().getVersion() : null;
        }));

        metrics.addCustomChart(new Metrics.SimplePie("dDiscordBot", () -> {
            var plugin = Bukkit.getPluginManager().getPlugin("dDiscordBot");
            return plugin != null ? plugin.getPluginMeta().getVersion() : null;
        }));
    }

    @Override
    public void onEnable() {
        instance = this;

        Debug.log("denizen-utilities", "Loading...");
        saveDefaultConfig();

        Compatibility.init();

        if (NMSHandler.initialize()) {
            if (getConfig().getBoolean("fixes.fakeinternaldata", false)) {
                NMSHandler.instance.patchEntityHelper();
            }
        }

        register();
        registerBridges();
        registerMetrics();

        Debug.log("denizen-utilities", "Loaded successfully!");
    }
}
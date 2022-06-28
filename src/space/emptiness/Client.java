/*
 * Decompiled with CFR 0_132.
 */
package space.emptiness;

import org.lwjgl.opengl.Display;
import space.emptiness.command.CommandManager;
import space.emptiness.events.EventManager;
import space.emptiness.gui.login.AltManager;
import space.emptiness.management.FileManager;
import space.emptiness.management.FriendManager;
import space.emptiness.module.Module;
import space.emptiness.module.ModuleManager;
import space.emptiness.module.modules.visual.UI.TabUI;
import space.emptiness.module.value.Value;

import java.awt.*;

public class Client {
    public static final String name = "Windy";
    public static final String version = "1.0";
    public static final String dev = "Windy Dev Group";
    public static boolean publicMode = false;
    public static Client instance = new Client();
    private static ModuleManager modulemanager;
    private CommandManager commandmanager;
    private AltManager altmanager;
    private FriendManager friendmanager;
    private TabUI tabui;
    public static long playTimeStart = 0;
    //public static ResourceLocation CLIENT_CAPE = new ResourceLocation("ETB/cape.png");

    public void initiate() {
        this.commandmanager = new CommandManager();
        this.commandmanager.init();
        this.friendmanager = new FriendManager();
        this.friendmanager.init();
        EventManager.register(this);
        this.modulemanager = new ModuleManager();
        this.modulemanager.init();
        this.tabui = new TabUI();
        this.tabui.init();
        this.altmanager = new AltManager();
        AltManager.init();
        AltManager.setupAlts();
        FileManager.init();
        Display.setTitle(name + " " + version + " " + "by " + dev);
    }

    public static ModuleManager getModuleManager() {
        return modulemanager;
    }

    public CommandManager getCommandManager() {
        return this.commandmanager;
    }

    public AltManager getAltManager() {
        return this.altmanager;
    }
    public final Color getClientColor() {
        return new Color(236, 133, 209);
    }
    public final Color getAlternateClientColor() {
        return new Color(28, 167, 222);
    }

    public void shutDown() {
        String values = "";
        instance.getModuleManager();
        for (Module m : ModuleManager.getModules()) {
            for (Value v : m.getValues()) {
                values = String.valueOf(values) + String.format("%s:%s:%s%s", m.getName(), v.getName(), v.getValue(), System.lineSeparator());
            }
        }
        FileManager.save("Values.txt", values, false);
        String enabled = "";
        instance.getModuleManager();
        for (Module m : ModuleManager.getModules()) {
            if (!m.isEnabled()) continue;
            enabled = String.valueOf(enabled) + String.format("%s%s", m.getName(), System.lineSeparator());
        }
        FileManager.save("Enabled.txt", enabled, false);
    }
}


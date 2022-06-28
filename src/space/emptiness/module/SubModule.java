package space.emptiness.module;

import net.minecraft.client.Minecraft;
import space.emptiness.events.EventManager;

public class SubModule {
    private String name, parentModName;
    private boolean isEnabled;
    public static Minecraft mc = Minecraft.getMinecraft();

    public SubModule(String name, String mainmodule) {
        this.name = name;
        this.parentModName = mainmodule;
    }

    public void onEnable() {
        EventManager.register(this);
    }

    public void onDisable() {
        EventManager.unregister(this);
    }

    public void setEnabled(boolean enabled) {
        if (enabled) {
            this.isEnabled = true;
            this.onEnable();
        } else {
            this.isEnabled = false;
            this.onDisable();
        }
    }

    public String getName() {
        return null;
    }
}

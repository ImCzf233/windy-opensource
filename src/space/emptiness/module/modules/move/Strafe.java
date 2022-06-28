package space.emptiness.module.modules.move;

import space.emptiness.events.EventTarget;
import space.emptiness.events.world.EventUpdate;
import space.emptiness.module.Category;
import space.emptiness.module.Module;
import space.emptiness.utils.PlayerUtil;
import space.emptiness.Client;

public class Strafe extends Module {

    public Strafe() {
        super("Strafe",new String[]{}, Category.Move);
    }

    @EventTarget
    void onUpdate(EventUpdate event) {
        if (
                Client.instance.getModuleManager().getModuleByName("Speed").isEnabled()) return;
        PlayerUtil.doStrafe();
    }
}

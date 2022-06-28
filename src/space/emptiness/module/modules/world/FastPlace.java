/*
 * Decompiled with CFR 0_132.
 */
package space.emptiness.module.modules.world;

import space.emptiness.events.EventTarget;
import space.emptiness.events.world.EventTick;
import space.emptiness.module.Category;
import space.emptiness.module.Module;

import java.awt.*;

public class FastPlace
extends Module {
    public FastPlace() {
        super("FastPlace", new String[]{"fplace", "fc"}, Category.World);
        this.setColor(new Color(226, 197, 78).getRGB());
    }

    @EventTarget
    private void onTick(EventTick e) {
        this.mc.rightClickDelayTimer = 0;
    }
}


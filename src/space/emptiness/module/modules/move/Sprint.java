/*
 * Decompiled with CFR 0_132.
 */
package space.emptiness.module.modules.move;

import space.emptiness.events.EventTarget;
import space.emptiness.events.world.EventUpdate;
import space.emptiness.module.Category;
import space.emptiness.module.Module;
import space.emptiness.module.value.Option;

import java.awt.*;

public class Sprint
extends Module {
    private Option<Boolean> omni = new Option<Boolean>("Omni-Directional", "omni", true);

    public Sprint() {
        super("Sprint", new String[]{"run"}, Category.Move);
        this.setColor(new Color(158, 205, 125).getRGB());
        this.addValues(this.omni);
        this.setEnabled(true);
        this.setRemoved(true);
    }

    @EventTarget
    private void onUpdate(EventUpdate event) {
        if (this.mc.thePlayer.getFoodStats().getFoodLevel() > 6 && this.omni.getValue() != false ? this.mc.thePlayer.moving() : this.mc.thePlayer.moveForward > 0.0f) {
            this.mc.thePlayer.setSprinting(true);
        }
    }
}


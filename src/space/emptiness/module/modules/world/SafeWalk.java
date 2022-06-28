/*
 * Decompiled with CFR 0_132.
 */
package space.emptiness.module.modules.world;

import space.emptiness.module.Category;
import space.emptiness.module.Module;

import java.awt.Color;

public class SafeWalk
extends Module {
    public SafeWalk() {
        super("SafeWalk", new String[]{"eagle", "parkour"}, Category.World);
        this.setColor(new Color(198, 253, 191).getRGB());
    }
}


/*
 * Decompiled with CFR 0_132.
 */
package space.emptiness.module.modules.visual;

import space.emptiness.module.Category;
import space.emptiness.module.Module;

import java.awt.Color;

public class NoRender
extends Module {
    public NoRender() {
        super("NoRender", new String[]{"noitems"}, Category.Visual);
        this.setColor(new Color(166, 185, 123).getRGB());
    }
}


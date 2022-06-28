/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package space.emptiness.module.modules.visual;

import space.emptiness.events.EventTarget;
import space.emptiness.events.rendering.EventPostRenderPlayer;
import space.emptiness.events.rendering.EventPreRenderPlayer;
import space.emptiness.module.Category;
import space.emptiness.module.Module;
import space.emptiness.module.value.Mode;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class Chams
extends Module {
    public Mode<Enum> mode = new Mode("Mode", "mode", (Enum[])ChamsMode.values(), (Enum)ChamsMode.Textured);

    public Chams() {
        super("Chams", new String[]{"seethru", "cham"}, Category.Visual);
        this.addValues(this.mode);
        this.setColor(new Color(159, 190, 192).getRGB());
    }

    @EventTarget
    private void preRenderPlayer(EventPreRenderPlayer e) {
        GL11.glEnable((int)32823);
        GL11.glPolygonOffset((float)1.0f, (float)-1100000.0f);
    }

    @EventTarget
    private void postRenderPlayer(EventPostRenderPlayer e) {
        GL11.glDisable((int)32823);
        GL11.glPolygonOffset((float)1.0f, (float)1100000.0f);
    }

    public static enum ChamsMode {
        Textured,
        Normal;
    }

}


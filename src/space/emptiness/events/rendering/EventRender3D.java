/*
 * Decompiled with CFR 0_132.
 */
package space.emptiness.events.rendering;

import space.emptiness.eventapi.events.Event;
import shadersmod.client.Shaders;

public class EventRender3D
        implements Event {
    public float ticks;
    public boolean isUsingShaders;

    public EventRender3D() {
        this.isUsingShaders = Shaders.getShaderPackName() != null;
    }

    public EventRender3D(float ticks) {
        this.ticks = ticks;
        this.isUsingShaders = Shaders.getShaderPackName() != null;
    }

    public float getPartialTicks() {
        return this.ticks;
    }

    public boolean isUsingShaders() {
        return this.isUsingShaders;
    }
}


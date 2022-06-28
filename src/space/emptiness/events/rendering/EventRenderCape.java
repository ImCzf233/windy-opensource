/*
 * Decompiled with CFR 0_132.
 */
package space.emptiness.events.rendering;

import space.emptiness.eventapi.events.callables.EventCancellable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class EventRenderCape
        extends EventCancellable {
    private ResourceLocation capeLocation;
    private final EntityPlayer player;

    public EventRenderCape(ResourceLocation capeLocation, EntityPlayer player) {
        this.capeLocation = capeLocation;
        this.player = player;
    }

    public ResourceLocation getLocation() {
        return this.capeLocation;
    }

    public void setLocation(ResourceLocation location) {
        this.capeLocation = location;
    }

    public EntityPlayer getPlayer() {
        return this.player;
    }
}


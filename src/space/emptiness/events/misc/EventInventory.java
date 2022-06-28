/*
 * Decompiled with CFR 0_132.
 */
package space.emptiness.events.misc;

import space.emptiness.eventapi.events.callables.EventCancellable;
import net.minecraft.entity.player.EntityPlayer;

public class EventInventory
        extends EventCancellable {
    private final EntityPlayer player;

    public EventInventory(EntityPlayer player) {
        this.player = player;
    }

    public EntityPlayer getPlayer() {
        return this.player;
    }
}


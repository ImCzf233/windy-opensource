package space.emptiness.events.misc;

import space.emptiness.eventapi.events.callables.EventCancellable;
import net.minecraft.network.Packet;

public class EventPacket extends EventCancellable {

    private Packet packet;
    private final boolean outGoing;

    public EventPacket(Packet packet, boolean outGoing) {
        this.packet = packet;
        this.outGoing = outGoing;
    }

    public Packet getPacket() {
        return packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }

    public boolean isOutGoing() {
        return outGoing;
    }

    public boolean isInComing() {
        return !outGoing;
    }
}

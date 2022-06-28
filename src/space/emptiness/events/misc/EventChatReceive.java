package space.emptiness.events.misc;

import space.emptiness.eventapi.events.callables.EventCancellable;
import net.minecraft.util.IChatComponent;

public class EventChatReceive extends EventCancellable {

    /**
     * Introduced in 1.8:
     * 0 : Standard Text Message
     * 1 : 'System' message, displayed as standard text.
     * 2 : 'Status' message, displayed above action bar, where song notifications are.
     */
    public final byte type;
    public IChatComponent message;

    public EventChatReceive(byte type, IChatComponent message) {
        this.type = type;
        this.message = message;
    }

}

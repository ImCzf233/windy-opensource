package space.emptiness.events.world;

import space.emptiness.eventapi.events.callables.EventCancellable;

public class EventSafeWalk extends EventCancellable {

    public EventSafeWalk(boolean safeWalking) {
        setCancelled(safeWalking);
    }
}

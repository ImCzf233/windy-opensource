/*
 * Decompiled with CFR 0_132.
 */
package space.emptiness.events.misc;

import space.emptiness.eventapi.events.callables.EventCancellable;

public class EventChat
extends EventCancellable {
    private String message;

    public EventChat(String message) {
        this.message = message;
        this.setType((byte)0);
    }

    private void setType(byte b) {
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}


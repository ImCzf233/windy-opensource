package space.emptiness.events.motion;

import jdk.nashorn.internal.objects.annotations.Getter;
import jdk.nashorn.internal.objects.annotations.Setter;
import space.emptiness.eventapi.events.Event;


public final class PreMotionEvent implements Event {
    private float yaw, pitch;
    private boolean ground;
    private double x, y, z;
}

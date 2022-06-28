package space.emptiness.events.world;


import space.emptiness.eventapi.events.Event;
import net.minecraft.entity.Entity;

public class EventLivingUpdate implements Event {
    public Entity entity;

    public EventLivingUpdate(Entity targetEntity) {
        this.entity = targetEntity;
    }

    public Entity getEntity() {
        return entity;
    }


}

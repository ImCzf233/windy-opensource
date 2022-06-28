package space.emptiness.events.attack;

import space.emptiness.eventapi.events.Event;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class EventFight implements Event {

    private final Entity ent;
    private final boolean pre;

    public EntityLivingBase getTarget() {
        return target;
    }

    public void setTarget(EntityLivingBase target) {
        this.target = target;
    }

    public EntityLivingBase target;
    public EventFight(Entity ent, boolean pre) {
        this.ent = ent;
        this.pre = pre;
    }

    public Entity getEntity() {
        return ent;
    }

    public boolean isPre() {
        return pre;
    }

    public boolean isPost() {
        return !pre;
    }
}

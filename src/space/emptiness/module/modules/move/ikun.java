package space.emptiness.module.modules.move;

import space.emptiness.events.EventTarget;
import space.emptiness.events.world.EventUpdate;
import space.emptiness.module.Category;
import space.emptiness.module.Module;

public class ikun extends Module {
    public ikun() {
        super("ikun", new String[]{}, Category.Move);
    }
    @EventTarget
    public boolean onUpdate(EventUpdate e) {
        if (mc.isSingleplayer()) {
            System.out.println("你干嘛呵呵哎呦~~~~~~~~~~~~");
            return true;
        }
        return false;
    }
}

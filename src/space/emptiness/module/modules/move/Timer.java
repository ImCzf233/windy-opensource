package space.emptiness.module.modules.move;

import space.emptiness.events.EventTarget;
import space.emptiness.events.world.EventUpdate;
import space.emptiness.module.Category;
import space.emptiness.module.Module;
import space.emptiness.module.value.Numbers;

public class Timer extends Module {
    public Timer() {
        super("Timer", new String[]{"GameSpeed"}, Category.Move);
        addValues(this.timerSpee);
    }

    public static Numbers<Float> timerSpee = new Numbers<Float>("TimerSpeed", "TimerSpeed", 1f, 0.5f, 2f, 0.25f);

    @EventTarget
    public void onUpdate(EventUpdate e) {
        mc.timer.timerSpeed = timerSpee.getValue();
    }

    public void onDisable() {
        mc.timer.timerSpeed = 1f;
    }
}

package space.emptiness.module.modules.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;
import space.emptiness.events.EventTarget;
import space.emptiness.events.attack.EventFight;
import space.emptiness.events.misc.EventPacket;
import space.emptiness.events.world.EventUpdate;
import space.emptiness.module.Category;
import space.emptiness.module.Module;
import space.emptiness.module.value.Mode;
import space.emptiness.module.value.Numbers;
import space.emptiness.module.value.Option;
import space.emptiness.utils.TimerUtil;
import space.emptiness.utils.move.PlayerUtils;

public class Criticals2 extends Module {
    public static Mode mode = new Mode("Mode", "Mode", CritMode.values(), CritMode.NoGround);
    public static boolean Crit;
    private final TimerUtil prevent = new TimerUtil();
    public static Minecraft mc = Minecraft.getMinecraft();
    public TimerUtil timer = new TimerUtil();
    public Numbers<Double> Delay = new Numbers("Delay", "Delay", 200,0,1000,1);
    public Numbers<Double> hurttime = new Numbers("HurtTime", "HurtTime", 10,0,20,1);
    public Option<Boolean> debug = new Option<>("Debug", "Debug", false);
    private boolean attacked;
    private int groundTicks;

    public Criticals2() {
        super("Criticals", new String[] { "criticals", "crit" }, Category.Combat);
        addValues(mode, hurttime, Delay);
    }

    private void crit (double yoffset, boolean onGround) {
        assert Minecraft.getNetHandler() != null;
        Minecraft.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(
                mc.thePlayer.posX,
                mc.thePlayer.posY + yoffset,
                mc.thePlayer.posZ,
                onGround
        ));
    }

    public void onEnable() {
        if (mc.thePlayer.onGround) {
            Minecraft.thePlayer.jump();
        }
        attacked = false;
        prevent.reset();
        groundTicks = 0;
        timer.reset();
    }

    @EventTarget
    void onPacket(EventPacket e) {
        if (mode.getValue().equals(CritMode.NoGround)) {
            if (e.getPacket() instanceof C03PacketPlayer) {
                ((C03PacketPlayer) e.getPacket()).onGround = false;
            }
        }
    }

    @EventTarget
    public void onAttack(EventFight e) {
        if (Minecraft.thePlayer.onGround && !Minecraft.thePlayer.isInWater()) {
            if (mode.getValue().equals(CritMode.Packet)) {
                crit(0.0625, false);
                crit(0, false);
                crit(1.1E-11, false);
                crit(0, false);
            }
            if (mode.getValue().equals(CritMode.Hypixel)) {
                double[] posy = new double[]{0.05,0.0016,0.0018,0.0016,0.002,0.04,0.0011};
                for (double i : posy) {
                    crit(i, false);
                }
            }
        }
    }
    @EventTarget
    void onUpdate(EventUpdate event) {
        setSuffix(mode.getModeAsString());
        if (PlayerUtils.isOnGround(0.01)) {
            ++this.groundTicks;
        } else {
            this.groundTicks = 0;
        }

        if (this.groundTicks > 20) {
            this.groundTicks = 20;
        }
    }

    public enum CritMode {
        Packet, Hypixel, NoGround
    }
}

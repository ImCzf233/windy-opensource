package space.emptiness.module.modules.combat;

import net.minecraft.util.BlockPos;
import space.emptiness.command.commands.Help;
import space.emptiness.events.EventTarget;
import space.emptiness.events.attack.EventFight;
import space.emptiness.events.misc.EventPacket;
import space.emptiness.events.world.EventUpdate;
import space.emptiness.module.Category;
import space.emptiness.module.Module;
import space.emptiness.module.value.Mode;
import space.emptiness.module.value.Numbers;
import space.emptiness.module.value.Option;
import space.emptiness.utils.Helper;
import space.emptiness.utils.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;
import space.emptiness.utils.move.PlayerUtils;

import java.util.Random;

public class Criticals extends Module {
    public static Mode mode = new Mode("Mode", "Mode", CritMode.values(), CritMode.Hypixel2);
    public static boolean Crit;
    private final TimerUtil prevent = new TimerUtil();
    public static Minecraft mc = Minecraft.getMinecraft();
    public TimerUtil timer = new TimerUtil();
    public Numbers<Double> Delay = new Numbers("Delay", "Delay", 200,0,1000,1);
    public Numbers<Double> hurttime = new Numbers("HurtTime", "HurtTime", 10,0,20,1);
    public Option<Boolean> debug = new Option<>("Debug", "Debug", false);
    private boolean attacked;
    private int groundTicks;

    public Criticals() {
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
        if (mode.getValue().equals(CritMode.Hypixel2)) {
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
        }
        boolean canCrit = this.groundTicks > 3 && Minecraft.theWorld.getBlockState(new BlockPos(Minecraft.thePlayer.posX, Minecraft.thePlayer.posY - 1.0, Minecraft.thePlayer.posZ)).getBlock().isFullBlock() && !PlayerUtils.isInLiquid() && !PlayerUtils.isOnLiquid() && !Minecraft.thePlayer.isOnLadder() && Minecraft.thePlayer.ridingEntity == null && !PlayerUtils.isOnGround(-2.0);
        if (e.isPost() && canCrit && e.getEntity().hurtResistantTime <= ((Number)this.hurttime.getValue()).intValue() && this.prevent.hasPassed(300L) && this.timer.hasPassed((long)((Number)this.Delay.getValue()).intValue() * 100L)) {
            this.attacked = true;
            this.timer.reset();
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
        double random;
        if(event.isPost()&&this.mode.getValue()==CritMode.Hypixel&&this.attacked){
            if (KillAura.target != null&& event.isOnGround()) {
                if (KillAura.target.hurtTime > Delay.getValue().intValue()) {
                    for (double offset : new double[]{0.06f, 0.01f}) {
                        random = (Math.random() * 0.001);
                        if (debug.getValue()) {
                            Helper.sendMessage(String.valueOf(random));
                        }
                        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + offset + random, mc.thePlayer.posZ, false));
                    }
                }
            }
        }
    }

    public enum CritMode {
        Packet, Hypixel, Hypixel2
    }
}
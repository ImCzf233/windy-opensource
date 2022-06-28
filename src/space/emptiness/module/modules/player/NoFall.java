
package space.emptiness.module.modules.player;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import space.emptiness.events.EventTarget;
import space.emptiness.events.misc.EventPacket;
import space.emptiness.events.world.EventUpdate;
import space.emptiness.module.Category;
import space.emptiness.module.Module;
import space.emptiness.module.modules.move.Speed;
import space.emptiness.module.value.Mode;
import space.emptiness.utils.move.PlayerUtils;

import java.awt.*;

import static space.emptiness.utils.PacketUtils.sendPacketNoEvent;

public class NoFall
extends Module {

    public NoFall() {
        super("Nofall", new String[]{}, Category.Player);
    }

    private double getLastTickYDistance() {
        return Math.hypot(mc.thePlayer.posY - mc.thePlayer.prevPosY, mc.thePlayer.posY - mc.thePlayer.prevPosY);
    }

    @EventTarget
    public void onPacket(EventPacket event) {
        if (mc.thePlayer.posY > 0 && mc.thePlayer.fallDistance >= 2 && mc.thePlayer.lastTickPosY - mc.thePlayer.posY > 0 && mc.thePlayer.motionY != 0) {
            if (!PlayerUtils.isBlockUnder() || mc.thePlayer.fallDistance > 255 || !PlayerUtils.isBlockUnder() && mc.thePlayer.fallDistance > 50) {
                return;
            }

            if (event.getPacket() instanceof C02PacketUseEntity) {
                C02PacketUseEntity packet = (C02PacketUseEntity) event.getPacket();

                if (packet.getAction() == C02PacketUseEntity.Action.ATTACK) {
                    event.setCancelled(true);
                }
            }

            if (event.getPacket() instanceof C03PacketPlayer) {
                C03PacketPlayer packet = (C03PacketPlayer) event.getPacket();

                if (packet.isMoving() && packet.rotating) {
                    sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(packet.x, packet.y, packet.z, packet.isOnGround()));
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventTarget
    public void onMotion(EventUpdate event) {
        setSuffix("Hypixel");
        if (event.isPre()) {
            if (mc.thePlayer.posY > 0 && mc.thePlayer.lastTickPosY - mc.thePlayer.posY > 0 && mc.thePlayer.motionY != 0 && mc.thePlayer.fallDistance >= 2.5) {
                if (!PlayerUtils.isBlockUnder() || mc.thePlayer.fallDistance > 255 || !PlayerUtils.isBlockUnder() && mc.thePlayer.fallDistance > 50) {
                    return;
                }

                if (mc.thePlayer.fallDistance > 10 || mc.thePlayer.ticksExisted % 2 == 0) {
                    sendPacketNoEvent(new C03PacketPlayer(true));
                    mc.timer.timerSpeed = 1.0F;
                }
            }
        }
    }
}

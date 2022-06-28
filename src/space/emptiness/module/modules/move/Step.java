/*
 * Decompiled with CFR 0_132.
 */
package space.emptiness.module.modules.move;

import space.emptiness.events.EventTarget;
import space.emptiness.events.world.EventUpdate;
import space.emptiness.module.Category;
import space.emptiness.module.Module;
import space.emptiness.module.value.Numbers;
import space.emptiness.module.value.Option;
import net.minecraft.network.play.client.C03PacketPlayer;

import java.awt.*;

public class Step
extends Module {
    private Numbers<Double> height = new Numbers<Double>("Height", "height", 1.0, 1.0, 10.0, 0.5);
    private Option<Boolean> ncp = new Option<Boolean>("NCP", "ncp", false);

    public Step() {
        super("Step", new String[]{"autojump"}, Category.Move);
        this.setColor(new Color(165, 238, 65).getRGB());
        this.addValues(this.ncp);
    }

    @Override
    public void onDisable() {
        this.mc.thePlayer.stepHeight = 0.6f;
    }

    @EventTarget
    private void onUpdate(EventUpdate e) {
        if (this.ncp.getValue().booleanValue()) {
            this.mc.thePlayer.stepHeight = 0.6f;
            if (this.mc.thePlayer.isCollidedHorizontally && this.mc.thePlayer.onGround && this.mc.thePlayer.isCollidedVertically && this.mc.thePlayer.isCollided && this.mc.thePlayer.isCollidedHorizontally && this.mc.thePlayer.onGround && this.mc.thePlayer.isCollidedVertically && this.mc.thePlayer.isCollided) {
                this.mc.thePlayer.setSprinting(true);
                this.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 0.42, this.mc.thePlayer.posZ, this.mc.thePlayer.onGround));
                this.mc.thePlayer.setSprinting(true);
                this.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 0.753, this.mc.thePlayer.posZ, this.mc.thePlayer.onGround));
                this.mc.thePlayer.setSprinting(true);
                this.mc.thePlayer.setPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 0.42, this.mc.thePlayer.posZ);
                this.mc.timer.timerSpeed = 0.5f;
                this.mc.thePlayer.setSprinting(true);
                new Thread(new Runnable(){

                    @Override
                    public void run() {
                        try {
                            Thread.sleep(100L);
                        }
                        catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        Step.this.mc.timer.timerSpeed = 1.0f;
                    }
                }).start();
            }
        } else {
            this.mc.thePlayer.stepHeight = 1.0f;
        }
    }

}


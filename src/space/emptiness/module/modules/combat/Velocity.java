package space.emptiness.module.modules.combat;

import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import space.emptiness.events.EventTarget;
import space.emptiness.events.rendering.EventRender2D;
import space.emptiness.events.world.EventPacketReceive;
import space.emptiness.events.world.EventUpdate;
import space.emptiness.module.Category;
import space.emptiness.module.Module;
import space.emptiness.module.value.Mode;
import space.emptiness.module.value.Numbers;

public class Velocity extends Module {
    private static final boolean AACPull = false;
    private double motionY;
    private double motionX;
    private double motionZ;
    private static Mode mode;
    private Numbers<Double> vertical;
    private Numbers<Double> horizontal;
    private Numbers<Double> xz;
    private Numbers<Double> y;
    private String modname;

    public Velocity() {
        super("Velocity", new String[]{"AntiKB"}, Category.Combat);
        this.mode = new Mode<Enum>("Mode","Mode", modes.values(), modes.Normal);
        this.vertical = new Numbers<Double>("Vertical","Vertical", 0.0, 0.0, 1.0, 0.01);
        this.horizontal = new Numbers<Double>("Horizontal","Horizontal", 0.0, 0.0, 1.0, 0.01);
        this.xz = new Numbers<Double>("Velocity_X/Z", "Velocity_X/Z",0.0, 0.0, 1.0, 0.01);
        this.y = new Numbers<Double>("Velocity_Y","Velocity_Y", 0.0, 0.0, 1.0, 0.01);
        this.addValues(mode, vertical, horizontal, xz, y);
    }


    @EventTarget
    public void onEvent(EventUpdate event) {

    }


    @EventTarget
    public void onEvent(EventPacketReceive event) {
        if (this.mode.getValue() == modes.Normal) {
            if (event.getPacket() instanceof S12PacketEntityVelocity) {
                S12PacketEntityVelocity packet = (S12PacketEntityVelocity) event.getPacket();
                if (packet.getEntityID() == this.mc.thePlayer.getEntityId()) {
                    if (((Double) this.vertical.getValue()).doubleValue() == 0.0D && (Double) this.horizontal.getValue() == 0.0D) {
                        event.setCancelled(true);
                    } else {
                        packet.motionX = (int) ((double) packet.motionX * (Double) this.horizontal.getValue());
                        packet.motionZ = (int) ((double) packet.motionZ * (Double) this.horizontal.getValue());
                        packet.motionY = (int) ((double) packet.motionY * (Double) this.vertical.getValue());
                    }
                }
            }


            if (event.getPacket() instanceof S27PacketExplosion) {
                S27PacketExplosion packet = (S27PacketExplosion) event.getPacket();
                if ((Double) this.vertical.getValue() == 0.0D && (Double) this.horizontal.getValue() == 0.0D) {
                    event.setCancelled(true);
                } else {
                    packet.field_149152_f = (float) ((double) packet.field_149152_f * (Double) this.horizontal.getValue());
                    packet.field_149153_g = (float) ((double) packet.field_149153_g * (Double) this.horizontal.getValue());
                    packet.field_149159_h = (float) ((double) packet.field_149159_h * (Double) this.vertical.getValue());
                }
            }
        }


    }

    @EventTarget
    public void onRender2d(EventRender2D e) {

   		this.setSuffix(this.mode.getValue());
   	if (this.mode.getValue() == modes.Normal){
   		this.setSuffix(this.vertical.getValue() +"% " + this.horizontal.getValue()+"%");
   	}
    }

    enum modes {
        Normal,

    }
}



package space.emptiness.module.modules.move;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import space.emptiness.events.EventTarget;
import space.emptiness.events.world.EventMove;
import space.emptiness.module.Category;
import space.emptiness.module.Module;
import space.emptiness.utils.move.MovementUtils;

import java.util.ArrayList;

public class Flight extends Module {
	private float stage;
	private int ticks;
	private boolean doFly;
	private double x, y, z;
	private ArrayList<Packet> packets = new ArrayList<>();
	private boolean hasClipped;
	private double speedStage;


	public Flight() {
		super("Flight", new String[]{}, Category.Move);
	}

	@EventTarget
	public void onMotion(EventMove e){
		mc.thePlayer.cameraYaw = mc.thePlayer.cameraPitch = 0.05f;
		mc.thePlayer.posY = y;
		if (mc.thePlayer.onGround && stage == 0) {
			mc.thePlayer.motionY = 0.09;
		}
		stage++;
		if (mc.thePlayer.onGround && stage > 2 && !hasClipped) {
			mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.15, mc.thePlayer.posZ, false));
			mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.15, mc.thePlayer.posZ, true));
			hasClipped = true;
		}
		if (doFly) {
			mc.thePlayer.motionY = 0;
			mc.thePlayer.onGround = true;
			mc.timer.timerSpeed = 2;
		} else {
			MovementUtils.setSpeed(0);
			mc.timer.timerSpeed = 5;
		}

	}


	@Override
	public void onEnable() {
		doFly = false;
		ticks = 0;
		stage = 0;
		x = mc.thePlayer.posX;
		y = mc.thePlayer.posY;
		z = mc.thePlayer.posZ;
		hasClipped = false;
		packets.clear();
		super.onEnable();
	}

	@Override
	public void onDisable() {

		mc.timer.timerSpeed = 1;
		super.onDisable();
	}

}
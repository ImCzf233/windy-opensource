
package space.emptiness.module.modules.move;

import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;
import space.emptiness.events.EventTarget;
import space.emptiness.events.misc.EventPacket;
import space.emptiness.events.world.EventUpdate;
import space.emptiness.module.Category;
import space.emptiness.module.Module;
import space.emptiness.module.value.Numbers;
import space.emptiness.module.value.Option;
import space.emptiness.utils.TimerUtil;

public class Lagback
extends Module {
    BlockPos lastGroundPos;
    TimerUtil time = new TimerUtil();
    TimerUtil timer2 = new TimerUtil();
    public Numbers<Double> fall = new Numbers<Double>("Distance", "dd", 8.0, 1.0, 10.0, 1.0);
    private Option<Boolean> ncp = new Option<Boolean>("NCP", "wda", !true);
	private boolean gng;
    public Lagback() {
    	   super("LagBack", new String[]{}, Category.Move);
    	   this.addValues(fall,ncp);
    }
    public void goToGround() {
	

		double minY = mc.thePlayer.posY -mc.thePlayer.fallDistance;

		if (minY <= 0)
			return;

		for (double y = mc.thePlayer.posY; y > minY;) {
			y -= 9.9;
			if (y < minY)
				y = minY;

			C03PacketPlayer.C04PacketPlayerPosition packet = new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, y, mc.thePlayer.posZ, true);
			mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, y, mc.thePlayer.posZ, true));
		}

		for (double y = minY; y < mc.thePlayer.posY;) {
			y += 9.9;
			if (y > mc.thePlayer.posY)
				y = mc.thePlayer.posY;

			C03PacketPlayer.C04PacketPlayerPosition packet = new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, y, mc.thePlayer.posZ, !true);
			mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, y, mc.thePlayer.posZ, !true));
		}
	}
	@EventTarget
	public void onPre(EventPacket e) {
		if (!e.isOutGoing()) {
			return;
		}
		  if (mc.thePlayer.fallDistance >= this.fall.getValue().floatValue() && !this.isBlockUnder()&&ncp.getValue()) {

		             if ((e.getPacket() instanceof C03PacketPlayer.C06PacketPlayerPosLook||e.getPacket() instanceof C03PacketPlayer.C04PacketPlayerPosition)) {
		            		mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0001, mc.thePlayer.posZ, true));
		            	 e.setCancelled(true);
		            	 
		            	 mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
		             }

		             if ((e.getPacket() instanceof C03PacketPlayer.C06PacketPlayerPosLook||e.getPacket() instanceof C03PacketPlayer.C04PacketPlayerPosition)) {
		                		mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, 0, mc.thePlayer.posZ, !true));
		                		e.setCancelled(true);
		                	 
		                	 mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
		                 }
		           		
		             

		  }
	}
    @EventTarget
    public void onUpdate(EventUpdate e) {
    	if (mc.thePlayer.onGround||mc.thePlayer.ticksExisted<5) {
    		gng=true;
    		this.setSuffix(ncp.getValue()?"NCP":"Hypixel");
    }

        if (mc.thePlayer.fallDistance >= this.fall.getValue().floatValue() && !this.isBlockUnder()&&!ncp.getValue()&&gng) {

        
					//mc.thePlayer.setPosition(this.mc.thePlayer.posX,mc.thePlayer.posY-5, this.mc.thePlayer.posZ);
					//mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.RIDING_JUMP));
        	mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY+11+mc.thePlayer.fallDistance, mc.thePlayer.posZ, !true));
        	
					mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,  mc.thePlayer.posY+5.5+mc.thePlayer.fallDistance, mc.thePlayer.posZ, true));

				//	updateFlyHeight();
					
		

					//mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
				//	mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SLEEPING));
					
					mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,  mc.thePlayer.posY+1.25+mc.thePlayer.fallDistance, mc.thePlayer.posZ, !true));
					
				

					
		
					//	mc.thePlayer.setPositionAndUpdate(this.mc.thePlayer.posX, mc.thePlayer.posY+1, this.mc.thePlayer.posZ);
						mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY+9.111+mc.thePlayer.fallDistance, mc.thePlayer.posZ, !true));
					
				
							
		
						
						gng=!true;
						
						
			
					
					
				}
    }

    private boolean isBlockUnder() {
        for (int i = (int)mc.thePlayer.posY; i > 0; --i) {
            BlockPos pos = new BlockPos(mc.thePlayer.posX, (double)i, mc.thePlayer.posZ);
            if (this.mc.theWorld.getBlockState(pos).getBlock() instanceof BlockAir) continue;
            return true;
        }
        return false;
    }
}


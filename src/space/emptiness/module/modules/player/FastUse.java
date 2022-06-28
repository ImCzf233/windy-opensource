/*
 * Decompiled with CFR 0_132.
 */
package space.emptiness.module.modules.player;

import space.emptiness.events.EventTarget;
import space.emptiness.events.world.EventUpdate;
import space.emptiness.module.Category;
import space.emptiness.module.Module;
import space.emptiness.module.value.Option;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.awt.*;

public class FastUse
extends Module {
    private Option<Boolean> guardian = new Option<Boolean>("Guardian", "guardian", true);

    public FastUse() {
        super("FastUse", new String[]{"fasteat", "fuse"}, Category.Player);
        this.addValues(this.guardian);
    }

    private boolean canConsume() {
        if (!(this.mc.thePlayer.getCurrentEquippedItem() != null && this.mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemPotion || this.mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemFood)) {
            return false;
        }
        return true;
    }

    @EventTarget
    private void onUpdate(EventUpdate e) {
        this.setColor(new Color(100, 200, 200).getRGB());
        if (this.guardian.getValue().booleanValue()) {
            if (this.mc.thePlayer.onGround && this.mc.thePlayer.getItemInUseDuration() == 1 && Minecraft.getMinecraft().gameSettings.keyBindUseItem.pressed && !(this.mc.thePlayer.getItemInUse().getItem() instanceof ItemBow) && !(this.mc.thePlayer.getItemInUse().getItem() instanceof ItemSword)) {
                int i = 0;
                while (i < 40) {
                    this.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(this.mc.thePlayer.rotationYaw, this.mc.thePlayer.rotationPitch, this.mc.thePlayer.onGround));
                    if (this.guardian.getValue().booleanValue() && this.mc.thePlayer.ticksExisted % 2 == 0) {
                        this.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY - 1.0, this.mc.thePlayer.posZ, false));
                    }
                    ++i;
                }
                this.mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            }
        } else if (this.mc.thePlayer.onGround && this.mc.thePlayer.getItemInUseDuration() == 16 && Minecraft.getMinecraft().gameSettings.keyBindUseItem.pressed && !(this.mc.thePlayer.getItemInUse().getItem() instanceof ItemBow) && !(this.mc.thePlayer.getItemInUse().getItem() instanceof ItemSword)) {
            int i = 0;
            while (i < 17) {
                this.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(this.mc.thePlayer.rotationYaw, this.mc.thePlayer.rotationPitch, this.mc.thePlayer.onGround));
                ++i;
            }
            this.mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
        }
    }
}


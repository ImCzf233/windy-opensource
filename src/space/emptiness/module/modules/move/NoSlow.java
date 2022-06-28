package space.emptiness.module.modules.move;


import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import space.emptiness.events.EventTarget;
import space.emptiness.events.misc.EventPacket;
import space.emptiness.events.world.EventUpdate;
import space.emptiness.module.Category;
import space.emptiness.module.Module;
import space.emptiness.module.ModuleManager;
import space.emptiness.module.value.Mode;
import space.emptiness.module.value.Option;
import space.emptiness.module.value.Value;
import space.emptiness.utils.PacketUtils;
import space.emptiness.utils.PlayerUtil;
import space.emptiness.utils.TimerUtil;
import space.emptiness.Client;
import space.emptiness.module.modules.combat.KillAura;

public class NoSlow extends Module {

    private Mode<Enum> mode = new Mode("Mode", "Mode",  JMode.values(),  JMode.Hypixel);
    public TimerUtil timer = new TimerUtil();

    boolean nextTemp, lastBlockingStat, waitC03;
    List<Packet> packetBuf = new LinkedList<>();

    public NoSlow() {
        super("NoSlow", new String[] { "NoSlowDown","NoSlow" }, Category.Move);
        this.setColor(new Color(188, 233, 248).getRGB());
        this.addValues(this.mode);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @EventTarget
    void onPacket(EventPacket e) {
        if (mode.getValue().equals(JMode.Vanilla) && e.getPacket() instanceof S30PacketWindowItems && (mc.thePlayer.isUsingItem() || mc.thePlayer.isBlocking())) {
            e.setCancelled(true);
        }
    }

    @EventTarget
    void onUpdate(EventUpdate e) {
        if (mode.getValue().equals(JMode.Hypixel)) {
            Module killAura = ModuleManager.getModuleByClass(KillAura.class);
            if ((!killAura.isEnabled() || !KillAura.shouldAttack()
                    && e.isPre()
                    && mc.thePlayer.getItemInUse() != null && mc.thePlayer.getItemInUse().getItem() != null)) {
                Item item = mc.thePlayer.getItemInUse().getItem();
                if (mc.thePlayer.isUsingItem() && (item instanceof ItemFood || item instanceof ItemBucketMilk || item instanceof ItemPotion) && mc.thePlayer.getItemInUseCount() >= 1)
                    PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            }
        }
    }
    enum JMode {
        Vanilla, Hypixel
    }
}
package space.emptiness.module.modules.combat;


import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import space.emptiness.events.EventTarget;
import space.emptiness.events.world.EventUpdate;
import space.emptiness.module.Category;
import space.emptiness.module.Module;
import space.emptiness.module.value.Numbers;
import space.emptiness.module.value.Option;
import space.emptiness.utils.InventoryUtil;
import space.emptiness.utils.PlayerUtil;
import space.emptiness.utils.TimerUtil;

public class AutoPot extends Module {

    //����
    private static boolean potting;

    //������Ʒ
    private int slot, last;

    //�����ӳ�
    private final TimerUtil timer = new TimerUtil();

    private final Option<Boolean> healthValue = new Option<>("Health", "Health Pot", true),
            speedValue = new Option<>("Speed", "Speed Pot", true),
            jumpValue = new Option<>("Jump", "Jump and pot", false);

    private final Numbers<Number> potHealthValue = new Numbers<>("Pot Health", "Health to health pot", 15.0, 3.0, 20.0, 1.0);

    public AutoPot() {
        super("Auto Pot", new String[]{}, Category.Combat);
        addValues(healthValue, speedValue, jumpValue, potHealthValue);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        //����
        potting = false;
        slot = -1;
        last = -1;

        //���ü�ʱ��
        timer.reset();
    }

    @Override
    public void onDisable() {
        super.onDisable();

        //����
        potting = false;
    }

    @EventTarget
    void onUpdate(EventUpdate event) {
        slot = getSlot();
        if (event.isPre() && timer.hasPassed(1000)) {
            int regenId = Potion.regeneration.getId();
            if (!mc.thePlayer.isPotionActive(regenId) && !potting && mc.thePlayer.onGround && healthValue.getValue() && mc.thePlayer.getHealth() <= potHealthValue.getValue().doubleValue() && hasPot(regenId)) {
                int cum = hasPot(regenId, slot);
                if (cum != -1) InventoryUtil.swap(cum, slot);
                last = mc.thePlayer.inventory.currentItem;
                mc.thePlayer.inventory.currentItem = slot;
                event.setPitch(jumpValue.getValue() && !PlayerUtil.MovementInput() ? -90 : (PlayerUtil.MovementInput() ? 85 : 90));
                if (jumpValue.getValue() && !PlayerUtil.MovementInput()) {
                    mc.thePlayer.motionY = (.41999998688698 + (PlayerUtil.getJumpEffect() * .1));
                    PlayerUtil.setSpeed(0);
                }
                potting = true;
                timer.reset();
            }

            int speedId = Potion.moveSpeed.getId();
            if (!mc.thePlayer.isPotionActive(speedId) && !potting && mc.thePlayer.onGround && speedValue.getValue() && hasPot(speedId)) {
                int cum = hasPot(speedId, slot);
                if (cum != -1) InventoryUtil.swap(cum, slot);
                last = mc.thePlayer.inventory.currentItem;
                mc.thePlayer.inventory.currentItem = slot;
                event.setPitch(jumpValue.getValue() && !PlayerUtil.MovementInput() ? -90 : (PlayerUtil.MovementInput() ? 85 : 90));
                if (jumpValue.getValue() && !PlayerUtil.MovementInput()) {
                    mc.thePlayer.motionY = (.41999998688698 + (PlayerUtil.getJumpEffect() * .1));
                    PlayerUtil.setSpeed(0);
                }
                potting = true;
                timer.reset();
            }
        }
        if (event.isPost() && potting) {
            if (mc.thePlayer.inventory.getCurrentItem() != null && mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getCurrentItem())) {
                mc.entityRenderer.itemRenderer.resetEquippedProgress2();
            }
            if (last != -1) mc.thePlayer.inventory.currentItem = last;
            potting = false;
            last = -1;
        }
    }

    private int hasPot(int id, int targetSlot) {
        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemPotion) {
                    ItemPotion pot = (ItemPotion) is.getItem();
                    if (pot.getEffects(is).isEmpty()) continue;
                    PotionEffect effect = pot.getEffects(is).get(0);
                    if (effect.getPotionID() == id) {
                        if (ItemPotion.isSplash(is.getItemDamage()) && isBestPot(pot, is)) {
                            if (36 + targetSlot != i) return i;
                        }
                    }
                }
            }
        }
        return -1;
    }

    private boolean hasPot(int id) {
        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemPotion) {
                    ItemPotion pot = (ItemPotion) is.getItem();
                    if (pot.getEffects(is).isEmpty()) continue;
                    PotionEffect effect = pot.getEffects(is).get(0);
                    if (effect.getPotionID() == id) {
                        if (ItemPotion.isSplash(is.getItemDamage()) && isBestPot(pot, is)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean isBestPot(ItemPotion potion, ItemStack stack) {
        if (potion.getEffects(stack) == null || potion.getEffects(stack).size() != 1)
            return false;
        PotionEffect effect = (PotionEffect) potion.getEffects(stack).get(0);
        int potionID = effect.getPotionID();
        int amplifier = effect.getAmplifier();
        int duration = effect.getDuration();
        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemPotion) {
                    ItemPotion pot = (ItemPotion) is.getItem();
                    if (pot.getEffects(is) != null) {
                        for (Object o : pot.getEffects(is)) {
                            PotionEffect effects = (PotionEffect) o;
                            int id = effects.getPotionID();
                            int ampl = effects.getAmplifier();
                            int dur = effects.getDuration();
                            if (id == potionID && ItemPotion.isSplash(is.getItemDamage())) {
                                if (ampl > amplifier) {
                                    return false;
                                } else if (ampl == amplifier && dur > duration) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private int getSlot() {
        int spoofSlot = 8;
        for (int i = 36; i < 45; i++) {
            if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                spoofSlot = i - 36;
                break;
            } else if (mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem() instanceof ItemPotion) {
                spoofSlot = i - 36;
                break;
            }
        }
        return spoofSlot;
    }

    public static boolean isPotting() {
        return potting;
    }
}

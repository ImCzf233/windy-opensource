package space.emptiness.module.modules.player;

import com.google.common.collect.Multimap;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import space.emptiness.events.EventTarget;
import space.emptiness.events.world.EventUpdate;
import space.emptiness.module.Category;
import space.emptiness.module.Module;
import space.emptiness.module.value.Mode;
import space.emptiness.module.value.Numbers;
import space.emptiness.module.value.Option;
import space.emptiness.utils.InventoryUtils;
import space.emptiness.utils.TimerUtil;
import space.emptiness.Client;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/*
 * Made by LeakedPvP
 */
public class InvManager extends Module {
    private static final Random RANDOM = new Random();
    public static List<Integer> blacklistedItems = new ArrayList<Integer>();
    private boolean allowSwitch = true;
    private boolean hasNoItems;
    public final TimerUtil timer = new TimerUtil();
    private Option<Boolean> openInv = new Option<Boolean>("Require Inventory Open?", "open inv", false);

    public InvManager() {
        super("InvCleaner", new String[]{"inventorycleaner", "invclean"}, Category.Player);
        this.addValues(this.openInv);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.hasNoItems = false;
    }

    @EventTarget
    private void onTick(EventUpdate event) {
        if (this.mc.thePlayer.isUsingItem()) {
            return;
        }
        if (this.mc.thePlayer.ticksExisted % 2 == 0 && RANDOM.nextInt(2) == 0 && (!this.openInv.getValue().booleanValue() || this.mc.currentScreen instanceof GuiInventory && this.openInv.getValue().booleanValue()) && this.timer.hasReached(59.0)) {
            CopyOnWriteArrayList<Integer> uselessItems = new CopyOnWriteArrayList<Integer>();
            int o = 0;
            while (o < 45) {
                if (this.mc.thePlayer.inventoryContainer.getSlot(o).getHasStack()) {
                    ItemStack item = this.mc.thePlayer.inventoryContainer.getSlot(o).getStack();
                    if (this.mc.thePlayer.inventory.armorItemInSlot(0) != item && this.mc.thePlayer.inventory.armorItemInSlot(1) != item && this.mc.thePlayer.inventory.armorItemInSlot(2) != item && this.mc.thePlayer.inventory.armorItemInSlot(3) != item) {
                        if (item != null && item.getItem() != null && Item.getIdFromItem(item.getItem()) != 0 && !this.stackIsUseful(o)) {
                            uselessItems.add(o);
                        }
                        this.hasNoItems = true;
                    }
                }
                ++o;
            }
            if (!uselessItems.isEmpty()) {
                this.mc.playerController.windowClick(this.mc.thePlayer.inventoryContainer.windowId, (Integer)uselessItems.get(0), 1, 4, this.mc.thePlayer);
                uselessItems.remove(0);
                this.timer.reset();
            }
        }
    }

    private void bestSword() {
        int slotToSwitch = 0;
        float swordDamage = 0.0f;
        int i = 9;
        while (i < 45) {
            ItemStack is;
            float swordD;
            if (this.mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && (is = this.mc.thePlayer.inventoryContainer.getSlot(i).getStack()).getItem() instanceof ItemSword && (swordD = this.getItemDamage(is)) > swordDamage) {
                swordDamage = swordD;
                slotToSwitch = i;
            }
            ++i;
        }
        if (this.allowSwitch) {
            this.mc.playerController.windowClick(this.mc.thePlayer.inventoryContainer.windowId, slotToSwitch, this.mc.thePlayer.inventory.currentItem, 2, this.mc.thePlayer);
            this.allowSwitch = false;
        }
    }

    private boolean stackIsUseful(int i) {
        int o;
        ItemStack item;
        boolean hasAlreadyOrBetter;
        ItemStack itemStack;
        itemStack = this.mc.thePlayer.inventoryContainer.getSlot(i).getStack();
        hasAlreadyOrBetter = false;
        if (itemStack.getItem() instanceof ItemSword || itemStack.getItem() instanceof ItemPickaxe || itemStack.getItem() instanceof ItemAxe) {
            o = 0;
            while (o < 45) {
                if (o != i && this.mc.thePlayer.inventoryContainer.getSlot(o).getHasStack() && ((item = this.mc.thePlayer.inventoryContainer.getSlot(o).getStack()) != null && item.getItem() instanceof ItemSword || item.getItem() instanceof ItemAxe || item.getItem() instanceof ItemPickaxe)) {
                    float damageFound = this.getItemDamage(itemStack);
                    float damageCurrent = this.getItemDamage(item);
                    if ((damageCurrent += EnchantmentHelper.func_152377_a(item, EnumCreatureAttribute.UNDEFINED)) > (damageFound += EnchantmentHelper.func_152377_a(itemStack, EnumCreatureAttribute.UNDEFINED))) {
                        hasAlreadyOrBetter = true;
                        break;
                    }
                }
                ++o;
            }
        } else if (itemStack.getItem() instanceof ItemArmor) {
            o = 0;
            while (o < 45) {
                if (i != o && this.mc.thePlayer.inventoryContainer.getSlot(o).getHasStack() && (item = this.mc.thePlayer.inventoryContainer.getSlot(o).getStack()) != null && item.getItem() instanceof ItemArmor) {
                    List<Integer> helmet = Arrays.asList(298, 314, 302, 306, 310);
                    List<Integer> chestplate = Arrays.asList(299, 315, 303, 307, 311);
                    List<Integer> leggings = Arrays.asList(300, 316, 304, 308, 312);
                    List<Integer> boots = Arrays.asList(301, 317, 305, 309, 313);
                    if (helmet.contains(Item.getIdFromItem(item.getItem())) && helmet.contains(Item.getIdFromItem(itemStack.getItem()))) {
                        if (helmet.indexOf(Item.getIdFromItem(itemStack.getItem())) < helmet.indexOf(Item.getIdFromItem(item.getItem()))) {
                            hasAlreadyOrBetter = true;
                            break;
                        }
                    } else if (chestplate.contains(Item.getIdFromItem(item.getItem())) && chestplate.contains(Item.getIdFromItem(itemStack.getItem()))) {
                        if (chestplate.indexOf(Item.getIdFromItem(itemStack.getItem())) < chestplate.indexOf(Item.getIdFromItem(item.getItem()))) {
                            hasAlreadyOrBetter = true;
                            break;
                        }
                    } else if (leggings.contains(Item.getIdFromItem(item.getItem())) && leggings.contains(Item.getIdFromItem(itemStack.getItem()))) {
                        if (leggings.indexOf(Item.getIdFromItem(itemStack.getItem())) < leggings.indexOf(Item.getIdFromItem(item.getItem()))) {
                            hasAlreadyOrBetter = true;
                            break;
                        }
                    } else if (boots.contains(Item.getIdFromItem(item.getItem())) && boots.contains(Item.getIdFromItem(itemStack.getItem())) && boots.indexOf(Item.getIdFromItem(itemStack.getItem())) < boots.indexOf(Item.getIdFromItem(item.getItem()))) {
                        hasAlreadyOrBetter = true;
                        break;
                    }
                }
                ++o;
            }
        }
        o = 0;
        while (o < 45) {
            if (i != o && this.mc.thePlayer.inventoryContainer.getSlot(o).getHasStack() && (item = this.mc.thePlayer.inventoryContainer.getSlot(o).getStack()) != null && (item.getItem() instanceof ItemSword || item.getItem() instanceof ItemAxe || item.getItem() instanceof ItemBow || item.getItem() instanceof ItemFishingRod || item.getItem() instanceof ItemArmor || item.getItem() instanceof ItemAxe || item.getItem() instanceof ItemPickaxe || Item.getIdFromItem(item.getItem()) == 346)) {
                Item found = item.getItem();
                if (Item.getIdFromItem(itemStack.getItem()) == Item.getIdFromItem(item.getItem())) {
                    hasAlreadyOrBetter = true;
                    break;
                }
            }
            ++o;
        }
        if (Item.getIdFromItem(itemStack.getItem()) == 367) {
            return false;
        }
        if (Item.getIdFromItem(itemStack.getItem()) == 30) {
            return true;
        }
        if (Item.getIdFromItem(itemStack.getItem()) == 259) {
            return true;
        }
        if (Item.getIdFromItem(itemStack.getItem()) == 262) {
            return true;
        }
        if (Item.getIdFromItem(itemStack.getItem()) == 264) {
            return true;
        }
        if (Item.getIdFromItem(itemStack.getItem()) == 265) {
            return true;
        }
        if (Item.getIdFromItem(itemStack.getItem()) == 346) {
            return true;
        }
        if (Item.getIdFromItem(itemStack.getItem()) == 384) {
            return true;
        }
        if (Item.getIdFromItem(itemStack.getItem()) == 345) {
            return true;
        }
        if (Item.getIdFromItem(itemStack.getItem()) == 296) {
            return true;
        }
        if (Item.getIdFromItem(itemStack.getItem()) == 336) {
            return true;
        }
        if (Item.getIdFromItem(itemStack.getItem()) == 266) {
            return true;
        }
        if (Item.getIdFromItem(itemStack.getItem()) == 280) {
            return true;
        }
        if (itemStack.hasDisplayName()) {
            return true;
        }
        if (hasAlreadyOrBetter) {
            return false;
        }
        if (itemStack.getItem() instanceof ItemArmor) {
            return true;
        }
        if (itemStack.getItem() instanceof ItemAxe) {
            return true;
        }
        if (itemStack.getItem() instanceof ItemBow) {
            return true;
        }
        if (itemStack.getItem() instanceof ItemSword) {
            return true;
        }
        if (itemStack.getItem() instanceof ItemPotion) {
            return true;
        }
        if (itemStack.getItem() instanceof ItemFlintAndSteel) {
            return true;
        }
        if (itemStack.getItem() instanceof ItemEnderPearl) {
            return true;
        }
        if (itemStack.getItem() instanceof ItemBlock) {
            return true;
        }
        if (itemStack.getItem() instanceof ItemFood) {
            return true;
        }
        if (itemStack.getItem() instanceof ItemPickaxe) {
            return true;
        }
        return false;
    }

    private float getItemDamage(ItemStack itemStack) {
        Iterator iterator;
        Multimap multimap = itemStack.getAttributeModifiers();
        if (!multimap.isEmpty() && (iterator = multimap.entries().iterator()).hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            AttributeModifier attributeModifier = (AttributeModifier)entry.getValue();
            double damage = attributeModifier.getOperation() != 1 && attributeModifier.getOperation() != 2 ? attributeModifier.getAmount() : attributeModifier.getAmount() * 100.0;
            if (attributeModifier.getAmount() > 1.0) {
                return 1.0f + (float)damage;
            }
            return 1.0f;
        }
        return 1.0f;
    }

    public boolean isValid(Item item) {
        if (blacklistedItems.contains(Item.getIdFromItem(item))) {
            if (this.openInv.getValue().booleanValue() && !(this.mc.currentScreen instanceof GuiInventory)) {
                return false;
            }
            return true;
        }
        return false;
    }

    public void setSwordSlot() {
        float bestDamage = 1.0f;
        int bestSlot = -1;
        int i = 0;
        while (i < 9) {
            ItemStack item = this.mc.thePlayer.inventory.getStackInSlot(i);
            if (item.stackSize > 0) {
                float damage = 0.0f;
                if (item.getItem() instanceof ItemSword) {
                    damage = ((ItemSword)item.getItem()).getMaxDamage();
                } else if (item.getItem() instanceof ItemTool) {
                    damage = ((ItemTool)item.getItem()).toolMaterial.getDamageVsEntity();
                }
                if (damage > bestDamage) {
                    bestDamage = damage;
                    bestSlot = i;
                }
            }
            ++i;
        }
        if (bestSlot != -1 && bestSlot != this.mc.thePlayer.inventory.currentItem) {
            this.mc.thePlayer.inventory.currentItem = bestSlot;
        }
    }
}


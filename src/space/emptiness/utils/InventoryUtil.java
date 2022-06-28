package space.emptiness.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Slot;

public class InventoryUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

    private static final int hotBarSize = 9;
    private static final int inventoryContainerSize = 36;
    private static final int allInventorySize = (hotBarSize + inventoryContainerSize);

    public static int getHotBarSize() {
        return hotBarSize;
    }

    public static int getInventoryContainerSize() {
        return inventoryContainerSize;
    }

    public static int getAllInventorySize() {
        return allInventorySize;
    }

    public static Slot getSlot(int id) {
        return mc.thePlayer.inventoryContainer.getSlot(id);
    }

    public static int findEmptySlot() {
        for (int i = 0; i < hotBarSize; i++) {
            int id = i + 36;
            if (!InventoryUtil.getSlot(id).getHasStack()) {
                return id;
            }
        }
        return -1;
    }

    public static void swap(int currentSlot, int targetSlot) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, currentSlot, targetSlot, 2, mc.thePlayer);
    }
}

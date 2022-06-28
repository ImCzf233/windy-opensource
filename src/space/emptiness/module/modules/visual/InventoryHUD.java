package space.emptiness.module.modules.visual;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import space.emptiness.events.EventTarget;
import space.emptiness.events.rendering.EventRender2D;
import space.emptiness.module.Category;
import space.emptiness.module.Module;
import space.emptiness.module.value.Numbers;
import space.emptiness.utils.color.Colors;
import space.emptiness.utils.render.RenderUtil;
import space.emptiness.gui.font.CFontRenderer;
import space.emptiness.gui.font.FontLoaders;

import java.awt.*;

public class InventoryHUD extends Module {
    public final Numbers<Double> posX = new Numbers<Double>("PosX", "PosX", 0.0, 0.0, 1000.0, 1.0);
    public final Numbers<Double> posY = new Numbers<Double>("PosY", "PosY", 10.0, 0.0, 1000.0, 1.0);

    public InventoryHUD() {
        super("InventoryHUD", new String[]{"InventoryHUD", "InventoryHUD"}, Category.Visual);
        this.addValues(posX,posY);
        removed = true;
    }
    private int x2;
    int y2;
    @EventTarget
    public void renderinventory(EventRender2D e) {
   	 CFontRenderer font4 = FontLoaders.kiona20;
	 final float x2 = this.posX.getValue().floatValue();
     final float y2 = this.posY.getValue().floatValue();

        Color fillWithOpacity = new Color(180, 180, 180, 180);
        Color outlineWithOpacity = new Color(250, 200, 200, 255);
     //   //shaded box
     ///   Gui.drawRect(posX.getValue() + 1, posY.getValue() + 1, posX.getValue() + 161, posY.getValue() + 55, fillWithOpacity.getRGB());
  //   /  //top
      //  Gui.drawRect(posX.getValue(), posY.getValue(), posX.getValue() + 162, posY.getValue() + 1, outlineWithOpacity.getRGB());
       // //bottom
       // Gui.drawRect(posX.getValue(), posY.getValue() + 55, posX.getValue() + 162, posY.getValue() + 56, outlineWithOpacity.getRGB());
        //left
      //  Gui.drawRect(posX.getValue(), posY.getValue(), posX.getValue() + 1, posY.getValue() + 56, outlineWithOpacity.getRGB());
        //right
      //  Gui.drawRect(posX.getValue() + 161, posY.getValue(), posX.getValue() + 162, posY.getValue() + 56, outlineWithOpacity.getRGB());

        float boxWidth = 165;

     //   RenderUtil.rectangleBordered(posX.getValue() - 3 - 0.5D, posY.getValue() - 3 - 0.3D, posX.getValue() + boxWidth + 0.5D, posY.getValue() + 59 + 0.3D, 0.5D, Colors.getColor(60), Colors.getColor(10));
      RenderUtil.rectangleBordered((posX.getValue() - 3) + 0.5D, (posY.getValue() - 3) -10D, (posX.getValue() + boxWidth) - 0.6D, (posY.getValue() + 59) - 0.6D, 1.0D, Colors.getColor(60), Colors.getColor(10));
      // RenderUtil.rectangleBordered((posX.getValue() - 3) + 2.5D, (posY.getValue() - 3) + 2.5D, (posX.getValue() + boxWidth) - 2.5D, (posY.getValue() + 59) - 2.5D, 0.5D, Colors.getColor(22), Colors.getColor(12));
      ///  RenderUtil.drawGradientSideways(posX.getValue(), posY.getValue() - 0.5, posX.getValue() + boxWidth - 2.5, posY.getValue(), ColorCreator.createRainbowFromOffset(-4000, 1200),ColorCreator.createRainbowFromOffset(5000, 1200));
      //  RenderUtil.rectangle(posX.getValue(), posY.getValue(), posX.getValue() + boxWidth - 2.5, posY.getValue(), Colors.getColor(0, 110));
      //  RenderUtil.rectangleBordered(posX.getValue(), posY.getValue(), posX.getValue() + 162, posY.getValue() + 56, 0.3D, Colors.getColor(48), Colors.getColor(10));
      //  RenderUtil.rectangle(posX.getValue() + 1, posY.getValue() + 1, posX.getValue() + 161, posY.getValue() + 55, Colors.getColor(17));
        //RenderUtil.rectangle((double)((float)(sr.getScaledWidth() / 2 - boxWidth + 6) + 4.5F), (double)(yOffset / 4 + boxHeight + 8), (double)(sr.getScaledWidth() / 2 - boxWidth + 35), (double)(yOffset / 4 + boxHeight + 9), Colors.getColor(17));
        //items
        font4.drawString("Inventory",this.posX.getValue(),this.posY.getValue()-9.7D,new Color(255,255,255).getRGB(),false);
        GlStateManager.pushMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        ItemStack[] items = mc.thePlayer.inventory.mainInventory;
        for (int size = items.length, item = 9; item < size; ++item) {
            final int slotX = (int) (posX.getValue() + (item) % 9 * 18);
            final int slotY = (int) (posY.getValue() + 2 + (item / 9 - 1) * 18);
            mc.getRenderItem().renderItemAndEffectIntoGUI(items[item], slotX + 1, slotY);
            mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, items[item], slotX, slotY);
        }
        RenderHelper.disableStandardItemLighting();
        mc.getRenderItem().zLevel = 0.0F;
        GlStateManager.popMatrix();
    }
}

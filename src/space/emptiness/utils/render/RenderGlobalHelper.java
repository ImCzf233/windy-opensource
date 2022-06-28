package space.emptiness.utils.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
public final class RenderGlobalHelper {
    public Minecraft mc;
    public RenderGlobal rg, orig;
    public boolean fancy_graphics;
    public int ambient_occlusion;
    public String skin = "";

    public RenderGlobalHelper() {
        mc = Minecraft.getMinecraft();
        rg = new RenderGlobal(mc);
        rg.updateDestroyBlockIcons();
        orig = null;
    }

    public void getSettings() {
        fancy_graphics = mc.gameSettings.fancyGraphics;
        ambient_occlusion = mc.gameSettings.ambientOcclusion;
//        skin = mc.gameSettings.skin;
    }

    public boolean settingsChanged() {
        return
                fancy_graphics != mc.gameSettings.fancyGraphics ||
                ambient_occlusion != mc.gameSettings.ambientOcclusion ;
//                skin != mc.gameSettings.skin;
    }

    public void switchTo() {
        if (orig == null)
            orig = mc.renderGlobal;
        if (orig.theWorld != rg.theWorld) {
            rg.setWorldAndLoadRenderers(orig.theWorld);
            getSettings();
        } else if (settingsChanged()) {
            rg.loadRenderers();
            getSettings();
        }
        mc.renderGlobal = rg;
    }

    public void switchFrom() {
        if (orig != null)
            mc.renderGlobal = orig;
        orig = null;
    }
}
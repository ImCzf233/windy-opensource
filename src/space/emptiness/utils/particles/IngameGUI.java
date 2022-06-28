package space.emptiness.utils.particles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;


/**
 * This class is used to render in-game gui.
 * <p>
 * This class will extend of the default in-game gui
 * yet will have more features as we add our own to the already existing gui.
 *
 * @author Tecnio
 * @since 02/12/2021
 */

public final class IngameGUI extends GuiIngame {

    public static float ticks, ticksSinceClickgui;

    public IngameGUI(Minecraft mcIn) {
        super(mcIn);
    }
}
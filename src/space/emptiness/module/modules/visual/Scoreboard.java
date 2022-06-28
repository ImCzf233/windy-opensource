package space.emptiness.module.modules.visual;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import space.emptiness.module.Category;
import space.emptiness.module.Module;
import space.emptiness.module.value.Mode;
import space.emptiness.module.value.Numbers;
import space.emptiness.module.value.Option;

import java.awt.*;

public class Scoreboard extends Module {
    /**
     * GuiInGame.java ♥
     */
    static ScaledResolution res = new ScaledResolution(mc, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
    public static Mode<Enum> serveripm = new Mode<>("ServerIPMode","ServerIPMode",serverIPMode.values(),serverIPMode.Rainbow);
    public Option<Boolean> NoScore = new Option<Boolean>("NoScore", "NoScore", false);
    public static Numbers<Double> X = new Numbers<Double>("X", "X", 4.5, 0.0, Toolkit.getDefaultToolkit().getScreenSize().getWidth(), 1.0);
    public static Numbers<Double> Y = new Numbers<Double>("Y", "Y", 4.5, 0.0, Toolkit.getDefaultToolkit().getScreenSize().getHeight(), 1.0);
    //public static Numbers<Double> r = new Numbers<Double>("Red", "Red", 100.0, 0.0, 255.0, 1.0);
    //public static Numbers<Double> g = new Numbers<Double>("Green", "Green", 100.0, 0.0, 255.0, 1.0);
    //public static Numbers<Double> b = new Numbers<Double>("Blue", "Blue", 100.0, 0.0, 255.0, 1.0);
    public static Numbers<Double> RectAlpha = new Numbers<Double>("RectAlpha","RectAlpha",100.0,0.0,255.0,1.0);
    public static Option<Boolean> NoPotion = new Option<Boolean>("NoScorePoints","NoScorePoints",false);
    public static Option<Boolean> serverip = new Option<Boolean>("ServerIP","ServerIP",false);
    public Scoreboard() {
        super("Scoreboard",new String[]{}, Category.Visual);
        addValues(serveripm,NoScore,X,Y,RectAlpha,NoPotion,serverip);
    }
    public static enum serverIPMode {
        Normal,
        Rainbow,
    }
}

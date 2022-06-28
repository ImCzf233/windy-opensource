package space.emptiness.module.modules.visual;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import space.emptiness.events.EventTarget;
import space.emptiness.events.rendering.EventRender2D;
import space.emptiness.module.Category;
import space.emptiness.module.Module;
import space.emptiness.module.value.Numbers;
import space.emptiness.utils.RoundedUtil;
import space.emptiness.utils.StencilUtil;
import space.emptiness.utils.color.Colors;
import space.emptiness.utils.render.ColorUtil;
import space.emptiness.utils.render.RenderUtil;
import space.emptiness.Client;
import space.emptiness.gui.font.CFontRenderer;
import space.emptiness.gui.font.FontLoaders;

import java.awt.*;

public class SessionStats extends Module {
    private static int endTime;
    private static long startTime;
    private static int gamesPlayed;
    private static int killCount;
    public final Numbers<Double> posX = new Numbers<>("PosX", "PosX", 0.0, 0.0, 1000.0, 1.0);
    public final Numbers<Double> posY = new Numbers<>("PosY", "PosY", 10.0, 0.0, 1000.0, 1.0);
    public SessionStats() {
        super("Session", new String[]{}, Category.Visual);
        addValues(posX,posY);
    }

    float hourYAnimation;
    float minuteYAnimation1;
    float minuteYAnimation2;
    float secondYAnimation2;
    float secondYAnimation1;
    long durationInMillis = System.currentTimeMillis() - Client.playTimeStart;
    long second = (durationInMillis / 1000) % 60;
    long minute = (durationInMillis / (1000 * 60)) % 60;
    long hour = (durationInMillis / (1000 * 60 * 60)) % 24;
    float secondsSeperateWidthAnim1;
    float secondsSeperateWidthAnim2;
    float minuteSeperateWidthAnim1;
    float minuteSeperateWidthAnim2;


    float playtimeWidth = 20.5f;
    @EventTarget
    public void onRender2D(EventRender2D e){
        CFontRenderer font3 = FontLoaders.kiona20;
        CFontRenderer font4 = FontLoaders.kiona24;
        double xDif = mc.thePlayer.posX - mc.thePlayer.prevPosX;
        double zDif = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
        double lastDist = Math.sqrt(xDif * xDif + zDif * zDif) * 20.0D;
        String text2 =
                (Object) ((Object) EnumChatFormatting.WHITE) + String.format("%.2f", lastDist
        );


        String time;
        if(Minecraft.getMinecraft().isSingleplayer()) {
            time = "SinglePlayer";
        } else {

            time = String.format("%02dh %02dm %02ds", hour, minute, second);
        }

        float boxWidth = 165;
        int[] playTime = getPlayTime();
     //   FontLoaders.GBD18.drawStringWithShadow(playTime+" ",(Double) this.posX.getValue(),(Double)this.posY.getValue(),new Color(255,255,255).getRGB());
        RenderUtil.rectangleBordered((posX.getValue() - 3) + 0.5D, (posY.getValue() - 3) -9D, (posX.getValue() + boxWidth) - 0.6D, (posY.getValue() + 59) -4D, 1.0D, Colors.getColor(60), Colors.getColor(10));
        font4.drawString("SessionStats",this.posX.getValue(),this.posY.getValue()-7.7D,new Color(255,255,255).getRGB(),false);
        font3.drawString("PlayerName"+"                 "+mc.thePlayer.getName()
                ,this.posX.getValue(),this.posY.getValue()+8D,new Color(255,255,255).getRGB(),false);
        font3.drawString("PlayerSpeed"+"                   "+text2
                ,this.posX.getValue(),this.posY.getValue()+20D,new Color(255,255,255).getRGB(),false);
        font3.drawString("PlayTime"+"                    "+time
                ,this.posX.getValue(),this.posY.getValue()+32D,new Color(255,255,255).getRGB(),false);
        //font3.drawString("Kills"+"                                                    "+ KillAura.killed
    //            ,this.posX.getValue(),this.posY.getValue()+44D,new Color(255,255,255).getRGB(),false);
    }

    private void drawAnimatedPlaytime(float playtimeX, float y, float width) {
        int[] playTime = getPlayTime();
        RoundedUtil.drawRoundOutline(playtimeX, y + 21, playtimeWidth, 13, 6, .5f, ColorUtil.applyOpacity(Color.WHITE, 0), Color.WHITE);
        //RoundedUtil.drawRound(playtimeX, y + 22, playtimeWidth, 11, 6, new Color(30, 30, 30));
        StencilUtil.initStencilToWrite();
        RoundedUtil.drawRound(playtimeX, y + 22, playtimeWidth, 11, 6, new Color(30, 30, 30));
        StencilUtil.readStencilBuffer(1);


        float secondX = playtimeX + playtimeWidth - 7;
        FontLoaders.GBD18.drawString("s", secondX, y + 24, -1);

        int secondsFirstPlace = (playTime[2] % 10);

        secondYAnimation2 = (float) RenderUtil.animate(20 * secondsFirstPlace, secondYAnimation2, .02);

        secondsSeperateWidthAnim1 = (float) RenderUtil.animate(FontLoaders.GBD18.getStringWidth(String.valueOf(secondsFirstPlace)), secondsSeperateWidthAnim1, .05);

        secondX -= secondsSeperateWidthAnim1 + .5;

        for (int i = 0; i < 10; i++) {
            FontLoaders.GBD18.drawString(String.valueOf(i), secondX, y + 24 + (i * 20) - secondYAnimation2, -1);
        }

        int secondsSecondPlace = Math.floorDiv(playTime[2], 10);

        secondYAnimation1 = (float) RenderUtil.animate(20 * (secondsSecondPlace), secondYAnimation1, .02);

        secondsSeperateWidthAnim2 = (float) RenderUtil.animate(FontLoaders.GBD18.getStringWidth(String.valueOf(secondsSecondPlace)), secondsSeperateWidthAnim2, .05);


        secondX -= secondsSeperateWidthAnim2 + .5;

        for (int i = 0; i < 10; i++) {
            FontLoaders.GBD18.drawString(String.valueOf(i), secondX, y + 24 + (i * 20) - secondYAnimation1, -1);
        }

        if (playTime[1] > 0) {

            float minuteX = playtimeX + playtimeWidth - 27;

            FontLoaders.GBD18.drawString("m", minuteX, y + 24, -1);

            int minuteFirstPlace = (playTime[1] % 10);

            minuteYAnimation1 = (float) RenderUtil.animate(20 * minuteFirstPlace, minuteYAnimation1, .02);

            minuteSeperateWidthAnim1 = (float) RenderUtil.animate(FontLoaders.GBD18.getStringWidth(String.valueOf(minuteFirstPlace)), minuteSeperateWidthAnim1, .05);

            minuteX -= minuteSeperateWidthAnim1 + .5;

            for (int i = 0; i < 10; i++) {
                FontLoaders.GBD18.drawString(String.valueOf(i), minuteX, y + 24 + (i * 20) - minuteYAnimation1, -1);
            }

            int minuteSecondPlace = Math.floorDiv(playTime[1], 10);

            minuteYAnimation2 = (float) RenderUtil.animate(20 * (minuteSecondPlace), minuteYAnimation2, .02);

            minuteSeperateWidthAnim2 = (float) RenderUtil.animate(FontLoaders.GBD18.getStringWidth(String.valueOf(minuteSecondPlace)), minuteSeperateWidthAnim2, .05);

            minuteX -= minuteSeperateWidthAnim2 + .5;

            for (int i = 0; i < 10; i++) {
                FontLoaders.GBD18.drawString(String.valueOf(i), minuteX, y + 24 + (i * 20) - minuteYAnimation2, -1);
            }

            if (playTime[0] > 0) {
                hourYAnimation = (float) RenderUtil.animate(20 * (playTime[0] % 10), hourYAnimation, .02);

                FontLoaders.GBD18.drawString("h", playtimeX + playtimeWidth - 44, y + 24, -1);
                for (int i = 0; i < 10; i++) {
                    FontLoaders.GBD18.drawString(String.valueOf(i), playtimeX + playtimeWidth - 49, y + 24 + (i * 20) - hourYAnimation, -1);
                }

            }

        }


        StencilUtil.uninitStencilBuffer();
    }

    public static int[] getPlayTime() {
        long diff = getTimeDiff();
        long diffSeconds = 0, diffMinutes = 0, diffHours = 0;
        if (diff > 0) {
            diffSeconds = diff / 1000 % 60;
            diffMinutes = diff / (60 * 1000) % 60;
            diffHours = diff / (60 * 60 * 1000) % 24;
        }
       /* String str = (int) diffSeconds + "s";
        if (diffMinutes > 0) str = (int) diffMinutes + "m " + str;
        if (diffHours > 0) str = (int) diffHours + "h " + str;*/
        return new int[]{(int) diffHours, (int) diffMinutes, (int) diffSeconds};
    }

    public static long getTimeDiff() {
        return (endTime == -1 ? System.currentTimeMillis() : endTime) - startTime;
    }
    @Override
    public void onEnable() {


    }

    @Override
    public void onDisable() {second =0;
    durationInMillis =0;
    minute =0;
    hour = 0;
    }

    public static void reset() {
        startTime = System.currentTimeMillis();
        endTime = -1;
        gamesPlayed = 0;
        killCount = 0;
    }

}

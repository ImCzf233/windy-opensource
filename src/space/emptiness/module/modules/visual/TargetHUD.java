package space.emptiness.module.modules.visual;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.ResourceLocation;
import space.emptiness.events.EventTarget;
import space.emptiness.events.rendering.EventRender2D;
import space.emptiness.gui.font.FontLoaders;
import space.emptiness.module.Category;
import space.emptiness.module.Module;
import space.emptiness.module.modules.combat.KillAura;
import space.emptiness.module.modules.move.Speed;
import space.emptiness.module.value.Mode;
import space.emptiness.utils.render.RenderUtil;
import space.emptiness.utils.render.RenderUtils;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class TargetHUD extends Module {
    private Mode<Enum> mode = new Mode("Mode", "Mode", TargetHUDMode.values(), TargetHUDMode.Space);

    private EntityLivingBase space_lastEntity;
    private double space_animationX;

    public TargetHUD() {
        super("TargetHUD", new String[]{}, Category.Visual);
        addValues(this.mode);
    }





    private void renderSpace(ScaledResolution scaledResolution, EntityLivingBase entity) {
        final double x = scaledResolution.getScaledWidth_double() / 2.0 + 20;
        final double y = scaledResolution.getScaledHeight_double() / 2.0 + 20;
        final int int_x = scaledResolution.getScaledWidth() / 2 + 20;
        final int int_y = scaledResolution.getScaledHeight() / 2 + 20;
        final double percentageOfHealth = Math.min(80,((entity.getHealth() + entity.getAbsorptionAmount()) / (entity.getMaxHealth() + entity.getAbsorptionAmount())) * 80);

        if (space_lastEntity != entity) {
            space_animationX = percentageOfHealth;
        } else {
            space_animationX = RenderUtil.getAnimationStateEasing(space_animationX,percentageOfHealth,8);
        }

        String renderName = entity.getDisplayName().getFormattedText();

        if (renderName.isEmpty()) {
            renderName = entity.getName();
        }

        RenderUtil.drawRect(x,y,x + 120,y + 27.5,new Color(66, 66, 66).getRGB());

        RenderUtil.drawBorderedRect(x + 2,int_y + 2,x + 26,int_y + 26,1, RenderUtils.blend(new Color(255,0,0),new Color(255,255,0),entity.hurtResistantTime / 20.0).getRGB(), 0);

        if (entity instanceof EntityPlayer) {
            RenderUtils.glColor(RenderUtils.blend(new Color(255,0,0),new Color(255,255,255),entity.hurtResistantTime / 20.0).getRGB());
            GlStateManager.enableBlend();
            final NetworkPlayerInfo playerInfo = mc.getNetHandler().getPlayerInfo(entity.getUniqueID());

            if (playerInfo != null && playerInfo.hasLocationSkin()) {
                mc.getTextureManager().bindTexture(playerInfo.getLocationSkin());
                Gui.drawScaledCustomSizeModalRect(int_x + 3, int_y + 3, 8.0f, 8.0f, 8, 8, 22, 22, 64.0f, 64.0f);

                if (((EntityPlayer) entity).isWearing(EnumPlayerModelParts.HAT)) {
                    Gui.drawScaledCustomSizeModalRect(int_x + 3, int_y + 3,40.0F,8.0f,8,8,22, 22,64.0F,64.0F);
                }
            } else {
                mc.customFontRenderer.drawStringWithOutline("?",(float) (x + 11),(float) (y + 10),-1);
            }
        } else {
            mc.customFontRenderer.drawStringWithOutline("?",(float) (x + 11),(float) (y + 10),-1);
        }

        FontLoaders.GBD20.drawStringWithShadow(renderName,x + 28,y + 2,-1);
        FontLoaders.GBD20.drawStringWithOutline("♥",x + 28,y + 12,new Color(255, 0, 0).getRGB());
        RenderUtil.drawRect(x + 38,y + 10,x + 118,y + 20,new Color(0, 0, 0, 80).getRGB());
        RenderUtil.drawRect(x + 38,y + 10,x  + 38 + space_animationX,y + 20,RenderUtils.blend(new Color(0,255,0),new Color(255,0,0),space_animationX / 100.0).getRGB());
        final String healthString = ((int) (entity.getHealth() + entity.getAbsorptionAmount())) + " / " + ((int) (entity.getMaxHealth() + entity.getAbsorptionAmount()));
        mc.customFontRenderer.drawStringWithOutline(healthString,(float) x + 59,(float) y + 11.5f,-1);
        space_lastEntity = entity;
    }

    private void renderMinecraft(ScaledResolution scaledResolution,EntityLivingBase entity) {
        RenderUtil.resetColor();
        mc.fontRendererObj.drawStringWithShadow(entity.getName(), scaledResolution.getScaledWidth() / 2.0f - mc.fontRendererObj.getStringWidth(entity.getName()) / 2.0f, scaledResolution.getScaledHeight() / 2.0f - 30, 16777215);
        mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/icons.png"));

        int yOffset = 0;

        if (entity.getHealth() < 8) {
            yOffset += ThreadLocalRandom.current().nextInt(0, 4);
        }

        float maxHealth_X = (float) (scaledResolution.getScaledWidth() / 2) - 50;
        int maxHealth_Last = 9;
        int maxHealth_Y_Offset = 0;
        for (int i = 0; i < entity.getMaxHealth() / 2; i++) {
            mc.ingameGUI.drawTexturedModalRect(maxHealth_X, (float) (scaledResolution.getScaledHeight() / 2 - 20) + yOffset + maxHealth_Y_Offset, 16, 0, 9, 9);

            maxHealth_X += 10;

            if (i >= maxHealth_Last) {
                maxHealth_X = (float) (scaledResolution.getScaledWidth() / 2) - 50;
                maxHealth_Y_Offset += 4;
                maxHealth_Last += 10;
            }
        }

        float health_X = (float) (scaledResolution.getScaledWidth() / 2) - 50;
        int health_Last = 9;
        int health_Y_Offset = 0;
        boolean right = false;
        for (float i = 0; i < entity.getHealth() / 2; i += 0.5) {
            mc.ingameGUI.drawTexturedModalRect(health_X, (float) (scaledResolution.getScaledHeight() / 2 - 20) + yOffset + health_Y_Offset, 52 + (right ? 4 : 0), 0, 5, 9);
            health_X += 5;
            right = !right;

            if (i > health_Last) {
                health_X = (float) (scaledResolution.getScaledWidth() / 2) - 50;
                health_Y_Offset += 4;
                health_Last += 10;
                right = false;
            }
        }
    }
    static enum TargetHUDMode {
        Space, Minecraft;
    }
}


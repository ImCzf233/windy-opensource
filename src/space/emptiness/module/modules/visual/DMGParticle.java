package space.emptiness.module.modules.visual;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.List;

import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.EnumChatFormatting;

import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;


import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import space.emptiness.events.EventTarget;
import space.emptiness.events.rendering.EventRender3D;
import space.emptiness.events.world.EventLivingUpdate;
import space.emptiness.events.world.EventPacketSend;
import space.emptiness.events.world.EventUpdate;
import space.emptiness.module.Category;
import space.emptiness.module.Module;
import space.emptiness.module.value.Mode;
import space.emptiness.module.value.Numbers;
import space.emptiness.utils.Particles;
import space.emptiness.utils.render.RenderUtil;
import space.emptiness.utils.sub.Location;

public class DMGParticle extends Module
{
    private Mode<Enum> mode = new Mode("DisplayMode","DisplayMode",Mode2.values(),Mode2.Red);
    public Numbers<Double> sizeV = new Numbers<Double>("Size","Size",3.0,1.0,10.0,1.0);
    private HashMap<EntityLivingBase, Float> healthMap;
    private List<Particles> particles;
    private double varAnimal = sizeV.getValue() / 200;
    private boolean isCriti = false;

    enum Mode2{
        Red,
        Gold,
        Texture;
    }
    public DMGParticle() {
        super("DMGParticle", new String[] {"DamageParticle"}, Category.Visual);
        addValues(mode,sizeV);
        this.healthMap = new HashMap<EntityLivingBase, Float>();
        this.particles = new ArrayList<Particles>();
    }
    
    @EventTarget
    public void onLivingUpdate(EventLivingUpdate e) {
        this.setColor(new Color(183, 184, 205).getRGB());
	EntityLivingBase entity = (EntityLivingBase) e.getEntity();
        if (entity == this.mc.thePlayer) {
            return;
        } 
        if (!this.healthMap.containsKey(entity)) {
            this.healthMap.put(entity, (entity).getHealth());
        }
        float floatValue = this.healthMap.get(entity);
        float health = entity.getHealth();
        if (floatValue != health) { 
            String text;
            try {
                if (floatValue - health < 0.0f) {
                    text = EnumChatFormatting.GREEN + "" + roundToPlace((floatValue - health) * -1.0f, 1);//"§a"
                }else {
                    if (isCriti) {//EntityParticleEmitter.particleTypes == EnumParticleTypes.CRIT
                        text = (EnumChatFormatting.AQUA) + "" + roundToPlace(floatValue - health, 1);
                        isCriti = false;
                    } else {
                        text = (mode.getValue() == Mode2.Red ? EnumChatFormatting.RED : EnumChatFormatting.YELLOW) + "" + roundToPlace(floatValue - health, 1);//"§c" : "§e"
                    }
                }
                Location location = new Location(entity);
                location.setY(entity.getEntityBoundingBox().minY + (entity.getEntityBoundingBox().maxY - entity.getEntityBoundingBox().minY) / 2.0);
                location.setX(location.getX() - 0.5 + new Random(System.currentTimeMillis()).nextInt(5) * 0.1);
                location.setZ(location.getZ() - 0.5 + new Random(System.currentTimeMillis() + (0x203FF36645D9EA2EL ^ 0x203FF36645D9EA2FL)).nextInt(5) * 0.1);
                this.particles.add(new Particles(location, text));
                this.healthMap.remove(entity);
                this.healthMap.put(entity, entity.getHealth());
            } catch (NumberFormatException eq) {

            }


        }
    }

    @EventTarget
    public void onSendPacket(EventPacketSend e) {

        if (e.getPacket() instanceof C02PacketUseEntity) {
            C02PacketUseEntity c02 = (C02PacketUseEntity) e.getPacket();
            if (c02.getAction() == C02PacketUseEntity.Action.INTERACT) {
                isCriti = true;
            }
        }


    }
    
    @EventTarget
    public void onRender(EventRender3D e) {
        for (Particles p : this.particles) {
            double x = p.location.getX();
            this.mc.getRenderManager();
            double n = x - RenderManager.renderPosX;
            double y = p.location.getY();
            this.mc.getRenderManager();
            double n2 = y - RenderManager.renderPosY;
            double z = p.location.getZ();
            this.mc.getRenderManager();
            double n3 = z - RenderManager.renderPosZ;
            GlStateManager.pushMatrix();
            GlStateManager.enablePolygonOffset();
            GlStateManager.doPolygonOffset(1.0f, -1500000.0f);
            GlStateManager.translate((float)n, (float)n2, (float)n3);
            GlStateManager.rotate(-this.mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
            float textY;
            if (this.mc.gameSettings.thirdPersonView == 2) {
        	textY = -1.0f;
            }else {
        	textY = 1.0f;
            }
            GlStateManager.rotate(this.mc.getRenderManager().playerViewX, textY, 0.0f, 0.0f);
            //this.varAnimal = (int) RenderUtil.getAnimationState(this.varAnimal, sizeV.getValue() / 100, Math.max(10.0, Math.abs(this.varAnimal - (sizeV.getValue() / 100)) * 30.0) * 0.1);
            final double size = (sizeV.getValue() / 100);//0.03
            GlStateManager.scale(-size, -size, size);
            RenderUtil.enableGL2D();
            RenderUtil.disableGL2D();
            GL11.glDepthMask(false);
            if (mode.getValue() == Mode2.Texture) {
                mc.getTextureManager().bindTexture((new ResourceLocation("textures/gui/icons.png")));
                if (p.text.contains(EnumChatFormatting.GREEN + "")) {
                    mc.ingameGUI.drawTexturedModalRect((-(this.mc.fontRendererObj.getStringWidth(p.text) / 2)), (-(this.mc.fontRendererObj.FONT_HEIGHT - 1)),52, 0, 9, 9);
                } else {
                    mc.ingameGUI.drawTexturedModalRect((-(this.mc.fontRendererObj.getStringWidth(p.text) / 2)), (-(this.mc.fontRendererObj.FONT_HEIGHT - 1)), 124, 0, 9, 9);
                }
            } else {
                mc.fontRendererObj.drawStringWithShadow(p.text, (-(this.mc.fontRendererObj.getStringWidth(p.text) / 2)), (-(this.mc.fontRendererObj.FONT_HEIGHT - 1)), 0);
            }
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glDepthMask(true);
            GlStateManager.doPolygonOffset(1.0f, 1500000.0f);
            GlStateManager.disablePolygonOffset();
            GlStateManager.popMatrix();
        }
    }
    
    public static double roundToPlace(double p_roundToPlace_0_,int p_roundToPlace_2_) {
        if (p_roundToPlace_2_ < 0) {
            throw new IllegalArgumentException();
        }
        return new BigDecimal(p_roundToPlace_0_).setScale(p_roundToPlace_2_, RoundingMode.HALF_UP).doubleValue();
    }
    
    @EventTarget
    public void onUpdate(EventUpdate e) {
        try {
            this.particles.forEach(this::lambda$onUpdate$0);
        } catch (ConcurrentModificationException e1){

        }
    }
    
    private void lambda$onUpdate$0(Particles update) {
        ++update.ticks;
        if (update.ticks <= 10) {
            update.location.setY(update.location.getY() + update.ticks * 0.005);
        }
        if (update.ticks > 20) {//20
            this.particles.remove(update);
        }
    }
}
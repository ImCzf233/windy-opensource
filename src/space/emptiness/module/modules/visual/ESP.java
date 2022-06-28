/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package space.emptiness.module.modules.visual;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.GL11;
import space.emptiness.events.EventTarget;
import space.emptiness.events.rendering.EventRender2D;
import space.emptiness.events.rendering.EventRender3D;
import space.emptiness.events.world.EventUpdate;
import space.emptiness.module.Category;
import space.emptiness.module.Module;
import space.emptiness.module.modules.player.Teams;
import space.emptiness.module.value.Mode;
import space.emptiness.module.value.Option;
import space.emptiness.utils.math.Vec3f;
import space.emptiness.utils.render.RenderUtil;
import space.emptiness.utils.render.gl.GLUtils;
import space.emptiness.management.FriendManager;

import java.awt.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ESP
extends Module {
    private ArrayList<Vec3f> points = new ArrayList();
    public Mode<Enum> mode = new Mode("Mode", "mode", (Enum[])ESPMode.values(), (Enum)ESPMode.TwoDimensional);
    public Mode<Enum> True2Dmode = new Mode("2DMode", "2Dmode", (Enum[])TwoDMode.values(), (Enum)TwoDMode.Box);
	public static Option<Boolean> HEALTH = new Option<Boolean>("Health", "Health", true);
	public static Option<Boolean> player = new Option<Boolean>("Players", "Players", true);
	public static Option<Boolean> invis = new Option<Boolean>("Invis", "Invis", true);
	public static Option<Boolean> ARMOR = new Option<Boolean>("Armor", "Armor", true);
	private Map<EntityLivingBase, double[]> entityConvertedPointsMap;
	FontRenderer fr;
	public ESP() {
        super("ESP",new String[]{}, Category.Visual);
        this.addValues(this.mode, this.True2Dmode, this.HEALTH, this.player, this.invis, this.ARMOR);
        int i = 0;
        while (i < 8) {
            this.points.add(new Vec3f());
            ++i;
        }
		entityConvertedPointsMap = new HashMap<EntityLivingBase, double[]>();
		fr = mc.fontRendererObj;
		new ArrayList<String>();
        this.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)).getRGB());
    }
    
    @EventTarget
    private void onUpdate(EventUpdate e) {
    	this.setSuffix(mode.getValue());
    }

    @EventTarget
    public void onScreen(EventRender2D eventRender) {
        if (this.mode.getValue() == ESPMode.TwoDimensional) {
            GlStateManager.pushMatrix();
            ScaledResolution scaledRes = new ScaledResolution(this.mc, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
            double twoDscale = (double)scaledRes.getScaleFactor() / Math.pow(scaledRes.getScaleFactor(), 2.0);
            GlStateManager.scale(twoDscale, twoDscale, twoDscale);
            for (Object o : this.mc.theWorld.getLoadedEntityList()) {
                if (!(o instanceof EntityLivingBase) || o == this.mc.thePlayer || !(o instanceof EntityPlayer)) continue;
                EntityLivingBase ent = (EntityLivingBase)o;
                this.render(ent);
            }
            GlStateManager.popMatrix();
        }else if (mode.getValue() == ESPMode.True2D) {
    		GlStateManager.pushMatrix();
    		for (final Entity entity : entityConvertedPointsMap.keySet()) {
    			final EntityPlayer ent = (EntityPlayer) entity;
    			final double[] renderPositions = entityConvertedPointsMap.get(ent);
    			final double[] renderPositionsBottom = { renderPositions[4], renderPositions[5], renderPositions[6] };
    			final double[] renderPositionsX = { renderPositions[7], renderPositions[8], renderPositions[9] };
    			final double[] renderPositionsX2 = { renderPositions[10], renderPositions[11], renderPositions[12] };
    			final double[] renderPositionsZ = { renderPositions[13], renderPositions[14], renderPositions[15] };
    			final double[] renderPositionsZ2 = { renderPositions[16], renderPositions[17], renderPositions[18] };
    			final double[] renderPositionsTop1 = { renderPositions[19], renderPositions[20], renderPositions[21] };
    			final double[] renderPositionsTop2 = { renderPositions[22], renderPositions[23], renderPositions[24] };
    			GlStateManager.pushMatrix();
    			GlStateManager.scale(0.5, 0.5, 0.5);
    			if (((boolean) invis.getValue() || !ent.isInvisible()) && ent instanceof EntityPlayer
    					&& !(ent instanceof EntityPlayerSP)) {
    				try {
    					final double[] xValues = { renderPositions[0], renderPositionsBottom[0], renderPositionsX[0],
    							renderPositionsX2[0], renderPositionsZ[0], renderPositionsZ2[0], renderPositionsTop1[0],
    							renderPositionsTop2[0] };
    					final double[] yValues = { renderPositions[1], renderPositionsBottom[1], renderPositionsX[1],
    							renderPositionsX2[1], renderPositionsZ[1], renderPositionsZ2[1], renderPositionsTop1[1],
    							renderPositionsTop2[1] };
    					double x = renderPositions[0];
    					double y = renderPositions[1];
    					double endx = renderPositionsBottom[0];
    					double endy = renderPositionsBottom[1];
    					double[] array;
    					for (int length = (array = xValues).length, j = 0; j < length; ++j) {
    						final double bdubs = array[j];
    						if (bdubs < x) {
    							x = bdubs;
    						}
    					}
    					double[] array2;
    					for (int length2 = (array2 = xValues).length, k = 0; k < length2; ++k) {
    						final double bdubs = array2[k];
    						if (bdubs > endx) {
    							endx = bdubs;
    						}
    					}
    					double[] array3;
    					for (int length3 = (array3 = yValues).length, l = 0; l < length3; ++l) {
    						final double bdubs = array3[l];
    						if (bdubs < y) {
    							y = bdubs;
    						}
    					}
    					double[] array4;
    					for (int length4 = (array4 = yValues).length, n = 0; n < length4; ++n) {
    						final double bdubs = array4[n];
    						if (bdubs > endy) {
    							endy = bdubs;
    						}
    					}
    					final double xDiff = (endx - x) / 4.0;
    					final double x2Diff = (endx - x) / 4.0;
    					final double yDiff = xDiff;
    					int color = getColor(255, 255);
    					if (Teams.isOnSameTeam((Entity) ent)) {
    						color = getColor(0, 255, 0, 255);
    					} else if (ent.hurtTime > 0) {
    						color = getColor(255, 0, 0, 255);
    					} else if (ent.isInvisible()) {
    						color = getColor(255, 255, 0, 255);
    					} else {
    						color = getColor(255, 255, 255, 255);
    					}
    					if (True2Dmode.getValue() == TwoDMode.Box) {
    						RenderUtil.rectangleBordered(x + 0.5, y + 0.5, endx - 0.5, endy - 0.5, 1.0,
    								getColor(0, 0, 0, 0), color);
    						RenderUtil.rectangleBordered(x - 0.5, y - 0.5, endx + 0.5, endy + 0.5, 1.0, getColor(0, 0),
    								getColor(0, 150));
    						RenderUtil.rectangleBordered(x + 1.5, y + 1.5, endx - 1.5, endy - 1.5, 1.0, getColor(0, 0),
    								getColor(0, 150));
    					}
    					if (True2Dmode.getValue() == TwoDMode.CornerB) {
    						RenderUtil.rectangle(x + 0.5, y + 0.5, x + 1.5, y + yDiff + 0.5, color);
    						RenderUtil.rectangle(x + 0.5, endy - 0.5, x + 1.5, endy - yDiff - 0.5, color);
    						RenderUtil.rectangle(x - 0.5, y + 0.5, x + 0.5, y + yDiff + 0.5, getColor(0, 150));
    						RenderUtil.rectangle(x + 1.5, y + 2.5, x + 2.5, y + yDiff + 0.5, getColor(0, 150));
    						RenderUtil.rectangle(x - 0.5, y + yDiff + 0.5, x + 2.5, y + yDiff + 1.5, getColor(0, 150));
    						RenderUtil.rectangle(x - 0.5, endy - 0.5, x + 0.5, endy - yDiff - 0.5, getColor(0, 150));
    						RenderUtil.rectangle(x + 1.5, endy - 2.5, x + 2.5, endy - yDiff - 0.5, getColor(0, 150));
    						RenderUtil.rectangle(x - 0.5, endy - yDiff - 0.5, x + 2.5, endy - yDiff - 1.5,
    								getColor(0, 150));
    						RenderUtil.rectangle(x + 1.0, y + 0.5, x + x2Diff, y + 1.5, color);
    						RenderUtil.rectangle(x - 0.5, y - 0.5, x + x2Diff, y + 0.5, getColor(0, 150));
    						RenderUtil.rectangle(x + 1.5, y + 1.5, x + x2Diff, y + 2.5, getColor(0, 150));
    						RenderUtil.rectangle(x + x2Diff, y - 0.5, x + x2Diff + 1.0, y + 2.5, getColor(0, 150));
    						RenderUtil.rectangle(x + 1.0, endy - 0.5, x + x2Diff, endy - 1.5, color);
    						RenderUtil.rectangle(x - 0.5, endy + 0.5, x + x2Diff, endy - 0.5, getColor(0, 150));
    						RenderUtil.rectangle(x + 1.5, endy - 1.5, x + x2Diff, endy - 2.5, getColor(0, 150));
    						RenderUtil.rectangle(x + x2Diff, endy + 0.5, x + x2Diff + 1.0, endy - 2.5, getColor(0, 150));
    						RenderUtil.rectangle(endx - 0.5, y + 0.5, endx - 1.5, y + yDiff + 0.5, color);
    						RenderUtil.rectangle(endx - 0.5, endy - 0.5, endx - 1.5, endy - yDiff - 0.5, color);
    						RenderUtil.rectangle(endx + 0.5, y + 0.5, endx - 0.5, y + yDiff + 0.5, getColor(0, 150));
    						RenderUtil.rectangle(endx - 1.5, y + 2.5, endx - 2.5, y + yDiff + 0.5, getColor(0, 150));
    						RenderUtil.rectangle(endx + 0.5, y + yDiff + 0.5, endx - 2.5, y + yDiff + 1.5,
    								getColor(0, 150));
    						RenderUtil.rectangle(endx + 0.5, endy - 0.5, endx - 0.5, endy - yDiff - 0.5, getColor(0, 150));
    						RenderUtil.rectangle(endx - 1.5, endy - 2.5, endx - 2.5, endy - yDiff - 0.5, getColor(0, 150));
    						RenderUtil.rectangle(endx + 0.5, endy - yDiff - 0.5, endx - 2.5, endy - yDiff - 1.5,
    								getColor(0, 150));
    						RenderUtil.rectangle(endx - 1.0, y + 0.5, endx - x2Diff, y + 1.5, color);
    						RenderUtil.rectangle(endx + 0.5, y - 0.5, endx - x2Diff, y + 0.5, getColor(0, 150));
    						RenderUtil.rectangle(endx - 1.5, y + 1.5, endx - x2Diff, y + 2.5, getColor(0, 150));
    						RenderUtil.rectangle(endx - x2Diff, y - 0.5, endx - x2Diff - 1.0, y + 2.5, getColor(0, 150));
    						RenderUtil.rectangle(endx - 1.0, endy - 0.5, endx - x2Diff, endy - 1.5, color);
    						RenderUtil.rectangle(endx + 0.5, endy + 0.5, endx - x2Diff, endy - 0.5, getColor(0, 150));
    						RenderUtil.rectangle(endx - 1.5, endy - 1.5, endx - x2Diff, endy - 2.5, getColor(0, 150));
    						RenderUtil.rectangle(endx - x2Diff, endy + 0.5, endx - x2Diff - 1.0, endy - 2.5,
    								getColor(0, 150));
    					}
    					if (True2Dmode.getValue() == TwoDMode.CornerA) {
    					}
    					if (HEALTH.getValue()) {
    						final float health = ent.getHealth();
    						final float[] fractions = { 0.0f, 0.5f, 1.0f };
    						final Color[] colors = { Color.RED, Color.YELLOW, Color.GREEN };
    						final float progress = health / ent.getMaxHealth();
    						final Color customColor = (health >= 0.0f) ? blendColors(fractions, colors, progress).brighter()
    								: Color.RED;
    						final double difference = y - endy + 0.5;
    						final double healthLocation = endy + difference * progress;
    						RenderUtil.rectangleBordered(x - 6.5, y - 0.5, x - 2.5, endy, 1.0, getColor(0, 100),
    								getColor(0, 150));
    						RenderUtil.rectangle(x - 5.5, endy - 1.0, x - 3.5, healthLocation, customColor.getRGB());
    						if (-difference > 50.0) {
    							for (int i = 1; i < 10; ++i) {
    								final double dThing = difference / 10.0 * i;
    								RenderUtil.rectangle(x - 6.5, endy - 0.5 + dThing, x - 2.5, endy - 0.5 + dThing - 1.0,
    										getColor(0, 0, 0, 255));
    							}
    						}
    						if ((int) getIncremental(progress * 100.0f, 1.0) <= 40) {
    							GlStateManager.pushMatrix();
    							GlStateManager.scale(2.0f, 2.0f, 2.0f);
    							getIncremental(health * 5.0f, 1.0);
    							GlStateManager.popMatrix();
    						}
    					}
    				} catch (Exception ex) {
    				}
    			}
    			GlStateManager.popMatrix();
    			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    		}
    		GL11.glScalef(1.0f, 1.0f, 1.0f);
    		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    		GlStateManager.popMatrix();
    		RenderUtil.rectangle(0.0, 0.0, 0.0, 0.0, -1);
        }
    }
    
    @EventTarget
    private void onRender(EventRender3D eventRender) {
    	if(mode.getValue() == ESPMode.Flat) {
    		this.doFlat();
    	}else if(mode.getValue() == ESPMode.Box) {
    		this.drawHBox();
    	}else if(mode.getValue() == ESPMode.Cylinder) {
    		for (Object o2 : mc.theWorld.loadedEntityList) {
    			if (!(o2 instanceof EntityPlayer)) continue;
    			if (o2 == mc.thePlayer) continue;
                EntityPlayer ent = (EntityPlayer)o2;
                mc.getRenderManager();
                double posX = ent.lastTickPosX + (ent.posX - ent.lastTickPosX) * (double)mc.timer.renderPartialTicks - RenderManager.renderPosX;
                mc.getRenderManager();
                double posY = ent.lastTickPosY + (ent.posY - ent.lastTickPosY) * (double)mc.timer.renderPartialTicks - RenderManager.renderPosY;
                mc.getRenderManager();
                double posZ = ent.lastTickPosZ + (ent.posZ - ent.lastTickPosZ) * (double)mc.timer.renderPartialTicks - RenderManager.renderPosZ;
                if (ent instanceof EntityPlayer) {
                    if (ent == mc.thePlayer) continue;
                    if(ent.hurtTime > 0) {
                    	RenderUtil.drawWolframEntityESP(ent, (new Color(255, 102, 113)).getRGB(), posX, posY, posZ);
                    }else {
                    	RenderUtil.drawWolframEntityESP(ent, (new Color(186, 100, 200)).getRGB(), posX, posY, posZ);
                    }
	                continue;
                }
    		}
    	}else if(mode.getValue() == ESPMode.True2D) {
    		try {
    			updatePositions(eventRender.ticks);
    		} catch (Exception ex) {
    		}
    	}
    }
    
    private void drawHBox() {
        for (Object o2 : mc.theWorld.loadedEntityList) {
            float lineBlue;
            double width;
            float lineGreen;
            double height;
            float alpha;
            float red;
            float lineAlpha;
            float lineRed;
            float lineWdith;
            float blue;
            float green;
            if (!(o2 instanceof EntityPlayer)) continue;
            Minecraft var10001 = mc;
            if (o2 == mc.thePlayer) continue;
            EntityPlayer ent = (EntityPlayer)o2;
            Color color = ent.hurtTime > 0 ? new Color(-1618884) : new Color(255, 255, 255);
            mc.getRenderManager();
            double x2 = ent.lastTickPosX + (ent.posX - ent.lastTickPosX) * (double)mc.timer.renderPartialTicks - RenderManager.renderPosX;
            mc.getRenderManager();
            double y2 = ent.lastTickPosY + (ent.posY - ent.lastTickPosY) * (double)mc.timer.renderPartialTicks - RenderManager.renderPosY;
            mc.getRenderManager();
            double z2 = ent.lastTickPosZ + (ent.posZ - ent.lastTickPosZ) * (double)mc.timer.renderPartialTicks - RenderManager.renderPosZ;
            if (ent instanceof EntityPlayer) {
                if (ent == mc.thePlayer) continue;
                width = ent.getEntityBoundingBox().maxX - ent.getEntityBoundingBox().minX;
                height = ent.getEntityBoundingBox().maxY - ent.getEntityBoundingBox().minY + 0.25;
                red = ent.hurtTime > 0 ? 1.0f : 1.0f;
                green = ent.hurtTime > 0 ? 0.2f : 1.0f;
                blue = ent.hurtTime > 0 ? 0.0f : 1.0f;
                alpha = 0.2f;
                lineRed = ent.hurtTime > 0 ? 1.0f : 1.0f;
                lineGreen = ent.hurtTime > 0 ? 0.2f : 1.0f;
                lineBlue = ent.hurtTime > 0 ? 0.0f : 1.0f;
                lineAlpha = 0.2f;
                lineWdith = 1.0f;
                RenderUtil.drawEntityESP(x2, y2, z2, width, height, red, green, blue, 0.2f, lineRed, lineGreen, lineBlue, 0.2f, 1.0f);
                continue;
            }
            if (ent == mc.thePlayer) continue;
            width = ent.getEntityBoundingBox().maxX - ent.getEntityBoundingBox().minX + 0.1;
            height = ent.getEntityBoundingBox().maxY - ent.getEntityBoundingBox().minY + 0.25;
            red = ent.hurtTime > 0 ? 1.0f : 1.0f;
            green = ent.hurtTime > 0 ? 0.2f : 1.0f;
            blue = ent.hurtTime > 0 ? 0.0f : 1.0f;
            alpha = 0.2f;
            lineRed = ent.hurtTime > 0 ? 1.0f : 1.0f;
            lineGreen = ent.hurtTime > 0 ? 0.2f : 1.0f;
            lineBlue = ent.hurtTime > 0 ? 0.0f : 1.0f;
            lineAlpha = 0.2f;
            lineWdith = 1.0f;
            RenderUtil.drawEntityESP(x2, y2, z2, width, height, red, green, blue, 0.2f, lineRed, lineGreen, lineBlue, 0.2f, 1.0f);
        }
    }

	private static void drawVerticalLine(double xPos, double yPos, double xSize, double thickness, Color color) {
		Tessellator tesselator = Tessellator.getInstance();
		WorldRenderer worldRenderer = tesselator.getWorldRenderer();
		worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		worldRenderer.pos(xPos - xSize, yPos - thickness / 2.0D, 0.0D).color((float) color.getRed() / 255.0F,
				(float) color.getGreen() / 255.0F, (float) color.getBlue() / 255.0F, (float) color.getAlpha() / 255.0F)
				.endVertex();
		worldRenderer.pos(xPos - xSize, yPos + thickness / 2.0D, 0.0D).color((float) color.getRed() / 255.0F,
				(float) color.getGreen() / 255.0F, (float) color.getBlue() / 255.0F, (float) color.getAlpha() / 255.0F)
				.endVertex();
		worldRenderer.pos(xPos + xSize, yPos + thickness / 2.0D, 0.0D).color((float) color.getRed() / 255.0F,
				(float) color.getGreen() / 255.0F, (float) color.getBlue() / 255.0F, (float) color.getAlpha() / 255.0F)
				.endVertex();
		worldRenderer.pos(xPos + xSize, yPos - thickness / 2.0D, 0.0D).color((float) color.getRed() / 255.0F,
				(float) color.getGreen() / 255.0F, (float) color.getBlue() / 255.0F, (float) color.getAlpha() / 255.0F)
				.endVertex();
		tesselator.draw();
	}

	private void drawHorizontalLine(double xPos, double yPos, double ySize, double thickness, Color color) {
		Tessellator tesselator = Tessellator.getInstance();
		WorldRenderer worldRenderer = tesselator.getWorldRenderer();
		worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		worldRenderer.pos(xPos - thickness / 2.0D, yPos - ySize, 0.0D).color((float) color.getRed() / 255.0F,
				(float) color.getGreen() / 255.0F, (float) color.getBlue() / 255.0F, (float) color.getAlpha() / 255.0F)
				.endVertex();
		worldRenderer.pos(xPos - thickness / 2.0D, yPos + ySize, 0.0D).color((float) color.getRed() / 255.0F,
				(float) color.getGreen() / 255.0F, (float) color.getBlue() / 255.0F, (float) color.getAlpha() / 255.0F)
				.endVertex();
		worldRenderer.pos(xPos + thickness / 2.0D, yPos + ySize, 0.0D).color((float) color.getRed() / 255.0F,
				(float) color.getGreen() / 255.0F, (float) color.getBlue() / 255.0F, (float) color.getAlpha() / 255.0F)
				.endVertex();
		worldRenderer.pos(xPos + thickness / 2.0D, yPos - ySize, 0.0D).color((float) color.getRed() / 255.0F,
				(float) color.getGreen() / 255.0F, (float) color.getBlue() / 255.0F, (float) color.getAlpha() / 255.0F)
				.endVertex();
		tesselator.draw();
	}
    
	private boolean isValid(EntityLivingBase entity) {
		if (entity == this.mc.thePlayer) {
			return false;
		} else if (entity.getHealth() <= 0.0F) {
			return false;
		} else if (entity instanceof EntityPlayer) {
			return true;
		} else if (entity instanceof EntityAnimal) {
			return true;
		} else if (entity.isInvisible() && entity == this.mc.thePlayer) {
			return true;
		} else {
			return entity instanceof EntityMob;
		}
	}
    
	private void doFlat() {
		/*  78 */       Iterator var2 = mc.theWorld.playerEntities.iterator();
		/*  79 */       while (var2.hasNext()) {
		/*  80 */         EntityPlayer entity = (EntityPlayer)var2.next();
		/*  81 */         if (entity != mc.thePlayer) {
		/*  82 */           if (!isValid(entity)) {
		/*     */             return;
		/*     */           }
		/*  85 */           GL11.glPushMatrix();
		/*  86 */           GL11.glEnable(3042);
		/*  87 */           GL11.glDisable(2929);
		/*  88 */           GL11.glNormal3f(0.0F, 1.0F, 0.0F);
		/*  89 */           GlStateManager.enableBlend();
		/*  90 */           GL11.glBlendFunc(770, 771);
		/*  91 */           GL11.glDisable(3553);
		/*  92 */           float partialTicks = this.mc.timer.renderPartialTicks;
		/*     */           
		/*  94 */           this.mc.getRenderManager(); double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - RenderManager.renderPosX;
		/*     */           
		/*  96 */           this.mc.getRenderManager(); double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - RenderManager.renderPosY;
		/*     */           
		/*  98 */           this.mc.getRenderManager(); double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - RenderManager.renderPosZ;
		/*  99 */           float DISTANCE = mc.thePlayer.getDistanceToEntity(entity);
		/* 100 */           float SCALE = 0.035F;
		/* 101 */           SCALE /= 2.0F;
		/* 102 */           GlStateManager.translate((float)x, 
		/* 103 */               (float)y + entity.height + 0.5F - (entity.isChild() ? (entity.height / 2.0F) : 0.0F), (float)z);
		/* 104 */           GL11.glNormal3f(0.0F, 1.0F, 0.0F);
		/* 105 */           GlStateManager.rotate(-(this.mc.getRenderManager()).playerViewY, 0.0F, 1.0F, 0.0F);
		/* 106 */           GL11.glScalef(-SCALE, -SCALE, -SCALE);
		/* 107 */           Tessellator tesselator = Tessellator.getInstance();
		/* 108 */           WorldRenderer worldRenderer = tesselator.getWorldRenderer();
		Color gray;
							gray = new Color(-13710223);
		/* 110 */         //  Color gray = new Color(-13710223);
		/* 111 */           double thickness = (0.9F + DISTANCE * 0.1F);
		/* 112 */           double xLeft = -35.0D;
		/* 113 */           double xRight = 35.0D;
		/* 114 */           double yUp = 10.0D;
		/* 115 */           double yDown = 150.0D;
		/* 116 */           double size = 40.0D;
		/* 117 */           drawVerticalLine(xLeft + size / 2.0D + 1.0D, yUp + 1.0D, size / 2.0D, thickness, gray);
		/* 118 */           drawHorizontalLine(xLeft + 1.0D, yUp + size + 1.0D, size, thickness, gray);
		/* 119 */           drawVerticalLine(xRight - size / 2.0D + 1.0D, yUp + 1.0D, size / 2.0D, thickness, gray);
		/* 120 */           drawHorizontalLine(xRight + 1.0D, yUp + size + 1.0D, size, thickness, gray);
		/* 121 */           drawVerticalLine(xLeft + size / 2.0D + 1.0D, yDown + 1.0D, size / 2.0D, thickness, gray);
		/* 122 */           drawHorizontalLine(xLeft + 1.0D, yDown + 1.0D - size, size, thickness, gray);
		/* 123 */           drawVerticalLine(xRight - size / 2.0D + 1.0D, yDown + 1.0D, size / 2.0D, thickness, gray);
		/* 124 */           drawHorizontalLine(xRight + 1.0D, yDown - size + 1.0D, size, thickness, gray);
		/* 125 */           GL11.glEnable(3553);
		/* 126 */           GL11.glEnable(2929);
		/* 127 */           GlStateManager.disableBlend();
		/* 128 */           GL11.glDisable(3042);
		/* 129 */           GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		/* 130 */           GL11.glNormal3f(1.0F, 1.0F, 1.0F);
		/* 131 */           GL11.glPopMatrix();
		/*     */          
		/*     */       }
		/*     */     } 
	}
    
    private void render(Entity entity) {
        Entity extended = entity;
        RenderManager renderManager = this.mc.getRenderManager();
        Vec3f offset = extended.interpolate(this.mc.timer.renderPartialTicks).sub(new Vec3f(extended.getPosition().getX(),extended.getPosition().getY(),extended.getPosition().getZ())).add(0.0, 0.1, 0.0);
        if (entity.isInvisible()) {
            return;
        }
        AxisAlignedBB bb = entity.getEntityBoundingBox().offset(offset.getX() - RenderManager.renderPosX, offset.getY() - RenderManager.renderPosY, offset.getZ() - RenderManager.renderPosZ);
        this.points.get(0).setX(bb.minX).setY(bb.minY).setZ(bb.minZ);
        this.points.get(1).setX(bb.maxX).setY(bb.minY).setZ(bb.minZ);
        this.points.get(2).setX(bb.maxX).setY(bb.minY).setZ(bb.maxZ);
        this.points.get(3).setX(bb.minX).setY(bb.minY).setZ(bb.maxZ);
        this.points.get(4).setX(bb.minX).setY(bb.maxY).setZ(bb.minZ);
        this.points.get(5).setX(bb.maxX).setY(bb.maxY).setZ(bb.minZ);
        this.points.get(6).setX(bb.maxX).setY(bb.maxY).setZ(bb.maxZ);
        this.points.get(7).setX(bb.minX).setY(bb.maxY).setZ(bb.maxZ);
        float left = Float.MAX_VALUE;
        float right = 0.0f;
        float top = Float.MAX_VALUE;
        float bottom = 0.0f;
        for (Vec3f point : this.points) {
            Vec3f screen = point.toScreen();
            if (screen.getZ() < 0.0 || screen.getZ() >= 1.0) continue;
            if (screen.getX() < (double)left) {
                left = (float)screen.getX();
            }
            if (screen.getY() < (double)top) {
                top = (float)screen.getY();
            }
            if (screen.getX() > (double)right) {
                right = (float)screen.getX();
            }
            if (screen.getY() <= (double)bottom) continue;
            bottom = (float)screen.getY();
        }
        if (bottom <= 1.0f && right <= 1.0f) {
            return;
        }
        this.box(left, top, right, bottom);
        this.name(entity, left, top, right, bottom);
        if (!(entity instanceof EntityLivingBase)) {
            return;
        }
        EntityLivingBase living = (EntityLivingBase)entity;
        this.health(living, left, top, right, bottom);
    }

    private void box(float left, float top, float right, float bottom) {
        GL11.glColor4d((double)1.0, (double)1.0, (double)1.0, (double)0.5);
        RenderUtil.drawLine(left, top, right, top, 2.0f);
        RenderUtil.drawLine(left, bottom, right, bottom, 2.0f);
        RenderUtil.drawLine(left, top, left, bottom, 2.0f);
        RenderUtil.drawLine(right, top, right, bottom, 2.0f);
        RenderUtil.drawLine(left + 1.0f, top + 1.0f, right - 1.0f, top + 1.0f, 1.0f);
        RenderUtil.drawLine(left + 1.0f, bottom - 1.0f, right - 1.0f, bottom - 1.0f, 1.0f);
        RenderUtil.drawLine(left + 1.0f, top + 1.0f, left + 1.0f, bottom - 1.0f, 1.0f);
        RenderUtil.drawLine(right - 1.0f, top + 1.0f, right - 1.0f, bottom - 1.0f, 1.0f);
        RenderUtil.drawLine(left - 1.0f, top - 1.0f, right + 1.0f, top - 1.0f, 1.0f);
        RenderUtil.drawLine(left - 1.0f, bottom + 1.0f, right + 1.0f, bottom + 1.0f, 1.0f);
        RenderUtil.drawLine(left - 1.0f, top + 1.0f, left - 1.0f, bottom + 1.0f, 1.0f);
        RenderUtil.drawLine(right + 1.0f, top - 1.0f, right + 1.0f, bottom + 1.0f, 1.0f);
    }

    private void name(Entity entity, float left, float top, float right, float bottom) {
        this.mc.fontRendererObj.drawCenteredString(FriendManager.isFriend(entity.getName()) ? "\u00a7b" + FriendManager.getAlias(entity.getName()) : entity.getName(), (int)(left + right) / 2, (int)(top - (float)this.mc.fontRendererObj.FONT_HEIGHT - 2.0f + 1.0f), -1);
        if (((EntityPlayer)entity).getCurrentEquippedItem() != null) {
            String stack = ((EntityPlayer)entity).getCurrentEquippedItem().getDisplayName();
            this.mc.fontRendererObj.drawCenteredString(stack, (int)(left + right) / 2, (int)bottom, -1);
        }
    }

    private void health(EntityLivingBase entity, float left, float top, float right, float bottom) {
        float height = bottom - top;
        float currentHealth = entity.getHealth();
        float maxHealth = entity.getMaxHealth();
        float healthPercent = currentHealth / maxHealth;
        GLUtils.glColor(this.getHealthColor(entity));
        RenderUtil.drawLine(left - 5.0f, top + height * (1.0f - healthPercent) + 1.0f, left - 5.0f, bottom, 2.0f);
    }

    private int getHealthColor(EntityLivingBase player) {
        float f = player.getHealth();
        float f1 = player.getMaxHealth();
        float f2 = Math.max(0.0f, Math.min(f, f1) / f1);
        return Color.HSBtoRGB(f2 / 3.0f, 1.0f, 1.0f) | -16777216;
    }
    
	public static double getIncremental(final double val, final double inc) {
		final double one = 1.0 / inc;
		return Math.round(val * one) / one;
	}

	public static Color blendColors(final float[] fractions, final Color[] colors, final float progress) {
		if (fractions == null) {
			throw new IllegalArgumentException("Fractions can't be null");
		}
		if (colors == null) {
			throw new IllegalArgumentException("Colours can't be null");
		}
		if (fractions.length != colors.length) {
			throw new IllegalArgumentException("Fractions and colours must have equal number of elements");
		}
		final int[] indicies = getFractionIndicies(fractions, progress);
		final float[] range = { fractions[indicies[0]], fractions[indicies[1]] };
		final Color[] colorRange = { colors[indicies[0]], colors[indicies[1]] };
		final float max = range[1] - range[0];
		final float value = progress - range[0];
		final float weight = value / max;
		return blend(colorRange[0], colorRange[1], 1.0f - weight);
	}

	public static Color blend(final Color color1, final Color color2, final double ratio) {
		final float r = (float) ratio;
		final float ir = 1.0f - r;
		final float[] rgb1 = new float[3];
		final float[] rgb2 = new float[3];
		color1.getColorComponents(rgb1);
		color2.getColorComponents(rgb2);
		float red = rgb1[0] * r + rgb2[0] * ir;
		float green = rgb1[1] * r + rgb2[1] * ir;
		float blue = rgb1[2] * r + rgb2[2] * ir;
		if (red < 0.0f) {
			red = 0.0f;
		} else if (red > 255.0f) {
			red = 255.0f;
		}
		if (green < 0.0f) {
			green = 0.0f;
		} else if (green > 255.0f) {
			green = 255.0f;
		}
		if (blue < 0.0f) {
			blue = 0.0f;
		} else if (blue > 255.0f) {
			blue = 255.0f;
		}
		Color color3 = null;
		try {
			color3 = new Color(red, green, blue);
		} catch (IllegalArgumentException exp) {
			final NumberFormat nf = NumberFormat.getNumberInstance();
			System.out.println(String.valueOf(nf.format(red)) + "; " + nf.format(green) + "; " + nf.format(blue));
			exp.printStackTrace();
		}
		return color3;
	}

	public static int[] getFractionIndicies(final float[] fractions, final float progress) {
		final int[] range = new int[2];
		int startPoint;
		for (startPoint = 0; startPoint < fractions.length && fractions[startPoint] <= progress; ++startPoint) {
		}
		if (startPoint >= fractions.length) {
			startPoint = fractions.length - 1;
		}
		range[0] = startPoint - 1;
		range[1] = startPoint;
		return range;
	}

	private void updatePositions(float ticks) {
		entityConvertedPointsMap.clear();
		final float pTicks = ticks;
		for (final Entity e2 : mc.theWorld.getLoadedEntityList()) {
			if (e2 instanceof EntityPlayer) {
				final EntityPlayer ent;
				if ((ent = (EntityPlayer) e2) == mc.thePlayer) {
					continue;
				}
				double x = ent.lastTickPosX + (ent.posX - ent.lastTickPosX) * pTicks - mc.getRenderManager().viewerPosX
						+ 0.36;
				double y = ent.lastTickPosY + (ent.posY - ent.lastTickPosY) * pTicks - mc.getRenderManager().viewerPosY;
				double z = ent.lastTickPosZ + (ent.posZ - ent.lastTickPosZ) * pTicks - mc.getRenderManager().viewerPosZ
						+ 0.36;
				final double topY;
				y = (topY = y + (ent.height + 0.15));
				final double[] convertedPoints = RenderUtil.convertTo2D(x, y, z);
				final double[] convertedPoints2 = RenderUtil.convertTo2D(x - 0.36, y, z - 0.36);
				final double xd = 0.0;
				if (convertedPoints2[2] < 0.0) {
					continue;
				}
				if (convertedPoints2[2] >= 1.0) {
					continue;
				}
				x = ent.lastTickPosX + (ent.posX - ent.lastTickPosX) * pTicks - mc.getRenderManager().viewerPosX - 0.36;
				z = ent.lastTickPosZ + (ent.posZ - ent.lastTickPosZ) * pTicks - mc.getRenderManager().viewerPosZ - 0.36;
				final double[] convertedPointsBottom = RenderUtil.convertTo2D(x, y, z);
				y = ent.lastTickPosY + (ent.posY - ent.lastTickPosY) * pTicks - mc.getRenderManager().viewerPosY - 0.05;
				final double[] convertedPointsx = RenderUtil.convertTo2D(x, y, z);
				x = ent.lastTickPosX + (ent.posX - ent.lastTickPosX) * pTicks - mc.getRenderManager().viewerPosX - 0.36;
				z = ent.lastTickPosZ + (ent.posZ - ent.lastTickPosZ) * pTicks - mc.getRenderManager().viewerPosZ + 0.36;
				final double[] convertedPointsTop1 = RenderUtil.convertTo2D(x, topY, z);
				final double[] convertedPointsx2 = RenderUtil.convertTo2D(x, y, z);
				x = ent.lastTickPosX + (ent.posX - ent.lastTickPosX) * pTicks - mc.getRenderManager().viewerPosX + 0.36;
				z = ent.lastTickPosZ + (ent.posZ - ent.lastTickPosZ) * pTicks - mc.getRenderManager().viewerPosZ + 0.36;
				final double[] convertedPointsz = RenderUtil.convertTo2D(x, y, z);
				x = ent.lastTickPosX + (ent.posX - ent.lastTickPosX) * pTicks - mc.getRenderManager().viewerPosX + 0.36;
				z = ent.lastTickPosZ + (ent.posZ - ent.lastTickPosZ) * pTicks - mc.getRenderManager().viewerPosZ - 0.36;
				final double[] convertedPointsTop2 = RenderUtil.convertTo2D(x, topY, z);
				final double[] convertedPointsz2 = RenderUtil.convertTo2D(x, y, z);
				entityConvertedPointsMap.put((EntityLivingBase) ent,
						new double[] { convertedPoints[0], convertedPoints[1], xd, convertedPoints[2],
								convertedPointsBottom[0], convertedPointsBottom[1], convertedPointsBottom[2],
								convertedPointsx[0], convertedPointsx[1], convertedPointsx[2], convertedPointsx2[0],
								convertedPointsx2[1], convertedPointsx2[2], convertedPointsz[0], convertedPointsz[1],
								convertedPointsz[2], convertedPointsz2[0], convertedPointsz2[1], convertedPointsz2[2],
								convertedPointsTop1[0], convertedPointsTop1[1], convertedPointsTop1[2],
								convertedPointsTop2[0], convertedPointsTop2[1], convertedPointsTop2[2] });
			}
		}
	}

	public static int getColor(int red, int green, int blue) {
		return getColor(red, green, blue, 255);
	}

	public static int getColor(int brightness, int alpha) {
		return getColor(brightness, brightness, brightness, alpha);
	}

	public static int getColor(int red, int green, int blue, int alpha) {
		int color = 0;
		color |= alpha << 24;
		color |= red << 16;
		color |= green << 8;
		return color |= blue;
	}

    public static enum ESPMode {
        Outline,
        TwoDimensional,
        Flat,
        Box, 
        Cylinder,
        True2D;
    }
    
    public static enum TwoDMode {
    	Box, CornerA, CornerB;
    }

}


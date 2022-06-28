/*
 * Decompiled with CFR 0_132.
 */
package space.emptiness.module.modules.visual;


import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.GL11;
import space.emptiness.events.EventTarget;
import space.emptiness.events.rendering.EventRender2D;
import space.emptiness.gui.notification.Notification;
import space.emptiness.module.Category;
import space.emptiness.module.Module;
import space.emptiness.module.ModuleManager;
import space.emptiness.module.value.Mode;
import space.emptiness.module.value.Numbers;
import space.emptiness.module.value.Option;
import space.emptiness.utils.math.RotationUtil;
import space.emptiness.utils.render.Compass;
import space.emptiness.utils.render.Palette;
import space.emptiness.utils.render.RenderGlobalHelper;
import space.emptiness.utils.render.RenderUtil;
import space.emptiness.Client;
import space.emptiness.gui.font.CFontRenderer;
import space.emptiness.gui.font.FontLoaders;
import space.emptiness.module.modules.visual.UI.TabUI;

import java.awt.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class HUD extends Module{
	public int mirrorFBO;
	public int mirrorTex;
	public int mirrorDepth;
	public int framecount;

	public long renderEndNanoTime;
	public Compass compass = new Compass(325, 325, 1, 2, true);


	public TabUI tabui;
	//    private Option<Boolean> info = new Option<Boolean>("Information", "Information", true);
//    public static Option<Boolean> rainbow1 = new Option<Boolean>("Rainbow", "Rainbow", true);
	public static Numbers<Double> r = new Numbers("R", "R", 120.0, 0.0, 255.0, 5.0);
	public static Numbers<Double> g = new Numbers("G", "G", 120.0, 0.0, 255.0, 5.0);
	public static Numbers<Double> b = new Numbers("B", "B", 255.0, 0.0, 255.0, 5.0);
	public static Numbers<Double> alpha = new Numbers<Double>("Alpha", "Alpha", 120.0, 0.0, 255.0, 5.0);
	//    public static Numbers<Double> fps = new Numbers<Double>("FPSBoost", "FPSBoost", 200.0, 0.0, 1000.0, 50.0);
//    private Option<Boolean> customlogo = new Option<Boolean>("CHOCOWARE", "CHOCOWARE", false);
	public static Option<Boolean> rendertabui = new Option<Boolean>("TabUI", "TabUI", true);
	public static Option<Boolean> compassvalue = new Option<Boolean>("Compass", "Compass", true);
	public static Option<Boolean> wingvalue = new Option<Boolean>("Wing", "Wing", true);
	//    public static Option<Boolean> RearView = new Option<Boolean>("RearView", "RearView", true);
	public static Option<Boolean> blackhotbar = new Option<Boolean>("BlackHotBar", "BlackHotBar", true);
	public static Option<Boolean> notification = new Option<Boolean>("Notification", "Notification", true);
	public static Option<Boolean> crosshair = new Option<Boolean>("Crosshair", "Crosshair", true);
	public static Option<Boolean> fpshurtcam = new Option<Boolean>("FPSHurtCam", "FPSHurtCam", true);
	//    public static Option<Boolean> infobar = new Option<Boolean>("InfoBar", "InfoBar", true);
	public static Option<Boolean> onscreenradar = new Option<Boolean>("OnScreenRadar", "OnScreenRadar", true);
	public static Option<Boolean> movescoreboardx = new Option<Boolean>("MoveScoreBoardX", "MoveScoreBoardX", false);
	public static Option<Boolean> movescoreboardy = new Option<Boolean>("MoveScoreBoardY", "MoveScoreBoardY", true);
	public static Numbers<Double> x = new Numbers<Double>("X", "X", 0.0, 0.0, 960.0, 5.0);
	public static Numbers<Double> y = new Numbers<Double>("Y", "Y", 400.0, 0.0, 540.0, 5.0);
	public static Option<Boolean> hidedebugline = new Option<Boolean>("HideDebugLine", "HideDebugLine", true);
	private Mode<Enum> mode = new Mode("ModListMode", "ModListMode", (Enum[]) HUDMode.values(), (Enum) HUDMode.Simple);
	public static Mode<Enum> colormode = new Mode("Color", "Color", (Enum[]) ColorMode.values(),
			(Enum) ColorMode.Custom);
	public static Mode<Enum> widgetmode = new Mode("Widget", "Widget", (Enum[]) WidgetMode.values(),
			(Enum) WidgetMode.None);
	public static boolean shouldMove;
	public static boolean useFont;
	public RenderGlobalHelper mirrorRenderGlobal;
	public static String clientname;
	public static boolean shouldchangename = false;
	private String[] directions = new String[]{"S", "SW", "W", "NW", "N", "NE", "E", "SE"};
	public static float hue;
	int alpha2 = 0;

	public HUD() {
		super("HUD", new String[]{"gui"}, Category.Visual);
		this.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)).getRGB());
		this.setEnabled(true);
		this.setRemoved(true);
		this.addValues(this.mode, this.colormode, this.widgetmode, this.r, this.g, this.b, this.alpha, this.rendertabui,
				this.compassvalue, this.notification, this.crosshair, this.wingvalue, this.fpshurtcam,blackhotbar,
				this.onscreenradar, this.movescoreboardx, this.movescoreboardy, this.x, this.y, this.hidedebugline);

//        mirrorRenderGlobal = new RenderGlobalHelper();
//        mirrorFBO = ARBFramebufferObject.glGenFramebuffers();
//        mirrorTex = GL11.glGenTextures();
//        mirrorDepth = GL11.glGenTextures();
//        GL11.glBindTexture(GL11.GL_TEXTURE_2D, mirrorTex);
//        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
//        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
//        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB8, 320, 180, 0, GL11.GL_RGBA, GL11.GL_INT,
//                (java.nio.IntBuffer) null);
//        GL11.glBindTexture(GL11.GL_TEXTURE_2D, mirrorDepth);
//        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
//        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
//        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT, 320, 180, 0, GL11.GL_DEPTH_COMPONENT,
//                GL11.GL_INT, (java.nio.IntBuffer) null);
//        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
//        renderEndNanoTime = mc.entityRenderer.renderEndNanoTime;
	}


	@EventTarget
	private void renderHud(EventRender2D event) {
		if(this.notification.getValue()){
			Notification.drawNotifications();
		}
		ScaledResolution sr = new ScaledResolution(mc, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
		CFontRenderer font = FontLoaders.kiona18;
		CFontRenderer font2 = FontLoaders.kiona22;
		useFont = true;
//        tabui.init();

		if (compassvalue.getValue()) {
			renderCompass(sr);
		}
		if ((hue += 6.0 / 5.0f) > 255.0f) {
			hue = 0.0f;
		}
		float h = hue;
		if (!this.mc.gameSettings.showDebugInfo) {
			if (this.onscreenradar.getValue()) {
				ScaledResolution a = new ScaledResolution(mc, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
				GlStateManager.pushMatrix();
				double size = 50;
				double xOffset = a.getScaledWidth() / 2 - 24.5;
				double yOffset = a.getScaledHeight() / 2 - 25.2;
				double playerOffsetX = mc.thePlayer.posX;
				double playerOffSetZ = mc.thePlayer.posZ;
				for (int i = 0; i < mc.theWorld.loadedEntityList.size(); ++i) {
					Entity gay = mc.theWorld.loadedEntityList.get(i);
					if (gay instanceof EntityPlayer && gay != mc.thePlayer) {
						double loaddist = 0.2;
						float pTicks = mc.timer.renderPartialTicks;
						double pos1 = (((gay.posX + (gay.posX - gay.lastTickPosX) * pTicks) - playerOffsetX)
								* loaddist);
						double pos2 = (((gay.posZ + (gay.posZ - gay.lastTickPosZ) * pTicks) - playerOffSetZ)
								* loaddist);
						double cos = Math.cos(mc.thePlayer.rotationYaw * (Math.PI * 2 / 360));
						double sin = Math.sin(mc.thePlayer.rotationYaw * (Math.PI * 2 / 360));
						double rotY = -(pos2 * cos - pos1 * sin);
						double rotX = -(pos1 * cos + pos2 * sin);
						double var7 = 0 - rotX;
						double var9 = 0 - rotY;
						if (MathHelper.sqrt_double(var7 * var7 + var9 * var9) < size / 2 - 4) {
							float angle = (float) (Math.atan2(rotY - 0, rotX - 0) * 180 / Math.PI);
							double x = ((size / 2) * Math.cos(Math.toRadians(angle))) + xOffset + size / 2;
							double y = ((size / 2) * Math.sin(Math.toRadians(angle))) + yOffset + size / 2;
							GlStateManager.pushMatrix();
							GlStateManager.translate(x, y, 0);
							GlStateManager.rotate(angle, 0, 0, 1);
							GlStateManager.scale(1.5, 1.0, 1.0);
							drawESPCircle(0, 0, 3.2, 3);
							drawESPCircle(0, 0, 3.0, 3);
							drawESPCircle(0, 0, 2.5, 3);
							drawESPCircle(0, 0, 2.0, 3);
							drawESPCircle(0, 0, 1.5, 3);
							drawESPCircle(0, 0, 1.0, 3);
							drawESPCircle(0, 0, 0.5, 3);
							GlStateManager.popMatrix();
						}
					}
				}
				GlStateManager.popMatrix();
			}
			if (this.crosshair.getValue()) {
				int i = RenderUtil.width();
				int j = RenderUtil.height();
				if (!mc.thePlayer.moving()) {
					Gui.drawRect(i / 2 - 6, j / 2, i / 2 - 2, j / 2 + 1, new Color(HUD.r.getValue().intValue(),
							HUD.g.getValue().intValue(), HUD.b.getValue().intValue(), 255).getRGB());
					Gui.drawRect(i / 2 + 3, j / 2, i / 2 - 8 + 15, j / 2 + 1, new Color(HUD.r.getValue().intValue(),
							HUD.g.getValue().intValue(), HUD.b.getValue().intValue(), 255).getRGB());
					Gui.drawRect(i / 2, j / 2 - 6, i / 2 + 1, j / 2 - 2, new Color(HUD.r.getValue().intValue(),
							HUD.g.getValue().intValue(), HUD.b.getValue().intValue(), 255).getRGB());
					Gui.drawRect(i / 2, j / 2 + 3, i / 2 + 1, j / 2 - 8 + 15, new Color(HUD.r.getValue().intValue(),
							HUD.g.getValue().intValue(), HUD.b.getValue().intValue(), 255).getRGB());
				} else {
					Gui.drawRect(i / 2 - 9, j / 2, i / 2 - 5, j / 2 + 1, new Color(HUD.r.getValue().intValue(),
							HUD.g.getValue().intValue(), HUD.b.getValue().intValue(), 255).getRGB());
					Gui.drawRect(i / 2 + 6, j / 2, i / 2 - 5 + 15, j / 2 + 1, new Color(HUD.r.getValue().intValue(),
							HUD.g.getValue().intValue(), HUD.b.getValue().intValue(), 255).getRGB());
					Gui.drawRect(i / 2, j / 2 - 9, i / 2 + 1, j / 2 - 5, new Color(HUD.r.getValue().intValue(),
							HUD.g.getValue().intValue(), HUD.b.getValue().intValue(), 255).getRGB());
					Gui.drawRect(i / 2, j / 2 + 6, i / 2 + 1, j / 2 - 5 + 15, new Color(HUD.r.getValue().intValue(),
							HUD.g.getValue().intValue(), HUD.b.getValue().intValue(), 255).getRGB());
				}
			}
			String name;
			String direction;

//            if(RearView.getValue()) {
//            	Tessellator tes = Tessellator.instance;
//                WorldRenderer wr = tes.getWorldRenderer();
//                if (mc.theWorld == null || mc.currentScreen != null || mc.gameSettings.thirdPersonView != 0
//                        || mc.thePlayer == null) return;
//
//                boolean onLeft = true;
//
//                if (framecount++ % 2 == 0) {
//                    updateMirror(event.getPartialTicks());
//                }
//
//                GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_CURRENT_BIT | GL11.GL_POLYGON_BIT | GL11.GL_TEXTURE_BIT);
//                GL11.glMatrixMode(GL11.GL_PROJECTION);
//                GL11.glLoadIdentity();
//                GL11.glOrtho(0, mc.displayWidth, mc.displayHeight, 0, 1000, 3000);
//                GL11.glMatrixMode(GL11.GL_MODELVIEW);
//                GL11.glLoadIdentity();
//                GL11.glTranslatef(0, 0, -2000);
//
//                if (!onLeft) {
//                    GL11.glTranslatef(mc.displayWidth, 0f, 0f);
//                    GL11.glScalef(-1f, 1, 1);
//                    GL11.glFrontFace(GL11.GL_CW);
//                }
//
//                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//                GL11.glDisable(GL11.GL_TEXTURE_2D);
//                GL11.glDisable(GL11.GL_LIGHTING);
//                GL11.glColor3ub((byte) 24, (byte) 24, (byte) 24);
//
//
//                GL11.glColor3ub((byte) 255, (byte) 255, (byte) 255);
//                GL11.glBindTexture(GL11.GL_TEXTURE_2D, mirrorTex);
//                GL11.glEnable(GL11.GL_TEXTURE_2D);
//                wr.begin(6,DefaultVertexFormats.POSITION_TEX);
//                wr.addVertexWithUV(mc.displayWidth / 30, mc.displayHeight / 30, 0, onLeft ? 1 : 0, 1);
//                wr.addVertexWithUV(mc.displayWidth / 30, mc.displayHeight / 3, 0.2, onLeft ? 1 : 0, 0);
//                wr.addVertexWithUV(mc.displayWidth / 3, mc.displayHeight / 3, 0.2, onLeft ? 0 : 1, 0);
//                wr.addVertexWithUV(mc.displayWidth / 3, mc.displayHeight / 30, 0, onLeft ? 0 : 1, 1);
//                tes.draw();
//
//                GL11.glPopAttrib();
//                mc.entityRenderer.setupOverlayRendering();
//            }

			if (widgetmode.getValue() != WidgetMode.None) {
				int width = 0;
				int height = 0;
				switch (widgetmode.getValue().toString()) {
					case "Widget_1": {
						width = 512;
						height = 374;
						break;
					}
					case "Widget_2": {
						width = 460;
						height = 512;
						break;
					}
					case "Widget_3": {
						width = 512;
						height = 490;
						break;
					}
					case "Widget_4": {
						width = 469;
						height = 512;
						break;
					}
					case "Widget_5": {
						width = 314;
						height = 512;
						break;
					}
					case "Widget_6": {
						width = 512;
						height = 496;
						break;
					}
					case "Widget_7": {
						width = 464;
						height = 512;
						break;
					}
					case "Widget_8": {
						width = 512;
						height = 506;
						break;
					}
					case "Widget_9": {
						width = 505;
						height = 512;
						break;
					}
					case "Widget_10": {
						width = 512;
						height = 493;
						break;
					}
					case "Widget_11": {
						width = 505;
						height = 512;
						break;
					}
					case "Widget_12": {
						width = 464;
						height = 512;
						break;
					}
					case "Widget_13": {
						width = 460;
						height = 512;
						break;
					}
					case "Widget_14": {
						width = 512;
						height = 483;
						break;
					}
					case "Widget_15": {
						width = 489;
						height = 512;
						break;
					}
					case "Widget_16": {
						width = 474;
						height = 512;
						break;
					}
					case "Widget_17": {
						width = 428;
						height = 512;
						break;
					}
					case "Widget_18": {
						width = 395;
						height = 512;
						break;
					}
					case "Widget_19": {
						width = 512;
						height = 493;
						break;
					}
					case "Widget_20": {
						width = 467;
						height = 512;
						break;
					}
					case "Widget_21": {
						width = 486;
						height = 512;
						break;
					}
					case "Widget_22": {
						width = 494;
						height = 512;
						break;
					}
				}
				width *= 0.25;
				height *= 0.25;
//            	if(blackhotbar.getValue()) {
//            		RenderUtil.drawCustomImage(RenderUtil.width() - 100 - width,RenderUtil.height() - height - (mc.ingameGUI.getChatGUI().getChatOpen() ? 14 : 22),width,height,new ResourceLocation("Thunder/widget/" + widgetmode.getValue().toString() + ".png"));
//            	}else {
				RenderUtil.drawCustomImage(RenderUtil.width() / 2 + 300 - width,
						RenderUtil.height() - height - (mc.ingameGUI.getChatGUI().getChatOpen() ? 14 : 0), width,
						height, new ResourceLocation("client/widget/" + widgetmode.getValue().toString() + ".png"));
//            	}
			}
//            if ((boolean)this.customlogo.getValue()) {
//            	int rainbowTick = 0;
//            Color rainbow1 = new Color(Color.getHSBColor(h / 255.0f, 0.4f, 0.8f).getRed(), Color.getHSBColor(h / 255.0f, 0.4f, 0.8f).getGreen(), 255);
//            	HUD.shouldMove = true;
//            	CFontRenderer ffff = FontLoaders.GBD18;
//            	String text = "CHOCOWARE.net | " + mc.thePlayer.getName() + " | " + getRemoteIp() +  " | " + (Minecraft.getDebugFPS() + fps.getValue().intValue()) + "FPS";
//            	int width = Math.max(100, ffff.getStringWidth(text) + 6);
//            	Gui.drawRect(2, 5, 2 + width, 22, new Color(0,0,0,120).getRGB());
//            	Gui.drawRect(2, 5, 2 + width, 6.5, colormode.getValue() == ColorMode.Rainbow ? rainbow1.getRGB() : new Color(r.getValue().intValue(),g.getValue().intValue(),b.getValue().intValue(),255).getRGB());
//            	ffff.drawCenteredStringWithShadow(text, width / 2 + 2, (22 + 6.5) / 2 - ffff.getStringHeight(text) / 2, new Color(255,255,255).getRGB());
//            }
//            else if (!(boolean)this.customlogo.getValue()) {
			int rainbowTick = 0;
			SimpleDateFormat df = new SimpleDateFormat("HH:mm");
			HUD.shouldMove = false;


			Color rainbow1 = new Color(Color.HSBtoRGB((float)((double)this.mc.thePlayer.ticksExisted / 50.0 + Math.sin((double)rainbowTick / 50.0 * 1.6)) % 1.0f, 0.5f, 1.0f));


			final CFontRenderer cFontRenderer3 = font2;
			Client.instance.getClass();
			if (!shouldchangename) {
				clientname = Client.name;
			}
			String append5 = clientname.substring(0, 1);
			String append6 = clientname.substring(1) + " (" + df.format(new Date(System.currentTimeMillis())) + ")";
			Client.instance.getClass();
			cFontRenderer3.drawStringWithShadow(append5.toString(), 2.0, 2.0,
					colormode.getValue() != ColorMode.Custom ? rainbow1.getRGB()
							: new Color(r.getValue().intValue(), g.getValue().intValue(), b.getValue().intValue(), 255)
							.getRGB());
			cFontRenderer3.drawStringWithShadow(append6.toString(), 2.0 + cFontRenderer3.getStringWidth(append5) + 0.5,
					2.0, new Color(255, 255, 255).getRGB());
//            }
			ArrayList<Module> sorted = new ArrayList<Module>();
			Client.instance.getModuleManager();
			for (Module m : ModuleManager.getModules()) {
				if ((!m.isEnabled() && m.wasArrayRemoved() && m.getAnimx() == 0) || m.wasRemoved())
					continue;
				sorted.add(m);
			}
			sorted.sort((o1,
						 o2) -> font
					.getStringWidth(o2.getSuffix().isEmpty() ? o2.getName()
							: String.format("%s %s", o2.getName(), o2.getSuffix()))
					- font.getStringWidth(o1.getSuffix().isEmpty() ? o1.getName()
					: String.format("%s %s", o1.getName(), o1.getSuffix())));
			int y = 1;
			int rainbowTick1 = 0;

			for (Module m : sorted) {
				int nextIndex = sorted.indexOf(m) + 1;
				name = m.getSuffix().isEmpty() ? m.getName() : String.format("%s %s", m.getName(), m.getSuffix());
				Module nextModule = null;
				if (sorted.size() > nextIndex) {
					nextModule = this.getNextEnabledModule(sorted, nextIndex);
				}

				if (m.isEnabled()) {
					m.setArrayRemoved(false);
					if (mc.thePlayer.ticksExisted >= 30) {
						m.setAnimx(Math.min(m.getAnimx() + font.getStringWidth(name) / 35, font.getStringWidth(name)));
					} else {
						m.setAnimx(font.getStringWidth(name));
					}
				} else {
					if (m.getAnimx() <= 0) {
						m.setArrayRemoved(true);
					} else {
						if (mc.thePlayer.ticksExisted >= 30) {
							m.setAnimx(Math.max(m.getAnimx() - font.getStringWidth(name) / 35, 0));
						} else {
							m.setAnimx(0);
						}
					}
				}

				float x = RenderUtil.width() - m.getAnimx();


				Color rainbow= new Color(Color.HSBtoRGB((float)((double)this.mc.thePlayer.ticksExisted / 50.0 + Math.sin((double)rainbowTick / 50.0 * 1.6)) % 1.0f, 0.5f, 1.0f));


				if (h > 255.0f) {
					h = 0.0f;
				}
				int color;
				color = Palette
						.fade(new Color(r.getValue().intValue(), g.getValue().intValue(), b.getValue().intValue(), 255),
								100, sorted.indexOf(nextModule) * 2 + 50)
						.getRGB();
				if (colormode.getValue() != ColorMode.Fade) {
					switch (mode.getModeAsString()) {
						case "Simple": {
							RenderUtil.drawBorderedRect(x - 6, y - 1, RenderUtil.width(), y + 11, 0.1f,
									new Color(0, 0, 0, 0).getRGB(),
									new Color(0, 0, 0, alpha.getValue().intValue()).getRGB());
							font.drawStringWithShadow(name, x - 3.0f, y + 2,
									colormode.getValue() == ColorMode.Rainbow ? rainbow.getRGB()
											: new Color(r.getValue().intValue(), g.getValue().intValue(),
											b.getValue().intValue(), 255).getRGB());
							break;
						}
						case "Rect": {
							RenderUtil.drawBorderedRect(x - 7, y - 1, RenderUtil.width(), y + 11, 0.1f,
									new Color(0, 0, 0, 0).getRGB(),
									new Color(0, 0, 0, alpha.getValue().intValue()).getRGB());
							// RenderUtil.drawBorderedRect(x - 9, y + 1, x - 7, y + 9, 0.1f, new
							// Color(0,0,0,0).getRGB(), new Color(0,0,0,90).getRGB());
							// Gui.drawRect(x - 9, y + 1, x - 7, y + 9, new Color(0,0,0,90).getRGB());
							Gui.drawFilledCircle(x - 7, y + 1, 2, new Color(0, 0, 0, alpha.getValue().intValue()).getRGB(),
									1);
							Gui.drawFilledCircle(x - 7, y + 9, 2, new Color(0, 0, 0, alpha.getValue().intValue()).getRGB(),
									2);

							font.drawStringWithShadow(name, x - 3.0f, y + 2,
									colormode.getValue() == ColorMode.Rainbow ? rainbow.getRGB()
											: new Color(r.getValue().intValue(), g.getValue().intValue(),
											b.getValue().intValue(), 255).getRGB());
							RenderUtil.drawBorderedRect(x - 6, y + 1, x - 6, y + 9, 1.0f,
									colormode.getValue() == ColorMode.Rainbow ? rainbow.getRGB()
											: new Color(r.getValue().intValue(), g.getValue().intValue(),
											b.getValue().intValue(), 255).getRGB(),
									colormode.getValue() == ColorMode.Rainbow ? rainbow.getRGB()
											: new Color(r.getValue().intValue(), g.getValue().intValue(),
											b.getValue().intValue(), 255).getRGB());
							Gui.drawRect(x - 9, y + 1, x - 7, y + 9,
									new Color(0, 0, 0, alpha.getValue().intValue()).getRGB());
							break;
						}
						case "OutLine": {
							Gui.drawRect(x - 7d, y - 1d, x - 6d, y + 11d,
									colormode.getValue() == ColorMode.Rainbow ? rainbow.getRGB()
											: new Color(r.getValue().intValue(), g.getValue().intValue(),
											b.getValue().intValue(), 255).getRGB());
							if (nextModule != null) {
								if (this.getNextEnabledModule(sorted, nextIndex).getSuffix() != "") {
									Gui.drawRect(x - 7d, y + 11d, RenderUtil.width()
													- font.getStringWidth(this.getNextEnabledModule(sorted, nextIndex).getName()
													+ " " + this.getNextEnabledModule(sorted, nextIndex).getSuffix())
													- 6d, y + 12d,
											colormode.getValue() == ColorMode.Rainbow ? rainbow.getRGB()
													: new Color(r.getValue().intValue(), g.getValue().intValue(),
													b.getValue().intValue(), 255).getRGB());
								} else {
									Gui.drawRect(x - 7d, y + 11d,
											RenderUtil.width() - font.getStringWidth(
													this.getNextEnabledModule(sorted, nextIndex).getName()) - 6d,
											y + 12d,
											colormode.getValue() == ColorMode.Rainbow ? rainbow.getRGB()
													: new Color(r.getValue().intValue(), g.getValue().intValue(),
													b.getValue().intValue(), 255).getRGB());
								}
							} else {
								Gui.drawRect(x - 7d, y + 11d, RenderUtil.width(), y + 12d,
										colormode.getValue() == ColorMode.Rainbow ? rainbow.getRGB()
												: new Color(r.getValue().intValue(), g.getValue().intValue(),
												b.getValue().intValue(), 255).getRGB());
							}
							RenderUtil.drawBorderedRect(x - 6, y - 1, RenderUtil.width(), y + 11, 0.1f,
									new Color(0, 0, 0, 0).getRGB(),
									new Color(0, 0, 0, alpha.getValue().intValue()).getRGB());
							font.drawStringWithShadow(name, x - 3.0f, y + 2,
									colormode.getValue() == ColorMode.Rainbow ? rainbow1.getRGB()
											: new Color(r.getValue().intValue(), g.getValue().intValue(),
											b.getValue().intValue(), 255).getRGB());
							break;
						}
						case "Side": {
							RenderUtil.drawBorderedRect(x - 7, y - 1, RenderUtil.width(), y + 11, 0.1f,
									new Color(0, 0, 0, 0).getRGB(),
									new Color(0, 0, 0, alpha.getValue().intValue()).getRGB());

							font.drawStringWithShadow(name, x - 5.0f, y + 2,
									colormode.getValue() == ColorMode.Rainbow ? rainbow.getRGB()
											: new Color(r.getValue().intValue(), g.getValue().intValue(),
											b.getValue().intValue(), 255).getRGB());
							RenderUtil.drawBorderedRect(RenderUtil.width(), y - 1, RenderUtil.width() - 2, y + 11, 1.0f,
									colormode.getValue() == ColorMode.Rainbow ? rainbow.getRGB()
											: new Color(r.getValue().intValue(), g.getValue().intValue(),
											b.getValue().intValue(), 255).getRGB(),
									colormode.getValue() == ColorMode.Rainbow ? rainbow.getRGB()
											: new Color(r.getValue().intValue(), g.getValue().intValue(),
											b.getValue().intValue(), 255).getRGB());
							break;
						}
					}
				} else {
					switch (mode.getModeAsString()) {
						case "Simple": {
							RenderUtil.drawBorderedRect(x - 6, y - 1, RenderUtil.width(), y + 11, 0.1f,
									new Color(0, 0, 0, 0).getRGB(),
									new Color(0, 0, 0, alpha.getValue().intValue()).getRGB());
							font.drawStringWithShadow(name, x - 3.0f, y + 2, color);
							break;
						}
						case "Rect": {
							RenderUtil.drawBorderedRect(x - 7, y - 1, RenderUtil.width(), y + 11, 0.1f,
									new Color(0, 0, 0, 0).getRGB(),
									new Color(0, 0, 0, alpha.getValue().intValue()).getRGB());
//	                    	RenderUtil.drawBorderedRect(x - 9, y + 1, x - 7, y + 9, 0.1f, new Color(0,0,0,0).getRGB(), new Color(0,0,0,90).getRGB());
//	                    	Gui.drawRect(x - 9, y + 1, x - 7, y + 9, new Color(0,0,0,90).getRGB());
							Gui.drawFilledCircle(x - 7, y + 1, 2, new Color(0, 0, 0, alpha.getValue().intValue()).getRGB(),
									1);
							Gui.drawFilledCircle(x - 7, y + 9, 2, new Color(0, 0, 0, alpha.getValue().intValue()).getRGB(),
									2);

							font.drawStringWithShadow(name, x - 3.0f, y + 2, color);
							RenderUtil.drawBorderedRect(x - 6, y + 1, x - 6, y + 9, 1.0f, color, color);
							Gui.drawRect(x - 9, y + 1, x - 7, y + 9,
									new Color(0, 0, 0, alpha.getValue().intValue()).getRGB());
							break;
						}
						case "OutLine": {
							Gui.drawRect(x - 7d, y - 1d, x - 6d, y + 11d, color);
							if (nextModule != null) {
								if (this.getNextEnabledModule(sorted, nextIndex).getSuffix() != "") {
									Gui.drawRect(x - 7d, y + 11d, RenderUtil.width()
											- font.getStringWidth(this.getNextEnabledModule(sorted, nextIndex).getName()
											+ " " + this.getNextEnabledModule(sorted, nextIndex).getSuffix())
											- 6d, y + 12d, color);
								} else {
									Gui.drawRect(x - 7d, y + 11d,
											RenderUtil.width() - font.getStringWidth(
													this.getNextEnabledModule(sorted, nextIndex).getName()) - 6d,
											y + 12d, color);
								}
							} else {
								Gui.drawRect(x - 7d, y + 11d, RenderUtil.width(), y + 12d, color);
							}
							RenderUtil.drawBorderedRect(x - 6, y - 1, RenderUtil.width(), y + 11, 0.1f,
									new Color(0, 0, 0, 0).getRGB(),
									new Color(0, 0, 0, alpha.getValue().intValue()).getRGB());
							font.drawStringWithShadow(name, x - 3.0f, y + 2, color);
							break;
						}
						case "Side": {
							RenderUtil.drawBorderedRect(x - 7, y - 1, RenderUtil.width(), y + 11, 0.1f,
									new Color(0, 0, 0, 0).getRGB(),
									new Color(0, 0, 0, alpha.getValue().intValue()).getRGB());

							font.drawStringWithShadow(name, x - 5.0f, y + 2, color);
							RenderUtil.drawBorderedRect(RenderUtil.width(), y - 1, RenderUtil.width() - 2, y + 11, 1.0f,
									color, color);
							break;
						}
					}
				}

				if (++rainbowTick1 > 100) {
					rainbowTick1 = 0;
				}
				y += 12;
				h += 9.0f;
			}
			double xDif = mc.thePlayer.posX - mc.thePlayer.prevPosX;
			double zDif = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
			double lastDist = Math.sqrt(xDif * xDif + zDif * zDif) * 20.0D;
			String text = (Object) ((Object) EnumChatFormatting.WHITE) + "X : "
					+ (Object) ((Object) EnumChatFormatting.GRAY) + MathHelper.floor_double(this.mc.thePlayer.posX)
					+ "   " + (Object) ((Object) EnumChatFormatting.WHITE) + "Y : "
					+ (Object) ((Object) EnumChatFormatting.GRAY) + MathHelper.floor_double(this.mc.thePlayer.posY)
					+ "   " + (Object) ((Object) EnumChatFormatting.WHITE) + "Z : "
					+ (Object) ((Object) EnumChatFormatting.GRAY) + MathHelper.floor_double(this.mc.thePlayer.posZ);
			String text2 = (((Object) EnumChatFormatting.WHITE) + "Speed : "
					+ (Object) ((Object) EnumChatFormatting.GRAY) + String.format("%.2f", lastDist)
			);
			int ychat;
			int n = ychat = this.mc.ingameGUI.getChatGUI().getChatOpen() ? 24 : 10;
//                if (this.info.getValue().booleanValue()) {
//                    font.drawStringWithShadow(text, 4.0, new ScaledResolution(this.mc).getScaledHeight() - ychat, new Color(11, 12, 17).getRGB());
//                	if(!mc.gameSettings.keyBindPlayerList.isKeyDown()) {
			font.drawStringWithShadow(text, 2, RenderUtil.height() - n, new Color(11, 12, 17).getRGB());
			if (!mc.ingameGUI.getChatGUI().getChatOpen()) {
				font.drawStringWithShadow(text2, 2, RenderUtil.height() - n - 12, new Color(11, 12, 17).getRGB());
			}
//                	}
//                    font.drawStringWithShadow((Object)((Object)EnumChatFormatting.GRAY) + "FPS: " + (Object)((Object)EnumChatFormatting.WHITE) + Minecraft.debugFPS, 2.0, shouldMove ? 90 : 75, -1);
			this.drawPotionStatus(new ScaledResolution(this.mc, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight));
			direction = this.directions[RotationUtil.wrapAngleToDirection(this.mc.thePlayer.rotationYaw,
					this.directions.length)];
			Client.instance.getClass();
			Client.instance.getClass();
//                    font.drawStringWithShadow("[" + direction + "]", font.getStringWidth(String.valueOf(Client.name) + " " + 0.6 + 10), shouldMove ? 15 : 2, new Color(102, 172, 255).getRGB());
			for (int slot = 3, xOffset = 0; slot >= 0; slot--) {
				ItemStack stack = mc.thePlayer.inventory.armorItemInSlot(slot);

				GuiIngame gi = new GuiIngame(mc);
				if (stack != null) {
					mc.getRenderItem().renderItemIntoGUI(stack, RenderUtil.width() / 2 + 15 - xOffset,
							RenderUtil.height() - 55
									- ((mc.thePlayer.isInsideOfMaterial(Material.water) && mc.thePlayer.getAir() > 0)
									? 10
									: 0));
					GL11.glDisable(GL11.GL_DEPTH_TEST);
					GL11.glScalef(0.5F, 0.5F, 0.5F);
					// mc.fontRendererObj.drawStringWithShadow(stack.getMaxDamage() -
					// stack.getItemDamage() + "", sr2.getScaledWidth() + 29 - xOffset * 2 +
					// (stack.getMaxDamage() - stack.getItemDamage() >= 100 ? 4 :
					// (stack.getMaxDamage() - stack.getItemDamage() <= 100 && stack.getMaxDamage()
					// - stack.getItemDamage() >= 10 ? 7 : 11)), sr2.getScaledHeight() * 2 - 112 -
					// yOffset + 28, 0xFFFFFF);
					GL11.glScalef(2F, 2F, 2F);
					GL11.glEnable(GL11.GL_DEPTH_TEST);
					xOffset -= 18;
				}

//               		}
			}
			if (fpshurtcam.getValue()) {
				if (mc.thePlayer.hurtTime >= 1) {
					if (alpha2 < 100) {
						alpha2 += 5;
					}
				} else {
					if (alpha2 > 0) {
						alpha2 -= 5;
					}
				}
				RenderUtil.drawGradientSidewaysV(0, 0, RenderUtil.width(), 25, new Color(255, 0, 0, 0).getRGB(),
						new Color(255, 0, 0, alpha2).getRGB());
				RenderUtil.drawGradientSidewaysV(0, RenderUtil.height() - 25, RenderUtil.width(), RenderUtil.height(),
						new Color(255, 0, 0, alpha2).getRGB(), new Color(255, 0, 0, 0).getRGB());
			}
		}

	}

	public void renderCompass(ScaledResolution sr) {
		compass.draw(sr);
	}

	private Module getNextEnabledModule(List<Module> modules, int startingIndex) {
		int modulesSize = modules.size();
		for (int i = startingIndex; i < modulesSize; ++i) {
			Module module = modules.get(i);
			if (!module.isEnabled())
				continue;
			return module;
		}
		return null;
	}

	public static String getRemoteIp() {
		String serverIp = "Singleplayer";

		if (mc.theWorld.isRemote) {
			final ServerData serverData = mc.getCurrentServerData();
			if (serverData != null)
				serverIp = serverData.serverIP;
		}

		return serverIp;
	}

	private void drawPotionStatus(ScaledResolution sr) {
		CFontRenderer font = FontLoaders.GBD18;
//        if(!this.blackhotbar.getValue()) {
		int y = 0;
		for (PotionEffect effect : this.mc.thePlayer.getActivePotionEffects()) {
			int ychat;
			Potion potion = Potion.potionTypes[effect.getPotionID()];
			String PType = I18n.format(potion.getName(), new Object[0]);
			switch (effect.getAmplifier()) {
				case 1: {
					PType = String.valueOf(PType) + " II";
					break;
				}
				case 2: {
					PType = String.valueOf(PType) + " III";
					break;
				}
				case 3: {
					PType = String.valueOf(PType) + " IV";
					break;
				}
			}
			if (effect.getDuration() < 600 && effect.getDuration() > 300) {
				PType = String.valueOf(PType) + "\u00a77:\u00a76 " + Potion.getDurationString(effect);
			} else if (effect.getDuration() < 300) {
				PType = String.valueOf(PType) + "\u00a77:\u00a7c " + Potion.getDurationString(effect);
			} else if (effect.getDuration() > 600) {
				PType = String.valueOf(PType) + "\u00a77:\u00a77 " + Potion.getDurationString(effect);
			}
			int n = ychat = this.mc.ingameGUI.getChatGUI().getChatOpen() ? 5 : -10;
			font.drawStringWithShadow(PType, sr.getScaledWidth() - font.getStringWidth(PType) - 2,
					sr.getScaledHeight() - font.getHeight() + y - 12 - ychat, potion.getLiquidColor());
			y -= 10;
		}
//        }else {
//        	int y = -22;
//            for (PotionEffect effect : this.mc.thePlayer.getActivePotionEffects()) {
//                Potion potion = Potion.potionTypes[effect.getPotionID()];
//                String PType = I18n.format(potion.getName(), new Object[0]);
//                switch (effect.getAmplifier()) {
//                    case 1: {
//                        PType = String.valueOf(PType) + " II";
//                        break;
//                    }
//                    case 2: {
//                        PType = String.valueOf(PType) + " III";
//                        break;
//                    }
//                    case 3: {
//                        PType = String.valueOf(PType) + " IV";
//                        break;
//                    }
//                }
//                if (effect.getDuration() < 600 && effect.getDuration() > 300) {
//                    PType = String.valueOf(PType) + "\u00a77:\u00a76 " + Potion.getDurationString(effect);
//                } else if (effect.getDuration() < 300) {
//                    PType = String.valueOf(PType) + "\u00a77:\u00a7c " + Potion.getDurationString(effect);
//                } else if (effect.getDuration() > 600) {
//                    PType = String.valueOf(PType) + "\u00a77:\u00a77 " + Potion.getDurationString(effect);
//                }
//                font.drawStringWithShadow(PType, sr.getScaledWidth() - font.getStringWidth(PType) - 2, sr.getScaledHeight() - font.getHeight() + y - 2, potion.getLiquidColor());
//                y -= 10;
//            }
//        }
	}

	public void updateMirror(float partialTick) {
		int w, h;
		float y, py, p, pp;
		boolean hide, pause;
		int view, limit;
		long endTime = 0;
		MovingObjectPosition mouseOver;
		w = mc.displayWidth;
		h = mc.displayHeight;
		y = mc.renderViewEntity.rotationYaw;
		py = mc.renderViewEntity.prevRotationYaw;
		p = mc.renderViewEntity.rotationPitch;
		pp = mc.renderViewEntity.prevRotationPitch;
		hide = mc.gameSettings.hideGUI;
		view = mc.gameSettings.thirdPersonView;
		limit = mc.gameSettings.limitFramerate;
		mouseOver = mc.objectMouseOver;

		switchToFB();

		if (limit != 0) {
			try {
				endTime = mc.entityRenderer.renderEndNanoTime;
			} catch (Exception e) {
			}
		}

		mc.displayHeight = 180;
		mc.displayWidth = 320;
		mc.gameSettings.hideGUI = true;
		mc.gameSettings.thirdPersonView = 0;
		mc.gameSettings.limitFramerate = 0;
		mc.renderViewEntity.rotationYaw += 180;
		mc.renderViewEntity.prevRotationYaw += 180;
		mc.renderViewEntity.rotationPitch = -p + 18;
		mc.renderViewEntity.prevRotationPitch = -pp + 18;

		mirrorRenderGlobal.switchTo();

		GL11.glPushAttrib(GL11.GL_VIEWPORT_BIT | GL11.GL_ENABLE_BIT | GL11.GL_CURRENT_BIT | GL11.GL_POLYGON_BIT
				| GL11.GL_TEXTURE_BIT);
		mc.entityRenderer.func_181560_a(partialTick, mc.entityRenderer.renderEndNanoTime);

		if (limit != 0) {
			try {
				mc.entityRenderer.renderEndNanoTime = endTime;
			} catch (Exception e) {
			}
		}
		GL11.glPopAttrib();

		mirrorRenderGlobal.switchFrom();
		mc.objectMouseOver = mouseOver;
		mc.renderViewEntity.rotationYaw = y;
		mc.renderViewEntity.prevRotationYaw = py;
		mc.renderViewEntity.rotationPitch = p;
		mc.renderViewEntity.prevRotationPitch = pp;
		mc.gameSettings.limitFramerate = limit;
		mc.gameSettings.thirdPersonView = view;
		mc.gameSettings.hideGUI = hide;
		mc.displayWidth = w;
		mc.displayHeight = h;

		switchFromFB();
	}

	private void switchToFB() {
		ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_DRAW_FRAMEBUFFER, mirrorFBO);
		ARBFramebufferObject.glFramebufferTexture2D(ARBFramebufferObject.GL_DRAW_FRAMEBUFFER,
				ARBFramebufferObject.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, mirrorTex, 0);
		ARBFramebufferObject.glFramebufferTexture2D(ARBFramebufferObject.GL_DRAW_FRAMEBUFFER,
				ARBFramebufferObject.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, mirrorDepth, 0);
	}

	public float getNormalYaw(int offset) {
		return normalise(this.mc.thePlayer.rotationYaw, -90 + offset, 90 + offset) * 2.0F;
	}

	public float normalise(double value, double start, double end) {
		double width = end - start;
		double offsetValue = value - start;

		return (float) (offsetValue - Math.floor(offsetValue / width) * width + start);
	}

	public int getColorForCompass(float offset, float otherOffset) {
		float normalYaw = getNormalYaw((int) offset + (int) otherOffset);

		int color = (int) Math.min(255.0F,
				Math.max(1.0F,
						Math.abs(-255.0F + (!String.valueOf(offset).contains("-")
								? (normalYaw + offset * 2.0F) / 2.0F / 90.0F * 255.0F
								: -((normalYaw + offset * 2.0F) / 2.0F / 90.0F * 255.0F))) + 4.0F));
		if (color == 32) {
			color = 31;
		}
		if (color == 66) {
			color = 65;
		}
		if (color == 67) {
			color = 66;
		}
		if (color == 35) {
			color = 34;
		}
		return new Color(255, 255, 255, color).getRGB();
	}

	private void switchFromFB() {
		ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_DRAW_FRAMEBUFFER, 0);
	}

	public void glColor() {
		float alpha = (float) 0.3;
		float red = (float) HUD.r.getValue().intValue() / 255;
		float green = (float) HUD.g.getValue().intValue() / 255;
		float blue = (float) HUD.b.getValue().intValue() / 255;
		GlStateManager.color(red, green, blue, alpha);
	}

	public void enableGL2D() {
		GL11.glDisable(2929);
		GL11.glEnable(3042);
		GL11.glDisable(3553);
		GL11.glBlendFunc(770, 771);
		GL11.glDepthMask(true);
		GL11.glEnable(2848);
		GL11.glHint(3154, 4354);
		GL11.glHint(3155, 4354);
	}

	public void disableGL2D() {
		GL11.glEnable(3553);
		GL11.glDisable(3042);
		GL11.glEnable(2929);
		GL11.glDisable(2848);
		GL11.glHint(3154, 4352);
		GL11.glHint(3155, 4352);
	}

	public void drawESPCircle(double cx, double cy, double r, double n) {
		GL11.glPushMatrix();
		cx *= 2.0;
		cy *= 2.0;
		double b = 6.2831852 / n;
		double p = Math.cos(b);
		double s = Math.sin(b);
		double x = r *= 2.0;
		double y = 0.0;
		enableGL2D();
		GL11.glScaled(0.5, 0.5, 0.5);
		GlStateManager.color(0, 0, 0);
		GlStateManager.resetColor();
		glColor();
		GL11.glBegin(2);
		double ii = 0;
		while (ii < n) {
			GL11.glVertex2d(x + cx, y + cy);
			double t = x;
			x = p * x - s * y;
			y = s * t + p * y;
			ii++;
		}
		GL11.glEnd();
		GL11.glScaled(2.0, 2.0, 2.0);
		disableGL2D();

		GlStateManager.disableBlend();
		GlStateManager.scale(0.5, 0.5, 0.5);
		GlStateManager.disableDepth();
		GlStateManager.disableLighting();
		GlStateManager.enableDepth();
		GlStateManager.scale(2.0f, 2.0f, 2.0f);
		GlStateManager.enableAlpha();
//        GlStateManager.popMatrix();
//        GL11.glPopMatrix();

		GlStateManager.color(1, 1, 1, 1);
		GL11.glPopMatrix();
	}


	static enum HUDMode {
		Simple, Rect, OutLine, Side;
	}

	public static enum ColorMode {
		Custom, Fade, Rainbow;
	}

	static enum WidgetMode {
		None, Widget_1, Widget_2, Widget_3, Widget_4, Widget_5, Widget_6, Widget_7, Widget_8, Widget_9, Widget_10,
		Widget_11, Widget_12, Widget_13, Widget_14, Widget_15, Widget_16, Widget_17, Widget_18, Widget_19, Widget_20,
		Widget_21, Widget_22;
	}

}



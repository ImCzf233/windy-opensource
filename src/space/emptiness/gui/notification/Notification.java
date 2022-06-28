package space.emptiness.gui.notification;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import space.emptiness.gui.font.CFontRenderer;
import space.emptiness.gui.font.FontLoaders;
import space.emptiness.utils.TimeHelper;
import space.emptiness.utils.render.RenderUtil;

import java.awt.*;
import java.util.ArrayList;

public class Notification {
	private String message;
	private String header;
	private TimeHelper timer;
	private double lastY;
	private double posY;
	private double width;
	private double height;
	private double animationX;
	private int color;
	private int imageWidth;
	private final ResourceLocation image;
	private long stayTime;
	Minecraft mc = Minecraft.getMinecraft();

	public static ArrayList notifications = new ArrayList();

	public Notification(final String header, final String message, final Type type) {
		this.message = message;
		this.header = header;
		(this.timer = new TimeHelper()).reset();
		CFontRenderer font = FontLoaders.kiona20;
		this.width = font.getStringWidth(message);
		this.height = 10.5;
		this.animationX = this.width;
		this.stayTime = 2000L;
		this.imageWidth = 20;
		this.posY = -1.0;
		this.image = new ResourceLocation("emptiness/icon/" + type.name().toLowerCase() + ".png");
		if (type.equals(Type.INFO)) {
			this.color = new Color(186, 186, 186,70).getRGB();
		} else if (type.equals(Type.ERROR)) {
			this.color = new Color(224, 79, 95,70).getRGB();
		} else if (type.equals(Type.SUCCESS)) {
			this.color = new Color(50, 190, 166,70).getRGB();
		} else if (type.equals(Type.WARNING)) {
			this.color = new Color(250, 250, 160,70).getRGB();
		}
	}

	public Notification(final String header, final String message, final Type type, final long stayTime) {
		this.message = message;
		this.header = header;
		(this.timer = new TimeHelper()).reset();
		CFontRenderer font = FontLoaders.kiona20;
		this.width = font.getStringWidth(message);
		this.height = 10.5;
		this.animationX = this.width;
		this.imageWidth = 20;
		this.posY = -1.0;
		this.image = new ResourceLocation("emptiness/icon/" + type.name().toLowerCase() + ".png");
		if (type.equals(Type.INFO)) {
			this.color = new Color(186, 186, 186,70).getRGB();
		} else if (type.equals(Type.ERROR)) {
			this.color = new Color(224, 79, 95,70).getRGB();
		} else if (type.equals(Type.SUCCESS)) {
			this.color = new Color(50, 190, 166,70).getRGB();
		} else if (type.equals(Type.WARNING)) {
			this.color = new Color(250, 250, 160,70).getRGB();
		}
	}

	public static void send(String header,String message, Type type, long stayTime) {

		if (notifications.size() > 8) {
			notifications.remove(0);
		}
		notifications.add(new Notification(header,message, type, stayTime));
	}

	public static void send(String header,String message, Type type) {

		if (notifications.size() > 8) {
			notifications.remove(0);
		}
		notifications.add(new Notification(header,message, type));
	}

	public static void drawNotifications() {
		ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
		double startY = (double) (res.getScaledHeight() - 25);
		double lastY = startY;

		for (int i = 0; i < notifications.size(); ++i) {
			Notification not = (Notification) notifications.get(i);
			if (not.shouldDelete()) {
				notifications.remove(i);
			}

			not.draw(startY, lastY);
			startY -= not.getHeight() + 30.0D;
		}

	}

	public void draw(final double getY, final double lastY) {
		this.lastY = lastY;
		this.animationX = RenderUtil.getAnimationState(this.animationX, this.isFinished() ? this.width : 0.0,
				Math.max(this.isFinished() ? 100 : 20,
						Math.abs(this.animationX - (this.isFinished() ? this.width : 0.0)) * 2.0));
		if (this.posY == -1.0) {
			this.posY = getY;
		} else {
			this.posY = RenderUtil.getAnimationState(this.posY, getY, 100.0);
		}
		final ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
		final int x1 = (int) (res.getScaledWidth() - this.width + this.animationX);
		final int x2 = (int) (res.getScaledWidth() + this.animationX);
		final int y1 = (int) this.posY - 40;
		final int y2 = (int) (y1 + this.height + 20);
		// OldVisualUtil.drawRect(x1, y1, x2, y2, new Color(64,68,75).getRGB());
		// OldVisualUtil.drawRect(x1, y1, x1 + 20, y1 + 20, new
		// Color(49,52,57).getRGB());
		// Gui.drawRect(x1, y1, x1 + 20, y1 + 20, new Color(49,52,57).getRGB());
//		VisualUtil.drawBorderedRect(x1 - 55, y1, x2, y2, 2, color, new Color(0, 0, 0, 0).getRGB());


		RenderUtil.drawRect(x1 - 55, y1, x2, y2, color);
//        	Gui.drawRect(x1 + 1, y1 + 1, x2, y2 - 1, new Color(0,0,0,100).getRGB());
//		VisualUtil.drawGradientSideways(x2 - 16, y1 + 1, x2, y2 - 1, new Color(0, 0, 0, 0).getRGB(), color);
//		VisualUtil.drawGradientSideways(x2 + 16, y1 + 1, x2 - 16, y2 - 1, new Color(0, 0, 0, 0).getRGB(), color);
////       	Gui.drawRect(0, 0, 0, 0, 0);
	//	RenderUtil.drawRect(x1 - 55, y2 - 2,
	//			x1 + Math.min((x2 - x1) * (System.currentTimeMillis() - timer.getLastMs()) / stayTime, x2 - x1), y2,
	//			color);

//		Gui.drawRect(x1, y1, x2, y2, new Color(0, 0, 0, 60).getRGB());

		RenderUtil.drawCustomImage((int) (x1 + (this.height - this.imageWidth) / 2.0) - 46,
				y1 + (int) ((this.height - this.imageWidth) / 2.0) + 9, this.imageWidth, this.imageWidth, this.image);
		CFontRenderer font = FontLoaders.tahoma18;
		font.drawString(this.header, (float) (x1 + this.width / 2.0) -65,
				(float) (y1 + this.height / 2 - font.getStringHeight(this.header) / 2 + 2.5),
				new Color(255, 255, 255).getRGB());
		font.drawString(this.message, (float) (x1 + this.width / 2.0) - 65,
				(float) (y1 + this.height / 2 - font.getStringHeight(this.message) / 2 + 15.5),
				new Color(255, 255, 255).getRGB());

	}

	public boolean shouldDelete() {
		return this.isFinished() && this.animationX >= this.width;
	}

	private boolean isFinished() {
		return this.timer.isDelayComplete(this.stayTime) && this.posY == this.lastY;
	}

	public double getHeight() {
		return this.height;
	}

	public enum Type {
		SUCCESS("SUCCESS", 0), INFO("INFO", 1), WARNING("WARNING", 2), ERROR("ERROR", 3);

		private Type(final String s, final int n) {
		}
	}

	public static void clear() {
		notifications.clear();
	}
}

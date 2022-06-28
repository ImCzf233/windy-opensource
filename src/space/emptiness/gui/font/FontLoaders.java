
package space.emptiness.gui.font;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.InputStream;

public abstract class FontLoaders {
  
	//Kiona
	public static CFontRenderer kiona14 = new CFontRenderer(FontLoaders.getKiona(14), true, true);

    public static CFontRenderer kiona18 = new CFontRenderer(FontLoaders.getKiona(18), true, true);
    public static CFontRenderer kiona20 = new CFontRenderer(FontLoaders.getKiona(20), true, true);
    public static CFontRenderer kiona22 = new CFontRenderer(FontLoaders.getKiona(22), true, true);
    public static CFontRenderer kiona24 = new CFontRenderer(FontLoaders.getKiona(24), true, true);

    public static CFontRenderer kiona28 = new CFontRenderer(FontLoaders.getKiona(28), true, true);



    public static CFontRenderer tahoma18 = new CFontRenderer(FontLoaders.getTahoma(18), true, true);



    public static CFontRenderer SF14 = new CFontRenderer(FontLoaders.getSF(14), true, true);

    public static CFontRenderer SF18 = new CFontRenderer(FontLoaders.getSF(18), true, true);

    public static CFontRenderer SF30 = new CFontRenderer(FontLoaders.getSF(30), true, true);


    public static CFontRenderer icon18 = new CFontRenderer(FontLoaders.getIcon(18), true, true);
    //SFBOLD
    public static CFontRenderer GBD14 = new CFontRenderer(FontLoaders.getGilroyBD(14), true, true);
    public static CFontRenderer GBD16 = new CFontRenderer(FontLoaders.getGilroyBD(16), true, true);
    public static CFontRenderer GBD18 = new CFontRenderer(FontLoaders.getGilroyBD(18), true, true);
    public static CFontRenderer GBD20 = new CFontRenderer(FontLoaders.getGilroyBD(20), true, true);

    public static CFontRenderer GBD30 = new CFontRenderer(FontLoaders.getGilroyBD(30), true, true);

    public static CFontRenderer other24 = new CFontRenderer(FontLoaders.getOther(24), true, true);

    public static CFontRenderer other42 = new CFontRenderer(FontLoaders.getOther(42), true, true);



    public static Font getKiona(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("emptiness/font/raleway.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        }
        catch (Exception ex) {
            font = new Font("default", 0, size);
        }
        return font;
    }



    private static Font getTahoma(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager()
                    .getResource(new ResourceLocation("emptiness/font/tahoma.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }
        return font;
    }
    private static Font getGilroyBD(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager()
                    .getResource(new ResourceLocation("emptiness/font/GilroyBD.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }
        return font;
    }

    private static Font getIcon(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager()
                    .getResource(new ResourceLocation("emptiness/font/icon.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }
        return font;
    }

    private static Font getOther(int size) {
		Font font;
		try {
			InputStream is = Minecraft.getMinecraft().getResourceManager()
					.getResource(new ResourceLocation("emptiness/font/other.ttf")).getInputStream();
			font = Font.createFont(0, is);
			font = font.deriveFont(0, size);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Error loading font");
			font = new Font("default", 0, size);
		}
		return font;
	}

    private static Font getSF(int size) {
		Font font;
		try {
			InputStream is = Minecraft.getMinecraft().getResourceManager()
					.getResource(new ResourceLocation("emptiness/font/SFREGULAR.ttf")).getInputStream();
			font = Font.createFont(0, is);
			font = font.deriveFont(0, size);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Error loading font");
			font = new Font("default", 0, size);
		}
		return font;
	}
}


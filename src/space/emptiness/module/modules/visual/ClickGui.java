/*
 * Decompiled with CFR 0.136.
 */
package space.emptiness.module.modules.visual;

import org.lwjgl.input.Keyboard;
import space.emptiness.gui.clickgui.GuiClickUI;
import space.emptiness.module.Category;
import space.emptiness.module.Module;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClickGui extends Module {
	public ClickGui() {
		super("ClickGui", new String[] { "clickui" }, Category.Visual);
		this.setRemoved(true);
	}

	public static int memoriseX = 30;
	public static int memoriseY = 30;
	public static int memoriseWheel = 0;
	public static List<Module> memoriseML = new CopyOnWriteArrayList<>();
	public static Category memoriseCatecory = null;

	@Override
	public void onEnable() {
		mc.displayGuiScreen(new GuiClickUI());
		GuiClickUI.setX(memoriseX);
		GuiClickUI.setY(memoriseY);
		GuiClickUI.setWheel(memoriseWheel);
		GuiClickUI.setInSetting(memoriseML);
		if (memoriseCatecory != null)
			GuiClickUI.setCategory(memoriseCatecory);
		this.setEnabled(false);
	}
}

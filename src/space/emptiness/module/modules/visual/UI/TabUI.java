/*
 * Decompiled with CFR 0_132.
 */
package space.emptiness.module.modules.visual.UI;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import space.emptiness.events.EventManager;
import space.emptiness.events.EventTarget;
import space.emptiness.events.misc.EventKey;
import space.emptiness.events.rendering.EventRender2D;
import space.emptiness.module.Category;
import space.emptiness.module.Module;
import space.emptiness.module.ModuleManager;
import space.emptiness.module.modules.visual.HUD;
import space.emptiness.module.value.Mode;
import space.emptiness.module.value.Numbers;
import space.emptiness.module.value.Option;
import space.emptiness.module.value.Value;
import space.emptiness.utils.Helper;
import space.emptiness.utils.math.MathUtil;
import space.emptiness.utils.render.RenderUtil;
import space.emptiness.Client;
import space.emptiness.gui.font.CFontRenderer;
import space.emptiness.gui.font.FontLoaders;
import space.emptiness.management.Manager;

import java.awt.*;

public class TabUI
implements Manager {
    private Section section = Section.TYPES;
    private Category selectedType = Category.values()[0];
    private Module selectedModule = null;
    private Value selectedValue = null;
    private int currentType = 0;
    private int currentModule = 0;
    private int currentValue = 0;
    private int height = 68;
    private int maxType;
    private int maxModule;
    private int maxValue;
    private static /* synthetic */ int[] $SWITCH_TABLE$com$Maki$module$modules$render$UI$TabUI$Section;

    @Override
    public void init() {
        Category[] arrCategory = Category.values();
        int n = arrCategory.length;
        int n2 = 0;
        while (n2 < n) {
            Category mt = arrCategory[n2];
            if (this.maxType <= Helper.mc.fontRendererObj.getStringWidth(mt.name().toUpperCase()) + 14) {
                this.maxType = Helper.mc.fontRendererObj.getStringWidth(mt.name().toUpperCase()) + 14;
            }
            ++n2;
        }
        Client.instance.getModuleManager();
        for (Module m : ModuleManager.getModules()) {
            if (this.maxModule > Helper.mc.fontRendererObj.getStringWidth(m.getName().toUpperCase()) + 4) continue;
            this.maxModule = Helper.mc.fontRendererObj.getStringWidth(m.getName().toUpperCase()) + 4;
        }
        Client.instance.getModuleManager();
        for (Module m : ModuleManager.getModules()) {
            if (m.getValues().isEmpty()) continue;
            for (Value val : m.getValues()) {
                if (this.maxValue > Helper.mc.fontRendererObj.getStringWidth(val.getDisplayName().toUpperCase()) + 4) continue;
                this.maxValue = Helper.mc.fontRendererObj.getStringWidth(val.getDisplayName().toUpperCase()) + 4;
            }
        }
        this.maxModule += 12;
        this.maxValue += 24;
        boolean highestWidth = false;
        this.maxType = this.maxType < this.maxModule ? this.maxModule : this.maxType;
        this.maxModule += this.maxType;
        this.maxValue += this.maxModule;
        EventManager.register(this);
    }

    private void resetValuesLength() {
        this.maxValue = 0;
        for (Value val : this.selectedModule.getValues()) {
            int off;
            int n = off = val instanceof Option ? 6 : Helper.mc.fontRendererObj.getStringWidth(String.format(" \u00a77%s", val.getValue().toString())) + 6;
            if (this.maxValue > Helper.mc.fontRendererObj.getStringWidth(val.getDisplayName().toUpperCase()) + off) continue;
            this.maxValue = Helper.mc.fontRendererObj.getStringWidth(val.getDisplayName().toUpperCase()) + off;
        }
        this.maxValue += this.maxModule;
    }

    @EventTarget
    private void renderTabGUI(EventRender2D e) {
    	if(!HUD.rendertabui.getValue()) return;
        block34 : {
            block33 : {
                CFontRenderer font = FontLoaders.SF18;
                if (!HUD.useFont) break block33;
                if (Helper.mc.gameSettings.showDebugInfo || !Client.instance.getModuleManager().getModuleByClass(HUD.class).isEnabled()) break block34;
                int categoryY = HUD.shouldMove ? 35 : 25;
                int moduleY = categoryY;
                int valueY = categoryY;
                RenderUtil.drawBorderedRect(2.0f, categoryY, this.maxType - 25, categoryY + 12 * Category.values().length, 2.0f, new Color(0, 0, 0, 130).getRGB(), new Color(0, 0, 0, 180).getRGB());
                Category[] moduleArray = Category.values();
                int mA = moduleArray.length;
                int mA2 = 0;
                while (mA2 < mA) {
                    Category mt = moduleArray[mA2];
                    if (this.selectedType == mt) {
                    	int rainbowTick = 0;
                        Color rainbow = new Color(Color.HSBtoRGB((float)((double)Minecraft.getMinecraft().thePlayer.ticksExisted / 50.0 + Math.sin((double)rainbowTick / 50.0 * 1.6)) % 1.0f, 0.5f, 1.0f));
                        Gui.drawRect(3.0, (double)categoryY + 1.5, 4.0, (double)(categoryY + Helper.mc.fontRendererObj.FONT_HEIGHT) + 1.5, new Color(HUD.r.getValue().intValue(),HUD.g.getValue().intValue(),HUD.b.getValue().intValue(),255).getRGB());
                        moduleY = categoryY;
                    }
                    if (this.selectedType == mt) {
                        font.drawStringWithShadow(mt.name(), 7.0, categoryY + 3, -1);
                        
                    } else {
                        font.drawStringWithShadow(mt.name(), 5.0, categoryY + 3, new Color(180, 180, 180).getRGB());
                    }
                    if (mt.name() == "Combat") {
        				FontLoaders.icon18.drawString("1", this.maxType - 35, categoryY + 4,
        						new Color(255, 255, 255).getRGB());
        			}else if (mt.name() == "Vision") {
        				FontLoaders.icon18.drawString("0", this.maxType - 35, categoryY + 4,
        						 new Color(255, 255, 255).getRGB());
        			}else if (mt.name() == "Move") {
        				FontLoaders.icon18.drawString("5", this.maxType - 35, categoryY + 4,
        						new Color(255, 255, 255).getRGB());
        			}else if (mt.name() == "Player") {
        				FontLoaders.icon18.drawString("6", this.maxType - 35, categoryY + 4,
        						new Color(255, 255, 255).getRGB());
        			}else if (mt.name() == "World") {
        				FontLoaders.icon18.drawString("3", this.maxType - 35, categoryY + 4,
        						new Color(255, 255, 255).getRGB());
        			}
                    categoryY += 12;
                    ++mA2;
                }
                if (this.section == Section.MODULES || this.section == Section.VALUES) {
                    RenderUtil.drawBorderedRect(this.maxType - 20, moduleY, this.maxModule - 38, moduleY + 12 * Client.instance.getModuleManager().getModulesInType(this.selectedType).size(), 2.0f, new Color(0, 0, 0, 130).getRGB(), new Color(0, 0, 0, 180).getRGB());
                    for (Module m : Client.instance.getModuleManager().getModulesInType(this.selectedType)) {
//                    	if (m.name == "ClickGui" || m.name == "Teleport") break;
                        if (this.selectedModule == m) {
                        	int rainbowTick = 0;
                            Color rainbow = new Color(Color.HSBtoRGB((float)((double)Minecraft.getMinecraft().thePlayer.ticksExisted / 50.0 + Math.sin((double)rainbowTick / 50.0 * 1.6)) % 1.0f, 0.5f, 1.0f));
                            Gui.drawRect((double)this.maxType - 17.5, (double)moduleY + 1.5, (double)this.maxType -18.5, (double)(moduleY + Helper.mc.fontRendererObj.FONT_HEIGHT) + 1.5, new Color(HUD.r.getValue().intValue(),HUD.g.getValue().intValue(),HUD.b.getValue().intValue(),255).getRGB());
                            valueY = moduleY;
                        }
                        if (this.selectedModule == m) {
                            font.drawStringWithShadow(m.getName(), this.maxType - 15, moduleY + 3, m.isEnabled() ? -1 : 11184810);
                        } else {
                            font.drawStringWithShadow(m.getName(), this.maxType - 17, moduleY + 3, m.isEnabled() ? -1 : 11184810);
                        }
                        if (!m.getValues().isEmpty()) {
                            Gui.drawRect(this.maxModule - 38, (double)moduleY + 0.5, this.maxModule - 39, (double)(moduleY + Helper.mc.fontRendererObj.FONT_HEIGHT) + 2.5, new Color(153, 200, 255).getRGB());
                            if (this.section == Section.VALUES && this.selectedModule == m) {
                                RenderUtil.drawBorderedRect(this.maxModule - 32, valueY, this.maxValue - 25, valueY + 12 * this.selectedModule.getValues().size(), 2.0f, new Color(10, 10, 10, 180).getRGB(), new Color(10, 10, 10, 180).getRGB());
                                for (Value val : this.selectedModule.getValues()) {
                                	int rainbowTick = 0;
                                    Color rainbow = new Color(Color.HSBtoRGB((float)((double)Minecraft.getMinecraft().thePlayer.ticksExisted / 50.0 + Math.sin((double)rainbowTick / 50.0 * 1.6)) % 1.0f, 0.5f, 1.0f));
                                    Gui.drawRect((double)this.maxModule - 29.5, (double)valueY + 1.5, (double)this.maxModule - 30.5, (double)(valueY + Helper.mc.fontRendererObj.FONT_HEIGHT) + 1.5, this.selectedValue == val ? new Color(HUD.r.getValue().intValue(),HUD.g.getValue().intValue(),HUD.b.getValue().intValue(),255).getRGB() : 0);
                                    if (val instanceof Option) {
                                        font.drawStringWithShadow(val.getDisplayName(), this.selectedValue == val ? this.maxModule - 27 : this.maxModule - 29, valueY + 3, (Boolean)val.getValue() != false ? new Color(HUD.r.getValue().intValue(),HUD.g.getValue().intValue(),HUD.b.getValue().intValue(),255).getRGB() : 11184810);
                                    } else {
                                        String toRender = String.format("%s: \u00a77%s", val.getDisplayName(), val.getValue().toString());
                                        if (this.selectedValue == val) {
                                            font.drawStringWithShadow(toRender, this.maxModule - 27, valueY + 3, -1);
                                        } else {
                                            font.drawStringWithShadow(toRender, this.maxModule - 29, valueY + 3, -1);
                                        }
                                    }
                                    valueY += 12;
                                }
                            }
                        }
                        moduleY += 12;
                    }
                }
                break block34;
            }
            if (!Helper.mc.gameSettings.showDebugInfo && Client.instance.getModuleManager().getModuleByClass(HUD.class).isEnabled()) {
                int categoryY = HUD.shouldMove ? 68 : 25;
                int moduleY = categoryY;
                int valueY = categoryY;
                RenderUtil.drawBorderedRect(2.0f, categoryY, this.maxType - 25, categoryY + 12 * Category.values().length, 2.0f, new Color(0, 0, 0, 130).getRGB(), new Color(0, 0, 0, 180).getRGB());
                Category[] moduleArray = Category.values();
                int mA = moduleArray.length;
                int mA2 = 0;
                while (mA2 < mA) {
                    Category mt = moduleArray[mA2];
                    if (this.selectedType == mt) {
                    	int rainbowTick = 0;
                        Color rainbow = new Color(Color.HSBtoRGB((float)((double)Minecraft.getMinecraft().thePlayer.ticksExisted / 50.0 + Math.sin((double)rainbowTick / 50.0 * 1.6)) % 1.0f, 0.5f, 1.0f));
                        Gui.drawRect(3.0, (double)categoryY + 1.5, 4.0, (double)(categoryY + Helper.mc.fontRendererObj.FONT_HEIGHT) + 1.5, rainbow.getRGB());
                        moduleY = categoryY;
                    }
                    if (this.selectedType == mt) {
                        Helper.mc.fontRendererObj.drawStringWithShadow(mt.name(), 7.0f, categoryY + 2, -1);
                    } else {
                        Helper.mc.fontRendererObj.drawStringWithShadow(mt.name(), 5.0f, categoryY + 2, new Color(180, 180, 180).getRGB());
                    }
                    categoryY += 12;
                    ++mA2;
                }
                if (this.section == Section.MODULES || this.section == Section.VALUES) {
                    RenderUtil.drawBorderedRect(this.maxType - 20, moduleY, this.maxModule - 38, moduleY + 12 * Client.instance.getModuleManager().getModulesInType(this.selectedType).size(), 2.0f, new Color(0, 0, 0, 130).getRGB(), new Color(0, 0, 0, 180).getRGB());
                    for (Module m : Client.instance.getModuleManager().getModulesInType(this.selectedType)) {
//                    	if (m.name == "ClickGui" || m.name == "Teleport") break;
                        if (this.selectedModule == m) {
                        	int rainbowTick = 0;
                            Color rainbow = new Color(Color.HSBtoRGB((float)((double)Minecraft.getMinecraft().thePlayer.ticksExisted / 50.0 + Math.sin((double)rainbowTick / 50.0 * 1.6)) % 1.0f, 0.5f, 1.0f));
                            Gui.drawRect((double)this.maxType - 17.5, (double)moduleY + 1.5, (double)this.maxType -18.5, (double)(moduleY + Helper.mc.fontRendererObj.FONT_HEIGHT) + 1.5, new Color(HUD.r.getValue().intValue(),HUD.g.getValue().intValue(),HUD.b.getValue().intValue(),255).getRGB());
                            valueY = moduleY;
                        }
                        if (this.selectedModule == m) {
                            Helper.mc.fontRendererObj.drawStringWithShadow(m.getName(), this.maxType - 15, moduleY + 2, m.isEnabled() ? -1 : 11184810);
                        } else {
                            Helper.mc.fontRendererObj.drawStringWithShadow(m.getName(), this.maxType - 17, moduleY + 2, m.isEnabled() ? -1 : 11184810);
                        }
                        if (!m.getValues().isEmpty()) {
                            Gui.drawRect(this.maxModule - 38, (double)moduleY + 0.5, this.maxModule - 39, (double)(moduleY + Helper.mc.fontRendererObj.FONT_HEIGHT) + 2.5, new Color(153, 200, 255).getRGB());
                            if (this.section == Section.VALUES && this.selectedModule == m) {
                                RenderUtil.drawBorderedRect(this.maxModule - 32, valueY, this.maxValue - 25, valueY + 12 * this.selectedModule.getValues().size(), 2.0f, new Color(10, 10, 10, 180).getRGB(), new Color(10, 10, 10, 180).getRGB());
                                for (Value val : this.selectedModule.getValues()) {
                                	int rainbowTick = 0;
                                    Color rainbow = new Color(Color.HSBtoRGB((float)((double)Minecraft.getMinecraft().thePlayer.ticksExisted / 50.0 + Math.sin((double)rainbowTick / 50.0 * 1.6)) % 1.0f, 0.5f, 1.0f));
                                    Gui.drawRect((double)this.maxModule - 29.5, (double)valueY + 1.5, (double)this.maxValue - 108.5, (double)(valueY + Helper.mc.fontRendererObj.FONT_HEIGHT) + 1.5, this.selectedValue == val ? new Color(HUD.r.getValue().intValue(),HUD.g.getValue().intValue(),HUD.b.getValue().intValue(),255).getRGB() : 0);
                                    if (val instanceof Option) {
                                        Helper.mc.fontRendererObj.drawStringWithShadow(val.getDisplayName(), this.selectedValue == val ? this.maxModule - 27 : this.maxModule - 29, valueY + 2, (Boolean)val.getValue() != false ? new Color(HUD.r.getValue().intValue(),HUD.g.getValue().intValue(),HUD.b.getValue().intValue(),255).getRGB() : 11184810);
                                    } else {
                                        String toRender = String.format("%s: \u00a77%s", val.getDisplayName(), val.getValue().toString());
                                        if (this.selectedValue == val) {
                                            Helper.mc.fontRendererObj.drawStringWithShadow(toRender, this.maxModule - 27, valueY + 2, -1);
                                        } else {
                                            Helper.mc.fontRendererObj.drawStringWithShadow(toRender, this.maxModule - 29, valueY + 2, -1);
                                        }
                                    }
                                    valueY += 12;
                                }
                            }
                        }
                        moduleY += 12;
                    }
                }
            }
        }
    }
    @EventTarget
    private void onKey(EventKey e) {
    	if(!HUD.rendertabui.getValue()) return;
        if (!Helper.mc.gameSettings.showDebugInfo) {
            block0 : switch (e.getKey()) {
                case 208: {
                    switch (TabUI.$SWITCH_TABLE$com$Maki$module$modules$render$UI$TabUI$Section()[this.section.ordinal()]) {
                        case 1: {
                            ++this.currentType;
                            if (this.currentType > Category.values().length - 1) {
                                this.currentType = 0;
                            }
                            this.selectedType = Category.values()[this.currentType];
                            break block0;
                        }
                        case 2: {
                            ++this.currentModule;
                            if (this.currentModule > Client.instance.getModuleManager().getModulesInType(this.selectedType).size() - 1) {
                                this.currentModule = 0;
                            }
                            this.selectedModule = Client.instance.getModuleManager().getModulesInType(this.selectedType).get(this.currentModule);
                            break block0;
                        }
                        case 3: {
                            ++this.currentValue;
                            if (this.currentValue > this.selectedModule.getValues().size() - 1) {
                                this.currentValue = 0;
                            }
                            this.selectedValue = this.selectedModule.getValues().get(this.currentValue);
                        }
                    }
                    break;
                }
                case 200: {
                    switch (TabUI.$SWITCH_TABLE$com$Maki$module$modules$render$UI$TabUI$Section()[this.section.ordinal()]) {
                        case 1: {
                            --this.currentType;
                            if (this.currentType < 0) {
                                this.currentType = Category.values().length - 1;
                            }
                            this.selectedType = Category.values()[this.currentType];
                            break block0;
                        }
                        case 2: {
                            --this.currentModule;
                            if (this.currentModule < 0) {
                                this.currentModule = Client.instance.getModuleManager().getModulesInType(this.selectedType).size() - 1;
                            }
                            this.selectedModule = Client.instance.getModuleManager().getModulesInType(this.selectedType).get(this.currentModule);
                            break block0;
                        }
                        case 3: {
                            --this.currentValue;
                            if (this.currentValue < 0) {
                                this.currentValue = this.selectedModule.getValues().size() - 1;
                            }
                            this.selectedValue = this.selectedModule.getValues().get(this.currentValue);
                        }
                    }
                    break;
                }
                case 205: {
                    switch (TabUI.$SWITCH_TABLE$com$Maki$module$modules$render$UI$TabUI$Section()[this.section.ordinal()]) {
                        case 1: {
                            this.currentModule = 0;
                            this.selectedModule = Client.instance.getModuleManager().getModulesInType(this.selectedType).get(this.currentModule);
                            this.section = Section.MODULES;
                            break block0;
                        }
                        case 2: {
                            if (this.selectedModule.getValues().isEmpty()) break block0;
                            this.resetValuesLength();
                            this.currentValue = 0;
                            this.selectedValue = this.selectedModule.getValues().get(this.currentValue);
                            this.section = Section.VALUES;
                            break block0;
                        }
                        case 3: {
//                            if (Helper.onServer("enjoytheban")) break block0;
                            if (this.selectedValue instanceof Option) {
                                this.selectedValue.setValue((Boolean)this.selectedValue.getValue() == false);
                            } else if (this.selectedValue instanceof Numbers) {
                                Numbers value = (Numbers)this.selectedValue;
                                double inc = (Double)value.getValue();
                                inc += ((Double)value.getIncrement()).doubleValue();
                                if ((inc = MathUtil.toDecimalLength(inc, 1)) > (Double)value.getMaximum()) {
                                    inc = (Double)((Numbers)this.selectedValue).getMinimum();
                                }
                                this.selectedValue.setValue(inc);
                            } else if (this.selectedValue instanceof Mode) {
                                Mode theme = (Mode)this.selectedValue;
                                Enum current = (Enum)theme.getValue();
                                int next = current.ordinal() + 1 >= theme.getModes().length ? 0 : current.ordinal() + 1;
                                this.selectedValue.setValue(theme.getModes()[next]);
                            }
                            this.resetValuesLength();
                        }
                    }
                    break;
                }
                case 28: {
                    switch (TabUI.$SWITCH_TABLE$com$Maki$module$modules$render$UI$TabUI$Section()[this.section.ordinal()]) {
                        case 1: {
                            break block0;
                        }
                        case 2: {
                            this.selectedModule.setEnabled(!this.selectedModule.isEnabled());
                            break block0;
                        }
                        case 3: {
                            this.section = Section.MODULES;
                        }
                    }
                    break;
                }
                case 203: {
                    switch (TabUI.$SWITCH_TABLE$com$Maki$module$modules$render$UI$TabUI$Section()[this.section.ordinal()]) {
                        case 1: {
                            break block0;
                        }
                        case 2: {
                            this.section = Section.TYPES;
                            this.currentModule = 0;
                            break block0;
                        }
                        case 3: {
//                            if (Helper.onServer("enjoytheban")) break block0;
                            if (this.selectedValue instanceof Option) {
                                this.selectedValue.setValue((Boolean)this.selectedValue.getValue() == false);
                            } else if (this.selectedValue instanceof Numbers) {
                                Numbers value = (Numbers)this.selectedValue;
                                double inc = (Double)value.getValue();
                                inc -= ((Double)value.getIncrement()).doubleValue();
                                if ((inc = MathUtil.toDecimalLength(inc, 1)) < (Double)value.getMinimum()) {
                                    inc = (Double)((Numbers)this.selectedValue).getMaximum();
                                }
                                this.selectedValue.setValue(inc);
                            } else if (this.selectedValue instanceof Mode) {
                                Mode theme = (Mode)this.selectedValue;
                                Enum current = (Enum)theme.getValue();
                                int next = current.ordinal() - 1 < 0 ? theme.getModes().length - 1 : current.ordinal() - 1;
                                this.selectedValue.setValue(theme.getModes()[next]);
                            }
                            this.maxValue = 0;
                            for (Value val : this.selectedModule.getValues()) {
                                int off;
                                int n = off = val instanceof Option ? 6 : Minecraft.getMinecraft().fontRendererObj.getStringWidth(String.format(" \u00a77%s", val.getValue().toString())) + 6;
                                if (this.maxValue > Minecraft.getMinecraft().fontRendererObj.getStringWidth(val.getDisplayName().toUpperCase()) + off) continue;
                                this.maxValue = Minecraft.getMinecraft().fontRendererObj.getStringWidth(val.getDisplayName().toUpperCase()) + off;
                            }
                            this.maxValue += this.maxModule;
                        }
                    }
                }
            }
        }
    }

    static /* synthetic */ int[] $SWITCH_TABLE$com$Maki$module$modules$render$UI$TabUI$Section() {
        int[] arrn;
        int[] arrn2 = $SWITCH_TABLE$com$Maki$module$modules$render$UI$TabUI$Section;
        if (arrn2 != null) {
            return arrn2;
        }
        arrn = new int[Section.values().length];
        try {
            arrn[Section.MODULES.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            arrn[Section.TYPES.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            arrn[Section.VALUES.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        $SWITCH_TABLE$com$Maki$module$modules$render$UI$TabUI$Section = arrn;
        return $SWITCH_TABLE$com$Maki$module$modules$render$UI$TabUI$Section;
    }

    public static enum Section {
        TYPES,
        MODULES,
        VALUES;
    }

}


package space.emptiness.module;
import com.sun.org.apache.xpath.internal.operations.Mod;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import space.emptiness.events.EventManager;
import space.emptiness.gui.notification.Notification;
import space.emptiness.utils.Helper;
import space.emptiness.utils.math.MathUtil;
import space.emptiness.utils.render.Translate;
import space.emptiness.Client;
import space.emptiness.command.Command;
import space.emptiness.management.FileManager;
import space.emptiness.module.value.Mode;
import space.emptiness.module.value.Numbers;
import space.emptiness.module.value.Option;
import space.emptiness.module.value.Value;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Module extends Mod {
   public String name;
    public int clickanim;
    private String suffix;
   private int color;
   private String[] alias;
   private boolean enabled;
   public boolean enabledOnStartup = false;
   private final Translate translate = new Translate(0.0f, 0.0f);
   private int key;
   public List<Value> values;
   public Category type;
   public boolean removed;
   public boolean arraylistremove = true;
   public float animx = 0;
   public static Minecraft mc = Minecraft.getMinecraft();
   public static Random random = new Random();

   public Module(String name, String[] alias, Category type) {
      this.name = name;
      this.alias = alias;
      this.type = type;
      this.suffix = "";
      this.key = 0;
      this.removed = false;
      this.enabled = false;
      this.values = new ArrayList();
   }

   public String getName() {
      return this.name;
   }
   
   public Translate getTranslate() {
       return this.translate;
   }

   public String[] getAlias() {
      return this.alias;
   }

   public Category getType() {
      return this.type;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public boolean wasRemoved() {
      return this.removed;
   }

   public void setRemoved(boolean removed) {
      this.removed = removed;
   }
   
   public boolean wasArrayRemoved() {
	      return this.arraylistremove;
	   }
   
   public void setArrayRemoved(boolean arraylistremove) {
	  this.arraylistremove = arraylistremove;
   }
   
   public void setAnimx(float aanimx) {
	   this.animx = aanimx;
   }
   
   public float getAnimx() {
	      return this.animx;
	   }

   public String getSuffix() {
      return this.suffix;
   }

   public void setSuffix(Object obj) {
      String suffix = obj.toString();
      if(suffix.isEmpty()) {
         this.suffix = suffix;
      } else {
         this.suffix = String.format(" \u00a7f%s\u00a77", new Object[]{EnumChatFormatting.GRAY + suffix});
      }

   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
      if(enabled) {
         this.onEnable();
         if (Minecraft.theWorld != null) {
             Minecraft.thePlayer.playSound("random.click", 0.2f, this.enabled ? 0.6f : 0.5f);
             Notification.send("Module Information","Enabled " + this.getName() , Notification.Type.SUCCESS);
         }

          EventManager.register(this);
      } else {
    	  if (Minecraft.theWorld != null) {
              Minecraft.thePlayer.playSound("random.click", 0.2f, this.enabled ? 0.6f : 0.5f);
              Notification.send("Module Information", "Disabled " + this.getName() , Notification.Type.ERROR);
          }
          EventManager.unregister(this);
         this.onDisable();
      }
      
   }

   public void setColor(int color) {
      this.color = color;
   }

   public int getColor() {
      return this.color;
   }

   protected void addValues(Value... values) {
      Value[] var5 = values;
      int var4 = values.length;

      for(int var3 = 0; var3 < var4; ++var3) {
         Value value = var5[var3];
         this.values.add(value);
      }

   }

   public List<Value> getValues() {
      return this.values;
   }

   public int getKey() {
      return this.key;
   }

   public void setKey(int key) {
	   this.key = key;
		String content = "";
		Client.instance.getModuleManager();

		Module m;
		for (Iterator var4 = ModuleManager.getModules().iterator(); var4.hasNext(); content = content + String.format(
				"%s:%s%s", new Object[] { m.getName(), Keyboard.getKeyName(m.getKey()), System.lineSeparator() })) {
			m = (Module) var4.next();
		}

		FileManager.save("Binds.txt", content, false);
   }

   public void onEnable() {
   }

   public void onDisable() {
   }

   public void makeCommand() {
      if(this.values.size() > 0) {
         String options = "";
         String other = "";
         Iterator var4 = this.values.iterator();

         Value v;
         while(var4.hasNext()) {
            v = (Value)var4.next();
            if(!(v instanceof Mode)) {
               if(options.isEmpty()) {
                  options = String.valueOf(options) + v.getName();
               } else {
                  options = String.valueOf(options) + String.format(", %s", new Object[]{v.getName()});
               }
            }
         }

         var4 = this.values.iterator();

         while(true) {
            do {
               if(!var4.hasNext()) {
                  Client.instance.getCommandManager().add(new Module$1(this, this.name, this.alias, String.format("%s%s", new Object[]{options.isEmpty()?"":String.format("%s,", new Object[]{options}), other.isEmpty()?"":String.format("%s", new Object[]{other})}), "Setup this module"));
                  return;
               }

               v = (Value)var4.next();
            } while(!(v instanceof Mode));

            Mode mode = (Mode)v;
            Enum[] modes;
            int length = (modes = mode.getModes()).length;

            for(int i = 0; i < length; ++i) {
               Enum e = modes[i];
               if(other.isEmpty()) {
                  other = String.valueOf(other) + e.name().toLowerCase();
               } else {
                  other = String.valueOf(other) + String.format(", %s", new Object[]{e.name().toLowerCase()});
               }
            }
         }
      }
   }
}

class Module$1 extends Command {
	   private final Module m;
	   final Module this$0;

	   Module$1(Module var1, String $anonymous0, String[] $anonymous1, String $anonymous2, String $anonymous3) {
	      super($anonymous0, $anonymous1, $anonymous2, $anonymous3);
	      this.this$0 = var1;
	      this.m = var1;
	   }

	   public String execute(String[] args) {
	      Option option;
	      if(args.length >= 2) {
	         option = null;
	         Numbers fuck = null;
	         Mode xd = null;
	         Iterator var6 = this.m.values.iterator();

	         Value v;
	         while(var6.hasNext()) {
	            v = (Value)var6.next();
	            if(v instanceof Option && v.getName().equalsIgnoreCase(args[0])) {
	               option = (Option)v;
	            }
	         }

	         if(option != null) {
	            option.setValue(Boolean.valueOf(!((Boolean)option.getValue()).booleanValue()));
	            Helper.sendMessage(String.format("> %s has been set to %s", new Object[]{option.getName(), option.getValue()}));
	         } else {
	            var6 = this.m.values.iterator();

	            while(var6.hasNext()) {
	               v = (Value)var6.next();
	               if(v instanceof Numbers && v.getName().equalsIgnoreCase(args[0])) {
	                  fuck = (Numbers)v;
	               }
	            }

	            if(fuck != null) {
	               if(MathUtil.parsable(args[1], (byte) 4)) {
	                  double v1 = MathUtil.round(Double.parseDouble(args[1]), 1);
	                  fuck.setValue(Double.valueOf(v1 > ((Double)fuck.getMaximum()).doubleValue()?((Double)fuck.getMaximum()).doubleValue():v1));
	                  Helper.sendMessage(String.format("> %s has been set to %s", new Object[]{fuck.getName(), fuck.getValue()}));
	               } else {
	                  Helper.sendMessage("> " + args[1] + " is not a number");
	               }
	            }

	            var6 = this.m.values.iterator();

	            while(var6.hasNext()) {
	               v = (Value)var6.next();
	               if(args[0].equalsIgnoreCase(v.getDisplayName()) && v instanceof Mode) {
	                  xd = (Mode)v;
	               }
	            }

	            if(xd != null) {
	               if(xd.isValid(args[1])) {
	                  xd.setMode(args[1]);
	                  Helper.sendMessage(String.format("> %s set to %s", new Object[]{xd.getName(), xd.getModeAsString()}));
	               } else {
	                  Helper.sendMessage("> " + args[1] + " is an invalid mode");
	               }
	            }
	         }

	         if(fuck == null && option == null && xd == null) {
	            this.syntaxError("Valid .<module> <setting> <mode if needed>");
	         }
	      } else if(args.length >= 1) {
	         option = null;
	         Iterator xd1 = this.m.values.iterator();

	         while(xd1.hasNext()) {
	            Value fuck1 = (Value)xd1.next();
	            if(fuck1 instanceof Option && fuck1.getName().equalsIgnoreCase(args[0])) {
	               option = (Option)fuck1;
	            }
	         }

	         if(option != null) {
	            option.setValue(Boolean.valueOf(!((Boolean)option.getValue()).booleanValue()));
	            String fuck2 = option.getName().substring(1);
	            String xd2 = option.getName().substring(0, 1).toUpperCase();
	            if(((Boolean)option.getValue()).booleanValue()) {
	               Helper.sendMessage(String.format("> %s has been set to \u00a7a%s", new Object[]{xd2 + fuck2, option.getValue()}));
	            } else {
	               Helper.sendMessage(String.format("> %s has been set to \u00a7c%s", new Object[]{xd2 + fuck2, option.getValue()}));
	            }
	         } else {
	            this.syntaxError("Valid .<module> <setting> <mode if needed>");
	         }
	      } else {
	         Helper.sendMessage(String.format("%s Values: \n %s", new Object[]{this.getName().substring(0, 1).toUpperCase() + this.getName().substring(1).toLowerCase(), this.getSyntax(), "false"}));
	      }

	      return null;
	   }
	}


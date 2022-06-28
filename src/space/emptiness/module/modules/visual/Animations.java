package space.emptiness.module.modules.visual;

import space.emptiness.events.EventTarget;
import space.emptiness.events.world.EventUpdate;
import space.emptiness.module.Category;
import space.emptiness.module.Module;
import space.emptiness.module.value.Mode;
import space.emptiness.module.value.Numbers;
import space.emptiness.module.value.Option;
import space.emptiness.module.value.Value;

public class Animations extends Module {
	/**
	 * ItemRenderer.java & EntityLivingBase.java â™?
	 */
	public static Mode mode;
	public static Option Smooth;
	public static Option EveryThingBlock;
	public static Numbers<Double> x;
	public static Numbers<Double> y;
	public static Numbers<Double> swingx;
	public static Numbers<Double> swingy;
	public static Numbers<Double> swingz;
	public static Numbers<Double> z;
	public static Numbers Speed;
	public static Numbers<Double> swingspeed = new Numbers<Double>("SwingSpeed", "SwingSpeed", 1.0, 0.1, 1.0, 0.1);

	static {
		mode = new Mode("Mode", "Mode", renderMode.values(), renderMode.Swang);
		Smooth = new Option("SmoothHit", "SmoothHit", false);
		EveryThingBlock = new Option("EveryThingBlock", "EveryThingBlock", false);
		x = new Numbers("X", "X", 0.0D, -1.0D, 1.0D, 0.1D);
		y = new Numbers("Y", "Y", 0.0D, -1.0D, 1.0D, 0.1D);
		swingx = new Numbers("Swingx", "Swingx", 0.0D, -1.0D, 1.0D, 0.1D);
		swingy = new Numbers("Swingy", "Swingy", 0.0D, -1.0D, 1.0D, 0.1D);
		swingz = new Numbers("Swingz", "Swingz", 0.0D, -1.0D, 1.0D, 0.1D);
		z = new Numbers("Z", "Z", 0.0D, -1.0D, 1.0D, 0.1D);
		Speed = new Numbers("Speed", "Speed", 10.0D, 1.0D, 50.0D, 1.0D);
	}

	public Animations() {
		super("Animations", new String[]{"BlockHitanimations"}, Category.Visual);
		this.addValues(new Value[]{mode, x, y, z, Speed,swingspeed, EveryThingBlock, swingx, swingy, swingz, Smooth});
	}

	public enum renderMode {
		Swang,
		Swank,
		Swing,
		Swong,
		SwAing,
		None,
		Old,
		Punch,
		Winter,
		Exhibition,

	}
	@EventTarget
	public void onUpdate(EventUpdate e)  {
		this.setSuffix(this.mode.getValue());
	}
}
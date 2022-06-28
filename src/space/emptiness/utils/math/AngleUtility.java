
package space.emptiness.utils.math;

import space.emptiness.utils.vec.Vector3;

import java.util.Random;

public class AngleUtility {
    private static float minYawSmoothing;
    private static float maxYawSmoothing;
    private static float minPitchSmoothing;
    private static float maxPitchSmoothing;
    private static Vector3<Float> delta;
    private static Angle smoothedAngle;
    private static Random random;
    private float smooth;

    public AngleUtility(float minYawSmoothing, float maxYawSmoothing, float minPitchSmoothing, float maxPitchSmoothing) {
        AngleUtility.minYawSmoothing = minYawSmoothing;
        AngleUtility.maxYawSmoothing = maxYawSmoothing;
        AngleUtility.minPitchSmoothing = minPitchSmoothing;
        AngleUtility.maxPitchSmoothing = maxPitchSmoothing;
        random = new Random();
        delta = new Vector3((Number)Float.valueOf((float)0.0f), (Number)Float.valueOf((float)0.0f), (Number)Float.valueOf((float)0.0f));
        smoothedAngle = new Angle(Float.valueOf((float)0.0f), Float.valueOf((float)0.0f));
    }

    public static float randomFloat(float min, float max) {
        return min + random.nextFloat() * (max - min);
    }

    public static Angle calculateAngle(Vector3<Double> destination, Vector3<Double> source) {
        Angle angles = new Angle(Float.valueOf((float)0.0f), Float.valueOf((float)0.0f));
        float height = 1.5f;
        delta.setX((Number)Float.valueOf((float)(destination.getX().floatValue() - source.getX().floatValue()))).setY((Number)Float.valueOf((float)(destination.getY().floatValue() + height - (source.getY().floatValue() + height)))).setZ((Number)Float.valueOf((float)(destination.getZ().floatValue() - source.getZ().floatValue())));
        double hypotenuse = Math.hypot((double)delta.getX().doubleValue(), (double)delta.getZ().doubleValue());
        float yawAtan = (float)Math.atan2((double)delta.getZ().floatValue(), (double)delta.getX().floatValue());
        float pitchAtan = (float)Math.atan2((double)delta.getY().floatValue(), (double)hypotenuse);
        float deg = 57.29578f;
        float yaw = yawAtan * deg - 90.0f;
        float pitch = -(pitchAtan * deg);
        return angles.setYaw(Float.valueOf((float)yaw)).setPitch(Float.valueOf((float)pitch)).constrantAngle();
    }

    public static Angle smoothAngle(Angle destination, Angle source, float i, float j) {
        return smoothedAngle.setYaw(Float.valueOf((float)(source.getYaw().floatValue() - destination.getYaw().floatValue()))).setPitch(Float.valueOf((float)(source.getPitch().floatValue() - destination.getPitch().floatValue()))).constrantAngle().setYaw(Float.valueOf((float)(source.getYaw().floatValue() - smoothedAngle.getYaw().floatValue() / 100.0f * AngleUtility.randomFloat(minYawSmoothing, maxYawSmoothing)))).setPitch(Float.valueOf((float)(source.getPitch().floatValue() - smoothedAngle.getPitch().floatValue() / 100.0f * AngleUtility.randomFloat(minPitchSmoothing, maxPitchSmoothing)))).constrantAngle();
    }
    public Angle smoothAngle(Angle destination, Angle source) {
        Angle angles = new Angle(source.getYaw() - destination.getYaw(), source.getPitch() - destination.getPitch()).constrantAngle();
        angles.setYaw(source.getYaw() - angles.getYaw() / 100.0f * this.smooth);
        angles.setPitch(source.getPitch() - angles.getPitch() / 100.0f * this.smooth);
        return angles.constrantAngle();
    }

}

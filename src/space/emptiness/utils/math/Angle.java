
package space.emptiness.utils.math;

import space.emptiness.utils.vec.Vector2;

public class Angle
extends Vector2<Float> {
    public Angle(Float x, Float y) {
        super((Number)x, (Number)y);
    }

    public Angle setYaw(Float yaw) {
        this.setX((Number)yaw);
        return this;
    }

    public Angle setPitch(Float pitch) {
        this.setY((Number)pitch);
        return this;
    }

    public Float getYaw() {
        return Float.valueOf((float)this.getX().floatValue());
    }

    public Float getPitch() {
        return Float.valueOf((float)this.getY().floatValue());
    }

    public Angle constrantAngle() {
        this.setYaw(Float.valueOf((float)(this.getYaw().floatValue() % 360.0f)));
        this.setPitch(Float.valueOf((float)(this.getPitch().floatValue() % 360.0f)));
        while (this.getYaw().floatValue() <= -180.0f) {
            this.setYaw(Float.valueOf((float)(this.getYaw().floatValue() + 360.0f)));
        }
        while (this.getPitch().floatValue() <= -180.0f) {
            this.setPitch(Float.valueOf((float)(this.getPitch().floatValue() + 360.0f)));
        }
        while (this.getYaw().floatValue() > 180.0f) {
            this.setYaw(Float.valueOf((float)(this.getYaw().floatValue() - 360.0f)));
        }
        while (this.getPitch().floatValue() > 180.0f) {
            this.setPitch(Float.valueOf((float)(this.getPitch().floatValue() - 360.0f)));
        }
        return this;
    }
}


package space.emptiness.utils.math;

public class myAngle {
    private float yaw;
    private float pitch;

    public myAngle(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public myAngle() {
        this(0.0f, 0.0f);
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public myAngle constrantAngle() {
        this.setYaw(this.getYaw() % 360.0f);
        this.setPitch(this.getPitch() % 360.0f);
        while (this.getYaw() <= -180.0f) {
            this.setYaw(this.getYaw() + 360.0f);
        }
        while (this.getPitch() <= -180.0f) {
            this.setPitch(this.getPitch() + 360.0f);
        }
        while (this.getYaw() > 180.0f) {
            this.setYaw(this.getYaw() - 360.0f);
        }
        while (this.getPitch() > 180.0f) {
            this.setPitch(this.getPitch() - 360.0f);
        }
        return this;
    }
}

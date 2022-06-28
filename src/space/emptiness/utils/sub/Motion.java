package space.emptiness.utils.sub;

import net.minecraft.entity.Entity;

public class Motion {

    private double motionX, motionY, motionZ;

    public Motion(double x, double y, double z) {
        motionX = x;
        motionY = y;
        motionZ = z;
    }

    public Motion(Entity entity) {
        motionX = entity.motionX;
        motionY = entity.motionY;
        motionZ = entity.motionZ;
    }

    public void reset() {
        motionX = 0;
        motionY = 0;
        motionZ = 0;
    }

    public void setTo(Motion motion) {
        motionX = motion.getMotionX();
        motionY = motion.getMotionY();
        motionZ = motion.getMotionZ();
    }

    public void add(double x, double y, double z) {
        motionX += x;
        motionY += y;
        motionZ += z;
    }

    public void remove(double x, double y, double z) {
        motionX -= x;
        motionY -= y;
        motionZ -= z;
    }

    public double getMotionX() {
        return motionX;
    }

    public double getMotionY() {
        return motionY;
    }

    public double getMotionZ() {
        return motionZ;
    }

    public void setMotionX(double motionX) {
        this.motionX = motionX;
    }

    public void setMotionY(double motionY) {
        this.motionY = motionY;
    }

    public void setMotionZ(double motionZ) {
        this.motionZ = motionZ;
    }
}

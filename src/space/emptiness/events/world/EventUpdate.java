package space.emptiness.events.world;

import space.emptiness.eventapi.events.Event;
import space.emptiness.eventapi.events.callables.EventTyped;
import net.minecraft.client.Minecraft;

public class EventUpdate implements Event {

    public final boolean PRE;

    public float YAW;
    public float PITCH;

    public double X;
    public double Y;
    public double Z;

    public static boolean GROUND;

    public static float RENDERPREVYAW;
    public static float RENDERYAW;
    public static float RENDERPREVPITCH;
    public static float RENDERPITCH;
    public EventTyped EventTyped;

    public EventUpdate(float yaw, float pitch, double posX, double posY, double posZ, boolean ground) {
        YAW = yaw;
        PITCH = pitch;
        GROUND = ground;
        X = posX;
        Y = posY;
        Z = posZ;
        PRE = true;
    }

    public EventUpdate(float yaw, float pitch) {
        RENDERPREVYAW = RENDERYAW;
        RENDERYAW = yaw;
        RENDERPREVPITCH = RENDERPITCH;
        RENDERPITCH = pitch;
        PRE = false;
    }

    public static float getRenderYaw() {
        return RENDERYAW;
    }

    public static float getRenderPitch() {
        return RENDERPITCH;
    }

    public static float getPrevRenderYaw() {
        return RENDERPREVYAW;
    }

    public static float getPrevRenderPitch() {
        return RENDERPREVPITCH;
    }

    public boolean isPre() {
        return PRE;
    }

    public boolean isPost() {
        return !PRE;
    }

    public double getX() {
        return X;
    }

    public void setX(double posX) {
        X = posX;
    }

    public double getY() {
        return Y;
    }

    public void setY(double posY) {
        Y = posY;
    }

    public double getZ() {
        return Z;
    }

    public void setZ(double posZ) {
        Z = posZ;
    }

    public float getYaw() {
        return YAW;
    }

    public void setYaw(float yaw) {
        YAW = yaw;
        Minecraft.getMinecraft().thePlayer.prevRenderYawOffset = RENDERPREVYAW;
        Minecraft.getMinecraft().thePlayer.renderYawOffset = RENDERYAW;
        Minecraft.getMinecraft().thePlayer.prevRotationYawHead = RENDERPREVYAW;
        Minecraft.getMinecraft().thePlayer.rotationYawHead = RENDERYAW;
    }

    public float getPitch() {
        return PITCH;
    }

    public void setPitch(float pitch) {
        this.PITCH = pitch;

    }

    public boolean isOnGround() {
        return GROUND;
    }

    public static void setOnGround(boolean ground) {
        GROUND = ground;
    }


    public int getType() {
        return 1;
    }
}

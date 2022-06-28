/*
 * Decompiled with CFR 0_132.
 */
package space.emptiness.utils.math;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.AxisAlignedBB;
import space.emptiness.utils.Helper;
import space.emptiness.utils.sub.Rotation;
import space.emptiness.events.misc.EventPacket;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class RotationUtil {
    private static Minecraft mc;

    public static float pitch() {
        return Helper.mc.thePlayer.rotationPitch;
    }

    public static void setRotation(float yaw,float pitch){
        Minecraft.getMinecraft().thePlayer.rotationYawHead = yaw;
        Minecraft.getMinecraft().thePlayer.rotationPitchHead =pitch;
        Minecraft.getMinecraft().thePlayer.renderYawOffset = yaw;
    }
    public static float[] getFixedRotation(final float[] rotations, final float[] lastRotations) {
        final Minecraft mc = Minecraft.getMinecraft();

        final float yaw = rotations[0];
        final float pitch = rotations[1];

        final float lastYaw = lastRotations[0];
        final float lastPitch = lastRotations[1];

        final float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
        final float gcd = f * f * f * 1.2F;

        final float deltaYaw = yaw - lastYaw;
        final float deltaPitch = pitch - lastPitch;

        final float fixedDeltaYaw = deltaYaw - (deltaYaw % gcd);
        final float fixedDeltaPitch = deltaPitch - (deltaPitch % gcd);

        final float fixedYaw = lastYaw + fixedDeltaYaw;
        final float fixedPitch = lastPitch + fixedDeltaPitch;

        return new float[]{fixedYaw, fixedPitch};
    }


    public static boolean isVisibleFOV(final Entity e, final float fov) {
        return ((Math.abs(RotationUtil.getRotations(e)[0] - mc.thePlayer.rotationYaw) % 360.0f > 180.0f) ? (360.0f - Math.abs(RotationUtil.getRotations(e)[0] - mc.thePlayer.rotationYaw) % 360.0f) : (Math.abs(RotationUtil.getRotations(e)[0] - mc.thePlayer.rotationYaw) % 360.0f)) <= fov;
    }


    public static void pitch(float pitch) {
        Helper.mc.thePlayer.rotationPitch = pitch;
    }

    public static float yaw() {
        return Helper.mc.thePlayer.rotationYaw;
    }

    public static void yaw(float yaw) {
        Helper.mc.thePlayer.rotationYaw = yaw;
    }

    public static float[] faceTarget(Entity target, float p_706252, float p_706253, boolean miss) {
        double var6;
        double var4 = target.posX - Helper.mc.thePlayer.posX;
        double var8 = target.posZ - Helper.mc.thePlayer.posZ;
        if (target instanceof EntityLivingBase) {
            EntityLivingBase var10 = (EntityLivingBase)target;
            var6 = var10.posY + (double)var10.getEyeHeight() - (Helper.mc.thePlayer.posY + (double)Helper.mc.thePlayer.getEyeHeight());
        } else {
            var6 = (target.getEntityBoundingBox().minY + target.getEntityBoundingBox().maxY) / 2.0 - (Helper.mc.thePlayer.posY + (double)Helper.mc.thePlayer.getEyeHeight());
        }
        Random rnd = new Random();
        double var14 = MathHelper.sqrt_double(var4 * var4 + var8 * var8);
        float var12 = (float)(Math.atan2(var8, var4) * 180.0 / 3.141592653589793) - 90.0f;
        float var13 = (float)(- Math.atan2(var6 - (target instanceof EntityPlayer ? 0.25 : 0.0), var14) * 180.0 / 3.141592653589793);
        float pitch = RotationUtil.changeRotation(Helper.mc.thePlayer.rotationPitch, var13, p_706253);
        float yaw = RotationUtil.changeRotation(Helper.mc.thePlayer.rotationYaw, var12, p_706252);
        return new float[]{yaw, pitch};
    }

    public static float changeRotation(float p_706631, float p_706632, float p_706633) {
        float var4 = MathHelper.wrapAngleTo180_float(p_706632 - p_706631);
        if (var4 > p_706633) {
            var4 = p_706633;
        }
        if (var4 < - p_706633) {
            var4 = - p_706633;
        }
        return p_706631 + var4;
    }

    public static double[] getRotationToEntity(Entity entity) {
        double pX = Helper.mc.thePlayer.posX;
        double pY = Helper.mc.thePlayer.posY + (double)Helper.mc.thePlayer.getEyeHeight();
        double pZ = Helper.mc.thePlayer.posZ;
        double eX = entity.posX;
        double eY = entity.posY + (double)(entity.height / 2.0f);
        double eZ = entity.posZ;
        double dX = pX - eX;
        double dY = pY - eY;
        double dZ = pZ - eZ;
        double dH = Math.sqrt(Math.pow(dX, 2.0) + Math.pow(dZ, 2.0));
        double yaw = Math.toDegrees(Math.atan2(dZ, dX)) + 90.0;
        double pitch = Math.toDegrees(Math.atan2(dH, dY));
        return new double[]{yaw, 90.0 - pitch};
    }

    public static float[] getRotations(Entity entity) {
        double diffY;
        if (entity == null) {
            return null;
        }
        double diffX = entity.posX - Helper.mc.thePlayer.posX;
        double diffZ = entity.posZ - Helper.mc.thePlayer.posZ;
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase elb = (EntityLivingBase)entity;
            diffY = elb.posY + ((double)elb.getEyeHeight() - 0.4) - (Helper.mc.thePlayer.posY + (double)Helper.mc.thePlayer.getEyeHeight());
        } else {
            diffY = (entity.boundingBox.minY + entity.boundingBox.maxY) / 2.0 - (Helper.mc.thePlayer.posY + (double)Helper.mc.thePlayer.getEyeHeight());
        }
        double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0 / 3.141592653589793) - 90.0f;
        float pitch = (float)(- Math.atan2(diffY, dist) * 180.0 / 3.141592653589793);
        return new float[]{yaw, pitch};
    }

    public static float getDistanceBetweenAngles(float angle1, float angle2) {
        float angle3 = Math.abs(angle1 - angle2) % 360.0f;
        if (angle3 > 180.0f) {
            angle3 = 0.0f;
        }
        return angle3;
    }

    public static float[] grabBlockRotations(BlockPos pos) {
        return RotationUtil.getVecRotation(Helper.mc.thePlayer.getPositionVector().addVector(0.0, Helper.mc.thePlayer.getEyeHeight(), 0.0), new Vec3((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5));
    }

    public static float[] getVecRotation(Vec3 position) {
        return RotationUtil.getVecRotation(Helper.mc.thePlayer.getPositionVector().addVector(0.0, Helper.mc.thePlayer.getEyeHeight(), 0.0), position);
    }

    public static float[] getVecRotation(Vec3 origin, Vec3 position) {
        Vec3 difference = position.subtract(origin);
        double distance = difference.flat().lengthVector();
        float yaw = (float)Math.toDegrees(Math.atan2(difference.zCoord, difference.xCoord)) - 90.0f;
        float pitch = (float)(- Math.toDegrees(Math.atan2(difference.yCoord, distance)));
        return new float[]{yaw, pitch};
    }

    public static int wrapAngleToDirection(float yaw, int zones) {
        int angle = (int)((double)(yaw + (float)(360 / (2 * zones))) + 0.5) % 360;
        if (angle < 0) {
            angle += 360;
        }
        return angle / (360 / zones);
    }
    public static float getYawDifference(float currentYaw, double targetX, double targetY, double targetZ) {
        double deltaX = targetX - mc.thePlayer.posX;
        double deltaY = targetY - mc.thePlayer.posY;
        double deltaZ = targetZ - mc.thePlayer.posZ;
        double yawToEntity = 0;
        double degrees = Math.toDegrees(Math.atan(deltaZ / deltaX));
        if ((deltaZ < 0.0D) && (deltaX < 0.0D)) {
            if (deltaX != 0) yawToEntity = 90.0D + degrees;
        } else if ((deltaZ < 0.0D) && (deltaX > 0.0D)) {
            if (deltaX != 0) yawToEntity = -90.0D + degrees;
        } else {
            if (deltaZ != 0) yawToEntity = Math.toDegrees(-Math.atan(deltaX / deltaZ));
        }
        return MathHelper.wrapAngleTo180_float(-(currentYaw - (float) yawToEntity));
    }

    public static float getPitchDifference(float currentPitch, double targetX, double targetY, double targetZ) {
        double deltaX = targetX - mc.thePlayer.posX;
        double deltaY = targetY - mc.thePlayer.posY;
        double deltaZ = targetZ - mc.thePlayer.posZ;
        double distanceXZ = MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ);
        double pitchToEntity = -Math.toDegrees(Math.atan(deltaY / distanceXZ));
        return -MathHelper.wrapAngleTo180_float(currentPitch - (float) pitchToEntity) - 2.5F;
    }

    public static float getYawDifference(float currentYaw, float targetYaw) {
        return ((((currentYaw - targetYaw) % 360F) + 540F) % 360F) - 180F;
    }

    public static boolean serverRotate(EventPacket event, Rotation customRotation) {
        //锟斤拷取锟斤拷锟捷帮拷
        final Packet<?> packet = event.getPacket();

        //锟叫讹拷锟角凤拷为锟秸帮拷
        if (event.isInComing()) {

            //锟叫讹拷锟斤拷锟捷帮拷锟斤拷锟斤拷
            if (packet instanceof S08PacketPlayerPosLook) {
                S08PacketPlayerPosLook s08PacketPlayerPosLook = (S08PacketPlayerPosLook) packet;

                //锟睫革拷锟斤拷锟捷帮拷
                s08PacketPlayerPosLook.yaw = customRotation.getYaw();
                s08PacketPlayerPosLook.pitch = customRotation.getPitch();
                event.setPacket(s08PacketPlayerPosLook);
                return true;
            }
        }
        return false;
    }


    public static boolean canEntityBeSeen(Entity e) {
        Vec3 vec1 = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
        AxisAlignedBB box = e.getEntityBoundingBox();
        Vec3 vec2 = new Vec3(e.posX, e.posY + (double) (e.getEyeHeight() / 1.32F), e.posZ);
        double minx = e.posX - 0.25D;
        double maxx = e.posX + 0.25D;
        double miny = e.posY;
        double maxy = e.posY + Math.abs(e.posY - box.maxY);
        double minz = e.posZ - 0.25D;
        double maxz = e.posZ + 0.25D;
        boolean see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
        if (see) {
            return true;
        } else {
            vec2 = new Vec3(maxx, miny, minz);
            see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
            if (see) {
                return true;
            } else {
                vec2 = new Vec3(minx, miny, minz);
                see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
                if (see) {
                    return true;
                } else {
                    vec2 = new Vec3(minx, miny, maxz);
                    see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
                    if (see) {
                        return true;
                    } else {
                        vec2 = new Vec3(maxx, miny, maxz);
                        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
                        if (see) {
                            return true;
                        } else {
                            vec2 = new Vec3(maxx, maxy, minz);
                            see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
                            if (see) {
                                return true;
                            } else {
                                vec2 = new Vec3(minx, maxy, minz);
                                see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
                                if (see) {
                                    return true;
                                } else {
                                    vec2 = new Vec3(minx, maxy, maxz - 0.1D);
                                    see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
                                    if (see) {
                                        return true;
                                    } else {
                                        vec2 = new Vec3(maxx, maxy, maxz);
                                        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
                                        return see;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    public static float[] getBowAngles(Entity entity) {
        double xDelta = entity.posX - entity.lastTickPosX;
        double zDelta = entity.posZ - entity.lastTickPosZ;
        Minecraft.getMinecraft();
        double d2 = mc.thePlayer.getDistanceToEntity(entity);
        d2 -= d2 % 0.8;
        double xMulti = 1.0;
        double zMulti = 1.0;
        boolean sprint = entity.isSprinting();
        xMulti = d2 / 0.8 * xDelta * (sprint ? 1.25 : 1.0);
        zMulti = d2 / 0.8 * zDelta * (sprint ? 1.25 : 1.0);
        Minecraft.getMinecraft();
        double x2 = entity.posX + xMulti - mc.thePlayer.posX;
        Minecraft.getMinecraft();
        double z2 = entity.posZ + zMulti - mc.thePlayer.posZ;
        Minecraft.getMinecraft();
        Minecraft.getMinecraft();
        double y2 = mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight() - (entity.posY + (double) entity.getEyeHeight());
        Minecraft.getMinecraft();
        double dist = mc.thePlayer.getDistanceToEntity(entity);
        float yaw = (float) Math.toDegrees(Math.atan2(z2, x2)) - 90.0f;
        float pitch = (float) Math.toDegrees(Math.atan2(y2, dist));
        return new float[]{yaw, pitch};
    }



}


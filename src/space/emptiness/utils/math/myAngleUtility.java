
package space.emptiness.utils.math;

import java.util.Iterator;
import java.util.Random;

import javax.vecmath.Vector3d;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class myAngleUtility {
    private boolean aac;
    private float smooth;
    private Random random;
    private static Minecraft mc = Minecraft.getMinecraft();

    public myAngleUtility(boolean aac, float smooth) {
        this.aac = aac;
        this.smooth = smooth;
        this.random = new Random();
    }

    public myAngle calculateAngle(Vector3d destination, Vector3d source) {
        myAngle angles = new myAngle();
        destination.x += (double)(this.aac ? this.randomFloat(-0.75f, 0.75f) : 0.0f) - source.x;
        destination.y += (double)(this.aac ? this.randomFloat(-0.25f, 0.5f) : 0.0f) - source.y;
        destination.z += (double)(this.aac ? this.randomFloat(-0.75f, 0.75f) : 0.0f) - source.z;
        double hypotenuse = Math.hypot((double)destination.x, (double)destination.z);
        angles.setYaw((float)(Math.atan2((double)destination.z, (double)destination.x) * 57.29577951308232) - 90.0f);
        angles.setPitch(-((float)(Math.atan2((double)destination.y, (double)hypotenuse) * 57.29577951308232)));
        return angles.constrantAngle();
    }

    public myAngle smoothAngle(myAngle destination, myAngle source) {
        myAngle angles = new myAngle(source.getYaw() - destination.getYaw(), source.getPitch() - destination.getPitch()).constrantAngle();
        angles.setYaw(source.getYaw() - angles.getYaw() / 100.0f * this.smooth);
        angles.setPitch(source.getPitch() - angles.getPitch() / 100.0f * this.smooth);
        return angles.constrantAngle();
    }

    public float randomFloat(float min, float max) {
        return min + this.random.nextFloat() * (max - min);
    }

    public static double isInFov(float var0, float var1, double var2, double var4, double var6) {
        Vec3 var8 = new Vec3((double)var0, (double)var1, 0.0);
        float[] var9 = myAngleUtility.getAngleBetweenVecs(new Vec3(Minecraft.thePlayer.posX, Minecraft.thePlayer.posY, Minecraft.thePlayer.posZ), new Vec3(var2, var4, var6));
        double var10 = MathHelper.wrapAngleTo180_double((double)(var8.xCoord - (double)var9[0]));
        return Math.abs((double)var10) * 2.0;
    }

    public static float[] getAngleBetweenVecs(Vec3 var0, Vec3 var1) {
        double var2 = var1.xCoord - var0.xCoord;
        double var4 = var1.yCoord - var0.yCoord;
        double var6 = var1.zCoord - var0.zCoord;
        double var8 = Math.sqrt((double)(var2 * var2 + var6 * var6));
        float var10 = (float)(Math.atan2((double)var6, (double)var2) * 180.0 / 3.141592653589793) - 90.0f;
        float var11 = (float)(-(Math.atan2((double)var4, (double)var8) * 180.0 / 3.141592653589793));
        return new float[]{var10, var11};
    }

    public static float[] getAnglesIgnoringNull(Entity var0, float var1, float var2) {
        float[] var3 = myAngleUtility.getAngles(var0);
        if (var3 == null) {
            return new float[]{0.0f, 0.0f};
        }
        float var4 = var3[0];
        float var5 = var3[1];
        return new float[]{var1 + MathHelper.wrapAngleTo180_float((float)(var4 - var1)), var2 + MathHelper.wrapAngleTo180_float((float)(var5 - var2)) + 5.0f};
    }

    public static float[] getAngles(Entity entity) {
        double var5;
        if (entity == null) {
            return null;
        }
        double var1 = entity.posX - Minecraft.thePlayer.posX;
        double var3 = entity.posZ - Minecraft.thePlayer.posZ;
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase var7 = (EntityLivingBase)entity;
            var5 = var7.posY + ((double)var7.getEyeHeight() - 0.4) - (Minecraft.thePlayer.posY + (double)Minecraft.thePlayer.getEyeHeight());
        } else {
            var5 = (entity.getEntityBoundingBox().minY + entity.getEntityBoundingBox().maxY) / 2.0 - (Minecraft.thePlayer.posY + (double)Minecraft.thePlayer.getEyeHeight());
        }
        double var11 = MathHelper.sqrt_double((double)(var1 * var1 + var3 * var3));
        float var9 = (float)(Math.atan2((double)var3, (double)var1) * 180.0 / 3.141592653589793) - 90.0f;
        float var10 = (float)(-(Math.atan2((double)var5, (double)var11) * 180.0 / 3.141592653589793));
        return new float[]{var9, var10};
    }

    public static boolean isValidToRotate(double var0, double var2) {
        if (Minecraft.thePlayer != null && Minecraft.theWorld != null && Minecraft.thePlayer.getEntityWorld() != null) {
            Entity var5;
            Iterator var4 = Minecraft.thePlayer.getEntityWorld().loadedEntityList.iterator();
            do {
                if (var4.hasNext()) continue;
                return false;
            } while (!((var5 = (Entity)var4.next()) instanceof EntityPlayer) || var5 == Minecraft.thePlayer || (double)Minecraft.thePlayer.getDistanceToEntity(var5) >= var0 || myAngleUtility.isInFov(Minecraft.thePlayer.rotationYaw, Minecraft.thePlayer.rotationPitch, var5.posX, var5.posY, var5.posZ) >= var2);
            return true;
        }
        return false;
    }

    public static double normalizeAngle(double var0, double var2) {
        double var4 = Math.abs((double)(var0 % 360.0 - var2 % 360.0));
        var4 = Math.min((double)(360.0 - var4), (double)var4);
        return Math.abs((double)var4);
    }

    private double getAngleYaw(EntityLivingBase var1) {
        return myAngleUtility.getAnglesIgnoringNull((Entity)var1, Minecraft.thePlayer.rotationYaw, Minecraft.thePlayer.rotationPitch)[0];
    }
}

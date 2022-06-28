package space.emptiness.utils;

import com.google.common.base.Predicates;
import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.*;

import java.util.List;

public class RaycastUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static Entity raycastEntity(double range, float[] rotations) {
        final Entity player = mc.getRenderViewEntity();

        if (player != null && mc.theWorld != null) {
            final Vec3 eyeHeight = player.getPositionEyes(mc.timer.renderPartialTicks);

            final Vec3 looks = Entity.getVectorForRotation(rotations[0], rotations[1]);
            final Vec3 vec = eyeHeight.addVector(looks.xCoord * range, looks.yCoord * range, looks.zCoord * range);
            final List<Entity> list = mc.theWorld.getEntitiesInAABBexcluding(player, player.getEntityBoundingBox().addCoord(looks.xCoord * range, looks.yCoord * range, looks.zCoord * range).expand(1, 1, 1), Predicates.and(EntitySelectors.NOT_SPECTATING, Entity::canBeCollidedWith));

            Entity raycastedEntity = null;

            for (Entity entity : list) {
                if (!(entity instanceof EntityLivingBase)) continue;

                final float borderSize = entity.getCollisionBorderSize();
                final AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().expand(borderSize, borderSize, borderSize);
                final MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(eyeHeight, vec);

                if (axisalignedbb.isVecInside(eyeHeight)) {
                    if (range >= 0.0D) {
                        raycastedEntity = entity;
                        range = 0.0D;
                    }
                } else if (movingobjectposition != null) {
                    double distance = eyeHeight.distanceTo(movingobjectposition.hitVec);

                    if (distance < range || range == 0.0D) {

                        if (entity == player.ridingEntity) {
                            if (range == 0.0D) {
                                raycastedEntity = entity;
                            }
                        } else {
                            raycastedEntity = entity;
                            range = distance;
                        }
                    }
                }
            }
            return raycastedEntity;
        }
        return null;
    }

    public static Entity surroundEntity(Entity target) {
        Entity entity = target;

        for (Entity possibleTarget : mc.theWorld.loadedEntityList) {
            if (!possibleTarget.isInvisible() || target.getDistanceToEntity(possibleTarget) > .5) continue;
            if (mc.thePlayer.getDistanceToEntity(possibleTarget) < mc.thePlayer.getDistanceToEntity(entity)) {
                entity = possibleTarget;
            }
        }

        return target;
    }

    public static BlockPos raycastPosition(double range) {
        final Entity renderViewEntity = mc.getRenderViewEntity();

        if (renderViewEntity != null && mc.theWorld != null) {
            final MovingObjectPosition movingObjectPosition = renderViewEntity.rayTrace(range, 1F);

            if (mc.theWorld.getBlockState(movingObjectPosition.getBlockPos()).getBlock() instanceof BlockAir)
                return null;

            return movingObjectPosition.getBlockPos();
        }
        return null;
    }
}

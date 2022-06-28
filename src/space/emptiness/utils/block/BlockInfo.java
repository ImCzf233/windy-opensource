package space.emptiness.utils.block;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

public class BlockInfo {
    public BlockPos blockPos;
    public EnumFacing enumFacing;
    public Vec3 vec3;
    public float yaw;
    public float pitch;

    public BlockInfo(BlockPos blockPos, EnumFacing enumFacing, Vec3 vec3, float yaw, float pitch) {
        if (vec3 == null) {
            this.vec3 = new Vec3((double) blockPos.getX() + 0.5, (double) blockPos.getY() + 0.5, (double) blockPos.getZ() + 0.5);
        } else {
            this.vec3 = vec3;
        }
        this.blockPos = blockPos;
        this.enumFacing = enumFacing;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public static BlockInfo get(BlockPos blockPos) {
        if (BlockUtil.canBeClicked(blockPos.add(0, -1, 0))) {
            return new BlockInfo(blockPos.add(0, -1, 0), EnumFacing.UP, null, Float.NaN, 90f);
        } else if (BlockUtil.canBeClicked(blockPos.add(0, 0, 1))) {
            return new BlockInfo(blockPos.add(0, 0, 1), EnumFacing.NORTH, null, 0f, 75f);
        } else if (BlockUtil.canBeClicked(blockPos.add(-1, 0, 0))) {
            return new BlockInfo(blockPos.add(-1, 0, 0), EnumFacing.EAST, null, 90f, 75f);
        } else if (BlockUtil.canBeClicked(blockPos.add(0, 0, -1))) {
            return new BlockInfo(blockPos.add(0, 0, -1), EnumFacing.SOUTH, null, -180f, 75f);
        } else if (BlockUtil.canBeClicked(blockPos.add(1, 0, 0))) {
            return new BlockInfo(blockPos.add(1, 0, 0), EnumFacing.WEST, null, -90f, 75f);
        } else return null;
    }
}


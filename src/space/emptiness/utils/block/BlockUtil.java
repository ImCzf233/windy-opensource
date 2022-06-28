/*
 * Decompiled with CFR 0_132.
 */
package space.emptiness.utils.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BlockUtil {
    public static final List<Block> BLOCK_BLACKLIST = Arrays.asList(Blocks.enchanting_table, Blocks.chest,
            Blocks.ender_chest, Blocks.trapped_chest, Blocks.anvil, Blocks.sand, Blocks.web, Blocks.torch,
            Blocks.crafting_table, Blocks.furnace, Blocks.waterlily, Blocks.dispenser, Blocks.stone_pressure_plate,
            Blocks.wooden_pressure_plate, Blocks.noteblock, Blocks.dropper, Blocks.tnt, Blocks.standing_banner,
            Blocks.wall_banner);
    public static Minecraft mc = Minecraft.getMinecraft();

    public static int findAutoBlockBlock() {
        for (int i = 36; i < 45; i++) {
            final ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

            if (itemStack != null && itemStack.getItem() instanceof ItemBlock && itemStack.stackSize != 0) {
                final ItemBlock itemBlock = (ItemBlock) itemStack.getItem();
                final Block block = itemBlock.getBlock();

                if (block.isFullCube() && !BLOCK_BLACKLIST.contains(block))
                    return i;
            }
        }

        for (int i = 36; i < 45; i++) {
            final ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

            if (itemStack != null && itemStack.getItem() instanceof ItemBlock && itemStack.stackSize > 0) {
                final ItemBlock itemBlock = (ItemBlock) itemStack.getItem();
                final Block block = itemBlock.getBlock();

                if (!BLOCK_BLACKLIST.contains(block))
                    return i;
            }
        }

        return -1;
    }

    public static BlockPos Facing2BlockPos(EnumFacing facing, BlockPos bp) {
        switch (facing) {
            case DOWN:
                return bp.add(0, -1, 0);
            case EAST:
                return bp.add(1, 0, 0);
            case NORTH:
                return bp.add(0, 0, -1);
            case SOUTH:
                return bp.add(0, 0, 1);
            case UP:
                return bp.add(0, 1, 0);
            case WEST:
                return bp.add(-1, 0, 0);
            default:
                return null;
        }
    }

    public static final Block getBlock(BlockPos blockPos) {
        WorldClient var10000 = mc.theWorld;
        Block var2;
        if (var10000 != null) {
            IBlockState var1 = var10000.getBlockState(blockPos);
            if (var1 != null) {
                var2 = var1.getBlock();
                return var2;
            }
        }

        var2 = null;
        return var2;
    }

    public static final Material getMaterial(BlockPos blockPos) {
        Block var10000 = getBlock(blockPos);
        return var10000 != null ? var10000.getMaterial() : null;
    }

    public static final boolean isReplaceable(BlockPos blockPos) {
        Material var10000 = getMaterial(blockPos);
        return var10000 != null && var10000.isReplaceable();
    }

    public static final IBlockState getState(BlockPos blockPos) {
        IBlockState var10000 = mc.theWorld.getBlockState(blockPos);
        return var10000;
    }

    public static final boolean canBeClicked(BlockPos blockPos) {
        Block var10000 = getBlock(blockPos);
        boolean var2;
        if (var10000 != null && var10000.canCollideCheck(getState(blockPos), false)) {
            WorldClient var1 = mc.theWorld;
            if (var1.getWorldBorder().contains(blockPos)) {
                var2 = true;
                return var2;
            }
        }

        var2 = false;
        return var2;
    }

    public static final String getBlockName(int id) {
        Block var10000 = Block.getBlockById(id);
        String var1 = var10000.getLocalizedName();
        return var1;
    }

    public static final boolean isFullBlock(BlockPos blockPos) {
        Block var10000 = getBlock(blockPos);
        if (var10000 != null) {
            AxisAlignedBB var2 = var10000.getCollisionBoundingBox(mc.theWorld, blockPos, getState(blockPos));
            if (var2 != null) {
                AxisAlignedBB axisAlignedBB = var2;
                return axisAlignedBB.maxX - axisAlignedBB.minX == 1.0D && axisAlignedBB.maxY - axisAlignedBB.minY == 1.0D && axisAlignedBB.maxZ - axisAlignedBB.minZ == 1.0D;
            }
        }

        return false;
    }

    public static final double getCenterDistance(BlockPos blockPos) {
        return mc.thePlayer.getDistance((double) blockPos.getX() + 0.5D, (double) blockPos.getY() + 0.5D, (double) blockPos.getZ() + 0.5D);
    }

    public static final Map<BlockPos, Block> searchBlocks(int radius) {
        Map<BlockPos, Block> blocks = new LinkedHashMap<>();
        int x = radius;
        int var3 = -radius + 1;
        if (radius >= var3) {
            while (true) {
                int y = radius;
                int var5 = -radius + 1;
                if (radius >= var5) {
                    while (true) {
                        int z = radius;
                        int var7 = -radius + 1;
                        if (radius >= var7) {
                            while (true) {
                                BlockPos blockPos = new BlockPos((int) mc.thePlayer.posX + x, (int) mc.thePlayer.posY + y, (int) mc.thePlayer.posZ + z);
                                Block var10000 = getBlock(blockPos);
                                if (var10000 != null) {
                                    Block block = var10000;
                                    blocks.put(blockPos, block);
                                }

                                if (z == var7) {
                                    break;
                                }

                                --z;
                            }
                        }

                        if (y == var5) {
                            break;
                        }

                        --y;
                    }
                }

                if (x == var3) {
                    break;
                }

                --x;
            }
        }

        return blocks;
    }

    public static final boolean collideBlock(AxisAlignedBB axisAlignedBB, Collidable collide) {
        EntityPlayerSP var10000 = mc.thePlayer;
        int x = MathHelper.floor_double(var10000.getEntityBoundingBox().minX);
        var10000 = mc.thePlayer;

        for (int var3 = MathHelper.floor_double(var10000.getEntityBoundingBox().maxX) + 1; x < var3; ++x) {
            var10000 = mc.thePlayer;
            int z = MathHelper.floor_double(var10000.getEntityBoundingBox().minZ);
            var10000 = mc.thePlayer;

            for (int var5 = MathHelper.floor_double(var10000.getEntityBoundingBox().maxZ) + 1; z < var5; ++z) {
                Block block = getBlock(new BlockPos(x, axisAlignedBB.minY, z));
                if (!collide.collideBlock(block)) {
                    return false;
                }
            }
        }

        return true;
    }


    public static final boolean collideBlockIntersects(AxisAlignedBB axisAlignedBB, Collidable collide) {
        EntityPlayerSP var10000 = mc.thePlayer;
        int x = MathHelper.floor_double(var10000.getEntityBoundingBox().minX);
        var10000 = mc.thePlayer;
        for (int var3 = MathHelper.floor_double(var10000.getEntityBoundingBox().maxX) + 1; x < var3; ++x) {
            var10000 = mc.thePlayer;
            int z = MathHelper.floor_double(var10000.getEntityBoundingBox().minZ);
            var10000 = mc.thePlayer;
            for (int var5 = MathHelper.floor_double(var10000.getEntityBoundingBox().maxZ) + 1; z < var5; ++z) {
                BlockPos blockPos = new BlockPos(x, axisAlignedBB.minY, z);
                Block block = getBlock(blockPos);
                if (collide.collideBlock(block) && block != null) {
                    AxisAlignedBB var9 = block.getCollisionBoundingBox(mc.theWorld, blockPos, getState(blockPos));
                    if (var9 != null) {
                        AxisAlignedBB boundingBox = var9;
                        var10000 = mc.thePlayer;
                        if (var10000.getEntityBoundingBox().intersectsWith(boundingBox)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    interface Collidable {
        boolean collideBlock(Block block);
    }

}


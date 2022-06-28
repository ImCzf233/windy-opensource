package space.emptiness.utils.block;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import space.emptiness.module.modules.world.Scaffold;

public class BlockCache {
    final Scaffold module;
    private final BlockPos position;
    private final EnumFacing facing;

    private BlockCache(Scaffold scaffold, BlockPos position, EnumFacing facing) {
        this.module = scaffold;
        this.position = position;
        this.facing = facing;
    }

    public BlockCache(Scaffold var1, BlockPos var2, EnumFacing var3, BlockCache var4) {
        this(var1, var2, var3);
    }

    public static BlockPos access$0(BlockCache var0) {
        return var0.getPosition();
    }

    public static EnumFacing access$1(BlockCache var0) {
        return var0.getFacing();
    }

    public static BlockPos access$2(BlockCache var0) {
        return var0.position;
    }

    public static EnumFacing access$3(BlockCache var0) {
        return var0.facing;
    }

    private BlockPos getPosition() {
        return this.position;
    }

    private EnumFacing getFacing() {
        return this.facing;
    }
}

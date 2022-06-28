package space.emptiness.module.modules.world;

import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import space.emptiness.events.EventTarget;
import space.emptiness.events.rendering.EventRender2D;
import space.emptiness.events.rendering.EventRender3D;
import space.emptiness.events.world.EventSafeWalk;
import space.emptiness.events.world.EventUpdate;
import space.emptiness.module.Category;
import space.emptiness.module.Module;
import space.emptiness.module.value.Mode;
import space.emptiness.module.value.Numbers;
import space.emptiness.module.value.Option;
import space.emptiness.utils.InventoryUtil;
import space.emptiness.utils.InventoryUtils;
import space.emptiness.utils.PlayerUtil;
import space.emptiness.utils.TimerUtil;
import space.emptiness.utils.block.BlockCache;
import space.emptiness.utils.block.BlockInfo;
import space.emptiness.utils.block.BlockUtil;
import space.emptiness.utils.math.MathUtil;
import space.emptiness.utils.move.PlayerUtils;
import space.emptiness.utils.render.RenderUtil;
import space.emptiness.utils.sub.Rotation;
import space.emptiness.Client;
import space.emptiness.gui.font.FontLoaders;

import java.awt.*;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Scaffold extends Module {

    // ??????????????
    private BlockData data;

    // ?????§Ý??????
    private int slot, towerTick;

    //???????
    private static final Rotation rotation = new Rotation(999, 999);

    // ????
    private final TimerUtil timer = new TimerUtil(), towerTimer = new TimerUtil();

    //???????Y(KeepY)
    private double posY;

    private final Numbers<Number> delayValue = new Numbers<>("Delay", "Delay", 0.5, 0.0, 10.0, 0.5),
            boostValue = new Numbers<>("TowerBoost", "Towerboost", 1.0, 0.0, 5.0, 0.1);

    private final Option<Boolean> towerMoveValue = new Option<>("Tower Move", "TowerMove", true),
            positionValue = new Option<>("Position", "Position", true),
            keepYValue = new Option<>("Keep Y", "Same Y", false),
            safeWalkValue = new Option<>("Safe Walk", "SafeWalk", true),
            sprintValue = new Option<>("Sprint", "Sprinting", true),
            swingValue = new Option<>("Swing", "Swing", true),
            towerValue = new Option<>("Tower", "Tower", true),
            eagleValue = new Option<>("Eagle", "Eagle", false);
    private static FloatBuffer colorBuffer;
    private static final Vec3 LIGHT0_POS;
    private static final Vec3 LIGHT1_POS;

    public Option<Boolean> mark = new Option("Mark","Mark",true);
    public Mode<Enum> markmode = new Mode("MarkMode","MarkMode",ESPMODES.values(),ESPMODES.Basic);

    private static final List<Block> blacklisted = Arrays.asList(Blocks.air, Blocks.water, Blocks.flowing_water, Blocks.lava,
            Blocks.flowing_lava, Blocks.enchanting_table, Blocks.ender_chest, Blocks.yellow_flower, Blocks.carpet,
            Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.crafting_table,
            Blocks.snow_layer, Blocks.packed_ice, Blocks.coal_ore, Blocks.diamond_ore, Blocks.emerald_ore,
            Blocks.chest, Blocks.torch, Blocks.anvil, Blocks.trapped_chest, Blocks.noteblock, Blocks.gold_ore,
            Blocks.iron_ore, Blocks.lapis_ore, Blocks.lit_redstone_ore, Blocks.redstone_ore,
            Blocks.wooden_pressure_plate, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate,
            Blocks.heavy_weighted_pressure_plate, Blocks.stone_button, Blocks.wooden_button, Blocks.cactus,
            Blocks.lever, Blocks.activator_rail, Blocks.rail, Blocks.detector_rail, Blocks.golden_rail,
            Blocks.furnace, Blocks.ladder, Blocks.oak_fence, Blocks.redstone_torch, Blocks.iron_trapdoor,
            Blocks.trapdoor, Blocks.tripwire_hook, Blocks.hopper, Blocks.acacia_fence_gate, Blocks.birch_fence_gate,
            Blocks.dark_oak_fence_gate, Blocks.jungle_fence_gate, Blocks.spruce_fence_gate, Blocks.oak_fence_gate,
            Blocks.dispenser, Blocks.sapling, Blocks.tallgrass, Blocks.deadbush, Blocks.web, Blocks.red_flower,
            Blocks.red_mushroom, Blocks.brown_mushroom, Blocks.nether_brick_fence, Blocks.vine, Blocks.double_plant,
            Blocks.flower_pot, Blocks.beacon, Blocks.pumpkin, Blocks.lit_pumpkin);

    public Scaffold() {
        super("Scaffold", new String[]{"BlockFly"}, Category.World);
        addValues(boostValue, delayValue, safeWalkValue, keepYValue, towerMoveValue, positionValue,mark,markmode, towerValue, sprintValue, swingValue, eagleValue);
    }



    private static FloatBuffer setColorBuffer(final double p_setColorBuffer_0_, final double p_setColorBuffer_2_, final double p_setColorBuffer_4_, final double p_setColorBuffer_6_) {
        return setColorBuffer((float)p_setColorBuffer_0_, (float)p_setColorBuffer_2_, (float)p_setColorBuffer_4_, (float)p_setColorBuffer_6_);
    }

    private static FloatBuffer setColorBuffer(final float p_setColorBuffer_0_, final float p_setColorBuffer_1_, final float p_setColorBuffer_2_, final float p_setColorBuffer_3_) {
        colorBuffer.clear();
        colorBuffer.put(p_setColorBuffer_0_).put(p_setColorBuffer_1_).put(p_setColorBuffer_2_).put(p_setColorBuffer_3_);
        colorBuffer.flip();
        return colorBuffer;
    }
    static {
        colorBuffer = GLAllocation.createDirectFloatBuffer(16);
        LIGHT0_POS = new Vec3(0.20000000298023224, 1.0, -0.699999988079071).normalize();
        LIGHT1_POS = new Vec3(-0.20000000298023224, 1.0, 0.699999988079071).normalize();
    }

    @EventTarget
    private void render(final EventRender2D e) {
        this.setColor(new Color(0xD1FCFF).getRGB());
        ScaledResolution sr = new ScaledResolution(mc);
        int width = sr.getScaledWidth();
        int height = sr.getScaledHeight();
        int middleX = width / 2;
        int middleY = height / 2;

        if (this.getBlockCount() + this.getallBlockCount() > 32) {
            mc.fontRendererObj.drawStringWithShadow(EnumChatFormatting.WHITE +String.valueOf(getBlockCount() + getallBlockCount())+" Blocks", middleX + 5 - FontLoaders.kiona20.getStringWidth(String.valueOf(getBlockCount() + getallBlockCount())) / 2, middleY + 25, new Color(255, 255, 255).getRGB());
        } else {
            mc.fontRendererObj.drawStringWithShadow(EnumChatFormatting.WHITE + String.valueOf(getBlockCount() + getallBlockCount())+" Blocks", middleX + 5 - FontLoaders.kiona20.getStringWidth(String.valueOf(getBlockCount() + getallBlockCount())) / 2, middleY + 25, new Color(255, 255, 255).getRGB());
        }


    }

    @EventTarget
    private void SAFASF(EventRender3D e) {
        if(this.mark.getValue()) {
            if (markmode.getValue() == ESPMODES.Basic) {
                String direction = mc.getRenderViewEntity().getHorizontalFacing().name();
                /* 125:    */
                /* 126:120 */
                BlockPos pos2 = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0D, mc.thePlayer.posZ);
                /* 127:122 */
                if ((mc.theWorld.getBlockState(pos2).getBlock() != Blocks.air) && (!mc.gameSettings.keyBindJump.pressed))
                    /* 128:    */ {
                    /* 129:123 */
                    if (direction.equalsIgnoreCase("NORTH"))
                        /* 130:    */ {
                        /* 131:124 */
                        BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0D, mc.thePlayer.posZ - 1.0D);
                        /* 132:125 */
                        renderChest(pos, Color.WHITE);
                        /* 133:    */
                    }
                    /* 134:126 */
                    else if (direction.equalsIgnoreCase("SOUTH"))
                        /* 135:    */ {
                        /* 136:127 */
                        BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0D, mc.thePlayer.posZ + 1.0D);
                        /* 137:128 */
                        renderChest(pos, Color.WHITE);
                        /* 138:    */
                    }
                    /* 139:129 */
                    else if (direction.equalsIgnoreCase("WEST"))
                        /* 140:    */ {
                        /* 141:130 */
                        BlockPos pos = new BlockPos(mc.thePlayer.posX - 1.0D, mc.thePlayer.posY - 1.0D, mc.thePlayer.posZ);
                        /* 142:131 */
                        renderChest(pos, Color.WHITE);
                        /* 143:    */
                    }
                    /* 144:132 */
                    else if (direction.equalsIgnoreCase("EAST"))
                        /* 145:    */ {
                        /* 146:133 */
                        BlockPos pos = new BlockPos(mc.thePlayer.posX + 1.0D, mc.thePlayer.posY - 1.0D, mc.thePlayer.posZ);
                        /* 147:134 */
                        renderChest(pos, Color.WHITE);
                        /* 148:    */
                    }
                    /* 149:    */
                }
                /* 150:    */
                else
                    /* 151:    */ {
                    /* 152:137 */
                    BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0D, mc.thePlayer.posZ);
                    /* 153:138 */
                    renderChest(pos, Color.WHITE);
                    /* 154:    */
                }

            } else {
                Color color = new Color(Color.BLACK.getRGB());
                Color color2 = new Color(Color.ORANGE.getRGB());
                double x = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * this.mc.timer.renderPartialTicks - RenderManager.renderPosX;
                double y = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * this.mc.timer.renderPartialTicks - RenderManager.renderPosY;
                double z = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * this.mc.timer.renderPartialTicks - RenderManager.renderPosZ;
                double x2 = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * this.mc.timer.renderPartialTicks - RenderManager.renderPosX;
                double y2 = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * this.mc.timer.renderPartialTicks - RenderManager.renderPosY;
                double z2 = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * this.mc.timer.renderPartialTicks - RenderManager.renderPosZ;
                x -= 0.65;
                z -= 0.65;
                x2 -= 0.5;
                z2 -= 0.5;
                y += mc.thePlayer.getEyeHeight() + 0.35 - (mc.thePlayer.isSneaking() ? 0.25 : 0.0);
                GL11.glPushMatrix();
                GL11.glEnable(3042);
                GL11.glBlendFunc(770, 771);
                final double rotAdd = -0.25 * (Math.abs(mc.thePlayer.rotationPitch) / 90.0f);
                GL11.glDisable(3553);
                GL11.glEnable(2848);
                GL11.glDisable(2929);
                GL11.glDepthMask(false);
                GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, 1.0f);
                GL11.glLineWidth(2.0f);
                RenderUtil.drawOutlinedBoundingBox(new AxisAlignedBB(x, y - 2, z, x + 1.3, y - 2, z + 1.3));
                RenderUtil.drawOutlinedBoundingBox(new AxisAlignedBB(x2, y - 2, z2, x2 + 1, y - 2, z2 + 1));
                if (this.mc.gameSettings.keyBindJump.pressed || Client.instance.getModuleManager().getModuleByName("Speed").isEnabled() && Minecraft.getMinecraft().thePlayer.moveForward != 0) {
                    GL11.glColor4f(color2.getRed() / 255.0f, color2.getGreen() / 255.0f, color2.getBlue() / 255.0f, 1.0f);
                    RenderUtil.drawOutlinedBoundingBox(new AxisAlignedBB(x, y - 2, z, x + 1.3, y - 2, z + 1.3));
                }
                GL11.glDisable(2848);
                GL11.glEnable(3553);
                GL11.glEnable(2929);
                GL11.glDepthMask(true);
                GL11.glDisable(3042);
                GL11.glPopMatrix();
            }
        }
    }
    public static void renderChest(BlockPos blockPos, Color white) {
        double d0 = (double)blockPos.getX() - Minecraft.getMinecraft().getRenderManager().viewerPosX;
        double d1 = (double)blockPos.getY() - Minecraft.getMinecraft().getRenderManager().viewerPosY;
        double d2 = (double)blockPos.getZ() - Minecraft.getMinecraft().getRenderManager().viewerPosZ;
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(1.0F);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glColor4d(255.0D, 255.0D, 255D, 15.0D);
        RenderGlobal.func_181561_a(new AxisAlignedBB(d0, d1, d2, d0 + 1.0D, d1 + 1.0D, d2 + 1.0D));
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    private int width1=0;



    private int getBlockColor(final int count) {
        final float f = (float)count;
        final float f2 = 64.0f;
        final float f3 = Math.max(0.0f, Math.min(f, f2) / f2);
        return Color.HSBtoRGB(f3 / 3.0f, 1.0f, 1.0f) | 0xFF000000;
    }

    private int getBlockCount() {
        int blockCount = 0;
        for (int i = 0; i < 45; ++i) {
            final Minecraft mc = Scaffold.mc;
            if (Minecraft.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final Minecraft mc2 = Scaffold.mc;
                final ItemStack is = Minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();
                final Item item = is.getItem();
                if (is.getItem() instanceof ItemBlock) {
                    if (!Scaffold.blacklisted.contains(((ItemBlock)item).getBlock())) {
                        blockCount += is.stackSize;
                    }
                }
            }
        }
        return blockCount;
    }

    public int getallBlockCount() {
        int n = 0;
        int i = 0;
        while (i < 36) {
            if (this.mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack stack = this.mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                final Item item = stack.getItem();
                if (stack.getItem() instanceof ItemBlock && this.isValid(item)) {
                    n += stack.stackSize;
                }
            }
            ++i;
        }
        return n;
    }


    @Override
    public void onEnable() {
        super.onEnable();

        // ???¡Â??????
        data = null;
        slot = -1;

        // ????
        mc.thePlayer.rotationYawHead = 999;
        mc.thePlayer.rotationPitchHead = 999;
        mc.thePlayer.renderYawOffset = 999;
        rotation.setYaw(999);
        rotation.setPitch(999);

        timer.reset();
        towerTimer.reset();

        posY = mc.thePlayer.posY;

        towerTick = 0;
    }

    @Override
    public void onDisable() {
        super.onDisable();

        mc.timer.timerSpeed = 1;

        mc.thePlayer.rotationYawHead = 999;
        mc.thePlayer.rotationPitchHead = 999;
        mc.thePlayer.renderYawOffset = 999;
        rotation.setYaw(999);
        rotation.setPitch(999);


        // ????
        if (eagleValue.getValue()) {
            mc.gameSettings.keyBindSneak.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode());
        }
    }

    @EventTarget
    void onUpdate(EventUpdate event) {
        mc.thePlayer.setSprinting(((Boolean) this.sprintValue.getValue()).booleanValue());
        //????????§Ù???
        if (getAllBlockCount() <= 0) return;

        //????Y
        if (keepYValue.getValue()) {

            //??????????????Y  ??????????
            if (posY > mc.thePlayer.posY || mc.thePlayer.fallDistance > 1.5) posY = mc.thePlayer.posY;

            if (PlayerUtils.isOnGround(1.15) && !PlayerUtils.MovementInput() && !PlayerUtils.isOnGround(-2) && mc.gameSettings.keyBindJump.pressed && towerValue.getValue()) {
                posY = mc.thePlayer.posY;
            }
        } else {
            posY = mc.thePlayer.posY;
        }

        // ???????????
        data = getBlockData(new BlockPos(mc.thePlayer.posX, posY - 1, mc.thePlayer.posZ)) == null ? getBlockData(new BlockPos(mc.thePlayer.posX, posY - 1, mc.thePlayer.posZ).down()) : getBlockData(new BlockPos(mc.thePlayer.posX, posY - 1, mc.thePlayer.posZ));

        //?????
        autoRefill(event);

        //????????????
        if (data == null) return;

        //?????????
        slot = getBestBlockSlotHotBar() == -1 ? -1 : (getBestBlockSlotHotBar() - 36);

        //????????????????§¹
        if (slot == -1) return;

        if (event.isPre()) {

            if (!sprintValue.getValue() && mc.thePlayer.isSprinting()) mc.thePlayer.setSprinting(false);

            //??
            onRotation(event);

            //????
            if (PlayerUtils.isOnGround(1.15) && !PlayerUtils.isOnGround(-2) && mc.gameSettings.keyBindJump.pressed && towerValue.getValue()) {
                if (PlayerUtils.MovementInput()) {

                    if (towerMoveValue.getValue() && !keepYValue.getValue()) {
                        if (PlayerUtils.isOnGround(.76) && !PlayerUtils.isOnGround(.75) && mc.thePlayer.motionY > .23 && mc.thePlayer.motionY < .25)
                            mc.thePlayer.motionY = (double) Math.round(mc.thePlayer.posY) - mc.thePlayer.posY;
                        if (PlayerUtils.isOnGround(1.0E-4)) {
                            mc.thePlayer.motionY = .41999998688698;
                            mc.thePlayer.motionX *= .9;
                            mc.thePlayer.motionZ *= .9;
                        } else if (mc.thePlayer.posY >= (double) Math.round(mc.thePlayer.posY) - 1.0E-4 && mc.thePlayer.posY <= (double) Math.round(mc.thePlayer.posY) + 1.0E-4)
                            mc.thePlayer.motionY = 0;
                    }

                    //????
                    if (mc.timer.timerSpeed == 1 + (boostValue.getValue().floatValue() == 0 ? 0 : boostValue.getValue().floatValue() + .015555F)) {
                        mc.timer.timerSpeed = 1;
                    }
                    towerTick = 0;
                } else {
                    if (PlayerUtils.isAirUnder(mc.thePlayer)) {

                        PlayerUtils.setSpeed(0);
                        mc.thePlayer.setPosition(mc.thePlayer.prevPosX, mc.thePlayer.posY, mc.thePlayer.prevPosZ);

                        mc.thePlayer.jump();

                        if (data != null && positionValue.getValue()) {
                            mc.thePlayer.setPosition(data.getBlockPos().getX() + .5, mc.thePlayer.posY, data.getBlockPos().getZ() + .5);
                        }

                        if (towerTick > 0) {
                            mc.timer.timerSpeed = 1 + (boostValue.getValue().floatValue() == 0 ? 0 : boostValue.getValue().floatValue() + .015555F);
                        }
                    }
                }
            } else {

                if (mc.timer.timerSpeed == 1 + (boostValue.getValue().floatValue() == 0 ? 0 : boostValue.getValue().floatValue() + .015555F)) {
                    mc.timer.timerSpeed = 1;
                }
                towerTick = 0;
            }

            //????
            if (eagleValue.getValue()) mc.gameSettings.keyBindSneak.pressed = PlayerUtils.isAirUnder(mc.thePlayer);
        }

        if (event.isPost()) {

            if (slot == -1) {
                timer.reset();
                return;
            }

            if (!timer.hasPassed(delayValue.getValue().intValue() * 100L)) return;

            int last = mc.thePlayer.inventory.currentItem;
            mc.thePlayer.inventory.currentItem = slot;
            if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem(), data.getBlockPos(), data.getEnumFacing(), getVec3ByBlockData(data).addVector(MathUtil.getRandom().nextDouble() / 5000, 0, MathUtil.getRandom().nextDouble() / 5000))) {
                if (swingValue.getValue()) mc.thePlayer.swingItem();
                else mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
            }
            mc.thePlayer.inventory.currentItem = last;
            timer.reset();
        }
    }

    @EventTarget
    void onSafeWalk(EventSafeWalk event) {
        event.setCancelled(safeWalkValue.getValue() && mc.thePlayer.onGround);
    }

    //??????
    private void onRotation(EventUpdate event) {

        Block under = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - .5F, mc.thePlayer.posZ)).getBlock();
        if (under == Blocks.air || !under.isFullBlock()) {

            Vec3 vec3 = getVec3ByBlockData(data);

            double x = vec3.xCoord - mc.thePlayer.posX;
            double y = mc.thePlayer.getEntityBoundingBox().minY + mc.thePlayer.getEyeHeight() - vec3.yCoord;
            double z = vec3.zCoord - mc.thePlayer.posZ;
            double dist = MathHelper.sqrt_double(x * x + z * z);
            float yaw = (float) (Math.atan2(z, x) * 180 / Math.PI) - 90- ((float) (Math.atan2(z, x) * 180 / Math.PI) - 90) % 45;
            float pitch = (float) (Math.atan2(y, dist) * 180 / Math.PI);
            if (yaw < 0) yaw += 360;


            mc.thePlayer.rotationYawHead = yaw;
            mc.thePlayer.rotationPitchHead = pitch;
            mc.thePlayer.renderYawOffset = yaw;
            rotation.setYaw(yaw);
            rotation.setPitch(pitch);
        }

        if (rotation.getYaw() != 999){
            mc.thePlayer.rotationYawHead = rotation.getYaw();
            mc.thePlayer.renderYawOffset = rotation.getYaw();
            event.setYaw(rotation.getYaw());
        }
        if (rotation.getPitch() != 999){
            event.setPitch(rotation.getPitch());
            mc.thePlayer.rotationPitchHead = rotation.getPitch();
        }
    }

    //??????????????????
    private boolean isValid(Item item) {
        if (!(item instanceof ItemBlock)) {
            return false;
        } else {
            ItemBlock iBlock = (ItemBlock) item;
            Block block = iBlock.getBlock();
            return !blacklisted.contains(block);
        }
    }

    //??????????¦Ë??
    private Vec3 getVec3ByBlockData(BlockData data) {
        BlockPos pos = data.getBlockPos();
        double rand = .5 + (PlayerUtils.MovementInput() ? (MathUtil.getRandom().nextBoolean() ? MathUtil.getRandom(-1E-12, -1E-8) : MathUtil.getRandom(1E-12, 1E-8)) : 0);
        double x = pos.getX() + rand;
        double y = pos.getY() + rand;
        double z = pos.getZ() + rand;
        return new Vec3(x, y, z);
    }

    //??????????????
    public int getAllBlockCount() {
        int blockCount = 0;
        for (int i = 0; i < 45; i++) {
            blockCount += (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem() instanceof ItemBlock) ? mc.thePlayer.inventoryContainer.getSlot(i).getStack().stackSize : 0;
        }
        return blockCount;
    }

    //????????????????
    public int getHotBarBlockCount() {
        int blockCount = 0;
        for (int i = 36; i < 45; i++) {
            blockCount += (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem() instanceof ItemBlock) ? mc.thePlayer.inventoryContainer.getSlot(i).getStack().stackSize : 0;
        }
        return blockCount;
    }

    private void autoRefill(EventUpdate event) {

        if (!event.isPre()) return;

        int bestInvSlot = getBestBlockSlotInventory();
        int bestHotbarSlot = getBestBlockSlotHotBar();
        int bestSlot = getBestBlockSlotHotBar() > 0 ? getBestBlockSlotHotBar() : getBestBlockSlotInventory();
        int spoofSlot = 42;
        if (bestHotbarSlot > 0 && bestInvSlot > 0) {
            if (mc.thePlayer.inventoryContainer.getSlot(bestInvSlot).getHasStack() && mc.thePlayer.inventoryContainer.getSlot(bestHotbarSlot).getHasStack()) {
                if (mc.thePlayer.inventoryContainer.getSlot(bestHotbarSlot).getStack().stackSize < mc.thePlayer.inventoryContainer.getSlot(bestInvSlot).getStack().stackSize) {
                    bestSlot = bestInvSlot;
                }
            }
        }
        if (getBestBlockSlotHotBar() != -1) {
            for (int a = 36; a < 45; a++) {
                if (mc.thePlayer.inventoryContainer.getSlot(a).getHasStack()) {
                    Item item = mc.thePlayer.inventoryContainer.getSlot(a).getStack().getItem();
                    if (item instanceof ItemBlock && isValid(item)) {
                        spoofSlot = a;
                        break;
                    }
                }
            }
        } else {
            for (int a = 36; a < 45; a++) {
                if (!mc.thePlayer.inventoryContainer.getSlot(a).getHasStack()) {
                    spoofSlot = a;
                    break;
                }
            }
        }

        if (mc.thePlayer.inventoryContainer.getSlot(spoofSlot).slotNumber != bestSlot) {
            InventoryUtils.swap(bestSlot, spoofSlot - 36);
            mc.playerController.updateController();
        }
    }

    //???????????????????????
    private int getBestBlockSlotHotBar() {
        int slot = -1;
        int size = 0;
        for (int i = 36; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                Item item = mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem();
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (item instanceof ItemBlock && isValid(item)) {
                    if (is.stackSize > size) {
                        size = is.stackSize;
                        slot = i;
                    }
                }
            }
        }
        return slot;
    }

    //??????????????????????
    private int getBestBlockSlotInventory() {
        int slot = -1;
        int size = 0;
        for (int i = 9; i < 36; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                Item item = mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem();
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (item instanceof ItemBlock && isValid(item)) {
                    if (is.stackSize > size) {
                        size = is.stackSize;
                        slot = i;
                    }
                }
            }
        }
        return slot;
    }

    //???????????
    private BlockData getBlockData(BlockPos pos) {
        if (isPosValid(pos.add(0, -1, 0))) {
            return new BlockData(pos.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosValid(pos.add(-1, 0, 0))) {
            return new BlockData(pos.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosValid(pos.add(1, 0, 0))) {
            return new BlockData(pos.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosValid(pos.add(0, 0, 1))) {
            return new BlockData(pos.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosValid(pos.add(0, 0, -1))) {
            return new BlockData(pos.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos1 = pos.add(-1, 0, 0);
        if (isPosValid(pos1.add(0, -1, 0))) {
            return new BlockData(pos1.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosValid(pos1.add(-1, 0, 0))) {
            return new BlockData(pos1.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosValid(pos1.add(1, 0, 0))) {
            return new BlockData(pos1.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosValid(pos1.add(0, 0, 1))) {
            return new BlockData(pos1.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosValid(pos1.add(0, 0, -1))) {
            return new BlockData(pos1.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos2 = pos.add(1, 0, 0);
        if (isPosValid(pos2.add(0, -1, 0))) {
            return new BlockData(pos2.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosValid(pos2.add(-1, 0, 0))) {
            return new BlockData(pos2.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosValid(pos2.add(1, 0, 0))) {
            return new BlockData(pos2.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosValid(pos2.add(0, 0, 1))) {
            return new BlockData(pos2.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosValid(pos2.add(0, 0, -1))) {
            return new BlockData(pos2.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos3 = pos.add(0, 0, 1);
        if (isPosValid(pos3.add(0, -1, 0))) {
            return new BlockData(pos3.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosValid(pos3.add(-1, 0, 0))) {
            return new BlockData(pos3.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosValid(pos3.add(1, 0, 0))) {
            return new BlockData(pos3.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosValid(pos3.add(0, 0, 1))) {
            return new BlockData(pos3.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosValid(pos3.add(0, 0, -1))) {
            return new BlockData(pos3.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos4 = pos.add(0, 0, -1);
        if (isPosValid(pos4.add(0, -1, 0))) {
            return new BlockData(pos4.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosValid(pos4.add(-1, 0, 0))) {
            return new BlockData(pos4.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosValid(pos4.add(1, 0, 0))) {
            return new BlockData(pos4.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosValid(pos4.add(0, 0, 1))) {
            return new BlockData(pos4.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosValid(pos4.add(0, 0, -1))) {
            return new BlockData(pos4.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos19 = pos.add(-2, 0, 0);
        if (isPosValid(pos1.add(0, -1, 0))) {
            return new BlockData(pos1.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosValid(pos1.add(-1, 0, 0))) {
            return new BlockData(pos1.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosValid(pos1.add(1, 0, 0))) {
            return new BlockData(pos1.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosValid(pos1.add(0, 0, 1))) {
            return new BlockData(pos1.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosValid(pos1.add(0, 0, -1))) {
            return new BlockData(pos1.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos29 = pos.add(2, 0, 0);
        if (isPosValid(pos2.add(0, -1, 0))) {
            return new BlockData(pos2.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosValid(pos2.add(-1, 0, 0))) {
            return new BlockData(pos2.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosValid(pos2.add(1, 0, 0))) {
            return new BlockData(pos2.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosValid(pos2.add(0, 0, 1))) {
            return new BlockData(pos2.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosValid(pos2.add(0, 0, -1))) {
            return new BlockData(pos2.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos39 = pos.add(0, 0, 2);
        if (isPosValid(pos3.add(0, -1, 0))) {
            return new BlockData(pos3.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosValid(pos3.add(-1, 0, 0))) {
            return new BlockData(pos3.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosValid(pos3.add(1, 0, 0))) {
            return new BlockData(pos3.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosValid(pos3.add(0, 0, 1))) {
            return new BlockData(pos3.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosValid(pos3.add(0, 0, -1))) {
            return new BlockData(pos3.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos49 = pos.add(0, 0, -2);
        if (isPosValid(pos4.add(0, -1, 0))) {
            return new BlockData(pos4.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosValid(pos4.add(-1, 0, 0))) {
            return new BlockData(pos4.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosValid(pos4.add(1, 0, 0))) {
            return new BlockData(pos4.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosValid(pos4.add(0, 0, 1))) {
            return new BlockData(pos4.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosValid(pos4.add(0, 0, -1))) {
            return new BlockData(pos4.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos5 = pos.add(0, -1, 0);
        if (isPosValid(pos5.add(0, -1, 0))) {
            return new BlockData(pos5.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosValid(pos5.add(-1, 0, 0))) {
            return new BlockData(pos5.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosValid(pos5.add(1, 0, 0))) {
            return new BlockData(pos5.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosValid(pos5.add(0, 0, 1))) {
            return new BlockData(pos5.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosValid(pos5.add(0, 0, -1))) {
            return new BlockData(pos5.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos6 = pos5.add(1, 0, 0);
        if (isPosValid(pos6.add(0, -1, 0))) {
            return new BlockData(pos6.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosValid(pos6.add(-1, 0, 0))) {
            return new BlockData(pos6.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosValid(pos6.add(1, 0, 0))) {
            return new BlockData(pos6.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosValid(pos6.add(0, 0, 1))) {
            return new BlockData(pos6.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosValid(pos6.add(0, 0, -1))) {
            return new BlockData(pos6.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos7 = pos5.add(-1, 0, 0);
        if (isPosValid(pos7.add(0, -1, 0))) {
            return new BlockData(pos7.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosValid(pos7.add(-1, 0, 0))) {
            return new BlockData(pos7.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosValid(pos7.add(1, 0, 0))) {
            return new BlockData(pos7.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosValid(pos7.add(0, 0, 1))) {
            return new BlockData(pos7.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosValid(pos7.add(0, 0, -1))) {
            return new BlockData(pos7.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos8 = pos5.add(0, 0, 1);
        if (isPosValid(pos8.add(0, -1, 0))) {
            return new BlockData(pos8.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosValid(pos8.add(-1, 0, 0))) {
            return new BlockData(pos8.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosValid(pos8.add(1, 0, 0))) {
            return new BlockData(pos8.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosValid(pos8.add(0, 0, 1))) {
            return new BlockData(pos8.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosValid(pos8.add(0, 0, -1))) {
            return new BlockData(pos8.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos9 = pos5.add(0, 0, -1);
        if (isPosValid(pos9.add(0, -1, 0))) {
            return new BlockData(pos9.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosValid(pos9.add(-1, 0, 0))) {
            return new BlockData(pos9.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosValid(pos9.add(1, 0, 0))) {
            return new BlockData(pos9.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosValid(pos9.add(0, 0, 1))) {
            return new BlockData(pos9.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosValid(pos9.add(0, 0, -1))) {
            return new BlockData(pos9.add(0, 0, -1), EnumFacing.SOUTH);
        }
        return null;
    }

    private boolean isPosValid(BlockPos pos) {
        Block block = mc.theWorld.getBlockState(pos).getBlock();
        return (block.getMaterial().isSolid() || !block.isTranslucent() || block.isVisuallyOpaque() || block instanceof BlockLadder || block instanceof BlockCarpet
                || block instanceof BlockSnow || block instanceof BlockSkull)
                && !block.getMaterial().isLiquid() && !(block instanceof BlockContainer);
    }

    public static boolean isRotating() {
        return rotation.getYaw() != 999 || rotation.getPitch() != 999;
    }

    static enum ESPMODES {
        Basic
    }

    private static class BlockData {
        private Vec3 vec;
        private final BlockPos pos;
        private final EnumFacing facing;

        public BlockData(BlockPos pos, EnumFacing facing) {
            this.pos = pos;
            this.facing = facing;
        }

        public Vec3 getVec() {
            return vec;
        }

        public void setVec(Vec3 vec) {
            this.vec = vec;
        }

        public BlockPos getBlockPos() {
            return pos;
        }

        public EnumFacing getEnumFacing() {
            return facing;
        }
    }
}

package net.minecraft.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapData;
import optifine.Config;
import optifine.DynamicLights;
import optifine.Reflector;
import org.lwjgl.opengl.GL11;
import shadersmod.client.Shaders;
import space.emptiness.Client;
import space.emptiness.module.ModuleManager;
import space.emptiness.module.modules.combat.KillAura;
import space.emptiness.module.modules.visual.Animations;

public class ItemRenderer
{
    private static final ResourceLocation RES_MAP_BACKGROUND = new ResourceLocation("textures/map/map_background.png");
    private static final ResourceLocation RES_UNDERWATER_OVERLAY = new ResourceLocation("textures/misc/underwater.png");
    private final Minecraft mc;
    private ItemStack itemToRender;
    private float equippedProgress;
    private float prevEquippedProgress;
    private final RenderManager renderManager;
    private final RenderItem itemRenderer;
    private int equippedItemSlot = -1;
    private static int[] $SWITCH_TABLE$net$minecraft$item$EnumAction;
    int ticks = 0;

    public ItemRenderer(Minecraft mcIn) {
        this.mc = mcIn;
        this.renderManager = mcIn.getRenderManager();
        this.itemRenderer = mcIn.getRenderItem();
    }

    public void renderItem(EntityLivingBase entityIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform) {
        if (heldStack != null) {
            Item item = heldStack.getItem();
            Block block = Block.getBlockFromItem(item);
            GlStateManager.pushMatrix();
            if (this.itemRenderer.shouldRenderItemIn3D(heldStack)) {
                GlStateManager.scale(2.0F, 2.0F, 2.0F);
                if (this.isBlockTranslucent(block) && (!Config.isShaders() || !Shaders.renderItemKeepDepthMask)) {
                    GlStateManager.depthMask(false);
                }
            }

            this.itemRenderer.renderItemModelForEntity(heldStack, entityIn, transform);
            if (this.isBlockTranslucent(block)) {
                GlStateManager.depthMask(true);
            }

            GlStateManager.popMatrix();
        }

    }

    private boolean isBlockTranslucent(Block blockIn) {
        return blockIn != null && blockIn.getBlockLayer() == EnumWorldBlockLayer.TRANSLUCENT;
    }

    private void rotateArroundXAndY(float angle, float angleY) {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(angle, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(angleY, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    private void setLightMapFromPlayer(AbstractClientPlayer clientPlayer) {
        int i = this.mc.theWorld.getCombinedLight(new BlockPos(clientPlayer.posX, clientPlayer.posY + (double) clientPlayer.getEyeHeight(), clientPlayer.posZ), 0);
        if (Config.isDynamicLights()) {
            i = DynamicLights.getCombinedLight(this.mc.getRenderViewEntity(), i);
        }

        float f = (float) (i & '\uffff');
        float f1 = (float) (i >> 16);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, f, f1);
    }

    private void rotateWithPlayerRotations(EntityPlayerSP entityplayerspIn, float partialTicks) {
        float f = entityplayerspIn.prevRenderArmPitch + (entityplayerspIn.renderArmPitch - entityplayerspIn.prevRenderArmPitch) * partialTicks;
        float f1 = entityplayerspIn.prevRenderArmYaw + (entityplayerspIn.renderArmYaw - entityplayerspIn.prevRenderArmYaw) * partialTicks;
        GlStateManager.rotate((entityplayerspIn.rotationPitch - f) * 0.1F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate((entityplayerspIn.rotationYaw - f1) * 0.1F, 0.0F, 1.0F, 0.0F);
    }

    private float getMapAngleFromPitch(float pitch) {
        float f = 1.0F - pitch / 45.0F + 0.1F;
        f = MathHelper.clamp_float(f, 0.0F, 1.0F);
        f = -MathHelper.cos(f * 3.1415927F) * 0.5F + 0.5F;
        return f;
    }

    private void renderRightArm(RenderPlayer renderPlayerIn) {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(54.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(64.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(-62.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.translate(0.25F, -0.85F, 0.75F);
        renderPlayerIn.renderRightArm(this.mc.thePlayer);
        GlStateManager.popMatrix();
    }

    private void renderLeftArm(RenderPlayer renderPlayerIn) {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(92.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(41.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.translate(-0.3F, -1.1F, 0.45F);
        renderPlayerIn.renderLeftArm(this.mc.thePlayer);
        GlStateManager.popMatrix();
    }

    private void renderPlayerArms(AbstractClientPlayer clientPlayer) {
        this.mc.getTextureManager().bindTexture(clientPlayer.getLocationSkin());
        Render render = this.renderManager.getEntityRenderObject(this.mc.thePlayer);
        RenderPlayer renderplayer = (RenderPlayer) render;
        if (!clientPlayer.isInvisible()) {
            GlStateManager.disableCull();
            this.renderRightArm(renderplayer);
            this.renderLeftArm(renderplayer);
            GlStateManager.enableCull();
        }

    }

    private void renderItemMap(AbstractClientPlayer clientPlayer, float pitch, float equipmentProgress, float swingProgress) {
        float f = -0.4F * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * 3.1415927F);
        float f1 = 0.2F * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * 3.1415927F * 2.0F);
        float f2 = -0.2F * MathHelper.sin(swingProgress * 3.1415927F);
        GlStateManager.translate(f, f1, f2);
        float f3 = this.getMapAngleFromPitch(pitch);
        GlStateManager.translate(0.0F, 0.04F, -0.72F);
        GlStateManager.translate(0.0F, equipmentProgress * -1.2F, 0.0F);
        GlStateManager.translate(0.0F, f3 * -0.5F, 0.0F);
        GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f3 * -85.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(0.0F, 1.0F, 0.0F, 0.0F);
        this.renderPlayerArms(clientPlayer);
        float f4 = MathHelper.sin(swingProgress * swingProgress * 3.1415927F);
        float f5 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * 3.1415927F);
        GlStateManager.rotate(f4 * -20.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f5 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(f5 * -80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.38F, 0.38F, 0.38F);
        GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(0.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(-1.0F, -1.0F, 0.0F);
        GlStateManager.scale(0.015625F, 0.015625F, 0.015625F);
        this.mc.getTextureManager().bindTexture(RES_MAP_BACKGROUND);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GL11.glNormal3f(0.0F, 0.0F, -1.0F);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(-7.0D, 135.0D, 0.0D).tex(0.0D, 1.0D).endVertex();
        worldrenderer.pos(135.0D, 135.0D, 0.0D).tex(1.0D, 1.0D).endVertex();
        worldrenderer.pos(135.0D, -7.0D, 0.0D).tex(1.0D, 0.0D).endVertex();
        worldrenderer.pos(-7.0D, -7.0D, 0.0D).tex(0.0D, 0.0D).endVertex();
        tessellator.draw();
        MapData mapdata = Items.filled_map.getMapData(this.itemToRender, this.mc.theWorld);
        if (mapdata != null) {
            this.mc.entityRenderer.getMapItemRenderer().renderMap(mapdata, false);
        }

    }

    private void renderPlayerArm(AbstractClientPlayer clientPlayer, float equipProgress, float swingProgress) {
        float f = -0.3F * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * 3.1415927F);
        float f1 = 0.4F * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * 3.1415927F * 2.0F);
        float f2 = -0.4F * MathHelper.sin(swingProgress * 3.1415927F);
        GlStateManager.translate(f, f1, f2);
        GlStateManager.translate(0.64000005F, -0.6F, -0.71999997F);
        GlStateManager.translate(0.0F, equipProgress * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float f3 = MathHelper.sin(swingProgress * swingProgress * 3.1415927F);
        float f4 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * 3.1415927F);
        GlStateManager.rotate(f4 * 70.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f3 * -20.0F, 0.0F, 0.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(clientPlayer.getLocationSkin());
        GlStateManager.translate(-1.0F, 3.6F, 3.5F);
        GlStateManager.rotate(120.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(200.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.scale(1.0F, 1.0F, 1.0F);
        GlStateManager.translate(5.6F, 0.0F, 0.0F);
        Render render = this.renderManager.getEntityRenderObject(this.mc.thePlayer);
        GlStateManager.disableCull();
        RenderPlayer renderplayer = (RenderPlayer) render;
        renderplayer.renderRightArm(this.mc.thePlayer);
        GlStateManager.enableCull();
    }

    private void doItemUsedTransformations(float swingProgress) {
        float var2;
        float var3;
        float var4;
        if ((Boolean) Animations.Smooth.getValue()) {
            var2 = -0.15F * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * 3.1415927F);
            var3 = -0.05F * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * 3.1415927F * 1.5F);
            var4 = -0.0F * MathHelper.sin(swingProgress * 3.1415927F);
            GlStateManager.translate((double) var2 + (Double) Animations.swingx.getValue(), (double) var3 + (Double) Animations.swingy.getValue(), (double) var4 + (Double) Animations.swingz.getValue());
        } else {
            var2 = -0.4F * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * 3.1415927F);
            var3 = 0.2F * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * 3.1415927F * 2.0F);
            var4 = -0.2F * MathHelper.sin(swingProgress * 3.1415927F);
            GlStateManager.translate((double) var2 + (Double) Animations.swingx.getValue(), (double) var3 + (Double) Animations.swingy.getValue(), (double) var4 + (Double) Animations.swingz.getValue());
        }

    }

    private void performDrinking(AbstractClientPlayer clientPlayer, float partialTicks) {
        float f = (float) clientPlayer.getItemInUseCount() - partialTicks + 1.0F;
        float f1 = f / (float) this.itemToRender.getMaxItemUseDuration();
        float f2 = MathHelper.abs(MathHelper.cos(f / 4.0F * 3.1415927F) * 0.1F);
        if (f1 >= 0.8F) {
            f2 = 0.0F;
        }

        GlStateManager.translate(0.0F, f2, 0.0F);
        float f3 = 1.0F - (float) Math.pow((double) f1, 27.0D);
        GlStateManager.translate(f3 * 0.6F, f3 * -0.5F, f3 * 0.0F);
        GlStateManager.rotate(f3 * 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f3 * 10.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(f3 * 30.0F, 0.0F, 0.0F, 1.0F);
    }

    private void transformFirstPersonItem(float equipProgress, float swingProgress) {
        if (this.mc != null && this.mc.thePlayer != null && this.mc.thePlayer.getItemInUse() != null && this.mc.thePlayer.getItemInUse().getItem() != null && Item.getIdFromItem(this.mc.thePlayer.getItemInUse().getItem()) == 261) {
            GlStateManager.translate(0.0F, 0.0F, -0.08F);
        }

        if (this.mc != null && this.mc.thePlayer != null && this.mc.thePlayer.getCurrentEquippedItem() != null && this.mc.thePlayer.getCurrentEquippedItem().getItem() != null && Item.getIdFromItem(this.mc.thePlayer.getCurrentEquippedItem().getItem()) == 346) {
            GlStateManager.translate(0.1F, -0.02F, -0.335F);
        }

        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, equipProgress * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float f = MathHelper.sin(swingProgress * swingProgress * 3.1415927F);
        float f1 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * 3.1415927F);
        GlStateManager.rotate(f * -20.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f1 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(f1 * -80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.4F, 0.4F, 0.4F);
    }

    private void doBowTransformations(float partialTicks, AbstractClientPlayer clientPlayer) {
        GlStateManager.rotate(-18.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(-12.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-8.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(-0.9F, 0.2F, 0.0F);
        float f = (float) this.itemToRender.getMaxItemUseDuration() - ((float) clientPlayer.getItemInUseCount() - partialTicks + 1.0F);
        float f1 = f / 20.0F;
        f1 = (f1 * f1 + f1 * 2.0F) / 3.0F;
        if (f1 > 1.0F) {
            f1 = 1.0F;
        }

        if (f1 > 0.1F) {
            float f2 = MathHelper.sin((f - 0.1F) * 1.3F);
            float f3 = f1 - 0.1F;
            float f4 = f2 * f3;
            GlStateManager.translate(f4 * 0.0F, f4 * 0.01F, f4 * 0.0F);
        }

        GlStateManager.translate(f1 * 0.0F, f1 * 0.0F, f1 * 0.1F);
        GlStateManager.scale(1.0F, 1.0F, 1.0F + f1 * 0.2F);
    }

    private void doBlockTransformations() {
        GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);
    }

    static int[] $SWITCH_TABLE$net$minecraft$item$EnumAction() {
        int[] arrn2 = $SWITCH_TABLE$net$minecraft$item$EnumAction;
        if (arrn2 != null) {
            return arrn2;
        } else {
            int[] arrn = new int[EnumAction.values().length];

            try {
                arrn[EnumAction.BLOCK.ordinal()] = 4;
            } catch (NoSuchFieldError var7) {
            }

            try {
                arrn[EnumAction.BOW.ordinal()] = 5;
            } catch (NoSuchFieldError var6) {
            }

            try {
                arrn[EnumAction.DRINK.ordinal()] = 3;
            } catch (NoSuchFieldError var5) {
            }

            try {
                arrn[EnumAction.EAT.ordinal()] = 2;
            } catch (NoSuchFieldError var4) {
            }

            try {
                arrn[EnumAction.NONE.ordinal()] = 1;
            } catch (NoSuchFieldError var3) {
            }

            $SWITCH_TABLE$net$minecraft$item$EnumAction = arrn;
            return $SWITCH_TABLE$net$minecraft$item$EnumAction;
        }
    }
    private void func_178098_a(float f, AbstractClientPlayer abstractClientPlayer) {
        GlStateManager.rotate(-18.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(-12.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-8.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.translate(-0.9f, 0.2f, 0.0f);
        float f2 = (float)this.itemToRender.getMaxItemUseDuration() - ((float)abstractClientPlayer.getItemInUseCount() - f + 1.0f);
        float f3 = f2 / 20.0f;
        f3 = (f3 * f3 + f3 * 2.0f) / 3.0f;
        if (f3 > 1.0f) {
            f3 = 1.0f;
        }
        if (f3 > 0.1f) {
            float f4 = MathHelper.sin((f2 - 0.1f) * 1.3f);
            float f5 = f3 - 0.1f;
            float f6 = f4 * f5;
            GlStateManager.translate(f6 * 0.0f, f6 * 0.01f, f6 * 0.0f);
        }
        GlStateManager.translate(f3 * 0.0f, f3 * 0.0f, f3 * 0.1f);
        GlStateManager.scale(1.0f, 1.0f, 1.0f + f3 * 0.2f);
    }

    private void func_178103_d() {
        GlStateManager.translate(-0.5f, 0.2f, 0.0f);
        GlStateManager.rotate(30.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-80.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(60.0f, 0.0f, 1.0f, 0.0f);
    }

    private void func_178104_a(AbstractClientPlayer abstractClientPlayer, float f) {
        float f2 = (float)abstractClientPlayer.getItemInUseCount() - f + 1.0f;
        float f3 = f2 / (float)this.itemToRender.getMaxItemUseDuration();
        float f4 = MathHelper.abs(MathHelper.cos(f2 / 4.0f * 3.1415927f) * 0.1f);
        if (f3 >= 0.8f) {
            f4 = 0.0f;
        }
        GlStateManager.translate(0.0f, f4, 0.0f);
        float f5 = 1.0f - (float)Math.pow(f3, 27.0);
        GlStateManager.translate(f5 * 0.6f, f5 * -0.5f, f5 * 0.0f);
        GlStateManager.rotate(f5 * 90.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(f5 * 10.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(f5 * 30.0f, 0.0f, 0.0f, 1.0f);
    }
    public void renderItemInFirstPerson(float partialTicks) {
        float f2 = 1.0F - (this.prevEquippedProgress + (this.equippedProgress - this.prevEquippedProgress) * partialTicks);
        EntityPlayerSP abstractclientplayer = this.mc.  thePlayer;
        float f1 = abstractclientplayer.getSwingProgress(partialTicks);
        float f22 = abstractclientplayer.prevRotationPitch + (abstractclientplayer.rotationPitch - abstractclientplayer.prevRotationPitch) * partialTicks;
        float f3 = abstractclientplayer.prevRotationYaw + (abstractclientplayer.rotationYaw - abstractclientplayer.prevRotationYaw) * partialTicks;
        this.rotateArroundXAndY(f22, f3);
        this.setLightMapFromPlayer(abstractclientplayer);
        this.rotateWithPlayerRotations(abstractclientplayer, partialTicks);
        GlStateManager.enableRescaleNormal();
        GlStateManager.pushMatrix();
        GlStateManager.enableCull();

        Animations hud = (Animations) Client.instance.getModuleManager().getModuleByClass(Animations.class);
        if (this.itemToRender != null) {
            KillAura ka = (KillAura) ModuleManager.getModuleByName("KillAura");
            if (this.itemToRender.getItem() == Items.filled_map) {
                this.renderItemMap(abstractclientplayer, f22, f2, f1);
            } else {
                float var15;
                float var14;
                float var9;
                float var8;
                float f4;
                if ((KillAura.blockingAnim && KillAura.getTarget() != null) || abstractclientplayer.getItemInUseCount() > 0) {
                    EnumAction enumaction = this.itemToRender.getItemUseAction();
                    label184:
                    switch ($SWITCH_TABLE$net$minecraft$item$EnumAction()[enumaction.ordinal()]) {
                        case 1:
                            this.transformFirstPersonItem(f2, f1);
                            GlStateManager.translate((Double) Animations.swingx.getValue(), (Double) Animations.swingy.getValue(), (Double) Animations.swingz.getValue());
                            break;
                        case 2:
                            GlStateManager.translate((Double) Animations.swingx.getValue(), (Double) Animations.swingy.getValue(), (Double) Animations.swingz.getValue());
                        case 3:
                            this.performDrinking(abstractclientplayer, partialTicks);
                            this.transformFirstPersonItem(f2, f1);
                            GlStateManager.translate((Double) Animations.swingx.getValue(), (Double) Animations.swingy.getValue(), (Double) Animations.swingz.getValue());
                            break;
                        case 4:
                            byte var5 = 0;
                            Animations Ani = (Animations) Client.instance.getModuleManager().getModuleByClass(Animations.class);
                            GlStateManager.translate((Double) Animations.x.getValue(), (Double) Animations.y.getValue(), (Double) Animations.z.getValue());
                            if (!Client.instance.getModuleManager().getModuleByClass(Animations.class).isEnabled()) {
                                this.transformFirstPersonItem(f2 / 2.0F, f1);
                                this.doBlockTransformations();
                            } else {
                                float var2 = 1.0F - (this.prevEquippedProgress + (this.equippedProgress - this.prevEquippedProgress) * partialTicks);
                                EntityPlayerSP var3 = this.mc.thePlayer;
                                var15 = var3.getSwingProgress(partialTicks);
                                if (Animations.mode.getValue() == Animations.renderMode.Swang) {
                                    var5 = 0;
                                }

                                if (Animations.mode.getValue() == Animations.renderMode.Swank) {
                                    var5 = 1;
                                }

                                if (Animations.mode.getValue() == Animations.renderMode.Swing) {
                                    var5 = 2;
                                }

                                if (Animations.mode.getValue() == Animations.renderMode.Swong) {
                                    var5 = 3;
                                }

                                if (Animations.mode.getValue() == Animations.renderMode.SwAing) {
                                    var5 = 4;
                                }


                                if (Animations.mode.getValue() == Animations.renderMode.None) {
                                    var5 = 7;
                                }

                                if (Animations.mode.getValue() == Animations.renderMode.Old) {
                                    var5 = 8;
                                }

                                if (Animations.mode.getValue() == Animations.renderMode.Punch) {
                                    var5 = 10;
                                }

                                if (Animations.mode.getValue() == Animations.renderMode.Winter) {
                                    var5 = 11;
                                }


                                if (Animations.mode.getValue() == Animations.renderMode.Exhibition) {
                                    float var92 = MathHelper.sin(MathHelper.sqrt_float(f1) * (float) Math.PI);
                                    this.transformFirstPersonItem(f2 / 2.0f, 0.0f);
                                    GL11.glRotatef((float) (-var92 * 40.0f / 2.0f), (float) (var92 / 2.0f), (float) -0.0f,
                                            (float) 9.0f);
                                    GL11.glRotatef((float) (-var92 * 30.0f), (float) 1.0f, (float) (var92 / 2.0f),
                                            (float) -0.0f);
                                    this.func_178103_d();
                                    break;
                                }

                                //if (Animations.mode.getValue() == Animations.renderMode.Russia) {
                                //    var5 = 15;
                                //}

                                switch (var5) {
                                    case 0:
                                        GL11.glTranslated(-0.10000000149011612D, 0.15000000596046448D, 0.0D);
                                        this.func_178096_b(var2 / 2.0F, var15);
                                        var14 = MathHelper.sin(MathHelper.sqrt_float(var15) * 3.1415927F);
                                        GlStateManager.rotate(var14 * 30.0F / 2.0F, -var14, -0.0F, 9.0F);
                                        GlStateManager.rotate(var14 * 40.0F, 1.0F, -var14 / 2.0F, -0.0F);
                                        this.doBlockTransformations();
                                        break label184;
                                    case 1:
                                        GL11.glTranslated(-0.10000000149011612D, 0.15000000596046448D, 0.0D);
                                        this.transformFirstPersonItem(f2 / 2.0F, f1);
                                        var14 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                                        GlStateManager.rotate(var14 * 30.0F, -var14, -0.0F, 9.0F);
                                        GlStateManager.rotate(var14 * 40.0F, 1.0F, -var14, -0.0F);
                                        this.doBlockTransformations();
                                        break label184;
                                    case 2:
                                        GL11.glTranslated(-0.10000000149011612D, 0.15000000596046448D, 0.0D);
                                        GL11.glTranslated(0.10000000149011612D, -0.15000000596046448D, 0.0D);
                                        this.transformFirstPersonItem(var2 / 2.0F, var15);
                                        this.doBlockTransformations();
                                        break label184;
                                    case 3:
                                        GL11.glTranslated(-0.10000000149011612D, 0.15000000596046448D, 0.0D);
                                        GlStateManager.translate(-0.0F, -0.1F, 0.0F);
                                        this.transformFirstPersonItem(f2 / 2.0F, 0.0F);
                                        var14 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                                        GL11.glRotated((double) (-var14 * 40.0F / 2.0F), (double) (var14 / 2.0F), -0.0D, 9.0D);
                                        GL11.glRotated((double) (-var14 * 30.0F), 1.0D, (double) (var14 / 2.0F), -0.0D);
                                        this.doBlockTransformations();
                                        break label184;
                                    case 4:
                                        GL11.glTranslated(-0.10000000149011612D, 0.15000000596046448D, 0.0D);
                                        GL11.glTranslated(0.10000000149011612D, -0.20000000298023224D, 0.0D);
                                        this.avatar(f2, f1);
                                        this.doBlockTransformations();
                                        break label184;
                                    case 5:
                                        this.transformFirstPersonItem(f2, 0.83F);
                                        this.doBlockTransformations();
                                        f4 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.83F);
                                        GlStateManager.translate(-0.5F, 0.2F, 0.2F);
                                        GlStateManager.rotate(-f4 * 0.0F, 0.0F, 0.0F, 0.0F);
                                        GlStateManager.rotate(-f4 * 43.0F, 58.0F, 23.0F, 45.0F);
                                        break label184;
                                    case 6:
                                        this.func_178096_b(f2, 0.0F);
                                        this.doBlockTransformations();
                                        f4 = MathHelper.sin(f1 * f1 * 3.1415927F);
                                        var8 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                                        GlStateManager.rotate(-var8 * 70.0F / 2.0F, -2.0F, -0.0F, 2.0F);
                                        GlStateManager.rotate(-var8 * 70.0F, 1.0F, -0.4F, -0.0F);
                                        break label184;
                                    case 7:
                                        this.transformFirstPersonItem(f2, 0.0F);
                                        this.doBlockTransformations();
                                        break label184;
                                    case 8:
                                        this.genCustom(f2, f1);
                                        this.doBlockTransformations();
                                        break label184;
                                    case 9:
                                        this.transformFirstPersonItem(f2, 0.0F);
                                        this.doBlockTransformations();
                                        f4 = MathHelper.sin(f1 * f1 * 3.1415927F);
                                        var8 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                                        GlStateManager.translate(-0.05F, 0.3F, 0.0F);
                                        GlStateManager.rotate(-var8 * 70.0F / 2.0F, -8.0F, -0.0F, 9.0F);
                                        GlStateManager.rotate(-var8 * 70.0F, 1.0F, -0.4F, -0.0F);
                                        break label184;
                                    case 10:
                                        this.transformFirstPersonItem(f2, 0.0F);
                                        this.doBlockTransformations();
                                        var9 = MathHelper.sin(f1 * f1 * 3.1415927F);
                                        var14 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                                        GlStateManager.translate(0.1F, 0.2F, 0.3F);
                                        GlStateManager.rotate(-var14 * 30.0F, -5.0F, 0.0F, 9.0F);
                                        GlStateManager.rotate(-var14 * 10.0F, 1.0F, -0.4F, -0.5F);
                                        break label184;
                                    case 11:
                                        this.x3IsBlack(-0.3F, f1);
                                        this.doBlockTransformations();
                                        break label184;
                                    case 12:
                                        this.Random(f1);
                                        this.doBlockTransformations();
                                        break label184;
                                    case 13:
                                        ticks += 1;
                                        this.transformFirstPersonItem(f2, 0.0F);
                                        //GlStateManager.translate(0.7, -0.4F, -0.8F);

                                        GlStateManager.rotate((float) this.ticks * 0.2F * ((Double) Animations.Speed.getValue()).floatValue(), 0.0F, 0.0F, 1.0F);
                                        GlStateManager.rotate(80.0F, 0.0F, 0.0F, 0.0F);
                                        float f5 = MathHelper.sin(MathHelper.sqrt_float(ticks) * (float) 0.04);
                                        GlStateManager.rotate(ticks*0.07f*50, -1.0F, f5 * 0.0F, -f5 * 9000.0F);

                                        GlStateManager.rotate(-70F, 0.0F, 1.0F, 0.0F);
                                        GlStateManager.rotate(-80.0F, 1.0F, 0.0F, 0.0F);
                                        GlStateManager.rotate(60F, 0.0F, 1.0F, 0.0F);
                                        GlStateManager.scale(2.40, 2.40, 2.40);

                                        this.doBlockTransformations();
                                }

                            }
                            break;
                        case 5:
                            this.transformFirstPersonItem(f2, f1);
                            this.doBowTransformations(partialTicks, abstractclientplayer);
                            GlStateManager.translate((Double) Animations.swingx.getValue(), (Double) Animations.swingy.getValue(), (Double) Animations.swingz.getValue());
                    }
                } else {
                    byte var5 = 0;
                    Animations Ani = (Animations) Client.instance.getModuleManager().getModuleByClass(Animations.class);
                    if ((Boolean) Animations.EveryThingBlock.getValue() && this.mc.gameSettings.keyBindUseItem.isKeyDown()) {
                        GL11.glTranslated((Double) Animations.x.getValue(), (Double) Animations.y.getValue(), (Double) Animations.z.getValue());
                        if (!Client.instance.getModuleManager().getModuleByClass(Animations.class).isEnabled()) {
                            this.transformFirstPersonItem(f2 / 2.0F, f1);
                            this.doBlockTransformations();
                        } else {
                            GlStateManager.translate((Double) Animations.x.getValue(), (Double) Animations.y.getValue(), (Double) Animations.z.getValue());
                            if (!Client.instance.getModuleManager().getModuleByClass(Animations.class).isEnabled()) {
                                this.transformFirstPersonItem(f2 / 2.0F, f1);
                                this.doBlockTransformations();
                            } else {
                                float var2 = 1.0F - (this.prevEquippedProgress + (this.equippedProgress - this.prevEquippedProgress) * partialTicks);
                                EntityPlayerSP var3 = this.mc.thePlayer;
                                float var4 = var3.getSwingProgress(partialTicks);
                                if (Animations.mode.getValue() == Animations.renderMode.Swang) {
                                    var5 = 0;
                                }

                                if (Animations.mode.getValue() == Animations.renderMode.Swank) {
                                    var5 = 1;
                                }

                                if (Animations.mode.getValue() == Animations.renderMode.Swing) {
                                    var5 = 2;
                                }

                                if (Animations.mode.getValue() == Animations.renderMode.Swong) {
                                    this.transformFirstPersonItem(var5, 0.0f);
                                    GlStateManager.rotate(-MathHelper.sin(MathHelper.sqrt_float(f1) * (float) Math.PI) * 40, 0, 0, 9);
                                    GlStateManager.rotate(-MathHelper.sin(MathHelper.sqrt_float(f1) * (float) Math.PI) * 50, 1, MathHelper.sin(MathHelper.sqrt_float(f1) * (float) Math.PI) / 2, 0);
                                    this.doBlockTransformations();
                                }

                                if (Animations.mode.getValue() == Animations.renderMode.SwAing) {
                                    var5 = 4;
                                }


                                if (Animations.mode.getValue() == Animations.renderMode.None) {
                                    var5 = 7;
                                }

                                if (Animations.mode.getValue() == Animations.renderMode.Old) {
                                    var5 = 8;
                                }


                                if (Animations.mode.getValue() == Animations.renderMode.Punch) {
                                    var5 = 10;
                                }

                                if (Animations.mode.getValue() == Animations.renderMode.Winter) {
                                    var5 = 11;
                                }


                                if (Animations.mode.getValue() == Animations.renderMode.Exhibition) {
                                    float var92 = MathHelper.sin(MathHelper.sqrt_float(f1) * (float) Math.PI);
                                    this.transformFirstPersonItem(f2 / 2.0f, 0.0f);
                                    GL11.glRotatef((float) (-var92 * 40.0f / 2.0f), (float) (var92 / 2.0f), (float) -0.0f,
                                            (float) 9.0f);
                                    GL11.glRotatef((float) (-var92 * 30.0f), (float) 1.0f, (float) (var92 / 2.0f),
                                            (float) -0.0f);
                                    this.func_178103_d();
                                }


                                switch (var5) {
                                    case 0:
                                        GL11.glTranslated(-0.10000000149011612D, 0.15000000596046448D, 0.0D);
                                        this.func_178096_b(var2 / 2.0F, var4);
                                        var15 = MathHelper.sin(MathHelper.sqrt_float(var4) * 3.1415927F);
                                        GlStateManager.rotate(var15 * 30.0F / 2.0F, -var15, -0.0F, 9.0F);
                                        GlStateManager.rotate(var15 * 40.0F, 1.0F, -var15 / 2.0F, -0.0F);
                                        this.doBlockTransformations();
                                        break;
                                    case 1:
                                        GL11.glTranslated(-0.10000000149011612D, 0.15000000596046448D, 0.0D);
                                        this.transformFirstPersonItem(f2 / 2.0F, f1);
                                        var15 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                                        GlStateManager.rotate(var15 * 30.0F, -var15, -0.0F, 9.0F);
                                        GlStateManager.rotate(var15 * 40.0F, 1.0F, -var15, -0.0F);
                                        this.doBlockTransformations();
                                        break;
                                    case 2:
                                        GL11.glTranslated(-0.10000000149011612D, 0.15000000596046448D, 0.0D);
                                        GL11.glTranslated(0.10000000149011612D, -0.15000000596046448D, 0.0D);
                                        this.transformFirstPersonItem(var2 / 2.0F, var4);
                                        this.doBlockTransformations();
                                        break;
                                    case 3:
                                        GL11.glTranslated(-0.10000000149011612D, 0.15000000596046448D, 0.0D);
                                        GlStateManager.translate(-0.0F, -0.1F, 0.0F);
                                        this.transformFirstPersonItem(f2 / 2.0F, 0.0F);
                                        var15 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                                        GL11.glRotated((double) (-var15 * 40.0F / 2.0F), (double) (var15 / 2.0F), -0.0D, 9.0D);
                                        GL11.glRotated((double) (-var15 * 30.0F), 1.0D, (double) (var15 / 2.0F), -0.0D);
                                        this.doBlockTransformations();
                                        break;
                                    case 4:
                                        GL11.glTranslated(-0.10000000149011612D, 0.15000000596046448D, 0.0D);
                                        GL11.glTranslated(0.10000000149011612D, -0.20000000298023224D, 0.0D);
                                        this.avatar(f2, f1);
                                        this.doBlockTransformations();
                                        break;
                                    case 5:
                                        this.transformFirstPersonItem(f2, 0.83F);
                                        this.doBlockTransformations();
                                        f4 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.83F);
                                        GlStateManager.translate(-0.5F, 0.2F, 0.2F);
                                        GlStateManager.rotate(-f4 * 0.0F, 0.0F, 0.0F, 0.0F);
                                        GlStateManager.rotate(-f4 * 43.0F, 58.0F, 23.0F, 45.0F);
                                        break;
                                    case 6:
                                        this.func_178096_b(f2, 0.0F);
                                        this.doBlockTransformations();
                                        var8 = MathHelper.sin(f1 * f1 * 3.1415927F);
                                        var9 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                                        GlStateManager.rotate(-var9 * 70.0F / 2.0F, -2.0F, -0.0F, 2.0F);
                                        GlStateManager.rotate(-var9 * 70.0F, 1.0F, -0.4F, -0.0F);
                                        break;
                                    case 7:
                                        this.transformFirstPersonItem(f2, 0.0F);
                                        this.doBlockTransformations();
                                        break;
                                    case 8:
                                        this.genCustom(f2, f1);
                                        this.doBlockTransformations();
                                        break;
                                    case 9:
                                        this.transformFirstPersonItem(f2, 0.0F);
                                        this.doBlockTransformations();
                                        var8 = MathHelper.sin(f1 * f1 * 3.1415927F);
                                        var9 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                                        GlStateManager.translate(-0.05F, 0.3F, 0.0F);
                                        GlStateManager.rotate(-var9 * 70.0F / 2.0F, -8.0F, -0.0F, 9.0F);
                                        GlStateManager.rotate(-var9 * 70.0F, 1.0F, -0.4F, -0.0F);
                                        break;
                                    case 10:
                                        this.transformFirstPersonItem(f2, 0.0F);
                                        this.doBlockTransformations();
                                        var15 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                                        GlStateManager.translate(0.1F, 0.2F, 0.3F);
                                        GlStateManager.rotate(-var15 * 30.0F, -5.0F, 0.0F, 9.0F);
                                        GlStateManager.rotate(-var15 * 10.0F, 1.0F, -0.4F, -0.5F);
                                        break;
                                    case 11:
                                        this.x3IsBlack(-0.3F, f1);
                                        this.doBlockTransformations();
                                        break;
                                    case 12:
                                        this.Random(f1);
                                        this.doBlockTransformations();
                                        break;
                                    case 13:
                                        ticks += 1;
                                        this.transformFirstPersonItem(f2, 0.0F);
                                        //GlStateManager.translate(0.7, -0.4F, -0.8F);

                                        GlStateManager.rotate((float) this.ticks * 0.2F * ((Double) Animations.Speed.getValue()).floatValue(), 0.0F, 0.0F, 1.0F);
                                        GlStateManager.rotate(80.0F, 0.0F, 0.0F, 0.0F);
                                        float f5 = MathHelper.sin(MathHelper.sqrt_float(ticks) * (float) 0.04);
                                        GlStateManager.rotate(ticks*0.07f*50, -1.0F, f5 * 0.0F, -f5 * 9000.0F);

                                        GlStateManager.rotate(-70F, 0.0F, 1.0F, 0.0F);
                                        GlStateManager.rotate(-80.0F, 1.0F, 0.0F, 0.0F);
                                        GlStateManager.rotate(60F, 0.0F, 1.0F, 0.0F);
                                        GlStateManager.scale(2.40, 2.40, 2.40);

                                        this.doBlockTransformations();
                                }
                            }
                        }
                    } else {
                        this.doItemUsedTransformations(f1);
                        this.transformFirstPersonItem(f2, f1);
                    }
                }
            }

            this.renderItem(abstractclientplayer, this.itemToRender, ItemCameraTransforms.TransformType.FIRST_PERSON);
        } else if (!abstractclientplayer.isInvisible()) {
            this.renderPlayerArm(abstractclientplayer, f2, f1);
        }

        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
    }

    private void x3IsBlack(float p_178096_1_, float p_178096_2_) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, p_178096_1_ * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(MathHelper.sin(MathHelper.sqrt_float(p_178096_2_) * 3.1415927F) * -35.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.4F, 0.4F, 0.4F);
    }

    private void genCustom(float p_178096_1_, float p_178096_2_) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, p_178096_1_ * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float var3 = MathHelper.sin(p_178096_2_ * p_178096_2_ * 3.1415927F);
        float var4 = MathHelper.sin(MathHelper.sqrt_float(p_178096_2_) * 3.1415927F);
        GlStateManager.rotate(var3 * -34.0F, 0.0F, 1.0F, 0.2F);
        GlStateManager.rotate(var4 * -20.7F, 0.2F, 0.1F, 1.0F);
        GlStateManager.rotate(var4 * -68.6F, 1.3F, 0.1F, 0.2F);
        GlStateManager.scale(0.4F, 0.4F, 0.4F);
    }

    private void circle(float swingProgress) {
        float f1 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * 3.1415927F);
        this.ticks+=2;
        GlStateManager.translate(0.7F, -0.4F, -0.8F);
        GlStateManager.rotate((float) this.ticks * 0.2F * ((Double) Animations.Speed.getValue()).floatValue(), 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(40.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(34.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.4D, 0.4D, 0.4D);
    }

    private void Random(float swingProgress) {
        ++this.ticks;
        GlStateManager.translate(0.7D, -0.4000000059604645D, -0.800000011920929D);
        GlStateManager.rotate(50.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(50.0F, 0.0F, 0.0F, -1.0F);
        GlStateManager.rotate((float) this.ticks * 0.2F * ((Double) Animations.Speed.getValue()).floatValue(), 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(-25.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.4D, 0.4D, 0.4D);
    }

    private void Rassssi(float swingProgress) {
        ++this.ticks;
        GlStateManager.translate(-0.5f, 0.2f, 0.0f);
        GlStateManager.rotate(30.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-80.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(60.0f, 0.0f, 1.0f, 0.0f);
        /*
        GlStateManager.translate(0.7D, -0.4000000059604645D, -0.800000011920929D);
        GlStateManager.rotate(50.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(50.0F, 0.0F, 0.0F, -1.0F);
        GlStateManager.rotate((float) this.ticks * 0.2F * ((Double) Animations.Speed.getValue()).floatValue(), 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(-25.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.4D, 0.4D, 0.4D)
         */
    }

    private void avatar(float equipProgress, float swingProgress) {
        GlStateManager.translate(0.6F, -0.48F, -0.79999995F);
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float f = MathHelper.sin(swingProgress * swingProgress * 3.1415927F);
        float f1 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * 3.1415927F);
        GlStateManager.rotate(f1 * -40.0F, 1.0F, -0.2F, 0.2F);
        GlStateManager.scale(0.4F, 0.4F, 0.4F);
    }

    private void func_178096_b(float p_178096_1_, float p_178096_2_) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, p_178096_1_ * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float var3 = MathHelper.sin(p_178096_2_ * p_178096_2_ * 3.1415927F);
        float var4 = MathHelper.sin(MathHelper.sqrt_float(p_178096_2_) * 3.1415927F);
        GlStateManager.rotate(var3 * -20.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(var4 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(var4 * -80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.4F, 0.4F, 0.4F);
    }

    private void tap(float var2, float swingProgress) {
        /*
        GL11.glTranslated(-0.10000000149011612D, 0.15000000596046448D, 0.0D);
        this.transformFirstPersonItem(f2 / 2.0F, f1);
        var15 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
        GlStateManager.rotate(var15 * 30.0F, -var15, -0.0F, 9.0F);
        GlStateManager.rotate(var15 * 40.0F, 1.0F, -var15, -0.0F);
        this.doBlockTransformations();

         */
    }

    private void circle2(float swingProgress) {
        float f1 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * 3.1415927F);
        ticks += 1;
        GlStateManager.translate(-0.0F, -0.2F, -0.6F);
        GlStateManager.rotate(-ticks*0.07f*50, 0, 0, -1.0f);
        GlStateManager.rotate(44, 0, 1, 0.6f);
        GlStateManager.rotate(44, 1, 0, -0.6f);
        GlStateManager.translate(1.0F, -0.2F, 0.5F);
        GlStateManager.rotate(-44, 1, 0, -0.6f);
        GlStateManager.scale(0.5, 0.5, 0.5);
    }

    private void Random2(float swingProgress) {
        ticks += 1;
        GlStateManager.translate(0.7, -0.4F, -0.8F);

//        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.rotate(80.0F, 0.0F, 0.0F, 0.0F);

//        GlStateManager.rotate(50, 1, 0, 0);
//        GlStateManager.rotate(50, 0, 0, -1);

        float f1 = MathHelper.sin(MathHelper.sqrt_float(ticks) * (float) 0.04);
        GlStateManager.rotate(ticks*0.07f*50, -1.0F, f1 * 0.0F, -f1 * 9000.0F);

//        GlStateManager.rotate((ticks) * 0.2f * 50, 0, 0, 1);
//        GlStateManager.rotate(-25f, 1, 0, 0);

//        GlStateManager.scale(0.4F, 0.4F, 0.4F);
        GlStateManager.translate(0.5F, -0.2F, 0.0F);
        GlStateManager.translate(-0.5, 0.1F, 0.1F);
        GlStateManager.rotate(-70.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);

        GlStateManager.scale(2.40, 2.40, 2.40);
    }

    public void renderOverlays(float partialTicks) {
        GlStateManager.disableAlpha();
        if (this.mc.thePlayer.isEntityInsideOpaqueBlock()) {
            IBlockState iblockstate = this.mc.theWorld.getBlockState(new BlockPos(this.mc.thePlayer));
            BlockPos blockpos = new BlockPos(this.mc.thePlayer);
            EntityPlayer entityplayer = this.mc.thePlayer;

            for (int i = 0; i < 8; ++i) {
                double d0 = entityplayer.posX + (double) (((float) ((i >> 0) % 2) - 0.5F) * entityplayer.width * 0.8F);
                double d1 = entityplayer.posY + (double) (((float) ((i >> 1) % 2) - 0.5F) * 0.1F);
                double d2 = entityplayer.posZ + (double) (((float) ((i >> 2) % 2) - 0.5F) * entityplayer.width * 0.8F);
                BlockPos blockpos1 = new BlockPos(d0, d1 + (double) entityplayer.getEyeHeight(), d2);
                IBlockState iblockstate1 = this.mc.theWorld.getBlockState(blockpos1);
                if (iblockstate1.getBlock().isVisuallyOpaque()) {
                    iblockstate = iblockstate1;
                    blockpos = blockpos1;
                }
            }

            if (iblockstate.getBlock().getRenderType() != -1) {
                Object object = Reflector.getFieldValue(Reflector.RenderBlockOverlayEvent_OverlayType_BLOCK);
                if (!Reflector.callBoolean(Reflector.ForgeEventFactory_renderBlockOverlay, new Object[]{this.mc.thePlayer, partialTicks, object, iblockstate, blockpos})) {
                    this.renderBlockInHand(partialTicks, this.mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(iblockstate));
                }
            }
        }

        if (!this.mc.thePlayer.isSpectator()) {
            if (this.mc.thePlayer.isInsideOfMaterial(Material.water) && !Reflector.callBoolean(Reflector.ForgeEventFactory_renderWaterOverlay, new Object[]{this.mc.thePlayer, partialTicks})) {
                this.renderWaterOverlayTexture(partialTicks);
            }

            if (this.mc.thePlayer.isBurning() && !Reflector.callBoolean(Reflector.ForgeEventFactory_renderFireOverlay, new Object[]{this.mc.thePlayer, partialTicks})) {
                this.renderFireInFirstPerson(partialTicks);
            }
        }

        GlStateManager.enableAlpha();
    }

    private void renderBlockInHand(float partialTicks, TextureAtlasSprite atlas) {
        this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        float f = 0.1F;
        GlStateManager.color(0.1F, 0.1F, 0.1F, 0.5F);
        GlStateManager.pushMatrix();
        float f1 = -1.0F;
        float f2 = 1.0F;
        float f3 = -1.0F;
        float f4 = 1.0F;
        float f5 = -0.5F;
        float f6 = atlas.getMinU();
        float f7 = atlas.getMaxU();
        float f8 = atlas.getMinV();
        float f9 = atlas.getMaxV();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(-1.0D, -1.0D, -0.5D).tex((double) f7, (double) f9).endVertex();
        worldrenderer.pos(1.0D, -1.0D, -0.5D).tex((double) f6, (double) f9).endVertex();
        worldrenderer.pos(1.0D, 1.0D, -0.5D).tex((double) f6, (double) f8).endVertex();
        worldrenderer.pos(-1.0D, 1.0D, -0.5D).tex((double) f7, (double) f8).endVertex();
        tessellator.draw();
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void renderWaterOverlayTexture(float partialTicks) {
        if (!Config.isShaders() || Shaders.isUnderwaterOverlay()) {
            this.mc.getTextureManager().bindTexture(RES_UNDERWATER_OVERLAY);
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            float f = this.mc.thePlayer.getBrightness(partialTicks);
            GlStateManager.color(f, f, f, 0.5F);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.pushMatrix();
            float f1 = 4.0F;
            float f2 = -1.0F;
            float f3 = 1.0F;
            float f4 = -1.0F;
            float f5 = 1.0F;
            float f6 = -0.5F;
            float f7 = -this.mc.thePlayer.rotationYaw / 64.0F;
            float f8 = this.mc.thePlayer.rotationPitch / 64.0F;
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
            worldrenderer.pos(-1.0D, -1.0D, -0.5D).tex((double) (4.0F + f7), (double) (4.0F + f8)).endVertex();
            worldrenderer.pos(1.0D, -1.0D, -0.5D).tex((double) (0.0F + f7), (double) (4.0F + f8)).endVertex();
            worldrenderer.pos(1.0D, 1.0D, -0.5D).tex((double) (0.0F + f7), (double) (0.0F + f8)).endVertex();
            worldrenderer.pos(-1.0D, 1.0D, -0.5D).tex((double) (4.0F + f7), (double) (0.0F + f8)).endVertex();
            tessellator.draw();
            GlStateManager.popMatrix();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableBlend();
        }

    }

    private void renderFireInFirstPerson(float partialTicks) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.9F);
        GlStateManager.depthFunc(519);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        float f = 1.0F;

        for (int i = 0; i < 2; ++i) {
            GlStateManager.pushMatrix();
            TextureAtlasSprite textureatlassprite = this.mc.getTextureMapBlocks().getAtlasSprite("minecraft:blocks/fire_layer_1");
            this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            float f1 = textureatlassprite.getMinU();
            float f2 = textureatlassprite.getMaxU();
            float f3 = textureatlassprite.getMinV();
            float f4 = textureatlassprite.getMaxV();
            float f5 = (0.0F - f) / 2.0F;
            float f6 = f5 + f;
            float f7 = 0.0F - f / 2.0F;

            float f8 = f7 + f;
            float f9 = -0.5F;
            GlStateManager.translate((float) (-(i * 2 - 1)) * 0.24F, -0.3F, 0.0F);
            GlStateManager.rotate((float) (i * 2 - 1) * 10.0F, 0.0F, 1.0F, 0.0F);
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
            worldrenderer.setSprite(textureatlassprite);
            worldrenderer.pos((double) f5, (double) f7, (double) f9).tex((double) f2, (double) f4).endVertex();
            worldrenderer.pos((double) f6, (double) f7, (double) f9).tex((double) f1, (double) f4).endVertex();
            worldrenderer.pos((double) f6, (double) f8, (double) f9).tex((double) f1, (double) f3).endVertex();
            worldrenderer.pos((double) f5, (double) f8, (double) f9).tex((double) f2, (double) f3).endVertex();
            tessellator.draw();
            GlStateManager.popMatrix();
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.depthFunc(515);

    }

    public void updateEquippedItem() {
        this.prevEquippedProgress = this.equippedProgress;
        EntityPlayerSP entityplayer = this.mc.thePlayer;
        ItemStack itemstack = entityplayer.inventory.getCurrentItem();
        boolean flag = false;
        if (this.itemToRender != null && itemstack != null) {
            if (!this.itemToRender.getIsItemStackEqual(itemstack)) {
                flag = true;
            }
        } else {
            flag = this.itemToRender != null || itemstack != null;
        }

        float f2 = 0.4F;
        float f22 = MathHelper.clamp_float((flag ? 0.0F : 1.0F) - this.equippedProgress, -f2, f2);
        this.equippedProgress += f22;
        if (this.equippedProgress < 0.1F) {
            this.itemToRender = itemstack;
            this.equippedItemSlot = entityplayer.inventory.currentItem;
        }

    }

    public void resetEquippedProgress() {
        this.equippedProgress = 0.0F;
    }

    public void resetEquippedProgress2() {
        this.equippedProgress = 0.0F;
    }
}

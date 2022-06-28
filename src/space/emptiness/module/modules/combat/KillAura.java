package space.emptiness.module.modules.combat;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemTool;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;
import space.emptiness.events.EventManager;
import space.emptiness.events.EventTarget;
import space.emptiness.events.attack.EventFight;
import space.emptiness.events.misc.EventPacket;
import space.emptiness.events.rendering.EventRender2D;
import space.emptiness.events.rendering.EventRender3D;
import space.emptiness.events.world.EventPacketReceive;
import space.emptiness.events.world.EventUpdate;
import space.emptiness.events.world.RespawnEvent;
import space.emptiness.management.EntityHelper;
import space.emptiness.module.Category;
import space.emptiness.module.Module;
import space.emptiness.module.ModuleManager;
import space.emptiness.module.modules.move.Speed;
import space.emptiness.module.modules.player.Teams;
import space.emptiness.module.modules.visual.HUD;
import space.emptiness.module.modules.world.Scaffold;
import space.emptiness.module.value.Mode;
import space.emptiness.module.value.Numbers;
import space.emptiness.module.value.Option;

import space.emptiness.utils.TimerUtil;
import space.emptiness.utils.math.MathUtil;
import space.emptiness.utils.math.MathUtils;
import space.emptiness.utils.math.RotationUtil;
import space.emptiness.utils.particles.Praticles;
import space.emptiness.utils.render.AnimationUtils;
import space.emptiness.utils.render.ColorUtils;
import space.emptiness.utils.render.RenderUtil;
import space.emptiness.utils.render.RenderUtils;
import space.emptiness.utils.render.gl.GLUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;


import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.ItemSword;

import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;

import org.lwjgl.util.glu.Cylinder;


import space.emptiness.Client;
import space.emptiness.gui.font.CFont;
import space.emptiness.gui.font.CFontRenderer;
import space.emptiness.gui.font.FontLoaders;
import space.emptiness.management.FriendManager;


public class KillAura
        extends Module {
    double anima = 0;
    int animAlpha = 0;
    float anim2 = 0f;
    public static float hue;
    float astolfoHelathAnim = 0f;
    boolean startAnim, stopAnim;
    public EntityLivingBase lastEnt;
    float easingHealth;

    private float lastHealth;
    private float damageDelt;
    private float damageDeltToPlayer;
    private float lastPlayerHealth;
    private final DecimalFormat format = new DecimalFormat("0.0");

    private static final Color COLOR = new Color(0, 0, 0, 180);
    public static EntityLivingBase target;
    private double healthBarWidth;
    private double hudHeight;

    private double animation;
    private double armoranimation;
    private float zLevel;
    public static EntityLivingBase lastTarget;
    private List targets = new ArrayList(0);
    private int index;
    boolean heiheihei = false;
    private final TimerUtil timer = new TimerUtil();
    public static Boolean executingblock;
    private final TimerUtil lossTimer = new TimerUtil();
    int cout = 0;
    int Sky;

    private boolean sentParticles;
    public static EntityLivingBase lastTarget_ = null;
    public static Boolean blockingAnim = false;
    private double x;
    private final double scale = 1;
    double anima1 = 0.0;
    double anima2 = 0.0;
    double alphaAnima = 0;
    private float displayHealth;

    private float animated;
    private float health;
    private int ticks;
    private final TimerUtil timerAttack = new TimerUtil();
    private static final Numbers<Double> Cps = new Numbers<Double>("CPS", "CPS", 10.0, 1.0, 20.0, 0.5);
    public static Numbers<Double> Range = new Numbers<Double>("Range", "Range", 4.5, 1.0, 6.0, 0.1);
    public static Numbers<Double> Blockreach = new Numbers<Double>("Blockreach", "Blockreach", 4.5, 1.0, 6.0, 0.1);
    //    public static Numbers<Double> turnspeed = new Numbers<Double>("TurnSpeeed","TurnSpeeed", 120.0, 0.0, 180.0, 5.0);
    public static Numbers<Double> SwitchDelay = new Numbers<Double>("SwitchDelay", "SwitchDelay", 500.0, 1.0, 5000.0, 1.0);
    private final Option<Boolean> Autoblock = new Option<Boolean>("Autoblock", "Autoblock", true);
    public static Option<Boolean> Players = new Option<Boolean>("Players", "Players", true);
    public static Option<Boolean> Animals = new Option<Boolean>("Animals", "Animals", true);
    public static Option<Boolean> Mobs = new Option<Boolean>("Mobs", "Mobs", false);
    private static final Option<Boolean> Invis = new Option<Boolean>("Invisibles", "Invisibles", false);
    private final Option<Boolean> Esp = new Option<Boolean>("ShowTarget", "ShowTarget", true);
    private final Mode<Enum> Mode = new Mode<Enum>("Mode", "Mode", AuraMode.values(), AuraMode.Single);
    private final space.emptiness.module.value.Mode<Enum> thudMode = new Mode<Enum>("THUDStyle", "TargetHudStyle", THUDMode.values(), THUDMode.Client);
    private static final Option<Boolean> thudOption = new Option<Boolean>("Information", "Information", true);
    private final Mode<Enum> rotmode = new Mode<Enum>("RotationMode", "RotationMode", AuraRotMode.values(), AuraRotMode.Hypixel);
    private boolean isBlocking;
    private final Comparator<Entity> angleComparator = Comparator.comparingDouble(e2 -> e2.getDistanceToEntity(Minecraft.thePlayer));

    private static final TimerUtil AttackTimer = new TimerUtil();

    private final TimerUtil SwitchTimer = new TimerUtil();
    private Object texture;
    float anim = 100;
    private static CFont cFont;

//    static enum SwitchMode {
//        Delay,
//        HurtTime
//    }

    public KillAura() {
        super("KillAura", new String[]{"ka", "killaura"}, Category.Combat);
        this.addValues(this.Mode, rotmode, thudMode, thudOption, this.Cps, Range, Blockreach, SwitchDelay, this.Esp, this.Autoblock, Players, Animals, Mobs, Invis);
    }

    @Override
    public void onDisable() {
        target = null;
        this.targets.clear();
        if (isBlocking) stopBlock();
    }

    @Override
    public void onEnable() {
        lossTimer.reset();
        target = null;
        this.index = 0;
    }

    public boolean serverLag() {
        return lossTimer.getLastDelay() >= 100;
    }

    @EventTarget
    public void onRender3D(EventRender3D event) {
        if (this.Esp.getValue()) {
            switch ((AuraMode) Mode.getValue()) {
                case Single:
                case Switch: {
                    if (target != null) {
                        drawShadow(target, 0.67, new Color(HUD.r.getValue().intValue(), HUD.g.getValue().intValue(), HUD.b.getValue().intValue()).getRGB(), true);
                    }
                    break;
                }
            }
        }
//        drawCircle(entity, entity.hurtTime >= 1 ? (new Color(255,0,0,160).getRGB()) : (new Color(HUD.r.getValue().intValue(),HUD.g.getValue().intValue(),HUD.b.getValue().intValue(),160).getRGB()),event);
    }

    public static void drawPlayerHead(String playerName, int x, int y, int width, int height) {
        for (Object player : Minecraft.theWorld.getLoadedEntityList()) {
            if (player instanceof EntityPlayer) {
                EntityPlayer ply = (EntityPlayer) player;
                if (playerName.equalsIgnoreCase(ply.getName())) {
                    GameProfile profile = new GameProfile(ply.getUniqueID(), ply.getName());
                    NetworkPlayerInfo networkplayerinfo1 = new NetworkPlayerInfo(profile);
                    new ScaledResolution(Minecraft.getMinecraft());
                    GL11.glDisable(2929);
                    GL11.glEnable(3042);
                    GL11.glDepthMask(false);
                    OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                    Minecraft.getMinecraft().getTextureManager().bindTexture(networkplayerinfo1.getLocationSkin());
                    if (ply.isWearing(EnumPlayerModelParts.HAT)) {
                        Gui.drawScaledCustomSizeModalRect(x, y, width, height, width, height, width * 4, height * 4, width * 8, height * 8);//drawScaledCustomSizeModalRect
                    }
                    GL11.glDepthMask(true);
                    GL11.glDisable(3042);
                    GL11.glEnable(2929);
                }
            }
        }
    }


    @EventTarget
    public void onRender2D(EventRender2D event) {
        ScaledResolution res = new ScaledResolution(mc);
        EntityLivingBase target1 = target;
        int x = res.getScaledWidth() / 2 + 30;
        int y = res.getScaledHeight() / 2 + 30;
        int Custom = new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)).getRGB();
        if (target1 != this.lastEnt && target1 != null) {
            this.lastEnt = target1;
        }
        if (startAnim) {
            stopAnim = false;
        }
        if (animAlpha == 255 && target == null) {
            stopAnim = true;
        }
        startAnim = target != null;
        if (startAnim) {
            anim = AnimationUtils.mvoeUD(anim, 0.0f, 0.09f);
            if (animAlpha < 255) {
                animAlpha += 15;
            }
        }
        if (stopAnim) {
            anim = AnimationUtils.mvoeUD(anim, 75f, 0.09f);
            if (animAlpha > 0) {
                animAlpha -= 15;
            }
        }
        if (target == null && animAlpha < 255) {
            startAnim = false;
            stopAnim = true;
        }

        final float nameWidth = 38;
        if (thudMode.getValue() == THUDMode.Client) {

            if (target != null && Esp.getValue()) {
                ScaledResolution scaledResolution = new ScaledResolution(mc);
                CFontRenderer fr = FontLoaders.kiona18;
                CFontRenderer fr2 = FontLoaders.kiona24;
                int width = 125;
                int height = 35;
                int x1 = scaledResolution.getScaledWidth() / 6 * 4 - width;
                int y1 = scaledResolution.getScaledHeight() / 4 * 3 - height;
                int healthBarX = x + 37;
                int healthBarWidth = healthBarX + fr.getStringWidth(target.getName()) + 5;
                int healtBarHeight = 15;
                int healhBarY = y + height - healtBarHeight - 3;
                int healthPercent = (int) EntityHelper.getEntityHealthPercent((EntityPlayer) target);
                float var28 = target.getHealth() + target.getAbsorptionAmount();
                float var32 = target.getMaxHealth() + target.getAbsorptionAmount();
                float var37 = 35 + fr.getStringWidth("aaaaaaaaaaaaaa40");
                float var42 = (float) ((double) Math.round((double) var28 * 100.0) / 100.0);
                float var46 = 100.0f / var32;
                float var48 = var42 * var46;
                float var51 = (var37 - 50.0f) / 100.0f;
                RenderUtil.drawRect(x - 1, y - 2, x + 41 + fr.getStringWidth("aaaaaaaaaaaaaa"), y + height + 1, RenderUtil.transparency(new Color(37, 37, 37).getRGB(), 0.6f));
                RenderUtil.drawHead((AbstractClientPlayer) target, x + 2, y + 2, 30, 30);
                try {
                    RenderUtil.drawRect(healthBarX, healhBarY, 616, healhBarY + healtBarHeight + 1, RenderUtil.transparency(new Color(255, 255,255).getRGB(), 0.8f));
                    RenderUtil.drawRect(healthBarX, healhBarY, (float) healthBarX + var48 * var51 + 3.0f, healhBarY + healtBarHeight + 1, RenderUtil.transparency(new Color(HUD.r.getValue().intValue(), HUD.g.getValue().intValue(), HUD.b.getValue().intValue()).getRGB(), 1f));
                    RenderUtil.drawRect(healthBarX, healhBarY - healtBarHeight, 616, healhBarY - 1, RenderUtil.transparency(new Color(255, 255,255).getRGB(), 0.8f));
                    fr.drawString(String.format("%.1f", Float.valueOf(var48)) + "% Health", x + 39, healhBarY + 4.5f, new Color(40, 40, 40).getRGB());
                    fr2.drawString(target.getName(), x + 37, y + 4, new Color(40, 40, 40).getRGB());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static int SkyRainbow(int var2, float bright, float st) {
        double v1 = Math.ceil(System.currentTimeMillis() + (long) (var2 * 109)) / 5;
        return Color.getHSBColor((double) ((float) ((v1 %= 360.0) / 360.0)) < 0.5 ? -((float) (v1 / 360.0)) : (float) (v1 / 360.0), st, bright).getRGB();
    }

    private void onArmor(EntityLivingBase target) {
        ScaledResolution res = new ScaledResolution(mc);
        int x = res.getScaledWidth() / 2 + 38;
        int y = res.getScaledHeight() / 2 + 26;
        final EntityPlayer player = (EntityPlayer)target;
        final ItemStack[] render = player.inventory.armorInventory;
        ItemStack render1 = null;
        if (player.getCurrentEquippedItem() != null) {
            render1 = player.getCurrentEquippedItem();
        }
        renderItemStack(render[3], x + 40, y + 40);
        renderItemStack(render[2], x + 56, y + 40);
        renderItemStack(render[1], x + 72, y + 40);
        renderItemStack(render[0], x + 88, y + 40);
        renderItemStack(render1	,  x + 104,y + 40);
    }

    public static void renderItemStack(final ItemStack stack, final int x, final int y) {
        GlStateManager.depthMask(true);
        GlStateManager.clear(256);
        RenderHelper.enableStandardItemLighting();
        Minecraft.getMinecraft().getRenderItem().zLevel = -150.0f;
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
        final RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
        Minecraft.getMinecraft();
        renderItem.renderItemOverlays(Minecraft.getMinecraft().fontRendererObj, stack, x, y);
        Minecraft.getMinecraft().getRenderItem().zLevel = 0.0f;
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableCull();
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        final double s = 0.5;
        GlStateManager.scale(s, s, s);
        GlStateManager.disableDepth();
        GlStateManager.enableDepth();
        GlStateManager.scale(2.0f, 2.0f, 2.0f);
    }

    private boolean hasSword() {
        if (Minecraft.thePlayer.inventory.getCurrentItem() != null) {
            return Minecraft.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword;
        } else {
            return false;
        }
    }

    public static boolean shouldAttack() {
        return AttackTimer.hasReached(1000.0 / (Cps.getValue() + MathUtil.randomDouble(-1.0, 1.0)));
    }

    @EventTarget
    private void onUpdate(EventUpdate event) {
        if(event.isPre()) {
            this.setSuffix(this.Mode.getValue());

            this.targets = getTargets(Range.getValue());

            if(target == null && isBlocking){
                stopBlock();
            }

            targets.sort(this.angleComparator);

            if (this.targets.size() > 1 && this.Mode.getValue() == AuraMode.Switch) {
                if (SwitchTimer.delay(SwitchDelay.getValue().longValue())) {
                    ++this.index;
                    SwitchTimer.reset();
                }
            }

            if (Minecraft.thePlayer.ticksExisted % SwitchDelay.getValue().intValue() == 0 && this.targets.size() > 1 && this.Mode.getValue() == AuraMode.Single) {

                if (target.getDistanceToEntity(Minecraft.thePlayer) > Range.getValue()) {
                    ++index;
                } else if (target.isDead) {
                    ++index;
                }
            }

            if (target != null) {
                target = null;
            }

            if (!this.targets.isEmpty()) {
                if (this.index >= this.targets.size()) {
                    this.index = 0;
                }
                target = (EntityLivingBase) this.targets.get(this.index);
                if (this.shouldAttack()) {
                    EventManager.call(new EventFight(target, true));
                    attackEntity(target);
                    this.AttackTimer.reset();
                    EventManager.call(new EventFight(target, false));
                }
                switch (this.rotmode.getValue().toString()) {
                    case "Hypixel": {
                        Minecraft.thePlayer.rotationYawHead = getRotation1(target)[0];
                        Minecraft.thePlayer.rotationPitchHead = getRotation1(target)[1];
                        Minecraft.thePlayer.renderYawOffset = getRotation1(target)[0];
                        event.setYaw(getRotation1(target)[0]);
                        event.setPitch(getRotation1(target)[1]);
                        break;
                    }
                }
            }
        }
    }

    private void attackEntity(Entity target) {
        if(!ModuleManager.getModuleByClass(Scaffold.class).isEnabled()) {
            Minecraft.thePlayer.swingItem();
            Minecraft.getNetHandler().getNetworkManager().sendPacket(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
            if (shouldBlock()) {
                doBlock();
            }
            this.timer.reset();
        }
    }

    public boolean shouldBlock() {
        return Autoblock.getValue() && this.hasSword() && target != null && target.isEntityAlive() && isEnabled() && !mc.playerController.isBreakingBlock();
    }

    //Blocking
    private void doBlock() {
        if (Minecraft.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword) {
            isBlocking = true;
            blockingAnim = true;
            double value = -1;
            Minecraft.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(value, value, value), 255, Minecraft.thePlayer.inventory.getCurrentItem(), 0, 0, 0));
        }
    }

    private void stopBlock(){
        blockingAnim = false;
        if (Minecraft.thePlayer.getCurrentEquippedItem() != null  ) {
            if(Minecraft.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword) {
                Minecraft.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
//                Minecraft.thePlayer.clearItemInUse();
            }
        }
        isBlocking = false;
    }


    public static List<Entity> getTargets(Double value) {
        return Minecraft.theWorld.loadedEntityList.stream().filter(e -> (double) Minecraft.thePlayer.getDistanceToEntity(e) <= value && CanAttack(e)).collect(Collectors.toList());
    }

    @EventTarget
    public void onSend(EventPacketReceive event) {
        lossTimer.reset();
    }

    @EventTarget
    public void onPacketSend(EventPacket e){
        if (e.isOutGoing()) {
            Packet packet = e.getPacket();
            if ((packet instanceof C07PacketPlayerDigging || packet instanceof C09PacketHeldItemChange)) {
                executingblock = false;
            } else if (packet instanceof C08PacketPlayerBlockPlacement && target != null && !executingblock) {
//                Helper.sendMessage("KaBlockTrue");
                executingblock = true;
            }
        }
    }

    private static boolean CanAttack(Entity e2) {
        if (Minecraft.thePlayer.getDistanceToEntity(e2) > Range.getValue())
            return false;
        if (e2.isInvisible() && !Invis.getValue())
            return false;
        if (!e2.isEntityAlive())
            return false;
        if (e2 == Minecraft.thePlayer || FriendManager.isFriend(e2.getName()) || e2.isDead || Minecraft.thePlayer.getHealth() == 0F)
            return false;
        if ((e2 instanceof EntityMob || e2 instanceof EntityGhast || e2 instanceof EntityGolem
                || e2 instanceof EntityDragon || e2 instanceof EntitySlime) && Mobs.getValue())
            return true;
        if ((e2 instanceof EntitySquid || e2 instanceof EntityBat || e2 instanceof EntityVillager)
                && Animals.getValue())
            return true;
        if (e2 instanceof EntityAnimal && Animals.getValue())
            return true;

        AntiBot ab2 = (AntiBot) ModuleManager.getModuleByClass(AntiBot.class);
        if (AntiBot.isEntityBot(e2))
            return false;

        return e2 instanceof EntityPlayer && Players.getValue() && !Teams.isOnSameTeam(e2);
    }

    public static float[] getRotationFromPositionSlow(double x, double z, double y) {
        double xDiff = x - Minecraft.thePlayer.posX;
        double zDiff = z - Minecraft.thePlayer.posZ;
        double yDiff = y - Minecraft.thePlayer.posY - 1.2;

        double dist = MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff);
        float yaw = (float) (Math.atan2(zDiff, xDiff) * 180 / 3.141592653589793D) - 90.0F;
        float pitch = (float) -(Math.atan2(yDiff, dist) * 180 / 3.141592653589793D);
        return new float[]{yaw, pitch};
    }

    public static float[] getRotation3(EntityLivingBase ent) {
        double x = ent.posX;
        double z = ent.posZ;
        double y = ent.posY + ent.getEyeHeight() / 2f;
        return getRotationFromPositionSlow(x, z, y);
    }

    public static float[] getRotation2(EntityLivingBase target) {
        Minecraft.getMinecraft();
        double xDiff = target.posX - Minecraft.thePlayer.posX;
        double yDiff = target.posY - Minecraft.thePlayer.posY-0.2;
        double zDiff = target.posZ - Minecraft.thePlayer.posZ;
        Minecraft.getMinecraft();

        Minecraft.getMinecraft();

        double dist = MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff);
        float yaw = (float)(Math.atan2(zDiff, xDiff) * 180 / Math.PI) - 90f;
        float pitch = (float)((- Math.atan2(yDiff, dist)) * 180 / Math.PI);
        float[] array = new float[2];
        int n = 0;
        Minecraft.getMinecraft();
        float rotationYaw = Minecraft.thePlayer.rotationYaw;
        float n2 = yaw;
        Minecraft.getMinecraft();
        array[n] = rotationYaw + MathHelper.wrapAngleTo180_float(n2 - Minecraft.thePlayer.rotationYaw);
        int n3 = 1;
        Minecraft.getMinecraft();
        float rotationPitch = Minecraft.thePlayer.rotationPitch;
        float n4 = pitch;
        Minecraft.getMinecraft();
        array[n3] = rotationPitch + MathHelper.wrapAngleTo180_float(n4 - Minecraft.thePlayer.rotationPitch);
        return array;
    }

    public static float[] getRotation1(EntityLivingBase entity) {
        EntityLivingBase entityLivingBase = entity;
        double diffX = entityLivingBase.posX - Minecraft.thePlayer.posX;
        double diffZ = entityLivingBase.posZ - Minecraft.thePlayer.posZ;
        double diffY = entityLivingBase.posY + (double) entity.getEyeHeight() - (Minecraft.thePlayer.posY + (double) Minecraft.thePlayer.getEyeHeight());
        double X = diffX;
        double Z = diffZ;
        double dist = MathHelper.sqrt_double(X * X + Z * Z);
        float yaw = (float) (Math.atan2(diffZ, diffX) * 180 / 3.141592653589) - 90.0f;
        float pitch = (float) (-(Math.atan2(diffY, dist) * 180 / 3.141592653589));
        return new float[]{yaw, pitch};
    }

    private void drawShadow(final Entity entity, final double rad, final int color, final boolean shade) {
        GL11.glPushMatrix();
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glEnable(2832);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
        GL11.glHint(3153, 4354);
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
        if (shade) GL11.glShadeModel(GL11.GL_SMOOTH);
        GlStateManager.disableCull();
        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);

        final double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks - RenderManager.renderPosX;
        final double y = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks - RenderManager.renderPosY) + Math.sin(System.currentTimeMillis() / 2E+2) + 1;
        final double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks - RenderManager.renderPosZ;

        Color c;

        for (float i = 0; i < Math.PI * 2; i += Math.PI * 2 / 64.F) {
            final double vecX = x + rad * Math.cos(i);
            final double vecZ = z + rad * Math.sin(i);

            c = new Color(HUD.r.getValue().intValue(),HUD.g.getValue().intValue(),HUD.b.getValue().intValue());

            if (shade) {
                GL11.glColor4f(c.getRed() / 255.F,
                        c.getGreen() / 255.F,
                        c.getBlue() / 255.F,
                        0
                );
                GL11.glVertex3d(vecX, y - Math.cos(System.currentTimeMillis() / 2E+2) / 2.0F, vecZ);
                GL11.glColor4f(c.getRed() / 255.F,
                        c.getGreen() / 255.F,
                        c.getBlue() / 255.F,
                        0.85F
                );
            }
            GL11.glVertex3d(vecX, y, vecZ);
        }

        GL11.glEnd();
        if (shade) GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.enableCull();
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(2848);
        GL11.glDisable(2848);
        GL11.glEnable(2832);
        GL11.glEnable(3553);
        GL11.glPopMatrix();
        GL11.glColor3f(255, 255, 255);
    }

    public static void glColor(int hex) {
        float alpha = (float)(hex >> 24 & 255) / 255.0f;
        float red = (float)(hex >> 16 & 255) / 255.0f;
        float green = (float)(hex >> 8 & 255) / 255.0f;
        float blue = (float)(hex & 255) / 255.0f;
        GL11.glColor4f(red, green, blue, alpha == 0.0f ? 1.0f : alpha);
    }

    public static EntityLivingBase getTarget() {
        return target;
    }

    enum AuraMode {
        Switch,
        Single,
    }
    enum THUDMode {
        Client
    }
    enum AuraRotMode {
        Hypixel,
        None
    }
}


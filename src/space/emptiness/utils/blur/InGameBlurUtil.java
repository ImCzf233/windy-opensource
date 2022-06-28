package space.emptiness.utils.blur;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL20;

import space.emptiness.events.EventManager;
import space.emptiness.events.misc.EventBlur;

import static org.lwjgl.opengl.GL11.*;

public class InGameBlurUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();
    public static ShaderUtil gaussianProgram = new ShaderUtil("rise/shader/blur.frag");
    public static Framebuffer toBlurBuffer = new Framebuffer(1, 1, false);
    public static Framebuffer blurredBuffer = new Framebuffer(1, 1, false);
    private static Framebuffer blurPass2 = new Framebuffer(1, 1, false);
    private static Framebuffer blurPass3 = new Framebuffer(1, 1, false);

    public static ScaledResolution sr = new ScaledResolution(mc, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);

    public static void renderGaussianBlur(final float radius, final float compression, final boolean callEvent, final boolean backTrackBlurOnScreen) {
        sr = new ScaledResolution(mc, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
        GlStateManager.enableBlend();
        GlStateManager.color(1, 1, 1, 1);
        OpenGlHelper.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);

        StencilUtil.initStencil();
        StencilUtil.bindWriteStencilBuffer();

        if (callEvent) {
            final EventBlur eventBlur = new EventBlur();
            EventManager.call(eventBlur);
        }

        if (backTrackBlurOnScreen) {
            ShaderUtil.drawQuads(sr);
        }

        StencilUtil.bindReadStencilBuffer(1);

        // horizontal pass
        gaussianProgram.init();
        setupBlurUniforms(radius);
        blurredBuffer.framebufferClear();
        blurredBuffer.bindFramebuffer(false);
        GL20.glUniform2f(gaussianProgram.getUniform("direction"), compression, 0.0F);
        glBindTexture(GL_TEXTURE_2D, mc.getFramebuffer().framebufferTexture);
        ShaderUtil.drawQuads(sr);
        blurredBuffer.unbindFramebuffer();

        // vertical pass
        gaussianProgram.init();
        setupBlurUniforms(radius);
        mc.getFramebuffer().bindFramebuffer(false);
        GL20.glUniform2f(gaussianProgram.getUniform("direction"), 0.0F, compression);
        glBindTexture(GL_TEXTURE_2D, blurredBuffer.framebufferTexture);
        ShaderUtil.drawQuads(sr);

        gaussianProgram.unload();
        StencilUtil.uninitStencilBuffer();
    }

    private static void setupBlurUniforms(final float radius) {
        GL20.glUniform1i(gaussianProgram.getUniform("texture"), 0);
        GL20.glUniform2f(gaussianProgram.getUniform("texelSize"), 1.0F / mc.displayWidth, 1.0F / mc.displayHeight);
        GL20.glUniform1f(gaussianProgram.getUniform("radius"), MathHelper.ceiling_float_int((2 * radius)));
    }


    public static void setupBuffers() {
        if (mc.displayWidth != blurredBuffer.framebufferWidth || mc.displayHeight != blurredBuffer.framebufferHeight) {
            blurredBuffer.deleteFramebuffer();
            blurredBuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, false);

            blurPass2.deleteFramebuffer();
            blurPass2 = new Framebuffer(mc.displayWidth, mc.displayHeight, false);

            blurPass3.deleteFramebuffer();
            blurPass3 = new Framebuffer(mc.displayWidth, mc.displayHeight, false);

            toBlurBuffer.deleteFramebuffer();
            toBlurBuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, false);
        }
    }

    public static void preBlur() {
        toBlurBuffer.bindFramebuffer(false);
        setupBuffers();
        GlStateManager.enableBlend();
        OpenGlHelper.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
        StencilUtil.initStencil();
        StencilUtil.bindWriteStencilBuffer();
    }

    public static void postBlur(final float radius, final float compression) {
        final ScaledResolution sr = new ScaledResolution(mc, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);

        StencilUtil.bindReadStencilBuffer(1);
        // horizontal pass
        gaussianProgram.init();
        setupBlurUniforms(radius);
        blurredBuffer.framebufferClear();
        blurredBuffer.bindFramebuffer(false);
        GL20.glUniform2f(gaussianProgram.getUniform("direction"), compression, 0.0F);
        glBindTexture(GL_TEXTURE_2D, mc.getFramebuffer().framebufferTexture);
        ShaderUtil.drawQuads(sr);
        blurredBuffer.unbindFramebuffer();

        // vertical pass
        gaussianProgram.init();
        setupBlurUniforms(radius);
        mc.getFramebuffer().bindFramebuffer(false);
        GL20.glUniform2f(gaussianProgram.getUniform("direction"), 0.0F, compression);
        glBindTexture(GL_TEXTURE_2D, blurredBuffer.framebufferTexture);
        ShaderUtil.drawQuads(sr);

        gaussianProgram.unload();
        StencilUtil.uninitStencilBuffer();
    }


    public static void backTrackBlur(final float radius, final float compression) {
        setupBuffers();
        renderGaussianBlur(radius, compression, false, true);
    }

}

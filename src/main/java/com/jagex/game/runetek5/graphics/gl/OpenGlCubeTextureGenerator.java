package com.jagex.game.runetek5.graphics.gl;

import com.jagex.game.runetek5.core.constants.OpenGLConstants;
import jaggl.OpenGL;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!qda")
public final class OpenGlCubeTextureGenerator extends GlEnvironmentSampler {

	@OriginalMember(owner = "client!qda", name = "q", descriptor = "Lclient!qo;")
	private final GlToolkit glToolkit;

	@OriginalMember(owner = "client!qda", name = "k", descriptor = "Lclient!q;")
	private final GlCubeTexture cubeTexture;

	@OriginalMember(owner = "client!qda", name = "<init>", descriptor = "(Lclient!qo;I)V")
	public OpenGlCubeTextureGenerator(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) int size) {
		this.glToolkit = glToolkit;
		this.cubeTexture = new GlCubeTexture(glToolkit, 6408, size);
	}

	@OriginalMember(owner = "client!qda", name = "a", descriptor = "(Lclient!q;FILclient!q;)Z")
	public boolean blendCubeTextures(@OriginalArg(0) GlCubeTexture glCubeTexture, @OriginalArg(1) float alpha, @OriginalArg(3) GlCubeTexture glCubeTexture2) {
		@Pc(7) boolean framebuffersReady = true;
		@Pc(11) GlFramebuffer glFramebuffer = this.glToolkit.integratedGraphicsImageProducer;
		this.glToolkit.K(OpenGlCubeTextureGenerator.savedClip);
		this.glToolkit.la();
		this.glToolkit.setOrthoMatrixMode();
		OpenGL.glMatrixMode(OpenGLConstants.GL_PROJECTION);
		OpenGL.glLoadIdentity();
		OpenGL.glOrtho(0.0D, 1.0D, 0.0D, 1.0D, -1.0D, 1.0D);
		OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW);
		OpenGL.glLoadIdentity();
		OpenGL.glPushAttrib(OpenGLConstants.GL_VIEWPORT_BIT);
		OpenGL.glViewport(0, 0, this.cubeTexture.size, this.cubeTexture.size);
		this.glToolkit.toggleFog(false);
		this.glToolkit.setLightingEnabled(false);
		this.glToolkit.toggleZbuffer(false);
		this.glToolkit.setDepthMask(false);
		this.glToolkit.renderTexture(-2);
		this.glToolkit.activateTexture(1);
		this.glToolkit.updatePointSizeRange(alpha, 0.0F, 0.0F, 0.0F);
		this.glToolkit.loadTextureCombineMode(34165, 34165);
		this.glToolkit.setActiveTexture(glCubeTexture2);
		this.glToolkit.activateTexture(0);
		this.glToolkit.setColourOp(1);
		this.glToolkit.setActiveTexture(glCubeTexture);
		this.glToolkit.pushTarget(glFramebuffer);
		for (@Pc(125) int i = 0; i < 6; i++) {
			@Pc(131) int cubeFace = i + 34069;
			glFramebuffer.attachCubeFace(cubeFace, this.cubeTexture);
			glFramebuffer.drawBuffer(0);
			if (!glFramebuffer.isFramebufferComplete()) {
				framebuffersReady = false;
				break;
			}
			OpenGL.glBegin(OpenGLConstants.GL_QUADS);
			if (cubeFace == 34069) {
				OpenGL.glTexCoord3i(65535, 65534, 65534);
				OpenGL.glMultiTexCoord3i(OpenGLConstants.GL_TEXTURE1, 65535, 65534, 65534);
				OpenGL.glVertex2f(0.0F, 0.0F);
				OpenGL.glTexCoord3i(65535, 65534, -65534);
				OpenGL.glMultiTexCoord3i(OpenGLConstants.GL_TEXTURE1, 65535, 65534, -65534);
				OpenGL.glVertex2f(1.0F, 0.0F);
				OpenGL.glTexCoord3i(65535, -65534, -65534);
				OpenGL.glMultiTexCoord3i(OpenGLConstants.GL_TEXTURE1, 65535, -65534, -65534);
				OpenGL.glVertex2f(1.0F, 1.0F);
				OpenGL.glTexCoord3i(65535, -65534, 65534);
				OpenGL.glMultiTexCoord3i(OpenGLConstants.GL_TEXTURE1, 65535, -65534, 65534);
				OpenGL.glVertex2f(0.0F, 1.0F);
			} else if (cubeFace == 34070) {
				OpenGL.glTexCoord3i(-65535, 65534, -65534);
				OpenGL.glMultiTexCoord3i(OpenGLConstants.GL_TEXTURE1, -65535, 65534, -65534);
				OpenGL.glVertex2f(0.0F, 0.0F);
				OpenGL.glTexCoord3i(-65535, 65534, 65534);
				OpenGL.glMultiTexCoord3i(OpenGLConstants.GL_TEXTURE1, -65535, 65534, 65534);
				OpenGL.glVertex2f(1.0F, 0.0F);
				OpenGL.glTexCoord3i(-65535, -65534, 65534);
				OpenGL.glMultiTexCoord3i(OpenGLConstants.GL_TEXTURE1, -65535, -65534, 65534);
				OpenGL.glVertex2f(1.0F, 1.0F);
				OpenGL.glTexCoord3i(-65535, -65534, -65534);
				OpenGL.glMultiTexCoord3i(OpenGLConstants.GL_TEXTURE1, -65535, -65534, -65534);
				OpenGL.glVertex2f(0.0F, 1.0F);
			} else if (cubeFace == 34071) {
				OpenGL.glTexCoord3i(-65534, 65535, -65534);
				OpenGL.glMultiTexCoord3i(OpenGLConstants.GL_TEXTURE1, -65534, 65535, -65534);
				OpenGL.glVertex2f(0.0F, 0.0F);
				OpenGL.glTexCoord3i(65534, 65535, -65534);
				OpenGL.glMultiTexCoord3i(OpenGLConstants.GL_TEXTURE1, 65534, 65535, -65534);
				OpenGL.glVertex2f(1.0F, 0.0F);
				OpenGL.glTexCoord3i(65534, 65535, 65534);
				OpenGL.glMultiTexCoord3i(OpenGLConstants.GL_TEXTURE1, 65534, 65535, 65534);
				OpenGL.glVertex2f(1.0F, 1.0F);
				OpenGL.glTexCoord3i(-65534, 65535, 65534);
				OpenGL.glMultiTexCoord3i(OpenGLConstants.GL_TEXTURE1, -65534, 65535, 65534);
				OpenGL.glVertex2f(0.0F, 1.0F);
			} else if (cubeFace == 34072) {
				OpenGL.glTexCoord3i(-65534, -65535, 65534);
				OpenGL.glMultiTexCoord3i(OpenGLConstants.GL_TEXTURE1, -65534, -65535, 65534);
				OpenGL.glVertex2f(0.0F, 0.0F);
				OpenGL.glTexCoord3i(65534, -65535, 65534);
				OpenGL.glMultiTexCoord3i(OpenGLConstants.GL_TEXTURE1, 65534, -65535, 65534);
				OpenGL.glVertex2f(1.0F, 0.0F);
				OpenGL.glTexCoord3i(65534, -65535, -65534);
				OpenGL.glMultiTexCoord3i(OpenGLConstants.GL_TEXTURE1, 65534, -65535, -65534);
				OpenGL.glVertex2f(1.0F, 1.0F);
				OpenGL.glTexCoord3i(-65534, -65535, -65534);
				OpenGL.glMultiTexCoord3i(OpenGLConstants.GL_TEXTURE1, -65534, -65535, -65534);
				OpenGL.glVertex2f(0.0F, 1.0F);
			} else if (cubeFace == 34073) {
				OpenGL.glTexCoord3i(-65534, 65534, 65535);
				OpenGL.glMultiTexCoord3i(OpenGLConstants.GL_TEXTURE1, -65534, 65534, 65535);
				OpenGL.glVertex2f(0.0F, 0.0F);
				OpenGL.glTexCoord3i(65534, 65534, 65535);
				OpenGL.glMultiTexCoord3i(OpenGLConstants.GL_TEXTURE1, 65534, 65534, 65535);
				OpenGL.glVertex2f(1.0F, 0.0F);
				OpenGL.glTexCoord3i(65534, -65534, 65535);
				OpenGL.glMultiTexCoord3i(OpenGLConstants.GL_TEXTURE1, 65534, -65534, 65535);
				OpenGL.glVertex2f(1.0F, 1.0F);
				OpenGL.glTexCoord3i(-65534, -65534, 65535);
				OpenGL.glMultiTexCoord3i(OpenGLConstants.GL_TEXTURE1, -65534, -65534, 65535);
				OpenGL.glVertex2f(0.0F, 1.0F);
			} else if (cubeFace == 34074) {
				OpenGL.glTexCoord3i(65534, 65534, -65535);
				OpenGL.glMultiTexCoord3i(OpenGLConstants.GL_TEXTURE1, 65534, 65534, -65535);
				OpenGL.glVertex2f(0.0F, 0.0F);
				OpenGL.glTexCoord3i(-65534, 65534, -65535);
				OpenGL.glMultiTexCoord3i(OpenGLConstants.GL_TEXTURE1, -65534, 65534, -65535);
				OpenGL.glVertex2f(1.0F, 0.0F);
				OpenGL.glTexCoord3i(-65534, -65534, -65535);
				OpenGL.glMultiTexCoord3i(OpenGLConstants.GL_TEXTURE1, -65534, -65534, -65535);
				OpenGL.glVertex2f(1.0F, 1.0F);
				OpenGL.glTexCoord3i(65534, -65534, -65535);
				OpenGL.glMultiTexCoord3i(OpenGLConstants.GL_TEXTURE1, 65534, -65534, -65535);
				OpenGL.glVertex2f(0.0F, 1.0F);
			}
			OpenGL.glEnd();
		}
		glFramebuffer.clearAttachment(0);
		this.glToolkit.popTarget(glFramebuffer);
		this.glToolkit.activateTexture(1);
		this.glToolkit.setActiveTexture(null);
		this.glToolkit.loadTextureCombineMode(8448, 8448);
		this.glToolkit.activateTexture(0);
		this.glToolkit.setActiveTexture(null);
		OpenGL.glPopAttrib();
		this.glToolkit.KA(OpenGlCubeTextureGenerator.savedClip[0], OpenGlCubeTextureGenerator.savedClip[1], OpenGlCubeTextureGenerator.savedClip[2], OpenGlCubeTextureGenerator.savedClip[3]);
		if (framebuffersReady && !this.glToolkit.isAmd) {
			this.cubeTexture.generateMipmaps();
		}
		return framebuffersReady;
	}

	@OriginalMember(owner = "client!qda", name = "a", descriptor = "(B)Lclient!q;")
	@Override
	public GlCubeTexture getCubeTexture() {
		return this.cubeTexture;
	}

	@OriginalMember(owner = "client!qda", name = "b", descriptor = "(I)I")
	public int getSize() {
		return this.cubeTexture.size;
	}
	@OriginalMember(owner = "client!gba", name = "t", descriptor = "[I")
	public static final int[] savedClip = new int[4];
}

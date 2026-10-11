package com.jagex.game.runetek5.graphics.shader;

import com.jagex.game.runetek5.GlShader;
import com.jagex.game.runetek5.cache.node.lru.OpenGlRenderBuffer;
import com.jagex.game.runetek5.GlVolumeTexture;
import com.jagex.game.runetek5.core.constants.OpenGLConstants;
import jaggl.OpenGL;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.graphics.gl.GlFramebuffer;
import com.jagex.game.runetek5.graphics.gl.GlProgram;
import com.jagex.game.runetek5.graphics.gl.GlToolkit;

@OriginalClass("client!qi")
public final class Texture3DShaderGL {

	@OriginalMember(owner = "client!qi", name = "c", descriptor = "Lclient!dw;")
	private GlProgram shader;

	@OriginalMember(owner = "client!qi", name = "a", descriptor = "Lclient!qo;")
	private final GlToolkit openglToolkit;

	@OriginalMember(owner = "client!qi", name = "<init>", descriptor = "(Lclient!qo;)V")
	public Texture3DShaderGL(@OriginalArg(0) GlToolkit glToolkit) {
		this.openglToolkit = glToolkit;
	}

	@OriginalMember(owner = "client!qi", name = "a", descriptor = "(Lclient!gj;Lclient!gj;IF)Z")
	public boolean activate(@OriginalArg(0) GlVolumeTexture glVolumeTexture, @OriginalArg(1) GlVolumeTexture glVolumeTexture2, @OriginalArg(3) float relief) {
		if (!this.createShader()) {
			return false;
		}
		@Pc(15) GlFramebuffer glFramebuffer = this.openglToolkit.integratedGraphicsImageProducer;
		@Pc(34) OpenGlRenderBuffer renderBuffer = new OpenGlRenderBuffer(this.openglToolkit, 6408, glVolumeTexture.width, glVolumeTexture.height);
		this.openglToolkit.pushTarget(glFramebuffer);
		@Pc(41) boolean success = false;
		glFramebuffer.attachRenderbuffer(0, renderBuffer);
		if (glFramebuffer.isFramebufferComplete()) {
			OpenGL.glPushMatrix();
			OpenGL.glLoadIdentity();
			OpenGL.glMatrixMode(OpenGLConstants.GL_PROJECTION);
			OpenGL.glPushMatrix();
			OpenGL.glLoadIdentity();
			OpenGL.glOrtho(0.0D, 1.0D, 0.0D, 1.0D, -1.0D, 1.0D);
			OpenGL.glPushAttrib(OpenGLConstants.GL_VIEWPORT_BIT);
			OpenGL.glViewport(0, 0, glVolumeTexture.width, glVolumeTexture.height);
			OpenGL.glUseProgramObjectARB(this.shader.programHandle);
			OpenGL.glUniform1iARB(OpenGL.glGetUniformLocationARB(this.shader.programHandle, "heightMap"), 0);
			OpenGL.glUniform1fARB(OpenGL.glGetUniformLocationARB(this.shader.programHandle, "rcpRelief"), 1.0F / relief);
			OpenGL.glUniform2fARB(OpenGL.glGetUniformLocationARB(this.shader.programHandle, "sampleSize"), 1.0F / (float) glVolumeTexture2.width, 1.0F / (float) glVolumeTexture2.height);
			for (@Pc(110) int i = 0; i < glVolumeTexture.depth; i++) {
				@Pc(119) float depthCoord = (float) i / (float) glVolumeTexture.depth;
				this.openglToolkit.setActiveTexture(glVolumeTexture2);
				OpenGL.glBegin(OpenGLConstants.GL_QUADS);
				OpenGL.glTexCoord3f(0.0F, 0.0F, depthCoord);
				OpenGL.glVertex2f(0.0F, 0.0F);
				OpenGL.glTexCoord3f(1.0F, 0.0F, depthCoord);
				OpenGL.glVertex2f(1.0F, 0.0F);
				OpenGL.glTexCoord3f(1.0F, 1.0F, depthCoord);
				OpenGL.glVertex2f(1.0F, 1.0F);
				OpenGL.glTexCoord3f(0.0F, 1.0F, depthCoord);
				OpenGL.glVertex2f(0.0F, 1.0F);
				OpenGL.glEnd();
				glVolumeTexture.copyFromFramebuffer(glVolumeTexture.width, i, glVolumeTexture.height);
			}
			OpenGL.glUseProgramObjectARB(0L);
			OpenGL.glPopAttrib();
			OpenGL.glPopMatrix();
			OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW);
			OpenGL.glPopMatrix();
			success = true;
		}
		glFramebuffer.clearAttachment(0);
		this.openglToolkit.popTarget(glFramebuffer);
		return success;
	}

	@OriginalMember(owner = "client!qi", name = "a", descriptor = "(I)Z")
	public boolean createShader() {
		if (this.openglToolkit.extFramebufferObject && this.openglToolkit.arbFragmentShader && this.shader == null) {
			@Pc(17) GlShader fragmentShader = GlShader.compile(35632, this.openglToolkit, "uniform float rcpRelief;\nuniform vec2 sampleSize;\nuniform sampler3D heightMap;\nvoid main() {\nfloat dx = texture3D(heightMap, vec3(-sampleSize.x, 0.0, 0.0)+gl_TexCoord[0].xyz).r - texture3D(heightMap, vec3(sampleSize.x, 0.0, 0.0)+gl_TexCoord[0].xyz).r;\nfloat dy = texture3D(heightMap, vec3(0.0, -sampleSize.y, 0.0)+gl_TexCoord[0].xyz).r - texture3D(heightMap, vec3(0.0, sampleSize.y, 0.0)+gl_TexCoord[0].xyz).r;\ngl_FragColor = vec4(0.5+normalize(vec3(dx, dy, rcpRelief))*0.5, texture3D(heightMap, gl_TexCoord[0].xyz).r);\n}\n");
			if (fragmentShader != null) {
				this.shader = GlProgram.link(this.openglToolkit, new GlShader[] { fragmentShader });
			}
		}
		return this.shader != null;
	}
}

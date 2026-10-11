package com.jagex.game.runetek5.graphics.texture;

import com.jagex.game.runetek5.GlShader;
import com.jagex.game.runetek5.DragOriginY;
import com.jagex.game.runetek5.core.constants.OpenGLConstants;
import com.jagex.game.runetek5.graphics.texture.PostProcessEffect;
import jaggl.OpenGL;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.graphics.gl.OpenGl2DTexture;
import com.jagex.game.runetek5.graphics.gl.GlFramebuffer;
import com.jagex.game.runetek5.graphics.gl.GlProgram;
import com.jagex.game.runetek5.graphics.gl.GlToolkit;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!rda")
public final class RectangularTextureGL extends PostProcessEffect {

	@OriginalMember(owner = "client!rda", name = "A", descriptor = "Lclient!dw;")
	private GlProgram textureDisplay;

	@OriginalMember(owner = "client!rda", name = "C", descriptor = "Lclient!dw;")
	private GlProgram mipmappedScenetexShader;

	@OriginalMember(owner = "client!rda", name = "D", descriptor = "Lclient!rg;")
	private GlFramebuffer bloomTextureB;

	@OriginalMember(owner = "client!rda", name = "E", descriptor = "I")
	private int paddedWidth;

	@OriginalMember(owner = "client!rda", name = "I", descriptor = "Lclient!tp;")
	private OpenGl2DTexture blurTextureB;

	@OriginalMember(owner = "client!rda", name = "L", descriptor = "I")
	private int sceneHeight;

	@OriginalMember(owner = "client!rda", name = "N", descriptor = "Lclient!tp;")
	private OpenGl2DTexture blurTextureA;

	@OriginalMember(owner = "client!rda", name = "O", descriptor = "Lclient!dw;")
	private GlProgram bloomScenetexShader;

	@OriginalMember(owner = "client!rda", name = "Q", descriptor = "[Lclient!tp;")
	private OpenGl2DTexture[] mipmaps;

	@OriginalMember(owner = "client!rda", name = "R", descriptor = "I")
	private int paddedHeight;

	@OriginalMember(owner = "client!rda", name = "U", descriptor = "I")
	private int sceneWidth;

	@OriginalMember(owner = "client!rda", name = "V", descriptor = "Lclient!dw;")
	private GlProgram scenetexShader;

	@OriginalMember(owner = "client!rda", name = "X", descriptor = "Lclient!rg;")
	private GlFramebuffer bloomTextureA;

	@OriginalMember(owner = "client!rda", name = "<init>", descriptor = "(Lclient!qo;)V")
	public RectangularTextureGL(@OriginalArg(0) GlToolkit glToolkit) {
		super(glToolkit);
	}

	@OriginalMember(owner = "client!rda", name = "a", descriptor = "(BI)V")
	@Override
	public void endPass(@OriginalArg(1) int pass) {
		OpenGL.glUseProgramObjectARB(0L);
		super.activeToolkit.activateTexture(1);
		super.activeToolkit.setActiveTexture(null);
		super.activeToolkit.activateTexture(0);
	}

	@OriginalMember(owner = "client!rda", name = "a", descriptor = "(Lclient!tp;Lclient!tp;IB)V")
	@Override
	public void draw(@OriginalArg(0) OpenGl2DTexture depthTexture, @OriginalArg(1) OpenGl2DTexture colorTexture, @OriginalArg(2) int pass) {
		OpenGL.glPushAttrib(OpenGLConstants.GL_VIEWPORT_BIT);
		OpenGL.glMatrixMode(OpenGLConstants.GL_PROJECTION);
		OpenGL.glPushMatrix();
		OpenGL.glLoadIdentity();
		OpenGL.glOrtho(0.0D, 1.0D, 0.0D, 1.0D, -1.0D, 1.0D);
		@Pc(269) long program;
		if (this.mipmaps == null) {
			super.activeToolkit.setActiveTexture(colorTexture);
			super.activeToolkit.pushTarget(this.bloomTextureB);
			this.bloomTextureB.drawBuffer(0);
			OpenGL.glViewport(0, 0, 256, 256);
			program = this.scenetexShader.programHandle;
			OpenGL.glUseProgramObjectARB(program);
			OpenGL.glUniform1iARB(OpenGL.glGetUniformLocationARB(program, "sceneTex"), 0);
			OpenGL.glUniform3fARB(OpenGL.glGetUniformLocationARB(program, "params"), RectangularTextureGL.whitePoint, 0.0F, 0.0F);
			OpenGL.glBegin(OpenGLConstants.GL_QUADS);
			OpenGL.glTexCoord2f(0.0F, 0.0F);
			OpenGL.glVertex2i(0, 0);
			OpenGL.glTexCoord2f((float) this.sceneWidth, 0.0F);
			OpenGL.glVertex2i(1, 0);
			OpenGL.glTexCoord2f((float) this.sceneWidth, (float) this.sceneHeight);
			OpenGL.glVertex2i(1, 1);
			OpenGL.glTexCoord2f(0.0F, (float) this.sceneHeight);
			OpenGL.glVertex2i(0, 1);
			OpenGL.glEnd();
		} else {
			super.activeToolkit.pushTarget(this.bloomTextureA);
			@Pc(32) int width = Utils.floorPowerOfTwo(this.sceneWidth);
			@Pc(37) int height = Utils.floorPowerOfTwo(this.sceneHeight);
			@Pc(39) int level = 0;
			while (width > 256 || height > 256) {
				OpenGL.glViewport(0, 0, width, height);
				this.bloomTextureA.attachTexture(this.mipmaps[level], 0);
				if (level == 0) {
					super.activeToolkit.setActiveTexture(colorTexture);
					OpenGL.glBegin(OpenGLConstants.GL_QUADS);
					OpenGL.glTexCoord2f(0.0F, 0.0F);
					OpenGL.glVertex2i(0, 0);
					OpenGL.glTexCoord2f((float) this.sceneWidth, 0.0F);
					OpenGL.glVertex2i(1, 0);
					OpenGL.glTexCoord2f((float) this.sceneWidth, (float) this.sceneHeight);
					OpenGL.glVertex2i(1, 1);
					OpenGL.glTexCoord2f(0.0F, (float) this.sceneHeight);
					OpenGL.glVertex2i(0, 1);
					OpenGL.glEnd();
				} else {
					super.activeToolkit.setActiveTexture(this.mipmaps[level - 1]);
					OpenGL.glBegin(OpenGLConstants.GL_QUADS);
					OpenGL.glTexCoord2f(0.0F, 0.0F);
					OpenGL.glVertex2i(0, 0);
					OpenGL.glTexCoord2f(1.0F, 0.0F);
					OpenGL.glVertex2i(1, 0);
					OpenGL.glTexCoord2f(1.0F, 1.0F);
					OpenGL.glVertex2i(1, 1);
					OpenGL.glTexCoord2f(0.0F, 1.0F);
					OpenGL.glVertex2i(0, 1);
					OpenGL.glEnd();
				}
				if (height > 256) {
					height >>= 0x1;
				}
				if (width > 256) {
					width >>= 0x1;
				}
				level++;
			}
			super.activeToolkit.popTarget(this.bloomTextureA);
			super.activeToolkit.setActiveTexture(this.mipmaps[level - 1]);
			super.activeToolkit.pushTarget(this.bloomTextureB);
			this.bloomTextureB.drawBuffer(0);
			OpenGL.glViewport(0, 0, 256, 256);
			@Pc(201) long mipmapProgram = this.mipmappedScenetexShader.programHandle;
			OpenGL.glUseProgramObjectARB(mipmapProgram);
			OpenGL.glUniform1iARB(OpenGL.glGetUniformLocationARB(mipmapProgram, "sceneTex"), 0);
			OpenGL.glUniform3fARB(OpenGL.glGetUniformLocationARB(mipmapProgram, "params"), RectangularTextureGL.whitePoint, 0.0F, 0.0F);
			OpenGL.glBegin(OpenGLConstants.GL_QUADS);
			OpenGL.glTexCoord2f(0.0F, 0.0F);
			OpenGL.glVertex2i(0, 0);
			OpenGL.glTexCoord2f(1.0F, 0.0F);
			OpenGL.glVertex2i(1, 0);
			OpenGL.glTexCoord2f(1.0F, 1.0F);
			OpenGL.glVertex2i(1, 1);
			OpenGL.glTexCoord2f(0.0F, 1.0F);
			OpenGL.glVertex2i(0, 1);
			OpenGL.glEnd();
		}
		this.bloomTextureB.drawBuffer(1);
		super.activeToolkit.setActiveTexture(this.blurTextureA);
		program = this.textureDisplay.programHandle;
		OpenGL.glUseProgramObjectARB(program);
		OpenGL.glUniform1iARB(OpenGL.glGetUniformLocationARB(program, "baseTex"), 0);
		OpenGL.glUniform3fARB(OpenGL.glGetUniformLocationARB(program, "step"), 0.00390625F, 0.0F, 0.0F);
		OpenGL.glBegin(OpenGLConstants.GL_QUADS);
		OpenGL.glTexCoord2f(0.0F, 0.0F);
		OpenGL.glVertex2i(0, 0);
		OpenGL.glTexCoord2f(1.0F, 0.0F);
		OpenGL.glVertex2i(1, 0);
		OpenGL.glTexCoord2f(1.0F, 1.0F);
		OpenGL.glVertex2i(1, 1);
		OpenGL.glTexCoord2f(0.0F, 1.0F);
		OpenGL.glVertex2i(0, 1);
		OpenGL.glEnd();
		this.bloomTextureB.drawBuffer(0);
		super.activeToolkit.setActiveTexture(this.blurTextureB);
		OpenGL.glUniform3fARB(OpenGL.glGetUniformLocationARB(program, "step"), 0.0F, 0.00390625F, 0.0F);
		OpenGL.glBegin(OpenGLConstants.GL_QUADS);
		OpenGL.glTexCoord2f(0.0F, 0.0F);
		OpenGL.glVertex2i(0, 0);
		OpenGL.glTexCoord2f(1.0F, 0.0F);
		OpenGL.glVertex2i(1, 0);
		OpenGL.glTexCoord2f(1.0F, 1.0F);
		OpenGL.glVertex2i(1, 1);
		OpenGL.glTexCoord2f(0.0F, 1.0F);
		OpenGL.glVertex2i(0, 1);
		OpenGL.glEnd();
		OpenGL.glPopAttrib();
		OpenGL.glPopMatrix();
		OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW);
		super.activeToolkit.popTarget(this.bloomTextureB);
		@Pc(439) long bloomProgram = this.bloomScenetexShader.programHandle;
		OpenGL.glUseProgramObjectARB(bloomProgram);
		OpenGL.glUniform1iARB(OpenGL.glGetUniformLocationARB(bloomProgram, "sceneTex"), 0);
		OpenGL.glUniform1iARB(OpenGL.glGetUniformLocationARB(bloomProgram, "bloomTex"), 1);
		OpenGL.glUniform3fARB(OpenGL.glGetUniformLocationARB(bloomProgram, "params"), RectangularTextureGL.brightpass, RectangularTextureGL.bloom, 0.0F);
		super.activeToolkit.activateTexture(1);
		super.activeToolkit.setActiveTexture(this.blurTextureA);
		super.activeToolkit.activateTexture(0);
		super.activeToolkit.setActiveTexture(colorTexture);
	}

	@OriginalMember(owner = "client!rda", name = "a", descriptor = "(IBI)V")
	@Override
	public void resize(@OriginalArg(0) int height, @OriginalArg(2) int width) {
		this.sceneHeight = height;
		this.sceneWidth = width;
		@Pc(20) int pow2Width = Utils.floorPowerOfTwo(this.sceneWidth);
		@Pc(25) int pow2Height = Utils.floorPowerOfTwo(this.sceneHeight);
		if (pow2Width == this.paddedWidth && this.paddedHeight == pow2Height) {
			return;
		}
		@Pc(42) int local42;
		if (this.mipmaps != null) {
			for (int mipmapIndex = 0; mipmapIndex < this.mipmaps.length; mipmapIndex++) {
				this.mipmaps[mipmapIndex].delete();
			}
			this.mipmaps = null;
		}
		if (pow2Width <= 256 && pow2Height <= 256) {
			this.bloomTextureA = null;
		} else {
			int mipWidth = pow2Width;
			@Pc(75) int mipHeight = pow2Height;
			@Pc(77) int level = 0;
			label59: while (true) {
				if (mipWidth <= 256 && mipHeight <= 256) {
					if (this.bloomTextureA == null) {
						this.bloomTextureA = new GlFramebuffer(super.activeToolkit);
					}
					mipWidth = pow2Width;
					this.mipmaps = new OpenGl2DTexture[level];
					mipHeight = pow2Height;
					level = 0;
					while (true) {
						if (mipWidth <= 256 && mipHeight <= 256) {
							break label59;
						}
						this.mipmaps[level++] = new OpenGl2DTexture(super.activeToolkit, 3553, 34842, mipWidth, mipHeight);
						if (mipHeight > 256) {
							mipHeight >>= 0x1;
						}
						if (mipWidth > 256) {
							mipWidth >>= 0x1;
						}
					}
				}
				if (mipHeight > 256) {
					mipHeight >>= 0x1;
				}
				if (mipWidth > 256) {
					mipWidth >>= 0x1;
				}
				level++;
			}
		}
		this.paddedWidth = pow2Width;
		this.paddedHeight = pow2Height;
	}

	@OriginalMember(owner = "client!rda", name = "b", descriptor = "(B)V")
	@Override
	public void destroy() {
		this.textureDisplay = null;
		this.blurTextureA = null;
		this.bloomTextureA = null;
		this.bloomTextureB = null;
		this.scenetexShader = null;
		this.mipmaps = null;
		this.mipmappedScenetexShader = null;
		this.bloomScenetexShader = null;
		this.blurTextureB = null;
	}

	@OriginalMember(owner = "client!rda", name = "a", descriptor = "(Z)Z")
	@Override
	public boolean isLoaded() {
		return this.bloomTextureB != null;
	}

	@OriginalMember(owner = "client!rda", name = "e", descriptor = "(B)Z")
	public boolean isSupported() {
		return super.activeToolkit.extFramebufferObject && super.activeToolkit.arbFragmentShader && super.activeToolkit.arbTextureFloat;
	}

	@OriginalMember(owner = "client!rda", name = "b", descriptor = "(Z)I")
	@Override
	public int getFormat() {
		return 1;
	}

	@OriginalMember(owner = "client!rda", name = "b", descriptor = "(I)Z")
	@Override
	public boolean createShaders() {
		if (!super.activeToolkit.extFramebufferObject || !super.activeToolkit.arbFragmentShader || !super.activeToolkit.arbTextureFloat) {
			return false;
		}
		this.bloomTextureB = new GlFramebuffer(super.activeToolkit);
		this.blurTextureA = new OpenGl2DTexture(super.activeToolkit, 3553, 34842, 256, 256);
		this.blurTextureA.setRepeat(false, false);
		this.blurTextureB = new OpenGl2DTexture(super.activeToolkit, 3553, 34842, 256, 256);
		this.blurTextureB.setRepeat(false, false);
		super.activeToolkit.pushTarget(this.bloomTextureB);
		this.bloomTextureB.attachTexture(this.blurTextureA, 0);
		this.bloomTextureB.attachTexture(this.blurTextureB, 1);
		this.bloomTextureB.drawBuffer(0);
		if (!this.bloomTextureB.isFramebufferComplete()) {
			super.activeToolkit.popTarget(this.bloomTextureB);
			return false;
		}
		super.activeToolkit.popTarget(this.bloomTextureB);
		this.scenetexShader = GlProgram.link(super.activeToolkit, new GlShader[] { GlShader.compile(35632, super.activeToolkit, "#extension GL_ARB_texture_rectangle : enable\nuniform vec3 params;\nuniform sampler2DRect sceneTex;\nconst vec3 lumCoef = vec3(0.2126, 0.7152, 0.0722);\nvoid main() {\n    vec4 col = texture2DRect(sceneTex, gl_TexCoord[0].xy);\n    gl_FragColor = col*step(params.x, dot(lumCoef, col.rgb));\n}\n") });
		this.mipmappedScenetexShader = GlProgram.link(super.activeToolkit, new GlShader[] { GlShader.compile(35632, super.activeToolkit, "uniform vec3 params;\nuniform sampler2D sceneTex;\nconst vec3 lumCoef = vec3(0.2126, 0.7152, 0.0722);\nvoid main() {\n    vec4 col = texture2D(sceneTex, gl_TexCoord[0].xy);\n    gl_FragColor = col*step(params.x, dot(lumCoef, col.rgb));\n}\n") });
		this.bloomScenetexShader = GlProgram.link(super.activeToolkit, new GlShader[] { GlShader.compile(35632, super.activeToolkit, "#extension GL_ARB_texture_rectangle : enable\nuniform vec3 params;\nuniform vec3 dimScale;\nuniform sampler2D bloomTex;\nuniform sampler2DRect sceneTex;\nconst vec3 lumCoef = vec3(0.2126, 0.7152, 0.0722);\nvoid main() {\n\t vec4 bloomCol = texture2D(bloomTex, gl_TexCoord[1].xy);\n\t vec4 sceneCol = texture2DRect(sceneTex, gl_TexCoord[0].xy);\n\t float preLum = 0.99*dot(lumCoef, sceneCol.rgb)+0.01;\n    float postLum = preLum*(1.0+(preLum/params.y))/(preLum+1.0);\n\t gl_FragColor = sceneCol*(postLum/preLum)+bloomCol*params.x;\n}\n") });
		this.textureDisplay = GlProgram.link(super.activeToolkit, new GlShader[] { GlShader.compile(35632, super.activeToolkit, "uniform vec3 step;\nuniform sampler2D baseTex;\nvoid main() {\n\tvec4 fragCol = texture2D(baseTex, gl_TexCoord[0].xy)*0.091396265;\n\tfragCol += texture2D(baseTex, gl_TexCoord[0].xy+(-1.0*step.xy))*0.088584304;\n\tfragCol += texture2D(baseTex, gl_TexCoord[0].xy+( 1.0*step.xy))*0.088584304;\n\tfragCol += texture2D(baseTex, gl_TexCoord[0].xy+(-2.0*step.xy))*0.08065692;\n\tfragCol += texture2D(baseTex, gl_TexCoord[0].xy+( 2.0*step.xy))*0.08065692;\n\tfragCol += texture2D(baseTex, gl_TexCoord[0].xy+(-3.0*step.xy))*0.068989515;\n\tfragCol += texture2D(baseTex, gl_TexCoord[0].xy+( 3.0*step.xy))*0.068989515;\n\tfragCol += texture2D(baseTex, gl_TexCoord[0].xy+(-4.0*step.xy))*0.055434637;\n\tfragCol += texture2D(baseTex, gl_TexCoord[0].xy+( 4.0*step.xy))*0.055434637;\n\tfragCol += texture2D(baseTex, gl_TexCoord[0].xy+(-5.0*step.xy))*0.04184426;\n\tfragCol += texture2D(baseTex, gl_TexCoord[0].xy+( 5.0*step.xy))*0.04184426;\n\tfragCol += texture2D(baseTex, gl_TexCoord[0].xy+(-6.0*step.xy))*0.029672023;\n\tfragCol += texture2D(baseTex, gl_TexCoord[0].xy+( 6.0*step.xy))*0.029672023;\n\tfragCol += texture2D(baseTex, gl_TexCoord[0].xy+(-7.0*step.xy))*0.019765828;\n\tfragCol += texture2D(baseTex, gl_TexCoord[0].xy+( 7.0*step.xy))*0.019765828;\n\tfragCol += texture2D(baseTex, gl_TexCoord[0].xy+(-8.0*step.xy))*0.012369139;\n\tfragCol += texture2D(baseTex, gl_TexCoord[0].xy+( 8.0*step.xy))*0.012369139;\n\tgl_FragColor = fragCol;\n}\n") });
		return this.mipmappedScenetexShader != null && this.scenetexShader != null && this.bloomScenetexShader != null && this.textureDisplay != null;
	}
	@OriginalMember(owner = "client!ada", name = "R", descriptor = "F")
	public static float whitePoint = 1.0F;
	@OriginalMember(owner = "client!oi", name = "L", descriptor = "F")
	public static float brightpass = 0.25F;
	@OriginalMember(owner = "client!fca", name = "u", descriptor = "F")
	public static float bloom = 1.0F;
}

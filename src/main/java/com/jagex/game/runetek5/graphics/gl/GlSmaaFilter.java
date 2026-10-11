package com.jagex.game.runetek5.graphics.gl;

import com.jagex.game.runetek5.GlShader;
import com.jagex.game.runetek5.core.constants.OpenGLConstants;
import jaggl.OpenGL;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Not part of the original client: SMAA 1x (Jimenez et al., github.com/iryoku/smaa, MIT licence;
 * the shader and lookup textures are in the smaa resources) over the OpenGL 3D view with
 * client.smaa. Luma edge detection, blending weights and neighbourhood blending run on the resolved
 * view, at the high preset, into textures of the view's size; the last one is what is drawn into the
 * frame. A driver that cannot compile the shaders leaves the filter off.
 */
public final class GlSmaaFilter {

	private static final int GL_LUMINANCE = 6409;
	private static final int GL_LUMINANCE_ALPHA = 6410;
	private static final int GL_TEXTURE_WRAP_S = 10242;
	private static final int GL_TEXTURE_WRAP_T = 10243;
	private static final int GL_CLAMP_TO_EDGE = 33071;
	private static final int GL_VERTEX_SHADER = 35633;
	private static final int GL_FRAGMENT_SHADER = 35632;

	private final GlToolkit toolkit;
	private GlProgram edgeProgram;
	private GlProgram blendProgram;
	private GlProgram neighbourhoodProgram;
	private OpenGl2DTexture areaTexture;
	private OpenGl2DTexture searchTexture;
	public boolean available;

	private int width;
	private int height;
	private OpenGl2DTexture edges;
	private OpenGl2DTexture weights;
	public OpenGl2DTexture output;
	private GlFramebuffer edgesTarget;
	private GlFramebuffer weightsTarget;
	private GlFramebuffer outputTarget;

	public GlSmaaFilter(GlToolkit toolkit) {
		this.toolkit = toolkit;
		try {
			String smaa = resource("SMAA.hlsl");
			this.edgeProgram = program(smaa,
				"out vec2 vTexcoord;\nout vec4 vOffset[3];\nvoid main() {\n\tvTexcoord = gl_MultiTexCoord0.xy;\n\tSMAAEdgeDetectionVS(vTexcoord, vOffset);\n\tgl_Position = ftransform();\n}\n",
				"in vec2 vTexcoord;\nin vec4 vOffset[3];\nuniform sampler2D colorTex;\nvoid main() {\n\tgl_FragColor = vec4(SMAALumaEdgeDetectionPS(vTexcoord, vOffset, colorTex), 0.0, 0.0);\n}\n");
			this.blendProgram = program(smaa,
				"out vec2 vTexcoord;\nout vec2 vPixcoord;\nout vec4 vOffset[3];\nvoid main() {\n\tvTexcoord = gl_MultiTexCoord0.xy;\n\tSMAABlendingWeightCalculationVS(vTexcoord, vPixcoord, vOffset);\n\tgl_Position = ftransform();\n}\n",
				"in vec2 vTexcoord;\nin vec2 vPixcoord;\nin vec4 vOffset[3];\nuniform sampler2D edgesTex;\nuniform sampler2D areaTex;\nuniform sampler2D searchTex;\nvoid main() {\n\tgl_FragColor = SMAABlendingWeightCalculationPS(vTexcoord, vPixcoord, vOffset, edgesTex, areaTex, searchTex, vec4(0.0));\n}\n");
			this.neighbourhoodProgram = program(smaa,
				"out vec2 vTexcoord;\nout vec4 vOffset;\nvoid main() {\n\tvTexcoord = gl_MultiTexCoord0.xy;\n\tSMAANeighborhoodBlendingVS(vTexcoord, vOffset);\n\tgl_Position = ftransform();\n}\n",
				"in vec2 vTexcoord;\nin vec4 vOffset;\nuniform sampler2D colorTex;\nuniform sampler2D blendTex;\nvoid main() {\n\tgl_FragColor = SMAANeighborhoodBlendingPS(vTexcoord, vOffset, colorTex, blendTex);\n}\n");
			if (this.edgeProgram == null || this.blendProgram == null || this.neighbourhoodProgram == null) {
				System.err.println("client.smaa: this OpenGL driver cannot compile the SMAA shaders (GLSL 1.30); SMAA is off");
				return;
			}
			this.areaTexture = lookup(resourceBytes("AreaTex.bin"), 160, 560, GL_LUMINANCE_ALPHA, true);
			this.searchTexture = lookup(resourceBytes("SearchTex.bin"), 64, 16, GL_LUMINANCE, false);
			this.available = true;
			System.out.println("client.smaa: SMAA 1x on");
		} catch (IOException ex) {
			System.err.println("client.smaa: the SMAA resources are missing; SMAA is off");
		}
	}

	private GlProgram program(String smaa, String vertexMain, String fragmentMain) {
		String header = "#version 130\n#define SMAA_GLSL_3\n#define SMAA_PRESET_HIGH\n#define SMAA_RT_METRICS rtMetrics\n#define SMAA_AREATEX_SELECT(sample) sample.ra\n#define SMAA_SEARCHTEX_SELECT(sample) sample.r\nuniform vec4 rtMetrics;\n";
		GlShader vertex = GlShader.compile(GL_VERTEX_SHADER, this.toolkit, header + "#define SMAA_INCLUDE_VS 1\n#define SMAA_INCLUDE_PS 0\n" + smaa + "\n" + vertexMain);
		GlShader fragment = GlShader.compile(GL_FRAGMENT_SHADER, this.toolkit, header + "#define SMAA_INCLUDE_VS 0\n#define SMAA_INCLUDE_PS 1\n" + smaa + "\n" + fragmentMain);
		if (vertex == null || fragment == null) {
			return null;
		}
		return GlProgram.link(this.toolkit, new GlShader[] { vertex, fragment });
	}

	private OpenGl2DTexture lookup(byte[] data, int width, int height, int format, boolean linear) {
		OpenGl2DTexture texture = new OpenGl2DTexture(this.toolkit, OpenGLConstants.GL_TEXTURE_2D, format, width, height);
		OpenGL.glPixelStorei(3317, 1);
		OpenGL.glTexSubImage2Dub(OpenGLConstants.GL_TEXTURE_2D, 0, 0, 0, width, height, format, OpenGLConstants.GL_UNSIGNED_BYTE, data, 0);
		OpenGL.glPixelStorei(3317, 4);
		texture.setLinearFilter(linear);
		clamp(texture);
		return texture;
	}

	private void clamp(OpenGl2DTexture texture) {
		this.toolkit.setActiveTexture(texture);
		OpenGL.glTexParameteri(OpenGLConstants.GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		OpenGL.glTexParameteri(OpenGLConstants.GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	}

	private OpenGl2DTexture target(int width, int height) {
		OpenGl2DTexture texture = new OpenGl2DTexture(this.toolkit, OpenGLConstants.GL_TEXTURE_2D, OpenGLConstants.GL_RGBA, width, height);
		texture.setLinearFilter(true);
		clamp(texture);
		return texture;
	}

	private GlFramebuffer framebuffer(OpenGl2DTexture texture) {
		GlFramebuffer framebuffer = new GlFramebuffer(this.toolkit);
		framebuffer.bind();
		framebuffer.attachTexture(texture, 0);
		framebuffer.drawBuffer(0);
		return framebuffer;
	}

	// Makes the work textures fit the view; the caller binds its own framebuffers again after.
	private void resize(int width, int height) {
		if (this.width == width && this.height == height) {
			return;
		}
		this.release();
		this.width = width;
		this.height = height;
		this.edges = this.target(width, height);
		this.weights = this.target(width, height);
		this.output = this.target(width, height);
		this.edgesTarget = this.framebuffer(this.edges);
		this.weightsTarget = this.framebuffer(this.weights);
		this.outputTarget = this.framebuffer(this.output);
	}

	public void release() {
		if (this.edgesTarget == null) {
			return;
		}
		GlFramebuffer[] targets = new GlFramebuffer[] { this.edgesTarget, this.weightsTarget, this.outputTarget };
		for (int i = 0; i < targets.length; i++) {
			targets[i].bind();
			targets[i].clearAttachment(0);
		}
		this.edges.delete();
		this.weights.delete();
		this.output.delete();
		this.edgesTarget = this.weightsTarget = this.outputTarget = null;
		this.width = this.height = 0;
	}

	/**
	 * Runs the three passes over the scene texture, into the output texture. The toolkit is in its
	 * 2D sprite state, with the scissor off; the projection and viewport are saved and put back, and
	 * texture units 1 and 2 left empty.
	 */
	public void apply(OpenGl2DTexture scene, int width, int height) {
		this.resize(width, height);
		OpenGL.glPushAttrib(OpenGLConstants.GL_VIEWPORT_BIT);
		OpenGL.glMatrixMode(OpenGLConstants.GL_PROJECTION);
		OpenGL.glPushMatrix();
		OpenGL.glLoadIdentity();
		OpenGL.glOrtho(0.0D, 1.0D, 0.0D, 1.0D, -1.0D, 1.0D);
		OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW);
		OpenGL.glViewport(0, 0, width, height);

		this.toolkit.pushTarget(this.edgesTarget);
		OpenGL.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
		OpenGL.glClear(OpenGLConstants.GL_COLOR_BUFFER_BIT);
		this.use(this.edgeProgram);
		this.bind(0, scene, this.edgeProgram, "colorTex");
		quad();
		this.toolkit.popTarget(this.edgesTarget);

		this.toolkit.pushTarget(this.weightsTarget);
		OpenGL.glClear(OpenGLConstants.GL_COLOR_BUFFER_BIT);
		this.use(this.blendProgram);
		this.bind(0, this.edges, this.blendProgram, "edgesTex");
		this.bind(1, this.areaTexture, this.blendProgram, "areaTex");
		this.bind(2, this.searchTexture, this.blendProgram, "searchTex");
		quad();
		this.toolkit.popTarget(this.weightsTarget);

		this.toolkit.pushTarget(this.outputTarget);
		this.use(this.neighbourhoodProgram);
		this.bind(0, scene, this.neighbourhoodProgram, "colorTex");
		this.bind(1, this.weights, this.neighbourhoodProgram, "blendTex");
		quad();
		this.toolkit.popTarget(this.outputTarget);

		OpenGL.glUseProgramObjectARB(0L);
		this.toolkit.activateTexture(2);
		this.toolkit.setActiveTexture(null);
		this.toolkit.activateTexture(1);
		this.toolkit.setActiveTexture(null);
		this.toolkit.activateTexture(0);
		OpenGL.glMatrixMode(OpenGLConstants.GL_PROJECTION);
		OpenGL.glPopMatrix();
		OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW);
		OpenGL.glPopAttrib();
	}

	private void use(GlProgram program) {
		OpenGL.glUseProgramObjectARB(program.programHandle);
		OpenGL.glUniform4fARB(OpenGL.glGetUniformLocationARB(program.programHandle, "rtMetrics"), 1.0F / (float) this.width, 1.0F / (float) this.height, (float) this.width, (float) this.height);
	}

	private void bind(int unit, OpenGl2DTexture texture, GlProgram program, String name) {
		this.toolkit.activateTexture(unit);
		this.toolkit.setActiveTexture(texture);
		OpenGL.glUniform1iARB(OpenGL.glGetUniformLocationARB(program.programHandle, name), unit);
	}

	private static void quad() {
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

	private static String resource(String name) throws IOException {
		return new String(resourceBytes(name), "ISO-8859-1");
	}

	private static byte[] resourceBytes(String name) throws IOException {
		InputStream in = GlSmaaFilter.class.getResourceAsStream("/smaa/" + name);
		if (in == null) {
			throw new IOException(name);
		}
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buffer = new byte[8192];
			int read;
			while ((read = in.read(buffer)) > 0) {
				out.write(buffer, 0, read);
			}
			return out.toByteArray();
		} finally {
			in.close();
		}
	}
}

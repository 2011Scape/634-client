/*
 * Not part of the original client: 634HD (toolkit 6), copied from GlSmaaFilter (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd

import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.graphics.hd.gl.HdGl
import com.jagex.game.runetek5.graphics.hd.gl.HdContext
import io.IOException
import io.InputStream
import kotlin.jvm.JvmField
import lang.jClass
import smaaGlslVersion

/**
 * Not part of the original client: SMAA 1x (Jimenez et al., github.com/iryoku/smaa, MIT licence;
 * the shader and lookup textures are in the smaa resources) over the HdContext 3D view with
 * client.smaa. Luma edge detection, blending weights and neighbourhood blending run on the resolved
 * view, at the high preset, into textures of the view's size; the last one is what is drawn into the
 * frame. A driver that cannot compile the shaders leaves the filter off.
 */
class HdSmaaFilter {

	private val toolkit: HdToolkit?
	private var edgeProgram: HdProgram? = null
	private var blendProgram: HdProgram? = null
	private var neighbourhoodProgram: HdProgram? = null
	private var areaTexture: Hd2DTexture? = null
	private var searchTexture: Hd2DTexture? = null
	@JvmField
	var available: Boolean = false

	private var width: Int = 0
	private var height: Int = 0
	private var edges: Hd2DTexture? = null
	private var weights: Hd2DTexture? = null
	@JvmField
	var output: Hd2DTexture? = null
	private var edgesTarget: HdFramebuffer? = null
	private var weightsTarget: HdFramebuffer? = null
	private var outputTarget: HdFramebuffer? = null

	constructor(toolkit: HdToolkit?) {
		this.toolkit = toolkit
		try {
			val smaa: String = resource("SMAA.hlsl")
			this.edgeProgram = program(smaa,
				"out vec2 vTexcoord;\nout vec4 vOffset[3];\nvoid main() {\n\tvTexcoord = gl_MultiTexCoord0.xy;\n\tSMAAEdgeDetectionVS(vTexcoord, vOffset);\n\tgl_Position = ftransform();\n}\n",
				"in vec2 vTexcoord;\nin vec4 vOffset[3];\nuniform sampler2D colorTex;\nvoid main() {\n\tgl_FragColor = vec4(SMAALumaEdgeDetectionPS(vTexcoord, vOffset, colorTex), 0.0, 0.0);\n}\n")
			this.blendProgram = program(smaa,
				"out vec2 vTexcoord;\nout vec2 vPixcoord;\nout vec4 vOffset[3];\nvoid main() {\n\tvTexcoord = gl_MultiTexCoord0.xy;\n\tSMAABlendingWeightCalculationVS(vTexcoord, vPixcoord, vOffset);\n\tgl_Position = ftransform();\n}\n",
				"in vec2 vTexcoord;\nin vec2 vPixcoord;\nin vec4 vOffset[3];\nuniform sampler2D edgesTex;\nuniform sampler2D areaTex;\nuniform sampler2D searchTex;\nvoid main() {\n\tgl_FragColor = SMAABlendingWeightCalculationPS(vTexcoord, vPixcoord, vOffset, edgesTex, areaTex, searchTex, vec4(0.0));\n}\n")
			this.neighbourhoodProgram = program(smaa,
				"out vec2 vTexcoord;\nout vec4 vOffset;\nvoid main() {\n\tvTexcoord = gl_MultiTexCoord0.xy;\n\tSMAANeighborhoodBlendingVS(vTexcoord, vOffset);\n\tgl_Position = ftransform();\n}\n",
				"in vec2 vTexcoord;\nin vec4 vOffset;\nuniform sampler2D colorTex;\nuniform sampler2D blendTex;\nvoid main() {\n\tgl_FragColor = SMAANeighborhoodBlendingPS(vTexcoord, vOffset, colorTex, blendTex);\n}\n")
			if (this.edgeProgram == null || this.blendProgram == null || this.neighbourhoodProgram == null) {
				lang.printlnErr("client.smaa: this HdContext driver cannot compile the SMAA shaders; SMAA is off")
				return
			}
			this.areaTexture = lookup(resourceBytes("AreaTex.bin"), 160, 560, GL_LUMINANCE_ALPHA, true)
			this.searchTexture = lookup(resourceBytes("SearchTex.bin"), 64, 16, GL_LUMINANCE, false)
			this.available = true
			println("client.smaa: SMAA 1x on")
		} catch (ex: IOException) {
			lang.printlnErr("client.smaa: the SMAA resources are missing; SMAA is off")
		}
	}

	private fun program(smaa: String, vertexMain: String, fragmentMain: String): HdProgram? {
		// the version line is "#version 130" on the JVM, "#version 120" in a GL 2.1 context (macOS), and
		// none in a browser (PlatformLoop.kt)
		val version: String = smaaGlslVersion
		val legacy: Boolean = version.startsWith("#version 120")
		val language: String = if (legacy) GLSL_120 else "#define SMAA_GLSL_3\n"
		// the logo in SMAA.hlsl's licence comment ends lines in backslashes, a line continuation that
		// GLSL before 4.20 does not have and a strict compiler rejects
		val source: String = if (legacy) smaa.replace("\\\n", "\n") else smaa
		val header: String = version + language + "#define SMAA_PRESET_HIGH\n#define SMAA_RT_METRICS rtMetrics\n#define SMAA_AREATEX_SELECT(sample) sample.ra\n#define SMAA_SEARCHTEX_SELECT(sample) sample.r\nuniform vec4 rtMetrics;\n"
		val vertex: HdShader? = HdShader.compile(GL_VERTEX_SHADER, this.toolkit, header + "#define SMAA_INCLUDE_VS 1\n#define SMAA_INCLUDE_PS 0\n" + source + "\n" + if (legacy) varyings(vertexMain, "out ") else vertexMain)
		val fragment: HdShader? = HdShader.compile(GL_FRAGMENT_SHADER, this.toolkit, header + "#define SMAA_INCLUDE_VS 0\n#define SMAA_INCLUDE_PS 1\n" + source + "\n" + if (legacy) varyings(fragmentMain, "in ") else fragmentMain)
		if (vertex == null || fragment == null) {
			return null
		}
		return HdProgram.link(this.toolkit, arrayOf<HdShader?>(vertex, fragment))
	}

	// GLSL 1.20 has no in/out variables: the mains' declarations that start a line become varyings.
	private fun varyings(main: String, qualifier: String): String {
		val lines: List<String> = main.split("\n")
		val out: StringBuilder = StringBuilder()
		for (i in 0 until lines.size) {
			val line: String = lines[i]
			if (line.startsWith(qualifier)) {
				out.append("varying ").append(line.substring(qualifier.length))
			} else {
				out.append(line)
			}
			if (i < lines.size - 1) {
				out.append('\n')
			}
		}
		return out.toString()
	}

	private fun lookup(data: ByteArray, width: Int, height: Int, format: Int, linear: Boolean): Hd2DTexture {
		val texture: Hd2DTexture = Hd2DTexture(this.toolkit, OpenGLConstants.GL_TEXTURE_2D, format, width, height)
		HdGl.glPixelStorei(3317, 1)
		HdGl.glTexSubImage2Dub(OpenGLConstants.GL_TEXTURE_2D, 0, 0, 0, width, height, format, OpenGLConstants.GL_UNSIGNED_BYTE, data, 0)
		HdGl.glPixelStorei(3317, 4)
		texture.setLinearFilter(linear)
		clamp(texture)
		return texture
	}

	private fun clamp(texture: Hd2DTexture) {
		this.toolkit!!.setActiveTexture(texture)
		HdGl.glTexParameteri(OpenGLConstants.GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
		HdGl.glTexParameteri(OpenGLConstants.GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
	}

	private fun target(width: Int, height: Int): Hd2DTexture {
		val texture: Hd2DTexture = Hd2DTexture(this.toolkit, OpenGLConstants.GL_TEXTURE_2D, OpenGLConstants.GL_RGBA, width, height)
		texture.setLinearFilter(true)
		clamp(texture)
		return texture
	}

	private fun framebuffer(texture: Hd2DTexture?): HdFramebuffer {
		val framebuffer: HdFramebuffer = HdFramebuffer(this.toolkit)
		framebuffer.bind()
		framebuffer.attachTexture(texture, 0)
		framebuffer.drawBuffer(0)
		return framebuffer
	}

	// Makes the work textures fit the view; the caller binds its own framebuffers again after.
	private fun resize(width: Int, height: Int) {
		if (this.width == width && this.height == height) {
			return
		}
		this.release()
		this.width = width
		this.height = height
		this.edges = this.target(width, height)
		this.weights = this.target(width, height)
		this.output = this.target(width, height)
		this.edgesTarget = this.framebuffer(this.edges)
		this.weightsTarget = this.framebuffer(this.weights)
		this.outputTarget = this.framebuffer(this.output)
	}

	fun release() {
		if (this.edgesTarget == null) {
			return
		}
		val targets: Array<HdFramebuffer?> = arrayOf<HdFramebuffer?>(this.edgesTarget, this.weightsTarget, this.outputTarget)
		for (i in 0 until targets.size) {
			targets[i]!!.bind()
			targets[i]!!.clearAttachment(0)
		}
		this.edges!!.delete()
		this.weights!!.delete()
		this.output!!.delete()
		this.outputTarget = null
		this.weightsTarget = null
		this.edgesTarget = null
		this.height = 0
		this.width = 0
	}

	/**
	 * Runs the three passes over the scene texture, into the output texture. The toolkit is in its
	 * 2D sprite state, with the scissor off; the projection and viewport are saved and put back, and
	 * texture units 1 and 2 left empty.
	 */
	fun apply(scene: Hd2DTexture?, width: Int, height: Int) {
		this.resize(width, height)
		HdGl.glPushAttrib(OpenGLConstants.GL_VIEWPORT_BIT)
		HdGl.glMatrixMode(OpenGLConstants.GL_PROJECTION)
		HdGl.glPushMatrix()
		HdGl.glLoadIdentity()
		HdGl.glOrtho(0.0, 1.0, 0.0, 1.0, -1.0, 1.0)
		HdGl.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
		HdGl.glViewport(0, 0, width, height)

		this.toolkit!!.pushTarget(this.edgesTarget)
		HdGl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
		HdGl.glClear(OpenGLConstants.GL_COLOR_BUFFER_BIT)
		this.use(this.edgeProgram)
		this.bind(0, scene, this.edgeProgram, "colorTex")
		quad()
		this.toolkit.popTarget(this.edgesTarget)

		this.toolkit.pushTarget(this.weightsTarget)
		HdGl.glClear(OpenGLConstants.GL_COLOR_BUFFER_BIT)
		this.use(this.blendProgram)
		this.bind(0, this.edges, this.blendProgram, "edgesTex")
		this.bind(1, this.areaTexture, this.blendProgram, "areaTex")
		this.bind(2, this.searchTexture, this.blendProgram, "searchTex")
		quad()
		this.toolkit.popTarget(this.weightsTarget)

		this.toolkit.pushTarget(this.outputTarget)
		this.use(this.neighbourhoodProgram)
		this.bind(0, scene, this.neighbourhoodProgram, "colorTex")
		this.bind(1, this.weights, this.neighbourhoodProgram, "blendTex")
		quad()
		this.toolkit.popTarget(this.outputTarget)

		HdGl.glUseProgramObjectARB(0L)
		this.toolkit.activateTexture(2)
		this.toolkit.setActiveTexture(null)
		this.toolkit.activateTexture(1)
		this.toolkit.setActiveTexture(null)
		this.toolkit.activateTexture(0)
		HdGl.glMatrixMode(OpenGLConstants.GL_PROJECTION)
		HdGl.glPopMatrix()
		HdGl.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
		HdGl.glPopAttrib()
	}

	private fun use(program: HdProgram?) {
		HdGl.glUseProgramObjectARB(program!!.programHandle)
		HdGl.glUniform4fARB(HdGl.glGetUniformLocationARB(program.programHandle, "rtMetrics"), 1.0f / this.width.toFloat(), 1.0f / this.height.toFloat(), this.width.toFloat(), this.height.toFloat())
	}

	private fun bind(unit: Int, texture: Hd2DTexture?, program: HdProgram?, name: String) {
		this.toolkit!!.activateTexture(unit)
		this.toolkit.setActiveTexture(texture)
		HdGl.glUniform1iARB(HdGl.glGetUniformLocationARB(program!!.programHandle, name), unit)
	}

	companion object {
		private const val GL_LUMINANCE: Int = 6409
		private const val GL_LUMINANCE_ALPHA: Int = 6410
		private const val GL_TEXTURE_WRAP_S: Int = 10242
		private const val GL_TEXTURE_WRAP_T: Int = 10243
		private const val GL_CLAMP_TO_EDGE: Int = 33071
		private const val GL_VERTEX_SHADER: Int = 35633
		private const val GL_FRAGMENT_SHADER: Int = 35632

		/*
		 * SMAA.hlsl's GLSL 3 language macros, for GLSL 1.20 (a macOS legacy context): texture2D, and
		 * texture2DLod from ARB_shader_texture_lod, where the offset is in pixels (SMAA_RT_METRICS);
		 * round() came in 1.30.
		 */
		private const val GLSL_120: String = "#extension GL_ARB_shader_texture_lod : require\n" +
			"#define SMAA_CUSTOM_SL\n" +
			"#define SMAATexture2D(tex) sampler2D tex\n" +
			"#define SMAATexturePass2D(tex) tex\n" +
			"#define SMAASampleLevelZero(tex, coord) texture2DLod(tex, coord, 0.0)\n" +
			"#define SMAASampleLevelZeroPoint(tex, coord) texture2DLod(tex, coord, 0.0)\n" +
			"#define SMAASampleLevelZeroOffset(tex, coord, offset) texture2DLod(tex, coord + vec2(offset) * SMAA_RT_METRICS.xy, 0.0)\n" +
			"#define SMAASample(tex, coord) texture2D(tex, coord)\n" +
			"#define SMAASamplePoint(tex, coord) texture2D(tex, coord)\n" +
			"#define SMAASampleOffset(tex, coord, offset) texture2D(tex, coord + vec2(offset) * SMAA_RT_METRICS.xy)\n" +
			"#define SMAA_FLATTEN\n" +
			"#define SMAA_BRANCH\n" +
			"#define lerp(a, b, t) mix(a, b, t)\n" +
			"#define saturate(a) clamp(a, 0.0, 1.0)\n" +
			"#define mad(a, b, c) (a * b + c)\n" +
			"#define round(a) floor((a) + 0.5)\n" +
			"#define float2 vec2\n" +
			"#define float3 vec3\n" +
			"#define float4 vec4\n" +
			"#define int2 ivec2\n" +
			"#define int3 ivec3\n" +
			"#define int4 ivec4\n" +
			"#define bool2 bvec2\n" +
			"#define bool3 bvec3\n" +
			"#define bool4 bvec4\n"

		private fun quad() {
			HdGl.glBegin(OpenGLConstants.GL_QUADS)
			HdGl.glTexCoord2f(0.0f, 0.0f)
			HdGl.glVertex2i(0, 0)
			HdGl.glTexCoord2f(1.0f, 0.0f)
			HdGl.glVertex2i(1, 0)
			HdGl.glTexCoord2f(1.0f, 1.0f)
			HdGl.glVertex2i(1, 1)
			HdGl.glTexCoord2f(0.0f, 1.0f)
			HdGl.glVertex2i(0, 1)
			HdGl.glEnd()
		}

		@Throws(IOException::class)
		private fun resource(name: String): String {
			val bytes: ByteArray = resourceBytes(name)
			val chars: CharArray = CharArray(bytes.size)
			for (i in 0 until bytes.size) {
				chars[i] = (bytes[i].toInt() and 0xFF).toChar()
			}
			return chars.concatToString()
		}

		@Throws(IOException::class)
		private fun resourceBytes(name: String): ByteArray {
			return lang.readResource("/smaa/" + name) ?: throw IOException(name)
		}
	}
}

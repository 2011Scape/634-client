package com.jagex.game.runetek5.graphics.gl

import com.jagex.game.runetek5.GlShader
import com.jagex.game.runetek5.core.constants.OpenGLConstants
import jaggl.OpenGL
import java.io.ByteArrayOutputStream
import io.IOException
import io.InputStream
import kotlin.jvm.JvmField
import lang.jClass

/**
 * Not part of the original client: SMAA 1x (Jimenez et al., github.com/iryoku/smaa, MIT licence;
 * the shader and lookup textures are in the smaa resources) over the OpenGL 3D view with
 * client.smaa. Luma edge detection, blending weights and neighbourhood blending run on the resolved
 * view, at the high preset, into textures of the view's size; the last one is what is drawn into the
 * frame. A driver that cannot compile the shaders leaves the filter off.
 */
class GlSmaaFilter {

	private val toolkit: GlToolkit?
	private var edgeProgram: GlProgram? = null
	private var blendProgram: GlProgram? = null
	private var neighbourhoodProgram: GlProgram? = null
	private var areaTexture: OpenGl2DTexture? = null
	private var searchTexture: OpenGl2DTexture? = null
	@JvmField
	var available: Boolean = false

	private var width: Int = 0
	private var height: Int = 0
	private var edges: OpenGl2DTexture? = null
	private var weights: OpenGl2DTexture? = null
	@JvmField
	var output: OpenGl2DTexture? = null
	private var edgesTarget: GlFramebuffer? = null
	private var weightsTarget: GlFramebuffer? = null
	private var outputTarget: GlFramebuffer? = null

	constructor(toolkit: GlToolkit?) {
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
				lang.printlnErr("client.smaa: this OpenGL driver cannot compile the SMAA shaders (GLSL 1.30); SMAA is off")
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

	private fun program(smaa: String, vertexMain: String, fragmentMain: String): GlProgram? {
		val header: String = "#version 130\n#define SMAA_GLSL_3\n#define SMAA_PRESET_HIGH\n#define SMAA_RT_METRICS rtMetrics\n#define SMAA_AREATEX_SELECT(sample) sample.ra\n#define SMAA_SEARCHTEX_SELECT(sample) sample.r\nuniform vec4 rtMetrics;\n"
		val vertex: GlShader? = GlShader.compile(GL_VERTEX_SHADER, this.toolkit, header + "#define SMAA_INCLUDE_VS 1\n#define SMAA_INCLUDE_PS 0\n" + smaa + "\n" + vertexMain)
		val fragment: GlShader? = GlShader.compile(GL_FRAGMENT_SHADER, this.toolkit, header + "#define SMAA_INCLUDE_VS 0\n#define SMAA_INCLUDE_PS 1\n" + smaa + "\n" + fragmentMain)
		if (vertex == null || fragment == null) {
			return null
		}
		return GlProgram.link(this.toolkit, arrayOf<GlShader?>(vertex, fragment))
	}

	private fun lookup(data: ByteArray, width: Int, height: Int, format: Int, linear: Boolean): OpenGl2DTexture {
		val texture: OpenGl2DTexture = OpenGl2DTexture(this.toolkit, OpenGLConstants.GL_TEXTURE_2D, format, width, height)
		OpenGL.glPixelStorei(3317, 1)
		OpenGL.glTexSubImage2Dub(OpenGLConstants.GL_TEXTURE_2D, 0, 0, 0, width, height, format, OpenGLConstants.GL_UNSIGNED_BYTE, data, 0)
		OpenGL.glPixelStorei(3317, 4)
		texture.setLinearFilter(linear)
		clamp(texture)
		return texture
	}

	private fun clamp(texture: OpenGl2DTexture) {
		this.toolkit!!.setActiveTexture(texture)
		OpenGL.glTexParameteri(OpenGLConstants.GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
		OpenGL.glTexParameteri(OpenGLConstants.GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
	}

	private fun target(width: Int, height: Int): OpenGl2DTexture {
		val texture: OpenGl2DTexture = OpenGl2DTexture(this.toolkit, OpenGLConstants.GL_TEXTURE_2D, OpenGLConstants.GL_RGBA, width, height)
		texture.setLinearFilter(true)
		clamp(texture)
		return texture
	}

	private fun framebuffer(texture: OpenGl2DTexture?): GlFramebuffer {
		val framebuffer: GlFramebuffer = GlFramebuffer(this.toolkit)
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
		val targets: Array<GlFramebuffer?> = arrayOf<GlFramebuffer?>(this.edgesTarget, this.weightsTarget, this.outputTarget)
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
	fun apply(scene: OpenGl2DTexture?, width: Int, height: Int) {
		this.resize(width, height)
		OpenGL.glPushAttrib(OpenGLConstants.GL_VIEWPORT_BIT)
		OpenGL.glMatrixMode(OpenGLConstants.GL_PROJECTION)
		OpenGL.glPushMatrix()
		OpenGL.glLoadIdentity()
		OpenGL.glOrtho(0.0, 1.0, 0.0, 1.0, -1.0, 1.0)
		OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
		OpenGL.glViewport(0, 0, width, height)

		this.toolkit!!.pushTarget(this.edgesTarget)
		OpenGL.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
		OpenGL.glClear(OpenGLConstants.GL_COLOR_BUFFER_BIT)
		this.use(this.edgeProgram)
		this.bind(0, scene, this.edgeProgram, "colorTex")
		quad()
		this.toolkit.popTarget(this.edgesTarget)

		this.toolkit.pushTarget(this.weightsTarget)
		OpenGL.glClear(OpenGLConstants.GL_COLOR_BUFFER_BIT)
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

		OpenGL.glUseProgramObjectARB(0L)
		this.toolkit.activateTexture(2)
		this.toolkit.setActiveTexture(null)
		this.toolkit.activateTexture(1)
		this.toolkit.setActiveTexture(null)
		this.toolkit.activateTexture(0)
		OpenGL.glMatrixMode(OpenGLConstants.GL_PROJECTION)
		OpenGL.glPopMatrix()
		OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
		OpenGL.glPopAttrib()
	}

	private fun use(program: GlProgram?) {
		OpenGL.glUseProgramObjectARB(program!!.programHandle)
		OpenGL.glUniform4fARB(OpenGL.glGetUniformLocationARB(program.programHandle, "rtMetrics"), 1.0f / this.width.toFloat(), 1.0f / this.height.toFloat(), this.width.toFloat(), this.height.toFloat())
	}

	private fun bind(unit: Int, texture: OpenGl2DTexture?, program: GlProgram?, name: String) {
		this.toolkit!!.activateTexture(unit)
		this.toolkit.setActiveTexture(texture)
		OpenGL.glUniform1iARB(OpenGL.glGetUniformLocationARB(program!!.programHandle, name), unit)
	}

	companion object {
		private const val GL_LUMINANCE: Int = 6409
		private const val GL_LUMINANCE_ALPHA: Int = 6410
		private const val GL_TEXTURE_WRAP_S: Int = 10242
		private const val GL_TEXTURE_WRAP_T: Int = 10243
		private const val GL_CLAMP_TO_EDGE: Int = 33071
		private const val GL_VERTEX_SHADER: Int = 35633
		private const val GL_FRAGMENT_SHADER: Int = 35632

		private fun quad() {
			OpenGL.glBegin(OpenGLConstants.GL_QUADS)
			OpenGL.glTexCoord2f(0.0f, 0.0f)
			OpenGL.glVertex2i(0, 0)
			OpenGL.glTexCoord2f(1.0f, 0.0f)
			OpenGL.glVertex2i(1, 0)
			OpenGL.glTexCoord2f(1.0f, 1.0f)
			OpenGL.glVertex2i(1, 1)
			OpenGL.glTexCoord2f(0.0f, 1.0f)
			OpenGL.glVertex2i(0, 1)
			OpenGL.glEnd()
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

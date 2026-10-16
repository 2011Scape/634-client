/*
 * Not part of the original client: 634HD (toolkit 6). jaggl's OpenGL API (the calls the Java OpenGL
 * toolkit makes) on a GL 3.3 core context: fixed-function state, immediate mode, the matrix stacks,
 * display lists and ARB vertex programs are emulated with GLSL (/634hd/shaders/ff.vert, ff.frag and
 * transpiled programs). Ported from the browser's WebGL 2 emulator (src/jsMain/kotlin/jaggl/
 * OpenGL.js.kt); what only the browser needs (interface scale, canvas alpha, typed arrays) is gone, and
 * the formats a core context lacks (luminance, alpha, intensity) become R8/RG8 with a swizzle.
 */
// The warnings of the original (deobfuscated) code, left as it was: the build treats warnings as errors.
@file:Suppress("SENSELESS_COMPARISON")

package com.jagex.game.runetek5.graphics.hd.gl

import jaggl.GlScratch
import java.util.Hashtable
import org.lwjgl.opengl.GL33C

const val GL_TEXTURE_1D = 3552
const val GL_TEXTURE_3D = 32879
const val GL_MULTISAMPLE = 32925

/** Extensions a 3.3 core context has in its core, as the toolkit asks for them. */
private val CORE_EXTENSIONS = setOf(
    "GL_ARB_multitexture", "GL_ARB_texture_env_combine", "GL_ARB_vertex_buffer_object",
    "GL_ARB_vertex_shader", "GL_ARB_fragment_shader", "GL_ARB_texture_cube_map",
    "GL_EXT_texture3D", "GL_EXT_framebuffer_object", "GL_EXT_framebuffer_blit",
    "GL_EXT_framebuffer_multisample", "GL_ARB_multisample", "GL_ARB_texture_float",
    "GL_ARB_half_float_pixel", "GL_ARB_shader_objects", "GL_ARB_shading_language_100",
    // emulated: glProgramStringARB transpiles ARBvp1.0 assembly to GLSL (ArbVertexProgramTranspiler)
    "GL_ARB_vertex_program",
)

/** Not emulated: the toolkit falls back as on hardware without them. */
private val UNSUPPORTED_EXTENSIONS = setOf(
    "GL_ARB_fragment_program", "GL_ARB_texture_rectangle",
)

private const val GL_VERTEX_PROGRAM_ARB = 34336
private const val GL_FRAGMENT_PROGRAM_ARB = 34820
private const val GL_PROGRAM_ERROR_POSITION_ARB = 34379
private const val GL_PROGRAM_FORMAT_ASCII_ARB = 34933

private fun fixTarget(target: Int): Int =
    if (target == GL_TEXTURE_1D) CoreGl.TEXTURE_2D else target

private fun IntArray?.slice(offset: Int, count: Int): IntArray {
    val src = this ?: return IntArray(count)
    return IntArray(count) { src[offset + it] }
}

private fun FloatArray?.slice(offset: Int, count: Int): FloatArray {
    val src = this ?: return FloatArray(count)
    return FloatArray(count) { src[offset + it] }
}

object HdGl {
    /** Whether [extension] is available, for HdContext.a(String). */
    fun hasExtension(extension: String?): Boolean {
        if (extension == null) return false
        if (HdContext.FORCED_FAILURE == "extension") return false
        if (extension in CORE_EXTENSIONS) return true
        if (extension in UNSUPPORTED_EXTENSIONS) return false
        val count = GL33C.glGetInteger(GL33C.GL_NUM_EXTENSIONS)
        for (i in 0 until count) {
            if (extension == GL33C.glGetStringi(GL33C.GL_EXTENSIONS, i)) return true
        }
        return false
    }

    /** One vertex array object, bound for good: a core context draws nothing without one. */
    internal fun createState(): GlState {
        val vao = CoreGl.createVertexArray()
        CoreGl.bindVertexArray(vao)
        val newState = GlState(CoreGl)
        newState.vertexArray = vao
        newState.immediateMode = ImmediateModeEmulator(CoreGl, newState)
        newState.fixedFunctionShader = FixedFunctionShader(CoreGl)
        return newState
    }

    internal fun resetWindowState(width: Int, height: Int) {
        attribStack.clear()
        viewportX = 0
        viewportY = 0
        viewportW = width
        viewportH = height
        scissorX = 0
        scissorY = 0
        scissorW = width
        scissorH = height
        readFramebuffer = null
        drawFramebuffer = null
    }

    // ---- core-profile formats ----

    /** The sized format a core context takes in place of a legacy internal format. */
    private fun coreInternalFormat(format: Int): Int = when (format) {
        1, GL_ALPHA, GL_LUMINANCE, GL_INTENSITY, GL_ALPHA8, GL_LUMINANCE8, GL_INTENSITY8 -> CoreGl.R8
        2, GL_LUMINANCE_ALPHA, GL_LUMINANCE8_ALPHA8 -> CoreGl.RG8
        3 -> CoreGl.RGB8
        4 -> CoreGl.RGBA8
        else -> format
    }

    /** The pixel format a core context takes in place of a legacy one. */
    private fun coreFormat(format: Int): Int = when (format) {
        GL_ALPHA, GL_LUMINANCE, GL_INTENSITY -> CoreGl.RED
        GL_LUMINANCE_ALPHA -> CoreGl.RG
        else -> format
    }

    /** Makes an R8/RG8 texture read back as the legacy format did; resets it for any other format. */
    private fun applySwizzle(face: Int, internalFormat: Int) {
        // a cube map's faces share the texture's swizzle, set on the cube map target (the specular
        // maps of shiny metal are GL_ALPHA cube maps: without it their alpha reads 1, all highlight)
        val target = if (face >= GL33C.GL_TEXTURE_CUBE_MAP_POSITIVE_X && face <= GL33C.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z) GL33C.GL_TEXTURE_CUBE_MAP else face
        if (target != CoreGl.TEXTURE_2D && target != GL_TEXTURE_3D && target != GL33C.GL_TEXTURE_CUBE_MAP) return
        val r = GL33C.GL_RED
        val g = GL33C.GL_GREEN
        val one = GL33C.GL_ONE
        val zero = GL33C.GL_ZERO
        when (internalFormat) {
            GL_ALPHA, GL_ALPHA8 -> swizzle(target, zero, zero, zero, r)
            1, GL_LUMINANCE, GL_LUMINANCE8 -> swizzle(target, r, r, r, one)
            GL_INTENSITY, GL_INTENSITY8 -> swizzle(target, r, r, r, r)
            2, GL_LUMINANCE_ALPHA, GL_LUMINANCE8_ALPHA8 -> swizzle(target, r, r, r, g)
            else -> swizzle(target, r, g, GL33C.GL_BLUE, GL33C.GL_ALPHA)
        }
    }

    private fun swizzle(target: Int, r: Int, g: Int, b: Int, a: Int) {
        GL33C.glTexParameteri(target, GL33C.GL_TEXTURE_SWIZZLE_R, r)
        GL33C.glTexParameteri(target, GL33C.GL_TEXTURE_SWIZZLE_G, g)
        GL33C.glTexParameteri(target, GL33C.GL_TEXTURE_SWIZZLE_B, b)
        GL33C.glTexParameteri(target, GL33C.GL_TEXTURE_SWIZZLE_A, a)
    }

    private fun bytesPerPixel(format: Int, type: Int): Int {
        if (type == GL_UNSIGNED_INT_8_8_8_8_REV || type == GL_UNSIGNED_INT_8_8_8_8) return 4
        val components = when (format) {
            GL_RGBA, GL_BGRA -> 4
            GL_RGB, GL_BGR -> 3
            GL_LUMINANCE_ALPHA, CoreGl.RG -> 2
            else -> 1
        }
        val size = when (type) {
            GL33C.GL_FLOAT, GL33C.GL_UNSIGNED_INT, GL33C.GL_INT -> 4
            GL33C.GL_UNSIGNED_SHORT, GL33C.GL_SHORT, GL33C.GL_HALF_FLOAT -> 2
            else -> 1
        }
        return components * size
    }

    /** Bytes GL reads for an image, rows padded to the unpack alignment (4 unless the client set 1). */
    private fun imageBytes(width: Int, height: Int, depth: Int, format: Int, type: Int): Int {
        val row = width * bytesPerPixel(format, type)
        val aligned = (row + unpackAlignment - 1) / unpackAlignment * unpackAlignment
        return aligned * height * depth
    }

    private var unpackAlignment = 4

    private const val GL_INTENSITY = 32841
    private const val GL_ALPHA8 = 32828
    private const val GL_LUMINANCE8 = 32832
    private const val GL_LUMINANCE8_ALPHA8 = 32837
    private const val GL_INTENSITY8 = 32843
    private const val GL_BGR = 32992
    private const val GL_CLAMP = 10496
    private const val GL_GENERATE_MIPMAP = 33169

    /** Caps a core context has; the fixed-function ones are emulated or have no meaning there. */
    private fun isCoreCap(cap: Int): Boolean = when (cap) {
        3042, 2929, 2884, 3089, 2960, 32823, 10754, 10753, 2848, 2881, 32926, 32927, 32928, 3024, 3058,
        34383, 34895, 34370, 36281, 36765, 35977 -> true
        else -> false
    }

    lateinit var state: GlState

    private val gl: CoreGl get() = CoreGl

    private const val GL_VIEWPORT_BIT = 0x800

    /**
     * Mirror of the current GL viewport, kept for [glPushAttrib]/[glPopAttrib]. Like the
     * scissor box below it is in the client's logical pixels: see [windowScale].
     */
    internal var viewportX = 0
        private set
    internal var viewportY = 0
        private set
    internal var viewportW = 0
        private set
    internal var viewportH = 0
        private set
    private var scissorX = 0
    private var scissorY = 0
    private var scissorW = 0
    private var scissorH = 0

    /** Framebuffers bound to the read and draw targets; null is the window. */
    private var readFramebuffer: WebGLFramebuffer? = null
    private var drawFramebuffer: WebGLFramebuffer? = null

    /**
     * 634HD's whole-number interface scale (HdSettings.interfaceScale): the window is [windowScale]
     * times the client's size, and everything drawn to it - the interface pixel-snapped and sampled
     * at texel centres (ImmediateModeEmulator), the 3D view at the window's own resolution - is
     * scaled here. Off-screen framebuffers keep the client's units.
     */
    @JvmField
    var windowScale: Int = 1

    internal val drawScale: Int get() = if (drawFramebuffer == null) windowScale else 1
    private val readScale: Int get() = if (readFramebuffer == null) windowScale else 1

    private fun applyViewport() {
        val n = drawScale
        gl.viewport(viewportX * n, viewportY * n, viewportW * n, viewportH * n)
    }

    private fun applyScissor() {
        val n = drawScale
        gl.scissor(scissorX * n, scissorY * n, scissorW * n, scissorH * n)
    }
    private val attribStack = ArrayList<IntArray?>()

    @JvmField
    val b: Hashtable<Any?, Any?> = Hashtable()

    private inline fun exec(crossinline op: () -> Unit) {
        if (state.displayLists.isRecording) {
            state.displayLists.record { op() }
        } else {
            op()
        }
    }

    private const val GL_BGRA = 32993
    private const val GL_RGBA = 6408
    private fun fixBgraFormat(format: Int): Int = format

    private const val GL_UNSIGNED_INT_8_8_8_8_REV = 33639
    private fun fixPixelType(type: Int): Int = type

    private const val GL_RGB = 6407

    private const val GL_COLOR_BUFFER_BIT = 0x4000
    private const val GL_SCISSOR_TEST = 0x0C11
    private const val GL_COLOR_CLEAR_VALUE = 0x0C22
    private const val GL_COLOR_WRITEMASK = 0x0C23
    private const val GL_FRAMEBUFFER = 0x8D40
    private const val GL_FRAMEBUFFER_BINDING = 0x8CA6

    // ---- Immediate mode -------------------------------------------------

    fun glBegin(arg0: Int) = exec { state.immediateMode.begin(arg0) }
    fun glEnd() = exec { state.immediateMode.end() }
    fun glVertex2i(arg0: Int, arg1: Int) = exec { state.immediateMode.vertex(arg0.toFloat(), arg1.toFloat(), 0f) }
    fun glVertex2f(arg0: Float, arg1: Float) = exec { state.immediateMode.vertex(arg0, arg1, 0f) }
    fun glVertex3f(arg0: Float, arg1: Float, arg2: Float) = exec { state.immediateMode.vertex(arg0, arg1, arg2) }

    fun glColor3f(arg0: Float, arg1: Float, arg2: Float) = exec {
        state.currentColor[0] = arg0; state.currentColor[1] = arg1; state.currentColor[2] = arg2; state.currentColor[3] = 1f
    }

    fun glColor4f(arg0: Float, arg1: Float, arg2: Float, arg3: Float) = exec {
        state.currentColor[0] = arg0; state.currentColor[1] = arg1; state.currentColor[2] = arg2; state.currentColor[3] = arg3
    }

    fun glColor3ub(arg0: Byte, arg1: Byte, arg2: Byte) = exec {
        state.currentColor[0] = (arg0.toInt() and 0xFF) / 255f
        state.currentColor[1] = (arg1.toInt() and 0xFF) / 255f
        state.currentColor[2] = (arg2.toInt() and 0xFF) / 255f
        state.currentColor[3] = 1f
    }

    fun glColor4ub(arg0: Byte, arg1: Byte, arg2: Byte, arg3: Byte) = exec {
        state.currentColor[0] = (arg0.toInt() and 0xFF) / 255f
        state.currentColor[1] = (arg1.toInt() and 0xFF) / 255f
        state.currentColor[2] = (arg2.toInt() and 0xFF) / 255f
        state.currentColor[3] = (arg3.toInt() and 0xFF) / 255f
    }

    fun glTexCoord2f(arg0: Float, arg1: Float) = exec {
        state.currentTexCoord[0] = arg0; state.currentTexCoord[1] = arg1; state.currentTexCoord[2] = 0f
    }
    fun glTexCoord2i(arg0: Int, arg1: Int) = exec {
        state.currentTexCoord[0] = arg0.toFloat(); state.currentTexCoord[1] = arg1.toFloat()
        state.currentTexCoord[2] = 0f
    }
    fun glTexCoord3f(arg0: Float, arg1: Float, arg2: Float) = exec {
        state.currentTexCoord[0] = arg0; state.currentTexCoord[1] = arg1; state.currentTexCoord[2] = arg2
    }
    fun glTexCoord3i(arg0: Int, arg1: Int, arg2: Int) = exec {
        state.currentTexCoord[0] = arg0.toFloat(); state.currentTexCoord[1] = arg1.toFloat()
        state.currentTexCoord[2] = arg2.toFloat()
    }

    fun glNormal3f(arg0: Float, arg1: Float, arg2: Float) = exec {
        state.currentNormal[0] = arg0; state.currentNormal[1] = arg1; state.currentNormal[2] = arg2
    }

    fun glRasterPos2i(arg0: Int, arg1: Int) {
        // Only known call sites are the glCopyPixels(GL_COLOR) copy-to-self pattern below; no-op otherwise.
    }

    // ---- Matrix stack -----------------------------------------------------

    fun glMatrixMode(arg0: Int) = exec { state.matrixStack.mode = arg0 }
    fun glLoadIdentity() = exec { matrixWillChange(); state.matrixStack.loadIdentity() }
    // Array arguments are copied before exec throughout: see glTexEnvfv.
    fun glLoadMatrixf(arg0: FloatArray?, arg1: Int) {
        val m = arg0.slice(arg1, 16)
        exec { matrixWillChange(); state.matrixStack.loadMatrix(m) }
    }
    fun glMultMatrixf(arg0: FloatArray?, arg1: Int) {
        val m = arg0.slice(arg1, 16)
        exec { matrixWillChange(); state.matrixStack.mult(m) }
    }
    fun glPushMatrix() = exec { matrixWillChange(); state.matrixStack.push() }
    fun glPopMatrix() = exec { matrixWillChange(); state.matrixStack.pop() }
    fun glTranslatef(arg0: Float, arg1: Float, arg2: Float) = exec { matrixWillChange(); state.matrixStack.translate(arg0, arg1, arg2) }
    fun glScalef(arg0: Float, arg1: Float, arg2: Float) = exec { matrixWillChange(); state.matrixStack.scale(arg0, arg1, arg2) }
    fun glRotatef(arg0: Float, arg1: Float, arg2: Float, arg3: Float) = exec { matrixWillChange(); state.matrixStack.rotate(arg0, arg1, arg2, arg3) }
    fun glOrtho(arg0: Double, arg1: Double, arg2: Double, arg3: Double, arg4: Double, arg5: Double) = exec { matrixWillChange();
        state.matrixStack.ortho(arg0, arg1, arg2, arg3, arg4, arg5)
    }

    fun glFrustum(arg0: Double, arg1: Double, arg2: Double, arg3: Double, arg4: Double, arg5: Double) = exec { matrixWillChange();
        state.matrixStack.frustum(arg0, arg1, arg2, arg3, arg4, arg5)
    }

    // ---- Display lists ------------------------------------------------

    fun glGenLists(arg0: Int): Int = state.displayLists.genLists(arg0)
    fun glDeleteLists(arg0: Int, arg1: Int) = state.displayLists.deleteLists(arg0, arg1)
    fun glNewList(arg0: Int, arg1: Int) {
        state.displayLists.newList(arg0)
    }

    fun glEndList() {
        state.displayLists.endList()
    }

    fun glCallList(arg0: Int) = exec { state.displayLists.callList(arg0) }

    // ---- Core / fixed-function state ------------------------------------

    /** A pending immediate-mode batch draws before a projection or texture matrix changes under it. */
    private fun matrixWillChange() {
        if (CoreGl.pendingImmediate && state.matrixStack.mode != GL_MODELVIEW) state.immediateMode.flushPending()
    }

    /** Draws a pending immediate-mode batch now (before a buffer swap, or GL use outside the emulator). */
    fun flushImmediate() {
        if (CoreGl.pendingImmediate) state.immediateMode.flushPending()
    }

    private fun texturingUnitIndex(): Int? = when (state.activeTextureUnit) {
        0, 1, 2 -> state.activeTextureUnit
        else -> null
    }

    fun glEnable(arg0: Int) = exec {
        when (arg0) {
            GL_VERTEX_PROGRAM_ARB -> state.vertexProgramEnabled = true
            // Fragment-program assembly has no WebGL2 emulation; absorb the enable so
            // the caller's enable/disable pairing never produces INVALID_ENUM noise.
            GL_FRAGMENT_PROGRAM_ARB -> {}
            // The enable/disable/setter paths below only raise dirty flags on a real change:
            // the renderer re-issues identical state per model, and every dirty flag costs a
            // batch of uniform uploads on the next draw.
            GL_LIGHTING -> if (!state.lightingEnabled) { state.lightingEnabled = true; state.ffpLightingDirty = true }
            WebGL2RenderingContext.TEXTURE_2D, WebGL2RenderingContext.TEXTURE_CUBE_MAP,
            GL_TEXTURE_1D, GL_TEXTURE_3D,
                -> texturingUnitIndex()?.let {
                    val target = fixTarget(arg0)
                    if (!state.texturingEnabled[it] || state.textureTarget[it] != target) {
                        state.texturingEnabled[it] = true
                        state.textureTarget[it] = target
                        state.texEnvDirty[it] = true
                    }
                }
            GL_TEXTURE_GEN_S, GL_TEXTURE_GEN_T, GL_TEXTURE_GEN_R, GL_TEXTURE_GEN_Q ->
                texturingUnitIndex()?.let {
                    val enabled = state.texGenEnabled[it]
                    if (enabled[arg0 - GL_TEXTURE_GEN_S] != true) {
                        enabled[arg0 - GL_TEXTURE_GEN_S] = true
                        state.texGenDirty[it] = true
                    }
                }
            GL_FOG -> if (!state.fogEnabled) { state.fogEnabled = true; state.ffpStateDirty = true }
            GL_ALPHA_TEST -> if (!state.alphaTestEnabled) { state.alphaTestEnabled = true; state.ffpStateDirty = true }
            in GL_LIGHT0..GL_LIGHT7 -> (arg0 - GL_LIGHT0).let {
                if (it in 0 until MAX_LIGHTS && !state.lightEnabled[it]) {
                    state.lightEnabled[it] = true
                    state.lightDirtyMask = state.lightDirtyMask or (1 shl it)
                }
            }
            GL_COLOR_MATERIAL, GL_NORMALIZE, GL_MULTISAMPLE -> {
            }
            else -> if (isCoreCap(arg0)) gl.enable(arg0)
        }
    }

    fun glDisable(arg0: Int) = exec {
        when (arg0) {
            GL_VERTEX_PROGRAM_ARB -> state.vertexProgramEnabled = false
            GL_FRAGMENT_PROGRAM_ARB -> {}
            GL_LIGHTING -> if (state.lightingEnabled) { state.lightingEnabled = false; state.ffpLightingDirty = true }
            WebGL2RenderingContext.TEXTURE_2D, WebGL2RenderingContext.TEXTURE_CUBE_MAP,
            GL_TEXTURE_1D, GL_TEXTURE_3D,
                -> texturingUnitIndex()?.let {
                    if (state.texturingEnabled[it]) { state.texturingEnabled[it] = false; state.texEnvDirty[it] = true }
                }
            GL_TEXTURE_GEN_S, GL_TEXTURE_GEN_T, GL_TEXTURE_GEN_R, GL_TEXTURE_GEN_Q ->
                texturingUnitIndex()?.let {
                    val enabled = state.texGenEnabled[it]
                    if (enabled[arg0 - GL_TEXTURE_GEN_S] != false) {
                        enabled[arg0 - GL_TEXTURE_GEN_S] = false
                        state.texGenDirty[it] = true
                    }
                }
            GL_FOG -> if (state.fogEnabled) { state.fogEnabled = false; state.ffpStateDirty = true }
            GL_ALPHA_TEST -> if (state.alphaTestEnabled) { state.alphaTestEnabled = false; state.ffpStateDirty = true }
            in GL_LIGHT0..GL_LIGHT7 -> (arg0 - GL_LIGHT0).let {
                if (it in 0 until MAX_LIGHTS && state.lightEnabled[it]) {
                    state.lightEnabled[it] = false
                    state.lightDirtyMask = state.lightDirtyMask or (1 shl it)
                }
            }
            GL_COLOR_MATERIAL, GL_NORMALIZE, GL_MULTISAMPLE -> {
            }
            else -> if (isCoreCap(arg0)) gl.disable(arg0)
        }
    }

    fun glShadeModel(arg0: Int) {}
    fun glColorMaterial(arg0: Int, arg1: Int) {}

    fun glHint(arg0: Int, arg1: Int) {
        // GL_GENERATE_MIPMAP_HINT and the fixed-function hints are not in a core context
        if (arg0 == WebGL2RenderingContext.FRAGMENT_SHADER_DERIVATIVE_HINT) {
            gl.hint(arg0, arg1)
        }
    }

    fun glPointSize(arg0: Float) {}
    /** Current glLineWidth, for [ImmediateModeEmulator]'s scaled lines (WebGL caps the real one at 1). */
    internal var lineWidth = 1f
        private set

    fun glLineWidth(arg0: Float) {
        lineWidth = arg0
        gl.lineWidth(arg0)
    }
    fun glPolygonMode(arg0: Int, arg1: Int) {}
    /**
     * The client brackets its offscreen passes - the sky cubemap (CubemapTextureGlSource),
     * bloom and glow - with `glPushAttrib(GL_VIEWPORT_BIT)` / `glPopAttrib()` around a
     * `glViewport` of the offscreen target's size, and relies on the pop to put the window
     * viewport back. Nothing else re-issues `glViewport` during normal play, so leaving these
     * as no-ops left the last offscreen viewport (128x128 for the cubemap) latched: the whole
     * frame rendered into that corner of the canvas until a window resize or a display-mode
     * change happened to call `glViewport` again. WebGL2 has no attribute stack, so keep a
     * minimal one here. `GL_VIEWPORT_BIT` is the only bit the client ever pushes; any other
     * mask still pushes a frame so pushes and pops stay balanced.
     */
    fun glPushAttrib(arg0: Int) {
        attribStack.add(if (arg0 and GL_VIEWPORT_BIT != 0) intArrayOf(viewportX, viewportY, viewportW, viewportH) else null)
    }

    fun glPopAttrib() {
        val saved = attribStack.removeLastOrNull() ?: return
        glViewport(saved[0], saved[1], saved[2], saved[3])
    }

    fun glAlphaFunc(arg0: Int, arg1: Float) = exec {
        if (state.alphaFunc == arg0 && state.alphaRef == arg1) return@exec
        state.alphaFunc = arg0
        state.alphaRef = arg1
        state.ffpStateDirty = true
    }

    fun glBlendFunc(arg0: Int, arg1: Int) = gl.blendFunc(arg0, arg1)
    fun glDepthFunc(arg0: Int) = gl.depthFunc(arg0)
    fun glDepthMask(arg0: Boolean) = gl.depthMask(arg0)
    fun glCullFace(arg0: Int) = gl.cullFace(arg0)
    fun glColorMask(arg0: Boolean, arg1: Boolean, arg2: Boolean, arg3: Boolean) = gl.colorMask(arg0, arg1, arg2, arg3)
    fun glScissor(arg0: Int, arg1: Int, arg2: Int, arg3: Int) {
        scissorX = arg0; scissorY = arg1; scissorW = arg2; scissorH = arg3
        applyScissor()
    }
    fun glStencilFunc(arg0: Int, arg1: Int, arg2: Int) = gl.stencilFunc(arg0, arg1, arg2)
    fun glStencilOp(arg0: Int, arg1: Int, arg2: Int) = gl.stencilOp(arg0, arg1, arg2)

    fun glViewport(arg0: Int, arg1: Int, arg2: Int, arg3: Int) {
        viewportX = arg0; viewportY = arg1; viewportW = arg2; viewportH = arg3
        applyViewport()
    }
    fun glClearColor(arg0: Float, arg1: Float, arg2: Float, arg3: Float) = gl.clearColor(arg0, arg1, arg2, arg3)
    fun glClearDepth(arg0: Float) = gl.clearDepth(arg0)
    fun glClear(arg0: Int) = gl.clear(arg0)
    fun glFinish() = gl.finish()
    fun glFlush() = gl.flush()

    fun glLightf(arg0: Int, arg1: Int, arg2: Float) = exec {
        val light = arg0 - GL_LIGHT0
        if (light !in 0 until MAX_LIGHTS) return@exec
        val i = when (arg1) {
            GL_CONSTANT_ATTENUATION -> 0
            GL_LINEAR_ATTENUATION -> 1
            GL_QUADRATIC_ATTENUATION -> 2
            else -> return@exec
        }
        val attenuation = state.lightAttenuation[light]
        if (attenuation[i] != arg2) {
            attenuation[i] = arg2
            state.lightDirtyMask = state.lightDirtyMask or (1 shl light)
        }
    }
    // Array arguments are copied before exec throughout: see glTexEnvfv.
    fun glLightfv(arg0: Int, arg1: Int, arg2: FloatArray?, arg3: Int) {
        val v = arg2.slice(arg3, 4)
        exec { lightfv(arg0, arg1, v) }
    }

    private fun lightfv(arg0: Int, arg1: Int, v: FloatArray) {
        val light = arg0 - GL_LIGHT0
        if (light !in 0 until MAX_LIGHTS) return
        val changed = when (arg1) {
            GL_AMBIENT -> copyIfChanged(v, state.lightAmbient[light])
            GL_DIFFUSE -> copyIfChanged(v, state.lightDiffuse[light])
            GL_POSITION -> copyIfChanged(state.transformLightPosition(v), state.lightPosition[light])
            else -> false
        }
        if (changed) state.lightDirtyMask = state.lightDirtyMask or (1 shl light)
    }

    /** Copies [src] into [dst] and returns true, or returns false if they already match. */
    private fun copyIfChanged(src: FloatArray, dst: FloatArray): Boolean {
        var same = true
        for (i in dst.indices) if (src[i] != dst[i]) { same = false; break }
        if (same) return false
        src.copyInto(dst, endIndex = dst.size)
        return true
    }

    fun glLightModelfv(arg0: Int, arg1: FloatArray?, arg2: Int) {
        val v = arg1.slice(arg2, 4)
        exec {
            if (arg0 == GL_LIGHT_MODEL_AMBIENT && copyIfChanged(v, state.globalAmbient)) {
                state.ffpLightingDirty = true
            }
        }
    }

    fun glMaterialfv(arg0: Int, arg1: Int, arg2: FloatArray?, arg3: Int) {}

    fun glFogf(arg0: Int, arg1: Float) = exec {
        when (arg0) {
            GL_FOG_START -> if (state.fogStart != arg1) { state.fogStart = arg1; state.ffpStateDirty = true }
            GL_FOG_END -> if (state.fogEnd != arg1) { state.fogEnd = arg1; state.ffpStateDirty = true }
        }
    }

    fun glFogi(arg0: Int, arg1: Int) {}
    fun glFogfv(arg0: Int, arg1: FloatArray?, arg2: Int) {
        val color = if (arg0 == GL_FOG_COLOR) arg1.slice(arg2, 4) else null
        val scalar = if (arg0 == GL_FOG_START || arg0 == GL_FOG_END) arg1?.get(arg2) else null
        exec {
            when (arg0) {
                GL_FOG_COLOR -> if (copyIfChanged(color!!, state.fogColor)) state.ffpStateDirty = true
                GL_FOG_START -> {
                    val v = scalar ?: state.fogStart
                    if (state.fogStart != v) { state.fogStart = v; state.ffpStateDirty = true }
                }
                GL_FOG_END -> {
                    val v = scalar ?: state.fogEnd
                    if (state.fogEnd != v) { state.fogEnd = v; state.ffpStateDirty = true }
                }
            }
        }
    }

    fun glTexGeni(arg0: Int, arg1: Int, arg2: Int) = exec {
        if (arg1 != GL_TEXTURE_GEN_MODE) return@exec
        if (arg0 !in GL_S..GL_Q) return@exec
        texturingUnitIndex()?.let {
            val modes = state.texGenMode[it]
            if (modes[arg0 - GL_S] != arg2) {
                modes[arg0 - GL_S] = arg2
                state.texGenDirty[it] = true
            }
        }
    }

    fun glTexGenfv(arg0: Int, arg1: Int, arg2: FloatArray?, arg3: Int) {
        if (arg2 == null || arg0 !in GL_S..GL_Q) return
        if (arg1 != GL_OBJECT_PLANE && arg1 != GL_EYE_PLANE) return
        // Copy now: callers reuse and mutate the array, and display lists replay this later.
        val plane = floatArrayOf(arg2[arg3], arg2[arg3 + 1], arg2[arg3 + 2], arg2[arg3 + 3])
        exec {
            val unit = texturingUnitIndex() ?: return@exec
            val coord = arg0 - GL_S
            if (arg1 == GL_OBJECT_PLANE) {
                if (!copyIfChanged(plane, state.texGenObjectPlane[unit][coord])) return@exec
            } else {
                // GL_EYE_PLANE: stored as plane * inverse(modelview at specification time), so
                // the plane stays fixed in eye space whatever the modelview does afterwards.
                val inv = Mat4.invert(state.matrixStack.modelview()) ?: Mat4.identity()
                val dst = state.texGenEyePlane[unit][coord]
                for (j in 0 until 4) {
                    var sum = 0f
                    for (i in 0 until 4) sum += plane[i] * inv[j * 4 + i]
                    dst[j] = sum
                }
            }
            state.texGenPlaneDirty[unit] = true
        }
    }
    fun glTexEnvi(arg0: Int, arg1: Int, arg2: Int) = exec {
        if (arg0 != GL_TEXTURE_ENV) return@exec
        val unit = texturingUnitIndex() ?: return@exec
        val values = when (arg1) {
            GL_COMBINE_RGB -> state.combineRgb
            GL_COMBINE_ALPHA -> state.combineAlpha
            GL_SOURCE0_RGB -> state.source0Rgb
            GL_SOURCE1_RGB -> state.source1Rgb
            GL_SOURCE2_RGB -> state.source2Rgb
            GL_OPERAND0_RGB -> state.operand0Rgb
            GL_OPERAND1_RGB -> state.operand1Rgb
            GL_OPERAND2_RGB -> state.operand2Rgb
            GL_SOURCE0_ALPHA -> state.source0Alpha
            GL_SOURCE1_ALPHA -> state.source1Alpha
            GL_SOURCE2_ALPHA -> state.source2Alpha
            GL_OPERAND0_ALPHA -> state.operand0Alpha
            GL_OPERAND1_ALPHA -> state.operand1Alpha
            GL_OPERAND2_ALPHA -> state.operand2Alpha
            GL_RGB_SCALE -> return@exec setEnvScale(state.rgbScale, unit, arg2.toFloat())
            GL_ALPHA_SCALE -> return@exec setEnvScale(state.alphaScale, unit, arg2.toFloat())
            // Anything else (e.g. GL_TEXTURE_ENV_MODE) isn't modelled, so nothing to upload.
            else -> return@exec
        }
        if (values[unit] != arg2) {
            values[unit] = arg2
            state.texEnvDirty[unit] = true
        }
    }

    private fun setEnvScale(scales: FloatArray, unit: Int, value: Float) {
        if (scales[unit] != value) {
            scales[unit] = value
            state.texEnvDirty[unit] = true
        }
    }

    fun glTexEnvf(arg0: Int, arg1: Int, arg2: Float) = exec {
        if (arg0 != GL_TEXTURE_ENV) return@exec
        val unit = texturingUnitIndex() ?: return@exec
        when (arg1) {
            GL_RGB_SCALE -> setEnvScale(state.rgbScale, unit, arg2)
            GL_ALPHA_SCALE -> setEnvScale(state.alphaScale, unit, arg2)
        }
    }
    fun glTexEnvfv(arg0: Int, arg1: Int, arg2: FloatArray?, arg3: Int) {
        // Copy now, not inside exec: GL captures array arguments when a display list is
        // compiled, and the client reuses the source array. WaterMaterialPass records its
        // unit-1 constant colour (the SD water's alpha) from the shared array that
        // method3808 also fills with each terrain point light's colour/intensity, so reading
        // it at replay gave the water the last light's alpha - flashing whenever the
        // "flickering effects" option animates light intensity.
        val color = arg2.slice(arg3, 4)
        exec {
            if (arg0 == GL_TEXTURE_ENV && arg1 == GL_TEXTURE_ENV_COLOR) {
                val unit = texturingUnitIndex() ?: return@exec
                if (copyIfChanged(color, state.textureEnvColor[unit])) state.texEnvDirty[unit] = true
            }
        }
    }

    // ---- Queries ----------------------------------------------------------

    private const val GL_MAX_TEXTURE_UNITS = 34018
    private const val GL_MAX_LIGHTS = 3377
    private const val GL_MAX_TEXTURE_COORDS = 34929
    private const val GL_POINT_SIZE_RANGE = 2834
    private fun sizedInternalFormat(format: Int): Int = when (format) {
        6408 -> WebGL2RenderingContext.RGBA8
        6407 -> WebGL2RenderingContext.RGB8
        6406 -> WebGL2RenderingContext.R8
        6409 -> WebGL2RenderingContext.R8
        6410 -> WebGL2RenderingContext.RG8
        6402 -> WebGL2RenderingContext.DEPTH_COMPONENT24
        34041 -> WebGL2RenderingContext.DEPTH24_STENCIL8
        else -> format
    }

    // texImage2D keeps WebGL1's legacy unsized formats (GL_ALPHA/GL_LUMINANCE/
    // GL_LUMINANCE_ALPHA) working as both internalformat and format for backward
    // compatibility, but texImage3D is a WebGL2-only entry point with no such carve-out:
    // it requires a *sized* internalformat (see sizedInternalFormat) paired with the
    // matching unsized *format* token (GL_RED/GL_RG), not the legacy one. Passing the
    // legacy format straight through (as glTexImage3Dub used to) is an invalid
    // combination for gl.texImage3D and silently fails, leaving the texture data
    // unset.
    private fun unsizedFormatFor3D(format: Int): Int = when (format) {
        6406, 6409 -> WebGL2RenderingContext.RED
        6410 -> WebGL2RenderingContext.RG
        else -> format
    }

    private fun translatePname(pname: Int): Int = when (pname) {
        GL_MAX_TEXTURE_UNITS, GL_MAX_TEXTURE_COORDS -> WebGL2RenderingContext.MAX_TEXTURE_IMAGE_UNITS
        GL_POINT_SIZE_RANGE -> WebGL2RenderingContext.ALIASED_POINT_SIZE_RANGE
        else -> pname
    }

    fun glGetError(): Int = gl.getError()
    fun glGetString(arg0: Int): String? {
        // The toolkit reads the version for its feature checks: what the emulated API offers.
        if (arg0 == 7938) return "2.1.0 634HD on " + (gl.getString(7938) ?: "")
        // A core context has no GL_EXTENSIONS string; the toolkit asks through HdContext.a(String).
        if (arg0 == 7939) return ""
        return gl.getString(arg0) ?: ""
    }

    fun glGetIntegerv(arg0: Int, arg1: IntArray?, arg2: Int) {
        // GL_PROGRAM_ERROR_POSITION_ARB is emulator state: the client's compile check
        // (ArbVertexProgram.method3442) reads it.
        if (arg0 == GL_PROGRAM_ERROR_POSITION_ARB) {
            arg1?.set(arg2, state.arbErrorPosition)
            return
        }
        if (arg1 == null) return
        when (arg0) {
            GL_MAX_LIGHTS -> { arg1[arg2] = MAX_LIGHTS; return }
            GL_MAX_TEXTURE_UNITS -> { arg1[arg2] = 3; return }
        }
        val values = when (arg0) {
            2978, 3088 -> 4 // GL_VIEWPORT, GL_SCISSOR_BOX
            3386 -> 2 // GL_MAX_VIEWPORT_DIMS
            else -> 1
        }
        val address = GlScratch.space(64)
        GL33C.nglGetIntegerv(translatePname(arg0), address)
        GlScratch.readInts(arg1, arg2, values)
    }

    fun glGetFloatv(arg0: Int, arg1: FloatArray?, arg2: Int) {
        // The fixed-function matrices live in the MatrixStack.
        when (arg0) {
            GL_MODELVIEW_MATRIX_PNAME -> {
                val m = state.matrixStack.modelview()
                for (i in m.indices) arg1?.set(arg2 + i, m[i])
                return
            }
            GL_PROJECTION_MATRIX_PNAME -> {
                val m = state.matrixStack.projection()
                for (i in m.indices) arg1?.set(arg2 + i, m[i])
                return
            }
            GL_TEXTURE_MATRIX_PNAME -> {
                val m = state.matrixStack.textureMatrix(state.matrixStack.textureUnit)
                for (i in m.indices) arg1?.set(arg2 + i, m[i])
                return
            }
        }
        if (arg1 == null) return
        val address = GlScratch.space(64)
        GL33C.nglGetFloatv(translatePname(arg0), address)
        GlScratch.readFloats(arg1, arg2, if (arg0 == GL_POINT_SIZE_RANGE || arg0 == 33901) 2 else 1)
    }

    // ---- Textures -----------------------------------------------------

    fun glGenTextures(arg0: Int, arg1: IntArray?, arg2: Int) {
        for (i in 0 until arg0) {
            val id = state.textures.allocate(gl.createTexture()!!)
            arg1?.set(arg2 + i, id)
        }
    }

    fun glDeleteTextures(arg0: Int, arg1: IntArray?, arg2: Int) {
        for (i in 0 until arg0) {
            val id = arg1?.get(arg2 + i) ?: continue
            state.textures[id]?.let { gl.deleteTexture(it) }
            state.textures.release(id)
        }
    }

    fun glBindTexture(arg0: Int, arg1: Int) = exec {
        val target = fixTarget(arg0)
        val tex = if (arg1 == 0) null else state.textures[arg1]
        val unit = state.activeTextureUnit
        if (target == WebGL2RenderingContext.TEXTURE_2D) {
            // Only 2D binds are skipped when redundant: bindSampler() rebinds 3D/cube
            // targets on reserved units behind boundTexture3D/boundTextureCubeMap's back.
            if (state.boundTexture2D[unit] !== tex) gl.bindTexture(target, tex)
            state.boundTexture2D[unit] = tex
        } else {
            gl.bindTexture(target, tex)
            if (target == WebGL2RenderingContext.TEXTURE_CUBE_MAP) state.boundTextureCubeMap[unit] = tex
            else if (target == GL_TEXTURE_3D) state.boundTexture3D[unit] = tex
        }
        // state.textureTarget (which drives uIs3D/uCubeMap) is otherwise only ever written
        // by glEnable and is sticky - nothing clears it when a unit stops being used for a
        // 3D/cube texture. If any other code path rebinds a plain 2D texture to the same
        // logical unit without an exactly-matching glDisable/glEnable pair, the stale target
        // silently persists and the next draw to reuse that unit (e.g. any other water tile,
        // since they all share this uniform) samples through the wrong sampler. Deriving the
        // target from the actual bind call too makes it self-healing regardless of
        // enable/disable call ordering elsewhere. Only a target *change* needs a re-upload;
        // marking dirty on every bind re-sent the texenv uniforms for nearly every model.
        texturingUnitIndex()?.let {
            if (state.textureTarget[it] != target) {
                state.textureTarget[it] = target
                state.texEnvDirty[it] = true
            }
        }
    }

    fun glTexParameteri(arg0: Int, arg1: Int, arg2: Int) {
        // GL_GENERATE_MIPMAP is not in a core context: the next level-0 upload generates instead
        if (arg1 == GL_GENERATE_MIPMAP) {
            state.boundTextureFor(fixTarget(arg0))?.generateMipmap = arg2 != 0
            return
        }
        gl.texParameteri(fixTarget(arg0), arg1, if (arg2 == GL_CLAMP) 33071 else arg2)
    }
    fun glTexParameterf(arg0: Int, arg1: Int, arg2: Float) {
        if (arg1 == GL_GENERATE_MIPMAP) {
            state.boundTextureFor(fixTarget(arg0))?.generateMipmap = arg2 != 0f
            return
        }
        gl.texParameterf(fixTarget(arg0), arg1, arg2)
    }
    fun glGenerateMipmapEXT(arg0: Int) = gl.generateMipmap(fixTarget(arg0))

    fun glActiveTexture(arg0: Int) = exec {
        state.activeTextureUnit = arg0 - WebGL2RenderingContext.TEXTURE0
        state.matrixStack.textureUnit = state.activeTextureUnit
        gl.activeTexture(arg0)
    }

    fun glClientActiveTexture(arg0: Int) {}

    fun glMultiTexCoord2f(arg0: Int, arg1: Float, arg2: Float) = exec {
        when (arg0 - WebGL2RenderingContext.TEXTURE0) {
            0 -> {
                state.currentTexCoord[0] = arg1; state.currentTexCoord[1] = arg2
                state.currentTexCoord[2] = 0f
            }
            1 -> {
                state.currentTexCoord1[0] = arg1; state.currentTexCoord1[1] = arg2
                state.currentTexCoord1[2] = 0f
            }
        }
    }

    fun glMultiTexCoord2i(arg0: Int, arg1: Int, arg2: Int) = exec {
        when (arg0 - WebGL2RenderingContext.TEXTURE0) {
            0 -> {
                state.currentTexCoord[0] = arg1.toFloat(); state.currentTexCoord[1] = arg2.toFloat()
                state.currentTexCoord[2] = 0f
            }
            1 -> {
                state.currentTexCoord1[0] = arg1.toFloat(); state.currentTexCoord1[1] = arg2.toFloat()
                state.currentTexCoord1[2] = 0f
            }
        }
    }

    fun glMultiTexCoord3i(arg0: Int, arg1: Int, arg2: Int, arg3: Int) = exec {
        when (arg0 - WebGL2RenderingContext.TEXTURE0) {
            0 -> {
                state.currentTexCoord[0] = arg1.toFloat(); state.currentTexCoord[1] = arg2.toFloat()
                state.currentTexCoord[2] = arg3.toFloat()
            }
            1 -> {
                state.currentTexCoord1[0] = arg1.toFloat(); state.currentTexCoord1[1] = arg2.toFloat()
                state.currentTexCoord1[2] = arg3.toFloat()
            }
        }
    }

    private const val GL_UNPACK_ROW_LENGTH = 0x0CF2
    private const val GL_UNPACK_ALIGNMENT = 0x0CF5
    private const val GL_LUMINANCE_ALPHA = 6410
    private const val GL_LUMINANCE = 6409
    private const val GL_ALPHA = 6406

    private const val GL_READ_FRAMEBUFFER = 0x8CA8
    private const val GL_DRAW_FRAMEBUFFER = 0x8CA9
    private const val GL_COLOR_ATTACHMENT0 = 0x8CE0
    private const val GL_NEAREST = 0x2600
    private const val GL_RGBA8 = 0x8058
    private const val GL_TEXTURE_MIN_FILTER = 0x2801
    private const val GL_TEXTURE_MAG_FILTER = 0x2800
    private const val GL_RASTERIZER_DISCARD = 0x8C89
    private const val GL_PACK_ALIGNMENT = 0x0D05
    private const val GL_UNSIGNED_INT_8_8_8_8 = 0x8035
    private const val GL_UNSIGNED_BYTE = 0x1401
    private const val GL_CUBE_MAP_POSITIVE_X = 0x8515
    private const val GL_CUBE_MAP_NEGATIVE_Z = 0x851A

    // ---- texture images ----

    fun glTexImage1Dub(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: Int, arg7: ByteArray?, arg8: Int) {
        // 1D textures are 2D textures one texel high: the emulated pipeline samples 2D
        glTexImage2Dub(CoreGl.TEXTURE_2D, arg1, arg2, arg3, 1, arg4, arg5, arg6, arg7, arg8)
    }

    fun glTexImage2Dub(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: Int, arg7: Int, arg8: ByteArray?, arg9: Int) {
        val address = if (arg8 == null) 0L else GlScratch.bytes(arg8, arg9, reserve = imageBytes(arg3, arg4, 1, arg6, arg7))
        texImage2D(fixTarget(arg0), arg1, arg2, arg3, arg4, arg6, arg7, address)
    }

    fun glTexImage2Di(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: Int, arg7: Int, arg8: IntArray?, arg9: Int) {
        val address = if (arg8 == null) 0L else GlScratch.ints(arg8, arg9)
        texImage2D(fixTarget(arg0), arg1, arg2, arg3, arg4, arg6, arg7, address)
    }

    fun glTexImage2Df(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: Int, arg7: Int, arg8: FloatArray?, arg9: Int) {
        val address = if (arg8 == null) 0L else GlScratch.floats(arg8, arg9)
        texImage2D(fixTarget(arg0), arg1, arg2, arg3, arg4, arg6, arg7, address)
    }

    private fun texImage2D(target: Int, level: Int, internalFormat: Int, width: Int, height: Int, format: Int, type: Int, address: Long) {
        gl.texImage2D(target, level, coreInternalFormat(internalFormat), width, height, 0, coreFormat(format), type, address)
        if (level == 0) {
            applySwizzle(target, internalFormat)
            state.recordTextureInternalFormat(target, internalFormat)
            state.recordTextureSize(target, width, height)
            val texture = state.boundTextureFor(target)
            if (texture != null && texture.generateMipmap && address != 0L) gl.generateMipmap(target)
        }
    }

    fun glTexImage3Dub(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: Int, arg7: Int, arg8: Int, arg9: ByteArray?, arg10: Int) {
        val address = if (arg9 == null) 0L else GlScratch.bytes(arg9, arg10, reserve = imageBytes(arg3, arg4, arg5, arg7, arg8))
        gl.texImage3D(arg0, arg1, coreInternalFormat(arg2), arg3, arg4, arg5, 0, coreFormat(arg7), arg8, address)
        if (arg1 == 0) applySwizzle(arg0, arg2)
    }

    fun glTexSubImage2Dub(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: Int, arg7: Int, arg8: ByteArray?, arg9: Int) {
        val data = arg8 ?: return
        val address = GlScratch.bytes(data, arg9, reserve = imageBytes(arg4, arg5, 1, arg6, arg7))
        texSubImage2D(fixTarget(arg0), arg1, arg2, arg3, arg4, arg5, arg6, arg7, address)
    }

    fun glTexSubImage2Di(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: Int, arg7: Int, arg8: IntArray?, arg9: Int) {
        val data = arg8 ?: return
        val address = GlScratch.ints(data, arg9)
        texSubImage2D(fixTarget(arg0), arg1, arg2, arg3, arg4, arg5, arg6, arg7, address)
        if (bytesPerPixel(arg6, arg7) == 4) replicateSpriteEdges(fixTarget(arg0), arg1, arg2, arg3, arg4, arg5, arg6, arg7, data, arg9)
    }

    fun glTexSubImage2Df(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: Int, arg7: Int, arg8: FloatArray?, arg9: Int) {
        val data = arg8 ?: return
        texSubImage2D(fixTarget(arg0), arg1, arg2, arg3, arg4, arg5, arg6, arg7, GlScratch.floats(data, arg9))
    }

    private fun texSubImage2D(target: Int, level: Int, x: Int, y: Int, width: Int, height: Int, format: Int, type: Int, address: Long) {
        gl.texSubImage2D(target, level, x, y, width, height, coreFormat(format), type, address)
        if (level == 0) {
            val texture = state.boundTextureFor(target)
            if (texture != null && texture.generateMipmap) gl.generateMipmap(target)
        }
    }

    /**
     * The client asks for GL_ARB_texture_rectangle and, without it (as here), pads every sprite up
     * to a power-of-two texture and samples only the sprite's part. Stretched sprites are drawn with
     * LINEAR filtering, so the texels just inside the sprite's right and bottom edges blend with the
     * transparent padding: a dark seam at the end of a stretched strip. Give the padding what
     * clamp-to-edge would: the sprite's last column and row, one texel outwards. Only 4-byte pixels
     * uploaded at the origin into a larger texture qualify, which leaves atlas sub-uploads alone.
     */
    private fun replicateSpriteEdges(target: Int, level: Int, x: Int, y: Int, w: Int, h: Int, format: Int, type: Int, data: IntArray, offset: Int) {
        if (level != 0 || x != 0 || y != 0 || w <= 0 || h <= 0) return
        val texture = state.boundTextureFor(target) ?: return
        if (texture.width < 0) return
        val padRight = w < texture.width
        val padBottom = h < texture.height
        if (!padRight && !padBottom) return
        if (data.size - offset < w * h) return
        val previousAlignment = unpackAlignment
        gl.pixelStorei(GL_UNPACK_ALIGNMENT, 4)
        gl.pixelStorei(GL_UNPACK_ROW_LENGTH, 0)
        if (padRight) {
            val column = edgeScratch(h)
            for (row in 0 until h) column[row] = data[offset + row * w + (w - 1)]
            gl.texSubImage2D(target, 0, w, 0, 1, h, coreFormat(format), type, GlScratch.ints(column, 0, h))
        }
        if (padBottom) {
            val rowWidth = if (padRight) w + 1 else w
            val row = edgeScratch(rowWidth)
            for (i in 0 until w) row[i] = data[offset + (h - 1) * w + i]
            if (padRight) row[w] = data[offset + (h - 1) * w + (w - 1)]
            gl.texSubImage2D(target, 0, 0, h, rowWidth, 1, coreFormat(format), type, GlScratch.ints(row, 0, rowWidth))
        }
        gl.pixelStorei(GL_UNPACK_ALIGNMENT, previousAlignment)
    }

    private var edgePixels = IntArray(256)

    private fun edgeScratch(size: Int): IntArray {
        if (edgePixels.size < size) edgePixels = IntArray(maxOf(size, edgePixels.size * 2))
        return edgePixels
    }

    fun glCopyTexImage2D(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: Int, arg7: Int) {
        val target = fixTarget(arg0)
        gl.copyTexImage2D(target, arg1, coreInternalFormat(arg2), arg3, arg4, arg5, arg6, 0)
        if (arg1 == 0) {
            applySwizzle(target, arg2)
            state.recordTextureInternalFormat(target, arg2)
            state.recordTextureSize(target, arg5, arg6)
        }
    }

    fun glCopyTexSubImage2D(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: Int, arg7: Int) {
        gl.copyTexSubImage2D(fixTarget(arg0), arg1, arg2, arg3, arg4, arg5, arg6, arg7)
    }

    /** client.test.hdWideShot=<file.png>: a frame read at an interface scale, saved at the window's full size (debugging). */
    fun wideShot(x: Int, y: Int, width: Int, height: Int) {
        val path = System.getProperty("client.test.hdWideShot") ?: return
        val n = readScale
        if (n == 1 || width <= 0 || height <= 0) return
        val w = width * n
        val h = height * n
        val pixels = IntArray(w * h)
        val address = GlScratch.space(pixels.size * 4)
        gl.readPixels(x * n, y * n, w, h, GL33C.GL_BGRA, GL33C.GL_UNSIGNED_BYTE, address)
        GlScratch.readInts(pixels, 0, pixels.size)
        val image = java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_RGB)
        for (row in 0 until h) {
            for (col in 0 until w) {
                image.setRGB(col, row, pixels[(h - 1 - row) * w + col] and 0xFFFFFF)
            }
        }
        javax.imageio.ImageIO.write(image, "png", java.io.File(path))
    }

    fun glReadPixelsi(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: IntArray?, arg7: Int) {
        val out = arg6 ?: return
        if (arg2 <= 0 || arg3 <= 0) return
        val n = readScale
        if (n != 1) {
            // the window is n times larger: read it all, keep one pixel of each n x n block
            val wide = IntArray(arg2 * n * arg3 * n)
            val address = GlScratch.space(wide.size * 4)
            gl.readPixels(arg0 * n, arg1 * n, arg2 * n, arg3 * n, coreFormat(arg4), arg5, address)
            GlScratch.readInts(wide, 0, wide.size)
            for (y in 0 until arg3) {
                for (x in 0 until arg2) {
                    out[arg7 + y * arg2 + x] = wide[(y * n) * arg2 * n + x * n]
                }
            }
            return
        }
        val address = GlScratch.space(arg2 * arg3 * 4)
        gl.readPixels(arg0, arg1, arg2, arg3, coreFormat(arg4), arg5, address)
        GlScratch.readInts(out, arg7, arg2 * arg3)
    }

    fun glReadPixelsub(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: ByteArray?, arg7: Int) {
        val out = arg6 ?: return
        if (arg2 <= 0 || arg3 <= 0) return
        val bytes = arg2 * arg3 * bytesPerPixel(arg4, arg5)
        val address = GlScratch.space(bytes + 64)
        gl.readPixels(arg0, arg1, arg2, arg3, coreFormat(arg4), arg5, address)
        GlScratch.readBytes(out, arg7, bytes)
    }

    fun glCopyTexSubImage3D(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: Int, arg7: Int, arg8: Int) =
        gl.copyTexSubImage3D(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8)
    fun glGetTexImageub(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: ByteArray?, arg5: Int) {
        val out = arg4 ?: return
        val address = GlScratch.space(out.size - arg5)
        GL33C.nglGetTexImage(fixTarget(arg0), arg1, coreFormat(arg2), arg3, address)
        GlScratch.readBytes(out, arg5)
    }
    fun glGetTexImagei(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: IntArray?, arg5: Int) {
        val out = arg4 ?: return
        val bytes = (out.size - arg5) * 4
        val address = GlScratch.space(bytes)
        GL33C.nglGetTexImage(fixTarget(arg0), arg1, coreFormat(arg2), arg3, address)
        GlScratch.readInts(out, arg5)
    }

    fun glGenBuffersARB(arg0: Int, arg1: IntArray?, arg2: Int) {
        for (i in 0 until arg0) {
            val buf = gl.createBuffer()
            if (buf == null) {
            }
            val id = state.buffers.allocate(buf!!)
            arg1?.set(arg2 + i, id)
        }
    }

    fun glDeleteBuffersARB(arg0: Int, arg1: IntArray?, arg2: Int) {
        for (i in 0 until arg0) {
            val id = arg1?.get(arg2 + i) ?: continue
            state.buffers[id]?.let { gl.deleteBuffer(it) }
            state.buffers.release(id)
        }
    }

    fun glBindBufferARB(arg0: Int, arg1: Int) {
        val buf = if (arg1 == 0) null else state.buffers[arg1]
        gl.bindBuffer(arg0, buf)
        when (arg0) {
            WebGL2RenderingContext.ARRAY_BUFFER -> state.boundArrayBuffer = buf
            WebGL2RenderingContext.ELEMENT_ARRAY_BUFFER -> state.boundElementArrayBuffer = buf
        }
    }

    fun glBufferDataARBa(arg0: Int, arg1: Int, arg2: Long, arg3: Int) {
        bufferWritten(arg0)
        if (arg2 == 0L) gl.bufferData(arg0, arg1, arg3)
        else gl.bufferDataAddress(arg0, arg1, arg2, arg3)
    }

    fun glBufferDataARBub(arg0: Int, arg1: Int, arg2: ByteArray?, arg3: Int, arg4: Int) {
        bufferWritten(arg0)
        if (arg2 == null) {
            gl.bufferData(arg0, arg1, arg4)
            return
        }
        gl.bufferData(arg0, arg2, arg3, arg1, arg4)
    }

    fun glBufferSubDataARBa(arg0: Int, arg1: Int, arg2: Int, arg3: Long) {
        bufferWritten(arg0)
        if (arg3 == 0L) return
        gl.bufferSubDataAddress(arg0, arg1, arg2, arg3)
    }

    fun glBufferSubDataARBub(arg0: Int, arg1: Int, arg2: Int, arg3: ByteArray?, arg4: Int) {
        bufferWritten(arg0)
        val data = arg3 ?: return
        gl.bufferSubData(arg0, arg1, data, arg4, arg2)
    }

    fun glMapBufferARB(arg0: Int, arg1: Int): Long = 0L
    fun glUnmapBufferARB(arg0: Int): Boolean = true

    fun glEnableClientState(arg0: Int) = exec {
        val loc = attribForClientState(arg0) ?: return@exec
        state.clientArrays[loc].enabled = true
    }

    fun glDisableClientState(arg0: Int) = exec {
        val loc = attribForClientState(arg0) ?: return@exec
        state.clientArrays[loc].enabled = false
    }

    private fun attribForClientState(cap: Int): Int? = when (cap) {
        GL_VERTEX_ARRAY -> ATTRIB_POSITION
        GL_COLOR_ARRAY -> ATTRIB_COLOR
        GL_TEXTURE_COORD_ARRAY -> ATTRIB_TEXCOORD0
        GL_NORMAL_ARRAY -> ATTRIB_NORMAL
        else -> null
    }

    fun glVertexPointer(arg0: Int, arg1: Int, arg2: Int, arg3: Long) = exec {
        val p = state.clientArrays[ATTRIB_POSITION]
        p.size = arg0; p.type = arg1; p.stride = arg2; p.offset = arg3
        p.sourceBuffer = state.boundArrayBuffer
    }

    fun glColorPointer(arg0: Int, arg1: Int, arg2: Int, arg3: Long) = exec {
        val p = state.clientArrays[ATTRIB_COLOR]
        p.size = arg0; p.type = arg1; p.stride = arg2; p.offset = arg3
        p.normalized = arg1 == WebGL2RenderingContext.UNSIGNED_BYTE
        p.sourceBuffer = state.boundArrayBuffer
    }

    fun glTexCoordPointer(arg0: Int, arg1: Int, arg2: Int, arg3: Long) = exec {
        val p = state.clientArrays[ATTRIB_TEXCOORD0]
        p.size = arg0; p.type = arg1; p.stride = arg2; p.offset = arg3
        p.sourceBuffer = state.boundArrayBuffer
    }

    fun glNormalPointer(arg0: Int, arg1: Int, arg2: Long) = exec {
        val p = state.clientArrays[ATTRIB_NORMAL]
        p.size = 3; p.type = arg0; p.stride = arg1; p.offset = arg2
        p.sourceBuffer = state.boundArrayBuffer
    }

    /**
     * Only issues the attribute calls whose GL-side state (tracked in GlState.glAttrib*)
     * differs from what this draw needs. ARRAY_BUFFER is state.boundArrayBuffer on entry -
     * every other ARRAY_BUFFER bind restores it - so a bind is only needed for a pointer
     * into a different buffer.
     */
    private fun bindClientArrays() {
        var bound = state.boundArrayBuffer
        for (loc in 0 until ATTRIB_COUNT) {
            val p = state.clientArrays[loc]
            if (p.enabled) {
                if (!state.glAttribEnabled[loc]) {
                    gl.enableVertexAttribArray(loc)
                    state.glAttribEnabled[loc] = true
                }
                if (state.glAttribBuffer[loc] !== p.sourceBuffer || state.glAttribSize[loc] != p.size ||
                    state.glAttribType[loc] != p.type || state.glAttribNormalized[loc] != p.normalized ||
                    state.glAttribStride[loc] != p.stride || state.glAttribOffset[loc] != p.offset
                ) {
                    if (bound !== p.sourceBuffer) {
                        gl.bindBuffer(WebGL2RenderingContext.ARRAY_BUFFER, p.sourceBuffer)
                        bound = p.sourceBuffer
                    }
                    gl.vertexAttribPointer(loc, p.size, p.type, p.normalized, p.stride, p.offset)
                    state.glAttribBuffer[loc] = p.sourceBuffer
                    state.glAttribSize[loc] = p.size
                    state.glAttribType[loc] = p.type
                    state.glAttribNormalized[loc] = p.normalized
                    state.glAttribStride[loc] = p.stride
                    state.glAttribOffset[loc] = p.offset
                }
            } else {
                if (state.glAttribEnabled[loc]) {
                    gl.disableVertexAttribArray(loc)
                    state.glAttribEnabled[loc] = false
                }
                when (loc) {
                    ATTRIB_COLOR -> setAttribConstant(loc, state.currentColor, 4)
                    ATTRIB_TEXCOORD0 -> setAttribConstant(loc, state.currentTexCoord, 3)
                    ATTRIB_TEXCOORD1 -> setAttribConstant(loc, state.currentTexCoord1, 3)
                    ATTRIB_NORMAL -> setAttribConstant(loc, state.currentNormal, 3)
                }
            }
        }
        // glBufferDataARB/glBufferSubDataARB act on the *currently bound* buffer rather than
        // on state.boundArrayBuffer, so the real binding has to be put back - otherwise a
        // later glBufferSubData with no intervening glBindBuffer writes into the wrong buffer.
        if (bound !== state.boundArrayBuffer) {
            gl.bindBuffer(WebGL2RenderingContext.ARRAY_BUFFER, state.boundArrayBuffer)
        }
    }

    /** glVertexAttrib3f/4f for a disabled array, skipped when the context already holds [v]. */
    private fun setAttribConstant(loc: Int, v: FloatArray, size: Int) {
        val c = state.glAttribConstant[loc]
        if (c[0] == v[0] && c[1] == v[1] && c[2] == v[2] && (size == 3 || c[3] == v[3])) return
        c[0] = v[0]; c[1] = v[1]; c[2] = v[2]
        if (size == 4) {
            c[3] = v[3]
            gl.vertexAttrib4f(loc, v[0], v[1], v[2], v[3])
        } else {
            gl.vertexAttrib3f(loc, v[0], v[1], v[2])
        }
    }

    fun glDrawArrays(arg0: Int, arg1: Int, arg2: Int) = exec {
        bindClientArrays()
        state.prepareDraw()
        val count = quadIndices(arg0, arg1, arg2)
        if (count < 0) {
            gl.drawArrays(arg0, arg1, arg2)
        } else {
            gl.bindBuffer(WebGL2RenderingContext.ELEMENT_ARRAY_BUFFER, state.quadIndexBuffer)
            gl.bufferData(WebGL2RenderingContext.ELEMENT_ARRAY_BUFFER, quadIndexScratch, count, WebGL2RenderingContext.STREAM_DRAW)
            gl.drawElements(WebGL2RenderingContext.TRIANGLES, count, WebGL2RenderingContext.UNSIGNED_INT, 0L)
            gl.bindBuffer(WebGL2RenderingContext.ELEMENT_ARRAY_BUFFER, state.boundElementArrayBuffer)
        }
    }

    // Fixed-function matrix *pname* values (as passed to glGetFloatv/glGetIntegerv).
    // These are shim-only state on JS (tracked by MatrixStack); WebGL2 has no such
    // legacy pnames, so gl.getParameter() returns null for them.
    private const val GL_MODELVIEW_MATRIX_PNAME = 2982
    private const val GL_PROJECTION_MATRIX_PNAME = 2983
    private const val GL_TEXTURE_MATRIX_PNAME = 2984

    // Reused/grown across quad draws instead of allocating a fresh boxed ArrayList<Int> (plus a
    // second Uint32Array copy pass) on every GL_QUADS/GL_QUAD_STRIP/GL_POLYGON draw call.
    private var quadIndexScratch = IntArray(0)

    private fun ensureQuadIndexCapacity(required: Int) {
        if (required <= quadIndexScratch.size) return
        var newSize = if (quadIndexScratch.isEmpty()) required else quadIndexScratch.size * 2
        while (newSize < required) newSize *= 2
        quadIndexScratch = IntArray(newSize)
    }

    /** Builds triangle indices for a quad or polygon draw in [quadIndexScratch]; -1 for any other mode. */
    private fun quadIndices(mode: Int, first: Int, count: Int): Int {
        val maxOut = when (mode) {
            GL_QUADS -> count / 4 * 6
            GL_QUAD_STRIP -> if (count >= 4) (count - 2) / 2 * 6 else 0
            GL_POLYGON -> if (count >= 3) (count - 2) * 3 else 0
            else -> return -1
        }
        ensureQuadIndexCapacity(maxOut)
        var out = 0
        when (mode) {
            GL_QUADS -> {
                var i = 0
                while (i + 4 <= count) {
                    val b = first + i
                    quadIndexScratch[out++] = b; quadIndexScratch[out++] = b + 1; quadIndexScratch[out++] = b + 2
                    quadIndexScratch[out++] = b; quadIndexScratch[out++] = b + 2; quadIndexScratch[out++] = b + 3
                    i += 4
                }
            }
            GL_QUAD_STRIP -> {
                var i = 0
                while (i + 4 <= count) {
                    val b = first + i
                    quadIndexScratch[out++] = b; quadIndexScratch[out++] = b + 1; quadIndexScratch[out++] = b + 3
                    quadIndexScratch[out++] = b; quadIndexScratch[out++] = b + 3; quadIndexScratch[out++] = b + 2
                    i += 2
                }
            }
            GL_POLYGON -> {
                for (i in 1..count - 2) {
                    quadIndexScratch[out++] = first; quadIndexScratch[out++] = first + i; quadIndexScratch[out++] = first + i + 1
                }
            }
        }
        return out
    }

    /**
     * 634HD: how many times each GL buffer (by name) has been written through the emulator, so a
     * shadow caster recorded from a buffer can tell whether its data is still what was drawn.
     */
    private val bufferWrites = HashMap<Int, Int>()

    private fun bufferWritten(target: Int) {
        val buffer = (if (target == WebGL2RenderingContext.ELEMENT_ARRAY_BUFFER) state.boundElementArrayBuffer else state.boundArrayBuffer) ?: return
        // a shadow caster drawn from this buffer keeps a copy of what it drew before it changes
        shadows?.beforeWrite(buffer.id)
        bufferWrites[buffer.id] = (bufferWrites[buffer.id] ?: 0) + 1
    }

    /** The write count of GL buffer [name] (0 if never written through the emulator). */
    fun bufferVersion(name: Int): Int = bufferWrites[name] ?: 0

    /** 634HD: the shadow map maker recording casters (HdToolkit sets it). */
    @JvmField
    var shadows: com.jagex.game.runetek5.graphics.hd.scene.HdShadows? = null

    fun glDrawElements(arg0: Int, arg1: Int, arg2: Int, arg3: Long) = exec {
        bindClientArrays()
        if (state.boundElementArrayBuffer == null) {
            // indices in client memory (an address): a core context draws only from a buffer
            val bytesPerIndex = when (arg2) {
                WebGL2RenderingContext.UNSIGNED_BYTE -> 1
                5123 -> 2
                else -> 4
            }
            // 634HD: an opaque model of the 3D view with its indices in memory: a shadow caster, its indices copied
            val recorder = shadows
            if (recorder != null && recorder.recording && state.hdScene != 0 && state.hdGround == 0 && arg0 == 4 && arg3 != 0L) {
                val p = state.clientArrays[ATTRIB_POSITION]
                if (p.enabled && p.sourceBuffer != null) {
                    recorder.recordIndices(p.sourceBuffer!!.id, p.size, p.type, p.stride, p.offset, arg3, arg1 * bytesPerIndex, arg1, arg2, state.matrixStack.modelview(), state)
                }
            }
            gl.bindBuffer(WebGL2RenderingContext.ELEMENT_ARRAY_BUFFER, state.clientElementBuffer)
            if (arg3 != 0L) {
                gl.bufferDataAddress(WebGL2RenderingContext.ELEMENT_ARRAY_BUFFER, arg1 * bytesPerIndex, arg3, WebGL2RenderingContext.STREAM_DRAW)
            }
            state.prepareDraw()
            gl.drawElements(arg0, arg1, arg2, 0L)
            gl.bindBuffer(WebGL2RenderingContext.ELEMENT_ARRAY_BUFFER, null)
        } else {
            // Every path that binds another ELEMENT_ARRAY_BUFFER restores this binding.
            state.prepareDraw()
            gl.drawElements(arg0, arg1, arg2, arg3)
            // 634HD: a model of the 3D view, recorded as a shadow caster (HdShadows); not the ground,
            // whose buffers are rewritten between frames and which needs no shadow of its own
            val recorder = shadows
            if (recorder != null && recorder.recording && state.hdScene != 0 && state.hdGround == 0 && arg0 == 4) {
                val p = state.clientArrays[ATTRIB_POSITION]
                if (p.enabled && p.sourceBuffer != null) {
                    recorder.record(p.sourceBuffer!!.id, p.size, p.type, p.stride, p.offset, state.boundElementArrayBuffer!!.id, arg1, arg2, arg3, state.matrixStack.modelview(), state)
                }
            }
        }
    }

    fun glPixelStorei(arg0: Int, arg1: Int) {
        if (arg0 == GL_UNPACK_ALIGNMENT) unpackAlignment = arg1
        gl.pixelStorei(arg0, arg1)
    }
    fun glPixelTransferf(arg0: Int, arg1: Float) {}
    fun glPixelZoom(arg0: Float, arg1: Float) {}

    fun glCopyPixels(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int) {}
    fun glDrawPixelsi(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: IntArray?, arg5: Int) {}
    fun glDrawPixelsub(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: ByteArray?, arg5: Int) {}

    fun glGenFramebuffersEXT(arg0: Int, arg1: IntArray?, arg2: Int) {
        for (i in 0 until arg0) {
            val id = state.framebuffers.allocate(gl.createFramebuffer()!!)
            arg1?.set(arg2 + i, id)
        }
    }

    fun glDeleteFramebuffersEXT(arg0: Int, arg1: IntArray?, arg2: Int) {
        for (i in 0 until arg0) {
            val id = arg1?.get(arg2 + i) ?: continue
            state.framebuffers[id]?.let { gl.deleteFramebuffer(it) }
            state.framebuffers.release(id)
        }
    }

    fun glBindFramebufferEXT(arg0: Int, arg1: Int) {
        val fb = if (arg1 == 0) null else state.framebuffers[arg1]
        gl.bindFramebuffer(arg0, fb)
        state.boundFramebuffer = fb
        if (arg0 != GL_DRAW_FRAMEBUFFER) readFramebuffer = fb
        if (arg0 != GL_READ_FRAMEBUFFER) {
            val scaleChanged = (drawFramebuffer == null) != (fb == null)
            drawFramebuffer = fb
            // Viewport and scissor are stored in logical pixels; switching between the window
            // and an offscreen target changes their scale, whichever order the client set them.
            if (scaleChanged && drawScale != 1) {
                applyViewport()
                applyScissor()
            }
        }
    }

    fun glFramebufferTexture2DEXT(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int) {
        val tex = if (arg3 == 0) null else state.textures[arg3]
        gl.framebufferTexture2D(arg0, arg1, fixTarget(arg2), tex, arg4)
    }

    fun glFramebufferTexture3DEXT(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int) {
        val tex = if (arg3 == 0) null else state.textures[arg3]
        gl.framebufferTextureLayer(arg0, arg1, tex, arg4, arg5)
    }

    fun glFramebufferRenderbufferEXT(arg0: Int, arg1: Int, arg2: Int, arg3: Int) {
        val rb = if (arg3 == 0) null else state.renderbuffers[arg3]
        gl.framebufferRenderbuffer(arg0, arg1, arg2, rb)
    }

    fun glCheckFramebufferStatusEXT(arg0: Int): Int = gl.checkFramebufferStatus(arg0)
    fun glBlitFramebufferEXT(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: Int, arg7: Int, arg8: Int, arg9: Int) {
        val r = readScale
        val d = drawScale
        gl.blitFramebuffer(arg0 * r, arg1 * r, arg2 * r, arg3 * r, arg4 * d, arg5 * d, arg6 * d, arg7 * d, arg8, arg9)
    }

    fun glDrawBuffer(arg0: Int) {
        gl.drawBuffer(arg0)
    }

    fun glDrawBuffersARB(arg0: Int, arg1: IntArray?, arg2: Int) = gl.drawBuffers(arg1.slice(arg2, arg0))
    fun glReadBuffer(arg0: Int) {
        gl.readBuffer(arg0)
    }

    fun glGenRenderbuffersEXT(arg0: Int, arg1: IntArray?, arg2: Int) {
        for (i in 0 until arg0) {
            val id = state.renderbuffers.allocate(gl.createRenderbuffer()!!)
            arg1?.set(arg2 + i, id)
        }
    }

    fun glDeleteRenderbuffersEXT(arg0: Int, arg1: IntArray?, arg2: Int) {
        for (i in 0 until arg0) {
            val id = arg1?.get(arg2 + i) ?: continue
            state.renderbuffers[id]?.let { gl.deleteRenderbuffer(it) }
            state.renderbuffers.release(id)
        }
    }

    fun glBindRenderbufferEXT(arg0: Int, arg1: Int) = gl.bindRenderbuffer(arg0, if (arg1 == 0) null else state.renderbuffers[arg1])
    fun glRenderbufferStorageEXT(arg0: Int, arg1: Int, arg2: Int, arg3: Int) =
        gl.renderbufferStorage(arg0, sizedInternalFormat(arg1), arg2, arg3)

    fun glRenderbufferStorageMultisampleEXT(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int) =
        gl.renderbufferStorageMultisample(arg0, arg1, sizedInternalFormat(arg2), arg3, arg4)

    fun glCreateShaderObjectARB(arg0: Int): Long {
        val shader = gl.createShader(arg0) ?: return 0L
        return state.glObjects.allocate(ShaderOrProgram.Shader(shader, arg0))
    }

    fun glShaderSourceARB(arg0: Long, arg1: String?) {
        val obj = state.glObjects[arg0] as? ShaderOrProgram.Shader ?: return
        gl.shaderSource(obj.shader, translateLegacyGlsl(arg1 ?: "", obj.type))
    }

    fun glShaderSourceRawARB(arg0: Long, arg1: ByteArray?) {
        val obj = state.glObjects[arg0] as? ShaderOrProgram.Shader ?: return
        gl.shaderSource(obj.shader, translateLegacyGlsl(arg1?.decodeToString() ?: "", obj.type))
    }

    fun glCompileShaderARB(arg0: Long) {
        (state.glObjects[arg0] as? ShaderOrProgram.Shader)?.let { gl.compileShader(it.shader) }
    }

    fun glCreateProgramObjectARB(): Long {
        val program = gl.createProgram() ?: return 0L
        return state.glObjects.allocate(ShaderOrProgram.Program(program))
    }

    fun glAttachObjectARB(arg0: Long, arg1: Long) {
        val obj = state.glObjects[arg0] as? ShaderOrProgram.Program ?: return
        val shaderObj = state.glObjects[arg1] as? ShaderOrProgram.Shader ?: return
        gl.attachShader(obj.program, shaderObj.shader)
        obj.attachedTypes.add(shaderObj.type)
    }

    fun glDetachObjectARB(arg0: Long, arg1: Long) {
        val obj = state.glObjects[arg0] as? ShaderOrProgram.Program ?: return
        val shaderObj = state.glObjects[arg1] as? ShaderOrProgram.Shader ?: return
        gl.detachShader(obj.program, shaderObj.shader)
        obj.attachedTypes.remove(shaderObj.type)
    }

    fun glLinkProgramARB(arg0: Long) {
        (state.glObjects[arg0] as? ShaderOrProgram.Program)?.let {
            if (WebGL2RenderingContext.VERTEX_SHADER !in it.attachedTypes && !it.hasSyntheticVertexShader) {
                gl.attachShader(it.program, state.syntheticVertexShader)
                it.hasSyntheticVertexShader = true
            }
            gl.linkProgram(it.program)
            it.compat = CompatUniformLocations(gl, it.program)
        }
    }

    fun glUseProgramObjectARB(arg0: Long) {
        val obj = if (arg0 == 0L) null else state.glObjects[arg0] as? ShaderOrProgram.Program
        state.useProgram(obj?.program)
        state.boundProgram = obj?.program
        state.boundProgramObj = obj
    }

    fun glDeleteObjectARB(arg0: Long) {
        when (val obj = state.glObjects[arg0]) {
            is ShaderOrProgram.Program -> gl.deleteProgram(obj.program)
            is ShaderOrProgram.Shader -> gl.deleteShader(obj.shader)
            null -> return
        }
        state.glObjects.release(arg0)
    }

    fun glGetObjectParameterivARB(arg0: Long, arg1: Int, arg2: IntArray?, arg3: Int) {
        val obj = state.glObjects[arg0]
        if (arg1 == 35716) {
            val log = when (obj) {
                is ShaderOrProgram.Program -> gl.getProgramInfoLog(obj.program)
                is ShaderOrProgram.Shader -> gl.getShaderInfoLog(obj.shader)
                null -> null
            }
            arg2?.set(arg3, (log?.length ?: 0) + 1)
            return
        }
        val v = when (obj) {
            is ShaderOrProgram.Program -> gl.getProgramParameter(obj.program, arg1)
            is ShaderOrProgram.Shader -> gl.getShaderParameter(obj.shader, arg1)
            null -> 0
        }
        arg2?.set(arg3, v)
    }

    fun glGetInfoLogARB(arg0: Long, arg1: Int, arg2: IntArray?, arg3: Int, arg4: ByteArray?, arg5: Int) {
        val log = when (val obj = state.glObjects[arg0]) {
            is ShaderOrProgram.Program -> gl.getProgramInfoLog(obj.program)
            is ShaderOrProgram.Shader -> gl.getShaderInfoLog(obj.shader)
            null -> null
        } ?: ""
        val bytes = log.encodeToByteArray()
        val n = minOf(bytes.size, arg1)
        arg2?.set(arg3, n)
        if (arg4 != null) for (i in 0 until n) arg4[arg5 + i] = bytes[i]
    }

    fun glGetProgramivARB(arg0: Int, arg1: Int, arg2: IntArray?, arg3: Int) {
        if (arg1 == GL_PROGRAM_ERROR_POSITION_ARB) {
            arg2?.set(arg3, state.arbErrorPosition)
            return
        }
        arg2?.set(arg3, 0)
    }

    fun glGetUniformLocationARB(arg0: Long, arg1: String?): Int {
        val program = (state.glObjects[arg0] as? ShaderOrProgram.Program)?.program ?: return -1
        return gl.getUniformLocation(program, arg1 ?: "")
    }

    fun glUniform1iARB(arg0: Int, arg1: Int) = gl.uniform1i(arg0, arg1)
    fun glUniform1fARB(arg0: Int, arg1: Float) = gl.uniform1f(arg0, arg1)
    fun glUniform2fARB(arg0: Int, arg1: Float, arg2: Float) = gl.uniform2f(arg0, arg1, arg2)
    fun glUniform3fARB(arg0: Int, arg1: Float, arg2: Float, arg3: Float) = gl.uniform3f(arg0, arg1, arg2, arg3)
    fun glUniform4fARB(arg0: Int, arg1: Float, arg2: Float, arg3: Float, arg4: Float) =
        gl.uniform4f(arg0, arg1, arg2, arg3, arg4)

    fun glUniformMatrix2fvARB(arg0: Int, arg1: Int, arg2: Boolean, arg3: FloatArray?, arg4: Int) =
        gl.uniformMatrix2fv(arg0, arg2, arg3.slice(arg4, 4 * arg1))

    fun glUniformMatrix3fvARB(arg0: Int, arg1: Int, arg2: Boolean, arg3: FloatArray?, arg4: Int) =
        gl.uniformMatrix3fv(arg0, arg2, arg3.slice(arg4, 9 * arg1))

    fun glUniformMatrix4fvARB(arg0: Int, arg1: Int, arg2: Boolean, arg3: FloatArray?, arg4: Int) =
        gl.uniformMatrix4fv(arg0, arg2, arg3.slice(arg4, 16 * arg1))

    // ---- ARB vertex-program assembly (transpiled to GLSL ES 300) --------
    // Programs load via glProgramRawARB/glProgramStringARB into the currently bound
    // program handle, transpile at load time, and compile eagerly so that a failure is
    // reported through GL_PROGRAM_ERROR_POSITION_ARB exactly like a driver error.

    fun glGenProgramARB(): Int = state.arbPrograms.allocate(ArbProgramRuntime())

    fun glDeleteProgramARB(arg0: Int) = exec {
        state.arbPrograms[arg0]?.delete(state.gl)
        state.arbPrograms.release(arg0)
    }

    fun glBindProgramARB(arg0: Int, arg1: Int) = exec {
        if (arg0 == GL_VERTEX_PROGRAM_ARB) {
            state.boundVertexProgram = if (arg1 == 0) null else state.arbPrograms[arg1]
        }
    }

    fun glProgramStringARB(arg0: Int, arg1: Int, arg2: String?) = exec {
        loadArbProgram(arg0, arg1, arg2, 0)
    }

    fun glProgramRawARB(arg0: Int, arg1: Int, arg2: ByteArray?) = exec {
        if (arg2 == null) {
            state.arbErrorPosition = 0
        } else {
            // The assembly is ASCII; strip any NUL padding before transpiling.
            loadArbProgram(arg0, arg1, arg2.decodeToString().substringBefore('\u0000'), 0)
        }
    }

    private fun loadArbProgram(target: Int, format: Int, text: String?, line: Int) {
        if (target != GL_VERTEX_PROGRAM_ARB) {
            // Fragment-program assembly (34820) has no emulation — fail the load so the
            // caller falls back exactly as it would on unsupported hardware.
            state.arbErrorPosition = if (target == GL_FRAGMENT_PROGRAM_ARB) 0 else line
            return
        }
        if (format != GL_PROGRAM_FORMAT_ASCII_ARB || text.isNullOrEmpty()) {
            state.arbErrorPosition = 0
            return
        }
        val runtime = state.boundVertexProgram
        if (runtime == null) {
            state.arbErrorPosition = 0
            return
        }
        state.arbErrorPosition = -1
        val loaded = runtime.loadSource(state.gl, text, line)
        // Building the program calls gl.useProgram directly to assign its samplers.
        state.invalidateProgramCache()
        if (!loaded) {
            state.arbErrorPosition = runtime.errorLine
        }
    }

    fun glProgramLocalParameter4fARB(arg0: Int, arg1: Int, arg2: Float, arg3: Float, arg4: Float, arg5: Float) = exec {
        if (arg0 == GL_VERTEX_PROGRAM_ARB) {
            state.boundVertexProgram?.setLocalParameter(arg1, arg2, arg3, arg4, arg5)
        }
    }

    fun glProgramLocalParameter4fvARB(arg0: Int, arg1: Int, arg2: FloatArray?, arg3: Int) {
        if (arg0 != GL_VERTEX_PROGRAM_ARB || arg2 == null || arg3 < 0 || arg3 + 4 > arg2.size) return
        // Read now rather than inside exec - see glTexEnvfv.
        val x = arg2[arg3]; val y = arg2[arg3 + 1]; val z = arg2[arg3 + 2]; val w = arg2[arg3 + 3]
        exec { state.boundVertexProgram?.setLocalParameter(arg1, x, y, z, w) }
    }
}

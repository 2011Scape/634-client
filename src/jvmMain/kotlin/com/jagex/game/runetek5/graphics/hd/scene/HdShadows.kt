/*
 * Not part of the original client: 634HD (toolkit 6). Sun shadows (client.hd.shadows): one depth map
 * from the sun, an orthographic box around the local player, made every frame once the opaque
 * entities have been submitted (HdToolkit.opaqueEntitiesDone) and read by everything drawn after it
 * (the retained scene, the ground, translucent entities; hdShadow in hd_lib.glsl).
 *
 * Casters are the retained scene's objects the client drew this frame (its object table, through
 * shadow_scene.vert) and the models drawn the emulated way before that point - players, NPCs, animated
 * locs - whose draws HdGl records while [recording] (their buffers, element ranges and model-to-world),
 * replayed through shadow.vert. A model drawn before the map is made is not darkened by it.
 */
package com.jagex.game.runetek5.graphics.hd.scene

import com.jagex.game.runetek5.client.features.HdSettings
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.graphics.hd.gl.HdGl
import com.jagex.game.runetek5.graphics.hd.gl.HdShaders
import com.jagex.game.runetek5.graphics.hd.gl.Mat4
import com.jagex.game.runetek5.graphics.hd.gl.WebGLProgram
import java.nio.ByteBuffer
import org.lwjgl.opengl.GL33C

class HdShadows {

    companion object {
        // past the units the retained scene (8-15) and the post passes use
        const val UNIT: Int = 16
        /** Half the side of the box the map covers, in scene units (512 a tile). */
        private const val HALF_EXTENT: Float = 26.0f * 512.0f
        /** How far towards the sun the light's eye sits, and the depth range beyond. */
        private const val DISTANCE: Float = 40000.0f
        private const val MAX_CASTERS: Int = 4096
    }

    private var size: Int = 0
    private var texture: Int = 0
    private var framebuffer: Int = 0
    private var sceneProgram: WebGLProgram? = null
    private var dynamicProgram: WebGLProgram? = null
    private var uSceneMatrix: Int = -1
    private var uDynamicMatrix: Int = -1

    /** Eye space to world space, this frame. */
    private val viewToWorld: FloatArray = FloatArray(16)

    /** World space to the light's clip space, this frame. */
    private val lightViewProjection: FloatArray = FloatArray(16)

    /** Eye space to the light's clip space (lightViewProjection after viewToWorld). */
    @JvmField
    val viewToLight: FloatArray = FloatArray(16)

    /** Eye space to the shadow map's texture space, for the frame block. */
    @JvmField
    val shadowMatrix: FloatArray = FloatArray(16)

    /** Whether the map is made this frame (a sun, shadows on). */
    var active: Boolean = false
        private set

    /** Whether HdGl records the opaque emulated draws as casters now. */
    @JvmField
    var recording: Boolean = false

    /** Recorded draws: position buffer and pointer, element range, model-view. */
    private class Casters {
        var count: Int = 0
        val vertexBuffer: IntArray = IntArray(MAX_CASTERS)
        val size: IntArray = IntArray(MAX_CASTERS)
        val type: IntArray = IntArray(MAX_CASTERS)
        val stride: IntArray = IntArray(MAX_CASTERS)
        val offset: LongArray = LongArray(MAX_CASTERS)
        val elementBuffer: IntArray = IntArray(MAX_CASTERS)
        val elements: IntArray = IntArray(MAX_CASTERS)
        val elementType: IntArray = IntArray(MAX_CASTERS)
        val elementOffset: LongArray = LongArray(MAX_CASTERS)
        val modelView: FloatArray = FloatArray(MAX_CASTERS * 16)
        // indices that were in client memory, copied here (elementBuffer -1, elementOffset into it)
        var indices: ByteBuffer = ByteBuffer.allocateDirect(1 shl 20).order(java.nio.ByteOrder.nativeOrder())
        var indexBuffer: Int = 0
    }

    // the opaque entities of this frame; the translucent ones (drawn after the map is made: players
    // with a cape, for one) recorded into [late] and drawn into the next frame's map from [previous]
    private val casters: Casters = Casters()
    private var late: Casters = Casters()
    private var previous: Casters = Casters()
    private var lateMode: Boolean = false

    /** Whether this frame's map was started (cleared) by an earlier pass. */
    private var rendered: Boolean = false

    /** This frame's casters already in the map. */
    private var drawnCasters: Int = 0

    /** After a pass's translucent entities: the next pass's opaque entities are this frame's casters. */
    fun endTransparentPass() {
        lateMode = false
    }

    private var debugFrame: Int = 0

    /** Indexed draws seen while recording but not recordable (no element buffer), for debugging. */
    @JvmField
    var missed: Int = 0

    private var vao: Int = 0
    private val matrix: FloatArray = FloatArray(16)
    private val matrixBuffer: java.nio.FloatBuffer = org.lwjgl.BufferUtils.createFloatBuffer(16)

    /** Casters drawn into the map last frame, for the debug panel. */
    var lastCasters: Int = 0
        private set

    /**
     * At the start of the frame, once the camera is known: where the map looks from (the sun's world
     * direction, from its eye-space [sunEye] and [viewToWorld]) and the shadow matrix for the frame
     * block. Recording of casters starts.
     */
    fun beginFrame(viewToWorld: FloatArray, sunEye: FloatArray, sunPresent: Boolean) {
        casters.count = 0
        casters.indices.clear()
        rendered = false
        drawnCasters = 0
        val swap: Casters = previous
        previous = late
        late = swap
        late.count = 0
        late.indices.clear()
        lateMode = false
        active = false
        recording = false
        if (HdSettings.shadows == 0 || !sunPresent) {
            return
        }
        val player = PlayerUpdate.localPlayer ?: return
        // the direction towards the sun in world space
        var dx: Float = viewToWorld[0] * sunEye[0] + viewToWorld[4] * sunEye[1] + viewToWorld[8] * sunEye[2]
        var dy: Float = viewToWorld[1] * sunEye[0] + viewToWorld[5] * sunEye[1] + viewToWorld[9] * sunEye[2]
        var dz: Float = viewToWorld[2] * sunEye[0] + viewToWorld[6] * sunEye[1] + viewToWorld[10] * sunEye[2]
        val length: Float = kotlin.math.sqrt(dx * dx + dy * dy + dz * dz)
        if (length < 0.0001f) return
        dx /= length
        dy /= length
        dz /= length
        // a sun at the horizon would stretch shadows across the map: keep it at least 25 degrees up
        // (the game's y grows downwards, so up is -y)
        if (-dy < 0.42f) {
            val flat: Float = kotlin.math.sqrt(dx * dx + dz * dz).coerceAtLeast(0.0001f)
            val scale: Float = kotlin.math.sqrt(1.0f - 0.42f * 0.42f) / flat
            dx *= scale
            dz *= scale
            dy = -0.42f
        }
        ensure()
        // the light's view: looking from the sun towards the player, y up as close to the world's as can be
        val cx: Float = player.x.toFloat()
        val cy: Float = player.y.toFloat()
        val cz: Float = player.z.toFloat()
        val view: FloatArray = lookAt(cx + dx * DISTANCE, cy + dy * DISTANCE, cz + dz * DISTANCE, cx, cy, cz)
        // snap the centre to whole texels in light space, so the map does not shimmer as the player walks
        val texel: Float = HALF_EXTENT * 2.0f / size
        val lx: Float = view[0] * cx + view[4] * cy + view[8] * cz + view[12]
        val ly: Float = view[1] * cx + view[5] * cy + view[9] * cz + view[13]
        val sx: Float = lx - kotlin.math.floor(lx / texel) * texel
        val sy: Float = ly - kotlin.math.floor(ly / texel) * texel
        val projection: FloatArray = Mat4.ortho(-HALF_EXTENT.toDouble(), HALF_EXTENT.toDouble(), -HALF_EXTENT.toDouble(), HALF_EXTENT.toDouble(), 1.0, (DISTANCE * 2.0f).toDouble())
        // centred on the player's light-space position, rounded down to a whole texel
        val centred: FloatArray = Mat4.multiply(Mat4.translate(-(lx - sx), -(ly - sy), 0.0f), view)
        System.arraycopy(Mat4.multiply(projection, centred), 0, lightViewProjection, 0, 16)
        System.arraycopy(Mat4.multiply(lightViewProjection, viewToWorld), 0, viewToLight, 0, 16)
        System.arraycopy(viewToWorld, 0, this.viewToWorld, 0, 16)
        val bias: FloatArray = Mat4.multiply(Mat4.translate(0.5f, 0.5f, 0.5f), Mat4.scale(0.5f, 0.5f, 0.5f))
        System.arraycopy(Mat4.multiply(bias, viewToLight), 0, shadowMatrix, 0, 16)
        active = true
        recording = true
    }

    private fun lookAt(ex: Float, ey: Float, ez: Float, tx: Float, ty: Float, tz: Float): FloatArray {
        var fx: Float = tx - ex
        var fy: Float = ty - ey
        var fz: Float = tz - ez
        val fl: Float = kotlin.math.sqrt(fx * fx + fy * fy + fz * fz)
        fx /= fl; fy /= fl; fz /= fl
        // world up is -y; pick another up when looking straight down it
        var ux: Float = 0.0f
        var uy: Float = -1.0f
        var uz: Float = 0.0f
        if (kotlin.math.abs(fy) > 0.99f) {
            uy = 0.0f
            uz = 1.0f
        }
        var sx: Float = fy * uz - fz * uy
        var sy: Float = fz * ux - fx * uz
        var sz: Float = fx * uy - fy * ux
        val sl: Float = kotlin.math.sqrt(sx * sx + sy * sy + sz * sz)
        sx /= sl; sy /= sl; sz /= sl
        ux = sy * fz - sz * fy
        uy = sz * fx - sx * fz
        uz = sx * fy - sy * fx
        return floatArrayOf(
            sx, ux, -fx, 0.0f,
            sy, uy, -fy, 0.0f,
            sz, uz, -fz, 0.0f,
            -(sx * ex + sy * ey + sz * ez), -(ux * ex + uy * ey + uz * ez), fx * ex + fy * ey + fz * ez, 1.0f,
        )
    }

    /** As [record], for indices in client memory at [address] ([bytes] long): they are copied. */
    fun recordIndices(vertexBuffer: Int, size: Int, type: Int, stride: Int, offset: Long, address: Long, bytes: Int, count: Int, elementType: Int, modelView: FloatArray) {
        val list: Casters = if (lateMode) late else casters
        if (list.count >= MAX_CASTERS || vertexBuffer == 0) return
        if (list.indices.remaining() < bytes) {
            val grown: ByteBuffer = ByteBuffer.allocateDirect(maxOf(list.indices.capacity() * 2, list.indices.position() + bytes)).order(java.nio.ByteOrder.nativeOrder())
            list.indices.flip()
            grown.put(list.indices)
            list.indices = grown
        }
        val at: Int = list.indices.position()
        list.indices.put(org.lwjgl.system.MemoryUtil.memByteBuffer(address, bytes))
        record(vertexBuffer, size, type, stride, offset, -1, count, elementType, at.toLong(), modelView)
    }

    /** Called by HdGl for an indexed triangle draw while [recording]. */
    fun record(vertexBuffer: Int, size: Int, type: Int, stride: Int, offset: Long, elementBuffer: Int, count: Int, elementType: Int, elementOffset: Long, modelView: FloatArray) {
        val list: Casters = if (lateMode) late else casters
        if (list.count >= MAX_CASTERS || vertexBuffer == 0 || elementBuffer == 0) return
        val i: Int = list.count++
        list.vertexBuffer[i] = vertexBuffer
        list.size[i] = size
        list.type[i] = type
        list.stride[i] = stride
        list.offset[i] = offset
        list.elementBuffer[i] = elementBuffer
        list.elements[i] = count
        list.elementType[i] = elementType
        list.elementOffset[i] = elementOffset
        // kept as model-to-world, so last frame's casters stay put when the camera moves
        val at: Int = i * 16
        for (col in 0 until 4) {
            for (row in 0 until 4) {
                var sum: Float = 0.0f
                for (k in 0 until 4) {
                    sum += viewToWorld[k * 4 + row] * modelView[col * 4 + k]
                }
                list.modelView[at + col * 4 + row] = sum
            }
        }
    }

    /** Draws [list]'s casters from [from] on; their copied indices go up first (all of them, each time). */
    private fun drawCasters(list: Casters, from: Int) {
        if (from >= list.count) return
        if (list.indices.position() > 0) {
            if (list.indexBuffer == 0) list.indexBuffer = GL33C.glGenBuffers()
            GL33C.glBindBuffer(GL33C.GL_ELEMENT_ARRAY_BUFFER, list.indexBuffer)
            val bytes: Int = list.indices.position()
            list.indices.flip()
            GL33C.glBufferData(GL33C.GL_ELEMENT_ARRAY_BUFFER, list.indices, GL33C.GL_STREAM_DRAW)
            list.indices.limit(list.indices.capacity())
            list.indices.position(bytes)
        }
        for (i in from until list.count) {
            for (k in 0 until 16) matrix[k] = list.modelView[i * 16 + k]
            val m: FloatArray = Mat4.multiply(lightViewProjection, matrix)
            matrixBuffer.clear()
            matrixBuffer.put(m).flip()
            GL33C.glUniformMatrix4fv(uDynamicMatrix, false, matrixBuffer)
            GL33C.glBindBuffer(GL33C.GL_ARRAY_BUFFER, list.vertexBuffer[i])
            GL33C.glVertexAttribPointer(0, list.size[i], list.type[i], false, list.stride[i], list.offset[i])
            GL33C.glBindBuffer(GL33C.GL_ELEMENT_ARRAY_BUFFER, if (list.elementBuffer[i] == -1) list.indexBuffer else list.elementBuffer[i])
            GL33C.glDrawElements(GL33C.GL_TRIANGLES, list.elements[i], list.elementType[i], list.elementOffset[i])
        }
    }

    private fun ensure() {
        val wanted: Int = HdSettings.shadows
        if (size == wanted && texture != 0) return
        releaseMap()
        size = wanted
        texture = GL33C.glGenTextures()
        GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + UNIT)
        GL33C.glBindTexture(GL33C.GL_TEXTURE_2D, texture)
        GL33C.glTexImage2D(GL33C.GL_TEXTURE_2D, 0, GL33C.GL_DEPTH_COMPONENT24, size, size, 0, GL33C.GL_DEPTH_COMPONENT, GL33C.GL_UNSIGNED_INT, null as ByteBuffer?)
        GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D, GL33C.GL_TEXTURE_MIN_FILTER, GL33C.GL_LINEAR)
        GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D, GL33C.GL_TEXTURE_MAG_FILTER, GL33C.GL_LINEAR)
        GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D, GL33C.GL_TEXTURE_WRAP_S, GL33C.GL_CLAMP_TO_EDGE)
        GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D, GL33C.GL_TEXTURE_WRAP_T, GL33C.GL_CLAMP_TO_EDGE)
        GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D, GL33C.GL_TEXTURE_COMPARE_MODE, GL33C.GL_COMPARE_REF_TO_TEXTURE)
        GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D, GL33C.GL_TEXTURE_COMPARE_FUNC, GL33C.GL_LEQUAL)
        GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + HdGl.state.activeTextureUnit)
        val previous: Int = GL33C.glGetInteger(GL33C.GL_DRAW_FRAMEBUFFER_BINDING)
        framebuffer = GL33C.glGenFramebuffers()
        GL33C.glBindFramebuffer(GL33C.GL_DRAW_FRAMEBUFFER, framebuffer)
        GL33C.glFramebufferTexture2D(GL33C.GL_DRAW_FRAMEBUFFER, GL33C.GL_DEPTH_ATTACHMENT, GL33C.GL_TEXTURE_2D, texture, 0)
        GL33C.glDrawBuffer(GL33C.GL_NONE)
        GL33C.glBindFramebuffer(GL33C.GL_DRAW_FRAMEBUFFER, previous)
        if (sceneProgram == null) {
            sceneProgram = link("shadow_scene.vert")
            dynamicProgram = link("shadow.vert")
            uSceneMatrix = GL33C.glGetUniformLocation(sceneProgram!!.id, "uViewToLight")
            uDynamicMatrix = GL33C.glGetUniformLocation(dynamicProgram!!.id, "uMatrix")
            vao = GL33C.glGenVertexArrays()
        }
    }

    private fun link(vertex: String): WebGLProgram {
        val vs = HdShaders.compile(GL33C.GL_VERTEX_SHADER, HdShaders.source(vertex), vertex)
        val fs = HdShaders.compile(GL33C.GL_FRAGMENT_SHADER, HdShaders.source("shadow.frag"), "shadow.frag")
        val program = HdShaders.link(vs, fs, vertex)
        GL33C.glDeleteShader(vs.id)
        GL33C.glDeleteShader(fs.id)
        return program
    }

    /**
     * Makes the map: the retained objects ([scene] draws them with the program it is handed) and the
     * recorded casters. GL state the emulator relies on is put back; the map is left on UNIT.
     */
    fun render(scene: HdSceneRenderer) {
        // the scene is drawn in a pass per level range: the first clears the map, later ones add to it
        val first: Boolean = !rendered
        rendered = true
        if (!active) {
            recording = false
            lastCasters = 0
            return
        }
        HdGl.flushImmediate()
        val drawFramebuffer: Int = GL33C.glGetInteger(GL33C.GL_DRAW_FRAMEBUFFER_BINDING)
        val viewport = IntArray(4)
        GL33C.glGetIntegerv(GL33C.GL_VIEWPORT, viewport)
        val scissor: Boolean = GL33C.glIsEnabled(GL33C.GL_SCISSOR_TEST)
        val blend: Boolean = GL33C.glIsEnabled(GL33C.GL_BLEND)
        val cull: Boolean = GL33C.glIsEnabled(GL33C.GL_CULL_FACE)
        val depthTest: Boolean = GL33C.glIsEnabled(GL33C.GL_DEPTH_TEST)
        val depthMask: Boolean = GL33C.glGetBoolean(GL33C.GL_DEPTH_WRITEMASK)
        val depthFunc: Int = GL33C.glGetInteger(GL33C.GL_DEPTH_FUNC)
        val colourMask = ByteBuffer.allocateDirect(4)
        GL33C.glGetBooleanv(GL33C.GL_COLOR_WRITEMASK, colourMask)
        val elementBinding: Int = GL33C.glGetInteger(GL33C.GL_ELEMENT_ARRAY_BUFFER_BINDING)
        GL33C.glBindFramebuffer(GL33C.GL_DRAW_FRAMEBUFFER, framebuffer)
        GL33C.glViewport(0, 0, size, size)
        GL33C.glDisable(GL33C.GL_SCISSOR_TEST)
        GL33C.glDisable(GL33C.GL_BLEND)
        GL33C.glDisable(GL33C.GL_CULL_FACE)
        GL33C.glEnable(GL33C.GL_DEPTH_TEST)
        GL33C.glDepthFunc(GL33C.GL_LESS)
        GL33C.glDepthMask(true)
        GL33C.glColorMask(false, false, false, false)
        if (first) {
            GL33C.glClear(GL33C.GL_DEPTH_BUFFER_BIT)
        }
        GL33C.glEnable(GL33C.GL_POLYGON_OFFSET_FILL)
        GL33C.glPolygonOffset(1.5f, 3.0f)
        // the map texture must not be bound for sampling while it is drawn into
        GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + UNIT)
        GL33C.glBindTexture(GL33C.GL_TEXTURE_2D, 0)

        val program: WebGLProgram = sceneProgram!!
        GL33C.glUseProgram(program.id)
        matrixBuffer.clear()
        matrixBuffer.put(viewToLight).flip()
        GL33C.glUniformMatrix4fv(uSceneMatrix, false, matrixBuffer)
        scene.drawShadowCasters(program)

        GL33C.glUseProgram(dynamicProgram!!.id)
        GL33C.glBindVertexArray(vao)
        GL33C.glEnableVertexAttribArray(0)
        drawCasters(casters, drawnCasters)
        drawnCasters = casters.count
        if (first) {
            // last frame's translucent casters (kept in world space): a frame behind
            drawCasters(previous, 0)
        }
        lastCasters = casters.count + previous.count + scene.lastShadowObjects
        if (System.getProperty("client.test.hdDebug") != null && (debugFrame++ % 100) == 0) {
            println("HDDEBUG shadows casters=" + casters.count + " late=" + previous.count + " retained=" + scene.lastShadowObjects + " missed=" + missed)
        }

        GL33C.glDisable(GL33C.GL_POLYGON_OFFSET_FILL)
        GL33C.glBindFramebuffer(GL33C.GL_DRAW_FRAMEBUFFER, drawFramebuffer)
        GL33C.glViewport(viewport[0], viewport[1], viewport[2], viewport[3])
        if (scissor) GL33C.glEnable(GL33C.GL_SCISSOR_TEST)
        if (blend) GL33C.glEnable(GL33C.GL_BLEND)
        if (cull) GL33C.glEnable(GL33C.GL_CULL_FACE)
        if (!depthTest) GL33C.glDisable(GL33C.GL_DEPTH_TEST)
        GL33C.glDepthFunc(depthFunc)
        GL33C.glDepthMask(depthMask)
        GL33C.glColorMask(colourMask.get(0).toInt() != 0, colourMask.get(1).toInt() != 0, colourMask.get(2).toInt() != 0, colourMask.get(3).toInt() != 0)
        val state = HdGl.state
        GL33C.glBindVertexArray(state.vertexArray)
        GL33C.glBindBuffer(GL33C.GL_ARRAY_BUFFER, state.boundArrayBuffer?.id ?: 0)
        GL33C.glBindBuffer(GL33C.GL_ELEMENT_ARRAY_BUFFER, elementBinding)
        // the map, for the receivers
        GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + UNIT)
        GL33C.glBindTexture(GL33C.GL_TEXTURE_2D, texture)
        GL33C.glBindSampler(UNIT, 0)
        GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + state.activeTextureUnit)
        state.invalidateProgramCache()
        // what is drawn from here on (translucent entities) goes into the next frame's map
        lateMode = true
    }

    private fun releaseMap() {
        if (texture != 0) GL33C.glDeleteTextures(texture)
        if (framebuffer != 0) GL33C.glDeleteFramebuffers(framebuffer)
        texture = 0
        framebuffer = 0
    }

    fun release() {
        releaseMap()
        if (sceneProgram != null) GL33C.glDeleteProgram(sceneProgram!!.id)
        if (dynamicProgram != null) GL33C.glDeleteProgram(dynamicProgram!!.id)
        if (vao != 0) GL33C.glDeleteVertexArrays(vao)
        for (list in arrayOf(casters, late, previous)) {
            if (list.indexBuffer != 0) GL33C.glDeleteBuffers(list.indexBuffer)
            list.indexBuffer = 0
        }
        sceneProgram = null
        dynamicProgram = null
        vao = 0
        size = 0
    }
}

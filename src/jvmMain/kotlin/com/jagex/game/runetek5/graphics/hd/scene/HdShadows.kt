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
 *
 * With client.hd.ambientOcclusion, the region's static scene is baked into an occlusion height map
 * (the heights of everything, seen from straight above) when its chunks are built or change, and the
 * moving models recorded each frame go into a small second one around the player. The shaders measure
 * how much of the sky a fragment sees from the two (hdAmbientOcclusion): darker at the foot of walls,
 * under trees and overhangs, on the ground and on moving models too.
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
        const val AO_UNIT: Int = 17
        const val AO_BAKE_UNIT: Int = 22
        const val GROUND_AO_UNIT: Int = 25
        /** How strongly occlusion darkens: 1 - strength in a fully closed spot. */
        private const val AO_STRENGTH: Float = 0.55f
        /** How far below the ground its shadow-casting copy sits (the game's y grows downwards). */
        private const val TERRAIN_DROP: Float = 96.0f
        /** The ground's baked occlusion: samples per tile side, and the ground levels kept at once. */
        private const val GROUND_SAMPLES: Int = 4
        private const val GROUND_SLOTS: Int = 12
        /** The baked map: the whole 104x104-tile scene, and the heights it holds (y grows downwards). */
        private const val BAKE_EXTENT: Float = 104.0f * 512.0f
        private const val BAKE_SIZE: Int = 2048
        private const val BAKE_TOP: Float = -20000.0f
        private const val BAKE_RANGE: Float = 32000.0f
        /**
         * The moving models' height map: its half side and resolution, and the height range it holds.
         * Ten tiles each way: past that, a moving model's shading on what is around it is too small to
         * see, and a ground pixel outside it reads only its baked texture (hdGroundOcclusion).
         */
        private const val AO_HALF_EXTENT: Float = 10.0f * 512.0f
        private const val AO_SIZE: Int = 512
        private const val AO_RANGE: Float = 16000.0f
        /** Half the side of the box the map covers, in scene units (512 a tile). */
        private const val HALF_EXTENT: Float = 26.0f * 512.0f
        /** How far towards the sun the light's eye sits, and the depth range beyond. */
        private const val DISTANCE: Float = 40000.0f
        private const val MAX_CASTERS: Int = 4096
        /** Each caster list's GPU copy of its draws' vertices, and the most one draw may copy. */
        private const val COPY_CAPACITY: Long = 16L * 1024L * 1024L
        private const val MAX_COPY: Long = 512L * 1024L
    }

    /** A depth map drawn from a matrix: the sun's, or the height map from above. */
    private class DepthMap(val unit: Int, val compare: Boolean) {
        var size: Int = 0
        var texture: Int = 0
        var framebuffer: Int = 0
        var active: Boolean = false
        /** World space to the map's clip space. */
        val worldToClip: FloatArray = FloatArray(16)
        /** Eye space to the map's clip space. */
        val viewToClip: FloatArray = FloatArray(16)
        val viewToClipBuffer: java.nio.FloatBuffer = org.lwjgl.BufferUtils.createFloatBuffer(16)

        fun ensure(wanted: Int) {
            if (size == wanted && texture != 0) return
            release()
            size = wanted
            texture = GL33C.glGenTextures()
            GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + unit)
            GL33C.glBindTexture(GL33C.GL_TEXTURE_2D, texture)
            GL33C.glTexImage2D(GL33C.GL_TEXTURE_2D, 0, GL33C.GL_DEPTH_COMPONENT24, size, size, 0, GL33C.GL_DEPTH_COMPONENT, GL33C.GL_UNSIGNED_INT, null as ByteBuffer?)
            GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D, GL33C.GL_TEXTURE_MIN_FILTER, GL33C.GL_LINEAR)
            GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D, GL33C.GL_TEXTURE_MAG_FILTER, GL33C.GL_LINEAR)
            GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D, GL33C.GL_TEXTURE_WRAP_S, GL33C.GL_CLAMP_TO_EDGE)
            GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D, GL33C.GL_TEXTURE_WRAP_T, GL33C.GL_CLAMP_TO_EDGE)
            if (compare) {
                GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D, GL33C.GL_TEXTURE_COMPARE_MODE, GL33C.GL_COMPARE_REF_TO_TEXTURE)
                GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D, GL33C.GL_TEXTURE_COMPARE_FUNC, GL33C.GL_LEQUAL)
            }
            GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + HdGl.state.activeTextureUnit)
            val previous: Int = GL33C.glGetInteger(GL33C.GL_DRAW_FRAMEBUFFER_BINDING)
            framebuffer = GL33C.glGenFramebuffers()
            GL33C.glBindFramebuffer(GL33C.GL_DRAW_FRAMEBUFFER, framebuffer)
            GL33C.glFramebufferTexture2D(GL33C.GL_DRAW_FRAMEBUFFER, GL33C.GL_DEPTH_ATTACHMENT, GL33C.GL_TEXTURE_2D, texture, 0)
            GL33C.glDrawBuffer(GL33C.GL_NONE)
            GL33C.glBindFramebuffer(GL33C.GL_DRAW_FRAMEBUFFER, previous)
        }

        fun release() {
            if (texture != 0) GL33C.glDeleteTextures(texture)
            if (framebuffer != 0) GL33C.glDeleteFramebuffers(framebuffer)
            texture = 0
            framebuffer = 0
            size = 0
        }
    }

    private val sun: DepthMap = DepthMap(UNIT, true)

    // the static casters' part of the sun's map, kept between frames (its own texture unit is unused:
    // it is only ever copied from)
    private val staticMap: DepthMap = DepthMap(24, false)
    private var staticKey: Long = 0L
    private var staticWanted: Long = 1L
    private var staticVersion: Int = -1
    private var staticRedraws: Int = 0
    private var readFramebuffer0: Int = 0
    private val colourMask: ByteBuffer = ByteBuffer.allocateDirect(4)
    private val heights: DepthMap = DepthMap(AO_UNIT, false)

    // the region's baked occlusion height map: all retained objects, drawn when the region's chunks
    // change (at most every BAKE_INTERVAL frames while a region builds), not every frame
    private val baked: DepthMap = DepthMap(AO_BAKE_UNIT, false)
    private var bakedVersion: Int = -1
    private var bakedPlane: Int = -1
    private var framesSinceBake: Int = 1000
    private var bakeProgram: WebGLProgram? = null
    private var bakeDepths: java.nio.FloatBuffer? = null

    /** World space to the baked map's (u, v, depth), for the frame block. */
    @JvmField
    val aoBakeMatrix: FloatArray = FloatArray(16)

    /** The baked map's top and height range, and 1 once a bake exists (frame block). */
    @JvmField
    val aoBakeParams: FloatArray = floatArrayOf(BAKE_TOP, BAKE_RANGE, 0.0f, 0.0f)

    /** Bakes done, for the debug panel. */
    var bakes: Int = 0
        private set

    /**
     * Bakes the region's occlusion height map again if its retained scene changed: straight down onto
     * the whole scene, every retained object placed by its captured model matrix.
     */
    fun bake(scene: HdSceneRenderer) {
        updateFade(scene)
        framesSinceBake++
        groundNanos = 0L
        scene.stepVertexOcclusion(1500000L)
        // once the region is built, and not more than every 2 seconds or so (a bake reads the map back)
        // the player's plane: occluders above it are left out (drawOccludersForBake), so a change of
        // floor bakes again
        val plane: Int = PlayerUpdate.localPlayer?.level?.toInt() ?: 0
        if (!HdSettings.ambientOcclusion || (scene.bakeVersion == bakedVersion && plane == bakedPlane) || scene.building() || framesSinceBake < 120) return
        bakedVersion = scene.bakeVersion
        bakedPlane = plane
        framesSinceBake = 0
        baked.ensure(BAKE_SIZE)
        ensureBakeProgram()
        val w: Float = BAKE_EXTENT
        val toUvd: FloatArray = floatArrayOf(
            1.0f / w, 0.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f / BAKE_RANGE, 0.0f,
            0.0f, 1.0f / w, 0.0f, 0.0f,
            0.0f, 0.0f, -BAKE_TOP / BAKE_RANGE, 1.0f,
        )
        System.arraycopy(toUvd, 0, aoBakeMatrix, 0, 16)
        val toClip: FloatArray = Mat4.multiply(Mat4.multiply(Mat4.translate(-1.0f, -1.0f, -1.0f), Mat4.scale(2.0f, 2.0f, 2.0f)), toUvd)
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
        val elementBinding: Int = GL33C.glGetInteger(GL33C.GL_ELEMENT_ARRAY_BUFFER_BINDING)
        val colourMask = ByteBuffer.allocateDirect(4)
        GL33C.glGetBooleanv(GL33C.GL_COLOR_WRITEMASK, colourMask)
        GL33C.glBindFramebuffer(GL33C.GL_DRAW_FRAMEBUFFER, baked.framebuffer)
        GL33C.glViewport(0, 0, BAKE_SIZE, BAKE_SIZE)
        GL33C.glDisable(GL33C.GL_SCISSOR_TEST)
        GL33C.glDisable(GL33C.GL_BLEND)
        GL33C.glDisable(GL33C.GL_CULL_FACE)
        GL33C.glEnable(GL33C.GL_DEPTH_TEST)
        GL33C.glDepthFunc(GL33C.GL_LESS)
        GL33C.glDepthMask(true)
        GL33C.glColorMask(false, false, false, false)
        GL33C.glClear(GL33C.GL_DEPTH_BUFFER_BIT)
        GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + AO_BAKE_UNIT)
        GL33C.glBindTexture(GL33C.GL_TEXTURE_2D, 0)
        val program: WebGLProgram = bakeProgram!!
        GL33C.glUseProgram(program.id)
        val matrix: java.nio.FloatBuffer = org.lwjgl.BufferUtils.createFloatBuffer(16)
        matrix.put(toClip).flip()
        GL33C.glUniformMatrix4fv(GL33C.glGetUniformLocation(program.id, "uWorldToClip"), false, matrix)
        scene.drawOccludersForBake(program, 23, plane)
        bakes++
        // and into the retained vertices: the height map read back, each vertex's sky measured once
        GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + AO_BAKE_UNIT)
        GL33C.glBindTexture(GL33C.GL_TEXTURE_2D, baked.texture)
        // one buffer, kept and reused: 16 MB of direct memory is not left to the garbage collector
        if (bakeDepths == null) bakeDepths = org.lwjgl.system.MemoryUtil.memAllocFloat(BAKE_SIZE * BAKE_SIZE)
        val depths: java.nio.FloatBuffer = bakeDepths!!
        depths.clear()
        GL33C.glGetTexImage(GL33C.GL_TEXTURE_2D, 0, GL33C.GL_DEPTH_COMPONENT, GL33C.GL_FLOAT, depths)
        scene.bakeVertexOcclusion(depths, BAKE_SIZE, BAKE_TOP, BAKE_RANGE, BAKE_EXTENT)
        aoBakeParams[2] = 1.0f
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
        GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + AO_BAKE_UNIT)
        GL33C.glBindTexture(GL33C.GL_TEXTURE_2D, baked.texture)
        GL33C.glBindSampler(AO_BAKE_UNIT, 0)
        GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + state.activeTextureUnit)
        state.invalidateProgramCache()
    }

    // the terrain as a shadow caster (HdSettings.terrainShadows): plane 0's tile corner heights as a
    // mesh, built once per region; drawn TERRAIN_DROP units lower, so flat ground does not shade itself
    // and only slopes steep enough to rise above the light's ray cast shadows
    private var terrainVao: Int = 0
    private var terrainVbo: Int = 0
    private var terrainIbo: Int = 0
    private var terrainIndices: Int = 0
    private var terrainGround: Any? = null
    private var staticTerrain: Boolean = false
    private var staticCutout: Boolean = false
    private var cutoutProgram: WebGLProgram? = null

    private fun ensureCutoutProgram(): WebGLProgram {
        var program: WebGLProgram? = cutoutProgram
        if (program == null) {
            program = HdShaders.program("caster_cutout")
            GL33C.glUseProgram(program.id)
            GL33C.glUniform1i(GL33C.glGetUniformLocation(program.id, "uMaterials"), HdMaterials.UNIT_MATERIALS)
            GL33C.glUniform1i(GL33C.glGetUniformLocation(program.id, "uSmallLinear"), HdMaterials.UNIT_SMALL_LINEAR)
            GL33C.glUniform1i(GL33C.glGetUniformLocation(program.id, "uLargeLinear"), HdMaterials.UNIT_LARGE_LINEAR)
            GL33C.glUniform1i(GL33C.glGetUniformLocation(program.id, "uFineLinear"), HdMaterials.UNIT_FINE_LINEAR)
            cutoutProgram = program
        }
        return program
    }

    /** Builds the terrain mesh for the scene's plane-0 ground if it changed; false when there is none. */
    private fun ensureTerrain(scene: HdSceneRenderer): Boolean {
        val grounds: Array<com.jagex.game.runetek5.scene.ground.Ground?> = com.jagex.game.runetek5.scene.Scene.aClass65Array2 ?: return false
        val ground: com.jagex.game.runetek5.scene.ground.Ground = grounds[0] ?: return false
        val heights: Array<IntArray?> = ground.tileHeights ?: return false
        if (ground === terrainGround && terrainIndices > 0) {
            return true
        }
        terrainGround = ground
        val columns: Int = minOf(ground.width + 1, heights.size)
        val rows: Int = minOf(ground.length + 1, heights[0]!!.size)
        val vertices: java.nio.FloatBuffer = org.lwjgl.system.MemoryUtil.memAllocFloat(columns * rows * 3)
        val indices: java.nio.IntBuffer = org.lwjgl.system.MemoryUtil.memAllocInt((columns - 1) * (rows - 1) * 6)
        try {
            for (x in 0 until columns) {
                for (z in 0 until rows) {
                    vertices.put(x * 512.0f).put(heights[x]!![z].toFloat()).put(z * 512.0f)
                }
            }
            for (x in 0 until columns - 1) {
                for (z in 0 until rows - 1) {
                    val a: Int = x * rows + z
                    val b: Int = (x + 1) * rows + z
                    indices.put(a).put(b).put(a + 1)
                    indices.put(b).put(b + 1).put(a + 1)
                }
            }
            vertices.flip()
            indices.flip()
            if (terrainVao == 0) {
                terrainVao = GL33C.glGenVertexArrays()
                terrainVbo = GL33C.glGenBuffers()
                terrainIbo = GL33C.glGenBuffers()
            }
            GL33C.glBindVertexArray(terrainVao)
            GL33C.glBindBuffer(GL33C.GL_ARRAY_BUFFER, terrainVbo)
            GL33C.glBufferData(GL33C.GL_ARRAY_BUFFER, vertices, GL33C.GL_STATIC_DRAW)
            GL33C.glEnableVertexAttribArray(0)
            GL33C.glVertexAttribPointer(0, 3, GL33C.GL_FLOAT, false, 12, 0L)
            GL33C.glBindBuffer(GL33C.GL_ELEMENT_ARRAY_BUFFER, terrainIbo)
            GL33C.glBufferData(GL33C.GL_ELEMENT_ARRAY_BUFFER, indices, GL33C.GL_STATIC_DRAW)
            terrainIndices = indices.limit()
            // the emulator's vertex array and element buffer are put back after the shadow pass (render)
        } finally {
            org.lwjgl.system.MemoryUtil.memFree(vertices)
            org.lwjgl.system.MemoryUtil.memFree(indices)
        }
        return terrainIndices > 0
    }

    private fun drawTerrain(map: DepthMap) {
        ensurePrograms()
        val program: WebGLProgram = dynamicProgram!!
        GL33C.glUseProgram(program.id)
        val matrix: FloatArray = Mat4.multiply(map.worldToClip, Mat4.translate(0.0f, TERRAIN_DROP, 0.0f))
        map.viewToClipBuffer.clear()
        map.viewToClipBuffer.put(matrix).flip()
        GL33C.glUniformMatrix4fv(uDynamicMatrix, false, map.viewToClipBuffer)
        GL33C.glBindVertexArray(terrainVao)
        GL33C.glDrawElements(GL33C.GL_TRIANGLES, terrainIndices, GL33C.GL_UNSIGNED_INT, 0L)
    }

    private fun ensureBakeProgram() {
        if (bakeProgram == null) {
            val vs = HdShaders.compile(GL33C.GL_VERTEX_SHADER, HdShaders.source("ao_bake.vert"), "ao_bake.vert")
            val fs = HdShaders.compile(GL33C.GL_FRAGMENT_SHADER, HdShaders.source("shadow.frag"), "shadow.frag")
            bakeProgram = HdShaders.link(vs, fs, "ao_bake")
            GL33C.glDeleteShader(vs.id)
            GL33C.glDeleteShader(fs.id)
        }
    }

    // the chunk the sun's map is centred on (the player's)
    private var sunChunkX: Int = 0
    private var sunChunkZ: Int = 0

    // the ground's occlusion, baked from the height map into a texture per ground level (GROUND_SAMPLES
    // a tile side), so a ground pixel reads one texel instead of measuring the sky itself; computed on
    // the CPU a little each frame after a bake, and used once complete
    private val groundOwner: Array<Any?> = arrayOfNulls(GROUND_SLOTS)
    private val groundTexture: IntArray = IntArray(GROUND_SLOTS)
    private val groundVersion: IntArray = IntArray(GROUND_SLOTS) { -1 }
    private val groundCursor: IntArray = IntArray(GROUND_SLOTS)
    private val groundReady: BooleanArray = BooleanArray(GROUND_SLOTS)
    // whether the slot's texture holds an earlier bake of its own ground: drawn until the new one is done
    private val groundHasTexture: BooleanArray = BooleanArray(GROUND_SLOTS)
    // the last frame a ground drew without occlusion, its first texture not ready yet
    private var groundWaitFrame: Int = -1
    private val groundUsed: IntArray = IntArray(GROUND_SLOTS)
    private val groundValues: Array<ByteBuffer?> = arrayOfNulls(GROUND_SLOTS)
    private var groundFrame: Int = 0
    // the time the grounds' occlusion took this frame: one budget shared by every ground
    private var groundNanos: Long = 0L

    /**
     * Called before [ground] draws, for its uHdGround: 1 without occlusion's switch; 2 with its baked
     * occlusion texture bound on GROUND_AO_UNIT - the current bake's, or while that is computed the
     * previous one's, so a new bake never changes the ground's look until it is done; 3 when it has
     * no texture yet (a region just loaded): no occlusion, which fades in once everything is baked
     * (aoFade). The grounds compute a little more each frame, for at most [budgetNanos] between them.
     */
    fun bindGroundOcclusion(ground: com.jagex.game.runetek5.scene.ground.Ground, budgetNanos: Long): Int {
        if (!HdSettings.ambientOcclusion) return 1
        val depths: java.nio.FloatBuffer? = bakeDepths
        if (depths == null || aoBakeParams[2] < 0.5f || ground.tileHeights == null) {
            groundWaitFrame = frameCount
            return 3
        }
        groundFrame++
        var slot: Int = -1
        for (i in 0 until GROUND_SLOTS) {
            if (groundOwner[i] === ground) slot = i
        }
        if (slot < 0) {
            // the slot used least recently: the grounds of a region left behind
            slot = 0
            for (i in 1 until GROUND_SLOTS) {
                if (groundUsed[i] < groundUsed[slot]) slot = i
            }
            groundOwner[slot] = ground
            groundVersion[slot] = -1
            groundHasTexture[slot] = false
        }
        groundUsed[slot] = groundFrame
        val width: Int = ground.width * GROUND_SAMPLES
        val length: Int = ground.length * GROUND_SAMPLES
        if (groundVersion[slot] != bakes) {
            groundVersion[slot] = bakes
            groundCursor[slot] = 0
            groundReady[slot] = false
            var values: ByteBuffer? = groundValues[slot]
            if (values == null || values.capacity() < width * length) {
                if (values != null) org.lwjgl.system.MemoryUtil.memFree(values)
                values = org.lwjgl.system.MemoryUtil.memAlloc(width * length)
                groundValues[slot] = values
            }
        }
        if (!groundReady[slot]) {
            val values: ByteBuffer = groundValues[slot]!!
            val heights: Array<IntArray?> = ground.tileHeights
            val start: Long = System.nanoTime()
            val step: Float = 512.0f / GROUND_SAMPLES
            val scale: Float = BAKE_SIZE / BAKE_EXTENT
            var cursor: Int = groundCursor[slot]
            val total: Int = width * length
            while (cursor < total) {
                if ((cursor and 63) == 0 && groundNanos + System.nanoTime() - start > budgetNanos) break
                val i: Int = cursor % width
                val j: Int = cursor / width
                val x: Float = (i + 0.5f) * step
                val z: Float = (j + 0.5f) * step
                // the ground's height there, between its tile corners
                val tileX: Int = minOf(i / GROUND_SAMPLES, heights.size - 2)
                val tileZ: Int = minOf(j / GROUND_SAMPLES, heights[0]!!.size - 2)
                val fx: Float = x / 512.0f - tileX
                val fz: Float = z / 512.0f - tileZ
                val h0: Float = heights[tileX]!![tileZ] * (1.0f - fx) + heights[tileX + 1]!![tileZ] * fx
                val h1: Float = heights[tileX]!![tileZ + 1] * (1.0f - fx) + heights[tileX + 1]!![tileZ + 1] * fx
                val y: Float = h0 * (1.0f - fz) + h1 * fz
                val ao: Float = HdSceneRenderer.skyVisibility(depths, BAKE_SIZE, scale, BAKE_TOP, BAKE_RANGE, x, y, z, 0.0f, -1.0f, 0.0f)
                values.put(cursor, (ao * 255.0f + 0.5f).toInt().coerceIn(0, 255).toByte())
                cursor++
            }
            groundCursor[slot] = cursor
            groundNanos += System.nanoTime() - start
            if (cursor < total) {
                if (!groundHasTexture[slot]) {
                    groundWaitFrame = frameCount
                    return 3
                }
                bindGroundTexture(slot)
                return 2
            }
            groundReady[slot] = true
            groundHasTexture[slot] = true
            if (groundTexture[slot] == 0) groundTexture[slot] = GL33C.glGenTextures()
            GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + GROUND_AO_UNIT)
            GL33C.glBindTexture(GL33C.GL_TEXTURE_2D, groundTexture[slot])
            GL33C.glPixelStorei(GL33C.GL_UNPACK_ALIGNMENT, 1)
            values.limit(total).position(0)
            GL33C.glTexImage2D(GL33C.GL_TEXTURE_2D, 0, GL33C.GL_R8, width, length, 0, GL33C.GL_RED, GL33C.GL_UNSIGNED_BYTE, values)
            values.clear()
            GL33C.glPixelStorei(GL33C.GL_UNPACK_ALIGNMENT, 4)
            GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D, GL33C.GL_TEXTURE_MIN_FILTER, GL33C.GL_LINEAR)
            GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D, GL33C.GL_TEXTURE_MAG_FILTER, GL33C.GL_LINEAR)
            GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D, GL33C.GL_TEXTURE_WRAP_S, GL33C.GL_CLAMP_TO_EDGE)
            GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D, GL33C.GL_TEXTURE_WRAP_T, GL33C.GL_CLAMP_TO_EDGE)
            GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + HdGl.state.activeTextureUnit)
            if (System.getProperty("client.test.hdDebug") != null) {
                println("HDDEBUG ground occlusion baked: " + width + "x" + length + " (slot " + slot + ")")
            }
        }
        bindGroundTexture(slot)
        return 2
    }

    private fun bindGroundTexture(slot: Int) {
        GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + GROUND_AO_UNIT)
        GL33C.glBindTexture(GL33C.GL_TEXTURE_2D, groundTexture[slot])
        GL33C.glBindSampler(GROUND_AO_UNIT, 0)
        GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + HdGl.state.activeTextureUnit)
    }

    // occlusion fades in after a region load (0 to 1 over about half a second) once the new bake, the
    // retained vertices and every ground drawn are done: until then the old bake does not fit the new
    // scene, and new geometry has none; without the fade it would all darken at once
    private var aoFade: Float = 1.0f
    private var frameCount: Int = 0
    private var seenBuilds: Int = -1
    private var fadeLast: Long = 0L

    /** Advances the fade; [scene]'s region builds restart it. Called once a frame from bake(). */
    private fun updateFade(scene: HdSceneRenderer) {
        frameCount++
        val now: Long = System.nanoTime()
        val seconds: Float = ((now - fadeLast).coerceIn(0L, 100000000L)).toFloat() / 1.0e9f
        fadeLast = now
        if (scene.builds != seenBuilds) {
            seenBuilds = scene.builds
            aoFade = 0.0f
        }
        val ready: Boolean = bakedVersion == scene.bakeVersion && !scene.building() && !scene.occlusionPending() && groundWaitFrame < frameCount - 1
        if (ready && aoFade < 1.0f) {
            aoFade = minOf(1.0f, aoFade + seconds * 2.0f)
            if (aoFade >= 1.0f && System.getProperty("client.test.hdDebug") != null) {
                println("HDDEBUG occlusion faded in, frame " + frameCount)
            }
        }
        aoParams[1] = AO_STRENGTH * aoFade
    }

    /** Binds the baked map for the frame's receivers (it stays bound between bakes otherwise). */
    fun bindBaked() {
        if (baked.texture == 0) return
        GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + AO_BAKE_UNIT)
        GL33C.glBindTexture(GL33C.GL_TEXTURE_2D, baked.texture)
        GL33C.glBindSampler(AO_BAKE_UNIT, 0)
        GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + HdGl.state.activeTextureUnit)
    }
    private var sceneProgram: WebGLProgram? = null
    private var dynamicProgram: WebGLProgram? = null
    private var uSceneMatrix: Int = -1
    private var uDynamicMatrix: Int = -1

    /** Eye space to world space, this frame. */
    private val viewToWorld: FloatArray = FloatArray(16)

    /** Eye space to the shadow map's texture space, for the frame block. */
    @JvmField
    val shadowMatrix: FloatArray = FloatArray(16)

    /** Whether the sun's map is made this frame (a sun, shadows on). */
    val active: Boolean get() = sun.active

    /** Whether the height map is made this frame (ambient occlusion on). */
    val aoActive: Boolean get() = heights.active

    /** World space to the height map's texture space (u, v, depth), for the frame block. */
    @JvmField
    val aoMatrix: FloatArray = FloatArray(16)

    /** The height map's parameters for the frame block: height range, strength, uv per scene unit. */
    @JvmField
    val aoParams: FloatArray = floatArrayOf(AO_RANGE, AO_STRENGTH, 1.0f / (AO_HALF_EXTENT * 2.0f), 0.0f)

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
        // drawn at the local player's position: a sun shadow caster, but not an occluder for AO (the
        // player would otherwise darken the ground under it as it walks)
        val atPlayer: BooleanArray = BooleanArray(MAX_CASTERS)
        // the buffers' write counts when recorded (HdGl.bufferVersion): a buffer written since holds
        // other data now, and the caster is skipped
        val vertexVersion: IntArray = IntArray(MAX_CASTERS)
        val offsetBase: LongArray = LongArray(MAX_CASTERS)
        val elementVersion: IntArray = IntArray(MAX_CASTERS)
        // indices that were in client memory, copied here (elementBuffer -1, elementOffset into it)
        var indices: ByteBuffer = org.lwjgl.system.MemoryUtil.memAlloc(1 shl 20)
        var indexBuffer: Int = 0
        // vertex data copied on the GPU when the caster was drawn (its own buffer may be rewritten
        // later in the frame by the next model sharing it)
        var copyBuffer: Int = 0
        var copyUsed: Long = 0L
    }

    // the opaque entities of this frame; the translucent ones (drawn after the map is made: players
    // with a cape, for one) recorded into [late] and drawn into the next frame's map from [previous]
    private val casters: Casters = Casters()
    private var late: Casters = Casters()
    private var previous: Casters = Casters()
    private var lateMode: Boolean = false

    // the stream's orphan count when the late casters began to be recorded
    private var lateOrphans: Int = 0

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
    /** The environment's sun pitch in degrees (environments.json), NaN for the game's, at least 50. */
    @JvmField
    var sunPitch: Float = Float.NaN

    /** The environment's sun yaw in degrees (environments.json; 0 north, 90 east), NaN for the game's. */
    @JvmField
    var sunYaw: Float = Float.NaN

    private var debugFrames: Int = 0
    private var debugInactive: Int = 0

    fun beginFrame(viewToWorld: FloatArray, sunEye: FloatArray, sunPresent: Boolean) {
        if (System.getProperty("client.test.hdDebug") != null && HdSettings.shadows != 0) {
            debugFrames++
            if (!sunPresent) debugInactive++
            if (debugFrames % 200 == 0) println("HDDEBUG shadow frames " + debugFrames + ", without a sun " + debugInactive)
        }
        casters.count = 0
        casters.copyUsed = 0L
        casters.indices.clear()
        rendered = false
        drawnCasters = 0
        val swap: Casters = previous
        previous = late
        late = swap
        // last frame's translucent casters drew from the stream; if it was orphaned since, their
        // ranges hold something else now, and they are dropped (a frame without their shadows)

        // the references: only last frame's late casters carry over (they are drawn this frame)
        referencedBy.clear()
        for (i in 0 until previous.count) {
            reference(previous.vertexBuffer[i], previous, i)
            if (previous.elementBuffer[i] > 0) reference(previous.elementBuffer[i], previous, i)
        }
        lateOrphans = HdDynamicStream.orphans
        late.count = 0
        late.copyUsed = 0L
        late.indices.clear()
        lateMode = false
        sun.active = false
        heights.active = false
        recording = false
        val player = PlayerUpdate.localPlayer ?: return
        System.arraycopy(viewToWorld, 0, this.viewToWorld, 0, 16)
        if (HdSettings.ambientOcclusion) {
            beginHeights(player.x.toFloat(), player.y.toFloat(), player.z.toFloat(), viewToWorld)
        }
        if (HdSettings.shadows != 0 && sunPresent) {
            beginSun(player.x.toFloat(), player.y.toFloat(), player.z.toFloat(), viewToWorld, sunEye)
        }
        recording = sun.active || heights.active
        if (recording) {
            ensurePrograms()
        }
    }

    /** The height map: straight down onto a square around the player; u along x, v along z, depth along y. */
    private fun beginHeights(cx: Float, cy: Float, cz: Float, viewToWorld: FloatArray) {
        heights.ensure(AO_SIZE)
        val texel: Float = AO_HALF_EXTENT * 2.0f / AO_SIZE
        val x0: Float = kotlin.math.floor((cx - AO_HALF_EXTENT) / texel) * texel
        val z0: Float = kotlin.math.floor((cz - AO_HALF_EXTENT) / texel) * texel
        val top: Float = cy - AO_RANGE * 0.5f
        aoParams[3] = top
        val w: Float = AO_HALF_EXTENT * 2.0f
        // world to (u, v, depth) in 0..1, column-major
        val toUvd: FloatArray = floatArrayOf(
            1.0f / w, 0.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f / AO_RANGE, 0.0f,
            0.0f, 1.0f / w, 0.0f, 0.0f,
            -x0 / w, -z0 / w, -top / AO_RANGE, 1.0f,
        )
        System.arraycopy(toUvd, 0, aoMatrix, 0, 16)
        // and to clip space for drawing it: 2 * uvd - 1
        val toClip: FloatArray = Mat4.multiply(Mat4.multiply(Mat4.translate(-1.0f, -1.0f, -1.0f), Mat4.scale(2.0f, 2.0f, 2.0f)), toUvd)
        System.arraycopy(toClip, 0, heights.worldToClip, 0, 16)
        System.arraycopy(Mat4.multiply(toClip, viewToWorld), 0, heights.viewToClip, 0, 16)
        heights.active = true
    }

    private fun beginSun(playerX: Float, playerY: Float, playerZ: Float, viewToWorld: FloatArray, sunEye: FloatArray) {
        // the map's centre moves in steps of four tiles, not with every step the player takes, so the
        // static casters drawn into it stay valid (and are drawn again only at a step: staticMap)
        val cx: Float = kotlin.math.floor(playerX / 2048.0f) * 2048.0f + 1024.0f
        val cz: Float = kotlin.math.floor(playerZ / 2048.0f) * 2048.0f + 1024.0f
        val cy: Float = kotlin.math.floor(playerY / 1024.0f) * 1024.0f
        sunChunkX = (cx.toInt() shr 9) shr 3
        sunChunkZ = (cz.toInt() shr 9) shr 3
        // the direction towards the sun in world space
        var dx: Float = viewToWorld[0] * sunEye[0] + viewToWorld[4] * sunEye[1] + viewToWorld[8] * sunEye[2]
        var dy: Float = viewToWorld[1] * sunEye[0] + viewToWorld[5] * sunEye[1] + viewToWorld[9] * sunEye[2]
        var dz: Float = viewToWorld[2] * sunEye[0] + viewToWorld[6] * sunEye[1] + viewToWorld[10] * sunEye[2]
        val length: Float = kotlin.math.sqrt(dx * dx + dy * dy + dz * dz)
        if (length < 0.0001f) return
        dx /= length
        dy /= length
        dz /= length
        // the game's light comes in low, and its shadows would cover whole courtyards: the sun is at
        // least 50 degrees up, or exactly the environment's pitch (environments.json sun.pitch); the
        // game's direction is kept around the compass (the game's y grows downwards, so up is -y)
        if (!sunYaw.isNaN()) {
            // the environment's compass direction towards the sun (its pitch is applied below)
            val radians: Double = Math.toRadians(sunYaw.toDouble())
            dx = Math.sin(radians).toFloat()
            dz = Math.cos(radians).toFloat()
        }
        val pitch: Float = if (sunPitch.isNaN()) maxOf(50.0f, Math.toDegrees(Math.asin((-dy).toDouble().coerceIn(-1.0, 1.0))).toFloat()) else sunPitch
        val up: Float = kotlin.math.sin(Math.toRadians(pitch.toDouble())).toFloat()
        val flat: Float = kotlin.math.sqrt(dx * dx + dz * dz).coerceAtLeast(0.0001f)
        val scale: Float = kotlin.math.sqrt(1.0f - up * up) / flat
        dx *= scale
        dz *= scale
        dy = -up
        sun.ensure(HdSettings.shadows)
        staticWanted = (cx.toLong() shl 40) xor (cz.toLong() shl 20) xor cy.toLong() xor (Math.round(dx * 1000.0f).toLong() shl 52) xor (Math.round(dy * 1000.0f).toLong() shl 44) xor (Math.round(dz * 1000.0f).toLong() shl 36) xor sun.size.toLong()
        val size: Int = sun.size
        // the light's view: looking from the sun towards the player, y up as close to the world's as can be
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
        System.arraycopy(Mat4.multiply(projection, centred), 0, sun.worldToClip, 0, 16)
        System.arraycopy(Mat4.multiply(sun.worldToClip, viewToWorld), 0, sun.viewToClip, 0, 16)
        val bias: FloatArray = Mat4.multiply(Mat4.translate(0.5f, 0.5f, 0.5f), Mat4.scale(0.5f, 0.5f, 0.5f))
        System.arraycopy(Mat4.multiply(bias, sun.viewToClip), 0, shadowMatrix, 0, 16)
        sun.active = true
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
            val used: Int = list.indices.position()
            list.indices.clear()
            list.indices = org.lwjgl.system.MemoryUtil.memRealloc(list.indices, maxOf(list.indices.capacity() * 2, used + bytes))
            list.indices.position(used)
        }
        val at: Int = list.indices.position()
        list.indices.put(org.lwjgl.system.MemoryUtil.memByteBuffer(address, bytes))
        record(vertexBuffer, size, type, stride, offset, -1, count, elementType, at.toLong(), modelView)
    }

    // copy on write: the casters (list, index) drawn from each buffer, this frame and last frame's late
    // ones; when HdGl is about to write a buffer, those casters' data is copied out first
    private val referencedBy = HashMap<Int, ArrayList<Any>>()

    private fun reference(buffer: Int, list: Casters, index: Int) {
        if (HdDynamicStream.handles.contains(buffer)) return
        var users: ArrayList<Any>? = referencedBy[buffer]
        if (users == null) {
            users = ArrayList()
            referencedBy[buffer] = users
        }
        users.add(list)
        users.add(index)
    }

    /**
     * Called by a stream before it replaces its storage: casters drawn from stream buffer [buffer]
     * (this frame's and last frame's late ones) get copies of their ranges ([written]: offset to
     * size), so they still cast after the stream moves on.
     */
    fun beforeStreamOrphan(buffer: Int, written: java.util.TreeMap<Int, Int>) {
        GL33C.glBindBuffer(GL33C.GL_COPY_READ_BUFFER, buffer)
        for (list in arrayOf(previous, late, casters)) {
            for (i in 0 until list.count) {
                if (list.vertexBuffer[i] == buffer) {
                    val range = written.floorEntry(list.offset[i].toInt())
                    val bytes: Long = if (range == null) -1L else range.key.toLong() + range.value - list.offset[i]
                    if (!copyOut(list, i, true, bytes)) list.elements[i] = 0
                }
                if (list.elementBuffer[i] == buffer) {
                    val bytes: Long = list.elements[i].toLong() * (if (list.elementType[i] == GL33C.GL_UNSIGNED_BYTE) 1L else if (list.elementType[i] == GL33C.GL_UNSIGNED_SHORT) 2L else 4L)
                    if (!copyOut(list, i, false, bytes)) list.elements[i] = 0
                }
            }
        }
    }

    /** Copies caster [i]'s vertices (or indices) from the buffer bound to COPY_READ into the list's copy buffer. */
    private fun copyOut(list: Casters, i: Int, vertices: Boolean, bytes: Long): Boolean {
        if (bytes <= 0L || bytes > MAX_COPY) return false
        if (list.copyBuffer == 0) {
            list.copyBuffer = GL33C.glGenBuffers()
            GL33C.glBindBuffer(GL33C.GL_COPY_WRITE_BUFFER, list.copyBuffer)
            GL33C.glBufferData(GL33C.GL_COPY_WRITE_BUFFER, COPY_CAPACITY, GL33C.GL_STREAM_COPY)
        }
        val at: Long = (list.copyUsed + 255L) and 255L.inv()
        if (at + bytes > COPY_CAPACITY) return false
        GL33C.glBindBuffer(GL33C.GL_COPY_WRITE_BUFFER, list.copyBuffer)
        val from: Long = if (vertices) list.offset[i] else list.elementOffset[i]
        GL33C.glCopyBufferSubData(GL33C.GL_COPY_READ_BUFFER, GL33C.GL_COPY_WRITE_BUFFER, from, at, bytes)
        list.copyUsed = at + bytes
        if (vertices) {
            list.vertexBuffer[i] = list.copyBuffer
            list.offset[i] = at
            list.vertexVersion[i] = 0
        } else {
            list.elementBuffer[i] = list.copyBuffer
            list.elementOffset[i] = at
            list.elementVersion[i] = 0
        }
        return true
    }

    /** Called by HdGl before it writes GL buffer [buffer]: casters drawn from it get their own copy. */
    fun beforeWrite(buffer: Int) {
        val users: ArrayList<Any> = referencedBy.remove(buffer) ?: return
        GL33C.glBindBuffer(GL33C.GL_COPY_READ_BUFFER, buffer)
        val bufferBytes: Long = GL33C.glGetBufferParameteri(GL33C.GL_COPY_READ_BUFFER, GL33C.GL_BUFFER_SIZE).toLong()
        var k: Int = 0
        while (k < users.size) {
            val list: Casters = users[k] as Casters
            val i: Int = users[k + 1] as Int
            k += 2
            if (i >= list.count) continue
            if (list.copyBuffer == 0) {
                list.copyBuffer = GL33C.glGenBuffers()
                GL33C.glBindBuffer(GL33C.GL_COPY_WRITE_BUFFER, list.copyBuffer)
                GL33C.glBufferData(GL33C.GL_COPY_WRITE_BUFFER, COPY_CAPACITY, GL33C.GL_STREAM_COPY)
            }
            GL33C.glBindBuffer(GL33C.GL_COPY_WRITE_BUFFER, list.copyBuffer)
            if (list.vertexBuffer[i] == buffer) {
                val bytes: Long = bufferBytes - list.offset[i]
                val at: Long = (list.copyUsed + 255L) and 255L.inv()
                if (bytes > 0L && bytes <= MAX_COPY && at + bytes <= COPY_CAPACITY) {
                    GL33C.glCopyBufferSubData(GL33C.GL_COPY_READ_BUFFER, GL33C.GL_COPY_WRITE_BUFFER, list.offset[i], at, bytes)
                    list.copyUsed = at + bytes
                    list.vertexBuffer[i] = list.copyBuffer
                    list.offset[i] = at
                    list.vertexVersion[i] = 0
                }
            }
            if (list.elementBuffer[i] == buffer) {
                val bytes: Long = list.elements[i].toLong() * (if (list.elementType[i] == GL33C.GL_UNSIGNED_BYTE) 1L else if (list.elementType[i] == GL33C.GL_UNSIGNED_SHORT) 2L else 4L)
                val at: Long = (list.copyUsed + 255L) and 255L.inv()
                if (bytes <= MAX_COPY && at + bytes <= COPY_CAPACITY) {
                    GL33C.glCopyBufferSubData(GL33C.GL_COPY_READ_BUFFER, GL33C.GL_COPY_WRITE_BUFFER, list.elementOffset[i], at, bytes)
                    list.copyUsed = at + bytes
                    list.elementBuffer[i] = list.copyBuffer
                    list.elementOffset[i] = at
                    list.elementVersion[i] = 0
                }
            }
        }
    }

    /** Called by HdGl for an indexed triangle draw while [recording]. */
    fun record(vertexBuffer: Int, size: Int, type: Int, stride: Int, offset: Long, elementBuffer: Int, count: Int, elementType: Int, elementOffset: Long, modelView: FloatArray) {
        if (PARTS.contains("norecord")) return
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
        list.vertexVersion[i] = HdGl.bufferVersion(vertexBuffer)
        list.elementVersion[i] = if (elementBuffer > 0) HdGl.bufferVersion(elementBuffer) else 0
        // nothing is copied now: only if a buffer it uses is about to be written (beforeWrite)
        reference(vertexBuffer, list, i)
        if (elementBuffer > 0) reference(elementBuffer, list, i)
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
        // a model placed at the local player's position is the player (its body, worn items, cape)
        val player = PlayerUpdate.localPlayer
        list.atPlayer[i] = player != null && kotlin.math.abs(list.modelView[at + 12] - player.x) < 48.0f && kotlin.math.abs(list.modelView[at + 14] - player.z) < 48.0f
    }

    /** Draws [list]'s casters from [from] on; their copied indices go up first (all of them, each time). */
    private fun drawCasters(list: Casters, from: Int, map: DepthMap, upload: Boolean) {
        if (from >= list.count) return
        if (upload && list.indices.position() > 0) {
            if (list.indexBuffer == 0) list.indexBuffer = GL33C.glGenBuffers()
            GL33C.glBindBuffer(GL33C.GL_ELEMENT_ARRAY_BUFFER, list.indexBuffer)
            val bytes: Int = list.indices.position()
            list.indices.flip()
            GL33C.glBufferData(GL33C.GL_ELEMENT_ARRAY_BUFFER, list.indices, GL33C.GL_STREAM_DRAW)
            list.indices.limit(list.indices.capacity())
            list.indices.position(bytes)
        }
        for (i in from until list.count) {
            if (map === heights && list.atPlayer[i]) continue
            if (stale(list.vertexBuffer[i], list.vertexVersion[i]) || (list.elementBuffer[i] > 0 && stale(list.elementBuffer[i], list.elementVersion[i]))) {
                // rewritten since it was drawn (an animated model updated in place): its data is not the caster's
                staleCasters++
                if (System.getProperty("client.test.hdDebug") != null && staleCasters % 5000 == 1) {
                    GL33C.glBindBuffer(GL33C.GL_COPY_READ_BUFFER, list.vertexBuffer[i])
                    val vs = GL33C.glGetBufferParameteri(GL33C.GL_COPY_READ_BUFFER, GL33C.GL_BUFFER_SIZE)
                    GL33C.glBindBuffer(GL33C.GL_COPY_READ_BUFFER, Math.max(0, list.elementBuffer[i]))
                    val es = if (list.elementBuffer[i] > 0) GL33C.glGetBufferParameteri(GL33C.GL_COPY_READ_BUFFER, GL33C.GL_BUFFER_SIZE) else -1
                    println("HDDEBUG stale caster: vertex buffer " + list.vertexBuffer[i] + " (" + vs + " bytes, offset " + list.offset[i] + ", versions " + list.vertexVersion[i] + "->" + HdGl.bufferVersion(list.vertexBuffer[i]) + "), elements " + list.elementBuffer[i] + " (" + es + " bytes, " + list.elementVersion[i] + "->" + (if (list.elementBuffer[i] > 0) HdGl.bufferVersion(list.elementBuffer[i]) else 0) + ")")
                }
                continue
            }
            for (k in 0 until 16) matrix[k] = list.modelView[i * 16 + k]
            val m: FloatArray = Mat4.multiply(map.worldToClip, matrix)
            matrixBuffer.clear()
            matrixBuffer.put(m).flip()
            GL33C.glUniformMatrix4fv(uDynamicMatrix, false, matrixBuffer)
            GL33C.glBindBuffer(GL33C.GL_ARRAY_BUFFER, list.vertexBuffer[i])
            GL33C.glVertexAttribPointer(0, list.size[i], list.type[i], false, list.stride[i], list.offset[i])
            GL33C.glBindBuffer(GL33C.GL_ELEMENT_ARRAY_BUFFER, if (list.elementBuffer[i] == -1) list.indexBuffer else list.elementBuffer[i])
            GL33C.glDrawElements(GL33C.GL_TRIANGLES, list.elements[i], list.elementType[i], list.elementOffset[i])
        }
    }

    // debugging: which casters go into the maps (client.test.hdShadowParts=static,now,late)
    private val PARTS: String = System.getProperty("client.test.hdShadowParts") ?: "static,now,late"

    /** Whether [buffer] was written since [version]; stream buffers only append, and are never stale within a frame. */
    private fun stale(buffer: Int, version: Int): Boolean = !HdDynamicStream.handles.contains(buffer) && HdGl.bufferVersion(buffer) != version

    /** Casters skipped because their buffers were rewritten after they were drawn (debugging). */
    private var staleCasters: Int = 0

    private var checks: Int = 0
    private var anomalies: Int = 0

    /** Debugging: the sun map's depth over the player against where the player is; logs a map that covers everything. */
    private fun checkMap(first: Boolean) {
        val player = PlayerUpdate.localPlayer ?: return
        val m: FloatArray = sun.worldToClip
        val x: Float = player.x.toFloat()
        val y: Float = player.y.toFloat() - 600.0f
        val z: Float = player.z.toFloat()
        val cx: Float = m[0] * x + m[4] * y + m[8] * z + m[12]
        val cy: Float = m[1] * x + m[5] * y + m[9] * z + m[13]
        val cz: Float = m[2] * x + m[6] * y + m[10] * z + m[14]
        val px: Int = ((cx * 0.5f + 0.5f) * sun.size).toInt()
        val py: Int = ((cy * 0.5f + 0.5f) * sun.size).toInt()
        val expected: Float = cz * 0.5f + 0.5f
        val previousRead: Int = GL33C.glGetInteger(GL33C.GL_READ_FRAMEBUFFER_BINDING)
        GL33C.glBindFramebuffer(GL33C.GL_READ_FRAMEBUFFER, sun.framebuffer)
        val depth = FloatArray(1)
        GL33C.glReadPixels(px.coerceIn(0, sun.size - 1), py.coerceIn(0, sun.size - 1), 1, 1, GL33C.GL_DEPTH_COMPONENT, GL33C.GL_FLOAT, depth)
        GL33C.glBindFramebuffer(GL33C.GL_READ_FRAMEBUFFER, previousRead)
        checks++
        if (depth[0] < expected - 0.05f) {
            anomalies++
            println("HDDEBUG shadow map anomaly " + anomalies + "/" + checks + ": depth over the player " + depth[0] + ", player at " + expected + ", first pass " + first + ", casters now " + casters.count + " (drawn " + drawnCasters + "), late " + previous.count)
            for (i in 0 until previous.count) {
                val o: Int = i * 16
                println("HDDEBUG   late #" + i + " at " + previous.modelView[o + 12] + "," + previous.modelView[o + 13] + "," + previous.modelView[o + 14] + " elements " + previous.elements[i] + " buffer " + previous.vertexBuffer[i] + " offset " + previous.offset[i] + " indices " + previous.elementBuffer[i])
            }
        } else if (checks % 300 == 0) {
            println("HDDEBUG shadow map checks " + checks + ", anomalies " + anomalies + ", stale casters skipped " + staleCasters)
        }
    }

    private fun ensurePrograms() {
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
        // the scene is drawn in a pass per level range: the first clears the maps, later ones add to them
        val first: Boolean = !rendered
        rendered = true
        if (!sun.active && !heights.active) {
            recording = false
            lastCasters = 0
            return
        }
        HdGl.flushImmediate()
        val drawFramebuffer: Int = GL33C.glGetInteger(GL33C.GL_DRAW_FRAMEBUFFER_BINDING)
        readFramebuffer0 = GL33C.glGetInteger(GL33C.GL_READ_FRAMEBUFFER_BINDING)
        val viewport = IntArray(4)
        GL33C.glGetIntegerv(GL33C.GL_VIEWPORT, viewport)
        val scissor: Boolean = GL33C.glIsEnabled(GL33C.GL_SCISSOR_TEST)
        val blend: Boolean = GL33C.glIsEnabled(GL33C.GL_BLEND)
        val cull: Boolean = GL33C.glIsEnabled(GL33C.GL_CULL_FACE)
        val depthTest: Boolean = GL33C.glIsEnabled(GL33C.GL_DEPTH_TEST)
        val depthMask: Boolean = GL33C.glGetBoolean(GL33C.GL_DEPTH_WRITEMASK)
        val depthFunc: Int = GL33C.glGetInteger(GL33C.GL_DEPTH_FUNC)
        GL33C.glGetBooleanv(GL33C.GL_COLOR_WRITEMASK, colourMask)
        val elementBinding: Int = GL33C.glGetInteger(GL33C.GL_ELEMENT_ARRAY_BUFFER_BINDING)
        GL33C.glDisable(GL33C.GL_SCISSOR_TEST)
        GL33C.glDisable(GL33C.GL_BLEND)
        GL33C.glDisable(GL33C.GL_CULL_FACE)
        GL33C.glEnable(GL33C.GL_DEPTH_TEST)
        GL33C.glDepthFunc(GL33C.GL_LESS)
        GL33C.glDepthMask(true)
        GL33C.glColorMask(false, false, false, false)
        var uploaded: Boolean = false
        for (map in arrayOf(sun, heights)) {
            if (!map.active) continue
            GL33C.glBindFramebuffer(GL33C.GL_DRAW_FRAMEBUFFER, map.framebuffer)
            GL33C.glViewport(0, 0, map.size, map.size)
            if (first) {
                GL33C.glClear(GL33C.GL_DEPTH_BUFFER_BIT)
            }
            if (map === sun) {
                GL33C.glEnable(GL33C.GL_POLYGON_OFFSET_FILL)
                GL33C.glPolygonOffset(1.5f, 3.0f)
            } else {
                GL33C.glDisable(GL33C.GL_POLYGON_OFFSET_FILL)
            }
            // the map texture must not be bound for sampling while it is drawn into
            GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + map.unit)
            GL33C.glBindTexture(GL33C.GL_TEXTURE_2D, 0)

            if (map === sun && first && PARTS.contains("static")) {
                // every static caster near the player, on screen or not, placed by its captured model
                // matrix: drawn into staticMap when the map moves or the scene changes, and copied in
                // from there every frame (the height map of moving models has no static scene: it is baked)
                staticMap.ensure(sun.size)
                val terrain: Boolean = HdSettings.terrainShadows && ensureTerrain(scene)
                val cutout: Boolean = HdSettings.cutoutShadows
                if (staticKey != staticWanted || staticVersion != scene.bakeVersion || staticTerrain != terrain || staticCutout != cutout) {
                    staticKey = staticWanted
                    staticVersion = scene.bakeVersion
                    staticTerrain = terrain
                    staticCutout = cutout
                    GL33C.glBindFramebuffer(GL33C.GL_DRAW_FRAMEBUFFER, staticMap.framebuffer)
                    GL33C.glClear(GL33C.GL_DEPTH_BUFFER_BIT)
                    ensureBakeProgram()
                    // with cut-out shadows the static casters' textures leave holes (leaves, fences)
                    val program: WebGLProgram = if (cutout) ensureCutoutProgram() else bakeProgram!!
                    GL33C.glUseProgram(program.id)
                    if (cutout) {
                        scene.bindMaterials()
                    }
                    map.viewToClipBuffer.clear()
                    map.viewToClipBuffer.put(map.worldToClip).flip()
                    GL33C.glUniformMatrix4fv(GL33C.glGetUniformLocation(program.id, "uWorldToClip"), false, map.viewToClipBuffer)
                    val reach: Int = ((HALF_EXTENT / 512.0f).toInt() + 6) / 8 + 1
                    scene.drawRegion(program, 23, sunChunkX - reach, sunChunkZ - reach, sunChunkX + reach, sunChunkZ + reach)
                    if (terrain) {
                        drawTerrain(map)
                    }
                    staticRedraws++
                }
                GL33C.glBindFramebuffer(GL33C.GL_READ_FRAMEBUFFER, staticMap.framebuffer)
                GL33C.glBindFramebuffer(GL33C.GL_DRAW_FRAMEBUFFER, sun.framebuffer)
                GL33C.glBlitFramebuffer(0, 0, sun.size, sun.size, 0, 0, sun.size, sun.size, GL33C.GL_DEPTH_BUFFER_BIT, GL33C.GL_NEAREST)
                GL33C.glBindFramebuffer(GL33C.GL_READ_FRAMEBUFFER, readFramebuffer0)
            }

            GL33C.glUseProgram(dynamicProgram!!.id)
            GL33C.glBindVertexArray(vao)
            GL33C.glEnableVertexAttribArray(0)
            if (PARTS.contains("now")) drawCasters(casters, drawnCasters, map, !uploaded)
            if (first && PARTS.contains("late")) {
                // last frame's translucent casters (kept in world space): a frame behind
                drawCasters(previous, 0, map, !uploaded)
            }
            uploaded = true
        }
        lastCasters = casters.count - drawnCasters + previous.count
        if (System.getProperty("client.test.hdDebugCasters") != null && first) {
            var live: Int = 0
            for (i in 0 until previous.count) if (previous.elements[i] > 0) live++
            println("HDDEBUG casters now " + casters.count + " late " + live + "/" + previous.count + " stale " + staleCasters)
        }
        if (System.getProperty("client.test.hdDebug") != null && sun.active) {
            checkMap(first)
        }
        drawnCasters = casters.count

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
        // the maps, for the receivers
        for (map in arrayOf(sun, heights)) {
            if (!map.active) continue
            GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + map.unit)
            GL33C.glBindTexture(GL33C.GL_TEXTURE_2D, map.texture)
            GL33C.glBindSampler(map.unit, 0)
        }
        GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + state.activeTextureUnit)
        state.invalidateProgramCache()
        // what is drawn from here on (translucent entities) goes into the next frame's maps
        lateMode = true
    }

    fun release() {
        val cutout: WebGLProgram? = cutoutProgram
        if (cutout != null) {
            GL33C.glDeleteProgram(cutout.id)
            cutoutProgram = null
        }
        if (terrainVao != 0) {
            GL33C.glDeleteVertexArrays(terrainVao)
            GL33C.glDeleteBuffers(terrainVbo)
            GL33C.glDeleteBuffers(terrainIbo)
            terrainVao = 0
            terrainIndices = 0
            terrainGround = null
        }
        for (i in 0 until GROUND_SLOTS) {
            groundHasTexture[i] = false
            if (groundTexture[i] != 0) GL33C.glDeleteTextures(groundTexture[i])
            groundTexture[i] = 0
            val values: ByteBuffer? = groundValues[i]
            if (values != null) org.lwjgl.system.MemoryUtil.memFree(values)
            groundValues[i] = null
            groundOwner[i] = null
            groundVersion[i] = -1
        }
        sun.release()
        staticMap.release()
        staticVersion = -1
        heights.release()
        baked.release()
        bakedVersion = -1
        if (bakeDepths != null) org.lwjgl.system.MemoryUtil.memFree(bakeDepths)
        bakeDepths = null
        if (bakeProgram != null) GL33C.glDeleteProgram(bakeProgram!!.id)
        bakeProgram = null
        if (sceneProgram != null) GL33C.glDeleteProgram(sceneProgram!!.id)
        if (dynamicProgram != null) GL33C.glDeleteProgram(dynamicProgram!!.id)
        if (vao != 0) GL33C.glDeleteVertexArrays(vao)
        for (list in arrayOf(casters, late, previous)) {
            if (list.indexBuffer != 0) GL33C.glDeleteBuffers(list.indexBuffer)
            list.indexBuffer = 0
            if (list.copyBuffer != 0) GL33C.glDeleteBuffers(list.copyBuffer)
            list.copyBuffer = 0
            if (list.indices.capacity() > 0) org.lwjgl.system.MemoryUtil.memFree(list.indices)
            list.indices = ByteBuffer.allocate(0)
        }
        sceneProgram = null
        dynamicProgram = null
        vao = 0
    }
}

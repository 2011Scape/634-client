/*
 * Not part of the original client: 634HD (toolkit 6). The retained static scene.
 *
 * Static locs (walls, wall and ground decorations, scenery that never animates) keep their model and
 * position until the scene changes. When a region is built (HdToolkit.sceneBuilt), every such entity
 * on the opaque static list is drawn once in "capture" mode: its own render() reaches HdModel.render,
 * which hands its model and matrix here instead of drawing. The model's uploaded vertex and index
 * data is read back from the GPU once and appended, in model space, to one large buffer, grouped by
 * 8x8-tile chunk; each object keeps its own index.
 *
 * Each frame the client's own traversal (SceneDrawer) still decides what is drawn: for a retained
 * model HdModel.render calls markVisible, which records the model-view-projection, model-view and point
 * lights exactly as the emulator would have for that draw. After the opaque entities
 * (Toolkit.opaqueEntitiesDone) the visible chunks are drawn in one call with scene.vert/scene.frag; an
 * object the client did not draw this frame is dropped in the vertex shader. Batches whose materials
 * need the emulated path (water, environment maps) keep drawing there, in the client's order.
 *
 * A change to the scene (Toolkit.sceneChanged) rebuilds only the chunk it touches, on the next frame.
 */
package com.jagex.game.runetek5.graphics.hd.scene

import com.jagex.game.runetek5.buffer.ArrayBufferGL
import com.jagex.game.runetek5.buffer.ElementsBufferGL
import com.jagex.game.runetek5.client.features.RenderStats
import com.jagex.game.runetek5.entity.Entity
import com.jagex.game.runetek5.entity.PickableEntity
import com.jagex.game.runetek5.entity.StaticGroundDecor
import com.jagex.game.runetek5.entity.StaticLocation
import com.jagex.game.runetek5.entity.StaticWall
import com.jagex.game.runetek5.entity.StaticWallDecor
import com.jagex.game.runetek5.graphics.hd.HdModel
import com.jagex.game.runetek5.graphics.hd.HdToolkit
import com.jagex.game.runetek5.graphics.hd.gl.CoreGl
import com.jagex.game.runetek5.graphics.hd.gl.GlState
import com.jagex.game.runetek5.graphics.hd.gl.HdGl
import com.jagex.game.runetek5.graphics.hd.gl.HdShaders
import com.jagex.game.runetek5.graphics.hd.gl.WebGLProgram
import com.jagex.game.runetek5.scene.Scene
import com.jagex.game.runetek5.toolkit.opengl.buffer.OpenGlArrayDrawBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import org.lwjgl.PointerBuffer
import org.lwjgl.opengl.GL33C
import org.lwjgl.system.MemoryUtil

class HdSceneRenderer(private val toolkit: HdToolkit) {

    companion object {
        private const val VERTEX_BYTES: Int = 44
        private const val OBJECT_TEXELS: Int = 17
        private const val OBJECT_FLOATS: Int = OBJECT_TEXELS * 4
        /** Chunks are 8x8 tiles in a grid wide enough for any scene (104 tiles, or more for extended views). */
        private const val CHUNK_GRID: Int = 64
        private const val CHUNKS: Int = CHUNK_GRID * CHUNK_GRID
        private const val MAX_POINT_LIGHTS: Int = 4
        private const val UNIT_OBJECT_SLOTS: Int = 13
        private const val UNIT_OBJECTS: Int = 14

        /** Time per frame for building chunks of a new region (or changed chunks). */
        private const val BUILD_BUDGET_NANOS: Long = 4000000L

        /** Set while sceneBuilt replays the static entities' render() to learn their models and matrices. */
        @JvmField
        var capturing: HdSceneRenderer? = null

        private fun isStatic(entity: Entity): Boolean =
            entity is StaticWall || entity is StaticWallDecor || entity is StaticGroundDecor || entity is StaticLocation

        private fun chunkOf(entity: Entity): Int {
            val chunkX: Int = (entity.x shr 9) shr 3
            val chunkZ: Int = (entity.z shr 9) shr 3
            if (chunkX < 0 || chunkZ < 0 || chunkX >= CHUNK_GRID || chunkZ >= CHUNK_GRID) {
                return -1
            }
            return chunkX * CHUNK_GRID + chunkZ
        }
    }

    val materials: HdMaterials = HdMaterials(toolkit)

    /** Whether a scene has been built for this toolkit; until then everything draws the emulated way. */
    var built: Boolean = false
        private set

    // ---- capture ----

    private var captureModels: Array<HdModel?> = arrayOfNulls(1024)

    private var captureCount: Int = 0

    // ---- objects ----

    private var objectModels: Array<HdModel?> = arrayOfNulls(4096)

    private var objectChunk: IntArray = IntArray(4096)

    private var objectCount: Int = 0

    // ---- chunks ----

    private val chunkIndexStart: IntArray = IntArray(CHUNKS)

    private val chunkIndexCount: IntArray = IntArray(CHUNKS)

    private val chunkTriangles: IntArray = IntArray(CHUNKS)

    private val chunkDirty: BooleanArray = BooleanArray(CHUNKS)

    private var dirtyCount: Int = 0

    // ---- geometry, mirrored on the CPU so the buffers can grow ----

    private var vertices: ByteBuffer = ByteBuffer.allocateDirect(1 shl 22).order(ByteOrder.nativeOrder())

    private var vertexCount: Int = 0

    private var indices: ByteBuffer = ByteBuffer.allocateDirect(1 shl 22).order(ByteOrder.nativeOrder())

    private var indexCount: Int = 0

    /** Vertices and indices already in the GL buffers; what follows is uploaded before the next draw. */
    private var uploadedVertices: Int = 0

    private var uploadedIndices: Int = 0

    private var vboCapacity: Int = 0

    private var iboCapacity: Int = 0

    // ---- per frame ----

    private var frameSlots: IntArray = IntArray(4096) { -1 }

    private var frameData: FloatArray = FloatArray(1024 * OBJECT_FLOATS)

    private var frameObjects: IntArray = IntArray(1024)

    private var frameCount: Int = 0

    private val chunkVisible: IntArray = IntArray(CHUNKS)

    private val visibleChunks: IntArray = IntArray(CHUNKS)

    private var visibleChunkCount: Int = 0

    private val scratchMatrix: FloatArray = FloatArray(16)

    private var debugFrame: Int = 0

    private val modelView: FloatArray = FloatArray(16)

    // ---- GL ----

    private var program: WebGLProgram? = null

    private var vao: Int = 0

    private var vbo: Int = 0

    private var ibo: Int = 0

    private var slotBuffer: Int = 0

    private var slotTexture: Int = 0

    private var objectBuffer: Int = 0

    private var objectTexture: Int = 0

    private var slotUpload: ByteBuffer = ByteBuffer.allocateDirect(4096 * 4).order(ByteOrder.nativeOrder())

    private var objectUpload: ByteBuffer = ByteBuffer.allocateDirect(1024 * OBJECT_FLOATS * 4).order(ByteOrder.nativeOrder())

    private val drawCounts = MemoryUtil.memAllocInt(CHUNKS)

    private val drawOffsets: PointerBuffer = MemoryUtil.memAllocPointer(CHUNKS)

    private var uGlobalAmbient: Int = -1
    private var uDirLight0: Int = -1
    private var uDirLight1: Int = -1
    private var uDirDiffuse0: Int = -1
    private var uDirDiffuse1: Int = -1
    private var uAlphaTestEnabled: Int = -1
    private var uAlphaFunc: Int = -1
    private var uAlphaRef: Int = -1
    private var uFogEnabled: Int = -1
    private var uFogColor: Int = -1
    private var uFogStart: Int = -1
    private var uFogEnd: Int = -1

    // ======================================================================== build

    /** Entities waiting to be built, per chunk, and the chunks in the order they will be built. */
    private val pending: Array<Array<Entity?>?> = arrayOfNulls(CHUNKS)

    private val pendingCount: IntArray = IntArray(CHUNKS)

    private val queue: IntArray = IntArray(CHUNKS)

    private var queueHead: Int = 0

    private var queueTail: Int = 0

    private var queueSorted: Boolean = false

    /**
     * Starts the retained scene for the static entities now in the scene (a new region). Only the
     * entity lists are walked here; the geometry is built a few chunks per frame (buildStep), nearest
     * the player first, so a region load does not stall a frame. Until its chunk is built an entity
     * draws the emulated way.
     */
    fun build() {
        clear()
        val heads: Array<Entity?> = Scene.stringStack ?: return
        var entity: Entity? = heads[0]
        while (entity != null) {
            if (isStatic(entity)) {
                val chunk: Int = chunkOf(entity)
                if (chunk >= 0) {
                    queueEntity(chunk, entity)
                }
            }
            entity = entity.nextEntity
        }
        built = true
        RenderStats.chunkRebuilds = 0
        RenderStats.lastRegionUploadMs = 0
        regionStart = System.nanoTime()
        regionBuildNanos = 0L
    }

    private var regionStart: Long = 0L

    private var regionBuildNanos: Long = 0L

    private fun queueEntity(chunk: Int, entity: Entity) {
        var list: Array<Entity?>? = pending[chunk]
        if (list == null) {
            list = arrayOfNulls(16)
            pending[chunk] = list
        } else if (pendingCount[chunk] == list.size) {
            list = list.copyOf(list.size * 2)
            pending[chunk] = list
        }
        if (pendingCount[chunk] == 0) {
            queue[queueTail++ % CHUNKS] = chunk
            queueSorted = false
        }
        list[pendingCount[chunk]++] = entity
    }

    /** Builds queued chunks until [budgetNanos] is spent (at least one). */
    private fun buildStep(budgetNanos: Long) {
        if (queueHead == queueTail) {
            return
        }
        val start: Long = System.nanoTime()
        if (!queueSorted) {
            sortQueue()
        }
        while (queueHead < queueTail) {
            val chunk: Int = queue[queueHead++ % CHUNKS]
            val list: Array<Entity?> = pending[chunk] ?: continue
            val count: Int = pendingCount[chunk]
            pendingCount[chunk] = 0
            buildChunk(chunk, list, count)
            java.util.Arrays.fill(list, 0, count, null)
            if (System.nanoTime() - start >= budgetNanos) {
                break
            }
        }
        val spent: Long = System.nanoTime() - start
        regionBuildNanos += spent
        // the longest stall a frame took, which is what a player feels as a hitch
        val spentMs: Int = (spent / 1000000L).toInt()
        if (spentMs > RenderStats.lastRegionUploadMs) {
            RenderStats.lastRegionUploadMs = spentMs
        }
        if (queueHead == queueTail) {
            RenderStats.lastRegionBuildMs = (regionBuildNanos / 1000000L).toInt()
            readBuffers.clear()
        }
    }

    /** Nearest the local player first. */
    private fun sortQueue() {
        val player: Entity? = com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate.localPlayer
        val centreX: Int = if (player == null) 0 else (player.x shr 9) shr 3
        val centreZ: Int = if (player == null) 0 else (player.z shr 9) shr 3
        val count: Int = queueTail - queueHead
        val chunks: IntArray = IntArray(count)
        for (i in 0 until count) {
            chunks[i] = queue[(queueHead + i) % CHUNKS]
        }
        val keys: LongArray = LongArray(count)
        for (i in 0 until count) {
            val dx: Int = chunks[i] / CHUNK_GRID - centreX
            val dz: Int = chunks[i] % CHUNK_GRID - centreZ
            keys[i] = ((dx * dx + dz * dz).toLong() shl 32) or chunks[i].toLong()
        }
        java.util.Arrays.sort(keys)
        for (i in 0 until count) {
            queue[(queueHead + i) % CHUNKS] = (keys[i] and 0xFFFFFFFFL).toInt()
        }
        queueSorted = true
    }

    /** Captures [count] entities' models and appends their geometry as [chunk]'s index range. */
    private fun buildChunk(chunk: Int, entities: Array<Entity?>, count: Int) {
        val firstIndex: Int = indexCount
        for (i in 0 until count) {
            captureCount = 0
            capturing = this
            try {
                val pickable: PickableEntity? = entities[i]!!.render(toolkit)
                if (pickable != null) {
                    PickableEntity.recycle(pickable)
                }
            } finally {
                capturing = null
            }
            for (c in 0 until captureCount) {
                val model: HdModel = captureModels[c]!!
                captureModels[c] = null
                if (model.retainedObject >= 0) {
                    // one model drawn by two entities: it cannot carry one object index; both draw emulated
                    val other: Int = model.retainedObject
                    objectModels[other] = null
                    model.retainedObject = -1
                    model.retainedBatches = null
                    continue
                }
                addObject(model, chunk)
            }
        }
        chunkIndexStart[chunk] = firstIndex
        chunkIndexCount[chunk] = indexCount - firstIndex
        chunkTriangles[chunk] = (indexCount - firstIndex) / 3
    }

    /** Called by HdModel.render while capturing. */
    fun capture(model: HdModel) {
        if (captureCount == captureModels.size) {
            captureModels = captureModels.copyOf(captureCount * 2)
        }
        captureModels[captureCount++] = model
    }

    private fun addObject(model: HdModel, chunk: Int) {
        if (objectCount == objectModels.size) {
            val size: Int = objectCount * 2
            objectModels = objectModels.copyOf(size)
            objectChunk = objectChunk.copyOf(size)
            val slots: IntArray = IntArray(size) { -1 }
            System.arraycopy(frameSlots, 0, slots, 0, frameSlots.size)
            frameSlots = slots
        }
        val objectId: Int = objectCount
        if (!model.exportForScene(this, objectId)) {
            return
        }
        objectModels[objectId] = model
        objectChunk[objectId] = chunk
        objectCount++
    }

    /** GL buffers read back during this region's build: copies of a model share their base model's buffer. */
    private val readBuffers: HashMap<Int, ByteBuffer> = HashMap()

    private fun read(buffer: ArrayBufferGL, bytes: Int): ByteBuffer? {
        val name: Int = CoreGl.bufferName(buffer.getId())
        if (name == 0) {
            return null
        }
        val known: ByteBuffer? = readBuffers[name]
        if (known != null && known.capacity() >= bytes) {
            return known
        }
        val size: Int = bufferSize(name)
        val data: ByteBuffer = ByteBuffer.allocateDirect(maxOf(size, bytes)).order(ByteOrder.nativeOrder())
        CoreGl.readBuffer(name, 0L, minOf(size, data.capacity()), data)
        readBuffers[name] = data
        return data
    }

    private fun bufferSize(name: Int): Int {
        GL33C.glBindBuffer(GL33C.GL_COPY_READ_BUFFER, name)
        val size: Int = GL33C.glGetBufferParameteri(GL33C.GL_COPY_READ_BUFFER, GL33C.GL_BUFFER_SIZE)
        GL33C.glBindBuffer(GL33C.GL_COPY_READ_BUFFER, 0)
        return size
    }

    private var indexScratch: ByteBuffer = ByteBuffer.allocateDirect(65536).order(ByteOrder.nativeOrder())

    private var vertexSlots: IntArray = IntArray(4096)

    /**
     * Called by HdModel.exportForScene with the model's uploaded buffers: appends the batches whose
     * materials the shared shader draws. Returns which batches were taken, or null if none.
     */
    fun appendModel(
        objectId: Int, vertexCountOfModel: Int,
        positions: OpenGlArrayDrawBuffer, colours: OpenGlArrayDrawBuffer, normals: OpenGlArrayDrawBuffer?, texCoords: OpenGlArrayDrawBuffer?,
        elements: ElementsBufferGL, faceCount: Int, batchOffsets: IntArray, batchMaterials: IntArray,
    ): BooleanArray? {
        val batches: Int = batchMaterials.size
        val taken: BooleanArray = BooleanArray(batches)
        val slots: IntArray = IntArray(batches)
        var any: Boolean = false
        for (b in 0 until batches) {
            val slot: Int = materials.slotFor(batchMaterials[b])
            slots[b] = slot
            if (slot >= 0) {
                taken[b] = true
                any = true
            }
        }
        if (!any) {
            return null
        }
        val positionBuffer: ArrayBufferGL = positions.buffer ?: return null
        val colourBuffer: ArrayBufferGL = colours.buffer ?: return null
        val positionData: ByteBuffer = read(positionBuffer, positionBuffer.getStride() * vertexCountOfModel) ?: return null
        val colourData: ByteBuffer = read(colourBuffer, colourBuffer.getStride() * vertexCountOfModel) ?: return null
        val normalBuffer: ArrayBufferGL? = normals?.buffer
        val normalData: ByteBuffer? = if (normalBuffer == null) null else read(normalBuffer, normalBuffer.getStride() * vertexCountOfModel)
        val uvBuffer: ArrayBufferGL? = texCoords?.buffer
        val uvData: ByteBuffer? = if (uvBuffer == null) null else read(uvBuffer, uvBuffer.getStride() * vertexCountOfModel)
        val elementName: Int = CoreGl.bufferName(elements.getId())
        if (elementName == 0 || elements.getIndexType() != 5123) {
            return null
        }
        val indexBytes: Int = faceCount * 3 * 2
        if (indexScratch.capacity() < indexBytes) {
            indexScratch = ByteBuffer.allocateDirect(indexBytes).order(ByteOrder.nativeOrder())
        }
        CoreGl.readBuffer(elementName, 0L, indexBytes, indexScratch)

        if (vertexSlots.size < vertexCountOfModel) {
            vertexSlots = IntArray(vertexCountOfModel)
        }
        java.util.Arrays.fill(vertexSlots, 0, vertexCountOfModel, 0)
        // indices first: they say which material each vertex is drawn with
        ensureIndices(faceCount * 3)
        val base: Int = vertexCount
        for (b in 0 until batches) {
            if (!taken[b]) {
                continue
            }
            for (face in batchOffsets[b] until batchOffsets[b + 1]) {
                for (k in 0 until 3) {
                    val vertex: Int = indexScratch.getShort((face * 3 + k) * 2).toInt() and 0xFFFF
                    vertexSlots[vertex] = slots[b]
                    indices.putInt(indexCount * 4, base + vertex)
                    indexCount++
                }
            }
        }
        ensureVertices(vertexCountOfModel)
        val positionStride: Int = positionBuffer.getStride()
        val positionOffset: Int = positions.offset.toInt() and 0xFF
        val colourStride: Int = colourBuffer.getStride()
        val colourOffset: Int = colours.offset.toInt() and 0xFF
        val normalStride: Int = normalBuffer?.getStride() ?: 0
        val normalOffset: Int = (normals?.offset?.toInt() ?: 0) and 0xFF
        val uvStride: Int = uvBuffer?.getStride() ?: 0
        val uvOffset: Int = (texCoords?.offset?.toInt() ?: 0) and 0xFF
        for (v in 0 until vertexCountOfModel) {
            val at: Int = (base + v) * VERTEX_BYTES
            val p: Int = v * positionStride + positionOffset
            vertices.putFloat(at, positionData.getFloat(p))
            vertices.putFloat(at + 4, positionData.getFloat(p + 4))
            vertices.putFloat(at + 8, positionData.getFloat(p + 8))
            vertices.putInt(at + 12, colourData.getInt(v * colourStride + colourOffset))
            if (uvData != null) {
                val t: Int = v * uvStride + uvOffset
                vertices.putFloat(at + 16, uvData.getFloat(t))
                vertices.putFloat(at + 20, uvData.getFloat(t + 4))
            } else {
                vertices.putFloat(at + 16, 0.0f)
                vertices.putFloat(at + 20, 0.0f)
            }
            if (normalData != null) {
                val n: Int = v * normalStride + normalOffset
                vertices.putFloat(at + 24, normalData.getFloat(n))
                vertices.putFloat(at + 28, normalData.getFloat(n + 4))
                vertices.putFloat(at + 32, normalData.getFloat(n + 8))
            } else {
                vertices.putFloat(at + 24, 0.0f)
                vertices.putFloat(at + 28, 0.0f)
                vertices.putFloat(at + 32, 0.0f)
            }
            vertices.putShort(at + 36, vertexSlots[v].toShort())
            vertices.putShort(at + 38, 0.toShort())
            vertices.putInt(at + 40, objectId)
        }
        vertexCount += vertexCountOfModel
        return taken
    }

    private fun ensureVertices(more: Int) {
        val needed: Int = (vertexCount + more) * VERTEX_BYTES
        if (needed > vertices.capacity()) {
            var size: Int = vertices.capacity() * 2
            while (size < needed) size *= 2
            val grown: ByteBuffer = ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder())
            vertices.clear()
            vertices.limit(vertexCount * VERTEX_BYTES)
            grown.put(vertices)
            vertices = grown
        }
    }

    private fun ensureIndices(more: Int) {
        val needed: Int = (indexCount + more) * 4
        if (needed > indices.capacity()) {
            var size: Int = indices.capacity() * 2
            while (size < needed) size *= 2
            val grown: ByteBuffer = ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder())
            indices.clear()
            indices.limit(indexCount * 4)
            grown.put(indices)
            indices = grown
        }
    }

    /** Drops the retained scene: every model draws the emulated way until the next build. */
    fun clear() {
        for (i in 0 until objectCount) {
            val model: HdModel? = objectModels[i]
            if (model != null) {
                model.retainedObject = -1
                model.retainedBatches = null
            }
            objectModels[i] = null
            frameSlots[i] = -1
        }
        objectCount = 0
        vertexCount = 0
        indexCount = 0
        uploadedVertices = 0
        uploadedIndices = 0
        frameCount = 0
        java.util.Arrays.fill(chunkIndexCount, 0)
        java.util.Arrays.fill(chunkTriangles, 0)
        java.util.Arrays.fill(chunkVisible, 0)
        java.util.Arrays.fill(chunkDirty, false)
        dirtyCount = 0
        visibleChunkCount = 0
        for (i in 0 until CHUNKS) {
            val list: Array<Entity?>? = pending[i]
            if (list != null && pendingCount[i] > 0) {
                java.util.Arrays.fill(list, 0, pendingCount[i], null)
            }
            pendingCount[i] = 0
        }
        queueHead = 0
        queueTail = 0
        readBuffers.clear()
        built = false
    }

    // ======================================================================== changes

    /** A loc was added, removed or moved on tile ([x], [z]): its chunk is rebuilt before the next draw. */
    fun sceneChanged(x: Int, z: Int) {
        if (!built) {
            return
        }
        val chunkX: Int = x shr 3
        val chunkZ: Int = z shr 3
        if (chunkX < 0 || chunkZ < 0 || chunkX >= CHUNK_GRID || chunkZ >= CHUNK_GRID) {
            return
        }
        val chunk: Int = chunkX * CHUNK_GRID + chunkZ
        if (!chunkDirty[chunk]) {
            chunkDirty[chunk] = true
            dirtyCount++
        }
    }

    private fun rebuildDirtyChunks() {
        if (dirtyCount == 0) {
            buildStep(BUILD_BUDGET_NANOS)
            return
        }
        // garbage (indices of rebuilt chunks) above half the geometry: start afresh
        var live: Int = 0
        for (chunk in 0 until CHUNKS) {
            live += chunkIndexCount[chunk]
        }
        if (indexCount > 1 shl 20 && live < indexCount / 2) {
            build()
            return
        }
        for (chunk in 0 until CHUNKS) {
            if (!chunkDirty[chunk]) {
                continue
            }
            chunkDirty[chunk] = false
            for (i in 0 until objectCount) {
                if (objectChunk[i] == chunk) {
                    val model: HdModel? = objectModels[i]
                    if (model != null) {
                        model.retainedObject = -1
                        model.retainedBatches = null
                        objectModels[i] = null
                    }
                }
            }
            chunkIndexCount[chunk] = 0
            chunkTriangles[chunk] = 0
            if (pendingCount[chunk] > 0) {
                // still waiting from the region build: queued again with the entities there now
                java.util.Arrays.fill(pending[chunk]!!, 0, pendingCount[chunk], null)
                pendingCount[chunk] = 0
            }
            val heads: Array<Entity?> = Scene.stringStack ?: continue
            var entity: Entity? = heads[0]
            while (entity != null) {
                if (isStatic(entity) && chunkOf(entity) == chunk) {
                    queueEntity(chunk, entity)
                }
                entity = entity.nextEntity
            }
            RenderStats.chunkRebuilds++
        }
        dirtyCount = 0
        buildStep(BUILD_BUDGET_NANOS)
    }

    // ======================================================================== per frame

    /**
     * Called by HdModel.render in place of its draw: [modelMatrix] is the matrix the emulator would
     * multiply onto the model-view stack (Matrix43.getColumnMajor), [lit] whether it lights the model.
     */
    fun markVisible(objectId: Int, modelMatrix: FloatArray, lit: Boolean) {
        if (frameSlots[objectId] >= 0) {
            return
        }
        val slot: Int = frameCount++
        if (slot == frameObjects.size) {
            frameObjects = frameObjects.copyOf(slot * 2)
            frameData = frameData.copyOf(slot * 2 * OBJECT_FLOATS)
        }
        frameObjects[slot] = objectId
        frameSlots[objectId] = slot
        val chunk: Int = objectChunk[objectId]
        if (chunkVisible[chunk]++ == 0) {
            visibleChunks[visibleChunkCount++] = chunk
        }
        val state: GlState = HdGl.state
        // exactly what glMultMatrixf and the draw would compute (MatrixStack.mult, MatrixStack.mvp)
        multiply(state.matrixStack.modelview(), modelMatrix, modelView)
        val out: FloatArray = frameData
        val at: Int = slot * OBJECT_FLOATS
        multiply(state.matrixStack.projection(), modelView, scratchMatrix)
        System.arraycopy(scratchMatrix, 0, out, at, 16)
        System.arraycopy(modelView, 0, out, at + 16, 16)
        var lights: Int = 0
        if (lit) {
            for (i in 0 until MAX_POINT_LIGHTS) {
                val light: Int = 2 + i
                if (!state.lightEnabled[light]) {
                    continue
                }
                val l: Int = at + 36 + lights * 8
                val position: FloatArray = state.lightPosition[light]
                out[l] = position[0]
                out[l + 1] = position[1]
                out[l + 2] = position[2]
                out[l + 3] = state.lightAttenuation[light][2]
                val diffuse: FloatArray = state.lightDiffuse[light]
                out[l + 4] = diffuse[0]
                out[l + 5] = diffuse[1]
                out[l + 6] = diffuse[2]
                // the toolkit sets the linear term to 0 (HdToolkit.prepareForRendering); the constant is 0 too, but kept
                out[l + 7] = state.lightAttenuation[light][0]
                lights++
            }
        }
        out[at + 32] = if (lit) 1.0f else 0.0f
        out[at + 33] = lights.toFloat()
        out[at + 34] = 0.0f
        out[at + 35] = 0.0f
    }

    /** Mat4.multiply's arithmetic, in the same order, into [out]. */
    private fun multiply(a: FloatArray, b: FloatArray, out: FloatArray) {
        for (col in 0 until 4) {
            for (row in 0 until 4) {
                var sum: Float = 0f
                for (k in 0 until 4) {
                    sum += a[k * 4 + row] * b[col * 4 + k]
                }
                out[col * 4 + row] = sum
            }
        }
    }

    /** After the opaque entities: rebuilds changed chunks, then draws every visible retained object. */
    fun flushOpaque() {
        HdGl.flushImmediate()
        if (frameCount == 0) {
            rebuildDirtyChunks()
            return
        }
        ensureProgram()
        uploadGeometry()
        if (System.getProperty("client.test.hdDebug") != null && (debugFrame++ % 200) == 0) {
            var withLights: Int = 0
            var litCount: Int = 0
            for (i in 0 until frameCount) {
                if (frameData[i * OBJECT_FLOATS + 33] > 0f) withLights++
                if (frameData[i * OBJECT_FLOATS + 32] > 0f) litCount++
            }
            var enabled: String = ""
            for (l in 0 until 8) enabled += if (HdGl.state.lightEnabled[l]) "1" else "0"
            println("HDDEBUG objects=" + objectCount + " visible=" + frameCount + " lit=" + litCount + " withLights=" + withLights + " lightsNow=" + enabled + " chunks=" + visibleChunkCount)
        }
        val state: GlState = HdGl.state
        // the toolkit's 3D state (blend, alpha test, depth, fog), as the emulated draws had it
        toolkit.setupMatrixRendering()
        val program: WebGLProgram = this.program!!
        GL33C.glUseProgram(program.id)
        CoreGl.uniform4fv(uGlobalAmbient, state.globalAmbient)
        setDirLight(uDirLight0, uDirDiffuse0, state, 0)
        setDirLight(uDirLight1, uDirDiffuse1, state, 1)
        GL33C.glUniform1i(uAlphaTestEnabled, if (state.alphaTestEnabled) 1 else 0)
        GL33C.glUniform1i(uAlphaFunc, state.alphaFunc)
        GL33C.glUniform1f(uAlphaRef, state.alphaRef)
        GL33C.glUniform1i(uFogEnabled, if (state.fogEnabled) 1 else 0)
        CoreGl.uniform4fv(uFogColor, state.fogColor)
        GL33C.glUniform1f(uFogStart, state.fogStart)
        GL33C.glUniform1f(uFogEnd, state.fogEnd)

        materials.update(toolkit.lastTickMillis.toLong())
        uploadFrame()
        materials.bind()
        GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + UNIT_OBJECT_SLOTS)
        GL33C.glBindTexture(GL33C.GL_TEXTURE_BUFFER, slotTexture)
        GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + UNIT_OBJECTS)
        GL33C.glBindTexture(GL33C.GL_TEXTURE_BUFFER, objectTexture)

        GL33C.glBindVertexArray(vao)
        drawCounts.clear()
        drawOffsets.clear()
        var triangles: Int = 0
        var draws: Int = 0
        for (i in 0 until visibleChunkCount) {
            val chunk: Int = visibleChunks[i]
            if (chunkIndexCount[chunk] == 0) {
                continue
            }
            drawCounts.put(chunkIndexCount[chunk])
            drawOffsets.put(chunkIndexStart[chunk].toLong() * 4L)
            triangles += chunkTriangles[chunk]
            draws++
        }
        drawCounts.flip()
        drawOffsets.flip()
        if (draws > 0) {
            CoreGl.multiDrawElements(GL33C.GL_TRIANGLES, drawCounts, GL33C.GL_UNSIGNED_INT, drawOffsets, triangles)
        }

        // the emulator's bindings back
        GL33C.glBindVertexArray(state.vertexArray)
        GL33C.glBindBuffer(GL33C.GL_ARRAY_BUFFER, state.boundArrayBuffer?.id ?: 0)
        GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + state.activeTextureUnit)
        state.invalidateProgramCache()

        // reset for the next frame
        for (i in 0 until frameCount) {
            frameSlots[frameObjects[i]] = -1
        }
        for (i in 0 until visibleChunkCount) {
            chunkVisible[visibleChunks[i]] = 0
        }
        frameCount = 0
        visibleChunkCount = 0
        rebuildDirtyChunks()
    }

    private fun setDirLight(direction: Int, diffuse: Int, state: GlState, light: Int) {
        val position: FloatArray = state.lightPosition[light]
        GL33C.glUniform4f(direction, position[0], position[1], position[2], if (state.lightEnabled[light]) 1.0f else 0.0f)
        CoreGl.uniform4fv(diffuse, state.lightDiffuse[light])
    }

    private fun uploadFrame() {
        val slots: Int = maxOf(objectCount, 1)
        if (slotUpload.capacity() < slots * 4) {
            slotUpload = ByteBuffer.allocateDirect(slots * 8).order(ByteOrder.nativeOrder())
        }
        slotUpload.clear()
        for (i in 0 until slots) {
            slotUpload.putInt(if (i < objectCount) frameSlots[i] else -1)
        }
        slotUpload.flip()
        GL33C.glBindBuffer(GL33C.GL_TEXTURE_BUFFER, slotBuffer)
        GL33C.glBufferData(GL33C.GL_TEXTURE_BUFFER, slotUpload, GL33C.GL_STREAM_DRAW)
        val floats: Int = frameCount * OBJECT_FLOATS
        if (objectUpload.capacity() < floats * 4) {
            objectUpload = ByteBuffer.allocateDirect(floats * 8).order(ByteOrder.nativeOrder())
        }
        objectUpload.clear()
        objectUpload.asFloatBuffer().put(frameData, 0, floats)
        objectUpload.limit(floats * 4)
        GL33C.glBindBuffer(GL33C.GL_TEXTURE_BUFFER, objectBuffer)
        GL33C.glBufferData(GL33C.GL_TEXTURE_BUFFER, objectUpload, GL33C.GL_STREAM_DRAW)
        GL33C.glBindBuffer(GL33C.GL_TEXTURE_BUFFER, 0)
    }

    private fun uploadGeometry() {
        if (uploadedVertices == vertexCount && uploadedIndices == indexCount) {
            return
        }
        GL33C.glBindVertexArray(vao)
        if (vertexCount * VERTEX_BYTES > vboCapacity || uploadedVertices > vertexCount) {
            vboCapacity = maxOf(vertexCount * VERTEX_BYTES * 3 / 2, 1 shl 20)
            GL33C.glBindBuffer(GL33C.GL_ARRAY_BUFFER, vbo)
            GL33C.glBufferData(GL33C.GL_ARRAY_BUFFER, vboCapacity.toLong(), GL33C.GL_STATIC_DRAW)
            uploadedVertices = 0
        }
        if (indexCount * 4 > iboCapacity || uploadedIndices > indexCount) {
            iboCapacity = maxOf(indexCount * 4 * 3 / 2, 1 shl 20)
            GL33C.glBindBuffer(GL33C.GL_ELEMENT_ARRAY_BUFFER, ibo)
            GL33C.glBufferData(GL33C.GL_ELEMENT_ARRAY_BUFFER, iboCapacity.toLong(), GL33C.GL_STATIC_DRAW)
            uploadedIndices = 0
        }
        if (uploadedVertices < vertexCount) {
            vertices.clear()
            vertices.position(uploadedVertices * VERTEX_BYTES)
            vertices.limit(vertexCount * VERTEX_BYTES)
            GL33C.glBindBuffer(GL33C.GL_ARRAY_BUFFER, vbo)
            GL33C.glBufferSubData(GL33C.GL_ARRAY_BUFFER, (uploadedVertices * VERTEX_BYTES).toLong(), vertices)
            vertices.clear()
            uploadedVertices = vertexCount
        }
        if (uploadedIndices < indexCount) {
            indices.clear()
            indices.position(uploadedIndices * 4)
            indices.limit(indexCount * 4)
            GL33C.glBindBuffer(GL33C.GL_ELEMENT_ARRAY_BUFFER, ibo)
            GL33C.glBufferSubData(GL33C.GL_ELEMENT_ARRAY_BUFFER, (uploadedIndices * 4).toLong(), indices)
            indices.clear()
            uploadedIndices = indexCount
        }
    }

    private fun ensureProgram() {
        if (program != null) {
            return
        }
        val created: WebGLProgram = HdShaders.program("scene")
        program = created
        val id: Int = created.id
        GL33C.glUseProgram(id)
        GL33C.glUniform1i(GL33C.glGetUniformLocation(id, "uObjectSlots"), UNIT_OBJECT_SLOTS)
        GL33C.glUniform1i(GL33C.glGetUniformLocation(id, "uObjects"), UNIT_OBJECTS)
        GL33C.glUniform1i(GL33C.glGetUniformLocation(id, "uMaterials"), HdMaterials.UNIT_MATERIALS)
        GL33C.glUniform1i(GL33C.glGetUniformLocation(id, "uSmallLinear"), HdMaterials.UNIT_SMALL_LINEAR)
        GL33C.glUniform1i(GL33C.glGetUniformLocation(id, "uSmallNearest"), HdMaterials.UNIT_SMALL_NEAREST)
        GL33C.glUniform1i(GL33C.glGetUniformLocation(id, "uLargeLinear"), HdMaterials.UNIT_LARGE_LINEAR)
        GL33C.glUniform1i(GL33C.glGetUniformLocation(id, "uLargeNearest"), HdMaterials.UNIT_LARGE_NEAREST)
        uGlobalAmbient = GL33C.glGetUniformLocation(id, "uGlobalAmbient")
        uDirLight0 = GL33C.glGetUniformLocation(id, "uDirLight[0]")
        uDirLight1 = GL33C.glGetUniformLocation(id, "uDirLight[1]")
        uDirDiffuse0 = GL33C.glGetUniformLocation(id, "uDirDiffuse[0]")
        uDirDiffuse1 = GL33C.glGetUniformLocation(id, "uDirDiffuse[1]")
        uAlphaTestEnabled = GL33C.glGetUniformLocation(id, "uAlphaTestEnabled")
        uAlphaFunc = GL33C.glGetUniformLocation(id, "uAlphaFunc")
        uAlphaRef = GL33C.glGetUniformLocation(id, "uAlphaRef")
        uFogEnabled = GL33C.glGetUniformLocation(id, "uFogEnabled")
        uFogColor = GL33C.glGetUniformLocation(id, "uFogColor")
        uFogStart = GL33C.glGetUniformLocation(id, "uFogStart")
        uFogEnd = GL33C.glGetUniformLocation(id, "uFogEnd")

        vao = GL33C.glGenVertexArrays()
        vbo = GL33C.glGenBuffers()
        ibo = GL33C.glGenBuffers()
        GL33C.glBindVertexArray(vao)
        GL33C.glBindBuffer(GL33C.GL_ARRAY_BUFFER, vbo)
        GL33C.glBufferData(GL33C.GL_ARRAY_BUFFER, (1 shl 20).toLong(), GL33C.GL_STATIC_DRAW)
        vboCapacity = 1 shl 20
        GL33C.glBindBuffer(GL33C.GL_ELEMENT_ARRAY_BUFFER, ibo)
        GL33C.glBufferData(GL33C.GL_ELEMENT_ARRAY_BUFFER, (1 shl 20).toLong(), GL33C.GL_STATIC_DRAW)
        iboCapacity = 1 shl 20
        GL33C.glEnableVertexAttribArray(0)
        GL33C.glVertexAttribPointer(0, 3, GL33C.GL_FLOAT, false, VERTEX_BYTES, 0L)
        GL33C.glEnableVertexAttribArray(1)
        GL33C.glVertexAttribPointer(1, 4, GL33C.GL_UNSIGNED_BYTE, true, VERTEX_BYTES, 12L)
        GL33C.glEnableVertexAttribArray(2)
        GL33C.glVertexAttribPointer(2, 2, GL33C.GL_FLOAT, false, VERTEX_BYTES, 16L)
        GL33C.glEnableVertexAttribArray(3)
        GL33C.glVertexAttribPointer(3, 3, GL33C.GL_FLOAT, false, VERTEX_BYTES, 24L)
        GL33C.glEnableVertexAttribArray(4)
        GL33C.glVertexAttribIPointer(4, 1, GL33C.GL_UNSIGNED_SHORT, VERTEX_BYTES, 36L)
        GL33C.glEnableVertexAttribArray(5)
        GL33C.glVertexAttribIPointer(5, 1, GL33C.GL_UNSIGNED_INT, VERTEX_BYTES, 40L)

        slotBuffer = GL33C.glGenBuffers()
        slotTexture = GL33C.glGenTextures()
        GL33C.glBindBuffer(GL33C.GL_TEXTURE_BUFFER, slotBuffer)
        GL33C.glBufferData(GL33C.GL_TEXTURE_BUFFER, 16L, GL33C.GL_STREAM_DRAW)
        GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + UNIT_OBJECT_SLOTS)
        GL33C.glBindTexture(GL33C.GL_TEXTURE_BUFFER, slotTexture)
        GL33C.glTexBuffer(GL33C.GL_TEXTURE_BUFFER, GL33C.GL_R32I, slotBuffer)
        objectBuffer = GL33C.glGenBuffers()
        objectTexture = GL33C.glGenTextures()
        GL33C.glBindBuffer(GL33C.GL_TEXTURE_BUFFER, objectBuffer)
        GL33C.glBufferData(GL33C.GL_TEXTURE_BUFFER, 16L, GL33C.GL_STREAM_DRAW)
        GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + UNIT_OBJECTS)
        GL33C.glBindTexture(GL33C.GL_TEXTURE_BUFFER, objectTexture)
        GL33C.glTexBuffer(GL33C.GL_TEXTURE_BUFFER, GL33C.GL_RGBA32F, objectBuffer)
        GL33C.glBindBuffer(GL33C.GL_TEXTURE_BUFFER, 0)
    }

    fun release() {
        clear()
        materials.release()
        val created: WebGLProgram? = program
        if (created != null) {
            GL33C.glDeleteProgram(created.id)
            GL33C.glDeleteVertexArrays(vao)
            GL33C.glDeleteBuffers(vbo)
            GL33C.glDeleteBuffers(ibo)
            GL33C.glDeleteBuffers(slotBuffer)
            GL33C.glDeleteBuffers(objectBuffer)
            GL33C.glDeleteTextures(slotTexture)
            GL33C.glDeleteTextures(objectTexture)
            program = null
        }
    }
}

/*
 * Not part of the original client: 634HD (toolkit 6). The room fill (HdSettings.lightGrid): each
 * lamp's light carried through its room on the tile grid and stopped by walls, as a soft fill the
 * scene shaders add (hdLightFill in hd_lib.glsl), and each lamp's own light kept behind the walls
 * round it (hdLightVisibility). Built on the CPU when the scene is built or a loc changes.
 *
 * The seeds are the lights of lights.json on their locs (HdLights) and the game's own map lights
 * (Scene.aClass300Array1). From each, a Dijkstra flood over the tiles within its reach, stepping
 * only through edges the collision map leaves open to a projectile: light goes where an arrow goes,
 * so walls and doors stop it but counters, tables and fences do not. What arrives is accumulated per
 * level into a grid of two texels a tile (one texel a tile bled through walls under bilinear
 * filtering), blurred once without crossing walls, and uploaded as a texture array, the tile's floor
 * height in alpha so the shader can pick the level a fragment stands on. The flood also records,
 * per static light, how much of it gets round the walls to each tile within 16, as a visibility
 * patch the shaders multiply that light's own falloff by.
 *
 * The fill is steady: the lights' flicker is left to their direct light (a whole room strobing
 * would be worse than no fill).
 */
package com.jagex.game.runetek5.graphics.hd.scene

import com.jagex.game.runetek5.MapRegion
import com.jagex.game.runetek5.cache.node.PointLight
import com.jagex.game.runetek5.client.features.HdSettings
import com.jagex.game.runetek5.core.constants.TileFlag
import com.jagex.game.runetek5.graphics.hd.gl.HdGl
import com.jagex.game.runetek5.graphics.light.StaticPointLight
import com.jagex.game.runetek5.scene.CollisionMap
import com.jagex.game.runetek5.scene.Scene
import com.jagex.game.runetek5.scene.SettingsBits
import java.nio.ByteBuffer
import java.nio.ByteOrder
import org.lwjgl.opengl.GL33C

// the flood and the blur are scan loops over cells with several early outs each; split up they read worse
@Suppress("LoopWithTooManyJumpStatements")
class HdLightGrid {

    companion object {
        /** The fill's texture unit (uHdLightGrid) and the visibility patches' (uHdLightVisibility). */
        const val GRID_UNIT: Int = 24
        const val VIS_UNIT: Int = 30

        /** Texels of the fill per tile, each way. */
        const val TEXELS_PER_TILE: Int = 2

        /** A visibility patch's width in tiles (the light in the middle). */
        const val PATCH: Int = 32
        private const val PATCH_HALF: Int = PATCH / 2

        private const val LEVELS: Int = 4
        private const val MAX_SEEDS: Int = 6144

        /** How far the fill reaches, as a multiple of the light's radius, and the least and most it can (tiles). */
        private const val FILL_REACH: Float = 2.0f
        private const val MAX_REACH: Int = PATCH_HALF - 1

        /**
         * The least reach: the game's own map lights are small (a radius of 2 or 3 tiles), and from a
         * corner one must still carry a soft fill to the far wall of a 6-tile room; at 6 tiles it is
         * down to a seventh, so a room with no lamp that near stays dark.
         */
        private const val MIN_REACH: Float = 7.0f

        /** The fill at a light's own tile, as a share of the light's colour times intensity. */
        private const val FILL_SCALE: Float = 1.0f

        /**
         * The fill's soft ceiling: the lamps' sum s becomes KNEE * (1 - exp(-s / KNEE)), so one lamp
         * is little changed (1 reads 0.68) while a hall of torches saturates gently instead of stacking
         * (4 reads 1.16, and its walls stay readable rather than going white).
         */
        private const val KNEE: Float = 1.2f

        /** The game's map lights seed the fill at this share (they are many, and bright up close). */
        private const val GAME_LIGHT_SCALE: Float = 1.0f

        /** The fill's strength with no time of day; with one it runs from DAY_STRENGTH at noon to 1 at night. */
        private const val PLAIN_STRENGTH: Float = 0.6f
        private const val DAY_STRENGTH: Float = 0.25f

        /** The alpha of a grid texel on a level with no floor there. */
        private const val NO_FLOOR: Float = 60000.0f

        // the collision map's line-of-sight wall bits (CollisionFlag): north, east, south, west, and a
        // loc that blocks projectiles filling its tile
        private const val LOS_NORTH: Int = 0x400
        private const val LOS_EAST: Int = 0x1000
        private const val LOS_SOUTH: Int = 0x4000
        private const val LOS_WEST: Int = 0x10000
        private const val LOS_SOLID: Int = 0x20000

        /** The collision map's blocked-floor bit: set at the start and cleared where a level's ground decodes, so still set where a level has no floor (a roof over a hall). */
        private const val BLOCKWALK: Int = 0x200000

        private const val DIAGONAL: Float = 1.4142135f
    }

    /** Set when the scene is built or changes; the grid is flooded again on the next frame. */
    @JvmField
    var dirty: Boolean = true

    // the seeds: tile, the level the light is on (and up to, for a game light that grows upwards),
    // colour times intensity, reach in tiles, and the visibility layer (0: none)
    private var seedCount: Int = 0
    private val seedX: IntArray = IntArray(MAX_SEEDS)
    private val seedZ: IntArray = IntArray(MAX_SEEDS)
    private val seedLevel: IntArray = IntArray(MAX_SEEDS)
    private val seedTop: IntArray = IntArray(MAX_SEEDS)
    private val seedRed: FloatArray = FloatArray(MAX_SEEDS)
    private val seedGreen: FloatArray = FloatArray(MAX_SEEDS)
    private val seedBlue: FloatArray = FloatArray(MAX_SEEDS)
    private val seedReach: FloatArray = FloatArray(MAX_SEEDS)
    private val seedLayer: IntArray = IntArray(MAX_SEEDS)
    // each seed's flood on its own level, kept so a loc change floods only the seeds it touches
    private val seedDistance: Array<FloatArray?> = arrayOfNulls(MAX_SEEDS)
    private val seedStale: BooleanArray = BooleanArray(MAX_SEEDS)
    private var partial: Boolean = false

    // per level, the texel bounds the deposits touched (the blur and the debug map cover only these)
    private val litMinX: IntArray = IntArray(LEVELS)
    private val litMinZ: IntArray = IntArray(LEVELS)
    private val litMaxX: IntArray = IntArray(LEVELS)
    private val litMaxZ: IntArray = IntArray(LEVELS)
    private var floodNanos: Long = 0L
    private var blurNanos: Long = 0L
    private var uploadNanos: Long = 0L

    // the grid: per level, width x height texels of r, g, b, floor y
    private var size: Int = 0
    private var width: Int = 0
    private var fill: FloatArray = FloatArray(0)
    private var pixels: ByteBuffer? = null
    private var gridTexture: Int = 0
    private var gridWidth: Int = 0

    // the visibility patches: one per lights.json loc light (layer index + 1), layer 0 all 255
    private var patchLayers: Int = 0
    private var patches: ByteArray = ByteArray(0)
    private var patchPixels: ByteBuffer? = null
    private var patchTexture: Int = 0
    private var patchTextureLayers: Int = 0

    // the flood's scratch: distance per box cell, a binary heap of cells by distance, the cells' heap slots
    private val boxSize: Int = (2 * MAX_REACH + 1) * (2 * MAX_REACH + 1)
    private val boxDistance: FloatArray = FloatArray(boxSize)
    private val heap: IntArray = IntArray(boxSize)
    private val heapSlot: IntArray = IntArray(boxSize)
    private var heapCount: Int = 0

    private val DEBUG: Boolean = System.getProperty("client.test.hdDebug") != null
    private var builds: Int = 0

    /** A wall or loc changed on tile ([x], [z]) of [level]: the seeds whose reach covers it are flooded again next frame. */
    fun changed(level: Int, x: Int, z: Int) {
        if (gridTexture == 0 || seedCount == 0) {
            dirty = true
            return
        }
        for (s in 0 until seedCount) {
            if (level < seedLevel[s] || level > seedTop[s]) continue
            val r: Int = kotlin.math.ceil(seedReach[s].toDouble()).toInt()
            if (Math.abs(x - seedX[s]) <= r && Math.abs(z - seedZ[s]) <= r) {
                seedStale[s] = true
                partial = true
            }
        }
    }

    /**
     * Builds the grid when needed, binds its textures and writes this frame's fill strength;
     * [night] 0 to 1 with a time of day, or -1 without one.
     */
    fun prepare(uniforms: HdFrameUniforms, lights: HdLights, night: Float) {
        val on: Boolean = HdSettings.lightGrid && (HdSettings.lights || HdSettings.playerLight)
        if (!on) {
            uniforms.setGridParams(0.0f, TEXELS_PER_TILE.toFloat())
            return
        }
        if (dirty || gridTexture == 0) {
            dirty = false
            partial = false
            rebuild(lights, uniforms)
        } else if (partial) {
            partial = false
            refresh()
        }
        bind()
        val strength: Float = if (night < 0.0f) PLAIN_STRENGTH else DAY_STRENGTH + (1.0f - DAY_STRENGTH) * night
        uniforms.setGridParams(strength, TEXELS_PER_TILE.toFloat())
        // each light written this frame: its wall visibility patch, and where the patch is centred
        for (i in 0 until lights.used) {
            uniforms.setLightGrid(i, layerOf(lights.usedLocIndex(i)).toFloat(), lights.usedX(i), lights.usedZ(i))
        }
    }

    /** The visibility layer of lights.json loc light [locIndex] (HdLights' index), 0 when none. */
    fun layerOf(locIndex: Int): Int = if (locIndex < 0 || locIndex + 1 >= patchLayers) 0 else locIndex + 1

    private fun rebuild(lights: HdLights, uniforms: HdFrameUniforms) {
        val started: Long = System.nanoTime()
        floodNanos = 0L
        blurNanos = 0L
        uploadNanos = 0L
        size = MapRegion.sceneSizeX
        if (size <= 0) return
        width = size * TEXELS_PER_TILE
        val texels: Int = width * width
        if (fill.size != LEVELS * texels * 4) {
            fill = FloatArray(LEVELS * texels * 4)
            pixels = ByteBuffer.allocateDirect(LEVELS * texels * 16).order(ByteOrder.nativeOrder())
        } else {
            java.util.Arrays.fill(fill, 0.0f)
        }
        collectSeeds(lights)
        val locs: Int = lights.locLightCount()
        patchLayers = locs + 1
        if (patches.size != patchLayers * PATCH * PATCH) {
            patches = ByteArray(patchLayers * PATCH * PATCH)
            patchPixels = ByteBuffer.allocateDirect(patchLayers * PATCH * PATCH).order(ByteOrder.nativeOrder())
        } else {
            java.util.Arrays.fill(patches, 0.toByte())
        }
        val flooding: Long = System.nanoTime()
        for (s in 0 until seedCount) {
            seedStale[s] = false
            cacheFlood(s)
        }
        accumulate()
        floodNanos = System.nanoTime() - flooding
        blur()
        writeFloors()
        upload()
        builds++
        val totalMs: Double = ((System.nanoTime() - started) / 100000L) / 10.0
        if (DEBUG) {
            val summary: StringBuilder = StringBuilder()
            val texels: Int = width * width
            for (level in 0 until LEVELS) {
                var energy: Float = 0.0f
                var lit: Int = 0
                var lowest: Float = Float.MAX_VALUE
                var highest: Float = -Float.MAX_VALUE
                for (i in 0 until texels) {
                    val at: Int = (level * texels + i) * 4
                    val e: Float = fill[at] + fill[at + 1] + fill[at + 2]
                    energy += e
                    if (e > 0.01f) lit++
                    val floor: Float = fill[at + 3]
                    if (floor < NO_FLOOR) {
                        if (floor < lowest) lowest = floor
                        if (floor > highest) highest = floor
                    }
                }
                summary.append(" level ").append(level).append(": lit texels ").append(lit).append(" energy ").append(energy.toInt()).append(" floor y ").append(lowest.toInt()).append("..").append(highest.toInt())
            }
            println("HDDEBUG light grid settings: lightGrid " + HdSettings.lightGrid + " lights " + HdSettings.lights + " flags " + Integer.toHexString(uniforms.flags))
            val player = com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate.localPlayer
            if (player != null) {
                val px: Int = player.x shr 9
                val pz: Int = player.z shr 9
                for (s in 0 until seedCount) {
                    if (Math.abs(seedX[s] - px) <= 10 && Math.abs(seedZ[s] - pz) <= 10) {
                        println("HDDEBUG light grid seed at " + seedX[s] + "," + seedZ[s] + " level " + seedLevel[s] + ".." + seedTop[s] + " reach " + seedReach[s] + " colour " + seedRed[s] + "," + seedGreen[s] + "," + seedBlue[s] + (if (seedLayer[s] > 0) " lights.json layer " + seedLayer[s] else " game light"))
                    }
                }
            }
            roomCensus(0)
            println("HDDEBUG light grid built: " + seedCount + " seeds (" + locs + " locs), " + size + " tiles, " + patchLayers + " patches, " + totalMs + " ms (flood " + floodNanos / 100000L / 10.0 + ", blur " + blurNanos / 100000L / 10.0 + ", upload " + uploadNanos / 100000L / 10.0 + ");" + summary)
            tileMap()
        }
    }

    /** The lights of lights.json on locs, then the game's map lights. */
    private fun collectSeeds(lights: HdLights) {
        seedCount = 0
        val locs: Int = lights.locLightCount()
        for (i in 0 until locs) {
            if (seedCount >= MAX_SEEDS) break
            val def = lights.locLightDef(i) ?: continue
            val s: Int = seedCount++
            seedX[s] = (lights.locLightX(i) + def.offsetX.toInt()) shr 9
            seedZ[s] = (lights.locLightZ(i) + def.offsetZ.toInt()) shr 9
            seedLevel[s] = lights.locLightLevel(i)
            seedTop[s] = seedLevel[s]
            seedRed[s] = def.red * def.intensity
            seedGreen[s] = def.green * def.intensity
            seedBlue[s] = def.blue * def.intensity
            seedReach[s] = minOf(MAX_REACH.toFloat(), maxOf(MIN_REACH, def.radius * FILL_REACH))
            seedLayer[s] = i + 1
        }
        val gameLights: Array<StaticPointLight?>? = Scene.aClass300Array1
        if (gameLights != null) {
            for (i in 0 until Scene.lightSlotCount) {
                if (seedCount >= MAX_SEEDS) break
                val light: StaticPointLight = gameLights[i] ?: continue
                val point: PointLight = light.pointLight ?: continue
                val s: Int = seedCount++
                seedX[s] = point.getX() shr 9
                seedZ[s] = point.getZ() shr 9
                seedLevel[s] = light.level
                seedTop[s] = if (light.growsUpwards) LEVELS - 1 else light.level
                val colour: Int = point.getColour()
                val intensity: Float = point.getIntensity().coerceIn(0.6f, 1.0f) * GAME_LIGHT_SCALE
                seedRed[s] = ((colour shr 16) and 0xFF) / 255.0f * intensity
                seedGreen[s] = ((colour shr 8) and 0xFF) / 255.0f * intensity
                seedBlue[s] = (colour and 0xFF) / 255.0f * intensity
                seedReach[s] = minOf(MAX_REACH.toFloat(), maxOf(MIN_REACH, point.getRadius() / 512.0f * FILL_REACH))
                seedLayer[s] = 0
            }
        }
    }

    /** The collision level of tile ([x], [z]) seen on [level]: a bridge tile's is the level below. */
    private fun clipLevel(level: Int, x: Int, z: Int): Int {
        val masks = SettingsBits.tileFunctionMasks
        if (level > 0 && masks != null && x in 0 until size && z in 0 until size) {
            val row = masks[1]?.get(x)
            if (row != null && (row[z].toInt() and TileFlag.BRIDGE) != 0) return level - 1
        }
        return level
    }

    private fun flags(map: CollisionMap, x: Int, z: Int): Int {
        if (x < 0 || z < 0 || x >= size || z >= size) return -1
        val rows = map.collisionFlags ?: return -1
        val row = rows[x - map.xOffset] ?: return -1
        return row[z - map.zOffset]
    }

    /** Whether light passes from tile (ax, az) to its orthogonal neighbour (bx, bz) on [map]. */
    private fun open(map: CollisionMap, ax: Int, az: Int, bx: Int, bz: Int): Boolean {
        val a: Int = flags(map, ax, az)
        val b: Int = flags(map, bx, bz)
        if (a == -1 || b == -1 || (b and LOS_SOLID) != 0) return false
        if (bx > ax) return (a and LOS_EAST) == 0 && (b and LOS_WEST) == 0
        if (bx < ax) return (a and LOS_WEST) == 0 && (b and LOS_EAST) == 0
        if (bz > az) return (a and LOS_NORTH) == 0 && (b and LOS_SOUTH) == 0
        return (a and LOS_SOUTH) == 0 && (b and LOS_NORTH) == 0
    }

    /** After a loc change: the stale seeds flooded again, then everything accumulated, blurred and uploaded. */
    private fun refresh() {
        val started: Long = System.nanoTime()
        var flooded: Int = 0
        for (s in 0 until seedCount) {
            if (!seedStale[s]) continue
            seedStale[s] = false
            cacheFlood(s)
            flooded++
        }
        val texels: Int = width * width
        for (i in 0 until LEVELS * texels) {
            fill[i * 4] = 0.0f
            fill[i * 4 + 1] = 0.0f
            fill[i * 4 + 2] = 0.0f
        }
        accumulate()
        blur()
        upload()
        val totalMs: Double = ((System.nanoTime() - started) / 100000L) / 10.0
        if (DEBUG) {
            println("HDDEBUG light grid refreshed: " + flooded + " seeds flooded again, " + totalMs + " ms (blur " + blurNanos / 100000L / 10.0 + ", upload " + uploadNanos / 100000L / 10.0 + ")")
        }
    }

    /** Floods seed [s] on its own level into its cached box of path distances. */
    private fun cacheFlood(s: Int) {
        val r: Int = minOf(MAX_REACH, kotlin.math.ceil(seedReach[s].toDouble()).toInt())
        val side: Int = 2 * r + 1
        var cache: FloatArray? = seedDistance[s]
        if (cache == null || cache.size != side * side) {
            cache = FloatArray(side * side)
            seedDistance[s] = cache
        }
        if (!dijkstra(s, seedLevel[s])) {
            java.util.Arrays.fill(cache, Float.MAX_VALUE)
            return
        }
        System.arraycopy(boxDistance, 0, cache, 0, side * side)
    }

    /** Every seed's fill into the (cleared) grid and its visibility patch, from the cached floods. */
    private fun accumulate() {
        for (level in 0 until LEVELS) {
            litMinX[level] = width
            litMinZ[level] = width
            litMaxX[level] = -1
            litMaxZ[level] = -1
        }
        java.util.Arrays.fill(patches, 0.toByte())
        java.util.Arrays.fill(patches, 0, PATCH * PATCH, (-1).toByte())
        for (s in 0 until seedCount) {
            val reach: Float = seedReach[s]
            if (reach <= 0.0f) continue
            val r: Int = minOf(MAX_REACH, kotlin.math.ceil(reach.toDouble()).toInt())
            val side: Int = 2 * r + 1
            for (level in seedLevel[s]..seedTop[s]) {
                if (level < 0 || level >= LEVELS) continue
                val distances: FloatArray
                if (level == seedLevel[s]) {
                    distances = seedDistance[s] ?: continue
                } else {
                    // a game light that grows upwards: its upper levels flooded afresh (they are few)
                    if (!dijkstra(s, level)) continue
                    distances = boxDistance
                }
                deposit(s, level, r, side, distances)
            }
        }
    }

    /**
     * Dijkstra from seed [s] over the tiles of [level] within its reach, into boxDistance (the box
     * (2r + 1)^2 about the seed); false when the level has no collision map.
     */
    private fun dijkstra(s: Int, level: Int): Boolean {
        val reach: Float = seedReach[s]
        val r: Int = minOf(MAX_REACH, kotlin.math.ceil(reach.toDouble()).toInt())
        val side: Int = 2 * r + 1
        val cells: Int = side * side
        val originX: Int = seedX[s] - r
        val originZ: Int = seedZ[s] - r
        val map: CollisionMap = MapRegion.aClass104Array3[clipLevel(level, seedX[s], seedZ[s])] ?: return false
        run {
            for (i in 0 until cells) {
                boxDistance[i] = Float.MAX_VALUE
                heapSlot[i] = -1
            }
            heapCount = 0
            val start: Int = r * side + r
            boxDistance[start] = 0.0f
            push(start)
            while (heapCount > 0) {
                val cell: Int = pop()
                val d: Float = boxDistance[cell]
                if (d >= reach) continue
                val cx: Int = cell % side
                val cz: Int = cell / side
                val tx: Int = originX + cx
                val tz: Int = originZ + cz
                for (dz in -1..1) {
                    for (dx in -1..1) {
                        if (dx == 0 && dz == 0) continue
                        val nx: Int = cx + dx
                        val nz: Int = cz + dz
                        if (nx < 0 || nz < 0 || nx >= side || nz >= side) continue
                        val next: Int = nz * side + nx
                        val step: Float = if (dx != 0 && dz != 0) DIAGONAL else 1.0f
                        val nd: Float = d + step
                        if (nd >= boxDistance[next]) continue
                        val ux: Int = tx + dx
                        val uz: Int = tz + dz
                        val passable: Boolean = if (dx != 0 && dz != 0) {
                            // round a corner only where both ways round it are open
                            open(map, tx, tz, ux, tz) && open(map, ux, tz, ux, uz) && open(map, tx, tz, tx, uz) && open(map, tx, uz, ux, uz)
                        } else {
                            open(map, tx, tz, ux, uz)
                        }
                        if (!passable) continue
                        boxDistance[next] = nd
                        if (heapSlot[next] < 0) push(next) else lift(heapSlot[next])
                    }
                }
            }
        }
        return true
    }

    /**
     * What arrived from seed [s] on [level], from its box of path [distances]: the fill (linear in the
     * path distance, so a room reads to its far wall) and the visibility patch (how the path distance
     * compares with the straight one, squared).
     */
    private fun deposit(s: Int, level: Int, r: Int, side: Int, distances: FloatArray) {
        val reach: Float = seedReach[s]
        val cells: Int = side * side
        val originX: Int = seedX[s] - r
        val originZ: Int = seedZ[s] - r
        run {
            val layerAt: Int = level * width * width * 4
            val red: Float = seedRed[s] * FILL_SCALE
            val green: Float = seedGreen[s] * FILL_SCALE
            val blue: Float = seedBlue[s] * FILL_SCALE
            val patchAt: Int = seedLayer[s] * PATCH * PATCH
            for (i in 0 until cells) {
                val d: Float = distances[i]
                val cx: Int = i % side
                val cz: Int = i / side
                val tx: Int = originX + cx
                val tz: Int = originZ + cz
                if (tx < 0 || tz < 0 || tx >= size || tz >= size) continue
                if (d < reach) {
                    val e: Float = 1.0f - d / reach
                    if (tx * TEXELS_PER_TILE < litMinX[level]) litMinX[level] = tx * TEXELS_PER_TILE
                    if (tz * TEXELS_PER_TILE < litMinZ[level]) litMinZ[level] = tz * TEXELS_PER_TILE
                    if (tx * TEXELS_PER_TILE + TEXELS_PER_TILE - 1 > litMaxX[level]) litMaxX[level] = tx * TEXELS_PER_TILE + TEXELS_PER_TILE - 1
                    if (tz * TEXELS_PER_TILE + TEXELS_PER_TILE - 1 > litMaxZ[level]) litMaxZ[level] = tz * TEXELS_PER_TILE + TEXELS_PER_TILE - 1
                    for (sz in 0 until TEXELS_PER_TILE) {
                        for (sx in 0 until TEXELS_PER_TILE) {
                            val at: Int = layerAt + ((tz * TEXELS_PER_TILE + sz) * width + tx * TEXELS_PER_TILE + sx) * 4
                            fill[at] += red * e
                            fill[at + 1] += green * e
                            fill[at + 2] += blue * e
                        }
                    }
                }
                if (seedLayer[s] > 0 && level == seedLevel[s]) {
                    val px: Int = cx - r + PATCH_HALF
                    val pz: Int = cz - r + PATCH_HALF
                    if (px in 0 until PATCH && pz in 0 until PATCH) {
                        val ex: Float = (cx - r).toFloat()
                        val ez: Float = (cz - r).toFloat()
                        val straight: Float = kotlin.math.sqrt(ex * ex + ez * ez)
                        var vis: Float = 0.0f
                        if (d < reach && straight < reach) {
                            val ratio: Float = (1.0f - d / reach) / (1.0f - straight / reach)
                            vis = (ratio * ratio).coerceIn(0.0f, 1.0f)
                        }
                        patches[patchAt + pz * PATCH + px] = (vis * 255.0f + 0.5f).toInt().toByte()
                    }
                }
            }
        }
    }

    // a binary min-heap of box cells on boxDistance

    private fun push(cell: Int) {
        heap[heapCount] = cell
        heapSlot[cell] = heapCount
        heapCount++
        lift(heapCount - 1)
    }

    private fun lift(slot: Int) {
        var i: Int = slot
        while (i > 0) {
            val parent: Int = (i - 1) / 2
            if (boxDistance[heap[parent]] <= boxDistance[heap[i]]) break
            swapHeap(i, parent)
            i = parent
        }
    }

    private fun pop(): Int {
        val top: Int = heap[0]
        heapCount--
        heapSlot[top] = -2
        if (heapCount > 0) {
            heap[0] = heap[heapCount]
            heapSlot[heap[0]] = 0
            var i: Int = 0
            while (true) {
                val left: Int = 2 * i + 1
                val right: Int = left + 1
                var least: Int = i
                if (left < heapCount && boxDistance[heap[left]] < boxDistance[heap[least]]) least = left
                if (right < heapCount && boxDistance[heap[right]] < boxDistance[heap[least]]) least = right
                if (least == i) break
                swapHeap(i, least)
                i = least
            }
        }
        return top
    }

    private fun swapHeap(a: Int, b: Int) {
        val t: Int = heap[a]
        heap[a] = heap[b]
        heap[b] = t
        heapSlot[heap[a]] = a
        heapSlot[heap[b]] = b
    }

    /** One 3x3 pass over the fill, in place per level, never averaging across a closed wall. */
    private fun blur() {
        val started: Long = System.nanoTime()
        val texels: Int = width * width
        knee()
        if (blurSource.size != texels * 4) blurSource = FloatArray(texels * 4)
        val source: FloatArray = blurSource
        for (level in 0 until LEVELS) {
            if (litMaxX[level] < 0) continue
            val layerAt: Int = level * texels * 4
            System.arraycopy(fill, layerAt, source, 0, texels * 4)
            val map: CollisionMap? = MapRegion.aClass104Array3[level]
            // only where something was deposited (and one texel round it): most of a scene is unlit
            val x0: Int = maxOf(0, litMinX[level] - 1)
            val z0: Int = maxOf(0, litMinZ[level] - 1)
            val x1: Int = minOf(width - 1, litMaxX[level] + 1)
            val z1: Int = minOf(width - 1, litMaxZ[level] + 1)
            for (z in z0..z1) {
                for (x in x0..x1) {
                    var r: Float = 0.0f
                    var g: Float = 0.0f
                    var b: Float = 0.0f
                    var weight: Float = 0.0f
                    val tx: Int = x / TEXELS_PER_TILE
                    val tz: Int = z / TEXELS_PER_TILE
                    for (dz in -1..1) {
                        for (dx in -1..1) {
                            val nx: Int = x + dx
                            val nz: Int = z + dz
                            if (nx < 0 || nz < 0 || nx >= width || nz >= width) continue
                            val ntx: Int = nx / TEXELS_PER_TILE
                            val ntz: Int = nz / TEXELS_PER_TILE
                            if (map != null && (ntx != tx || ntz != tz)) {
                                // a neighbour in another tile counts only through an open edge
                                val through: Boolean = if (ntx != tx && ntz != tz) {
                                    (open(map, tx, tz, ntx, tz) && open(map, ntx, tz, ntx, ntz)) || (open(map, tx, tz, tx, ntz) && open(map, tx, ntz, ntx, ntz))
                                } else {
                                    open(map, tx, tz, ntx, ntz)
                                }
                                if (!through) continue
                            }
                            val w: Float = if (dx == 0 && dz == 0) 2.0f else 1.0f
                            val at: Int = (nz * width + nx) * 4
                            r += source[at] * w
                            g += source[at + 1] * w
                            b += source[at + 2] * w
                            weight += w
                        }
                    }
                    val at: Int = layerAt + (z * width + x) * 4
                    fill[at] = r / weight
                    fill[at + 1] = g / weight
                    fill[at + 2] = b / weight
                }
            }
        }
        blurNanos = System.nanoTime() - started
    }

    private var blurSource: FloatArray = FloatArray(0)

    /** The soft ceiling (KNEE) over every deposited texel's colour. */
    private fun knee() {
        val texels: Int = width * width
        for (level in 0 until LEVELS) {
            if (litMaxX[level] < 0) continue
            val layerAt: Int = level * texels * 4
            for (z in litMinZ[level]..litMaxZ[level]) {
                for (x in litMinX[level]..litMaxX[level]) {
                    val at: Int = layerAt + (z * width + x) * 4
                    for (c in 0 until 3) {
                        val v: Float = fill[at + c]
                        if (v > 0.0f) fill[at + c] = KNEE * (1.0f - kotlin.math.exp(-v / KNEE))
                    }
                }
            }
        }
    }

    /** Whether seed [s] stands in component [c] of [component]: on one of its tiles, or on a wall or solid loc beside one. */
    private fun seedInComponent(s: Int, c: Int, component: IntArray): Boolean {
        val x: Int = seedX[s]
        val z: Int = seedZ[s]
        if (x < 0 || z < 0 || x >= size || z >= size) return false
        if (component[z * size + x] == c) return true
        if (x > 0 && component[z * size + x - 1] == c) return true
        if (x < size - 1 && component[z * size + x + 1] == c) return true
        if (z > 0 && component[(z - 1) * size + x] == c) return true
        if (z < size - 1 && component[(z + 1) * size + x] == c) return true
        return false
    }

    /**
     * client.test.hdDebug: every room of the player's level (the tiles joined by edges open to a
     * projectile, 9 to 120 of them, so halls and houses, not the outdoors), with its lamps, lit
     * tiles, faintest tile, and the fill at its tile farthest from a lamp: the one-lamp rooms are the
     * measure of "one lamp lights its room".
     */
    private fun roomCensus(level: Int) {
        val map: CollisionMap = MapRegion.aClass104Array3[level] ?: return
        val texels: Int = width * width
        val component: IntArray = IntArray(size * size)
        val queue: IntArray = IntArray(size * size)
        var rooms: Int = 0
        var oneLamp: Int = 0
        var lampless: Int = 0
        var lamplessLit: Int = 0
        var lamplessReadable: Int = 0
        for (startZ in 0 until size) {
            for (startX in 0 until size) {
                val start: Int = startZ * size + startX
                if (component[start] != 0) continue
                val f: Int = flags(map, startX, startZ)
                if (f == -1 || (f and LOS_SOLID) != 0) continue
                rooms++
                component[start] = rooms
                var head: Int = 0
                var tail: Int = 0
                queue[tail++] = start
                var tiles: Int = 0
                var lit: Int = 0
                var faintest: Float = Float.MAX_VALUE
                var minX: Int = size
                var maxX: Int = -1
                var minZ: Int = size
                var maxZ: Int = -1
                while (head < tail) {
                    val cell: Int = queue[head++]
                    val cx: Int = cell % size
                    val cz: Int = cell / size
                    tiles++
                    if (cx < minX) minX = cx
                    if (cx > maxX) maxX = cx
                    if (cz < minZ) minZ = cz
                    if (cz > maxZ) maxZ = cz
                    var e: Float = 0.0f
                    for (sz in 0 until TEXELS_PER_TILE) {
                        for (sx in 0 until TEXELS_PER_TILE) {
                            val at: Int = level * texels * 4 + ((cz * TEXELS_PER_TILE + sz) * width + cx * TEXELS_PER_TILE + sx) * 4
                            e += fill[at] + fill[at + 1] + fill[at + 2]
                        }
                    }
                    e /= (TEXELS_PER_TILE * TEXELS_PER_TILE).toFloat()
                    if (e > 0.01f) lit++
                    if (e < faintest) faintest = e
                    for (dir in 0 until 4) {
                        val nx: Int = cx + (if (dir == 0) 1 else if (dir == 1) -1 else 0)
                        val nz: Int = cz + (if (dir == 2) 1 else if (dir == 3) -1 else 0)
                        if (nx < 0 || nz < 0 || nx >= size || nz >= size) continue
                        val next: Int = nz * size + nx
                        if (component[next] != 0) continue
                        if (!open(map, cx, cz, nx, nz)) continue
                        component[next] = rooms
                        queue[tail++] = next
                    }
                }
                if (tiles > 120) {
                    // the outdoors (or a very large hall): every lit tile must be near a lamp of its own
                    var outLit: Int = 0
                    var outFar: Int = 0
                    var outFarReadable: Int = 0
                    for (i in 0 until tail) {
                        val cell: Int = queue[i]
                        val cx: Int = cell % size
                        val cz: Int = cell / size
                        var e: Float = 0.0f
                        for (sz in 0 until TEXELS_PER_TILE) {
                            for (sx in 0 until TEXELS_PER_TILE) {
                                val at: Int = level * texels * 4 + ((cz * TEXELS_PER_TILE + sz) * width + cx * TEXELS_PER_TILE + sx) * 4
                                e += fill[at] + fill[at + 1] + fill[at + 2]
                            }
                        }
                        e /= (TEXELS_PER_TILE * TEXELS_PER_TILE).toFloat()
                        if (e <= 0.01f) continue
                        outLit++
                        // within some lamp's own reach (and the blur's tile)? else the light came from elsewhere
                        var covered: Boolean = false
                        for (s in 0 until seedCount) {
                            if (seedLevel[s] != level || !seedInComponent(s, rooms, component)) continue
                            val dx: Float = (seedX[s] - cx).toFloat()
                            val dz: Float = (seedZ[s] - cz).toFloat()
                            if (kotlin.math.sqrt(dx * dx + dz * dz) <= seedReach[s] + 1.5f) {
                                covered = true
                                break
                            }
                        }
                        if (!covered) {
                            outFar++
                            if (e > 0.15f) outFarReadable++
                        }
                    }
                    println("HDDEBUG open area of " + tiles + " tiles: " + outLit + " lit, of them " + outFar + " beyond the reach of every lamp of the area (" + outFarReadable + " readable): light through a wall would show here")
                    continue
                }
                if (tiles < 9) continue
                var lamps: Int = 0
                for (s in 0 until seedCount) {
                    if (seedLevel[s] == level && seedInComponent(s, rooms, component)) lamps++
                }
                if (lamps == 0) {
                    // a room with no lamp: dark, but for faint spill through an open door
                    var brightest: Float = 0.0f
                    for (i in 0 until tail) {
                        val cell: Int = queue[i]
                        val cx: Int = cell % size
                        val cz: Int = cell / size
                        var e: Float = 0.0f
                        for (sz in 0 until TEXELS_PER_TILE) {
                            for (sx in 0 until TEXELS_PER_TILE) {
                                val at: Int = level * texels * 4 + ((cz * TEXELS_PER_TILE + sz) * width + cx * TEXELS_PER_TILE + sx) * 4
                                e += fill[at] + fill[at + 1] + fill[at + 2]
                            }
                        }
                        e /= (TEXELS_PER_TILE * TEXELS_PER_TILE).toFloat()
                        if (e > brightest) brightest = e
                    }
                    lampless++
                    if (brightest > 0.01f) lamplessLit++
                    if (brightest > 0.15f) lamplessReadable++
                    continue
                }
                // the tile farthest from any lamp of the room, and its fill
                var farTile: Float = 0.0f
                var farFill: Float = 0.0f
                for (i in 0 until tail) {
                    val cell: Int = queue[i]
                    val cx: Int = cell % size
                    val cz: Int = cell / size
                    var nearest: Float = Float.MAX_VALUE
                    for (s in 0 until seedCount) {
                        if (seedLevel[s] != level || !seedInComponent(s, rooms, component)) continue
                        val dx: Float = (seedX[s] - cx).toFloat()
                        val dz: Float = (seedZ[s] - cz).toFloat()
                        val dist: Float = kotlin.math.sqrt(dx * dx + dz * dz)
                        if (dist < nearest) nearest = dist
                    }
                    if (nearest > farTile) {
                        farTile = nearest
                        var e: Float = 0.0f
                        for (sz in 0 until TEXELS_PER_TILE) {
                            for (sx in 0 until TEXELS_PER_TILE) {
                                val at: Int = level * texels * 4 + ((cz * TEXELS_PER_TILE + sz) * width + cx * TEXELS_PER_TILE + sx) * 4
                                e += fill[at] + fill[at + 1] + fill[at + 2]
                            }
                        }
                        farFill = e / (TEXELS_PER_TILE * TEXELS_PER_TILE).toFloat()
                    }
                }
                if (lamps == 1) {
                    oneLamp++
                    // the one-lamp room drawn: 'L' the lamp, '#'/'+'/'.' the fill, ' ' none, '%' a solid loc in it, '~' not the room
                    for (cz in maxZ downTo minZ) {
                        val line: StringBuilder = StringBuilder()
                        for (cx in minX..maxX) {
                            val inRoom: Boolean = component[cz * size + cx] == rooms
                            var isLamp: Boolean = false
                            for (s in 0 until seedCount) if (seedLevel[s] == level && seedX[s] == cx && seedZ[s] == cz) isLamp = true
                            if (isLamp) {
                                line.append('L')
                                continue
                            }
                            if (!inRoom) {
                                line.append(if ((flags(map, cx, cz) and LOS_SOLID) != 0) '%' else '~')
                                continue
                            }
                            var e: Float = 0.0f
                            for (sz in 0 until TEXELS_PER_TILE) {
                                for (sx in 0 until TEXELS_PER_TILE) {
                                    val at: Int = level * texels * 4 + ((cz * TEXELS_PER_TILE + sz) * width + cx * TEXELS_PER_TILE + sx) * 4
                                    e += fill[at] + fill[at + 1] + fill[at + 2]
                                }
                            }
                            e /= (TEXELS_PER_TILE * TEXELS_PER_TILE).toFloat()
                            line.append(if (e > 0.6f) '#' else if (e > 0.15f) '+' else if (e > 0.01f) '.' else ' ')
                        }
                        println("HDDEBUG   |" + line + "|")
                    }
                }
                println("HDDEBUG room at " + minX + ".." + maxX + "," + minZ + ".." + maxZ + " (" + (maxX - minX + 1) + "x" + (maxZ - minZ + 1) + ", " + tiles + " tiles): lamps " + lamps + ", lit " + lit + "/" + tiles + ", faintest " + String.format(java.util.Locale.ROOT, "%.2f", faintest) + ", farthest tile " + String.format(java.util.Locale.ROOT, "%.1f", farTile) + " from a lamp at " + String.format(java.util.Locale.ROOT, "%.2f", farFill))
            }
        }
        println("HDDEBUG rooms on level " + level + ": " + oneLamp + " with one lamp; " + lampless + " rooms with no lamp, " + lamplessLit + " of them with any light (" + lamplessReadable + " with readable light): a room with no lamp stays dark but for spill through an open door")
    }

    /** client.test.hdDebug: the fill of level 0 round the player as a tile map ('#' strong, '+' some, '.' faint, ' ' none, 'P' the player). */
    private fun tileMap() {
        val player = com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate.localPlayer ?: return
        val px: Int = player.x shr 9
        val pz: Int = player.z shr 9
        val level: Int = player.level.toInt().coerceIn(0, LEVELS - 1)
        val texels: Int = width * width
        // the player's room: the tiles reached from the player's through edges open to a projectile
        // (the same rule the flood uses), within the window; a breadth-first walk
        val half: Int = 14
        val span: Int = 2 * half + 1
        val inside: BooleanArray = BooleanArray(span * span)
        val map: CollisionMap? = MapRegion.aClass104Array3[clipLevel(level, px, pz)]
        if (map != null) {
            val queue: IntArray = IntArray(span * span)
            var head: Int = 0
            var tail: Int = 0
            inside[half * span + half] = true
            queue[tail++] = half * span + half
            while (head < tail) {
                val cell: Int = queue[head++]
                val cx: Int = cell % span
                val cz: Int = cell / span
                for (dir in 0 until 4) {
                    val nx: Int = cx + (if (dir == 0) 1 else if (dir == 1) -1 else 0)
                    val nz: Int = cz + (if (dir == 2) 1 else if (dir == 3) -1 else 0)
                    if (nx < 0 || nz < 0 || nx >= span || nz >= span) continue
                    val next: Int = nz * span + nx
                    if (inside[next]) continue
                    if (!open(map, px - half + cx, pz - half + cz, px - half + nx, pz - half + nz)) continue
                    inside[next] = true
                    queue[tail++] = next
                }
            }
        }
        var roomTiles: Int = 0
        var roomLit: Int = 0
        var outsideTiles: Int = 0
        var outsideLit: Int = 0
        var outsideSolidLit: Int = 0
        var leaks: Int = 0
        // the lamps in the room, the room's faintest lit tile and its farthest tile from a lamp
        var roomSeeds: Int = 0
        var roomFaintest: Float = Float.MAX_VALUE
        var farTile: Float = 0.0f
        var farFill: Float = 0.0f
        var beyondSix: Int = 0
        var beyondSixReadable: Int = 0
        for (s in 0 until seedCount) {
            if (seedLevel[s] != level) continue
            val wx: Int = seedX[s] - px + half
            val wz: Int = seedZ[s] - pz + half
            if (wx in 0 until span && wz in 0 until span && inside[wz * span + wx]) roomSeeds++
        }
        println("HDDEBUG light grid level " + level + " round the player (" + px + "," + pz + "), north up, one char a tile ('#' strong, '+' some, '.' faint, ' ' none; lower case: outside the player's room):")
        for (tz in pz + half downTo pz - half) {
            val line: StringBuilder = StringBuilder()
            for (tx in px - half..px + half) {
                if (tx < 0 || tz < 0 || tx >= size || tz >= size) {
                    line.append('?')
                    continue
                }
                var e: Float = 0.0f
                for (sz in 0 until TEXELS_PER_TILE) {
                    for (sx in 0 until TEXELS_PER_TILE) {
                        val at: Int = level * texels * 4 + ((tz * TEXELS_PER_TILE + sz) * width + tx * TEXELS_PER_TILE + sx) * 4
                        e += fill[at] + fill[at + 1] + fill[at + 2]
                    }
                }
                e /= (TEXELS_PER_TILE * TEXELS_PER_TILE).toFloat()
                val room: Boolean = inside[(tz - pz + half) * span + (tx - px + half)]
                if (e > 0.01f) {
                    // the straight distance to the nearest lamp on this level
                    var nearest: Float = Float.MAX_VALUE
                    for (s in 0 until seedCount) {
                        if (seedLevel[s] != level) continue
                        val dx: Float = (seedX[s] - tx).toFloat()
                        val dz: Float = (seedZ[s] - tz).toFloat()
                        val dist: Float = kotlin.math.sqrt(dx * dx + dz * dz)
                        if (dist < nearest) nearest = dist
                    }
                    if (nearest > 6.0f) {
                        beyondSix++
                        if (e > 0.15f) beyondSixReadable++
                    }
                    if (room && nearest > farTile) {
                        farTile = nearest
                        farFill = e
                    }
                }
                if (room) {
                    roomTiles++
                    if (e > 0.01f) roomLit++
                    if (e < roomFaintest) roomFaintest = e
                } else {
                    outsideTiles++
                    if (e > 0.01f) {
                        outsideLit++
                        val solid: Boolean = map != null && (flags(map, tx, tz) and LOS_SOLID) != 0
                        if (solid) {
                            outsideSolidLit++
                        } else {
                            // a lit open tile next to the room across a closed edge: light through a wall
                            val wx: Int = tx - px + half
                            val wz: Int = tz - pz + half
                            var touchesRoom: Boolean = false
                            if (wx > 0 && inside[wz * span + wx - 1]) touchesRoom = true
                            if (wx < span - 1 && inside[wz * span + wx + 1]) touchesRoom = true
                            if (wz > 0 && inside[(wz - 1) * span + wx]) touchesRoom = true
                            if (wz < span - 1 && inside[(wz + 1) * span + wx]) touchesRoom = true
                            if (touchesRoom) leaks++
                        }
                    }
                }
                if (tx == px && tz == pz) {
                    line.append('P')
                    continue
                }
                val c: Char = if (e > 0.6f) '#' else if (e > 0.15f) '+' else if (e > 0.01f) '.' else ' '
                line.append(if (room) c else if (c == '#') 'x' else if (c == '+') 'o' else if (c == '.') ',' else '_')
            }
            println("HDDEBUG |" + line + "|")
        }
        println("HDDEBUG light grid: the player's room has " + roomTiles + " tiles in the window, " + roomLit + " of them lit; outside it " + outsideTiles + " tiles, " + outsideLit + " lit, of which " + outsideSolidLit + " are solid locs (booths, stalls) and " + leaks + " are open tiles next to the room across a wall (light through a wall)")
        // the fill's profile from the room's strongest lamp along its longest open run: tile by tile,
        // the energy and the colour's ratio to the lamp's (a bounce term falls off smoothly and keeps
        // the lamp's hue)
        var strongest: Int = -1
        for (s in 0 until seedCount) {
            if (seedLevel[s] != level) continue
            val wx: Int = seedX[s] - px + half
            val wz: Int = seedZ[s] - pz + half
            if (wx !in 0 until span || wz !in 0 until span || !inside[wz * span + wx]) continue
            if (strongest < 0 || seedRed[s] + seedGreen[s] + seedBlue[s] > seedRed[strongest] + seedGreen[strongest] + seedBlue[strongest]) strongest = s
        }
        if (strongest >= 0 && map != null) {
            var bestDir: Int = 0
            var bestRun: Int = 0
            for (dir in 0 until 4) {
                var run: Int = 0
                var cx: Int = seedX[strongest]
                var cz: Int = seedZ[strongest]
                while (run < 12) {
                    val nx: Int = cx + (if (dir == 0) 1 else if (dir == 1) -1 else 0)
                    val nz: Int = cz + (if (dir == 2) 1 else if (dir == 3) -1 else 0)
                    if (!open(map, cx, cz, nx, nz)) break
                    cx = nx
                    cz = nz
                    run++
                }
                if (run > bestRun) {
                    bestRun = run
                    bestDir = dir
                }
            }
            val lampSum: Float = seedRed[strongest] + seedGreen[strongest] + seedBlue[strongest]
            val line: StringBuilder = StringBuilder()
            var cx: Int = seedX[strongest]
            var cz: Int = seedZ[strongest]
            for (step in 0..bestRun) {
                var r: Float = 0.0f
                var g: Float = 0.0f
                var b: Float = 0.0f
                for (sz in 0 until TEXELS_PER_TILE) {
                    for (sx in 0 until TEXELS_PER_TILE) {
                        val at: Int = level * texels * 4 + ((cz * TEXELS_PER_TILE + sz) * width + cx * TEXELS_PER_TILE + sx) * 4
                        r += fill[at]
                        g += fill[at + 1]
                        b += fill[at + 2]
                    }
                }
                val n: Float = (TEXELS_PER_TILE * TEXELS_PER_TILE).toFloat()
                r /= n
                g /= n
                b /= n
                val sum: Float = r + g + b
                // the hue as the red share of the sum, against the lamp's
                val hue: String = if (sum > 0.001f) String.format(java.util.Locale.ROOT, "%.2f", r / sum) else "-"
                line.append(" ").append(step).append(":").append(String.format(java.util.Locale.ROOT, "%.2f", sum)).append("/").append(hue)
                cx += if (bestDir == 0) 1 else if (bestDir == 1) -1 else 0
                cz += if (bestDir == 2) 1 else if (bestDir == 3) -1 else 0
            }
            println("HDDEBUG light grid: fill from the room's strongest lamp (" + String.format(java.util.Locale.ROOT, "%.2f", lampSum * FILL_SCALE) + " at red share " + String.format(java.util.Locale.ROOT, "%.2f", seedRed[strongest] / lampSum) + ") along " + bestRun + " open tiles, tile:energy/red share:" + line)
        }
        println("HDDEBUG light grid: lamps in the room " + roomSeeds + ", the room's faintest tile " + String.format(java.util.Locale.ROOT, "%.2f", if (roomFaintest == Float.MAX_VALUE) 0.0f else roomFaintest) + ", its farthest lit tile " + String.format(java.util.Locale.ROOT, "%.1f", farTile) + " tiles from a lamp at " + String.format(java.util.Locale.ROOT, "%.2f", farFill) + "; lit tiles in the window over 6 tiles from any lamp " + beyondSix + ", of them readable (over 0.15) " + beyondSixReadable)
    }

    /**
     * Each level's floor height per tile into the grid's alpha (the game's y, down), NO_FLOOR where
     * the level has no ground there: every level has a height everywhere, so without this a wall
     * fragment above its floor matched the level above (a roof over the hall) and got no fill.
     */
    private fun writeFloors() {
        val texels: Int = width * width
        val grounds = Scene.aClass65Array1
        for (level in 0 until LEVELS) {
            val layerAt: Int = level * texels * 4
            val heights = grounds?.getOrNull(level)?.tileHeights
            val map: CollisionMap? = MapRegion.aClass104Array3[level]
            for (z in 0 until width) {
                val tz: Int = minOf(size - 1, z / TEXELS_PER_TILE)
                for (x in 0 until width) {
                    val tx: Int = minOf(size - 1, x / TEXELS_PER_TILE)
                    val column = heights?.getOrNull(tx)
                    var floor: Float = if (column != null && tz < column.size) column[tz].toFloat() else NO_FLOOR
                    if (level > 0 && map != null && (flags(map, tx, tz) and BLOCKWALK) != 0) floor = NO_FLOOR
                    fill[layerAt + (z * width + x) * 4 + 3] = floor
                }
            }
        }
    }

    private fun upload() {
        val started: Long = System.nanoTime()
        val previous: Int = HdGl.state.activeTextureUnit
        val data: ByteBuffer = pixels!!
        data.clear()
        data.asFloatBuffer().put(fill)
        data.position(0).limit(fill.size * 4)
        GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + GRID_UNIT)
        if (gridTexture == 0) gridTexture = GL33C.glGenTextures()
        GL33C.glBindTexture(GL33C.GL_TEXTURE_2D_ARRAY, gridTexture)
        if (gridWidth != width) {
            GL33C.glTexImage3D(GL33C.GL_TEXTURE_2D_ARRAY, 0, GL33C.GL_RGBA32F, width, width, LEVELS, 0, GL33C.GL_RGBA, GL33C.GL_FLOAT, data)
            GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D_ARRAY, GL33C.GL_TEXTURE_MIN_FILTER, GL33C.GL_LINEAR)
            GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D_ARRAY, GL33C.GL_TEXTURE_MAG_FILTER, GL33C.GL_LINEAR)
            GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D_ARRAY, GL33C.GL_TEXTURE_WRAP_S, GL33C.GL_CLAMP_TO_EDGE)
            GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D_ARRAY, GL33C.GL_TEXTURE_WRAP_T, GL33C.GL_CLAMP_TO_EDGE)
            gridWidth = width
        } else {
            GL33C.glTexSubImage3D(GL33C.GL_TEXTURE_2D_ARRAY, 0, 0, 0, 0, width, width, LEVELS, GL33C.GL_RGBA, GL33C.GL_FLOAT, data)
        }
        val patchData: ByteBuffer = patchPixels!!
        patchData.clear()
        patchData.put(patches)
        patchData.position(0).limit(patches.size)
        GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + VIS_UNIT)
        if (patchTexture == 0) patchTexture = GL33C.glGenTextures()
        GL33C.glBindTexture(GL33C.GL_TEXTURE_2D_ARRAY, patchTexture)
        GL33C.glPixelStorei(GL33C.GL_UNPACK_ALIGNMENT, 1)
        if (patchTextureLayers != patchLayers) {
            GL33C.glTexImage3D(GL33C.GL_TEXTURE_2D_ARRAY, 0, GL33C.GL_R8, PATCH, PATCH, patchLayers, 0, GL33C.GL_RED, GL33C.GL_UNSIGNED_BYTE, patchData)
            GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D_ARRAY, GL33C.GL_TEXTURE_MIN_FILTER, GL33C.GL_LINEAR)
            GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D_ARRAY, GL33C.GL_TEXTURE_MAG_FILTER, GL33C.GL_LINEAR)
            GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D_ARRAY, GL33C.GL_TEXTURE_WRAP_S, GL33C.GL_CLAMP_TO_EDGE)
            GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D_ARRAY, GL33C.GL_TEXTURE_WRAP_T, GL33C.GL_CLAMP_TO_EDGE)
            patchTextureLayers = patchLayers
        } else {
            GL33C.glTexSubImage3D(GL33C.GL_TEXTURE_2D_ARRAY, 0, 0, 0, 0, PATCH, PATCH, patchLayers, GL33C.GL_RED, GL33C.GL_UNSIGNED_BYTE, patchData)
        }
        GL33C.glPixelStorei(GL33C.GL_UNPACK_ALIGNMENT, 4)
        GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + previous)
        uploadNanos = System.nanoTime() - started
    }

    /** Binds both textures on their units (the emulator tracks the active unit, so it is put back). */
    private fun bind() {
        if (gridTexture == 0) return
        val previous: Int = HdGl.state.activeTextureUnit
        GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + GRID_UNIT)
        GL33C.glBindTexture(GL33C.GL_TEXTURE_2D_ARRAY, gridTexture)
        GL33C.glBindSampler(GRID_UNIT, 0)
        GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + VIS_UNIT)
        GL33C.glBindTexture(GL33C.GL_TEXTURE_2D_ARRAY, patchTexture)
        GL33C.glBindSampler(VIS_UNIT, 0)
        GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + previous)
    }

    fun release() {
        if (gridTexture != 0) {
            GL33C.glDeleteTextures(gridTexture)
            gridTexture = 0
            gridWidth = 0
        }
        if (patchTexture != 0) {
            GL33C.glDeleteTextures(patchTexture)
            patchTexture = 0
            patchTextureLayers = 0
        }
        dirty = true
    }
}

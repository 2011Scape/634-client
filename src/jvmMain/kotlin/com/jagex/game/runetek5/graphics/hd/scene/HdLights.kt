/*
 * Not part of the original client: 634HD (toolkit 6). Point lights from lights.json (HdContent), with
 * client.hd.lights: on locs (static and animated, found in the scene's entity lists whenever the scene
 * changes), NPCs, projectiles and spot animations played on the map (found every frame). The nearest
 * to the player, up to HdFrameUniforms.MAX_LIGHTS, go into the frame's uniform block in eye space;
 * the scene shaders add them per pixel (hdPointLights in hd_lib.glsl).
 */
package com.jagex.game.runetek5.graphics.hd.scene

import com.jagex.game.runetek5.NPCList
import com.jagex.game.runetek5.client.features.HdSettings
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.config.item.ItemType
import com.jagex.game.runetek5.config.rsinterface.itemcontainer.ItemContainer
import com.jagex.game.runetek5.config.sequence.PositionedGfx
import com.jagex.game.runetek5.cache.node.Node
import com.jagex.game.runetek5.entity.Entity
import com.jagex.game.runetek5.entity.NPCEntityNode
import com.jagex.game.runetek5.entity.ProjectileAnimation
import com.jagex.game.runetek5.entity.gameobject.GameObject
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.cache.node.lru.ProjectileAnimationNode
import com.jagex.game.runetek5.graphics.hd.content.HdContent
import com.jagex.game.runetek5.rendering.Renderers
import com.jagex.game.runetek5.scene.Scene

class HdLights {

    companion object {
        /** How far from the player a light is considered, in scene units (512 a tile). */
        private const val RANGE: Int = 40 * 512
        private const val MAX_CANDIDATES: Int = 4096

        /** Floats per GPU particle emitter (emitters). */
        const val EMITTER_FLOATS: Int = 7

        /** Function mask for the flame lookup's model: keeps its vertices, faces and face colours. */
        private const val FLAME_MASK: Int = 0x7C000
        const val MAX_EMITTERS: Int = 256
        private const val EMITTER_RANGE: Int = 24 * 512
    }

    // the loc lights, found when the scene changes: position, the definition, a phase for flicker
    private var locCount: Int = 0
    private val locX: IntArray = IntArray(MAX_CANDIDATES)
    private val locY: IntArray = IntArray(MAX_CANDIDATES)
    private val locZ: IntArray = IntArray(MAX_CANDIDATES)
    private val locLevel: IntArray = IntArray(MAX_CANDIDATES)
    private val locDef: Array<HdContent.LightDef?> = arrayOfNulls(MAX_CANDIDATES)

    /** Each loc's flame (HdModel.hdFlamePoint) in world space, for its particles; flameFound false: none. */
    private val locFlameX: IntArray = IntArray(MAX_CANDIDATES)
    private val locFlameY: IntArray = IntArray(MAX_CANDIDATES)
    private val locFlameZ: IntArray = IntArray(MAX_CANDIDATES)
    private val locFlameFound: BooleanArray = BooleanArray(MAX_CANDIDATES)
    private val locFlameWidth: IntArray = IntArray(MAX_CANDIDATES)
    private val locFlameHeight: IntArray = IntArray(MAX_CANDIDATES)

    /** Flames in model space by loc id, shape and rotation (id | shape << 16 | rotation << 22): x, y, z, way found (0 none), width, height. */
    private val flameCache: HashMap<Int, IntArray> = HashMap()

    /** Each candidate's particle source: its flame where its model has one, else the light. */
    private val emitX: FloatArray = FloatArray(MAX_CANDIDATES)
    private val emitY: FloatArray = FloatArray(MAX_CANDIDATES)
    private val emitZ: FloatArray = FloatArray(MAX_CANDIDATES)
    private val emitWidth: FloatArray = FloatArray(MAX_CANDIDATES)
    private val emitHeight: FloatArray = FloatArray(MAX_CANDIDATES)

    // this frame's candidates
    private var count: Int = 0
    private val x: FloatArray = FloatArray(MAX_CANDIDATES)
    private val y: FloatArray = FloatArray(MAX_CANDIDATES)
    private val z: FloatArray = FloatArray(MAX_CANDIDATES)
    private val distance: FloatArray = FloatArray(MAX_CANDIDATES)
    private val def: Array<HdContent.LightDef?> = arrayOfNulls(MAX_CANDIDATES)
    private val phase: FloatArray = FloatArray(MAX_CANDIDATES)
    // the loc light each candidate is (its index among the loc lights), -1 for the others
    private val locIndex: IntArray = IntArray(MAX_CANDIDATES)

    /** Set when the scene's entities change; the loc lights are found again on the next frame. */
    @JvmField
    var dirty: Boolean = true

    private var contentVersion: Int = -1

    private val DEBUG: Boolean = System.getProperty("client.test.hdDebug") != null

    /** client.test.hdLocScan: prints every distinct loc of the scene once (authoring lights.json). */
    private val SCAN: Boolean = System.getProperty("client.test.hdLocScan") != null
    private val ITEM_SCAN: Boolean = System.getProperty("client.test.hdItemScan") != null
    private var itemsScanned: Boolean = false
    private val scanned: HashSet<Int> = HashSet()
    private var debugFrame: Int = 0

    /** GPU particle emitters this frame: world x, y, z, kind (1 fire, 2 lava), strength, flame width and height (0: unknown). */
    @JvmField
    val emitters: FloatArray = FloatArray(MAX_EMITTERS * EMITTER_FLOATS)

    var emitterCount: Int = 0
        private set

    /** The light nearest the player this frame, for the debug panel's editor. */
    var nearest: HdContent.LightDef? = null
        private set

    /** The lights written this frame, for the debug panel. */
    var used: Int = 0
        private set

    /**
     * The carried light (HdSettings.playerLight): the light of the lit light sources the player has in
     * the backpack or wears (a lit torch, candle, lantern, mining helmet...), as the game's own light
     * sources (oldschool.runescape.wiki/w/Light_sources): their brightness tiers add up, to at most 3,
     * and set the light's reach and strength; the brightest source sets its colour, but a lit sapphire
     * lantern makes it blue, an emerald lantern green, both teal. A lit source is an
     * item with an "Extinguish" action. Full at night, faint by day. Found every CARRIED_EVERY frames.
     */
    private val playerLight: HdContent.LightDef = HdContent.LightDef("Carried light", IntArray(0), IntArray(0), IntArray(0), IntArray(0),
        1.0f, 0.62f, 0.3f, 0.0f, 0.0f, 0.0f, 250.0f, 0.0f, 0)

    private val CARRIED_EVERY: Int = 15
    private var carriedFrame: Int = 0

    /** The carried light's brightness tier now (0 none, up to 3), the brightest source's colour (sRGB). */
    private var carriedTier: Int = 0
    private var carriedRed: Float = 0.0f
    private var carriedGreen: Float = 0.0f
    private var carriedBlue: Float = 0.0f
    private var carriedFlame: Boolean = false

    /** Each tier's reach in tiles and strength. */
    private val TIER_RADIUS: FloatArray = floatArrayOf(0.0f, 4.5f, 6.0f, 7.5f)
    private val TIER_INTENSITY: FloatArray = floatArrayOf(0.0f, 1.5f, 1.9f, 2.3f)

    /** The backpack's and the worn items' containers. */
    private val CONTAINER_BACKPACK: Int = 93
    private val CONTAINER_WORN: Int = 94

    /** Finds the player's lit light sources: carriedTier and the colour of the brightest. */
    private fun findCarried() {
        carriedTier = 0
        var best: Int = 0
        var sapphire: Boolean = false
        var emerald: Boolean = false
        for (c in 0 until 2) {
            val container: ItemContainer = ItemContainer.getContainer(if (c == 0) CONTAINER_BACKPACK else CONTAINER_WORN, false) ?: continue
            val ids: IntArray = container.itemIds ?: continue
            for (slot in ids.indices) {
                val id: Int = ids[slot]
                if (id < 0) continue
                val item: ItemType = TypeLists.OBJ!!.list(id)
                val name: String = item.name ?: continue
                var lit: Boolean = false
                val actions: Array<String?>? = item.inventoryActions
                if (actions != null) {
                    for (action in actions) {
                        if (action == "Extinguish") lit = true
                    }
                }
                // a firemaking cape gives light while worn; so does the fire cape's lava
                val fireCape: Boolean = c == 1 && name == "Fire cape"
                if (!lit && !fireCape && !(c == 1 && name.startsWith("Firemaking cape"))) continue
                val tier: Int = lightTier(name)
                carriedTier += tier
                if (name == "Sapphire lantern") sapphire = true
                if (name == "Emerald lantern") emerald = true
                if (tier > best) {
                    best = tier
                    carriedFlame = fireCape || name == "Lit torch" || name == "Torch" || name.contains("candle") || name == "Oil lamp"
                    // flames warm orange, the fire cape's lava deeper, lanterns a steadier yellow (the gem
                    // lenses below)
                    if (fireCape) {
                        carriedRed = 1.0f; carriedGreen = 0.5f; carriedBlue = 0.15f
                    } else if (carriedFlame) {
                        carriedRed = 1.0f; carriedGreen = 0.62f; carriedBlue = 0.3f
                    } else {
                        carriedRed = 1.0f; carriedGreen = 0.8f; carriedBlue = 0.5f
                    }
                }
            }
        }
        if (carriedTier > 3) carriedTier = 3
        // a lit gem lantern colours the light, whatever else is lit: sapphire blue, emerald green, both teal
        if (sapphire && emerald) {
            carriedRed = 0.25f; carriedGreen = 0.95f; carriedBlue = 0.9f
        } else if (sapphire) {
            carriedRed = 0.3f; carriedGreen = 0.55f; carriedBlue = 1.0f
        } else if (emerald) {
            carriedRed = 0.3f; carriedGreen = 1.0f; carriedBlue = 0.4f
        }
        if (sapphire || emerald) carriedFlame = false
    }

    /** A lit light source's brightness tier by its name (the wiki's table); 1 for one not listed. */
    private fun lightTier(name: String): Int = when (name) {
        "Bullseye lantern", "Emerald lantern" -> 3
        "Oil lamp", "Oil lantern", "Sapphire lantern", "Mining helmet", "Fire cape" -> 2
        else -> if (name.startsWith("Firemaking cape")) 3 else 1
    }

    /**
     * Finds the lights for this frame and writes the nearest into [uniforms]; [view] is world to eye,
     * [night] 0 by day to 1 at night (the carried light's strength).
     */
    fun prepare(content: HdContent, uniforms: HdFrameUniforms, view: FloatArray, seconds: Float, night: Float) {
        used = 0
        emitterCount = 0
        var carried: Boolean = false
        if (HdSettings.playerLight) {
            if (carriedFrame++ % CARRIED_EVERY == 0) findCarried()
            carried = carriedTier > 0
        }
        if (!HdSettings.lights && !HdSettings.particles && !carried) {
            uniforms.setLightCount(0)
            nearest = null
            return
        }
        if (dirty || contentVersion != content.version) {
            findLocLights(content)
            dirty = false
            contentVersion = content.version
        }
        if (ITEM_SCAN && !itemsScanned) {
            // client.test.hdItemScan: prints the items that could be light sources (authoring the carried light)
            itemsScanned = true
            val list = TypeLists.OBJ!!
            for (id in 0 until list.num) {
                val item = list.list(id)
                val name: String = item.name ?: continue
                val lower: String = name.lowercase()
                if (lower.contains("torch") || lower.contains("candle") || lower.contains("lantern") || lower.contains("lamp") || lower.contains("mining helmet") || lower.contains("firelighter")) {
                    println("HDITEM " + id + " " + name + " noted=" + (item.certTemplateId != -1) + " actions=" + item.inventoryActions?.joinToString("|"))
                }
            }
        }
        val player = PlayerUpdate.localPlayer
        if (player == null) {
            uniforms.setLightCount(0)
            return
        }
        val px: Int = player.x
        val pz: Int = player.z
        val level: Int = player.level.toInt()
        count = 0
        if (carried) {
            // first, at the player: always among the lights used; full at night, faint by day
            playerLight.radius = TIER_RADIUS[carriedTier]
            playerLight.intensity = TIER_INTENSITY[carriedTier] * maxOf(night, 0.15f)
            playerLight.red = carriedRed
            playerLight.green = carriedGreen
            playerLight.blue = carriedBlue
            playerLight.flicker = if (carriedFlame) 1 else 0
            add(px, player.y, pz, playerLight, px, pz, 0.0f, -1)
            distance[0] = -1.0f
        }
        for (i in 0 until locCount) {
            if (!HdSettings.lights && !HdSettings.particles) break
            if (locLevel[i] != level) continue
            if (add(locX[i], locY[i], locZ[i], locDef[i]!!, px, pz, (i * 7919 % 1000).toFloat(), i) && locFlameFound[i]) {
                // the light and its embers at the loc's own flame, the embers sized to it
                val at: Int = count - 1
                emitX[at] = locFlameX[i].toFloat()
                emitY[at] = locFlameY[i].toFloat()
                emitZ[at] = locFlameZ[i].toFloat()
                emitWidth[at] = locFlameWidth[i].toFloat()
                emitHeight[at] = locFlameHeight[i].toFloat()
                x[at] = emitX[at]
                y[at] = emitY[at]
                z[at] = emitZ[at]
            }
        }
        if (HdSettings.lights && content.lightsByNpc.isNotEmpty()) {
            val npcs: Int = NPCList.NPC_UPDATE_INDEX
            val ids: IntArray = NPCList.NPC_UPDATE_INDICES
            for (i in 0 until npcs) {
                val node: NPCEntityNode = NPCList.NPC_MAP.get(ids[i].toLong()) as NPCEntityNode? ?: continue
                val npc = node.npc ?: continue
                val type = npc.npcType ?: continue
                val light: HdContent.LightDef = content.lightsByNpc[type.id] ?: continue
                add(npc.x, npc.y, npc.z, light, px, pz, ids[i].toFloat(), -1)
            }
        }
        if (HdSettings.lights && content.lightsByProjectile.isNotEmpty()) {
            val sentinel: Node = ProjectileAnimation.aClass341_24.sentinel
            var node: Node? = sentinel.next
            while (node != null && node !== sentinel) {
                val projectile: ProjectileAnimation? = (node as? ProjectileAnimationNode)?.projectileAnimation
                if (projectile != null) {
                    val light: HdContent.LightDef? = content.lightsByProjectile[projectile.hdId()]
                    if (light != null) add(projectile.x, projectile.y, projectile.z, light, px, pz, 0.0f, -1)
                }
                node = node.next
            }
        }
        if (HdSettings.lights && content.lightsBySpotAnimation.isNotEmpty()) {
            val sentinel: Node = Renderers.activeToolkit.sentinel
            var node: Node? = sentinel.next
            while (node != null && node !== sentinel) {
                val gfx = (node as? PositionedGfx)?.objectGfx
                if (gfx != null && !gfx.finished) {
                    val light: HdContent.LightDef? = content.lightsBySpotAnimation[gfx.hdId()]
                    if (light != null) add(gfx.x, gfx.y, gfx.z, light, px, pz, 0.0f, -1)
                }
                node = node.next
            }
        }
        // the GPU particles' emitters (HdGpuParticles): fires and lava among the loc lights here
        if (HdSettings.particles) {
            for (i in 0 until count) {
                val light: HdContent.LightDef = def[i]!!
                if (light.flicker == 0 || light === playerLight || emitterCount >= MAX_EMITTERS || distance[i] > EMITTER_RANGE.toFloat() * EMITTER_RANGE) continue
                val at: Int = emitterCount * EMITTER_FLOATS
                emitters[at] = emitX[i]
                emitters[at + 1] = emitY[i]
                emitters[at + 2] = emitZ[i]
                emitters[at + 3] = if (light.flicker == 2) 2.0f else 1.0f
                emitters[at + 4] = minOf(1.0f, light.intensity * light.radius / 4.0f)
                emitters[at + 5] = emitWidth[i]
                emitters[at + 6] = emitHeight[i]
                emitterCount++
            }
        }
        if (!HdSettings.lights && !carried) {
            uniforms.setLightCount(0)
            nearest = null
            return
        }
        // with point lights off, only the night light
        if (!HdSettings.lights) count = 1
        // the nearest first: a partial selection of up to MAX_LIGHTS
        val limit: Int = minOf(count, HdFrameUniforms.MAX_LIGHTS)
        for (i in 0 until limit) {
            var best: Int = i
            for (j in i + 1 until count) {
                if (distance[j] < distance[best]) best = j
            }
            if (best != i) swap(i, best)
            val light: HdContent.LightDef = def[i]!!
            val wx: Float = x[i]
            val wy: Float = y[i]
            val wz: Float = z[i]
            val ex: Float = view[0] * wx + view[4] * wy + view[8] * wz + view[12]
            val ey: Float = view[1] * wx + view[5] * wy + view[9] * wz + view[13]
            val ez: Float = view[2] * wx + view[6] * wy + view[10] * wz + view[14]
            var intensity: Float = light.intensity
            if (light.flicker == 1) {
                val t: Float = seconds * 9.0f + phase[i]
                intensity *= 0.86f + 0.08f * kotlin.math.sin(t) + 0.06f * kotlin.math.sin(t * 2.7f + 1.3f)
            } else if (light.flicker == 2) {
                intensity *= 0.8f + 0.2f * kotlin.math.sin(seconds * 1.6f + phase[i])
            }
            uniforms.setLight(i, ex, ey, ez, light.radius * 512.0f, light.red * intensity, light.green * intensity, light.blue * intensity)
        }
        uniforms.setLightCount(limit)
        used = limit
        nearest = if (limit > 0) def[0] else null
        if (DEBUG && (debugFrame++ % 50) == 0) {
            println("HDDEBUG lights locs=" + locCount + " candidates=" + count + " used=" + limit + (if (limit > 0) " first=" + def[0]!!.name + " at " + x[0] + "," + y[0] + "," + z[0] + " player " + px + "," + player.y + "," + pz + " eye " + (view[0] * x[0] + view[4] * y[0] + view[8] * z[0] + view[12]) + "," + (view[1] * x[0] + view[5] * y[0] + view[9] * z[0] + view[13]) + "," + (view[2] * x[0] + view[6] * y[0] + view[10] * z[0] + view[14]) else ""))
        }
    }

    /** The lights written this frame (used of them), for HdLightGrid: which loc light each is (-1 none) and its world x, z. */
    fun usedLocIndex(i: Int): Int = locIndex[i]
    fun usedX(i: Int): Float = x[i]
    fun usedZ(i: Int): Float = z[i]
    fun usedRadius(i: Int): Float = def[i]?.radius ?: 0.0f

    /** The loc lights found in the scene (HdLightGrid seeds its flood from them): how many, and each one's world position, level and definition. */
    fun locLightCount(): Int = locCount
    fun locLightX(i: Int): Int = locX[i]
    fun locLightZ(i: Int): Int = locZ[i]
    fun locLightLevel(i: Int): Int = locLevel[i]
    fun locLightDef(i: Int): HdContent.LightDef? = locDef[i]

    /** Adds a light candidate; false when it is out of range (or there is no room). */
    private fun add(wx: Int, wy: Int, wz: Int, light: HdContent.LightDef, px: Int, pz: Int, phase: Float, loc: Int): Boolean {
        if (count >= MAX_CANDIDATES) return false
        this.locIndex[count] = loc
        val dx: Int = wx - px
        val dz: Int = wz - pz
        if (dx > RANGE || dx < -RANGE || dz > RANGE || dz < -RANGE) return false
        this.x[count] = wx + light.offsetX
        // the game's y grows downwards: the offset's height lifts the light
        this.y[count] = wy - light.offsetY
        this.z[count] = wz + light.offsetZ
        this.distance[count] = (dx.toFloat() * dx + dz.toFloat() * dz)
        this.def[count] = light
        this.phase[count] = phase
        this.emitX[count] = this.x[count]
        this.emitY[count] = this.y[count]
        this.emitZ[count] = this.z[count]
        this.emitWidth[count] = 0.0f
        this.emitHeight[count] = 0.0f
        count++
        return true
    }

    private fun swap(a: Int, b: Int) {
        var f: Float = x[a]; x[a] = x[b]; x[b] = f
        f = y[a]; y[a] = y[b]; y[b] = f
        f = z[a]; z[a] = z[b]; z[b] = f
        f = distance[a]; distance[a] = distance[b]; distance[b] = f
        f = phase[a]; phase[a] = phase[b]; phase[b] = f
        val l: Int = locIndex[a]; locIndex[a] = locIndex[b]; locIndex[b] = l
        val d: HdContent.LightDef? = def[a]; def[a] = def[b]; def[b] = d
    }

    /** Walks the scene's entity lists for locs with a light. */
    private fun findLocLights(content: HdContent) {
        locCount = 0
        if (content.lightsByObject.isEmpty()) return
        // static and animated (Array94) locs, opaque and translucent, in the scene's entity lists
        findIn(Scene.stringStack, content)
        findIn(Scene.aClass3_Sub1Array129, content)
        findIn(Scene.aClass3_Sub1Array94, content)
        findIn(Scene.aClass3_Sub1Array143, content)
        findIn(Scene.aClass3_Sub1Array145, content)
    }

    private fun findIn(heads: Array<Entity?>?, content: HdContent) {
        var entity: Entity? = heads?.get(0)
        val player = PlayerUpdate.localPlayer
        while (entity != null && locCount < MAX_CANDIDATES) {
            if (DEBUG && entity is GameObject && player != null && Math.abs(entity.x - player.x) < 8 * 512 && Math.abs(entity.z - player.z) < 8 * 512) {
                val id: Int = (entity as GameObject).getId()
                val name: String? = try { com.jagex.game.runetek5.config.TypeLists.LOC!!.list(id).name } catch (ex: Exception) { "?" }
                println("HDDEBUG near loc " + id + " " + name + " " + entity.javaClass.simpleName + " at " + (entity.x shr 9) + "," + (entity.z shr 9))
            }
            if (SCAN && entity is GameObject && scanned.add((entity as GameObject).getId())) {
                // client.test.hdLocScan (authoring): every loc in the scene once, to find light sources
                val id: Int = (entity as GameObject).getId()
                val name: String? = try { com.jagex.game.runetek5.config.TypeLists.LOC!!.list(id).name } catch (ex: Exception) { "?" }
                println("HDDEBUG scan loc " + id + " \"" + name + "\" at " + (entity.x shr 9) + "," + (entity.z shr 9) + (if (content.lightsByObject[id] != null) " light " + content.lightsByObject[id]!!.name else ""))
            }
            if (entity is GameObject) {
                val light: HdContent.LightDef? = content.lightsByObject[(entity as GameObject).getId()]
                if (light != null) {
                    locX[locCount] = entity.x
                    locY[locCount] = entity.y
                    locZ[locCount] = entity.z
                    locLevel[locCount] = entity.level.toInt()
                    locDef[locCount] = light
                    locFlameFound[locCount] = false
                    if (light.flicker != 0) flame(entity, locCount)
                    locCount++
                }
            }
            entity = entity.nextEntity
        }
    }

    /**
     * Finds loc [index]'s flame for its particles: the loc's model built once per loc id, shape and
     * rotation (as the game builds it, without the ground's contour) and its flame point
     * (HdModel.hdFlamePoint), placed where the loc stands (with a wall decoration's offset).
     */
    private fun flame(entity: Entity, index: Int) {
        val loc: GameObject = entity as GameObject
        val id: Int = loc.getId()
        val shape: Int = loc.getShape()
        val rotation: Int = loc.getRotation()
        val key: Int = (id and 0xFFFF) or ((shape and 0x3F) shl 16) or ((rotation and 0x7) shl 22)
        var point: IntArray? = flameCache[key]
        if (point == null) {
            point = IntArray(6)
            try {
                val type = com.jagex.game.runetek5.config.TypeLists.LOC!!.list(id)
                val toolkit = com.jagex.game.runetek5.client.ClientState.activeToolkit
                val model = type?.getModelAndShadow(toolkit, null, shape, FLAME_MASK, 0, false, null, 0, 0, rotation)?.model
                if (model is com.jagex.game.runetek5.graphics.hd.HdModel) {
                    // candles, torches and braziers burn at the top of their model
                    val name: String = locDef[index]?.name ?: ""
                    point[3] = model.hdFlamePoint(point, name == "Candles" || name == "Torches" || name == "Braziers")
                }
            } catch (ex: Exception) {
                if (DEBUG) println("HDDEBUG flame failed " + id + ": " + ex)
                point[3] = 0
            }
            flameCache[key] = point
            if (DEBUG) println("HDDEBUG flame loc " + id + " shape " + shape + " rotation " + rotation + " way " + point[3] + " at " + point[0] + "," + point[1] + "," + point[2] + " size " + point[4] + "x" + point[5])
        }
        if (point[3] == 0) return
        var ox: Int = 0
        var oz: Int = 0
        if (entity is com.jagex.game.runetek5.entity.WallDecor) {
            ox = entity.offsetX.toInt()
            oz = entity.offsetZ.toInt()
        }
        locFlameX[index] = entity.x + ox + point[0]
        locFlameY[index] = entity.y + point[1]
        locFlameZ[index] = entity.z + oz + point[2]
        locFlameWidth[index] = point[4]
        locFlameHeight[index] = point[5]
        val category: String? = locDef[index]?.name
        if (category == "Ovens") {
            // a closed stove: its fire faces span its front, but only a few small sparks get out
            locFlameWidth[index] = minOf(point[4], 40)
            locFlameHeight[index] = minOf(point[5], 50)
        } else if (category == "Fireplaces") {
            // under a mantel: the embers go up the chimney, so they die within the opening
            locFlameHeight[index] = minOf(point[5], 45)
        }
        locFlameFound[index] = true
    }
}

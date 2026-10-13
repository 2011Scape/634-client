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
    }

    // the loc lights, found when the scene changes: position, the definition, a phase for flicker
    private var locCount: Int = 0
    private val locX: IntArray = IntArray(MAX_CANDIDATES)
    private val locY: IntArray = IntArray(MAX_CANDIDATES)
    private val locZ: IntArray = IntArray(MAX_CANDIDATES)
    private val locLevel: IntArray = IntArray(MAX_CANDIDATES)
    private val locDef: Array<HdContent.LightDef?> = arrayOfNulls(MAX_CANDIDATES)

    // this frame's candidates
    private var count: Int = 0
    private val x: FloatArray = FloatArray(MAX_CANDIDATES)
    private val y: FloatArray = FloatArray(MAX_CANDIDATES)
    private val z: FloatArray = FloatArray(MAX_CANDIDATES)
    private val distance: FloatArray = FloatArray(MAX_CANDIDATES)
    private val def: Array<HdContent.LightDef?> = arrayOfNulls(MAX_CANDIDATES)
    private val phase: FloatArray = FloatArray(MAX_CANDIDATES)

    /** Set when the scene's entities change; the loc lights are found again on the next frame. */
    @JvmField
    var dirty: Boolean = true

    private var contentVersion: Int = -1

    private val DEBUG: Boolean = System.getProperty("client.test.hdDebug") != null
    private var debugFrame: Int = 0

    /** The light nearest the player this frame, for the debug panel's editor. */
    var nearest: HdContent.LightDef? = null
        private set

    /** The lights written this frame, for the debug panel. */
    var used: Int = 0
        private set

    /** Finds the lights for this frame and writes the nearest into [uniforms]; [view] is world to eye. */
    fun prepare(content: HdContent, uniforms: HdFrameUniforms, view: FloatArray, seconds: Float) {
        used = 0
        if (!HdSettings.lights) {
            uniforms.setLightCount(0)
            nearest = null
            return
        }
        if (dirty || contentVersion != content.version) {
            findLocLights(content)
            dirty = false
            contentVersion = content.version
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
        for (i in 0 until locCount) {
            if (locLevel[i] != level) continue
            add(locX[i], locY[i], locZ[i], locDef[i]!!, px, pz, (i * 7919 % 1000).toFloat())
        }
        if (content.lightsByNpc.isNotEmpty()) {
            val npcs: Int = NPCList.NPC_UPDATE_INDEX
            val ids: IntArray = NPCList.NPC_UPDATE_INDICES
            for (i in 0 until npcs) {
                val node: NPCEntityNode = NPCList.NPC_MAP.get(ids[i].toLong()) as NPCEntityNode? ?: continue
                val npc = node.npc ?: continue
                val type = npc.npcType ?: continue
                val light: HdContent.LightDef = content.lightsByNpc[type.id] ?: continue
                add(npc.x, npc.y, npc.z, light, px, pz, ids[i].toFloat())
            }
        }
        if (content.lightsByProjectile.isNotEmpty()) {
            val sentinel: Node = ProjectileAnimation.aClass341_24.sentinel
            var node: Node? = sentinel.next
            while (node != null && node !== sentinel) {
                val projectile: ProjectileAnimation? = (node as? ProjectileAnimationNode)?.projectileAnimation
                if (projectile != null) {
                    val light: HdContent.LightDef? = content.lightsByProjectile[projectile.hdId()]
                    if (light != null) add(projectile.x, projectile.y, projectile.z, light, px, pz, 0.0f)
                }
                node = node.next
            }
        }
        if (content.lightsBySpotAnimation.isNotEmpty()) {
            val sentinel: Node = Renderers.activeToolkit.sentinel
            var node: Node? = sentinel.next
            while (node != null && node !== sentinel) {
                val gfx = (node as? PositionedGfx)?.objectGfx
                if (gfx != null && !gfx.finished) {
                    val light: HdContent.LightDef? = content.lightsBySpotAnimation[gfx.hdId()]
                    if (light != null) add(gfx.x, gfx.y, gfx.z, light, px, pz, 0.0f)
                }
                node = node.next
            }
        }
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

    private fun add(wx: Int, wy: Int, wz: Int, light: HdContent.LightDef, px: Int, pz: Int, phase: Float) {
        if (count >= MAX_CANDIDATES) return
        val dx: Int = wx - px
        val dz: Int = wz - pz
        if (dx > RANGE || dx < -RANGE || dz > RANGE || dz < -RANGE) return
        this.x[count] = wx + light.offsetX
        // the game's y grows downwards: the offset's height lifts the light
        this.y[count] = wy - light.offsetY
        this.z[count] = wz + light.offsetZ
        this.distance[count] = (dx.toFloat() * dx + dz.toFloat() * dz)
        this.def[count] = light
        this.phase[count] = phase
        count++
    }

    private fun swap(a: Int, b: Int) {
        var f: Float = x[a]; x[a] = x[b]; x[b] = f
        f = y[a]; y[a] = y[b]; y[b] = f
        f = z[a]; z[a] = z[b]; z[b] = f
        f = distance[a]; distance[a] = distance[b]; distance[b] = f
        f = phase[a]; phase[a] = phase[b]; phase[b] = f
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
            if (entity is GameObject) {
                val light: HdContent.LightDef? = content.lightsByObject[(entity as GameObject).getId()]
                if (light != null) {
                    locX[locCount] = entity.x
                    locY[locCount] = entity.y
                    locZ[locCount] = entity.z
                    locLevel[locCount] = entity.level.toInt()
                    locDef[locCount] = light
                    locCount++
                }
            }
            entity = entity.nextEntity
        }
    }
}

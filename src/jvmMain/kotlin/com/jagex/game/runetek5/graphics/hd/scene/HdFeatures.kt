/*
 * Not part of the original client: 634HD (toolkit 6). The per-frame side of the rendering features
 * (HdSettings): what they need is worked out once the camera is set (HdToolkit.setCamera) and written
 * into the frame's uniform block (HdFrameUniforms) before the scene is drawn.
 *
 * The environment (environments.json, HdContent) is the one whose area holds the local player; when
 * that changes its values are blended over TRANSITION_SECONDS (three seconds, smoothstepped),
 * at once when the player jumped SKIP_TRANSITION_TILES or more (a teleport). Where it gives nothing,
 * the game's own atmosphere (fog colour and distance, sun) is used, so dungeons keep their dark fog.
 */
package com.jagex.game.runetek5.graphics.hd.scene

import com.jagex.game.runetek5.graphics.hd.HdToolkit
import com.jagex.game.runetek5.graphics.hd.gl.GlState
import com.jagex.game.runetek5.graphics.hd.gl.HdGl
import com.jagex.game.runetek5.graphics.hd.gl.Mat4
import com.jagex.game.runetek5.MapRegion
import com.jagex.game.runetek5.client.features.ClientFeatures
import com.jagex.game.runetek5.client.features.HdSettings
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.graphics.hd.content.HdContent

class HdFeatures {

    private companion object {
        /** The time of day's shadow sun moves in steps of this many degrees. */
        const val SHADOW_STEP: Float = 0.5f

        /** The shadow sun's lowest pitch in degrees with the time of day. */
        const val SHADOW_MIN_PITCH: Float = 12.0f

        /** A change of environment blends over this long. */
        const val TRANSITION_SECONDS: Float = 3.0f

        /** A move of this many tiles in a frame (a teleport) switches the environment at once. */
        const val SKIP_TRANSITION_TILES: Int = 41

        /** The distance fog's depth where neither the environment nor the default gives one (the open overworld's). */
        const val DEFAULT_FOG_DEPTH: Float = 6.5f
    }

    private val start: Long = System.nanoTime()

    /** Eye space to world space, this frame. */
    @JvmField
    val viewToWorld: FloatArray = FloatArray(16)

    /** World space to eye space (the camera), this frame. */
    @JvmField
    val worldToView: FloatArray = FloatArray(16)

    /** The environment that applies now (null: none), its name for the debug panel. */
    var environment: HdContent.EnvironmentDef? = null
        private set

    // the values uploaded this frame: sky top, sky horizon (rgb each); fog colour (rgb), depth, end
    // (eye units), ground fog start, end (world y), opacity; the sun's and ambient light's tints (rgb
    // each, times intensity); the water tint (rgb, strength); the area's waves' height, speed, glint and
    // size; the shadow sun's pitch and yaw (degrees; NaN: the game's)
    private val sky: FloatArray = FloatArray(6)
    private val fog: FloatArray = FloatArray(8)
    private val envLight: FloatArray = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f)
    private val water: FloatArray = floatArrayOf(0.11f, 0.24f, 0.3f, 0.2f)
    private val waterParams: FloatArray = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f)
    private var sunPitch: Float = Float.NaN
    private var sunYaw: Float = Float.NaN
    // the live values, eased towards the current environment's target every frame (so the time of
    // day and the game's own fog colour move smoothly)...
    private val liveSky: FloatArray = FloatArray(6)
    private val liveFog: FloatArray = FloatArray(8)
    private val liveEnvLight: FloatArray = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f)
    private val liveWater: FloatArray = floatArrayOf(0.11f, 0.24f, 0.3f, 0.2f)
    private val liveWaterParams: FloatArray = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f)
    private var liveSunPitch: Float = Float.NaN
    private var liveSunYaw: Float = Float.NaN
    // ...and what was uploaded when the environment last changed: the uploaded values blend from these
    // to the live ones over TRANSITION_SECONDS
    private val fromSky: FloatArray = FloatArray(6)
    private val fromFog: FloatArray = FloatArray(8)
    private val fromEnvLight: FloatArray = FloatArray(6)
    private val fromWater: FloatArray = FloatArray(4)
    private val fromWaterParams: FloatArray = FloatArray(4)
    private var fromSunPitch: Float = Float.NaN
    private var fromSunYaw: Float = Float.NaN
    private var transitionStart: Long = 0L
    private var lastEnvironment: HdContent.EnvironmentDef? = null
    private var lastTileX: Int = Int.MIN_VALUE
    private var lastTileY: Int = Int.MIN_VALUE
    // the environment at the player's tile, looked up again only when the tile or the content changes
    private var cachedEnvironment: HdContent.EnvironmentDef? = null
    private var cachedTileX: Int = Int.MIN_VALUE
    private var cachedTileY: Int = Int.MIN_VALUE
    private var cachedLevel: Int = -1
    private var cachedVersion: Int = -1
    private var eased: Boolean = false
    private var lastNanos: Long = 0L
    // the water clock: seconds of water time, advanced every frame at the area's water speed (a speed
    // that scaled the frame's seconds moved the ripples' phase through many repeats when it blended at an
    // environment's edge); wraps hourly, as the frame's seconds do
    private var waterClock: Float = 0.0f
    private var waterClockNanos: Long = 0L
    private val target: FloatArray = FloatArray(14)

    /** The sun's shadow map (HdSettings.shadows). */
    val shadows: HdShadows = HdShadows()

    /** The point lights from lights.json (HdSettings.lights). */
    val lights: HdLights = HdLights()

    /** The water's planar reflection (HdSettings.reflections), drawn in HdToolkit.opaqueEntitiesDone. */
    val reflections: HdReflections = HdReflections()

    /** The room fill and the lights' wall visibility (HdSettings.lightGrid). */
    val lightGrid: HdLightGrid = HdLightGrid()

    private var viewX: Int = 0
    private var viewY: Int = 0
    private var viewWidth: Int = 1
    private var viewHeight: Int = 1

    /** The 3D view's place, from beginScene. */
    fun beginFrame(toolkit: HdToolkit, x: Int, y: Int, width: Int, height: Int) {
        viewX = x
        viewY = y
        viewWidth = maxOf(1, width)
        viewHeight = maxOf(1, height)
        // no reflection until this frame's is drawn
        HdGl.state.hdReflection[2] = 0.0f
    }

    /** Once the camera is set: everything the frame's uniform block holds, then uploaded. */
    fun prepare(toolkit: HdToolkit, uniforms: HdFrameUniforms) {
        val state: GlState = HdGl.state
        val view: FloatArray = state.matrixStack.modelview()
        System.arraycopy(view, 0, worldToView, 0, 16)
        val inverse: FloatArray = Mat4.invert(view) ?: Mat4.identity()
        System.arraycopy(inverse, 0, viewToWorld, 0, 16)
        uniforms.setViewToWorld(viewToWorld)
        uniforms.setViewport(viewX.toFloat(), viewY.toFloat(), viewWidth.toFloat(), viewHeight.toFloat())
        val seconds: Float = ((System.nanoTime() - start) / 1000000L % 3600000L).toFloat() / 1000.0f
        uniforms.setTime(seconds, 1.0f, 0.0f, 0.0f)
        // the sun as the fixed-function pipeline has it: light 0's eye-space direction and diffuse
        val sun: FloatArray = state.lightPosition[0]
        val length: Float = kotlin.math.sqrt(sun[0] * sun[0] + sun[1] * sun[1] + sun[2] * sun[2])
        if (length > 0.0f) {
            uniforms.setSun(sun[0] / length, sun[1] / length, sun[2] / length, state.lightEnabled[0])
        } else {
            uniforms.setSun(0.0f, 1.0f, 0.0f, false)
        }
        val diffuse: FloatArray = state.lightDiffuse[0]
        uniforms.setSunColour(diffuse[0], diffuse[1], diffuse[2])
        uniforms.setAmbient(state.globalAmbient[0], state.globalAmbient[1], state.globalAmbient[2])
        this.environment(toolkit.hdContent, state)
        uniforms.setWater(water[0], water[1], water[2], water[3])
        uniforms.setWaterParams(waterParams[0], waterParams[1], waterParams[2], waterParams[3])
        val nanos: Long = System.nanoTime()
        // a stall (a map build, a paused window) advances the clock a quarter second at most
        val dt: Float = if (waterClockNanos == 0L) 0.0f else minOf(0.25f, (nanos - waterClockNanos) / 1.0e9f)
        waterClockNanos = nanos
        waterClock = (waterClock + dt * waterParams[1]) % 3600.0f
        uniforms.setWaterClock(waterClock, fromWaterParams[3], transitionProgress)
        uniforms.setActors(actors, if (HdSettings.grassDisplacement) findActors() else 0)
        // day and night: the sun or moon replaces the game's light; its colour and the ambient tint the
        // environment's, and the shadow sun follows it
        val time: Boolean = HdSettings.dayNight != 0 && dayNight(worldToView)
        val area: Boolean = HdSettings.environmentLighting
        uniforms.setEnvironmentLight(
            (if (area) envLight[0] else 1.0f) * (if (time) dayLight[0] else 1.0f), (if (area) envLight[1] else 1.0f) * (if (time) dayLight[1] else 1.0f), (if (area) envLight[2] else 1.0f) * (if (time) dayLight[2] else 1.0f),
            (if (area) envLight[3] else 1.0f) * (if (time) dayLight[3] else 1.0f), (if (area) envLight[4] else 1.0f) * (if (time) dayLight[4] else 1.0f), (if (area) envLight[5] else 1.0f) * (if (time) dayLight[5] else 1.0f))
        uniforms.setDayNight(lightEye[0], lightEye[1], lightEye[2], time, moonWorld[0], moonWorld[1], moonWorld[2], if (time) nightness else 0.0f)
        uniforms.setTonemap(HdPost.exposure())
        uniforms.setSceneSize(
            (com.jagex.game.runetek5.MapRegion.sceneSizeX * 512).toFloat(), (com.jagex.game.runetek5.MapRegion.sceneSizeY * 512).toFloat(),
            (com.jagex.game.runetek5.MapRegion.sceneBaseX * 512).toFloat(), (com.jagex.game.runetek5.MapRegion.sceneBaseY * 512).toFloat(),
        )
        if (time) {
            uniforms.setSun(lightEye[0], lightEye[1], lightEye[2], true)
        }
        // the sun's shadow map: where it looks from, and the matrix the receivers use
        HdGl.shadows = this.shadows
        // with the time of day the shadow sun moves in steps of SHADOW_STEP degrees: moved a little every
        // frame, the map's texels fell differently each frame and shadow edges and shadowed ground
        // flickered (and the static casters were drawn again every frame). Kept SHADOW_MIN_PITCH up, as
        // near the horizon the shadows stretch over many texels each (and the sun is faint there).
        this.shadows.sunPitch = if (time) shadowStep(maxOf(lightPitch, SHADOW_MIN_PITCH)) else sunPitch
        this.shadows.sunYaw = if (time) shadowStep(lightYaw) % 360.0f else sunYaw
        this.shadows.beginFrame(viewToWorld, state.lightPosition[0], state.lightEnabled[0] && length > 0.0f)
        if (this.shadows.active) {
            uniforms.setShadowMatrix(this.shadows.shadowMatrix)
        }
        if (this.shadows.aoActive) {
            uniforms.setAmbientOcclusion(this.shadows.aoMatrix, this.shadows.aoParams)
            this.shadows.bake(toolkit.sceneRenderer)
            this.shadows.bindBaked()
            uniforms.setAmbientOcclusionBake(this.shadows.aoBakeMatrix, this.shadows.aoBakeParams)
        }
        // the fog's depth scaled to the draw distance in tiles, (depth * min(dd, 90) / 10)
        val drawTiles: Float = fog[4] / 512.0f
        uniforms.setFog(fog[0], fog[1], fog[2], fog[3] * minOf(drawTiles, 90.0f) / 10.0f, fog[4], fog[3] / 100.0f)
        uniforms.setGroundFog(fog[5], fog[6], fog[7])
        uniforms.setSky(sky[0], sky[1], sky[2], sky[3], sky[4], sky[5])
        // locs fade over the last three tiles before the fog closes (HD_FADE)
        val fadeEnd: Float = if (HdSettings.fog) fog[4] else state.fogEnd
        // the fade's band: 3 tiles before the fog's end, or a quarter of the way when the view reaches
        // further than the game's (client.renderDistance): the far sea and shore fade in over a long way
        // instead of standing pale and sharp before the fog
        val band: Float = if (ClientFeatures.RENDER_DISTANCE > 1.0) maxOf(1536.0f, fadeEnd * 0.25f) else 1536.0f
        uniforms.setTime(seconds, 1.0f, fadeEnd - band, band)
        if (System.getProperty("client.test.hdDebug") != null && (debugFrame++ % 120) == 0) {
            println(
                "HDDEBUG fog game " + state.fogStart + ".." + state.fogEnd + " depth " + fog[3] + " (scaled " + (fog[3] * minOf(drawTiles, 90.0f) / 10.0f) + ", sky " + (fog[3] / 100.0f) + ") end " + fog[4] +
                    " ground " + fog[5] + ".." + fog[6] + " op " + fog[7] + " t " + transitionProgress + " env " + this.environment?.name + " source " + this.environment?.source,
            )
        }
        // the grading table of the environment (HdSettings.colourGrading)
        val lut: String? = this.environment?.lut
        toolkit.hdPost.useGrading(if (lut != null && HdSettings.colourGrading) toolkit.hdPost.gradingTable(lut) else 0)
        this.lights.prepare(toolkit.hdContent, uniforms, worldToView, seconds, if (HdSettings.dayNight != 0) nightness else 0.0f)
        this.lightGrid.prepare(uniforms, this.lights, toolkit.hdContent, if (HdSettings.dayNight != 0) nightness else -1.0f)
        uniforms.setDebug(toolkit.debugOverlay())
    }

    /** How far the last change of environment has blended in (0 to 1), for the debug print. */
    private var transitionProgress: Float = 1.0f

    /**
     * Picks the environment at the local player and eases the live sky, fog and light values towards
     * it; when the environment changes, the uploaded values blend from what they were to the live ones
     * over TRANSITION_SECONDS (at once after a jump of SKIP_TRANSITION_TILES, or a content reload).
     */
    private fun environment(content: HdContent, state: GlState) {
        content.poll()
        val player = PlayerUpdate.localPlayer
        var environment: HdContent.EnvironmentDef? = null
        var tileX: Int = Int.MIN_VALUE
        var tileY: Int = Int.MIN_VALUE
        if (player != null) {
            tileX = (player.x shr 9) + MapRegion.sceneBaseX
            tileY = (player.z shr 9) + MapRegion.sceneBaseY
            val level: Int = player.level.toInt()
            if (tileX != cachedTileX || tileY != cachedTileY || level != cachedLevel || content.version != cachedVersion) {
                cachedTileX = tileX
                cachedTileY = tileY
                cachedLevel = level
                cachedVersion = content.version
                cachedEnvironment = content.environmentAt(tileX, tileY, level)
            }
            environment = cachedEnvironment
        }
        this.environment = environment
        val fogColour: FloatArray = state.fogColor
        // the fog colour: the environment's, else the game's (its zone's, #c8c0a8 by default, the HD
        // 2008 sky colour, so a generated environment without a colour gets the sky's)
        val envFog: FloatArray? = environment?.fogColour
        target[6] = envFog?.get(0) ?: fogColour[0]
        target[7] = envFog?.get(1) ?: fogColour[1]
        target[8] = envFog?.get(2) ?: fogColour[2]
        // the sky: the environment's, else drawn from its fog colour (a deeper, bluer top)
        val top: FloatArray? = environment?.skyTop
        val horizon: FloatArray? = environment?.skyHorizon
        target[0] = top?.get(0) ?: target[6] * 0.62f
        target[1] = top?.get(1) ?: target[7] * 0.74f
        target[2] = top?.get(2) ?: minOf(1.0f, target[8] * 0.95f + 0.04f)
        target[3] = horizon?.get(0) ?: target[6]
        target[4] = horizon?.get(1) ?: target[7]
        target[5] = horizon?.get(2) ?: target[8]
        // the fog's depth: the environment's, else the default environment's, else the open overworld's
        var depth: Float = environment?.fogDepth ?: Float.NaN
        if (depth.isNaN()) depth = content.environments.firstOrNull { it.isDefault }?.fogDepth ?: Float.NaN
        target[9] = if (depth.isNaN()) DEFAULT_FOG_DEPTH else depth
        // the fog's end: tiles in the file, scene units (512 a tile) here; else the game's
        val end: Float = environment?.fogEnd ?: Float.NaN
        target[10] = if (end.isNaN()) state.fogEnd else end * 512.0f
        target[11] = environment?.groundFogStart ?: 0.0f
        target[12] = environment?.groundFogEnd ?: 0.0f
        target[13] = environment?.groundFogOpacity ?: 0.0f
        if (HdSettings.dayNight != 0) {
            // day and night: the sky and fog darken to a night blue, and warm near sunrise and sunset
            val night: Float = nightness
            val dusk: Float = 1.0f - smooth(0.0f, 14.0f, kotlin.math.abs(sunElevationNow))
            val nightColours: FloatArray = floatArrayOf(0.02f, 0.03f, 0.07f, 0.05f, 0.07f, 0.13f, 0.04f, 0.05f, 0.1f)
            for (i in 0 until 9) {
                target[i] += (nightColours[i] - target[i]) * night
            }
            // the horizon and fog take an orange glow at dusk
            val glow: FloatArray = floatArrayOf(0.95f, 0.55f, 0.32f)
            for (i in 0 until 3) {
                target[3 + i] += (glow[i] - target[3 + i]) * dusk * 0.45f
                target[6 + i] += (glow[i] - target[6 + i]) * dusk * 0.3f
            }
        }
        val now: Long = System.nanoTime()
        val reloaded: Boolean = !eased || content.version != easedVersion
        val step: Float = if (reloaded) 1.0f else 1.0f - kotlin.math.exp(-((now - lastNanos) / 1.0e9f) / 0.4f)
        easedVersion = content.version
        lastNanos = now
        eased = true
        for (i in 0 until 6) {
            liveSky[i] += (target[i] - liveSky[i]) * step
        }
        for (i in 0 until 8) {
            liveFog[i] += (target[6 + i] - liveFog[i]) * step
        }
        // the light's tints, the water and the sun's angles follow the same way
        val sunColour: FloatArray? = environment?.sunColour
        val sunIntensity: Float = environment?.sunIntensity ?: 1.0f
        val ambientColour: FloatArray? = environment?.ambientColour
        val ambientIntensity: Float = environment?.ambientIntensity ?: 1.0f
        for (i in 0 until 3) {
            liveEnvLight[i] += ((sunColour?.get(i) ?: 1.0f) * sunIntensity - liveEnvLight[i]) * step
            liveEnvLight[3 + i] += ((ambientColour?.get(i) ?: 1.0f) * ambientIntensity - liveEnvLight[3 + i]) * step
        }
        val tint: FloatArray? = environment?.waterTint
        val strength: Float = environment?.waterStrength ?: Float.NaN
        val waterTarget: FloatArray = if (tint != null) floatArrayOf(tint[0], tint[1], tint[2], if (strength.isNaN()) 0.45f else strength) else floatArrayOf(0.11f, 0.24f, 0.3f, if (strength.isNaN()) 0.2f else strength)
        val paramsTarget: FloatArray = floatArrayOf(environment?.waterWaves ?: 1.0f, environment?.waterSpeed ?: 1.0f, environment?.waterGlint ?: 1.0f, environment?.waterSize ?: 1.0f)
        for (i in 0 until 4) {
            liveWater[i] += (waterTarget[i] - liveWater[i]) * step
            liveWaterParams[i] += (paramsTarget[i] - liveWaterParams[i]) * step
        }
        // the ripples' size takes its target at once: the surface crossfades the ripples between two fixed
        // sizes over the transition, and an eased size would slide the pattern it fades in
        liveWaterParams[3] = paramsTarget[3]
        liveSunPitch = easeAngle(liveSunPitch, environment?.sunPitch ?: Float.NaN, step)
        liveSunYaw = easeAngle(liveSunYaw, environment?.sunYaw ?: Float.NaN, step)
        // The transition: a new environment blends in from what was shown over TRANSITION_SECONDS
        // (smoothstepped), at once after a teleport, at login or when the content was reloaded
        if (environment !== lastEnvironment) {
            val jumped: Boolean = reloaded || lastEnvironment == null || lastTileX == Int.MIN_VALUE || tileX == Int.MIN_VALUE ||
                maxOf(kotlin.math.abs(tileX - lastTileX), kotlin.math.abs(tileY - lastTileY)) >= SKIP_TRANSITION_TILES
            System.arraycopy(sky, 0, fromSky, 0, 6)
            System.arraycopy(fog, 0, fromFog, 0, 8)
            System.arraycopy(envLight, 0, fromEnvLight, 0, 6)
            System.arraycopy(water, 0, fromWater, 0, 4)
            System.arraycopy(waterParams, 0, fromWaterParams, 0, 4)
            fromSunPitch = sunPitch
            fromSunYaw = sunYaw
            transitionStart = if (jumped) now - (TRANSITION_SECONDS * 1.0e9f).toLong() else now
            lastEnvironment = environment
        }
        lastTileX = tileX
        lastTileY = tileY
        val t: Float = smooth(0.0f, 1.0f, (now - transitionStart) / (TRANSITION_SECONDS * 1.0e9f))
        transitionProgress = t
        for (i in 0 until 6) {
            sky[i] = fromSky[i] + (liveSky[i] - fromSky[i]) * t
            envLight[i] = fromEnvLight[i] + (liveEnvLight[i] - fromEnvLight[i]) * t
        }
        for (i in 0 until 8) {
            fog[i] = fromFog[i] + (liveFog[i] - fromFog[i]) * t
        }
        for (i in 0 until 4) {
            water[i] = fromWater[i] + (liveWater[i] - fromWater[i]) * t
            waterParams[i] = fromWaterParams[i] + (liveWaterParams[i] - fromWaterParams[i]) * t
        }
        // the ripples' size is not blended: the surface crossfades the ripples from the size shown when the
        // environment changed (fromWaterParams[3], uploaded with the water clock) to the area's over the
        // transition (a blended size scales the world-space pattern about the world's origin: a sweep)
        waterParams[3] = liveWaterParams[3]
        sunPitch = easeAngle(fromSunPitch, liveSunPitch, t)
        sunYaw = easeAngle(fromSunYaw, liveSunYaw, t)
    }

    // the characters grass bends away from: the local player, then the nearest NPCs (world x, z)
    private val actors: FloatArray = FloatArray(HdFrameUniforms.MAX_ACTORS * 2)

    private fun findActors(): Int {
        val player = PlayerUpdate.localPlayer ?: return 0
        var count: Int = 0
        actors[0] = player.x.toFloat()
        actors[1] = player.z.toFloat()
        count++
        val npcs: Int = com.jagex.game.runetek5.NPCList.NPC_UPDATE_INDEX
        val ids: IntArray = com.jagex.game.runetek5.NPCList.NPC_UPDATE_INDICES
        for (i in 0 until npcs) {
            if (count >= HdFrameUniforms.MAX_ACTORS) break
            val node = com.jagex.game.runetek5.NPCList.NPC_MAP.get(ids[i].toLong()) as com.jagex.game.runetek5.entity.NPCEntityNode? ?: continue
            val npc = node.npc ?: continue
            if (Math.abs(npc.x - player.x) > 10 * 512 || Math.abs(npc.z - player.z) > 10 * 512) continue
            actors[count * 2] = npc.x.toFloat()
            actors[count * 2 + 1] = npc.z.toFloat()
            count++
        }
        return count
    }

    // day and night (HdSettings.dayNight), worked out each frame: the light's tint (rgb) and the
    // ambient tint (rgb); the light's eye-space direction; the moon's world direction; how much night
    private val dayLight: FloatArray = FloatArray(6)
    private val lightEye: FloatArray = FloatArray(3)
    private val moonWorld: FloatArray = FloatArray(3)
    private var nightness: Float = 0.0f

    private var testTimeStart: Long = 0L

    /** An angle in degrees rounded to a whole SHADOW_STEP. */
    private fun shadowStep(degrees: Float): Float = Math.round(degrees / SHADOW_STEP) * SHADOW_STEP
    private var sunElevationNow: Float = 45.0f
    private var lightPitch: Float = Float.NaN
    private var lightYaw: Float = Float.NaN

    /**
     * The time of day (the clock, or a cycle of HdSettings.dayNight minutes, 0 at midnight) and from
     * it the sun (up to 70 degrees at noon, rising in the east) or, while the sun is down, the moon
     * across the sky from it; their colours (warm near the horizon, a faint blue moonlight), the
     * ambient light (down to 0.45 at night), the night for the sky and fog. True when worked out.
     */
    private fun dayNight(worldToView: FloatArray): Boolean {
        // a smoke test can fix the time (client.test.timeOfDay: 0 midnight, 0.5 noon)
        val forced: Double? = if (System.getProperty("client.test") != null) System.getProperty("client.test.timeOfDay")?.toDoubleOrNull() else null
        // client.test.timeRuns: from there the time runs on at the cycle's speed (shadow flicker checks)
        if (forced != null && testTimeStart == 0L && System.getProperty("client.test.timeRuns") != null) testTimeStart = System.currentTimeMillis()
        val run: Double = if (testTimeStart == 0L || HdSettings.dayNight <= 0) 0.0 else (System.currentTimeMillis() - testTimeStart) / (HdSettings.dayNight * 60000.0)
        val day: Double = if (forced != null) (forced + run) % 1.0 else if (HdSettings.dayNight < 0) {
            val now = java.time.LocalTime.now()
            now.toSecondOfDay() / 86400.0
        } else {
            (System.currentTimeMillis() % (HdSettings.dayNight * 60000L)).toDouble() / (HdSettings.dayNight * 60000.0)
        }
        val sunElevation: Double = 70.0 * Math.sin(2.0 * Math.PI * (day - 0.25))
        val sunYaw: Double = (90.0 + 360.0 * day) % 360.0
        // the sun by day, the moon by night (opposite it), switched while both are on the horizon
        val sunUp: Boolean = sunElevation > -2.0
        val elevation: Double = if (sunUp) Math.max(sunElevation, 4.0) else Math.max(-sunElevation, 4.0)
        val yaw: Double = if (sunUp) sunYaw else (sunYaw + 180.0) % 360.0
        val e: Double = Math.toRadians(elevation)
        val a: Double = Math.toRadians(yaw)
        // towards the light, world space (the game's y grows downwards)
        val wx: Float = (Math.cos(e) * Math.sin(a)).toFloat()
        val wy: Float = (-Math.sin(e)).toFloat()
        val wz: Float = (Math.cos(e) * Math.cos(a)).toFloat()
        lightEye[0] = worldToView[0] * wx + worldToView[4] * wy + worldToView[8] * wz
        lightEye[1] = worldToView[1] * wx + worldToView[5] * wy + worldToView[9] * wz
        lightEye[2] = worldToView[2] * wx + worldToView[6] * wy + worldToView[10] * wz
        val length: Float = kotlin.math.sqrt(lightEye[0] * lightEye[0] + lightEye[1] * lightEye[1] + lightEye[2] * lightEye[2]).coerceAtLeast(0.0001f)
        for (i in 0 until 3) lightEye[i] /= length
        lightPitch = elevation.toFloat()
        lightYaw = yaw.toFloat()
        val moonE: Double = Math.toRadians(-sunElevation)
        val moonA: Double = Math.toRadians((sunYaw + 180.0) % 360.0)
        moonWorld[0] = (Math.cos(moonE) * Math.sin(moonA)).toFloat()
        moonWorld[1] = (-Math.sin(moonE)).toFloat()
        moonWorld[2] = (Math.cos(moonE) * Math.cos(moonA)).toFloat()
        val s: Float = sunElevation.toFloat()
        sunElevationNow = s
        // the sun's colour: warm low down, white high; its strength fades out below the horizon
        val high: Float = smooth(2.0f, 30.0f, s)
        val sunStrength: Float = smooth(-4.0f, 8.0f, s)
        val moonStrength: Float = 0.32f * smooth(2.0f, -8.0f, s)
        dayLight[0] = (1.0f * high + 1.0f * (1.0f - high)) * sunStrength + 0.55f * moonStrength
        dayLight[1] = (1.0f * high + 0.62f * (1.0f - high)) * sunStrength + 0.62f * moonStrength
        dayLight[2] = (1.0f * high + 0.38f * (1.0f - high)) * sunStrength + 0.85f * moonStrength
        val ambient: Float = 0.45f + 0.55f * smooth(-10.0f, 15.0f, s)
        dayLight[3] = ambient * (0.85f + 0.15f * high)
        dayLight[4] = ambient * (0.88f + 0.12f * high)
        dayLight[5] = ambient
        nightness = smooth(4.0f, -12.0f, s)
        return true
    }

    /** 0 below [from], 1 above [to] (or the other way round when [to] is below [from]), smooth between. */
    private fun smooth(from: Float, to: Float, x: Float): Float {
        val t: Float = ((x - from) / (to - from)).coerceIn(0.0f, 1.0f)
        return t * t * (3.0f - 2.0f * t)
    }

    /** An angle eased towards [target] (degrees, the short way round); NaN on either side switches at once. */
    private fun easeAngle(current: Float, target: Float, step: Float): Float {
        if (current.isNaN() || target.isNaN()) return target
        var delta: Float = (target - current) % 360.0f
        if (delta > 180.0f) delta -= 360.0f
        if (delta < -180.0f) delta += 360.0f
        return current + delta * step
    }

    private var easedVersion: Int = -1

    private var debugFrame: Int = 0

    fun release() {
        shadows.release()
        lightGrid.release()
        if (HdGl.shadows === shadows) HdGl.shadows = null
    }
}

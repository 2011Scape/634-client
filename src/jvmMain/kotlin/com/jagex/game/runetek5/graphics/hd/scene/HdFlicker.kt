/*
 * Not part of the original client: 634HD (toolkit 6). The game's map lights flicker on the 50 Hz
 * logic tick (StaticPointLight.applyEffect): a sine, a sawtooth, a noise table that jumps every
 * 20 ms, or a hard on/off blink at 1 to 4 Hz. Fixed-function lighting clamped the lit colour at
 * 1, which hid most of it; in linear colour, through the bloom's threshold, the steps read as a
 * strobe on a lantern. Here each light's intensity is low-passed over time and given a floor, so a
 * flame breathes instead of blinking. The original curves are untouched: this sits between
 * PointLight.getIntensity and the GL light (HdToolkit.updatePointLights), which every lighting path
 * of toolkit 6 reads (ff.vert, ff.frag, and the retained scene's per-object light snapshot).
 */
package com.jagex.game.runetek5.graphics.hd.scene

import com.jagex.game.runetek5.cache.node.PointLight
import java.util.WeakHashMap

class HdFlicker {

    companion object {
        /** Seconds for the smoothed intensity to cover 63 % of a step in the game's value. */
        private const val TAU: Float = 0.12f

        /** The least a flickering light falls to, as a share of its full brightness. */
        private const val FLOOR: Float = 0.6f

        /** A gap between updates longer than this (a light out of view) restarts the filter. */
        private const val MAX_GAP_NANOS: Long = 250000000L
    }

    // per light: the smoothed intensity and the time of its last update
    private val state = WeakHashMap<PointLight, FloatArray>()
    private val lastUpdate = WeakHashMap<PointLight, LongArray>()

    /** The intensity to light with for [light] now, from its game value (0 to 1) smoothed and floored. */
    fun intensity(light: PointLight, now: Long): Float {
        val raw = light.getIntensity()
        var smoothed = state[light]
        var last = lastUpdate[light]
        if (smoothed == null || last == null || now - last[0] > MAX_GAP_NANOS) {
            smoothed = floatArrayOf(raw)
            last = longArrayOf(now)
            state[light] = smoothed
            lastUpdate[light] = last
        } else {
            val dt = (now - last[0]).toFloat() / 1.0e9f
            last[0] = now
            if (dt > 0.0f) {
                val alpha = 1.0f - kotlin.math.exp(-dt / TAU)
                smoothed[0] += (raw - smoothed[0]) * alpha
            }
        }
        return FLOOR + (1.0f - FLOOR) * smoothed[0]
    }
}

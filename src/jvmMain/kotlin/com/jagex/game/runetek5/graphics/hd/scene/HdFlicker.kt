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
        private const val TAU: Float = 0.3f

        /** The least a flickering light falls to, as a share of its full brightness. */
        private const val FLOOR: Float = 0.7f

        /** A gap between updates longer than this (a light out of view) restarts the filter. */
        private const val MAX_GAP_NANOS: Long = 250000000L

        // The shimmer of a flame on top of the game's smoothed curve, for the lights the game
        // flickers: a slow drift and a faster tremble, together at most 10 % of the brightness.
        private const val DRIFT_HZ: Float = 1.1f
        private const val DRIFT: Float = 0.06f
        private const val TREMBLE_HZ: Float = 4.3f
        private const val TREMBLE: Float = 0.04f
        private const val TWO_PI: Float = 6.2831855f
    }

    // per light: the smoothed intensity, the least and the greatest game value seen (a light whose
    // value never changes is a steady lamp and gets no shimmer), and the time of its last update
    private val state = WeakHashMap<PointLight, FloatArray>()
    private val lastUpdate = WeakHashMap<PointLight, LongArray>()

    /** The intensity to light with for [light] now, from its game value (0 to 1) smoothed and floored. */
    fun intensity(light: PointLight, now: Long): Float {
        val raw = light.getIntensity()
        var smoothed = state[light]
        var last = lastUpdate[light]
        if (smoothed == null || last == null || now - last[0] > MAX_GAP_NANOS) {
            smoothed = floatArrayOf(raw, raw, raw)
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
            if (raw < smoothed[1]) smoothed[1] = raw
            if (raw > smoothed[2]) smoothed[2] = raw
        }
        var shaped = FLOOR + (1.0f - FLOOR) * smoothed[0]
        if (smoothed[2] - smoothed[1] > 0.01f) {
            // each light trembles to its own beat: the phase from its identity
            val phase = (System.identityHashCode(light) and 0x3FF).toFloat() / 1024.0f * TWO_PI
            val seconds = (now % 3600000000000L).toFloat() / 1.0e9f
            val drift = kotlin.math.sin(TWO_PI * DRIFT_HZ * seconds + phase)
            val tremble = kotlin.math.sin(TWO_PI * TREMBLE_HZ * seconds + phase * 1.7f)
            // each sine scaled to 0..1, so the multiplier runs from 1 - DRIFT - TREMBLE up to 1
            shaped *= 1.0f - DRIFT * (1.0f - drift) * 0.5f - TREMBLE * (1.0f - tremble) * 0.5f
        }
        return shaped
    }
}

/*
 * Not part of the original client: 634HD (toolkit 6). The per-frame side of the rendering features
 * (HdSettings): what they need is worked out once the camera is set (HdToolkit.setCamera) and written
 * into the frame's uniform block (HdFrameUniforms) before the scene is drawn.
 */
package com.jagex.game.runetek5.graphics.hd.scene

import com.jagex.game.runetek5.graphics.hd.HdToolkit
import com.jagex.game.runetek5.graphics.hd.gl.GlState
import com.jagex.game.runetek5.graphics.hd.gl.HdGl
import com.jagex.game.runetek5.graphics.hd.gl.Mat4

class HdFeatures {

    private val start: Long = System.nanoTime()

    /** Eye space to world space, this frame. */
    @JvmField
    val viewToWorld: FloatArray = FloatArray(16)

    /** World space to eye space (the camera), this frame. */
    @JvmField
    val worldToView: FloatArray = FloatArray(16)

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
        uniforms.setFog(state.fogColor[0], state.fogColor[1], state.fogColor[2], state.fogStart, state.fogEnd, 0.0f, 0.0f)
        uniforms.setSky(state.fogColor[0], state.fogColor[1], state.fogColor[2], state.fogColor[0], state.fogColor[1], state.fogColor[2])
        uniforms.setLightCount(0)
    }

    fun release() {
    }
}

/*
 * Not part of the original client: 634HD (toolkit 6), copied from GlEnvironmentSampler (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd

import com.jagex.game.runetek5.EnvironmentSampler

abstract class HdEnvironmentSampler : EnvironmentSampler {

	constructor() : super()

	abstract fun getCubeTexture(): HdCubeTexture?
}

package com.jagex.game.runetek5.client.features

/**
 * Not part of the original client: a toolkit outside common code (634HD, src/jvmMain) that draws the
 * 3D view through the anti-aliasing pipeline as GlToolkit does, so ClientFeatures can reach it
 * without naming it.
 */
interface SceneTargetToolkit {

	fun beginScene(x: Int, y: Int, width: Int, height: Int)

	fun endScene()

	fun dropSceneTarget()
}

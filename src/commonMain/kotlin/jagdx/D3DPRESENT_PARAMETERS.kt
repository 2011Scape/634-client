/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/jagdx/D3DPRESENT_PARAMETERS.kt), so that common code can use it (milestone 3).
 */
package jagdx

import awt.Canvas

expect class D3DPRESENT_PARAMETERS {
	var Windowed: Boolean
	var EnableAutoDepthStencil: Boolean
	var BackBufferFormat: Int
	var MultiSampleQuality: Int
	var MultiSampleType: Int
	var AutoDepthStencilFormat: Int
	var BackBufferHeight: Int
	var PresentationInterval: Int
	var BackBufferWidth: Int
	constructor(canvas: Canvas?)
}

/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/jagdx/D3DCAPS.kt), so that common code can use it (milestone 3).
 */
package jagdx

expect class D3DCAPS {
	constructor()
	var MaxSimultaneousTextures: Int
	var DevCaps: Int
	var SrcBlendCaps: Int
	var RasterCaps: Int
	var MaxStreams: Int
	var VertexShaderVersion: Int
	var TextureCaps: Int
	var TextureOpCaps: Int
	var DestBlendCaps: Int
	var MaxActiveLights: Int
}

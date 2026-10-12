/*
 * Not part of the original client: the browser side of a native class (milestone 4). The native
 * library behind it is not available in a browser, so it throws NativeUnavailableException, which
 * the client treats like a missing library.
 */
package jagdx

import NativeUnavailableException
import nativeUnavailable

actual class D3DCAPS {
	actual constructor() {
		throw NativeUnavailableException("D3DCAPS")
	}
	actual var MaxSimultaneousTextures: Int = 0
	actual var DevCaps: Int = 0
	actual var SrcBlendCaps: Int = 0
	actual var RasterCaps: Int = 0
	actual var MaxStreams: Int = 0
	actual var VertexShaderVersion: Int = 0
	actual var TextureCaps: Int = 0
	actual var TextureOpCaps: Int = 0
	actual var DestBlendCaps: Int = 0
	actual var MaxActiveLights: Int = 0
}

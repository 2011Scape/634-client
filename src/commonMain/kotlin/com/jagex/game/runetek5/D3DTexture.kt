package com.jagex.game.runetek5

import com.jagex.game.runetek5.graphics.texture.sampler.SampleMode
import com.jagex.game.runetek5.toolkit.D3DToolkit
import jagdx.IDirect3DBaseTexture
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

@OriginalClass("client!cg")
abstract class D3DTexture {

	@OriginalMember(owner = "client!cg", name = "c", descriptor = "Lclient!ria;")
	@JvmField
	var filter: FilterMode? = SampleMode.LINEAR

	@OriginalMember(owner = "client!cg", name = "d", descriptor = "Lclient!baa;")
	@JvmField
	val type: DataType?

	@OriginalMember(owner = "client!cg", name = "b", descriptor = "Lclient!bfa;")
	@JvmField
	val toolkit: D3DToolkit?

	@OriginalMember(owner = "client!cg", name = "a", descriptor = "Z")
	@JvmField
	val mipmapped: Boolean

	@OriginalMember(owner = "client!cg", name = "e", descriptor = "Lclient!wq;")
	@JvmField
	val format: PixelFormat?

	@OriginalMember(owner = "client!cg", name = "<init>", descriptor = "(Lclient!bfa;Lclient!wq;Lclient!baa;ZI)V")
	constructor(@OriginalArg(0) d3DToolkit: D3DToolkit?, @OriginalArg(1) format: PixelFormat?, @OriginalArg(2) type: DataType?, @OriginalArg(3) mipmapped: Boolean, @OriginalArg(4) pixelCount: Int) {
		this.type = type
		this.toolkit = d3DToolkit
		this.mipmapped = mipmapped
		this.format = format
	}

	@OriginalMember(owner = "client!cg", name = "a", descriptor = "(Lclient!ria;I)V")
	open fun setFilterMode(@OriginalArg(0) filter: FilterMode?) {
		this.filter = filter
	}

	@OriginalMember(owner = "client!cg", name = "c", descriptor = "(I)Lclient!jagdx/IDirect3DBaseTexture;")
	abstract fun getTexture(): IDirect3DBaseTexture?
}

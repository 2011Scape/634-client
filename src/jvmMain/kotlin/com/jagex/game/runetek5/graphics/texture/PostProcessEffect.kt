package com.jagex.game.runetek5.graphics.texture

import com.jagex.game.runetek5.cache.node.Node
import com.jagex.game.runetek5.graphics.gl.GlToolkit
import com.jagex.game.runetek5.graphics.gl.OpenGl2DTexture
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!fba")
abstract class PostProcessEffect : Node {

	@OriginalMember(owner = "client!fba", name = "r", descriptor = "Z")
	@JvmField
	var isActive: Boolean = false

	@OriginalMember(owner = "client!fba", name = "o", descriptor = "Lclient!qo;")
	@JvmField
	val activeToolkit: GlToolkit?

	@OriginalMember(owner = "client!fba", name = "<init>", descriptor = "(Lclient!qo;)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?) : super() {
		this.activeToolkit = glToolkit
	}

	@OriginalMember(owner = "client!fba", name = "a", descriptor = "(Lclient!tp;Lclient!tp;IB)V")
	abstract fun draw(@OriginalArg(0) depthTexture: OpenGl2DTexture?, @OriginalArg(1) colorTexture: OpenGl2DTexture?, @OriginalArg(2) pass: Int)

	@OriginalMember(owner = "client!fba", name = "a", descriptor = "(Z)Z")
	abstract fun isLoaded(): Boolean

	@OriginalMember(owner = "client!fba", name = "a", descriptor = "(IBI)V")
	abstract fun resize(@OriginalArg(0) height: Int, @OriginalArg(2) width: Int)

	@OriginalMember(owner = "client!fba", name = "a", descriptor = "(BI)V")
	abstract fun endPass(@OriginalArg(1) pass: Int)

	@OriginalMember(owner = "client!fba", name = "b", descriptor = "(I)Z")
	abstract fun createShaders(): Boolean

	@OriginalMember(owner = "client!fba", name = "d", descriptor = "(I)Z")
	fun requiresDepth(): Boolean {
		return false
	}

	@OriginalMember(owner = "client!fba", name = "a", descriptor = "(B)Z")
	fun isActive(): Boolean {
		return this.isActive
	}

	@OriginalMember(owner = "client!fba", name = "b", descriptor = "(Z)I")
	open fun getFormat(): Int {
		return 0
	}

	@OriginalMember(owner = "client!fba", name = "b", descriptor = "(B)V")
	abstract fun destroy()

	@OriginalMember(owner = "client!fba", name = "f", descriptor = "(I)I")
	fun getPassCount(): Int {
		return 1
	}
}

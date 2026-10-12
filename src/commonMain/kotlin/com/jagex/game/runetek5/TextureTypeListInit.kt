package com.jagex.game.runetek5

import com.jagex.game.runetek5.config.material.MaterialProvider
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.config.material.texture.TextureTypeList
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

object TextureTypeListInit {

	@OriginalMember(owner = "client!oia", name = "a", descriptor = "(Lclient!in;ZLclient!d;)V")
	@JvmStatic
	fun initialize(@OriginalArg(0) modelResource: Js5ResourceProvider?, @OriginalArg(2) textureSource: MaterialProvider?) {
		TextureTypeList.modelResource = modelResource
		TextureTypeListInit.textureSource = textureSource
	}
	@OriginalMember(owner = "client!gq", name = "K", descriptor = "Lclient!d;")
	@JvmField
	var textureSource: MaterialProvider? = null
}

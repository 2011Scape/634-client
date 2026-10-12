package com.jagex.game.runetek5.config.particle

import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.OriginalArg
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

object ParticleTypeList {
	@OriginalMember(owner = "client!dha", name = "D", descriptor = "Lclient!in;")
	@JvmField
	var PARTICLE_INDEX: Js5ResourceProvider? = null

	@OriginalMember(owner = "client!ak", name = "a", descriptor = "(ILclient!in;)V")
	@JvmStatic
	fun initialize(@OriginalArg(1) particleIndex: Js5ResourceProvider?) {
		ParticleTypeList.PARTICLE_INDEX = particleIndex
	}
}

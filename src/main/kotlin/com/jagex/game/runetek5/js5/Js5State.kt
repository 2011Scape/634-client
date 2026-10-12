package com.jagex.game.runetek5.js5

import com.jagex.game.runetek5.sign.SignedResource
import org.openrs2.deob.annotation.OriginalMember

object Js5State {

	const val INIT: Int = 0

	const val WAITING_CONNECTION_OPENED: Int = 1

	const val SEND_HEADER: Int = 2

	const val WAITING_FIRST_RESPONSE: Int = 3

	const val WAITING_LOADING_REQUIREMENTS: Int = 4

	@OriginalMember(owner = "client!io", name = "d", descriptor = "I")
	@JvmField
	var init: Int = 0

	@OriginalMember(owner = "client!aa", name = "b", descriptor = "Lclient!of;")
	@JvmField
	var aClass240_9: SignedResource? = null

	@OriginalMember(owner = "client!in", name = "P", descriptor = "J")
	@JvmField
	var js5ConnectTime: Long = 0L

	@OriginalMember(owner = "client!ec", name = "j", descriptor = "I")
	@JvmField
	var pulsesBeforeJs5Reconnect: Int = 0
}

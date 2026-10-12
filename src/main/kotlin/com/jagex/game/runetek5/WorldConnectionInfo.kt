package com.jagex.game.runetek5

import com.jagex.game.runetek5.sign.SignLink
import com.jagex.game.runetek5.sign.SignedResource
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!pc")
class WorldConnectionInfo {

	@OriginalMember(owner = "client!pc", name = "f", descriptor = "I")
	@JvmField
	var worldId: Int = 0

	@OriginalMember(owner = "client!pc", name = "j", descriptor = "Ljava/lang/String;")
	@JvmField
	var host: String? = null

	@OriginalMember(owner = "client!pc", name = "b", descriptor = "Z")
	private var useProxy: Boolean = false

	@OriginalMember(owner = "client!pc", name = "i", descriptor = "Z")
	private var usePrimaryPort: Boolean = true

	@OriginalMember(owner = "client!pc", name = "a", descriptor = "I")
	@JvmField
	var primaryPort: Int = 443

	@OriginalMember(owner = "client!pc", name = "k", descriptor = "I")
	@JvmField
	var secondaryPort: Int = 43594

	@OriginalMember(owner = "client!pc", name = "a", descriptor = "(I)V")
	fun cycleConnectionMode() {
		if (!this.usePrimaryPort) {
			this.useProxy = true
			this.usePrimaryPort = true
		} else if (this.useProxy) {
			this.useProxy = false
		} else {
			this.usePrimaryPort = false
		}
	}

	@OriginalMember(owner = "client!pc", name = "a", descriptor = "(Lclient!wea;B)Lclient!of;")
	fun openSocket(@OriginalArg(0) signLink: SignLink?): SignedResource {
		return signLink!!.openSocket(this.useProxy, if (this.usePrimaryPort) this.primaryPort else this.secondaryPort, this.host)
	}

	@OriginalMember(owner = "client!pc", name = "a", descriptor = "(BLclient!pc;)Z")
	fun isSameServer(@OriginalArg(1) other: WorldConnectionInfo?): Boolean {
		if (other == null) {
			return false
		} else {
			return this.worldId == other.worldId && this.host!!.equals(other.host)
		}
	}
}

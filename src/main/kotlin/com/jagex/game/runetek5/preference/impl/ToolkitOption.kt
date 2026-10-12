package com.jagex.game.runetek5.preference.impl

import com.jagex.game.runetek5.preference.GamePreferences
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.preference.Preference
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.NativeLibraryOwner

@OriginalClass("client!eea")
class ToolkitOption : Preference {

	@OriginalMember(owner = "client!eea", name = "z", descriptor = "Z")
	private var verifySettings: Boolean = true

	@OriginalMember(owner = "client!eea", name = "C", descriptor = "Z")
	@JvmField var isDefault: Boolean

	// Kotlin drops a "= false" initializer, but Java ran it after the super constructor,
	// resetting the true set by getDefaultValue(); keep that explicit store.
	init {
		this.isDefault = false
	}

	@OriginalMember(owner = "client!eea", name = "<init>", descriptor = "(ILclient!dh;)V")
	constructor(@OriginalArg(0) value: Int, @OriginalArg(1) clientOptions: GamePreferences?) : super(value, clientOptions)

	@OriginalMember(owner = "client!eea", name = "<init>", descriptor = "(Lclient!dh;)V")
	constructor(@OriginalArg(0) clientOptions: GamePreferences?) : super(clientOptions)

	@OriginalMember(owner = "client!eea", name = "b", descriptor = "(II)I")
	override fun canSet(@OriginalArg(1) value: Int): Int {
		if (this.gamePreferences!!.getProcessorSpecs().isSigned()) {
			return if (value == 3 && !NativeLibraryOwner.hasNativeLibraryFile("jagdx")) 3 else 2
		} else {
			return 3
		}
	}

	@OriginalMember(owner = "client!eea", name = "a", descriptor = "(II)V")
	override fun setValue(@OriginalArg(1) value: Int) {
		this.value = value
		this.isDefault = false
	}

	@OriginalMember(owner = "client!eea", name = "b", descriptor = "(ZI)V")
	fun setActive(@OriginalArg(0) verifySettings: Boolean) {
		this.verifySettings = verifySettings
	}

	@OriginalMember(owner = "client!eea", name = "b", descriptor = "(I)I")
	override fun getDefaultValue(): Int {
		this.isDefault = true
		return if (this.gamePreferences!!.getProcessorSpecs().isSigned()) 2 else 0
	}

	@OriginalMember(owner = "client!eea", name = "a", descriptor = "(Z)V")
	override fun validate() {
		if (!this.gamePreferences!!.getProcessorSpecs().isSigned()) {
			this.value = 0
		}
		if (this.value < 0 || this.value > 5) {
			this.value = this.getDefaultValue()
		}
	}

	@OriginalMember(owner = "client!eea", name = "f", descriptor = "(I)I")
	fun getValue(): Int {
		return this.value
	}

	@OriginalMember(owner = "client!eea", name = "a", descriptor = "(B)Z")
	fun isActive(): Boolean {
		return this.verifySettings
	}

	@OriginalMember(owner = "client!eea", name = "g", descriptor = "(I)Z")
	fun canMod(): Boolean {
		return this.gamePreferences!!.getProcessorSpecs().isSigned()
	}

	companion object {
		@OriginalMember(owner = "client!oba", name = "e", descriptor = "Z")
		@JvmField var renderWithCpu: Boolean = false

		@OriginalMember(owner = "client!paa", name = "tb", descriptor = "I")
		@JvmField var autoSetupToolkit: Int = 0
	}
}

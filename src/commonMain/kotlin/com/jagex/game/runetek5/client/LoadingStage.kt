package com.jagex.game.runetek5.client

import com.jagex.game.runetek5.LocalisedText
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.graphics.loginscreen.LoginScreenComponent
import com.jagex.game.runetek5.tools.LocalizedText
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@OriginalClass("client!jj")
class LoadingStage @OriginalMember(owner = "client!jj", name = "<init>", descriptor = "(ILclient!va;Lclient!va;IIZZ)V") constructor(@OriginalArg(0) step: Int, @OriginalArg(1) localisedText: LocalisedText?, @OriginalArg(2) localisedText2: LocalisedText?, @OriginalArg(3) primaryCompletionDisplay: Int, @OriginalArg(4) secondaryCompletionDisplay: Int, @OriginalArg(5) showPercentage: Boolean, @OriginalArg(6) hasProgressTracking: Boolean) {

	@OriginalMember(owner = "client!jj", name = "a", descriptor = "Lclient!va;")
	@JvmField
	val stalledText: LocalisedText?

	@OriginalMember(owner = "client!jj", name = "b", descriptor = "Z")
	@JvmField
	val showPercentage: Boolean

	@OriginalMember(owner = "client!jj", name = "d", descriptor = "Lclient!va;")
	@JvmField
	val localisedText2: LocalisedText?

	@OriginalMember(owner = "client!jj", name = "e", descriptor = "Z")
	@JvmField
	val hasProgressTracking: Boolean

	@OriginalMember(owner = "client!jj", name = "f", descriptor = "I")
	private val step: Int

	@OriginalMember(owner = "client!jj", name = "j", descriptor = "I")
	@JvmField
	val secondaryCompletionDisplay: Int

	@OriginalMember(owner = "client!jj", name = "n", descriptor = "I")
	@JvmField
	val primaryCompletionDisplay: Int

	@OriginalMember(owner = "client!jj", name = "<init>", descriptor = "(ILclient!va;I)V")
	constructor(@OriginalArg(0) number: Int, @OriginalArg(1) localisedText: LocalisedText?, @OriginalArg(2) completionDisplay: Int) : this(number, localisedText, localisedText, completionDisplay, completionDisplay, true, false)

	@OriginalMember(owner = "client!jj", name = "<init>", descriptor = "(ILclient!va;Lclient!va;II)V")
	constructor(@OriginalArg(0) number: Int, @OriginalArg(1) localisedText: LocalisedText?, @OriginalArg(2) localisedText2: LocalisedText?, @OriginalArg(3) primaryCompletionDisplay: Int, @OriginalArg(4) secondaryCompletionDisplay: Int) : this(number, localisedText, localisedText2, primaryCompletionDisplay, secondaryCompletionDisplay, true, false)

	init {
		this.stalledText = localisedText
		this.secondaryCompletionDisplay = secondaryCompletionDisplay
		this.step = step
		this.primaryCompletionDisplay = primaryCompletionDisplay
		this.localisedText2 = localisedText2
		this.hasProgressTracking = hasProgressTracking
		this.showPercentage = showPercentage
	}

	@OriginalMember(owner = "client!jj", name = "a", descriptor = "(I)I")
	fun getStep(): Int {
		return this.step
	}

	@OriginalMember(owner = "client!jj", name = "toString", descriptor = "()Ljava/lang/String;")
	override fun toString(): String {
		throw IllegalStateException()
	}

	companion object {
		@OriginalMember(owner = "client!qca", name = "c", descriptor = "[Lclient!dca;")
		@JvmField
		var gametipsToDisplay: Array<LoginScreenComponent?>? = null

		@OriginalMember(owner = "client!hm", name = "d", descriptor = "[Lclient!jj;")
		@JvmField
		var LOADING_STAGES: Array<LoadingStage?>? = null
		@OriginalMember(owner = "client!nca", name = "H", descriptor = "Ljava/lang/String;")
		@JvmField
		var LOADING_TEXT: String? = null

		@OriginalMember(owner = "client!gf", name = "f", descriptor = "I")
		@JvmField
		var lastDisplayedGametip: Int = -1

		@OriginalMember(owner = "client!tr", name = "h", descriptor = "I")
		@JvmField
		var displayedLoadingPercentage: Int = 0
		@OriginalMember(owner = "client!et", name = "L", descriptor = "Lclient!jj;")
		@JvmField
		var LOADING_STAGE: LoadingStage? = null
		@OriginalMember(owner = "client!jj", name = "z", descriptor = "Lclient!jj;")
		@JvmField
		val aClass167_17: LoadingStage = LoadingStage(14, LocalizedText.DOWNLOADING_UPDATES, LocalizedText.DOWNLOADING_UPDATES, 94, 95)

		@OriginalMember(owner = "client!jj", name = "F", descriptor = "Lclient!jj;")
		@JvmField
		val aClass167_23: LoadingStage = LoadingStage(20, LocalizedText.DOWNLOADING_UPDATES, 100)

		@OriginalMember(owner = "client!jj", name = "p", descriptor = "Lclient!jj;")
		@JvmField
		val aClass167_7: LoadingStage = LoadingStage(4, LocalizedText.CHECKING_FOR_UPDATES, LocalizedText.CHECKING_FOR_UPDATES, 3, 4)

		@OriginalMember(owner = "client!jj", name = "w", descriptor = "Lclient!jj;")
		@JvmField
		val aClass167_14: LoadingStage = LoadingStage(11, LocalizedText.DOWNLOADING_UPDATES, LocalizedText.DOWNLOADING_UPDATES, 0, 92, true, true)

		@OriginalMember(owner = "client!jj", name = "g", descriptor = "Lclient!jj;")
		@JvmField
		val aClass167_3: LoadingStage = LoadingStage(0, LocalizedText.CHECKING_FOR_UPDATES, LocalizedText.CHECKING_FOR_UPDATES, 0, 1)

		@OriginalMember(owner = "client!jj", name = "u", descriptor = "Lclient!jj;")
		@JvmField
		val aClass167_12: LoadingStage = LoadingStage(9, LocalizedText.CHECKING_FOR_UPDATES, 99)

		@OriginalMember(owner = "client!jj", name = "B", descriptor = "Lclient!jj;")
		@JvmField
		val aClass167_19: LoadingStage = LoadingStage(16, LocalizedText.DOWNLOADING_UPDATES, 97)

		@OriginalMember(owner = "client!jj", name = "C", descriptor = "Lclient!jj;")
		@JvmField
		val aClass167_20: LoadingStage = LoadingStage(17, LocalizedText.DOWNLOADING_UPDATES, 97)

		@OriginalMember(owner = "client!jj", name = "E", descriptor = "Lclient!jj;")
		@JvmField
		val aClass167_22: LoadingStage = LoadingStage(19, LocalizedText.DOWNLOADING_UPDATES, 100)

		@OriginalMember(owner = "client!jj", name = "m", descriptor = "Lclient!jj;")
		@JvmField
		val aClass167_5: LoadingStage = LoadingStage(2, LocalizedText.CHECKING_FOR_UPDATES, LocalizedText.CHECKING_FOR_UPDATES, 2, 3)

		@OriginalMember(owner = "client!jj", name = "r", descriptor = "Lclient!jj;")
		@JvmField
		val aClass167_9: LoadingStage = LoadingStage(6, LocalizedText.CHECKING_FOR_UPDATES, 4)

		@OriginalMember(owner = "client!jj", name = "t", descriptor = "Lclient!jj;")
		@JvmField
		val aClass167_11: LoadingStage = LoadingStage(8, LocalizedText.CHECKING_FOR_UPDATES, LocalizedText.CHECKING_FOR_UPDATES, 5, 98, true, true)

		@OriginalMember(owner = "client!jj", name = "v", descriptor = "Lclient!jj;")
		@JvmField
		val aClass167_13: LoadingStage = LoadingStage(10, LocalizedText.CHECKING_FOR_UPDATES, 100)

		@OriginalMember(owner = "client!jj", name = "A", descriptor = "Lclient!jj;")
		@JvmField
		val aClass167_18: LoadingStage = LoadingStage(15, LocalizedText.DOWNLOADING_UPDATES, LocalizedText.DOWNLOADING_UPDATES, 96, 97)

		@OriginalMember(owner = "client!jj", name = "o", descriptor = "Lclient!jj;")
		@JvmField
		val aClass167_6: LoadingStage = LoadingStage(3, LocalizedText.CHECKING_FOR_UPDATES, 3)

		@OriginalMember(owner = "client!jj", name = "q", descriptor = "Lclient!jj;")
		@JvmField
		val aClass167_8: LoadingStage = LoadingStage(5, LocalizedText.CHECKING_FOR_UPDATES, 4)

		@OriginalMember(owner = "client!jj", name = "c", descriptor = "(I)[Lclient!jj;")
		@JvmStatic
		fun getValues(): Array<LoadingStage?>? {
			return arrayOf<LoadingStage?>(aClass167_3, aClass167_4, aClass167_5, aClass167_6, aClass167_7, aClass167_8, aClass167_9, aClass167_10, aClass167_11, aClass167_12, aClass167_13, aClass167_14, aClass167_15, LoadingStage.aClass167_16, aClass167_17, aClass167_18, aClass167_19, aClass167_20, aClass167_21, aClass167_22, aClass167_23)
		}

		@OriginalMember(owner = "client!jj", name = "x", descriptor = "Lclient!jj;")
		@JvmField
		val aClass167_15: LoadingStage = LoadingStage(12, LocalizedText.DOWNLOADING_UPDATES, LocalizedText.DOWNLOADING_UPDATES, 92, 92)

		@OriginalMember(owner = "client!jj", name = "s", descriptor = "Lclient!jj;")
		@JvmField
		val aClass167_10: LoadingStage = LoadingStage(7, LocalizedText.CHECKING_FOR_UPDATES, LocalizedText.CHECKING_FOR_UPDATES, 4, 5)

		@OriginalMember(owner = "client!jj", name = "l", descriptor = "Lclient!jj;")
		@JvmField
		val aClass167_4: LoadingStage = LoadingStage(1, LocalizedText.CHECKING_FOR_UPDATES, 2)

		@OriginalMember(owner = "client!jj", name = "D", descriptor = "Lclient!jj;")
		@JvmField
		val aClass167_21: LoadingStage = LoadingStage(18, LocalizedText.DOWNLOADING_UPDATES, 100)
		@OriginalMember(owner = "client!vh", name = "e", descriptor = "J")
		@JvmField
		var loadingStageChangedTime: Long = 0L

		@OriginalMember(owner = "client!gha", name = "b", descriptor = "(I)V")
		@JvmStatic
		fun initializeTips() {
			if (LoadingStage.gametipsToDisplay == null) {
				return
			}
			@Pc(9) val loadingScreens: Array<LoginScreenComponent?> = LoadingStage.gametipsToDisplay!!
			@Pc(11) var i: Int = 0
			while (i < loadingScreens.size) {
				@Pc(17) val loadingScreen: LoginScreenComponent? = loadingScreens[i]
				loadingScreen!!.init()
				i++
			}
		}
		@OriginalMember(owner = "client!gr", name = "f", descriptor = "[I")
		@JvmField
		val JS5_ARCHIVE_LOAD_WEIGHTS: IntArray = intArrayOf(4, 4, 1, 2, 6, 4, 2, 44, 2, 2, 2, 2, 2, 1, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1)

		@OriginalMember(owner = "client!pn", name = "e", descriptor = "J")
		@JvmField
		var lastGcTime: Long = 0L

		@OriginalMember(owner = "client!wq", name = "a", descriptor = "Z")
		@JvmField
		var safeModeChosenAtStartup: Boolean = false

		@OriginalMember(owner = "client!jj", name = "y", descriptor = "Lclient!jj;")
		@JvmField
		val aClass167_16: LoadingStage = LoadingStage(13, LocalizedText.DOWNLOADING_UPDATES, LocalizedText.DOWNLOADING_UPDATES, 92, 93)

		@OriginalMember(owner = "client!jf", name = "r", descriptor = "J")
		@JvmField
		var gcWaitStartTime: Long = 0L

		@OriginalMember(owner = "client!cb", name = "g", descriptor = "I")
		@JvmField
		var js5IndexPercentBaseline: Int = -1
	}
}

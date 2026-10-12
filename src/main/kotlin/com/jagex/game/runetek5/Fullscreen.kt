package com.jagex.game.runetek5

import com.jagex.game.runetek5.client.GameShell
import com.jagex.game.runetek5.core.util.Arrays
import com.jagex.game.runetek5.preference.GamePreferences
import com.jagex.game.runetek5.sign.SignLink
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

object Fullscreen {

	@OriginalMember(owner = "client!ev", name = "b", descriptor = "(I)[Lclient!b;")
	@JvmStatic
	fun getActiveDisplayModes(): Array<DisplayModeDetails?>? {
		if (Fullscreen.aClass25Array1 == null) {
			@Pc(17) val displayModes: Array<DisplayModeDetails?> = SignLink.getDisplayModes(GameShell.signLink)
			@Pc(21) val filteredModes: Array<DisplayModeDetails?> = arrayOfNulls<DisplayModeDetails>(displayModes.size)
			@Pc(23) var length: Int = 0
			@Pc(28) val maxScreenSize: Int = GamePreferences.uid!!.maxScreenSize!!.getValue()
			@Pc(100) var j: Int
			@Pc(106) var storedMode: DisplayModeDetails?
			label70@ for (@Pc(30) i in 0 until displayModes.size) {
				@Pc(36) val mode: DisplayModeDetails = displayModes[i]!!
				if ((mode.bitDepth <= 0 || mode.bitDepth >= 24) && mode.width >= 800 && mode.height >= 600 && (maxScreenSize != 2 || mode.width <= 800 && mode.height <= 600) && (maxScreenSize != 1 || mode.width <= 1024 && mode.height <= 768)) {
					j = 0
					while (j < length) {
						storedMode = filteredModes[j]
						if (storedMode!!.width == mode.width && mode.height == storedMode.height) {
							if (storedMode.bitDepth < mode.bitDepth) {
								filteredModes[j] = mode
							}
							continue@label70
						}
						j++
					}
					filteredModes[length] = mode
					length++
				}
			}
			Fullscreen.aClass25Array1 = arrayOfNulls<DisplayModeDetails>(length)
			Arrays.copy(filteredModes, 0, Fullscreen.aClass25Array1, 0, length)
			@Pc(166) val areas: IntArray = IntArray(Fullscreen.aClass25Array1!!.size)
			j = 0
			while (j < Fullscreen.aClass25Array1!!.size) {
				storedMode = Fullscreen.aClass25Array1!![j]
				areas[j] = storedMode!!.width * storedMode.height
				j++
			}
			IntKeyedQuicksort.sort(@Suppress("UNCHECKED_CAST") (Fullscreen.aClass25Array1!! as Array<Any?>), areas)
		}
		return Fullscreen.aClass25Array1
	}

	@OriginalMember(owner = "client!si", name = "d", descriptor = "[Lclient!b;")
	@JvmField
	var aClass25Array1: Array<DisplayModeDetails?>? = null
}

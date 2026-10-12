package com.jagex.game.runetek5.config.iftype

import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client, which keeps how a sub-interface was opened as an int: 0 a modal,
 * which closing modals removes; 1 an overlay; 3 opened by a script. The server's IF_OPENSUB byte
 * can carry any other value, which becomes UNKNOWN: the client only ever compares the kind with
 * these three and never reads it back as a number.
 */
enum class SubInterfaceType(@JvmField val id: Int) {

	MODAL(0),

	OVERLAY(1),

	OVERLAY_CLIENT(3),

	UNKNOWN(-1);

	companion object {
		@JvmStatic
		fun fromId(id: Int): SubInterfaceType {
			if (id == 0) {
				return MODAL
			} else if (id == 1) {
				return OVERLAY
			} else if (id == 3) {
				return OVERLAY_CLIENT
			}
			return UNKNOWN
		}
	}
}

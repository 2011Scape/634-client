package com.jagex.game.runetek5.config.rsinterface

/**
 * Not part of the original client, which keeps a component's type as the int from its definition
 * (the low 7 bits of its second byte) or from the script that creates it.
 * Ids with no constant (10 to 127 from the cache, any other from a script) are UNKNOWN: the client
 * only ever compares the type with the ids named here, and never reads it back as a number.
 */
enum class ComponentType(@JvmField val id: Int) {

	CONTAINER(0),

	TYPE_1(1),

	TYPE_2(2),

	FIGURE(3),

	TEXT(4),

	SPRITE(5),

	MODEL(6),

	TYPE_7(7),

	TYPE_8(8),

	LINE(9),

	UNKNOWN(-1);

	companion object {
		private val BY_ID: Array<ComponentType?> = arrayOfNulls<ComponentType>(10)

		init {
			for (type in values()) {
				if (type.id >= 0) {
					BY_ID[type.id] = type
				}
			}
		}

		@JvmStatic
		fun fromId(id: Int): ComponentType? {
			return if (id >= 0 && id < BY_ID.size) BY_ID[id] else UNKNOWN
		}
	}
}

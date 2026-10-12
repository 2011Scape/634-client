package com.jagex.game.runetek5.config.rsinterface

/**
 * Not part of the original client, which keeps what a component's model shows as an int. A
 * server or script value with no constant becomes UNKNOWN: the client only
 * compares the type with these ids (an unknown one builds no model, as before), except for the
 * raw model cache key, which uses the id.
 */
enum class MediaType(@JvmField val id: Int) {

	UNDEFINED(0),

	RAW_MODEL(1),

	NPC_HEAD(2),

	PLAYER_HEAD(3),

	ITEM(4),

	PLAYER_MODEL(5),

	NPC_MODEL(6),

	PLAYER_HEAD_IGNOREWORN(7),

	ITEM_CONTAINER_MALE(8),

	ITEM_CONTAINER_FEMALE(9),

	UNKNOWN(-1);

	companion object {
		private val BY_ID: Array<MediaType?> = arrayOfNulls<MediaType>(10)

		init {
			for (type in values()) {
				if (type.id >= 0) {
					BY_ID[type.id] = type
				}
			}
		}

		@JvmStatic
		fun fromId(id: Int): MediaType? {
			return if (id >= 0 && id < BY_ID.size) BY_ID[id] else UNKNOWN
		}
	}
}

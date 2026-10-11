package com.jagex.game.runetek5.config.rsinterface;

/**
 * Not part of the original client, which keeps what a component's model shows as an int. A
 * server or script value with no constant becomes UNKNOWN: the client only
 * compares the type with these ids (an unknown one builds no model, as before), except for the
 * raw model cache key, which uses the id.
 */
public enum MediaType {

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

	private static final MediaType[] BY_ID = new MediaType[10];

	static {
		for (MediaType type : values()) {
			if (type.id >= 0) {
				BY_ID[type.id] = type;
			}
		}
	}

	public final int id;

	MediaType(int id) {
		this.id = id;
	}

	public static MediaType fromId(int id) {
		return id >= 0 && id < BY_ID.length ? BY_ID[id] : UNKNOWN;
	}
}

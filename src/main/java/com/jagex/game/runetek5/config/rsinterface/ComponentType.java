package com.jagex.game.runetek5.config.rsinterface;

/**
 * Not part of the original client, which keeps a component's type as the int from its definition
 * (the low 7 bits of its second byte) or from the script that creates it.
 * Ids with no constant (10 to 127 from the cache, any other from a script) are UNKNOWN: the client
 * only ever compares the type with the ids named here, and never reads it back as a number.
 */
public enum ComponentType {

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

	private static final ComponentType[] BY_ID = new ComponentType[10];

	static {
		for (ComponentType type : values()) {
			if (type.id >= 0) {
				BY_ID[type.id] = type;
			}
		}
	}

	public final int id;

	ComponentType(int id) {
		this.id = id;
	}

	public static ComponentType fromId(int id) {
		return id >= 0 && id < BY_ID.length ? BY_ID[id] : UNKNOWN;
	}
}

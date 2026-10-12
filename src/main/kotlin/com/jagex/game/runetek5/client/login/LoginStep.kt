package com.jagex.game.runetek5.client.login

/**
 * Not part of the original client, which keeps the world login's progress as an int (0 to 14). Each
 * step is named for what the login code does in it (Static701.method5595).
 */
enum class LoginStep {

	/** Not logging in: the login code returns at once. */
	IDLE,

	/** Open the connection to the world. */
	CONNECT,

	/** Wait for the server to accept the connection. */
	WAIT_FOR_CONNECTION_ACK,

	/** Send the login request. */
	SEND_LOGIN_REQUEST,

	/** Read the login response code. */
	READ_RESPONSE,

	/** A video advertisement is showing; the login code waits, as when idle. */
	SHOWING_ADVERTISEMENT,

	/** The advertisement has finished: tell the server, then read the response again. */
	ADVERTISEMENT_FINISHED,

	/** World hopping is blocked: read how long for. */
	READ_HOP_BLOCKED_TIME,

	/** Logged in: read the size of the login details. */
	READ_LOGIN_DETAILS_SIZE,

	/** Read the login details (rights, player index, members, ...). */
	READ_LOGIN_DETAILS,

	/** Read the payload of the first packet. */
	READ_FIRST_PACKET,

	/** Read the first packet's header. */
	READ_FIRST_PACKET_HEADER,

	/** Decode the first packet's opcode and size. */
	READ_FIRST_PACKET_SIZE,

	/** The login was refused by a script: read the reason. */
	READ_DISALLOWED_REASON,

	/** Reconnected: read the reconnect packet. */
	READ_RECONNECT_PACKET
}

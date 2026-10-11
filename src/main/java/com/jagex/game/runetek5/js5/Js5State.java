package com.jagex.game.runetek5.js5;
import com.jagex.game.runetek5.sign.SignedResource;
import org.openrs2.deob.annotation.OriginalMember;

public final class Js5State {

	public static final int INIT = 0;

	public static final int WAITING_CONNECTION_OPENED = 1;

	public static final int SEND_HEADER = 2;

	public static final int WAITING_FIRST_RESPONSE = 3;

	public static final int WAITING_LOADING_REQUIREMENTS = 4;

	private Js5State() {
		/* empty */
	}
	@OriginalMember(owner = "client!io", name = "d", descriptor = "I")
	public static int init = 0;

	@OriginalMember(owner = "client!aa", name = "b", descriptor = "Lclient!of;")
	public static SignedResource aClass240_9;

	@OriginalMember(owner = "client!in", name = "P", descriptor = "J")
	public static long js5ConnectTime;

	@OriginalMember(owner = "client!ec", name = "j", descriptor = "I")
	public static int pulsesBeforeJs5Reconnect = 0;
}

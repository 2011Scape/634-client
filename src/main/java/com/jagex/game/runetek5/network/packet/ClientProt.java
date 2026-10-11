package com.jagex.game.runetek5.network.packet;

import com.jagex.game.runetek5.ClientPacket;
import org.openrs2.deob.annotation.OriginalMember;

public final class ClientProt {
	@OriginalMember(owner = "client!mm", name = "x", descriptor = "Lclient!ej;")
	public static final ClientPacket IF_DRAG_ONTO_IF = new ClientPacket(78, 16);

	@OriginalMember(owner = "client!rga", name = "B", descriptor = "Lclient!ej;")
	public static final ClientPacket SCREEN_SIZE = new ClientPacket(7, 6);

	@OriginalMember(owner = "client!nba", name = "d", descriptor = "Lclient!ej;")
	public static final ClientPacket TRANSMITVAR_VERIFYID = new ClientPacket(13, 2);

	@OriginalMember(owner = "client!od", name = "r", descriptor = "Lclient!ej;")
	public static final ClientPacket PING = new ClientPacket(0, 0);

	@OriginalMember(owner = "client!ah", name = "e", descriptor = "Lclient!ej;")
	public static final ClientPacket IF_ON_IF = new ClientPacket(72, 16);

	private ClientProt() {
		/* empty */
	}
	@OriginalMember(owner = "client!re", name = "p", descriptor = "Lclient!ej;")
	public static final ClientPacket OPOBJ4 = new ClientPacket(24, 7);

	@OriginalMember(owner = "client!qia", name = "F", descriptor = "Lclient!ej;")
	public static final ClientPacket OPLOC2 = new ClientPacket(36, 7);

	@OriginalMember(owner = "client!bp", name = "e", descriptor = "Lclient!ej;")
	public static final ClientPacket OPOBJ5 = new ClientPacket(26, 7);

	@OriginalMember(owner = "client!tca", name = "i", descriptor = "Lclient!ej;")
	public static final ClientPacket IF_BUTTON8 = new ClientPacket(60, 8);

	@OriginalMember(owner = "client!qda", name = "p", descriptor = "Lclient!ej;")
	public static final ClientPacket OPPLAYER3 = new ClientPacket(79, 3);

	@OriginalMember(owner = "client!kg", name = "h", descriptor = "Lclient!ej;")
	public static final ClientPacket OPNPC5 = new ClientPacket(65, 3);

	@OriginalMember(owner = "client!tr", name = "d", descriptor = "Lclient!ej;")
	public static final ClientPacket OPPLAYER6 = new ClientPacket(51, 3);

	@OriginalMember(owner = "client!hq", name = "e", descriptor = "Lclient!ej;")
	public static final ClientPacket OPPLAYER2 = new ClientPacket(12, 3);

	@OriginalMember(owner = "client!gh", name = "Rb", descriptor = "Lclient!ej;")
	public static final ClientPacket OPOBJ3 = new ClientPacket(45, 7);

	@OriginalMember(owner = "client!oea", name = "j", descriptor = "Lclient!ej;")
	public static final ClientPacket REPORT_ABUSE = new ClientPacket(66, -1);

	@OriginalMember(owner = "client!sda", name = "j", descriptor = "Lclient!ej;")
	public static final ClientPacket IF_BUTTON6 = new ClientPacket(39, 8);

	@OriginalMember(owner = "client!qi", name = "b", descriptor = "Lclient!ej;")
	public static final ClientPacket OPOBJ2 = new ClientPacket(16, 7);

	@OriginalMember(owner = "client!er", name = "p", descriptor = "Lclient!ej;")
	public static final ClientPacket IF_BUTTON3 = new ClientPacket(9, 8);

	@OriginalMember(owner = "client!pq", name = "f", descriptor = "Lclient!ej;")
	public static final ClientPacket OPPLAYER8 = new ClientPacket(18, 3);

	@OriginalMember(owner = "client!tb", name = "c", descriptor = "Lclient!ej;")
	public static final ClientPacket aClass94_81 = new ClientPacket(71, 2);

	@OriginalMember(owner = "client!tu", name = "a", descriptor = "Lclient!ej;")
	public static final ClientPacket OPLOC3 = new ClientPacket(80, 7);

	@OriginalMember(owner = "client!kb", name = "g", descriptor = "Lclient!ej;")
	public static final ClientPacket OPOBJ6 = new ClientPacket(37, 2);

	@OriginalMember(owner = "client!ug", name = "H", descriptor = "Lclient!ej;")
	public static final ClientPacket EVENT_MOUSE_MOVE = new ClientPacket(83, -1);

	@OriginalMember(owner = "client!ha", name = "k", descriptor = "Lclient!ej;")
	public static final ClientPacket IGNORELIST_DEL = new ClientPacket(73, -1);

	@OriginalMember(owner = "client!sl", name = "db", descriptor = "Lclient!ej;")
	public static final ClientPacket SEND_PRIVATE_MESSAGE = new ClientPacket(20, -1);

	@OriginalMember(owner = "client!bk", name = "s", descriptor = "Lclient!ej;")
	public static final ClientPacket WRITE_PING = new ClientPacket(21, 2);

	@OriginalMember(owner = "client!qr", name = "e", descriptor = "Lclient!ej;")
	public static final ClientPacket OPLOC5 = new ClientPacket(38, 7);

	@OriginalMember(owner = "client!gh", name = "Lb", descriptor = "Lclient!ej;")
	public static final ClientPacket aClass94_37 = new ClientPacket(84, -1);
	@OriginalMember(owner = "client!pj", name = "K", descriptor = "Lclient!ej;")
	public static final ClientPacket OPEN_URL = new ClientPacket(67, -1);

	@OriginalMember(owner = "client!qg", name = "f", descriptor = "Lclient!ej;")
	public static final ClientPacket MOVE_GAMECLICK = new ClientPacket(35, 5);

	@OriginalMember(owner = "client!pi", name = "A", descriptor = "Lclient!ej;")
	public static final ClientPacket CHAT_TYPE = new ClientPacket(31, 1);

	@OriginalMember(owner = "client!naa", name = "Y", descriptor = "Lclient!ej;")
	public static final ClientPacket REQUEST_WORLD_LIST = new ClientPacket(30, 4);

	@OriginalMember(owner = "client!lr", name = "J", descriptor = "Lclient!ej;")
	public static final ClientPacket OPPLAYER7 = new ClientPacket(57, 3);

	@OriginalMember(owner = "client!mm", name = "s", descriptor = "Lclient!ej;")
	public static final ClientPacket OPPLAYER1 = new ClientPacket(25, 3);
	@OriginalMember(owner = "client!nba", name = "h", descriptor = "Lclient!ej;")
	public static final ClientPacket IF_BUTTON4 = new ClientPacket(15, 8);

	@OriginalMember(owner = "client!co", name = "fb", descriptor = "Lclient!ej;")
	public static final ClientPacket OPNPC4 = new ClientPacket(62, 3);

	@OriginalMember(owner = "client!nd", name = "d", descriptor = "Lclient!ej;")
	public static final ClientPacket FRIEND_SETRANK = new ClientPacket(74, -1);
	@OriginalMember(owner = "client!jg", name = "y", descriptor = "Lclient!ej;")
	public static final ClientPacket OPNPC1 = new ClientPacket(63, 3);

	@OriginalMember(owner = "client!rca", name = "a", descriptor = "Lclient!ej;")
	public static final ClientPacket IF_BUTTON2 = new ClientPacket(59, 8);

	@OriginalMember(owner = "client!ah", name = "a", descriptor = "Lclient!ej;")
	public static final ClientPacket OPOBJT = new ClientPacket(34, 15);

	@OriginalMember(owner = "client!cda", name = "a", descriptor = "Lclient!ej;")
	public static final ClientPacket MOVE_MINIMAPCLICK = new ClientPacket(82, 18);

	@OriginalMember(owner = "client!de", name = "e", descriptor = "Lclient!ej;")
	public static final ClientPacket aClass94_23 = new ClientPacket(47, 4);

	@OriginalMember(owner = "client!eb", name = "n", descriptor = "Lclient!ej;")
	public static final ClientPacket RESUME_P_NAMEDIALOG = new ClientPacket(43, -1);

	@OriginalMember(owner = "client!ek", name = "g", descriptor = "Lclient!ej;")
	public static final ClientPacket EVENT_APPLET_FOCUS = new ClientPacket(8, 1);

	@OriginalMember(owner = "client!qt", name = "m", descriptor = "Lclient!ej;")
	public static final ClientPacket aClass94_89 = new ClientPacket(76, 4);

	@OriginalMember(owner = "client!vea", name = "a", descriptor = "Lclient!ej;")
	public static final ClientPacket IF_BUTTON10 = new ClientPacket(42, 8);

	@OriginalMember(owner = "client!jh", name = "z", descriptor = "Lclient!ej;")
	public static final ClientPacket OPPLAYER5 = new ClientPacket(81, 3);

	@OriginalMember(owner = "client!ij", name = "u", descriptor = "Lclient!ej;")
	public static final ClientPacket CHAT_SETFILTER = new ClientPacket(75, 3);

	@OriginalMember(owner = "client!be", name = "q", descriptor = "Lclient!ej;")
	public static final ClientPacket CLOSE_MODAL = new ClientPacket(70, 0);

	@OriginalMember(owner = "client!fc", name = "R", descriptor = "Lclient!ej;")
	public static final ClientPacket OPOBJ1 = new ClientPacket(22, 7);

	@OriginalMember(owner = "client!eaa", name = "j", descriptor = "Lclient!ej;")
	public static final ClientPacket OPNPC2 = new ClientPacket(29, 3);

	@OriginalMember(owner = "client!jca", name = "l", descriptor = "Lclient!ej;")
	public static final ClientPacket OPPLAYER4 = new ClientPacket(44, 3);

	@OriginalMember(owner = "client!eb", name = "l", descriptor = "Lclient!ej;")
	public static final ClientPacket MAP_BUILD_COMPLETE = new ClientPacket(4, 0);

	@OriginalMember(owner = "client!vea", name = "e", descriptor = "Lclient!ej;")
	public static final ClientPacket EVENT_CAMERA_POSITION = new ClientPacket(55, 4);

	@OriginalMember(owner = "client!in", name = "r", descriptor = "Lclient!ej;")
	public static final ClientPacket QUICKCHAT_PUBLIC = new ClientPacket(3, -1);

	@OriginalMember(owner = "client!gq", name = "D", descriptor = "Lclient!ej;")
	public static final ClientPacket OPLOC4 = new ClientPacket(56, 7);

	@OriginalMember(owner = "client!ew", name = "h", descriptor = "Lclient!ej;")
	public static final ClientPacket IF_BUTTON7 = new ClientPacket(33, 8);

	@OriginalMember(owner = "client!nq", name = "d", descriptor = "Lclient!ej;")
	public static final ClientPacket OPNPCT = new ClientPacket(61, 11);

	@OriginalMember(owner = "client!jk", name = "w", descriptor = "Lclient!ej;")
	public static final ClientPacket IGNORELIST_ADD = new ClientPacket(14, -1);

	@OriginalMember(owner = "client!ij", name = "s", descriptor = "Lclient!ej;")
	public static final ClientPacket RESUME_COUNTDIALOG = new ClientPacket(32, 4);

	@OriginalMember(owner = "client!rba", name = "s", descriptor = "Lclient!ej;")
	public static final ClientPacket OPNPC6 = new ClientPacket(68, 2);

	@OriginalMember(owner = "client!ht", name = "l", descriptor = "Lclient!ej;")
	public static final ClientPacket IF_BUTTON1 = new ClientPacket(23, 8);

	@OriginalMember(owner = "client!haa", name = "q", descriptor = "Lclient!ej;")
	public static final ClientPacket CLIENT_CHEAT = new ClientPacket(53, -1);

	@OriginalMember(owner = "client!ae", name = "b", descriptor = "Lclient!ej;")
	public static final ClientPacket IF_BUTTON9 = new ClientPacket(11, 8);

	@OriginalMember(owner = "client!op", name = "b", descriptor = "Lclient!ej;")
	public static final ClientPacket QUICKCHAT_PRIVATE = new ClientPacket(19, -1);

	@OriginalMember(owner = "client!iia", name = "h", descriptor = "Lclient!ej;")
	public static final ClientPacket OPNPC3 = new ClientPacket(5, 3);

	@OriginalMember(owner = "client!oc", name = "f", descriptor = "Lclient!ej;")
	public static final ClientPacket REFLECTION_CHECK_REPLY = new ClientPacket(77, -1);

	@OriginalMember(owner = "client!es", name = "n", descriptor = "Lclient!ej;")
	public static final ClientPacket FRIENDCHAT_KICKUSER = new ClientPacket(64, -1);

	@OriginalMember(owner = "client!gw", name = "G", descriptor = "Lclient!ej;")
	public static final ClientPacket OPLOC1 = new ClientPacket(27, 7);

	@OriginalMember(owner = "client!gb", name = "n", descriptor = "Lclient!ej;")
	public static final ClientPacket IF_BUTTON5 = new ClientPacket(17, 8);

	@OriginalMember(owner = "client!rfa", name = "b", descriptor = "Lclient!ej;")
	public static final ClientPacket MESSAGE_PUBLIC = new ClientPacket(41, -1);

	@OriginalMember(owner = "client!hn", name = "W", descriptor = "Lclient!ej;")
	public static final ClientPacket RESUME_P_STRINGDIALOG = new ClientPacket(1, -1);

	@OriginalMember(owner = "client!es", name = "c", descriptor = "Lclient!ej;")
	public static final ClientPacket FRIENDLIST_DEL = new ClientPacket(6, -1);

	@OriginalMember(owner = "client!aia", name = "N", descriptor = "Lclient!ej;")
	public static final ClientPacket RESUME_PAUSEBUTTON = new ClientPacket(2, 6);

	@OriginalMember(owner = "client!rfa", name = "g", descriptor = "Lclient!ej;")
	public static final ClientPacket OPLOC6 = new ClientPacket(46, 2);

	@OriginalMember(owner = "client!bea", name = "F", descriptor = "Lclient!ej;")
	public static final ClientPacket OPLOCT = new ClientPacket(54, 15);

	@OriginalMember(owner = "client!jca", name = "n", descriptor = "Lclient!ej;")
	public static final ClientPacket OPPLAYERT = new ClientPacket(48, 11);

	@OriginalMember(owner = "client!ah", name = "c", descriptor = "Lclient!ej;")
	public static final ClientPacket APCOORDT = new ClientPacket(40, 12);

	@OriginalMember(owner = "client!ht", name = "i", descriptor = "Lclient!ej;")
	public static final ClientPacket EVENT_MOUSE_CLICK = new ClientPacket(49, 6);

	@OriginalMember(owner = "client!wba", name = "L", descriptor = "Lclient!ej;")
	public static final ClientPacket FRIENDLIST_ADD = new ClientPacket(10, -1);
}

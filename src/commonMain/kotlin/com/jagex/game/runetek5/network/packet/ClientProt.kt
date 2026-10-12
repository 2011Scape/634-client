package com.jagex.game.runetek5.network.packet

import com.jagex.game.runetek5.ClientPacket
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

object ClientProt {
	@OriginalMember(owner = "client!mm", name = "x", descriptor = "Lclient!ej;")
	@JvmField
	val IF_DRAG_ONTO_IF: ClientPacket = ClientPacket(78, 16)

	@OriginalMember(owner = "client!rga", name = "B", descriptor = "Lclient!ej;")
	@JvmField
	val SCREEN_SIZE: ClientPacket = ClientPacket(7, 6)

	@OriginalMember(owner = "client!nba", name = "d", descriptor = "Lclient!ej;")
	@JvmField
	val TRANSMITVAR_VERIFYID: ClientPacket = ClientPacket(13, 2)

	@OriginalMember(owner = "client!od", name = "r", descriptor = "Lclient!ej;")
	@JvmField
	val PING: ClientPacket = ClientPacket(0, 0)

	@OriginalMember(owner = "client!ah", name = "e", descriptor = "Lclient!ej;")
	@JvmField
	val IF_ON_IF: ClientPacket = ClientPacket(72, 16)

	@OriginalMember(owner = "client!re", name = "p", descriptor = "Lclient!ej;")
	@JvmField
	val OPOBJ4: ClientPacket = ClientPacket(24, 7)

	@OriginalMember(owner = "client!qia", name = "F", descriptor = "Lclient!ej;")
	@JvmField
	val OPLOC2: ClientPacket = ClientPacket(36, 7)

	@OriginalMember(owner = "client!bp", name = "e", descriptor = "Lclient!ej;")
	@JvmField
	val OPOBJ5: ClientPacket = ClientPacket(26, 7)

	@OriginalMember(owner = "client!tca", name = "i", descriptor = "Lclient!ej;")
	@JvmField
	val IF_BUTTON8: ClientPacket = ClientPacket(60, 8)

	@OriginalMember(owner = "client!qda", name = "p", descriptor = "Lclient!ej;")
	@JvmField
	val OPPLAYER3: ClientPacket = ClientPacket(79, 3)

	@OriginalMember(owner = "client!kg", name = "h", descriptor = "Lclient!ej;")
	@JvmField
	val OPNPC5: ClientPacket = ClientPacket(65, 3)

	@OriginalMember(owner = "client!tr", name = "d", descriptor = "Lclient!ej;")
	@JvmField
	val OPPLAYER6: ClientPacket = ClientPacket(51, 3)

	@OriginalMember(owner = "client!hq", name = "e", descriptor = "Lclient!ej;")
	@JvmField
	val OPPLAYER2: ClientPacket = ClientPacket(12, 3)

	@OriginalMember(owner = "client!gh", name = "Rb", descriptor = "Lclient!ej;")
	@JvmField
	val OPOBJ3: ClientPacket = ClientPacket(45, 7)

	@OriginalMember(owner = "client!oea", name = "j", descriptor = "Lclient!ej;")
	@JvmField
	val REPORT_ABUSE: ClientPacket = ClientPacket(66, -1)

	@OriginalMember(owner = "client!sda", name = "j", descriptor = "Lclient!ej;")
	@JvmField
	val IF_BUTTON6: ClientPacket = ClientPacket(39, 8)

	@OriginalMember(owner = "client!qi", name = "b", descriptor = "Lclient!ej;")
	@JvmField
	val OPOBJ2: ClientPacket = ClientPacket(16, 7)

	@OriginalMember(owner = "client!er", name = "p", descriptor = "Lclient!ej;")
	@JvmField
	val IF_BUTTON3: ClientPacket = ClientPacket(9, 8)

	@OriginalMember(owner = "client!pq", name = "f", descriptor = "Lclient!ej;")
	@JvmField
	val OPPLAYER8: ClientPacket = ClientPacket(18, 3)

	@OriginalMember(owner = "client!tb", name = "c", descriptor = "Lclient!ej;")
	@JvmField
	val aClass94_81: ClientPacket = ClientPacket(71, 2)

	@OriginalMember(owner = "client!tu", name = "a", descriptor = "Lclient!ej;")
	@JvmField
	val OPLOC3: ClientPacket = ClientPacket(80, 7)

	@OriginalMember(owner = "client!kb", name = "g", descriptor = "Lclient!ej;")
	@JvmField
	val OPOBJ6: ClientPacket = ClientPacket(37, 2)

	@OriginalMember(owner = "client!ug", name = "H", descriptor = "Lclient!ej;")
	@JvmField
	val EVENT_MOUSE_MOVE: ClientPacket = ClientPacket(83, -1)

	@OriginalMember(owner = "client!ha", name = "k", descriptor = "Lclient!ej;")
	@JvmField
	val IGNORELIST_DEL: ClientPacket = ClientPacket(73, -1)

	@OriginalMember(owner = "client!sl", name = "db", descriptor = "Lclient!ej;")
	@JvmField
	val SEND_PRIVATE_MESSAGE: ClientPacket = ClientPacket(20, -1)

	@OriginalMember(owner = "client!bk", name = "s", descriptor = "Lclient!ej;")
	@JvmField
	val WRITE_PING: ClientPacket = ClientPacket(21, 2)

	@OriginalMember(owner = "client!qr", name = "e", descriptor = "Lclient!ej;")
	@JvmField
	val OPLOC5: ClientPacket = ClientPacket(38, 7)

	@OriginalMember(owner = "client!gh", name = "Lb", descriptor = "Lclient!ej;")
	@JvmField
	val aClass94_37: ClientPacket = ClientPacket(84, -1)

	@OriginalMember(owner = "client!pj", name = "K", descriptor = "Lclient!ej;")
	@JvmField
	val OPEN_URL: ClientPacket = ClientPacket(67, -1)

	@OriginalMember(owner = "client!qg", name = "f", descriptor = "Lclient!ej;")
	@JvmField
	val MOVE_GAMECLICK: ClientPacket = ClientPacket(35, 5)

	@OriginalMember(owner = "client!pi", name = "A", descriptor = "Lclient!ej;")
	@JvmField
	val CHAT_TYPE: ClientPacket = ClientPacket(31, 1)

	@OriginalMember(owner = "client!naa", name = "Y", descriptor = "Lclient!ej;")
	@JvmField
	val REQUEST_WORLD_LIST: ClientPacket = ClientPacket(30, 4)

	@OriginalMember(owner = "client!lr", name = "J", descriptor = "Lclient!ej;")
	@JvmField
	val OPPLAYER7: ClientPacket = ClientPacket(57, 3)

	@OriginalMember(owner = "client!mm", name = "s", descriptor = "Lclient!ej;")
	@JvmField
	val OPPLAYER1: ClientPacket = ClientPacket(25, 3)

	@OriginalMember(owner = "client!nba", name = "h", descriptor = "Lclient!ej;")
	@JvmField
	val IF_BUTTON4: ClientPacket = ClientPacket(15, 8)

	@OriginalMember(owner = "client!co", name = "fb", descriptor = "Lclient!ej;")
	@JvmField
	val OPNPC4: ClientPacket = ClientPacket(62, 3)

	@OriginalMember(owner = "client!nd", name = "d", descriptor = "Lclient!ej;")
	@JvmField
	val FRIEND_SETRANK: ClientPacket = ClientPacket(74, -1)

	@OriginalMember(owner = "client!jg", name = "y", descriptor = "Lclient!ej;")
	@JvmField
	val OPNPC1: ClientPacket = ClientPacket(63, 3)

	@OriginalMember(owner = "client!rca", name = "a", descriptor = "Lclient!ej;")
	@JvmField
	val IF_BUTTON2: ClientPacket = ClientPacket(59, 8)

	@OriginalMember(owner = "client!ah", name = "a", descriptor = "Lclient!ej;")
	@JvmField
	val OPOBJT: ClientPacket = ClientPacket(34, 15)

	@OriginalMember(owner = "client!cda", name = "a", descriptor = "Lclient!ej;")
	@JvmField
	val MOVE_MINIMAPCLICK: ClientPacket = ClientPacket(82, 18)

	@OriginalMember(owner = "client!de", name = "e", descriptor = "Lclient!ej;")
	@JvmField
	val aClass94_23: ClientPacket = ClientPacket(47, 4)

	@OriginalMember(owner = "client!eb", name = "n", descriptor = "Lclient!ej;")
	@JvmField
	val RESUME_P_NAMEDIALOG: ClientPacket = ClientPacket(43, -1)

	@OriginalMember(owner = "client!ek", name = "g", descriptor = "Lclient!ej;")
	@JvmField
	val EVENT_APPLET_FOCUS: ClientPacket = ClientPacket(8, 1)

	@OriginalMember(owner = "client!qt", name = "m", descriptor = "Lclient!ej;")
	@JvmField
	val aClass94_89: ClientPacket = ClientPacket(76, 4)

	@OriginalMember(owner = "client!vea", name = "a", descriptor = "Lclient!ej;")
	@JvmField
	val IF_BUTTON10: ClientPacket = ClientPacket(42, 8)

	@OriginalMember(owner = "client!jh", name = "z", descriptor = "Lclient!ej;")
	@JvmField
	val OPPLAYER5: ClientPacket = ClientPacket(81, 3)

	@OriginalMember(owner = "client!ij", name = "u", descriptor = "Lclient!ej;")
	@JvmField
	val CHAT_SETFILTER: ClientPacket = ClientPacket(75, 3)

	@OriginalMember(owner = "client!be", name = "q", descriptor = "Lclient!ej;")
	@JvmField
	val CLOSE_MODAL: ClientPacket = ClientPacket(70, 0)

	@OriginalMember(owner = "client!fc", name = "R", descriptor = "Lclient!ej;")
	@JvmField
	val OPOBJ1: ClientPacket = ClientPacket(22, 7)

	@OriginalMember(owner = "client!eaa", name = "j", descriptor = "Lclient!ej;")
	@JvmField
	val OPNPC2: ClientPacket = ClientPacket(29, 3)

	@OriginalMember(owner = "client!jca", name = "l", descriptor = "Lclient!ej;")
	@JvmField
	val OPPLAYER4: ClientPacket = ClientPacket(44, 3)

	@OriginalMember(owner = "client!eb", name = "l", descriptor = "Lclient!ej;")
	@JvmField
	val MAP_BUILD_COMPLETE: ClientPacket = ClientPacket(4, 0)

	@OriginalMember(owner = "client!vea", name = "e", descriptor = "Lclient!ej;")
	@JvmField
	val EVENT_CAMERA_POSITION: ClientPacket = ClientPacket(55, 4)

	@OriginalMember(owner = "client!in", name = "r", descriptor = "Lclient!ej;")
	@JvmField
	val QUICKCHAT_PUBLIC: ClientPacket = ClientPacket(3, -1)

	@OriginalMember(owner = "client!gq", name = "D", descriptor = "Lclient!ej;")
	@JvmField
	val OPLOC4: ClientPacket = ClientPacket(56, 7)

	@OriginalMember(owner = "client!ew", name = "h", descriptor = "Lclient!ej;")
	@JvmField
	val IF_BUTTON7: ClientPacket = ClientPacket(33, 8)

	@OriginalMember(owner = "client!nq", name = "d", descriptor = "Lclient!ej;")
	@JvmField
	val OPNPCT: ClientPacket = ClientPacket(61, 11)

	@OriginalMember(owner = "client!jk", name = "w", descriptor = "Lclient!ej;")
	@JvmField
	val IGNORELIST_ADD: ClientPacket = ClientPacket(14, -1)

	@OriginalMember(owner = "client!ij", name = "s", descriptor = "Lclient!ej;")
	@JvmField
	val RESUME_COUNTDIALOG: ClientPacket = ClientPacket(32, 4)

	@OriginalMember(owner = "client!rba", name = "s", descriptor = "Lclient!ej;")
	@JvmField
	val OPNPC6: ClientPacket = ClientPacket(68, 2)

	@OriginalMember(owner = "client!ht", name = "l", descriptor = "Lclient!ej;")
	@JvmField
	val IF_BUTTON1: ClientPacket = ClientPacket(23, 8)

	@OriginalMember(owner = "client!haa", name = "q", descriptor = "Lclient!ej;")
	@JvmField
	val CLIENT_CHEAT: ClientPacket = ClientPacket(53, -1)

	@OriginalMember(owner = "client!ae", name = "b", descriptor = "Lclient!ej;")
	@JvmField
	val IF_BUTTON9: ClientPacket = ClientPacket(11, 8)

	@OriginalMember(owner = "client!op", name = "b", descriptor = "Lclient!ej;")
	@JvmField
	val QUICKCHAT_PRIVATE: ClientPacket = ClientPacket(19, -1)

	@OriginalMember(owner = "client!iia", name = "h", descriptor = "Lclient!ej;")
	@JvmField
	val OPNPC3: ClientPacket = ClientPacket(5, 3)

	@OriginalMember(owner = "client!oc", name = "f", descriptor = "Lclient!ej;")
	@JvmField
	val REFLECTION_CHECK_REPLY: ClientPacket = ClientPacket(77, -1)

	@OriginalMember(owner = "client!es", name = "n", descriptor = "Lclient!ej;")
	@JvmField
	val FRIENDCHAT_KICKUSER: ClientPacket = ClientPacket(64, -1)

	@OriginalMember(owner = "client!gw", name = "G", descriptor = "Lclient!ej;")
	@JvmField
	val OPLOC1: ClientPacket = ClientPacket(27, 7)

	@OriginalMember(owner = "client!gb", name = "n", descriptor = "Lclient!ej;")
	@JvmField
	val IF_BUTTON5: ClientPacket = ClientPacket(17, 8)

	@OriginalMember(owner = "client!rfa", name = "b", descriptor = "Lclient!ej;")
	@JvmField
	val MESSAGE_PUBLIC: ClientPacket = ClientPacket(41, -1)

	@OriginalMember(owner = "client!hn", name = "W", descriptor = "Lclient!ej;")
	@JvmField
	val RESUME_P_STRINGDIALOG: ClientPacket = ClientPacket(1, -1)

	@OriginalMember(owner = "client!es", name = "c", descriptor = "Lclient!ej;")
	@JvmField
	val FRIENDLIST_DEL: ClientPacket = ClientPacket(6, -1)

	@OriginalMember(owner = "client!aia", name = "N", descriptor = "Lclient!ej;")
	@JvmField
	val RESUME_PAUSEBUTTON: ClientPacket = ClientPacket(2, 6)

	@OriginalMember(owner = "client!rfa", name = "g", descriptor = "Lclient!ej;")
	@JvmField
	val OPLOC6: ClientPacket = ClientPacket(46, 2)

	@OriginalMember(owner = "client!bea", name = "F", descriptor = "Lclient!ej;")
	@JvmField
	val OPLOCT: ClientPacket = ClientPacket(54, 15)

	@OriginalMember(owner = "client!jca", name = "n", descriptor = "Lclient!ej;")
	@JvmField
	val OPPLAYERT: ClientPacket = ClientPacket(48, 11)

	@OriginalMember(owner = "client!ah", name = "c", descriptor = "Lclient!ej;")
	@JvmField
	val APCOORDT: ClientPacket = ClientPacket(40, 12)

	@OriginalMember(owner = "client!ht", name = "i", descriptor = "Lclient!ej;")
	@JvmField
	val EVENT_MOUSE_CLICK: ClientPacket = ClientPacket(49, 6)

	@OriginalMember(owner = "client!wba", name = "L", descriptor = "Lclient!ej;")
	@JvmField
	val FRIENDLIST_ADD: ClientPacket = ClientPacket(10, -1)
}

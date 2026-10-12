package com.jagex.game.runetek5.network.packet

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.MusicTrackProt
import com.jagex.game.runetek5.UpdateZoneFullFollowsProt
import com.jagex.game.runetek5.graphics.shader.ShaderVariableType
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@OriginalClass("client!mu")
class ServerProt {

	@OriginalMember(owner = "client!mu", name = "d", descriptor = "I")
	@JvmField
	val size: Int

	@OriginalMember(owner = "client!mu", name = "b", descriptor = "I")
	private val opcode: Int

	@OriginalMember(owner = "client!mu", name = "<init>", descriptor = "(II)V")
	constructor(@OriginalArg(0) opcode: Int, @OriginalArg(1) size: Int) {
		this.size = size
		this.opcode = opcode
	}

	@OriginalMember(owner = "client!mu", name = "a", descriptor = "(B)I")
	fun getOpcode(): Int {
		return this.opcode
	}

	@OriginalMember(owner = "client!mu", name = "toString", descriptor = "()Ljava/lang/String;")
	override fun toString(): String {
		throw IllegalStateException()
	}

	companion object {
		@OriginalMember(owner = "client!haa", name = "hb", descriptor = "Lclient!mu;")
		@JvmField
		val CAM_MOVETO: ServerProt = ServerProt(119, 6)

		@OriginalMember(owner = "client!ek", name = "i", descriptor = "Lclient!mu;")
		@JvmField
		val IF_SETGRAPHIC: ServerProt = ServerProt(75, 6)

		@OriginalMember(owner = "client!aw", name = "Q", descriptor = "Lclient!mu;")
		@JvmField
		val MESSAGE_PRIVATE_ECHO: ServerProt = ServerProt(61, -1)

		@OriginalMember(owner = "client!hi", name = "l", descriptor = "Lclient!mu;")
		@JvmField
		val MAP_PROJANIM: ServerProt = ServerProt(15, 16)

		@OriginalMember(owner = "client!fca", name = "v", descriptor = "Lclient!mu;")
		@JvmField
		val IF_SETCOLOR: ServerProt = ServerProt(32, 6)

		@OriginalMember(owner = "client!jb", name = "R", descriptor = "Lclient!mu;")
		@JvmField
		val IF_SETTEXTFONT: ServerProt = ServerProt(107, 6)

		@OriginalMember(owner = "client!f", name = "c", descriptor = "Lclient!mu;")
		@JvmField
		val FRIENDS_CHAT_CHANNEL: ServerProt = ServerProt(13, -2)

		@OriginalMember(owner = "client!hu", name = "c", descriptor = "Lclient!mu;")
		@JvmField
		val UPDATE_INV_STOP_TRANSMIT: ServerProt = ServerProt(59, 3)

		@OriginalMember(owner = "client!ef", name = "b", descriptor = "Lclient!mu;")
		@JvmField
		val SET_DRAW_ORDER: ServerProt = ServerProt(87, 1)

		@OriginalMember(owner = "client!ga", name = "p", descriptor = "Lclient!mu;")
		@JvmField
		val VORBIS_SPEECH_SOUND: ServerProt = ServerProt(11, 6)

		@OriginalMember(owner = "client!io", name = "a", descriptor = "Lclient!mu;")
		@JvmField
		val SOUND_AREA: ServerProt = ServerProt(66, 6)

		@OriginalMember(owner = "client!hi", name = "a", descriptor = "Lclient!mu;")
		@JvmField
		val DYNAMIC_MAP_REGION: ServerProt = ServerProt(70, -2)

		@OriginalMember(owner = "client!ef", name = "e", descriptor = "Lclient!mu;")
		@JvmField
		val VORBIS_PRELOAD_SOUND: ServerProt = ServerProt(67, 3)

		@OriginalMember(owner = "client!k", name = "d", descriptor = "Lclient!mu;")
		@JvmField
		val CLIENT_SETVARCSTR_LARGE: ServerProt = ServerProt(53, -2)

		@OriginalMember(owner = "client!hg", name = "p", descriptor = "Lclient!mu;")
		@JvmField
		val RUN_ENERGY: ServerProt = ServerProt(4, 1)

		@OriginalMember(owner = "client!k", name = "g", descriptor = "Lclient!mu;")
		@JvmField
		val MESSAGE_PRIVATE: ServerProt = ServerProt(78, -1)

		@OriginalMember(owner = "client!ufa", name = "a", descriptor = "Lclient!mu;")
		@JvmField
		val UPDATE_REBOOT_TIMER: ServerProt = ServerProt(56, 2)

		@OriginalMember(owner = "client!pt", name = "a", descriptor = "Lclient!mu;")
		@JvmField
		val UPDATE_IGNORE_LIST: ServerProt = ServerProt(120, -2)

		@OriginalMember(owner = "client!wp", name = "a", descriptor = "Lclient!mu;")
		@JvmField
		val IF_SETPLAYERMODEL: ServerProt = ServerProt(114, 4)

		@OriginalMember(owner = "client!pm", name = "c", descriptor = "Lclient!mu;")
		@JvmField
		val LOGOUT_LOBBY: ServerProt = ServerProt(60, 0)

		@OriginalMember(owner = "client!pga", name = "f", descriptor = "Lclient!mu;")
		@JvmField
		val MESSAGE_QUICKCHAT_FRIENDS_CHAT: ServerProt = ServerProt(111, -1)

		@OriginalMember(owner = "client!oc", name = "d", descriptor = "Lclient!mu;")
		@JvmField
		val IF_SETSCROLLPOS: ServerProt = ServerProt(81, 6)

		@OriginalMember(owner = "client!ns", name = "c", descriptor = "Lclient!mu;")
		@JvmField
		val SPOT_ANIM: ServerProt = ServerProt(84, 7)

		@OriginalMember(owner = "client!bn", name = "a", descriptor = "Lclient!mu;")
		@JvmField
		val CAM_LOOKAT: ServerProt = ServerProt(25, 6)

		@OriginalMember(owner = "client!si", name = "b", descriptor = "Lclient!mu;")
		@JvmField
		val IDENTIFY_HOST_NAME: ServerProt = ServerProt(49, 4)

		@OriginalMember(owner = "client!rfa", name = "i", descriptor = "Lclient!mu;")
		@JvmField
		val MESSAGE_QUICKCHAT_PRIVATE: ServerProt = ServerProt(94, -1)

		@OriginalMember(owner = "client!wf", name = "b", descriptor = "Lclient!mu;")
		@JvmField
		val VARP_LARGE: ServerProt = ServerProt(44, 6)

		@OriginalMember(owner = "client!nm", name = "b", descriptor = "Lclient!mu;")
		@JvmField
		val GAME_MESSAGE: ServerProt = ServerProt(52, -1)

		@OriginalMember(owner = "client!bn", name = "h", descriptor = "Lclient!mu;")
		@JvmField
		val MESSAGE_QUICKCHAT_PRIVATE_ECHO: ServerProt = ServerProt(99, -1)

		@OriginalMember(owner = "client!pt", name = "e", descriptor = "Lclient!mu;")
		@JvmField
		val CAM_RESET: ServerProt = ServerProt(80, 0)

		@OriginalMember(owner = "client!nb", name = "e", descriptor = "Lclient!mu;")
		@JvmField
		val SET_CURSOR: ServerProt = ServerProt(50, -1)

		@OriginalMember(owner = "client!ns", name = "f", descriptor = "Lclient!mu;")
		@JvmField
		val UPDATE_INV_FULL: ServerProt = ServerProt(20, -2)

		@OriginalMember(owner = "client!nq", name = "a", descriptor = "Lclient!mu;")
		@JvmField
		val MESSAGE_PUBLIC: ServerProt = ServerProt(38, -1)

		@OriginalMember(owner = "client!mr", name = "d", descriptor = "Lclient!mu;")
		@JvmField
		val IF_SETANIM: ServerProt = ServerProt(24, 6)

		@OriginalMember(owner = "client!maa", name = "b", descriptor = "Lclient!mu;")
		@JvmField
		val CHAT_FILTER_SETTINGS_PRIVATECHAT: ServerProt = ServerProt(113, 1)

		@OriginalMember(owner = "client!bja", name = "g", descriptor = "Lclient!mu;")
		@JvmField
		val UPDATE_UID192: ServerProt = ServerProt(74, 28)

		@OriginalMember(owner = "client!wc", name = "a", descriptor = "Lclient!mu;")
		@JvmField
		val SET_VORBIS_SOUND: ServerProt = ServerProt(1, 2)

		@OriginalMember(owner = "client!lj", name = "B", descriptor = "Lclient!mu;")
		@JvmField
		val MESSAGE_PLAYER_GROUP: ServerProt = ServerProt(30, -1)

		@OriginalMember(owner = "client!ep", name = "r", descriptor = "Lclient!mu;")
		@JvmField
		val FRIENDLIST_LOADED: ServerProt = ServerProt(18, 0)

		@OriginalMember(owner = "client!am", name = "b", descriptor = "Lclient!mu;")
		@JvmField
		val DEBUG_SERVER_TRIGGERS: ServerProt = ServerProt(40, -1)

		@OriginalMember(owner = "client!hia", name = "a", descriptor = "Lclient!mu;")
		@JvmField
		val IF_SETPLAYERHEAD_IGNOREWORN: ServerProt = ServerProt(95, 10)

		@OriginalMember(owner = "client!ep", name = "s", descriptor = "Lclient!mu;")
		@JvmField
		val IF_SETTEXT: ServerProt = ServerProt(10, -2)

		@OriginalMember(owner = "client!hia", name = "c", descriptor = "Lclient!mu;")
		@JvmField
		val REGION: ServerProt = ServerProt(19, -2)

		@OriginalMember(owner = "client!gi", name = "a", descriptor = "Lclient!mu;")
		@JvmField
		val IF_RESETTARGETPARAM: ServerProt = ServerProt(110, 10)

		@OriginalMember(owner = "client!hea", name = "e", descriptor = "Lclient!mu;")
		@JvmField
		val MAP_PROJANIM_HALFSQ: ServerProt = ServerProt(51, 17)

		@OriginalMember(owner = "client!fk", name = "o", descriptor = "Lclient!mu;")
		@JvmField
		val SHOW_FACE_HERE: ServerProt = ServerProt(117, 1)

		@OriginalMember(owner = "client!ve", name = "q", descriptor = "Lclient!mu;")
		@JvmField
		val OBJECT_PREFETCH: ServerProt = ServerProt(79, 3)

		@OriginalMember(owner = "client!up", name = "L", descriptor = "Lclient!mu;")
		@JvmField
		val REFLECTION_CHECK: ServerProt = ServerProt(93, -2)

		@OriginalMember(owner = "client!ok", name = "k", descriptor = "Lclient!mu;")
		@JvmField
		val VARBIT_LARGE: ServerProt = ServerProt(68, 6)

		@OriginalMember(owner = "client!ria", name = "b", descriptor = "Lclient!mu;")
		@JvmField
		val NPC_UPDATE: ServerProt = ServerProt(118, -2)

		@OriginalMember(owner = "client!lk", name = "B", descriptor = "Lclient!mu;")
		@JvmField
		val ADD_IGNORE: ServerProt = ServerProt(9, -1)

		@OriginalMember(owner = "client!wca", name = "t", descriptor = "Lclient!mu;")
		@JvmField
		val CAM_SMOOTHRESET: ServerProt = ServerProt(89, 0)

		@OriginalMember(owner = "client!jt", name = "l", descriptor = "Lclient!mu;")
		@JvmField
		val IF_MOVESUB: ServerProt = ServerProt(39, 8)

		@OriginalMember(owner = "client!bk", name = "x", descriptor = "Lclient!mu;")
		@JvmField
		val PLAYER_UPDATE: ServerProt = ServerProt(105, -2)

		@OriginalMember(owner = "client!tu", name = "d", descriptor = "Lclient!mu;")
		@JvmField
		val CAM_FORCEANGLE: ServerProt = ServerProt(43, 4)

		@OriginalMember(owner = "client!qu", name = "M", descriptor = "Lclient!mu;")
		@JvmField
		val IF_OPENTOP: ServerProt = ServerProt(102, 3)

		@OriginalMember(owner = "client!qf", name = "e", descriptor = "Lclient!mu;")
		@JvmField
		val UPDATE_STAT: ServerProt = ServerProt(47, 6)

		@OriginalMember(owner = "client!gv", name = "e", descriptor = "Lclient!mu;")
		@JvmField
		val UPDATE_SITESETTINGS_COOKIE: ServerProt = ServerProt(42, -1)

		@OriginalMember(owner = "client!re", name = "t", descriptor = "Lclient!mu;")
		@JvmField
		val RESET_ALL_ANIMATIONS: ServerProt = ServerProt(12, 0)

		@OriginalMember(owner = "client!sfa", name = "c", descriptor = "Lclient!mu;")
		@JvmField
		val GROUND_ITEM_COUNT: ServerProt = ServerProt(71, 7)

		@OriginalMember(owner = "client!ub", name = "a", descriptor = "Lclient!mu;")
		@JvmField
		val UPDATE_ZONE_PARTIAL_ENCLOSED: ServerProt = ServerProt(48, -2)

		@OriginalMember(owner = "client!uaa", name = "i", descriptor = "Lclient!mu;")
		@JvmField
		val SET_SONG_VOLUME: ServerProt = ServerProt(6, 2)

		@OriginalMember(owner = "client!vca", name = "w", descriptor = "Lclient!mu;")
		@JvmField
		val SPOT_ANIM_SPECIFIC: ServerProt = ServerProt(26, 11)

		@OriginalMember(owner = "client!qu", name = "J", descriptor = "Lclient!mu;")
		@JvmField
		val VARBIT_SMALL: ServerProt = ServerProt(73, 3)

		@OriginalMember(owner = "client!sl", name = "J", descriptor = "Lclient!mu;")
		@JvmField
		val BLOCK_MINIMAP_STATE: ServerProt = ServerProt(104, 1)

		@OriginalMember(owner = "client!dg", name = "q", descriptor = "Lclient!mu;")
		@JvmField
		val MIDI_SONG_LOCATION: ServerProt = ServerProt(5, 6)

		@OriginalMember(owner = "client!qia", name = "B", descriptor = "Lclient!mu;")
		@JvmField
		val GROUND_ITEM_REVEAL: ServerProt = ServerProt(109, 7)

		@OriginalMember(owner = "client!vu", name = "p", descriptor = "Lclient!mu;")
		@JvmField
		val TILE_MESSAGE: ServerProt = ServerProt(46, -1)

		@OriginalMember(owner = "client!wd", name = "K", descriptor = "Lclient!mu;")
		@JvmField
		val OPEN_URL: ServerProt = ServerProt(100, -1)

		@OriginalMember(owner = "client!tea", name = "l", descriptor = "Lclient!mu;")
		@JvmField
		val MESSAGE_QUICKCHAT_PLAYER_GROUP: ServerProt = ServerProt(21, -1)

		@OriginalMember(owner = "client!rw", name = "l", descriptor = "Lclient!mu;")
		@JvmField
		val VORBIS_SOUND: ServerProt = ServerProt(106, 8)

		@OriginalMember(owner = "client!qia", name = "E", descriptor = "Lclient!mu;")
		@JvmField
		val TRIGGER_ONDIALOGABORT: ServerProt = ServerProt(72, 0)

		@OriginalMember(owner = "client!eq", name = "o", descriptor = "Lclient!mu;")
		@JvmField
		val CLIENT_SETVARC_SMALL: ServerProt = ServerProt(3, 3)

		@OriginalMember(owner = "client!cga", name = "d", descriptor = "Lclient!mu;")
		@JvmField
		val REMOVE_GROUND_ITEM: ServerProt = ServerProt(27, 3)

		@OriginalMember(owner = "client!uba", name = "u", descriptor = "Lclient!mu;")
		@JvmField
		val IF_SETNPCHEAD: ServerProt = ServerProt(86, 6)

		@OriginalMember(owner = "client!fia", name = "h", descriptor = "Lclient!mu;")
		@JvmField
		val IF_SETANGLE: ServerProt = ServerProt(88, 10)

		@OriginalMember(owner = "client!afa", name = "j", descriptor = "Lclient!mu;")
		@JvmField
		val IF_SETITEM: ServerProt = ServerProt(90, 10)

		@OriginalMember(owner = "client!bk", name = "y", descriptor = "Lclient!mu;")
		@JvmField
		val OUTDATED_INTERFACE_PULSE_EVENT: ServerProt = ServerProt(96, 8)

		@OriginalMember(owner = "client!ut", name = "b", descriptor = "Lclient!mu;")
		@JvmField
		val IF_SETPOSITION: ServerProt = ServerProt(45, 8)

		@OriginalMember(owner = "client!pp", name = "y", descriptor = "Lclient!mu;")
		@JvmField
		val OBJ_ANIM: ServerProt = ServerProt(17, 4)

		@OriginalMember(owner = "client!qa", name = "c", descriptor = "Lclient!mu;")
		@JvmField
		val CLIENT_SETVARCSTR_SMALL: ServerProt = ServerProt(92, -1)

		@OriginalMember(owner = "client!pn", name = "d", descriptor = "Lclient!mu;")
		@JvmField
		val IF_SETPLAYERHEAD: ServerProt = ServerProt(77, 4)

		@OriginalMember(owner = "client!pba", name = "I", descriptor = "Lclient!mu;")
		@JvmField
		val PLAYER_WEIGHT: ServerProt = ServerProt(91, 2)

		@OriginalMember(owner = "client!pb", name = "o", descriptor = "Lclient!mu;")
		@JvmField
		val HINT_ARROW: ServerProt = ServerProt(69, 12)

		@OriginalMember(owner = "client!ql", name = "i", descriptor = "Lclient!mu;")
		@JvmField
		val CREATE_OBJECT: ServerProt = ServerProt(82, 4)

		@OriginalMember(owner = "client!kba", name = "z", descriptor = "Lclient!mu;")
		@JvmField
		val IF_SETTARGETPARAM: ServerProt = ServerProt(34, 12)

		@OriginalMember(owner = "client!or", name = "q", descriptor = "Lclient!mu;")
		@JvmField
		val CLIENT_SETVARC_LARGE: ServerProt = ServerProt(16, 6)

		@OriginalMember(owner = "client!pb", name = "p", descriptor = "Lclient!mu;")
		@JvmField
		val MINIMAP_FLAG: ServerProt = ServerProt(64, 2)

		@OriginalMember(owner = "client!kn", name = "b", descriptor = "Lclient!mu;")
		@JvmField
		val UPDATE_DOB: ServerProt = ServerProt(54, 4)

		@OriginalMember(owner = "client!pr", name = "p", descriptor = "Lclient!mu;")
		@JvmField
		val ANIMATE_NPC: ServerProt = ServerProt(115, 11)

		@OriginalMember(owner = "client!qj", name = "g", descriptor = "Lclient!mu;")
		@JvmField
		val WORLD_LIST: ServerProt = ServerProt(98, -2)

		@OriginalMember(owner = "client!pda", name = "L", descriptor = "Lclient!mu;")
		@JvmField
		val UPDATE_INV_PARTIAL: ServerProt = ServerProt(28, -2)

		@OriginalMember(owner = "client!pw", name = "H", descriptor = "Lclient!mu;")
		@JvmField
		val IF_SETMODEL: ServerProt = ServerProt(23, 6)

		@OriginalMember(owner = "client!kl", name = "w", descriptor = "Lclient!mu;")
		@JvmField
		val SOUND_SYNTH: ServerProt = ServerProt(103, 8)

		@OriginalMember(owner = "client!ko", name = "j", descriptor = "Lclient!mu;")
		@JvmField
		val IF_OPENSUB: ServerProt = ServerProt(37, 7)

		@OriginalMember(owner = "client!mt", name = "h", descriptor = "Lclient!mu;")
		@JvmField
		val RUN_CS2_SCRIPT: ServerProt = ServerProt(8, -2)

		@OriginalMember(owner = "client!el", name = "b", descriptor = "Lclient!mu;")
		@JvmField
		val UPDATE_FRIENDCHAT_CHANNEL_SINGLEUSER: ServerProt = ServerProt(31, -1)

		@OriginalMember(owner = "client!wda", name = "i", descriptor = "Lclient!mu;")
		@JvmField
		val LOGOUT_FULL: ServerProt = ServerProt(55, 0)

		@OriginalMember(owner = "client!cq", name = "u", descriptor = "Lclient!mu;")
		@JvmField
		val MUSIC_EFFECT: ServerProt = ServerProt(116, 6)

		@OriginalMember(owner = "client!su", name = "b", descriptor = "Lclient!mu;")
		@JvmField
		val IF_SETHIDE: ServerProt = ServerProt(108, 5)

		@OriginalMember(owner = "client!po", name = "s", descriptor = "Lclient!mu;")
		@JvmField
		val IF_SETPLAYERMODEL_OTHER: ServerProt = ServerProt(57, 10)

		@OriginalMember(owner = "client!pg", name = "f", descriptor = "Lclient!mu;")
		@JvmField
		val PLAYER_OPTION: ServerProt = ServerProt(0, -1)

		@OriginalMember(owner = "client!vfa", name = "h", descriptor = "Lclient!mu;")
		@JvmField
		val STOP_VORBIS_STREAM: ServerProt = ServerProt(14, 2)

		@OriginalMember(owner = "client!dga", name = "O", descriptor = "Lclient!mu;")
		@JvmField
		val CHAT_FILTER_SETTINGS: ServerProt = ServerProt(83, 2)

		@OriginalMember(owner = "client!ui", name = "h", descriptor = "Lclient!mu;")
		@JvmField
		val CREATE_GROUND_ITEM: ServerProt = ServerProt(63, 5)

		@OriginalMember(owner = "client!wm", name = "w", descriptor = "Lclient!mu;")
		@JvmField
		val SET_CAMERA_TARGET: ServerProt = ServerProt(62, 4)

		@OriginalMember(owner = "client!w", name = "b", descriptor = "Lclient!mu;")
		@JvmField
		val OBJ_ANIM_SPECIFIC: ServerProt = ServerProt(29, 7)

		@OriginalMember(owner = "client!hk", name = "c", descriptor = "Lclient!mu;")
		@JvmField
		val MESSAGE_FRIENDS_CHAT: ServerProt = ServerProt(65, -1)

		@OriginalMember(owner = "client!fe", name = "c", descriptor = "Lclient!mu;")
		@JvmField
		val CLEAR_VARPS: ServerProt = ServerProt(22, 0)

		@OriginalMember(owner = "client!bu", name = "r", descriptor = "Lclient!mu;")
		@JvmField
		val UPDATE_GE_SLOT: ServerProt = ServerProt(101, 20)

		@OriginalMember(owner = "client!jp", name = "a", descriptor = "Lclient!mu;")
		@JvmField
		val RESET_SOUNDS: ServerProt = ServerProt(35, 0)

		@OriginalMember(owner = "client!ui", name = "e", descriptor = "Lclient!mu;")
		@JvmField
		val IF_CLOSESUB: ServerProt = ServerProt(112, 4)

		@OriginalMember(owner = "client!ou", name = "v", descriptor = "Lclient!mu;")
		@JvmField
		val DESTROY_OBJECT: ServerProt = ServerProt(58, 2)

		@OriginalMember(owner = "client!dba", name = "t", descriptor = "Lclient!mu;")
		@JvmField
		val UPDATE_ZONE_PARTIAL_FOLLOWS: ServerProt = ServerProt(41, 3)

		@OriginalMember(owner = "client!wl", name = "s", descriptor = "Lclient!mu;")
		@JvmField
		val PING: ServerProt = ServerProt(97, 0)

		@OriginalMember(owner = "client!kk", name = "h", descriptor = "Lclient!mu;")
		@JvmField
		val CAM_SHAKE: ServerProt = ServerProt(76, 6)

		@OriginalMember(owner = "client!su", name = "j", descriptor = "Lclient!mu;")
		@JvmField
		val VARP_SMALL: ServerProt = ServerProt(7, 3)

		@OriginalMember(owner = "client!bu", name = "s", descriptor = "Lclient!mu;")
		@JvmField
		val FRIEND_STATUS: ServerProt = ServerProt(36, -2)

		@OriginalMember(owner = "client!qba", name = "b", descriptor = "(I)[Lclient!mu;")
		@JvmStatic
		fun serverProtValues(): Array<ServerProt?> {
			return arrayOf<ServerProt?>(ServerProt.PLAYER_OPTION, ServerProt.SET_VORBIS_SOUND, ShaderVariableType.IF_SETVIDEO, ServerProt.CLIENT_SETVARC_SMALL, ServerProt.RUN_ENERGY, ServerProt.MIDI_SONG_LOCATION, ServerProt.SET_SONG_VOLUME, ServerProt.VARP_SMALL, ServerProt.RUN_CS2_SCRIPT, ServerProt.ADD_IGNORE, ServerProt.IF_SETTEXT, ServerProt.VORBIS_SPEECH_SOUND, ServerProt.RESET_ALL_ANIMATIONS, ServerProt.FRIENDS_CHAT_CHANNEL, ServerProt.STOP_VORBIS_STREAM, ServerProt.MAP_PROJANIM, ServerProt.CLIENT_SETVARC_LARGE, ServerProt.OBJ_ANIM, ServerProt.FRIENDLIST_LOADED, ServerProt.REGION, ServerProt.UPDATE_INV_FULL, ServerProt.MESSAGE_QUICKCHAT_PLAYER_GROUP, ServerProt.CLEAR_VARPS, ServerProt.IF_SETMODEL, ServerProt.IF_SETANIM, ServerProt.CAM_LOOKAT, ServerProt.SPOT_ANIM_SPECIFIC, ServerProt.REMOVE_GROUND_ITEM, ServerProt.UPDATE_INV_PARTIAL, ServerProt.OBJ_ANIM_SPECIFIC, ServerProt.MESSAGE_PLAYER_GROUP, ServerProt.UPDATE_FRIENDCHAT_CHANNEL_SINGLEUSER, ServerProt.IF_SETCOLOR, UpdateZoneFullFollowsProt.UPDATE_ZONE_FULL_FOLLOWS, ServerProt.IF_SETTARGETPARAM, ServerProt.RESET_SOUNDS, ServerProt.FRIEND_STATUS, ServerProt.IF_OPENSUB, ServerProt.MESSAGE_PUBLIC, ServerProt.IF_MOVESUB, ServerProt.DEBUG_SERVER_TRIGGERS, ServerProt.UPDATE_ZONE_PARTIAL_FOLLOWS, ServerProt.UPDATE_SITESETTINGS_COOKIE, ServerProt.CAM_FORCEANGLE, ServerProt.VARP_LARGE, ServerProt.IF_SETPOSITION, ServerProt.TILE_MESSAGE, ServerProt.UPDATE_STAT, ServerProt.UPDATE_ZONE_PARTIAL_ENCLOSED, ServerProt.IDENTIFY_HOST_NAME, ServerProt.SET_CURSOR, ServerProt.MAP_PROJANIM_HALFSQ, ServerProt.GAME_MESSAGE, ServerProt.CLIENT_SETVARCSTR_LARGE, ServerProt.UPDATE_DOB, ServerProt.LOGOUT_FULL, ServerProt.UPDATE_REBOOT_TIMER, ServerProt.IF_SETPLAYERMODEL_OTHER, ServerProt.DESTROY_OBJECT, ServerProt.UPDATE_INV_STOP_TRANSMIT, ServerProt.LOGOUT_LOBBY, ServerProt.MESSAGE_PRIVATE_ECHO, ServerProt.SET_CAMERA_TARGET, ServerProt.CREATE_GROUND_ITEM, ServerProt.MINIMAP_FLAG, ServerProt.MESSAGE_FRIENDS_CHAT, ServerProt.SOUND_AREA, ServerProt.VORBIS_PRELOAD_SOUND, ServerProt.VARBIT_LARGE, ServerProt.HINT_ARROW, ServerProt.DYNAMIC_MAP_REGION, ServerProt.GROUND_ITEM_COUNT, ServerProt.TRIGGER_ONDIALOGABORT, ServerProt.VARBIT_SMALL, ServerProt.UPDATE_UID192, ServerProt.IF_SETGRAPHIC, ServerProt.CAM_SHAKE, ServerProt.IF_SETPLAYERHEAD, ServerProt.MESSAGE_PRIVATE, ServerProt.OBJECT_PREFETCH, ServerProt.CAM_RESET, ServerProt.IF_SETSCROLLPOS, ServerProt.CREATE_OBJECT, ServerProt.CHAT_FILTER_SETTINGS, ServerProt.SPOT_ANIM, MusicTrackProt.MUSIC_TRACK, ServerProt.IF_SETNPCHEAD, ServerProt.SET_DRAW_ORDER, ServerProt.IF_SETANGLE, ServerProt.CAM_SMOOTHRESET, ServerProt.IF_SETITEM, ServerProt.PLAYER_WEIGHT, ServerProt.CLIENT_SETVARCSTR_SMALL, ServerProt.REFLECTION_CHECK, ServerProt.MESSAGE_QUICKCHAT_PRIVATE, ServerProt.IF_SETPLAYERHEAD_IGNOREWORN, ServerProt.OUTDATED_INTERFACE_PULSE_EVENT, ServerProt.PING, ServerProt.WORLD_LIST, ServerProt.MESSAGE_QUICKCHAT_PRIVATE_ECHO, ServerProt.OPEN_URL, ServerProt.UPDATE_GE_SLOT, ServerProt.IF_OPENTOP, ServerProt.SOUND_SYNTH, ServerProt.BLOCK_MINIMAP_STATE, ServerProt.PLAYER_UPDATE, ServerProt.VORBIS_SOUND, ServerProt.IF_SETTEXTFONT, ServerProt.IF_SETHIDE, ServerProt.GROUND_ITEM_REVEAL, ServerProt.IF_RESETTARGETPARAM, ServerProt.MESSAGE_QUICKCHAT_FRIENDS_CHAT, ServerProt.IF_CLOSESUB, ServerProt.CHAT_FILTER_SETTINGS_PRIVATECHAT, ServerProt.IF_SETPLAYERMODEL, ServerProt.ANIMATE_NPC, ServerProt.MUSIC_EFFECT, ServerProt.SHOW_FACE_HERE, ServerProt.NPC_UPDATE, ServerProt.CAM_MOVETO, ServerProt.UPDATE_IGNORE_LIST)
		}
	}
}

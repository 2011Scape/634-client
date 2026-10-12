package com.jagex.game.runetek5.config

import com.jagex.game.runetek5.js5.Js5ResourceProvider
import com.jagex.game.runetek5.js5.Js5WorkerThread
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.js5.request.remote.RemoteFileWorker
import com.jagex.game.runetek5.cache.Decompressor
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.buffer.BitPacket
import com.jagex.game.runetek5.sound.player.area.AreaSoundPlayer
import com.jagex.game.runetek5.cache.FileOnDisk
import com.jagex.game.runetek5.js5.request.RequestManager

object Resource {
	@OriginalMember(owner = "client!np", name = "d", descriptor = "Lclient!in;")
	@JvmField
	var QC_MESSAGES: Js5ResourceProvider? = null

	@OriginalMember(owner = "client!fa", name = "y", descriptor = "Lclient!tha;")
	@JvmField
	var remoteFileRequesterJs5: Js5WorkerThread? = null

	@OriginalMember(owner = "client!ma", name = "db", descriptor = "Lclient!in;")
	@JvmField
	var VARBIT: Js5ResourceProvider? = null

	@OriginalMember(owner = "client!lba", name = "a", descriptor = "Lclient!in;")
	@JvmField
	var QC_MENU: Js5ResourceProvider? = null

	@OriginalMember(owner = "client!mha", name = "r", descriptor = "Lclient!in;")
	@JvmField
	var FONT_METRICS: Js5ResourceProvider? = null

	@OriginalMember(owner = "client!tfa", name = "d", descriptor = "Lclient!in;")
	@JvmField
	var ENUM: Js5ResourceProvider? = null

	@OriginalMember(owner = "client!uc", name = "F", descriptor = "Lclient!in;")
	@JvmField
	var SEQ: Js5ResourceProvider? = null

	@OriginalMember(owner = "client!po", name = "t", descriptor = "Lclient!in;")
	@JvmField
	var OBJ: Js5ResourceProvider? = null

	@OriginalMember(owner = "client!mo", name = "h", descriptor = "I")
	@JvmField
	var gameTipsCrc: Int = 0

	@OriginalMember(owner = "client!hfa", name = "d", descriptor = "Lclient!in;")
	@JvmField
	var HUFFMAN: Js5ResourceProvider? = null

	@OriginalMember(owner = "client!lw", name = "d", descriptor = "Lclient!in;")
	@JvmField
	var LOC: Js5ResourceProvider? = null

	@OriginalMember(owner = "client!ica", name = "y", descriptor = "Lclient!in;")
	@JvmField
	var MATERIAL: Js5ResourceProvider? = null

	@OriginalMember(owner = "client!kl", name = "v", descriptor = "Lclient!in;")
	@JvmField
	var DEFAULTS: Js5ResourceProvider? = null

	@OriginalMember(owner = "client!ru", name = "b", descriptor = "Lclient!in;")
	@JvmField
	var NPC: Js5ResourceProvider? = null

	@OriginalMember(owner = "client!qw", name = "M", descriptor = "Lclient!in;")
	@JvmField
	var WORLD_MAP: Js5ResourceProvider? = null

	@OriginalMember(owner = "client!uh", name = "a", descriptor = "Lclient!in;")
	@JvmField
	var BILLBOARD: Js5ResourceProvider? = null

	@OriginalMember(owner = "client!bf", name = "e", descriptor = "Lclient!in;")
	@JvmField
	var CS2: Js5ResourceProvider? = null

	@OriginalMember(owner = "client!bia", name = "d", descriptor = "Lclient!in;")
	@JvmField
	var VORBIS: Js5ResourceProvider? = null

	@OriginalMember(owner = "client!dha", name = "A", descriptor = "Lclient!in;")
	@JvmField
	var MUSIC: Js5ResourceProvider? = null

	@OriginalMember(owner = "client!nn", name = "f", descriptor = "Lclient!in;")
	@JvmField
	var SOUND_EFFECT: Js5ResourceProvider? = null

	@OriginalMember(owner = "client!su", name = "m", descriptor = "Lclient!in;")
	@JvmField
	var PARTICLE: Js5ResourceProvider? = null

	@OriginalMember(owner = "client!oia", name = "o", descriptor = "Lclient!in;")
	@JvmField
	var FRAME_SET: Js5ResourceProvider? = null

	@OriginalMember(owner = "client!naa", name = "X", descriptor = "Lclient!in;")
	@JvmField
	var JINGLE: Js5ResourceProvider? = null

	@OriginalMember(owner = "client!ri", name = "t", descriptor = "Lclient!in;")
	@JvmField
	var INTERFACE: Js5ResourceProvider? = null

	@OriginalMember(owner = "client!ld", name = "j", descriptor = "Lclient!in;")
	@JvmField
	var MAPS: Js5ResourceProvider? = null

	@OriginalMember(owner = "client!nh", name = "f", descriptor = "Lclient!in;")
	@JvmField
	var SOUND_MIDI: Js5ResourceProvider? = null

	@OriginalMember(owner = "client!l", name = "s", descriptor = "Lclient!in;")
	@JvmField
	var GFX: Js5ResourceProvider? = null

	@OriginalMember(owner = "client!kba", name = "u", descriptor = "Lclient!in;")
	@JvmField
	var MIDI_INSTRUMENT: Js5ResourceProvider? = null

	@OriginalMember(owner = "client!ou", name = "u", descriptor = "Lclient!in;")
	@JvmField
	var TEXTURE: Js5ResourceProvider? = null

	@OriginalMember(owner = "client!nba", name = "k", descriptor = "Lclient!in;")
	@JvmField
	var MODEL: Js5ResourceProvider? = null

	@OriginalMember(owner = "client!cl", name = "V", descriptor = "Lclient!in;")
	@JvmField
	var GAME_TIPS: Js5ResourceProvider? = null

	@OriginalMember(owner = "client!hj", name = "b", descriptor = "Lclient!in;")
	@JvmField
	var SPRITES: Js5ResourceProvider? = null

	@OriginalMember(owner = "client!nia", name = "d", descriptor = "Lclient!in;")
	@JvmField
	var NATIVE_LIBRARY: Js5ResourceProvider? = null

	@OriginalMember(owner = "client!qg", name = "a", descriptor = "Lclient!in;")
	@JvmField
	var FRAME_BASE: Js5ResourceProvider? = null

	@OriginalMember(owner = "client!nv", name = "j", descriptor = "Lclient!in;")
	@JvmField
	var SHADER: Js5ResourceProvider? = null
	@OriginalMember(owner = "client!maa", name = "c", descriptor = "Lclient!in;")
	@JvmField
	var CONFIG: Js5ResourceProvider? = null
	@OriginalMember(owner = "client!am", name = "e", descriptor = "[Lclient!bja;")
	@JvmField
	val remoteFileWorkersByIndex: Array<RemoteFileWorker?> = arrayOfNulls<RemoteFileWorker>(37)
	@OriginalMember(owner = "client!vd", name = "k", descriptor = "Lclient!nw;")
	@JvmField
	var referenceTableDecompressor: Decompressor? = null
	@OriginalMember(owner = "client!ou", name = "a", descriptor = "(ZIBI)Lclient!in;")
	@JvmStatic
	fun createProvider(@OriginalArg(0) directMemoryMode: Boolean, @OriginalArg(1) archiveId: Int, @OriginalArg(3) loadType: Int): Js5ResourceProvider {
		@Pc(10) var fileSystem_Client: Decompressor? = null
		if (Resource.cacheFileData != null) {
			fileSystem_Client = Decompressor(archiveId, Resource.cacheFileData, Resource.cacheFileIndices[archiveId], 1000000)
		}
		Resource.remoteFileWorkersByIndex[archiveId] = RequestManager.requestManager!!.getWorker(fileSystem_Client, archiveId, Resource.referenceTableDecompressor)
		Resource.remoteFileWorkersByIndex[archiveId]!!.prefetchAll()
		return Js5ResourceProvider(Resource.remoteFileWorkersByIndex[archiveId], directMemoryMode, loadType)
	}
	@OriginalMember(owner = "client!bj", name = "a", descriptor = "(ILclient!ua;)V")
	@JvmStatic
	fun writeIndexCrcs(@OriginalArg(1) bitPacket: BitPacket?) {
		bitPacket!!.writeInt(Resource.FRAME_SET!!.getIndexCrc())
		bitPacket!!.writeInt(Resource.FRAME_BASE!!.getIndexCrc())
		bitPacket!!.writeInt(Resource.CONFIG!!.getIndexCrc())
		bitPacket!!.writeInt(Resource.INTERFACE!!.getIndexCrc())
		bitPacket!!.writeInt(Resource.SOUND_EFFECT!!.getIndexCrc())
		bitPacket!!.writeInt(Resource.MAPS!!.getIndexCrc())
		bitPacket!!.writeInt(Resource.MUSIC!!.getIndexCrc())
		bitPacket!!.writeInt(Resource.MODEL!!.getIndexCrc())
		bitPacket!!.writeInt(Resource.SPRITES!!.getIndexCrc())
		bitPacket!!.writeInt(Resource.TEXTURE!!.getIndexCrc())
		bitPacket!!.writeInt(Resource.HUFFMAN!!.getIndexCrc())
		bitPacket!!.writeInt(Resource.JINGLE!!.getIndexCrc())
		bitPacket!!.writeInt(Resource.CS2!!.getIndexCrc())
		bitPacket!!.writeInt(Resource.FONT_METRICS!!.getIndexCrc())
		bitPacket!!.writeInt(Resource.MIDI_INSTRUMENT!!.getIndexCrc())
		bitPacket!!.writeInt(Resource.SOUND_MIDI!!.getIndexCrc())
		bitPacket!!.writeInt(Resource.LOC!!.getIndexCrc())
		bitPacket!!.writeInt(Resource.ENUM!!.getIndexCrc())
		bitPacket!!.writeInt(Resource.NPC!!.getIndexCrc())
		bitPacket!!.writeInt(Resource.OBJ!!.getIndexCrc())
		bitPacket!!.writeInt(Resource.SEQ!!.getIndexCrc())
		bitPacket!!.writeInt(Resource.GFX!!.getIndexCrc())
		bitPacket!!.writeInt(Resource.VARBIT!!.getIndexCrc())
		bitPacket!!.writeInt(Resource.WORLD_MAP!!.getIndexCrc())
		bitPacket!!.writeInt(Resource.QC_MESSAGES!!.getIndexCrc())
		bitPacket!!.writeInt(Resource.QC_MENU!!.getIndexCrc())
		bitPacket!!.writeInt(Resource.MATERIAL!!.getIndexCrc())
		bitPacket!!.writeInt(Resource.PARTICLE!!.getIndexCrc())
		bitPacket!!.writeInt(Resource.DEFAULTS!!.getIndexCrc())
		bitPacket!!.writeInt(Resource.BILLBOARD!!.getIndexCrc())
		bitPacket!!.writeInt(Resource.NATIVE_LIBRARY!!.getIndexCrc())
		bitPacket!!.writeInt(Resource.SHADER!!.getIndexCrc())
		bitPacket!!.writeInt(AreaSoundPlayer.getLoadingSpritesIndexCrc())
		bitPacket!!.writeInt(Resource.getGameTipsCrc())
		bitPacket!!.writeInt(Resource.VORBIS!!.getIndexCrc())
	}

	@OriginalMember(owner = "client!pga", name = "r", descriptor = "Lclient!in;")
	@JvmField
	var aClass157_219: Js5ResourceProvider? = null
	@OriginalMember(owner = "client!mea", name = "c", descriptor = "[Lclient!l;")
	@JvmField
	val cacheFileIndices: Array<FileOnDisk?> = arrayOfNulls<FileOnDisk>(37)

	@OriginalMember(owner = "client!vc", name = "h", descriptor = "Lclient!l;")
	@JvmField
	var cacheFileData: FileOnDisk? = null

	@OriginalMember(owner = "client!bv", name = "z", descriptor = "Lclient!l;")
	@JvmField
	var aClass184_1: FileOnDisk? = null

	@OriginalMember(owner = "client!v", name = "N", descriptor = "Lclient!in;")
	@JvmField
	var aClass157_291: Js5ResourceProvider? = null

	@OriginalMember(owner = "client!tn", name = "e", descriptor = "Lclient!in;")
	@JvmField
	var LOADING_SPRITES: Js5ResourceProvider? = null

	@OriginalMember(owner = "client!sia", name = "c", descriptor = "Lclient!in;")
	@JvmField
	var aClass157_255: Js5ResourceProvider? = null
	@OriginalMember(owner = "client!gq", name = "j", descriptor = "(I)I")
	@JvmStatic
	fun getGameTipsCrc(): Int {
		return Resource.gameTipsCrc
	}
}


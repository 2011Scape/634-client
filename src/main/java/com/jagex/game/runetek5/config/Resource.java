package com.jagex.game.runetek5.config;

import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import com.jagex.game.runetek5.js5.Js5WorkerThread;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.js5.request.remote.RemoteFileWorker;
import com.jagex.game.runetek5.cache.Decompressor;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.buffer.BitPacket;
import com.jagex.game.runetek5.sound.player.area.AreaSoundPlayer;
import com.jagex.game.runetek5.cache.FileOnDisk;
import com.jagex.game.runetek5.js5.request.RequestManager;

public final class Resource {
	@OriginalMember(owner = "client!np", name = "d", descriptor = "Lclient!in;")
	public static Js5ResourceProvider QC_MESSAGES;

	@OriginalMember(owner = "client!fa", name = "y", descriptor = "Lclient!tha;")
	public static Js5WorkerThread remoteFileRequesterJs5;

	@OriginalMember(owner = "client!ma", name = "db", descriptor = "Lclient!in;")
	public static Js5ResourceProvider VARBIT;

	@OriginalMember(owner = "client!lba", name = "a", descriptor = "Lclient!in;")
	public static Js5ResourceProvider QC_MENU;

	@OriginalMember(owner = "client!mha", name = "r", descriptor = "Lclient!in;")
	public static Js5ResourceProvider FONT_METRICS;

	@OriginalMember(owner = "client!tfa", name = "d", descriptor = "Lclient!in;")
	public static Js5ResourceProvider ENUM;

	@OriginalMember(owner = "client!uc", name = "F", descriptor = "Lclient!in;")
	public static Js5ResourceProvider SEQ;

	@OriginalMember(owner = "client!po", name = "t", descriptor = "Lclient!in;")
	public static Js5ResourceProvider OBJ;

	@OriginalMember(owner = "client!mo", name = "h", descriptor = "I")
	public static int gameTipsCrc;

	@OriginalMember(owner = "client!hfa", name = "d", descriptor = "Lclient!in;")
	public static Js5ResourceProvider HUFFMAN;

	@OriginalMember(owner = "client!lw", name = "d", descriptor = "Lclient!in;")
	public static Js5ResourceProvider LOC;

	@OriginalMember(owner = "client!ica", name = "y", descriptor = "Lclient!in;")
	public static Js5ResourceProvider MATERIAL;

	@OriginalMember(owner = "client!kl", name = "v", descriptor = "Lclient!in;")
	public static Js5ResourceProvider DEFAULTS;

	@OriginalMember(owner = "client!ru", name = "b", descriptor = "Lclient!in;")
	public static Js5ResourceProvider NPC;

	@OriginalMember(owner = "client!qw", name = "M", descriptor = "Lclient!in;")
	public static Js5ResourceProvider WORLD_MAP;

	@OriginalMember(owner = "client!uh", name = "a", descriptor = "Lclient!in;")
	public static Js5ResourceProvider BILLBOARD;

	@OriginalMember(owner = "client!bf", name = "e", descriptor = "Lclient!in;")
	public static Js5ResourceProvider CS2;

	@OriginalMember(owner = "client!bia", name = "d", descriptor = "Lclient!in;")
	public static Js5ResourceProvider VORBIS;

	@OriginalMember(owner = "client!dha", name = "A", descriptor = "Lclient!in;")
	public static Js5ResourceProvider MUSIC;

	@OriginalMember(owner = "client!nn", name = "f", descriptor = "Lclient!in;")
	public static Js5ResourceProvider SOUND_EFFECT;

	@OriginalMember(owner = "client!su", name = "m", descriptor = "Lclient!in;")
	public static Js5ResourceProvider PARTICLE;

	@OriginalMember(owner = "client!oia", name = "o", descriptor = "Lclient!in;")
	public static Js5ResourceProvider FRAME_SET;

	@OriginalMember(owner = "client!naa", name = "X", descriptor = "Lclient!in;")
	public static Js5ResourceProvider JINGLE;

	@OriginalMember(owner = "client!ri", name = "t", descriptor = "Lclient!in;")
	public static Js5ResourceProvider INTERFACE;

	private Resource() {
		/* empty */
	}
	@OriginalMember(owner = "client!ld", name = "j", descriptor = "Lclient!in;")
	public static Js5ResourceProvider MAPS;

	@OriginalMember(owner = "client!nh", name = "f", descriptor = "Lclient!in;")
	public static Js5ResourceProvider SOUND_MIDI;

	@OriginalMember(owner = "client!l", name = "s", descriptor = "Lclient!in;")
	public static Js5ResourceProvider GFX;

	@OriginalMember(owner = "client!kba", name = "u", descriptor = "Lclient!in;")
	public static Js5ResourceProvider MIDI_INSTRUMENT;

	@OriginalMember(owner = "client!ou", name = "u", descriptor = "Lclient!in;")
	public static Js5ResourceProvider TEXTURE;

	@OriginalMember(owner = "client!nba", name = "k", descriptor = "Lclient!in;")
	public static Js5ResourceProvider MODEL;

	@OriginalMember(owner = "client!cl", name = "V", descriptor = "Lclient!in;")
	public static Js5ResourceProvider GAME_TIPS;

	@OriginalMember(owner = "client!hj", name = "b", descriptor = "Lclient!in;")
	public static Js5ResourceProvider SPRITES;

	@OriginalMember(owner = "client!nia", name = "d", descriptor = "Lclient!in;")
	public static Js5ResourceProvider NATIVE_LIBRARY;

	@OriginalMember(owner = "client!qg", name = "a", descriptor = "Lclient!in;")
	public static Js5ResourceProvider FRAME_BASE;

	@OriginalMember(owner = "client!nv", name = "j", descriptor = "Lclient!in;")
	public static Js5ResourceProvider SHADER;
	@OriginalMember(owner = "client!maa", name = "c", descriptor = "Lclient!in;")
	public static Js5ResourceProvider CONFIG;
	@OriginalMember(owner = "client!am", name = "e", descriptor = "[Lclient!bja;")
	public static final RemoteFileWorker[] remoteFileWorkersByIndex = new RemoteFileWorker[37];
	@OriginalMember(owner = "client!vd", name = "k", descriptor = "Lclient!nw;")
	public static Decompressor referenceTableDecompressor;
	@OriginalMember(owner = "client!ou", name = "a", descriptor = "(ZIBI)Lclient!in;")
	public static Js5ResourceProvider createProvider(@OriginalArg(0) boolean directMemoryMode, @OriginalArg(1) int archiveId, @OriginalArg(3) int loadType) {
		@Pc(10) Decompressor fileSystem_Client = null;
		if (Resource.cacheFileData != null) {
			fileSystem_Client = new Decompressor(archiveId, Resource.cacheFileData, Resource.cacheFileIndices[archiveId], 1000000);
		}
		Resource.remoteFileWorkersByIndex[archiveId] = RequestManager.requestManager.getWorker(fileSystem_Client, archiveId, Resource.referenceTableDecompressor);
		Resource.remoteFileWorkersByIndex[archiveId].prefetchAll();
		return new Js5ResourceProvider(Resource.remoteFileWorkersByIndex[archiveId], directMemoryMode, loadType);
	}
	@OriginalMember(owner = "client!bj", name = "a", descriptor = "(ILclient!ua;)V")
	public static void writeIndexCrcs(@OriginalArg(1) BitPacket bitPacket) {
		bitPacket.writeInt(Resource.FRAME_SET.getIndexCrc());
		bitPacket.writeInt(Resource.FRAME_BASE.getIndexCrc());
		bitPacket.writeInt(Resource.CONFIG.getIndexCrc());
		bitPacket.writeInt(Resource.INTERFACE.getIndexCrc());
		bitPacket.writeInt(Resource.SOUND_EFFECT.getIndexCrc());
		bitPacket.writeInt(Resource.MAPS.getIndexCrc());
		bitPacket.writeInt(Resource.MUSIC.getIndexCrc());
		bitPacket.writeInt(Resource.MODEL.getIndexCrc());
		bitPacket.writeInt(Resource.SPRITES.getIndexCrc());
		bitPacket.writeInt(Resource.TEXTURE.getIndexCrc());
		bitPacket.writeInt(Resource.HUFFMAN.getIndexCrc());
		bitPacket.writeInt(Resource.JINGLE.getIndexCrc());
		bitPacket.writeInt(Resource.CS2.getIndexCrc());
		bitPacket.writeInt(Resource.FONT_METRICS.getIndexCrc());
		bitPacket.writeInt(Resource.MIDI_INSTRUMENT.getIndexCrc());
		bitPacket.writeInt(Resource.SOUND_MIDI.getIndexCrc());
		bitPacket.writeInt(Resource.LOC.getIndexCrc());
		bitPacket.writeInt(Resource.ENUM.getIndexCrc());
		bitPacket.writeInt(Resource.NPC.getIndexCrc());
		bitPacket.writeInt(Resource.OBJ.getIndexCrc());
		bitPacket.writeInt(Resource.SEQ.getIndexCrc());
		bitPacket.writeInt(Resource.GFX.getIndexCrc());
		bitPacket.writeInt(Resource.VARBIT.getIndexCrc());
		bitPacket.writeInt(Resource.WORLD_MAP.getIndexCrc());
		bitPacket.writeInt(Resource.QC_MESSAGES.getIndexCrc());
		bitPacket.writeInt(Resource.QC_MENU.getIndexCrc());
		bitPacket.writeInt(Resource.MATERIAL.getIndexCrc());
		bitPacket.writeInt(Resource.PARTICLE.getIndexCrc());
		bitPacket.writeInt(Resource.DEFAULTS.getIndexCrc());
		bitPacket.writeInt(Resource.BILLBOARD.getIndexCrc());
		bitPacket.writeInt(Resource.NATIVE_LIBRARY.getIndexCrc());
		bitPacket.writeInt(Resource.SHADER.getIndexCrc());
		bitPacket.writeInt(AreaSoundPlayer.getLoadingSpritesIndexCrc());
		bitPacket.writeInt(Resource.getGameTipsCrc());
		bitPacket.writeInt(Resource.VORBIS.getIndexCrc());
	}

	@OriginalMember(owner = "client!pga", name = "r", descriptor = "Lclient!in;")
	public static Js5ResourceProvider aClass157_219;
	@OriginalMember(owner = "client!mea", name = "c", descriptor = "[Lclient!l;")
	public static final FileOnDisk[] cacheFileIndices = new FileOnDisk[37];

	@OriginalMember(owner = "client!vc", name = "h", descriptor = "Lclient!l;")
	public static FileOnDisk cacheFileData;

	@OriginalMember(owner = "client!bv", name = "z", descriptor = "Lclient!l;")
	public static FileOnDisk aClass184_1;

	@OriginalMember(owner = "client!v", name = "N", descriptor = "Lclient!in;")
	public static Js5ResourceProvider aClass157_291;

	@OriginalMember(owner = "client!tn", name = "e", descriptor = "Lclient!in;")
	public static Js5ResourceProvider LOADING_SPRITES;

	@OriginalMember(owner = "client!sia", name = "c", descriptor = "Lclient!in;")
	public static Js5ResourceProvider aClass157_255;
	@OriginalMember(owner = "client!gq", name = "j", descriptor = "(I)I")
	public static int getGameTipsCrc() {
		return Resource.gameTipsCrc;
	}
}

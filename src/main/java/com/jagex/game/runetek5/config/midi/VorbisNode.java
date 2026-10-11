package com.jagex.game.runetek5.config.midi;

import com.jagex.game.runetek5.cache.node.Node;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.sound.jagtheora.VorbisFileReference;
import com.jagex.game.runetek5.graphics.Toolkit;
import com.jagex.game.runetek5.graphics.sprite.NativeSprite;
import com.jagex.game.runetek5.sound.jagtheora.JagTheoraTheoraNode;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable;

@OriginalClass("client!il")
public final class VorbisNode extends Node {

	@OriginalMember(owner = "client!il", name = "s", descriptor = "Z")
	public boolean isReferencedThisCycle;

	@OriginalMember(owner = "client!il", name = "B", descriptor = "Z")
	public boolean isPaused;

	@OriginalMember(owner = "client!il", name = "D", descriptor = "Z")
	public boolean isStreamActive;

	@OriginalMember(owner = "client!il", name = "C", descriptor = "I")
	public final int id;

	@OriginalMember(owner = "client!il", name = "o", descriptor = "Lclient!qc;")
	public final VorbisFileReference vfReference;

	@OriginalMember(owner = "client!il", name = "t", descriptor = "I")
	public final int fileId;

	@OriginalMember(owner = "client!il", name = "w", descriptor = "Z")
	public final boolean sendPacket;

	@OriginalMember(owner = "client!il", name = "<init>", descriptor = "(ILclient!qc;IZ)V")
	public VorbisNode(@OriginalArg(0) int fileId, @OriginalArg(1) VorbisFileReference js5Video, @OriginalArg(2) int volume, @OriginalArg(3) boolean sendPacket) {
		this.id = volume;
		this.vfReference = js5Video;
		this.fileId = fileId;
		this.sendPacket = sendPacket;
	}
	@OriginalMember(owner = "client!qda", name = "a", descriptor = "(Lclient!ha;II)Lclient!mi;")
	public static NativeSprite getVideoFrame(@OriginalArg(0) Toolkit toolkit, @OriginalArg(2) int videoId) {
		@Pc(10) VorbisNode videoType = (VorbisNode) VorbisNode.INDEX36_FILE_CACHE.get((long) videoId);
		if (videoType != null) {
			@Pc(17) JagTheoraTheoraNode oggTheoraStream = videoType.vfReference.getTheoraDecoder();
			videoType.isReferencedThisCycle = true;
			if (oggTheoraStream != null) {
				return oggTheoraStream.getFrameSprite(toolkit);
			}
		}
		return null;
	}
	@OriginalMember(owner = "client!ht", name = "c", descriptor = "Lclient!eq;")
	public static final IterableHashTable INDEX36_FILE_CACHE = new IterableHashTable(4);
	@OriginalMember(owner = "client!oea", name = "a", descriptor = "(II)V")
	public static void toggleVorbisPause(@OriginalArg(1) int videoId) {
		@Pc(15) VorbisNode videoType = (VorbisNode) VorbisNode.INDEX36_FILE_CACHE.get((long) videoId);
		if (videoType != null) {
			videoType.isPaused = !videoType.isPaused;
			videoType.vfReference.togglePause(videoType.isPaused);
		}
	}
}

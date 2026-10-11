package com.jagex.game.runetek5.config.sequence.frameset;

import com.jagex.game.runetek5.core.datastruct.key.Deque;
import com.jagex.game.runetek5.cache.node.lru.CacheableNode;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.DraggedComponentLayer;

@OriginalClass("client!gw")
public final class FrameSet extends CacheableNode {

	@OriginalMember(owner = "client!gw", name = "y", descriptor = "[[B")
	private byte[][] frameData;

	@OriginalMember(owner = "client!gw", name = "A", descriptor = "[Lclient!gk;")
	public AnimFrame[] frames;

	@OriginalMember(owner = "client!gw", name = "w", descriptor = "I")
	private final int groupId;

	@OriginalMember(owner = "client!gw", name = "<init>", descriptor = "(I)V")
	public FrameSet(@OriginalArg(0) int groupId) {
		this.groupId = groupId;
	}

	@OriginalMember(owner = "client!gw", name = "a", descriptor = "(BI)Z")
	public boolean modifiesBillboards(@OriginalArg(1) int frameIndex) {
		return this.frames[frameIndex].modifiesBillboard;
	}

	@OriginalMember(owner = "client!gw", name = "a", descriptor = "(I)Z")
	public boolean isReady() {
		if (this.frames != null) {
			return true;
		}
		@Pc(35) int[] fileIds;
		@Pc(42) int local42;
		if (this.frameData == null) {
			@Pc(15) Js5ResourceProvider frameskeletonResource = FrameSet.frameskeletonResource;
			synchronized (FrameSet.frameskeletonResource) {
				if (!FrameSet.frameskeletonResource.isGroupReady(this.groupId)) {
					return false;
				}
				fileIds = FrameSet.frameskeletonResource.getFileIds(this.groupId);
				this.frameData = new byte[fileIds.length][];
				for (int fileIndex = 0; fileIndex < fileIds.length; fileIndex++) {
					this.frameData[fileIndex] = FrameSet.frameskeletonResource.getFile(this.groupId, fileIds[fileIndex]);
				}
			}
		}
		@Pc(73) boolean loaded = true;
		for (@Pc(75) int i = 0; i < this.frameData.length; i++) {
			@Pc(82) byte[] data = this.frameData[i];
			@Pc(87) Packet packet = new Packet(data);
			packet.currentPosition = 1;
			int frameBaseId = packet.readUShort();
			@Pc(96) Js5ResourceProvider framebaseLock = FrameSet.framebaseResource;
			synchronized (FrameSet.framebaseResource) {
				loaded &= FrameSet.framebaseResource.isFileReady(frameBaseId);
			}
		}
		if (!loaded) {
			return false;
		}
		@Pc(125) Deque deque = new Deque();
		@Pc(127) Js5ResourceProvider frameskeletonLock = FrameSet.frameskeletonResource;
		synchronized (FrameSet.frameskeletonResource) {
			@Pc(135) int fileCount = FrameSet.frameskeletonResource.getGroupCapacity(this.groupId);
			this.frames = new AnimFrame[fileCount];
			fileIds = FrameSet.frameskeletonResource.getFileIds(this.groupId);
		}
		for (int fileIndex = 0; fileIndex < fileIds.length; fileIndex++) {
			@Pc(168) byte[] data = this.frameData[fileIndex];
			@Pc(173) Packet packet2 = new Packet(data);
			packet2.currentPosition = 1;
			@Pc(180) int baseId = packet2.readUShort();
			@Pc(182) AnimBase animBase = null;
			for (@Pc(187) AnimBase animBase2 = (AnimBase) deque.first(); animBase2 != null; animBase2 = (AnimBase) deque.next()) {
				if (animBase2.id == baseId) {
					animBase = animBase2;
					break;
				}
			}
			if (animBase == null) {
				@Pc(207) Js5ResourceProvider framebaseLock = FrameSet.framebaseResource;
				synchronized (FrameSet.framebaseResource) {
					animBase = new AnimBase(baseId, FrameSet.framebaseResource.getFile(baseId));
				}
				deque.addLast(animBase);
			}
			this.frames[fileIds[fileIndex]] = new AnimFrame(data, animBase);
		}
		this.frameData = null;
		return true;
	}

	@OriginalMember(owner = "client!gw", name = "c", descriptor = "(II)Z")
	public boolean modifiesAlpha(@OriginalArg(0) int frameIndex) {
		return this.frames[frameIndex].modifiesAlpha;
	}

	@OriginalMember(owner = "client!gw", name = "d", descriptor = "(II)Z")
	public boolean modifiesColors(@OriginalArg(0) int frameIndex) {
		return this.frames[frameIndex].modifiesColor;
	}
	@OriginalMember(owner = "client!oaa", name = "a", descriptor = "Lclient!in;")
	public static Js5ResourceProvider frameskeletonResource;
	@OriginalMember(owner = "client!kaa", name = "u", descriptor = "Lclient!in;")
	public static Js5ResourceProvider framebaseResource;
	@OriginalMember(owner = "client!su", name = "a", descriptor = "(ILclient!in;ILclient!in;)V")
	public static void initialize(@OriginalArg(1) Js5ResourceProvider frameskeletonResource, @OriginalArg(3) Js5ResourceProvider framebaseResource) {
		FrameSet.framebaseResource = framebaseResource;
		FrameSet.frameskeletonResource = frameskeletonResource;
	}
}

package com.jagex.game.runetek5.cache.node.lru;

import com.jagex.game.runetek5.cache.node.lru.CacheableNode;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.client.periodic.PulseEventHandler;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!et")
public final class PulseEvent extends CacheableNode {

	@OriginalMember(owner = "client!et", name = "A", descriptor = "I")
	public int tertiary;

	@OriginalMember(owner = "client!et", name = "B", descriptor = "I")
	public int secondary;

	@OriginalMember(owner = "client!et", name = "C", descriptor = "I")
	public int primary;

	@OriginalMember(owner = "client!et", name = "E", descriptor = "Ljava/lang/String;")
	public String string;

	@OriginalMember(owner = "client!et", name = "<init>", descriptor = "(II)V")
	public PulseEvent(@OriginalArg(0) int type, @OriginalArg(1) int id) {
		super.key = (long) type << 32 | (long) id;
	}

	@OriginalMember(owner = "client!et", name = "b", descriptor = "(Z)I")
	public int getType() {
		return (int) (super.key >>> 32 & 0xFFL);
	}

	@OriginalMember(owner = "client!et", name = "a", descriptor = "(I)V")
	public void queueHighPriority() {
		super.indexReference |= Long.MIN_VALUE;
		if (this.executeAfter() == 0L) {
			PulseEventHandler.HIGH_PRIORITY.insert(this);
		}
	}

	@OriginalMember(owner = "client!et", name = "e", descriptor = "(I)I")
	public int getId() {
		return (int) super.key;
	}

	@OriginalMember(owner = "client!et", name = "e", descriptor = "(B)J")
	public long executeAfter() {
		return super.indexReference & Long.MAX_VALUE;
	}

	@OriginalMember(owner = "client!et", name = "f", descriptor = "(I)V")
	public void queueLowPriority() {
		super.indexReference = Long.MIN_VALUE & super.indexReference | Utils.currentTimeMillis() + 500L;
		PulseEventHandler.LOW_PRIORITY.insert(this);
	}
	@OriginalMember(owner = "client!au", name = "a", descriptor = "(IIZ)V")
	public static void priorityIfGraphicUpdate(@OriginalArg(0) int idHash, @OriginalArg(1) int graphicId) {
		@Pc(8) PulseEvent pulseEvent = PulseEventHandler.createPulseEvent(idHash, 14);
		pulseEvent.queueHighPriority();
		pulseEvent.primary = graphicId;
	}

	@OriginalMember(owner = "client!hc", name = "d", descriptor = "(III)V")
	public static void priorityClientvarcUpdate(@OriginalArg(0) int varId, @OriginalArg(2) int value) {
		@Pc(12) PulseEvent pulseEvent = PulseEventHandler.createPulseEvent(varId, 1);
		pulseEvent.queueHighPriority();
		pulseEvent.primary = value;
	}

	@OriginalMember(owner = "client!jaa", name = "b", descriptor = "(BI)V")
	public static void delayedIfPositionUpdate(@OriginalArg(1) int componentHash) {
		@Pc(15) PulseEvent pulseEvent = PulseEventHandler.createPulseEvent(componentHash, 11);
		pulseEvent.queueLowPriority();
	}

	@OriginalMember(owner = "client!il", name = "a", descriptor = "(ZII)V")
	public static void priorityIfHideUpdate(@OriginalArg(1) int hidden, @OriginalArg(2) int componentHash) {
		@Pc(8) PulseEvent pulseEvent = PulseEventHandler.createPulseEvent(componentHash, 7);
		pulseEvent.queueHighPriority();
		pulseEvent.primary = hidden;
	}
	@OriginalMember(owner = "client!rea", name = "a", descriptor = "(II)V")
	public static void delayedIfGraphicUpdate(@OriginalArg(0) int idHash) {
		@Pc(8) PulseEvent event = PulseEventHandler.createPulseEvent(idHash, 14);
		event.queueLowPriority();
	}

	@OriginalMember(owner = "client!nga", name = "a", descriptor = "(IIBII)V")
	public static void priorityIfangleUpdate(@OriginalArg(0) int zoom, @OriginalArg(1) int rotationX, @OriginalArg(3) int rotationY, @OriginalArg(4) int componentHash) {
		@Pc(8) PulseEvent event = PulseEventHandler.createPulseEvent(componentHash, 8);
		event.queueHighPriority();
		event.primary = rotationX;
		event.tertiary = zoom;
		event.secondary = rotationY;
	}

	@OriginalMember(owner = "client!ro", name = "a", descriptor = "(III)V")
	public static void priorityT13Update(@OriginalArg(1) int componentHash, @OriginalArg(2) int value) {
		@Pc(8) PulseEvent event = PulseEventHandler.createPulseEvent(componentHash, 13);
		event.queueHighPriority();
		event.primary = value;
	}

	@OriginalMember(owner = "client!rl", name = "b", descriptor = "(II)V")
	public static void delayedIfScrollPosUpdate(@OriginalArg(1) int idHash) {
		@Pc(8) PulseEvent event = PulseEventHandler.createPulseEvent(idHash, 12);
		event.queueLowPriority();
	}
	@OriginalMember(owner = "client!tt", name = "a", descriptor = "(ILjava/lang/String;I)V")
	public static void priorityIfTextUpdate(@OriginalArg(0) int componentHash, @OriginalArg(1) String text) {
		@Pc(15) PulseEvent event = PulseEventHandler.createPulseEvent(componentHash, 3);
		event.queueHighPriority();
		event.string = text;
	}

	@OriginalMember(owner = "client!paa", name = "a", descriptor = "(BI)V")
	public static void delayedIfAnimUpdate(@OriginalArg(1) int idHash) {
		@Pc(16) PulseEvent event = PulseEventHandler.createPulseEvent(idHash, 5);
		event.queueLowPriority();
	}

	@OriginalMember(owner = "client!rc", name = "b", descriptor = "(II)V")
	public static void delayedIfOffsetUpdate(@OriginalArg(0) int idHash) {
		@Pc(8) PulseEvent event = PulseEventHandler.createPulseEvent(idHash, 10);
		event.queueLowPriority();
	}

	@OriginalMember(owner = "client!ua", name = "b", descriptor = "(III)V")
	public static void priorityIfscrollposUpdate(@OriginalArg(0) int componentHash, @OriginalArg(2) int scrollPos) {
		@Pc(8) PulseEvent event = PulseEventHandler.createPulseEvent(componentHash, 12);
		event.queueHighPriority();
		event.primary = scrollPos;
	}

	@OriginalMember(owner = "client!qda", name = "a", descriptor = "(Z)V")
	public static void delayedMinimapFlagUpdate() {
		@Pc(13) PulseEvent event = PulseEventHandler.createPulseEvent(0, 15);
		event.queueLowPriority();
	}

	@OriginalMember(owner = "client!sm", name = "a", descriptor = "(II)V")
	public static void delayedVarcUpdate(@OriginalArg(1) int varcId) {
		@Pc(13) PulseEvent event = PulseEventHandler.createPulseEvent(varcId, 1);
		event.queueLowPriority();
	}

	@OriginalMember(owner = "client!bi", name = "a", descriptor = "(II)V")
	public static void delayedIfColourUpdate(@OriginalArg(0) int idHash) {
		@Pc(8) PulseEvent event = PulseEventHandler.createPulseEvent(idHash, 6);
		event.queueLowPriority();
	}

	@OriginalMember(owner = "client!mea", name = "i", descriptor = "Lclient!eq;")
	public static final IterableHashTable aClass99_48 = new IterableHashTable(16);

	@OriginalMember(owner = "client!qi", name = "a", descriptor = "(II)V")
	public static void delayedIfVideoUpdate(@OriginalArg(0) int idHash) {
		@Pc(8) PulseEvent event = PulseEventHandler.createPulseEvent(idHash, 17);
		event.queueLowPriority();
	}

	@OriginalMember(owner = "client!hq", name = "a", descriptor = "(III)V")
	public static void priorityIfFontUpdate(@OriginalArg(0) int fontId, @OriginalArg(2) int idHash) {
		@Pc(13) PulseEvent event = PulseEventHandler.createPulseEvent(idHash, 16);
		event.queueHighPriority();
		event.primary = fontId;
	}

	@OriginalMember(owner = "client!ss", name = "a", descriptor = "(IIIII)V")
	public static void priorityIfcontentUpdate(@OriginalArg(0) int modelType, @OriginalArg(1) int componentHash, @OriginalArg(2) int primaryModelId, @OriginalArg(4) int secondaryModelId) {
		@Pc(8) PulseEvent event = PulseEventHandler.createPulseEvent(componentHash, 4);
		event.queueHighPriority();
		event.secondary = primaryModelId;
		event.primary = modelType;
		event.tertiary = secondaryModelId;
	}

	@OriginalMember(owner = "client!ic", name = "a", descriptor = "(IIIII)V")
	public static void priorityIfOffsetUpdate(@OriginalArg(0) int offsetX, @OriginalArg(1) int offsetY, @OriginalArg(3) int idHash, @OriginalArg(4) int rotationZ) {
		@Pc(8) PulseEvent event = PulseEventHandler.createPulseEvent(idHash, 10);
		event.queueHighPriority();
		event.secondary = offsetY;
		event.tertiary = rotationZ;
		event.primary = offsetX;
	}
	@OriginalMember(owner = "client!pn", name = "a", descriptor = "(III)V")
	public static void priorityIfAnimUpdate(@OriginalArg(0) int animId, @OriginalArg(2) int componentHash) {
		@Pc(8) PulseEvent event = PulseEventHandler.createPulseEvent(componentHash, 5);
		event.queueHighPriority();
		event.primary = animId;
	}

	@OriginalMember(owner = "client!pr", name = "e", descriptor = "(II)V")
	public static void delayedIfAngleUpdate(@OriginalArg(0) int idHash) {
		@Pc(8) PulseEvent event = PulseEventHandler.createPulseEvent(idHash, 8);
		event.queueLowPriority();
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(Ljava/lang/String;II)V")
	public static void priorityVarcstringUpdate(@OriginalArg(0) String value, @OriginalArg(1) int varId) {
		@Pc(8) PulseEvent event = PulseEventHandler.createPulseEvent(varId, 2);
		event.queueHighPriority();
		event.string = value;
	}

	@OriginalMember(owner = "client!pba", name = "a", descriptor = "(IBI)V")
	public static void priorityIfVideoUpdate(@OriginalArg(0) int videoId, @OriginalArg(2) int componentHash) {
		@Pc(8) PulseEvent event = PulseEventHandler.createPulseEvent(componentHash, 17);
		event.queueHighPriority();
		event.primary = videoId;
	}

	@OriginalMember(owner = "client!mj", name = "a", descriptor = "(BI)V")
	public static void delayedIfHideUpdate(@OriginalArg(1) int componentHash) {
		@Pc(8) PulseEvent event = PulseEventHandler.createPulseEvent(componentHash, 7);
		event.queueLowPriority();
	}
	@OriginalMember(owner = "client!kk", name = "b", descriptor = "(II)V")
	public static void delayedIfItemUpdate(@OriginalArg(1) int componentHash) {
		@Pc(8) PulseEvent event = PulseEventHandler.createPulseEvent(componentHash, 9);
		event.queueLowPriority();
	}

	@OriginalMember(owner = "client!cf", name = "b", descriptor = "(BI)V")
	public static void delayedIfFontUpdate(@OriginalArg(1) int componentHash) {
		@Pc(8) PulseEvent event = PulseEventHandler.createPulseEvent(componentHash, 16);
		event.queueLowPriority();
	}

	@OriginalMember(owner = "client!fn", name = "a", descriptor = "(II)V")
	public static void delayedIfContentUpdate(@OriginalArg(0) int componentHash) {
		@Pc(16) PulseEvent event = PulseEventHandler.createPulseEvent(componentHash, 4);
		event.queueLowPriority();
	}

	@OriginalMember(owner = "client!eea", name = "a", descriptor = "(III)V")
	public static void priorityMinimapFlagUpdate(@OriginalArg(1) int flagY, @OriginalArg(2) int flagX) {
		@Pc(8) PulseEvent event = PulseEventHandler.createPulseEvent(0, 15);
		event.queueHighPriority();
		event.primary = flagX;
		event.secondary = flagY;
	}
	@OriginalMember(owner = "client!li", name = "a", descriptor = "(III)V")
	public static void priorityIfColourUpdate(@OriginalArg(1) int colour, @OriginalArg(2) int idHash) {
		@Pc(8) PulseEvent event = PulseEventHandler.createPulseEvent(idHash, 6);
		event.queueHighPriority();
		event.primary = colour;
	}

	@OriginalMember(owner = "client!b", name = "a", descriptor = "(IZ)V")
	public static void delayedVarcStringUpdate(@OriginalArg(0) int varcId) {
		@Pc(13) PulseEvent event = PulseEventHandler.createPulseEvent(varcId, 2);
		event.queueLowPriority();
	}
}

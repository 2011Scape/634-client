package com.jagex.game.runetek5.statics;

import com.jagex.game.runetek5.network.packet.TcpPacket;
import com.jagex.game.runetek5.QuickChatPhrase;
import com.jagex.game.runetek5.StackFrame;
import com.jagex.game.runetek5.cache.node.IntNode;
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable;
import com.jagex.game.runetek5.cache.node.Node;
import com.jagex.game.runetek5.cache.node.Queue;
import com.jagex.game.runetek5.cache.node.lru.LRUCache;
import com.jagex.game.runetek5.core.util.Arrays;
import com.jagex.game.runetek5.LocalisedText;
import com.jagex.game.runetek5.config.rsinterface.Component;
import com.jagex.game.runetek5.config.rsinterface.SubInterface;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.Calendar;
import java.util.Date;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.rsinterface.Interface;
import com.jagex.game.runetek5.utils.Utils;

public final class CS2State {

	@OriginalMember(owner = "client!kca", name = "b", descriptor = "Lclient!at;")
	public static Component activeComponent;

	@OriginalMember(owner = "client!kca", name = "c", descriptor = "Lclient!iia;")
	public static QuickChatPhrase currentQuickChatPhrase;

	@OriginalMember(owner = "client!kca", name = "u", descriptor = "Lclient!at;")
	public static Component activeComponent2;

	@OriginalMember(owner = "client!kca", name = "d", descriptor = "[Ljava/lang/String;")
	public static final String[] stringStack = new String[1000];

	@OriginalMember(owner = "client!kca", name = "f", descriptor = "I")
	public static int framePtr = 0;

	@OriginalMember(owner = "client!kca", name = "l", descriptor = "Ljava/util/Calendar;")
	public static final Calendar LOCAL_CALENDAR = Calendar.getInstance();

	@OriginalMember(owner = "client!kca", name = "t", descriptor = "[Lclient!qea;")
	public static final StackFrame[] frames = new StackFrame[50];

	@OriginalMember(owner = "client!kca", name = "v", descriptor = "I")
	public static int stringStackPtr = 0;

	@OriginalMember(owner = "client!kca", name = "x", descriptor = "[I")
	public static final int[] globalArrayLengths = new int[5];

	@OriginalMember(owner = "client!kca", name = "y", descriptor = "I")
	public static int intStackPtr = 0;

	@OriginalMember(owner = "client!kca", name = "z", descriptor = "Lclient!jr;")
	public static final LRUCache aClass169_33 = new LRUCache(4);

	@OriginalMember(owner = "client!kca", name = "A", descriptor = "[I")
	public static final int[] coordConversionBuffer = new int[3];

	@OriginalMember(owner = "client!kca", name = "B", descriptor = "[Ljava/lang/String;")
	private static final String[] monthNames = new String[] { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

	@OriginalMember(owner = "client!kca", name = "C", descriptor = "I")
	public static int hookRequestCount = 0;

	@OriginalMember(owner = "client!kca", name = "a", descriptor = "(I)V")
	public static void sendToBack(@OriginalArg(0) int idHash) {
		@Pc(3) Component component = Interface.getComponent(idHash);
		if (component == null) {
			return;
		}
		@Pc(9) int interfaceId = idHash >>> 16;
		@Pc(13) Component[] components = Interface.aClass24ArrayArray1[interfaceId];
		if (components == null) {
			@Pc(19) Component[] components2 = Interface.interfaces[interfaceId];
			@Pc(22) int length = components2.length;
			components = Interface.aClass24ArrayArray1[interfaceId] = new Component[length];
			Arrays.copy(components2, 0, components, 0, components2.length);
		}
		@Pc(38) int index;
		for (index = 0; index < components.length && components[index] != component; index++) {
		}
		if (index >= components.length) {
			return;
		}
		Arrays.copy(components, 0, components, 1, index);
		components[0] = component;
	}

	@OriginalMember(owner = "client!kca", name = "a", descriptor = "(C)I")
	public static int isPrintableChar(@OriginalArg(0) char c) {
		return Utils.isPrintableChar(c) ? 1 : 0;
	}

	@OriginalMember(owner = "client!kca", name = "c", descriptor = "(I)Ljava/lang/String;")
	public static String runedayToDateString(@OriginalArg(0) int runeday) {
		@Pc(6) long millis = ((long) runeday + 11745L) * 86400000L;
		LOCAL_CALENDAR.setTime(new Date(millis));
		@Pc(16) int day = LOCAL_CALENDAR.get(5);
		@Pc(20) int month = LOCAL_CALENDAR.get(2);
		@Pc(24) int year = LOCAL_CALENDAR.get(1);
		return day + "-" + monthNames[month] + "-" + year;
	}

	@OriginalMember(owner = "client!kca", name = "d", descriptor = "(I)V")
	public static void sendToFront(@OriginalArg(0) int idHash) {
		@Pc(3) Component component = Interface.getComponent(idHash);
		if (component == null) {
			return;
		}
		@Pc(9) int interfaceId = idHash >>> 16;
		@Pc(13) Component[] components = Interface.aClass24ArrayArray1[interfaceId];
		if (components == null) {
			@Pc(19) Component[] components2 = Interface.interfaces[interfaceId];
			@Pc(22) int length = components2.length;
			components = Interface.aClass24ArrayArray1[interfaceId] = new Component[length];
			Arrays.copy(components2, 0, components, 0, components2.length);
		}
		@Pc(38) int index;
		for (index = 0; index < components.length && components[index] != component; index++) {
		}
		if (index >= components.length) {
			return;
		}
		Arrays.copy(components, index + 1, components, index, components.length - index - 1);
		components[components.length - 1] = component;
	}

	@OriginalMember(owner = "client!kca", name = "c", descriptor = "(IZ)V")
	public static void profileOutput() {
	}
}

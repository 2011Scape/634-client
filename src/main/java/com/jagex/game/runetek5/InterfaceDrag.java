package com.jagex.game.runetek5;

import com.jagex.game.runetek5.config.rsinterface.Component;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.rsinterface.Interface;
import com.jagex.game.runetek5.network.BufferedConnectionContext;
import com.jagex.game.runetek5.network.packet.ClientProt;
import com.jagex.game.runetek5.network.packet.TcpPacket;

public final class InterfaceDrag {

	@OriginalMember(owner = "client!client", name = "b", descriptor = "(Lclient!at;)Lclient!at;")
	public static Component getDragTarget(@OriginalArg(0) Component component) {
		@Pc(4) int depth = InterfaceManager.getComponentSettings(component).getDragDepth();
		if (depth == 0) {
			return null;
		}
		for (@Pc(10) int i = 0; i < depth; i++) {
			component = Interface.getComponent(component.parentId);
			if (component == null) {
				return null;
			}
		}
		return component;
	}
	@OriginalMember(owner = "client!vs", name = "f", descriptor = "Lclient!at;")
	public static Component component = null;
	@OriginalMember(owner = "client!wia", name = "e", descriptor = "Z")
	public static boolean isDragging = false;

	@OriginalMember(owner = "client!ak", name = "a", descriptor = "(ILclient!at;Lclient!at;)V")
	public static void sendDragOnto(@OriginalArg(1) Component component, @OriginalArg(2) Component component2) {
		@Pc(12) TcpPacket packet = TcpPacket.createPacket(ClientProt.IF_DRAG_ONTO_IF, TcpPacket.isaac);
		packet.buffer.writeInt(component.idHash);
		packet.buffer.writeShortLittle(component2.slotId);
		packet.buffer.writeIntMiddle(component2.idHash);
		packet.buffer.writeShort(component2.containerItemId);
		packet.buffer.writeShortAddLittle(component.slotId);
		packet.buffer.writeShortAddLittle(component.containerItemId);
		BufferedConnectionContext.queuePacket(packet);
	}

	@OriginalMember(owner = "client!wn", name = "a", descriptor = "(Lclient!at;B)Lclient!at;")
	public static Component getDragParent(@OriginalArg(0) Component component) {
		@Pc(14) Component component2 = InterfaceDrag.getDragTarget(component);
		if (component2 == null) {
			component2 = component.depthChildOverride;
		}
		return component2;
	}

	@OriginalMember(owner = "client!cga", name = "i", descriptor = "I")
	public static final int DRAG_RENDER_OPAQUE = 2;

	@OriginalMember(owner = "client!bo", name = "g", descriptor = "I")
	public static final int DRAG_RENDER_IN_LAYER = 1;

	@OriginalMember(owner = "client!sb", name = "k", descriptor = "I")
	public static int dragOffsetY = 0;

	@OriginalMember(owner = "client!wga", name = "Jc", descriptor = "I")
	public static int dragOriginX = -1;
	@OriginalMember(owner = "client!jda", name = "m", descriptor = "I")
	public static int dragOffsetX = 0;

	@OriginalMember(owner = "client!fd", name = "M", descriptor = "I")
	public static int dragParentX = -1;

	@OriginalMember(owner = "client!gb", name = "r", descriptor = "Z")
	public static boolean dragComponentFound = false;

	@OriginalMember(owner = "client!lf", name = "Z", descriptor = "Lclient!at;")
	public static Component aClass24_13 = null;
	@OriginalMember(owner = "client!ab", name = "y", descriptor = "I")
	public static int draggedComponentOffsetX;

	@OriginalMember(owner = "client!nv", name = "f", descriptor = "I")
	public static int draggedComponentOffsetY;

	@OriginalMember(owner = "client!dia", name = "c", descriptor = "Lclient!at;")
	public static Component aClass24_7 = null;

	@OriginalMember(owner = "client!ep", name = "t", descriptor = "Z")
	public static boolean dragParentFound = false;
}

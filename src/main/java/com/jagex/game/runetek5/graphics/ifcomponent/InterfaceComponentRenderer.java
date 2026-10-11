package com.jagex.game.runetek5.graphics.ifcomponent;

import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.OriginalArg;
import com.jagex.game.runetek5.config.rsinterface.Component;
import com.jagex.game.runetek5.client.menu.MiniMenu;
import com.jagex.game.runetek5.config.rsinterface.ComponentType;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.graphics.Matrix;

public final class InterfaceComponentRenderer {
	@OriginalMember(owner = "client!gea", name = "g", descriptor = "I")
	public static int chatDisplayX = -1;

	@OriginalMember(owner = "client!nfa", name = "d", descriptor = "I")
	public static int chatDisplayY = -1;

	@OriginalMember(owner = "client!mca", name = "t", descriptor = "I")
	public static int worldMapRequestedScrollX = -1;

	@OriginalMember(owner = "client!ou", name = "s", descriptor = "I")
	public static int consoleMessageCount = 0;

	@OriginalMember(owner = "client!vaa", name = "I", descriptor = "I")
	public static int menuHeight;

	@OriginalMember(owner = "client!ku", name = "b", descriptor = "I")
	public static int deferredDragOffsetY = 0;

	private InterfaceComponentRenderer() {
		/* empty */
	}
	@OriginalMember(owner = "client!iaa", name = "a", descriptor = "(II)V")
	public static void setDeferredDragOffsetY(@OriginalArg(1) int deferredDragOffsetY) {
		InterfaceComponentRenderer.deferredDragOffsetY = deferredDragOffsetY;
	}
	@OriginalMember(owner = "client!lj", name = "a", descriptor = "(Lclient!at;ZII)V")
	public static void setChatDisplay(@OriginalArg(0) Component component, @OriginalArg(2) int chatDisplayX, @OriginalArg(3) int chatDisplayY) {
		InterfaceComponentRenderer.chatDisplayX = chatDisplayX;
		InterfaceComponentRenderer.chatDisplayY = chatDisplayY;
		MiniMenu.component = component;
	}
	@OriginalMember(owner = "client!hh", name = "a", descriptor = "(Lclient!at;I)V")
	public static void requestItemSprite(@OriginalArg(0) Component component) {
		if (component.type == ComponentType.SPRITE && component.containerItemId != -1) {
			Component.queueItemSpriteIfMissing(ClientState.activeToolkit, component);
		}
	}
	@OriginalMember(owner = "client!gb", name = "f", descriptor = "Lclient!bca;")
	public static Matrix aClass30_6;
}

package com.jagex.game.runetek5.graphics.ifcomponent

import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.OriginalArg
import com.jagex.game.runetek5.config.rsinterface.Component
import com.jagex.game.runetek5.client.menu.MiniMenu
import com.jagex.game.runetek5.config.rsinterface.ComponentType
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.graphics.Matrix

object InterfaceComponentRenderer {
	@OriginalMember(owner = "client!gea", name = "g", descriptor = "I")
	@JvmField
	var chatDisplayX: Int = -1

	@OriginalMember(owner = "client!nfa", name = "d", descriptor = "I")
	@JvmField
	var chatDisplayY: Int = -1

	@OriginalMember(owner = "client!mca", name = "t", descriptor = "I")
	@JvmField
	var worldMapRequestedScrollX: Int = -1

	@OriginalMember(owner = "client!ou", name = "s", descriptor = "I")
	@JvmField
	var consoleMessageCount: Int = 0

	@OriginalMember(owner = "client!vaa", name = "I", descriptor = "I")
	@JvmField
	var menuHeight: Int = 0

	@OriginalMember(owner = "client!ku", name = "b", descriptor = "I")
	@JvmField
	var deferredDragOffsetY: Int = 0

	@OriginalMember(owner = "client!iaa", name = "a", descriptor = "(II)V")
	@JvmStatic
	fun setDeferredDragOffsetY(@OriginalArg(1) deferredDragOffsetY: Int) {
		InterfaceComponentRenderer.deferredDragOffsetY = deferredDragOffsetY
	}
	@OriginalMember(owner = "client!lj", name = "a", descriptor = "(Lclient!at;ZII)V")
	@JvmStatic
	fun setChatDisplay(@OriginalArg(0) component: Component?, @OriginalArg(2) chatDisplayX: Int, @OriginalArg(3) chatDisplayY: Int) {
		InterfaceComponentRenderer.chatDisplayX = chatDisplayX
		InterfaceComponentRenderer.chatDisplayY = chatDisplayY
		MiniMenu.component = component
	}
	@OriginalMember(owner = "client!hh", name = "a", descriptor = "(Lclient!at;I)V")
	@JvmStatic
	fun requestItemSprite(@OriginalArg(0) component: Component?) {
		if (component!!.type === ComponentType.SPRITE && component.containerItemId != -1) {
			Component.queueItemSpriteIfMissing(ClientState.activeToolkit, component)
		}
	}
	@OriginalMember(owner = "client!gb", name = "f", descriptor = "Lclient!bca;")
	@JvmField
	var aClass30_6: Matrix? = null
}

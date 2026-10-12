package com.jagex.game.runetek5

import com.jagex.game.runetek5.config.rsinterface.Component
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.rsinterface.Interface
import com.jagex.game.runetek5.network.BufferedConnectionContext
import com.jagex.game.runetek5.network.packet.ClientProt
import com.jagex.game.runetek5.network.packet.TcpPacket
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

object InterfaceDrag {

	@OriginalMember(owner = "client!client", name = "b", descriptor = "(Lclient!at;)Lclient!at;")
	@JvmStatic
	fun getDragTarget(@OriginalArg(0) component: Component?): Component? {
		var component: Component? = component
		@Pc(4) val depth: Int = InterfaceManager.getComponentSettings(component)!!.getDragDepth()
		if (depth == 0) {
			return null
		}
		@Pc(10) var i: Int = 0
		while (i < depth) {
			component = Interface.getComponent(component!!.parentId)
			if (component == null) {
				return null
			}
			i++
		}
		return component
	}

	@OriginalMember(owner = "client!vs", name = "f", descriptor = "Lclient!at;")
	@JvmField
	var component: Component? = null

	@OriginalMember(owner = "client!wia", name = "e", descriptor = "Z")
	@JvmField
	var isDragging: Boolean = false

	@OriginalMember(owner = "client!ak", name = "a", descriptor = "(ILclient!at;Lclient!at;)V")
	@JvmStatic
	fun sendDragOnto(@OriginalArg(1) component: Component?, @OriginalArg(2) component2: Component?) {
		@Pc(12) val packet: TcpPacket = TcpPacket.createPacket(ClientProt.IF_DRAG_ONTO_IF, TcpPacket.isaac)
		packet.buffer!!.writeInt(component!!.idHash)
		packet.buffer!!.writeShortLittle(component2!!.slotId)
		packet.buffer!!.writeIntMiddle(component2.idHash)
		packet.buffer!!.writeShort(component2.containerItemId)
		packet.buffer!!.writeShortAddLittle(component.slotId)
		packet.buffer!!.writeShortAddLittle(component.containerItemId)
		BufferedConnectionContext.queuePacket(packet)
	}

	@OriginalMember(owner = "client!wn", name = "a", descriptor = "(Lclient!at;B)Lclient!at;")
	@JvmStatic
	fun getDragParent(@OriginalArg(0) component: Component?): Component? {
		@Pc(14) var component2: Component? = InterfaceDrag.getDragTarget(component)
		if (component2 == null) {
			component2 = component!!.depthChildOverride
		}
		return component2
	}

	@OriginalMember(owner = "client!cga", name = "i", descriptor = "I")
	const val DRAG_RENDER_OPAQUE: Int = 2

	@OriginalMember(owner = "client!bo", name = "g", descriptor = "I")
	const val DRAG_RENDER_IN_LAYER: Int = 1

	@OriginalMember(owner = "client!sb", name = "k", descriptor = "I")
	@JvmField
	var dragOffsetY: Int = 0

	@OriginalMember(owner = "client!wga", name = "Jc", descriptor = "I")
	@JvmField
	var dragOriginX: Int = -1

	@OriginalMember(owner = "client!jda", name = "m", descriptor = "I")
	@JvmField
	var dragOffsetX: Int = 0

	@OriginalMember(owner = "client!fd", name = "M", descriptor = "I")
	@JvmField
	var dragParentX: Int = -1

	@OriginalMember(owner = "client!gb", name = "r", descriptor = "Z")
	@JvmField
	var dragComponentFound: Boolean = false

	@OriginalMember(owner = "client!lf", name = "Z", descriptor = "Lclient!at;")
	@JvmField
	var aClass24_13: Component? = null

	@OriginalMember(owner = "client!ab", name = "y", descriptor = "I")
	@JvmField
	var draggedComponentOffsetX: Int = 0

	@OriginalMember(owner = "client!nv", name = "f", descriptor = "I")
	@JvmField
	var draggedComponentOffsetY: Int = 0

	@OriginalMember(owner = "client!dia", name = "c", descriptor = "Lclient!at;")
	@JvmField
	var aClass24_7: Component? = null

	@OriginalMember(owner = "client!ep", name = "t", descriptor = "Z")
	@JvmField
	var dragParentFound: Boolean = false
}

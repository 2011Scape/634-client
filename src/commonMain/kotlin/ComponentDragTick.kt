import com.jagex.game.runetek5.InterfaceDrag
import com.jagex.game.runetek5.client.menu.MiniMenu
import com.jagex.game.runetek5.DragOriginY
import com.jagex.game.runetek5.config.rsinterface.HookRequest
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.graphics.ifcomponent.InterfaceRenderer
import com.jagex.game.runetek5.interaction.chat.friendschat.FriendsChat
import com.jagex.game.runetek5.client.input.mouse.MouseRecord
import com.jagex.game.runetek5.config.rsinterface.Component
import com.jagex.game.runetek5.OrthoMode
import com.jagex.game.runetek5.client.ClientState
import kotlin.jvm.JvmStatic

object ComponentDragTick {

	@OriginalMember(owner = "client!eg", name = "a", descriptor = "(I)V")
	@JvmStatic
	fun processDrag() {
		Component.redrawComponent(InterfaceDrag.component)
		InterfaceRenderer.dragTickCount++
		if (InterfaceDrag.dragComponentFound && InterfaceDrag.dragParentFound) {
			@Pc(26) var orthoOffsetX: Int = 0
			@Pc(28) var orthoOffsetY: Int = 0
			if (OrthoMode.orthoEnabled) {
				orthoOffsetX = OrthoMode.getOrthoOffsetX()
				orthoOffsetY = OrthoMode.getOrthoDrawOffsetY()
			}
			@Pc(42) var dragX: Int = orthoOffsetX + MouseRecord.mouseRecorder!!.getMouseX()
			@Pc(48) var dragY: Int = orthoOffsetY + MouseRecord.mouseRecorder!!.getMouseY()
			dragY -= InterfaceDrag.dragOffsetY
			dragX -= InterfaceDrag.dragOffsetX
			if (dragX < InterfaceDrag.dragParentX) {
				dragX = InterfaceDrag.dragParentX
			}
			if (dragX + InterfaceDrag.component!!.width > InterfaceDrag.dragParentX - -InterfaceDrag.aClass24_7!!.width) {
				dragX = InterfaceDrag.aClass24_7!!.width + InterfaceDrag.dragParentX - InterfaceDrag.component!!.width
			}
			if (dragY < FriendsChat.dragParentY) {
				dragY = FriendsChat.dragParentY
			}
			if (InterfaceDrag.component!!.height + dragY > FriendsChat.dragParentY - -InterfaceDrag.aClass24_7!!.height) {
				dragY = FriendsChat.dragParentY + InterfaceDrag.aClass24_7!!.height - InterfaceDrag.component!!.height
			}
			@Pc(119) val scrollX: Int = dragX + InterfaceDrag.aClass24_7!!.scrollX - InterfaceDrag.dragParentX
			@Pc(127) val scrollY: Int = dragY + InterfaceDrag.aClass24_7!!.scrollY - FriendsChat.dragParentY
			@Pc(179) var hookRequest: HookRequest
			if (MouseRecord.mouseRecorder!!.isAnyButtonDown()) {
				if (InterfaceDrag.component!!.dragDeadTime < InterfaceRenderer.dragTickCount) {
					@Pc(218) val dragDeltaX: Int = dragX - InterfaceDrag.dragOriginX
					@Pc(223) val dragDeltaY: Int = dragY - DragOriginY.dragOriginY
					if (InterfaceDrag.component!!.dragDeadzone < dragDeltaX || -InterfaceDrag.component!!.dragDeadzone > dragDeltaX || dragDeltaY > InterfaceDrag.component!!.dragDeadzone || -InterfaceDrag.component!!.dragDeadzone > dragDeltaY) {
						InterfaceDrag.isDragging = true
					}
				}
				if (InterfaceDrag.component!!.onDrag != null && InterfaceDrag.isDragging) {
					hookRequest = HookRequest()
					hookRequest.source = InterfaceDrag.component
					hookRequest.mouseX = scrollX
					hookRequest.params = InterfaceDrag.component!!.onDrag
					hookRequest.mouseY = scrollY
					CS2Interpreter.executeHookInner(hookRequest)
				}
			} else {
				if (InterfaceDrag.isDragging) {
					Static657.cancelInterfaceSelection()
					if (InterfaceDrag.component!!.onDragComplete != null) {
						hookRequest = HookRequest()
						hookRequest.targetComponent = InterfaceDrag.aClass24_13
						hookRequest.mouseX = scrollX
						hookRequest.params = InterfaceDrag.component!!.onDragComplete
						hookRequest.mouseY = scrollY
						hookRequest.source = InterfaceDrag.component
						CS2Interpreter.executeHookInner(hookRequest)
					}
					if (InterfaceDrag.aClass24_13 != null && InterfaceDrag.getDragTarget(InterfaceDrag.component) != null) {
						InterfaceDrag.sendDragOnto(InterfaceDrag.component, InterfaceDrag.aClass24_13)
					}
				} else if ((MiniMenu.menuOpenMode == 1 || MiniMenu.isTopEntryInterfaceAction()) && MiniMenu.entryCount > 2) {
					MiniMenuDeferredClick.processDeferredClick(InterfaceDrag.dragOriginX + InterfaceDrag.dragOffsetX, InterfaceDrag.dragOffsetY - -DragOriginY.dragOriginY)
				} else if (MiniMenu.hasEntries()) {
					MiniMenuDeferredClick.processDeferredClick(InterfaceDrag.dragOriginX + InterfaceDrag.dragOffsetX, InterfaceDrag.dragOffsetY + DragOriginY.dragOriginY)
				}
				InterfaceDrag.component = null
			}
		} else if (InterfaceRenderer.dragTickCount > 1) {
			InterfaceDrag.component = null
		}
	}
}

package com.jagex.game.runetek5

import com.jagex.game.runetek5.core.datastruct.key.Deque
import com.jagex.game.runetek5.client.input.mouse.MouseRecord
import com.jagex.game.runetek5.config.rsinterface.Component
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.input.mouse.SimpleMouseLog
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.utils.Utils

@OriginalClass("client!bd")
class LegacyMouseManager @OriginalMember(owner = "client!bd", name = "<init>", descriptor = "(Ljava/awt/Component;Z)V") constructor(@OriginalArg(0) component: java.awt.Component?, @OriginalArg(1) logMovement: Boolean) : AbstractMouseManager(), MouseListener, MouseMotionListener {

	@OriginalMember(owner = "client!bd", name = "j", descriptor = "I")
	private var snapshotClickMask: Int = 0

	@OriginalMember(owner = "client!bd", name = "t", descriptor = "I")
	private var snapshotMouseX: Int = 0

	@OriginalMember(owner = "client!bd", name = "z", descriptor = "I")
	private var snapshotMouseY: Int = 0

	@OriginalMember(owner = "client!bd", name = "E", descriptor = "I")
	private var mouseY: Int = 0

	@OriginalMember(owner = "client!bd", name = "F", descriptor = "I")
	private var mouseX: Int = 0

	@OriginalMember(owner = "client!bd", name = "H", descriptor = "I")
	private var clickMask: Int = 0

	@OriginalMember(owner = "client!bd", name = "K", descriptor = "Ljava/awt/Component;")
	private var component: java.awt.Component? = null

	@OriginalMember(owner = "client!bd", name = "s", descriptor = "Lclient!uh;")
	private var pendingMouseActions: Deque? = Deque()

	@OriginalMember(owner = "client!bd", name = "B", descriptor = "Lclient!uh;")
	private var loggedMouseActions: Deque? = Deque()

	@OriginalMember(owner = "client!bd", name = "J", descriptor = "Z")
	private val logMovement: Boolean

	init {
		this.listen(component)
		this.logMovement = logMovement
	}

	@OriginalMember(owner = "client!bd", name = "a", descriptor = "(BLjava/awt/Component;)V")
	private fun listen(@OriginalArg(1) component: java.awt.Component?) {
		this.reset()
		this.component = component
		this.component!!.addMouseListener(this)
		this.component!!.addMouseMotionListener(this)
	}

	@OriginalMember(owner = "client!bd", name = "mouseDragged", descriptor = "(Ljava/awt/event/MouseEvent;)V")
	@Synchronized
	override fun mouseDragged(@OriginalArg(0) mouseEvent: java.awt.event.MouseEvent?) {
		this.setPosition(mouseEvent!!.getY(), mouseEvent.getX())
	}

	@OriginalMember(owner = "client!bd", name = "mouseEntered", descriptor = "(Ljava/awt/event/MouseEvent;)V")
	@Synchronized
	override fun mouseEntered(@OriginalArg(0) mouseEvent: java.awt.event.MouseEvent?) {
		this.setPosition(mouseEvent!!.getY(), mouseEvent.getX())
	}

	@OriginalMember(owner = "client!bd", name = "a", descriptor = "(ILjava/awt/event/MouseEvent;)I")
	private fun getClickMask(@OriginalArg(1) mouseEvent: java.awt.event.MouseEvent?): Int {
		@Pc(6) val modifiers: Int = mouseEvent!!.getModifiers()
		@Pc(14) val leftDown: Boolean = (modifiers and 0x10) != 0
		@Pc(22) var middleDown: Boolean = (modifiers and 0x8) != 0
		@Pc(30) val rightDown: Boolean = (modifiers and 0x4) != 0
		if (middleDown && (leftDown || rightDown)) {
			middleDown = false
		}
		if (leftDown && rightDown) {
			return 4
		} else if (leftDown) {
			return 1
		} else if (middleDown) {
			return 2
		} else if (rightDown) {
			return 4
		} else {
			return 0
		}
	}

	@OriginalMember(owner = "client!bd", name = "mouseExited", descriptor = "(Ljava/awt/event/MouseEvent;)V")
	@Synchronized
	override fun mouseExited(@OriginalArg(0) mouseEvent: java.awt.event.MouseEvent?) {
		this.setPosition(mouseEvent!!.getY(), mouseEvent.getX())
	}

	@OriginalMember(owner = "client!bd", name = "a", descriptor = "(B)Z")
	override fun isRightButtonDown(): Boolean {
		return (this.snapshotClickMask and 0x4) != 0
	}

	@OriginalMember(owner = "client!bd", name = "b", descriptor = "(I)V")
	@Synchronized
	override fun mainLogic() {
		this.snapshotClickMask = this.clickMask
		this.snapshotMouseY = this.mouseY
		this.snapshotMouseX = this.mouseX
		@Pc(23) val deque: Deque? = this.pendingMouseActions
		this.pendingMouseActions = this.loggedMouseActions
		this.loggedMouseActions = deque
		this.loggedMouseActions!!.clear()
	}

	@OriginalMember(owner = "client!bd", name = "mouseMoved", descriptor = "(Ljava/awt/event/MouseEvent;)V")
	@Synchronized
	override fun mouseMoved(@OriginalArg(0) mouseEvent: java.awt.event.MouseEvent?) {
		this.setPosition(mouseEvent!!.getY(), mouseEvent.getX())
	}

	@OriginalMember(owner = "client!bd", name = "a", descriptor = "(IBI)V")
	private fun setPosition(@OriginalArg(0) y: Int, @OriginalArg(2) x: Int) {
		this.mouseX = x
		this.mouseY = y
		if (this.logMovement) {
			this.log(-1, y, x, 0)
		}
	}

	@OriginalMember(owner = "client!bd", name = "b", descriptor = "(B)I")
	override fun getMouseY(): Int {
		return this.snapshotMouseY
	}

	@OriginalMember(owner = "client!bd", name = "f", descriptor = "(I)Lclient!bu;")
	override fun nextSubmission(): MouseRecord? {
		return this.pendingMouseActions!!.removeFirst() as MouseRecord?
	}

	@OriginalMember(owner = "client!bd", name = "a", descriptor = "(IIIII)V")
	private fun log(@OriginalArg(0) clickType: Int, @OriginalArg(1) y: Int, @OriginalArg(2) x: Int, @OriginalArg(3) clickCount: Int) {
		@Pc(7) val simpleMouseLog: SimpleMouseLog = SimpleMouseLog()
		simpleMouseLog.mouseX = x
		simpleMouseLog.mouseY = y
		simpleMouseLog.meta = clickCount
		simpleMouseLog.clickType = clickType
		simpleMouseLog.time = Utils.currentTimeMillis()
		this.loggedMouseActions!!.addLast(simpleMouseLog)
	}

	@OriginalMember(owner = "client!bd", name = "mouseClicked", descriptor = "(Ljava/awt/event/MouseEvent;)V")
	@Synchronized
	override fun mouseClicked(@OriginalArg(0) mouseEvent: java.awt.event.MouseEvent?) {
		if (mouseEvent!!.isPopupTrigger()) {
			mouseEvent.consume()
		}
	}

	@OriginalMember(owner = "client!bd", name = "c", descriptor = "(I)V")
	override fun remove() {
		this.reset()
	}

	@OriginalMember(owner = "client!bd", name = "a", descriptor = "(I)Z")
	override fun isMiddleButtonDown(): Boolean {
		return (this.snapshotClickMask and 0x2) != 0
	}

	@OriginalMember(owner = "client!bd", name = "e", descriptor = "(I)Z")
	override fun isLeftButtonDown(): Boolean {
		return (this.snapshotClickMask and 0x1) != 0
	}

	@OriginalMember(owner = "client!bd", name = "mouseReleased", descriptor = "(Ljava/awt/event/MouseEvent;)V")
	@Synchronized
	override fun mouseReleased(@OriginalArg(0) mouseEvent: java.awt.event.MouseEvent?) {
		@Pc(8) var mask: Int = this.getClickMask(mouseEvent)
		if ((mask and this.clickMask) == 0) {
			mask = this.clickMask
		}
		if ((mask and 0x1) != 0) {
			this.log(3, mouseEvent!!.getY(), mouseEvent.getX(), mouseEvent.getClickCount())
		}
		if ((mask and 0x4) != 0) {
			this.log(5, mouseEvent!!.getY(), mouseEvent.getX(), mouseEvent.getClickCount())
		}
		if ((mask and 0x2) != 0) {
			this.log(4, mouseEvent!!.getY(), mouseEvent.getX(), mouseEvent.getClickCount())
		}
		this.clickMask = this.clickMask and mask.inv()
		if (mouseEvent!!.isPopupTrigger()) {
			mouseEvent.consume()
		}
	}

	@OriginalMember(owner = "client!bd", name = "g", descriptor = "(I)V")
	private fun reset() {
		if (this.component == null) {
			return
		}
		this.component!!.removeMouseMotionListener(this)
		this.component!!.removeMouseListener(this)
		this.component = null
		this.pendingMouseActions = null
		this.loggedMouseActions = null
		this.snapshotClickMask = 0
		this.snapshotMouseY = this.snapshotClickMask
		this.snapshotMouseX = this.snapshotMouseY
		this.clickMask = 0
		this.mouseY = this.clickMask
		this.mouseX = this.mouseY
	}

	@OriginalMember(owner = "client!bd", name = "a", descriptor = "(Z)I")
	override fun getMouseX(): Int {
		return this.snapshotMouseX
	}

	@OriginalMember(owner = "client!bd", name = "mousePressed", descriptor = "(Ljava/awt/event/MouseEvent;)V")
	@Synchronized
	override fun mousePressed(@OriginalArg(0) mouseEvent: java.awt.event.MouseEvent?) {
		@Pc(10) val mask: Int = this.getClickMask(mouseEvent)
		if (mask == 1) {
			this.log(0, mouseEvent!!.getY(), mouseEvent.getX(), mouseEvent.getClickCount())
		} else if (mask == 4) {
			this.log(2, mouseEvent!!.getY(), mouseEvent.getX(), mouseEvent.getClickCount())
		} else if (mask == 2) {
			this.log(1, mouseEvent!!.getY(), mouseEvent.getX(), mouseEvent.getClickCount())
		}
		this.clickMask = this.clickMask or mask
		if (mouseEvent!!.isPopupTrigger()) {
			mouseEvent.consume()
		}
	}
}

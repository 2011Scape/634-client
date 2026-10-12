package com.jagex.game.runetek5.client.input.mouse

import com.jagex.game.runetek5.AbstractMouseManager
import com.jagex.game.runetek5.core.datastruct.key.Deque
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import java.awt.event.MouseWheelEvent
import java.awt.event.MouseWheelListener
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.event.mouse.MouseLog
import com.jagex.game.runetek5.utils.Utils

@OriginalClass("client!aea")
class MouseManager @OriginalMember(owner = "client!aea", name = "<init>", descriptor = "(Ljava/awt/Component;Z)V") constructor(@OriginalArg(0) component: java.awt.Component?, @OriginalArg(1) canvasInitialized: Boolean) : AbstractMouseManager(), MouseListener, MouseMotionListener, MouseWheelListener {

	@OriginalMember(owner = "client!aea", name = "d", descriptor = "I")
	private var mouseY: Int = 0

	@OriginalMember(owner = "client!aea", name = "e", descriptor = "I")
	private var mouseX: Int = 0

	@OriginalMember(owner = "client!aea", name = "g", descriptor = "I")
	private var clickMask: Int = 0

	@OriginalMember(owner = "client!aea", name = "i", descriptor = "I")
	private var snapshotMouseY: Int = 0

	@OriginalMember(owner = "client!aea", name = "j", descriptor = "I")
	private var clickState: Int = 0

	@OriginalMember(owner = "client!aea", name = "k", descriptor = "I")
	private var snapshotMouseX: Int = 0

	@OriginalMember(owner = "client!aea", name = "m", descriptor = "Ljava/awt/Component;")
	private var canvasComponent: java.awt.Component? = null

	@OriginalMember(owner = "client!aea", name = "f", descriptor = "Lclient!uh;")
	private var pendingMouseActions: Deque? = Deque()

	@OriginalMember(owner = "client!aea", name = "h", descriptor = "Lclient!uh;")
	private var logged: Deque? = Deque()

	@OriginalMember(owner = "client!aea", name = "l", descriptor = "Z")
	private val canvasInitialized: Boolean

	init {
		this.listen(component)
		this.canvasInitialized = canvasInitialized
	}

	@OriginalMember(owner = "client!aea", name = "a", descriptor = "(I)Z")
	override fun isMiddleButtonDown(): Boolean {
		return (this.clickMask and 0x2) != 0
	}

	@OriginalMember(owner = "client!aea", name = "a", descriptor = "(IIIIZ)V")
	private fun log(@OriginalArg(0) y: Int, @OriginalArg(1) clickType: Int, @OriginalArg(2) meta: Int, @OriginalArg(3) eventY: Int) {
		@Pc(3) val log: CompleteMouseLog = CompleteMouseLog()
		log.mouseY = eventY
		log.clickType = clickType
		log.extra = y
		log.time = Utils.currentTimeMillis()
		log.meta = meta
		this.logged!!.addLast(log)
	}

	@OriginalMember(owner = "client!aea", name = "mouseReleased", descriptor = "(Ljava/awt/event/MouseEvent;)V")
	@Synchronized
	override fun mouseReleased(@OriginalArg(0) mouseEvent: java.awt.event.MouseEvent) {
		@Pc(4) var buttonMask: Int = this.getClickMask(mouseEvent)
		if ((buttonMask and this.clickState) == 0) {
			buttonMask = this.clickState
		}
		if ((buttonMask and 0x1) != 0) {
			this.log(mouseEvent.getX(), 3, mouseEvent.getClickCount(), mouseEvent.getY())
		}
		if ((buttonMask and 0x4) != 0) {
			this.log(mouseEvent.getX(), 5, mouseEvent.getClickCount(), mouseEvent.getY())
		}
		if ((buttonMask and 0x2) != 0) {
			this.log(mouseEvent.getX(), 4, mouseEvent.getClickCount(), mouseEvent.getY())
		}
		this.clickState = this.clickState and buttonMask.inv()
		if (mouseEvent.isPopupTrigger()) {
			mouseEvent.consume()
		}
	}

	@OriginalMember(owner = "client!aea", name = "b", descriptor = "(B)I")
	override fun getMouseY(): Int {
		return this.mouseY
	}

	@OriginalMember(owner = "client!aea", name = "a", descriptor = "(III)V")
	private fun setPosition(@OriginalArg(0) snapshotMouseX: Int, @OriginalArg(2) x: Int) {
		this.snapshotMouseY = x
		this.snapshotMouseX = snapshotMouseX
		if (this.canvasInitialized) {
			this.log(snapshotMouseX, -1, 0, x)
		}
	}

	@OriginalMember(owner = "client!aea", name = "a", descriptor = "(B)Z")
	override fun isRightButtonDown(): Boolean {
		return (this.clickMask and 0x4) != 0
	}

	@OriginalMember(owner = "client!aea", name = "mousePressed", descriptor = "(Ljava/awt/event/MouseEvent;)V")
	@Synchronized
	override fun mousePressed(@OriginalArg(0) mouseEvent: java.awt.event.MouseEvent) {
		@Pc(4) val buttonMask: Int = this.getClickMask(mouseEvent)
		if (buttonMask == 1) {
			this.log(mouseEvent.getX(), 0, mouseEvent.getClickCount(), mouseEvent.getY())
		} else if (buttonMask == 4) {
			this.log(mouseEvent.getX(), 2, mouseEvent.getClickCount(), mouseEvent.getY())
		} else if (buttonMask == 2) {
			this.log(mouseEvent.getX(), 1, mouseEvent.getClickCount(), mouseEvent.getY())
		}
		this.clickState = this.clickState or buttonMask
		if (mouseEvent.isPopupTrigger()) {
			mouseEvent.consume()
		}
	}

	@OriginalMember(owner = "client!aea", name = "a", descriptor = "(Z)I")
	override fun getMouseX(): Int {
		return this.mouseX
	}

	@OriginalMember(owner = "client!aea", name = "a", descriptor = "(Ljava/awt/event/MouseEvent;I)I")
	private fun getClickMask(@OriginalArg(0) mouseEvent: java.awt.event.MouseEvent): Int {
		if (mouseEvent.getButton() == 1) {
			return if (mouseEvent.isMetaDown()) 4 else 1
		} else if (mouseEvent.getButton() == 2) {
			return 2
		} else if (mouseEvent.getButton() == 3) {
			return 4
		} else {
			return 0
		}
	}

	@OriginalMember(owner = "client!aea", name = "mouseEntered", descriptor = "(Ljava/awt/event/MouseEvent;)V")
	@Synchronized
	override fun mouseEntered(@OriginalArg(0) mouseEvent: java.awt.event.MouseEvent) {
		this.setPosition(mouseEvent.getX(), mouseEvent.getY())
	}

	@OriginalMember(owner = "client!aea", name = "mouseClicked", descriptor = "(Ljava/awt/event/MouseEvent;)V")
	@Synchronized
	override fun mouseClicked(@OriginalArg(0) mouseEvent: java.awt.event.MouseEvent) {
		if (mouseEvent.isPopupTrigger()) {
			mouseEvent.consume()
		}
	}

	@OriginalMember(owner = "client!aea", name = "e", descriptor = "(I)Z")
	override fun isLeftButtonDown(): Boolean {
		return (this.clickMask and 0x1) != 0
	}

	@OriginalMember(owner = "client!aea", name = "f", descriptor = "(I)Lclient!bu;")
	override fun nextSubmission(): MouseRecord? {
		return this.pendingMouseActions!!.removeFirst() as MouseRecord?
	}

	@OriginalMember(owner = "client!aea", name = "mouseMoved", descriptor = "(Ljava/awt/event/MouseEvent;)V")
	@Synchronized
	override fun mouseMoved(@OriginalArg(0) mouseEvent: java.awt.event.MouseEvent) {
		this.setPosition(mouseEvent.getX(), mouseEvent.getY())
	}

	@OriginalMember(owner = "client!aea", name = "g", descriptor = "(I)V")
	private fun reset() {
		if (this.canvasComponent == null) {
			return
		}
		this.canvasComponent!!.removeMouseWheelListener(this)
		this.canvasComponent!!.removeMouseMotionListener(this)
		this.canvasComponent!!.removeMouseListener(this)
		this.canvasComponent = null
		this.clickState = 0
		this.snapshotMouseY = this.clickState
		this.snapshotMouseX = this.snapshotMouseY
		this.clickMask = 0
		this.mouseY = this.clickMask
		this.mouseX = this.mouseY
		this.pendingMouseActions = null
		this.logged = null
	}

	@OriginalMember(owner = "client!aea", name = "b", descriptor = "(I)V")
	@Synchronized
	override fun mainLogic() {
		this.mouseY = this.snapshotMouseY
		this.mouseX = this.snapshotMouseX
		this.clickMask = this.clickState
		@Pc(17) val deque: Deque? = this.pendingMouseActions
		this.pendingMouseActions = this.logged
		this.logged = deque
		this.logged!!.clear()
	}

	@OriginalMember(owner = "client!aea", name = "mouseWheelMoved", descriptor = "(Ljava/awt/event/MouseWheelEvent;)V")
	@Synchronized
	override fun mouseWheelMoved(@OriginalArg(0) mouseWheelEvent: MouseWheelEvent) {
		@Pc(2) val x: Int = mouseWheelEvent.getX()
		@Pc(5) val y: Int = mouseWheelEvent.getY()
		@Pc(8) val wheelRotation: Int = mouseWheelEvent.getWheelRotation()
		this.log(x, MouseLog.TYPE_SCROLL, wheelRotation, y)
		mouseWheelEvent.consume()
	}

	@OriginalMember(owner = "client!aea", name = "mouseExited", descriptor = "(Ljava/awt/event/MouseEvent;)V")
	@Synchronized
	override fun mouseExited(@OriginalArg(0) mouseEvent: java.awt.event.MouseEvent) {
		this.setPosition(mouseEvent.getX(), mouseEvent.getY())
	}

	@OriginalMember(owner = "client!aea", name = "mouseDragged", descriptor = "(Ljava/awt/event/MouseEvent;)V")
	@Synchronized
	override fun mouseDragged(@OriginalArg(0) mouseEvent: java.awt.event.MouseEvent) {
		this.setPosition(mouseEvent.getX(), mouseEvent.getY())
	}

	@OriginalMember(owner = "client!aea", name = "c", descriptor = "(I)V")
	override fun remove() {
		this.reset()
	}

	@OriginalMember(owner = "client!aea", name = "a", descriptor = "(ILjava/awt/Component;)V")
	private fun listen(@OriginalArg(1) component2: java.awt.Component?) {
		this.reset()
		this.canvasComponent = component2
		this.canvasComponent!!.addMouseListener(this)
		this.canvasComponent!!.addMouseMotionListener(this)
		this.canvasComponent!!.addMouseWheelListener(this)
	}

	companion object {
		@OriginalMember(owner = "client!ed", name = "U", descriptor = "I")
		@JvmField
		var lastSentMouseX: Int = -1

		@OriginalMember(owner = "client!afa", name = "o", descriptor = "I")
		@JvmField
		var lastSentMouseY: Int = -1

		@OriginalMember(owner = "client!hda", name = "h", descriptor = "J")
		@JvmField
		var lastClickTime: Long = -1L
		@OriginalMember(owner = "client!jb", name = "E", descriptor = "Lclient!uh;")
		@JvmField
		val MOUSE_MOVEMENTS: Deque = Deque()
	}
}

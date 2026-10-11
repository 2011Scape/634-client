package com.jagex.game.runetek5.client.input.mouse;

import com.jagex.game.runetek5.AbstractMouseManager;
import com.jagex.game.runetek5.core.datastruct.key.Deque;
import com.jagex.game.runetek5.client.input.mouse.MouseRecord;
import com.jagex.game.runetek5.config.rsinterface.Component;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.event.mouse.MouseLog;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!aea")
public final class MouseManager extends AbstractMouseManager implements MouseListener, MouseMotionListener, MouseWheelListener {

	@OriginalMember(owner = "client!aea", name = "d", descriptor = "I")
	private int mouseY;

	@OriginalMember(owner = "client!aea", name = "e", descriptor = "I")
	private int mouseX;

	@OriginalMember(owner = "client!aea", name = "g", descriptor = "I")
	private int clickMask;

	@OriginalMember(owner = "client!aea", name = "i", descriptor = "I")
	private int snapshotMouseY;

	@OriginalMember(owner = "client!aea", name = "j", descriptor = "I")
	private int clickState;

	@OriginalMember(owner = "client!aea", name = "k", descriptor = "I")
	private int snapshotMouseX;

	@OriginalMember(owner = "client!aea", name = "m", descriptor = "Ljava/awt/Component;")
	private java.awt.Component canvasComponent;

	@OriginalMember(owner = "client!aea", name = "f", descriptor = "Lclient!uh;")
	private Deque pendingMouseActions = new Deque();

	@OriginalMember(owner = "client!aea", name = "h", descriptor = "Lclient!uh;")
	private Deque logged = new Deque();

	@OriginalMember(owner = "client!aea", name = "l", descriptor = "Z")
	private final boolean canvasInitialized;

	@OriginalMember(owner = "client!aea", name = "<init>", descriptor = "(Ljava/awt/Component;Z)V")
	public MouseManager(@OriginalArg(0) java.awt.Component component, @OriginalArg(1) boolean canvasInitialized) {
		this.listen(component);
		this.canvasInitialized = canvasInitialized;
	}

	@OriginalMember(owner = "client!aea", name = "a", descriptor = "(I)Z")
	@Override
	public boolean isMiddleButtonDown() {
		return (this.clickMask & 0x2) != 0;
	}

	@OriginalMember(owner = "client!aea", name = "a", descriptor = "(IIIIZ)V")
	private void log(@OriginalArg(0) int y, @OriginalArg(1) int clickType, @OriginalArg(2) int meta, @OriginalArg(3) int eventY) {
		@Pc(3) CompleteMouseLog log = new CompleteMouseLog();
		log.mouseY = eventY;
		log.clickType = clickType;
		log.extra = y;
		log.time = Utils.currentTimeMillis();
		log.meta = meta;
		this.logged.addLast(log);
	}

	@OriginalMember(owner = "client!aea", name = "mouseReleased", descriptor = "(Ljava/awt/event/MouseEvent;)V")
	@Override
	public synchronized void mouseReleased(@OriginalArg(0) java.awt.event.MouseEvent mouseEvent) {
		@Pc(4) int buttonMask = this.getClickMask(mouseEvent);
		if ((buttonMask & this.clickState) == 0) {
			buttonMask = this.clickState;
		}
		if ((buttonMask & 0x1) != 0) {
			this.log(mouseEvent.getX(), 3, mouseEvent.getClickCount(), mouseEvent.getY());
		}
		if ((buttonMask & 0x4) != 0) {
			this.log(mouseEvent.getX(), 5, mouseEvent.getClickCount(), mouseEvent.getY());
		}
		if ((buttonMask & 0x2) != 0) {
			this.log(mouseEvent.getX(), 4, mouseEvent.getClickCount(), mouseEvent.getY());
		}
		this.clickState &= ~buttonMask;
		if (mouseEvent.isPopupTrigger()) {
			mouseEvent.consume();
		}
	}

	@OriginalMember(owner = "client!aea", name = "b", descriptor = "(B)I")
	@Override
	public int getMouseY() {
		return this.mouseY;
	}

	@OriginalMember(owner = "client!aea", name = "a", descriptor = "(III)V")
	private void setPosition(@OriginalArg(0) int snapshotMouseX, @OriginalArg(2) int x) {
		this.snapshotMouseY = x;
		this.snapshotMouseX = snapshotMouseX;
		if (this.canvasInitialized) {
			this.log(snapshotMouseX, -1, 0, x);
		}
	}

	@OriginalMember(owner = "client!aea", name = "a", descriptor = "(B)Z")
	@Override
	public boolean isRightButtonDown() {
		return (this.clickMask & 0x4) != 0;
	}

	@OriginalMember(owner = "client!aea", name = "mousePressed", descriptor = "(Ljava/awt/event/MouseEvent;)V")
	@Override
	public synchronized void mousePressed(@OriginalArg(0) java.awt.event.MouseEvent mouseEvent) {
		@Pc(4) int buttonMask = this.getClickMask(mouseEvent);
		if (buttonMask == 1) {
			this.log(mouseEvent.getX(), 0, mouseEvent.getClickCount(), mouseEvent.getY());
		} else if (buttonMask == 4) {
			this.log(mouseEvent.getX(), 2, mouseEvent.getClickCount(), mouseEvent.getY());
		} else if (buttonMask == 2) {
			this.log(mouseEvent.getX(), 1, mouseEvent.getClickCount(), mouseEvent.getY());
		}
		this.clickState |= buttonMask;
		if (mouseEvent.isPopupTrigger()) {
			mouseEvent.consume();
		}
	}

	@OriginalMember(owner = "client!aea", name = "a", descriptor = "(Z)I")
	@Override
	public int getMouseX() {
		return this.mouseX;
	}

	@OriginalMember(owner = "client!aea", name = "a", descriptor = "(Ljava/awt/event/MouseEvent;I)I")
	private int getClickMask(@OriginalArg(0) java.awt.event.MouseEvent mouseEvent) {
		if (mouseEvent.getButton() == 1) {
			return mouseEvent.isMetaDown() ? 4 : 1;
		} else if (mouseEvent.getButton() == 2) {
			return 2;
		} else if (mouseEvent.getButton() == 3) {
			return 4;
		} else {
			return 0;
		}
	}

	@OriginalMember(owner = "client!aea", name = "mouseEntered", descriptor = "(Ljava/awt/event/MouseEvent;)V")
	@Override
	public synchronized void mouseEntered(@OriginalArg(0) java.awt.event.MouseEvent mouseEvent) {
		this.setPosition(mouseEvent.getX(), mouseEvent.getY());
	}

	@OriginalMember(owner = "client!aea", name = "mouseClicked", descriptor = "(Ljava/awt/event/MouseEvent;)V")
	@Override
	public synchronized void mouseClicked(@OriginalArg(0) java.awt.event.MouseEvent mouseEvent) {
		if (mouseEvent.isPopupTrigger()) {
			mouseEvent.consume();
		}
	}

	@OriginalMember(owner = "client!aea", name = "e", descriptor = "(I)Z")
	@Override
	public boolean isLeftButtonDown() {
		return (this.clickMask & 0x1) != 0;
	}

	@OriginalMember(owner = "client!aea", name = "f", descriptor = "(I)Lclient!bu;")
	@Override
	public MouseRecord nextSubmission() {
		return (MouseRecord) this.pendingMouseActions.removeFirst();
	}

	@OriginalMember(owner = "client!aea", name = "mouseMoved", descriptor = "(Ljava/awt/event/MouseEvent;)V")
	@Override
	public synchronized void mouseMoved(@OriginalArg(0) java.awt.event.MouseEvent mouseEvent) {
		this.setPosition(mouseEvent.getX(), mouseEvent.getY());
	}

	@OriginalMember(owner = "client!aea", name = "g", descriptor = "(I)V")
	private void reset() {
		if (this.canvasComponent == null) {
			return;
		}
		this.canvasComponent.removeMouseWheelListener(this);
		this.canvasComponent.removeMouseMotionListener(this);
		this.canvasComponent.removeMouseListener(this);
		this.canvasComponent = null;
		this.snapshotMouseX = this.snapshotMouseY = this.clickState = 0;
		this.mouseX = this.mouseY = this.clickMask = 0;
		this.pendingMouseActions = null;
		this.logged = null;
	}

	@OriginalMember(owner = "client!aea", name = "b", descriptor = "(I)V")
	@Override
	public synchronized void mainLogic() {
		this.mouseY = this.snapshotMouseY;
		this.mouseX = this.snapshotMouseX;
		this.clickMask = this.clickState;
		@Pc(17) Deque deque = this.pendingMouseActions;
		this.pendingMouseActions = this.logged;
		this.logged = deque;
		this.logged.clear();
	}

	@OriginalMember(owner = "client!aea", name = "mouseWheelMoved", descriptor = "(Ljava/awt/event/MouseWheelEvent;)V")
	@Override
	public synchronized void mouseWheelMoved(@OriginalArg(0) MouseWheelEvent mouseWheelEvent) {
		@Pc(2) int x = mouseWheelEvent.getX();
		@Pc(5) int y = mouseWheelEvent.getY();
		@Pc(8) int wheelRotation = mouseWheelEvent.getWheelRotation();
		this.log(x, MouseLog.TYPE_SCROLL, wheelRotation, y);
		mouseWheelEvent.consume();
	}

	@OriginalMember(owner = "client!aea", name = "mouseExited", descriptor = "(Ljava/awt/event/MouseEvent;)V")
	@Override
	public synchronized void mouseExited(@OriginalArg(0) java.awt.event.MouseEvent mouseEvent) {
		this.setPosition(mouseEvent.getX(), mouseEvent.getY());
	}

	@OriginalMember(owner = "client!aea", name = "mouseDragged", descriptor = "(Ljava/awt/event/MouseEvent;)V")
	@Override
	public synchronized void mouseDragged(@OriginalArg(0) java.awt.event.MouseEvent mouseEvent) {
		this.setPosition(mouseEvent.getX(), mouseEvent.getY());
	}

	@OriginalMember(owner = "client!aea", name = "c", descriptor = "(I)V")
	@Override
	public void remove() {
		this.reset();
	}

	@OriginalMember(owner = "client!aea", name = "a", descriptor = "(ILjava/awt/Component;)V")
	private void listen(@OriginalArg(1) java.awt.Component component2) {
		this.reset();
		this.canvasComponent = component2;
		this.canvasComponent.addMouseListener(this);
		this.canvasComponent.addMouseMotionListener(this);
		this.canvasComponent.addMouseWheelListener(this);
	}
	@OriginalMember(owner = "client!ed", name = "U", descriptor = "I")
	public static int lastSentMouseX = -1;

	@OriginalMember(owner = "client!afa", name = "o", descriptor = "I")
	public static int lastSentMouseY = -1;

	@OriginalMember(owner = "client!hda", name = "h", descriptor = "J")
	public static long lastClickTime = -1L;
	@OriginalMember(owner = "client!jb", name = "E", descriptor = "Lclient!uh;")
	public static final Deque MOUSE_MOVEMENTS = new Deque();
}

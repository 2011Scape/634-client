package com.jagex.game.runetek5;

import com.jagex.game.runetek5.core.datastruct.key.Deque;
import com.jagex.game.runetek5.client.input.mouse.MouseRecord;
import com.jagex.game.runetek5.config.rsinterface.Component;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.input.mouse.SimpleMouseLog;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!bd")
public final class LegacyMouseManager extends AbstractMouseManager implements MouseListener, MouseMotionListener {

	@OriginalMember(owner = "client!bd", name = "j", descriptor = "I")
	private int snapshotClickMask;

	@OriginalMember(owner = "client!bd", name = "t", descriptor = "I")
	private int snapshotMouseX;

	@OriginalMember(owner = "client!bd", name = "z", descriptor = "I")
	private int snapshotMouseY;

	@OriginalMember(owner = "client!bd", name = "E", descriptor = "I")
	private int mouseY;

	@OriginalMember(owner = "client!bd", name = "F", descriptor = "I")
	private int mouseX;

	@OriginalMember(owner = "client!bd", name = "H", descriptor = "I")
	private int clickMask;

	@OriginalMember(owner = "client!bd", name = "K", descriptor = "Ljava/awt/Component;")
	private java.awt.Component component;

	@OriginalMember(owner = "client!bd", name = "s", descriptor = "Lclient!uh;")
	private Deque pendingMouseActions = new Deque();

	@OriginalMember(owner = "client!bd", name = "B", descriptor = "Lclient!uh;")
	private Deque loggedMouseActions = new Deque();

	@OriginalMember(owner = "client!bd", name = "J", descriptor = "Z")
	private final boolean logMovement;

	@OriginalMember(owner = "client!bd", name = "<init>", descriptor = "(Ljava/awt/Component;Z)V")
	public LegacyMouseManager(@OriginalArg(0) java.awt.Component component, @OriginalArg(1) boolean logMovement) {
		this.listen(component);
		this.logMovement = logMovement;
	}

	@OriginalMember(owner = "client!bd", name = "a", descriptor = "(BLjava/awt/Component;)V")
	private void listen(@OriginalArg(1) java.awt.Component component) {
		this.reset();
		this.component = component;
		this.component.addMouseListener(this);
		this.component.addMouseMotionListener(this);
	}

	@OriginalMember(owner = "client!bd", name = "mouseDragged", descriptor = "(Ljava/awt/event/MouseEvent;)V")
	@Override
	public synchronized void mouseDragged(@OriginalArg(0) java.awt.event.MouseEvent mouseEvent) {
		this.setPosition(mouseEvent.getY(), mouseEvent.getX());
	}

	@OriginalMember(owner = "client!bd", name = "mouseEntered", descriptor = "(Ljava/awt/event/MouseEvent;)V")
	@Override
	public synchronized void mouseEntered(@OriginalArg(0) java.awt.event.MouseEvent mouseEvent) {
		this.setPosition(mouseEvent.getY(), mouseEvent.getX());
	}

	@OriginalMember(owner = "client!bd", name = "a", descriptor = "(ILjava/awt/event/MouseEvent;)I")
	private int getClickMask(@OriginalArg(1) java.awt.event.MouseEvent mouseEvent) {
		@Pc(6) int modifiers = mouseEvent.getModifiers();
		@Pc(14) boolean leftDown = (modifiers & 0x10) != 0;
		@Pc(22) boolean middleDown = (modifiers & 0x8) != 0;
		@Pc(30) boolean rightDown = (modifiers & 0x4) != 0;
		if (middleDown && (leftDown || rightDown)) {
			middleDown = false;
		}
		if (leftDown && rightDown) {
			return 4;
		} else if (leftDown) {
			return 1;
		} else if (middleDown) {
			return 2;
		} else if (rightDown) {
			return 4;
		} else {
			return 0;
		}
	}

	@OriginalMember(owner = "client!bd", name = "mouseExited", descriptor = "(Ljava/awt/event/MouseEvent;)V")
	@Override
	public synchronized void mouseExited(@OriginalArg(0) java.awt.event.MouseEvent mouseEvent) {
		this.setPosition(mouseEvent.getY(), mouseEvent.getX());
	}

	@OriginalMember(owner = "client!bd", name = "a", descriptor = "(B)Z")
	@Override
	public boolean isRightButtonDown() {
		return (this.snapshotClickMask & 0x4) != 0;
	}

	@OriginalMember(owner = "client!bd", name = "b", descriptor = "(I)V")
	@Override
	public synchronized void mainLogic() {
		this.snapshotClickMask = this.clickMask;
		this.snapshotMouseY = this.mouseY;
		this.snapshotMouseX = this.mouseX;
		@Pc(23) Deque deque = this.pendingMouseActions;
		this.pendingMouseActions = this.loggedMouseActions;
		this.loggedMouseActions = deque;
		this.loggedMouseActions.clear();
	}

	@OriginalMember(owner = "client!bd", name = "mouseMoved", descriptor = "(Ljava/awt/event/MouseEvent;)V")
	@Override
	public synchronized void mouseMoved(@OriginalArg(0) java.awt.event.MouseEvent mouseEvent) {
		this.setPosition(mouseEvent.getY(), mouseEvent.getX());
	}

	@OriginalMember(owner = "client!bd", name = "a", descriptor = "(IBI)V")
	private void setPosition(@OriginalArg(0) int y, @OriginalArg(2) int x) {
		this.mouseX = x;
		this.mouseY = y;
		if (this.logMovement) {
			this.log(-1, y, x, 0);
		}
	}

	@OriginalMember(owner = "client!bd", name = "b", descriptor = "(B)I")
	@Override
	public int getMouseY() {
		return this.snapshotMouseY;
	}

	@OriginalMember(owner = "client!bd", name = "f", descriptor = "(I)Lclient!bu;")
	@Override
	public MouseRecord nextSubmission() {
		return (MouseRecord) this.pendingMouseActions.removeFirst();
	}

	@OriginalMember(owner = "client!bd", name = "a", descriptor = "(IIIII)V")
	private void log(@OriginalArg(0) int clickType, @OriginalArg(1) int y, @OriginalArg(2) int x, @OriginalArg(3) int clickCount) {
		@Pc(7) SimpleMouseLog simpleMouseLog = new SimpleMouseLog();
		simpleMouseLog.mouseX = x;
		simpleMouseLog.mouseY = y;
		simpleMouseLog.meta = clickCount;
		simpleMouseLog.clickType = clickType;
		simpleMouseLog.time = Utils.currentTimeMillis();
		this.loggedMouseActions.addLast(simpleMouseLog);
	}

	@OriginalMember(owner = "client!bd", name = "mouseClicked", descriptor = "(Ljava/awt/event/MouseEvent;)V")
	@Override
	public synchronized void mouseClicked(@OriginalArg(0) java.awt.event.MouseEvent mouseEvent) {
		if (mouseEvent.isPopupTrigger()) {
			mouseEvent.consume();
		}
	}

	@OriginalMember(owner = "client!bd", name = "c", descriptor = "(I)V")
	@Override
	public void remove() {
		this.reset();
	}

	@OriginalMember(owner = "client!bd", name = "a", descriptor = "(I)Z")
	@Override
	public boolean isMiddleButtonDown() {
		return (this.snapshotClickMask & 0x2) != 0;
	}

	@OriginalMember(owner = "client!bd", name = "e", descriptor = "(I)Z")
	@Override
	public boolean isLeftButtonDown() {
		return (this.snapshotClickMask & 0x1) != 0;
	}

	@OriginalMember(owner = "client!bd", name = "mouseReleased", descriptor = "(Ljava/awt/event/MouseEvent;)V")
	@Override
	public synchronized void mouseReleased(@OriginalArg(0) java.awt.event.MouseEvent mouseEvent) {
		@Pc(8) int mask = this.getClickMask(mouseEvent);
		if ((mask & this.clickMask) == 0) {
			mask = this.clickMask;
		}
		if ((mask & 0x1) != 0) {
			this.log(3, mouseEvent.getY(), mouseEvent.getX(), mouseEvent.getClickCount());
		}
		if ((mask & 0x4) != 0) {
			this.log(5, mouseEvent.getY(), mouseEvent.getX(), mouseEvent.getClickCount());
		}
		if ((mask & 0x2) != 0) {
			this.log(4, mouseEvent.getY(), mouseEvent.getX(), mouseEvent.getClickCount());
		}
		this.clickMask &= ~mask;
		if (mouseEvent.isPopupTrigger()) {
			mouseEvent.consume();
		}
	}

	@OriginalMember(owner = "client!bd", name = "g", descriptor = "(I)V")
	private void reset() {
		if (this.component == null) {
			return;
		}
		this.component.removeMouseMotionListener(this);
		this.component.removeMouseListener(this);
		this.component = null;
		this.pendingMouseActions = null;
		this.loggedMouseActions = null;
		this.snapshotMouseX = this.snapshotMouseY = this.snapshotClickMask = 0;
		this.mouseX = this.mouseY = this.clickMask = 0;
	}

	@OriginalMember(owner = "client!bd", name = "a", descriptor = "(Z)I")
	@Override
	public int getMouseX() {
		return this.snapshotMouseX;
	}

	@OriginalMember(owner = "client!bd", name = "mousePressed", descriptor = "(Ljava/awt/event/MouseEvent;)V")
	@Override
	public synchronized void mousePressed(@OriginalArg(0) java.awt.event.MouseEvent mouseEvent) {
		@Pc(10) int mask = this.getClickMask(mouseEvent);
		if (mask == 1) {
			this.log(0, mouseEvent.getY(), mouseEvent.getX(), mouseEvent.getClickCount());
		} else if (mask == 4) {
			this.log(2, mouseEvent.getY(), mouseEvent.getX(), mouseEvent.getClickCount());
		} else if (mask == 2) {
			this.log(1, mouseEvent.getY(), mouseEvent.getX(), mouseEvent.getClickCount());
		}
		this.clickMask |= mask;
		if (mouseEvent.isPopupTrigger()) {
			mouseEvent.consume();
		}
	}
}

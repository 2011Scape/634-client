package com.jagex.game.runetek5.client.input.keyboard;

import com.jagex.game.runetek5.core.datastruct.key.Deque;
import com.jagex.game.runetek5.config.rsinterface.Component;
import com.jagex.game.runetek5.sign.SignLinkEnvironment;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.Method;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.input.keyboard.KeyRecord;
import com.jagex.game.runetek5.client.input.keyboard.KeyRecorder;
import com.jagex.game.runetek5.client.input.keyboard.KeyRecordEntry;
import com.jagex.game.runetek5.client.event.keyboard.KeyLogConstants;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;
import com.jagex.game.runetek5.utils.Utils;
import com.jagex.game.runetek5.client.ClientState;

@OriginalClass("client!vfa")
public final class CanvasKeyRecorder extends KeyRecorder implements KeyListener, FocusListener {

	@OriginalMember(owner = "client!vfa", name = "x", descriptor = "Ljava/awt/Component;")
	private java.awt.Component component;

	@OriginalMember(owner = "client!vfa", name = "u", descriptor = "Lclient!uh;")
	private final Deque keysAwaitingProcessing = new Deque();

	@OriginalMember(owner = "client!vfa", name = "v", descriptor = "Lclient!uh;")
	private final Deque keyRecordEntries = new Deque();

	@OriginalMember(owner = "client!vfa", name = "w", descriptor = "[Z")
	private final boolean[] keysHeld = new boolean[112];

	@OriginalMember(owner = "client!vfa", name = "<init>", descriptor = "(Ljava/awt/Component;)V")
	public CanvasKeyRecorder(@OriginalArg(0) java.awt.Component component) {
		CanvasKeyRecorder.initRemappedKeys();
		this.setComponent(component);
	}

	@OriginalMember(owner = "client!vfa", name = "b", descriptor = "(I)Lclient!ll;")
	@Override
	public KeyRecord removeFirstRecorded() {
		return (KeyRecord) this.keysAwaitingProcessing.removeFirst();
	}

	@OriginalMember(owner = "client!vfa", name = "a", descriptor = "(II)Z")
	@Override
	public boolean isPressed(@OriginalArg(0) int keyCode) {
		return keyCode >= 0 && keyCode < 112 ? this.keysHeld[keyCode] : false;
	}

	@OriginalMember(owner = "client!vfa", name = "a", descriptor = "(B)V")
	private void reset() {
		if (this.component == null) {
			return;
		}
		this.component.removeKeyListener(this);
		this.component.removeFocusListener(this);
		this.component = null;
		for (@Pc(26) int i = 0; i < 112; i++) {
			this.keysHeld[i] = false;
		}
		this.keysAwaitingProcessing.clear();
		this.keyRecordEntries.clear();
	}

	@OriginalMember(owner = "client!vfa", name = "keyPressed", descriptor = "(Ljava/awt/event/KeyEvent;)V")
	@Override
	public synchronized void keyPressed(@OriginalArg(0) KeyEvent keyEvent) {
		this.handleKeyEvent(keyEvent, 0);
	}

	@OriginalMember(owner = "client!vfa", name = "a", descriptor = "(Ljava/awt/event/KeyEvent;II)V")
	private void handleKeyEvent(@OriginalArg(0) KeyEvent keyEvent, @OriginalArg(2) int type) {
		@Pc(8) int keyCode = keyEvent.getKeyCode();
		if (keyCode == 0) {
			keyCode = 0;
		} else if (keyCode >= 0 && keyCode < CanvasKeyRecorder.REMAPPED_KEYS.length) {
			keyCode = CanvasKeyRecorder.REMAPPED_KEYS[keyCode];
			if (type == 0 && (keyCode & 0x80) != 0) {
				keyCode = 0;
			} else {
				keyCode &= 0xFFFFFF7F;
			}
		} else {
			keyCode = 0;
		}
		if (keyCode != 0) {
			this.addKeyRecordEntry(keyCode, '\u0000', type);
			keyEvent.consume();
		}
	}

	@OriginalMember(owner = "client!vfa", name = "c", descriptor = "(I)V")
	@Override
	public void remove() {
		this.reset();
	}

	@OriginalMember(owner = "client!vfa", name = "a", descriptor = "(IICI)V")
	private void addKeyRecordEntry(@OriginalArg(0) int keyCode, @OriginalArg(2) char keyCharacter, @OriginalArg(3) int type) {
		@Pc(7) KeyRecordEntry simpleKeyLog = new KeyRecordEntry();
		simpleKeyLog.type = type;
		simpleKeyLog.keyCode = keyCode;
		simpleKeyLog.keyCharacter = keyCharacter;
		simpleKeyLog.timeSubmitted = Utils.currentTimeMillis();
		this.keyRecordEntries.addLast(simpleKeyLog);
	}

	@OriginalMember(owner = "client!vfa", name = "keyReleased", descriptor = "(Ljava/awt/event/KeyEvent;)V")
	@Override
	public synchronized void keyReleased(@OriginalArg(0) KeyEvent keyEvent) {
		this.handleKeyEvent(keyEvent, 1);
	}

	@OriginalMember(owner = "client!vfa", name = "keyTyped", descriptor = "(Ljava/awt/event/KeyEvent;)V")
	@Override
	public synchronized void keyTyped(@OriginalArg(0) KeyEvent keyEvent) {
		@Pc(6) char keyChar = keyEvent.getKeyChar();
		if (keyChar != '\u0000' && Utils.isCp1252Char(keyChar)) {
			this.addKeyRecordEntry(-1, keyChar, KeyLogConstants.TYPE_TYPED);
			keyEvent.consume();
		}
	}

	@OriginalMember(owner = "client!vfa", name = "focusGained", descriptor = "(Ljava/awt/event/FocusEvent;)V")
	@Override
	public void focusGained(@OriginalArg(0) FocusEvent focusEvent) {
	}

	@OriginalMember(owner = "client!vfa", name = "b", descriptor = "(B)I")
	private int getModifierFlags() {
		@Pc(5) int flags = 0;
		if (this.keysHeld[81]) {
			flags = 1;
		}
		if (this.keysHeld[82]) {
			flags |= 0x4;
		}
		if (this.keysHeld[86]) {
			flags |= 0x2;
		}
		return flags;
	}

	@OriginalMember(owner = "client!vfa", name = "a", descriptor = "(I)V")
	@Override
	public synchronized void prepareAwaitingKeys() {
		this.keysAwaitingProcessing.clear();
		for (@Pc(19) KeyRecordEntry simpleKeyLog = (KeyRecordEntry) this.keyRecordEntries.removeFirst(); simpleKeyLog != null; simpleKeyLog = (KeyRecordEntry) this.keyRecordEntries.removeFirst()) {
			simpleKeyLog.modifierFlags = this.getModifierFlags();
			if (simpleKeyLog.type == KeyLogConstants.TYPE_PRESSED) {
				if (!this.keysHeld[simpleKeyLog.keyCode]) {
					@Pc(39) KeyRecordEntry simpleKeyLog2 = new KeyRecordEntry();
					simpleKeyLog2.keyCode = simpleKeyLog.keyCode;
					simpleKeyLog2.type = 0;
					simpleKeyLog2.keyCharacter = '\u0000';
					simpleKeyLog2.modifierFlags = simpleKeyLog.modifierFlags;
					simpleKeyLog2.timeSubmitted = simpleKeyLog.timeSubmitted;
					this.keysAwaitingProcessing.addLast(simpleKeyLog2);
					this.keysHeld[simpleKeyLog.keyCode] = true;
				}
				simpleKeyLog.type = KeyLogConstants.TYPE_HELD;
				this.keysAwaitingProcessing.addLast(simpleKeyLog);
			} else if (simpleKeyLog.type == KeyLogConstants.TYPE_RELEASED) {
				if (this.keysHeld[simpleKeyLog.keyCode]) {
					this.keysAwaitingProcessing.addLast(simpleKeyLog);
					this.keysHeld[simpleKeyLog.keyCode] = false;
				}
			} else if (simpleKeyLog.type == -1) {
				for (@Pc(89) int keyCode = 0; keyCode < 112; keyCode++) {
					if (this.keysHeld[keyCode]) {
						@Pc(100) KeyRecordEntry simpleKeyLog3 = new KeyRecordEntry();
						simpleKeyLog3.keyCharacter = '\u0000';
						simpleKeyLog3.keyCode = keyCode;
						simpleKeyLog3.modifierFlags = simpleKeyLog.modifierFlags;
						simpleKeyLog3.type = 1;
						simpleKeyLog3.timeSubmitted = simpleKeyLog.timeSubmitted;
						this.keysAwaitingProcessing.addLast(simpleKeyLog3);
						this.keysHeld[keyCode] = false;
					}
				}
			} else if (simpleKeyLog.type == KeyLogConstants.TYPE_TYPED) {
				this.keysAwaitingProcessing.addLast(simpleKeyLog);
			}
		}
	}

	@OriginalMember(owner = "client!vfa", name = "focusLost", descriptor = "(Ljava/awt/event/FocusEvent;)V")
	@Override
	public synchronized void focusLost(@OriginalArg(0) FocusEvent focusEvent) {
		this.addKeyRecordEntry(0, '\u0000', -1);
	}

	@OriginalMember(owner = "client!vfa", name = "a", descriptor = "(Ljava/awt/Component;I)V")
	private void setComponent(@OriginalArg(0) java.awt.Component component2) {
		this.reset();
		this.component = component2;
		@Pc(16) Method method = SignLinkEnvironment.setFocusTraversalKeysEnabledMethod;
		if (method != null) {
			try {
				method.invoke(this.component, Boolean.FALSE);
			} catch (@Pc(31) Throwable ex) {
			}
		}
		this.component.addKeyListener(this);
		this.component.addFocusListener(this);
	}
	@OriginalMember(owner = "client!mw", name = "K", descriptor = "[I")
	public static final int[] REMAPPED_KEYS = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 85, 80, 84, 0, 91, 0, 0, 0, 81, 82, 86, 0, 0, 0, 0, 0, 0, 0, 0, 13, 0, 0, 0, 0, 83, 104, 105, 103, 102, 96, 98, 97, 99, 0, 0, 0, 0, 0, 0, 0, 25, 16, 17, 18, 19, 20, 21, 22, 23, 24, 0, 0, 0, 0, 0, 0, 0, 48, 68, 66, 50, 34, 51, 52, 53, 39, 54, 55, 56, 70, 69, 40, 41, 32, 35, 49, 36, 38, 67, 33, 65, 37, 64, 0, 0, 0, 0, 0, 228, 231, 227, 233, 224, 219, 225, 230, 226, 232, 89, 87, 0, 88, 229, 90, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 0, 0, 0, 101, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 100, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	@OriginalMember(owner = "client!eu", name = "a", descriptor = "(Ljava/awt/Component;B)Lclient!efa;")
	public static KeyRecorder create(@OriginalArg(0) java.awt.Component component) {
		return new CanvasKeyRecorder(component);
	}

	@OriginalMember(owner = "client!gh", name = "p", descriptor = "(B)V")
	public static void initRemappedKeys() {
		if (PlayerUpdate.javaVendor.toLowerCase().indexOf("microsoft") != -1) {
			CanvasKeyRecorder.REMAPPED_KEYS[222] = 59;
			CanvasKeyRecorder.REMAPPED_KEYS[187] = 27;
			CanvasKeyRecorder.REMAPPED_KEYS[186] = 57;
			CanvasKeyRecorder.REMAPPED_KEYS[221] = 43;
			CanvasKeyRecorder.REMAPPED_KEYS[219] = 42;
			CanvasKeyRecorder.REMAPPED_KEYS[191] = 73;
			CanvasKeyRecorder.REMAPPED_KEYS[190] = 72;
			CanvasKeyRecorder.REMAPPED_KEYS[220] = 74;
			CanvasKeyRecorder.REMAPPED_KEYS[188] = 71;
			CanvasKeyRecorder.REMAPPED_KEYS[189] = 26;
			CanvasKeyRecorder.REMAPPED_KEYS[192] = 58;
			CanvasKeyRecorder.REMAPPED_KEYS[223] = 28;
			return;
		}
		CanvasKeyRecorder.REMAPPED_KEYS[46] = 72;
		CanvasKeyRecorder.REMAPPED_KEYS[92] = 74;
		CanvasKeyRecorder.REMAPPED_KEYS[44] = 71;
		CanvasKeyRecorder.REMAPPED_KEYS[61] = 27;
		CanvasKeyRecorder.REMAPPED_KEYS[91] = 42;
		if (SignLinkEnvironment.setFocusTraversalKeysEnabledMethod == null) {
			CanvasKeyRecorder.REMAPPED_KEYS[192] = 58;
			CanvasKeyRecorder.REMAPPED_KEYS[222] = 59;
		} else {
			CanvasKeyRecorder.REMAPPED_KEYS[520] = 59;
			CanvasKeyRecorder.REMAPPED_KEYS[192] = 28;
			CanvasKeyRecorder.REMAPPED_KEYS[222] = 58;
		}
		CanvasKeyRecorder.REMAPPED_KEYS[45] = 26;
		CanvasKeyRecorder.REMAPPED_KEYS[47] = 73;
		CanvasKeyRecorder.REMAPPED_KEYS[59] = 57;
		CanvasKeyRecorder.REMAPPED_KEYS[93] = 43;
	}
}

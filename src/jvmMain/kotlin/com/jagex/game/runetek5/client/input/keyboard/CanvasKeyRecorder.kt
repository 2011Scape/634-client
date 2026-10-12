package com.jagex.game.runetek5.client.input.keyboard

import com.jagex.game.runetek5.core.datastruct.key.Deque
import com.jagex.game.runetek5.sign.SignLinkEnvironment
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.lang.reflect.Method
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.event.keyboard.KeyLogConstants
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.utils.Utils

@OriginalClass("client!vfa")
class CanvasKeyRecorder @OriginalMember(owner = "client!vfa", name = "<init>", descriptor = "(Ljava/awt/Component;)V") constructor(@OriginalArg(0) component: java.awt.Component?) : KeyRecorder(), KeyListener, FocusListener {

	@OriginalMember(owner = "client!vfa", name = "x", descriptor = "Ljava/awt/Component;")
	private var component: java.awt.Component? = null

	@OriginalMember(owner = "client!vfa", name = "u", descriptor = "Lclient!uh;")
	private val keysAwaitingProcessing: Deque = Deque()

	@OriginalMember(owner = "client!vfa", name = "v", descriptor = "Lclient!uh;")
	private val keyRecordEntries: Deque = Deque()

	@OriginalMember(owner = "client!vfa", name = "w", descriptor = "[Z")
	private val keysHeld: BooleanArray = BooleanArray(112)

	init {
		CanvasKeyRecorder.initRemappedKeys()
		this.setComponent(component)
	}

	@OriginalMember(owner = "client!vfa", name = "b", descriptor = "(I)Lclient!ll;")
	override fun removeFirstRecorded(): KeyRecord? {
		return this.keysAwaitingProcessing.removeFirst() as KeyRecord?
	}

	@OriginalMember(owner = "client!vfa", name = "a", descriptor = "(II)Z")
	override fun isPressed(@OriginalArg(0) keyCode: Int): Boolean {
		return if (keyCode >= 0 && keyCode < 112) this.keysHeld[keyCode] else false
	}

	@OriginalMember(owner = "client!vfa", name = "a", descriptor = "(B)V")
	private fun reset() {
		if (this.component == null) {
			return
		}
		this.component!!.removeKeyListener(this)
		this.component!!.removeFocusListener(this)
		this.component = null
		@Pc(26) var i: Int = 0
		while (i < 112) {
			this.keysHeld[i] = false
			i++
		}
		this.keysAwaitingProcessing.clear()
		this.keyRecordEntries.clear()
	}

	@OriginalMember(owner = "client!vfa", name = "keyPressed", descriptor = "(Ljava/awt/event/KeyEvent;)V")
	@Synchronized
	override fun keyPressed(@OriginalArg(0) keyEvent: KeyEvent) {
		this.handleKeyEvent(keyEvent, 0)
	}

	@OriginalMember(owner = "client!vfa", name = "a", descriptor = "(Ljava/awt/event/KeyEvent;II)V")
	private fun handleKeyEvent(@OriginalArg(0) keyEvent: KeyEvent, @OriginalArg(2) type: Int) {
		@Pc(8) var keyCode: Int = keyEvent.getKeyCode()
		if (keyCode == 0) {
			keyCode = 0
		} else if (keyCode >= 0 && keyCode < CanvasKeyRecorder.REMAPPED_KEYS.size) {
			keyCode = CanvasKeyRecorder.REMAPPED_KEYS[keyCode]
			if (type == 0 && (keyCode and 0x80) != 0) {
				keyCode = 0
			} else {
				keyCode = keyCode and 0xFFFFFF7F.toInt()
			}
		} else {
			keyCode = 0
		}
		if (keyCode != 0) {
			this.addKeyRecordEntry(keyCode, '\u0000', type)
			keyEvent.consume()
		}
	}

	@OriginalMember(owner = "client!vfa", name = "c", descriptor = "(I)V")
	override fun remove() {
		this.reset()
	}

	@OriginalMember(owner = "client!vfa", name = "a", descriptor = "(IICI)V")
	private fun addKeyRecordEntry(@OriginalArg(0) keyCode: Int, @OriginalArg(2) keyCharacter: Char, @OriginalArg(3) type: Int) {
		@Pc(7) val simpleKeyLog: KeyRecordEntry = KeyRecordEntry()
		simpleKeyLog.type = type
		simpleKeyLog.keyCode = keyCode
		simpleKeyLog.keyCharacter = keyCharacter
		simpleKeyLog.timeSubmitted = Utils.currentTimeMillis()
		this.keyRecordEntries.addLast(simpleKeyLog)
	}

	@OriginalMember(owner = "client!vfa", name = "keyReleased", descriptor = "(Ljava/awt/event/KeyEvent;)V")
	@Synchronized
	override fun keyReleased(@OriginalArg(0) keyEvent: KeyEvent) {
		this.handleKeyEvent(keyEvent, 1)
	}

	@OriginalMember(owner = "client!vfa", name = "keyTyped", descriptor = "(Ljava/awt/event/KeyEvent;)V")
	@Synchronized
	override fun keyTyped(@OriginalArg(0) keyEvent: KeyEvent) {
		@Pc(6) val keyChar: Char = keyEvent.getKeyChar()
		if (keyChar != '\u0000' && Utils.isCp1252Char(keyChar)) {
			this.addKeyRecordEntry(-1, keyChar, KeyLogConstants.TYPE_TYPED)
			keyEvent.consume()
		}
	}

	@OriginalMember(owner = "client!vfa", name = "focusGained", descriptor = "(Ljava/awt/event/FocusEvent;)V")
	override fun focusGained(@OriginalArg(0) focusEvent: FocusEvent?) {
	}

	@OriginalMember(owner = "client!vfa", name = "b", descriptor = "(B)I")
	private fun getModifierFlags(): Int {
		@Pc(5) var flags: Int = 0
		if (this.keysHeld[81]) {
			flags = 1
		}
		if (this.keysHeld[82]) {
			flags = flags or 0x4
		}
		if (this.keysHeld[86]) {
			flags = flags or 0x2
		}
		return flags
	}

	@OriginalMember(owner = "client!vfa", name = "a", descriptor = "(I)V")
	@Synchronized
	override fun prepareAwaitingKeys() {
		this.keysAwaitingProcessing.clear()
		@Pc(19) var simpleKeyLog: KeyRecordEntry? = this.keyRecordEntries.removeFirst() as KeyRecordEntry?
		while (simpleKeyLog != null) {
			simpleKeyLog.modifierFlags = this.getModifierFlags()
			if (simpleKeyLog.type == KeyLogConstants.TYPE_PRESSED) {
				if (!this.keysHeld[simpleKeyLog.keyCode]) {
					@Pc(39) val simpleKeyLog2: KeyRecordEntry = KeyRecordEntry()
					simpleKeyLog2.keyCode = simpleKeyLog.keyCode
					simpleKeyLog2.type = 0
					simpleKeyLog2.keyCharacter = '\u0000'
					simpleKeyLog2.modifierFlags = simpleKeyLog.modifierFlags
					simpleKeyLog2.timeSubmitted = simpleKeyLog.timeSubmitted
					this.keysAwaitingProcessing.addLast(simpleKeyLog2)
					this.keysHeld[simpleKeyLog.keyCode] = true
				}
				simpleKeyLog.type = KeyLogConstants.TYPE_HELD
				this.keysAwaitingProcessing.addLast(simpleKeyLog)
			} else if (simpleKeyLog.type == KeyLogConstants.TYPE_RELEASED) {
				if (this.keysHeld[simpleKeyLog.keyCode]) {
					this.keysAwaitingProcessing.addLast(simpleKeyLog)
					this.keysHeld[simpleKeyLog.keyCode] = false
				}
			} else if (simpleKeyLog.type == -1) {
				@Pc(89) var keyCode: Int = 0
				while (keyCode < 112) {
					if (this.keysHeld[keyCode]) {
						@Pc(100) val simpleKeyLog3: KeyRecordEntry = KeyRecordEntry()
						simpleKeyLog3.keyCharacter = '\u0000'
						simpleKeyLog3.keyCode = keyCode
						simpleKeyLog3.modifierFlags = simpleKeyLog.modifierFlags
						simpleKeyLog3.type = 1
						simpleKeyLog3.timeSubmitted = simpleKeyLog.timeSubmitted
						this.keysAwaitingProcessing.addLast(simpleKeyLog3)
						this.keysHeld[keyCode] = false
					}
					keyCode++
				}
			} else if (simpleKeyLog.type == KeyLogConstants.TYPE_TYPED) {
				this.keysAwaitingProcessing.addLast(simpleKeyLog)
			}
			simpleKeyLog = this.keyRecordEntries.removeFirst() as KeyRecordEntry?
		}
	}

	@OriginalMember(owner = "client!vfa", name = "focusLost", descriptor = "(Ljava/awt/event/FocusEvent;)V")
	@Synchronized
	override fun focusLost(@OriginalArg(0) focusEvent: FocusEvent?) {
		this.addKeyRecordEntry(0, '\u0000', -1)
	}

	@OriginalMember(owner = "client!vfa", name = "a", descriptor = "(Ljava/awt/Component;I)V")
	private fun setComponent(@OriginalArg(0) component2: java.awt.Component?) {
		this.reset()
		this.component = component2
		@Pc(16) val method: Method? = SignLinkEnvironment.setFocusTraversalKeysEnabledMethod
		if (method != null) {
			try {
				method.invoke(this.component, java.lang.Boolean.FALSE)
			} catch (@Pc(31) ex: Throwable) {
			}
		}
		this.component!!.addKeyListener(this)
		this.component!!.addFocusListener(this)
	}

	companion object {
		@OriginalMember(owner = "client!mw", name = "K", descriptor = "[I")
		@JvmField
		val REMAPPED_KEYS: IntArray = intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 85, 80, 84, 0, 91, 0, 0, 0, 81, 82, 86, 0, 0, 0, 0, 0, 0, 0, 0, 13, 0, 0, 0, 0, 83, 104, 105, 103, 102, 96, 98, 97, 99, 0, 0, 0, 0, 0, 0, 0, 25, 16, 17, 18, 19, 20, 21, 22, 23, 24, 0, 0, 0, 0, 0, 0, 0, 48, 68, 66, 50, 34, 51, 52, 53, 39, 54, 55, 56, 70, 69, 40, 41, 32, 35, 49, 36, 38, 67, 33, 65, 37, 64, 0, 0, 0, 0, 0, 228, 231, 227, 233, 224, 219, 225, 230, 226, 232, 89, 87, 0, 88, 229, 90, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 0, 0, 0, 101, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 100, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
		@OriginalMember(owner = "client!eu", name = "a", descriptor = "(Ljava/awt/Component;B)Lclient!efa;")
		@JvmStatic
		fun create(@OriginalArg(0) component: java.awt.Component?): KeyRecorder {
			return CanvasKeyRecorder(component)
		}

		@OriginalMember(owner = "client!gh", name = "p", descriptor = "(B)V")
		@JvmStatic
		fun initRemappedKeys() {
			if (PlayerUpdate.javaVendor!!.lowercase(java.util.Locale.getDefault()).indexOf("microsoft") != -1) {
				CanvasKeyRecorder.REMAPPED_KEYS[222] = 59
				CanvasKeyRecorder.REMAPPED_KEYS[187] = 27
				CanvasKeyRecorder.REMAPPED_KEYS[186] = 57
				CanvasKeyRecorder.REMAPPED_KEYS[221] = 43
				CanvasKeyRecorder.REMAPPED_KEYS[219] = 42
				CanvasKeyRecorder.REMAPPED_KEYS[191] = 73
				CanvasKeyRecorder.REMAPPED_KEYS[190] = 72
				CanvasKeyRecorder.REMAPPED_KEYS[220] = 74
				CanvasKeyRecorder.REMAPPED_KEYS[188] = 71
				CanvasKeyRecorder.REMAPPED_KEYS[189] = 26
				CanvasKeyRecorder.REMAPPED_KEYS[192] = 58
				CanvasKeyRecorder.REMAPPED_KEYS[223] = 28
				return
			}
			CanvasKeyRecorder.REMAPPED_KEYS[46] = 72
			CanvasKeyRecorder.REMAPPED_KEYS[92] = 74
			CanvasKeyRecorder.REMAPPED_KEYS[44] = 71
			CanvasKeyRecorder.REMAPPED_KEYS[61] = 27
			CanvasKeyRecorder.REMAPPED_KEYS[91] = 42
			if (SignLinkEnvironment.setFocusTraversalKeysEnabledMethod == null) {
				CanvasKeyRecorder.REMAPPED_KEYS[192] = 58
				CanvasKeyRecorder.REMAPPED_KEYS[222] = 59
			} else {
				CanvasKeyRecorder.REMAPPED_KEYS[520] = 59
				CanvasKeyRecorder.REMAPPED_KEYS[192] = 28
				CanvasKeyRecorder.REMAPPED_KEYS[222] = 58
			}
			CanvasKeyRecorder.REMAPPED_KEYS[45] = 26
			CanvasKeyRecorder.REMAPPED_KEYS[47] = 73
			CanvasKeyRecorder.REMAPPED_KEYS[59] = 57
			CanvasKeyRecorder.REMAPPED_KEYS[93] = 43
		}
	}
}

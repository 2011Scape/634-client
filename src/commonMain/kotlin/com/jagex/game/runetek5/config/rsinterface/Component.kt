package com.jagex.game.runetek5.config.rsinterface

import com.jagex.game.runetek5.plugin.api.Widget
import com.jagex.game.runetek5.plugin.api.WidgetVisitor
import com.jagex.game.runetek5.Skybox
import com.jagex.game.runetek5.graphics.sprite.SpriteType
import com.jagex.game.runetek5.entity.ParticleSystem
import com.jagex.game.runetek5.config.sequence.SeqType
import com.jagex.game.runetek5.config.sequence.SeqTypeList
import com.jagex.game.runetek5.cache.node.IntNode
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable
import com.jagex.game.runetek5.cache.node.Node
import com.jagex.game.runetek5.cache.node.StringNode
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerAppearance
import com.jagex.game.runetek5.BrightnessTableBuilder
import com.jagex.game.runetek5.config.bas.BasTypeList
import com.jagex.game.runetek5.config.idktype.IDKTypeList
import com.jagex.game.runetek5.config.rsinterface.IFEvents
import com.jagex.game.runetek5.config.npctype.NPCTypeList
import com.jagex.game.runetek5.config.item.ItemType
import com.jagex.game.runetek5.config.objtype.ObjTypeList
import com.jagex.game.runetek5.config.sun.SunTypeList
import com.jagex.game.runetek5.config.skyboxtype.SkyBoxTypeList
import com.jagex.game.runetek5.vardomain.VarDomain
import com.jagex.game.runetek5.config.rsinterface.ComponentSurface
import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer
import com.jagex.game.runetek5.graphics.Matrix
import com.jagex.game.runetek5.graphics.model.Mesh
import com.jagex.game.runetek5.graphics.model.Model
import com.jagex.game.runetek5.graphics.sprite.NativeSprite
import com.jagex.game.runetek5.graphics.SpriteDataProvider
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.graphics.model.particle.EffectiveVertex
import com.jagex.game.runetek5.graphics.model.particle.EmissiveTriangle
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.cache.node.lru.LRUCache
import com.jagex.game.runetek5.core.datastruct.key.Deque
import com.jagex.game.runetek5.InterfaceManager
import com.jagex.game.runetek5.InterfaceRectangles
import com.jagex.game.runetek5.cache.node.HashTableIterator
import com.jagex.game.runetek5.config.Resource
import com.jagex.game.runetek5.graphics.font.FontManager
import com.jagex.game.runetek5.graphics.ifcomponent.InterfaceRenderer
import com.jagex.game.runetek5.cache.node.JavaObjSprite
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import com.jagex.game.runetek5.client.ClientState
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import com.jagex.game.runetek5.plugin.PluginSpriteOverrides

@OriginalClass("client!at")
class Component : Widget {

	/* Not part of the original client: the plugin API's view of a component (plugin.api.Widget). */

	override fun interfaceId(): Int {
		return this.idHash shr 16
	}

	override fun componentId(): Int {
		return this.idHash and 0xFFFF
	}

	override fun text(): String? {
		return this.text
	}

	override fun x(): Int {
		return this.x
	}

	override fun y(): Int {
		return this.y
	}

	override fun width(): Int {
		return this.width
	}

	override fun height(): Int {
		return this.height
	}

	override fun isHidden(): Boolean {
		return this.hidden
	}

	override fun isDrawnThisFrame(): Boolean {
		return this.lastRenderedCycle == InterfaceRenderer.lastRenderedCycle
	}

	override fun setHidden(hidden: Boolean) {
		this.hidden = hidden
	}

	override fun dragDeadTime(): Int {
		return this.dragDeadTime
	}

	override fun setDragDeadTime(cycles: Int) {
		this.dragDeadTime = cycles
	}

	override fun setText(text: String?) {
		this.text = text
	}

	override fun id(): Int {
		return this.idHash
	}

	override fun type(): Int {
		val type: ComponentType = this.type ?: return -1
		return type.id
	}

	override fun parentId(): Int {
		return this.parentId
	}

	override fun slot(): Int {
		return this.slotId
	}

	override fun screenX(): Int {
		var x: Int = this.x
		var parent: Component? = parentComponent()
		var depth: Int = 0
		while (parent != null && depth < 32) {
			x += parent.x - parent.scrollX
			parent = parent.parentComponent()
			depth++
		}
		return x
	}

	override fun screenY(): Int {
		var y: Int = this.y
		var parent: Component? = parentComponent()
		var depth: Int = 0
		while (parent != null && depth < 32) {
			y += parent.y - parent.scrollY
			parent = parent.parentComponent()
			depth++
		}
		return y
	}

	/** The component this one is inside, read without loading anything: the parent by id, or the component a sub-interface is open in. */
	private fun parentComponent(): Component? {
		var parentHash: Int = this.parentId
		if (parentHash == -1) {
			val interfaceId: Int = this.idHash ushr 16
			val open: HashTableIterator = HashTableIterator(InterfaceManager.OPEN_INTERFACES)
			var sub: SubInterface? = open.first() as SubInterface?
			while (sub != null) {
				if (sub.interfaceId == interfaceId) {
					parentHash = sub.key.toInt()
					break
				}
				sub = open.next() as SubInterface?
			}
			if (parentHash == -1) {
				return null
			}
		}
		return Interface.peekComponent(parentHash)
	}

	override fun itemId(): Int {
		return this.containerItemId
	}

	override fun itemQuantity(): Int {
		return this.itemStackSize
	}

	override fun spriteId(): Int {
		return this.spriteId
	}

	override fun modelId(): Int {
		return if (this.mediaType === MediaType.RAW_MODEL) this.primaryMediaId else -1
	}

	override fun colour(): Int {
		return this.color
	}

	override fun transparency(): Int {
		return this.transparency
	}

	override fun actionCount(): Int {
		val options: Array<String?> = this.options ?: return 0
		return options.size
	}

	override fun action(i: Int): String? {
		val options: Array<String?> = this.options ?: return null
		return if (i >= 0 && i < options.size) options[i] else null
	}

	override fun targetText(): String? {
		return this.opBase
	}

	override fun name(): String? {
		return this.name
	}

	override fun scrollX(): Int {
		return this.scrollX
	}

	override fun scrollY(): Int {
		return this.scrollY
	}

	override fun scrollWidth(): Int {
		return this.scrollWidth
	}

	override fun scrollHeight(): Int {
		return this.scrollHeight
	}

	override fun contentType(): Int {
		return this.contentType
	}

	override fun childCount(): Int {
		val children: Array<Component?> = this.slotChildren ?: return 0
		return children.size
	}

	override fun child(i: Int): Widget? {
		val children: Array<Component?> = this.slotChildren ?: return null
		return if (i >= 0 && i < children.size) children[i] else null
	}

	override fun staticChildren(visitor: WidgetVisitor) {
		val components: Array<Component?> = Interface.peekComponents(this.idHash ushr 16) ?: return
		for (i in 0 until components.size) {
			val component: Component = components[i] ?: continue
			if (component.parentId == this.idHash) {
				visitor.visit(component)
			}
		}
	}

	@OriginalMember(owner = "client!at", name = "a", descriptor = "[Ljava/lang/Object;")
	@JvmField
	var onStatTransmit: Array<Any?>? = null

	@OriginalMember(owner = "client!at", name = "d", descriptor = "I")
	@JvmField
	var lightPositionX: Int = 0

	@OriginalMember(owner = "client!at", name = "g", descriptor = "[I")
	@JvmField
	var rowStartX: IntArray? = null

	@OriginalMember(owner = "client!at", name = "i", descriptor = "[Ljava/lang/Object;")
	@JvmField
	var onTargetLeave: Array<Any?>? = null

	@OriginalMember(owner = "client!at", name = "j", descriptor = "[Ljava/lang/Object;")
	@JvmField
	var hookParams: Array<Any?>? = null

	@OriginalMember(owner = "client!at", name = "m", descriptor = "[Ljava/lang/Object;")
	@JvmField
	var onMouseLeaveScript: Array<Any?>? = null

	@OriginalMember(owner = "client!at", name = "o", descriptor = "[Ljava/lang/Object;")
	@JvmField
	var onKey: Array<Any?>? = null

	@OriginalMember(owner = "client!at", name = "p", descriptor = "[I")
	@JvmField
	var varps: IntArray? = null

	@OriginalMember(owner = "client!at", name = "q", descriptor = "[Ljava/lang/Object;")
	@JvmField
	var onCamFinished: Array<Any?>? = null

	@OriginalMember(owner = "client!at", name = "v", descriptor = "[Ljava/lang/Object;")
	@JvmField
	var onDragComplete: Array<Any?>? = null

	@OriginalMember(owner = "client!at", name = "w", descriptor = "I")
	@JvmField
	var lightPositionZ: Int = 0

	@OriginalMember(owner = "client!at", name = "E", descriptor = "[Ljava/lang/Object;")
	@JvmField
	var onSubChange: Array<Any?>? = null

	@OriginalMember(owner = "client!at", name = "J", descriptor = "[I")
	@JvmField
	var opCursors: IntArray? = null

	@OriginalMember(owner = "client!at", name = "K", descriptor = "[I")
	@JvmField
	var cycleSteps: IntArray? = null

	@OriginalMember(owner = "client!at", name = "L", descriptor = "[Ljava/lang/Object;")
	@JvmField
	var onScrollWheel: Array<Any?>? = null

	@OriginalMember(owner = "client!at", name = "O", descriptor = "Lclient!eq;")
	private var clientParams: IterableHashTable? = null

	@OriginalMember(owner = "client!at", name = "R", descriptor = "[Ljava/lang/Object;")
	@JvmField
	var onTargetEnter: Array<Any?>? = null

	@OriginalMember(owner = "client!at", name = "Y", descriptor = "Ljava/lang/String;")
	@JvmField
	var name: String? = null

	@OriginalMember(owner = "client!at", name = "eb", descriptor = "[Ljava/lang/Object;")
	@JvmField
	var onResize: Array<Any?>? = null

	@OriginalMember(owner = "client!at", name = "ib", descriptor = "[I")
	@JvmField
	var statTransmitFilter: IntArray? = null

	@OriginalMember(owner = "client!at", name = "jb", descriptor = "I")
	@JvmField
	var serverTriggers: Int = 0

	@OriginalMember(owner = "client!at", name = "kb", descriptor = "I")
	@JvmField
	var lightPositionY: Int = 0

	@OriginalMember(owner = "client!at", name = "lb", descriptor = "[Ljava/lang/Object;")
	@JvmField
	var onFriendTransmit: Array<Any?>? = null

	@OriginalMember(owner = "client!at", name = "mb", descriptor = "Z")
	@JvmField
	var flipHorizontal: Boolean = false

	@OriginalMember(owner = "client!at", name = "rb", descriptor = "Lclient!uj;")
	@JvmField
	var particleSystem: ParticleSystem? = null

	@OriginalMember(owner = "client!at", name = "sb", descriptor = "[Ljava/lang/Object;")
	@JvmField
	var params: Array<Any?>? = null

	@OriginalMember(owner = "client!at", name = "tb", descriptor = "[Ljava/lang/Object;")
	@JvmField
	var onRelease: Array<Any?>? = null

	@OriginalMember(owner = "client!at", name = "xb", descriptor = "[B")
	@JvmField
	var keyTriggeringAction: ByteArray? = null

	@OriginalMember(owner = "client!at", name = "Cb", descriptor = "[Ljava/lang/Object;")
	@JvmField
	var mouseLeaveScript: Array<Any?>? = null

	@OriginalMember(owner = "client!at", name = "Eb", descriptor = "I")
	@JvmField
	var primaryMediaId: Int = 0

	@OriginalMember(owner = "client!at", name = "Kb", descriptor = "I")
	@JvmField
	var sunRotationX: Int = 0

	@OriginalMember(owner = "client!at", name = "Mb", descriptor = "[Ljava/lang/Object;")
	@JvmField
	var onClanTransmit: Array<Any?>? = null

	@OriginalMember(owner = "client!at", name = "Ob", descriptor = "[Ljava/lang/Object;")
	@JvmField
	var onClick: Array<Any?>? = null

	@OriginalMember(owner = "client!at", name = "Pb", descriptor = "[Ljava/lang/Object;")
	@JvmField
	var onTimer: Array<Any?>? = null

	@OriginalMember(owner = "client!at", name = "Wb", descriptor = "[I")
	@JvmField
	var mouseLeaveArrayParams: IntArray? = null

	@OriginalMember(owner = "client!at", name = "Xb", descriptor = "[I")
	@JvmField
	var rowWidth: IntArray? = null

	@OriginalMember(owner = "client!at", name = "Zb", descriptor = "I")
	@JvmField
	var type: ComponentType? = ComponentType.CONTAINER

	@OriginalMember(owner = "client!at", name = "cc", descriptor = "[Ljava/lang/Object;")
	@JvmField
	var onVarpTransmit: Array<Any?>? = null

	@OriginalMember(owner = "client!at", name = "jc", descriptor = "Z")
	@JvmField
	var hasOrigin: Boolean = false

	@OriginalMember(owner = "client!at", name = "kc", descriptor = "[Ljava/lang/Object;")
	@JvmField
	var onClickRepeat: Array<Any?>? = null

	@OriginalMember(owner = "client!at", name = "pc", descriptor = "Z")
	@JvmField
	var flipVertical: Boolean = false

	@OriginalMember(owner = "client!at", name = "xc", descriptor = "[Lclient!at;")
	@JvmField
	var slotChildren: Array<Component?>? = null

	@OriginalMember(owner = "client!at", name = "Ac", descriptor = "[I")
	@JvmField
	var cycles: IntArray? = null

	@OriginalMember(owner = "client!at", name = "Cc", descriptor = "[Ljava/lang/Object;")
	@JvmField
	var onMouseMove: Array<Any?>? = null

	@OriginalMember(owner = "client!at", name = "Ec", descriptor = "[Ljava/lang/Object;")
	@JvmField
	var onHold: Array<Any?>? = null

	@OriginalMember(owner = "client!at", name = "Gc", descriptor = "[Ljava/lang/Object;")
	@JvmField
	var onStockTransmit: Array<Any?>? = null

	@OriginalMember(owner = "client!at", name = "Kc", descriptor = "[Ljava/lang/Object;")
	@JvmField
	var onMouseOver: Array<Any?>? = null

	@OriginalMember(owner = "client!at", name = "Oc", descriptor = "[Ljava/lang/Object;")
	@JvmField
	var onLoadScript: Array<Any?>? = null

	@OriginalMember(owner = "client!at", name = "Pc", descriptor = "Ljava/lang/String;")
	@JvmField
	var resumeText: String? = null

	@OriginalMember(owner = "client!at", name = "Rc", descriptor = "[I")
	@JvmField
	var keyPressArray: IntArray? = null

	@OriginalMember(owner = "client!at", name = "Tc", descriptor = "[Ljava/lang/Object;")
	@JvmField
	var onChatTransmit: Array<Any?>? = null

	@OriginalMember(owner = "client!at", name = "Vc", descriptor = "[Ljava/lang/Object;")
	@JvmField
	var onKeyType: Array<Any?>? = null

	@OriginalMember(owner = "client!at", name = "Wc", descriptor = "[Ljava/lang/Object;")
	@JvmField
	var onDrag: Array<Any?>? = null

	@OriginalMember(owner = "client!at", name = "ed", descriptor = "[I")
	@JvmField
	var mouseWheelArray: IntArray? = null

	@OriginalMember(owner = "client!at", name = "fd", descriptor = "[B")
	@JvmField
	var keyTriggerParams: ByteArray? = null

	@OriginalMember(owner = "client!at", name = "gd", descriptor = "[Ljava/lang/String;")
	@JvmField
	var options: Array<String?>? = null

	@OriginalMember(owner = "client!at", name = "id", descriptor = "I")
	@JvmField
	var sunRotationY: Int = 0

	@OriginalMember(owner = "client!at", name = "jd", descriptor = "[Ljava/lang/Object;")
	@JvmField
	var onMiscTransmit: Array<Any?>? = null

	@OriginalMember(owner = "client!at", name = "md", descriptor = "[Ljava/lang/Object;")
	@JvmField
	var popupScript: Array<Any?>? = null

	@OriginalMember(owner = "client!at", name = "nd", descriptor = "[Ljava/lang/Object;")
	@JvmField
	var onSubClose: Array<Any?>? = null

	@OriginalMember(owner = "client!at", name = "c", descriptor = "I")
	@JvmField
	var lineSpacing: Int = 0

	@OriginalMember(owner = "client!at", name = "h", descriptor = "I")
	@JvmField
	var renderStack: Int = 2

	@OriginalMember(owner = "client!at", name = "r", descriptor = "I")
	@JvmField
	var originX: Int = 0

	@OriginalMember(owner = "client!at", name = "z", descriptor = "I")
	@JvmField
	var transparency: Int = 0

	@OriginalMember(owner = "client!at", name = "F", descriptor = "I")
	@JvmField
	var fontId: Int = -1

	@OriginalMember(owner = "client!at", name = "y", descriptor = "I")
	@JvmField
	var targetLeaveCursor: Int = -1

	@OriginalMember(owner = "client!at", name = "D", descriptor = "I")
	@JvmField
	var textVerticalAli: Int = 0

	@OriginalMember(owner = "client!at", name = "f", descriptor = "Z")
	@JvmField
	var hasInteraction: Boolean = false

	@OriginalMember(owner = "client!at", name = "db", descriptor = "I")
	@JvmField
	var lastServerTick: Int = -1

	@OriginalMember(owner = "client!at", name = "l", descriptor = "Z")
	@JvmField
	var hasScript: Boolean = false

	@OriginalMember(owner = "client!at", name = "t", descriptor = "I")
	@JvmField
	var lineWidth: Int = 1

	@OriginalMember(owner = "client!at", name = "T", descriptor = "I")
	@JvmField
	var spriteScale: Int = 100

	@OriginalMember(owner = "client!at", name = "Ib", descriptor = "I")
	@JvmField
	var spritePitch: Int = 0

	@OriginalMember(owner = "client!at", name = "H", descriptor = "I")
	@JvmField
	var slotId: Int = -1

	@OriginalMember(owner = "client!at", name = "Bb", descriptor = "I")
	@JvmField
	var y: Int = 0

	@OriginalMember(owner = "client!at", name = "e", descriptor = "I")
	@JvmField
	var spriteRoll: Int = 0

	@OriginalMember(owner = "client!at", name = "Ab", descriptor = "I")
	@JvmField
	var color: Int = 0

	@OriginalMember(owner = "client!at", name = "Hb", descriptor = "I")
	@JvmField
	var spriteId: Int = -1

	@OriginalMember(owner = "client!at", name = "N", descriptor = "I")
	@JvmField
	var aspectX: Int = 1

	@OriginalMember(owner = "client!at", name = "pb", descriptor = "I")
	@JvmField
	var lastKeyCycle: Int = 0

	@OriginalMember(owner = "client!at", name = "b", descriptor = "I")
	@JvmField
	var borderThickness: Int = 0

	@OriginalMember(owner = "client!at", name = "hb", descriptor = "I")
	@JvmField
	var nextFrameId: Int = 1

	@OriginalMember(owner = "client!at", name = "Fb", descriptor = "Z")
	@JvmField
	var priorityRender: Boolean = false

	@OriginalMember(owner = "client!at", name = "yb", descriptor = "I")
	@JvmField
	var scrollX: Int = 0

	@OriginalMember(owner = "client!at", name = "ab", descriptor = "I")
	@JvmField
	var lastContainerTransmitCycle: Int = 0

	@OriginalMember(owner = "client!at", name = "bc", descriptor = "Z")
	@JvmField
	var preventClickThrough: Boolean = false

	@OriginalMember(owner = "client!at", name = "nc", descriptor = "I")
	@JvmField
	var basePositionX: Int = 0

	@OriginalMember(owner = "client!at", name = "C", descriptor = "I")
	@JvmField
	var animation: Int = -1

	@OriginalMember(owner = "client!at", name = "Ub", descriptor = "Z")
	@JvmField
	var shadow: Boolean = false

	@OriginalMember(owner = "client!at", name = "X", descriptor = "Z")
	@JvmField
	var wearCol: Boolean = false

	@OriginalMember(owner = "client!at", name = "Q", descriptor = "I")
	@JvmField
	var targetOpCursor: Int = -1

	@OriginalMember(owner = "client!at", name = "qb", descriptor = "I")
	@JvmField
	var basePositionY: Int = 0

	@OriginalMember(owner = "client!at", name = "gb", descriptor = "I")
	@JvmField
	var dragDeadzone: Int = 0

	@OriginalMember(owner = "client!at", name = "fb", descriptor = "I")
	@JvmField
	var baseHeight: Int = 0

	@OriginalMember(owner = "client!at", name = "wc", descriptor = "I")
	@JvmField
	var dragType: Int = Component.DRAG_RENDER_TRANSLUCENT

	@OriginalMember(owner = "client!at", name = "ec", descriptor = "I")
	@JvmField
	var secondaryMediaId: Int = -1

	@OriginalMember(owner = "client!at", name = "M", descriptor = "I")
	@JvmField
	var width: Int = 0

	@OriginalMember(owner = "client!at", name = "ac", descriptor = "I")
	@JvmField
	var aspectY: Int = 1

	@OriginalMember(owner = "client!at", name = "W", descriptor = "I")
	@JvmField
	var moveOverCursor: Int = -1

	@OriginalMember(owner = "client!at", name = "sc", descriptor = "Z")
	@JvmField
	var isPressed: Boolean = false

	@OriginalMember(owner = "client!at", name = "qc", descriptor = "I")
	@JvmField
	var scrollHeight: Int = 0

	@OriginalMember(owner = "client!at", name = "S", descriptor = "I")
	@JvmField
	var lastStatTransmitCycle: Int = 0

	@OriginalMember(owner = "client!at", name = "s", descriptor = "Z")
	@JvmField
	var usesOrthogonal: Boolean = false

	@OriginalMember(owner = "client!at", name = "A", descriptor = "Z")
	@JvmField
	var tiling: Boolean = false

	@OriginalMember(owner = "client!at", name = "Hc", descriptor = "I")
	@JvmField
	var offsetX: Int = 0

	@OriginalMember(owner = "client!at", name = "Qb", descriptor = "I")
	@JvmField
	var contentType: Int = 0

	@OriginalMember(owner = "client!at", name = "Gb", descriptor = "I")
	@JvmField
	var scrollY: Int = 0

	@OriginalMember(owner = "client!at", name = "zb", descriptor = "Lclient!wb;")
	@JvmField
	var targetParams: IFEvents? = Component.DEFAULT_SETTINGS

	@OriginalMember(owner = "client!at", name = "lc", descriptor = "I")
	@JvmField
	var offsetY: Int = 0

	@OriginalMember(owner = "client!at", name = "Lb", descriptor = "I")
	@JvmField
	var cacheReference: Int = -1

	@OriginalMember(owner = "client!at", name = "zc", descriptor = "I")
	@JvmField
	var x: Int = 0

	@OriginalMember(owner = "client!at", name = "Vb", descriptor = "I")
	@JvmField
	var mediaType: MediaType? = MediaType.RAW_MODEL

	@OriginalMember(owner = "client!at", name = "dc", descriptor = "B")
	@JvmField
	var aspectWidthType: Byte = 0

	@OriginalMember(owner = "client!at", name = "Jc", descriptor = "Z")
	@JvmField
	var filled: Boolean = false

	@OriginalMember(owner = "client!at", name = "bb", descriptor = "B")
	@JvmField
	var aspectHeightType: Byte = 0

	@OriginalMember(owner = "client!at", name = "hc", descriptor = "Lclient!at;")
	@JvmField
	var depthChildOverride: Component? = null

	@OriginalMember(owner = "client!at", name = "Yb", descriptor = "I")
	@JvmField
	var maxTextLines: Int = 0

	@OriginalMember(owner = "client!at", name = "tc", descriptor = "I")
	@JvmField
	var lastRenderedCycle: Int = -1

	@OriginalMember(owner = "client!at", name = "cb", descriptor = "I")
	@JvmField
	var lastVarpTransmitCycle: Int = 0

	@OriginalMember(owner = "client!at", name = "yc", descriptor = "I")
	@JvmField
	var originY: Int = 0

	@OriginalMember(owner = "client!at", name = "ob", descriptor = "Z")
	@JvmField
	var isHooked: Boolean = false

	@OriginalMember(owner = "client!at", name = "G", descriptor = "I")
	@JvmField
	var dragDeadTime: Int = 0

	@OriginalMember(owner = "client!at", name = "bd", descriptor = "I")
	@JvmField
	var angle2d: Int = 0

	@OriginalMember(owner = "client!at", name = "Db", descriptor = "Ljava/lang/String;")
	@JvmField
	var opBase: String? = ""

	@OriginalMember(owner = "client!at", name = "wb", descriptor = "Z")
	@JvmField
	var alpha: Boolean = false

	@OriginalMember(owner = "client!at", name = "B", descriptor = "I")
	@JvmField
	var scrollWidth: Int = 0

	@OriginalMember(owner = "client!at", name = "rc", descriptor = "Ljava/lang/String;")
	@JvmField
	var text: String? = ""

	@OriginalMember(owner = "client!at", name = "Nc", descriptor = "I")
	@JvmField
	var lastMouseWheelCycle: Int = 0

	@OriginalMember(owner = "client!at", name = "gc", descriptor = "I")
	@JvmField
	var itemStackSize: Int = 0

	@OriginalMember(owner = "client!at", name = "Mc", descriptor = "Z")
	@JvmField
	var hidden: Boolean = false

	@OriginalMember(owner = "client!at", name = "Xc", descriptor = "I")
	@JvmField
	var targetOverCursor: Int = -1

	@OriginalMember(owner = "client!at", name = "dd", descriptor = "I")
	@JvmField
	var idHash: Int = -1

	@OriginalMember(owner = "client!at", name = "k", descriptor = "B")
	@JvmField
	var aspectYType: Byte = 0

	@OriginalMember(owner = "client!at", name = "Ic", descriptor = "I")
	@JvmField
	var spriteShadow: Int = 0

	@OriginalMember(owner = "client!at", name = "hd", descriptor = "I")
	@JvmField
	var parentId: Int = -1

	@OriginalMember(owner = "client!at", name = "Nb", descriptor = "I")
	@JvmField
	var textHorizontalAli: Int = 0

	@OriginalMember(owner = "client!at", name = "oc", descriptor = "I")
	@JvmField
	var height: Int = 0

	@OriginalMember(owner = "client!at", name = "fc", descriptor = "Ljava/lang/String;")
	@JvmField
	var targetVerb: String? = ""

	@OriginalMember(owner = "client!at", name = "vb", descriptor = "Z")
	@JvmField
	var lineDirection: Boolean = false

	@OriginalMember(owner = "client!at", name = "Zc", descriptor = "I")
	@JvmField
	var aspectHeight: Int = 0

	@OriginalMember(owner = "client!at", name = "mc", descriptor = "I")
	@JvmField
	var originZ: Int = 0

	@OriginalMember(owner = "client!at", name = "I", descriptor = "I")
	@JvmField
	var skyboxId: Int = -1

	@OriginalMember(owner = "client!at", name = "uc", descriptor = "I")
	@JvmField
	var currentFrameId: Int = 0

	@OriginalMember(owner = "client!at", name = "Lc", descriptor = "I")
	@JvmField
	var containerItemId: Int = -1

	@OriginalMember(owner = "client!at", name = "U", descriptor = "I")
	@JvmField
	var spriteYaw: Int = 0

	@OriginalMember(owner = "client!at", name = "Fc", descriptor = "I")
	@JvmField
	var videoFileId: Int = -1

	@OriginalMember(owner = "client!at", name = "pd", descriptor = "I")
	@JvmField
	var baseWidth: Int = 0

	@OriginalMember(owner = "client!at", name = "vc", descriptor = "I")
	@JvmField
	var aspectWidth: Int = 0

	@OriginalMember(owner = "client!at", name = "od", descriptor = "I")
	@JvmField
	var currentFrameDuration: Int = 0

	@OriginalMember(owner = "client!at", name = "Qc", descriptor = "B")
	@JvmField
	var aspectXType: Byte = 0


	@OriginalMember(owner = "client!at", name = "a", descriptor = "(Lclient!ha;B)Lclient!aa;")
	fun getClippingMask(@OriginalArg(0) toolkit: Toolkit?): ComponentSurface? {
		@Pc(13) var clippingMask: ComponentSurface? = Component.aClass169_13.get(this.idHash.toLong()) as ComponentSurface?
		if (clippingMask != null) {
			return clippingMask
		}
		@Pc(23) val indexedImage: SpriteType? = SpriteDataProvider.listSingle(Component.packet, this.spriteId, 0)
		if (indexedImage == null) {
			return null
		}
		@Pc(36) val sizeX: Int = indexedImage.minX + indexedImage.width + indexedImage.borderX
		@Pc(45) val sizeY: Int = indexedImage.minY + indexedImage.height + indexedImage.borderY
		this.rowWidth = IntArray(sizeY)
		this.rowStartX = IntArray(sizeY)
		@Pc(55) var i: Int = 0
		while (i < indexedImage.height) {
			@Pc(59) var clipLeft: Int = 0
			@Pc(61) var j: Int = 0
			while (j < indexedImage.width) {
				if (indexedImage.pixels!![indexedImage.width * i + j].toInt() != 0) {
					clipLeft = j
					break
				}
				j++
			}
			@Pc(88) var clipRight: Int = sizeX
			@Pc(90) var k: Int = clipLeft
			while (k < indexedImage.width) {
				if (indexedImage.pixels!![indexedImage.width * i + k].toInt() == 0) {
					clipRight = k
					break
				}
				k++
			}
			this.rowStartX!![indexedImage.minY + i] = indexedImage.minX + clipLeft
			this.rowWidth!![i + indexedImage.minY] = clipRight - clipLeft
			i++
		}
		clippingMask = toolkit!!.createMask(sizeX, sizeY, this.rowStartX, this.rowWidth)
		Component.aClass169_13.put(clippingMask, this.idHash.toLong())
		return clippingMask
	}

	@OriginalMember(owner = "client!at", name = "b", descriptor = "(Lclient!ha;B)Lclient!da;")
	fun getFontRenderer(@OriginalArg(0) toolkit: Toolkit?): AbstractFontRenderer? {
		@Pc(10) val font: AbstractFontRenderer? = FontManager.getFont(toolkit, false, this.fontId)
		Component.missingMedia = font == null
		return font
	}

	@OriginalMember(owner = "client!at", name = "a", descriptor = "(III)I")
	fun getIntParam(@OriginalArg(0) defaultValue: Int, @OriginalArg(1) paramKey: Int): Int {
		if (this.clientParams == null) {
			return defaultValue
		} else {
			@Pc(22) val intNode: IntNode? = this.clientParams!!.get(paramKey.toLong()) as IntNode?
			return if (intNode == null) defaultValue else intNode.value
		}
	}

	@OriginalMember(owner = "client!at", name = "a", descriptor = "(ILjava/lang/String;I)Ljava/lang/String;")
	fun getStringParam(@OriginalArg(0) paramKey: Int, @OriginalArg(1) defaultValue: String?): String? {
		if (this.clientParams == null) {
			return defaultValue
		} else {
			@Pc(26) val stringNode: StringNode? = this.clientParams!!.get(paramKey.toLong()) as StringNode?
			return if (stringNode == null) defaultValue else stringNode.value
		}
	}

	@OriginalMember(owner = "client!at", name = "a", descriptor = "(Lclient!oha;Lclient!lfa;ILclient!ha;IILclient!ub;Lclient!dd;ILclient!oo;Lclient!hg;Lclient!uga;Lclient!vda;I)Lclient!ka;")
	fun getAnimatedMediaModel(@OriginalArg(0) idkTypeList: IDKTypeList?, @OriginalArg(1) seqTypeList: SeqTypeList?, @OriginalArg(2) functionMask: Int, @OriginalArg(3) toolkit: Toolkit?, @OriginalArg(4) currentFrameId: Int, @OriginalArg(5) currentFrameDuration: Int, @OriginalArg(6) objTypeList: ObjTypeList?, @OriginalArg(7) varDomain: VarDomain?, @OriginalArg(9) playerAppearance: PlayerAppearance?, @OriginalArg(10) seqType: SeqType?, @OriginalArg(11) basTypeList: BasTypeList?, @OriginalArg(12) npcTypeList: NPCTypeList?, @OriginalArg(13) nextFrameId: Int): Model? {
		var functionMask: Int = functionMask
		Component.missingMedia = false
		if (this.mediaType === MediaType.UNDEFINED) {
			return null
		} else if (this.mediaType === MediaType.RAW_MODEL && this.primaryMediaId == -1) {
			return null
		} else {
			@Pc(65) var model: Model?
			if (this.mediaType === MediaType.RAW_MODEL) {
				if (seqType != null) {
					functionMask = seqType.getFunctionMask(nextFrameId, currentFrameId, true) or 0x800
				}
				@Pc(59) val uid: Long = (this.primaryMediaId + (this.mediaType!!.id shl 16) + (toolkit!!.index shl 29)).toLong()
				model = Component.MODEL_CACHE.get(uid) as Model?
				if (model == null || toolkit.compareFunctionMasks(model.ua(), functionMask) != 0) {
					if (model != null) {
						functionMask = toolkit.combineFunctionMasks(functionMask, model.ua())
					}
					@Pc(88) val mesh: Mesh? = Mesh.fromResource(Resource.aClass157_291, this.primaryMediaId)
					if (mesh == null) {
						Component.missingMedia = true
						return null
					}
					if (mesh.version < 13) {
						mesh.upscale()
					}
					model = toolkit.createModel(mesh, functionMask, Interface.preferenceMask, 64, 768)
					Component.MODEL_CACHE.put(model, uid)
				}
				if (seqType != null) {
					model = seqType.animateModel(model, nextFrameId, currentFrameDuration, functionMask, currentFrameId)
				}
				model!!.s(2048)
				return model
			}
			@Pc(153) var model2: Model?
			if (this.mediaType === MediaType.NPC_HEAD) {
				model2 = npcTypeList!!.list(this.primaryMediaId).getAnimatedHeadModel(varDomain, toolkit, currentFrameDuration, nextFrameId, seqType, seqTypeList, currentFrameId)
				if (model2 == null) {
					Component.missingMedia = true
					return null
				} else {
					return model2
				}
			} else if (this.mediaType === MediaType.PLAYER_HEAD) {
				if (playerAppearance == null) {
					return null
				}
				model2 = playerAppearance.getAnimatedHeadModel(objTypeList, seqTypeList, idkTypeList, varDomain, npcTypeList, currentFrameId, nextFrameId, seqType, currentFrameDuration, toolkit)
				if (model2 == null) {
					Component.missingMedia = true
					return null
				} else {
					return model2
				}
			} else if (this.mediaType === MediaType.ITEM) {
				@Pc(216) val objType: ItemType? = objTypeList!!.list(this.primaryMediaId)
				@Pc(228) val model3: Model? = objType!!.getAnimatedModel(playerAppearance, seqType, toolkit, 2048, currentFrameDuration, 10, currentFrameId, nextFrameId)
				if (model3 == null) {
					Component.missingMedia = true
					return null
				} else {
					return model3
				}
			} else if (this.mediaType === MediaType.NPC_MODEL) {
				model2 = npcTypeList!!.list(this.primaryMediaId).getBodyModel(0, null, seqTypeList, null, 0, basTypeList, currentFrameId, seqType, varDomain, toolkit, 0, null, nextFrameId, 0, 2048, currentFrameDuration)
				if (model2 == null) {
					Component.missingMedia = true
					return null
				} else {
					return model2
				}
			} else if (this.mediaType !== MediaType.PLAYER_HEAD_IGNOREWORN) {
				return null
			} else if (playerAppearance == null) {
				return null
			} else {
				@Pc(287) val primaryHead: Int = this.primaryMediaId ushr 16
				@Pc(292) val secondaryHead: Int = this.primaryMediaId and 0xFFFF
				@Pc(295) val tertiaryHead: Int = this.secondaryMediaId
				model = playerAppearance.getPlainAnimatedHeadModel(nextFrameId, tertiaryHead, secondaryHead, seqTypeList, idkTypeList, primaryHead, currentFrameDuration, toolkit, currentFrameId, seqType)
				if (model == null) {
					Component.missingMedia = true
					return null
				} else {
					return model
				}
			}
		}
	}

	@OriginalMember(owner = "client!at", name = "a", descriptor = "(IIB)V")
	fun addCursor(@OriginalArg(0) index: Int, @OriginalArg(1) cursor: Int) {
		if (this.opCursors == null || this.opCursors!!.size <= index) {
			@Pc(22) val cursors: IntArray = IntArray(index + 1)
			if (this.opCursors != null) {
				@Pc(27) var i: Int = 0
				while (i < this.opCursors!!.size) {
					cursors[i] = this.opCursors!![i]
					i++
				}
				@Pc(50) var length: Int = this.opCursors!!.size
				while (length < index) {
					cursors[length] = -1
					length++
				}
			}
			this.opCursors = cursors
		}
		this.opCursors!![index] = cursor
	}

	@OriginalMember(owner = "client!at", name = "a", descriptor = "(Lclient!cea;I)[Ljava/lang/Object;")
	private fun decodeScript(@OriginalArg(0) packet: Packet?): Array<Any?>? {
		@Pc(14) val argumentCount: Int = packet!!.readUByte()
		if (argumentCount == 0) {
			return null
		}
		@Pc(21) val objects: Array<Any?> = arrayOfNulls<Any>(argumentCount)
		@Pc(23) var i: Int = 0
		while (i < argumentCount) {
			@Pc(29) val valueType: Int = packet.readUByte()
			if (valueType == 0) {
				objects[i] = (packet.readInt())
			} else if (valueType == 1) {
				objects[i] = packet.readString()
			}
			i++
		}
		this.hasScript = true
		return objects
	}

	@OriginalMember(owner = "client!at", name = "a", descriptor = "(Lclient!cea;Z)V")
	fun decode(@OriginalArg(0) packet: Packet?) {
		@Pc(9) var formatVersion: Int = packet!!.readUByte()
		if (formatVersion == 255) {
			formatVersion = -1
		}
		var type: Int = packet.readUByte()
		if ((type and 0x80) != 0) {
			type = type and 0x7F
			this.name = packet.readString()
		}
		this.type = ComponentType.fromId(type)
		this.contentType = packet.readUShort()
		this.basePositionX = packet.readShort()
		this.basePositionY = packet.readShort()
		this.baseWidth = packet.readUShort()
		this.baseHeight = packet.readUShort()
		this.aspectWidthType = packet.readByte()
		this.aspectHeightType = packet.readByte()
		this.aspectXType = packet.readByte()
		this.aspectYType = packet.readByte()
		this.parentId = packet.readUShort()
		if (this.parentId == 65535) {
			this.parentId = -1
		} else {
			this.parentId += this.idHash and 0xFFFF0000.toInt()
		}
		@Pc(107) val interactionMask: Int = packet.readUByte()
		if (formatVersion >= 0) {
			this.preventClickThrough = (interactionMask and 0x2) != 0
		}
		this.hidden = (interactionMask and 0x1) != 0
		if (this.type === ComponentType.CONTAINER) {
			this.scrollWidth = packet.readUShort()
			this.scrollHeight = packet.readUShort()
			if (formatVersion < 0) {
				this.preventClickThrough = packet.readUByte() == 1
			}
		}
		@Pc(184) var local184: Int
		if (this.type === ComponentType.SPRITE) {
			this.spriteId = packet.readInt()
			this.angle2d = packet.readUShort()
			val spriteFlags: Int = packet.readUByte()
			this.tiling = (spriteFlags and 0x1) != 0
			this.alpha = (spriteFlags and 0x2) != 0
			this.transparency = packet.readUByte()
			this.borderThickness = packet.readUByte()
			this.spriteShadow = packet.readInt()
			this.flipVertical = packet.readUByte() == 1
			this.flipHorizontal = packet.readUByte() == 1
			this.color = packet.readInt()
		}
		if (this.type === ComponentType.MODEL) {
			this.mediaType = MediaType.RAW_MODEL
			this.primaryMediaId = packet.readUShort()
			if (this.primaryMediaId == 65535) {
				this.primaryMediaId = -1
			}
			val modelFlags: Int = packet.readUByte()
			this.hasOrigin = (modelFlags and 0x2) == 2
			@Pc(293) val hasTransform: Boolean = (modelFlags and 0x1) == 1
			this.usesOrthogonal = (modelFlags and 0x4) == 4
			this.priorityRender = (modelFlags and 0x8) == 8
			if (hasTransform) {
				this.originX = packet.readShort()
				this.originY = packet.readShort()
				this.spritePitch = packet.readUShort()
				this.spriteRoll = packet.readUShort()
				this.spriteYaw = packet.readUShort()
				this.spriteScale = packet.readUShort()
			} else if (this.hasOrigin) {
				this.originX = packet.readShort()
				this.originY = packet.readShort()
				this.originZ = packet.readShort()
				this.spritePitch = packet.readUShort()
				this.spriteRoll = packet.readUShort()
				this.spriteYaw = packet.readUShort()
				this.spriteScale = packet.readShort()
			}
			this.animation = packet.readUShort()
			if (this.animation == 65535) {
				this.animation = -1
			}
			if (this.aspectWidthType.toInt() != 0) {
				this.aspectWidth = packet.readUShort()
			}
			if (this.aspectHeightType.toInt() != 0) {
				this.aspectHeight = packet.readUShort()
			}
		}
		if (this.type === ComponentType.TEXT) {
			this.fontId = packet.readUShort()
			if (this.fontId == 65535) {
				this.fontId = -1
			}
			this.text = packet.readString()
			this.lineSpacing = packet.readUByte()
			this.textHorizontalAli = packet.readUByte()
			this.textVerticalAli = packet.readUByte()
			this.shadow = packet.readUByte() == 1
			this.color = packet.readInt()
			this.transparency = packet.readUByte()
			if (formatVersion >= 0) {
				this.maxTextLines = packet.readUByte()
			}
		}
		if (this.type === ComponentType.FIGURE) {
			this.color = packet.readInt()
			this.filled = packet.readUByte() == 1
			this.transparency = packet.readUByte()
		}
		if (this.type === ComponentType.LINE) {
			this.lineWidth = packet.readUByte()
			this.color = packet.readInt()
			this.lineDirection = packet.readUByte() == 1
		}
		val events: Int = packet.readUMedium()
		@Pc(547) var keyTriggerData: Int = packet.readUByte()
		@Pc(572) var local572: Int
		if (keyTriggerData != 0) {
			this.keyTriggerParams = ByteArray(11)
			this.keyTriggeringAction = ByteArray(11)
			this.cycleSteps = IntArray(11)
			while (keyTriggerData != 0) {
				val triggerIndex: Int = (keyTriggerData shr 4) - 1
				keyTriggerData = (keyTriggerData shl 8) or packet.readUByte()
				keyTriggerData = keyTriggerData and 0xFFF
				if (keyTriggerData == 4095) {
					keyTriggerData = -1
				}
				@Pc(595) val triggerAction: Byte = packet.readByte()
				if (triggerAction.toInt() != 0) {
					this.hasInteraction = true
				}
				@Pc(604) val triggerParam: Byte = packet.readByte()
				this.cycleSteps!![triggerIndex] = keyTriggerData
				this.keyTriggeringAction!![triggerIndex] = triggerAction
				this.keyTriggerParams!![triggerIndex] = triggerParam
				keyTriggerData = packet.readUByte()
			}
		}
		this.opBase = packet.readString()
		val opCounts: Int = packet.readUByte()
		@Pc(641) val optionCount: Int = opCounts and 0xF
		@Pc(652) var local652: Int
		if (optionCount > 0) {
			this.options = arrayOfNulls<String>(optionCount)
			var optionIndex: Int = 0
			while (optionIndex < optionCount) {
				this.options!![optionIndex] = packet.readString()
				optionIndex++
			}
		}
		@Pc(669) val cursorCount: Int = opCounts shr 4
		@Pc(686) var local686: Int
		if (cursorCount > 0) {
			val cursorIndex: Int = packet.readUByte()
			this.opCursors = IntArray(cursorIndex + 1)
			var cursorSlot: Int = 0
			while (cursorSlot < this.opCursors!!.size) {
				this.opCursors!![cursorSlot] = -1
				cursorSlot++
			}
			this.opCursors!![cursorIndex] = packet.readUShort()
		}
		if (cursorCount > 1) {
			val cursorIndex: Int = packet.readUByte()
			this.opCursors!![cursorIndex] = packet.readUShort()
		}
		this.resumeText = packet.readString()
		if (this.resumeText!!.equals("")) {
			this.resumeText = null
		}
		this.dragDeadzone = packet.readUByte()
		this.dragDeadTime = packet.readUByte()
		this.dragType = packet.readUByte()
		this.targetVerb = packet.readString()
		var targetParam: Int = -1
		if (IFEvents.getUseOptionFlags(events) != 0) {
			targetParam = packet.readUShort()
			this.targetOverCursor = packet.readUShort()
			if (targetParam == 65535) {
				targetParam = -1
			}
			if (this.targetOverCursor == 65535) {
				this.targetOverCursor = -1
			}
			this.targetLeaveCursor = packet.readUShort()
			if (this.targetLeaveCursor == 65535) {
				this.targetLeaveCursor = -1
			}
		}
		if (formatVersion >= 0) {
			this.moveOverCursor = packet.readUShort()
			if (this.moveOverCursor == 65535) {
				this.moveOverCursor = -1
			}
		}
		this.targetParams = IFEvents(events, targetParam)
		if (formatVersion >= 0) {
			val intParamCount: Int = packet.readUByte()
			@Pc(833) var local833: Int
			@Pc(837) var local837: Int
			@Pc(827) var i: Int = 0
			while (i < intParamCount) {
				val intParamKey: Int = packet.readUMedium()
				val intParamValue: Int = packet.readInt()
				this.clientParams!!.put(intParamKey.toLong(), IntNode(intParamValue))
				i++
			}
			val stringParamCount: Int = packet.readUByte()
			var stringParamIndex: Int = 0
			while (stringParamIndex < stringParamCount) {
				@Pc(867) val paramKey: Int = packet.readUMedium()
				@Pc(871) val string: String? = packet.readJagString()
				this.clientParams!!.put(paramKey.toLong(), StringNode(string))
				stringParamIndex++
			}
		}
		this.onLoadScript = this.decodeScript(packet)
		this.onMouseOver = this.decodeScript(packet)
		this.onMouseLeaveScript = this.decodeScript(packet)
		this.hookParams = this.decodeScript(packet)
		this.onTargetEnter = this.decodeScript(packet)
		this.onVarpTransmit = this.decodeScript(packet)
		this.mouseLeaveScript = this.decodeScript(packet)
		this.onStatTransmit = this.decodeScript(packet)
		this.onTimer = this.decodeScript(packet)
		this.params = this.decodeScript(packet)
		if (formatVersion >= 0) {
			this.onTargetLeave = this.decodeScript(packet)
		}
		this.popupScript = this.decodeScript(packet)
		this.onClick = this.decodeScript(packet)
		this.onClickRepeat = this.decodeScript(packet)
		this.onRelease = this.decodeScript(packet)
		this.onHold = this.decodeScript(packet)
		this.onDrag = this.decodeScript(packet)
		this.onDragComplete = this.decodeScript(packet)
		this.onMouseMove = this.decodeScript(packet)
		this.onKey = this.decodeScript(packet)
		this.onScrollWheel = this.decodeScript(packet)
		this.varps = this.decodeScript3(packet)
		this.mouseLeaveArrayParams = this.decodeScript3(packet)
		this.statTransmitFilter = this.decodeScript3(packet)
		this.keyPressArray = this.decodeScript3(packet)
		this.mouseWheelArray = this.decodeScript3(packet)
	}

	@OriginalMember(owner = "client!at", name = "a", descriptor = "(Z)V")
	fun clearScriptHooks() {
		this.mouseLeaveArrayParams = null
		this.statTransmitFilter = null
		this.onChatTransmit = null
		this.varps = null
		this.popupScript = null
		this.onClick = null
		this.mouseWheelArray = null
		this.params = null
		this.onTimer = null
		this.onMiscTransmit = null
		this.onSubChange = null
		this.mouseLeaveScript = null
		this.onKeyType = null
		this.onTargetEnter = null
		this.onStatTransmit = null
		this.onClickRepeat = null
		this.onResize = null
		this.onMouseOver = null
		this.onMouseLeaveScript = null
		this.keyPressArray = null
		this.onScrollWheel = null
		this.onCamFinished = null
		this.onMouseMove = null
		this.onDrag = null
		this.onHold = null
		this.onKey = null
		this.onLoadScript = null
		this.onDragComplete = null
		this.onTargetLeave = null
		this.onRelease = null
		this.onSubClose = null
		this.onVarpTransmit = null
		this.onStockTransmit = null
		this.onClanTransmit = null
		this.onFriendTransmit = null
		this.hookParams = null
	}

	@OriginalMember(owner = "client!at", name = "a", descriptor = "(II)V")
	fun removeParam(@OriginalArg(0) paramKey: Int) {
		if (this.clientParams != null) {
			@Pc(13) val node: Node? = this.clientParams!!.get(paramKey.toLong())
			if (node != null) {
				node.unlink()
			}
		}
	}

	@OriginalMember(owner = "client!at", name = "b", descriptor = "(III)V")
	fun setIntParam(@OriginalArg(1) paramKey: Int, @OriginalArg(2) paramValue: Int) {
		if (this.clientParams == null) {
			this.clientParams = IterableHashTable(16)
			this.clientParams!!.put(paramKey.toLong(), IntNode(paramValue))
			return
		}
		@Pc(35) val intNode: IntNode? = this.clientParams!!.get(paramKey.toLong()) as IntNode?
		if (intNode == null) {
			this.clientParams!!.put(paramKey.toLong(), IntNode(paramValue))
		} else {
			intNode.value = paramValue
		}
	}

	@OriginalMember(owner = "client!at", name = "a", descriptor = "(ILclient!ka;Lclient!ha;ILclient!bca;)V")
	fun cycleParticles(@OriginalArg(1) model: Model?, @OriginalArg(2) toolkit: Toolkit?, @OriginalArg(3) cycle: Int, @OriginalArg(4) matrix: Matrix?) {
		model!!.updateParticlePosition(matrix)
		@Pc(17) val modelParticleEmitters: Array<EmissiveTriangle?>? = model.getEmissiveTriangles()
		@Pc(20) val modelParticleEffectors: Array<EffectiveVertex?>? = model.getEffectiveVertices()
		if ((this.particleSystem == null || this.particleSystem!!.removed) && (modelParticleEmitters != null || modelParticleEffectors != null)) {
			this.particleSystem = ParticleSystem.get(cycle, false)
		}
		if (this.particleSystem != null) {
			this.particleSystem!!.update(toolkit, cycle.toLong(), modelParticleEmitters, modelParticleEffectors)
		}
	}

	@OriginalMember(owner = "client!at", name = "a", descriptor = "(IILjava/lang/String;)V")
	fun addOptionName(@OriginalArg(0) index: Int, @OriginalArg(2) option: String?) {
		if (this.options == null || index >= this.options!!.size) {
			@Pc(14) val strings: Array<String?> = arrayOfNulls<String>(index + 1)
			if (this.options != null) {
				@Pc(19) var i: Int = 0
				while (i < this.options!!.size) {
					strings[i] = this.options!![i]
					i++
				}
			}
			this.options = strings
		}
		this.options!![index] = option
	}

	@OriginalMember(owner = "client!at", name = "b", descriptor = "(IILjava/lang/String;)V")
	fun setStringParam(@OriginalArg(1) paramKey: Int, @OriginalArg(2) paramValue: String?) {
		if (this.clientParams == null) {
			this.clientParams = IterableHashTable(16)
			this.clientParams!!.put(paramKey.toLong(), StringNode(paramValue))
			return
		}
		@Pc(36) val stringNode: StringNode? = this.clientParams!!.get(paramKey.toLong()) as StringNode?
		if (stringNode == null) {
			this.clientParams!!.put(paramKey.toLong(), StringNode(paramValue))
		} else {
			stringNode.value = paramValue
		}
	}

	@OriginalMember(owner = "client!at", name = "a", descriptor = "(ZLclient!cea;)[I")
	private fun decodeScript3(@OriginalArg(1) packet: Packet?): IntArray? {
		@Pc(9) val count: Int = packet!!.readUByte()
		if (count == 0) {
			return null
		}
		@Pc(24) val values: IntArray = IntArray(count)
		@Pc(26) var i: Int = 0
		while (i < count) {
			values[i] = packet.readInt()
			i++
		}
		return values
	}

	@OriginalMember(owner = "client!at", name = "c", descriptor = "(Lclient!ha;B)Lclient!mi;")
	fun getSprite(@OriginalArg(0) toolkit: Toolkit?): NativeSprite? {
		Component.missingMedia = false
		// Not part of the original client: a plugin's picture in place of the sprite (ClientApi.overrideSprite).
		val overridden: NativeSprite? = PluginSpriteOverrides.sprite(this.spriteId)
		if (overridden != null) {
			return overridden
		}
		@Pc(48) val spriteHash: Long = (this.spriteShadow.toLong() shl 40) + ((if (this.flipVertical) 1L else 0L) shl 38) + (this.borderThickness.toLong() shl 36) + ((if (this.alpha) 1L else 0L) shl 35) + this.spriteId.toLong() + ((if (this.flipHorizontal) 1L else 0L) shl 39)
		@Pc(54) var sprite: NativeSprite? = Component.NATIVE_SPRITE_CACHE.get(spriteHash) as NativeSprite?
		if (sprite != null) {
			return sprite
		}
		@Pc(70) val indexedImage: SpriteType? = SpriteDataProvider.listSingle(Component.packet, this.spriteId, 0)
		if (indexedImage == null) {
			Component.missingMedia = true
			return null
		}
		if (this.flipVertical) {
			indexedImage.flipVertical()
		}
		if (this.flipHorizontal) {
			indexedImage.flipHorizontal()
		}
		if (this.borderThickness > 0) {
			indexedImage.drawSmallBorder(this.borderThickness)
		} else if (this.spriteShadow != 0) {
			indexedImage.drawSmallBorder(1)
		}
		if (this.borderThickness >= 1) {
			indexedImage.drawThickBorder(1)
		}
		if (this.borderThickness >= 2) {
			indexedImage.drawThickBorder(16777215)
		}
		if (this.spriteShadow != 0) {
			indexedImage.outline(this.spriteShadow or 0xFF000000.toInt())
		}
		sprite = toolkit!!.generateSprite(indexedImage, true)
		Component.NATIVE_SPRITE_CACHE.put(sprite, spriteHash, sprite!!.getWidth() * sprite.getHeight() * 4)
		return sprite
	}

	@OriginalMember(owner = "client!at", name = "a", descriptor = "(ZLclient!hq;Lclient!lca;)Lclient!wr;")
	fun calculateLighting(@OriginalArg(1) sunTypeList: SunTypeList?, @OriginalArg(2) skyBoxTypeList: SkyBoxTypeList?): Skybox? {
		if (this.skyboxId == -1) {
			return null
		}
		@Pc(63) val uid: Long = ((this.lightPositionZ.toLong() shl 16) and (0xFFFFL shl 16)) or ((this.lightPositionY.toLong() shl 32) and (0xFFFFL shl 32)) or ((this.lightPositionX.toLong() shl 48) and (0xFFFFL shl 48)) or (this.skyboxId.toLong() and 0xFFFFL)
		@Pc(69) var lighting: Skybox? = Component.COMPONENT_LIGHTING.get(uid) as Skybox?
		if (lighting == null) {
			lighting = skyBoxTypeList!!.generateSkybox(this.lightPositionY, this.lightPositionZ, this.lightPositionX, sunTypeList, this.skyboxId)
			Component.COMPONENT_LIGHTING.put(lighting, uid)
		}
		return lighting
	}

	companion object {
		@OriginalMember(owner = "client!at", name = "Uc", descriptor = "[B")
		@JvmField
		val normalizeTable: ByteArray = ByteArray(32896)

		init {
			@Pc(4) var index: Int = 0
			@Pc(6) var y: Int = 0
			while (y < 256) {
				@Pc(9) var x: Int = 0
				while (x <= y) {
					normalizeTable[index++] = (255.0 / kotlin.math.sqrt(((y * y + x * x + 65535).toFloat() / 65535.0f).toDouble())).toInt().toByte()
					x++
				}
				y++
			}
		}

		@OriginalMember(owner = "client!vq", name = "z", descriptor = "Z")
		@JvmField
		var missingMedia: Boolean = false

		@OriginalMember(owner = "client!ww", name = "L", descriptor = "Lclient!uh;")
		@JvmField
		val ITEMS: Deque = Deque()

		@OriginalMember(owner = "client!ew", name = "g", descriptor = "Lclient!jr;")
		@JvmField
		val MODEL_CACHE: LRUCache = LRUCache(50)

		@OriginalMember(owner = "client!kj", name = "N", descriptor = "Lclient!jr;")
		@JvmField
		val COMPONENT_LIGHTING: LRUCache = LRUCache(4)

		@OriginalMember(owner = "client!ej", name = "f", descriptor = "Lclient!jr;")
		@JvmField
		val NATIVE_SPRITE_CACHE: LRUCache = LRUCache(3000000, 200)

		@OriginalMember(owner = "client!ek", name = "h", descriptor = "I")
		const val DRAG_RENDER_TRANSLUCENT: Int = 0

		@OriginalMember(owner = "client!rfa", name = "h", descriptor = "Lclient!wb;")
		@JvmField
		val DEFAULT_SETTINGS: IFEvents = IFEvents(0, -1)

		@OriginalMember(owner = "client!tr", name = "a", descriptor = "(ILclient!at;)V")
		@JvmStatic
		fun redrawComponent(@OriginalArg(1) component: Component?) {
			if (component!!.lastRenderedCycle == InterfaceRenderer.lastRenderedCycle) {
				InterfaceRectangles.IS_COMPONENT_ON_TOP[component.cacheReference] = true
			}
		}

		@OriginalMember(owner = "client!fk", name = "a", descriptor = "(ILclient!at;Z)Ljava/lang/String;")
		@JvmStatic
		fun getOpName(@OriginalArg(0) opIndex: Int, @OriginalArg(1) component: Component?): String? {
			if (!InterfaceManager.getComponentSettings(component)!!.isOpEnabled(opIndex) && component!!.params == null) {
				return null
			} else if (component!!.options == null || component.options!!.size <= opIndex || component.options!![opIndex] == null || component.options!![opIndex]!!.trim { it <= ' ' }.length == 0) {
				return if (InterfaceManager.qaOpTest) "Hidden-" + opIndex else null
			} else {
				return component.options!![opIndex]
			}
		}

		@OriginalMember(owner = "client!kda", name = "a", descriptor = "(II)V")
		@JvmStatic
		fun setPreferenceMask(@OriginalArg(0) preferenceMask: Int) {
			Interface.preferenceMask = preferenceMask
			Component.MODEL_CACHE.reset()
		}

		@OriginalMember(owner = "client!dba", name = "A", descriptor = "Lclient!jr;")
		@JvmField
		val aClass169_13: LRUCache = LRUCache(8)

		@OriginalMember(owner = "client!jp", name = "a", descriptor = "(II)V")
		@JvmStatic
		fun cleanCaches() {
			Component.NATIVE_SPRITE_CACHE.clean(50)
			Component.MODEL_CACHE.clean(50)
			Component.aClass169_13.clean(50)
			Component.COMPONENT_LIGHTING.clean(50)
		}

		@OriginalMember(owner = "client!pm", name = "c", descriptor = "(I)V")
		@JvmStatic
		fun removeCacheSoftReferences() {
			Component.NATIVE_SPRITE_CACHE.removeSoftReferences()
			Component.MODEL_CACHE.removeSoftReferences()
			Component.aClass169_13.removeSoftReferences()
			Component.COMPONENT_LIGHTING.removeSoftReferences()
		}

		@OriginalMember(owner = "client!pea", name = "a", descriptor = "(B)V")
		@JvmStatic
		fun resetCaches() {
			Component.NATIVE_SPRITE_CACHE.reset()
			Component.MODEL_CACHE.reset()
			Component.aClass169_13.reset()
			Component.COMPONENT_LIGHTING.reset()
		}

		@OriginalMember(owner = "client!cd", name = "l", descriptor = "I")
		const val CONTENT_TYPE_MINIMAP: Int = 1338

		@OriginalMember(owner = "client!tda", name = "b", descriptor = "I")
		const val CONTENT_TYPE_LOCAL_PLAYER_MODEL: Int = 328

		@OriginalMember(owner = "client!bu", name = "o", descriptor = "I")
		const val CONTENT_TYPE_WORLDMAP: Int = 1400

		@OriginalMember(owner = "client!nv", name = "g", descriptor = "I")
		const val CONTENT_TYPE_DEBUG_INFORMATION: Int = 1405

		@OriginalMember(owner = "client!wm", name = "x", descriptor = "I")
		const val CONTENT_TYPE_MOUSE_OVER_TEXT: Int = 1406

		@OriginalMember(owner = "client!mea", name = "a", descriptor = "(BILclient!at;)I")
		@JvmStatic
		fun getOpCursor(@OriginalArg(1) op: Int, @OriginalArg(2) component: Component?): Int {
			if (!InterfaceManager.getComponentSettings(component)!!.isOpEnabled(op) && component!!.params == null) {
				return -1
			} else if (component!!.opCursors == null || op >= component.opCursors!!.size) {
				return -1
			} else {
				return component.opCursors!![op]
			}
		}

		@OriginalMember(owner = "client!tr", name = "a", descriptor = "(ZILclient!at;)V")
		@JvmStatic
		fun layoutChildren(@OriginalArg(0) fireResizeHooks: Boolean, @OriginalArg(2) component: Component?) {
			@Pc(20) val width: Int = if (component!!.scrollWidth == 0) component.width else component.scrollWidth
			@Pc(29) val height: Int = if (component.scrollHeight == 0) component.height else component.scrollHeight
			InterfaceManager.updateInterfaceDimensions(component.idHash, width, fireResizeHooks, height, Interface.interfaces!![component.idHash shr 16])
			if (component.slotChildren != null) {
				InterfaceManager.updateInterfaceDimensions(component.idHash, width, fireResizeHooks, height, component.slotChildren)
			}
			@Pc(62) val subInterface: SubInterface? = InterfaceManager.OPEN_INTERFACES.get(component.idHash.toLong()) as SubInterface?
			if (subInterface != null) {
				InterfaceManager.updateInterfaceDimensions(height, subInterface.interfaceId, fireResizeHooks, width)
			}
		}

		@OriginalMember(owner = "client!bm", name = "a", descriptor = "(BIILclient!at;)V")
		@JvmStatic
		fun updatePosition(@OriginalArg(1) parentWidth: Int, @OriginalArg(2) parentHeight: Int, @OriginalArg(3) component: Component?) {
			if (component!!.aspectXType.toInt() == 0) {
				component.x = component.basePositionX
			} else if (component.aspectXType.toInt() == 1) {
				component.x = (parentWidth - component.width) / 2 + component.basePositionX
			} else if (component.aspectXType.toInt() == 2) {
				component.x = parentWidth - component.basePositionX - component.width
			} else if (component.aspectXType.toInt() == 3) {
				component.x = parentWidth * component.basePositionX shr 14
			} else if (component.aspectXType.toInt() == 4) {
				component.x = (parentWidth - component.width) / 2 + (parentWidth * component.basePositionX shr 14)
			} else {
				component.x = parentWidth - component.width - (parentWidth * component.basePositionX shr 14)
			}
			if (component.aspectYType.toInt() == 0) {
				component.y = component.basePositionY
			} else if (component.aspectYType.toInt() == 1) {
				component.y = (parentHeight - component.height) / 2 + component.basePositionY
			} else if (component.aspectYType.toInt() == 2) {
				component.y = parentHeight - component.height - component.basePositionY
			} else if (component.aspectYType.toInt() == 3) {
				component.y = component.basePositionY * parentHeight shr 14
			} else if (component.aspectYType.toInt() == 4) {
				component.y = (parentHeight * component.basePositionY shr 14) + (parentHeight - component.height) / 2
			} else {
				component.y = parentHeight - (parentHeight * component.basePositionY shr 14) - component.height
			}
			if (!InterfaceManager.qaOpTest || InterfaceManager.getComponentSettings(component)!!.settingsHash == 0 && component.type !== ComponentType.CONTAINER) {
				return
			}
			if (component.y < 0) {
				component.y = 0
			} else if (component.height + component.y > parentHeight) {
				component.y = parentHeight - component.height
			}
			if (component.x < 0) {
				component.x = 0
			} else if (parentWidth < component.x + component.width) {
				component.x = parentWidth - component.width
				return
			}
		}

		@OriginalMember(owner = "client!cga", name = "k", descriptor = "I")
		const val anInt2217: Int = 1403

		@OriginalMember(owner = "client!sr", name = "a", descriptor = "(Lclient!at;I)Lclient!at;")
		@JvmStatic
		fun getParentLayer(@OriginalArg(0) component: Component?): Component? {
			if (component!!.parentId != -1) {
				return Interface.getComponent(component.parentId)
			}
			@Pc(24) val interfaceId: Int = component.idHash ushr 16
			@Pc(29) val hashTableIterator: HashTableIterator = HashTableIterator(InterfaceManager.OPEN_INTERFACES)
			@Pc(34) var subInterface: SubInterface? = hashTableIterator.first() as SubInterface?
			while (subInterface != null) {
				if (subInterface.interfaceId == interfaceId) {
					return Interface.getComponent(subInterface.key.toInt())
				}
				subInterface = hashTableIterator.next() as SubInterface?
			}
			return null
		}

		@OriginalMember(owner = "client!rw", name = "m", descriptor = "I")
		const val WORLD_LIST_VERSION: Int = 1401

		@OriginalMember(owner = "client!ga", name = "a", descriptor = "(Lclient!ha;ILclient!at;)V")
		@JvmStatic
		fun queueItemSpriteIfMissing(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(2) component: Component?) {
			@Pc(38) val spriteMissing: Boolean = TypeLists.OBJ!!.getCachedSprite(component!!.borderThickness, component.containerItemId, component.itemStackSize, component.spriteShadow or 0xFF000000.toInt(), component.renderStack, toolkit, if (component.wearCol) PlayerUpdate.localPlayer!!.playerModel else null) == null
			if (spriteMissing) {
				Component.ITEMS.addLast(JavaObjSprite(component.containerItemId, component.itemStackSize, component.borderThickness, component.spriteShadow or 0xFF000000.toInt(), component.renderStack, component.wearCol))
				Component.redrawComponent(component)
			}
		}

		@OriginalMember(owner = "client!mfa", name = "a", descriptor = "(ILclient!at;)Ljava/lang/String;")
		@JvmStatic
		fun getUseOptionString(@OriginalArg(1) component: Component?): String? {
			if (InterfaceManager.getComponentSettings(component)!!.getUseOptionFlags() == 0) {
				return null
			} else if (component!!.targetVerb == null || component.targetVerb!!.trim { it <= ' ' }.length == 0) {
				return if (InterfaceManager.qaOpTest) "Hidden-use" else null
			} else {
				return component.targetVerb
			}
		}

		@OriginalMember(owner = "client!od", name = "w", descriptor = "I")
		const val CONTENT_TYPE_GAME_WINDOW: Int = 1337

		@OriginalMember(owner = "client!lo", name = "a", descriptor = "(IBI)Lclient!at;")
		@JvmStatic
		fun getComponentWithSlot(@OriginalArg(0) slotId: Int, @OriginalArg(2) idHash: Int): Component? {
			@Pc(9) val component: Component? = Interface.getComponent(idHash)
			if (slotId == -1) {
				return component
			} else if (component == null || component.slotChildren == null || component.slotChildren!!.size <= slotId) {
				return null
			} else {
				return component.slotChildren!![slotId]
			}
		}

		@OriginalMember(owner = "client!jb", name = "Q", descriptor = "Lclient!in;")
		@JvmField
		var packet: Js5ResourceProvider? = null

		@OriginalMember(owner = "client!pb", name = "q", descriptor = "I")
		const val CONTENT_TYPE_COMPASS: Int = 1339
	}
}

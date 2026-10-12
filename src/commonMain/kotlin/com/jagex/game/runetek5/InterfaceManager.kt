package com.jagex.game.runetek5

import com.jagex.game.runetek5.config.rsinterface.MediaType
import com.jagex.game.runetek5.config.sequence.SeqType
import com.jagex.game.runetek5.config.rsinterface.Component
import com.jagex.game.runetek5.config.rsinterface.HookRequest
import com.jagex.game.runetek5.sign.SignLink
import com.jagex.game.runetek5.sign.SignedResource
import com.jagex.game.runetek5.sign.SignedResourceStatus
import awt.Frame
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.client.GameShell
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.graphics.ifcomponent.InterfaceRenderer
import com.jagex.game.runetek5.client.menu.MiniMenu
import com.jagex.game.runetek5.client.window.WindowMode
import com.jagex.game.runetek5.tools.LocalizedText
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.config.rsinterface.IFEvents
import com.jagex.game.runetek5.config.rsinterface.Interface
import com.jagex.game.runetek5.config.rsinterface.SubInterface
import com.jagex.game.runetek5.config.Resource
import com.jagex.game.runetek5.core.datastruct.key.Deque
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable
import com.jagex.game.runetek5.core.util.TimeUtils
import com.jagex.game.runetek5.preference.GamePreferences
import com.jagex.game.runetek5.utils.Utils
import com.jagex.game.runetek5.config.rsinterface.ComponentType
import com.jagex.game.runetek5.client.ClientState
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

object InterfaceManager {

	@OriginalMember(owner = "client!fs", name = "a", descriptor = "(IILclient!at;ZI)V")
	@JvmStatic
	fun initSizes(@OriginalArg(0) parentWidth: Int, @OriginalArg(2) component: Component?, @OriginalArg(3) fireResizeHooks: Boolean, @OriginalArg(4) parentHeight: Int) {
		@Pc(19) val previousWidth: Int = component!!.width
		@Pc(22) val previousHeight: Int = component.height
		if (component.aspectWidthType.toInt() == 0) {
			component.width = component.baseWidth
		} else if (component.aspectWidthType.toInt() == 1) {
			component.width = parentWidth - component.baseWidth
		} else if (component.aspectWidthType.toInt() == 2) {
			component.width = (component.baseWidth * parentWidth) shr 14
		}
		if (component.aspectHeightType.toInt() == 0) {
			component.height = component.baseHeight
		} else if (component.aspectHeightType.toInt() == 1) {
			component.height = parentHeight - component.baseHeight
		} else if (component.aspectHeightType.toInt() == 2) {
			component.height = (component.baseHeight * parentHeight) shr 14
		}
		if (component.aspectWidthType.toInt() == 4) {
			component.width = component.height * component.aspectX / component.aspectY
		}
		if (component.aspectHeightType.toInt() == 4) {
			component.height = component.aspectY * component.width / component.aspectX
		}
		if (InterfaceManager.qaOpTest && (InterfaceManager.getComponentSettings(component)!!.settingsHash != 0 || component.type == ComponentType.CONTAINER)) {
			if (component.height < 5 && component.width < 5) {
				component.height = 5
				component.width = 5
			} else {
				if (component.height <= 0) {
					component.height = 5
				}
				if (component.width <= 0) {
					component.width = 5
				}
			}
		}
		if (component.contentType == Component.CONTENT_TYPE_GAME_WINDOW) {
			InterfaceManager.gameScreenComponent = component
		}
		if (fireResizeHooks && component.onResize != null && (component.width != previousWidth || component.height != previousHeight)) {
			@Pc(202) val hookRequest: HookRequest = HookRequest()
			hookRequest.source = component
			hookRequest.params = component.onResize
			InterfaceManager.PENDING_HOOK_REQUESTS.addLast(hookRequest)
		}
	}

	@OriginalMember(owner = "client!lf", name = "a", descriptor = "(IILclient!wea;III)Ljava/awt/Frame;")
	@JvmStatic
	fun calculateFullscreenFrame(@OriginalArg(0) height: Int, @OriginalArg(1) width: Int, @OriginalArg(2) signLink: SignLink?, @OriginalArg(3) bitDepth: Int): Frame? {
		var bitDepth: Int = bitDepth
		if (!signLink!!.isFullscreenSupported()) {
			return null
		}
		@Pc(20) val displayModes: Array<DisplayModeDetails?>? = SignLink.getDisplayModes(signLink)
		if (displayModes == null) {
			return null
		}
		@Pc(26) var found: Boolean = false
		@Pc(28) var i: Int = 0
		while (i < displayModes.size) {
			if (width == displayModes[i]!!.width && displayModes[i]!!.height == height && (!found || bitDepth < displayModes[i]!!.bitDepth)) {
				bitDepth = displayModes[i]!!.bitDepth
				found = true
			}
			i++
		}
		if (!found) {
			return null
		}
		@Pc(101) val signedResource: SignedResource = signLink.enterFullscreen(height, bitDepth, width)
		while (signedResource.status == SignedResourceStatus.IDLE) {
			TimeUtils.sleep(10L)
		}
		@Pc(116) val frame: Frame? = signedResource.result as Frame?
		if (frame == null) {
			return null
		} else if (signedResource.status == SignedResourceStatus.ERROR) {
			InterfaceManager.disposeFrame(frame, signLink)
			return null
		} else {
			return frame
		}
	}

	@OriginalMember(owner = "client!jg", name = "a", descriptor = "(Lclient!at;B)V")
	@JvmStatic
	fun prepareSequenceOnInterface(@OriginalArg(0) component: Component?) {
		if (Component.CONTENT_TYPE_LOCAL_PLAYER_MODEL != component!!.contentType) {
			return
		}
		if (PlayerUpdate.localPlayer!!.displayName == null) {
			component.secondaryMediaId = 0
			component.primaryMediaId = 0
			return
		}
		component.spritePitch = 150
		component.spriteRoll = (kotlin.math.sin(ClientState.clientCycle.toDouble() / 40.0) * 256.0).toInt() and 0x7FF
		component.mediaType = MediaType.PLAYER_MODEL
		component.primaryMediaId = PlayerUpdate.myPlayerIndex
		component.secondaryMediaId = Utils.hashString(PlayerUpdate.localPlayer!!.displayName!!)
		component.animation = PlayerUpdate.localPlayer!!.movingSequence!!.sequenceId
		component.currentFrameDuration = 0
		component.nextFrameId = PlayerUpdate.localPlayer!!.movingSequence!!.nextFrameId
		component.currentFrameId = PlayerUpdate.localPlayer!!.movingSequence!!.currentFrameId
		@Pc(76) val seqType: SeqType? = if (component.animation == -1) null else TypeLists.SEQ!!.list(component.animation)
		if (seqType != null) {
			SoundManager.playInterfaceSequenceSound(component.currentFrameId, seqType)
		}
	}

	@OriginalMember(owner = "client!mea", name = "a", descriptor = "(Lclient!at;III)V")
	@JvmStatic
	fun startDragging(@OriginalArg(0) component: Component?, @OriginalArg(1) offsetX: Int, @OriginalArg(2) offsetY: Int) {
		if (InterfaceDrag.component != null || MiniMenu.isOpen || (component == null || InterfaceDrag.getDragParent(component) == null)) {
			return
		}
		InterfaceDrag.component = component
		InterfaceDrag.aClass24_7 = InterfaceDrag.getDragParent(component)
		InterfaceRenderer.dragTickCount = 0
		InterfaceDrag.dragOffsetY = offsetY
		InterfaceDrag.dragOffsetX = offsetX
		InterfaceDrag.isDragging = false
	}

	@OriginalMember(owner = "client!gn", name = "a", descriptor = "(B)V")
	@JvmStatic
	fun canvasAdjustment() {
		@Pc(5) var screenSizePreference: Int = 0
		if (GamePreferences.uid != null) {
			screenSizePreference = GamePreferences.uid!!.maxScreenSize!!.getValue()
		}
		@Pc(28) val preferredWidth: Int
		@Pc(46) val preferredHeight: Int
		if (screenSizePreference == 2) {
			preferredWidth = if (WindowMode.resizableScreenWidth <= 800) WindowMode.resizableScreenWidth else 800
			GameShell.screenCanvasWidth = preferredWidth
			MiniMenu.gameCanvasX = (WindowMode.resizableScreenWidth - preferredWidth) / 2
			preferredHeight = if (WindowMode.resizableScreenHeight > 600) 600 else WindowMode.resizableScreenHeight
			GameShell.screenCanvasHeight = preferredHeight
			GameShell.gameCanvasY = 0
		} else if (screenSizePreference == 1) {
			preferredWidth = if (WindowMode.resizableScreenWidth <= 1024) WindowMode.resizableScreenWidth else 1024
			MiniMenu.gameCanvasX = (WindowMode.resizableScreenWidth - preferredWidth) / 2
			preferredHeight = if (WindowMode.resizableScreenHeight <= 768) WindowMode.resizableScreenHeight else 768
			GameShell.screenCanvasWidth = preferredWidth
			GameShell.gameCanvasY = 0
			GameShell.screenCanvasHeight = preferredHeight
		} else {
			GameShell.gameCanvasY = 0
			GameShell.screenCanvasHeight = WindowMode.resizableScreenHeight
			MiniMenu.gameCanvasX = 0
			GameShell.screenCanvasWidth = WindowMode.resizableScreenWidth
		}
	}

	@OriginalMember(owner = "client!ida", name = "a", descriptor = "(II)Ljava/lang/String;")
	@JvmStatic
	fun formatAmount(@OriginalArg(1) amount: Int): String? {
		@Pc(8) var string: String = amount.toString()
		@Pc(18) var i: Int = string.length - 3
		while (i > 0) {
			string = string.substring(0, i) + "," + string.substring(i)
			i -= 3
		}
		if (string.length > 9) {
			return " <col=00ff80>" + string.substring(0, string.length - 8) + LocalizedText.aClass351_35.get(ClientState.language) + " (" + string + ")</col>"
		} else if (string.length > 6) {
			return " <col=ffffff>" + string.substring(0, string.length - 4) + LocalizedText.aClass351_37.get(ClientState.language) + " (" + string + ")</col>"
		} else {
			return " <col=ffff00>" + string + "</col>"
		}
	}

	@OriginalMember(owner = "client!jj", name = "a", descriptor = "(Ljava/awt/Frame;Lclient!wea;Z)V")
	@JvmStatic
	fun disposeFrame(@OriginalArg(0) frame: Frame?, @OriginalArg(1) signLink: SignLink?) {
		while (true) {
			@Pc(10) val signedResource: SignedResource = signLink!!.exitFullscreen(frame)
			while (signedResource.status == SignedResourceStatus.IDLE) {
				TimeUtils.sleep(10L)
			}
			if (signedResource.status == SignedResourceStatus.SUCCESS) {
				frame!!.setVisible(false)
				frame.dispose()
				return
			}
			TimeUtils.sleep(100L)
		}
	}

	@OriginalMember(owner = "client!client", name = "a", descriptor = "(Lclient!at;)Lclient!wb;")
	@JvmStatic
	fun getComponentSettings(@OriginalArg(0) component: Component?): IFEvents? {
		@Pc(13) val serverActiveProperties: IFEvents? = IFEvents.ICOMPONENT_SETTINGS_SLOTS.get((component!!.idHash.toLong() shl 32) + component.slotId.toLong()) as IFEvents?
		return if (serverActiveProperties == null) component.targetParams else serverActiveProperties
	}

	@OriginalMember(owner = "client!cp", name = "a", descriptor = "(IB)Z")
	@JvmStatic
	fun loadInterface(@OriginalArg(0) interfaceId: Int): Boolean {
		if (InterfaceManager.loadedInterfaces!![interfaceId]) {
			return true
		} else if (Resource.aClass157_255!!.isGroupReady(interfaceId)) {
			@Pc(23) val componentCount: Int = Resource.aClass157_255!!.getGroupCapacity(interfaceId)
			if (componentCount == 0) {
				InterfaceManager.loadedInterfaces!![interfaceId] = true
				return true
			}
			if (Interface.interfaces!![interfaceId] == null) {
				Interface.interfaces!![interfaceId] = arrayOfNulls<Component>(componentCount)
			}
			@Pc(42) var i: Int = 0
			while (i < componentCount) {
				if (Interface.interfaces!![interfaceId]!![i] == null) {
					@Pc(58) val data: ByteArray? = Resource.aClass157_255!!.getFile(interfaceId, i)
					if (data != null) {
						val componentArray: Array<Component?> = Interface.interfaces!![interfaceId]!!
						@Pc(70) val component: Component = Component()
						componentArray[i] = component
						component.idHash = i + (interfaceId shl 16)
						if (data[0].toInt() != -1) {
							throw IllegalStateException("if1")
						}
						component.decode(Packet(data))
					}
				}
				i++
			}
			InterfaceManager.loadedInterfaces!![interfaceId] = true
			return true
		} else {
			return false
		}
	}

	@OriginalMember(owner = "client!me", name = "a", descriptor = "(B)V")
	@JvmStatic
	fun resetInterfaces() {
		Interface.interfaces = arrayOfNulls<Array<Component?>>(Resource.aClass157_255!!.getCapacity())
		Interface.aClass24ArrayArray1 = arrayOfNulls<Array<Component?>>(Resource.aClass157_255!!.getCapacity())
		InterfaceManager.loadedInterfaces = BooleanArray(Resource.aClass157_255!!.getCapacity())
	}

	@OriginalMember(owner = "client!pe", name = "d", descriptor = "[Z")
	@JvmField
	var loadedInterfaces: BooleanArray? = null

	@OriginalMember(owner = "client!lm", name = "a", descriptor = "(I[Lclient!at;I)V")
	@JvmStatic
	fun updateComponentAnimations(@OriginalArg(0) idHash: Int, @OriginalArg(1) components: Array<Component?>?) {
		@Pc(7) var i: Int = 0
		while (i < components!!.size) {
			@Pc(13) val component: Component? = components[i]
			if (component != null && idHash == component.parentId && !InterfaceRenderer.isComponentHidden(component)) {
				if (component.type == ComponentType.CONTAINER) {
					updateComponentAnimations(component.idHash, components)
					if (component.slotChildren != null) {
						updateComponentAnimations(component.idHash, component.slotChildren)
					}
					@Pc(47) val subInterface: SubInterface? = InterfaceManager.OPEN_INTERFACES.get(component.idHash.toLong()) as SubInterface?
					if (subInterface != null) {
						InterfaceManager.loadAndAnimateInterface(subInterface.interfaceId)
					}
				}
				if (component.type == ComponentType.MODEL && component.animation != -1) {
					@Pc(69) val seqType: SeqType? = TypeLists.SEQ!!.list(component.animation)
					if (seqType != null) {
						component.currentFrameDuration += ClientState.animatingStep
						@Pc(80) val animation: Int = component.currentFrameId
						while (component.currentFrameDuration > seqType.durations!![component.currentFrameId]) {
							component.currentFrameDuration -= seqType.durations!![component.currentFrameId]
							component.currentFrameId++
							if (component.currentFrameId >= seqType.frames!!.size) {
								component.currentFrameId -= seqType.replayInterval
								if (component.currentFrameId < 0 || seqType.frames!!.size <= component.currentFrameId) {
									component.currentFrameId = 0
								}
							}
							component.nextFrameId = component.currentFrameId + 1
							if (seqType.frames!!.size <= component.nextFrameId) {
								component.nextFrameId -= seqType.replayInterval
								if (component.nextFrameId < 0 || component.nextFrameId >= seqType.frames!!.size) {
									component.nextFrameId = -1
								}
							}
							Component.redrawComponent(component)
						}
						if (animation != component.currentFrameId) {
							SoundManager.playInterfaceSequenceSound(component.currentFrameId, seqType)
						}
					}
				}
			}
			i++
		}
	}

	@OriginalMember(owner = "client!ab", name = "E", descriptor = "Lclient!at;")
	@JvmField
	var gameScreenComponent: Component? = null

	@OriginalMember(owner = "client!dga", name = "H", descriptor = "Lclient!uh;")
	@JvmField
	val PENDING_HOOK_REQUESTS: Deque = Deque()

	@OriginalMember(owner = "client!nha", name = "c", descriptor = "Lclient!eq;")
	@JvmField
	var OPEN_INTERFACES: IterableHashTable = IterableHashTable(8)

	@OriginalMember(owner = "client!al", name = "a", descriptor = "(ZIIZI[Lclient!at;)V")
	@JvmStatic
	fun updateInterfaceDimensions(@OriginalArg(1) idHash: Int, @OriginalArg(2) parentWidth: Int, @OriginalArg(3) fireResizeHooks: Boolean, @OriginalArg(4) parentHeight: Int, @OriginalArg(5) components: Array<Component?>?) {
		@Pc(11) var i: Int = 0
		while (i < components!!.size) {
			@Pc(17) val component: Component? = components[i]
			if (component != null && idHash == component.parentId) {
				InterfaceManager.initSizes(parentWidth, component, fireResizeHooks, parentHeight)
				Component.updatePosition(parentWidth, parentHeight, component)
				if (component.scrollX > component.scrollWidth - component.width) {
					component.scrollX = component.scrollWidth - component.width
				}
				if (component.scrollX < 0) {
					component.scrollX = 0
				}
				if (component.scrollY > component.scrollHeight - component.height) {
					component.scrollY = component.scrollHeight - component.height
				}
				if (component.scrollY < 0) {
					component.scrollY = 0
				}
				if (component.type == ComponentType.CONTAINER) {
					Component.layoutChildren(fireResizeHooks, component)
				}
			}
			i++
		}
	}

	@OriginalMember(owner = "client!mt", name = "a", descriptor = "(Lclient!at;I)V")
	@JvmStatic
	fun relayoutAgainstParent(@OriginalArg(0) component: Component?) {
		@Pc(15) val component2: Component? = Component.getParentLayer(component)
		@Pc(23) val screenCanvasWidth: Int
		@Pc(20) val parentHeight: Int
		if (component2 == null) {
			parentHeight = GameShell.screenCanvasHeight
			screenCanvasWidth = GameShell.screenCanvasWidth
		} else {
			parentHeight = component2.height
			screenCanvasWidth = component2.width
		}
		InterfaceManager.initSizes(screenCanvasWidth, component, false, parentHeight)
		Component.updatePosition(screenCanvasWidth, parentHeight, component)
	}

	@OriginalMember(owner = "client!jaa", name = "a", descriptor = "(IIIZI)V")
	@JvmStatic
	fun updateInterfaceDimensions(@OriginalArg(0) parentHeight: Int, @OriginalArg(2) interfaceId: Int, @OriginalArg(3) fireResizeHooks: Boolean, @OriginalArg(4) parentWidth: Int) {
		if (InterfaceManager.loadInterface(interfaceId)) {
			InterfaceManager.updateInterfaceDimensions(-1, parentWidth, fireResizeHooks, parentHeight, Interface.interfaces!![interfaceId])
		}
	}

	@OriginalMember(owner = "client!cc", name = "P", descriptor = "I")
	@JvmField
	var lastMiscUpdateTick: Int = 0

	@OriginalMember(owner = "client!vc", name = "g", descriptor = "Z")
	@JvmField
	var qaOpTest: Boolean = false

	@OriginalMember(owner = "client!wga", name = "Bd", descriptor = "I")
	@JvmField
	var interfaceDebugMode: Int = 0

	@OriginalMember(owner = "client!it", name = "l", descriptor = "Lclient!at;")
	@JvmField
	var activeDialogueContinue: Component? = null

	@OriginalMember(owner = "client!ada", name = "a", descriptor = "(IB)V")
	@JvmStatic
	fun loadAndAnimateInterface(@OriginalArg(0) interfaceId: Int) {
		if (InterfaceManager.loadInterface(interfaceId)) {
			InterfaceManager.updateComponentAnimations(-1, Interface.interfaces!![interfaceId])
		}
	}
}

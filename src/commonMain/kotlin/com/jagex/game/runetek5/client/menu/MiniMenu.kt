package com.jagex.game.runetek5.client.menu

import com.jagex.game.runetek5.plugin.api.MenuTarget
import com.jagex.game.runetek5.plugin.PluginRuntime
import com.jagex.game.runetek5.entity.NPCEntity
import com.jagex.game.runetek5.entity.NPCEntityNode
import com.jagex.game.runetek5.entity.PlayerEntity
import com.jagex.game.runetek5.core.constants.MiniMenuAction
import com.jagex.game.runetek5.core.datastruct.key.DequeIterator
import com.jagex.game.runetek5.cache.node.QueueIterator
import com.jagex.game.runetek5.core.datastruct.key.MouseOverTextY
import com.jagex.game.runetek5.config.rsinterface.Component
import com.jagex.game.runetek5.config.npc.NPCType
import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer
import com.jagex.game.runetek5.graphics.Toolkit
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.event.keyboard.SimpleKeyboardMonitorConstants
import com.jagex.game.runetek5.graphics.font.FontMetrics
import com.jagex.game.runetek5.graphics.ifcomponent.InterfaceComponentRenderer
import com.jagex.game.runetek5.graphics.sprite.StaticMedia
import com.jagex.game.runetek5.client.GameShell
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.client.input.keyboard.KeyRecorder
import com.jagex.game.runetek5.LocType
import com.jagex.game.runetek5.cache.node.Queue
import com.jagex.game.runetek5.cache.node.lru.LRUCache
import com.jagex.game.runetek5.core.datastruct.key.Deque
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable
import com.jagex.game.runetek5.tools.LocalizedText
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.client.Game
import com.jagex.game.runetek5.interaction.chat.ChatBox
import com.jagex.game.runetek5.client.input.mouse.CursorManager
import com.jagex.game.runetek5.client.input.mouse.MouseRecord
import com.jagex.game.runetek5.config.rsinterface.Interface
import com.jagex.game.runetek5.Cursor
import com.jagex.game.runetek5.InterfaceDrag
import com.jagex.game.runetek5.InterfaceRectangles
import com.jagex.game.runetek5.NPCList
import com.jagex.game.runetek5.Find
import com.jagex.game.runetek5.OrthoMode
import com.jagex.game.runetek5.ScriptRunner
import com.jagex.game.runetek5.cache.node.lru.CacheableNode
import com.jagex.game.runetek5.config.Resource
import com.jagex.game.runetek5.graphics.SpriteDataProvider
import com.jagex.game.runetek5.graphics.sprite.NativeSprite
import com.jagex.game.runetek5.graphics.sprite.SpriteType
import com.jagex.game.runetek5.vardomain.PlayerVarDomain
import com.jagex.game.runetek5.config.rsinterface.ComponentType
import com.jagex.game.runetek5.client.ClientState
import util.Random
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

object MiniMenu {

	@OriginalMember(owner = "client!gt", name = "p", descriptor = "[I")
	@JvmField
	val NPC_TURN_DIRECTION: IntArray = intArrayOf(-1, 8192, 0, -1, 12288, 10240, 14336, -1, 4096, 6144, 2048)

	@OriginalMember(owner = "client!vj", name = "db", descriptor = "Z")
	@JvmField
	var debugOps: Boolean = false

	@OriginalMember(owner = "client!hc", name = "a", descriptor = "(ZLjava/lang/String;IBZIIZIJLjava/lang/String;JI)V")
	@JvmStatic
	fun addEntry(@OriginalArg(0) isSelectable: Boolean, @OriginalArg(1) string: String?, @OriginalArg(2) param1: Int, @OriginalArg(4) isUncategorized: Boolean, @OriginalArg(5) param0: Int, @OriginalArg(6) itemId: Int, @OriginalArg(7) showOtherLevelIcon: Boolean, @OriginalArg(8) clickType: Int, @OriginalArg(9) bitpacked: Long, @OriginalArg(10) string2: String?, @OriginalArg(11) entityUid: Long, @OriginalArg(12) cursor: Int) {
		if (!MiniMenu.isOpen && MiniMenu.entryCount < 500) {
			@Pc(17) val resolvedCursor: Int = if (cursor == -1) Cursor.interfaceCursorId else cursor
			@Pc(38) val miniMenuEntryInner: MiniMenuEntry = MiniMenuEntry(string2, string, resolvedCursor, clickType, itemId, entityUid, param0, param1, showOtherLevelIcon, isSelectable, bitpacked, isUncategorized)
			MiniMenu.addEntry(miniMenuEntryInner)
		}
	}

	@OriginalMember(owner = "client!jb", name = "a", descriptor = "(BII)Z")
	@JvmStatic
	fun isOpenForComponent(@OriginalArg(1) slot: Int, @OriginalArg(2) componentHash: Int): Boolean {
		if (!MiniMenu.isOpen) {
			return false
		}
		@Pc(18) val interfaceId: Int = componentHash shr 16
		@Pc(22) val componentId: Int = componentHash and 0xFFFF
		if (Interface.interfaces!![interfaceId] == null || Interface.interfaces!![interfaceId]!![componentId] == null) {
			return false
		}
		@Pc(40) val component: Component = Interface.interfaces!![interfaceId]!![componentId]!!
		@Pc(51) var miniMenuEntryInner: MiniMenuEntry?
		if (slot == -1 && component.type == ComponentType.CONTAINER) {
			miniMenuEntryInner = MiniMenu.entryList.first() as MiniMenuEntry?
			while (miniMenuEntryInner != null) {
				if (miniMenuEntryInner.clickType == MiniMenuAction.TGT_BUTTON || miniMenuEntryInner.clickType == MiniMenuAction.IF_BUTTONX2 || miniMenuEntryInner.clickType == MiniMenuAction.IF_BUTTONT || miniMenuEntryInner.clickType == MiniMenuAction.IF_BUTTONX1 || miniMenuEntryInner.clickType == MiniMenuAction.PAUSE_BUTTON) {
					@Pc(82) var component2: Component? = Interface.getComponent(miniMenuEntryInner.param1)
					while (component2 != null) {
						if (component2.idHash == component.idHash) {
							return true
						}
						component2 = Component.getParentLayer(component2)
					}
				}
				miniMenuEntryInner = MiniMenu.entryList.next() as MiniMenuEntry?
			}
		} else {
			miniMenuEntryInner = MiniMenu.entryList.first() as MiniMenuEntry?
			while (miniMenuEntryInner != null) {
				if (miniMenuEntryInner.param0 == slot && miniMenuEntryInner.param1 == component.idHash && (miniMenuEntryInner.clickType == MiniMenuAction.TGT_BUTTON || miniMenuEntryInner.clickType == MiniMenuAction.IF_BUTTONX2 || miniMenuEntryInner.clickType == MiniMenuAction.IF_BUTTONT || miniMenuEntryInner.clickType == MiniMenuAction.IF_BUTTONX1 || miniMenuEntryInner.clickType == MiniMenuAction.PAUSE_BUTTON)) {
					return true
				}
				miniMenuEntryInner = MiniMenu.entryList.next() as MiniMenuEntry?
			}
		}
		return false
	}

	@OriginalMember(owner = "client!un", name = "n", descriptor = "(I)Z")
	@JvmStatic
	fun isTopEntryInterfaceAction(): Boolean {
		if (MiniMenu.aClass2_Sub6_Sub3_3 == null) {
			return false
		} else {
			if (MiniMenu.aClass2_Sub6_Sub3_3!!.clickType >= 2000) {
				MiniMenu.aClass2_Sub6_Sub3_3!!.clickType -= 2000
			}
			return MiniMenu.aClass2_Sub6_Sub3_3!!.clickType == MiniMenuAction.IF_BUTTONX2
		}
	}

	@OriginalMember(owner = "client!vo", name = "a", descriptor = "(IIILclient!ha;Lclient!db;IIIBIII)V")
	@JvmStatic
	fun drawMenuEntry(@OriginalArg(0) menuY: Int, @OriginalArg(1) hoverColor: Int, @OriginalArg(2) entryY: Int, @OriginalArg(3) toolkit: Toolkit?, @OriginalArg(4) miniMenuEntryInner: MiniMenuEntry?, @OriginalArg(5) menuWidth: Int, @OriginalArg(6) mouseY: Int, @OriginalArg(7) textColor: Int, @OriginalArg(9) menuHeight: Int, @OriginalArg(10) menuX: Int, @OriginalArg(11) mouseX: Int) {
		var textColor: Int = textColor
		if (menuX < mouseX && mouseX < menuX + menuWidth && mouseY > entryY - 13 && mouseY < entryY + 3 && miniMenuEntryInner!!.showOtherLevelIcon) {
			textColor = hoverColor
		}
		@Pc(52) var quests: IntArray? = null
		if (MiniMenu.isObjClickType(miniMenuEntryInner!!.clickType)) {
			quests = TypeLists.OBJ!!.list(miniMenuEntryInner.entityUid.toInt()).quests
		} else if (miniMenuEntryInner.itemId != -1) {
			quests = TypeLists.OBJ!!.list(miniMenuEntryInner.itemId).quests
		} else if (MiniMenu.isNpcClickType(miniMenuEntryInner.clickType)) {
			@Pc(122) val npcEntityNode: NPCEntityNode? = NPCList.NPC_MAP.get(miniMenuEntryInner.entityUid) as NPCEntityNode?
			if (npcEntityNode != null) {
				@Pc(127) val npcEntity: NPCEntity? = npcEntityNode.npc
				@Pc(130) var npcType: NPCType? = npcEntity!!.npcType
				if (npcType!!.alternativeIds != null) {
					npcType = npcType.getMultiNpc(PlayerVarDomain.playerVarProvider)
				}
				if (npcType != null) {
					quests = npcType.quests
				}
			}
		} else if (MiniMenu.isLocClickType(miniMenuEntryInner.clickType)) {
			@Pc(91) var locType: LocType?
			if (miniMenuEntryInner.clickType == MiniMenuAction.OPLOC6) {
				locType = TypeLists.LOC!!.list(miniMenuEntryInner.entityUid.toInt())
			} else {
				locType = TypeLists.LOC!!.list(((miniMenuEntryInner.entityUid ushr 32) and 0x7FFFFFFFL).toInt())
			}
			if (locType.alternativeIds != null) {
				locType = locType.getMultiLoc(PlayerVarDomain.playerVarProvider)
			}
			if (locType != null) {
				quests = locType.quests
			}
		}
		@Pc(164) var string: String? = MiniMenu.getTextFrom(miniMenuEntryInner)
		if (quests != null) {
			string = string + ChatBox.getQuestIconTags(quests)
		}
		MiniMenu.minimenuHeader!!.drawStringWithSprites(entryY, string, textColor, menuX + 3, StaticMedia.chatIcons, StaticMedia.chatScaleHeights)
		if (miniMenuEntryInner.isSelectable) {
			StaticMedia.otherLevelSprite!!.render(FontMetrics.defaultFontMetrics!!.getWidthNoSprites(string) + menuX + 5, entryY + -12)
		}
	}

	@OriginalMember(owner = "client!vo", name = "a", descriptor = "(IIZ)Z")
	@JvmStatic
	fun hasHigherPriority(@OriginalArg(0) existingClickType: Int, @OriginalArg(1) clickType: Int): Boolean {
		if (existingClickType >= 1000 && clickType < 1000) {
			return true
		} else if (existingClickType >= 1000 || clickType >= 1000) {
			return existingClickType >= 1000 && clickType >= 1000
		} else if (MiniMenu.isUseOnClickType(clickType)) {
			return true
		} else {
			return !MiniMenu.isUseOnClickType(existingClickType)
		}
	}

	@OriginalMember(owner = "client!wq", name = "a", descriptor = "(BII)V")
	@JvmStatic
	fun openContextMenu(@OriginalArg(1) mouseX: Int, @OriginalArg(2) mouseY: Int) {
		@Pc(13) var menuWidth: Int = FontMetrics.defaultFontMetrics!!.getWidthNoSprites(LocalizedText.CHOOSE_OPTION.get(ClientState.language))
		@Pc(55) val menuHeight: Int
		@Pc(26) var local26: Int
		if (MiniMenu.isCollapsed) {
			@Pc(62) var miniMenuEntry: ColoredMiniMenuEntry? = MiniMenu.sortedColoredEntries.first() as ColoredMiniMenuEntry?
			while (miniMenuEntry != null) {
				var entryWidth: Int
				if (miniMenuEntry.queueSize == 1) {
					entryWidth = MiniMenu.getWidthForEntryWithQuests(miniMenuEntry.nodeQueue.sentinel.secondaryNext as MiniMenuEntry?)
				} else {
					entryWidth = MiniMenu.getPrefixTextWidth(miniMenuEntry)
				}
				if (entryWidth > menuWidth) {
					menuWidth = entryWidth
				}
				miniMenuEntry = MiniMenu.sortedColoredEntries.next() as ColoredMiniMenuEntry?
			}
			menuWidth += 8
			menuHeight = MiniMenu.coloredEntryCount * 16 + 21
			InterfaceComponentRenderer.menuHeight = (if (FontMetrics.useBoldFont) 26 else 22) + MiniMenu.coloredEntryCount * 16
		} else {
			@Pc(20) var miniMenuEntryInner: MiniMenuEntry? = MiniMenu.entryList.first() as MiniMenuEntry?
			while (miniMenuEntryInner != null) {
				val entryWidth: Int = MiniMenu.getWidthForEntryWithQuests(miniMenuEntryInner)
				if (menuWidth < entryWidth) {
					menuWidth = entryWidth
				}
				miniMenuEntryInner = MiniMenu.entryList.next() as MiniMenuEntry?
			}
			menuWidth += 8
			InterfaceComponentRenderer.menuHeight = (if (FontMetrics.useBoldFont) 26 else 22) + MiniMenu.entryCount * 16
			menuHeight = MiniMenu.entryCount * 16 + 21
		}
		@Pc(124) var menuX: Int = mouseX - menuWidth / 2
		if (GameShell.screenCanvasWidth < menuWidth + menuX) {
			menuX = GameShell.screenCanvasWidth - menuWidth
		}
		if (menuX < 0) {
			menuX = 0
		}
		var menuY: Int = mouseY
		if (mouseY + menuHeight > GameShell.screenCanvasHeight) {
			menuY = GameShell.screenCanvasHeight - menuHeight
		}
		if (menuY < 0) {
			menuY = 0
		}
		MiniMenu.menuOpenX = menuX
		MiniMenu.isOpen = true
		MiniMenu.menuOpenY = menuY
		MiniMenu.menuOpenWidth = menuWidth
	}

	@OriginalMember(owner = "client!co", name = "d", descriptor = "(B)I")
	@JvmStatic
	fun getClickType(): Int {
		if (MiniMenu.isOpen) {
			return 6
		} else if (MiniMenu.aClass2_Sub6_Sub3_1 == null) {
			return 0
		} else {
			@Pc(19) val clickType: Int = MiniMenu.aClass2_Sub6_Sub3_1!!.clickType
			if (MiniMenu.isComponentClickType(clickType)) {
				return 1
			} else if (MiniMenu.isObjClickType(clickType)) {
				return 2
			} else if (MiniMenu.isLocClickType(clickType)) {
				return 3
			} else if (MiniMenu.isNpcClickType(clickType)) {
				return 4
			} else {
				return 5
			}
		}
	}

	@OriginalMember(owner = "client!tk", name = "a", descriptor = "(Lclient!db;Z)I")
	@JvmStatic
	fun getWidthForEntryWithQuests(@OriginalArg(0) miniMenuEntryInner: MiniMenuEntry?): Int {
		@Pc(7) var string: String? = MiniMenu.getTextFrom(miniMenuEntryInner)
		@Pc(9) var quests: IntArray? = null
		if (MiniMenu.isObjClickType(miniMenuEntryInner!!.clickType)) {
			quests = TypeLists.OBJ!!.list(miniMenuEntryInner.entityUid.toInt()).quests
		} else if (miniMenuEntryInner.itemId != -1) {
			quests = TypeLists.OBJ!!.list(miniMenuEntryInner.itemId).quests
		} else if (MiniMenu.isNpcClickType(miniMenuEntryInner.clickType)) {
			@Pc(78) val npcEntityNode: NPCEntityNode? = NPCList.NPC_MAP.get(miniMenuEntryInner.entityUid) as NPCEntityNode?
			if (npcEntityNode != null) {
				@Pc(83) val npcEntity: NPCEntity? = npcEntityNode.npc
				@Pc(86) var npcType: NPCType? = npcEntity!!.npcType
				if (npcType!!.alternativeIds != null) {
					npcType = npcType.getMultiNpc(PlayerVarDomain.playerVarProvider)
				}
				if (npcType != null) {
					quests = npcType.quests
				}
			}
		} else if (MiniMenu.isLocClickType(miniMenuEntryInner.clickType)) {
			@Pc(43) var locType: LocType?
			if (miniMenuEntryInner.clickType == MiniMenuAction.OPLOC6) {
				locType = TypeLists.LOC!!.list(miniMenuEntryInner.entityUid.toInt())
			} else {
				locType = TypeLists.LOC!!.list(((miniMenuEntryInner.entityUid ushr 32) and 0x7FFFFFFFL).toInt())
			}
			if (locType.alternativeIds != null) {
				locType = locType.getMultiLoc(PlayerVarDomain.playerVarProvider)
			}
			if (locType != null) {
				quests = locType.quests
			}
		}
		if (quests != null) {
			string = string + ChatBox.getQuestIconTags(quests)
		}
		@Pc(135) var width: Int = FontMetrics.defaultFontMetrics!!.getTextWidth(string, StaticMedia.chatIcons)
		if (miniMenuEntryInner.isSelectable) {
			width += StaticMedia.otherLevelSprite!!.getWidth() + 4
		}
		return width
	}

	@OriginalMember(owner = "client!bw", name = "r", descriptor = "I")
	@JvmField
	var gameCanvasX: Int = 0

	@OriginalMember(owner = "client!bda", name = "c", descriptor = "(II)Z")
	@JvmStatic
	fun containsEntryForNpc(@OriginalArg(0) npcIndex: Int): Boolean {
		@Pc(14) var miniMenuEntryInner: MiniMenuEntry? = MiniMenu.entryList.first() as MiniMenuEntry?
		while (miniMenuEntryInner != null) {
			if (MiniMenu.isNpcClickType(miniMenuEntryInner.clickType) && npcIndex.toLong() == miniMenuEntryInner.entityUid) {
				return true
			}
			miniMenuEntryInner = MiniMenu.entryList.next() as MiniMenuEntry?
		}
		return false
	}

	@OriginalMember(owner = "client!cw", name = "a", descriptor = "(Lclient!ha;B)V")
	@JvmStatic
	fun renderTooltipText(@OriginalArg(0) toolkit: Toolkit?) {
		if (MiniMenu.entryCount < 2 && !MiniMenu.interfaceSelectionActive || InterfaceDrag.component != null) {
			return
		}
		@Pc(42) var string: String?
		if (MiniMenu.interfaceSelectionActive && MiniMenu.entryCount < 2) {
			string = MiniMenu.useOnType + LocalizedText.aClass351_34.get(ClientState.language) + MiniMenu.useOnTarget + " ->"
		} else if (MiniMenu.shiftClickEnabled && KeyRecorder.keyRecorder!!.isPressed(SimpleKeyboardMonitorConstants.KEY_CODE_SHIFT) && MiniMenu.entryCount > 2) {
			string = MiniMenu.getTextFrom(MiniMenu.aClass2_Sub6_Sub3_1)
		} else {
			@Pc(56) val miniMenuEntryInner: MiniMenuEntry? = MiniMenu.aClass2_Sub6_Sub3_1
			if (miniMenuEntryInner == null) {
				return
			}
			string = MiniMenu.getTextFrom(miniMenuEntryInner)
			@Pc(65) var quests: IntArray? = null
			if (MiniMenu.isObjClickType(miniMenuEntryInner.clickType)) {
				quests = TypeLists.OBJ!!.list(miniMenuEntryInner.entityUid.toInt()).quests
			} else if (miniMenuEntryInner.itemId != -1) {
				quests = TypeLists.OBJ!!.list(miniMenuEntryInner.itemId).quests
			} else if (MiniMenu.isNpcClickType(miniMenuEntryInner.clickType)) {
				@Pc(96) val npcEntityNode: NPCEntityNode? = NPCList.NPC_MAP.get(miniMenuEntryInner.entityUid) as NPCEntityNode?
				if (npcEntityNode != null) {
					@Pc(101) val npcEntity: NPCEntity? = npcEntityNode.npc
					@Pc(104) var npcType: NPCType? = npcEntity!!.npcType
					if (npcType!!.alternativeIds != null) {
						npcType = npcType.getMultiNpc(PlayerVarDomain.playerVarProvider)
					}
					if (npcType != null) {
						quests = npcType.quests
					}
				}
			} else if (MiniMenu.isLocClickType(miniMenuEntryInner.clickType)) {
				@Pc(140) var locType: LocType?
				if (miniMenuEntryInner.clickType == MiniMenuAction.OPLOC6) {
					locType = TypeLists.LOC!!.list(miniMenuEntryInner.entityUid.toInt())
				} else {
					locType = TypeLists.LOC!!.list(((miniMenuEntryInner.entityUid ushr 32) and 0x7FFFFFFFL).toInt())
				}
				if (locType.alternativeIds != null) {
					locType = locType.getMultiLoc(PlayerVarDomain.playerVarProvider)
				}
				if (locType != null) {
					quests = locType.quests
				}
			}
			if (quests != null) {
				string = string + ChatBox.getQuestIconTags(quests)
			}
		}
		if (MiniMenu.entryCount > 2) {
			string = string + "<col=ffffff> / " + (MiniMenu.entryCount - 2) + LocalizedText.aClass351_27.get(ClientState.language)
		}
		if (MouseOverTextY.component != null) {
			@Pc(217) var font: AbstractFontRenderer? = MouseOverTextY.component!!.getFontRenderer(toolkit)
			if (font == null) {
				font = MiniMenu.minimenuHeader
			}
			font!!.renderTextAntiMacro(StaticMedia.chatScaleHeights, MouseOverTextY.component!!.width, StaticMedia.chatIcons, MouseOverTextY.component!!.textVerticalAli, MouseOverTextY.component!!.color, MouseOverTextY.component!!.height, MouseOverTextY.component!!.spriteShadow, MiniMenu.RANDOMIZER, MouseOverTextY.component!!.textHorizontalAli, MouseOverTextY.mouseOverTextY, MiniMenu.randomSeed, MouseOverTextY.mouseOverTextX, string, MiniMenu.tooltipTextBounds)
			InterfaceRectangles.inboundsComponentToTop(MiniMenu.tooltipTextBounds[0], MiniMenu.tooltipTextBounds[2], MiniMenu.tooltipTextBounds[1], MiniMenu.tooltipTextBounds[3])
		} else if (MiniMenu.component != null && Game.CURRENT_GAME === Game.RUNESCAPE) {
			@Pc(282) val extraWidth: Int = MiniMenu.minimenuHeader!!.renderAntiMacroText(MiniMenu.randomSeed, StaticMedia.chatScaleHeights, string, StaticMedia.chatIcons, InterfaceComponentRenderer.chatDisplayX + 16, InterfaceComponentRenderer.chatDisplayY + 4, MiniMenu.RANDOMIZER)
			InterfaceRectangles.inboundsComponentToTop(InterfaceComponentRenderer.chatDisplayY + 4, FontMetrics.defaultFontMetrics!!.getWidthNoSprites(string) + extraWidth, InterfaceComponentRenderer.chatDisplayX, 16)
		}
	}

	@OriginalMember(owner = "client!aca", name = "a", descriptor = "(I)I")
	@JvmStatic
	fun getMinimenuCursor(): Int {
		if (InterfaceDrag.component == null) {
			if (!MiniMenu.isOpen && MiniMenu.aClass2_Sub6_Sub3_1 != null) {
				return MiniMenu.aClass2_Sub6_Sub3_1!!.cursor
			}
			@Pc(23) val mouseX: Int = MouseRecord.mouseRecorder!!.getMouseX()
			@Pc(27) val mouseY: Int = MouseRecord.mouseRecorder!!.getMouseY()
			@Pc(57) var hoveredIndex: Int
			@Pc(59) var entryIndex: Int
			@Pc(71) var local71: Int
			@Pc(139) var miniMenuEntryInner: MiniMenuEntry?
			if (MiniMenu.isCollapsed) {
				@Pc(134) var queueIterator: QueueIterator
				if (MiniMenu.menuOpenX < mouseX && mouseX < MiniMenu.menuOpenWidth + MiniMenu.menuOpenX) {
					hoveredIndex = -1
					entryIndex = 0
					while (entryIndex < MiniMenu.coloredEntryCount) {
						if (FontMetrics.useBoldFont) {
							val entryY: Int = MiniMenu.menuOpenY + entryIndex * 16 + 33
							if (entryY - 13 < mouseY && mouseY <= entryY + 3) {
								hoveredIndex = entryIndex
							}
						} else {
							val entryY: Int = entryIndex * 16 + MiniMenu.menuOpenY + 31
							if (entryY - 13 < mouseY && entryY + 3 >= mouseY) {
								hoveredIndex = entryIndex
							}
						}
						entryIndex++
					}
					if (hoveredIndex != -1) {
						var currentIndex: Int = 0
						queueIterator = QueueIterator(MiniMenu.sortedColoredEntries)
						@Pc(239) var miniMenuEntry: ColoredMiniMenuEntry? = queueIterator.first() as ColoredMiniMenuEntry?
						while (miniMenuEntry != null) {
							if (hoveredIndex == currentIndex++) {
								return (miniMenuEntry.nodeQueue.sentinel.secondaryNext as MiniMenuEntry).cursor
							}
							miniMenuEntry = queueIterator.next() as ColoredMiniMenuEntry?
						}
					}
				} else if (MiniMenu.activeSubmenu != null && MiniMenu.activeSubmenuX < mouseX && mouseX < MiniMenu.activeSubmenuWidth + MiniMenu.activeSubmenuX) {
					hoveredIndex = -1
					entryIndex = 0
					while (entryIndex < MiniMenu.activeSubmenu!!.queueSize) {
						if (FontMetrics.useBoldFont) {
							val entryY: Int = MiniMenu.activeSubmenuY + entryIndex * 16 + 33
							if (entryY - 13 < mouseY && mouseY <= entryY + 3) {
								hoveredIndex = entryIndex
							}
						} else {
							val entryY: Int = entryIndex * 16 + MiniMenu.activeSubmenuY + 31
							if (mouseY > entryY - 13 && entryY + 3 >= mouseY) {
								hoveredIndex = entryIndex
							}
						}
						entryIndex++
					}
					if (hoveredIndex != -1) {
						var currentIndex: Int = 0
						queueIterator = QueueIterator(MiniMenu.activeSubmenu!!.nodeQueue)
						miniMenuEntryInner = queueIterator.first() as MiniMenuEntry?
						while (miniMenuEntryInner != null) {
							if (currentIndex++ == hoveredIndex) {
								return miniMenuEntryInner.cursor
							}
							miniMenuEntryInner = queueIterator.next() as MiniMenuEntry?
						}
					}
				}
			} else if (MiniMenu.menuOpenX < mouseX && mouseX < MiniMenu.menuOpenX + MiniMenu.menuOpenWidth) {
				hoveredIndex = -1
				entryIndex = 0
				while (entryIndex < MiniMenu.entryCount) {
					if (FontMetrics.useBoldFont) {
						val entryY: Int = (MiniMenu.entryCount - entryIndex - 1) * 16 + MiniMenu.menuOpenY + 33
						if (mouseY > entryY - 13 && mouseY <= entryY + 3) {
							hoveredIndex = entryIndex
						}
					} else {
						val entryY: Int = MiniMenu.menuOpenY + (-entryIndex + (MiniMenu.entryCount - 1)) * 16 + 31
						if (mouseY > entryY - 13 && entryY + 3 >= mouseY) {
							hoveredIndex = entryIndex
						}
					}
					entryIndex++
				}
				if (hoveredIndex != -1) {
					var currentIndex: Int = 0
					@Pc(357) val dequeIterator: DequeIterator = DequeIterator(MiniMenu.entryList)
					miniMenuEntryInner = dequeIterator.first() as MiniMenuEntry?
					while (miniMenuEntryInner != null) {
						if (hoveredIndex == currentIndex++) {
							return miniMenuEntryInner.cursor
						}
						miniMenuEntryInner = dequeIterator.next() as MiniMenuEntry?
					}
				}
			}
		}
		return -1
	}

	@OriginalMember(owner = "client!cj", name = "a", descriptor = "(BLclient!db;)Ljava/lang/String;")
	@JvmStatic
	fun getTextFrom(@OriginalArg(1) miniMenuEntryInner: MiniMenuEntry?): String? {
		if (miniMenuEntryInner!!.targetName == null || miniMenuEntryInner.targetName!!.length == 0) {
			return if (miniMenuEntryInner.op == null || miniMenuEntryInner.op!!.length <= 0) miniMenuEntryInner.opBase else miniMenuEntryInner.opBase + LocalizedText.aClass351_34.get(ClientState.language) + miniMenuEntryInner.op
		} else if (miniMenuEntryInner.op == null || miniMenuEntryInner.op!!.length <= 0) {
			return miniMenuEntryInner.opBase + LocalizedText.aClass351_34.get(ClientState.language) + miniMenuEntryInner.targetName
		} else {
			return miniMenuEntryInner.opBase + LocalizedText.aClass351_34.get(ClientState.language) + miniMenuEntryInner.op + LocalizedText.aClass351_34.get(ClientState.language) + miniMenuEntryInner.targetName
		}
	}

	@OriginalMember(owner = "client!vv", name = "a", descriptor = "(BZLclient!ke;)V")
	@JvmStatic
	fun playerHovered(@OriginalArg(1) onDifferentPlane: Boolean, @OriginalArg(2) playerEntity: PlayerEntity?) {
		if (MiniMenu.entryCount >= 400) {
			return
		}
		if (PlayerUpdate.localPlayer !== playerEntity) {
			@Pc(184) val string: String?
			@Pc(108) var local108: Int
			if (playerEntity!!.elo == 0) {
				@Pc(61) var combatColorDisplayed: Boolean = true
				if (PlayerUpdate.localPlayer!!.cbLevelRelated != -1 && playerEntity.cbLevelRelated != -1) {
					@Pc(85) val maxCombatLevel: Int = if (PlayerUpdate.localPlayer!!.combatLevel > playerEntity.combatLevel) PlayerUpdate.localPlayer!!.combatLevel else playerEntity.combatLevel
					@Pc(96) val minCbLevelRelated: Int = if (PlayerUpdate.localPlayer!!.cbLevelRelated >= playerEntity.cbLevelRelated) playerEntity.cbLevelRelated else PlayerUpdate.localPlayer!!.cbLevelRelated
					val maxLevelDifference: Int = maxCombatLevel * 10 / 100 + minCbLevelRelated + 5
					@Pc(115) var levelDifference: Int = PlayerUpdate.localPlayer!!.combatLevel - playerEntity.combatLevel
					if (levelDifference < 0) {
						levelDifference = -levelDifference
					}
					if (levelDifference > maxLevelDifference) {
						combatColorDisplayed = false
					}
				}
				@Pc(145) val string2: String? = if (Game.STELLAR_DAWN === Game.CURRENT_GAME) LocalizedText.aClass351_32.get(ClientState.language) else LocalizedText.aClass351_30.get(ClientState.language)
				if (playerEntity.cbLevelWithSumm <= playerEntity.combatLevel) {
					string = playerEntity.getUsernameWithTitle() + (if (combatColorDisplayed) MiniMenu.getCombatLevelColour(PlayerUpdate.localPlayer!!.combatLevel, playerEntity.combatLevel) else "<col=ffffff>") + " (" + string2 + playerEntity.combatLevel + ")"
				} else {
					string = playerEntity.getUsernameWithTitle() + (if (combatColorDisplayed) MiniMenu.getCombatLevelColour(PlayerUpdate.localPlayer!!.combatLevel, playerEntity.combatLevel) else "<col=ffffff>") + " (" + string2 + playerEntity.combatLevel + "+" + (playerEntity.cbLevelWithSumm - playerEntity.combatLevel) + ")"
				}
			} else if (playerEntity.elo == -1) {
				string = playerEntity.getUsernameWithTitle()
			} else {
				string = playerEntity.getUsernameWithTitle() + " (" + LocalizedText.aClass351_31.get(ClientState.language) + playerEntity.elo + ")"
			}
			if (MiniMenu.interfaceSelectionActive && !onDifferentPlane && (MiniMenu.useTargetMask and 0x8) != 0) {
				MiniMenu.addEntry(false, MiniMenu.useOnTarget + " -> <col=ffffff>" + string, 0, false, 0, -1, true, MiniMenuAction.TGT_PLAYER, playerEntity.playerIndex.toLong(), MiniMenu.useOnType, playerEntity.playerIndex.toLong(), MiniMenu.useOnCursor)
			}
			if (onDifferentPlane) {
				MiniMenu.addEntry(true, "", 0, false, 0, 0, false, MiniMenuAction.LABEL, playerEntity.playerIndex.toLong(), "<col=cccccc>" + string, 0L, -1)
			} else {
				@Pc(333) var i: Int = 7
				while (i >= 0) {
					if (MiniMenu.PLAYER_OPTIONS[i] != null) {
						@Pc(341) var clickTypeOffset: Short = 0
						if (Game.CURRENT_GAME === Game.RUNESCAPE && MiniMenu.PLAYER_OPTIONS[i]!!.equals(LocalizedText.aClass351_25.get(ClientState.language), ignoreCase = true)) {
							if (PlayerUpdate.localPlayer!!.combatLevel < playerEntity.combatLevel) {
								clickTypeOffset = 2000
							}
							if (PlayerUpdate.localPlayer!!.teamId != 0 && playerEntity.teamId != 0) {
								if (PlayerUpdate.localPlayer!!.teamId == playerEntity.teamId) {
									clickTypeOffset = 2000
								} else {
									clickTypeOffset = 0
								}
							}
						} else if (MiniMenu.PLAYER_OPTION_REDUCED_PRIORITY[i]) {
							clickTypeOffset = 2000
						}
						@Pc(396) val clickType: Short = (clickTypeOffset + MiniMenu.PLAYER_OPTION_CLICK_TYPES[i]).toShort()
						val cursor: Int = if (MiniMenu.PLAYER_OPTION_CURSORS[i] == -1) CursorManager.defaultAttackCursor else MiniMenu.PLAYER_OPTION_CURSORS[i]
						MiniMenu.addEntry(false, "<col=ffffff>" + string, 0, false, 0, -1, true, clickType.toInt(), playerEntity.playerIndex.toLong(), MiniMenu.PLAYER_OPTIONS[i], playerEntity.playerIndex.toLong(), cursor)
					}
					i--
				}
			}
			// Not part of the original client: the plugins' entries for the player (PluginRuntime).
			PluginRuntime.menuTarget(MenuTarget.PLAYER, playerEntity.x shr 9, playerEntity.z shr 9, playerEntity.level.toInt(), playerEntity.playerIndex, playerEntity.playerIndex.toLong())
			if (!onDifferentPlane) {
				@Pc(450) var miniMenuEntryInner: MiniMenuEntry? = MiniMenu.entryList.first() as MiniMenuEntry?
				while (miniMenuEntryInner != null) {
					if (miniMenuEntryInner.clickType == MiniMenuAction.WALK) {
						miniMenuEntryInner.targetName = "<col=ffffff>" + string
						return
					}
					miniMenuEntryInner = MiniMenu.entryList.next() as MiniMenuEntry?
				}
			}
		} else if (MiniMenu.interfaceSelectionActive && (MiniMenu.useTargetMask and 0x10) != 0) {
			MiniMenu.addEntry(false, MiniMenu.useOnTarget + " -> <col=ffffff>" + LocalizedText.aClass351_39.get(ClientState.language), 0, false, 0, -1, true, MiniMenuAction.TGT_SELF, playerEntity!!.playerIndex.toLong(), MiniMenu.useOnType, 0L, MiniMenu.useOnCursor)
		}
	}

	@OriginalMember(owner = "client!qt", name = "e", descriptor = "(I)V")
	@JvmStatic
	fun addCancelOption() {
		MiniMenu.cancelOption = MiniMenuEntry(LocalizedText.CANCEL.get(ClientState.language), "", Cursor.interfaceCursorId, MiniMenuAction.CANCEL, -1, 0L, 0, 0, true, false, 0L, true)
	}
	@OriginalMember(owner = "client!vga", name = "e", descriptor = "I")
	@JvmField
	var deferredClickType: Int = 0

	@OriginalMember(owner = "client!tba", name = "b", descriptor = "Lclient!vq;")
	@JvmField
	var activeSubmenu: ColoredMiniMenuEntry? = null

	@OriginalMember(owner = "client!lk", name = "z", descriptor = "I")
	@JvmField
	var lastChatMessageTick: Int = 0

	@OriginalMember(owner = "client!nk", name = "w", descriptor = "Z")
	@JvmField
	var isCollapsed: Boolean = false

	@OriginalMember(owner = "client!dj", name = "f", descriptor = "I")
	@JvmField
	var maxVisibleEntries: Int = -1

	@OriginalMember(owner = "client!jia", name = "M", descriptor = "Lclient!db;")
	@JvmField
	var cancelOption: MiniMenuEntry? = null

	@OriginalMember(owner = "client!oaa", name = "g", descriptor = "Lclient!db;")
	@JvmField
	var deferredEntry: MiniMenuEntry? = null

	@OriginalMember(owner = "client!uh", name = "d", descriptor = "Lclient!da;")
	@JvmField
	var minimenuHeader: AbstractFontRenderer? = null
	@OriginalMember(owner = "client!kk", name = "e", descriptor = "I")
	@JvmField
	var entryCount: Int = 0
	@OriginalMember(owner = "client!sn", name = "h", descriptor = "Lclient!mn;")
	@JvmField
	val sortedColoredEntries: Queue = Queue()

	@OriginalMember(owner = "client!db", name = "H", descriptor = "Lclient!eq;")
	@JvmField
	val coloredEntryMap: IterableHashTable = IterableHashTable(16)

	@OriginalMember(owner = "client!uea", name = "c", descriptor = "Lclient!jr;")
	@JvmField
	val coloredEntryCache: LRUCache = LRUCache(30)

	@OriginalMember(owner = "client!hl", name = "I", descriptor = "Lclient!uh;")
	@JvmField
	val entryList: Deque = Deque()
	@OriginalMember(owner = "client!gr", name = "h", descriptor = "I")
	@JvmField
	var coloredEntryCount: Int = 0

	@OriginalMember(owner = "client!jk", name = "e", descriptor = "(B)V")
	@JvmStatic
	fun closeSubmenu() {
		if (MiniMenu.activeSubmenu != null) {
			MiniMenu.activeSubmenu = null
			InterfaceRectangles.inboundsComponentToTop(MiniMenu.activeSubmenuX, MiniMenu.activeSubmenuWidth, MiniMenu.activeSubmenuY, MiniMenu.activeSubmenuHeight)
		}
	}

	@OriginalMember(owner = "client!k", name = "a", descriptor = "I")
	@JvmField
	var menuOpenWidth: Int = 0

	@OriginalMember(owner = "client!gq", name = "b", descriptor = "(B)Z")
	@JvmStatic
	fun hasEntries(): Boolean {
		return MiniMenu.entryCount > 0
	}

	@OriginalMember(owner = "client!ft", name = "g", descriptor = "(I)V")
	@JvmStatic
	fun reset() {
		@Pc(10) var miniMenuEntry: ColoredMiniMenuEntry? = MiniMenu.sortedColoredEntries.first() as ColoredMiniMenuEntry?
		while (miniMenuEntry != null) {
			if (miniMenuEntry.queueSize > 1) {
				miniMenuEntry.queueSize = 0
				MiniMenu.coloredEntryCache.put(miniMenuEntry, (miniMenuEntry.nodeQueue.sentinel.secondaryNext as MiniMenuEntry).bitpacked)
				miniMenuEntry.nodeQueue.clear()
			}
			miniMenuEntry = MiniMenu.sortedColoredEntries.next() as ColoredMiniMenuEntry?
		}
		MiniMenu.coloredEntryCount = 0
		MiniMenu.entryCount = 0
		MiniMenu.entryList.clear()
		MiniMenu.coloredEntryMap.clear()
		MiniMenu.sortedColoredEntries.clear()
		MiniMenu.isOpen = false
	}

	@OriginalMember(owner = "client!hc", name = "v", descriptor = "I")
	@JvmField
	var activeSubmenuX: Int = 0
	@OriginalMember(owner = "client!me", name = "a", descriptor = "(Lclient!vq;IZI)V")
	@JvmStatic
	fun openSubmenu(@OriginalArg(0) miniMenuEntry: ColoredMiniMenuEntry?, @OriginalArg(1) mouseY: Int, @OriginalArg(3) anchorY: Int) {
		if (!MiniMenu.isOpen) {
			return
		}
		@Pc(10) var width: Int = 0
		@Pc(22) var local22: Int
		@Pc(16) var miniMenuEntryInner: MiniMenuEntry? = miniMenuEntry!!.nodeQueue.first() as MiniMenuEntry?
		while (miniMenuEntryInner != null) {
			val entryWidth: Int = MiniMenu.getWidthForEntryWithQuests(miniMenuEntryInner)
			if (width < entryWidth) {
				width = entryWidth
			}
			miniMenuEntryInner = miniMenuEntry.nodeQueue.next() as MiniMenuEntry?
		}
		width += 8
		MiniMenu.activeSubmenuHeight = miniMenuEntry.queueSize * 16 + (if (FontMetrics.useBoldFont) 26 else 22)
		val submenuHeight: Int = miniMenuEntry.queueSize * 16 + 21
		@Pc(66) var x: Int = MiniMenu.menuOpenWidth + MiniMenu.menuOpenX
		if (width + x > GameShell.screenCanvasWidth) {
			x = MiniMenu.menuOpenX - width
		}
		if (x < 0) {
			x = 0
		}
		@Pc(89) val headerOffset: Int = if (FontMetrics.useBoldFont) 33 else 31
		@Pc(96) var y: Int = anchorY + 13 - headerOffset
		if (GameShell.screenCanvasHeight < y + submenuHeight) {
			y = GameShell.screenCanvasHeight - submenuHeight
		}
		MiniMenu.activeSubmenuX = x
		if (y < 0) {
			y = 0
		}
		MiniMenu.activeSubmenu = miniMenuEntry
		MiniMenu.activeSubmenuWidth = width
		MiniMenu.activeSubmenuY = y
	}

	@OriginalMember(owner = "client!tq", name = "a", descriptor = "(ILclient!db;)V")
	@JvmStatic
	fun removeEntry(@OriginalArg(1) miniMenuEntryInner: MiniMenuEntry?) {
		if (MiniMenu.isOpen) {
			return
		}
		miniMenuEntryInner!!.unlink()
		MiniMenu.entryCount--
		if (!miniMenuEntryInner.isUncategorized) {
			@Pc(82) val key: Long = miniMenuEntryInner.bitpacked
			@Pc(88) var miniMenuEntry: ColoredMiniMenuEntry?
			miniMenuEntry = MiniMenu.coloredEntryMap.get(key) as ColoredMiniMenuEntry?
			while (miniMenuEntry != null && !(miniMenuEntry.tagStart!! == miniMenuEntryInner.op)) {
				miniMenuEntry = MiniMenu.coloredEntryMap.nextWithKey() as ColoredMiniMenuEntry?
			}
			if (miniMenuEntry != null && miniMenuEntry.removeEntry(miniMenuEntryInner)) {
				MiniMenu.insertSortedEntry(miniMenuEntry)
			}
			return
		}
		@Pc(27) var miniMenuEntry2: ColoredMiniMenuEntry? = MiniMenu.sortedColoredEntries.first() as ColoredMiniMenuEntry?
		while (miniMenuEntry2 != null) {
			if (miniMenuEntry2.tagStart!! == miniMenuEntryInner.op) {
				@Pc(37) var found: Boolean = false
				@Pc(43) var miniMenuEntryInner2: MiniMenuEntry? = miniMenuEntry2.nodeQueue.first() as MiniMenuEntry?
				while (miniMenuEntryInner2 != null) {
					if (miniMenuEntryInner === miniMenuEntryInner2) {
						if (miniMenuEntry2.removeEntry(miniMenuEntryInner)) {
							MiniMenu.insertSortedEntry(miniMenuEntry2)
						}
						found = true
						break
					}
					miniMenuEntryInner2 = miniMenuEntry2.nodeQueue.next() as MiniMenuEntry?
				}
				if (found) {
					return
				}
			}
			miniMenuEntry2 = MiniMenu.sortedColoredEntries.next() as ColoredMiniMenuEntry?
		}
	}
	@OriginalMember(owner = "client!gt", name = "a", descriptor = "(B)Ljava/lang/String;")
	@JvmStatic
	fun getActionText(): String? {
		return if (MiniMenu.isOpen || MiniMenu.aClass2_Sub6_Sub3_1 == null) "" else MiniMenu.aClass2_Sub6_Sub3_1!!.opBase
	}

	@OriginalMember(owner = "client!bja", name = "b", descriptor = "(B)Ljava/lang/String;")
	@JvmStatic
	fun getColoredNameText(): String? {
		if (MiniMenu.isOpen || MiniMenu.aClass2_Sub6_Sub3_1 == null) {
			return ""
		} else if ((MiniMenu.aClass2_Sub6_Sub3_1!!.op == null || MiniMenu.aClass2_Sub6_Sub3_1!!.op!!.length == 0) && MiniMenu.aClass2_Sub6_Sub3_1!!.targetName != null && MiniMenu.aClass2_Sub6_Sub3_1!!.targetName!!.length > 0) {
			return MiniMenu.aClass2_Sub6_Sub3_1!!.targetName
		} else {
			return MiniMenu.aClass2_Sub6_Sub3_1!!.op
		}
	}
	@OriginalMember(owner = "client!tba", name = "a", descriptor = "(BLclient!vq;)V")
	@JvmStatic
	fun insertSortedEntry(@OriginalArg(1) miniMenuEntry: ColoredMiniMenuEntry?) {
		miniMenuEntry!!.unlinkSecondary()
		@Pc(18) var inserted: Boolean = false
		@Pc(23) var miniMenuEntry2: ColoredMiniMenuEntry? = MiniMenu.sortedColoredEntries.first() as ColoredMiniMenuEntry?
		while (miniMenuEntry2 != null) {
			if (MiniMenu.hasHigherPriority(miniMenuEntry2.getAction(), miniMenuEntry.getAction())) {
				CacheableNode.insertSecondaryBefore(miniMenuEntry2, miniMenuEntry)
				inserted = true
				break
			}
			miniMenuEntry2 = MiniMenu.sortedColoredEntries.next() as ColoredMiniMenuEntry?
		}
		if (!inserted) {
			MiniMenu.sortedColoredEntries.insert(miniMenuEntry)
		}
	}

	@OriginalMember(owner = "client!sk", name = "a", descriptor = "(BLclient!ha;)V")
	@JvmStatic
	fun renderOpenMenuSimple(@OriginalArg(1) toolkit: Toolkit?) {
		@Pc(7) var offsetX: Int = 0
		@Pc(9) var offsetY: Int = 0
		if (OrthoMode.orthoEnabled) {
			offsetX = OrthoMode.getOrthoOffsetX()
			offsetY = OrthoMode.getOrthoDrawOffsetY()
		}
		MiniMenu.drawSimpleMenuBox(MiniMenu.menuOpenWidth, MiniMenu.menuOpenY + offsetY, toolkit, MiniMenu.menuOpenX + offsetX, InterfaceComponentRenderer.menuHeight)
		MiniMenu.minimenuHeader!!.drawString(LocalizedText.CHOOSE_OPTION.get(ClientState.language), -10660793, offsetY + MiniMenu.menuOpenY + 14, offsetX + MiniMenu.menuOpenX + 3, -1)
		@Pc(64) val mouseX: Int = MouseRecord.mouseRecorder!!.getMouseX() + offsetX
		@Pc(70) val mouseY: Int = offsetY + MouseRecord.mouseRecorder!!.getMouseY()
		@Pc(74) var entryIndex: Int
		@Pc(91) var entryY: Int
		if (MiniMenu.isCollapsed) {
			entryIndex = 0
			@Pc(79) var miniMenuEntry: ColoredMiniMenuEntry? = MiniMenu.sortedColoredEntries.first() as ColoredMiniMenuEntry?
			while (miniMenuEntry != null) {
				entryY = offsetY + MiniMenu.menuOpenY + entryIndex * 16 + 31
				entryIndex++
				if (miniMenuEntry.queueSize == 1) {
					MiniMenu.drawMenuEntry(offsetY + MiniMenu.menuOpenY, -256, entryY, toolkit, miniMenuEntry.nodeQueue.sentinel.secondaryNext as MiniMenuEntry?, MiniMenu.menuOpenWidth, mouseY, -1, InterfaceComponentRenderer.menuHeight, MiniMenu.menuOpenX + offsetX, mouseX)
				} else {
					MiniMenu.drawColoredMenuEntry(-1, entryY, miniMenuEntry, mouseX, MiniMenu.menuOpenWidth, -256, mouseY, toolkit, MiniMenu.menuOpenY + offsetY, MiniMenu.menuOpenX - -offsetX, InterfaceComponentRenderer.menuHeight)
				}
				miniMenuEntry = MiniMenu.sortedColoredEntries.next() as ColoredMiniMenuEntry?
			}
			if (MiniMenu.activeSubmenu != null) {
				MiniMenu.drawSimpleMenuBox(MiniMenu.activeSubmenuWidth, MiniMenu.activeSubmenuY, toolkit, MiniMenu.activeSubmenuX, MiniMenu.activeSubmenuHeight)
				entryIndex = 0
				MiniMenu.minimenuHeader!!.drawString(MiniMenu.activeSubmenu!!.tagStart, -10660793, MiniMenu.activeSubmenuY + 14, MiniMenu.activeSubmenuX + 3, -1)
				@Pc(179) var miniMenuEntryInner: MiniMenuEntry? = MiniMenu.activeSubmenu!!.nodeQueue.first() as MiniMenuEntry?
				while (miniMenuEntryInner != null) {
					@Pc(189) val submenuEntryY: Int = MiniMenu.activeSubmenuY + entryIndex * 16 + 31
					entryIndex++
					MiniMenu.drawMenuEntry(MiniMenu.activeSubmenuY, -256, submenuEntryY, toolkit, miniMenuEntryInner, MiniMenu.activeSubmenuWidth, mouseY, -1, MiniMenu.activeSubmenuHeight, MiniMenu.activeSubmenuX, mouseX)
					miniMenuEntryInner = MiniMenu.activeSubmenu!!.nodeQueue.next() as MiniMenuEntry?
				}
				InterfaceRectangles.markRectanglesForFlip(MiniMenu.activeSubmenuWidth, MiniMenu.activeSubmenuY, MiniMenu.activeSubmenuX, MiniMenu.activeSubmenuHeight)
			}
		} else {
			entryIndex = 0
			@Pc(226) var miniMenuEntryInner2: MiniMenuEntry? = MiniMenu.entryList.first() as MiniMenuEntry?
			while (miniMenuEntryInner2 != null) {
				entryY = offsetY + MiniMenu.menuOpenY + (-entryIndex + -1 + MiniMenu.entryCount) * 16 + 31
				MiniMenu.drawMenuEntry(offsetY + MiniMenu.menuOpenY, -256, entryY, toolkit, miniMenuEntryInner2, MiniMenu.menuOpenWidth, mouseY, -1, InterfaceComponentRenderer.menuHeight, MiniMenu.menuOpenX + offsetX, mouseX)
				entryIndex++
				miniMenuEntryInner2 = MiniMenu.entryList.next() as MiniMenuEntry?
			}
		}
		InterfaceRectangles.markRectanglesForFlip(MiniMenu.menuOpenWidth, MiniMenu.menuOpenY + offsetY, MiniMenu.menuOpenX + offsetX, InterfaceComponentRenderer.menuHeight)
	}
	@OriginalMember(owner = "client!bg", name = "M", descriptor = "I")
	@JvmField
	var activeSubmenuWidth: Int = 0

	@OriginalMember(owner = "client!kaa", name = "x", descriptor = "Lclient!db;")
	@JvmField
	var aClass2_Sub6_Sub3_3: MiniMenuEntry? = null

	@OriginalMember(owner = "client!lr", name = "I", descriptor = "I")
	@JvmField
	var menuOpenY: Int = 0
	@OriginalMember(owner = "client!ui", name = "k", descriptor = "I")
	@JvmField
	var menuHighlightColour: Int = 0

	@OriginalMember(owner = "client!ui", name = "m", descriptor = "I")
	@JvmField
	var menuBackgroundColour: Int = 0

	@OriginalMember(owner = "client!pg", name = "j", descriptor = "Lclient!uh;")
	@JvmField
	val aClass341_46: Deque = Deque()
	@OriginalMember(owner = "client!lm", name = "x", descriptor = "I")
	@JvmField
	var menuHighlightTransparency: Int = 0

	@OriginalMember(owner = "client!kga", name = "g", descriptor = "[Z")
	@JvmField
	val PLAYER_OPTION_REDUCED_PRIORITY: BooleanArray = BooleanArray(8)

	@OriginalMember(owner = "client!qt", name = "e", descriptor = "[I")
	@JvmField
	val tooltipTextBounds: IntArray = IntArray(4)

	@OriginalMember(owner = "client!ce", name = "q", descriptor = "Z")
	@JvmField
	var isOpen: Boolean = false

	@OriginalMember(owner = "client!lj", name = "z", descriptor = "I")
	@JvmField
	var menuHoverTextColour: Int = 0

	@OriginalMember(owner = "client!vs", name = "a", descriptor = "(ILclient!vq;)I")
	@JvmStatic
	fun getPrefixTextWidth(@OriginalArg(1) miniMenuEntry: ColoredMiniMenuEntry?): Int {
		@Pc(14) val string: String = MiniMenu.formatTag(miniMenuEntry)
		return FontMetrics.defaultFontMetrics!!.getTextWidth(string, StaticMedia.chatIcons)
	}

	@OriginalMember(owner = "client!pu", name = "g", descriptor = "I")
	@JvmField
	var activeSubmenuHeight: Int = 0

	@OriginalMember(owner = "client!g", name = "b", descriptor = "I")
	@JvmField
	var activeSubmenuY: Int = 0

	@OriginalMember(owner = "client!nb", name = "b", descriptor = "Z")
	@JvmField
	var shiftClickEnabled: Boolean = false
	@OriginalMember(owner = "client!cj", name = "f", descriptor = "Lclient!db;")
	@JvmField
	var aClass2_Sub6_Sub3_1: MiniMenuEntry? = null

	@OriginalMember(owner = "client!r", name = "C", descriptor = "Z")
	@JvmField
	var interfaceSelectionActive: Boolean = false

	@OriginalMember(owner = "client!va", name = "b", descriptor = "(I)V")
	@JvmStatic
	fun resetMiniMenuSprites() {
		StaticMedia.menuLeftSideBorderSprite = null
		StaticMedia.menuBottomLeftCornerSprite = null
		StaticMedia.menuBottomBorderSprite = null
		StaticMedia.chatIcons = null
		StaticMedia.menuHeaderLeftCornerSprite = null
		MiniMenu.menuHeaderRightCornerSprite = null
		MiniMenu.menuHeaderSprite = null
		StaticMedia.menuRightSideBorderSprite = null
		StaticMedia.menuBottomRightCornerSprite = null
	}

	@OriginalMember(owner = "client!cf", name = "a", descriptor = "(BI)Z")
	@JvmStatic
	fun isUseOnClickType(@OriginalArg(1) clickType: Int): Boolean {
		return clickType == 15 || clickType == 2 || clickType == 30 || clickType == 49 || clickType == 51 || clickType == 50 || clickType == 6
	}

	@OriginalMember(owner = "client!gh", name = "Pb", descriptor = "I")
	@JvmField
	var useOnCursor: Int = 0

	@OriginalMember(owner = "client!jt", name = "v", descriptor = "I")
	@JvmField
	var menuOpenX: Int = 0

	@OriginalMember(owner = "client!sk", name = "g", descriptor = "I")
	@JvmField
	var menuHeaderSpriteId: Int = 0

	@OriginalMember(owner = "client!ug", name = "D", descriptor = "I")
	@JvmField
	var menuTextColour: Int = 0

	@OriginalMember(owner = "client!ti", name = "d", descriptor = "(II)Z")
	@JvmStatic
	fun isComponentClickType(@OriginalArg(1) clickType: Int): Boolean {
		return clickType == MiniMenuAction.IF_BUTTONX1 || clickType == MiniMenuAction.TGT_BUTTON || clickType == MiniMenuAction.IF_BUTTONX2 || clickType == MiniMenuAction.IF_BUTTONT || clickType == MiniMenuAction.PAUSE_BUTTON
	}

	@OriginalMember(owner = "client!di", name = "a", descriptor = "(B)V")
	@JvmStatic
	fun close() {
		MiniMenu.closeSubmenu()
		MiniMenu.isOpen = false
		InterfaceRectangles.inboundsComponentToTop(MiniMenu.menuOpenX, MiniMenu.menuOpenWidth, MiniMenu.menuOpenY, InterfaceComponentRenderer.menuHeight)
	}

	@OriginalMember(owner = "client!ua", name = "Zb", descriptor = "[S")
	@JvmField
	val PLAYER_OPTION_CLICK_TYPES: ShortArray = shortArrayOf(MiniMenuAction.OPPLAYER1.toShort(), MiniMenuAction.OPPLAYER2.toShort(), MiniMenuAction.OPPLAYER3.toShort(), MiniMenuAction.OPPLAYER4.toShort(), MiniMenuAction.OPPLAYER5.toShort(), MiniMenuAction.OPPLAYER6.toShort(), MiniMenuAction.OPPLAYER7.toShort(), MiniMenuAction.OPPLAYER8.toShort())

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(BLclient!db;)V")
	@JvmStatic
	fun addEntry(@OriginalArg(1) miniMenuEntryInner: MiniMenuEntry?) {
		if (miniMenuEntryInner == null) {
			return
		}
		MiniMenu.entryList.addLast(miniMenuEntryInner)
		MiniMenu.entryCount++
		@Pc(37) var miniMenuEntry: ColoredMiniMenuEntry?
		if (miniMenuEntryInner.isUncategorized || "" == miniMenuEntryInner.op) {
			miniMenuEntry = ColoredMiniMenuEntry(miniMenuEntryInner.op)
			MiniMenu.coloredEntryCount++
		} else {
			@Pc(45) val key: Long = miniMenuEntryInner.bitpacked
			miniMenuEntry = MiniMenu.coloredEntryMap.get(key) as ColoredMiniMenuEntry?
			while (miniMenuEntry != null && !(miniMenuEntry.tagStart!! == miniMenuEntryInner.op)) {
				miniMenuEntry = MiniMenu.coloredEntryMap.nextWithKey() as ColoredMiniMenuEntry?
			}
			if (miniMenuEntry == null) {
				miniMenuEntry = MiniMenu.coloredEntryCache.get(key) as ColoredMiniMenuEntry?
				if (miniMenuEntry != null && !(miniMenuEntry.tagStart!! == miniMenuEntryInner.op)) {
					miniMenuEntry = null
				}
				if (miniMenuEntry == null) {
					miniMenuEntry = ColoredMiniMenuEntry(miniMenuEntryInner.op)
				}
				MiniMenu.coloredEntryMap.put(key, miniMenuEntry)
				MiniMenu.coloredEntryCount++
			}
		}
		if (miniMenuEntry.addEntry(miniMenuEntryInner)) {
			MiniMenu.insertSortedEntry(miniMenuEntry)
		}
	}

	@OriginalMember(owner = "client!tw", name = "a", descriptor = "(IZI)Ljava/lang/String;")
	@JvmStatic
	fun getCombatLevelColour(@OriginalArg(0) myCombatLevel: Int, @OriginalArg(2) targetCombatLevel: Int): String {
		@Pc(13) val diff: Int = myCombatLevel - targetCombatLevel
		if (diff < -9) {
			return "<col=ff0000>"
		} else if (diff < -6) {
			return "<col=ff3000>"
		} else if (diff < -3) {
			return "<col=ff7000>"
		} else if (diff < 0) {
			return "<col=ffb000>"
		} else if (diff > 9) {
			return "<col=00ff00>"
		} else if (diff > 6) {
			return "<col=40ff00>"
		} else if (diff > 3) {
			return "<col=80ff00>"
		} else if (diff > 0) {
			return "<col=c0ff00>"
		} else {
			return "<col=ffff00>"
		}
	}

	@OriginalMember(owner = "client!rba", name = "a", descriptor = "(IILclient!ha;IIZII)V")
	@JvmStatic
	fun drawSimpleMenuBox(@OriginalArg(0) width: Int, @OriginalArg(1) y: Int, @OriginalArg(2) toolkit: Toolkit?, @OriginalArg(3) x: Int, @OriginalArg(6) height: Int) {
		toolkit!!.fillRect(width, x, y, height, -10660793)
		toolkit.fillRect(width - 2, x + 1, y + 1, 16, -16777216)
		toolkit.drawOpaqueRectangle(width - 2, y + 18, -16777216, x + 1, height + -19)
	}

	@OriginalMember(owner = "client!tk", name = "f", descriptor = "Lclient!mi;")
	@JvmField
	var menuHeaderSprite: NativeSprite? = null

	@OriginalMember(owner = "client!jt", name = "a", descriptor = "(Lclient!ha;I)V")
	@JvmStatic
	fun renderOpenMenuStyled(@OriginalArg(0) toolkit: Toolkit?) {
		@Pc(7) var offsetX: Int = 0
		@Pc(9) var offsetY: Int = 0
		if (OrthoMode.orthoEnabled) {
			offsetX = OrthoMode.getOrthoOffsetX()
			offsetY = OrthoMode.getOrthoDrawOffsetY()
		}
		@Pc(21) val menuX: Int = offsetX + menuOpenX
		@Pc(25) val menuY: Int = MiniMenu.menuOpenY + offsetY
		@Pc(27) val menuWidth: Int = MiniMenu.menuOpenWidth
		@Pc(31) val menuHeight: Int = InterfaceComponentRenderer.menuHeight - 3
		MiniMenu.drawStyledMenuHeader(MiniMenu.menuOpenWidth, offsetX + menuOpenX, toolkit, LocalizedText.CHOOSE_OPTION.get(ClientState.language), offsetY + MiniMenu.menuOpenY, InterfaceComponentRenderer.menuHeight)
		@Pc(57) val mouseX: Int = offsetX + MouseRecord.mouseRecorder!!.getMouseX()
		@Pc(68) val mouseY: Int = offsetY + MouseRecord.mouseRecorder!!.getMouseY()
		@Pc(72) var entryIndex: Int
		@Pc(77) var miniMenuEntryInner: MiniMenuEntry?
		@Pc(93) var entryY: Int
		@Pc(283) var submenuEntryY: Int
		@Pc(164) var miniMenuEntry: ColoredMiniMenuEntry?
		@Pc(271) var miniMenuEntryInner2: MiniMenuEntry?
		if (MiniMenu.isCollapsed) {
			entryIndex = 0
			miniMenuEntry = MiniMenu.sortedColoredEntries.first() as ColoredMiniMenuEntry?
			while (miniMenuEntry != null) {
				entryY = entryIndex * 16 + menuY + 33
				if (mouseX > offsetX + menuOpenX && mouseX < MiniMenu.menuOpenWidth + offsetX + menuOpenX && entryY - 13 < mouseY && mouseY < entryY + 4 && (miniMenuEntry.queueSize > 1 || (miniMenuEntry.nodeQueue.sentinel.secondaryNext as MiniMenuEntry).showOtherLevelIcon)) {
					toolkit!!.aa(menuOpenX + offsetX, entryY + -12, MiniMenu.menuOpenWidth, 16, ((255 - MiniMenu.menuHighlightTransparency) shl 24) or MiniMenu.menuHighlightColour, 1)
				}
				entryIndex++
				miniMenuEntry = MiniMenu.sortedColoredEntries.next() as ColoredMiniMenuEntry?
			}
			if (MiniMenu.activeSubmenu != null) {
				entryIndex = 0
				MiniMenu.drawStyledMenuHeader(MiniMenu.activeSubmenuWidth, MiniMenu.activeSubmenuX, toolkit, MiniMenu.activeSubmenu!!.tagStart, MiniMenu.activeSubmenuY, MiniMenu.activeSubmenuHeight)
				miniMenuEntryInner2 = MiniMenu.activeSubmenu!!.nodeQueue.first() as MiniMenuEntry?
				while (miniMenuEntryInner2 != null) {
					submenuEntryY = entryIndex * 16 + MiniMenu.activeSubmenuY + 20 + 13
					if (MiniMenu.activeSubmenuX < mouseX && MiniMenu.activeSubmenuX + MiniMenu.activeSubmenuWidth > mouseX && mouseY > submenuEntryY - 13 && submenuEntryY + 4 > mouseY && miniMenuEntryInner2.showOtherLevelIcon) {
						toolkit!!.aa(MiniMenu.activeSubmenuX, submenuEntryY - 12, MiniMenu.activeSubmenuWidth, 16, ((255 - MiniMenu.menuHighlightTransparency) shl 24) or MiniMenu.menuHighlightColour, 1)
					}
					entryIndex++
					miniMenuEntryInner2 = MiniMenu.activeSubmenu!!.nodeQueue.next() as MiniMenuEntry?
				}
				MiniMenu.drawStyledMenuBorder(MiniMenu.activeSubmenuX, MiniMenu.activeSubmenuWidth, MiniMenu.activeSubmenuHeight, MiniMenu.activeSubmenuY, toolkit)
			}
		} else {
			entryIndex = 0
			miniMenuEntryInner = MiniMenu.entryList.first() as MiniMenuEntry?
			while (miniMenuEntryInner != null) {
				entryY = (MiniMenu.entryCount - entryIndex - 1) * 16 + menuY + 13 + 20
				if (menuOpenX + offsetX < mouseX && menuOpenX + offsetX + MiniMenu.menuOpenWidth > mouseX && entryY - 13 < mouseY && entryY + 4 > mouseY && miniMenuEntryInner.showOtherLevelIcon) {
					toolkit!!.aa(offsetX + menuOpenX, entryY + -12, MiniMenu.menuOpenWidth, 16, ((255 - MiniMenu.menuHighlightTransparency) shl 24) or MiniMenu.menuHighlightColour, 1)
				}
				entryIndex++
				miniMenuEntryInner = MiniMenu.entryList.next() as MiniMenuEntry?
			}
		}
		MiniMenu.drawStyledMenuBorder(menuOpenX + offsetX, MiniMenu.menuOpenWidth, InterfaceComponentRenderer.menuHeight, MiniMenu.menuOpenY + offsetY, toolkit)
		if (MiniMenu.isCollapsed) {
			entryIndex = 0
			miniMenuEntry = MiniMenu.sortedColoredEntries.first() as ColoredMiniMenuEntry?
			while (miniMenuEntry != null) {
				entryY = offsetY + MiniMenu.menuOpenY + entryIndex * 16 + 13 + 20
				entryIndex++
				if (miniMenuEntry.queueSize == 1) {
					MiniMenu.drawMenuEntry(MiniMenu.menuOpenY + offsetY, MiniMenu.menuHoverTextColour or 0xFF000000.toInt(), entryY, toolkit, miniMenuEntry.nodeQueue.sentinel.secondaryNext as MiniMenuEntry?, MiniMenu.menuOpenWidth, mouseY, MiniMenu.menuTextColour or 0xFF000000.toInt(), InterfaceComponentRenderer.menuHeight, menuOpenX + offsetX, mouseX)
				} else {
					MiniMenu.drawColoredMenuEntry(MiniMenu.menuTextColour or 0xFF000000.toInt(), entryY, miniMenuEntry, mouseX, MiniMenu.menuOpenWidth, MiniMenu.menuHoverTextColour or 0xFF000000.toInt(), mouseY, toolkit, MiniMenu.menuOpenY + offsetY, offsetX + menuOpenX, InterfaceComponentRenderer.menuHeight)
				}
				miniMenuEntry = MiniMenu.sortedColoredEntries.next() as ColoredMiniMenuEntry?
			}
			if (MiniMenu.activeSubmenu != null) {
				entryIndex = 0
				miniMenuEntryInner2 = MiniMenu.activeSubmenu!!.nodeQueue.first() as MiniMenuEntry?
				while (miniMenuEntryInner2 != null) {
					submenuEntryY = entryIndex * 16 + MiniMenu.activeSubmenuY + 13 + 20
					entryIndex++
					MiniMenu.drawMenuEntry(MiniMenu.activeSubmenuY, MiniMenu.menuHoverTextColour or 0xFF000000.toInt(), submenuEntryY, toolkit, miniMenuEntryInner2, MiniMenu.activeSubmenuWidth, mouseY, MiniMenu.menuTextColour or 0xFF000000.toInt(), MiniMenu.activeSubmenuHeight, MiniMenu.activeSubmenuX, mouseX)
					miniMenuEntryInner2 = MiniMenu.activeSubmenu!!.nodeQueue.next() as MiniMenuEntry?
				}
				InterfaceRectangles.markRectanglesForFlip(MiniMenu.activeSubmenuWidth, MiniMenu.activeSubmenuY, MiniMenu.activeSubmenuX, MiniMenu.activeSubmenuHeight)
			}
		} else {
			entryIndex = 0
			miniMenuEntryInner = MiniMenu.entryList.first() as MiniMenuEntry?
			while (miniMenuEntryInner != null) {
				entryY = menuY + (MiniMenu.entryCount + -1 + -entryIndex) * 16 + 20 + 13
				entryIndex++
				MiniMenu.drawMenuEntry(menuY, MiniMenu.menuHoverTextColour or 0xFF000000.toInt(), entryY, toolkit, miniMenuEntryInner, menuWidth, mouseY, MiniMenu.menuTextColour or 0xFF000000.toInt(), menuHeight, menuX, mouseX)
				miniMenuEntryInner = MiniMenu.entryList.next() as MiniMenuEntry?
			}
		}
		InterfaceRectangles.markRectanglesForFlip(MiniMenu.menuOpenWidth, MiniMenu.menuOpenY + offsetY, offsetX + menuOpenX, InterfaceComponentRenderer.menuHeight)
	}

	@OriginalMember(owner = "client!uv", name = "l", descriptor = "I")
	@JvmField
	var examineCursor: Int = -1

	@OriginalMember(owner = "client!lca", name = "a", descriptor = "(IIIIIILclient!ha;)V")
	@JvmStatic
	fun drawStyledMenuBorder(@OriginalArg(1) x: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(5) y: Int, @OriginalArg(6) toolkit: Toolkit?) {
		if ((StaticMedia.menuBottomBorderSprite == null || StaticMedia.menuLeftSideBorderSprite == null || StaticMedia.menuBottomLeftCornerSprite == null) && Resource.SPRITES!!.isFileReady(Find.menuBottomBorderSpriteId) && Resource.SPRITES!!.isFileReady(StaticMedia.menuSideBorderSpriteId) && Resource.SPRITES!!.isFileReady(ScriptRunner.menuCornerSpriteId)) {
			@Pc(27) val indexedImage: SpriteType? = SpriteDataProvider.listSingle(Resource.SPRITES, StaticMedia.menuSideBorderSpriteId, 0)
			StaticMedia.menuLeftSideBorderSprite = toolkit!!.generateSprite(indexedImage, true)
			indexedImage!!.flipHorizontal()
			StaticMedia.menuRightSideBorderSprite = toolkit.generateSprite(indexedImage, true)
			StaticMedia.menuBottomBorderSprite = toolkit.generateSprite(SpriteDataProvider.listSingle(Resource.SPRITES, Find.menuBottomBorderSpriteId, 0), true)
			@Pc(52) val indexedImage2: SpriteType? = SpriteDataProvider.listSingle(Resource.SPRITES, ScriptRunner.menuCornerSpriteId, 0)
			StaticMedia.menuBottomLeftCornerSprite = toolkit.generateSprite(indexedImage2, true)
			indexedImage2!!.flipHorizontal()
			StaticMedia.menuBottomRightCornerSprite = toolkit.generateSprite(indexedImage2, true)
		}
		if (StaticMedia.menuBottomBorderSprite == null || StaticMedia.menuLeftSideBorderSprite == null || StaticMedia.menuBottomLeftCornerSprite == null) {
			return
		}
		@Pc(85) val bottomTileCount: Int = (width - StaticMedia.menuBottomLeftCornerSprite!!.getWidth() * 2) / StaticMedia.menuBottomBorderSprite!!.getWidth()
		@Pc(87) var i: Int = 0
		while (i < bottomTileCount) {
			StaticMedia.menuBottomBorderSprite!!.render(x + StaticMedia.menuBottomLeftCornerSprite!!.getWidth() + i * StaticMedia.menuBottomBorderSprite!!.getWidth(), -StaticMedia.menuBottomBorderSprite!!.getHeight() + y + height)
			i++
		}
		@Pc(122) val sideTileCount: Int = (height - StaticMedia.menuBottomLeftCornerSprite!!.getHeight() - 20) / StaticMedia.menuLeftSideBorderSprite!!.getHeight()
		@Pc(124) var j: Int = 0
		while (j < sideTileCount) {
			StaticMedia.menuLeftSideBorderSprite!!.render(x, StaticMedia.menuLeftSideBorderSprite!!.getHeight() * j + y + 20)
			StaticMedia.menuRightSideBorderSprite!!.render(width + x - StaticMedia.menuRightSideBorderSprite!!.getWidth(), StaticMedia.menuLeftSideBorderSprite!!.getHeight() * j + 20 + y)
			j++
		}
		StaticMedia.menuBottomLeftCornerSprite!!.render(x, y + height - StaticMedia.menuBottomLeftCornerSprite!!.getHeight())
		StaticMedia.menuBottomRightCornerSprite!!.render(x + width - StaticMedia.menuBottomLeftCornerSprite!!.getWidth(), y - (-height + StaticMedia.menuBottomLeftCornerSprite!!.getHeight()))
	}

	@OriginalMember(owner = "client!vu", name = "d", descriptor = "(II)Z")
	@JvmStatic
	fun isLocClickType(@OriginalArg(1) clickType: Int): Boolean {
		if (clickType == MiniMenuAction.OPLOC1 || clickType == MiniMenuAction.OPLOC2 || clickType == MiniMenuAction.OPLOC3 || clickType == MiniMenuAction.OPLOC4 || clickType == MiniMenuAction.OPLOC5) {
			return true
		} else {
			return clickType == MiniMenuAction.TGT_LOC || clickType == MiniMenuAction.OPLOC6
		}
	}

	@OriginalMember(owner = "client!fba", name = "x", descriptor = "Lclient!mi;")
	@JvmField
	var menuHeaderRightCornerSprite: NativeSprite? = null
	@OriginalMember(owner = "client!oga", name = "a", descriptor = "(II)Z")
	@JvmStatic
	fun isObjClickType(@OriginalArg(1) clickType: Int): Boolean {
		if (clickType == MiniMenuAction.OPOBJ1 || clickType == MiniMenuAction.OPOBJ2 || clickType == MiniMenuAction.OPOBJ3 || clickType == MiniMenuAction.OPOBJ4 || clickType == MiniMenuAction.OPOBJ5) {
			return true
		} else {
			return clickType == MiniMenuAction.TGT_OBJ || clickType == MiniMenuAction.OPOBJ6
		}
	}

	@OriginalMember(owner = "client!pda", name = "R", descriptor = "I")
	@JvmField
	var menuHeaderCornerSpriteId: Int = 0

	@OriginalMember(owner = "client!kl", name = "a", descriptor = "(IB)Z")
	@JvmStatic
	fun isNpcClickType(@OriginalArg(0) clickType: Int): Boolean {
		if (clickType == MiniMenuAction.OPNPC1 || clickType == MiniMenuAction.OPNPC2 || clickType == MiniMenuAction.OPNPC3 || clickType == MiniMenuAction.OPNPC4 || clickType == MiniMenuAction.OPNPC5) {
			return true
		} else {
			return clickType == MiniMenuAction.TGT_NPC || clickType == MiniMenuAction.OPNPC6
		}
	}

	@OriginalMember(owner = "client!il", name = "a", descriptor = "(ILclient!vq;)Ljava/lang/String;")
	@JvmStatic
	fun formatTag(@OriginalArg(1) miniMenuEntry: ColoredMiniMenuEntry?): String {
		return miniMenuEntry!!.tagStart + " <col=ffffff>>"
	}

	@OriginalMember(owner = "client!fi", name = "S", descriptor = "Lclient!uh;")
	@JvmField
	val aClass341_36: Deque = Deque()

	@OriginalMember(owner = "client!hj", name = "c", descriptor = "Lclient!at;")
	@JvmField
	var component: Component? = null

	@OriginalMember(owner = "client!hu", name = "f", descriptor = "Ljava/lang/String;")
	@JvmField
	var useOnType: String? = null

	@OriginalMember(owner = "client!kda", name = "a", descriptor = "(IILclient!vq;IIIILclient!ha;IIII)V")
	@JvmStatic
	fun drawColoredMenuEntry(@OriginalArg(0) color: Int, @OriginalArg(1) y: Int, @OriginalArg(2) miniMenuEntry: ColoredMiniMenuEntry?, @OriginalArg(3) mouseX: Int, @OriginalArg(4) width: Int, @OriginalArg(5) hoverColor: Int, @OriginalArg(6) mouseY: Int, @OriginalArg(7) toolkit: Toolkit?, @OriginalArg(8) menuY: Int, @OriginalArg(10) x: Int, @OriginalArg(11) menuHeight: Int) {
		var color: Int = color
		if (x < mouseX && mouseX < width + x && y - 13 < mouseY && mouseY < y + 3) {
			color = hoverColor
		}
		@Pc(36) val string: String = MiniMenu.formatTag(miniMenuEntry)
		MiniMenu.minimenuHeader!!.drawStringWithSprites(y, string, color, x + 3, StaticMedia.chatIcons, StaticMedia.chatScaleHeights)
	}

	@OriginalMember(owner = "client!jo", name = "a", descriptor = "(BLclient!ha;)V")
	@JvmStatic
	fun render(@OriginalArg(1) toolkit: Toolkit?) {
		if (MiniMenu.isOpen) {
			MiniMenu.renderOpenMenu(toolkit)
		} else {
			MiniMenu.renderTooltipText(toolkit)
		}
	}

	@OriginalMember(owner = "client!iv", name = "Fb", descriptor = "I")
	@JvmField
	var menuBackgroundTransparency: Int = 0

	@OriginalMember(owner = "client!kv", name = "a", descriptor = "(IILclient!ha;Ljava/lang/String;ZIII)V")
	@JvmStatic
	fun drawStyledMenuHeader(@OriginalArg(0) width: Int, @OriginalArg(1) x: Int, @OriginalArg(2) toolkit: Toolkit?, @OriginalArg(3) string: String?, @OriginalArg(5) y: Int, @OriginalArg(6) height: Int) {
		if (MiniMenu.menuHeaderSprite == null || StaticMedia.menuHeaderLeftCornerSprite == null) {
			if (Resource.SPRITES!!.isFileReady(MiniMenu.menuHeaderSpriteId) && Resource.SPRITES!!.isFileReady(MiniMenu.menuHeaderCornerSpriteId)) {
				MiniMenu.menuHeaderSprite = toolkit!!.generateSprite(SpriteDataProvider.listSingle(Resource.SPRITES, MiniMenu.menuHeaderSpriteId, 0), true)
				@Pc(28) val indexedImage: SpriteType? = SpriteDataProvider.listSingle(Resource.SPRITES, MiniMenu.menuHeaderCornerSpriteId, 0)
				StaticMedia.menuHeaderLeftCornerSprite = toolkit.generateSprite(indexedImage, true)
				indexedImage!!.flipHorizontal()
				MiniMenu.menuHeaderRightCornerSprite = toolkit.generateSprite(indexedImage, true)
			} else {
				toolkit!!.aa(x, y, width, 20, ((255 - MiniMenu.menuBackgroundTransparency) shl 24) or MiniMenu.menuBackgroundColour, 1)
			}
		}
		if (MiniMenu.menuHeaderSprite != null && StaticMedia.menuHeaderLeftCornerSprite != null) {
			@Pc(75) val tileCount: Int = (width - StaticMedia.menuHeaderLeftCornerSprite!!.getWidth() * 2) / MiniMenu.menuHeaderSprite!!.getWidth()
			@Pc(77) var i: Int = 0
			while (i < tileCount) {
				MiniMenu.menuHeaderSprite!!.render(StaticMedia.menuHeaderLeftCornerSprite!!.getWidth() + x + MiniMenu.menuHeaderSprite!!.getWidth() * i, y)
				i++
			}
			StaticMedia.menuHeaderLeftCornerSprite!!.render(x, y)
			MiniMenu.menuHeaderRightCornerSprite!!.render(x + width - MiniMenu.menuHeaderRightCornerSprite!!.getWidth(), y)
		}
		MiniMenu.minimenuHeader!!.drawString(string, MiniMenu.menuTextColour or 0xFF000000.toInt(), y + 14, x + 3, -1)
		toolkit!!.aa(x, y + 20, width, height - 20, MiniMenu.menuBackgroundColour or ((-MiniMenu.menuBackgroundTransparency + 255) shl 24), 1)
	}

	@OriginalMember(owner = "client!hu", name = "e", descriptor = "Ljava/lang/String;")
	@JvmField
	var useOnTarget: String? = null

	@OriginalMember(owner = "client!gw", name = "e", descriptor = "(B)V")
	@JvmStatic
	fun removeComponentEntries() {
		@Pc(14) var miniMenuEntryInner: MiniMenuEntry? = MiniMenu.entryList.first() as MiniMenuEntry?
		while (miniMenuEntryInner != null) {
			if (MiniMenu.isComponentClickType(miniMenuEntryInner.clickType)) {
				MiniMenu.removeEntry(miniMenuEntryInner)
			}
			miniMenuEntryInner = MiniMenu.entryList.next() as MiniMenuEntry?
		}
	}

	@OriginalMember(owner = "client!mq", name = "ab", descriptor = "[Ljava/lang/String;")
	@JvmField
	val PLAYER_OPTIONS: Array<String?> = arrayOfNulls<String>(8)

	@OriginalMember(owner = "client!kba", name = "y", descriptor = "Ljava/lang/String;")
	@JvmField
	var walkHereText: String? = null
	@OriginalMember(owner = "client!un", name = "E", descriptor = "I")
	@JvmField
	var menuOpenMode: Int = 0

	@OriginalMember(owner = "client!ca", name = "d", descriptor = "I")
	@JvmField
	var targetParamId: Int = 0

	@OriginalMember(owner = "client!vg", name = "a", descriptor = "[I")
	@JvmField
	val PLAYER_OPTION_CURSORS: IntArray = IntArray(8)

	@OriginalMember(owner = "client!eq", name = "j", descriptor = "I")
	@JvmField
	var randomSeed: Int = 0

	@OriginalMember(owner = "client!sc", name = "b", descriptor = "Ljava/util/Random;")
	@JvmField
	val RANDOMIZER: Random = Random()

	@OriginalMember(owner = "client!gs", name = "c", descriptor = "I")
	@JvmField
	var slotId: Int = -1

	@OriginalMember(owner = "client!nga", name = "a", descriptor = "Z")
	@JvmField
	var showFaceHere: Boolean = false

	@OriginalMember(owner = "client!wp", name = "a", descriptor = "(ZLclient!ha;)V")
	@JvmStatic
	fun renderOpenMenu(@OriginalArg(1) toolkit: Toolkit?) {
		if (FontMetrics.useBoldFont) {
			MiniMenu.renderOpenMenuStyled(toolkit)
		} else {
			MiniMenu.renderOpenMenuSimple(toolkit)
		}
	}

	@OriginalMember(owner = "client!cda", name = "e", descriptor = "I")
	@JvmField
	var selectedItemId: Int = -1
	@OriginalMember(owner = "client!iea", name = "d", descriptor = "I")
	@JvmField
	var useTargetMask: Int = 0
}

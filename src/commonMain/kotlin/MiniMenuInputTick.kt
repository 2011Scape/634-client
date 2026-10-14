import com.jagex.game.runetek5.core.constants.MiniMenuAction
import com.jagex.game.runetek5.client.menu.MiniMenu
import com.jagex.game.runetek5.client.menu.MiniMenuEntry
import com.jagex.game.runetek5.Preferences
import com.jagex.game.runetek5.DraggedComponentLayer
import com.jagex.game.runetek5.core.datastruct.key.DequeIterator
import com.jagex.game.runetek5.cache.node.QueueIterator
import com.jagex.game.runetek5.client.menu.ColoredMiniMenuEntry
import com.jagex.game.runetek5.client.input.mouse.MouseRecord
import com.jagex.game.runetek5.graphics.gl.GlxMatrixScratch
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.event.keyboard.SimpleKeyboardMonitorConstants
import com.jagex.game.runetek5.client.event.mouse.MouseLog
import com.jagex.game.runetek5.client.features.ClientFeatures
import com.jagex.game.runetek5.client.features.HdOptionsButton
import com.jagex.game.runetek5.client.features.HdSettingsPanel
import com.jagex.game.runetek5.graphics.font.FontMetrics
import com.jagex.game.runetek5.graphics.ifcomponent.InterfaceComponentRenderer
import com.jagex.game.runetek5.client.input.keyboard.KeyRecorder
import com.jagex.game.runetek5.client.GameShell
import com.jagex.game.runetek5.InterfaceDrag
import com.jagex.game.runetek5.client.input.mouse.MouseManager
import com.jagex.game.runetek5.client.ClientState
import kotlin.jvm.JvmStatic

object MiniMenuInputTick {

	@OriginalMember(owner = "client!uga", name = "c", descriptor = "(I)V")
	@JvmStatic
	fun processMenuInput() {
		if (!MiniMenu.isOpen) {
			MiniMenu.isCollapsed = MiniMenu.maxVisibleEntries != -1 && MiniMenu.entryCount >= MiniMenu.maxVisibleEntries || GameShell.screenCanvasHeight < MiniMenu.entryCount * 16 + (if (FontMetrics.useBoldFont) 26 else 22)
		}
		MiniMenu.aClass341_46.clear()
		MiniMenu.aClass341_36.clear()
		@Pc(54) var local54: Int
		@Pc(49) var miniMenuEntryInner: MiniMenuEntry? = MiniMenu.entryList.first() as MiniMenuEntry?
		while (miniMenuEntryInner != null) {
			val entryClickType: Int = miniMenuEntryInner.clickType
			if (entryClickType < 1000) {
				miniMenuEntryInner.unlink()
				if (entryClickType == MiniMenuAction.TGT_GROUND || entryClickType == MiniMenuAction.TGT_LOC || entryClickType == MiniMenuAction.TGT_NPC || entryClickType == MiniMenuAction.TGT_OBJ || entryClickType == MiniMenuAction.TGT_PLAYER || entryClickType == MiniMenuAction.TGT_SELF || entryClickType == MiniMenuAction.TGT_BUTTON) {
					MiniMenu.aClass341_36.addLast(miniMenuEntryInner)
				} else {
					MiniMenu.aClass341_46.addLast(miniMenuEntryInner)
				}
			}
			miniMenuEntryInner = MiniMenu.entryList.next() as MiniMenuEntry?
		}
		MiniMenu.aClass341_46.transferAllTo(MiniMenu.entryList)
		MiniMenu.aClass341_36.transferAllTo(MiniMenu.entryList)
		if (MiniMenu.entryCount > 1) {
			if (MiniMenu.shiftClickEnabled && KeyRecorder.keyRecorder!!.isPressed(SimpleKeyboardMonitorConstants.KEY_CODE_SHIFT) && MiniMenu.entryCount > 2) {
				MiniMenu.aClass2_Sub6_Sub3_1 = MiniMenu.entryList.sentinel.previous!!.previous as MiniMenuEntry?
			} else {
				MiniMenu.aClass2_Sub6_Sub3_1 = MiniMenu.entryList.sentinel.previous as MiniMenuEntry?
			}
			if (ClientFeatures.shiftDrop && KeyRecorder.keyRecorder!!.isPressed(81)) {
				// Not part of the original client: with Shift held, a left click on an inventory item drops it.
				val drop: MiniMenuEntry? = ClientFeatures.shiftDropEntry(MiniMenu.entryList)
				if (drop != null) {
					MiniMenu.aClass2_Sub6_Sub3_1 = drop
				}
			}
			MiniMenu.aClass2_Sub6_Sub3_3 = MiniMenu.entryList.sentinel.previous as MiniMenuEntry?
		} else {
			MiniMenu.aClass2_Sub6_Sub3_1 = null
			MiniMenu.aClass2_Sub6_Sub3_3 = null
		}
		var clickType: Int = -1
		@Pc(158) val mouseEvent: MouseRecord? = MouseManager.MOUSE_MOVEMENTS.first() as MouseRecord?
		if (mouseEvent != null) {
			clickType = mouseEvent.getClickType()
		}
		if (!MiniMenu.isOpen) {
			// Not part of the original client: a left press on the 634HD toggle (HdOptionsButton) or its
			// settings panel (HdSettingsPanel) is their own.
			if (clickType == MouseLog.TYPE_PRESS_LEFT && mouseEvent != null && (HdSettingsPanel.pressed(mouseEvent.getMouseX(), mouseEvent.getMouseY()) || HdOptionsButton.pressed(mouseEvent.getMouseX(), mouseEvent.getMouseY()))) {
				if (HdSettingsPanel.mapRebuildWanted) {
					// the panel's draw distance changed: the map is built again at it
					HdSettingsPanel.mapRebuildWanted = false
					MapRebuild.rebuildMap()
				}
				return
			}
			if (clickType == MouseLog.TYPE_PRESS_LEFT && (MiniMenu.menuOpenMode == 1 && MiniMenu.entryCount > 2 || MiniMenu.isTopEntryInterfaceAction())) {
				clickType = MouseLog.TYPE_PRESS_RIGHT
			}
			if (clickType == MouseLog.TYPE_PRESS_RIGHT && MiniMenu.entryCount > 0 && mouseEvent != null) {
				if (InterfaceDrag.component == null && Preferences.favoriteWorlds == 0) {
					MiniMenu.openContextMenu(mouseEvent.getMouseX(), mouseEvent.getMouseY())
				} else {
					MiniMenu.deferredClickType = 2
				}
			}
			if (clickType == MouseLog.TYPE_PRESS_LEFT) {
				if (MiniMenu.aClass2_Sub6_Sub3_1 != null) {
					MiniMenuDefaultClick.clickDefaultEntry()
				} else if (MiniMenu.interfaceSelectionActive) {
					Static657.cancelInterfaceSelection()
				}
			}
			if (InterfaceDrag.component != null || Preferences.favoriteWorlds != 0) {
				return
			}
			MiniMenu.deferredEntry = null
			MiniMenu.deferredClickType = 0
			return
		}
		@Pc(173) var mouseX: Int
		@Pc(177) var mouseY: Int
		@Pc(256) var local256: Int
		@Pc(258) var local258: Int
		if (clickType == -1) {
			mouseX = MouseRecord.mouseRecorder!!.getMouseX()
			mouseY = MouseRecord.mouseRecorder!!.getMouseY()
			@Pc(179) var isInSubmenuBounds: Boolean = false
			if (MiniMenu.activeSubmenu != null) {
				if (MiniMenu.activeSubmenuX - 10 <= mouseX && mouseX <= MiniMenu.activeSubmenuX + MiniMenu.activeSubmenuWidth + 10 && MiniMenu.activeSubmenuY - 10 <= mouseY && mouseY <= MiniMenu.activeSubmenuHeight + MiniMenu.activeSubmenuY + 10) {
					isInSubmenuBounds = true
				} else {
					MiniMenu.closeSubmenu()
				}
			}
			if (!isInSubmenuBounds) {
				if (MiniMenu.menuOpenX - 10 > mouseX || mouseX > MiniMenu.menuOpenX + MiniMenu.menuOpenWidth + 10 || mouseY < MiniMenu.menuOpenY - 10 || MiniMenu.menuOpenY + InterfaceComponentRenderer.menuHeight + 10 < mouseY) {
					MiniMenu.close()
				} else if (MiniMenu.isCollapsed) {
					var hoveredRow: Int = -1
					var hoveredRowY: Int = -1
					@Pc(272) var local272: Int
					@Pc(260) var i: Int = 0
					while (i < MiniMenu.coloredEntryCount) {
						if (FontMetrics.useBoldFont) {
							val entryY: Int = i * 16 + MiniMenu.menuOpenY + 33
							if (entryY - 13 < mouseY && entryY + 4 > mouseY) {
								hoveredRowY = entryY - 13
								hoveredRow = i
								break
							}
						} else {
							val entryY: Int = i * 16 + MiniMenu.menuOpenY + 31
							if (mouseY > entryY - 13 && mouseY < entryY + 3) {
								hoveredRowY = entryY - 13
								hoveredRow = i
								break
							}
						}
						i++
					}
					if (hoveredRow != -1) {
						var entryPosition: Int = 0
						@Pc(343) val queueIterator: QueueIterator = QueueIterator(MiniMenu.sortedColoredEntries)
						@Pc(348) var miniMenuEntry: ColoredMiniMenuEntry? = queueIterator.first() as ColoredMiniMenuEntry?
						while (miniMenuEntry != null) {
							if (entryPosition == hoveredRow) {
								if (miniMenuEntry.queueSize > 1) {
									MiniMenu.openSubmenu(miniMenuEntry, mouseY, hoveredRowY)
								}
								break
							}
							entryPosition++
							miniMenuEntry = queueIterator.next() as ColoredMiniMenuEntry?
						}
					}
				}
			}
		}
		if (clickType != MouseLog.TYPE_PRESS_LEFT) {
			return
		}
		mouseX = mouseEvent!!.getMouseX()
		mouseY = mouseEvent.getMouseY()
		@Pc(414) var hoveredIndex: Int
		@Pc(487) var queueIterator2: QueueIterator
		@Pc(492) var miniMenuEntryInner2: MiniMenuEntry?
		if (MiniMenu.activeSubmenu != null && mouseX >= MiniMenu.activeSubmenuX && MiniMenu.activeSubmenuWidth + MiniMenu.activeSubmenuX >= mouseX && mouseY >= MiniMenu.activeSubmenuY && MiniMenu.activeSubmenuY + MiniMenu.activeSubmenuHeight >= mouseY) {
			hoveredIndex = -1
			var entryIndex: Int = 0
			while (entryIndex < MiniMenu.activeSubmenu!!.queueSize) {
				if (FontMetrics.useBoldFont) {
					val entryY: Int = MiniMenu.activeSubmenuY + entryIndex * 16 + 33
					if (mouseY > entryY - 13 && entryY + 4 > mouseY) {
						hoveredIndex = entryIndex
					}
				} else {
					val entryY: Int = entryIndex * 16 + MiniMenu.activeSubmenuY + 31
					if (entryY - 13 < mouseY && entryY + 3 > mouseY) {
						hoveredIndex = entryIndex
					}
				}
				entryIndex++
			}
			if (hoveredIndex != -1) {
				var entryPosition: Int = 0
				queueIterator2 = QueueIterator(MiniMenu.activeSubmenu!!.nodeQueue)
				miniMenuEntryInner2 = queueIterator2.first() as MiniMenuEntry?
				while (miniMenuEntryInner2 != null) {
					if (hoveredIndex == entryPosition) {
						MiniMenuActionExecutor.processMenuClick(miniMenuEntryInner2, mouseY, mouseX)
						break
					}
					entryPosition++
					miniMenuEntryInner2 = queueIterator2.next() as MiniMenuEntry?
				}
			}
			MiniMenu.close()
			return
		}
		if (mouseX < MiniMenu.menuOpenX || mouseX > MiniMenu.menuOpenX + MiniMenu.menuOpenWidth || mouseY < MiniMenu.menuOpenY || mouseY > MiniMenu.menuOpenY + InterfaceComponentRenderer.menuHeight) {
			return
		}
		if (MiniMenu.isCollapsed) {
			hoveredIndex = -1
			var entryIndex: Int = 0
			while (entryIndex < MiniMenu.coloredEntryCount) {
				if (FontMetrics.useBoldFont) {
					val entryY: Int = MiniMenu.menuOpenY + entryIndex * 16 + 33
					if (entryY - 13 < mouseY && mouseY < entryY + 4) {
						hoveredIndex = entryIndex
						break
					}
				} else {
					val entryY: Int = MiniMenu.menuOpenY + entryIndex * 16 + 31
					if (entryY - 13 < mouseY && entryY + 3 > mouseY) {
						hoveredIndex = entryIndex
						break
					}
				}
				entryIndex++
			}
			if (hoveredIndex != -1) {
				var entryPosition: Int = 0
				queueIterator2 = QueueIterator(MiniMenu.sortedColoredEntries)
				@Pc(632) var miniMenuEntry2: ColoredMiniMenuEntry? = queueIterator2.first() as ColoredMiniMenuEntry?
				while (miniMenuEntry2 != null) {
					if (hoveredIndex == entryPosition) {
						MiniMenuActionExecutor.processMenuClick(miniMenuEntry2.nodeQueue.sentinel.secondaryNext as MiniMenuEntry?, mouseY, mouseX)
						MiniMenu.close()
						return
					}
					entryPosition++
					miniMenuEntry2 = queueIterator2.next() as ColoredMiniMenuEntry?
				}
				return
			}
		} else {
			hoveredIndex = -1
			var entryIndex: Int = 0
			while (entryIndex < MiniMenu.entryCount) {
				if (FontMetrics.useBoldFont) {
					val entryY: Int = MiniMenu.menuOpenY + (MiniMenu.entryCount - (1 - -entryIndex)) * 16 + 33
					if (mouseY > entryY - 13 && mouseY < entryY + 4) {
						hoveredIndex = entryIndex
					}
				} else {
					val entryY: Int = MiniMenu.menuOpenY + (MiniMenu.entryCount - entryIndex - 1) * 16 + 31
					if (mouseY > entryY - 13 && entryY + 3 > mouseY) {
						hoveredIndex = entryIndex
					}
				}
				entryIndex++
			}
			if (hoveredIndex != -1) {
				var entryPosition: Int = 0
				@Pc(756) val dequeIterator: DequeIterator = DequeIterator(MiniMenu.entryList)
				miniMenuEntryInner2 = dequeIterator.first() as MiniMenuEntry?
				while (miniMenuEntryInner2 != null) {
					if (hoveredIndex == entryPosition) {
						MiniMenuActionExecutor.processMenuClick(miniMenuEntryInner2, mouseY, mouseX)
						break
					}
					entryPosition++
					miniMenuEntryInner2 = dequeIterator.next() as MiniMenuEntry?
				}
			}
			MiniMenu.close()
		}
		return
	}
}

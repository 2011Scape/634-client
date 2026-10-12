package com.jagex.game.runetek5.client.menu

import com.jagex.game.runetek5.core.constants.MiniMenuAction
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.client.features.ClientFeatures
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.config.param.ParamType
import com.jagex.game.runetek5.config.rsinterface.Component
import com.jagex.game.runetek5.tools.LocalizedText
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.InterfaceManager
import com.jagex.game.runetek5.MapRegion
import com.jagex.game.runetek5.client.Game
import com.jagex.game.runetek5.client.input.mouse.CursorManager
import com.jagex.game.runetek5.config.iftype.TargetMask
import com.jagex.game.runetek5.config.npc.NPCType
import com.jagex.game.runetek5.entity.NPCEntity
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.vardomain.PlayerVarDomain
import com.jagex.game.runetek5.client.ClientState

object MiniMenuBuilder {
	@OriginalMember(owner = "client!oh", name = "c", descriptor = "I")
	@JvmField
	var USE_OPTIONS_FLAGS: Int = 0

	@OriginalMember(owner = "client!ti", name = "a", descriptor = "(IILclient!at;B)V")
	@JvmStatic
	fun interfaceComponentHovered(@OriginalArg(0) relativeY: Int, @OriginalArg(1) relativeX: Int, @OriginalArg(2) component: Component?) {
		if (MiniMenu.interfaceSelectionActive) {
			@Pc(13) val paramType: ParamType? = if (MiniMenu.targetParamId == -1) null else TypeLists.PARAMS!!.list(MiniMenu.targetParamId)
			if (InterfaceManager.getComponentSettings(component)!!.isTargetableByUse() && (MiniMenu.useTargetMask and 0x20) != 0 && (paramType == null || component!!.getIntParam(paramType.defaultInt, MiniMenu.targetParamId) != paramType.defaultInt)) {
				// Not part of the original client: the debug suffix, empty unless client.debug is set.
				MiniMenu.addEntry(false, MiniMenu.useOnTarget + " -> " + component!!.opBase + ClientFeatures.debugSuffix(component.idHash shr 16, component.idHash and 0xFFFF), component.idHash, false, component.slotId, component.containerItemId, true, MiniMenuAction.TGT_BUTTON, (component.idHash or (component.slotId shl 0)).toLong(), MiniMenu.useOnType, 0L, MiniMenu.useOnCursor)
			}
		}
		@Pc(87) var string: String?
		@Pc(80) var i: Int = 9
		while (i >= 5) {
			string = Component.getOpName(i, component)
			if (string != null) {
				// Not part of the original client: the debug suffix, empty unless client.debug is set.
				MiniMenu.addEntry(false, component!!.opBase + ClientFeatures.debugSuffix(component.idHash shr 16, component.idHash and 0xFFFF), component.idHash, false, component.slotId, component.containerItemId, true, MiniMenuAction.IF_BUTTONX2, ((component.slotId shl 0) or component.idHash).toLong(), string, (i + 1).toLong(), Component.getOpCursor(i, component))
			}
			i--
		}
		string = Component.getUseOptionString(component)
		if (string != null) {
			// Not part of the original client: the debug suffix, empty unless client.debug is set.
			MiniMenu.addEntry(false, component!!.opBase + ClientFeatures.debugSuffix(component.idHash shr 16, component.idHash and 0xFFFF), component.idHash, false, component.slotId, component.containerItemId, true, MiniMenuAction.IF_BUTTONT, ((component.slotId shl 0) or component.idHash).toLong(), string, 0L, component.targetOpCursor)
		}
		@Pc(175) var j: Int = 4
		while (j >= 0) {
			@Pc(182) val string2: String? = Component.getOpName(j, component)
			if (string2 != null) {
				MiniMenu.addEntry(false, component!!.opBase, component.idHash, false, component.slotId, component.containerItemId, true, MiniMenuAction.IF_BUTTONX1, ((component.slotId shl 0) or component.idHash).toLong(), string2, (j + 1).toLong(), Component.getOpCursor(j, component))
			}
			j--
		}
		if (!InterfaceManager.getComponentSettings(component)!!.continueOptionEnabled()) {
			return
		}
		if (component!!.resumeText == null) {
			MiniMenu.addEntry(false, "", component.idHash, false, component.slotId, component.containerItemId, true, MiniMenuAction.PAUSE_BUTTON, ((component.slotId shl 0) or component.idHash).toLong(), LocalizedText.aClass351_11.get(ClientState.language), 0L, -1)
		} else {
			MiniMenu.addEntry(false, "", component.idHash, false, component.slotId, component.containerItemId, true, MiniMenuAction.PAUSE_BUTTON, ((component.slotId shl 0) or component.idHash).toLong(), component.resumeText, 0L, -1)
		}
	}
	@OriginalMember(owner = "client!em", name = "a", descriptor = "(ZZLclient!ff;)V")
	@JvmStatic
	fun npcHovered(@OriginalArg(0) differentLevel: Boolean, @OriginalArg(2) npcEntity: NPCEntity?) {
		if (MiniMenu.entryCount >= 400) {
			return
		}
		@Pc(14) var npcType: NPCType? = npcEntity!!.npcType
		if (npcType!!.alternativeIds != null) {
			npcType = npcType.getMultiNpc(PlayerVarDomain.playerVarProvider)
			if (npcType == null) {
				return
			}
		}
		if (!npcType.visible) {
			return
		}
		@Pc(32) var string: String? = npcType.name
		if (npcType.combatLevel != 0) {
			@Pc(51) val string2: String? = if (Game.STELLAR_DAWN === Game.CURRENT_GAME) LocalizedText.aClass351_32.get(ClientState.language) else LocalizedText.aClass351_30.get(ClientState.language)
			string = string + MiniMenu.getCombatLevelColour(PlayerUpdate.localPlayer!!.combatLevel, npcType.combatLevel) + " (" + string2 + npcType.combatLevel + ")"
		}
		if (MiniMenu.interfaceSelectionActive && !differentLevel) {
			@Pc(88) val paramType: ParamType? = if (MiniMenu.targetParamId == -1) null else TypeLists.PARAMS!!.list(MiniMenu.targetParamId)
			if ((MiniMenu.useTargetMask and TargetMask.TGT_NPC) != 0 && (paramType == null || npcType.getParam(paramType.defaultInt, MiniMenu.targetParamId) != paramType.defaultInt)) {
				MiniMenu.addEntry(false, MiniMenu.useOnTarget + " -> <col=ffff00>" + string, 0, false, 0, -1, true, MiniMenuAction.TGT_NPC, npcEntity.playerIndex.toLong(), MiniMenu.useOnType, npcEntity.playerIndex.toLong(), MiniMenu.useOnCursor)
			}
		}
		if (!differentLevel) {
			@Pc(139) var strings: Array<String?>? = npcType.actions
			if (MiniMenuBuilder.PREFIX_MINIMENU_OPS) {
				strings = MiniMenuBuilder.prefixOpIndices(strings)
			}
			@Pc(149) var actionIndex: Int
			if (strings != null) {
				actionIndex = 4
				while (actionIndex >= 0) {
					if (strings[actionIndex] != null && (npcType.gameType.toInt() == 0 || !strings[actionIndex]!!.equals(LocalizedText.aClass351_25.get(ClientState.language), ignoreCase = true))) {
						@Pc(172) var clickType: Byte = 0
						if (actionIndex == 0) {
							clickType = MiniMenuAction.OPNPC1.toByte()
						}
						@Pc(178) var cursor: Int = CursorManager.defaultAttackCursor
						if (actionIndex == 1) {
							clickType = MiniMenuAction.OPNPC2.toByte()
						}
						if (actionIndex == 2) {
							clickType = MiniMenuAction.OPNPC3.toByte()
						}
						if (actionIndex == 3) {
							clickType = MiniMenuAction.OPNPC4.toByte()
						}
						if (npcType.primaryCursorActionIndex == actionIndex) {
							cursor = npcType.primaryCursor
						}
						if (actionIndex == 4) {
							clickType = MiniMenuAction.OPNPC5.toByte()
						}
						if (actionIndex == npcType.secondaryCursorActionIndex) {
							cursor = npcType.secondaryCursor
						}
						MiniMenu.addEntry(false, "<col=ffff00>" + string, 0, false, 0, -1, true, clickType.toInt(), npcEntity.playerIndex.toLong(), strings[actionIndex], npcEntity.playerIndex.toLong(), if (strings[actionIndex]!!.equals(LocalizedText.aClass351_25.get(ClientState.language), ignoreCase = true)) npcType.attackCursor else cursor)
					}
					actionIndex--
				}
			}
			if (npcType.gameType.toInt() == 1 && strings != null) {
				actionIndex = 4
				while (actionIndex >= 0) {
					if (strings[actionIndex] != null && strings[actionIndex]!!.equals(LocalizedText.aClass351_25.get(ClientState.language), ignoreCase = true)) {
						@Pc(290) var levelPenalty: Short = 0
						if (npcType.combatLevel > PlayerUpdate.localPlayer!!.combatLevel) {
							levelPenalty = 2000
						}
						@Pc(303) var attackClickType: Short = 0
						if (actionIndex == 0) {
							attackClickType = MiniMenuAction.OPNPC1.toShort()
						}
						if (actionIndex == 1) {
							attackClickType = MiniMenuAction.OPNPC2.toShort()
						}
						if (actionIndex == 2) {
							attackClickType = MiniMenuAction.OPNPC3.toShort()
						}
						if (actionIndex == 3) {
							attackClickType = MiniMenuAction.OPNPC4.toShort()
						}
						if (actionIndex == 4) {
							attackClickType = MiniMenuAction.OPNPC5.toShort()
						}
						if (attackClickType.toInt() != 0) {
							attackClickType = (attackClickType + levelPenalty).toShort()
						}
						MiniMenu.addEntry(false, "<col=ffff00>" + string, 0, false, 0, -1, true, attackClickType.toInt(), npcEntity.playerIndex.toLong(), strings[actionIndex], npcEntity.playerIndex.toLong(), npcType.attackCursor)
					}
					actionIndex--
				}
			}
		}
		// Not part of the original client: the debug suffix, empty unless client.debug is set.
		MiniMenu.addEntry(differentLevel, "<col=ffff00>" + string + ClientFeatures.debugSuffix(npcType.id, (npcEntity.x shr 9) + MapRegion.sceneBaseX - npcEntity.npcType!!.size + 1, (npcEntity.z shr 9) + MapRegion.sceneBaseY - npcEntity.npcType!!.size + 1, npcEntity.level.toInt()), 0, false, 0, -1, true, MiniMenuAction.OPNPC6, npcEntity.playerIndex.toLong(), LocalizedText.aClass351_24.get(ClientState.language), npcEntity.playerIndex.toLong(), MiniMenu.examineCursor)
	}
	@OriginalMember(owner = "client!fs", name = "a", descriptor = "([Ljava/lang/String;I)[Ljava/lang/String;")
	@JvmStatic
	fun prefixOpIndices(@OriginalArg(0) strings: Array<String?>?): Array<String?> {
		@Pc(8) val strings2: Array<String?> = arrayOfNulls<String>(5)
		@Pc(10) var i: Int = 0
		while (i < 5) {
			strings2[i] = i.toString() + ": "
			if (strings != null && strings[i] != null) {
				strings2[i] = strings2[i] + strings[i]
			}
			i++
		}
		return strings2
	}

	@OriginalMember(owner = "client!cu", name = "x", descriptor = "Z")
	const val PREFIX_MINIMENU_OPS: Boolean = false
}

package com.jagex.game.runetek5.client.menu;

import com.jagex.game.runetek5.core.constants.MiniMenuAction;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.client.features.ClientFeatures;
import com.jagex.game.runetek5.config.TypeLists;
import com.jagex.game.runetek5.config.param.ParamType;
import com.jagex.game.runetek5.config.rsinterface.Component;
import com.jagex.game.runetek5.network.world.WorldList;
import com.jagex.game.runetek5.tools.LocalizedText;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.InterfaceManager;
import com.jagex.game.runetek5.MapRegion;
import com.jagex.game.runetek5.client.Game;
import com.jagex.game.runetek5.client.input.mouse.CursorManager;
import com.jagex.game.runetek5.config.iftype.TargetMask;
import com.jagex.game.runetek5.config.npc.NPCType;
import com.jagex.game.runetek5.entity.NPCEntity;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;
import com.jagex.game.runetek5.vardomain.PlayerVarDomain;
import com.jagex.game.runetek5.client.ClientState;

public final class MiniMenuBuilder {
	@OriginalMember(owner = "client!oh", name = "c", descriptor = "I")
	public static int USE_OPTIONS_FLAGS;

	private MiniMenuBuilder() {
		/* empty */
	}
	@OriginalMember(owner = "client!ti", name = "a", descriptor = "(IILclient!at;B)V")
	public static void interfaceComponentHovered(@OriginalArg(0) int relativeY, @OriginalArg(1) int relativeX, @OriginalArg(2) Component component) {
		if (MiniMenu.interfaceSelectionActive) {
			@Pc(13) ParamType paramType = MiniMenu.targetParamId == -1 ? null : TypeLists.PARAMS.list(MiniMenu.targetParamId);
			if (InterfaceManager.getComponentSettings(component).isTargetableByUse() && (MiniMenu.useTargetMask & 0x20) != 0 && (paramType == null || component.getIntParam(paramType.defaultInt, MiniMenu.targetParamId) != paramType.defaultInt)) {
				// Not part of the original client: the debug suffix, empty unless client.debug is set.
				MiniMenu.addEntry(false, MiniMenu.useOnTarget + " -> " + component.opBase + ClientFeatures.debugSuffix(component.idHash >> 16, component.idHash & 0xFFFF), component.idHash, false, component.slotId, component.containerItemId, true, MiniMenuAction.TGT_BUTTON, (long) (component.idHash | component.slotId << 0), MiniMenu.useOnType, 0L, MiniMenu.useOnCursor);
			}
		}
		@Pc(87) String string;
		for (@Pc(80) int i = 9; i >= 5; i--) {
			string = Component.getOpName(i, component);
			if (string != null) {
				// Not part of the original client: the debug suffix, empty unless client.debug is set.
				MiniMenu.addEntry(false, component.opBase + ClientFeatures.debugSuffix(component.idHash >> 16, component.idHash & 0xFFFF), component.idHash, false, component.slotId, component.containerItemId, true, MiniMenuAction.IF_BUTTONX2, (long) (component.slotId << 0 | component.idHash), string, (long) (i + 1), Component.getOpCursor(i, component));
			}
		}
		string = Component.getUseOptionString(component);
		if (string != null) {
			// Not part of the original client: the debug suffix, empty unless client.debug is set.
			MiniMenu.addEntry(false, component.opBase + ClientFeatures.debugSuffix(component.idHash >> 16, component.idHash & 0xFFFF), component.idHash, false, component.slotId, component.containerItemId, true, MiniMenuAction.IF_BUTTONT, (long) (component.slotId << 0 | component.idHash), string, 0L, component.targetOpCursor);
		}
		for (@Pc(175) int j = 4; j >= 0; j--) {
			@Pc(182) String string2 = Component.getOpName(j, component);
			if (string2 != null) {
				MiniMenu.addEntry(false, component.opBase, component.idHash, false, component.slotId, component.containerItemId, true, MiniMenuAction.IF_BUTTONX1, (long) (component.slotId << 0 | component.idHash), string2, (long) (j + 1), Component.getOpCursor(j, component));
			}
		}
		if (!InterfaceManager.getComponentSettings(component).continueOptionEnabled()) {
			return;
		}
		if (component.resumeText == null) {
			MiniMenu.addEntry(false, "", component.idHash, false, component.slotId, component.containerItemId, true, MiniMenuAction.PAUSE_BUTTON, (long) (component.slotId << 0 | component.idHash), LocalizedText.aClass351_11.get(ClientState.language), 0L, -1);
		} else {
			MiniMenu.addEntry(false, "", component.idHash, false, component.slotId, component.containerItemId, true, MiniMenuAction.PAUSE_BUTTON, (long) (component.slotId << 0 | component.idHash), component.resumeText, 0L, -1);
		}
	}
	@OriginalMember(owner = "client!em", name = "a", descriptor = "(ZZLclient!ff;)V")
	public static void npcHovered(@OriginalArg(0) boolean differentLevel, @OriginalArg(2) NPCEntity npcEntity) {
		if (MiniMenu.entryCount >= 400) {
			return;
		}
		@Pc(14) NPCType npcType = npcEntity.npcType;
		if (npcType.alternativeIds != null) {
			npcType = npcType.getMultiNpc(PlayerVarDomain.playerVarProvider);
			if (npcType == null) {
				return;
			}
		}
		if (!npcType.visible) {
			return;
		}
		@Pc(32) String string = npcType.name;
		if (npcType.combatLevel != 0) {
			@Pc(51) String string2 = Game.STELLAR_DAWN == Game.CURRENT_GAME ? LocalizedText.aClass351_32.get(ClientState.language) : LocalizedText.aClass351_30.get(ClientState.language);
			string = string + MiniMenu.getCombatLevelColour(PlayerUpdate.localPlayer.combatLevel, npcType.combatLevel) + " (" + string2 + npcType.combatLevel + ")";
		}
		if (MiniMenu.interfaceSelectionActive && !differentLevel) {
			@Pc(88) ParamType paramType = MiniMenu.targetParamId == -1 ? null : TypeLists.PARAMS.list(MiniMenu.targetParamId);
			if ((MiniMenu.useTargetMask & TargetMask.TGT_NPC) != 0 && (paramType == null || npcType.getParam(paramType.defaultInt, MiniMenu.targetParamId) != paramType.defaultInt)) {
				MiniMenu.addEntry(false, MiniMenu.useOnTarget + " -> <col=ffff00>" + string, 0, false, 0, -1, true, MiniMenuAction.TGT_NPC, (long) npcEntity.playerIndex, MiniMenu.useOnType, (long) npcEntity.playerIndex, MiniMenu.useOnCursor);
			}
		}
		if (!differentLevel) {
			@Pc(139) String[] strings = npcType.actions;
			if (MiniMenuBuilder.PREFIX_MINIMENU_OPS) {
				strings = MiniMenuBuilder.prefixOpIndices(strings);
			}
			@Pc(149) int actionIndex;
			if (strings != null) {
				for (actionIndex = 4; actionIndex >= 0; actionIndex--) {
					if (strings[actionIndex] != null && (npcType.gameType == 0 || !strings[actionIndex].equalsIgnoreCase(LocalizedText.aClass351_25.get(ClientState.language)))) {
						@Pc(172) byte clickType = 0;
						if (actionIndex == 0) {
							clickType = MiniMenuAction.OPNPC1;
						}
						@Pc(178) int cursor = CursorManager.defaultAttackCursor;
						if (actionIndex == 1) {
							clickType = MiniMenuAction.OPNPC2;
						}
						if (actionIndex == 2) {
							clickType = MiniMenuAction.OPNPC3;
						}
						if (actionIndex == 3) {
							clickType = MiniMenuAction.OPNPC4;
						}
						if (npcType.primaryCursorActionIndex == actionIndex) {
							cursor = npcType.primaryCursor;
						}
						if (actionIndex == 4) {
							clickType = MiniMenuAction.OPNPC5;
						}
						if (actionIndex == npcType.secondaryCursorActionIndex) {
							cursor = npcType.secondaryCursor;
						}
						MiniMenu.addEntry(false, "<col=ffff00>" + string, 0, false, 0, -1, true, clickType, (long) npcEntity.playerIndex, strings[actionIndex], (long) npcEntity.playerIndex, strings[actionIndex].equalsIgnoreCase(LocalizedText.aClass351_25.get(ClientState.language)) ? npcType.attackCursor : cursor);
					}
				}
			}
			if (npcType.gameType == 1 && strings != null) {
				for (actionIndex = 4; actionIndex >= 0; actionIndex--) {
					if (strings[actionIndex] != null && strings[actionIndex].equalsIgnoreCase(LocalizedText.aClass351_25.get(ClientState.language))) {
						@Pc(290) short levelPenalty = 0;
						if (npcType.combatLevel > PlayerUpdate.localPlayer.combatLevel) {
							levelPenalty = 2000;
						}
						@Pc(303) short attackClickType = 0;
						if (actionIndex == 0) {
							attackClickType = MiniMenuAction.OPNPC1;
						}
						if (actionIndex == 1) {
							attackClickType = MiniMenuAction.OPNPC2;
						}
						if (actionIndex == 2) {
							attackClickType = MiniMenuAction.OPNPC3;
						}
						if (actionIndex == 3) {
							attackClickType = MiniMenuAction.OPNPC4;
						}
						if (actionIndex == 4) {
							attackClickType = MiniMenuAction.OPNPC5;
						}
						if (attackClickType != 0) {
							attackClickType += levelPenalty;
						}
						MiniMenu.addEntry(false, "<col=ffff00>" + string, 0, false, 0, -1, true, attackClickType, (long) npcEntity.playerIndex, strings[actionIndex], (long) npcEntity.playerIndex, npcType.attackCursor);
					}
				}
			}
		}
		// Not part of the original client: the debug suffix, empty unless client.debug is set.
		MiniMenu.addEntry(differentLevel, "<col=ffff00>" + string + ClientFeatures.debugSuffix(npcType.id, (npcEntity.x >> 9) + MapRegion.sceneBaseX - npcEntity.npcType.size + 1, (npcEntity.z >> 9) + MapRegion.sceneBaseY - npcEntity.npcType.size + 1, npcEntity.level), 0, false, 0, -1, true, MiniMenuAction.OPNPC6, (long) npcEntity.playerIndex, LocalizedText.aClass351_24.get(ClientState.language), (long) npcEntity.playerIndex, MiniMenu.examineCursor);
	}
	@OriginalMember(owner = "client!fs", name = "a", descriptor = "([Ljava/lang/String;I)[Ljava/lang/String;")
	public static String[] prefixOpIndices(@OriginalArg(0) String[] strings) {
		@Pc(8) String[] strings2 = new String[5];
		for (@Pc(10) int i = 0; i < 5; i++) {
			strings2[i] = i + ": ";
			if (strings != null && strings[i] != null) {
				strings2[i] = strings2[i] + strings[i];
			}
		}
		return strings2;
	}

	@OriginalMember(owner = "client!cu", name = "x", descriptor = "Z")
	public static final boolean PREFIX_MINIMENU_OPS = false;
}

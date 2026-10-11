package com.jagex.game.runetek5.client.menu;

import com.jagex.game.runetek5.client.menu.MiniMenuEntry;
import com.jagex.game.runetek5.entity.NPCEntity;
import com.jagex.game.runetek5.entity.NPCEntityNode;
import com.jagex.game.runetek5.entity.PlayerEntity;
import com.jagex.game.runetek5.core.constants.MiniMenuAction;
import com.jagex.game.runetek5.core.datastruct.key.DequeIterator;
import com.jagex.game.runetek5.cache.node.QueueIterator;
import com.jagex.game.runetek5.core.datastruct.key.MouseOverTextY;
import com.jagex.game.runetek5.client.menu.ColoredMiniMenuEntry;
import com.jagex.game.runetek5.config.rsinterface.Component;
import com.jagex.game.runetek5.config.npc.NPCType;
import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer;
import com.jagex.game.runetek5.graphics.Toolkit;
import com.jagex.game.runetek5.graphics.gl.GlxMatrixScratch;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.event.keyboard.SimpleKeyboardMonitorConstants;
import com.jagex.game.runetek5.graphics.font.FontMetrics;
import com.jagex.game.runetek5.graphics.ifcomponent.InterfaceComponentRenderer;
import com.jagex.game.runetek5.graphics.sprite.StaticMedia;
import com.jagex.game.runetek5.client.GameShell;
import com.jagex.game.runetek5.config.TypeLists;
import com.jagex.game.runetek5.client.input.keyboard.KeyRecorder;
import com.jagex.game.runetek5.LocType;
import com.jagex.game.runetek5.network.world.WorldList;
import com.jagex.game.runetek5.cache.node.Queue;
import com.jagex.game.runetek5.cache.node.lru.LRUCache;
import com.jagex.game.runetek5.core.datastruct.key.Deque;
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable;
import com.jagex.game.runetek5.tools.LocalizedText;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;
import com.jagex.game.runetek5.client.Game;
import com.jagex.game.runetek5.interaction.chat.ChatBox;
import com.jagex.game.runetek5.client.input.mouse.CursorManager;
import com.jagex.game.runetek5.client.input.mouse.MouseRecord;
import com.jagex.game.runetek5.config.rsinterface.Interface;
import com.jagex.game.runetek5.Cursor;
import com.jagex.game.runetek5.InterfaceDrag;
import com.jagex.game.runetek5.InterfaceRectangles;
import com.jagex.game.runetek5.NPCList;
import com.jagex.game.runetek5.Find;
import com.jagex.game.runetek5.OrthoMode;
import com.jagex.game.runetek5.ScriptRunner;
import com.jagex.game.runetek5.cache.node.lru.CacheableNode;
import com.jagex.game.runetek5.config.Resource;
import com.jagex.game.runetek5.graphics.SpriteDataProvider;
import com.jagex.game.runetek5.graphics.sprite.NativeSprite;
import com.jagex.game.runetek5.graphics.sprite.SpriteType;
import com.jagex.game.runetek5.vardomain.PlayerVarDomain;
import com.jagex.game.runetek5.config.rsinterface.ComponentType;
import com.jagex.game.runetek5.client.ClientState;
import java.util.Random;

public final class MiniMenu {

	@OriginalMember(owner = "client!gt", name = "p", descriptor = "[I")
	public static final int[] NPC_TURN_DIRECTION = new int[] { -1, 8192, 0, -1, 12288, 10240, 14336, -1, 4096, 6144, 2048 };

	@OriginalMember(owner = "client!vj", name = "db", descriptor = "Z")
	public static boolean debugOps = false;

	@OriginalMember(owner = "client!hc", name = "a", descriptor = "(ZLjava/lang/String;IBZIIZIJLjava/lang/String;JI)V")
	public static void addEntry(@OriginalArg(0) boolean isSelectable, @OriginalArg(1) String string, @OriginalArg(2) int param1, @OriginalArg(4) boolean isUncategorized, @OriginalArg(5) int param0, @OriginalArg(6) int itemId, @OriginalArg(7) boolean showOtherLevelIcon, @OriginalArg(8) int clickType, @OriginalArg(9) long bitpacked, @OriginalArg(10) String string2, @OriginalArg(11) long entityUid, @OriginalArg(12) int cursor) {
		if (!MiniMenu.isOpen && MiniMenu.entryCount < 500) {
			@Pc(17) int resolvedCursor = cursor == -1 ? Cursor.interfaceCursorId : cursor;
			@Pc(38) MiniMenuEntry miniMenuEntryInner = new MiniMenuEntry(string2, string, resolvedCursor, clickType, itemId, entityUid, param0, param1, showOtherLevelIcon, isSelectable, bitpacked, isUncategorized);
			MiniMenu.addEntry(miniMenuEntryInner);
		}
	}

	@OriginalMember(owner = "client!jb", name = "a", descriptor = "(BII)Z")
	public static boolean isOpenForComponent(@OriginalArg(1) int slot, @OriginalArg(2) int componentHash) {
		if (!MiniMenu.isOpen) {
			return false;
		}
		@Pc(18) int interfaceId = componentHash >> 16;
		@Pc(22) int componentId = componentHash & 0xFFFF;
		if (Interface.interfaces[interfaceId] == null || Interface.interfaces[interfaceId][componentId] == null) {
			return false;
		}
		@Pc(40) Component component = Interface.interfaces[interfaceId][componentId];
		@Pc(51) MiniMenuEntry miniMenuEntryInner;
		if (slot == -1 && component.type == ComponentType.CONTAINER) {
			for (miniMenuEntryInner = (MiniMenuEntry) MiniMenu.entryList.first(); miniMenuEntryInner != null; miniMenuEntryInner = (MiniMenuEntry) MiniMenu.entryList.next()) {
				if (miniMenuEntryInner.clickType == MiniMenuAction.TGT_BUTTON || miniMenuEntryInner.clickType == MiniMenuAction.IF_BUTTONX2 || miniMenuEntryInner.clickType == MiniMenuAction.IF_BUTTONT || miniMenuEntryInner.clickType == MiniMenuAction.IF_BUTTONX1 || miniMenuEntryInner.clickType == MiniMenuAction.PAUSE_BUTTON) {
					for (@Pc(82) Component component2 = Interface.getComponent(miniMenuEntryInner.param1); component2 != null; component2 = Component.getParentLayer(component2)) {
						if (component2.idHash == component.idHash) {
							return true;
						}
					}
				}
			}
		} else {
			for (miniMenuEntryInner = (MiniMenuEntry) MiniMenu.entryList.first(); miniMenuEntryInner != null; miniMenuEntryInner = (MiniMenuEntry) MiniMenu.entryList.next()) {
				if (miniMenuEntryInner.param0 == slot && miniMenuEntryInner.param1 == component.idHash && (miniMenuEntryInner.clickType == MiniMenuAction.TGT_BUTTON || miniMenuEntryInner.clickType == MiniMenuAction.IF_BUTTONX2 || miniMenuEntryInner.clickType == MiniMenuAction.IF_BUTTONT || miniMenuEntryInner.clickType == MiniMenuAction.IF_BUTTONX1 || miniMenuEntryInner.clickType == MiniMenuAction.PAUSE_BUTTON)) {
					return true;
				}
			}
		}
		return false;
	}

	@OriginalMember(owner = "client!un", name = "n", descriptor = "(I)Z")
	public static boolean isTopEntryInterfaceAction() {
		if (MiniMenu.aClass2_Sub6_Sub3_3 == null) {
			return false;
		} else {
			if (MiniMenu.aClass2_Sub6_Sub3_3.clickType >= 2000) {
				MiniMenu.aClass2_Sub6_Sub3_3.clickType -= 2000;
			}
			return MiniMenu.aClass2_Sub6_Sub3_3.clickType == MiniMenuAction.IF_BUTTONX2;
		}
	}

	@OriginalMember(owner = "client!vo", name = "a", descriptor = "(IIILclient!ha;Lclient!db;IIIBIII)V")
	public static void drawMenuEntry(@OriginalArg(0) int menuY, @OriginalArg(1) int hoverColor, @OriginalArg(2) int entryY, @OriginalArg(3) Toolkit toolkit, @OriginalArg(4) MiniMenuEntry miniMenuEntryInner, @OriginalArg(5) int menuWidth, @OriginalArg(6) int mouseY, @OriginalArg(7) int textColor, @OriginalArg(9) int menuHeight, @OriginalArg(10) int menuX, @OriginalArg(11) int mouseX) {
		if (menuX < mouseX && mouseX < menuX + menuWidth && mouseY > entryY - 13 && mouseY < entryY + 3 && miniMenuEntryInner.showOtherLevelIcon) {
			textColor = hoverColor;
		}
		@Pc(52) int[] quests = null;
		if (MiniMenu.isObjClickType(miniMenuEntryInner.clickType)) {
			quests = TypeLists.OBJ.list((int) miniMenuEntryInner.entityUid).quests;
		} else if (miniMenuEntryInner.itemId != -1) {
			quests = TypeLists.OBJ.list(miniMenuEntryInner.itemId).quests;
		} else if (MiniMenu.isNpcClickType(miniMenuEntryInner.clickType)) {
			@Pc(122) NPCEntityNode npcEntityNode = (NPCEntityNode) NPCList.NPC_MAP.get((long) miniMenuEntryInner.entityUid);
			if (npcEntityNode != null) {
				@Pc(127) NPCEntity npcEntity = npcEntityNode.npc;
				@Pc(130) NPCType npcType = npcEntity.npcType;
				if (npcType.alternativeIds != null) {
					npcType = npcType.getMultiNpc(PlayerVarDomain.playerVarProvider);
				}
				if (npcType != null) {
					quests = npcType.quests;
				}
			}
		} else if (MiniMenu.isLocClickType(miniMenuEntryInner.clickType)) {
			@Pc(91) LocType locType;
			if (miniMenuEntryInner.clickType == MiniMenuAction.OPLOC6) {
				locType = TypeLists.LOC.list((int) miniMenuEntryInner.entityUid);
			} else {
				locType = TypeLists.LOC.list((int) (miniMenuEntryInner.entityUid >>> 32 & 0x7FFFFFFFL));
			}
			if (locType.alternativeIds != null) {
				locType = locType.getMultiLoc(PlayerVarDomain.playerVarProvider);
			}
			if (locType != null) {
				quests = locType.quests;
			}
		}
		@Pc(164) String string = MiniMenu.getTextFrom(miniMenuEntryInner);
		if (quests != null) {
			string = string + ChatBox.getQuestIconTags(quests);
		}
		MiniMenu.minimenuHeader.drawStringWithSprites(entryY, string, textColor, menuX + 3, StaticMedia.chatIcons, StaticMedia.chatScaleHeights);
		if (miniMenuEntryInner.isSelectable) {
			StaticMedia.otherLevelSprite.render(FontMetrics.defaultFontMetrics.getWidthNoSprites(string) + menuX + 5, entryY + -12);
		}
	}

	@OriginalMember(owner = "client!vo", name = "a", descriptor = "(IIZ)Z")
	public static boolean hasHigherPriority(@OriginalArg(0) int existingClickType, @OriginalArg(1) int clickType) {
		if (existingClickType >= 1000 && clickType < 1000) {
			return true;
		} else if (existingClickType >= 1000 || clickType >= 1000) {
			return existingClickType >= 1000 && clickType >= 1000;
		} else if (MiniMenu.isUseOnClickType(clickType)) {
			return true;
		} else {
			return !MiniMenu.isUseOnClickType(existingClickType);
		}
	}

	@OriginalMember(owner = "client!wq", name = "a", descriptor = "(BII)V")
	public static void openContextMenu(@OriginalArg(1) int mouseX, @OriginalArg(2) int mouseY) {
		@Pc(13) int menuWidth = FontMetrics.defaultFontMetrics.getWidthNoSprites(LocalizedText.CHOOSE_OPTION.get(ClientState.language));
		@Pc(55) int menuHeight;
		@Pc(26) int local26;
		if (MiniMenu.isCollapsed) {
			for (@Pc(62) ColoredMiniMenuEntry miniMenuEntry = (ColoredMiniMenuEntry) MiniMenu.sortedColoredEntries.first(); miniMenuEntry != null; miniMenuEntry = (ColoredMiniMenuEntry) MiniMenu.sortedColoredEntries.next()) {
				int entryWidth;
				if (miniMenuEntry.queueSize == 1) {
					entryWidth = MiniMenu.getWidthForEntryWithQuests((MiniMenuEntry) miniMenuEntry.nodeQueue.sentinel.secondaryNext);
				} else {
					entryWidth = MiniMenu.getPrefixTextWidth(miniMenuEntry);
				}
				if (entryWidth > menuWidth) {
					menuWidth = entryWidth;
				}
			}
			menuWidth += 8;
			menuHeight = MiniMenu.coloredEntryCount * 16 + 21;
			InterfaceComponentRenderer.menuHeight = (FontMetrics.useBoldFont ? 26 : 22) + MiniMenu.coloredEntryCount * 16;
		} else {
			for (@Pc(20) MiniMenuEntry miniMenuEntryInner = (MiniMenuEntry) MiniMenu.entryList.first(); miniMenuEntryInner != null; miniMenuEntryInner = (MiniMenuEntry) MiniMenu.entryList.next()) {
				int entryWidth = MiniMenu.getWidthForEntryWithQuests(miniMenuEntryInner);
				if (menuWidth < entryWidth) {
					menuWidth = entryWidth;
				}
			}
			menuWidth += 8;
			InterfaceComponentRenderer.menuHeight = (FontMetrics.useBoldFont ? 26 : 22) + MiniMenu.entryCount * 16;
			menuHeight = MiniMenu.entryCount * 16 + 21;
		}
		@Pc(124) int menuX = mouseX - menuWidth / 2;
		if (GameShell.screenCanvasWidth < menuWidth + menuX) {
			menuX = GameShell.screenCanvasWidth - menuWidth;
		}
		if (menuX < 0) {
			menuX = 0;
		}
		int menuY = mouseY;
		if (mouseY + menuHeight > GameShell.screenCanvasHeight) {
			menuY = GameShell.screenCanvasHeight - menuHeight;
		}
		if (menuY < 0) {
			menuY = 0;
		}
		MiniMenu.menuOpenX = menuX;
		MiniMenu.isOpen = true;
		MiniMenu.menuOpenY = menuY;
		MiniMenu.menuOpenWidth = menuWidth;
	}

	@OriginalMember(owner = "client!co", name = "d", descriptor = "(B)I")
	public static int getClickType() {
		if (MiniMenu.isOpen) {
			return 6;
		} else if (MiniMenu.aClass2_Sub6_Sub3_1 == null) {
			return 0;
		} else {
			@Pc(19) int clickType = MiniMenu.aClass2_Sub6_Sub3_1.clickType;
			if (MiniMenu.isComponentClickType(clickType)) {
				return 1;
			} else if (MiniMenu.isObjClickType(clickType)) {
				return 2;
			} else if (MiniMenu.isLocClickType(clickType)) {
				return 3;
			} else if (MiniMenu.isNpcClickType(clickType)) {
				return 4;
			} else {
				return 5;
			}
		}
	}

	@OriginalMember(owner = "client!tk", name = "a", descriptor = "(Lclient!db;Z)I")
	public static int getWidthForEntryWithQuests(@OriginalArg(0) MiniMenuEntry miniMenuEntryInner) {
		@Pc(7) String string = MiniMenu.getTextFrom(miniMenuEntryInner);
		@Pc(9) int[] quests = null;
		if (MiniMenu.isObjClickType(miniMenuEntryInner.clickType)) {
			quests = TypeLists.OBJ.list((int) miniMenuEntryInner.entityUid).quests;
		} else if (miniMenuEntryInner.itemId != -1) {
			quests = TypeLists.OBJ.list(miniMenuEntryInner.itemId).quests;
		} else if (MiniMenu.isNpcClickType(miniMenuEntryInner.clickType)) {
			@Pc(78) NPCEntityNode npcEntityNode = (NPCEntityNode) NPCList.NPC_MAP.get((long) miniMenuEntryInner.entityUid);
			if (npcEntityNode != null) {
				@Pc(83) NPCEntity npcEntity = npcEntityNode.npc;
				@Pc(86) NPCType npcType = npcEntity.npcType;
				if (npcType.alternativeIds != null) {
					npcType = npcType.getMultiNpc(PlayerVarDomain.playerVarProvider);
				}
				if (npcType != null) {
					quests = npcType.quests;
				}
			}
		} else if (MiniMenu.isLocClickType(miniMenuEntryInner.clickType)) {
			@Pc(43) LocType locType;
			if (miniMenuEntryInner.clickType == MiniMenuAction.OPLOC6) {
				locType = TypeLists.LOC.list((int) miniMenuEntryInner.entityUid);
			} else {
				locType = TypeLists.LOC.list((int) (miniMenuEntryInner.entityUid >>> 32 & 0x7FFFFFFFL));
			}
			if (locType.alternativeIds != null) {
				locType = locType.getMultiLoc(PlayerVarDomain.playerVarProvider);
			}
			if (locType != null) {
				quests = locType.quests;
			}
		}
		if (quests != null) {
			string = string + ChatBox.getQuestIconTags(quests);
		}
		@Pc(135) int width = FontMetrics.defaultFontMetrics.getTextWidth(string, StaticMedia.chatIcons);
		if (miniMenuEntryInner.isSelectable) {
			width += StaticMedia.otherLevelSprite.getWidth() + 4;
		}
		return width;
	}

	@OriginalMember(owner = "client!bw", name = "r", descriptor = "I")
	public static int gameCanvasX = 0;

	@OriginalMember(owner = "client!bda", name = "c", descriptor = "(II)Z")
	public static boolean containsEntryForNpc(@OriginalArg(0) int npcIndex) {
		for (@Pc(14) MiniMenuEntry miniMenuEntryInner = (MiniMenuEntry) MiniMenu.entryList.first(); miniMenuEntryInner != null; miniMenuEntryInner = (MiniMenuEntry) MiniMenu.entryList.next()) {
			if (MiniMenu.isNpcClickType(miniMenuEntryInner.clickType) && (long) npcIndex == miniMenuEntryInner.entityUid) {
				return true;
			}
		}
		return false;
	}

	@OriginalMember(owner = "client!cw", name = "a", descriptor = "(Lclient!ha;B)V")
	public static void renderTooltipText(@OriginalArg(0) Toolkit toolkit) {
		if (MiniMenu.entryCount < 2 && !MiniMenu.interfaceSelectionActive || InterfaceDrag.component != null) {
			return;
		}
		@Pc(42) String string;
		if (MiniMenu.interfaceSelectionActive && MiniMenu.entryCount < 2) {
			string = MiniMenu.useOnType + LocalizedText.aClass351_34.get(ClientState.language) + MiniMenu.useOnTarget + " ->";
		} else if (MiniMenu.shiftClickEnabled && KeyRecorder.keyRecorder.isPressed(SimpleKeyboardMonitorConstants.KEY_CODE_SHIFT) && MiniMenu.entryCount > 2) {
			string = MiniMenu.getTextFrom(MiniMenu.aClass2_Sub6_Sub3_1);
		} else {
			@Pc(56) MiniMenuEntry miniMenuEntryInner = MiniMenu.aClass2_Sub6_Sub3_1;
			if (miniMenuEntryInner == null) {
				return;
			}
			string = MiniMenu.getTextFrom(miniMenuEntryInner);
			@Pc(65) int[] quests = null;
			if (MiniMenu.isObjClickType(miniMenuEntryInner.clickType)) {
				quests = TypeLists.OBJ.list((int) miniMenuEntryInner.entityUid).quests;
			} else if (miniMenuEntryInner.itemId != -1) {
				quests = TypeLists.OBJ.list(miniMenuEntryInner.itemId).quests;
			} else if (MiniMenu.isNpcClickType(miniMenuEntryInner.clickType)) {
				@Pc(96) NPCEntityNode npcEntityNode = (NPCEntityNode) NPCList.NPC_MAP.get((long) miniMenuEntryInner.entityUid);
				if (npcEntityNode != null) {
					@Pc(101) NPCEntity npcEntity = npcEntityNode.npc;
					@Pc(104) NPCType npcType = npcEntity.npcType;
					if (npcType.alternativeIds != null) {
						npcType = npcType.getMultiNpc(PlayerVarDomain.playerVarProvider);
					}
					if (npcType != null) {
						quests = npcType.quests;
					}
				}
			} else if (MiniMenu.isLocClickType(miniMenuEntryInner.clickType)) {
				@Pc(140) LocType locType;
				if (miniMenuEntryInner.clickType == MiniMenuAction.OPLOC6) {
					locType = TypeLists.LOC.list((int) miniMenuEntryInner.entityUid);
				} else {
					locType = TypeLists.LOC.list((int) (miniMenuEntryInner.entityUid >>> 32 & 0x7FFFFFFFL));
				}
				if (locType.alternativeIds != null) {
					locType = locType.getMultiLoc(PlayerVarDomain.playerVarProvider);
				}
				if (locType != null) {
					quests = locType.quests;
				}
			}
			if (quests != null) {
				string = string + ChatBox.getQuestIconTags(quests);
			}
		}
		if (MiniMenu.entryCount > 2) {
			string = string + "<col=ffffff> / " + (MiniMenu.entryCount - 2) + LocalizedText.aClass351_27.get(ClientState.language);
		}
		if (MouseOverTextY.component != null) {
			@Pc(217) AbstractFontRenderer font = MouseOverTextY.component.getFontRenderer(toolkit);
			if (font == null) {
				font = MiniMenu.minimenuHeader;
			}
			font.renderTextAntiMacro(StaticMedia.chatScaleHeights, MouseOverTextY.component.width, StaticMedia.chatIcons, MouseOverTextY.component.textVerticalAli, MouseOverTextY.component.color, MouseOverTextY.component.height, MouseOverTextY.component.spriteShadow, MiniMenu.RANDOMIZER, MouseOverTextY.component.textHorizontalAli, MouseOverTextY.mouseOverTextY, MiniMenu.randomSeed, MouseOverTextY.mouseOverTextX, string, MiniMenu.tooltipTextBounds);
			InterfaceRectangles.inboundsComponentToTop(MiniMenu.tooltipTextBounds[0], MiniMenu.tooltipTextBounds[2], MiniMenu.tooltipTextBounds[1], MiniMenu.tooltipTextBounds[3]);
		} else if (MiniMenu.component != null && Game.CURRENT_GAME == Game.RUNESCAPE) {
			@Pc(282) int extraWidth = MiniMenu.minimenuHeader.renderAntiMacroText(MiniMenu.randomSeed, StaticMedia.chatScaleHeights, string, StaticMedia.chatIcons, InterfaceComponentRenderer.chatDisplayX + 16, InterfaceComponentRenderer.chatDisplayY + 4, MiniMenu.RANDOMIZER);
			InterfaceRectangles.inboundsComponentToTop(InterfaceComponentRenderer.chatDisplayY + 4, FontMetrics.defaultFontMetrics.getWidthNoSprites(string) + extraWidth, InterfaceComponentRenderer.chatDisplayX, 16);
		}
	}

	@OriginalMember(owner = "client!aca", name = "a", descriptor = "(I)I")
	public static int getMinimenuCursor() {
		if (InterfaceDrag.component == null) {
			if (!MiniMenu.isOpen && MiniMenu.aClass2_Sub6_Sub3_1 != null) {
				return MiniMenu.aClass2_Sub6_Sub3_1.cursor;
			}
			@Pc(23) int mouseX = MouseRecord.mouseRecorder.getMouseX();
			@Pc(27) int mouseY = MouseRecord.mouseRecorder.getMouseY();
			@Pc(57) int hoveredIndex;
			@Pc(59) int entryIndex;
			@Pc(71) int local71;
			@Pc(139) MiniMenuEntry miniMenuEntryInner;
			if (MiniMenu.isCollapsed) {
				@Pc(134) QueueIterator queueIterator;
				if (MiniMenu.menuOpenX < mouseX && mouseX < MiniMenu.menuOpenWidth + MiniMenu.menuOpenX) {
					hoveredIndex = -1;
					for (entryIndex = 0; entryIndex < MiniMenu.coloredEntryCount; entryIndex++) {
						if (FontMetrics.useBoldFont) {
							int entryY = MiniMenu.menuOpenY + entryIndex * 16 + 33;
							if (entryY - 13 < mouseY && mouseY <= entryY + 3) {
								hoveredIndex = entryIndex;
							}
						} else {
							int entryY = entryIndex * 16 + MiniMenu.menuOpenY + 31;
							if (entryY - 13 < mouseY && entryY + 3 >= mouseY) {
								hoveredIndex = entryIndex;
							}
						}
					}
					if (hoveredIndex != -1) {
						int currentIndex = 0;
						queueIterator = new QueueIterator(MiniMenu.sortedColoredEntries);
						for (@Pc(239) ColoredMiniMenuEntry miniMenuEntry = (ColoredMiniMenuEntry) queueIterator.first(); miniMenuEntry != null; miniMenuEntry = (ColoredMiniMenuEntry) queueIterator.next()) {
							if (hoveredIndex == currentIndex++) {
								return ((MiniMenuEntry) miniMenuEntry.nodeQueue.sentinel.secondaryNext).cursor;
							}
						}
					}
				} else if (MiniMenu.activeSubmenu != null && MiniMenu.activeSubmenuX < mouseX && mouseX < MiniMenu.activeSubmenuWidth + MiniMenu.activeSubmenuX) {
					hoveredIndex = -1;
					for (entryIndex = 0; entryIndex < MiniMenu.activeSubmenu.queueSize; entryIndex++) {
						if (FontMetrics.useBoldFont) {
							int entryY = MiniMenu.activeSubmenuY + entryIndex * 16 + 33;
							if (entryY - 13 < mouseY && mouseY <= entryY + 3) {
								hoveredIndex = entryIndex;
							}
						} else {
							int entryY = entryIndex * 16 + MiniMenu.activeSubmenuY + 31;
							if (mouseY > entryY - 13 && entryY + 3 >= mouseY) {
								hoveredIndex = entryIndex;
							}
						}
					}
					if (hoveredIndex != -1) {
						int currentIndex = 0;
						queueIterator = new QueueIterator(MiniMenu.activeSubmenu.nodeQueue);
						for (miniMenuEntryInner = (MiniMenuEntry) queueIterator.first(); miniMenuEntryInner != null; miniMenuEntryInner = (MiniMenuEntry) queueIterator.next()) {
							if (currentIndex++ == hoveredIndex) {
								return miniMenuEntryInner.cursor;
							}
						}
					}
				}
			} else if (MiniMenu.menuOpenX < mouseX && mouseX < MiniMenu.menuOpenX + MiniMenu.menuOpenWidth) {
				hoveredIndex = -1;
				for (entryIndex = 0; entryIndex < MiniMenu.entryCount; entryIndex++) {
					if (FontMetrics.useBoldFont) {
						int entryY = (MiniMenu.entryCount - entryIndex - 1) * 16 + MiniMenu.menuOpenY + 33;
						if (mouseY > entryY - 13 && mouseY <= entryY + 3) {
							hoveredIndex = entryIndex;
						}
					} else {
						int entryY = MiniMenu.menuOpenY + (-entryIndex + (MiniMenu.entryCount - 1)) * 16 + 31;
						if (mouseY > entryY - 13 && entryY + 3 >= mouseY) {
							hoveredIndex = entryIndex;
						}
					}
				}
				if (hoveredIndex != -1) {
					int currentIndex = 0;
					@Pc(357) DequeIterator dequeIterator = new DequeIterator(MiniMenu.entryList);
					for (miniMenuEntryInner = (MiniMenuEntry) dequeIterator.first(); miniMenuEntryInner != null; miniMenuEntryInner = (MiniMenuEntry) dequeIterator.next()) {
						if (hoveredIndex == currentIndex++) {
							return miniMenuEntryInner.cursor;
						}
					}
				}
			}
		}
		return -1;
	}

	@OriginalMember(owner = "client!cj", name = "a", descriptor = "(BLclient!db;)Ljava/lang/String;")
	public static String getTextFrom(@OriginalArg(1) MiniMenuEntry miniMenuEntryInner) {
		if (miniMenuEntryInner.targetName == null || miniMenuEntryInner.targetName.length() == 0) {
			return miniMenuEntryInner.op == null || miniMenuEntryInner.op.length() <= 0 ? miniMenuEntryInner.opBase : miniMenuEntryInner.opBase + LocalizedText.aClass351_34.get(ClientState.language) + miniMenuEntryInner.op;
		} else if (miniMenuEntryInner.op == null || miniMenuEntryInner.op.length() <= 0) {
			return miniMenuEntryInner.opBase + LocalizedText.aClass351_34.get(ClientState.language) + miniMenuEntryInner.targetName;
		} else {
			return miniMenuEntryInner.opBase + LocalizedText.aClass351_34.get(ClientState.language) + miniMenuEntryInner.op + LocalizedText.aClass351_34.get(ClientState.language) + miniMenuEntryInner.targetName;
		}
	}

	@OriginalMember(owner = "client!vv", name = "a", descriptor = "(BZLclient!ke;)V")
	public static void playerHovered(@OriginalArg(1) boolean onDifferentPlane, @OriginalArg(2) PlayerEntity playerEntity) {
		if (MiniMenu.entryCount >= 400) {
			return;
		}
		if (PlayerUpdate.localPlayer != playerEntity) {
			@Pc(184) String string;
			@Pc(108) int local108;
			if (playerEntity.elo == 0) {
				@Pc(61) boolean combatColorDisplayed = true;
				if (PlayerUpdate.localPlayer.cbLevelRelated != -1 && playerEntity.cbLevelRelated != -1) {
					@Pc(85) int maxCombatLevel = PlayerUpdate.localPlayer.combatLevel > playerEntity.combatLevel ? PlayerUpdate.localPlayer.combatLevel : playerEntity.combatLevel;
					@Pc(96) int minCbLevelRelated = PlayerUpdate.localPlayer.cbLevelRelated >= playerEntity.cbLevelRelated ? playerEntity.cbLevelRelated : PlayerUpdate.localPlayer.cbLevelRelated;
					int maxLevelDifference = maxCombatLevel * 10 / 100 + minCbLevelRelated + 5;
					@Pc(115) int levelDifference = PlayerUpdate.localPlayer.combatLevel - playerEntity.combatLevel;
					if (levelDifference < 0) {
						levelDifference = -levelDifference;
					}
					if (levelDifference > maxLevelDifference) {
						combatColorDisplayed = false;
					}
				}
				@Pc(145) String string2 = Game.STELLAR_DAWN == Game.CURRENT_GAME ? LocalizedText.aClass351_32.get(ClientState.language) : LocalizedText.aClass351_30.get(ClientState.language);
				if (playerEntity.cbLevelWithSumm <= playerEntity.combatLevel) {
					string = playerEntity.getUsernameWithTitle() + (combatColorDisplayed ? MiniMenu.getCombatLevelColour(PlayerUpdate.localPlayer.combatLevel, playerEntity.combatLevel) : "<col=ffffff>") + " (" + string2 + playerEntity.combatLevel + ")";
				} else {
					string = playerEntity.getUsernameWithTitle() + (combatColorDisplayed ? MiniMenu.getCombatLevelColour(PlayerUpdate.localPlayer.combatLevel, playerEntity.combatLevel) : "<col=ffffff>") + " (" + string2 + playerEntity.combatLevel + "+" + (playerEntity.cbLevelWithSumm - playerEntity.combatLevel) + ")";
				}
			} else if (playerEntity.elo == -1) {
				string = playerEntity.getUsernameWithTitle();
			} else {
				string = playerEntity.getUsernameWithTitle() + " (" + LocalizedText.aClass351_31.get(ClientState.language) + playerEntity.elo + ")";
			}
			if (MiniMenu.interfaceSelectionActive && !onDifferentPlane && (MiniMenu.useTargetMask & 0x8) != 0) {
				MiniMenu.addEntry(false, MiniMenu.useOnTarget + " -> <col=ffffff>" + string, 0, false, 0, -1, true, MiniMenuAction.TGT_PLAYER, (long) playerEntity.playerIndex, MiniMenu.useOnType, (long) playerEntity.playerIndex, MiniMenu.useOnCursor);
			}
			if (onDifferentPlane) {
				MiniMenu.addEntry(true, "", 0, false, 0, 0, false, MiniMenuAction.LABEL, (long) playerEntity.playerIndex, "<col=cccccc>" + string, 0L, -1);
			} else {
				for (@Pc(333) int i = 7; i >= 0; i--) {
					if (MiniMenu.PLAYER_OPTIONS[i] != null) {
						@Pc(341) short clickTypeOffset = 0;
						if (Game.CURRENT_GAME == Game.RUNESCAPE && MiniMenu.PLAYER_OPTIONS[i].equalsIgnoreCase(LocalizedText.aClass351_25.get(ClientState.language))) {
							if (PlayerUpdate.localPlayer.combatLevel < playerEntity.combatLevel) {
								clickTypeOffset = 2000;
							}
							if (PlayerUpdate.localPlayer.teamId != 0 && playerEntity.teamId != 0) {
								if (PlayerUpdate.localPlayer.teamId == playerEntity.teamId) {
									clickTypeOffset = 2000;
								} else {
									clickTypeOffset = 0;
								}
							}
						} else if (MiniMenu.PLAYER_OPTION_REDUCED_PRIORITY[i]) {
							clickTypeOffset = 2000;
						}
						@Pc(396) short clickType = (short) (clickTypeOffset + MiniMenu.PLAYER_OPTION_CLICK_TYPES[i]);
						int cursor = MiniMenu.PLAYER_OPTION_CURSORS[i] == -1 ? CursorManager.defaultAttackCursor : MiniMenu.PLAYER_OPTION_CURSORS[i];
						MiniMenu.addEntry(false, "<col=ffffff>" + string, 0, false, 0, -1, true, clickType, (long) playerEntity.playerIndex, MiniMenu.PLAYER_OPTIONS[i], (long) playerEntity.playerIndex, cursor);
					}
				}
			}
			if (!onDifferentPlane) {
				for (@Pc(450) MiniMenuEntry miniMenuEntryInner = (MiniMenuEntry) MiniMenu.entryList.first(); miniMenuEntryInner != null; miniMenuEntryInner = (MiniMenuEntry) MiniMenu.entryList.next()) {
					if (miniMenuEntryInner.clickType == MiniMenuAction.WALK) {
						miniMenuEntryInner.targetName = "<col=ffffff>" + string;
						return;
					}
				}
			}
		} else if (MiniMenu.interfaceSelectionActive && (MiniMenu.useTargetMask & 0x10) != 0) {
			MiniMenu.addEntry(false, MiniMenu.useOnTarget + " -> <col=ffffff>" + LocalizedText.aClass351_39.get(ClientState.language), 0, false, 0, -1, true, MiniMenuAction.TGT_SELF, (long) playerEntity.playerIndex, MiniMenu.useOnType, 0L, MiniMenu.useOnCursor);
		}
	}

	@OriginalMember(owner = "client!qt", name = "e", descriptor = "(I)V")
	public static void addCancelOption() {
		MiniMenu.cancelOption = new MiniMenuEntry(LocalizedText.CANCEL.get(ClientState.language), "", Cursor.interfaceCursorId, MiniMenuAction.CANCEL, -1, 0L, 0, 0, true, false, 0L, true);
	}
	@OriginalMember(owner = "client!vga", name = "e", descriptor = "I")
	public static int deferredClickType = 0;

	@OriginalMember(owner = "client!tba", name = "b", descriptor = "Lclient!vq;")
	public static ColoredMiniMenuEntry activeSubmenu = null;

	@OriginalMember(owner = "client!lk", name = "z", descriptor = "I")
	public static int lastChatMessageTick = 0;

	@OriginalMember(owner = "client!nk", name = "w", descriptor = "Z")
	public static boolean isCollapsed = false;

	@OriginalMember(owner = "client!dj", name = "f", descriptor = "I")
	public static int maxVisibleEntries = -1;

	@OriginalMember(owner = "client!jia", name = "M", descriptor = "Lclient!db;")
	public static MiniMenuEntry cancelOption;

	@OriginalMember(owner = "client!oaa", name = "g", descriptor = "Lclient!db;")
	public static MiniMenuEntry deferredEntry;

	@OriginalMember(owner = "client!uh", name = "d", descriptor = "Lclient!da;")
	public static AbstractFontRenderer minimenuHeader;
	@OriginalMember(owner = "client!kk", name = "e", descriptor = "I")
	public static int entryCount = 0;
	@OriginalMember(owner = "client!sn", name = "h", descriptor = "Lclient!mn;")
	public static final Queue sortedColoredEntries = new Queue();

	@OriginalMember(owner = "client!db", name = "H", descriptor = "Lclient!eq;")
	public static final IterableHashTable coloredEntryMap = new IterableHashTable(16);

	@OriginalMember(owner = "client!uea", name = "c", descriptor = "Lclient!jr;")
	public static final LRUCache coloredEntryCache = new LRUCache(30);

	@OriginalMember(owner = "client!hl", name = "I", descriptor = "Lclient!uh;")
	public static final Deque entryList = new Deque();
	@OriginalMember(owner = "client!gr", name = "h", descriptor = "I")
	public static int coloredEntryCount = 0;

	@OriginalMember(owner = "client!jk", name = "e", descriptor = "(B)V")
	public static void closeSubmenu() {
		if (MiniMenu.activeSubmenu != null) {
			MiniMenu.activeSubmenu = null;
			InterfaceRectangles.inboundsComponentToTop(MiniMenu.activeSubmenuX, MiniMenu.activeSubmenuWidth, MiniMenu.activeSubmenuY, MiniMenu.activeSubmenuHeight);
		}
	}

	@OriginalMember(owner = "client!k", name = "a", descriptor = "I")
	public static int menuOpenWidth;

	@OriginalMember(owner = "client!gq", name = "b", descriptor = "(B)Z")
	public static boolean hasEntries() {
		return MiniMenu.entryCount > 0;
	}

	@OriginalMember(owner = "client!ft", name = "g", descriptor = "(I)V")
	public static void reset() {
		for (@Pc(10) ColoredMiniMenuEntry miniMenuEntry = (ColoredMiniMenuEntry) MiniMenu.sortedColoredEntries.first(); miniMenuEntry != null; miniMenuEntry = (ColoredMiniMenuEntry) MiniMenu.sortedColoredEntries.next()) {
			if (miniMenuEntry.queueSize > 1) {
				miniMenuEntry.queueSize = 0;
				MiniMenu.coloredEntryCache.put(miniMenuEntry, ((MiniMenuEntry) miniMenuEntry.nodeQueue.sentinel.secondaryNext).bitpacked);
				miniMenuEntry.nodeQueue.clear();
			}
		}
		MiniMenu.coloredEntryCount = 0;
		MiniMenu.entryCount = 0;
		MiniMenu.entryList.clear();
		MiniMenu.coloredEntryMap.clear();
		MiniMenu.sortedColoredEntries.clear();
		MiniMenu.isOpen = false;
	}

	@OriginalMember(owner = "client!hc", name = "v", descriptor = "I")
	public static int activeSubmenuX;
	@OriginalMember(owner = "client!me", name = "a", descriptor = "(Lclient!vq;IZI)V")
	public static void openSubmenu(@OriginalArg(0) ColoredMiniMenuEntry miniMenuEntry, @OriginalArg(1) int mouseY, @OriginalArg(3) int anchorY) {
		if (!MiniMenu.isOpen) {
			return;
		}
		@Pc(10) int width = 0;
		@Pc(22) int local22;
		for (@Pc(16) MiniMenuEntry miniMenuEntryInner = (MiniMenuEntry) miniMenuEntry.nodeQueue.first(); miniMenuEntryInner != null; miniMenuEntryInner = (MiniMenuEntry) miniMenuEntry.nodeQueue.next()) {
			int entryWidth = MiniMenu.getWidthForEntryWithQuests(miniMenuEntryInner);
			if (width < entryWidth) {
				width = entryWidth;
			}
		}
		width += 8;
		MiniMenu.activeSubmenuHeight = miniMenuEntry.queueSize * 16 + (FontMetrics.useBoldFont ? 26 : 22);
		int submenuHeight = miniMenuEntry.queueSize * 16 + 21;
		@Pc(66) int x = MiniMenu.menuOpenWidth + MiniMenu.menuOpenX;
		if (width + x > GameShell.screenCanvasWidth) {
			x = MiniMenu.menuOpenX - width;
		}
		if (x < 0) {
			x = 0;
		}
		@Pc(89) int headerOffset = FontMetrics.useBoldFont ? 33 : 31;
		@Pc(96) int y = anchorY + 13 - headerOffset;
		if (GameShell.screenCanvasHeight < y + submenuHeight) {
			y = GameShell.screenCanvasHeight - submenuHeight;
		}
		MiniMenu.activeSubmenuX = x;
		if (y < 0) {
			y = 0;
		}
		MiniMenu.activeSubmenu = miniMenuEntry;
		MiniMenu.activeSubmenuWidth = width;
		MiniMenu.activeSubmenuY = y;
	}

	@OriginalMember(owner = "client!tq", name = "a", descriptor = "(ILclient!db;)V")
	public static void removeEntry(@OriginalArg(1) MiniMenuEntry miniMenuEntryInner) {
		if (MiniMenu.isOpen) {
			return;
		}
		miniMenuEntryInner.unlink();
		MiniMenu.entryCount--;
		if (!miniMenuEntryInner.isUncategorized) {
			@Pc(82) long key = miniMenuEntryInner.bitpacked;
			@Pc(88) ColoredMiniMenuEntry miniMenuEntry;
			for (miniMenuEntry = (ColoredMiniMenuEntry) MiniMenu.coloredEntryMap.get(key); miniMenuEntry != null && !miniMenuEntry.tagStart.equals(miniMenuEntryInner.op); miniMenuEntry = (ColoredMiniMenuEntry) MiniMenu.coloredEntryMap.nextWithKey()) {
			}
			if (miniMenuEntry != null && miniMenuEntry.removeEntry(miniMenuEntryInner)) {
				MiniMenu.insertSortedEntry(miniMenuEntry);
			}
			return;
		}
		for (@Pc(27) ColoredMiniMenuEntry miniMenuEntry2 = (ColoredMiniMenuEntry) MiniMenu.sortedColoredEntries.first(); miniMenuEntry2 != null; miniMenuEntry2 = (ColoredMiniMenuEntry) MiniMenu.sortedColoredEntries.next()) {
			if (miniMenuEntry2.tagStart.equals(miniMenuEntryInner.op)) {
				@Pc(37) boolean found = false;
				for (@Pc(43) MiniMenuEntry miniMenuEntryInner2 = (MiniMenuEntry) miniMenuEntry2.nodeQueue.first(); miniMenuEntryInner2 != null; miniMenuEntryInner2 = (MiniMenuEntry) miniMenuEntry2.nodeQueue.next()) {
					if (miniMenuEntryInner == miniMenuEntryInner2) {
						if (miniMenuEntry2.removeEntry(miniMenuEntryInner)) {
							MiniMenu.insertSortedEntry(miniMenuEntry2);
						}
						found = true;
						break;
					}
				}
				if (found) {
					return;
				}
			}
		}
	}
	@OriginalMember(owner = "client!gt", name = "a", descriptor = "(B)Ljava/lang/String;")
	public static String getActionText() {
		return MiniMenu.isOpen || MiniMenu.aClass2_Sub6_Sub3_1 == null ? "" : MiniMenu.aClass2_Sub6_Sub3_1.opBase;
	}

	@OriginalMember(owner = "client!bja", name = "b", descriptor = "(B)Ljava/lang/String;")
	public static String getColoredNameText() {
		if (MiniMenu.isOpen || MiniMenu.aClass2_Sub6_Sub3_1 == null) {
			return "";
		} else if ((MiniMenu.aClass2_Sub6_Sub3_1.op == null || MiniMenu.aClass2_Sub6_Sub3_1.op.length() == 0) && MiniMenu.aClass2_Sub6_Sub3_1.targetName != null && MiniMenu.aClass2_Sub6_Sub3_1.targetName.length() > 0) {
			return MiniMenu.aClass2_Sub6_Sub3_1.targetName;
		} else {
			return MiniMenu.aClass2_Sub6_Sub3_1.op;
		}
	}
	@OriginalMember(owner = "client!tba", name = "a", descriptor = "(BLclient!vq;)V")
	public static void insertSortedEntry(@OriginalArg(1) ColoredMiniMenuEntry miniMenuEntry) {
		miniMenuEntry.unlinkSecondary();
		@Pc(18) boolean inserted = false;
		for (@Pc(23) ColoredMiniMenuEntry miniMenuEntry2 = (ColoredMiniMenuEntry) MiniMenu.sortedColoredEntries.first(); miniMenuEntry2 != null; miniMenuEntry2 = (ColoredMiniMenuEntry) MiniMenu.sortedColoredEntries.next()) {
			if (MiniMenu.hasHigherPriority(miniMenuEntry2.getAction(), miniMenuEntry.getAction())) {
				CacheableNode.insertSecondaryBefore(miniMenuEntry2, miniMenuEntry);
				inserted = true;
				break;
			}
		}
		if (!inserted) {
			MiniMenu.sortedColoredEntries.insert(miniMenuEntry);
		}
	}

	@OriginalMember(owner = "client!sk", name = "a", descriptor = "(BLclient!ha;)V")
	public static void renderOpenMenuSimple(@OriginalArg(1) Toolkit toolkit) {
		@Pc(7) int offsetX = 0;
		@Pc(9) int offsetY = 0;
		if (OrthoMode.orthoEnabled) {
			offsetX = OrthoMode.getOrthoOffsetX();
			offsetY = OrthoMode.getOrthoDrawOffsetY();
		}
		MiniMenu.drawSimpleMenuBox(MiniMenu.menuOpenWidth, MiniMenu.menuOpenY + offsetY, toolkit, MiniMenu.menuOpenX + offsetX, InterfaceComponentRenderer.menuHeight);
		MiniMenu.minimenuHeader.drawString(LocalizedText.CHOOSE_OPTION.get(ClientState.language), -10660793, offsetY + MiniMenu.menuOpenY + 14, offsetX + MiniMenu.menuOpenX + 3, -1);
		@Pc(64) int mouseX = MouseRecord.mouseRecorder.getMouseX() + offsetX;
		@Pc(70) int mouseY = offsetY + MouseRecord.mouseRecorder.getMouseY();
		@Pc(74) int entryIndex;
		@Pc(91) int entryY;
		if (MiniMenu.isCollapsed) {
			entryIndex = 0;
			for (@Pc(79) ColoredMiniMenuEntry miniMenuEntry = (ColoredMiniMenuEntry) MiniMenu.sortedColoredEntries.first(); miniMenuEntry != null; miniMenuEntry = (ColoredMiniMenuEntry) MiniMenu.sortedColoredEntries.next()) {
				entryY = offsetY + MiniMenu.menuOpenY + entryIndex * 16 + 31;
				entryIndex++;
				if (miniMenuEntry.queueSize == 1) {
					MiniMenu.drawMenuEntry(offsetY + MiniMenu.menuOpenY, -256, entryY, toolkit, (MiniMenuEntry) miniMenuEntry.nodeQueue.sentinel.secondaryNext, MiniMenu.menuOpenWidth, mouseY, -1, InterfaceComponentRenderer.menuHeight, MiniMenu.menuOpenX + offsetX, mouseX);
				} else {
					MiniMenu.drawColoredMenuEntry(-1, entryY, miniMenuEntry, mouseX, MiniMenu.menuOpenWidth, -256, mouseY, toolkit, MiniMenu.menuOpenY + offsetY, MiniMenu.menuOpenX - -offsetX, InterfaceComponentRenderer.menuHeight);
				}
			}
			if (MiniMenu.activeSubmenu != null) {
				MiniMenu.drawSimpleMenuBox(MiniMenu.activeSubmenuWidth, MiniMenu.activeSubmenuY, toolkit, MiniMenu.activeSubmenuX, MiniMenu.activeSubmenuHeight);
				entryIndex = 0;
				MiniMenu.minimenuHeader.drawString(MiniMenu.activeSubmenu.tagStart, -10660793, MiniMenu.activeSubmenuY + 14, MiniMenu.activeSubmenuX + 3, -1);
				for (@Pc(179) MiniMenuEntry miniMenuEntryInner = (MiniMenuEntry) MiniMenu.activeSubmenu.nodeQueue.first(); miniMenuEntryInner != null; miniMenuEntryInner = (MiniMenuEntry) MiniMenu.activeSubmenu.nodeQueue.next()) {
					@Pc(189) int submenuEntryY = MiniMenu.activeSubmenuY + entryIndex * 16 + 31;
					entryIndex++;
					MiniMenu.drawMenuEntry(MiniMenu.activeSubmenuY, -256, submenuEntryY, toolkit, miniMenuEntryInner, MiniMenu.activeSubmenuWidth, mouseY, -1, MiniMenu.activeSubmenuHeight, MiniMenu.activeSubmenuX, mouseX);
				}
				InterfaceRectangles.markRectanglesForFlip(MiniMenu.activeSubmenuWidth, MiniMenu.activeSubmenuY, MiniMenu.activeSubmenuX, MiniMenu.activeSubmenuHeight);
			}
		} else {
			entryIndex = 0;
			for (@Pc(226) MiniMenuEntry miniMenuEntryInner2 = (MiniMenuEntry) MiniMenu.entryList.first(); miniMenuEntryInner2 != null; miniMenuEntryInner2 = (MiniMenuEntry) MiniMenu.entryList.next()) {
				entryY = offsetY + MiniMenu.menuOpenY + (-entryIndex + -1 + MiniMenu.entryCount) * 16 + 31;
				MiniMenu.drawMenuEntry(offsetY + MiniMenu.menuOpenY, -256, entryY, toolkit, miniMenuEntryInner2, MiniMenu.menuOpenWidth, mouseY, -1, InterfaceComponentRenderer.menuHeight, MiniMenu.menuOpenX + offsetX, mouseX);
				entryIndex++;
			}
		}
		InterfaceRectangles.markRectanglesForFlip(MiniMenu.menuOpenWidth, MiniMenu.menuOpenY + offsetY, MiniMenu.menuOpenX + offsetX, InterfaceComponentRenderer.menuHeight);
	}
	@OriginalMember(owner = "client!bg", name = "M", descriptor = "I")
	public static int activeSubmenuWidth;

	@OriginalMember(owner = "client!kaa", name = "x", descriptor = "Lclient!db;")
	public static MiniMenuEntry aClass2_Sub6_Sub3_3;

	@OriginalMember(owner = "client!lr", name = "I", descriptor = "I")
	public static int menuOpenY;
	@OriginalMember(owner = "client!ui", name = "k", descriptor = "I")
	public static int menuHighlightColour;

	@OriginalMember(owner = "client!ui", name = "m", descriptor = "I")
	public static int menuBackgroundColour;

	@OriginalMember(owner = "client!pg", name = "j", descriptor = "Lclient!uh;")
	public static final Deque aClass341_46 = new Deque();
	@OriginalMember(owner = "client!lm", name = "x", descriptor = "I")
	public static int menuHighlightTransparency;

	@OriginalMember(owner = "client!kga", name = "g", descriptor = "[Z")
	public static final boolean[] PLAYER_OPTION_REDUCED_PRIORITY = new boolean[8];

	@OriginalMember(owner = "client!qt", name = "e", descriptor = "[I")
	public static final int[] tooltipTextBounds = new int[4];

	@OriginalMember(owner = "client!ce", name = "q", descriptor = "Z")
	public static boolean isOpen = false;

	@OriginalMember(owner = "client!lj", name = "z", descriptor = "I")
	public static int menuHoverTextColour;

	@OriginalMember(owner = "client!vs", name = "a", descriptor = "(ILclient!vq;)I")
	public static int getPrefixTextWidth(@OriginalArg(1) ColoredMiniMenuEntry miniMenuEntry) {
		@Pc(14) String string = MiniMenu.formatTag(miniMenuEntry);
		return FontMetrics.defaultFontMetrics.getTextWidth(string, StaticMedia.chatIcons);
	}

	@OriginalMember(owner = "client!pu", name = "g", descriptor = "I")
	public static int activeSubmenuHeight;

	@OriginalMember(owner = "client!g", name = "b", descriptor = "I")
	public static int activeSubmenuY;

	@OriginalMember(owner = "client!nb", name = "b", descriptor = "Z")
	public static boolean shiftClickEnabled = false;
	@OriginalMember(owner = "client!cj", name = "f", descriptor = "Lclient!db;")
	public static MiniMenuEntry aClass2_Sub6_Sub3_1;

	@OriginalMember(owner = "client!r", name = "C", descriptor = "Z")
	public static boolean interfaceSelectionActive = false;

	@OriginalMember(owner = "client!va", name = "b", descriptor = "(I)V")
	public static void resetMiniMenuSprites() {
		StaticMedia.menuLeftSideBorderSprite = null;
		StaticMedia.menuBottomLeftCornerSprite = null;
		StaticMedia.menuBottomBorderSprite = null;
		StaticMedia.chatIcons = null;
		StaticMedia.menuHeaderLeftCornerSprite = null;
		MiniMenu.menuHeaderRightCornerSprite = null;
		MiniMenu.menuHeaderSprite = null;
		StaticMedia.menuRightSideBorderSprite = null;
		StaticMedia.menuBottomRightCornerSprite = null;
	}

	@OriginalMember(owner = "client!cf", name = "a", descriptor = "(BI)Z")
	public static boolean isUseOnClickType(@OriginalArg(1) int clickType) {
		return clickType == 15 || clickType == 2 || clickType == 30 || clickType == 49 || clickType == 51 || clickType == 50 || clickType == 6;
	}

	@OriginalMember(owner = "client!gh", name = "Pb", descriptor = "I")
	public static int useOnCursor;

	@OriginalMember(owner = "client!jt", name = "v", descriptor = "I")
	public static int menuOpenX;

	@OriginalMember(owner = "client!sk", name = "g", descriptor = "I")
	public static int menuHeaderSpriteId;

	@OriginalMember(owner = "client!ug", name = "D", descriptor = "I")
	public static int menuTextColour;

	@OriginalMember(owner = "client!ti", name = "d", descriptor = "(II)Z")
	public static boolean isComponentClickType(@OriginalArg(1) int clickType) {
		return clickType == MiniMenuAction.IF_BUTTONX1 || clickType == MiniMenuAction.TGT_BUTTON || clickType == MiniMenuAction.IF_BUTTONX2 || clickType == MiniMenuAction.IF_BUTTONT || clickType == MiniMenuAction.PAUSE_BUTTON;
	}

	@OriginalMember(owner = "client!di", name = "a", descriptor = "(B)V")
	public static void close() {
		MiniMenu.closeSubmenu();
		MiniMenu.isOpen = false;
		InterfaceRectangles.inboundsComponentToTop(MiniMenu.menuOpenX, MiniMenu.menuOpenWidth, MiniMenu.menuOpenY, InterfaceComponentRenderer.menuHeight);
	}

	@OriginalMember(owner = "client!ua", name = "Zb", descriptor = "[S")
	public static final short[] PLAYER_OPTION_CLICK_TYPES = new short[] { MiniMenuAction.OPPLAYER1, MiniMenuAction.OPPLAYER2, MiniMenuAction.OPPLAYER3, MiniMenuAction.OPPLAYER4, MiniMenuAction.OPPLAYER5, MiniMenuAction.OPPLAYER6, MiniMenuAction.OPPLAYER7, MiniMenuAction.OPPLAYER8 };

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(BLclient!db;)V")
	public static void addEntry(@OriginalArg(1) MiniMenuEntry miniMenuEntryInner) {
		if (miniMenuEntryInner == null) {
			return;
		}
		MiniMenu.entryList.addLast(miniMenuEntryInner);
		MiniMenu.entryCount++;
		@Pc(37) ColoredMiniMenuEntry miniMenuEntry;
		if (miniMenuEntryInner.isUncategorized || "".equals(miniMenuEntryInner.op)) {
			miniMenuEntry = new ColoredMiniMenuEntry(miniMenuEntryInner.op);
			MiniMenu.coloredEntryCount++;
		} else {
			@Pc(45) long key = miniMenuEntryInner.bitpacked;
			for (miniMenuEntry = (ColoredMiniMenuEntry) MiniMenu.coloredEntryMap.get(key); miniMenuEntry != null && !miniMenuEntry.tagStart.equals(miniMenuEntryInner.op); miniMenuEntry = (ColoredMiniMenuEntry) MiniMenu.coloredEntryMap.nextWithKey()) {
			}
			if (miniMenuEntry == null) {
				miniMenuEntry = (ColoredMiniMenuEntry) MiniMenu.coloredEntryCache.get(key);
				if (miniMenuEntry != null && !miniMenuEntry.tagStart.equals(miniMenuEntryInner.op)) {
					miniMenuEntry = null;
				}
				if (miniMenuEntry == null) {
					miniMenuEntry = new ColoredMiniMenuEntry(miniMenuEntryInner.op);
				}
				MiniMenu.coloredEntryMap.put(key, miniMenuEntry);
				MiniMenu.coloredEntryCount++;
			}
		}
		if (miniMenuEntry.addEntry(miniMenuEntryInner)) {
			MiniMenu.insertSortedEntry(miniMenuEntry);
		}
	}

	@OriginalMember(owner = "client!tw", name = "a", descriptor = "(IZI)Ljava/lang/String;")
	public static String getCombatLevelColour(@OriginalArg(0) int myCombatLevel, @OriginalArg(2) int targetCombatLevel) {
		@Pc(13) int diff = myCombatLevel - targetCombatLevel;
		if (diff < -9) {
			return "<col=ff0000>";
		} else if (diff < -6) {
			return "<col=ff3000>";
		} else if (diff < -3) {
			return "<col=ff7000>";
		} else if (diff < 0) {
			return "<col=ffb000>";
		} else if (diff > 9) {
			return "<col=00ff00>";
		} else if (diff > 6) {
			return "<col=40ff00>";
		} else if (diff > 3) {
			return "<col=80ff00>";
		} else if (diff > 0) {
			return "<col=c0ff00>";
		} else {
			return "<col=ffff00>";
		}
	}

	@OriginalMember(owner = "client!rba", name = "a", descriptor = "(IILclient!ha;IIZII)V")
	public static void drawSimpleMenuBox(@OriginalArg(0) int width, @OriginalArg(1) int y, @OriginalArg(2) Toolkit toolkit, @OriginalArg(3) int x, @OriginalArg(6) int height) {
		toolkit.fillRect(width, x, y, height, -10660793);
		toolkit.fillRect(width - 2, x + 1, y + 1, 16, -16777216);
		toolkit.drawOpaqueRectangle(width - 2, y + 18, -16777216, x + 1, height + -19);
	}

	@OriginalMember(owner = "client!tk", name = "f", descriptor = "Lclient!mi;")
	public static NativeSprite menuHeaderSprite;

	@OriginalMember(owner = "client!jt", name = "a", descriptor = "(Lclient!ha;I)V")
	public static void renderOpenMenuStyled(@OriginalArg(0) Toolkit toolkit) {
		@Pc(7) int offsetX = 0;
		@Pc(9) int offsetY = 0;
		if (OrthoMode.orthoEnabled) {
			offsetX = OrthoMode.getOrthoOffsetX();
			offsetY = OrthoMode.getOrthoDrawOffsetY();
		}
		@Pc(21) int menuX = offsetX + menuOpenX;
		@Pc(25) int menuY = MiniMenu.menuOpenY + offsetY;
		@Pc(27) int menuWidth = MiniMenu.menuOpenWidth;
		@Pc(31) int menuHeight = InterfaceComponentRenderer.menuHeight - 3;
		MiniMenu.drawStyledMenuHeader(MiniMenu.menuOpenWidth, offsetX + menuOpenX, toolkit, LocalizedText.CHOOSE_OPTION.get(ClientState.language), offsetY + MiniMenu.menuOpenY, InterfaceComponentRenderer.menuHeight);
		@Pc(57) int mouseX = offsetX + MouseRecord.mouseRecorder.getMouseX();
		@Pc(68) int mouseY = offsetY + MouseRecord.mouseRecorder.getMouseY();
		@Pc(72) int entryIndex;
		@Pc(77) MiniMenuEntry miniMenuEntryInner;
		@Pc(93) int entryY;
		@Pc(283) int submenuEntryY;
		@Pc(164) ColoredMiniMenuEntry miniMenuEntry;
		@Pc(271) MiniMenuEntry miniMenuEntryInner2;
		if (MiniMenu.isCollapsed) {
			entryIndex = 0;
			for (miniMenuEntry = (ColoredMiniMenuEntry) MiniMenu.sortedColoredEntries.first(); miniMenuEntry != null; miniMenuEntry = (ColoredMiniMenuEntry) MiniMenu.sortedColoredEntries.next()) {
				entryY = entryIndex * 16 + menuY + 33;
				if (mouseX > offsetX + menuOpenX && mouseX < MiniMenu.menuOpenWidth + offsetX + menuOpenX && entryY - 13 < mouseY && mouseY < entryY + 4 && (miniMenuEntry.queueSize > 1 || ((MiniMenuEntry) miniMenuEntry.nodeQueue.sentinel.secondaryNext).showOtherLevelIcon)) {
					toolkit.aa(menuOpenX + offsetX, entryY + -12, MiniMenu.menuOpenWidth, 16, 255 - MiniMenu.menuHighlightTransparency << 24 | MiniMenu.menuHighlightColour, 1);
				}
				entryIndex++;
			}
			if (MiniMenu.activeSubmenu != null) {
				entryIndex = 0;
				MiniMenu.drawStyledMenuHeader(MiniMenu.activeSubmenuWidth, MiniMenu.activeSubmenuX, toolkit, MiniMenu.activeSubmenu.tagStart, MiniMenu.activeSubmenuY, MiniMenu.activeSubmenuHeight);
				for (miniMenuEntryInner2 = (MiniMenuEntry) MiniMenu.activeSubmenu.nodeQueue.first(); miniMenuEntryInner2 != null; miniMenuEntryInner2 = (MiniMenuEntry) MiniMenu.activeSubmenu.nodeQueue.next()) {
					submenuEntryY = entryIndex * 16 + MiniMenu.activeSubmenuY + 20 + 13;
					if (MiniMenu.activeSubmenuX < mouseX && MiniMenu.activeSubmenuX + MiniMenu.activeSubmenuWidth > mouseX && mouseY > submenuEntryY - 13 && submenuEntryY + 4 > mouseY && miniMenuEntryInner2.showOtherLevelIcon) {
						toolkit.aa(MiniMenu.activeSubmenuX, submenuEntryY - 12, MiniMenu.activeSubmenuWidth, 16, 255 - MiniMenu.menuHighlightTransparency << 24 | MiniMenu.menuHighlightColour, 1);
					}
					entryIndex++;
				}
				MiniMenu.drawStyledMenuBorder(MiniMenu.activeSubmenuX, MiniMenu.activeSubmenuWidth, MiniMenu.activeSubmenuHeight, MiniMenu.activeSubmenuY, toolkit);
			}
		} else {
			entryIndex = 0;
			for (miniMenuEntryInner = (MiniMenuEntry) MiniMenu.entryList.first(); miniMenuEntryInner != null; miniMenuEntryInner = (MiniMenuEntry) MiniMenu.entryList.next()) {
				entryY = (MiniMenu.entryCount - entryIndex - 1) * 16 + menuY + 13 + 20;
				if (menuOpenX + offsetX < mouseX && menuOpenX + offsetX + MiniMenu.menuOpenWidth > mouseX && entryY - 13 < mouseY && entryY + 4 > mouseY && miniMenuEntryInner.showOtherLevelIcon) {
					toolkit.aa(offsetX + menuOpenX, entryY + -12, MiniMenu.menuOpenWidth, 16, 255 - MiniMenu.menuHighlightTransparency << 24 | MiniMenu.menuHighlightColour, 1);
				}
				entryIndex++;
			}
		}
		MiniMenu.drawStyledMenuBorder(menuOpenX + offsetX, MiniMenu.menuOpenWidth, InterfaceComponentRenderer.menuHeight, MiniMenu.menuOpenY + offsetY, toolkit);
		if (MiniMenu.isCollapsed) {
			entryIndex = 0;
			for (miniMenuEntry = (ColoredMiniMenuEntry) MiniMenu.sortedColoredEntries.first(); miniMenuEntry != null; miniMenuEntry = (ColoredMiniMenuEntry) MiniMenu.sortedColoredEntries.next()) {
				entryY = offsetY + MiniMenu.menuOpenY + entryIndex * 16 + 13 + 20;
				entryIndex++;
				if (miniMenuEntry.queueSize == 1) {
					MiniMenu.drawMenuEntry(MiniMenu.menuOpenY + offsetY, MiniMenu.menuHoverTextColour | 0xFF000000, entryY, toolkit, (MiniMenuEntry) miniMenuEntry.nodeQueue.sentinel.secondaryNext, MiniMenu.menuOpenWidth, mouseY, MiniMenu.menuTextColour | 0xFF000000, InterfaceComponentRenderer.menuHeight, menuOpenX + offsetX, mouseX);
				} else {
					MiniMenu.drawColoredMenuEntry(MiniMenu.menuTextColour | 0xFF000000, entryY, miniMenuEntry, mouseX, MiniMenu.menuOpenWidth, MiniMenu.menuHoverTextColour | 0xFF000000, mouseY, toolkit, MiniMenu.menuOpenY + offsetY, offsetX + menuOpenX, InterfaceComponentRenderer.menuHeight);
				}
			}
			if (MiniMenu.activeSubmenu != null) {
				entryIndex = 0;
				for (miniMenuEntryInner2 = (MiniMenuEntry) MiniMenu.activeSubmenu.nodeQueue.first(); miniMenuEntryInner2 != null; miniMenuEntryInner2 = (MiniMenuEntry) MiniMenu.activeSubmenu.nodeQueue.next()) {
					submenuEntryY = entryIndex * 16 + MiniMenu.activeSubmenuY + 13 + 20;
					entryIndex++;
					MiniMenu.drawMenuEntry(MiniMenu.activeSubmenuY, MiniMenu.menuHoverTextColour | 0xFF000000, submenuEntryY, toolkit, miniMenuEntryInner2, MiniMenu.activeSubmenuWidth, mouseY, MiniMenu.menuTextColour | 0xFF000000, MiniMenu.activeSubmenuHeight, MiniMenu.activeSubmenuX, mouseX);
				}
				InterfaceRectangles.markRectanglesForFlip(MiniMenu.activeSubmenuWidth, MiniMenu.activeSubmenuY, MiniMenu.activeSubmenuX, MiniMenu.activeSubmenuHeight);
			}
		} else {
			entryIndex = 0;
			for (miniMenuEntryInner = (MiniMenuEntry) MiniMenu.entryList.first(); miniMenuEntryInner != null; miniMenuEntryInner = (MiniMenuEntry) MiniMenu.entryList.next()) {
				entryY = menuY + (MiniMenu.entryCount + -1 + -entryIndex) * 16 + 20 + 13;
				entryIndex++;
				MiniMenu.drawMenuEntry(menuY, MiniMenu.menuHoverTextColour | 0xFF000000, entryY, toolkit, miniMenuEntryInner, menuWidth, mouseY, MiniMenu.menuTextColour | 0xFF000000, menuHeight, menuX, mouseX);
			}
		}
		InterfaceRectangles.markRectanglesForFlip(MiniMenu.menuOpenWidth, MiniMenu.menuOpenY + offsetY, offsetX + menuOpenX, InterfaceComponentRenderer.menuHeight);
	}

	@OriginalMember(owner = "client!uv", name = "l", descriptor = "I")
	public static int examineCursor = -1;

	@OriginalMember(owner = "client!lca", name = "a", descriptor = "(IIIIIILclient!ha;)V")
	public static void drawStyledMenuBorder(@OriginalArg(1) int x, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(5) int y, @OriginalArg(6) Toolkit toolkit) {
		if ((StaticMedia.menuBottomBorderSprite == null || StaticMedia.menuLeftSideBorderSprite == null || StaticMedia.menuBottomLeftCornerSprite == null) && Resource.SPRITES.isFileReady(Find.menuBottomBorderSpriteId) && Resource.SPRITES.isFileReady(StaticMedia.menuSideBorderSpriteId) && Resource.SPRITES.isFileReady(ScriptRunner.menuCornerSpriteId)) {
			@Pc(27) SpriteType indexedImage = SpriteDataProvider.listSingle(Resource.SPRITES, StaticMedia.menuSideBorderSpriteId, 0);
			StaticMedia.menuLeftSideBorderSprite = toolkit.generateSprite(indexedImage, true);
			indexedImage.flipHorizontal();
			StaticMedia.menuRightSideBorderSprite = toolkit.generateSprite(indexedImage, true);
			StaticMedia.menuBottomBorderSprite = toolkit.generateSprite(SpriteDataProvider.listSingle(Resource.SPRITES, Find.menuBottomBorderSpriteId, 0), true);
			@Pc(52) SpriteType indexedImage2 = SpriteDataProvider.listSingle(Resource.SPRITES, ScriptRunner.menuCornerSpriteId, 0);
			StaticMedia.menuBottomLeftCornerSprite = toolkit.generateSprite(indexedImage2, true);
			indexedImage2.flipHorizontal();
			StaticMedia.menuBottomRightCornerSprite = toolkit.generateSprite(indexedImage2, true);
		}
		if (StaticMedia.menuBottomBorderSprite == null || StaticMedia.menuLeftSideBorderSprite == null || StaticMedia.menuBottomLeftCornerSprite == null) {
			return;
		}
		@Pc(85) int bottomTileCount = (width - StaticMedia.menuBottomLeftCornerSprite.getWidth() * 2) / StaticMedia.menuBottomBorderSprite.getWidth();
		for (@Pc(87) int i = 0; i < bottomTileCount; i++) {
			StaticMedia.menuBottomBorderSprite.render(x + StaticMedia.menuBottomLeftCornerSprite.getWidth() + i * StaticMedia.menuBottomBorderSprite.getWidth(), -StaticMedia.menuBottomBorderSprite.getHeight() + y + height);
		}
		@Pc(122) int sideTileCount = (height - StaticMedia.menuBottomLeftCornerSprite.getHeight() - 20) / StaticMedia.menuLeftSideBorderSprite.getHeight();
		for (@Pc(124) int j = 0; j < sideTileCount; j++) {
			StaticMedia.menuLeftSideBorderSprite.render(x, StaticMedia.menuLeftSideBorderSprite.getHeight() * j + y + 20);
			StaticMedia.menuRightSideBorderSprite.render(width + x - StaticMedia.menuRightSideBorderSprite.getWidth(), StaticMedia.menuLeftSideBorderSprite.getHeight() * j + 20 + y);
		}
		StaticMedia.menuBottomLeftCornerSprite.render(x, y + height - StaticMedia.menuBottomLeftCornerSprite.getHeight());
		StaticMedia.menuBottomRightCornerSprite.render(x + width - StaticMedia.menuBottomLeftCornerSprite.getWidth(), y - (-height + StaticMedia.menuBottomLeftCornerSprite.getHeight()));
	}

	@OriginalMember(owner = "client!vu", name = "d", descriptor = "(II)Z")
	public static boolean isLocClickType(@OriginalArg(1) int clickType) {
		if (clickType == MiniMenuAction.OPLOC1 || clickType == MiniMenuAction.OPLOC2 || clickType == MiniMenuAction.OPLOC3 || clickType == MiniMenuAction.OPLOC4 || clickType == MiniMenuAction.OPLOC5) {
			return true;
		} else {
			return clickType == MiniMenuAction.TGT_LOC || clickType == MiniMenuAction.OPLOC6;
		}
	}

	@OriginalMember(owner = "client!fba", name = "x", descriptor = "Lclient!mi;")
	public static NativeSprite menuHeaderRightCornerSprite;
	@OriginalMember(owner = "client!oga", name = "a", descriptor = "(II)Z")
	public static boolean isObjClickType(@OriginalArg(1) int clickType) {
		if (clickType == MiniMenuAction.OPOBJ1 || clickType == MiniMenuAction.OPOBJ2 || clickType == MiniMenuAction.OPOBJ3 || clickType == MiniMenuAction.OPOBJ4 || clickType == MiniMenuAction.OPOBJ5) {
			return true;
		} else {
			return clickType == MiniMenuAction.TGT_OBJ || clickType == MiniMenuAction.OPOBJ6;
		}
	}

	@OriginalMember(owner = "client!pda", name = "R", descriptor = "I")
	public static int menuHeaderCornerSpriteId;

	@OriginalMember(owner = "client!kl", name = "a", descriptor = "(IB)Z")
	public static boolean isNpcClickType(@OriginalArg(0) int clickType) {
		if (clickType == MiniMenuAction.OPNPC1 || clickType == MiniMenuAction.OPNPC2 || clickType == MiniMenuAction.OPNPC3 || clickType == MiniMenuAction.OPNPC4 || clickType == MiniMenuAction.OPNPC5) {
			return true;
		} else {
			return clickType == MiniMenuAction.TGT_NPC || clickType == MiniMenuAction.OPNPC6;
		}
	}

	@OriginalMember(owner = "client!il", name = "a", descriptor = "(ILclient!vq;)Ljava/lang/String;")
	public static String formatTag(@OriginalArg(1) ColoredMiniMenuEntry miniMenuEntry) {
		return miniMenuEntry.tagStart + " <col=ffffff>>";
	}

	@OriginalMember(owner = "client!fi", name = "S", descriptor = "Lclient!uh;")
	public static final Deque aClass341_36 = new Deque();

	@OriginalMember(owner = "client!hj", name = "c", descriptor = "Lclient!at;")
	public static Component component = null;

	@OriginalMember(owner = "client!hu", name = "f", descriptor = "Ljava/lang/String;")
	public static String useOnType = null;

	@OriginalMember(owner = "client!kda", name = "a", descriptor = "(IILclient!vq;IIIILclient!ha;IIII)V")
	public static void drawColoredMenuEntry(@OriginalArg(0) int color, @OriginalArg(1) int y, @OriginalArg(2) ColoredMiniMenuEntry miniMenuEntry, @OriginalArg(3) int mouseX, @OriginalArg(4) int width, @OriginalArg(5) int hoverColor, @OriginalArg(6) int mouseY, @OriginalArg(7) Toolkit toolkit, @OriginalArg(8) int menuY, @OriginalArg(10) int x, @OriginalArg(11) int menuHeight) {
		if (x < mouseX && mouseX < width + x && y - 13 < mouseY && mouseY < y + 3) {
			color = hoverColor;
		}
		@Pc(36) String string = MiniMenu.formatTag(miniMenuEntry);
		MiniMenu.minimenuHeader.drawStringWithSprites(y, string, color, x + 3, StaticMedia.chatIcons, StaticMedia.chatScaleHeights);
	}

	@OriginalMember(owner = "client!jo", name = "a", descriptor = "(BLclient!ha;)V")
	public static void render(@OriginalArg(1) Toolkit toolkit) {
		if (MiniMenu.isOpen) {
			MiniMenu.renderOpenMenu(toolkit);
		} else {
			MiniMenu.renderTooltipText(toolkit);
		}
	}

	@OriginalMember(owner = "client!iv", name = "Fb", descriptor = "I")
	public static int menuBackgroundTransparency;

	@OriginalMember(owner = "client!kv", name = "a", descriptor = "(IILclient!ha;Ljava/lang/String;ZIII)V")
	public static void drawStyledMenuHeader(@OriginalArg(0) int width, @OriginalArg(1) int x, @OriginalArg(2) Toolkit toolkit, @OriginalArg(3) String string, @OriginalArg(5) int y, @OriginalArg(6) int height) {
		if (MiniMenu.menuHeaderSprite == null || StaticMedia.menuHeaderLeftCornerSprite == null) {
			if (Resource.SPRITES.isFileReady(MiniMenu.menuHeaderSpriteId) && Resource.SPRITES.isFileReady(MiniMenu.menuHeaderCornerSpriteId)) {
				MiniMenu.menuHeaderSprite = toolkit.generateSprite(SpriteDataProvider.listSingle(Resource.SPRITES, MiniMenu.menuHeaderSpriteId, 0), true);
				@Pc(28) SpriteType indexedImage = SpriteDataProvider.listSingle(Resource.SPRITES, MiniMenu.menuHeaderCornerSpriteId, 0);
				StaticMedia.menuHeaderLeftCornerSprite = toolkit.generateSprite(indexedImage, true);
				indexedImage.flipHorizontal();
				MiniMenu.menuHeaderRightCornerSprite = toolkit.generateSprite(indexedImage, true);
			} else {
				toolkit.aa(x, y, width, 20, 255 - MiniMenu.menuBackgroundTransparency << 24 | MiniMenu.menuBackgroundColour, 1);
			}
		}
		if (MiniMenu.menuHeaderSprite != null && StaticMedia.menuHeaderLeftCornerSprite != null) {
			@Pc(75) int tileCount = (width - StaticMedia.menuHeaderLeftCornerSprite.getWidth() * 2) / MiniMenu.menuHeaderSprite.getWidth();
			for (@Pc(77) int i = 0; i < tileCount; i++) {
				MiniMenu.menuHeaderSprite.render(StaticMedia.menuHeaderLeftCornerSprite.getWidth() + x + MiniMenu.menuHeaderSprite.getWidth() * i, y);
			}
			StaticMedia.menuHeaderLeftCornerSprite.render(x, y);
			MiniMenu.menuHeaderRightCornerSprite.render(x + width - MiniMenu.menuHeaderRightCornerSprite.getWidth(), y);
		}
		MiniMenu.minimenuHeader.drawString(string, MiniMenu.menuTextColour | 0xFF000000, y + 14, x + 3, -1);
		toolkit.aa(x, y + 20, width, height - 20, MiniMenu.menuBackgroundColour | -MiniMenu.menuBackgroundTransparency + 255 << 24, 1);
	}

	@OriginalMember(owner = "client!hu", name = "e", descriptor = "Ljava/lang/String;")
	public static String useOnTarget = null;

	@OriginalMember(owner = "client!gw", name = "e", descriptor = "(B)V")
	public static void removeComponentEntries() {
		for (@Pc(14) MiniMenuEntry miniMenuEntryInner = (MiniMenuEntry) MiniMenu.entryList.first(); miniMenuEntryInner != null; miniMenuEntryInner = (MiniMenuEntry) MiniMenu.entryList.next()) {
			if (MiniMenu.isComponentClickType(miniMenuEntryInner.clickType)) {
				MiniMenu.removeEntry(miniMenuEntryInner);
			}
		}
	}

	@OriginalMember(owner = "client!mq", name = "ab", descriptor = "[Ljava/lang/String;")
	public static final String[] PLAYER_OPTIONS = new String[8];

	@OriginalMember(owner = "client!kba", name = "y", descriptor = "Ljava/lang/String;")
	public static String walkHereText;
	@OriginalMember(owner = "client!un", name = "E", descriptor = "I")
	public static int menuOpenMode = 0;

	@OriginalMember(owner = "client!ca", name = "d", descriptor = "I")
	public static int targetParamId;

	@OriginalMember(owner = "client!vg", name = "a", descriptor = "[I")
	public static final int[] PLAYER_OPTION_CURSORS = new int[8];

	@OriginalMember(owner = "client!eq", name = "j", descriptor = "I")
	public static int randomSeed;

	@OriginalMember(owner = "client!sc", name = "b", descriptor = "Ljava/util/Random;")
	public static final Random RANDOMIZER = new Random();

	@OriginalMember(owner = "client!gs", name = "c", descriptor = "I")
	public static int slotId = -1;

	@OriginalMember(owner = "client!nga", name = "a", descriptor = "Z")
	public static boolean showFaceHere;

	@OriginalMember(owner = "client!wp", name = "a", descriptor = "(ZLclient!ha;)V")
	public static void renderOpenMenu(@OriginalArg(1) Toolkit toolkit) {
		if (FontMetrics.useBoldFont) {
			MiniMenu.renderOpenMenuStyled(toolkit);
		} else {
			MiniMenu.renderOpenMenuSimple(toolkit);
		}
	}

	@OriginalMember(owner = "client!cda", name = "e", descriptor = "I")
	public static int selectedItemId = -1;
	@OriginalMember(owner = "client!iea", name = "d", descriptor = "I")
	public static int useTargetMask;
}

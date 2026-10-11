package com.jagex.game.runetek5;

import com.jagex.game.runetek5.graphics.sprite.SpriteType;
import com.jagex.game.runetek5.entity.NPCEntity;
import com.jagex.game.runetek5.entity.NPCEntityNode;
import com.jagex.game.runetek5.entity.PathingEntity;
import com.jagex.game.runetek5.entity.PlayerEntity;
import com.jagex.game.runetek5.core.constants.HintArrowType;
import com.jagex.game.runetek5.config.hitsplat.HitSplatType;
import com.jagex.game.runetek5.config.npc.NPCType;
import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer;
import com.jagex.game.runetek5.graphics.font.FontMetrics;
import com.jagex.game.runetek5.graphics.sprite.NativeSprite;
import com.jagex.game.runetek5.graphics.gl.HitsplatSlotXOffsets;
import com.jagex.game.runetek5.statics.OutPlayerIndexCount;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.event.keyboard.FriendOrSelfCheck;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;
import com.jagex.game.runetek5.config.Resource;
import com.jagex.game.runetek5.rendering.Renderers;
import com.jagex.game.runetek5.client.menu.MiniMenu;
import com.jagex.game.runetek5.graphics.hint.HintArrow;
import com.jagex.game.runetek5.graphics.font.FontManager;
import com.jagex.game.runetek5.graphics.sprite.StaticMedia;
import com.jagex.game.runetek5.interaction.chat.ChatBox;
import com.jagex.game.runetek5.config.TypeLists;
import com.jagex.game.runetek5.cache.node.lru.LRUCache;
import com.jagex.game.runetek5.entity.EntityOverhead;
import com.jagex.game.runetek5.vardomain.PlayerVarDomain;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.client.login.LoginManager;
import com.jagex.game.runetek5.config.defaults.EntityDefaults;
import com.jagex.game.runetek5.scene.Scene;

public final class OverlayManager {

	@OriginalMember(owner = "client!cf", name = "i", descriptor = "I")
	public static final int MAX_OVERHEAD_TEXTS = 50;

	@OriginalMember(owner = "client!cf", name = "a", descriptor = "[I")
	public static final int[] OVERHEAD_TEXT_EFFECTS = new int[MAX_OVERHEAD_TEXTS];

	@OriginalMember(owner = "client!cf", name = "g", descriptor = "[I")
	public static final int[] OVERHEAD_TEXT_TICKS_LEFT = new int[MAX_OVERHEAD_TEXTS];

	@OriginalMember(owner = "client!cf", name = "h", descriptor = "[I")
	public static final int[] OVERHEAD_TEXT_Y = new int[MAX_OVERHEAD_TEXTS];

	@OriginalMember(owner = "client!cf", name = "l", descriptor = "[I")
	public static final int[] OVERHEAD_TEXT_COLOURS = new int[MAX_OVERHEAD_TEXTS];

	@OriginalMember(owner = "client!cf", name = "m", descriptor = "[Ljava/lang/String;")
	public static final String[] OVERHEAD_TEXTS = new String[MAX_OVERHEAD_TEXTS];

	@OriginalMember(owner = "client!cf", name = "n", descriptor = "[I")
	public static final int[] OVERHEAD_TEXT_HALF_WIDTHS = new int[MAX_OVERHEAD_TEXTS];

	@OriginalMember(owner = "client!cf", name = "q", descriptor = "[I")
	public static final int[] OVERHEAD_TEXT_X = new int[MAX_OVERHEAD_TEXTS];
	@OriginalMember(owner = "client!qo", name = "b", descriptor = "(IIIIIII)V")
	public static void drawEntities(@OriginalArg(0) int clipTop, @OriginalArg(1) int height, @OriginalArg(3) int clipLeft, @OriginalArg(4) int clipWidth) {
		@Pc(7) int playerCount = OutPlayerIndexCount.NUM_OUT_PLAYER_INDICES;
		OverlayManager.overheadTextCount = 0;
		@Pc(11) int[] outPlayerIndices = PlayerUpdate.OUT_PLAYER_INDICES;
		@Pc(177) int local177;
		@Pc(202) int local202;
		@Pc(259) int local259;
		@Pc(337) int local337;
		@Pc(340) int local340;
		@Pc(1157) int local1157;
		@Pc(1202) int local1202;
		@Pc(1204) int local1204;
		@Pc(439) int local439;
		@Pc(1060) int local1060;
		@Pc(576) int local576;
		for (@Pc(13) int i = 0; i < NPCList.NPC_UPDATE_INDEX + playerCount; i++) {
			@Pc(17) NPCType npcType = null;
			@Pc(26) PathingEntity pathingEntity;
			if (playerCount > i) {
				pathingEntity = PlayerUpdate.players[outPlayerIndices[i]];
			} else {
				pathingEntity = ((NPCEntityNode) NPCList.NPC_MAP.get((long) NPCList.NPC_UPDATE_INDICES[i - playerCount])).npc;
				npcType = ((NPCEntity) pathingEntity).npcType;
				if (npcType.alternativeIds != null) {
					npcType = npcType.getMultiNpc(PlayerVarDomain.playerVarProvider);
					if (npcType == null) {
						continue;
					}
				}
			}
			if (pathingEntity.worldPriority >= 0 && (SceneDrawer.timesSceneRendered == pathingEntity.drawCycle || pathingEntity.level == PlayerUpdate.localPlayer.level)) {
				OverlayManager.projectEntityToScreen(pathingEntity.getOverheadHeight(), clipWidth >> 1, height >> 1, pathingEntity);
				if (OverlayManager.projectedScreenPosition[0] >= 0) {
					if (pathingEntity.hintDetails.text != null && (playerCount <= i || ChatBox.publicFilter == 0 || ChatBox.publicFilter == 3 || ChatBox.publicFilter == 1 && FriendOrSelfCheck.isFriendOrSelf(((PlayerEntity) pathingEntity).displayName)) && OverlayManager.overheadTextCount < OverlayManager.MAX_OVERHEAD_TEXTS) {
						OverlayManager.OVERHEAD_TEXT_HALF_WIDTHS[OverlayManager.overheadTextCount] = FontMetrics.defaultFontMetrics.getWidthNoSprites(pathingEntity.hintDetails.text) / 2;
						OverlayManager.OVERHEAD_TEXT_X[OverlayManager.overheadTextCount] = OverlayManager.projectedScreenPosition[0];
						OverlayManager.OVERHEAD_TEXT_Y[OverlayManager.overheadTextCount] = OverlayManager.projectedScreenPosition[1];
						OverlayManager.OVERHEAD_TEXT_COLOURS[OverlayManager.overheadTextCount] = pathingEntity.hintDetails.colorType;
						OverlayManager.OVERHEAD_TEXT_EFFECTS[OverlayManager.overheadTextCount] = pathingEntity.hintDetails.drawType;
						OverlayManager.OVERHEAD_TEXT_TICKS_LEFT[OverlayManager.overheadTextCount] = pathingEntity.hintDetails.ticksLeft;
						OverlayManager.OVERHEAD_TEXTS[OverlayManager.overheadTextCount] = pathingEntity.hintDetails.text;
						OverlayManager.overheadTextCount++;
					}
					int overheadY = OverlayManager.projectedScreenPosition[1] + clipTop;
					@Pc(244) NativeSprite[] sprites;
					@Pc(251) SpriteType[] indexedImages;
					@Pc(309) NativeSprite sprite;
					if (pathingEntity.isHidden || pathingEntity.healthBarEndCycle <= ClientState.clientCycle) {
						overheadY -= Math.max(FontMetrics.defaultFontMetrics.paddingTop, StaticMedia.hitbarDefaultSprites[0].getHeight());
					} else {
						@Pc(192) byte barSpriteIndex = 1;
						int healthBarSpriteGroup;
						if (i >= playerCount) {
							healthBarSpriteGroup = npcType.healthBarSpriteGroup;
							if (healthBarSpriteGroup == -1) {
								healthBarSpriteGroup = pathingEntity.getBasType().healthBarSpriteGroup;
							}
						} else {
							@Pc(219) PlayerEntity playerEntity = PlayerUpdate.players[outPlayerIndices[i]];
							healthBarSpriteGroup = pathingEntity.getBasType().healthBarSpriteGroup;
							if (playerEntity.pMapdot) {
								barSpriteIndex = 2;
							}
						}
						@Pc(231) NativeSprite[] sprites2 = StaticMedia.hitbarDefaultSprites;
						if (healthBarSpriteGroup != -1) {
							sprites = (NativeSprite[]) OverlayManager.aClass169_22.get((long) healthBarSpriteGroup);
							if (sprites == null) {
								indexedImages = SpriteType.decodeGroup(Resource.SPRITES, healthBarSpriteGroup);
								if (indexedImages != null) {
									sprites = new NativeSprite[indexedImages.length];
									for (int imageIndex = 0; imageIndex < indexedImages.length; imageIndex++) {
										sprites[imageIndex] = ClientState.activeToolkit.generateSprite(indexedImages[imageIndex], true);
									}
									OverlayManager.aClass169_22.put(sprites, (long) healthBarSpriteGroup);
								}
							}
							if (sprites != null && sprites.length >= 2) {
								sprites2 = sprites;
							}
						}
						if (barSpriteIndex >= sprites2.length) {
							barSpriteIndex = 1;
						}
						@Pc(305) NativeSprite sprite2 = sprites2[0];
						sprite = sprites2[barSpriteIndex];
						overheadY -= Math.max(FontMetrics.defaultFontMetrics.paddingTop, sprite2.getHeight());
						int healthBarX = OverlayManager.projectedScreenPosition[0] + clipLeft - (sprite2.getWidth() >> 1);
						int healthFillWidth = sprite2.getWidth() * pathingEntity.healthRatio / 255;
						int healthBarHeight = sprite2.getHeight();
						if (pathingEntity.healthRatio > 0 && healthFillWidth < 2) {
							healthFillWidth = 2;
						}
						sprite2.render(healthBarX, overheadY);
						ClientState.activeToolkit.T(healthBarX, overheadY, healthFillWidth + healthBarX, healthBarHeight + overheadY);
						sprite.render(healthBarX, overheadY);
						ClientState.activeToolkit.KA(clipLeft, clipTop, clipLeft + clipWidth, height + clipTop);
						OrthoMode.markOrthoTilesDirty(healthBarX, healthBarX + sprite2.scaleWidth(), overheadY, overheadY + healthBarHeight);
					}
					overheadY -= 2;
					if (!pathingEntity.isHidden) {
						@Pc(422) NativeSprite sprite3;
						@Pc(431) NativeSprite sprite4;
						if (pathingEntity.secondaryBarEndCycle > ClientState.clientCycle) {
							sprite3 = StaticMedia.timerbarDefaultSprites[pathingEntity.secondaryBarAlternate ? 2 : 0];
							sprite4 = StaticMedia.timerbarDefaultSprites[pathingEntity.secondaryBarAlternate ? 3 : 1];
							int secondaryBarSpriteGroup;
							if (pathingEntity instanceof NPCEntity) {
								secondaryBarSpriteGroup = npcType.secondaryBarSpriteGroup;
								if (secondaryBarSpriteGroup == -1) {
									secondaryBarSpriteGroup = pathingEntity.getBasType().secondaryBarSpriteGroup;
								}
							} else {
								secondaryBarSpriteGroup = pathingEntity.getBasType().secondaryBarSpriteGroup;
							}
							if (secondaryBarSpriteGroup != -1) {
								sprites = (NativeSprite[]) OverlayManager.aClass169_17.get((long) secondaryBarSpriteGroup);
								if (sprites == null) {
									indexedImages = SpriteType.decodeGroup(Resource.SPRITES, secondaryBarSpriteGroup);
									if (indexedImages != null) {
										sprites = new NativeSprite[indexedImages.length];
										for (int imageIndex = 0; imageIndex < indexedImages.length; imageIndex++) {
											sprites[imageIndex] = ClientState.activeToolkit.generateSprite(indexedImages[imageIndex], true);
										}
										OverlayManager.aClass169_17.put(sprites, (long) secondaryBarSpriteGroup);
									}
								}
								if (sprites != null && sprites.length == 4) {
									sprite4 = sprites[pathingEntity.secondaryBarAlternate ? 3 : 1];
									sprite3 = sprites[pathingEntity.secondaryBarAlternate ? 2 : 0];
								}
							}
							@Pc(536) int remainingCycles = pathingEntity.secondaryBarEndCycle - ClientState.clientCycle;
							int timerFillWidth;
							if (pathingEntity.secondaryBarHoldCycles < remainingCycles) {
								remainingCycles -= pathingEntity.secondaryBarHoldCycles;
								int elapsedCycles = pathingEntity.secondaryBarStep == 0 ? 0 : pathingEntity.secondaryBarStep * ((pathingEntity.secondaryBarDuration - remainingCycles) / pathingEntity.secondaryBarStep);
								timerFillWidth = sprite3.getWidth() * elapsedCycles / pathingEntity.secondaryBarDuration;
							} else {
								timerFillWidth = sprite3.getWidth();
							}
							int timerBarHeight = sprite3.getHeight();
							overheadY -= timerBarHeight;
							int timerBarX = OverlayManager.projectedScreenPosition[0] + clipLeft - (sprite3.getWidth() >> 1);
							sprite3.render(timerBarX, overheadY);
							ClientState.activeToolkit.T(timerBarX, overheadY, timerBarX + timerFillWidth, overheadY - -timerBarHeight);
							sprite4.render(timerBarX, overheadY);
							ClientState.activeToolkit.KA(clipLeft, clipTop, clipWidth + clipLeft, clipTop - -height);
							OrthoMode.markOrthoTilesDirty(timerBarX, sprite3.scaleWidth() + timerBarX, overheadY, timerBarHeight + overheadY);
							overheadY -= 2;
						}
						if (playerCount > i) {
							@Pc(653) PlayerEntity playerEntity2 = (PlayerEntity) pathingEntity;
							if (playerEntity2.skullId != -1) {
								overheadY -= 25;
								sprite4 = StaticMedia.headiconPkSprites[playerEntity2.skullId];
								sprite4.render(clipLeft + OverlayManager.projectedScreenPosition[0] - 12, overheadY);
								OrthoMode.markOrthoTilesDirty(clipLeft + OverlayManager.projectedScreenPosition[0] - 12, clipLeft - -OverlayManager.projectedScreenPosition[0] + -12 + sprite4.scaleWidth(), overheadY, overheadY + sprite4.scaleHeight());
								overheadY -= 2;
							}
							if (playerEntity2.prayerHeadIconId != -1) {
								overheadY -= 25;
								sprite4 = StaticMedia.headiconPrayerSprites[playerEntity2.prayerHeadIconId];
								sprite4.render(clipLeft + OverlayManager.projectedScreenPosition[0] - 12, overheadY);
								OrthoMode.markOrthoTilesDirty(OverlayManager.projectedScreenPosition[0] + clipLeft - 12, OverlayManager.projectedScreenPosition[0] + clipLeft - (-sprite4.scaleWidth() + 12), overheadY, overheadY + sprite4.scaleHeight());
								overheadY -= 2;
							}
						} else if (npcType.headIcons >= 0 && StaticMedia.headiconPrayerSprites.length > npcType.headIcons) {
							sprite3 = StaticMedia.headiconPrayerSprites[npcType.headIcons];
							overheadY -= 25;
							sprite3.render(clipLeft + OverlayManager.projectedScreenPosition[0] - (sprite3.getWidth() >> 1), overheadY);
							OrthoMode.markOrthoTilesDirty(OverlayManager.projectedScreenPosition[0] + clipLeft - (sprite3.getWidth() >> 1), OverlayManager.projectedScreenPosition[0] + (clipLeft - (sprite3.getWidth() >> 1)) + sprite3.scaleWidth(), overheadY, sprite3.scaleHeight() + overheadY);
							overheadY -= 2;
						}
					}
					@Pc(825) HintArrow[] hintArrows;
					@Pc(833) HintArrow hintArrow;
					if (!(pathingEntity instanceof PlayerEntity)) {
						int maxArrowHeight = 0;
						hintArrows = PlayerUpdate.HINT_ARROWS;
						for (int arrowIndex = 0; arrowIndex < hintArrows.length; arrowIndex++) {
							hintArrow = hintArrows[arrowIndex];
							if (hintArrow != null && hintArrow.targetType == HintArrowType.NPC && NPCList.NPC_UPDATE_INDICES[i - playerCount] == hintArrow.targetIndex) {
								sprite = StaticMedia.hintHeadiconSprites[hintArrow.arrowSprite];
								if (maxArrowHeight < sprite.getHeight()) {
									maxArrowHeight = sprite.getHeight();
								}
								if (ClientState.clientCycle % 20 < 10) {
									sprite.render(clipLeft + OverlayManager.projectedScreenPosition[0] - 12, overheadY + -sprite.getHeight());
									OrthoMode.markOrthoTilesDirty(clipLeft + OverlayManager.projectedScreenPosition[0] - 12, OverlayManager.projectedScreenPosition[0] + clipLeft + -12 - -sprite.scaleWidth(), overheadY - sprite.getHeight(), -sprite.getHeight() + (overheadY - -sprite.scaleHeight()));
								}
							}
						}
						if (maxArrowHeight > 0) {
						}
					} else if (i >= 0) {
						int maxArrowHeight = 0;
						hintArrows = PlayerUpdate.HINT_ARROWS;
						for (int arrowIndex = 0; arrowIndex < hintArrows.length; arrowIndex++) {
							hintArrow = hintArrows[arrowIndex];
							if (hintArrow != null && hintArrow.targetType == 10 && hintArrow.targetIndex == outPlayerIndices[i]) {
								sprite = StaticMedia.hintHeadiconSprites[hintArrow.arrowSprite];
								if (sprite.getHeight() > maxArrowHeight) {
									maxArrowHeight = sprite.getHeight();
								}
								sprite.render(OverlayManager.projectedScreenPosition[0] + clipLeft - 12, overheadY + -sprite.getHeight());
								OrthoMode.markOrthoTilesDirty(clipLeft + OverlayManager.projectedScreenPosition[0] - 12, clipLeft + (OverlayManager.projectedScreenPosition[0] + -12 - -sprite.scaleWidth()), overheadY - sprite.getHeight(), overheadY + -sprite.getHeight() - -sprite.scaleHeight());
							}
						}
						if (maxArrowHeight > 0) {
						}
					}
					for (int hitIndex = 0; hitIndex < PathingEntity.maximumHits; hitIndex++) {
						int hitsplatEndCycle = pathingEntity.hitsplatCycles[hitIndex];
						int hitsplatId = pathingEntity.activeHitsplatIds[hitIndex];
						@Pc(1067) HitSplatType hitmarkType = null;
						int visibleCycles = 0;
						if (hitsplatId >= 0) {
							if (ClientState.clientCycle >= hitsplatEndCycle) {
								continue;
							}
							hitmarkType = TypeLists.aClass307_2.list(pathingEntity.activeHitsplatIds[hitIndex]);
							visibleCycles = hitmarkType.cyclesVisible;
						} else if (hitsplatEndCycle < 0) {
							continue;
						}
						int soakHitsplatId = pathingEntity.soakHitsplatIds[hitIndex];
						@Pc(1103) HitSplatType hitmarkType2 = null;
						if (soakHitsplatId >= 0) {
							hitmarkType2 = TypeLists.aClass307_2.list(soakHitsplatId);
						}
						if (hitsplatEndCycle - visibleCycles <= ClientState.clientCycle) {
							int hitsplatHealthRatio = pathingEntity.hitsplatHealthRatios[hitIndex];
							if (hitsplatHealthRatio >= 0) {
								pathingEntity.healthBarEndCycle = ClientState.clientCycle + 300;
								pathingEntity.healthRatio = hitsplatHealthRatio;
								pathingEntity.hitsplatHealthRatios[hitIndex] = -1;
							}
							if (hitmarkType == null) {
								pathingEntity.hitsplatCycles[hitIndex] = -1;
							} else {
								int hitsplatHeightOffset = pathingEntity.getOverheadHeight() / 2;
								OverlayManager.projectEntityToScreen(hitsplatHeightOffset, clipWidth >> 1, height >> 1, pathingEntity);
								if (OverlayManager.projectedScreenPosition[0] > -1) {
									OverlayManager.projectedScreenPosition[0] += HitsplatSlotXOffsets.hitsplatOffsetsX[hitIndex];
									OverlayManager.projectedScreenPosition[1] += EntityDefaults.hitsplatOffsetsY[hitIndex];
									int leftCapWidth = 0;
									int middleFillWidth = 0;
									@Pc(1206) int width = 0;
									@Pc(1208) int rightCapWidth = 0;
									@Pc(1210) int leftCapOffsetX = 0;
									@Pc(1212) int middleFillOffsetX = 0;
									@Pc(1214) int innerLeftOffsetX = 0;
									@Pc(1216) int rightCapOffsetX = 0;
									@Pc(1218) NativeSprite sprite5 = null;
									@Pc(1220) NativeSprite sprite6 = null;
									@Pc(1222) NativeSprite sprite7 = null;
									@Pc(1224) NativeSprite sprite8 = null;
									@Pc(1226) int soakLeftCapWidth = 0;
									@Pc(1228) int soakMiddleFillWidth = 0;
									@Pc(1230) int soakInnerLeftWidth = 0;
									@Pc(1232) int soakRightCapWidth = 0;
									@Pc(1234) int soakLeftCapOffsetX = 0;
									@Pc(1236) int soakMiddleFillOffsetX = 0;
									@Pc(1238) int soakInnerLeftOffsetX = 0;
									@Pc(1240) int soakRightCapOffsetX = 0;
									@Pc(1242) int maxSpriteHeight = 0;
									@Pc(1247) NativeSprite sprite9 = hitmarkType.getLeftCapSprite(ClientState.activeToolkit);
									@Pc(1255) int local1255;
									if (sprite9 != null) {
										leftCapWidth = sprite9.getWidth();
										int leftCapHeight = sprite9.getHeight();
										sprite9.projectOffsets(OverlayManager.HITMARK_SPRITE_OFFSETS);
										if (leftCapHeight > 0) {
											maxSpriteHeight = leftCapHeight;
										}
										leftCapOffsetX = OverlayManager.HITMARK_SPRITE_OFFSETS[0];
									}
									@Pc(1276) NativeSprite sprite10 = hitmarkType.getMiddleFillSprite(ClientState.activeToolkit);
									if (sprite10 != null) {
										middleFillWidth = sprite10.getWidth();
										int middleFillHeight = sprite10.getHeight();
										sprite10.projectOffsets(OverlayManager.HITMARK_SPRITE_OFFSETS);
										if (maxSpriteHeight < middleFillHeight) {
											maxSpriteHeight = middleFillHeight;
										}
										middleFillOffsetX = OverlayManager.HITMARK_SPRITE_OFFSETS[0];
									}
									@Pc(1305) NativeSprite sprite11 = hitmarkType.getInnerLeftSprite(ClientState.activeToolkit);
									if (sprite11 != null) {
										width = sprite11.getWidth();
										int innerLeftHeight = sprite11.getHeight();
										if (innerLeftHeight > maxSpriteHeight) {
											maxSpriteHeight = innerLeftHeight;
										}
										sprite11.projectOffsets(OverlayManager.HITMARK_SPRITE_OFFSETS);
										innerLeftOffsetX = OverlayManager.HITMARK_SPRITE_OFFSETS[0];
									}
									@Pc(1334) NativeSprite sprite12 = hitmarkType.getRightCapSprite(ClientState.activeToolkit);
									if (sprite12 != null) {
										rightCapWidth = sprite12.getWidth();
										int rightCapHeight = sprite12.getHeight();
										sprite12.projectOffsets(OverlayManager.HITMARK_SPRITE_OFFSETS);
										if (rightCapHeight > maxSpriteHeight) {
											maxSpriteHeight = rightCapHeight;
										}
										rightCapOffsetX = OverlayManager.HITMARK_SPRITE_OFFSETS[0];
									}
									if (hitmarkType2 != null) {
										sprite5 = hitmarkType2.getLeftCapSprite(ClientState.activeToolkit);
										if (sprite5 != null) {
											soakLeftCapWidth = sprite5.getWidth();
											int soakLeftCapHeight = sprite5.getHeight();
											if (soakLeftCapHeight > maxSpriteHeight) {
												maxSpriteHeight = soakLeftCapHeight;
											}
											sprite5.projectOffsets(OverlayManager.HITMARK_SPRITE_OFFSETS);
											soakLeftCapOffsetX = OverlayManager.HITMARK_SPRITE_OFFSETS[0];
										}
										sprite6 = hitmarkType2.getMiddleFillSprite(ClientState.activeToolkit);
										if (sprite6 != null) {
											soakMiddleFillWidth = sprite6.getWidth();
											int soakMiddleFillHeight = sprite6.getHeight();
											if (soakMiddleFillHeight > maxSpriteHeight) {
												maxSpriteHeight = soakMiddleFillHeight;
											}
											sprite6.projectOffsets(OverlayManager.HITMARK_SPRITE_OFFSETS);
											soakMiddleFillOffsetX = OverlayManager.HITMARK_SPRITE_OFFSETS[0];
										}
										sprite7 = hitmarkType2.getInnerLeftSprite(ClientState.activeToolkit);
										if (sprite7 != null) {
											soakInnerLeftWidth = sprite7.getWidth();
											int soakInnerLeftHeight = sprite7.getHeight();
											sprite7.projectOffsets(OverlayManager.HITMARK_SPRITE_OFFSETS);
											if (maxSpriteHeight < soakInnerLeftHeight) {
												maxSpriteHeight = soakInnerLeftHeight;
											}
											soakInnerLeftOffsetX = OverlayManager.HITMARK_SPRITE_OFFSETS[0];
										}
										sprite8 = hitmarkType2.getRightCapSprite(ClientState.activeToolkit);
										if (sprite8 != null) {
											soakRightCapWidth = sprite8.getWidth();
											int soakRightCapHeight = sprite8.getHeight();
											if (soakRightCapHeight > maxSpriteHeight) {
												maxSpriteHeight = soakRightCapHeight;
											}
											sprite8.projectOffsets(OverlayManager.HITMARK_SPRITE_OFFSETS);
											soakRightCapOffsetX = OverlayManager.HITMARK_SPRITE_OFFSETS[0];
										}
									}
									@Pc(1468) AbstractFontRenderer font2 = Renderers.FONT_RENDERER;
									@Pc(1470) AbstractFontRenderer font3 = Renderers.FONT_RENDERER;
									@Pc(1472) FontMetrics fontMetrics = FontManager.worldMapAreaFont;
									int fontId = hitmarkType.fontId;
									@Pc(1477) FontMetrics fontMetrics2 = FontManager.worldMapAreaFont;
									@Pc(1488) AbstractFontRenderer font4;
									@Pc(1493) FontMetrics fontMetrics3;
									if (fontId >= 0) {
										font4 = FontManager.getFont(ClientState.activeToolkit, true, fontId);
										fontMetrics3 = FontManager.getFontMetrics(ClientState.activeToolkit, fontId);
										if (font4 != null && fontMetrics3 != null) {
											font2 = font4;
											fontMetrics = fontMetrics3;
										}
									}
									if (hitmarkType2 != null) {
										fontId = hitmarkType2.fontId;
										if (fontId >= 0) {
											font4 = FontManager.getFont(ClientState.activeToolkit, true, fontId);
											fontMetrics3 = FontManager.getFontMetrics(ClientState.activeToolkit, fontId);
											if (font4 != null && fontMetrics3 != null) {
												fontMetrics2 = fontMetrics3;
												font3 = font4;
											}
										}
									}
									@Pc(1534) String string = null;
									@Pc(1538) int soakWidth = 0;
									@Pc(1546) String string2 = hitmarkType.getAmountText(pathingEntity.hitsplatText[hitIndex]);
									@Pc(1551) int regularWidth = fontMetrics.getWidthNoSprites(string2);
									if (hitmarkType2 != null) {
										string = hitmarkType2.getAmountText(pathingEntity.soakHitsplatText[hitIndex]);
										soakWidth = fontMetrics2.getWidthNoSprites(string);
									}
									@Pc(1568) int regularTileCount = 0;
									if (middleFillWidth > 0) {
										regularTileCount = regularWidth / middleFillWidth + 1;
									}
									@Pc(1578) int soakTileCount = 0;
									if (hitmarkType2 != null && soakMiddleFillWidth > 0) {
										soakTileCount = soakWidth / soakMiddleFillWidth + 1;
									}
									@Pc(1590) int totalWidth = 0;
									if (leftCapWidth > 0) {
										totalWidth = leftCapWidth;
									}
									totalWidth += 2;
									@Pc(1601) int innerLeftStartX = totalWidth;
									if (width > 0) {
										totalWidth += width;
									}
									@Pc(1609) int middleFillStartX = totalWidth;
									@Pc(1611) int textStartX = totalWidth;
									@Pc(1626) int local1626;
									if (middleFillWidth <= 0) {
										totalWidth += regularWidth;
									} else {
										int regularTilesWidth = middleFillWidth * regularTileCount;
										textStartX = totalWidth + (regularTilesWidth - regularWidth) / 2;
										totalWidth += regularTilesWidth;
									}
									int rightCapStartX = totalWidth;
									if (rightCapWidth > 0) {
										totalWidth += rightCapWidth;
									}
									@Pc(1652) int soakLeftCapStartX = 0;
									@Pc(1654) int soakInnerLeftStartX = 0;
									@Pc(1656) int soakMiddleFillStartX = 0;
									@Pc(1658) int soakRightCapStartX = 0;
									@Pc(1660) int soakTextStartX = 0;
									@Pc(1690) int local1690;
									if (hitmarkType2 != null) {
										totalWidth += 2;
										soakLeftCapStartX = totalWidth;
										if (soakLeftCapWidth > 0) {
											totalWidth += soakLeftCapWidth;
										}
										totalWidth += 2;
										soakInnerLeftStartX = totalWidth;
										if (soakInnerLeftWidth > 0) {
											totalWidth += soakInnerLeftWidth;
										}
										soakTextStartX = totalWidth;
										soakMiddleFillStartX = totalWidth;
										if (soakMiddleFillWidth > 0) {
											int soakTilesWidth = soakTileCount * soakMiddleFillWidth;
											soakTextStartX = totalWidth + (soakTilesWidth - soakWidth) / 2;
											totalWidth += soakTilesWidth;
										} else {
											totalWidth += soakWidth;
										}
										soakRightCapStartX = totalWidth;
										if (soakRightCapWidth > 0) {
											totalWidth += soakRightCapWidth;
										}
									}
									int hitsplatCyclesLeft = pathingEntity.hitsplatCycles[hitIndex] - ClientState.clientCycle;
									@Pc(1739) int scrollX = hitmarkType.scrollOffsetX - hitmarkType.scrollOffsetX * hitsplatCyclesLeft / hitmarkType.cyclesVisible;
									@Pc(1751) int scrollY = hitmarkType.scrollOffsetY * hitsplatCyclesLeft / hitmarkType.cyclesVisible - hitmarkType.scrollOffsetY;
									@Pc(1765) int drawX = clipLeft + OverlayManager.projectedScreenPosition[0] + scrollX - (totalWidth >> 1);
									@Pc(1776) int drawY = clipTop + OverlayManager.projectedScreenPosition[1] + scrollY - 12;
									@Pc(1778) int clipMinY = drawY;
									@Pc(1782) int clipMaxY = drawY + maxSpriteHeight;
									@Pc(1790) int textDrawY = drawY + hitmarkType.textOffsetY + 15;
									@Pc(1796) int textTopY = textDrawY - fontMetrics.paddingTop;
									@Pc(1802) int textBottomY = textDrawY + fontMetrics.paddingBottom;
									if (textTopY < drawY) {
										clipMinY = textTopY;
									}
									if (clipMaxY < textBottomY) {
										clipMaxY = textBottomY;
									}
									@Pc(1818) int soakTextDrawY = 0;
									@Pc(1834) int local1834;
									@Pc(1844) int local1844;
									if (hitmarkType2 != null) {
										soakTextDrawY = drawY + hitmarkType2.textOffsetY + 15;
										int soakTextTopY = soakTextDrawY - fontMetrics2.paddingTop;
										if (soakTextTopY < clipMinY) {
											clipMinY = soakTextTopY;
										}
										int soakTextBottomY = fontMetrics2.paddingBottom + soakTextDrawY;
										if (soakTextBottomY > clipMaxY) {
											clipMaxY = soakTextBottomY;
										}
									}
									int alpha = 255;
									if (hitmarkType.fadeStartCycle >= 0) {
										alpha = (hitsplatCyclesLeft << 8) / (hitmarkType.cyclesVisible - hitmarkType.fadeStartCycle);
									}
									if (alpha >= 0 && alpha < 255) {
										int alphaMask = alpha << 24;
										@Pc(1884) int colourArgb = alphaMask | 0xFFFFFF;
										if (sprite9 != null) {
											sprite9.render(drawX - leftCapOffsetX, drawY, 0, colourArgb, 1);
										}
										if (sprite11 != null) {
											sprite11.render(innerLeftStartX + drawX - innerLeftOffsetX, drawY, 0, colourArgb, 1);
										}
										@Pc(1916) int tileIndex;
										if (sprite10 != null) {
											for (tileIndex = 0; tileIndex < regularTileCount; tileIndex++) {
												sprite10.render(middleFillStartX + drawX + middleFillWidth * tileIndex - middleFillOffsetX, drawY, 0, colourArgb, 1);
											}
										}
										if (sprite12 != null) {
											sprite12.render(rightCapStartX + drawX - rightCapOffsetX, drawY, 0, colourArgb, 1);
										}
										font2.drawString(string2, hitmarkType.color | alphaMask, textDrawY, textStartX + drawX, 0);
										if (hitmarkType2 != null) {
											if (sprite5 != null) {
												sprite5.render(drawX + soakLeftCapStartX - soakLeftCapOffsetX, drawY, 0, colourArgb, 1);
											}
											if (sprite7 != null) {
												sprite7.render(soakInnerLeftStartX + drawX - soakInnerLeftOffsetX, drawY, 0, colourArgb, 1);
											}
											if (sprite6 != null) {
												for (tileIndex = 0; tileIndex < soakTileCount; tileIndex++) {
													sprite6.render(tileIndex * soakMiddleFillWidth + drawX + soakMiddleFillStartX - soakMiddleFillOffsetX, drawY, 0, colourArgb, 1);
												}
											}
											if (sprite8 != null) {
												sprite8.render(drawX + soakRightCapStartX - soakRightCapOffsetX, drawY, 0, colourArgb, 1);
											}
											font3.drawString(string, alphaMask | hitmarkType2.color, soakTextDrawY, drawX + soakTextStartX, 0);
										}
									} else {
										if (sprite9 != null) {
											sprite9.render(drawX - leftCapOffsetX, drawY);
										}
										if (sprite11 != null) {
											sprite11.render(drawX + innerLeftStartX - innerLeftOffsetX, drawY);
										}
										if (sprite10 != null) {
											for (int tileIndex = 0; tileIndex < regularTileCount; tileIndex++) {
												sprite10.render(middleFillStartX + drawX + tileIndex * middleFillWidth - middleFillOffsetX, drawY);
											}
										}
										if (sprite12 != null) {
											sprite12.render(rightCapStartX + drawX - rightCapOffsetX, drawY);
										}
										font2.drawString(string2, hitmarkType.color | 0xFF000000, textDrawY, drawX + textStartX, 0);
										if (hitmarkType2 != null) {
											if (sprite5 != null) {
												sprite5.render(drawX + soakLeftCapStartX - soakLeftCapOffsetX, drawY);
											}
											if (sprite7 != null) {
												sprite7.render(soakInnerLeftStartX + drawX - soakInnerLeftOffsetX, drawY);
											}
											if (sprite6 != null) {
												for (int tileIndex = 0; tileIndex < soakTileCount; tileIndex++) {
													sprite6.render(tileIndex * soakMiddleFillWidth + drawX + soakMiddleFillStartX - soakMiddleFillOffsetX, drawY);
												}
											}
											if (sprite8 != null) {
												sprite8.render(soakRightCapStartX + drawX - soakRightCapOffsetX, drawY);
											}
											font3.drawString(string, hitmarkType2.color | 0xFF000000, soakTextDrawY, soakTextStartX + drawX, 0);
										}
									}
									OrthoMode.markOrthoTilesDirty(drawX, drawX + totalWidth, clipMinY, clipMaxY + 1);
								}
							}
						}
					}
				}
			}
		}
		@Pc(2228) int local2228;
		for (@Pc(2222) int j = 0; j < PathingEntity.overheadSpriteCount; j++) {
			int hostIndex = PathingEntity.ENTITY_INDEX_REFERENCES[j];
			@Pc(2241) PathingEntity pathingEntity2;
			if (hostIndex >= 2048) {
				pathingEntity2 = ((NPCEntityNode) NPCList.NPC_MAP.get((long) (hostIndex - 2048))).npc;
			} else {
				pathingEntity2 = PlayerUpdate.players[hostIndex];
			}
			int spriteOwnerIndex = PathingEntity.NPC_INDEX_REFERENCES[j];
			@Pc(2266) PathingEntity pathingEntity3;
			if (spriteOwnerIndex >= 2048) {
				pathingEntity3 = ((NPCEntityNode) NPCList.NPC_MAP.get((long) (spriteOwnerIndex - 2048))).npc;
			} else {
				pathingEntity3 = PlayerUpdate.players[spriteOwnerIndex];
			}
			OverlayManager.drawOverheadSprite(--pathingEntity2.overheadStackCount, height, clipTop, clipWidth, clipLeft, pathingEntity3, pathingEntity2);
		}
		int textLineHeight = FontMetrics.defaultFontMetrics.paddingBottom + FontMetrics.defaultFontMetrics.paddingTop + 2;
		for (int textIndex = 0; textIndex < OverlayManager.overheadTextCount; textIndex++) {
			int textX = OverlayManager.OVERHEAD_TEXT_X[textIndex];
			int textY = OverlayManager.OVERHEAD_TEXT_Y[textIndex];
			int textHalfWidth = OverlayManager.OVERHEAD_TEXT_HALF_WIDTHS[textIndex];
			@Pc(2323) boolean overlapping = true;
			while (overlapping) {
				overlapping = false;
				for (int otherTextIndex = 0; otherTextIndex < textIndex; otherTextIndex++) {
					if (textY + 2 > OverlayManager.OVERHEAD_TEXT_Y[otherTextIndex] + -textLineHeight && textY - textLineHeight < OverlayManager.OVERHEAD_TEXT_Y[otherTextIndex] + 2 && textX - textHalfWidth < OverlayManager.OVERHEAD_TEXT_X[otherTextIndex] - -OverlayManager.OVERHEAD_TEXT_HALF_WIDTHS[otherTextIndex] && OverlayManager.OVERHEAD_TEXT_X[otherTextIndex] - OverlayManager.OVERHEAD_TEXT_HALF_WIDTHS[otherTextIndex] < textX + textHalfWidth && textY > OverlayManager.OVERHEAD_TEXT_Y[otherTextIndex] - textLineHeight) {
						textY = OverlayManager.OVERHEAD_TEXT_Y[otherTextIndex] - textLineHeight;
						overlapping = true;
					}
				}
			}
			OverlayManager.OVERHEAD_TEXT_Y[textIndex] = textY;
			@Pc(2415) String string3 = OverlayManager.OVERHEAD_TEXTS[textIndex];
			int textWidth = FontMetrics.defaultFontMetrics.getWidthNoSprites(string3);
			int leftX = textX + clipLeft;
			int topY = textY + clipTop - FontMetrics.defaultFontMetrics.paddingTop;
			int rightX = leftX + textWidth;
			@Pc(2445) int bottomY = textY + clipTop + FontMetrics.defaultFontMetrics.paddingBottom;
			if (OverlayManager.chatEffectsMode == 0) {
				@Pc(2475) int colour = 16776960;
				if (OverlayManager.OVERHEAD_TEXT_COLOURS[textIndex] < 6) {
					colour = OverlayManager.OVERHEAD_CHAT_COLOURS[OverlayManager.OVERHEAD_TEXT_COLOURS[textIndex]];
				}
				if (OverlayManager.OVERHEAD_TEXT_COLOURS[textIndex] == 6) {
					colour = SceneDrawer.timesSceneRendered % 20 >= 10 ? 16776960 : 16711680;
				}
				if (OverlayManager.OVERHEAD_TEXT_COLOURS[textIndex] == 7) {
					colour = SceneDrawer.timesSceneRendered % 20 < 10 ? 255 : 65535;
				}
				if (OverlayManager.OVERHEAD_TEXT_COLOURS[textIndex] == 8) {
					colour = SceneDrawer.timesSceneRendered % 20 >= 10 ? 8454016 : 45056;
				}
				@Pc(2546) int local2546;
				if (OverlayManager.OVERHEAD_TEXT_COLOURS[textIndex] == 9) {
					int elapsedTicks = 150 - OverlayManager.OVERHEAD_TEXT_TICKS_LEFT[textIndex];
					if (elapsedTicks < 50) {
						colour = elapsedTicks * 1280 + 16711680;
					} else if (elapsedTicks < 100) {
						colour = 16384000 + 16776960 - elapsedTicks * 327680;
					} else if (elapsedTicks < 150) {
						colour = (elapsedTicks - 100) * 5 + 65280;
					}
				}
				if (OverlayManager.OVERHEAD_TEXT_COLOURS[textIndex] == 10) {
					int elapsedTicks = 150 - OverlayManager.OVERHEAD_TEXT_TICKS_LEFT[textIndex];
					if (elapsedTicks < 50) {
						colour = elapsedTicks * 5 + 16711680;
					} else if (elapsedTicks < 100) {
						colour = 33095935 - elapsedTicks * 327680;
					} else if (elapsedTicks < 150) {
						colour = elapsedTicks * 327680 + 500 - elapsedTicks * 5 - 32767745;
					}
				}
				if (OverlayManager.OVERHEAD_TEXT_COLOURS[textIndex] == 11) {
					int elapsedTicks = 150 - OverlayManager.OVERHEAD_TEXT_TICKS_LEFT[textIndex];
					if (elapsedTicks < 50) {
						colour = 16777215 - elapsedTicks * 327685;
					} else if (elapsedTicks < 100) {
						colour = (elapsedTicks - 50) * 327685 + 65280;
					} else if (elapsedTicks < 150) {
						colour = 16777215 - (elapsedTicks - 100) * 327680;
					}
				}
				int textArgb = colour | 0xFF000000;
				if (OverlayManager.OVERHEAD_TEXT_EFFECTS[textIndex] == 0) {
					MiniMenu.minimenuHeader.renderCentered(textX + clipLeft, textArgb, string3, -16777216, textY + clipTop);
					leftX -= textWidth >> 1;
					rightX -= textWidth >> 1;
				}
				if (OverlayManager.OVERHEAD_TEXT_EFFECTS[textIndex] == 1) {
					rightX -= textWidth >> 1;
					leftX -= textWidth >> 1;
					MiniMenu.minimenuHeader.renderCenteredWave(textArgb, textY + clipTop, string3, clipLeft + textX, SceneDrawer.timesSceneRendered);
					bottomY += 5;
					topY -= 5;
				}
				if (OverlayManager.OVERHEAD_TEXT_EFFECTS[textIndex] == 2) {
					bottomY += 5;
					rightX -= (textWidth >> 1) - 5;
					MiniMenu.minimenuHeader.renderCenteredWave2(string3, textArgb, SceneDrawer.timesSceneRendered, textY + clipTop, clipLeft - -textX);
					topY -= 5;
					leftX -= (textWidth >> 1) + 5;
				}
				if (OverlayManager.OVERHEAD_TEXT_EFFECTS[textIndex] == 3) {
					rightX -= textWidth >> 1;
					leftX -= textWidth >> 1;
					bottomY += 7;
					MiniMenu.minimenuHeader.renderCenteredShake(SceneDrawer.timesSceneRendered, clipTop + textY, string3, textArgb, 150 - OverlayManager.OVERHEAD_TEXT_TICKS_LEFT[textIndex], textX + clipLeft);
					topY -= 7;
				}
				@Pc(2858) int local2858;
				if (OverlayManager.OVERHEAD_TEXT_EFFECTS[textIndex] == 4) {
					int textScrollX = (150 - OverlayManager.OVERHEAD_TEXT_TICKS_LEFT[textIndex]) * (FontMetrics.defaultFontMetrics.getWidthNoSprites(string3) + 100) / 150;
					ClientState.activeToolkit.T(clipLeft + textX - 50, clipTop, textX + clipLeft + 50, clipTop - -height);
					leftX += 50 - textScrollX;
					rightX += 50 - textScrollX;
					MiniMenu.minimenuHeader.drawString(string3, textArgb, clipTop + textY, clipLeft - (-textX - 50) - textScrollX, -16777216);
					ClientState.activeToolkit.KA(clipLeft, clipTop, clipWidth + clipLeft, clipTop + height);
				}
				if (OverlayManager.OVERHEAD_TEXT_EFFECTS[textIndex] == 5) {
					int elapsedTicks = 150 - OverlayManager.OVERHEAD_TEXT_TICKS_LEFT[textIndex];
					int slideOffsetY = 0;
					if (elapsedTicks < 25) {
						slideOffsetY = elapsedTicks - 25;
					} else if (elapsedTicks > 125) {
						slideOffsetY = elapsedTicks - 125;
					}
					int fontHeight = FontMetrics.defaultFontMetrics.paddingBottom + FontMetrics.defaultFontMetrics.paddingTop;
					ClientState.activeToolkit.T(clipLeft, textY + clipTop - fontHeight - 1, clipWidth + clipLeft, textY + clipTop + 5);
					leftX -= textWidth >> 1;
					bottomY += slideOffsetY;
					MiniMenu.minimenuHeader.renderCentered(textX + clipLeft, textArgb, string3, -16777216, slideOffsetY + clipTop + textY);
					topY += slideOffsetY;
					rightX -= textWidth >> 1;
					ClientState.activeToolkit.KA(clipLeft, clipTop, clipWidth + clipLeft, height + clipTop);
				}
			} else {
				leftX -= textWidth >> 1;
				rightX -= textWidth >> 1;
				MiniMenu.minimenuHeader.renderCentered(textX + clipLeft, -256, string3, -16777216, textY + clipTop);
			}
			OrthoMode.markOrthoTilesDirty(leftX, rightX + 1, topY, bottomY + 1);
		}
	}
	@OriginalMember(owner = "client!ok", name = "e", descriptor = "[I")
	public static final int[] HITMARK_SPRITE_OFFSETS = new int[4];

	@OriginalMember(owner = "client!qk", name = "a", descriptor = "(IBIIIIIILclient!kda;Lclient!kda;)V")
	public static void drawOverheadSprite(@OriginalArg(0) int stackIndex, @OriginalArg(3) int viewportHeight, @OriginalArg(4) int viewportY, @OriginalArg(6) int viewportWidth, @OriginalArg(7) int viewportX, @OriginalArg(8) PathingEntity pathingEntity, @OriginalArg(9) PathingEntity pathingEntity2) {
		@Pc(7) int spriteId = pathingEntity.getOverheadSprite();
		if (spriteId == -1) {
			return;
		}
		@Pc(20) NativeSprite sprite = (NativeSprite) OverlayManager.aClass169_7.get((long) spriteId);
		if (sprite == null) {
			@Pc(27) SpriteType[] indexedImages = SpriteType.decodeGroup(Resource.SPRITES, spriteId);
			if (indexedImages == null) {
				return;
			}
			sprite = ClientState.activeToolkit.generateSprite(indexedImages[0], true);
			OverlayManager.aClass169_7.put(sprite, (long) spriteId);
		}
		OverlayManager.projectToScreenWithOffset(pathingEntity2.level, 0, viewportHeight >> 1, pathingEntity2.x, pathingEntity2.getSize() * 256, pathingEntity2.z, viewportWidth >> 1);
		@Pc(73) int baseX = viewportX + OverlayManager.projectedScreenPosition[0] - 18;
		@Pc(83) int baseY = OverlayManager.projectedScreenPosition[1] + viewportY - 54 - 16;
		@Pc(91) int x = baseX + stackIndex / 4 * 18;
		@Pc(99) int y = baseY + stackIndex % 4 * 18;
		sprite.render(x, y);
		if (pathingEntity == pathingEntity2) {
			ClientState.activeToolkit.drawOpaqueRectangle(18, y - 1, -256, x - 1, 18);
		}
		OrthoMode.markOrthoTilesDirty(x - 1, x + 18, y - 1, y - -18);
		@Pc(143) EntityOverhead overhead = EntityOverheadAllocator.allocate();
		overhead.maxX = x + 16;
		overhead.minY = y;
		overhead.pathingEntity = pathingEntity;
		overhead.maxY = y + 16;
		overhead.minX = x;
		EntityOverheadAllocator.aClass325_7.offer(overhead);
	}

	@OriginalMember(owner = "client!lh", name = "f", descriptor = "I")
	public static int overheadTextCount = 0;

	@OriginalMember(owner = "client!en", name = "i", descriptor = "Lclient!jr;")
	public static final LRUCache aClass169_22 = new LRUCache(4);
	@OriginalMember(owner = "client!bg", name = "N", descriptor = "Lclient!jr;")
	public static final LRUCache aClass169_7 = new LRUCache(8);

	@OriginalMember(owner = "client!ie", name = "a", descriptor = "(IIIIILclient!kda;B)V")
	public static void projectEntityToScreen(@OriginalArg(0) int heightOffset, @OriginalArg(1) int halfWidth, @OriginalArg(4) int halfHeight, @OriginalArg(5) PathingEntity pathingEntity) {
		OverlayManager.projectToScreenWithOffset(pathingEntity.level, heightOffset, halfHeight, pathingEntity.x, 0, pathingEntity.z, halfWidth);
	}
	@OriginalMember(owner = "client!hm", name = "f", descriptor = "Lclient!jr;")
	public static final LRUCache aClass169_17 = new LRUCache(4);

	@OriginalMember(owner = "client!mv", name = "g", descriptor = "[I")
	public static final int[] OVERHEAD_CHAT_COLOURS = new int[] { 16776960, 16711680, 65280, 65535, 16711935, 16777215 };
	@OriginalMember(owner = "client!kha", name = "a", descriptor = "(IIIIIIIII)V")
	public static void projectToScreen(@OriginalArg(0) int halfWidth, @OriginalArg(1) int heightOffset, @OriginalArg(4) int halfHeight, @OriginalArg(5) int level, @OriginalArg(6) int z, @OriginalArg(8) int x) {
		OverlayManager.projectToScreenWithOffset(level, heightOffset, halfHeight, x, 0, z, halfWidth);
	}

	@OriginalMember(owner = "client!lp", name = "a", descriptor = "(IIIIIIIBII)V")
	public static void projectToScreenWithOffset(@OriginalArg(0) int level, @OriginalArg(2) int heightOffset, @OriginalArg(3) int halfHeight, @OriginalArg(4) int x, @OriginalArg(5) int offsetX, @OriginalArg(6) int z, @OriginalArg(8) int halfWidth) {
		if (x < 512 || z < 512 || x > (MapRegion.sceneSizeX - 2) * 512 || (MapRegion.sceneSizeY - 2) * 512 < z) {
			OverlayManager.projectedScreenPosition[0] = OverlayManager.projectedScreenPosition[1] = -1;
			return;
		}
		@Pc(50) int y = Scene.tileHeightForAbsolutePosition(x, level, z) - heightOffset;
		if (OrthoMode.orthoEnabled) {
			OrthoMode.applyOrthoViewport(true);
		} else {
			ClientState.aClass30_3.translate(offsetX, 0, 0);
			ClientState.activeToolkit.setCamera(ClientState.aClass30_3);
		}
		if (OrthoMode.orthographicProjection) {
			ClientState.activeToolkit.HA(x, y, z, HintArrow.scaledOrthoZoom, OverlayManager.projectedScreenPosition);
		} else {
			ClientState.activeToolkit.da(x, y, z, OverlayManager.projectedScreenPosition);
		}
		if (OrthoMode.orthoEnabled) {
			OrthoMode.restoreOrthoCamera();
		} else {
			ClientState.aClass30_3.translate(-offsetX, 0, 0);
			ClientState.activeToolkit.setCamera(ClientState.aClass30_3);
		}
	}

	@OriginalMember(owner = "client!vn", name = "M", descriptor = "I")
	public static int chatEffectsMode = 0;

	@OriginalMember(owner = "client!vu", name = "t", descriptor = "[I")
	public static final int[] projectedScreenPosition = new int[3];
}

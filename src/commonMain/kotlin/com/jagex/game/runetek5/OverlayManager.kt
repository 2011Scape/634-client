// The warnings of the original (deobfuscated) code, left as it was: the build treats warnings as errors.
@file:Suppress("UNCHECKED_CAST")

package com.jagex.game.runetek5

import com.jagex.game.runetek5.cache.node.lru.LRUCache
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.client.event.keyboard.FriendOrSelfCheck
import com.jagex.game.runetek5.client.menu.MiniMenu
import com.jagex.game.runetek5.config.Resource
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.config.defaults.EntityDefaults
import com.jagex.game.runetek5.config.hitsplat.HitSplatType
import com.jagex.game.runetek5.config.npc.NPCType
import com.jagex.game.runetek5.core.constants.HintArrowType
import com.jagex.game.runetek5.entity.EntityOverhead
import com.jagex.game.runetek5.entity.NPCEntity
import com.jagex.game.runetek5.entity.NPCEntityNode
import com.jagex.game.runetek5.entity.PathingEntity
import com.jagex.game.runetek5.entity.PlayerEntity
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer
import com.jagex.game.runetek5.graphics.font.FontManager
import com.jagex.game.runetek5.graphics.font.FontMetrics
import com.jagex.game.runetek5.graphics.gl.HitsplatSlotXOffsets
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.graphics.hint.HintArrow
import com.jagex.game.runetek5.graphics.sprite.NativeSprite
import com.jagex.game.runetek5.graphics.sprite.SpriteType
import com.jagex.game.runetek5.graphics.sprite.StaticMedia
import com.jagex.game.runetek5.interaction.chat.ChatBox
import com.jagex.game.runetek5.rendering.Renderers
import com.jagex.game.runetek5.scene.Scene
import com.jagex.game.runetek5.statics.OutPlayerIndexCount
import com.jagex.game.runetek5.vardomain.PlayerVarDomain
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

object OverlayManager {

	@OriginalMember(owner = "client!cf", name = "i", descriptor = "I")
	const val MAX_OVERHEAD_TEXTS: Int = 50

	@OriginalMember(owner = "client!cf", name = "a", descriptor = "[I")
	@JvmField
	val OVERHEAD_TEXT_EFFECTS: IntArray = IntArray(MAX_OVERHEAD_TEXTS)

	@OriginalMember(owner = "client!cf", name = "g", descriptor = "[I")
	@JvmField
	val OVERHEAD_TEXT_TICKS_LEFT: IntArray = IntArray(MAX_OVERHEAD_TEXTS)

	@OriginalMember(owner = "client!cf", name = "h", descriptor = "[I")
	@JvmField
	val OVERHEAD_TEXT_Y: IntArray = IntArray(MAX_OVERHEAD_TEXTS)

	@OriginalMember(owner = "client!cf", name = "l", descriptor = "[I")
	@JvmField
	val OVERHEAD_TEXT_COLOURS: IntArray = IntArray(MAX_OVERHEAD_TEXTS)

	@OriginalMember(owner = "client!cf", name = "m", descriptor = "[Ljava/lang/String;")
	@JvmField
	val OVERHEAD_TEXTS: Array<String?> = arrayOfNulls<String>(MAX_OVERHEAD_TEXTS)

	@OriginalMember(owner = "client!cf", name = "n", descriptor = "[I")
	@JvmField
	val OVERHEAD_TEXT_HALF_WIDTHS: IntArray = IntArray(MAX_OVERHEAD_TEXTS)

	@OriginalMember(owner = "client!cf", name = "q", descriptor = "[I")
	@JvmField
	val OVERHEAD_TEXT_X: IntArray = IntArray(MAX_OVERHEAD_TEXTS)

	@OriginalMember(owner = "client!qo", name = "b", descriptor = "(IIIIIII)V")
	@JvmStatic
	fun drawEntities(@OriginalArg(0) clipTop: Int, @OriginalArg(1) height: Int, @OriginalArg(3) clipLeft: Int, @OriginalArg(4) clipWidth: Int) {
		@Pc(7) val playerCount: Int = OutPlayerIndexCount.NUM_OUT_PLAYER_INDICES
		OverlayManager.overheadTextCount = 0
		@Pc(11) val outPlayerIndices: IntArray = PlayerUpdate.OUT_PLAYER_INDICES
		@Pc(177) var local177: Int
		@Pc(202) var local202: Int
		@Pc(259) var local259: Int
		@Pc(337) var local337: Int
		@Pc(340) var local340: Int
		@Pc(1157) var local1157: Int
		@Pc(1202) var local1202: Int
		@Pc(1204) var local1204: Int
		@Pc(439) var local439: Int
		@Pc(1060) var local1060: Int
		@Pc(576) var local576: Int
		@Pc(13) var i: Int = 0
		while (i < NPCList.NPC_UPDATE_INDEX + playerCount) {
			@Pc(17) var npcType: NPCType? = null
			@Pc(26) val pathingEntity: PathingEntity?
			if (playerCount > i) {
				pathingEntity = PlayerUpdate.players[outPlayerIndices[i]]
			} else {
				pathingEntity = (NPCList.NPC_MAP.get(NPCList.NPC_UPDATE_INDICES[i - playerCount].toLong()) as NPCEntityNode?)!!.npc
				npcType = (pathingEntity as NPCEntity?)!!.npcType
				if (npcType!!.alternativeIds != null) {
					npcType = npcType.getMultiNpc(PlayerVarDomain.playerVarProvider)
					if (npcType == null) {
						i++
						continue
					}
				}
			}
			if (pathingEntity!!.worldPriority >= 0 && (SceneDrawer.timesSceneRendered == pathingEntity.drawCycle || pathingEntity.level == PlayerUpdate.localPlayer!!.level)) {
				OverlayManager.projectEntityToScreen(pathingEntity.getOverheadHeight(), clipWidth shr 1, height shr 1, pathingEntity)
				if (OverlayManager.projectedScreenPosition[0] >= 0) {
					// Not part of the original client: whether this actor adds a chat line (Actor.overheadTop)
					val textsBefore: Int = OverlayManager.overheadTextCount
					if (pathingEntity.hintDetails.text != null && (playerCount <= i || ChatBox.publicFilter == 0 || ChatBox.publicFilter == 3 || ChatBox.publicFilter == 1 && FriendOrSelfCheck.isFriendOrSelf((pathingEntity as PlayerEntity).displayName)) && OverlayManager.overheadTextCount < OverlayManager.MAX_OVERHEAD_TEXTS) {
						OverlayManager.OVERHEAD_TEXT_HALF_WIDTHS[OverlayManager.overheadTextCount] = FontMetrics.defaultFontMetrics!!.getWidthNoSprites(pathingEntity.hintDetails.text) / 2
						OverlayManager.OVERHEAD_TEXT_X[OverlayManager.overheadTextCount] = OverlayManager.projectedScreenPosition[0]
						OverlayManager.OVERHEAD_TEXT_Y[OverlayManager.overheadTextCount] = OverlayManager.projectedScreenPosition[1]
						OverlayManager.OVERHEAD_TEXT_COLOURS[OverlayManager.overheadTextCount] = pathingEntity.hintDetails.colorType
						OverlayManager.OVERHEAD_TEXT_EFFECTS[OverlayManager.overheadTextCount] = pathingEntity.hintDetails.drawType
						OverlayManager.OVERHEAD_TEXT_TICKS_LEFT[OverlayManager.overheadTextCount] = pathingEntity.hintDetails.ticksLeft
						OverlayManager.OVERHEAD_TEXTS[OverlayManager.overheadTextCount] = pathingEntity.hintDetails.text
						OverlayManager.overheadTextCount++
					}
					var overheadY: Int = OverlayManager.projectedScreenPosition[1] + clipTop
					@Pc(244) var sprites: Array<NativeSprite?>?
					@Pc(251) var indexedImages: Array<SpriteType?>?
					@Pc(309) var sprite: NativeSprite?
					if (pathingEntity.isHidden || pathingEntity.healthBarEndCycle <= ClientState.clientCycle) {
						overheadY -= kotlin.math.max(FontMetrics.defaultFontMetrics!!.paddingTop, StaticMedia.hitbarDefaultSprites!![0]!!.getHeight())
					} else {
						@Pc(192) var barSpriteIndex: Byte = 1
						var healthBarSpriteGroup: Int
						if (i >= playerCount) {
							healthBarSpriteGroup = npcType!!.healthBarSpriteGroup
							if (healthBarSpriteGroup == -1) {
								healthBarSpriteGroup = pathingEntity.getBasType()!!.healthBarSpriteGroup
							}
						} else {
							@Pc(219) val playerEntity: PlayerEntity? = PlayerUpdate.players[outPlayerIndices[i]]
							healthBarSpriteGroup = pathingEntity.getBasType()!!.healthBarSpriteGroup
							if (playerEntity!!.pMapdot) {
								barSpriteIndex = 2
							}
						}
						@Pc(231) var sprites2: Array<NativeSprite?>? = StaticMedia.hitbarDefaultSprites
						if (healthBarSpriteGroup != -1) {
							sprites = OverlayManager.aClass169_22.get(healthBarSpriteGroup.toLong()) as Array<NativeSprite?>?
							if (sprites == null) {
								indexedImages = SpriteType.decodeGroup(Resource.SPRITES, healthBarSpriteGroup)
								if (indexedImages != null) {
									sprites = arrayOfNulls<NativeSprite>(indexedImages.size)
									var imageIndex: Int = 0
									while (imageIndex < indexedImages.size) {
										sprites[imageIndex] = ClientState.activeToolkit!!.generateSprite(indexedImages[imageIndex], true)
										imageIndex++
									}
									OverlayManager.aClass169_22.put(sprites, healthBarSpriteGroup.toLong())
								}
							}
							if (sprites != null && sprites.size >= 2) {
								sprites2 = sprites
							}
						}
						if (barSpriteIndex >= sprites2!!.size) {
							barSpriteIndex = 1
						}
						@Pc(305) val sprite2: NativeSprite? = sprites2[0]
						sprite = sprites2[barSpriteIndex.toInt()]
						overheadY -= kotlin.math.max(FontMetrics.defaultFontMetrics!!.paddingTop, sprite2!!.getHeight())
						val healthBarX: Int = OverlayManager.projectedScreenPosition[0] + clipLeft - (sprite2.getWidth() shr 1)
						var healthFillWidth: Int = sprite2.getWidth() * pathingEntity.healthRatio / 255
						val healthBarHeight: Int = sprite2.getHeight()
						if (pathingEntity.healthRatio > 0 && healthFillWidth < 2) {
							healthFillWidth = 2
						}
						sprite2.render(healthBarX, overheadY)
						ClientState.activeToolkit!!.T(healthBarX, overheadY, healthFillWidth + healthBarX, healthBarHeight + overheadY)
						sprite!!.render(healthBarX, overheadY)
						ClientState.activeToolkit!!.KA(clipLeft, clipTop, clipLeft + clipWidth, height + clipTop)
						OrthoMode.markOrthoTilesDirty(healthBarX, healthBarX + sprite2.scaleWidth(), overheadY, overheadY + healthBarHeight)
					}
					overheadY -= 2
					if (!pathingEntity.isHidden) {
						@Pc(422) var sprite3: NativeSprite?
						@Pc(431) var sprite4: NativeSprite?
						if (pathingEntity.secondaryBarEndCycle > ClientState.clientCycle) {
							sprite3 = StaticMedia.timerbarDefaultSprites!![if (pathingEntity.secondaryBarAlternate) 2 else 0]
							sprite4 = StaticMedia.timerbarDefaultSprites!![if (pathingEntity.secondaryBarAlternate) 3 else 1]
							var secondaryBarSpriteGroup: Int
							if (pathingEntity is NPCEntity) {
								secondaryBarSpriteGroup = npcType!!.secondaryBarSpriteGroup
								if (secondaryBarSpriteGroup == -1) {
									secondaryBarSpriteGroup = pathingEntity.getBasType()!!.secondaryBarSpriteGroup
								}
							} else {
								secondaryBarSpriteGroup = pathingEntity.getBasType()!!.secondaryBarSpriteGroup
							}
							if (secondaryBarSpriteGroup != -1) {
								sprites = OverlayManager.aClass169_17.get(secondaryBarSpriteGroup.toLong()) as Array<NativeSprite?>?
								if (sprites == null) {
									indexedImages = SpriteType.decodeGroup(Resource.SPRITES, secondaryBarSpriteGroup)
									if (indexedImages != null) {
										sprites = arrayOfNulls<NativeSprite>(indexedImages.size)
										var imageIndex: Int = 0
										while (imageIndex < indexedImages.size) {
											sprites[imageIndex] = ClientState.activeToolkit!!.generateSprite(indexedImages[imageIndex], true)
											imageIndex++
										}
										OverlayManager.aClass169_17.put(sprites, secondaryBarSpriteGroup.toLong())
									}
								}
								if (sprites != null && sprites.size == 4) {
									sprite4 = sprites[if (pathingEntity.secondaryBarAlternate) 3 else 1]
									sprite3 = sprites[if (pathingEntity.secondaryBarAlternate) 2 else 0]
								}
							}
							@Pc(536) var remainingCycles: Int = pathingEntity.secondaryBarEndCycle - ClientState.clientCycle
							val timerFillWidth: Int
							if (pathingEntity.secondaryBarHoldCycles < remainingCycles) {
								remainingCycles -= pathingEntity.secondaryBarHoldCycles
								val elapsedCycles: Int = if (pathingEntity.secondaryBarStep == 0) 0 else pathingEntity.secondaryBarStep * ((pathingEntity.secondaryBarDuration - remainingCycles) / pathingEntity.secondaryBarStep)
								timerFillWidth = sprite3!!.getWidth() * elapsedCycles / pathingEntity.secondaryBarDuration
							} else {
								timerFillWidth = sprite3!!.getWidth()
							}
							val timerBarHeight: Int = sprite3.getHeight()
							overheadY -= timerBarHeight
							val timerBarX: Int = OverlayManager.projectedScreenPosition[0] + clipLeft - (sprite3.getWidth() shr 1)
							sprite3.render(timerBarX, overheadY)
							ClientState.activeToolkit!!.T(timerBarX, overheadY, timerBarX + timerFillWidth, overheadY - -timerBarHeight)
							sprite4!!.render(timerBarX, overheadY)
							ClientState.activeToolkit!!.KA(clipLeft, clipTop, clipWidth + clipLeft, clipTop - -height)
							OrthoMode.markOrthoTilesDirty(timerBarX, sprite3.scaleWidth() + timerBarX, overheadY, timerBarHeight + overheadY)
							overheadY -= 2
						}
						if (playerCount > i) {
							@Pc(653) val playerEntity2: PlayerEntity = pathingEntity as PlayerEntity
							if (playerEntity2.skullId != -1) {
								overheadY -= 25
								sprite4 = StaticMedia.headiconPkSprites!![playerEntity2.skullId]
								sprite4!!.render(clipLeft + OverlayManager.projectedScreenPosition[0] - 12, overheadY)
								OrthoMode.markOrthoTilesDirty(clipLeft + OverlayManager.projectedScreenPosition[0] - 12, clipLeft - -OverlayManager.projectedScreenPosition[0] + -12 + sprite4.scaleWidth(), overheadY, overheadY + sprite4.scaleHeight())
								overheadY -= 2
							}
							if (playerEntity2.prayerHeadIconId != -1) {
								overheadY -= 25
								sprite4 = StaticMedia.headiconPrayerSprites!![playerEntity2.prayerHeadIconId]
								sprite4!!.render(clipLeft + OverlayManager.projectedScreenPosition[0] - 12, overheadY)
								OrthoMode.markOrthoTilesDirty(OverlayManager.projectedScreenPosition[0] + clipLeft - 12, OverlayManager.projectedScreenPosition[0] + clipLeft - (-sprite4.scaleWidth() + 12), overheadY, overheadY + sprite4.scaleHeight())
								overheadY -= 2
							}
						} else if (npcType!!.headIcons >= 0 && StaticMedia.headiconPrayerSprites!!.size > npcType.headIcons) {
							sprite3 = StaticMedia.headiconPrayerSprites!![npcType.headIcons]
							overheadY -= 25
							sprite3!!.render(clipLeft + OverlayManager.projectedScreenPosition[0] - (sprite3.getWidth() shr 1), overheadY)
							OrthoMode.markOrthoTilesDirty(OverlayManager.projectedScreenPosition[0] + clipLeft - (sprite3.getWidth() shr 1), OverlayManager.projectedScreenPosition[0] + (clipLeft - (sprite3.getWidth() shr 1)) + sprite3.scaleWidth(), overheadY, sprite3.scaleHeight() + overheadY)
							overheadY -= 2
						}
					}
					// Not part of the original client: the top of what is drawn over the head (the health bar, the head icons and a
					// chat line) in the game view, for plugins (Actor.overheadTop, API 23)
					var overheadTop: Int = overheadY - clipTop
					if (OverlayManager.overheadTextCount > textsBefore) {
						overheadTop = kotlin.math.min(overheadTop, OverlayManager.projectedScreenPosition[1] - FontMetrics.defaultFontMetrics!!.paddingTop - 2)
					}
					pathingEntity.overheadTop = overheadTop
					pathingEntity.overheadTopCycle = ClientState.clientCycle
					@Pc(825) var hintArrows: Array<HintArrow?>
					@Pc(833) var hintArrow: HintArrow?
					if (pathingEntity !is PlayerEntity) {
						var maxArrowHeight: Int = 0
						hintArrows = PlayerUpdate.HINT_ARROWS
						var arrowIndex: Int = 0
						while (arrowIndex < hintArrows.size) {
							hintArrow = hintArrows[arrowIndex]
							if (hintArrow != null && hintArrow.targetType == HintArrowType.NPC && NPCList.NPC_UPDATE_INDICES[i - playerCount] == hintArrow.targetIndex) {
								sprite = StaticMedia.hintHeadiconSprites!![hintArrow.arrowSprite]
								if (maxArrowHeight < sprite!!.getHeight()) {
									maxArrowHeight = sprite.getHeight()
								}
								if (ClientState.clientCycle % 20 < 10) {
									sprite.render(clipLeft + OverlayManager.projectedScreenPosition[0] - 12, overheadY + -sprite.getHeight())
									OrthoMode.markOrthoTilesDirty(clipLeft + OverlayManager.projectedScreenPosition[0] - 12, OverlayManager.projectedScreenPosition[0] + clipLeft + -12 - -sprite.scaleWidth(), overheadY - sprite.getHeight(), -sprite.getHeight() + (overheadY - -sprite.scaleHeight()))
								}
							}
							arrowIndex++
						}
						if (maxArrowHeight > 0) {
						}
					} else if (i >= 0) {
						var maxArrowHeight: Int = 0
						hintArrows = PlayerUpdate.HINT_ARROWS
						var arrowIndex: Int = 0
						while (arrowIndex < hintArrows.size) {
							hintArrow = hintArrows[arrowIndex]
							if (hintArrow != null && hintArrow.targetType == 10 && hintArrow.targetIndex == outPlayerIndices[i]) {
								sprite = StaticMedia.hintHeadiconSprites!![hintArrow.arrowSprite]
								if (sprite!!.getHeight() > maxArrowHeight) {
									maxArrowHeight = sprite.getHeight()
								}
								sprite.render(OverlayManager.projectedScreenPosition[0] + clipLeft - 12, overheadY + -sprite.getHeight())
								OrthoMode.markOrthoTilesDirty(clipLeft + OverlayManager.projectedScreenPosition[0] - 12, clipLeft + (OverlayManager.projectedScreenPosition[0] + -12 - -sprite.scaleWidth()), overheadY - sprite.getHeight(), overheadY + -sprite.getHeight() - -sprite.scaleHeight())
							}
							arrowIndex++
						}
						if (maxArrowHeight > 0) {
						}
					}
					var hitIndex: Int = 0
					while (hitIndex < PathingEntity.maximumHits) {
						val hitsplatEndCycle: Int = pathingEntity.hitsplatCycles[hitIndex]
						val hitsplatId: Int = pathingEntity.activeHitsplatIds[hitIndex]
						@Pc(1067) var hitmarkType: HitSplatType? = null
						var visibleCycles: Int = 0
						if (hitsplatId >= 0) {
							if (ClientState.clientCycle >= hitsplatEndCycle) {
								hitIndex++
								continue
							}
							hitmarkType = TypeLists.aClass307_2!!.list(pathingEntity.activeHitsplatIds[hitIndex])
							visibleCycles = hitmarkType!!.cyclesVisible
						} else if (hitsplatEndCycle < 0) {
							hitIndex++
							continue
						}
						val soakHitsplatId: Int = pathingEntity.soakHitsplatIds[hitIndex]
						@Pc(1103) var hitmarkType2: HitSplatType? = null
						if (soakHitsplatId >= 0) {
							hitmarkType2 = TypeLists.aClass307_2!!.list(soakHitsplatId)
						}
						if (hitsplatEndCycle - visibleCycles <= ClientState.clientCycle) {
							val hitsplatHealthRatio: Int = pathingEntity.hitsplatHealthRatios[hitIndex]
							if (hitsplatHealthRatio >= 0) {
								pathingEntity.healthBarEndCycle = ClientState.clientCycle + 300
								pathingEntity.healthRatio = hitsplatHealthRatio
								pathingEntity.hitsplatHealthRatios[hitIndex] = -1
							}
							if (hitmarkType == null) {
								pathingEntity.hitsplatCycles[hitIndex] = -1
							} else {
								val hitsplatHeightOffset: Int = pathingEntity.getOverheadHeight() / 2
								OverlayManager.projectEntityToScreen(hitsplatHeightOffset, clipWidth shr 1, height shr 1, pathingEntity)
								if (OverlayManager.projectedScreenPosition[0] > -1) {
									OverlayManager.projectedScreenPosition[0] += HitsplatSlotXOffsets.hitsplatOffsetsX!![hitIndex]
									OverlayManager.projectedScreenPosition[1] += EntityDefaults.hitsplatOffsetsY!![hitIndex]
									var leftCapWidth: Int = 0
									var middleFillWidth: Int = 0
									@Pc(1206) var width: Int = 0
									@Pc(1208) var rightCapWidth: Int = 0
									@Pc(1210) var leftCapOffsetX: Int = 0
									@Pc(1212) var middleFillOffsetX: Int = 0
									@Pc(1214) var innerLeftOffsetX: Int = 0
									@Pc(1216) var rightCapOffsetX: Int = 0
									@Pc(1218) var sprite5: NativeSprite? = null
									@Pc(1220) var sprite6: NativeSprite? = null
									@Pc(1222) var sprite7: NativeSprite? = null
									@Pc(1224) var sprite8: NativeSprite? = null
									@Pc(1226) var soakLeftCapWidth: Int = 0
									@Pc(1228) var soakMiddleFillWidth: Int = 0
									@Pc(1230) var soakInnerLeftWidth: Int = 0
									@Pc(1232) var soakRightCapWidth: Int = 0
									@Pc(1234) var soakLeftCapOffsetX: Int = 0
									@Pc(1236) var soakMiddleFillOffsetX: Int = 0
									@Pc(1238) var soakInnerLeftOffsetX: Int = 0
									@Pc(1240) var soakRightCapOffsetX: Int = 0
									@Pc(1242) var maxSpriteHeight: Int = 0
									@Pc(1247) val sprite9: NativeSprite? = hitmarkType.getLeftCapSprite(ClientState.activeToolkit)
									@Pc(1255) var local1255: Int
									if (sprite9 != null) {
										leftCapWidth = sprite9.getWidth()
										val leftCapHeight: Int = sprite9.getHeight()
										sprite9.projectOffsets(OverlayManager.HITMARK_SPRITE_OFFSETS)
										if (leftCapHeight > 0) {
											maxSpriteHeight = leftCapHeight
										}
										leftCapOffsetX = OverlayManager.HITMARK_SPRITE_OFFSETS[0]
									}
									@Pc(1276) val sprite10: NativeSprite? = hitmarkType.getMiddleFillSprite(ClientState.activeToolkit)
									if (sprite10 != null) {
										middleFillWidth = sprite10.getWidth()
										val middleFillHeight: Int = sprite10.getHeight()
										sprite10.projectOffsets(OverlayManager.HITMARK_SPRITE_OFFSETS)
										if (maxSpriteHeight < middleFillHeight) {
											maxSpriteHeight = middleFillHeight
										}
										middleFillOffsetX = OverlayManager.HITMARK_SPRITE_OFFSETS[0]
									}
									@Pc(1305) val sprite11: NativeSprite? = hitmarkType.getInnerLeftSprite(ClientState.activeToolkit)
									if (sprite11 != null) {
										width = sprite11.getWidth()
										val innerLeftHeight: Int = sprite11.getHeight()
										if (innerLeftHeight > maxSpriteHeight) {
											maxSpriteHeight = innerLeftHeight
										}
										sprite11.projectOffsets(OverlayManager.HITMARK_SPRITE_OFFSETS)
										innerLeftOffsetX = OverlayManager.HITMARK_SPRITE_OFFSETS[0]
									}
									@Pc(1334) val sprite12: NativeSprite? = hitmarkType.getRightCapSprite(ClientState.activeToolkit)
									if (sprite12 != null) {
										rightCapWidth = sprite12.getWidth()
										val rightCapHeight: Int = sprite12.getHeight()
										sprite12.projectOffsets(OverlayManager.HITMARK_SPRITE_OFFSETS)
										if (rightCapHeight > maxSpriteHeight) {
											maxSpriteHeight = rightCapHeight
										}
										rightCapOffsetX = OverlayManager.HITMARK_SPRITE_OFFSETS[0]
									}
									if (hitmarkType2 != null) {
										sprite5 = hitmarkType2.getLeftCapSprite(ClientState.activeToolkit)
										if (sprite5 != null) {
											soakLeftCapWidth = sprite5.getWidth()
											val soakLeftCapHeight: Int = sprite5.getHeight()
											if (soakLeftCapHeight > maxSpriteHeight) {
												maxSpriteHeight = soakLeftCapHeight
											}
											sprite5.projectOffsets(OverlayManager.HITMARK_SPRITE_OFFSETS)
											soakLeftCapOffsetX = OverlayManager.HITMARK_SPRITE_OFFSETS[0]
										}
										sprite6 = hitmarkType2.getMiddleFillSprite(ClientState.activeToolkit)
										if (sprite6 != null) {
											soakMiddleFillWidth = sprite6.getWidth()
											val soakMiddleFillHeight: Int = sprite6.getHeight()
											if (soakMiddleFillHeight > maxSpriteHeight) {
												maxSpriteHeight = soakMiddleFillHeight
											}
											sprite6.projectOffsets(OverlayManager.HITMARK_SPRITE_OFFSETS)
											soakMiddleFillOffsetX = OverlayManager.HITMARK_SPRITE_OFFSETS[0]
										}
										sprite7 = hitmarkType2.getInnerLeftSprite(ClientState.activeToolkit)
										if (sprite7 != null) {
											soakInnerLeftWidth = sprite7.getWidth()
											val soakInnerLeftHeight: Int = sprite7.getHeight()
											sprite7.projectOffsets(OverlayManager.HITMARK_SPRITE_OFFSETS)
											if (maxSpriteHeight < soakInnerLeftHeight) {
												maxSpriteHeight = soakInnerLeftHeight
											}
											soakInnerLeftOffsetX = OverlayManager.HITMARK_SPRITE_OFFSETS[0]
										}
										sprite8 = hitmarkType2.getRightCapSprite(ClientState.activeToolkit)
										if (sprite8 != null) {
											soakRightCapWidth = sprite8.getWidth()
											val soakRightCapHeight: Int = sprite8.getHeight()
											if (soakRightCapHeight > maxSpriteHeight) {
												maxSpriteHeight = soakRightCapHeight
											}
											sprite8.projectOffsets(OverlayManager.HITMARK_SPRITE_OFFSETS)
											soakRightCapOffsetX = OverlayManager.HITMARK_SPRITE_OFFSETS[0]
										}
									}
									@Pc(1468) var font2: AbstractFontRenderer? = Renderers.FONT_RENDERER
									@Pc(1470) var font3: AbstractFontRenderer? = Renderers.FONT_RENDERER
									@Pc(1472) var fontMetrics: FontMetrics? = FontManager.worldMapAreaFont
									var fontId: Int = hitmarkType.fontId
									@Pc(1477) var fontMetrics2: FontMetrics? = FontManager.worldMapAreaFont
									@Pc(1488) var font4: AbstractFontRenderer?
									@Pc(1493) var fontMetrics3: FontMetrics?
									if (fontId >= 0) {
										font4 = FontManager.getFont(ClientState.activeToolkit, true, fontId)
										fontMetrics3 = FontManager.getFontMetrics(ClientState.activeToolkit, fontId)
										if (font4 != null && fontMetrics3 != null) {
											font2 = font4
											fontMetrics = fontMetrics3
										}
									}
									if (hitmarkType2 != null) {
										fontId = hitmarkType2.fontId
										if (fontId >= 0) {
											font4 = FontManager.getFont(ClientState.activeToolkit, true, fontId)
											fontMetrics3 = FontManager.getFontMetrics(ClientState.activeToolkit, fontId)
											if (font4 != null && fontMetrics3 != null) {
												fontMetrics2 = fontMetrics3
												font3 = font4
											}
										}
									}
									@Pc(1534) var string: String? = null
									@Pc(1538) var soakWidth: Int = 0
									@Pc(1546) val string2: String? = hitmarkType.getAmountText(pathingEntity.hitsplatText[hitIndex])
									@Pc(1551) val regularWidth: Int = fontMetrics!!.getWidthNoSprites(string2)
									if (hitmarkType2 != null) {
										string = hitmarkType2.getAmountText(pathingEntity.soakHitsplatText[hitIndex])
										soakWidth = fontMetrics2!!.getWidthNoSprites(string)
									}
									@Pc(1568) var regularTileCount: Int = 0
									if (middleFillWidth > 0) {
										regularTileCount = regularWidth / middleFillWidth + 1
									}
									@Pc(1578) var soakTileCount: Int = 0
									if (hitmarkType2 != null && soakMiddleFillWidth > 0) {
										soakTileCount = soakWidth / soakMiddleFillWidth + 1
									}
									@Pc(1590) var totalWidth: Int = 0
									if (leftCapWidth > 0) {
										totalWidth = leftCapWidth
									}
									totalWidth += 2
									@Pc(1601) val innerLeftStartX: Int = totalWidth
									if (width > 0) {
										totalWidth += width
									}
									@Pc(1609) val middleFillStartX: Int = totalWidth
									@Pc(1611) var textStartX: Int = totalWidth
									@Pc(1626) var local1626: Int
									if (middleFillWidth <= 0) {
										totalWidth += regularWidth
									} else {
										val regularTilesWidth: Int = middleFillWidth * regularTileCount
										textStartX = totalWidth + (regularTilesWidth - regularWidth) / 2
										totalWidth += regularTilesWidth
									}
									val rightCapStartX: Int = totalWidth
									if (rightCapWidth > 0) {
										totalWidth += rightCapWidth
									}
									@Pc(1652) var soakLeftCapStartX: Int = 0
									@Pc(1654) var soakInnerLeftStartX: Int = 0
									@Pc(1656) var soakMiddleFillStartX: Int = 0
									@Pc(1658) var soakRightCapStartX: Int = 0
									@Pc(1660) var soakTextStartX: Int = 0
									@Pc(1690) var local1690: Int
									if (hitmarkType2 != null) {
										totalWidth += 2
										soakLeftCapStartX = totalWidth
										if (soakLeftCapWidth > 0) {
											totalWidth += soakLeftCapWidth
										}
										totalWidth += 2
										soakInnerLeftStartX = totalWidth
										if (soakInnerLeftWidth > 0) {
											totalWidth += soakInnerLeftWidth
										}
										soakTextStartX = totalWidth
										soakMiddleFillStartX = totalWidth
										if (soakMiddleFillWidth > 0) {
											val soakTilesWidth: Int = soakTileCount * soakMiddleFillWidth
											soakTextStartX = totalWidth + (soakTilesWidth - soakWidth) / 2
											totalWidth += soakTilesWidth
										} else {
											totalWidth += soakWidth
										}
										soakRightCapStartX = totalWidth
										if (soakRightCapWidth > 0) {
											totalWidth += soakRightCapWidth
										}
									}
									val hitsplatCyclesLeft: Int = pathingEntity.hitsplatCycles[hitIndex] - ClientState.clientCycle
									@Pc(1739) val scrollX: Int = hitmarkType.scrollOffsetX - hitmarkType.scrollOffsetX * hitsplatCyclesLeft / hitmarkType.cyclesVisible
									@Pc(1751) val scrollY: Int = hitmarkType.scrollOffsetY * hitsplatCyclesLeft / hitmarkType.cyclesVisible - hitmarkType.scrollOffsetY
									@Pc(1765) val drawX: Int = clipLeft + OverlayManager.projectedScreenPosition[0] + scrollX - (totalWidth shr 1)
									@Pc(1776) val drawY: Int = clipTop + OverlayManager.projectedScreenPosition[1] + scrollY - 12
									@Pc(1778) var clipMinY: Int = drawY
									@Pc(1782) var clipMaxY: Int = drawY + maxSpriteHeight
									@Pc(1790) val textDrawY: Int = drawY + hitmarkType.textOffsetY + 15
									@Pc(1796) val textTopY: Int = textDrawY - fontMetrics.paddingTop
									@Pc(1802) val textBottomY: Int = textDrawY + fontMetrics.paddingBottom
									if (textTopY < drawY) {
										clipMinY = textTopY
									}
									if (clipMaxY < textBottomY) {
										clipMaxY = textBottomY
									}
									@Pc(1818) var soakTextDrawY: Int = 0
									@Pc(1834) var local1834: Int
									@Pc(1844) var local1844: Int
									if (hitmarkType2 != null) {
										soakTextDrawY = drawY + hitmarkType2.textOffsetY + 15
										val soakTextTopY: Int = soakTextDrawY - fontMetrics2!!.paddingTop
										if (soakTextTopY < clipMinY) {
											clipMinY = soakTextTopY
										}
										val soakTextBottomY: Int = fontMetrics2.paddingBottom + soakTextDrawY
										if (soakTextBottomY > clipMaxY) {
											clipMaxY = soakTextBottomY
										}
									}
									var alpha: Int = 255
									if (hitmarkType.fadeStartCycle >= 0) {
										alpha = (hitsplatCyclesLeft shl 8) / (hitmarkType.cyclesVisible - hitmarkType.fadeStartCycle)
									}
									if (alpha >= 0 && alpha < 255) {
										val alphaMask: Int = alpha shl 24
										@Pc(1884) val colourArgb: Int = alphaMask or 0xFFFFFF
										if (sprite9 != null) {
											sprite9.render(drawX - leftCapOffsetX, drawY, 0, colourArgb, 1)
										}
										if (sprite11 != null) {
											sprite11.render(innerLeftStartX + drawX - innerLeftOffsetX, drawY, 0, colourArgb, 1)
										}
										@Pc(1916) var tileIndex: Int
										if (sprite10 != null) {
											tileIndex = 0
											while (tileIndex < regularTileCount) {
												sprite10.render(middleFillStartX + drawX + middleFillWidth * tileIndex - middleFillOffsetX, drawY, 0, colourArgb, 1)
												tileIndex++
											}
										}
										if (sprite12 != null) {
											sprite12.render(rightCapStartX + drawX - rightCapOffsetX, drawY, 0, colourArgb, 1)
										}
										font2!!.drawString(string2, hitmarkType.color or alphaMask, textDrawY, textStartX + drawX, 0)
										if (hitmarkType2 != null) {
											if (sprite5 != null) {
												sprite5.render(drawX + soakLeftCapStartX - soakLeftCapOffsetX, drawY, 0, colourArgb, 1)
											}
											if (sprite7 != null) {
												sprite7.render(soakInnerLeftStartX + drawX - soakInnerLeftOffsetX, drawY, 0, colourArgb, 1)
											}
											if (sprite6 != null) {
												tileIndex = 0
												while (tileIndex < soakTileCount) {
													sprite6.render(tileIndex * soakMiddleFillWidth + drawX + soakMiddleFillStartX - soakMiddleFillOffsetX, drawY, 0, colourArgb, 1)
													tileIndex++
												}
											}
											if (sprite8 != null) {
												sprite8.render(drawX + soakRightCapStartX - soakRightCapOffsetX, drawY, 0, colourArgb, 1)
											}
											font3!!.drawString(string, alphaMask or hitmarkType2.color, soakTextDrawY, drawX + soakTextStartX, 0)
										}
									} else {
										if (sprite9 != null) {
											sprite9.render(drawX - leftCapOffsetX, drawY)
										}
										if (sprite11 != null) {
											sprite11.render(drawX + innerLeftStartX - innerLeftOffsetX, drawY)
										}
										if (sprite10 != null) {
											var tileIndex: Int = 0
											while (tileIndex < regularTileCount) {
												sprite10.render(middleFillStartX + drawX + tileIndex * middleFillWidth - middleFillOffsetX, drawY)
												tileIndex++
											}
										}
										if (sprite12 != null) {
											sprite12.render(rightCapStartX + drawX - rightCapOffsetX, drawY)
										}
										font2!!.drawString(string2, hitmarkType.color or 0xFF000000.toInt(), textDrawY, drawX + textStartX, 0)
										if (hitmarkType2 != null) {
											if (sprite5 != null) {
												sprite5.render(drawX + soakLeftCapStartX - soakLeftCapOffsetX, drawY)
											}
											if (sprite7 != null) {
												sprite7.render(soakInnerLeftStartX + drawX - soakInnerLeftOffsetX, drawY)
											}
											if (sprite6 != null) {
												var tileIndex: Int = 0
												while (tileIndex < soakTileCount) {
													sprite6.render(tileIndex * soakMiddleFillWidth + drawX + soakMiddleFillStartX - soakMiddleFillOffsetX, drawY)
													tileIndex++
												}
											}
											if (sprite8 != null) {
												sprite8.render(soakRightCapStartX + drawX - soakRightCapOffsetX, drawY)
											}
											font3!!.drawString(string, hitmarkType2.color or 0xFF000000.toInt(), soakTextDrawY, soakTextStartX + drawX, 0)
										}
									}
									OrthoMode.markOrthoTilesDirty(drawX, drawX + totalWidth, clipMinY, clipMaxY + 1)
								}
							}
						}
						hitIndex++
					}
				}
			}
			i++
		}
		@Pc(2228) var local2228: Int
		@Pc(2222) var j: Int = 0
		while (j < PathingEntity.overheadSpriteCount) {
			val hostIndex: Int = PathingEntity.ENTITY_INDEX_REFERENCES[j]
			@Pc(2241) val pathingEntity2: PathingEntity?
			if (hostIndex >= 2048) {
				pathingEntity2 = (NPCList.NPC_MAP.get((hostIndex - 2048).toLong()) as NPCEntityNode?)!!.npc
			} else {
				pathingEntity2 = PlayerUpdate.players[hostIndex]
			}
			val spriteOwnerIndex: Int = PathingEntity.NPC_INDEX_REFERENCES[j]
			@Pc(2266) val pathingEntity3: PathingEntity?
			if (spriteOwnerIndex >= 2048) {
				pathingEntity3 = (NPCList.NPC_MAP.get((spriteOwnerIndex - 2048).toLong()) as NPCEntityNode?)!!.npc
			} else {
				pathingEntity3 = PlayerUpdate.players[spriteOwnerIndex]
			}
			OverlayManager.drawOverheadSprite(--pathingEntity2!!.overheadStackCount, height, clipTop, clipWidth, clipLeft, pathingEntity3, pathingEntity2)
			j++
		}
		val textLineHeight: Int = FontMetrics.defaultFontMetrics!!.paddingBottom + FontMetrics.defaultFontMetrics!!.paddingTop + 2
		var textIndex: Int = 0
		while (textIndex < OverlayManager.overheadTextCount) {
			val textX: Int = OverlayManager.OVERHEAD_TEXT_X[textIndex]
			var textY: Int = OverlayManager.OVERHEAD_TEXT_Y[textIndex]
			val textHalfWidth: Int = OverlayManager.OVERHEAD_TEXT_HALF_WIDTHS[textIndex]
			@Pc(2323) var overlapping: Boolean = true
			while (overlapping) {
				overlapping = false
				var otherTextIndex: Int = 0
				while (otherTextIndex < textIndex) {
					if (textY + 2 > OverlayManager.OVERHEAD_TEXT_Y[otherTextIndex] + -textLineHeight && textY - textLineHeight < OverlayManager.OVERHEAD_TEXT_Y[otherTextIndex] + 2 && textX - textHalfWidth < OverlayManager.OVERHEAD_TEXT_X[otherTextIndex] - -OverlayManager.OVERHEAD_TEXT_HALF_WIDTHS[otherTextIndex] && OverlayManager.OVERHEAD_TEXT_X[otherTextIndex] - OverlayManager.OVERHEAD_TEXT_HALF_WIDTHS[otherTextIndex] < textX + textHalfWidth && textY > OverlayManager.OVERHEAD_TEXT_Y[otherTextIndex] - textLineHeight) {
						textY = OverlayManager.OVERHEAD_TEXT_Y[otherTextIndex] - textLineHeight
						overlapping = true
					}
					otherTextIndex++
				}
			}
			OverlayManager.OVERHEAD_TEXT_Y[textIndex] = textY
			@Pc(2415) val string3: String? = OverlayManager.OVERHEAD_TEXTS[textIndex]
			val textWidth: Int = FontMetrics.defaultFontMetrics!!.getWidthNoSprites(string3)
			var leftX: Int = textX + clipLeft
			var topY: Int = textY + clipTop - FontMetrics.defaultFontMetrics!!.paddingTop
			var rightX: Int = leftX + textWidth
			@Pc(2445) var bottomY: Int = textY + clipTop + FontMetrics.defaultFontMetrics!!.paddingBottom
			if (OverlayManager.chatEffectsMode == 0) {
				@Pc(2475) var colour: Int = 16776960
				if (OverlayManager.OVERHEAD_TEXT_COLOURS[textIndex] < 6) {
					colour = OverlayManager.OVERHEAD_CHAT_COLOURS[OverlayManager.OVERHEAD_TEXT_COLOURS[textIndex]]
				}
				if (OverlayManager.OVERHEAD_TEXT_COLOURS[textIndex] == 6) {
					colour = if (SceneDrawer.timesSceneRendered % 20 >= 10) 16776960 else 16711680
				}
				if (OverlayManager.OVERHEAD_TEXT_COLOURS[textIndex] == 7) {
					colour = if (SceneDrawer.timesSceneRendered % 20 < 10) 255 else 65535
				}
				if (OverlayManager.OVERHEAD_TEXT_COLOURS[textIndex] == 8) {
					colour = if (SceneDrawer.timesSceneRendered % 20 >= 10) 8454016 else 45056
				}
				@Pc(2546) var local2546: Int
				if (OverlayManager.OVERHEAD_TEXT_COLOURS[textIndex] == 9) {
					val elapsedTicks: Int = 150 - OverlayManager.OVERHEAD_TEXT_TICKS_LEFT[textIndex]
					if (elapsedTicks < 50) {
						colour = elapsedTicks * 1280 + 16711680
					} else if (elapsedTicks < 100) {
						colour = 16384000 + 16776960 - elapsedTicks * 327680
					} else if (elapsedTicks < 150) {
						colour = (elapsedTicks - 100) * 5 + 65280
					}
				}
				if (OverlayManager.OVERHEAD_TEXT_COLOURS[textIndex] == 10) {
					val elapsedTicks: Int = 150 - OverlayManager.OVERHEAD_TEXT_TICKS_LEFT[textIndex]
					if (elapsedTicks < 50) {
						colour = elapsedTicks * 5 + 16711680
					} else if (elapsedTicks < 100) {
						colour = 33095935 - elapsedTicks * 327680
					} else if (elapsedTicks < 150) {
						colour = elapsedTicks * 327680 + 500 - elapsedTicks * 5 - 32767745
					}
				}
				if (OverlayManager.OVERHEAD_TEXT_COLOURS[textIndex] == 11) {
					val elapsedTicks: Int = 150 - OverlayManager.OVERHEAD_TEXT_TICKS_LEFT[textIndex]
					if (elapsedTicks < 50) {
						colour = 16777215 - elapsedTicks * 327685
					} else if (elapsedTicks < 100) {
						colour = (elapsedTicks - 50) * 327685 + 65280
					} else if (elapsedTicks < 150) {
						colour = 16777215 - (elapsedTicks - 100) * 327680
					}
				}
				val textArgb: Int = colour or 0xFF000000.toInt()
				if (OverlayManager.OVERHEAD_TEXT_EFFECTS[textIndex] == 0) {
					if (string3 != null && string3.contains("<img=")) {
						// Not part of the original client: a plugin's icons in the line (Emojis), drawn as the chat box draws them
						MiniMenu.minimenuHeader!!.renderCenteredWithSprites(textX + clipLeft, textArgb, string3, -16777216, textY + clipTop, StaticMedia.chatIcons, StaticMedia.chatScaleHeights)
					} else {
						MiniMenu.minimenuHeader!!.renderCentered(textX + clipLeft, textArgb, string3, -16777216, textY + clipTop)
					}
					leftX -= textWidth shr 1
					rightX -= textWidth shr 1
				}
				if (OverlayManager.OVERHEAD_TEXT_EFFECTS[textIndex] == 1) {
					rightX -= textWidth shr 1
					leftX -= textWidth shr 1
					MiniMenu.minimenuHeader!!.renderCenteredWave(textArgb, textY + clipTop, string3, clipLeft + textX, SceneDrawer.timesSceneRendered)
					bottomY += 5
					topY -= 5
				}
				if (OverlayManager.OVERHEAD_TEXT_EFFECTS[textIndex] == 2) {
					bottomY += 5
					rightX -= (textWidth shr 1) - 5
					MiniMenu.minimenuHeader!!.renderCenteredWave2(string3, textArgb, SceneDrawer.timesSceneRendered, textY + clipTop, clipLeft - -textX)
					topY -= 5
					leftX -= (textWidth shr 1) + 5
				}
				if (OverlayManager.OVERHEAD_TEXT_EFFECTS[textIndex] == 3) {
					rightX -= textWidth shr 1
					leftX -= textWidth shr 1
					bottomY += 7
					MiniMenu.minimenuHeader!!.renderCenteredShake(SceneDrawer.timesSceneRendered, clipTop + textY, string3, textArgb, 150 - OverlayManager.OVERHEAD_TEXT_TICKS_LEFT[textIndex], textX + clipLeft)
					topY -= 7
				}
				@Pc(2858) var local2858: Int
				if (OverlayManager.OVERHEAD_TEXT_EFFECTS[textIndex] == 4) {
					val textScrollX: Int = (150 - OverlayManager.OVERHEAD_TEXT_TICKS_LEFT[textIndex]) * (FontMetrics.defaultFontMetrics!!.getWidthNoSprites(string3) + 100) / 150
					ClientState.activeToolkit!!.T(clipLeft + textX - 50, clipTop, textX + clipLeft + 50, clipTop - -height)
					leftX += 50 - textScrollX
					rightX += 50 - textScrollX
					MiniMenu.minimenuHeader!!.drawString(string3, textArgb, clipTop + textY, clipLeft - (-textX - 50) - textScrollX, -16777216)
					ClientState.activeToolkit!!.KA(clipLeft, clipTop, clipWidth + clipLeft, clipTop + height)
				}
				if (OverlayManager.OVERHEAD_TEXT_EFFECTS[textIndex] == 5) {
					val elapsedTicks: Int = 150 - OverlayManager.OVERHEAD_TEXT_TICKS_LEFT[textIndex]
					var slideOffsetY: Int = 0
					if (elapsedTicks < 25) {
						slideOffsetY = elapsedTicks - 25
					} else if (elapsedTicks > 125) {
						slideOffsetY = elapsedTicks - 125
					}
					val fontHeight: Int = FontMetrics.defaultFontMetrics!!.paddingBottom + FontMetrics.defaultFontMetrics!!.paddingTop
					ClientState.activeToolkit!!.T(clipLeft, textY + clipTop - fontHeight - 1, clipWidth + clipLeft, textY + clipTop + 5)
					leftX -= textWidth shr 1
					bottomY += slideOffsetY
					MiniMenu.minimenuHeader!!.renderCentered(textX + clipLeft, textArgb, string3, -16777216, slideOffsetY + clipTop + textY)
					topY += slideOffsetY
					rightX -= textWidth shr 1
					ClientState.activeToolkit!!.KA(clipLeft, clipTop, clipWidth + clipLeft, height + clipTop)
				}
			} else {
				leftX -= textWidth shr 1
				rightX -= textWidth shr 1
				MiniMenu.minimenuHeader!!.renderCentered(textX + clipLeft, -256, string3, -16777216, textY + clipTop)
			}
			OrthoMode.markOrthoTilesDirty(leftX, rightX + 1, topY, bottomY + 1)
			textIndex++
		}
	}

	@OriginalMember(owner = "client!ok", name = "e", descriptor = "[I")
	@JvmField
	val HITMARK_SPRITE_OFFSETS: IntArray = IntArray(4)

	@OriginalMember(owner = "client!qk", name = "a", descriptor = "(IBIIIIIILclient!kda;Lclient!kda;)V")
	@JvmStatic
	fun drawOverheadSprite(@OriginalArg(0) stackIndex: Int, @OriginalArg(3) viewportHeight: Int, @OriginalArg(4) viewportY: Int, @OriginalArg(6) viewportWidth: Int, @OriginalArg(7) viewportX: Int, @OriginalArg(8) pathingEntity: PathingEntity?, @OriginalArg(9) pathingEntity2: PathingEntity?) {
		@Pc(7) val spriteId: Int = pathingEntity!!.getOverheadSprite()
		if (spriteId == -1) {
			return
		}
		@Pc(20) var sprite: NativeSprite? = OverlayManager.aClass169_7.get(spriteId.toLong()) as NativeSprite?
		if (sprite == null) {
			@Pc(27) val indexedImages: Array<SpriteType?>? = SpriteType.decodeGroup(Resource.SPRITES, spriteId)
			if (indexedImages == null) {
				return
			}
			sprite = ClientState.activeToolkit!!.generateSprite(indexedImages[0], true)
			OverlayManager.aClass169_7.put(sprite, spriteId.toLong())
		}
		OverlayManager.projectToScreenWithOffset(pathingEntity2!!.level.toInt(), 0, viewportHeight shr 1, pathingEntity2.x, pathingEntity2.getSize() * 256, pathingEntity2.z, viewportWidth shr 1)
		@Pc(73) val baseX: Int = viewportX + OverlayManager.projectedScreenPosition[0] - 18
		@Pc(83) val baseY: Int = OverlayManager.projectedScreenPosition[1] + viewportY - 54 - 16
		@Pc(91) val x: Int = baseX + stackIndex / 4 * 18
		@Pc(99) val y: Int = baseY + stackIndex % 4 * 18
		sprite!!.render(x, y)
		if (pathingEntity === pathingEntity2) {
			ClientState.activeToolkit!!.drawOpaqueRectangle(18, y - 1, -256, x - 1, 18)
		}
		OrthoMode.markOrthoTilesDirty(x - 1, x + 18, y - 1, y - -18)
		@Pc(143) val overhead: EntityOverhead = EntityOverheadAllocator.allocate()
		overhead.maxX = x + 16
		overhead.minY = y
		overhead.pathingEntity = pathingEntity
		overhead.maxY = y + 16
		overhead.minX = x
		EntityOverheadAllocator.aClass325_7.offer(overhead)
	}

	@OriginalMember(owner = "client!lh", name = "f", descriptor = "I")
	@JvmField
	var overheadTextCount: Int = 0

	@OriginalMember(owner = "client!en", name = "i", descriptor = "Lclient!jr;")
	@JvmField
	val aClass169_22: LRUCache = LRUCache(4)

	@OriginalMember(owner = "client!bg", name = "N", descriptor = "Lclient!jr;")
	@JvmField
	val aClass169_7: LRUCache = LRUCache(8)

	@OriginalMember(owner = "client!ie", name = "a", descriptor = "(IIIIILclient!kda;B)V")
	@JvmStatic
	fun projectEntityToScreen(@OriginalArg(0) heightOffset: Int, @OriginalArg(1) halfWidth: Int, @OriginalArg(4) halfHeight: Int, @OriginalArg(5) pathingEntity: PathingEntity?) {
		OverlayManager.projectToScreenWithOffset(pathingEntity!!.level.toInt(), heightOffset, halfHeight, pathingEntity.x, 0, pathingEntity.z, halfWidth)
	}

	@OriginalMember(owner = "client!hm", name = "f", descriptor = "Lclient!jr;")
	@JvmField
	val aClass169_17: LRUCache = LRUCache(4)

	@OriginalMember(owner = "client!mv", name = "g", descriptor = "[I")
	@JvmField
	val OVERHEAD_CHAT_COLOURS: IntArray = intArrayOf(16776960, 16711680, 65280, 65535, 16711935, 16777215)

	@OriginalMember(owner = "client!kha", name = "a", descriptor = "(IIIIIIIII)V")
	@JvmStatic
	fun projectToScreen(@OriginalArg(0) halfWidth: Int, @OriginalArg(1) heightOffset: Int, @OriginalArg(4) halfHeight: Int, @OriginalArg(5) level: Int, @OriginalArg(6) z: Int, @OriginalArg(8) x: Int) {
		OverlayManager.projectToScreenWithOffset(level, heightOffset, halfHeight, x, 0, z, halfWidth)
	}

	/**
	 * Not part of the original client: projects a scene point at any height ([y] as the models have
	 * it, up being negative) into [projectedScreenPosition], view-relative; false when it is off the
	 * screen. The camera is set the way [projectToScreenWithOffset] sets it.
	 */
	@JvmStatic
	fun projectWorldPoint(x: Int, y: Int, z: Int): Boolean {
		val toolkit: Toolkit = ClientState.activeToolkit ?: return false
		if (OrthoMode.orthoEnabled) {
			OrthoMode.applyOrthoViewport(true)
		} else {
			toolkit.setCamera(ClientState.aClass30_3)
		}
		if (OrthoMode.orthographicProjection) {
			toolkit.HA(x, y, z, HintArrow.scaledOrthoZoom, OverlayManager.projectedScreenPosition)
		} else {
			toolkit.da(x, y, z, OverlayManager.projectedScreenPosition)
		}
		if (OrthoMode.orthoEnabled) {
			OrthoMode.restoreOrthoCamera()
		}
		return OverlayManager.projectedScreenPosition[0] >= 0
	}

	@OriginalMember(owner = "client!lp", name = "a", descriptor = "(IIIIIIIBII)V")
	@JvmStatic
	fun projectToScreenWithOffset(@OriginalArg(0) level: Int, @OriginalArg(2) heightOffset: Int, @OriginalArg(3) halfHeight: Int, @OriginalArg(4) x: Int, @OriginalArg(5) offsetX: Int, @OriginalArg(6) z: Int, @OriginalArg(8) halfWidth: Int) {
		if (x < 512 || z < 512 || x > (MapRegion.sceneSizeX - 2) * 512 || (MapRegion.sceneSizeY - 2) * 512 < z) {
			OverlayManager.projectedScreenPosition[1] = -1
			OverlayManager.projectedScreenPosition[0] = OverlayManager.projectedScreenPosition[1]
			return
		}
		@Pc(50) val y: Int = Scene.tileHeightForAbsolutePosition(x, level, z) - heightOffset
		if (OrthoMode.orthoEnabled) {
			OrthoMode.applyOrthoViewport(true)
		} else {
			ClientState.aClass30_3!!.translate(offsetX, 0, 0)
			ClientState.activeToolkit!!.setCamera(ClientState.aClass30_3)
		}
		if (OrthoMode.orthographicProjection) {
			ClientState.activeToolkit!!.HA(x, y, z, HintArrow.scaledOrthoZoom, OverlayManager.projectedScreenPosition)
		} else {
			ClientState.activeToolkit!!.da(x, y, z, OverlayManager.projectedScreenPosition)
		}
		if (OrthoMode.orthoEnabled) {
			OrthoMode.restoreOrthoCamera()
		} else {
			ClientState.aClass30_3!!.translate(-offsetX, 0, 0)
			ClientState.activeToolkit!!.setCamera(ClientState.aClass30_3)
		}
	}

	@OriginalMember(owner = "client!vn", name = "M", descriptor = "I")
	@JvmField
	var chatEffectsMode: Int = 0

	@OriginalMember(owner = "client!vu", name = "t", descriptor = "[I")
	@JvmField
	val projectedScreenPosition: IntArray = IntArray(3)
}

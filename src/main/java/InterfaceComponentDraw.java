import com.jagex.game.runetek5.config.rsinterface.MediaType;
import com.jagex.game.runetek5.client.input.camera.Camera;
import com.jagex.game.runetek5.TextureCombineSource;
import com.jagex.game.runetek5.InterfaceRectangles;
import com.jagex.game.runetek5.config.rsinterface.itemcontainer.ItemContainer;
import com.jagex.game.runetek5.CutsceneManager;
import com.jagex.game.runetek5.InterfaceManager;
import com.jagex.game.runetek5.Minimap;
import com.jagex.game.runetek5.entity.PlayerEntity;
import com.jagex.game.runetek5.config.sequence.SeqType;
import com.jagex.game.runetek5.ClipComponentsDebug;
import com.jagex.game.runetek5.BloomComponentContentType;
import com.jagex.game.runetek5.DraggedComponentLayer;
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMap;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerAppearance;
import com.jagex.game.runetek5.BrightnessTableBuilder;
import com.jagex.game.runetek5.config.rsinterface.Component;
import com.jagex.game.runetek5.config.rsinterface.SubInterface;
import com.jagex.game.runetek5.config.objtype.ObjStackability;
import com.jagex.game.runetek5.config.item.ItemType;
import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer;
import com.jagex.game.runetek5.graphics.font.FontMetrics;
import com.jagex.game.runetek5.graphics.model.Model;
import com.jagex.game.runetek5.graphics.sprite.NativeSprite;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.features.ClientFeatures;
import com.jagex.game.runetek5.config.TypeLists;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;
import com.jagex.game.runetek5.graphics.ifcomponent.InterfaceRenderer;
import com.jagex.game.runetek5.graphics.model.particle.ParticleSystemManager;
import com.jagex.game.runetek5.graphics.sprite.StaticMedia;
import com.jagex.game.runetek5.scene.culling.Culling;
import com.jagex.game.runetek5.rendering.Renderers;
import com.jagex.game.runetek5.interaction.chat.friendschat.FriendsChat;
import com.jagex.game.runetek5.config.Resource;
import com.jagex.game.runetek5.client.input.mouse.MouseRecord;
import com.jagex.game.runetek5.config.midi.VorbisNode;
import com.jagex.game.runetek5.sound.SoundResourceManager;
import com.jagex.game.runetek5.tools.LocalizedText;
import com.jagex.game.runetek5.graphics.font.FontManager;
import com.jagex.game.runetek5.interaction.chat.ChatBox;
import com.jagex.game.runetek5.sound.player.area.SoundEffectPlayer;
import com.jagex.game.runetek5.InterfaceDrag;
import com.jagex.game.runetek5.graphics.ifcomponent.InterfaceComponentRenderer;
import com.jagex.game.runetek5.MapRegion;
import com.jagex.game.runetek5.OrthoMode;
import com.jagex.game.runetek5.statics.OutboundByteCounter;
import com.jagex.game.runetek5.utils.Utils;
import com.jagex.game.runetek5.vardomain.PlayerVarDomain;
import com.jagex.game.runetek5.config.rsinterface.ComponentType;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.Js5TextureSource;
import com.jagex.game.runetek5.client.GameShell;

public final class InterfaceComponentDraw {

	@OriginalMember(owner = "client!jia", name = "a", descriptor = "(IIZIIIIZ[Lclient!at;II)V")
	public static void drawComponents(@OriginalArg(0) int clippingLeft, @OriginalArg(1) int offsetY, @OriginalArg(2) boolean isTopLevelLayer, @OriginalArg(3) int offsetX, @OriginalArg(4) int clippingTop, @OriginalArg(5) int parentCacheReference, @OriginalArg(6) int clippingRight, @OriginalArg(8) Component[] components, @OriginalArg(9) int idHash, @OriginalArg(10) int clippingBottom) {
		ClientState.activeToolkit.KA(clippingLeft, clippingTop, clippingRight, clippingBottom);
		for (@Pc(17) int i = 0; i < components.length; i++) {
			@Pc(23) Component component = components[i];
			if (component != null && (component.parentId == idHash || idHash == -1412584499 && InterfaceDrag.component == component)) {
				@Pc(44) int dirtyMinX = offsetX + component.x;
				@Pc(49) int dirtyMinY = component.y + offsetY;
				@Pc(56) int dirtyMaxX = component.width + dirtyMinX + 1;
				@Pc(63) int dirtyMaxY = component.height + dirtyMinY + 1;
				@Pc(89) int componentReference;
				if (parentCacheReference == -1) {
					InterfaceRectangles.INTERFACE_BG_COMPONENTS[Camera.componentBackgroundCount].setBounds(offsetX + component.x, offsetY + component.y, component.width, component.height);
					componentReference = Camera.componentBackgroundCount++;
				} else {
					componentReference = parentCacheReference;
				}
				component.lastRenderedCycle = ClientState.clientCycle;
				component.cacheReference = componentReference;
				if (!InterfaceRenderer.isComponentHidden(component)) {
					if (component.contentType != 0) {
						InterfaceManager.prepareSequenceOnInterface(component);
					}
					@Pc(114) int startX = component.x + offsetX;
					@Pc(119) int startY = component.y + offsetY;
					@Pc(121) int orthoOffsetX = 0;
					@Pc(123) int orthoOffsetY = 0;
					if (OrthoMode.orthoEnabled) {
						orthoOffsetX = OrthoMode.getOrthoOffsetX();
						orthoOffsetY = OrthoMode.getOrthoDrawOffsetY();
					}
					@Pc(134) int transparency = component.transparency;
					if (InterfaceManager.qaOpTest && (InterfaceManager.getComponentSettings(component).settingsHash != 0 || component.type == ComponentType.CONTAINER) && transparency > 127) {
						transparency = 127;
					}
					@Pc(188) int local188;
					@Pc(194) int local194;
					if (component == InterfaceDrag.component) {
						if (idHash != -1412584499 && (InterfaceDrag.DRAG_RENDER_OPAQUE == component.dragType || Component.DRAG_RENDER_TRANSLUCENT == component.dragType)) {
							InterfaceDrag.draggedComponentOffsetY = offsetY;
							InterfaceDrag.draggedComponentOffsetX = offsetX;
							DraggedComponentLayer.components = components;
							continue;
						}
						if (InterfaceDrag.isDragging && InterfaceDrag.dragParentFound) {
							int dragX = MouseRecord.mouseRecorder.getMouseX() + orthoOffsetX;
							int dragY = MouseRecord.mouseRecorder.getMouseY() + orthoOffsetY;
							dragX -= InterfaceDrag.dragOffsetX;
							dragY -= InterfaceDrag.dragOffsetY;
							if (InterfaceDrag.dragParentX > dragX) {
								dragX = InterfaceDrag.dragParentX;
							}
							if (dragY < FriendsChat.dragParentY) {
								dragY = FriendsChat.dragParentY;
							}
							if (component.width + dragX > InterfaceDrag.aClass24_7.width + InterfaceDrag.dragParentX) {
								dragX = InterfaceDrag.dragParentX + InterfaceDrag.aClass24_7.width - component.width;
							}
							if (dragY + component.height > InterfaceDrag.aClass24_7.height + FriendsChat.dragParentY) {
								dragY = FriendsChat.dragParentY + InterfaceDrag.aClass24_7.height - component.height;
							}
							startX = dragX;
							startY = dragY;
						}
						if (Component.DRAG_RENDER_TRANSLUCENT == component.dragType) {
							transparency = 128;
						}
					}
					@Pc(313) int endX;
					@Pc(324) int endY;
					@Pc(275) int rightX;
					@Pc(280) int local280;
					int clipStartX;
					int clipStartY;
					if (component.type == ComponentType.TYPE_2) {
						clipStartX = clippingLeft;
						clipStartY = clippingTop;
						endY = clippingBottom;
						endX = clippingRight;
					} else {
						rightX = startX + component.width;
						int bottomY = startY + component.height;
						if (component.type == ComponentType.LINE) {
							bottomY++;
							rightX++;
						}
						clipStartX = clippingLeft < startX ? startX : clippingLeft;
						clipStartY = startY <= clippingTop ? clippingTop : startY;
						endX = clippingRight <= rightX ? clippingRight : rightX;
						endY = bottomY < clippingBottom ? bottomY : clippingBottom;
					}
					if (clipStartX < endX && clipStartY < endY) {
						@Pc(631) int local631;
						@Pc(642) int local642;
						@Pc(732) int local732;
						@Pc(595) int local595;
						@Pc(629) int local629;
						if (component.contentType != 0) {
							if (component.contentType == Component.CONTENT_TYPE_GAME_WINDOW || Component.anInt2217 == component.contentType) {
								InterfaceComponentRenderer.setChatDisplay(component, startY, startX);
								if (!OrthoMode.orthoEnabled) {
									ClientState.drawGameArea(Component.anInt2217 == component.contentType, component.width, startX, component.height, startY);
									ClientState.activeToolkit.KA(clippingLeft, clippingTop, clippingRight, clippingBottom);
								}
								InterfaceRectangles.IS_COMPONENT_ON_TOP[componentReference] = true;
								continue;
							}
							if (Component.CONTENT_TYPE_MINIMAP == component.contentType) {
								if (component.getClippingMask(ClientState.activeToolkit) != null) {
									SoundEffectPlayer.updatePlayers();
									Minimap.drawMinimap(component, ClientState.activeToolkit, startX, startY);
									InterfaceRectangles.RECTANGLE_NEEDS_FLIP[componentReference] = true;
									ClientState.activeToolkit.KA(clippingLeft, clippingTop, clippingRight, clippingBottom);
									if (OrthoMode.orthoEnabled) {
										if (isTopLevelLayer) {
											OrthoMode.markOrthoTilesDirty(dirtyMinX, dirtyMaxX, dirtyMinY, dirtyMaxY);
										} else {
											OrthoMode.expandDirtyArea(dirtyMaxY, dirtyMinX, dirtyMaxX, dirtyMinY);
										}
									}
								}
								continue;
							}
							if (Component.CONTENT_TYPE_COMPASS == component.contentType) {
								if (component.getClippingMask(ClientState.activeToolkit) != null) {
									Minimap.drawCompass(startY, component, startX);
									InterfaceRectangles.RECTANGLE_NEEDS_FLIP[componentReference] = true;
									ClientState.activeToolkit.KA(clippingLeft, clippingTop, clippingRight, clippingBottom);
									if (OrthoMode.orthoEnabled) {
										if (isTopLevelLayer) {
											OrthoMode.markOrthoTilesDirty(dirtyMinX, dirtyMaxX, dirtyMinY, dirtyMaxY);
										} else {
											OrthoMode.expandDirtyArea(dirtyMaxY, dirtyMinX, dirtyMaxX, dirtyMinY);
										}
									}
								}
								continue;
							}
							if (Component.CONTENT_TYPE_WORLDMAP == component.contentType) {
								WorldMapViewDraw.drawWorldmap(startX, component.width, component.height, ClientState.activeToolkit, startY, Js5TextureSource.anInterface4_10);
								InterfaceRectangles.IS_COMPONENT_ON_TOP[componentReference] = true;
								ClientState.activeToolkit.KA(clippingLeft, clippingTop, clippingRight, clippingBottom);
								continue;
							}
							if (Component.WORLD_LIST_VERSION == component.contentType) {
								WorldMap.drawWorldmapOverview(component.height, ClientState.activeToolkit, startX, component.width, startY);
								InterfaceRectangles.IS_COMPONENT_ON_TOP[componentReference] = true;
								ClientState.activeToolkit.KA(clippingLeft, clippingTop, clippingRight, clippingBottom);
								continue;
							}
							if (component.contentType == Component.CONTENT_TYPE_DEBUG_INFORMATION) {
								if (!Culling.wallCullingEnabled && !ToolkitProfiler.showProfiling) {
									continue;
								}
								rightX = component.width + startX;
								int textY = startY + 15;
								if (OrthoMode.orthoEnabled) {
									if (isTopLevelLayer) {
										OrthoMode.markOrthoTilesDirty(dirtyMinX, dirtyMaxX, dirtyMinY, dirtyMaxY);
									} else {
										OrthoMode.expandDirtyArea(dirtyMaxY, dirtyMinX, dirtyMaxX, dirtyMinY);
									}
								}
								if (Culling.wallCullingEnabled) {
									int fpsColour = -256;
									if (Camera.fps < 20) {
										fpsColour = -65536;
									}
									FontManager.aClass68_4.renderRightAligned("Fps:" + Camera.fps, textY, fpsColour, rightX, -1);
									textY += 15;
									@Pc(619) Runtime runtime = Runtime.getRuntime();
									int usedMemoryKb = (int) ((runtime.totalMemory() - runtime.freeMemory()) / 1024L);
									int memoryColour = -256;
									if (usedMemoryKb > 98304) {
										memoryColour = -65536;
										if (GameShell.force64Mb) {
											TypeLists.removeSoftReferences();
											for (int gcAttempt = 0; gcAttempt < 10; gcAttempt++) {
												System.gc();
											}
											usedMemoryKb = (int) ((runtime.totalMemory() - runtime.freeMemory()) / 1024L);
											if (usedMemoryKb > 65536) {
												ChatBox.writeGameMessage("WARNING: Memory usage over 64MB! Please inform whoever is responsible for the content/area you are using/in.");
											}
										}
									}
									FontManager.aClass68_4.renderRightAligned("Mem:" + usedMemoryKb + "k", textY, memoryColour, rightX, -1);
									textY += 15;
									FontManager.aClass68_4.renderRightAligned("In:" + OutboundByteCounter.inboundBytesPerSecond + "B/s Out:" + OutboundByteCounter.outboundBytesPerSecond + "B/s", textY, -256, rightX, -1);
									textY += 15;
									int offheapKb = ClientState.activeToolkit.E() / 1024;
									FontManager.aClass68_4.renderRightAligned("Offheap:" + offheapKb + "k", textY, offheapKb <= 65536 ? -256 : -65536, rightX, -1);
									textY += 15;
									int totalGroups = 0;
									@Pc(734) int verifiedGroups = 0;
									@Pc(736) int totalVerifiedGroups = 0;
									for (@Pc(738) int j = 0; j < 37; j++) {
										if (Resource.remoteFileWorkersByIndex[j] != null) {
											totalGroups += Resource.remoteFileWorkersByIndex[j].getTotalGroups();
											verifiedGroups += Resource.remoteFileWorkersByIndex[j].getVerifiedGroups();
											totalVerifiedGroups += Resource.remoteFileWorkersByIndex[j].getTotalVerifiedGroups();
										}
									}
									@Pc(780) int totalVerifiedPercent = totalVerifiedGroups * 100 / totalGroups;
									@Pc(786) int verifiedPercentHundredths = verifiedGroups * 10000 / totalGroups;
									@Pc(806) String string = "Cache:" + Utils.formatLocalisedNumber(2, true, 0, (long) verifiedPercentHundredths) + "% (" + totalVerifiedPercent + "%)";
									Renderers.FONT_RENDERER.renderRightAligned(string, textY, -256, rightX, -1);
									textY += 12;
									if (ClientFeatures.DEBUG) {
										// Not part of the original client: the local player's tile, region and chunk.
										int debugX = (PlayerUpdate.localPlayer.x >> 9) + MapRegion.sceneBaseX;
										int debugY = (PlayerUpdate.localPlayer.z >> 9) + MapRegion.sceneBaseY;
										Renderers.FONT_RENDERER.renderRightAligned("Coordinates: " + debugX + ", " + debugY + ", " + PlayerUpdate.localPlayer.level, textY, -256, rightX, -1);
										textY += 12;
										Renderers.FONT_RENDERER.renderRightAligned("Region id: " + ((debugX >> 6 << 8) + (debugY >> 6)) + " (" + (debugX >> 6) + ", " + (debugY >> 6) + ")", textY, -256, rightX, -1);
										textY += 12;
										Renderers.FONT_RENDERER.renderRightAligned("Chunk: " + (debugX >> 3) + ", " + (debugY >> 3), textY, -256, rightX, -1);
										textY += 12;
									}
								}
								if (ParticleSystemManager.renderedParticleCount > 0) {
									Renderers.FONT_RENDERER.renderRightAligned("Particles: " + CutsceneManager.runningParticleSystemCount + " / " + ParticleSystemManager.renderedParticleCount, textY, -256, rightX, -1);
								}
								textY += 12;
								if (ToolkitProfiler.showProfiling) {
									Renderers.FONT_RENDERER.renderRightAligned("Polys: " + ClientState.activeToolkit.I() + " Models: " + ClientState.activeToolkit.M(), textY, -256, rightX, -1);
									textY += 12;
									Renderers.FONT_RENDERER.renderRightAligned("Ls: " + ToolkitProfiler.anInt8154 + " La: " + ToolkitProfiler.anInt6496 + " NPC: " + ToolkitProfiler.debugNpcCount + " Pl: " + ToolkitProfiler.debugPlayerCount, textY, -256, rightX, -1);
									ToolkitProfiler.resetEntityDebugCounters();
									textY += 12;
								}
								InterfaceRectangles.IS_COMPONENT_ON_TOP[componentReference] = true;
								continue;
							}
						}
						if (component.type == ComponentType.CONTAINER) {
							if (BloomComponentContentType.BLOOM_CONTENT_TYPE == component.contentType && ClientState.activeToolkit.bloom()) {
								ClientState.activeToolkit.method7261(startX, startY, component.width, component.height);
							}
							drawComponents(clipStartX, startY - component.scrollY, isTopLevelLayer, startX - component.scrollX, clipStartY, componentReference, endX, components, component.idHash, endY);
							if (component.slotChildren != null) {
								drawComponents(clipStartX, startY - component.scrollY, isTopLevelLayer, startX - component.scrollX, clipStartY, componentReference, endX, component.slotChildren, component.idHash, endY);
							}
							@Pc(974) SubInterface subInterface = (SubInterface) InterfaceManager.OPEN_INTERFACES.get((long) component.idHash);
							if (subInterface != null) {
								InterfaceRootDraw.drawInterface(startY, subInterface.interfaceId, clipStartY, componentReference, startX, endX, endY, clipStartX);
							}
							if (BloomComponentContentType.BLOOM_CONTENT_TYPE == component.contentType && ClientState.activeToolkit.bloom()) {
								ClientState.activeToolkit.compositeBloomEffects();
							}
							ClientState.activeToolkit.KA(clippingLeft, clippingTop, clippingRight, clippingBottom);
						}
						if (InterfaceRectangles.PREV_COMPONENT_ON_TOP[componentReference] || InterfaceManager.interfaceDebugMode > 1) {
							if (component.type == ComponentType.FIGURE) {
								if (transparency == 0) {
									if (component.filled) {
										ClientState.activeToolkit.aa(startX, startY, component.width, component.height, component.color, 0);
									} else {
										ClientState.activeToolkit.outlineRect(startX, startY, component.width, component.height, component.color, 0);
									}
								} else if (component.filled) {
									ClientState.activeToolkit.aa(startX, startY, component.width, component.height, component.color & 0xFFFFFF | 255 - (transparency & 0xFF) << 24, 1);
								} else {
									ClientState.activeToolkit.outlineRect(startX, startY, component.width, component.height, component.color & 0xFFFFFF | 255 - (transparency & 0xFF) << 24, 1);
								}
								if (OrthoMode.orthoEnabled) {
									if (isTopLevelLayer) {
										OrthoMode.markOrthoTilesDirty(dirtyMinX, dirtyMaxX, dirtyMinY, dirtyMaxY);
									} else {
										OrthoMode.expandDirtyArea(dirtyMaxY, dirtyMinX, dirtyMaxX, dirtyMinY);
									}
								}
							} else if (component.type == ComponentType.TEXT) {
								@Pc(1130) AbstractFontRenderer font = component.getFontRenderer(ClientState.activeToolkit);
								if (font != null) {
									int textColour = component.color;
									@Pc(1145) String string2 = component.text;
									if (component.containerItemId != -1) {
										@Pc(1155) ItemType objType = TypeLists.OBJ.list(component.containerItemId);
										string2 = objType.name;
										if (string2 == null) {
											string2 = "null";
										}
										if ((objType.stackable == ObjStackability.ALWAYS || component.itemStackSize != 1) && component.itemStackSize != -1) {
											string2 = "<col=ff9040>" + string2 + "</col> x" + InterfaceManager.formatAmount(component.itemStackSize);
										}
									}
									if (component.videoFileId != -1) {
										string2 = SoundResourceManager.getVideoSubtitleText(component.videoFileId);
										if (string2 == null) {
											string2 = "";
										}
									}
									if (InterfaceManager.activeDialogueContinue == component) {
										string2 = LocalizedText.aClass351_33.get(ClientState.language);
										textColour = component.color;
									}
									if (ClipComponentsDebug.clipComponents) {
										ClientState.activeToolkit.T(startX, startY, component.width + startX, component.height + startY);
									}
									font.renderText(null, component.height, null, 0, StaticMedia.nameIconSprites, 255 - (transparency & 0xFF) << 24 | textColour, startY, component.width, component.textVerticalAli, string2, 0, startX, component.textHorizontalAli, component.shadow ? 255 - (transparency & 0xFF) << 24 : -1, component.maxTextLines, component.lineSpacing);
									if (ClipComponentsDebug.clipComponents) {
										ClientState.activeToolkit.KA(clippingLeft, clippingTop, clippingRight, clippingBottom);
									}
									if (string2.trim().length() > 0) {
										if (!ClipComponentsDebug.clipComponents) {
											@Pc(1320) FontMetrics fontMetrics = FontManager.getFontMetrics(ClientState.activeToolkit, component.fontId);
											int textWidth = fontMetrics.getTextWidthMultiline(string2, component.width, StaticMedia.nameIconSprites);
											int textHeight = fontMetrics.getTextHeightMultiline(StaticMedia.nameIconSprites, component.lineSpacing, component.width, string2);
											if (OrthoMode.orthoEnabled) {
												if (isTopLevelLayer) {
													OrthoMode.markOrthoTilesDirty(startX, textWidth + startX, startY, startY + textHeight);
												} else {
													OrthoMode.expandDirtyArea(startY + textHeight, startX, startX + textWidth, startY);
												}
											}
										} else if (OrthoMode.orthoEnabled) {
											if (isTopLevelLayer) {
												OrthoMode.markOrthoTilesDirty(dirtyMinX, dirtyMaxX, dirtyMinY, dirtyMaxY);
											} else {
												OrthoMode.expandDirtyArea(dirtyMaxY, dirtyMinX, dirtyMaxX, dirtyMinY);
											}
										}
									}
								} else if (Component.missingMedia) {
									Component.redrawComponent(component);
								}
							} else {
								@Pc(1489) int local1489;
								if (component.type == ComponentType.SPRITE) {
									if (component.skyboxId >= 0) {
										component.calculateLighting(TypeLists.SUN, TypeLists.SKYBOX).renderSkybox(0, ClientState.activeToolkit, startX, component.width, startY, component.sunRotationY << 3, component.sunRotationX << 3, 0, component.height);
									} else {
										@Pc(1432) NativeSprite sprite;
										if (component.containerItemId != -1) {
											@Pc(1414) PlayerAppearance playerModel = component.wearCol ? PlayerUpdate.localPlayer.playerModel : null;
											sprite = TypeLists.OBJ.getCachedSprite(component.borderThickness, component.containerItemId, component.itemStackSize, component.spriteShadow | 0xFF000000, component.renderStack, ClientState.activeToolkit, playerModel);
										} else if (component.videoFileId == -1) {
											sprite = component.getSprite(ClientState.activeToolkit);
										} else {
											sprite = VorbisNode.getVideoFrame(ClientState.activeToolkit, component.videoFileId);
										}
										if (sprite != null) {
											int spriteWidth = sprite.scaleWidth();
											int spriteHeight = sprite.scaleHeight();
											int spriteTint = 255 - (transparency & 0xFF) << 24 | (component.color == 0 ? 16777215 : component.color & 0xFFFFFF);
											if (component.tiling) {
												ClientState.activeToolkit.T(startX, startY, startX + component.width, component.height + startY);
												if (component.angle2d != 0) {
													int tileColumns = (spriteWidth + component.width - 1) / spriteWidth;
													int tileRows = (spriteHeight + component.height - 1) / spriteHeight;
													for (int column = 0; column < tileColumns; column++) {
														for (int row = 0; row < tileRows; row++) {
															if (component.color == 0) {
																sprite.drawRotatedCenter((float) spriteWidth / 2.0F + (float) (startX + spriteWidth * column), (float) spriteHeight / 2.0F + (float) (row * spriteHeight + startY), 4096, component.angle2d);
															} else {
																sprite.drawRotatedCenterColored((float) spriteWidth / 2.0F + (float) (spriteWidth * column + startX), (float) spriteHeight / 2.0F + (float) (startY + row * spriteHeight), 4096, component.angle2d, spriteTint);
															}
														}
													}
												} else if (component.color == 0 && transparency == 0) {
													sprite.drawTiled(startX, startY, component.width, component.height);
												} else {
													sprite.drawScaledColored(startX, startY, component.width, component.height, 0, spriteTint, 1);
												}
												ClientState.activeToolkit.KA(clippingLeft, clippingTop, clippingRight, clippingBottom);
											} else if (component.color == 0 && transparency == 0) {
												if (component.angle2d != 0) {
													sprite.drawRotatedCenter((float) startX + (float) component.width / 2.0F, (float) component.height / 2.0F + (float) startY, component.width * 4096 / spriteWidth, component.angle2d);
												} else if (spriteWidth == component.width && component.height == spriteHeight) {
													sprite.render(startX, startY);
												} else {
													sprite.drawTintedScaled(startX, startY, component.width, component.height);
												}
											} else if (component.angle2d != 0) {
												sprite.drawRotatedCenterColored((float) startX + (float) component.width / 2.0F, (float) startY + (float) component.height / 2.0F, component.width * 4096 / spriteWidth, component.angle2d, spriteTint);
											} else if (spriteWidth == component.width && component.height == spriteHeight) {
												sprite.render(startX, startY, 0, spriteTint, 1);
											} else {
												sprite.drawCircle(startX, startY, component.width, component.height, 0, spriteTint, 1);
											}
										} else if (Component.missingMedia) {
											Component.redrawComponent(component);
										}
									}
									if (OrthoMode.orthoEnabled) {
										if (isTopLevelLayer) {
											OrthoMode.markOrthoTilesDirty(dirtyMinX, dirtyMaxX, dirtyMinY, dirtyMaxY);
										} else {
											OrthoMode.expandDirtyArea(dirtyMaxY, dirtyMinX, dirtyMaxX, dirtyMinY);
										}
									}
								} else if (component.type == ComponentType.MODEL) {
									Minimap.prepareLights();
									@Pc(1807) Model model = null;
									int modelCentreOffsetY = 0;
									@Pc(1840) SeqType seqType;
									@Pc(1848) PlayerAppearance playerModel2;
									if (component.containerItemId != -1) {
										@Pc(1820) ItemType objType2 = TypeLists.OBJ.list(component.containerItemId);
										if (objType2 != null) {
											objType2 = objType2.getStackVariant(component.itemStackSize);
											seqType = component.animation == -1 ? null : TypeLists.SEQ.list(component.animation);
											playerModel2 = component.wearCol ? PlayerUpdate.localPlayer.playerModel : null;
											model = objType2.getAnimatedModel(playerModel2, seqType, ClientState.activeToolkit, 2048, component.currentFrameDuration, 1, component.currentFrameId, component.nextFrameId);
											if (model == null) {
												Component.redrawComponent(component);
											} else {
												modelCentreOffsetY = -model.fa() >> 1;
											}
										}
									} else if (component.mediaType == MediaType.PLAYER_MODEL) {
										int playerIndex = component.primaryMediaId;
										if (playerIndex >= 0 && playerIndex < 2048) {
											@Pc(1900) PlayerEntity playerEntity = PlayerUpdate.players[playerIndex];
											@Pc(1913) SeqType seqType2 = component.animation == -1 ? null : TypeLists.SEQ.list(component.animation);
											if (playerEntity != null && (playerIndex == PlayerUpdate.myPlayerIndex || Utils.hashString(playerEntity.displayName) == component.secondaryMediaId)) {
												model = playerEntity.playerModel.getBodyModel(PlayerVarDomain.playerVarProvider, null, null, TypeLists.OBJ, 0, seqType2, component.nextFrameId, -1, null, 2048, TypeLists.IDK, component.currentFrameDuration, ClientState.activeToolkit, TypeLists.NPC, TypeLists.SEQ, 0, component.currentFrameId, 0, TypeLists.BAS);
											}
										}
									} else if (component.mediaType == MediaType.ITEM_CONTAINER_MALE || component.mediaType == MediaType.ITEM_CONTAINER_FEMALE) {
										@Pc(2039) ItemContainer itemContainer = ItemContainer.getContainer(component.primaryMediaId, false);
										seqType = component.animation == -1 ? null : TypeLists.SEQ.list(component.animation);
										if (itemContainer != null) {
											playerModel2 = component.wearCol ? PlayerUpdate.localPlayer.playerModel : null;
											model = itemContainer.getAnimatedBodyModel(seqType, playerModel2, component.secondaryMediaId, component.currentFrameId, component.currentFrameDuration, component.nextFrameId, ClientState.activeToolkit, component.mediaType == MediaType.ITEM_CONTAINER_FEMALE);
										}
									} else if (component.animation == -1) {
										model = component.getAnimatedMediaModel(TypeLists.IDK, TypeLists.SEQ, 2048, ClientState.activeToolkit, -1, 0, TypeLists.OBJ, PlayerVarDomain.playerVarProvider, PlayerUpdate.localPlayer.playerModel, null, TypeLists.BAS, TypeLists.NPC, -1);
										if (model == null && Component.missingMedia) {
											Component.redrawComponent(component);
										}
									} else {
										@Pc(1978) SeqType seqType3 = TypeLists.SEQ.list(component.animation);
										model = component.getAnimatedMediaModel(TypeLists.IDK, TypeLists.SEQ, 2048, ClientState.activeToolkit, component.currentFrameId, component.currentFrameDuration, TypeLists.OBJ, PlayerVarDomain.playerVarProvider, PlayerUpdate.localPlayer.playerModel, seqType3, TypeLists.BAS, TypeLists.NPC, component.nextFrameId);
										if (model == null && Component.missingMedia) {
											Component.redrawComponent(component);
										}
									}
									if (model != null) {
										int zoomX;
										if (component.aspectWidth > 0) {
											zoomX = (component.width << 9) / component.aspectWidth;
										} else {
											zoomX = 512;
										}
										int zoomY;
										if (component.aspectHeight <= 0) {
											zoomY = 512;
										} else {
											zoomY = (component.height << 9) / component.aspectHeight;
										}
										int centreX = startX + component.width / 2;
										int centreY = startY + component.height / 2;
										if (!component.hasOrigin) {
											centreX += zoomX * component.originX >> 9;
											centreY += zoomY * component.originY >> 9;
										}
										ClientState.aClass30_3.makeIdentity();
										ClientState.activeToolkit.setCamera(ClientState.aClass30_3);
										ClientState.activeToolkit.DA(centreX, centreY, zoomX, zoomY);
										ClientState.activeToolkit.ya();
										if (component.priorityRender) {
											ClientState.activeToolkit.C(false);
										}
										if (component.hasOrigin) {
											InterfaceComponentRenderer.aClass30_6.makeRotationX(component.spritePitch);
											InterfaceComponentRenderer.aClass30_6.rotateAxisY(component.spriteRoll);
											InterfaceComponentRenderer.aClass30_6.rotateAxisZ(component.spriteYaw);
											InterfaceComponentRenderer.aClass30_6.translate(component.originX, component.originY, component.originZ);
										} else {
											int pitchSinOffset = (component.spriteScale << 2) * TextureCombineSource.SINE[component.spritePitch << 3] >> 14;
											int pitchCosOffset = TextureCombineSource.COSINE[component.spritePitch << 3] * (component.spriteScale << 2) >> 14;
											InterfaceComponentRenderer.aClass30_6.makeRotationZ(-component.spriteYaw << 3);
											InterfaceComponentRenderer.aClass30_6.rotateAxisY(component.spriteRoll << 3);
											InterfaceComponentRenderer.aClass30_6.translate(component.offsetX << 2, (component.offsetY << 2) + pitchSinOffset + modelCentreOffsetY, (component.offsetY << 2) + pitchCosOffset);
											InterfaceComponentRenderer.aClass30_6.rotateAxisX(component.spritePitch << 3);
										}
										component.cycleParticles(model, ClientState.activeToolkit, ClientState.clientCycle, InterfaceComponentRenderer.aClass30_6);
										if (ClipComponentsDebug.clipComponents) {
											ClientState.activeToolkit.T(startX, startY, component.width + startX, startY + component.height);
										}
										if (component.hasOrigin) {
											if (component.usesOrthogonal) {
												model.renderOrtho(InterfaceComponentRenderer.aClass30_6, null, component.spriteScale, 1);
											} else {
												model.render(InterfaceComponentRenderer.aClass30_6, null, 1);
												if (component.particleSystem != null) {
													ClientState.activeToolkit.render(component.particleSystem.rebuildRenderedParticles());
												}
											}
										} else if (component.usesOrthogonal) {
											model.renderOrtho(InterfaceComponentRenderer.aClass30_6, null, component.spriteScale << 2, 1);
										} else {
											model.render(InterfaceComponentRenderer.aClass30_6, null, 1);
											if (component.particleSystem != null) {
												ClientState.activeToolkit.render(component.particleSystem.rebuildRenderedParticles());
											}
										}
										if (ClipComponentsDebug.clipComponents) {
											ClientState.activeToolkit.KA(clippingLeft, clippingTop, clippingRight, clippingBottom);
										}
										if (component.priorityRender) {
											ClientState.activeToolkit.C(true);
										}
									}
									if (OrthoMode.orthoEnabled) {
										if (isTopLevelLayer) {
											OrthoMode.markOrthoTilesDirty(dirtyMinX, dirtyMaxX, dirtyMinY, dirtyMaxY);
										} else {
											OrthoMode.expandDirtyArea(dirtyMaxY, dirtyMinX, dirtyMaxX, dirtyMinY);
										}
									}
								} else if (component.type == ComponentType.LINE) {
									int lineStartY;
									int lineEndX;
									int lineEndY;
									if (component.lineDirection) {
										lineStartY = component.height + startY;
										lineEndX = component.width + startX;
										lineEndY = startY;
									} else {
										lineEndY = startY + component.height;
										lineEndX = component.width + startX;
										lineStartY = startY;
									}
									if (component.lineWidth == 1) {
										ClientState.activeToolkit.line(startX, lineStartY, lineEndX, lineEndY, component.color, 0);
									} else {
										ClientState.activeToolkit.strongLine(startX, lineStartY, lineEndX, lineEndY, component.color, component.lineWidth);
									}
									if (OrthoMode.orthoEnabled) {
										if (isTopLevelLayer) {
											OrthoMode.markOrthoTilesDirty(dirtyMinX, dirtyMaxX, dirtyMinY, dirtyMaxY);
										} else {
											OrthoMode.expandDirtyArea(dirtyMaxY, dirtyMinX, dirtyMaxX, dirtyMinY);
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
}

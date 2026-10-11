package com.jagex.game.runetek5;

import com.jagex.game.runetek5.config.rsinterface.MediaType;
import com.jagex.game.runetek5.config.sequence.SeqType;
import com.jagex.game.runetek5.config.rsinterface.Component;
import com.jagex.game.runetek5.config.rsinterface.HookRequest;
import com.jagex.game.runetek5.sign.SignLink;
import com.jagex.game.runetek5.sign.SignedResource;
import com.jagex.game.runetek5.sign.SignedResourceStatus;
import java.awt.Frame;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;
import com.jagex.game.runetek5.client.GameShell;
import com.jagex.game.runetek5.config.TypeLists;
import com.jagex.game.runetek5.graphics.ifcomponent.InterfaceRenderer;
import com.jagex.game.runetek5.client.menu.MiniMenu;
import com.jagex.game.runetek5.client.window.WindowMode;
import com.jagex.game.runetek5.tools.LocalizedText;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.config.rsinterface.IFEvents;
import com.jagex.game.runetek5.config.rsinterface.Interface;
import com.jagex.game.runetek5.config.rsinterface.SubInterface;
import com.jagex.game.runetek5.config.Resource;
import com.jagex.game.runetek5.core.datastruct.key.Deque;
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable;
import com.jagex.game.runetek5.core.util.TimeUtils;
import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.utils.Utils;
import com.jagex.game.runetek5.config.rsinterface.ComponentType;
import com.jagex.game.runetek5.client.ClientState;

public final class InterfaceManager {

	@OriginalMember(owner = "client!fs", name = "a", descriptor = "(IILclient!at;ZI)V")
	public static void initSizes(@OriginalArg(0) int parentWidth, @OriginalArg(2) Component component, @OriginalArg(3) boolean fireResizeHooks, @OriginalArg(4) int parentHeight) {
		@Pc(19) int previousWidth = component.width;
		@Pc(22) int previousHeight = component.height;
		if (component.aspectWidthType == 0) {
			component.width = component.baseWidth;
		} else if (component.aspectWidthType == 1) {
			component.width = parentWidth - component.baseWidth;
		} else if (component.aspectWidthType == 2) {
			component.width = component.baseWidth * parentWidth >> 14;
		}
		if (component.aspectHeightType == 0) {
			component.height = component.baseHeight;
		} else if (component.aspectHeightType == 1) {
			component.height = parentHeight - component.baseHeight;
		} else if (component.aspectHeightType == 2) {
			component.height = component.baseHeight * parentHeight >> 14;
		}
		if (component.aspectWidthType == 4) {
			component.width = component.height * component.aspectX / component.aspectY;
		}
		if (component.aspectHeightType == 4) {
			component.height = component.aspectY * component.width / component.aspectX;
		}
		if (InterfaceManager.qaOpTest && (InterfaceManager.getComponentSettings(component).settingsHash != 0 || component.type == ComponentType.CONTAINER)) {
			if (component.height < 5 && component.width < 5) {
				component.height = 5;
				component.width = 5;
			} else {
				if (component.height <= 0) {
					component.height = 5;
				}
				if (component.width <= 0) {
					component.width = 5;
				}
			}
		}
		if (component.contentType == Component.CONTENT_TYPE_GAME_WINDOW) {
			InterfaceManager.gameScreenComponent = component;
		}
		if (fireResizeHooks && component.onResize != null && (component.width != previousWidth || component.height != previousHeight)) {
			@Pc(202) HookRequest hookRequest = new HookRequest();
			hookRequest.source = component;
			hookRequest.params = component.onResize;
			InterfaceManager.PENDING_HOOK_REQUESTS.addLast(hookRequest);
		}
	}

	@OriginalMember(owner = "client!lf", name = "a", descriptor = "(IILclient!wea;III)Ljava/awt/Frame;")
	public static Frame calculateFullscreenFrame(@OriginalArg(0) int height, @OriginalArg(1) int width, @OriginalArg(2) SignLink signLink, @OriginalArg(3) int bitDepth) {
		if (!signLink.isFullscreenSupported()) {
			return null;
		}
		@Pc(20) DisplayModeDetails[] displayModes = SignLink.getDisplayModes(signLink);
		if (displayModes == null) {
			return null;
		}
		@Pc(26) boolean found = false;
		for (@Pc(28) int i = 0; i < displayModes.length; i++) {
			if (width == displayModes[i].width && displayModes[i].height == height && (!found || bitDepth < displayModes[i].bitDepth)) {
				bitDepth = displayModes[i].bitDepth;
				found = true;
			}
		}
		if (!found) {
			return null;
		}
		@Pc(101) SignedResource signedResource = signLink.enterFullscreen(height, bitDepth, width);
		while (signedResource.status == SignedResourceStatus.IDLE) {
			TimeUtils.sleep(10L);
		}
		@Pc(116) Frame frame = (Frame) signedResource.result;
		if (frame == null) {
			return null;
		} else if (signedResource.status == SignedResourceStatus.ERROR) {
			InterfaceManager.disposeFrame(frame, signLink);
			return null;
		} else {
			return frame;
		}
	}

	@OriginalMember(owner = "client!jg", name = "a", descriptor = "(Lclient!at;B)V")
	public static void prepareSequenceOnInterface(@OriginalArg(0) Component component) {
		if (Component.CONTENT_TYPE_LOCAL_PLAYER_MODEL != component.contentType) {
			return;
		}
		if (PlayerUpdate.localPlayer.displayName == null) {
			component.secondaryMediaId = 0;
			component.primaryMediaId = 0;
			return;
		}
		component.spritePitch = 150;
		component.spriteRoll = (int) (Math.sin((double) ClientState.clientCycle / 40.0D) * 256.0D) & 0x7FF;
		component.mediaType = MediaType.PLAYER_MODEL;
		component.primaryMediaId = PlayerUpdate.myPlayerIndex;
		component.secondaryMediaId = Utils.hashString(PlayerUpdate.localPlayer.displayName);
		component.animation = PlayerUpdate.localPlayer.movingSequence.sequenceId;
		component.currentFrameDuration = 0;
		component.nextFrameId = PlayerUpdate.localPlayer.movingSequence.nextFrameId;
		component.currentFrameId = PlayerUpdate.localPlayer.movingSequence.currentFrameId;
		@Pc(76) SeqType seqType = component.animation == -1 ? null : TypeLists.SEQ.list(component.animation);
		if (seqType != null) {
			SoundManager.playInterfaceSequenceSound(component.currentFrameId, seqType);
		}
	}

	@OriginalMember(owner = "client!mea", name = "a", descriptor = "(Lclient!at;III)V")
	public static void startDragging(@OriginalArg(0) Component component, @OriginalArg(1) int offsetX, @OriginalArg(2) int offsetY) {
		if (InterfaceDrag.component != null || MiniMenu.isOpen || (component == null || InterfaceDrag.getDragParent(component) == null)) {
			return;
		}
		InterfaceDrag.component = component;
		InterfaceDrag.aClass24_7 = InterfaceDrag.getDragParent(component);
		InterfaceRenderer.dragTickCount = 0;
		InterfaceDrag.dragOffsetY = offsetY;
		InterfaceDrag.dragOffsetX = offsetX;
		InterfaceDrag.isDragging = false;
	}

	@OriginalMember(owner = "client!gn", name = "a", descriptor = "(B)V")
	public static void canvasAdjustment() {
		@Pc(5) int screenSizePreference = 0;
		if (GamePreferences.uid != null) {
			screenSizePreference = GamePreferences.uid.maxScreenSize.getValue();
		}
		@Pc(28) int preferredWidth;
		@Pc(46) int preferredHeight;
		if (screenSizePreference == 2) {
			preferredWidth = WindowMode.resizableScreenWidth <= 800 ? WindowMode.resizableScreenWidth : 800;
			GameShell.screenCanvasWidth = preferredWidth;
			MiniMenu.gameCanvasX = (WindowMode.resizableScreenWidth - preferredWidth) / 2;
			preferredHeight = WindowMode.resizableScreenHeight > 600 ? 600 : WindowMode.resizableScreenHeight;
			GameShell.screenCanvasHeight = preferredHeight;
			GameShell.gameCanvasY = 0;
		} else if (screenSizePreference == 1) {
			preferredWidth = WindowMode.resizableScreenWidth <= 1024 ? WindowMode.resizableScreenWidth : 1024;
			MiniMenu.gameCanvasX = (WindowMode.resizableScreenWidth - preferredWidth) / 2;
			preferredHeight = WindowMode.resizableScreenHeight <= 768 ? WindowMode.resizableScreenHeight : 768;
			GameShell.screenCanvasWidth = preferredWidth;
			GameShell.gameCanvasY = 0;
			GameShell.screenCanvasHeight = preferredHeight;
		} else {
			GameShell.gameCanvasY = 0;
			GameShell.screenCanvasHeight = WindowMode.resizableScreenHeight;
			MiniMenu.gameCanvasX = 0;
			GameShell.screenCanvasWidth = WindowMode.resizableScreenWidth;
		}
	}

	@OriginalMember(owner = "client!ida", name = "a", descriptor = "(II)Ljava/lang/String;")
	public static String formatAmount(@OriginalArg(1) int amount) {
		@Pc(8) String string = Integer.toString(amount);
		for (@Pc(18) int i = string.length() - 3; i > 0; i -= 3) {
			string = string.substring(0, i) + "," + string.substring(i);
		}
		if (string.length() > 9) {
			return " <col=00ff80>" + string.substring(0, string.length() - 8) + LocalizedText.aClass351_35.get(ClientState.language) + " (" + string + ")</col>";
		} else if (string.length() > 6) {
			return " <col=ffffff>" + string.substring(0, string.length() - 4) + LocalizedText.aClass351_37.get(ClientState.language) + " (" + string + ")</col>";
		} else {
			return " <col=ffff00>" + string + "</col>";
		}
	}

	@OriginalMember(owner = "client!jj", name = "a", descriptor = "(Ljava/awt/Frame;Lclient!wea;Z)V")
	public static void disposeFrame(@OriginalArg(0) Frame frame, @OriginalArg(1) SignLink signLink) {
		while (true) {
			@Pc(10) SignedResource signedResource = signLink.exitFullscreen(frame);
			while (signedResource.status == SignedResourceStatus.IDLE) {
				TimeUtils.sleep(10L);
			}
			if (signedResource.status == SignedResourceStatus.SUCCESS) {
				frame.setVisible(false);
				frame.dispose();
				return;
			}
			TimeUtils.sleep(100L);
		}
	}
	@OriginalMember(owner = "client!client", name = "a", descriptor = "(Lclient!at;)Lclient!wb;")
	public static IFEvents getComponentSettings(@OriginalArg(0) Component component) {
		@Pc(13) IFEvents serverActiveProperties = (IFEvents) IFEvents.ICOMPONENT_SETTINGS_SLOTS.get(((long) component.idHash << 32) + (long) component.slotId);
		return serverActiveProperties == null ? component.targetParams : serverActiveProperties;
	}

	@OriginalMember(owner = "client!cp", name = "a", descriptor = "(IB)Z")
	public static boolean loadInterface(@OriginalArg(0) int interfaceId) {
		if (InterfaceManager.loadedInterfaces[interfaceId]) {
			return true;
		} else if (Resource.aClass157_255.isGroupReady(interfaceId)) {
			@Pc(23) int componentCount = Resource.aClass157_255.getGroupCapacity(interfaceId);
			if (componentCount == 0) {
				InterfaceManager.loadedInterfaces[interfaceId] = true;
				return true;
			}
			if (Interface.interfaces[interfaceId] == null) {
				Interface.interfaces[interfaceId] = new Component[componentCount];
			}
			for (@Pc(42) int i = 0; i < componentCount; i++) {
				if (Interface.interfaces[interfaceId][i] == null) {
					@Pc(58) byte[] data = Resource.aClass157_255.getFile(interfaceId, i);
					if (data != null) {
						@Pc(70) Component component = Interface.interfaces[interfaceId][i] = new Component();
						component.idHash = i + (interfaceId << 16);
						if (data[0] != -1) {
							throw new IllegalStateException("if1");
						}
						component.decode(new Packet(data));
					}
				}
			}
			InterfaceManager.loadedInterfaces[interfaceId] = true;
			return true;
		} else {
			return false;
		}
	}

	@OriginalMember(owner = "client!me", name = "a", descriptor = "(B)V")
	public static void resetInterfaces() {
		Interface.interfaces = new Component[Resource.aClass157_255.getCapacity()][];
		Interface.aClass24ArrayArray1 = new Component[Resource.aClass157_255.getCapacity()][];
		InterfaceManager.loadedInterfaces = new boolean[Resource.aClass157_255.getCapacity()];
	}

	@OriginalMember(owner = "client!pe", name = "d", descriptor = "[Z")
	public static boolean[] loadedInterfaces;

	@OriginalMember(owner = "client!lm", name = "a", descriptor = "(I[Lclient!at;I)V")
	public static void updateComponentAnimations(@OriginalArg(0) int idHash, @OriginalArg(1) Component[] components) {
		for (@Pc(7) int i = 0; i < components.length; i++) {
			@Pc(13) Component component = components[i];
			if (component != null && idHash == component.parentId && !InterfaceRenderer.isComponentHidden(component)) {
				if (component.type == ComponentType.CONTAINER) {
					updateComponentAnimations(component.idHash, components);
					if (component.slotChildren != null) {
						updateComponentAnimations(component.idHash, component.slotChildren);
					}
					@Pc(47) SubInterface subInterface = (SubInterface) InterfaceManager.OPEN_INTERFACES.get((long) component.idHash);
					if (subInterface != null) {
						InterfaceManager.loadAndAnimateInterface(subInterface.interfaceId);
					}
				}
				if (component.type == ComponentType.MODEL && component.animation != -1) {
					@Pc(69) SeqType seqType = TypeLists.SEQ.list(component.animation);
					if (seqType != null) {
						component.currentFrameDuration += ClientState.animatingStep;
						@Pc(80) int animation = component.currentFrameId;
						while (component.currentFrameDuration > seqType.durations[component.currentFrameId]) {
							component.currentFrameDuration -= seqType.durations[component.currentFrameId];
							component.currentFrameId++;
							if (component.currentFrameId >= seqType.frames.length) {
								component.currentFrameId -= seqType.replayInterval;
								if (component.currentFrameId < 0 || seqType.frames.length <= component.currentFrameId) {
									component.currentFrameId = 0;
								}
							}
							component.nextFrameId = component.currentFrameId + 1;
							if (seqType.frames.length <= component.nextFrameId) {
								component.nextFrameId -= seqType.replayInterval;
								if (component.nextFrameId < 0 || component.nextFrameId >= seqType.frames.length) {
									component.nextFrameId = -1;
								}
							}
							Component.redrawComponent(component);
						}
						if (animation != component.currentFrameId) {
							SoundManager.playInterfaceSequenceSound(component.currentFrameId, seqType);
						}
					}
				}
			}
		}
	}
	@OriginalMember(owner = "client!ab", name = "E", descriptor = "Lclient!at;")
	public static Component gameScreenComponent = null;

	@OriginalMember(owner = "client!dga", name = "H", descriptor = "Lclient!uh;")
	public static final Deque PENDING_HOOK_REQUESTS = new Deque();

	@OriginalMember(owner = "client!nha", name = "c", descriptor = "Lclient!eq;")
	public static IterableHashTable OPEN_INTERFACES = new IterableHashTable(8);
	@OriginalMember(owner = "client!al", name = "a", descriptor = "(ZIIZI[Lclient!at;)V")
	public static void updateInterfaceDimensions(@OriginalArg(1) int idHash, @OriginalArg(2) int parentWidth, @OriginalArg(3) boolean fireResizeHooks, @OriginalArg(4) int parentHeight, @OriginalArg(5) Component[] components) {
		for (@Pc(11) int i = 0; i < components.length; i++) {
			@Pc(17) Component component = components[i];
			if (component != null && idHash == component.parentId) {
				InterfaceManager.initSizes(parentWidth, component, fireResizeHooks, parentHeight);
				Component.updatePosition(parentWidth, parentHeight, component);
				if (component.scrollX > component.scrollWidth - component.width) {
					component.scrollX = component.scrollWidth - component.width;
				}
				if (component.scrollX < 0) {
					component.scrollX = 0;
				}
				if (component.scrollY > component.scrollHeight - component.height) {
					component.scrollY = component.scrollHeight - component.height;
				}
				if (component.scrollY < 0) {
					component.scrollY = 0;
				}
				if (component.type == ComponentType.CONTAINER) {
					Component.layoutChildren(fireResizeHooks, component);
				}
			}
		}
	}

	@OriginalMember(owner = "client!mt", name = "a", descriptor = "(Lclient!at;I)V")
	public static void relayoutAgainstParent(@OriginalArg(0) Component component) {
		@Pc(15) Component component2 = Component.getParentLayer(component);
		@Pc(23) int screenCanvasWidth;
		@Pc(20) int parentHeight;
		if (component2 == null) {
			parentHeight = GameShell.screenCanvasHeight;
			screenCanvasWidth = GameShell.screenCanvasWidth;
		} else {
			parentHeight = component2.height;
			screenCanvasWidth = component2.width;
		}
		InterfaceManager.initSizes(screenCanvasWidth, component, false, parentHeight);
		Component.updatePosition(screenCanvasWidth, parentHeight, component);
	}

	@OriginalMember(owner = "client!jaa", name = "a", descriptor = "(IIIZI)V")
	public static void updateInterfaceDimensions(@OriginalArg(0) int parentHeight, @OriginalArg(2) int interfaceId, @OriginalArg(3) boolean fireResizeHooks, @OriginalArg(4) int parentWidth) {
		if (InterfaceManager.loadInterface(interfaceId)) {
			InterfaceManager.updateInterfaceDimensions(-1, parentWidth, fireResizeHooks, parentHeight, Interface.interfaces[interfaceId]);
		}
	}
	@OriginalMember(owner = "client!cc", name = "P", descriptor = "I")
	public static int lastMiscUpdateTick = 0;

	@OriginalMember(owner = "client!vc", name = "g", descriptor = "Z")
	public static boolean qaOpTest = false;

	@OriginalMember(owner = "client!wga", name = "Bd", descriptor = "I")
	public static int interfaceDebugMode = 0;

	@OriginalMember(owner = "client!it", name = "l", descriptor = "Lclient!at;")
	public static Component activeDialogueContinue = null;

	@OriginalMember(owner = "client!ada", name = "a", descriptor = "(IB)V")
	public static void loadAndAnimateInterface(@OriginalArg(0) int interfaceId) {
		if (InterfaceManager.loadInterface(interfaceId)) {
			InterfaceManager.updateComponentAnimations(-1, Interface.interfaces[interfaceId]);
		}
	}
}

package com.jagex.game.runetek5.config.rsinterface;

import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.cache.node.Node;
import com.jagex.game.runetek5.InterfaceManager;
import com.jagex.game.runetek5.config.Resource;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;

public final class Interface {
	@OriginalMember(owner = "client!qba", name = "D", descriptor = "I")
	public static int preferenceMask;

	private Interface() {
		/* empty */
	}
	@OriginalMember(owner = "client!gda", name = "a", descriptor = "(IB)V")
	public static void resetComponentSequences(@OriginalArg(0) int interfaceId) {
		if (!InterfaceManager.loadInterface(interfaceId)) {
			return;
		}
		@Pc(18) Component[] components = Interface.interfaces[interfaceId];
		for (@Pc(20) int i = 0; i < components.length; i++) {
			@Pc(26) Component component = components[i];
			if (component != null) {
				component.currentFrameId = 0;
				component.nextFrameId = 1;
				component.currentFrameDuration = 0;
			}
		}
	}

	@OriginalMember(owner = "client!hda", name = "a", descriptor = "(IB)V")
	public static void unloadInterface(@OriginalArg(0) int interfaceId) {
		if (interfaceId != -1 && InterfaceManager.loadedInterfaces[interfaceId]) {
			Resource.aClass157_255.discardUnpacked(interfaceId);
			Interface.interfaces[interfaceId] = null;
			Interface.aClass24ArrayArray1[interfaceId] = null;
			InterfaceManager.loadedInterfaces[interfaceId] = false;
		}
	}
	@OriginalMember(owner = "client!wj", name = "K", descriptor = "[[Lclient!at;")
	public static Component[][] interfaces;

	@OriginalMember(owner = "client!pg", name = "b", descriptor = "(II)V")
	public static void clearInterfaceSettings(@OriginalArg(1) int interfaceId) {
		for (@Pc(11) Node node = IFEvents.ICOMPONENT_SETTINGS_SLOTS.first(); node != null; node = IFEvents.ICOMPONENT_SETTINGS_SLOTS.next()) {
			if ((node.key >> 48 & 0xFFFFL) == (long) interfaceId) {
				node.unlink();
			}
		}
	}
	@OriginalMember(owner = "client!da", name = "a", descriptor = "(II)Lclient!at;")
	public static Component getComponent(@OriginalArg(1) int idHash) {
		@Pc(12) int interfaceId = idHash >> 16;
		@Pc(16) int componentId = idHash & 0xFFFF;
		if (Interface.interfaces[interfaceId] == null || Interface.interfaces[interfaceId][componentId] == null) {
			@Pc(30) boolean loaded = InterfaceManager.loadInterface(interfaceId);
			if (!loaded) {
				return null;
			}
		}
		return Interface.interfaces[interfaceId][componentId];
	}

	@OriginalMember(owner = "client!kb", name = "a", descriptor = "(Lclient!in;BLclient!in;Lclient!in;Lclient!in;)V")
	public static void init(@OriginalArg(0) Js5ResourceProvider fontMetricsResource, @OriginalArg(2) Js5ResourceProvider interfaceResource, @OriginalArg(3) Js5ResourceProvider packet, @OriginalArg(4) Js5ResourceProvider modelResource) {
		Resource.aClass157_291 = modelResource;
		Component.packet = packet;
		Resource.aClass157_255 = interfaceResource;
		Interface.interfaces = new Component[Resource.aClass157_255.getCapacity()][];
		InterfaceManager.loadedInterfaces = new boolean[Resource.aClass157_255.getCapacity()];
	}

	@OriginalMember(owner = "client!ou", name = "p", descriptor = "[[Lclient!at;")
	public static Component[][] aClass24ArrayArray1;
}

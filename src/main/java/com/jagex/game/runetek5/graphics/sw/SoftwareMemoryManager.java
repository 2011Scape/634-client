package com.jagex.game.runetek5.graphics.sw;

import com.jagex.game.runetek5.core.datastruct.key.Deque;
import com.jagex.game.runetek5.graphics.sw.SoftwareObject;
import com.jagex.game.runetek5.cache.node.SoftwareObjectNode;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

public final class SoftwareMemoryManager {

	@OriginalMember(owner = "client!caa", name = "a", descriptor = "(B)V")
	public static synchronized void freePending() {
		while (true) {
			@Pc(4) SoftwareObjectNode softwareObjectNode = (SoftwareObjectNode) objects.removeFirst();
			if (softwareObjectNode == null) {
				return;
			}
			softwareObjectNode.object.w(true);
			softwareObjectNode.unlink();
		}
	}

	@OriginalMember(owner = "client!caa", name = "a", descriptor = "(BZ)V")
	public static synchronized void markShutdown() {
		shutdown = true;
	}

	@OriginalMember(owner = "client!caa", name = "a", descriptor = "(I)V")
	public static synchronized void instanceDestroyed() {
		instances--;
		if (instances == 0) {
			freePending();
		}
	}

	@OriginalMember(owner = "client!caa", name = "a", descriptor = "(ILclient!e;)V")
	public static synchronized void free(@OriginalArg(1) SoftwareObject object) {
		if (shutdown) {
			return;
		}
		if (instances <= 0) {
			object.w(false);
		} else {
			@Pc(13) SoftwareObjectNode node = new SoftwareObjectNode();
			node.object = object;
			objects.addLast(node);
		}
	}

	@OriginalMember(owner = "client!caa", name = "a", descriptor = "Z")
	public static boolean shutdown = false;

	@OriginalMember(owner = "client!caa", name = "b", descriptor = "(B)V")
	public static synchronized void instanceCreated() {
		instances++;
	}

	@OriginalMember(owner = "client!caa", name = "b", descriptor = "Lclient!uh;")
	public static final Deque objects = new Deque();

	@OriginalMember(owner = "client!caa", name = "c", descriptor = "I")
	public static int instances = 0;
}

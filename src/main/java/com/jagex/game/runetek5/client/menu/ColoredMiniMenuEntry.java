package com.jagex.game.runetek5.client.menu;

import com.jagex.game.runetek5.client.menu.MiniMenu;
import com.jagex.game.runetek5.client.menu.MiniMenuEntry;
import com.jagex.game.runetek5.cache.node.lru.CacheableNode;
import com.jagex.game.runetek5.cache.node.Queue;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!vq")
public final class ColoredMiniMenuEntry extends CacheableNode {

	@OriginalMember(owner = "client!vq", name = "y", descriptor = "I")
	public int queueSize;

	@OriginalMember(owner = "client!vq", name = "A", descriptor = "Ljava/lang/String;")
	public final String tagStart;

	@OriginalMember(owner = "client!vq", name = "E", descriptor = "Lclient!mn;")
	public final Queue nodeQueue;

	@OriginalMember(owner = "client!vq", name = "<init>", descriptor = "(Ljava/lang/String;)V")
	public ColoredMiniMenuEntry(@OriginalArg(0) String string) {
		this.tagStart = string;
		this.nodeQueue = new Queue();
	}

	@OriginalMember(owner = "client!vq", name = "a", descriptor = "(Lclient!db;I)Z")
	public boolean addEntry(@OriginalArg(0) MiniMenuEntry miniMenuEntryInner) {
		@Pc(7) boolean isFirst = true;
		miniMenuEntryInner.unlinkSecondary();
		@Pc(16) MiniMenuEntry miniMenuEntryInner2 = (MiniMenuEntry) this.nodeQueue.first();
		while (miniMenuEntryInner2 != null) {
			if (MiniMenu.hasHigherPriority(miniMenuEntryInner2.clickType, miniMenuEntryInner.clickType)) {
				CacheableNode.insertSecondaryBefore(miniMenuEntryInner2, miniMenuEntryInner);
				this.queueSize++;
				if (isFirst) {
					return false;
				}
				return true;
			}
			miniMenuEntryInner2 = (MiniMenuEntry) this.nodeQueue.next();
			isFirst = false;
		}
		this.nodeQueue.insert(miniMenuEntryInner);
		this.queueSize++;
		return isFirst;
	}

	@OriginalMember(owner = "client!vq", name = "a", descriptor = "(BLclient!db;)Z")
	public boolean removeEntry(@OriginalArg(1) MiniMenuEntry miniMenuEntryInner) {
		@Pc(15) int action = this.getAction();
		miniMenuEntryInner.unlinkSecondary();
		this.queueSize--;
		if (this.queueSize != 0) {
			return this.getAction() != action;
		}
		this.unlink();
		this.unlinkSecondary();
		MiniMenu.coloredEntryCount--;
		MiniMenu.coloredEntryCache.put(this, miniMenuEntryInner.bitpacked);
		return false;
	}

	@OriginalMember(owner = "client!vq", name = "a", descriptor = "(I)I")
	public int getAction() {
		return this.nodeQueue.sentinel == this.nodeQueue.sentinel.secondaryNext ? -1 : ((MiniMenuEntry) this.nodeQueue.sentinel.secondaryNext).clickType;
	}
}

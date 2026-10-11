package com.jagex.game.runetek5.cache.node.lru;

import com.jagex.game.runetek5.cache.node.IntNode;
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable;
import com.jagex.game.runetek5.cache.node.lru.CacheableNode;
import com.jagex.game.runetek5.cache.node.Node;
import com.jagex.game.runetek5.cache.node.StringNode;
import com.jagex.game.runetek5.buffer.Packet;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!bda")
public final class StructType extends CacheableNode {

	@OriginalMember(owner = "client!bda", name = "C", descriptor = "Lclient!eq;")
	private IterableHashTable params;

	@OriginalMember(owner = "client!bda", name = "a", descriptor = "(IIB)I")
	public int getParam(@OriginalArg(0) int paramKey, @OriginalArg(1) int defaultValue) {
		if (this.params == null) {
			return defaultValue;
		} else {
			@Pc(16) IntNode intNode = (IntNode) this.params.get((long) paramKey);
			return intNode == null ? defaultValue : intNode.value;
		}
	}

	@OriginalMember(owner = "client!bda", name = "a", descriptor = "(ILclient!cea;B)V")
	private void decode(@OriginalArg(0) int opcode, @OriginalArg(1) Packet packet) {
		if (opcode != 249) {
			return;
		}
		@Pc(18) int count = packet.readUByte();
		@Pc(25) int local25;
		if (this.params == null) {
			int bucketCount = Utils.nextPowerOfTwo(count);
			this.params = new IterableHashTable(bucketCount);
		}
		for (int paramIndex = 0; paramIndex < count; paramIndex++) {
			@Pc(46) boolean isString = packet.readUByte() == 1;
			@Pc(50) int paramKey = packet.readUMedium();
			@Pc(59) Node node;
			if (isString) {
				node = new StringNode(packet.readString());
			} else {
				node = new IntNode(packet.readInt());
			}
			this.params.put((long) paramKey, node);
		}
	}

	@OriginalMember(owner = "client!bda", name = "a", descriptor = "(Lclient!cea;B)V")
	public void decode(@OriginalArg(0) Packet packet) {
		while (true) {
			@Pc(5) int opcode = packet.readUByte();
			if (opcode == 0) {
				return;
			}
			this.decode(opcode, packet);
		}
	}

	@OriginalMember(owner = "client!bda", name = "a", descriptor = "(ILjava/lang/String;I)Ljava/lang/String;")
	public String getStringParam(@OriginalArg(1) String string, @OriginalArg(2) int paramKey) {
		if (this.params == null) {
			return string;
		} else {
			@Pc(21) StringNode stringNode = (StringNode) this.params.get((long) paramKey);
			return stringNode == null ? string : stringNode.value;
		}
	}
}

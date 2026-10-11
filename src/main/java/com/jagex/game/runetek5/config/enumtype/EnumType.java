package com.jagex.game.runetek5.config.enumtype;

import com.jagex.game.runetek5.cache.node.EnumReverseNode;
import com.jagex.game.runetek5.cache.node.IntNode;
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable;
import com.jagex.game.runetek5.cache.node.Node;
import com.jagex.game.runetek5.cache.node.StringNode;
import com.jagex.game.runetek5.buffer.Packet;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!nd")
public final class EnumType {

	@OriginalMember(owner = "client!nd", name = "b", descriptor = "I")
	private int defaultIntValue;

	@OriginalMember(owner = "client!nd", name = "e", descriptor = "Lclient!eq;")
	public IterableHashTable parameters;

	@OriginalMember(owner = "client!nd", name = "h", descriptor = "Lclient!eq;")
	private IterableHashTable reversed;

	@OriginalMember(owner = "client!nd", name = "p", descriptor = "C")
	public char keyType;

	@OriginalMember(owner = "client!nd", name = "q", descriptor = "C")
	public char valueType;

	@OriginalMember(owner = "client!nd", name = "l", descriptor = "Ljava/lang/String;")
	private String defaultStringValue = "null";

	@OriginalMember(owner = "client!nd", name = "a", descriptor = "(Lclient!cea;II)V")
	private void decode(@OriginalArg(0) Packet packet, @OriginalArg(2) int opcode) {
		if (opcode == 1) {
			this.keyType = Utils.cp1252ToChar(packet.readByte());
		} else if (opcode == 2) {
			this.valueType = Utils.cp1252ToChar(packet.readByte());
		} else if (opcode == 3) {
			this.defaultStringValue = packet.readString();
		} else if (opcode == 4) {
			this.defaultIntValue = packet.readInt();
		} else if (opcode == 5 || opcode == 6) {
			@Pc(65) int count = packet.readUShort();
			this.parameters = new IterableHashTable(Utils.nextPowerOfTwo(count));
			for (@Pc(75) int i = 0; i < count; i++) {
				@Pc(81) int key = packet.readInt();
				@Pc(91) Node node;
				if (opcode == 5) {
					node = new StringNode(packet.readString());
				} else {
					node = new IntNode(packet.readInt());
				}
				this.parameters.put((long) key, node);
			}
		}
	}

	@OriginalMember(owner = "client!nd", name = "a", descriptor = "(ZLjava/lang/String;)Z")
	public boolean hasOutputString(@OriginalArg(1) String value) {
		if (this.parameters == null) {
			return false;
		}
		if (this.reversed == null) {
			this.buildReverseStringTable();
		}
		for (@Pc(31) EnumReverseNode frequency = (EnumReverseNode) this.reversed.get(Utils.hashStringLong(value)); frequency != null; frequency = (EnumReverseNode) this.reversed.nextWithKey()) {
			if (frequency.value.equals(value)) {
				return true;
			}
		}
		return false;
	}

	@OriginalMember(owner = "client!nd", name = "a", descriptor = "(ZI)Z")
	public boolean hasOutput(@OriginalArg(1) int key) {
		if (this.parameters == null) {
			return false;
		}
		if (this.reversed == null) {
			this.buildReverseIntTable();
		}
		@Pc(27) IntNode intNode = (IntNode) this.reversed.get((long) key);
		return intNode != null;
	}

	@OriginalMember(owner = "client!nd", name = "a", descriptor = "(B)V")
	private void buildReverseIntTable() {
		this.reversed = new IterableHashTable(this.parameters.getBucketCount());
		for (@Pc(20) IntNode intNode = (IntNode) this.parameters.first(); intNode != null; intNode = (IntNode) this.parameters.next()) {
			@Pc(29) IntNode intNode2 = new IntNode((int) intNode.key);
			this.reversed.put((long) intNode.value, intNode2);
		}
	}

	@OriginalMember(owner = "client!nd", name = "a", descriptor = "(ILclient!cea;)V")
	public void decode(@OriginalArg(1) Packet packet) {
		while (true) {
			@Pc(11) int opcode = packet.readUByte();
			if (opcode == 0) {
				return;
			}
			this.decode(packet, opcode);
		}
	}

	@OriginalMember(owner = "client!nd", name = "a", descriptor = "(Z)V")
	private void buildReverseStringTable() {
		this.reversed = new IterableHashTable(this.parameters.getBucketCount());
		for (@Pc(20) StringNode stringNode = (StringNode) this.parameters.first(); stringNode != null; stringNode = (StringNode) this.parameters.next()) {
			@Pc(31) EnumReverseNode entry = new EnumReverseNode(stringNode.value, (int) stringNode.key);
			this.reversed.put(Utils.hashStringLong(stringNode.value), entry);
		}
	}

	@OriginalMember(owner = "client!nd", name = "b", descriptor = "(ZI)I")
	public int getInt(@OriginalArg(1) int key) {
		if (this.parameters == null) {
			return this.defaultIntValue;
		} else {
			@Pc(25) IntNode intNode = (IntNode) this.parameters.get((long) key);
			return intNode == null ? this.defaultIntValue : intNode.value;
		}
	}

	@OriginalMember(owner = "client!nd", name = "a", descriptor = "(II)Ljava/lang/String;")
	public String getString(@OriginalArg(0) int key) {
		if (this.parameters == null) {
			return this.defaultStringValue;
		} else {
			@Pc(22) StringNode stringNode = (StringNode) this.parameters.get((long) key);
			return stringNode == null ? this.defaultStringValue : stringNode.value;
		}
	}
}

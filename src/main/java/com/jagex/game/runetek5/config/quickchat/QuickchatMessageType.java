package com.jagex.game.runetek5.config.quickchat;

import com.jagex.game.runetek5.QuickChatValueType;
import com.jagex.game.runetek5.cache.node.lru.CacheableNode;
import com.jagex.game.runetek5.buffer.Packet;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!td")
public final class QuickchatMessageType extends CacheableNode {

	@OriginalMember(owner = "client!td", name = "A", descriptor = "[Ljava/lang/String;")
	private String[] message;

	@OriginalMember(owner = "client!td", name = "B", descriptor = "[[I")
	private int[][] configs;

	@OriginalMember(owner = "client!td", name = "C", descriptor = "[I")
	public int[] responses;

	@OriginalMember(owner = "client!td", name = "D", descriptor = "Lclient!aha;")
	public QuickchatMessageTypeList quickchatMessageTypelist;

	@OriginalMember(owner = "client!td", name = "K", descriptor = "[I")
	private int[] valueTypes;

	@OriginalMember(owner = "client!td", name = "y", descriptor = "Z")
	public boolean searchable = true;

	@OriginalMember(owner = "client!td", name = "b", descriptor = "(Z)V")
	public void postDecode() {
		if (this.responses != null) {
			for (@Pc(15) int i = 0; i < this.responses.length; i++) {
				this.responses[i] |= 0x8000;
			}
		}
	}

	@OriginalMember(owner = "client!td", name = "a", descriptor = "(B[ILclient!cea;)V")
	public void encodeValues(@OriginalArg(1) int[] values, @OriginalArg(2) Packet packet) {
		if (this.valueTypes == null) {
			return;
		}
		for (@Pc(19) int i = 0; i < this.valueTypes.length; i++) {
			if (values.length <= i) {
				return;
			}
			@Pc(27) int valueSize = this.getValueType(i).clientTransmitSize;
			if (valueSize > 0) {
				packet.writeVariableLengthLong(valueSize, (long) values[i]);
			}
		}
	}

	@OriginalMember(owner = "client!td", name = "c", descriptor = "(II)Lclient!oaa;")
	public QuickChatValueType getValueType(@OriginalArg(1) int index) {
		return this.valueTypes == null || index < 0 || this.valueTypes.length < index ? null : QuickChatValueType.forId(this.valueTypes[index]);
	}

	@OriginalMember(owner = "client!td", name = "a", descriptor = "(IIZ)I")
	public int getDynamicCommandParam(@OriginalArg(0) int type, @OriginalArg(1) int index) {
		if (this.valueTypes == null || index < 0 || this.valueTypes.length < index) {
			return -1;
		} else if (this.configs[index] == null || type < 0 || this.configs[index].length < type) {
			return -1;
		} else {
			return this.configs[index][type];
		}
	}

	@OriginalMember(owner = "client!td", name = "a", descriptor = "(ILclient!cea;I)V")
	private void decode(@OriginalArg(0) int opcode, @OriginalArg(1) Packet packet) {
		if (opcode == 1) {
			this.message = Utils.split('<', packet.readString());
			return;
		}
		@Pc(17) int count;
		@Pc(23) int index;
		if (opcode == 2) {
			count = packet.readUByte();
			this.responses = new int[count];
			for (index = 0; index < count; index++) {
				this.responses[index] = packet.readUShort();
			}
		} else if (opcode == 3) {
			count = packet.readUByte();
			this.configs = new int[count][];
			this.valueTypes = new int[count];
			for (index = 0; index < count; index++) {
				@Pc(69) int valueTypeId = packet.readUShort();
				@Pc(75) QuickChatValueType valueType = QuickChatValueType.forId(valueTypeId);
				if (valueType != null) {
					this.valueTypes[index] = valueTypeId;
					this.configs[index] = new int[valueType.parameterCount];
					for (@Pc(91) int i = 0; i < valueType.parameterCount; i++) {
						this.configs[index][i] = packet.readUShort();
					}
				}
			}
		} else if (opcode == 4) {
			this.searchable = false;
		}
	}

	@OriginalMember(owner = "client!td", name = "f", descriptor = "(B)I")
	public int getDynamicCommandCount() {
		return this.valueTypes == null ? 0 : this.valueTypes.length;
	}

	@OriginalMember(owner = "client!td", name = "a", descriptor = "(Lclient!cea;B)Ljava/lang/String;")
	public String fillDynamicValues(@OriginalArg(0) Packet packet) {
		@Pc(17) StringBuffer stringBuffer = new StringBuffer(80);
		if (this.valueTypes != null) {
			for (@Pc(22) int i = 0; i < this.valueTypes.length; i++) {
				stringBuffer.append(this.message[i]);
				stringBuffer.append(this.quickchatMessageTypelist.getValueDisplay(packet.bitpackLong(QuickChatValueType.forId(this.valueTypes[i]).serverTransmitSize), this.configs[i], this.getValueType(i)));
			}
		}
		stringBuffer.append(this.message[this.message.length - 1]);
		return stringBuffer.toString();
	}

	@OriginalMember(owner = "client!td", name = "a", descriptor = "(Lclient!cea;I)V")
	public void decode(@OriginalArg(0) Packet packet) {
		while (true) {
			@Pc(11) int opcode = packet.readUByte();
			if (opcode == 0) {
				return;
			}
			this.decode(opcode, packet);
		}
	}

	@OriginalMember(owner = "client!td", name = "a", descriptor = "(I)Ljava/lang/String;")
	public String getText() {
		@Pc(16) StringBuffer buffer = new StringBuffer(80);
		if (this.message == null) {
			return "";
		}
		buffer.append(this.message[0]);
		for (@Pc(30) int i = 1; i < this.message.length; i++) {
			buffer.append("...");
			buffer.append(this.message[i]);
		}
		return buffer.toString();
	}
}

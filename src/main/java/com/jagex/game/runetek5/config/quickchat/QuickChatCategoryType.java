package com.jagex.game.runetek5.config.quickchat;

import com.jagex.game.runetek5.cache.node.lru.CacheableNode;
import com.jagex.game.runetek5.buffer.Packet;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!ug")
public final class QuickChatCategoryType extends CacheableNode {

	@OriginalMember(owner = "client!ug", name = "x", descriptor = "[I")
	public int[] messages;

	@OriginalMember(owner = "client!ug", name = "z", descriptor = "[C")
	public char[] messageHotkeys;

	@OriginalMember(owner = "client!ug", name = "E", descriptor = "Ljava/lang/String;")
	public String name;

	@OriginalMember(owner = "client!ug", name = "F", descriptor = "[C")
	public char[] subCategoryHotkeys;

	@OriginalMember(owner = "client!ug", name = "J", descriptor = "[I")
	public int[] subCategories;

	@OriginalMember(owner = "client!ug", name = "a", descriptor = "(ILclient!cea;)V")
	public void decode(@OriginalArg(1) Packet packet) {
		while (true) {
			@Pc(17) int opcode = packet.readUByte();
			if (opcode == 0) {
				return;
			}
			this.decode(packet, opcode);
		}
	}

	@OriginalMember(owner = "client!ug", name = "a", descriptor = "(BC)I")
	public int findPhraseByShortcut(@OriginalArg(1) char shortcut) {
		if (this.messages == null) {
			return -1;
		}
		for (@Pc(12) int i = 0; i < this.messages.length; i++) {
			if (shortcut == this.messageHotkeys[i]) {
				return this.messages[i];
			}
		}
		return -1;
	}

	@OriginalMember(owner = "client!ug", name = "f", descriptor = "(B)V")
	public void postDecode() {
		@Pc(6) int i;
		if (this.messages != null) {
			for (i = 0; i < this.messages.length; i++) {
				this.messages[i] |= 0x8000;
			}
		}
		if (this.subCategories != null) {
			for (i = 0; i < this.subCategories.length; i++) {
				this.subCategories[i] |= 0x8000;
			}
		}
	}

	@OriginalMember(owner = "client!ug", name = "a", descriptor = "(CI)I")
	public int findSubcategoryByShortcut(@OriginalArg(0) char shortcut) {
		if (this.subCategories == null) {
			return -1;
		}
		for (@Pc(12) int i = 0; i < this.subCategories.length; i++) {
			if (this.subCategoryHotkeys[i] == shortcut) {
				return this.subCategories[i];
			}
		}
		return -1;
	}

	@OriginalMember(owner = "client!ug", name = "a", descriptor = "(ILclient!cea;I)V")
	private void decode(@OriginalArg(1) Packet packet, @OriginalArg(2) int opcode) {
		if (opcode == 1) {
			this.name = packet.readString();
			return;
		}
		@Pc(15) int count;
		@Pc(25) int index;
		@Pc(38) byte hotkey;
		if (opcode == 2) {
			count = packet.readUByte();
			this.subCategories = new int[count];
			this.subCategoryHotkeys = new char[count];
			for (index = 0; index < count; index++) {
				this.subCategories[index] = packet.readUShort();
				hotkey = packet.readByte();
				this.subCategoryHotkeys[index] = hotkey == 0 ? 0 : Utils.cp1252ToChar(hotkey);
			}
		} else if (opcode == 3) {
			count = packet.readUByte();
			this.messageHotkeys = new char[count];
			this.messages = new int[count];
			for (index = 0; index < count; index++) {
				this.messages[index] = packet.readUShort();
				hotkey = packet.readByte();
				this.messageHotkeys[index] = hotkey == 0 ? 0 : Utils.cp1252ToChar(hotkey);
			}
		}
	}
}

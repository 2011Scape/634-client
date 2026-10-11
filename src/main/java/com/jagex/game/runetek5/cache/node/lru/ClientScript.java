package com.jagex.game.runetek5.cache.node.lru;

import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable;
import com.jagex.game.runetek5.cache.node.lru.CacheableNode;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.ClientTrigger;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.cache.node.IntNode;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.Resource;
import com.jagex.game.runetek5.cache.node.CacheableNodeQueue;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!jk")
public final class ClientScript extends CacheableNode {

	@OriginalMember(owner = "client!jk", name = "x", descriptor = "I")
	public int intLocalsCount;

	@OriginalMember(owner = "client!jk", name = "y", descriptor = "I")
	public int stringLocalsCount;

	@OriginalMember(owner = "client!jk", name = "z", descriptor = "Ljava/lang/String;")
	public String scriptName;

	@OriginalMember(owner = "client!jk", name = "A", descriptor = "Lclient!uw;")
	public ClientTrigger triggerType;

	@OriginalMember(owner = "client!jk", name = "B", descriptor = "[Ljava/lang/String;")
	public String[] stringOpValues;

	@OriginalMember(owner = "client!jk", name = "C", descriptor = "[Lclient!eq;")
	public IterableHashTable[] switchMaps;

	@OriginalMember(owner = "client!jk", name = "D", descriptor = "[I")
	public int[] intOpValues;

	@OriginalMember(owner = "client!jk", name = "E", descriptor = "I")
	public int stringArgsCount;

	@OriginalMember(owner = "client!jk", name = "F", descriptor = "[I")
	public int[] operations;

	@OriginalMember(owner = "client!jk", name = "G", descriptor = "I")
	public int intArgsCount;
	@OriginalMember(owner = "client!jda", name = "a", descriptor = "([BI)Lclient!jk;")
	public static ClientScript decodeScript(@OriginalArg(0) byte[] src) {
		@Pc(14) ClientScript clientScript = new ClientScript();
		@Pc(19) Packet packet = new Packet(src);
		packet.currentPosition = packet.buffer.length - 2;
		@Pc(30) int switchDataLength = packet.readUShort();
		@Pc(41) int codeEnd = packet.buffer.length - switchDataLength - 12 - 2;
		packet.currentPosition = codeEnd;
		@Pc(48) int instructionCount = packet.readInt();
		clientScript.intLocalsCount = packet.readUShort();
		clientScript.stringLocalsCount = packet.readUShort();
		clientScript.intArgsCount = packet.readUShort();
		clientScript.stringArgsCount = packet.readUShort();
		@Pc(72) int switchCount = packet.readUByte();
		@Pc(80) int local80;
		@Pc(86) int local86;
		if (switchCount > 0) {
			clientScript.switchMaps = new IterableHashTable[switchCount];
			for (int switchIndex = 0; switchIndex < switchCount; switchIndex++) {
				int caseCount = packet.readUShort();
				@Pc(93) IterableHashTable iterableHashTable = new IterableHashTable(Utils.nextPowerOfTwo(caseCount));
				clientScript.switchMaps[switchIndex] = iterableHashTable;
				while (caseCount-- > 0) {
					@Pc(104) int caseValue = packet.readInt();
					@Pc(108) int jumpOffset = packet.readInt();
					iterableHashTable.put((long) caseValue, new IntNode(jumpOffset));
				}
			}
		}
		packet.currentPosition = 0;
		clientScript.scriptName = packet.readNullString();
		clientScript.intOpValues = new int[instructionCount];
		clientScript.operations = new int[instructionCount];
		clientScript.stringOpValues = new String[instructionCount];
		int instructionIndex = 0;
		while (codeEnd > packet.currentPosition) {
			int opcode = packet.readUShort();
			if (opcode == 3) {
				clientScript.stringOpValues[instructionIndex] = packet.readString().intern();
			} else if (opcode >= 100 || opcode == 21 || opcode == 38 || opcode == 39) {
				clientScript.intOpValues[instructionIndex] = packet.readUByte();
			} else {
				clientScript.intOpValues[instructionIndex] = packet.readInt();
			}
			clientScript.operations[instructionIndex++] = opcode;
		}
		return clientScript;
	}
	@OriginalMember(owner = "client!on", name = "a", descriptor = "(IIILclient!uw;)Lclient!jk;")
	public static ClientScript getTriggerScript(@OriginalArg(0) int subjectId, @OriginalArg(1) int categoryId, @OriginalArg(3) ClientTrigger triggerType) {
		@Pc(10) int key = triggerType.id | subjectId << 10;
		@Pc(19) ClientScript clientScript = (ClientScript) ClientScript.aClass379_1.get((long) key << 16);
		if (clientScript != null) {
			return clientScript;
		}
		@Pc(31) byte[] data = Resource.CS2.getFile(Resource.CS2.getGroupId(key));
		if (data == null) {
			key = triggerType.id | categoryId + 65536 << 10;
			clientScript = (ClientScript) ClientScript.aClass379_1.get((long) key << 16);
			if (clientScript != null) {
				return clientScript;
			}
			data = Resource.CS2.getFile(Resource.CS2.getGroupId(key));
			if (data == null) {
				key = triggerType.id | 0x3FFFC00;
				clientScript = (ClientScript) ClientScript.aClass379_1.get((long) key << 16);
				if (clientScript != null) {
					return clientScript;
				}
				data = Resource.CS2.getFile(Resource.CS2.getGroupId(key));
				if (data == null) {
					return null;
				} else if (data.length <= 1) {
					return null;
				} else {
					try {
						clientScript = ClientScript.decodeScript(data);
					} catch (@Pc(191) Exception ex) {
						throw new RuntimeException(ex.getMessage() + " S: " + key);
					}
					clientScript.triggerType = triggerType;
					ClientScript.aClass379_1.put((long) key << 16, clientScript);
					return clientScript;
				}
			} else if (data.length <= 1) {
				return null;
			} else {
				try {
					clientScript = ClientScript.decodeScript(data);
				} catch (@Pc(121) Exception ex2) {
					throw new RuntimeException(ex2.getMessage() + " S: " + key);
				}
				clientScript.triggerType = triggerType;
				ClientScript.aClass379_1.put((long) key << 16, clientScript);
				return clientScript;
			}
		} else if (data.length <= 1) {
			return null;
		} else {
			try {
				clientScript = ClientScript.decodeScript(data);
			} catch (@Pc(47) Exception ex3) {
				throw new RuntimeException(ex3.getMessage() + " S: " + key);
			}
			clientScript.triggerType = triggerType;
			ClientScript.aClass379_1.put((long) key << 16, clientScript);
			return clientScript;
		}
	}
	@OriginalMember(owner = "client!dea", name = "a", descriptor = "(II)Lclient!jk;")
	public static ClientScript getClientScript(@OriginalArg(1) int scriptId) {
		@Pc(10) ClientScript clientScript = (ClientScript) ClientScript.aClass379_1.get((long) scriptId);
		if (clientScript != null) {
			return clientScript;
		}
		@Pc(20) byte[] data = Resource.CS2.getFile(scriptId, 0);
		if (data == null || data.length <= 1) {
			return null;
		}
		try {
			clientScript = ClientScript.decodeScript(data);
		} catch (@Pc(34) Exception ex) {
			throw new RuntimeException(ex.getMessage() + " S: " + scriptId);
		}
		ClientScript.aClass379_1.put((long) scriptId, clientScript);
		return clientScript;
	}
	@OriginalMember(owner = "client!afa", name = "s", descriptor = "Lclient!wu;")
	public static final CacheableNodeQueue aClass379_1 = new CacheableNodeQueue(128);

	@OriginalMember(owner = "client!gea", name = "b", descriptor = "(I)V")
	public static void clearCache() {
		ClientScript.aClass379_1.clear();
	}
}

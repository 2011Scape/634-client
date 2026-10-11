package com.jagex.game.runetek5.config.quest;

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

@OriginalClass("client!cq")
public final class QuestType {

	@OriginalMember(owner = "client!cq", name = "b", descriptor = "[[I")
	private int[][] varbitValues;

	@OriginalMember(owner = "client!cq", name = "c", descriptor = "[[I")
	private int[][] varpValues;

	@OriginalMember(owner = "client!cq", name = "d", descriptor = "[I")
	private int[] minVarbitValue;

	@OriginalMember(owner = "client!cq", name = "g", descriptor = "[I")
	private int[] minVarpValue;

	@OriginalMember(owner = "client!cq", name = "h", descriptor = "[[I")
	private int[][] levelRequirements;

	@OriginalMember(owner = "client!cq", name = "i", descriptor = "[I")
	private int[] maxVarbitValue;

	@OriginalMember(owner = "client!cq", name = "j", descriptor = "[I")
	private int[] questPrerequisiteIds;

	@OriginalMember(owner = "client!cq", name = "l", descriptor = "Lclient!eq;")
	private IterableHashTable params;

	@OriginalMember(owner = "client!cq", name = "m", descriptor = "[I")
	private int[] maxVarpValue;

	@OriginalMember(owner = "client!cq", name = "n", descriptor = "[I")
	private int[] unusedOpcode10;

	@OriginalMember(owner = "client!cq", name = "o", descriptor = "[I")
	private int[] varbitRequirements;

	@OriginalMember(owner = "client!cq", name = "p", descriptor = "Ljava/lang/String;")
	private String sortName;

	@OriginalMember(owner = "client!cq", name = "q", descriptor = "[Ljava/lang/String;")
	private String[] varpRequirementNames;

	@OriginalMember(owner = "client!cq", name = "r", descriptor = "Ljava/lang/String;")
	private String name;

	@OriginalMember(owner = "client!cq", name = "t", descriptor = "[I")
	private int[] varpRequirements;

	@OriginalMember(owner = "client!cq", name = "v", descriptor = "[Ljava/lang/String;")
	private String[] varbitRequirementNames;

	@OriginalMember(owner = "client!cq", name = "f", descriptor = "I")
	public int graphicId = -1;

	@OriginalMember(owner = "client!cq", name = "a", descriptor = "(B)V")
	public void postDecode() {
		if (this.sortName == null) {
			this.sortName = this.name;
		}
	}

	@OriginalMember(owner = "client!cq", name = "a", descriptor = "(Lclient!cea;IB)V")
	private void decode(@OriginalArg(0) Packet packet, @OriginalArg(1) int opcode) {
		if (opcode == 1) {
			this.name = packet.readJagString();
		} else if (opcode == 2) {
			this.sortName = packet.readJagString();
		} else {
			@Pc(38) int count;
			@Pc(45) int local45;
			if (opcode == 3) {
				count = packet.readUByte();
				this.varpValues = new int[count][3];
				for (int varpValueIndex = 0; varpValueIndex < count; varpValueIndex++) {
					this.varpValues[varpValueIndex][0] = packet.readUShort();
					this.varpValues[varpValueIndex][1] = packet.readInt();
					this.varpValues[varpValueIndex][2] = packet.readInt();
				}
			} else if (opcode == 4) {
				count = packet.readUByte();
				this.varbitValues = new int[count][3];
				for (int varbitValueIndex = 0; varbitValueIndex < count; varbitValueIndex++) {
					this.varbitValues[varbitValueIndex][0] = packet.readUShort();
					this.varbitValues[varbitValueIndex][1] = packet.readInt();
					this.varbitValues[varbitValueIndex][2] = packet.readInt();
				}
			} else if (opcode == 5) {
				packet.readUShort();
			} else if (opcode == 6) {
				packet.readUByte();
				return;
			} else if (opcode == 7) {
				packet.readUByte();
				return;
			} else if (opcode != 8) {
				if (opcode == 9) {
					packet.readUByte();
					return;
				}
				if (opcode == 10) {
					count = packet.readUByte();
					this.unusedOpcode10 = new int[count];
					for (int unusedIndex = 0; unusedIndex < count; unusedIndex++) {
						this.unusedOpcode10[unusedIndex] = packet.readInt();
					}
					return;
				}
				if (opcode == 12) {
					packet.readInt();
					return;
				}
				if (opcode != 13) {
					if (opcode == 14) {
						count = packet.readUByte();
						this.levelRequirements = new int[count][2];
						for (int levelIndex = 0; levelIndex < count; levelIndex++) {
							this.levelRequirements[levelIndex][0] = packet.readUByte();
							this.levelRequirements[levelIndex][1] = packet.readUByte();
						}
					} else if (opcode == 15) {
						packet.readUShort();
						return;
					} else if (opcode == 17) {
						this.graphicId = packet.readUShort();
						return;
					} else if (opcode == 18) {
						count = packet.readUByte();
						this.varpRequirements = new int[count];
						this.varpRequirementNames = new String[count];
						this.maxVarpValue = new int[count];
						this.minVarpValue = new int[count];
						for (int varpRequirementIndex = 0; varpRequirementIndex < count; varpRequirementIndex++) {
							this.varpRequirements[varpRequirementIndex] = packet.readInt();
							this.minVarpValue[varpRequirementIndex] = packet.readInt();
							this.maxVarpValue[varpRequirementIndex] = packet.readInt();
							this.varpRequirementNames[varpRequirementIndex] = packet.readString();
						}
						return;
					} else if (opcode == 19) {
						count = packet.readUByte();
						this.varbitRequirementNames = new String[count];
						this.maxVarbitValue = new int[count];
						this.varbitRequirements = new int[count];
						this.minVarbitValue = new int[count];
						for (int varbitRequirementIndex = 0; varbitRequirementIndex < count; varbitRequirementIndex++) {
							this.varbitRequirements[varbitRequirementIndex] = packet.readInt();
							this.minVarbitValue[varbitRequirementIndex] = packet.readInt();
							this.maxVarbitValue[varbitRequirementIndex] = packet.readInt();
							this.varbitRequirementNames[varbitRequirementIndex] = packet.readString();
						}
						return;
					} else if (opcode == 249) {
						count = packet.readUByte();
						if (this.params == null) {
							int bucketCount = Utils.nextPowerOfTwo(count);
							this.params = new IterableHashTable(bucketCount);
						}
						for (int paramIndex = 0; paramIndex < count; paramIndex++) {
							@Pc(351) boolean isString = packet.readUByte() == 1;
							@Pc(355) int key = packet.readUMedium();
							@Pc(364) Node node;
							if (isString) {
								node = new StringNode(packet.readString());
							} else {
								node = new IntNode(packet.readInt());
							}
							this.params.put((long) key, node);
						}
						return;
					}
					return;
				}
				count = packet.readUByte();
				this.questPrerequisiteIds = new int[count];
				for (int prerequisiteIndex = 0; prerequisiteIndex < count; prerequisiteIndex++) {
					this.questPrerequisiteIds[prerequisiteIndex] = packet.readUShort();
				}
				return;
			}
		}
	}

	@OriginalMember(owner = "client!cq", name = "a", descriptor = "(ILclient!cea;)V")
	public void decode(@OriginalArg(1) Packet packet) {
		while (true) {
			@Pc(17) int opcode = packet.readUByte();
			if (opcode == 0) {
				return;
			}
			this.decode(packet, opcode);
		}
	}
}

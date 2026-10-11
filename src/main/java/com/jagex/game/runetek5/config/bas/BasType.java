package com.jagex.game.runetek5.config.bas;

import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.graphics.Matrix;
import com.jagex.game.runetek5.graphics.Toolkit;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!sg")
public final class BasType {

	@OriginalMember(owner = "client!sg", name = "f", descriptor = "[I")
	public int[] objVisibility;

	@OriginalMember(owner = "client!sg", name = "g", descriptor = "[I")
	public int[] turnAngleAdjustment;

	@OriginalMember(owner = "client!sg", name = "j", descriptor = "[[I")
	public int[][] projectionOffset;

	@OriginalMember(owner = "client!sg", name = "y", descriptor = "[Lclient!bca;")
	private Matrix[] transformMatrices;

	@OriginalMember(owner = "client!sg", name = "N", descriptor = "[[I")
	public int[][] equipmentTransforms;

	@OriginalMember(owner = "client!sg", name = "h", descriptor = "I")
	public int teleTurnCcwSequence = -1;

	@OriginalMember(owner = "client!sg", name = "a", descriptor = "I")
	public int rollAcceleration = 0;

	@OriginalMember(owner = "client!sg", name = "k", descriptor = "I")
	public int runTurnCwSequence = -1;

	@OriginalMember(owner = "client!sg", name = "o", descriptor = "[I")
	public int[] randomStandSequences = null;

	@OriginalMember(owner = "client!sg", name = "d", descriptor = "I")
	public int pitchMaxVelocity = 0;

	@OriginalMember(owner = "client!sg", name = "w", descriptor = "I")
	public int secondaryBarSpriteGroup = -1;

	@OriginalMember(owner = "client!sg", name = "r", descriptor = "I")
	public int standSequenceId = -1;

	@OriginalMember(owner = "client!sg", name = "i", descriptor = "I")
	public int healthBarSpriteGroup = -1;

	@OriginalMember(owner = "client!sg", name = "t", descriptor = "I")
	public int runDir3 = -1;

	@OriginalMember(owner = "client!sg", name = "p", descriptor = "I")
	public int standTurnCcwSequence = -1;

	@OriginalMember(owner = "client!sg", name = "v", descriptor = "I")
	public int standTurnCwSequence = -1;

	@OriginalMember(owner = "client!sg", name = "F", descriptor = "I")
	public int modelWidth = 0;

	@OriginalMember(owner = "client!sg", name = "l", descriptor = "I")
	public int hillRotateX = 0;

	@OriginalMember(owner = "client!sg", name = "q", descriptor = "I")
	public int yawMaxVelocity = 0;

	@OriginalMember(owner = "client!sg", name = "n", descriptor = "I")
	public int runDir2 = -1;

	@OriginalMember(owner = "client!sg", name = "C", descriptor = "[I")
	public int[] randomStandSequenceChances = null;

	@OriginalMember(owner = "client!sg", name = "s", descriptor = "I")
	public int runningSequence = -1;

	@OriginalMember(owner = "client!sg", name = "J", descriptor = "I")
	public int rollMaxVelocity = 0;

	@OriginalMember(owner = "client!sg", name = "A", descriptor = "I")
	public int teleTurnCwSequence = -1;

	@OriginalMember(owner = "client!sg", name = "L", descriptor = "I")
	public int walkDir2 = -1;

	@OriginalMember(owner = "client!sg", name = "z", descriptor = "I")
	public int pitchTargetAngle = 0;

	@OriginalMember(owner = "client!sg", name = "B", descriptor = "I")
	public int iconHeightOverride = -1;

	@OriginalMember(owner = "client!sg", name = "P", descriptor = "I")
	public int modelLength = 0;

	@OriginalMember(owner = "client!sg", name = "M", descriptor = "I")
	public int walkDir3 = -1;

	@OriginalMember(owner = "client!sg", name = "T", descriptor = "I")
	public int unused = -1;

	@OriginalMember(owner = "client!sg", name = "R", descriptor = "I")
	public int hillRotateZ = 0;

	@OriginalMember(owner = "client!sg", name = "e", descriptor = "I")
	public int runTurnCcwSequence = -1;

	@OriginalMember(owner = "client!sg", name = "D", descriptor = "I")
	private int randomStandChanceTotal = 0;

	@OriginalMember(owner = "client!sg", name = "m", descriptor = "Z")
	public boolean rendersShadow = true;

	@OriginalMember(owner = "client!sg", name = "Y", descriptor = "I")
	public int rollTargetAngle = 0;

	@OriginalMember(owner = "client!sg", name = "O", descriptor = "I")
	public int teleportSequence = -1;

	@OriginalMember(owner = "client!sg", name = "H", descriptor = "I")
	public int walkTurnCcwSequence = -1;

	@OriginalMember(owner = "client!sg", name = "Q", descriptor = "I")
	private final int toolkitIndex = -1;

	@OriginalMember(owner = "client!sg", name = "Z", descriptor = "I")
	public int walkSequence = -1;

	@OriginalMember(owner = "client!sg", name = "X", descriptor = "I")
	public int runDir1 = -1;

	@OriginalMember(owner = "client!sg", name = "W", descriptor = "I")
	public int yawAcceleration = 0;

	@OriginalMember(owner = "client!sg", name = "V", descriptor = "I")
	public int teleDir1 = -1;

	@OriginalMember(owner = "client!sg", name = "x", descriptor = "I")
	public int teleDir3 = -1;

	@OriginalMember(owner = "client!sg", name = "cb", descriptor = "I")
	public int walkDir1 = -1;

	@OriginalMember(owner = "client!sg", name = "bb", descriptor = "I")
	public int walkTurnCwSequence = -1;

	@OriginalMember(owner = "client!sg", name = "K", descriptor = "I")
	public int pitchAcceleration = 0;

	@OriginalMember(owner = "client!sg", name = "gb", descriptor = "I")
	public int teleDir2 = -1;

	@OriginalMember(owner = "client!sg", name = "a", descriptor = "(Lclient!ha;I)[Lclient!bca;")
	public Matrix[] getSubmeshMatrices(@OriginalArg(0) Toolkit toolkit) {
		if (this.transformMatrices != null && this.toolkitIndex == toolkit.index) {
			return this.transformMatrices;
		} else if (this.equipmentTransforms == null) {
			return null;
		} else {
			this.transformMatrices = new Matrix[this.equipmentTransforms.length];
			for (@Pc(33) int i = 0; i < this.equipmentTransforms.length; i++) {
				@Pc(37) int translateX = 0;
				@Pc(39) int translateY = 0;
				@Pc(41) int translateZ = 0;
				@Pc(43) int rotateX = 0;
				@Pc(45) int rotateY = 0;
				@Pc(47) int rotateZ = 0;
				if (this.equipmentTransforms[i] != null) {
					translateY = this.equipmentTransforms[i][1];
					translateZ = this.equipmentTransforms[i][2];
					rotateX = this.equipmentTransforms[i][3] << 3;
					translateX = this.equipmentTransforms[i][0];
					rotateY = this.equipmentTransforms[i][4] << 3;
					rotateZ = this.equipmentTransforms[i][5] << 3;
				}
				if (translateX != 0 || translateY != 0 || translateZ != 0 || rotateX != 0 || rotateY != 0 || rotateZ != 0) {
					@Pc(123) Matrix matrix = this.transformMatrices[i] = toolkit.createMatrix();
					if (rotateZ != 0) {
						matrix.rotateAxisZ(rotateZ);
					}
					if (rotateX != 0) {
						matrix.rotateAxisX(rotateX);
					}
					if (rotateY != 0) {
						matrix.rotateAxisY(rotateY);
					}
					matrix.translate(translateX, translateY, translateZ);
				}
			}
			return this.transformMatrices;
		}
	}

	@OriginalMember(owner = "client!sg", name = "a", descriptor = "(Lclient!cea;I)V")
	public void decode(@OriginalArg(0) Packet packet) {
		while (true) {
			@Pc(5) int opcode = packet.readUByte();
			if (opcode == 0) {
				return;
			}
			this.decode(packet, opcode);
		}
	}

	@OriginalMember(owner = "client!sg", name = "b", descriptor = "(B)I")
	public int ready() {
		if (this.standSequenceId != -1) {
			return this.standSequenceId;
		} else if (this.randomStandSequences == null) {
			return -1;
		} else {
			@Pc(22) int weight = (int) ((double) this.randomStandChanceTotal * Math.random());
			@Pc(24) int i;
			for (i = 0; weight >= this.randomStandSequenceChances[i]; i++) {
				weight -= this.randomStandSequenceChances[i];
			}
			return this.randomStandSequences[i];
		}
	}

	@OriginalMember(owner = "client!sg", name = "a", descriptor = "(Lclient!cea;II)V")
	private void decode(@OriginalArg(0) Packet packet, @OriginalArg(2) int opcode) {
		if (opcode == 1) {
			this.standSequenceId = packet.readUShort();
			this.walkSequence = packet.readUShort();
			if (this.walkSequence == 65535) {
				this.walkSequence = -1;
			}
			if (this.standSequenceId == 65535) {
				this.standSequenceId = -1;
			}
		} else if (opcode == 2) {
			this.teleportSequence = packet.readUShort();
		} else if (opcode == 3) {
			this.teleDir3 = packet.readUShort();
		} else if (opcode == 4) {
			this.teleDir2 = packet.readUShort();
		} else if (opcode == 5) {
			this.teleDir1 = packet.readUShort();
		} else if (opcode == 6) {
			this.runningSequence = packet.readUShort();
		} else if (opcode == 7) {
			this.runDir3 = packet.readUShort();
		} else if (opcode == 8) {
			this.runDir2 = packet.readUShort();
		} else if (opcode == 9) {
			this.runDir1 = packet.readUShort();
		} else if (opcode == 26) {
			this.modelWidth = (short) (packet.readUByte() * 4);
			this.modelLength = (short) (packet.readUByte() * 4);
		} else {
			@Pc(111) int local111;
			@Pc(119) int i;
			if (opcode == 27) {
				if (this.equipmentTransforms == null) {
					this.equipmentTransforms = new int[12][];
				}
				int slot = packet.readUByte();
				this.equipmentTransforms[slot] = new int[6];
				for (i = 0; i < 6; i++) {
					this.equipmentTransforms[slot][i] = packet.readShort();
				}
			} else if (opcode == 28) {
				this.objVisibility = new int[12];
				for (int slot = 0; slot < 12; slot++) {
					this.objVisibility[slot] = packet.readUByte();
					if (this.objVisibility[slot] == 255) {
						this.objVisibility[slot] = -1;
					}
				}
			} else if (opcode == 29) {
				this.yawAcceleration = packet.readUByte();
			} else if (opcode == 30) {
				this.yawMaxVelocity = packet.readUShort();
			} else if (opcode == 31) {
				this.rollAcceleration = packet.readUByte();
			} else if (opcode == 32) {
				this.rollMaxVelocity = packet.readUShort();
			} else if (opcode == 33) {
				this.rollTargetAngle = packet.readShort();
			} else if (opcode == 34) {
				this.pitchAcceleration = packet.readUByte();
			} else if (opcode == 35) {
				this.pitchMaxVelocity = packet.readUShort();
			} else if (opcode == 36) {
				this.pitchTargetAngle = packet.readShort();
			} else if (opcode == 37) {
				this.unused = packet.readUByte();
			} else if (opcode == 38) {
				this.standTurnCcwSequence = packet.readUShort();
			} else if (opcode == 39) {
				this.standTurnCwSequence = packet.readUShort();
			} else if (opcode == 40) {
				this.walkDir3 = packet.readUShort();
			} else if (opcode == 41) {
				this.walkDir2 = packet.readUShort();
			} else if (opcode == 42) {
				this.walkDir1 = packet.readUShort();
			} else if (opcode == 43) {
				this.healthBarSpriteGroup = packet.readUShort();
			} else if (opcode == 44) {
				this.secondaryBarSpriteGroup = packet.readUShort();
			} else if (opcode == 45) {
				this.iconHeightOverride = packet.readUShort();
			} else if (opcode == 46) {
				this.teleTurnCcwSequence = packet.readUShort();
			} else if (opcode == 47) {
				this.teleTurnCwSequence = packet.readUShort();
			} else if (opcode == 48) {
				this.runTurnCcwSequence = packet.readUShort();
			} else if (opcode == 49) {
				this.runTurnCwSequence = packet.readUShort();
			} else if (opcode == 50) {
				this.walkTurnCcwSequence = packet.readUShort();
			} else if (opcode == 51) {
				this.walkTurnCwSequence = packet.readUShort();
			} else if (opcode == 52) {
				int sequenceCount = packet.readUByte();
				this.randomStandSequences = new int[sequenceCount];
				this.randomStandSequenceChances = new int[sequenceCount];
				for (i = 0; i < sequenceCount; i++) {
					this.randomStandSequences[i] = packet.readUShort();
					@Pc(346) int chance = packet.readUByte();
					this.randomStandSequenceChances[i] = chance;
					this.randomStandChanceTotal += chance;
				}
			} else if (opcode == 53) {
				this.rendersShadow = false;
			} else if (opcode == 54) {
				this.hillRotateX = packet.readUByte() << 6;
				this.hillRotateZ = packet.readUByte() << 6;
			} else if (opcode == 55) {
				if (this.turnAngleAdjustment == null) {
					this.turnAngleAdjustment = new int[12];
				}
				int slot = packet.readUByte();
				this.turnAngleAdjustment[slot] = packet.readUShort();
			} else if (opcode == 56) {
				if (this.projectionOffset == null) {
					this.projectionOffset = new int[12][];
				}
				int slot = packet.readUByte();
				this.projectionOffset[slot] = new int[3];
				for (i = 0; i < 3; i++) {
					this.projectionOffset[slot][i] = packet.readShort();
				}
			}
		}
	}

	@OriginalMember(owner = "client!sg", name = "a", descriptor = "(II)Z")
	public boolean isReady(@OriginalArg(0) int id) {
		if (id == -1) {
			return false;
		} else if (id == this.standSequenceId) {
			return true;
		} else {
			if (this.randomStandSequences != null) {
				for (@Pc(34) int i = 0; i < this.randomStandSequences.length; i++) {
					if (id == this.randomStandSequences[i]) {
						return true;
					}
				}
			}
			return false;
		}
	}
}

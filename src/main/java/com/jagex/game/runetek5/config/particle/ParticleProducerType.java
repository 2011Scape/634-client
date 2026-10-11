package com.jagex.game.runetek5.config.particle;

import com.jagex.game.runetek5.buffer.Packet;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.cache.node.lru.LRUCache;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;

@OriginalClass("client!qb")
public final class ParticleProducerType {

	@OriginalMember(owner = "client!qb", name = "a", descriptor = "I")
	public int colorFadeStart;

	@OriginalMember(owner = "client!qb", name = "e", descriptor = "I")
	public int speedStep;

	@OriginalMember(owner = "client!qb", name = "h", descriptor = "[I")
	public int[] effectiveVertexUids;

	@OriginalMember(owner = "client!qb", name = "i", descriptor = "I")
	public int alphaFadeStart;

	@OriginalMember(owner = "client!qb", name = "k", descriptor = "I")
	public int startSpeedChange;

	@OriginalMember(owner = "client!qb", name = "m", descriptor = "[I")
	public int[] copiedParticleFileIds;

	@OriginalMember(owner = "client!qb", name = "n", descriptor = "I")
	public int faceColorRgb;

	@OriginalMember(owner = "client!qb", name = "p", descriptor = "I")
	public int startSizeChange;

	@OriginalMember(owner = "client!qb", name = "q", descriptor = "I")
	public int maximumParticleRate;

	@OriginalMember(owner = "client!qb", name = "r", descriptor = "I")
	public int minimumStartG;

	@OriginalMember(owner = "client!qb", name = "s", descriptor = "I")
	public int minimumParticleRate;

	@OriginalMember(owner = "client!qb", name = "t", descriptor = "I")
	public int minimumSpeed;

	@OriginalMember(owner = "client!qb", name = "v", descriptor = "I")
	public int minimumLifetime;

	@OriginalMember(owner = "client!qb", name = "w", descriptor = "[I")
	public int[] localEffectors;

	@OriginalMember(owner = "client!qb", name = "A", descriptor = "I")
	public int minimumStartB;

	@OriginalMember(owner = "client!qb", name = "E", descriptor = "[I")
	public int[] generalEffectors;

	@OriginalMember(owner = "client!qb", name = "F", descriptor = "I")
	public int greenVariance;

	@OriginalMember(owner = "client!qb", name = "H", descriptor = "I")
	public int blueFadeStep;

	@OriginalMember(owner = "client!qb", name = "J", descriptor = "I")
	public int maximumLifetime;

	@OriginalMember(owner = "client!qb", name = "K", descriptor = "I")
	public int blueVariance;

	@OriginalMember(owner = "client!qb", name = "M", descriptor = "I")
	public int alphaVariance;

	@OriginalMember(owner = "client!qb", name = "O", descriptor = "I")
	private int maximumStartB;

	@OriginalMember(owner = "client!qb", name = "P", descriptor = "I")
	public int minimumStartA;

	@OriginalMember(owner = "client!qb", name = "R", descriptor = "S")
	public short maximalAngleH;

	@OriginalMember(owner = "client!qb", name = "S", descriptor = "I")
	public int alphaFadeStep;

	@OriginalMember(owner = "client!qb", name = "T", descriptor = "I")
	public int maximumSize;

	@OriginalMember(owner = "client!qb", name = "U", descriptor = "S")
	public short minimumAngleV;

	@OriginalMember(owner = "client!qb", name = "V", descriptor = "I")
	private int maximumStartG;

	@OriginalMember(owner = "client!qb", name = "W", descriptor = "I")
	private int maximumStartR;

	@OriginalMember(owner = "client!qb", name = "X", descriptor = "I")
	public int greenFadeStep;

	@OriginalMember(owner = "client!qb", name = "cb", descriptor = "I")
	public int redFadeStep;

	@OriginalMember(owner = "client!qb", name = "eb", descriptor = "I")
	private int minimumStartColorRgb;

	@OriginalMember(owner = "client!qb", name = "fb", descriptor = "I")
	public int minimumStartR;

	@OriginalMember(owner = "client!qb", name = "hb", descriptor = "S")
	public short minimalAngleH;

	@OriginalMember(owner = "client!qb", name = "jb", descriptor = "I")
	public int redVariance;

	@OriginalMember(owner = "client!qb", name = "kb", descriptor = "I")
	private int maximumStartColorRgb;

	@OriginalMember(owner = "client!qb", name = "nb", descriptor = "I")
	public int speedFallOffStep;

	@OriginalMember(owner = "client!qb", name = "ob", descriptor = "I")
	public int maximumSpeed;

	@OriginalMember(owner = "client!qb", name = "pb", descriptor = "I")
	public int sizeChangeStep;

	@OriginalMember(owner = "client!qb", name = "rb", descriptor = "S")
	public short maximalAngleV;

	@OriginalMember(owner = "client!qb", name = "sb", descriptor = "I")
	public int minimumSize;

	@OriginalMember(owner = "client!qb", name = "ub", descriptor = "I")
	private int maximumStartA;

	@OriginalMember(owner = "client!qb", name = "g", descriptor = "I")
	public int minimumSetting = 0;

	@OriginalMember(owner = "client!qb", name = "j", descriptor = "Z")
	public boolean isTextured = false;

	@OriginalMember(owner = "client!qb", name = "o", descriptor = "I")
	public int nonTexturedProducerId = -1;

	@OriginalMember(owner = "client!qb", name = "G", descriptor = "I")
	public int endSpeed = -1;

	@OriginalMember(owner = "client!qb", name = "N", descriptor = "Z")
	public boolean periodic = true;

	@OriginalMember(owner = "client!qb", name = "l", descriptor = "I")
	public int lowestDisplayPlane = -2;

	@OriginalMember(owner = "client!qb", name = "y", descriptor = "Z")
	public boolean adjustsToScene = false;

	@OriginalMember(owner = "client!qb", name = "D", descriptor = "I")
	private int speedChange = 100;

	@OriginalMember(owner = "client!qb", name = "d", descriptor = "Z")
	public boolean killAboveSurface = true;

	@OriginalMember(owner = "client!qb", name = "u", descriptor = "I")
	public int emissionEndTime = -1;

	@OriginalMember(owner = "client!qb", name = "Z", descriptor = "I")
	public int highestDisplayPlane = -2;

	@OriginalMember(owner = "client!qb", name = "c", descriptor = "I")
	private int colorFading = 100;

	@OriginalMember(owner = "client!qb", name = "db", descriptor = "I")
	public int endSize = -1;

	@OriginalMember(owner = "client!qb", name = "bb", descriptor = "I")
	public int lifetime = -1;

	@OriginalMember(owner = "client!qb", name = "gb", descriptor = "Z")
	public boolean adjustsLightIntensity = true;

	@OriginalMember(owner = "client!qb", name = "Y", descriptor = "I")
	public int updatesPerCycle = 0;

	@OriginalMember(owner = "client!qb", name = "ab", descriptor = "Z")
	public boolean killOverlapping = false;

	@OriginalMember(owner = "client!qb", name = "Q", descriptor = "I")
	public int materialId = -1;

	@OriginalMember(owner = "client!qb", name = "ib", descriptor = "I")
	private int sizeChange = 100;

	@OriginalMember(owner = "client!qb", name = "qb", descriptor = "I")
	private int alphaFading = 100;

	@OriginalMember(owner = "client!qb", name = "mb", descriptor = "I")
	public int speedUpdateType = 0;

	@OriginalMember(owner = "client!qb", name = "lb", descriptor = "Z")
	public boolean unusedOpcode26 = true;

	@OriginalMember(owner = "client!qb", name = "I", descriptor = "Z")
	public boolean activeFirst = true;

	@OriginalMember(owner = "client!qb", name = "tb", descriptor = "Z")
	public boolean uniformColorVariance = true;

	@OriginalMember(owner = "client!qb", name = "a", descriptor = "(ILclient!cea;I)V")
	private void decode(@OriginalArg(1) Packet packet, @OriginalArg(2) int opcode) {
		if (opcode == 1) {
			this.minimalAngleH = (short) packet.readUShort();
			this.maximalAngleH = (short) packet.readUShort();
			this.minimumAngleV = (short) packet.readUShort();
			this.maximalAngleV = (short) packet.readUShort();
			this.minimalAngleH = (short) (this.minimalAngleH << 3);
			this.minimumAngleV = (short) (this.minimumAngleV << 3);
			this.maximalAngleV = (short) (this.maximalAngleV << 3);
			this.maximalAngleH = (short) (this.maximalAngleH << 3);
		} else if (opcode == 2) {
			packet.readUByte();
		} else if (opcode == 3) {
			this.minimumSpeed = packet.readInt();
			this.maximumSpeed = packet.readInt();
		} else if (opcode == 4) {
			this.speedUpdateType = packet.readUByte();
			this.speedFallOffStep = packet.readByte();
		} else if (opcode == 5) {
			this.minimumSize = this.maximumSize = packet.readUShort() << 12 << 2;
		} else if (opcode == 6) {
			this.minimumStartColorRgb = packet.readInt();
			this.maximumStartColorRgb = packet.readInt();
		} else if (opcode == 7) {
			this.minimumLifetime = packet.readUShort();
			this.maximumLifetime = packet.readUShort();
		} else if (opcode == 8) {
			this.minimumParticleRate = packet.readUShort();
			this.maximumParticleRate = packet.readUShort();
		} else {
			@Pc(223) int count;
			@Pc(229) int index;
			if (opcode == 9) {
				count = packet.readUByte();
				this.generalEffectors = new int[count];
				for (index = 0; index < count; index++) {
					this.generalEffectors[index] = packet.readUShort();
				}
			} else if (opcode == 10) {
				count = packet.readUByte();
				this.localEffectors = new int[count];
				for (index = 0; index < count; index++) {
					this.localEffectors[index] = packet.readUShort();
				}
			} else if (opcode == 12) {
				this.lowestDisplayPlane = packet.readByte();
			} else if (opcode == 13) {
				this.highestDisplayPlane = packet.readByte();
			} else if (opcode == 14) {
				this.updatesPerCycle = packet.readUShort();
			} else if (opcode == 15) {
				this.materialId = packet.readUShort();
			} else if (opcode == 16) {
				this.activeFirst = packet.readUByte() == 1;
				this.emissionEndTime = packet.readUShort();
				this.lifetime = packet.readUShort();
				this.periodic = packet.readUByte() == 1;
			} else if (opcode == 17) {
				this.nonTexturedProducerId = packet.readUShort();
			} else if (opcode == 18) {
				this.faceColorRgb = packet.readInt();
			} else if (opcode == 19) {
				this.minimumSetting = packet.readUByte();
			} else if (opcode == 20) {
				this.colorFading = packet.readUByte();
			} else if (opcode == 21) {
				this.alphaFading = packet.readUByte();
			} else if (opcode == 22) {
				this.endSpeed = packet.readInt();
			} else if (opcode == 23) {
				this.speedChange = packet.readUByte();
			} else if (opcode == 24) {
				this.uniformColorVariance = false;
			} else if (opcode == 25) {
				count = packet.readUByte();
				this.effectiveVertexUids = new int[count];
				for (index = 0; index < count; index++) {
					this.effectiveVertexUids[index] = packet.readUShort();
				}
			} else if (opcode == 26) {
				this.unusedOpcode26 = false;
			} else if (opcode == 27) {
				this.endSize = packet.readUShort() << 12 << 2;
			} else if (opcode == 28) {
				this.sizeChange = packet.readUByte();
			} else if (opcode == 29) {
				packet.readShort();
			} else if (opcode == 30) {
				this.isTextured = true;
			} else if (opcode == 31) {
				this.minimumSize = packet.readUShort() << 12 << 2;
				this.maximumSize = packet.readUShort() << 12 << 2;
			} else if (opcode == 32) {
				this.adjustsLightIntensity = false;
			} else if (opcode == 33) {
				this.killOverlapping = true;
			} else if (opcode == 34) {
				this.killAboveSurface = false;
				return;
			}
		}
	}

	@OriginalMember(owner = "client!qb", name = "a", descriptor = "(ILclient!cea;)V")
	public void decode(@OriginalArg(1) Packet packet) {
		while (true) {
			@Pc(9) int opcode = packet.readUByte();
			if (opcode == 0) {
				return;
			}
			this.decode(packet, opcode);
		}
	}

	@OriginalMember(owner = "client!qb", name = "a", descriptor = "(I)V")
	public void postDecode() {
		this.minimumStartR = this.minimumStartColorRgb >> 16 & 0xFF;
		this.maximumStartR = this.maximumStartColorRgb >> 16 & 0xFF;
		if (this.lowestDisplayPlane > -2 || this.highestDisplayPlane > -2) {
			this.adjustsToScene = true;
		}
		this.maximumStartG = this.maximumStartColorRgb >> 8 & 0xFF;
		this.minimumStartG = this.minimumStartColorRgb >> 8 & 0xFF;
		this.redVariance = this.maximumStartR - this.minimumStartR;
		this.maximumStartB = this.maximumStartColorRgb & 0xFF;
		this.minimumStartB = this.minimumStartColorRgb & 0xFF;
		this.greenVariance = this.maximumStartG - this.minimumStartG;
		this.minimumStartA = this.minimumStartColorRgb >> 24 & 0xFF;
		this.blueVariance = this.maximumStartB - this.minimumStartB;
		this.maximumStartA = this.maximumStartColorRgb >> 24 & 0xFF;
		this.alphaVariance = this.maximumStartA - this.minimumStartA;
		if (this.endSize != -1) {
			this.startSizeChange = this.maximumLifetime * this.sizeChange / 100;
			if (this.startSizeChange == 0) {
				this.startSizeChange = 1;
			}
			this.sizeChangeStep = (this.endSize - (this.maximumSize - this.minimumSize) / 2 - this.minimumSize) / this.startSizeChange;
		}
		if (this.faceColorRgb != 0) {
			this.alphaFadeStart = this.maximumLifetime * this.alphaFading / 100;
			this.colorFadeStart = this.colorFading * this.maximumLifetime / 100;
			if (this.colorFadeStart == 0) {
				this.colorFadeStart = 1;
			}
			this.redFadeStep = ((this.faceColorRgb >> 16 & 0xFF) - this.minimumStartR - this.redVariance / 2 << 8) / this.colorFadeStart;
			this.blueFadeStep = ((this.faceColorRgb & 0xFF) - this.minimumStartB - this.blueVariance / 2 << 8) / this.colorFadeStart;
			if (this.alphaFadeStart == 0) {
				this.alphaFadeStart = 1;
			}
			this.greenFadeStep = ((this.faceColorRgb >> 8 & 0xFF) - this.greenVariance / 2 - this.minimumStartG << 8) / this.colorFadeStart;
			this.redFadeStep += this.redFadeStep > 0 ? -4 : 4;
			this.blueFadeStep += this.blueFadeStep <= 0 ? 4 : -4;
			this.alphaFadeStep = ((this.faceColorRgb >> 24 & 0xFF) - this.alphaVariance / 2 - this.minimumStartA << 8) / this.alphaFadeStart;
			this.greenFadeStep += this.greenFadeStep > 0 ? -4 : 4;
			this.alphaFadeStep += this.alphaFadeStep <= 0 ? 4 : -4;
		}
		if (this.endSpeed == -1) {
			return;
		}
		this.startSpeedChange = this.speedChange * this.maximumLifetime / 100;
		if (this.startSpeedChange == 0) {
			this.startSpeedChange = 1;
		}
		this.speedStep = (this.endSpeed - this.minimumSpeed - (this.maximumSpeed - this.minimumSpeed) / 2) / this.startSpeedChange;
	}
	@OriginalMember(owner = "client!gl", name = "j", descriptor = "Lclient!jr;")
	public static final LRUCache particleProducers = new LRUCache(64);
	@OriginalMember(owner = "client!g", name = "a", descriptor = "(II)Lclient!qb;")
	public static ParticleProducerType get(@OriginalArg(0) int id) {
		@Pc(12) ParticleProducerType particleEmitterType = (ParticleProducerType) ParticleProducerType.particleProducers.get((long) id);
		if (particleEmitterType != null) {
			return particleEmitterType;
		}
		@Pc(22) byte[] data = ParticleProducerType.aClass157_19.getFile(0, id);
		particleEmitterType = new ParticleProducerType();
		if (data != null) {
			particleEmitterType.decode(new Packet(data));
		}
		particleEmitterType.postDecode();
		ParticleProducerType.particleProducers.put(particleEmitterType, (long) id);
		return particleEmitterType;
	}

	@OriginalMember(owner = "client!ap", name = "a", descriptor = "(Z)V")
	public static void clearCache() {
		ParticleProducerType.particleProducers.reset();
	}
	@OriginalMember(owner = "client!av", name = "s", descriptor = "Lclient!in;")
	public static Js5ResourceProvider aClass157_19;

	@OriginalMember(owner = "client!nd", name = "a", descriptor = "(Lclient!in;B)V")
	public static void setParticleResourceProvider(@OriginalArg(0) Js5ResourceProvider particleResource) {
		ParticleProducerType.aClass157_19 = particleResource;
	}
}

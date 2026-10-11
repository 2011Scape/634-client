package com.jagex.game.runetek5.config.sequence;

import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.graphics.model.Model;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.sequence.frameset.FrameSet;

@OriginalClass("client!hg")
public final class SeqType {

	@OriginalMember(owner = "client!hg", name = "b", descriptor = "[I")
	public int[] soundMaxDelay;

	@OriginalMember(owner = "client!hg", name = "c", descriptor = "[I")
	public int[] frames;

	@OriginalMember(owner = "client!hg", name = "q", descriptor = "Lclient!lfa;")
	public SeqTypeList seqtypelist;

	@OriginalMember(owner = "client!hg", name = "s", descriptor = "[I")
	public int[] frameSoundVolume;

	@OriginalMember(owner = "client!hg", name = "z", descriptor = "[I")
	private int[] interfaceFrames;

	@OriginalMember(owner = "client!hg", name = "C", descriptor = "[Z")
	public boolean[] interLeaveOrder;

	@OriginalMember(owner = "client!hg", name = "D", descriptor = "[[I")
	public int[][] frameSounds;

	@OriginalMember(owner = "client!hg", name = "E", descriptor = "[I")
	public int[] soundMinDelay;

	@OriginalMember(owner = "client!hg", name = "G", descriptor = "[I")
	public int[] durations;

	@OriginalMember(owner = "client!hg", name = "I", descriptor = "I")
	public int id;

	@OriginalMember(owner = "client!hg", name = "h", descriptor = "Z")
	public boolean lights = false;

	@OriginalMember(owner = "client!hg", name = "d", descriptor = "I")
	public int replayInterval = -1;

	@OriginalMember(owner = "client!hg", name = "g", descriptor = "Z")
	public boolean tweened = false;

	@OriginalMember(owner = "client!hg", name = "k", descriptor = "I")
	public int moveType = -1;

	@OriginalMember(owner = "client!hg", name = "e", descriptor = "I")
	public int priority = 5;

	@OriginalMember(owner = "client!hg", name = "t", descriptor = "Z")
	public boolean shadowTweened = false;

	@OriginalMember(owner = "client!hg", name = "B", descriptor = "I")
	public int interruptType = -1;

	@OriginalMember(owner = "client!hg", name = "o", descriptor = "I")
	public int replacementShield = -1;

	@OriginalMember(owner = "client!hg", name = "n", descriptor = "I")
	public int loopType = 2;

	@OriginalMember(owner = "client!hg", name = "A", descriptor = "I")
	public int replacementWeapon = -1;

	@OriginalMember(owner = "client!hg", name = "j", descriptor = "I")
	public int replayCount = 99;

	@OriginalMember(owner = "client!hg", name = "H", descriptor = "Z")
	public boolean vorbis = false;

	@OriginalMember(owner = "client!hg", name = "a", descriptor = "(IIIZ)I")
	public int getFunctionMask(@OriginalArg(0) int nextFrameId, @OriginalArg(2) int currentFrameId, @OriginalArg(3) boolean includeInterfaceFrames) {
		@Pc(5) int functionMask = 0;
		@Pc(7) int nextFrame = 0;
		@Pc(12) int frame = this.frames[currentFrameId];
		@Pc(14) FrameSet animFrameset = null;
		@Pc(22) FrameSet animFrameset2 = this.seqtypelist.listFrameSet(frame >> 16);
		@Pc(26) int frameIndex = frame & 0xFFFF;
		if (animFrameset2 == null) {
			return 0;
		}
		if ((this.tweened || SeqType.forceTweening) && nextFrameId != -1 && nextFrameId < this.frames.length) {
			nextFrame = this.frames[nextFrameId];
			animFrameset = this.seqtypelist.listFrameSet(nextFrame >> 16);
			nextFrame &= 0xFFFF;
		}
		if (this.lights) {
			functionMask = 512;
		}
		if (animFrameset2.modifiesColors(frameIndex)) {
			functionMask |= 0x80;
		}
		if (animFrameset2.modifiesAlpha(frameIndex)) {
			functionMask |= 0x100;
		}
		if (animFrameset2.modifiesBillboards(frameIndex)) {
			functionMask |= 0x400;
		}
		if (animFrameset != null) {
			if (animFrameset.modifiesColors(nextFrame)) {
				functionMask |= 0x80;
			}
			if (animFrameset.modifiesAlpha(nextFrame)) {
				functionMask |= 0x100;
			}
			if (animFrameset.modifiesBillboards(nextFrame)) {
				functionMask |= 0x400;
			}
		}
		if (this.interfaceFrames != null && includeInterfaceFrames) {
			@Pc(148) int interfaceFrame;
			@Pc(161) FrameSet animFrameset3;
			if (currentFrameId < this.interfaceFrames.length) {
				interfaceFrame = this.interfaceFrames[currentFrameId];
				if (interfaceFrame != 65535) {
					animFrameset3 = this.seqtypelist.listFrameSet(interfaceFrame >> 16);
					interfaceFrame &= 0xFFFF;
					if (animFrameset3 != null) {
						if (animFrameset3.modifiesColors(interfaceFrame)) {
							functionMask |= 0x80;
						}
						if (animFrameset3.modifiesAlpha(interfaceFrame)) {
							functionMask |= 0x100;
						}
						if (animFrameset3.modifiesBillboards(interfaceFrame)) {
							functionMask |= 0x400;
						}
					}
				}
			}
			if ((this.tweened || SeqType.forceTweening) && nextFrameId != -1 && nextFrameId < this.interfaceFrames.length) {
				interfaceFrame = this.interfaceFrames[nextFrameId];
				if (interfaceFrame != 65535) {
					animFrameset3 = this.seqtypelist.listFrameSet(interfaceFrame >> 16);
					interfaceFrame &= 0xFFFF;
					if (animFrameset3 != null) {
						if (animFrameset3.modifiesColors(interfaceFrame)) {
							functionMask |= 0x80;
						}
						if (animFrameset3.modifiesAlpha(interfaceFrame)) {
							functionMask |= 0x100;
						}
						if (animFrameset3.modifiesBillboards(interfaceFrame)) {
							functionMask |= 0x400;
						}
					}
				}
			}
		}
		return functionMask | 0x20;
	}

	@OriginalMember(owner = "client!hg", name = "a", descriptor = "(Lclient!cea;II)V")
	private void decode(@OriginalArg(0) Packet packet, @OriginalArg(1) int opcode) {
		@Pc(14) int local14;
		@Pc(20) int i;
		@Pc(39) int local39;
		@Pc(54) int j;
		if (opcode == 1) {
			int frameCount = packet.readUShort();
			this.durations = new int[frameCount];
			for (i = 0; i < frameCount; i++) {
				this.durations[i] = packet.readUShort();
			}
			this.frames = new int[frameCount];
			for (int frameIndex = 0; frameIndex < frameCount; frameIndex++) {
				this.frames[frameIndex] = packet.readUShort();
			}
			for (j = 0; j < frameCount; j++) {
				this.frames[j] += packet.readUShort() << 16;
			}
		} else if (opcode == 2) {
			this.replayInterval = packet.readUShort();
		} else if (opcode == 3) {
			this.interLeaveOrder = new boolean[256];
			int orderCount = packet.readUByte();
			for (i = 0; i < orderCount; i++) {
				this.interLeaveOrder[packet.readUByte()] = true;
			}
		} else if (opcode == 5) {
			this.priority = packet.readUByte();
		} else if (opcode == 6) {
			this.replacementShield = packet.readUShort();
		} else if (opcode == 7) {
			this.replacementWeapon = packet.readUShort();
		} else if (opcode == 8) {
			this.replayCount = packet.readUByte();
		} else if (opcode == 9) {
			this.interruptType = packet.readUByte();
		} else if (opcode == 10) {
			this.moveType = packet.readUByte();
		} else if (opcode == 11) {
			this.loopType = packet.readUByte();
		} else if (opcode == 12) {
			int interfaceFrameCount = packet.readUByte();
			this.interfaceFrames = new int[interfaceFrameCount];
			for (i = 0; i < interfaceFrameCount; i++) {
				this.interfaceFrames[i] = packet.readUShort();
			}
			for (int frameIndex = 0; frameIndex < interfaceFrameCount; frameIndex++) {
				this.interfaceFrames[frameIndex] = (packet.readUShort() << 16) + this.interfaceFrames[frameIndex];
			}
		} else if (opcode == 13) {
			int frameSoundCount = packet.readUShort();
			this.frameSounds = new int[frameSoundCount][];
			for (i = 0; i < frameSoundCount; i++) {
				int soundCount = packet.readUByte();
				if (soundCount > 0) {
					this.frameSounds[i] = new int[soundCount];
					this.frameSounds[i][0] = packet.readUMedium();
					for (j = 1; j < soundCount; j++) {
						this.frameSounds[i][j] = packet.readUShort();
					}
				}
			}
		} else if (opcode == 14) {
			this.lights = true;
		} else if (opcode == 15) {
			this.tweened = true;
		} else if (opcode == 16) {
			this.shadowTweened = true;
		} else if (opcode == 18) {
			this.vorbis = true;
		} else if (opcode == 19) {
			if (this.frameSoundVolume == null) {
				this.frameSoundVolume = new int[this.frameSounds.length];
				for (int frameIndex = 0; frameIndex < this.frameSounds.length; frameIndex++) {
					this.frameSoundVolume[frameIndex] = 255;
				}
			}
			this.frameSoundVolume[packet.readUByte()] = packet.readUByte();
		} else if (opcode == 20) {
			if (this.soundMaxDelay == null || this.soundMinDelay == null) {
				this.soundMaxDelay = new int[this.frameSounds.length];
				this.soundMinDelay = new int[this.frameSounds.length];
				for (int frameIndex = 0; frameIndex < this.frameSounds.length; frameIndex++) {
					this.soundMaxDelay[frameIndex] = 256;
					this.soundMinDelay[frameIndex] = 256;
				}
			}
			int frameIndex = packet.readUByte();
			this.soundMaxDelay[frameIndex] = packet.readUShort();
			this.soundMinDelay[frameIndex] = packet.readUShort();
		}
	}

	@OriginalMember(owner = "client!hg", name = "a", descriptor = "(Lclient!ka;IIBIIII)Lclient!ka;")
	public Model animateRotatedModel(@OriginalArg(0) Model model, @OriginalArg(1) int rotation, @OriginalArg(2) int nextFrameId, @OriginalArg(3) byte copyType, @OriginalArg(4) int currentFrameId, @OriginalArg(5) int frameCycle, @OriginalArg(7) int functionMask) {
		@Pc(8) int frameDuration = this.durations[currentFrameId];
		@Pc(13) int frame = this.frames[currentFrameId];
		@Pc(21) FrameSet animFrameset = this.seqtypelist.listFrameSet(frame >> 16);
		@Pc(25) int frameIndex = frame & 0xFFFF;
		if (animFrameset == null) {
			return model.copy(copyType, functionMask, true);
		}
		@Pc(35) FrameSet animFrameset2 = null;
		if ((this.tweened || SeqType.forceTweening) && nextFrameId != -1 && nextFrameId < this.frames.length) {
			@Pc(57) int nextFrame = this.frames[nextFrameId];
			animFrameset2 = this.seqtypelist.listFrameSet(nextFrame >> 16);
			nextFrameId = nextFrame & 0xFFFF;
		}
		if (this.lights) {
			functionMask |= 0x200;
		}
		if (animFrameset.modifiesColors(frameIndex)) {
			functionMask |= 0x80;
		}
		if (animFrameset.modifiesAlpha(frameIndex)) {
			functionMask |= 0x100;
		}
		if (animFrameset.modifiesBillboards(frameIndex)) {
			functionMask |= 0x400;
		}
		if (animFrameset2 != null) {
			if (animFrameset2.modifiesColors(nextFrameId)) {
				functionMask |= 0x80;
			}
			if (animFrameset2.modifiesAlpha(nextFrameId)) {
				functionMask |= 0x100;
			}
			if (animFrameset2.modifiesBillboards(nextFrameId)) {
				functionMask |= 0x400;
			}
		}
		functionMask |= 0x20;
		@Pc(152) Model model2 = model.copy(copyType, functionMask, true);
		model2.animateModelFrame(frameIndex, frameDuration, animFrameset2, rotation, animFrameset, this.lights, nextFrameId, frameCycle - 1);
		return model2;
	}

	@OriginalMember(owner = "client!hg", name = "a", descriptor = "(Lclient!cea;Z)V")
	public void decode(@OriginalArg(0) Packet packet) {
		while (true) {
			@Pc(9) int opcode = packet.readUByte();
			if (opcode == 0) {
				return;
			}
			this.decode(packet, opcode);
		}
	}

	@OriginalMember(owner = "client!hg", name = "a", descriptor = "(ILclient!ka;IIII)Lclient!ka;")
	public Model animateModel(@OriginalArg(1) Model model, @OriginalArg(2) int nextFrameId, @OriginalArg(3) int frameCycle, @OriginalArg(4) int functionMask, @OriginalArg(5) int currentFrameId) {
		@Pc(8) int frameDuration = this.durations[currentFrameId];
		@Pc(13) int frame = this.frames[currentFrameId];
		@Pc(21) FrameSet animFrameset = this.seqtypelist.listFrameSet(frame >> 16);
		@Pc(25) int frameIndex = frame & 0xFFFF;
		if (animFrameset == null) {
			return model.copy((byte) 1, functionMask, true);
		}
		@Pc(35) FrameSet animFrameset2 = null;
		if ((this.tweened || SeqType.forceTweening) && nextFrameId != -1 && this.frames.length > nextFrameId) {
			@Pc(53) int nextFrame = this.frames[nextFrameId];
			animFrameset2 = this.seqtypelist.listFrameSet(nextFrame >> 16);
			nextFrameId = nextFrame & 0xFFFF;
		}
		@Pc(67) FrameSet animFrameset3 = null;
		@Pc(69) FrameSet animFrameset4 = null;
		@Pc(71) int interfaceFrame = 0;
		@Pc(73) int nextInterfaceFrame = 0;
		if (this.interfaceFrames != null) {
			if (this.interfaceFrames.length > currentFrameId) {
				interfaceFrame = this.interfaceFrames[currentFrameId];
				if (interfaceFrame != 65535) {
					animFrameset3 = this.seqtypelist.listFrameSet(interfaceFrame >> 16);
					interfaceFrame &= 0xFFFF;
				}
			}
			if ((this.tweened || SeqType.forceTweening) && nextFrameId != -1 && this.interfaceFrames.length > nextFrameId) {
				nextInterfaceFrame = this.interfaceFrames[nextFrameId];
				if (nextInterfaceFrame != 65535) {
					animFrameset4 = this.seqtypelist.listFrameSet(nextInterfaceFrame >> 16);
					nextInterfaceFrame &= 0xFFFF;
				}
			}
		}
		if (this.lights) {
			functionMask |= 0x200;
		}
		if (animFrameset.modifiesColors(frameIndex)) {
			functionMask |= 0x80;
		}
		if (animFrameset.modifiesAlpha(frameIndex)) {
			functionMask |= 0x100;
		}
		if (animFrameset.modifiesBillboards(frameIndex)) {
			functionMask |= 0x400;
		}
		if (animFrameset3 != null) {
			if (animFrameset3.modifiesColors(interfaceFrame)) {
				functionMask |= 0x80;
			}
			if (animFrameset3.modifiesAlpha(interfaceFrame)) {
				functionMask |= 0x100;
			}
			if (animFrameset3.modifiesBillboards(interfaceFrame)) {
				functionMask |= 0x400;
			}
		}
		if (animFrameset2 != null) {
			if (animFrameset2.modifiesColors(nextFrameId)) {
				functionMask |= 0x80;
			}
			if (animFrameset2.modifiesAlpha(nextFrameId)) {
				functionMask |= 0x100;
			}
			if (animFrameset2.modifiesBillboards(nextFrameId)) {
				functionMask |= 0x400;
			}
		}
		if (animFrameset4 != null) {
			if (animFrameset4.modifiesColors(nextInterfaceFrame)) {
				functionMask |= 0x80;
			}
			if (animFrameset4.modifiesAlpha(nextInterfaceFrame)) {
				functionMask |= 0x100;
			}
			if (animFrameset4.modifiesBillboards(nextInterfaceFrame)) {
				functionMask |= 0x400;
			}
		}
		functionMask |= 0x20;
		@Pc(272) Model model2 = model.copy((byte) 1, functionMask, true);
		model2.animateModelFrame(frameIndex, frameDuration, animFrameset2, 0, animFrameset, this.lights, nextFrameId, frameCycle - 1);
		if (animFrameset3 != null) {
			model2.animateModelFrame(interfaceFrame, frameDuration, animFrameset4, 0, animFrameset3, this.lights, nextInterfaceFrame, frameCycle - 1);
		}
		return model2;
	}

	@OriginalMember(owner = "client!hg", name = "a", descriptor = "(B)V")
	public void postDecode() {
		if (this.interruptType == -1) {
			if (this.interLeaveOrder == null) {
				this.interruptType = 0;
			} else {
				this.interruptType = 2;
			}
		}
		if (this.moveType != -1) {
			return;
		}
		if (this.interLeaveOrder == null) {
			this.moveType = 0;
		} else {
			this.moveType = 2;
		}
	}
	@OriginalMember(owner = "client!hu", name = "g", descriptor = "Z")
	public static boolean forceTweening = false;
}

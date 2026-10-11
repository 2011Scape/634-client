package com.jagex.game.runetek5.sound.player.area;

import com.jagex.game.runetek5.cache.node.Node;
import com.jagex.game.runetek5.config.npc.NPCType;
import com.jagex.game.runetek5.sound.jagtheora.AreaSoundStream;
import com.jagex.game.runetek5.cache.node.impl.VariableRateSoundPacket;
import com.jagex.game.runetek5.cache.node.VorbisSound;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.LocType;
import com.jagex.game.runetek5.entity.NPCEntity;
import com.jagex.game.runetek5.entity.PlayerEntity;
import com.jagex.game.runetek5.sound.jagtheora.PcmStreamMixer;
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable;
import org.openrs2.deob.annotation.OriginalArg;
import com.jagex.game.runetek5.core.datastruct.key.Deque;
import com.jagex.game.runetek5.SoundManager;
import com.jagex.game.runetek5.config.bas.BasType;
import com.jagex.game.runetek5.vardomain.PlayerVarDomain;

@OriginalClass("client!hf")
public final class AmbientSound extends Node {

	@OriginalMember(owner = "client!hf", name = "o", descriptor = "I")
	public int soundMaxStep;

	@OriginalMember(owner = "client!hf", name = "q", descriptor = "I")
	public int stepsUntilNextSound;

	@OriginalMember(owner = "client!hf", name = "t", descriptor = "Lclient!via;")
	public AreaSoundStream ambientSoundGroupStream;

	@OriginalMember(owner = "client!hf", name = "u", descriptor = "Z")
	public boolean vorbis;

	@OriginalMember(owner = "client!hf", name = "w", descriptor = "Lclient!via;")
	public AreaSoundStream soundEffect;

	@OriginalMember(owner = "client!hf", name = "x", descriptor = "I")
	public int ambientSoundVolume;

	@OriginalMember(owner = "client!hf", name = "y", descriptor = "I")
	public int minX;

	@OriginalMember(owner = "client!hf", name = "z", descriptor = "Lclient!ke;")
	public PlayerEntity playerEntity;

	@OriginalMember(owner = "client!hf", name = "A", descriptor = "I")
	public int soundMinStep;

	@OriginalMember(owner = "client!hf", name = "B", descriptor = "I")
	public int minDelay;

	@OriginalMember(owner = "client!hf", name = "D", descriptor = "Lclient!hp;")
	public VorbisSound ambientSoundInstrument;

	@OriginalMember(owner = "client!hf", name = "E", descriptor = "Z")
	public boolean hasTransformation;

	@OriginalMember(owner = "client!hf", name = "F", descriptor = "I")
	public int soundId;

	@OriginalMember(owner = "client!hf", name = "G", descriptor = "Lclient!ob;")
	public VariableRateSoundPacket soundEffectBuffer;

	@OriginalMember(owner = "client!hf", name = "H", descriptor = "I")
	public int plane;

	@OriginalMember(owner = "client!hf", name = "I", descriptor = "Lclient!hp;")
	public VorbisSound instrumentEffect;

	@OriginalMember(owner = "client!hf", name = "J", descriptor = "I")
	public int absEndX;

	@OriginalMember(owner = "client!hf", name = "L", descriptor = "Lclient!ff;")
	public NPCEntity npcEntity;

	@OriginalMember(owner = "client!hf", name = "M", descriptor = "Lclient!ob;")
	public VariableRateSoundPacket soundGroupBuffer;

	@OriginalMember(owner = "client!hf", name = "N", descriptor = "I")
	public int level;

	@OriginalMember(owner = "client!hf", name = "O", descriptor = "I")
	public int ambientSoundMinHearDistance;

	@OriginalMember(owner = "client!hf", name = "P", descriptor = "Lclient!iv;")
	public LocType emittingLoc;

	@OriginalMember(owner = "client!hf", name = "Q", descriptor = "I")
	public int ambientSoundMaxDelay;

	@OriginalMember(owner = "client!hf", name = "R", descriptor = "[I")
	public int[] ambientSoundGroupIds;

	@OriginalMember(owner = "client!hf", name = "S", descriptor = "I")
	public int absStartX;

	@OriginalMember(owner = "client!hf", name = "T", descriptor = "Z")
	public boolean random;

	@OriginalMember(owner = "client!hf", name = "v", descriptor = "I")
	public int movementSpeed = 0;

	@OriginalMember(owner = "client!hf", name = "a", descriptor = "(B)V")
	public void update() {
		@Pc(8) int sound = this.soundId;
		@Pc(15) boolean vorbis = this.vorbis;
		if (this.emittingLoc != null) {
			@Pc(130) LocType locType = this.emittingLoc.getMultiLoc(PlayerVarDomain.playerVarProvider);
			if (locType == null) {
				this.minDelay = 256;
				this.vorbis = false;
				this.random = false;
				this.ambientSoundGroupIds = null;
				this.soundMaxStep = 0;
				this.ambientSoundVolume = 0;
				this.ambientSoundMaxDelay = 256;
				this.soundMinStep = 0;
				this.soundId = -1;
				this.ambientSoundMinHearDistance = 0;
			} else {
				this.minDelay = locType.ambientSoundMinDelay;
				this.soundId = locType.soundId;
				this.ambientSoundMaxDelay = locType.ambientSoundMaxDelay;
				this.ambientSoundMinHearDistance = locType.soundRadius << 9;
				this.vorbis = locType.instrumentSoundEffect;
				this.soundMinStep = locType.soundMinInterval;
				this.ambientSoundVolume = locType.soundVolume;
				this.soundMaxStep = locType.soundMaxInterval;
				this.random = locType.instrumentAmbientSound;
				this.ambientSoundGroupIds = locType.soundGroupIds;
			}
		} else if (this.npcEntity != null) {
			@Pc(60) int npcSound = AmbientSound.getSoundEffectId(this.npcEntity);
			if (npcSound != sound) {
				this.soundId = npcSound;
				@Pc(70) NPCType npcType = this.npcEntity.npcType;
				if (npcType.alternativeIds != null) {
					npcType = npcType.getMultiNpc(PlayerVarDomain.playerVarProvider);
				}
				if (npcType == null) {
					this.ambientSoundVolume = this.ambientSoundMinHearDistance = 0;
					this.minDelay = 256;
					this.ambientSoundMaxDelay = 256;
					this.vorbis = this.npcEntity.npcType.instrumentSoundEffect;
				} else {
					this.ambientSoundMinHearDistance = npcType.ambientSoundMinHearDistance << 9;
					this.minDelay = npcType.ambientSoundMinDelay;
					this.ambientSoundVolume = npcType.ambientSoundVolume;
					this.vorbis = npcType.instrumentSoundEffect;
					this.ambientSoundMaxDelay = npcType.ambientSoundMaxDelay;
				}
			}
		} else if (this.playerEntity != null) {
			this.soundId = SoundEffectPlayer.getSoundEffectId(this.playerEntity);
			this.ambientSoundMinHearDistance = this.playerEntity.isNpc << 9;
			this.ambientSoundVolume = this.playerEntity.ambientSoundVolume;
			this.minDelay = 256;
			this.ambientSoundMaxDelay = 256;
			this.vorbis = this.playerEntity.vorbis;
		}
		if (this.soundId == sound && vorbis == this.vorbis) {
			return;
		}
		if (this.soundEffect == null) {
			return;
		}
		AmbientSound.gameSounds.remove(this.soundEffect);
		this.soundEffectBuffer = null;
		this.soundEffect = null;
		this.instrumentEffect = null;
	}
	@OriginalMember(owner = "client!vv", name = "o", descriptor = "Lclient!sq;")
	public static PcmStreamMixer gameSounds;
	@OriginalMember(owner = "client!qh", name = "M", descriptor = "Lclient!eq;")
	public static final IterableHashTable PLAYER_AMBIENT_SOUNDS = new IterableHashTable(16);
	@OriginalMember(owner = "client!wc", name = "a", descriptor = "(Lclient!ke;I)V")
	public static void unlinkPlayerSound(@OriginalArg(0) PlayerEntity playerEntity) {
		@Pc(16) AmbientSound positionedSound = (AmbientSound) AmbientSound.PLAYER_AMBIENT_SOUNDS.get((long) playerEntity.playerIndex);
		if (positionedSound == null) {
			return;
		}
		if (positionedSound.soundEffect != null) {
			AmbientSound.gameSounds.remove(positionedSound.soundEffect);
			positionedSound.soundEffect = null;
		}
		positionedSound.unlink();
	}
	@OriginalMember(owner = "client!rw", name = "k", descriptor = "Lclient!uh;")
	public static final Deque aClass341_59 = new Deque();
	@OriginalMember(owner = "client!lia", name = "a", descriptor = "(ZLclient!ke;)V")
	public static void updatePlayerAmbientSound(@OriginalArg(1) PlayerEntity playerEntity) {
		@Pc(11) AmbientSound positionedSound = (AmbientSound) AmbientSound.PLAYER_AMBIENT_SOUNDS.get((long) playerEntity.playerIndex);
		if (positionedSound == null) {
			AmbientSound.createAmbientSoundEffect(null, playerEntity.level, playerEntity.movementBaseY[0], playerEntity.movementBaseX[0], playerEntity, null, 0);
		} else {
			positionedSound.update();
		}
	}

	@OriginalMember(owner = "client!gl", name = "a", descriptor = "(ZLclient!ff;)V")
	public static void unlinkEntitySound(@OriginalArg(1) NPCEntity npcEntity) {
		for (@Pc(15) AmbientSound positionedSound = (AmbientSound) AmbientSound.aClass341_25.first(); positionedSound != null; positionedSound = (AmbientSound) AmbientSound.aClass341_25.next()) {
			if (positionedSound.npcEntity == npcEntity) {
				if (positionedSound.soundEffect != null) {
					AmbientSound.gameSounds.remove(positionedSound.soundEffect);
					positionedSound.soundEffect = null;
				}
				positionedSound.unlink();
				return;
			}
		}
	}

	@OriginalMember(owner = "client!ds", name = "a", descriptor = "(IIIII)V")
	public static void updateAllAmbientSounds(@OriginalArg(1) int level, @OriginalArg(2) int step, @OriginalArg(3) int absZ, @OriginalArg(4) int absX) {
		for (@Pc(10) AmbientSound positionedSound = (AmbientSound) AmbientSound.aClass341_59.first(); positionedSound != null; positionedSound = (AmbientSound) AmbientSound.aClass341_59.next()) {
			SoundManager.updateAmbientSound(step, absX, level, absZ, positionedSound);
		}
		for (@Pc(31) AmbientSound positionedSound2 = (AmbientSound) AmbientSound.aClass341_25.first(); positionedSound2 != null; positionedSound2 = (AmbientSound) AmbientSound.aClass341_25.next()) {
			@Pc(35) byte movementSpeed = 1;
			@Pc(40) BasType basType = positionedSound2.npcEntity.getBasType();
			if (positionedSound2.npcEntity.movingSequence.sequenceId == -1 || positionedSound2.npcEntity.movingSequence.hasType) {
				movementSpeed = 0;
			} else if (basType.runningSequence == positionedSound2.npcEntity.movingSequence.sequenceId || positionedSound2.npcEntity.movingSequence.sequenceId == basType.runDir3 || basType.runDir1 == positionedSound2.npcEntity.movingSequence.sequenceId || basType.runDir2 == positionedSound2.npcEntity.movingSequence.sequenceId) {
				movementSpeed = 2;
			} else if (basType.teleportSequence == positionedSound2.npcEntity.movingSequence.sequenceId || basType.teleDir3 == positionedSound2.npcEntity.movingSequence.sequenceId || positionedSound2.npcEntity.movingSequence.sequenceId == basType.teleDir1 || positionedSound2.npcEntity.movingSequence.sequenceId == basType.teleDir2) {
				movementSpeed = 3;
			}
			if (movementSpeed != positionedSound2.movementSpeed) {
				@Pc(137) int soundId = AmbientSound.getSoundEffectId(positionedSound2.npcEntity);
				@Pc(141) NPCType npcType = positionedSound2.npcEntity.npcType;
				if (npcType.alternativeIds != null) {
					npcType = npcType.getMultiNpc(PlayerVarDomain.playerVarProvider);
				}
				if (npcType == null || soundId == -1) {
					positionedSound2.movementSpeed = movementSpeed;
					positionedSound2.vorbis = false;
					positionedSound2.soundId = -1;
				} else if (soundId == positionedSound2.soundId && npcType.instrumentSoundEffect == positionedSound2.vorbis) {
					positionedSound2.ambientSoundVolume = npcType.ambientSoundVolume;
					positionedSound2.movementSpeed = movementSpeed;
				} else {
					@Pc(182) boolean replaceSound = false;
					if (positionedSound2.soundEffect == null) {
						replaceSound = true;
					} else {
						positionedSound2.ambientSoundVolume -= 512;
						if (positionedSound2.ambientSoundVolume <= 0) {
							AmbientSound.gameSounds.remove(positionedSound2.soundEffect);
							replaceSound = true;
							positionedSound2.soundEffect = null;
						}
					}
					if (replaceSound) {
						positionedSound2.vorbis = npcType.instrumentSoundEffect;
						positionedSound2.soundId = soundId;
						positionedSound2.soundEffectBuffer = null;
						positionedSound2.instrumentEffect = null;
						positionedSound2.movementSpeed = movementSpeed;
						positionedSound2.ambientSoundVolume = npcType.ambientSoundVolume;
					}
				}
			}
			positionedSound2.minX = positionedSound2.npcEntity.x;
			positionedSound2.absStartX = positionedSound2.npcEntity.x + (positionedSound2.npcEntity.getSize() << 8);
			positionedSound2.absEndX = positionedSound2.npcEntity.z;
			positionedSound2.plane = positionedSound2.npcEntity.z + (positionedSound2.npcEntity.getSize() << 8);
			SoundManager.updateAmbientSound(step, absX, level, absZ, positionedSound2);
		}
		for (@Pc(297) AmbientSound positionedSound3 = (AmbientSound) AmbientSound.PLAYER_AMBIENT_SOUNDS.first(); positionedSound3 != null; positionedSound3 = (AmbientSound) AmbientSound.PLAYER_AMBIENT_SOUNDS.next()) {
			@Pc(308) byte movementSpeed = 1;
			@Pc(313) BasType basType2 = positionedSound3.playerEntity.getBasType();
			if (positionedSound3.playerEntity.movingSequence.sequenceId == -1 || positionedSound3.playerEntity.movingSequence.hasType) {
				movementSpeed = 0;
			} else if (positionedSound3.playerEntity.movingSequence.sequenceId == basType2.runningSequence || basType2.runDir3 == positionedSound3.playerEntity.movingSequence.sequenceId || basType2.runDir1 == positionedSound3.playerEntity.movingSequence.sequenceId || basType2.runDir2 == positionedSound3.playerEntity.movingSequence.sequenceId) {
				movementSpeed = 2;
			} else if (basType2.teleportSequence == positionedSound3.playerEntity.movingSequence.sequenceId || positionedSound3.playerEntity.movingSequence.sequenceId == basType2.teleDir3 || basType2.teleDir1 == positionedSound3.playerEntity.movingSequence.sequenceId || positionedSound3.playerEntity.movingSequence.sequenceId == basType2.teleDir2) {
				movementSpeed = 3;
			}
			if (positionedSound3.movementSpeed != movementSpeed) {
				@Pc(409) int soundId = SoundEffectPlayer.getSoundEffectId(positionedSound3.playerEntity);
				if (soundId == positionedSound3.soundId && positionedSound3.playerEntity.vorbis == positionedSound3.vorbis) {
					positionedSound3.ambientSoundVolume = positionedSound3.playerEntity.ambientSoundVolume;
					positionedSound3.movementSpeed = movementSpeed;
				} else {
					@Pc(431) boolean replaceSound = false;
					if (positionedSound3.soundEffect == null) {
						replaceSound = true;
					} else {
						positionedSound3.ambientSoundVolume -= 512;
						if (positionedSound3.ambientSoundVolume <= 0) {
							AmbientSound.gameSounds.remove(positionedSound3.soundEffect);
							positionedSound3.soundEffect = null;
							replaceSound = true;
						}
					}
					if (replaceSound) {
						positionedSound3.vorbis = positionedSound3.playerEntity.vorbis;
						positionedSound3.ambientSoundVolume = positionedSound3.playerEntity.ambientSoundVolume;
						positionedSound3.instrumentEffect = null;
						positionedSound3.soundId = soundId;
						positionedSound3.soundEffectBuffer = null;
						positionedSound3.movementSpeed = movementSpeed;
					}
				}
			}
			positionedSound3.minX = positionedSound3.playerEntity.x;
			positionedSound3.absStartX = positionedSound3.playerEntity.x + (positionedSound3.playerEntity.getSize() << 8);
			positionedSound3.absEndX = positionedSound3.playerEntity.z;
			positionedSound3.plane = positionedSound3.playerEntity.z + (positionedSound3.playerEntity.getSize() << 8);
			SoundManager.updateAmbientSound(step, absX, level, absZ, positionedSound3);
		}
	}

	@OriginalMember(owner = "client!client", name = "wb", descriptor = "Lclient!uh;")
	public static final Deque aClass341_25 = new Deque();
	@OriginalMember(owner = "client!ad", name = "a", descriptor = "(ILclient!ff;IIILclient!ke;Lclient!iv;I)V")
	public static void createAmbientSoundEffect(@OriginalArg(1) NPCEntity npcEntity, @OriginalArg(2) int level, @OriginalArg(3) int z, @OriginalArg(4) int x, @OriginalArg(5) PlayerEntity playerEntity, @OriginalArg(6) LocType locType, @OriginalArg(7) int rotation) {
		@Pc(12) AmbientSound positionedSound = new AmbientSound();
		positionedSound.minX = x << 9;
		positionedSound.level = level;
		positionedSound.absEndX = z << 9;
		if (locType != null) {
			positionedSound.emittingLoc = locType;
			@Pc(163) int sizeX = locType.sizeX;
			@Pc(166) int sizeY = locType.sizeY;
			if (rotation == 1 || rotation == 3) {
				sizeX = locType.sizeY;
				sizeY = locType.sizeX;
			}
			positionedSound.plane = sizeY + z << 9;
			positionedSound.soundMinStep = locType.soundMinInterval;
			positionedSound.soundId = locType.soundId;
			positionedSound.minDelay = locType.ambientSoundMinDelay;
			positionedSound.ambientSoundGroupIds = locType.soundGroupIds;
			positionedSound.ambientSoundMaxDelay = locType.ambientSoundMaxDelay;
			positionedSound.random = locType.instrumentAmbientSound;
			positionedSound.ambientSoundMinHearDistance = locType.soundRadius << 9;
			positionedSound.soundMaxStep = locType.soundMaxInterval;
			positionedSound.absStartX = sizeX + x << 9;
			positionedSound.ambientSoundVolume = locType.soundVolume;
			positionedSound.vorbis = locType.instrumentSoundEffect;
			if (locType.alternativeIds != null) {
				positionedSound.hasTransformation = true;
				positionedSound.update();
			}
			if (positionedSound.ambientSoundGroupIds != null) {
				positionedSound.stepsUntilNextSound = positionedSound.soundMinStep + (int) ((double) (positionedSound.soundMaxStep - positionedSound.soundMinStep) * Math.random());
			}
			AmbientSound.aClass341_59.addLast(positionedSound);
			return;
		}
		if (npcEntity != null) {
			positionedSound.npcEntity = npcEntity;
			@Pc(35) NPCType npcType = npcEntity.npcType;
			if (npcType.alternativeIds != null) {
				positionedSound.hasTransformation = true;
				npcType = npcType.getMultiNpc(PlayerVarDomain.playerVarProvider);
			}
			if (npcType != null) {
				positionedSound.absStartX = x + npcType.size << 9;
				positionedSound.plane = z + npcType.size << 9;
				positionedSound.soundId = AmbientSound.getSoundEffectId(npcEntity);
				positionedSound.ambientSoundMaxDelay = npcType.ambientSoundMaxDelay;
				positionedSound.ambientSoundMinHearDistance = npcType.ambientSoundMinHearDistance << 9;
				positionedSound.ambientSoundVolume = npcType.ambientSoundVolume;
				positionedSound.minDelay = npcType.ambientSoundMinDelay;
				positionedSound.vorbis = npcType.instrumentSoundEffect;
			}
			AmbientSound.aClass341_25.addLast(positionedSound);
			return;
		}
		if (playerEntity == null) {
			return;
		}
		positionedSound.playerEntity = playerEntity;
		positionedSound.absStartX = playerEntity.getSize() + x << 9;
		positionedSound.plane = playerEntity.getSize() + z << 9;
		positionedSound.soundId = SoundEffectPlayer.getSoundEffectId(playerEntity);
		positionedSound.minDelay = 256;
		positionedSound.ambientSoundMinHearDistance = playerEntity.isNpc << 9;
		positionedSound.ambientSoundVolume = playerEntity.ambientSoundVolume;
		positionedSound.ambientSoundMaxDelay = 256;
		positionedSound.vorbis = playerEntity.vorbis;
		AmbientSound.PLAYER_AMBIENT_SOUNDS.put((long) playerEntity.playerIndex, positionedSound);
		return;
	}

	@OriginalMember(owner = "client!ci", name = "a", descriptor = "(Lclient!iv;IIII)V")
	public static void unlinkLocSound(@OriginalArg(0) LocType locType, @OriginalArg(1) int x, @OriginalArg(2) int z, @OriginalArg(3) int level) {
		for (@Pc(15) AmbientSound positionedSound = (AmbientSound) AmbientSound.aClass341_59.first(); positionedSound != null; positionedSound = (AmbientSound) AmbientSound.aClass341_59.next()) {
			if (level == positionedSound.level && x << 9 == positionedSound.minX && z << 9 == positionedSound.absEndX && locType.id == positionedSound.emittingLoc.id) {
				if (positionedSound.soundEffect != null) {
					AmbientSound.gameSounds.remove(positionedSound.soundEffect);
					positionedSound.soundEffect = null;
				}
				if (positionedSound.ambientSoundGroupStream != null) {
					AmbientSound.gameSounds.remove(positionedSound.ambientSoundGroupStream);
					positionedSound.ambientSoundGroupStream = null;
				}
				positionedSound.unlink();
				return;
			}
		}
	}
	@OriginalMember(owner = "client!haa", name = "a", descriptor = "(BZ)V")
	public static void unlinkSounds(@OriginalArg(1) boolean clearEntitySounds) {
		for (@Pc(10) AmbientSound positionedSound = (AmbientSound) AmbientSound.aClass341_59.first(); positionedSound != null; positionedSound = (AmbientSound) AmbientSound.aClass341_59.next()) {
			if (positionedSound.soundEffect != null) {
				AmbientSound.gameSounds.remove(positionedSound.soundEffect);
				positionedSound.soundEffect = null;
			}
			if (positionedSound.ambientSoundGroupStream != null) {
				AmbientSound.gameSounds.remove(positionedSound.ambientSoundGroupStream);
				positionedSound.ambientSoundGroupStream = null;
			}
			positionedSound.unlink();
		}
		if (!clearEntitySounds) {
			return;
		}
		for (@Pc(53) AmbientSound positionedSound2 = (AmbientSound) AmbientSound.aClass341_25.first(); positionedSound2 != null; positionedSound2 = (AmbientSound) AmbientSound.aClass341_25.next()) {
			if (positionedSound2.soundEffect != null) {
				AmbientSound.gameSounds.remove(positionedSound2.soundEffect);
				positionedSound2.soundEffect = null;
			}
			positionedSound2.unlink();
		}
		for (@Pc(80) AmbientSound positionedSound3 = (AmbientSound) AmbientSound.PLAYER_AMBIENT_SOUNDS.first(); positionedSound3 != null; positionedSound3 = (AmbientSound) AmbientSound.PLAYER_AMBIENT_SOUNDS.next()) {
			if (positionedSound3.soundEffect != null) {
				AmbientSound.gameSounds.remove(positionedSound3.soundEffect);
				positionedSound3.soundEffect = null;
			}
			positionedSound3.unlink();
		}
	}

	@OriginalMember(owner = "client!iaa", name = "b", descriptor = "(B)V")
	public static void updateTransformingSounds() {
		for (@Pc(15) AmbientSound positionedSound = (AmbientSound) AmbientSound.aClass341_59.first(); positionedSound != null; positionedSound = (AmbientSound) AmbientSound.aClass341_59.next()) {
			if (positionedSound.hasTransformation) {
				positionedSound.update();
			}
		}
		for (@Pc(35) AmbientSound positionedSound2 = (AmbientSound) AmbientSound.aClass341_25.first(); positionedSound2 != null; positionedSound2 = (AmbientSound) AmbientSound.aClass341_25.next()) {
			if (positionedSound2.hasTransformation) {
				positionedSound2.update();
			}
		}
	}

	@OriginalMember(owner = "client!gr", name = "a", descriptor = "(Lclient!ff;I)I")
	public static int getSoundEffectId(@OriginalArg(0) NPCEntity npcEntity) {
		@Pc(8) NPCType npcType = npcEntity.npcType;
		if (npcType.alternativeIds != null) {
			npcType = npcType.getMultiNpc(PlayerVarDomain.playerVarProvider);
			if (npcType == null) {
				return -1;
			}
		}
		@Pc(23) int soundEffectId = npcType.idleSoundEffect;
		@Pc(32) BasType basType = npcEntity.getBasType();
		if (npcEntity.movingSequence.sequenceId == -1 || npcEntity.movingSequence.hasType) {
			soundEffectId = npcType.walkingSoundEffect;
		} else if (npcEntity.movingSequence.sequenceId == basType.runningSequence || basType.runDir3 == npcEntity.movingSequence.sequenceId || basType.runDir1 == npcEntity.movingSequence.sequenceId || npcEntity.movingSequence.sequenceId == basType.runDir2) {
			soundEffectId = npcType.runningSoundEffect;
		} else if (basType.teleportSequence == npcEntity.movingSequence.sequenceId || npcEntity.movingSequence.sequenceId == basType.teleDir3 || npcEntity.movingSequence.sequenceId == basType.teleDir1 || basType.teleDir2 == npcEntity.movingSequence.sequenceId) {
			soundEffectId = npcType.teleportSoundEffect;
		}
		return soundEffectId;
	}
}

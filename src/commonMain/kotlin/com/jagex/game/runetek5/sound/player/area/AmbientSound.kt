package com.jagex.game.runetek5.sound.player.area

import com.jagex.game.runetek5.cache.node.Node
import com.jagex.game.runetek5.config.npc.NPCType
import com.jagex.game.runetek5.sound.jagtheora.AreaSoundStream
import com.jagex.game.runetek5.cache.node.impl.VariableRateSoundPacket
import com.jagex.game.runetek5.cache.node.VorbisSound
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.LocType
import com.jagex.game.runetek5.entity.NPCEntity
import com.jagex.game.runetek5.entity.PlayerEntity
import com.jagex.game.runetek5.sound.jagtheora.PcmStreamMixer
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable
import org.openrs2.deob.annotation.OriginalArg
import com.jagex.game.runetek5.core.datastruct.key.Deque
import com.jagex.game.runetek5.SoundManager
import com.jagex.game.runetek5.config.bas.BasType
import com.jagex.game.runetek5.vardomain.PlayerVarDomain
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@OriginalClass("client!hf")
class AmbientSound : Node() {

	@OriginalMember(owner = "client!hf", name = "o", descriptor = "I")
	@JvmField
	var soundMaxStep: Int = 0

	@OriginalMember(owner = "client!hf", name = "q", descriptor = "I")
	@JvmField
	var stepsUntilNextSound: Int = 0

	@OriginalMember(owner = "client!hf", name = "t", descriptor = "Lclient!via;")
	@JvmField
	var ambientSoundGroupStream: AreaSoundStream? = null

	@OriginalMember(owner = "client!hf", name = "u", descriptor = "Z")
	@JvmField
	var vorbis: Boolean = false

	@OriginalMember(owner = "client!hf", name = "w", descriptor = "Lclient!via;")
	@JvmField
	var soundEffect: AreaSoundStream? = null

	@OriginalMember(owner = "client!hf", name = "x", descriptor = "I")
	@JvmField
	var ambientSoundVolume: Int = 0

	@OriginalMember(owner = "client!hf", name = "y", descriptor = "I")
	@JvmField
	var minX: Int = 0

	@OriginalMember(owner = "client!hf", name = "z", descriptor = "Lclient!ke;")
	@JvmField
	var playerEntity: PlayerEntity? = null

	@OriginalMember(owner = "client!hf", name = "A", descriptor = "I")
	@JvmField
	var soundMinStep: Int = 0

	@OriginalMember(owner = "client!hf", name = "B", descriptor = "I")
	@JvmField
	var minDelay: Int = 0

	@OriginalMember(owner = "client!hf", name = "D", descriptor = "Lclient!hp;")
	@JvmField
	var ambientSoundInstrument: VorbisSound? = null

	@OriginalMember(owner = "client!hf", name = "E", descriptor = "Z")
	@JvmField
	var hasTransformation: Boolean = false

	@OriginalMember(owner = "client!hf", name = "F", descriptor = "I")
	@JvmField
	var soundId: Int = 0

	@OriginalMember(owner = "client!hf", name = "G", descriptor = "Lclient!ob;")
	@JvmField
	var soundEffectBuffer: VariableRateSoundPacket? = null

	@OriginalMember(owner = "client!hf", name = "H", descriptor = "I")
	@JvmField
	var plane: Int = 0

	@OriginalMember(owner = "client!hf", name = "I", descriptor = "Lclient!hp;")
	@JvmField
	var instrumentEffect: VorbisSound? = null

	@OriginalMember(owner = "client!hf", name = "J", descriptor = "I")
	@JvmField
	var absEndX: Int = 0

	@OriginalMember(owner = "client!hf", name = "L", descriptor = "Lclient!ff;")
	@JvmField
	var npcEntity: NPCEntity? = null

	@OriginalMember(owner = "client!hf", name = "M", descriptor = "Lclient!ob;")
	@JvmField
	var soundGroupBuffer: VariableRateSoundPacket? = null

	@OriginalMember(owner = "client!hf", name = "N", descriptor = "I")
	@JvmField
	var level: Int = 0

	@OriginalMember(owner = "client!hf", name = "O", descriptor = "I")
	@JvmField
	var ambientSoundMinHearDistance: Int = 0

	@OriginalMember(owner = "client!hf", name = "P", descriptor = "Lclient!iv;")
	@JvmField
	var emittingLoc: LocType? = null

	@OriginalMember(owner = "client!hf", name = "Q", descriptor = "I")
	@JvmField
	var ambientSoundMaxDelay: Int = 0

	@OriginalMember(owner = "client!hf", name = "R", descriptor = "[I")
	@JvmField
	var ambientSoundGroupIds: IntArray? = null

	@OriginalMember(owner = "client!hf", name = "S", descriptor = "I")
	@JvmField
	var absStartX: Int = 0

	@OriginalMember(owner = "client!hf", name = "T", descriptor = "Z")
	@JvmField
	var random: Boolean = false

	@OriginalMember(owner = "client!hf", name = "v", descriptor = "I")
	@JvmField
	var movementSpeed: Int = 0

	@OriginalMember(owner = "client!hf", name = "a", descriptor = "(B)V")
	fun update() {
		@Pc(8) val sound: Int = this.soundId
		@Pc(15) val vorbis: Boolean = this.vorbis
		if (this.emittingLoc != null) {
			@Pc(130) val locType: LocType? = this.emittingLoc!!.getMultiLoc(PlayerVarDomain.playerVarProvider)
			if (locType == null) {
				this.minDelay = 256
				this.vorbis = false
				this.random = false
				this.ambientSoundGroupIds = null
				this.soundMaxStep = 0
				this.ambientSoundVolume = 0
				this.ambientSoundMaxDelay = 256
				this.soundMinStep = 0
				this.soundId = -1
				this.ambientSoundMinHearDistance = 0
			} else {
				this.minDelay = locType.ambientSoundMinDelay
				this.soundId = locType.soundId
				this.ambientSoundMaxDelay = locType.ambientSoundMaxDelay
				this.ambientSoundMinHearDistance = locType.soundRadius shl 9
				this.vorbis = locType.instrumentSoundEffect
				this.soundMinStep = locType.soundMinInterval
				this.ambientSoundVolume = locType.soundVolume
				this.soundMaxStep = locType.soundMaxInterval
				this.random = locType.instrumentAmbientSound
				this.ambientSoundGroupIds = locType.soundGroupIds
			}
		} else if (this.npcEntity != null) {
			@Pc(60) val npcSound: Int = AmbientSound.getSoundEffectId(this.npcEntity)
			if (npcSound != sound) {
				this.soundId = npcSound
				@Pc(70) var npcType: NPCType? = this.npcEntity!!.npcType
				if (npcType!!.alternativeIds != null) {
					npcType = npcType.getMultiNpc(PlayerVarDomain.playerVarProvider)
				}
				if (npcType == null) {
					this.ambientSoundMinHearDistance = 0
					this.ambientSoundVolume = 0
					this.minDelay = 256
					this.ambientSoundMaxDelay = 256
					this.vorbis = this.npcEntity!!.npcType!!.instrumentSoundEffect
				} else {
					this.ambientSoundMinHearDistance = npcType.ambientSoundMinHearDistance shl 9
					this.minDelay = npcType.ambientSoundMinDelay
					this.ambientSoundVolume = npcType.ambientSoundVolume
					this.vorbis = npcType.instrumentSoundEffect
					this.ambientSoundMaxDelay = npcType.ambientSoundMaxDelay
				}
			}
		} else if (this.playerEntity != null) {
			this.soundId = SoundEffectPlayer.getSoundEffectId(this.playerEntity)
			this.ambientSoundMinHearDistance = this.playerEntity!!.isNpc shl 9
			this.ambientSoundVolume = this.playerEntity!!.ambientSoundVolume
			this.minDelay = 256
			this.ambientSoundMaxDelay = 256
			this.vorbis = this.playerEntity!!.vorbis
		}
		if (this.soundId == sound && vorbis == this.vorbis) {
			return
		}
		if (this.soundEffect == null) {
			return
		}
		AmbientSound.gameSounds!!.remove(this.soundEffect)
		this.soundEffectBuffer = null
		this.soundEffect = null
		this.instrumentEffect = null
	}

	companion object {
		@OriginalMember(owner = "client!vv", name = "o", descriptor = "Lclient!sq;")
		@JvmField
		var gameSounds: PcmStreamMixer? = null
		@OriginalMember(owner = "client!qh", name = "M", descriptor = "Lclient!eq;")
		@JvmField
		val PLAYER_AMBIENT_SOUNDS: IterableHashTable = IterableHashTable(16)
		@OriginalMember(owner = "client!wc", name = "a", descriptor = "(Lclient!ke;I)V")
		@JvmStatic
		fun unlinkPlayerSound(@OriginalArg(0) playerEntity: PlayerEntity?) {
			@Pc(16) val positionedSound: AmbientSound? = AmbientSound.PLAYER_AMBIENT_SOUNDS.get(playerEntity!!.playerIndex.toLong()) as AmbientSound?
			if (positionedSound == null) {
				return
			}
			if (positionedSound.soundEffect != null) {
				AmbientSound.gameSounds!!.remove(positionedSound.soundEffect)
				positionedSound.soundEffect = null
			}
			positionedSound.unlink()
		}
		@OriginalMember(owner = "client!rw", name = "k", descriptor = "Lclient!uh;")
		@JvmField
		val aClass341_59: Deque = Deque()
		@OriginalMember(owner = "client!lia", name = "a", descriptor = "(ZLclient!ke;)V")
		@JvmStatic
		fun updatePlayerAmbientSound(@OriginalArg(1) playerEntity: PlayerEntity?) {
			@Pc(11) val positionedSound: AmbientSound? = AmbientSound.PLAYER_AMBIENT_SOUNDS.get(playerEntity!!.playerIndex.toLong()) as AmbientSound?
			if (positionedSound == null) {
				AmbientSound.createAmbientSoundEffect(null, playerEntity.level.toInt(), playerEntity.movementBaseY[0], playerEntity.movementBaseX[0], playerEntity, null, 0)
			} else {
				positionedSound.update()
			}
		}

		@OriginalMember(owner = "client!gl", name = "a", descriptor = "(ZLclient!ff;)V")
		@JvmStatic
		fun unlinkEntitySound(@OriginalArg(1) npcEntity: NPCEntity?) {
			@Pc(15) var positionedSound: AmbientSound? = AmbientSound.aClass341_25.first() as AmbientSound?
			while (positionedSound != null) {
				if (positionedSound.npcEntity === npcEntity) {
					if (positionedSound.soundEffect != null) {
						AmbientSound.gameSounds!!.remove(positionedSound.soundEffect)
						positionedSound.soundEffect = null
					}
					positionedSound.unlink()
					return
				}
				positionedSound = AmbientSound.aClass341_25.next() as AmbientSound?
			}
		}

		@OriginalMember(owner = "client!ds", name = "a", descriptor = "(IIIII)V")
		@JvmStatic
		fun updateAllAmbientSounds(@OriginalArg(1) level: Int, @OriginalArg(2) step: Int, @OriginalArg(3) absZ: Int, @OriginalArg(4) absX: Int) {
			@Pc(10) var positionedSound: AmbientSound? = AmbientSound.aClass341_59.first() as AmbientSound?
			while (positionedSound != null) {
				SoundManager.updateAmbientSound(step, absX, level, absZ, positionedSound)
				positionedSound = AmbientSound.aClass341_59.next() as AmbientSound?
			}
			@Pc(31) var positionedSound2: AmbientSound? = AmbientSound.aClass341_25.first() as AmbientSound?
			while (positionedSound2 != null) {
				@Pc(35) var movementSpeed: Byte = 1
				@Pc(40) val basType: BasType? = positionedSound2.npcEntity!!.getBasType()
				if (positionedSound2.npcEntity!!.movingSequence.sequenceId == -1 || positionedSound2.npcEntity!!.movingSequence.hasType) {
					movementSpeed = 0
				} else if (basType!!.runningSequence == positionedSound2.npcEntity!!.movingSequence.sequenceId || positionedSound2.npcEntity!!.movingSequence.sequenceId == basType.runDir3 || basType.runDir1 == positionedSound2.npcEntity!!.movingSequence.sequenceId || basType.runDir2 == positionedSound2.npcEntity!!.movingSequence.sequenceId) {
					movementSpeed = 2
				} else if (basType.teleportSequence == positionedSound2.npcEntity!!.movingSequence.sequenceId || basType.teleDir3 == positionedSound2.npcEntity!!.movingSequence.sequenceId || positionedSound2.npcEntity!!.movingSequence.sequenceId == basType.teleDir1 || positionedSound2.npcEntity!!.movingSequence.sequenceId == basType.teleDir2) {
					movementSpeed = 3
				}
				if (movementSpeed.toInt() != positionedSound2.movementSpeed) {
					@Pc(137) val soundId: Int = AmbientSound.getSoundEffectId(positionedSound2.npcEntity)
					@Pc(141) var npcType: NPCType? = positionedSound2.npcEntity!!.npcType
					if (npcType!!.alternativeIds != null) {
						npcType = npcType.getMultiNpc(PlayerVarDomain.playerVarProvider)
					}
					if (npcType == null || soundId == -1) {
						positionedSound2.movementSpeed = movementSpeed.toInt()
						positionedSound2.vorbis = false
						positionedSound2.soundId = -1
					} else if (soundId == positionedSound2.soundId && npcType.instrumentSoundEffect == positionedSound2.vorbis) {
						positionedSound2.ambientSoundVolume = npcType.ambientSoundVolume
						positionedSound2.movementSpeed = movementSpeed.toInt()
					} else {
						@Pc(182) var replaceSound: Boolean = false
						if (positionedSound2.soundEffect == null) {
							replaceSound = true
						} else {
							positionedSound2.ambientSoundVolume -= 512
							if (positionedSound2.ambientSoundVolume <= 0) {
								AmbientSound.gameSounds!!.remove(positionedSound2.soundEffect)
								replaceSound = true
								positionedSound2.soundEffect = null
							}
						}
						if (replaceSound) {
							positionedSound2.vorbis = npcType.instrumentSoundEffect
							positionedSound2.soundId = soundId
							positionedSound2.soundEffectBuffer = null
							positionedSound2.instrumentEffect = null
							positionedSound2.movementSpeed = movementSpeed.toInt()
							positionedSound2.ambientSoundVolume = npcType.ambientSoundVolume
						}
					}
				}
				positionedSound2.minX = positionedSound2.npcEntity!!.x
				positionedSound2.absStartX = positionedSound2.npcEntity!!.x + (positionedSound2.npcEntity!!.getSize() shl 8)
				positionedSound2.absEndX = positionedSound2.npcEntity!!.z
				positionedSound2.plane = positionedSound2.npcEntity!!.z + (positionedSound2.npcEntity!!.getSize() shl 8)
				SoundManager.updateAmbientSound(step, absX, level, absZ, positionedSound2)
				positionedSound2 = AmbientSound.aClass341_25.next() as AmbientSound?
			}
			@Pc(297) var positionedSound3: AmbientSound? = AmbientSound.PLAYER_AMBIENT_SOUNDS.first() as AmbientSound?
			while (positionedSound3 != null) {
				@Pc(308) var movementSpeed: Byte = 1
				@Pc(313) val basType2: BasType? = positionedSound3.playerEntity!!.getBasType()
				if (positionedSound3.playerEntity!!.movingSequence.sequenceId == -1 || positionedSound3.playerEntity!!.movingSequence.hasType) {
					movementSpeed = 0
				} else if (positionedSound3.playerEntity!!.movingSequence.sequenceId == basType2!!.runningSequence || basType2.runDir3 == positionedSound3.playerEntity!!.movingSequence.sequenceId || basType2.runDir1 == positionedSound3.playerEntity!!.movingSequence.sequenceId || basType2.runDir2 == positionedSound3.playerEntity!!.movingSequence.sequenceId) {
					movementSpeed = 2
				} else if (basType2.teleportSequence == positionedSound3.playerEntity!!.movingSequence.sequenceId || positionedSound3.playerEntity!!.movingSequence.sequenceId == basType2.teleDir3 || basType2.teleDir1 == positionedSound3.playerEntity!!.movingSequence.sequenceId || positionedSound3.playerEntity!!.movingSequence.sequenceId == basType2.teleDir2) {
					movementSpeed = 3
				}
				if (positionedSound3.movementSpeed != movementSpeed.toInt()) {
					@Pc(409) val soundId: Int = SoundEffectPlayer.getSoundEffectId(positionedSound3.playerEntity)
					if (soundId == positionedSound3.soundId && positionedSound3.playerEntity!!.vorbis == positionedSound3.vorbis) {
						positionedSound3.ambientSoundVolume = positionedSound3.playerEntity!!.ambientSoundVolume
						positionedSound3.movementSpeed = movementSpeed.toInt()
					} else {
						@Pc(431) var replaceSound: Boolean = false
						if (positionedSound3.soundEffect == null) {
							replaceSound = true
						} else {
							positionedSound3.ambientSoundVolume -= 512
							if (positionedSound3.ambientSoundVolume <= 0) {
								AmbientSound.gameSounds!!.remove(positionedSound3.soundEffect)
								positionedSound3.soundEffect = null
								replaceSound = true
							}
						}
						if (replaceSound) {
							positionedSound3.vorbis = positionedSound3.playerEntity!!.vorbis
							positionedSound3.ambientSoundVolume = positionedSound3.playerEntity!!.ambientSoundVolume
							positionedSound3.instrumentEffect = null
							positionedSound3.soundId = soundId
							positionedSound3.soundEffectBuffer = null
							positionedSound3.movementSpeed = movementSpeed.toInt()
						}
					}
				}
				positionedSound3.minX = positionedSound3.playerEntity!!.x
				positionedSound3.absStartX = positionedSound3.playerEntity!!.x + (positionedSound3.playerEntity!!.getSize() shl 8)
				positionedSound3.absEndX = positionedSound3.playerEntity!!.z
				positionedSound3.plane = positionedSound3.playerEntity!!.z + (positionedSound3.playerEntity!!.getSize() shl 8)
				SoundManager.updateAmbientSound(step, absX, level, absZ, positionedSound3)
				positionedSound3 = AmbientSound.PLAYER_AMBIENT_SOUNDS.next() as AmbientSound?
			}
		}

		@OriginalMember(owner = "client!client", name = "wb", descriptor = "Lclient!uh;")
		@JvmField
		val aClass341_25: Deque = Deque()
		@OriginalMember(owner = "client!ad", name = "a", descriptor = "(ILclient!ff;IIILclient!ke;Lclient!iv;I)V")
		@JvmStatic
		fun createAmbientSoundEffect(@OriginalArg(1) npcEntity: NPCEntity?, @OriginalArg(2) level: Int, @OriginalArg(3) z: Int, @OriginalArg(4) x: Int, @OriginalArg(5) playerEntity: PlayerEntity?, @OriginalArg(6) locType: LocType?, @OriginalArg(7) rotation: Int) {
			@Pc(12) val positionedSound: AmbientSound = AmbientSound()
			positionedSound.minX = x shl 9
			positionedSound.level = level
			positionedSound.absEndX = z shl 9
			if (locType != null) {
				positionedSound.emittingLoc = locType
				@Pc(163) var sizeX: Int = locType.sizeX
				@Pc(166) var sizeY: Int = locType.sizeY
				if (rotation == 1 || rotation == 3) {
					sizeX = locType.sizeY
					sizeY = locType.sizeX
				}
				positionedSound.plane = (sizeY + z) shl 9
				positionedSound.soundMinStep = locType.soundMinInterval
				positionedSound.soundId = locType.soundId
				positionedSound.minDelay = locType.ambientSoundMinDelay
				positionedSound.ambientSoundGroupIds = locType.soundGroupIds
				positionedSound.ambientSoundMaxDelay = locType.ambientSoundMaxDelay
				positionedSound.random = locType.instrumentAmbientSound
				positionedSound.ambientSoundMinHearDistance = locType.soundRadius shl 9
				positionedSound.soundMaxStep = locType.soundMaxInterval
				positionedSound.absStartX = (sizeX + x) shl 9
				positionedSound.ambientSoundVolume = locType.soundVolume
				positionedSound.vorbis = locType.instrumentSoundEffect
				if (locType.alternativeIds != null) {
					positionedSound.hasTransformation = true
					positionedSound.update()
				}
				if (positionedSound.ambientSoundGroupIds != null) {
					positionedSound.stepsUntilNextSound = positionedSound.soundMinStep + ((positionedSound.soundMaxStep - positionedSound.soundMinStep).toDouble() * util.random()).toInt()
				}
				AmbientSound.aClass341_59.addLast(positionedSound)
				return
			}
			if (npcEntity != null) {
				positionedSound.npcEntity = npcEntity
				@Pc(35) var npcType: NPCType? = npcEntity.npcType
				if (npcType!!.alternativeIds != null) {
					positionedSound.hasTransformation = true
					npcType = npcType.getMultiNpc(PlayerVarDomain.playerVarProvider)
				}
				if (npcType != null) {
					positionedSound.absStartX = (x + npcType.size) shl 9
					positionedSound.plane = (z + npcType.size) shl 9
					positionedSound.soundId = AmbientSound.getSoundEffectId(npcEntity)
					positionedSound.ambientSoundMaxDelay = npcType.ambientSoundMaxDelay
					positionedSound.ambientSoundMinHearDistance = npcType.ambientSoundMinHearDistance shl 9
					positionedSound.ambientSoundVolume = npcType.ambientSoundVolume
					positionedSound.minDelay = npcType.ambientSoundMinDelay
					positionedSound.vorbis = npcType.instrumentSoundEffect
				}
				AmbientSound.aClass341_25.addLast(positionedSound)
				return
			}
			if (playerEntity == null) {
				return
			}
			positionedSound.playerEntity = playerEntity
			positionedSound.absStartX = (playerEntity.getSize() + x) shl 9
			positionedSound.plane = (playerEntity.getSize() + z) shl 9
			positionedSound.soundId = SoundEffectPlayer.getSoundEffectId(playerEntity)
			positionedSound.minDelay = 256
			positionedSound.ambientSoundMinHearDistance = playerEntity.isNpc shl 9
			positionedSound.ambientSoundVolume = playerEntity.ambientSoundVolume
			positionedSound.ambientSoundMaxDelay = 256
			positionedSound.vorbis = playerEntity.vorbis
			AmbientSound.PLAYER_AMBIENT_SOUNDS.put(playerEntity.playerIndex.toLong(), positionedSound)
			return
		}

		@OriginalMember(owner = "client!ci", name = "a", descriptor = "(Lclient!iv;IIII)V")
		@JvmStatic
		fun unlinkLocSound(@OriginalArg(0) locType: LocType?, @OriginalArg(1) x: Int, @OriginalArg(2) z: Int, @OriginalArg(3) level: Int) {
			@Pc(15) var positionedSound: AmbientSound? = AmbientSound.aClass341_59.first() as AmbientSound?
			while (positionedSound != null) {
				if (level == positionedSound.level && (x shl 9) == positionedSound.minX && (z shl 9) == positionedSound.absEndX && locType!!.id == positionedSound.emittingLoc!!.id) {
					if (positionedSound.soundEffect != null) {
						AmbientSound.gameSounds!!.remove(positionedSound.soundEffect)
						positionedSound.soundEffect = null
					}
					if (positionedSound.ambientSoundGroupStream != null) {
						AmbientSound.gameSounds!!.remove(positionedSound.ambientSoundGroupStream)
						positionedSound.ambientSoundGroupStream = null
					}
					positionedSound.unlink()
					return
				}
				positionedSound = AmbientSound.aClass341_59.next() as AmbientSound?
			}
		}
		@OriginalMember(owner = "client!haa", name = "a", descriptor = "(BZ)V")
		@JvmStatic
		fun unlinkSounds(@OriginalArg(1) clearEntitySounds: Boolean) {
			@Pc(10) var positionedSound: AmbientSound? = AmbientSound.aClass341_59.first() as AmbientSound?
			while (positionedSound != null) {
				if (positionedSound.soundEffect != null) {
					AmbientSound.gameSounds!!.remove(positionedSound.soundEffect)
					positionedSound.soundEffect = null
				}
				if (positionedSound.ambientSoundGroupStream != null) {
					AmbientSound.gameSounds!!.remove(positionedSound.ambientSoundGroupStream)
					positionedSound.ambientSoundGroupStream = null
				}
				positionedSound.unlink()
				positionedSound = AmbientSound.aClass341_59.next() as AmbientSound?
			}
			if (!clearEntitySounds) {
				return
			}
			@Pc(53) var positionedSound2: AmbientSound? = AmbientSound.aClass341_25.first() as AmbientSound?
			while (positionedSound2 != null) {
				if (positionedSound2.soundEffect != null) {
					AmbientSound.gameSounds!!.remove(positionedSound2.soundEffect)
					positionedSound2.soundEffect = null
				}
				positionedSound2.unlink()
				positionedSound2 = AmbientSound.aClass341_25.next() as AmbientSound?
			}
			@Pc(80) var positionedSound3: AmbientSound? = AmbientSound.PLAYER_AMBIENT_SOUNDS.first() as AmbientSound?
			while (positionedSound3 != null) {
				if (positionedSound3.soundEffect != null) {
					AmbientSound.gameSounds!!.remove(positionedSound3.soundEffect)
					positionedSound3.soundEffect = null
				}
				positionedSound3.unlink()
				positionedSound3 = AmbientSound.PLAYER_AMBIENT_SOUNDS.next() as AmbientSound?
			}
		}

		@OriginalMember(owner = "client!iaa", name = "b", descriptor = "(B)V")
		@JvmStatic
		fun updateTransformingSounds() {
			@Pc(15) var positionedSound: AmbientSound? = AmbientSound.aClass341_59.first() as AmbientSound?
			while (positionedSound != null) {
				if (positionedSound.hasTransformation) {
					positionedSound.update()
				}
				positionedSound = AmbientSound.aClass341_59.next() as AmbientSound?
			}
			@Pc(35) var positionedSound2: AmbientSound? = AmbientSound.aClass341_25.first() as AmbientSound?
			while (positionedSound2 != null) {
				if (positionedSound2.hasTransformation) {
					positionedSound2.update()
				}
				positionedSound2 = AmbientSound.aClass341_25.next() as AmbientSound?
			}
		}

		@OriginalMember(owner = "client!gr", name = "a", descriptor = "(Lclient!ff;I)I")
		@JvmStatic
		fun getSoundEffectId(@OriginalArg(0) npcEntity: NPCEntity?): Int {
			@Pc(8) var npcType: NPCType? = npcEntity!!.npcType
			if (npcType!!.alternativeIds != null) {
				npcType = npcType.getMultiNpc(PlayerVarDomain.playerVarProvider)
				if (npcType == null) {
					return -1
				}
			}
			@Pc(23) var soundEffectId: Int = npcType.idleSoundEffect
			@Pc(32) val basType: BasType? = npcEntity.getBasType()
			if (npcEntity.movingSequence.sequenceId == -1 || npcEntity.movingSequence.hasType) {
				soundEffectId = npcType.walkingSoundEffect
			} else if (npcEntity.movingSequence.sequenceId == basType!!.runningSequence || basType.runDir3 == npcEntity.movingSequence.sequenceId || basType.runDir1 == npcEntity.movingSequence.sequenceId || npcEntity.movingSequence.sequenceId == basType.runDir2) {
				soundEffectId = npcType.runningSoundEffect
			} else if (basType.teleportSequence == npcEntity.movingSequence.sequenceId || npcEntity.movingSequence.sequenceId == basType.teleDir3 || npcEntity.movingSequence.sequenceId == basType.teleDir1 || basType.teleDir2 == npcEntity.movingSequence.sequenceId) {
				soundEffectId = npcType.teleportSoundEffect
			}
			return soundEffectId
		}
	}
}

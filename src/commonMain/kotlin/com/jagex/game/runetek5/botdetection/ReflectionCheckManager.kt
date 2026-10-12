package com.jagex.game.runetek5.botdetection

import com.jagex.game.runetek5.buffer.BitPacket
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.core.datastruct.key.Deque
import com.jagex.game.runetek5.sign.SignLink
import com.jagex.game.runetek5.sign.SignedResource
import com.jagex.game.runetek5.sign.SignedResourceStatus
import io.ByteArrayInputStream
import io.IOException
import io.InvalidClassException
import io.ObjectInputStream
import io.OptionalDataException
import io.StreamCorruptedException
import lang.reflect.Field
import lang.reflect.InvocationTargetException
import lang.reflect.Method
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import lang.Class
import lang.ClassNotFoundException
import lang.IllegalAccessException
import lang.SecurityException
import lang.getDeclaredMethod

object ReflectionCheckManager {
	@OriginalMember(owner = "client!ri", name = "r", descriptor = "Lclient!uh;")
	@JvmField
	var PENDING_REFLECTION_CHECKS: Deque = Deque()

	@OriginalMember(owner = "client!mr", name = "a", descriptor = "(ILclient!cea;Lclient!wea;I)V")
	@JvmStatic
	fun requestReflectionCheck(@OriginalArg(0) packetSize: Int, @OriginalArg(1) packet: Packet?, @OriginalArg(2) signLink: SignLink?) {
		@Pc(9) val reflectionCheck: ReflectionCheck = ReflectionCheck()
		reflectionCheck.operationCount = packet!!.readUByte()
		reflectionCheck.id = packet.readInt()
		reflectionCheck.operations = IntArray(reflectionCheck.operationCount)
		reflectionCheck.argumentBuffers = arrayOfNulls<Array<ByteArray?>>(reflectionCheck.operationCount)
		reflectionCheck.methodsToCheck = arrayOfNulls<SignedResource>(reflectionCheck.operationCount)
		reflectionCheck.fieldsToCheck = arrayOfNulls<SignedResource>(reflectionCheck.operationCount)
		reflectionCheck.exceptionsEncountered = IntArray(reflectionCheck.operationCount)
		reflectionCheck.fieldInput = IntArray(reflectionCheck.operationCount)
		@Pc(55) var i: Int = 0
		while (i < reflectionCheck.operationCount) {
			try {
				@Pc(61) val operation: Int = packet.readUByte()
				@Pc(86) var string: String?
				@Pc(90) var string2: String?
				@Pc(94) val local94: Int
				if (operation == 0 || operation == 1 || operation == 2) {
					string = packet.readString()
					string2 = packet.readString()
					var fieldValue: Int = 0
					if (operation == 1) {
						fieldValue = packet.readInt()
					}
					reflectionCheck.operations!![i] = operation
					reflectionCheck.fieldInput!![i] = fieldValue
					reflectionCheck.fieldsToCheck!![i] = signLink!!.getDeclaredField(string2, ReflectionCheckManager.getTypeClass(string))
				} else if (operation == 3 || operation == 4) {
					string = packet.readString()
					string2 = packet.readString()
					val argumentCount: Int = packet.readUByte()
					@Pc(97) val strings: Array<String?> = arrayOfNulls<String>(argumentCount)
					for (@Pc(99) j in 0 until argumentCount) {
						strings[j] = packet.readString()
					}
					@Pc(114) val arguments: Array<ByteArray?> = arrayOfNulls<ByteArray>(argumentCount)
					@Pc(125) val local125: Int
					if (operation == 3) {
						for (@Pc(119) k in 0 until argumentCount) {
							val argumentLength: Int = packet.readInt()
							arguments[k] = ByteArray(argumentLength)
							packet.readBytes(0, argumentLength, arguments[k])
						}
					}
					reflectionCheck.operations!![i] = operation
					@Pc(150) val classes: Array<Class<*>?> = arrayOfNulls<Class<*>>(argumentCount)
					for (argumentIndex in 0 until argumentCount) {
						classes[argumentIndex] = ReflectionCheckManager.getTypeClass(strings[argumentIndex])
					}
					reflectionCheck.methodsToCheck!![i] = signLink!!.getDeclaredMethod(classes, ReflectionCheckManager.getTypeClass(string), string2)
					reflectionCheck.argumentBuffers!![i] = arguments
				}
			} catch (@Pc(231) ex: ClassNotFoundException) {
				reflectionCheck.exceptionsEncountered!![i] = -1
			} catch (@Pc(238) ex2: SecurityException) {
				reflectionCheck.exceptionsEncountered!![i] = -2
			} catch (@Pc(245) ex3: NullPointerException) {
				reflectionCheck.exceptionsEncountered!![i] = -3
			} catch (@Pc(252) ex4: Exception) {
				reflectionCheck.exceptionsEncountered!![i] = -4
			} catch (@Pc(259) ex5: Throwable) {
				reflectionCheck.exceptionsEncountered!![i] = -5
			}
			i++
		}
		ReflectionCheckManager.PENDING_REFLECTION_CHECKS.addLast(reflectionCheck)
	}

	@OriginalMember(owner = "client!raa", name = "a", descriptor = "(BLclient!ua;)V")
	@JvmStatic
	fun collectResult(@OriginalArg(1) bitPacket: BitPacket?) {
		@Pc(10) val reflectionCheck: ReflectionCheck? = ReflectionCheckManager.PENDING_REFLECTION_CHECKS.first() as ReflectionCheck?
		if (reflectionCheck == null) {
			return
		}
		@Pc(15) var pending: Boolean = false
		@Pc(17) var i: Int = 0
		while (i < reflectionCheck.operationCount) {
			if (reflectionCheck.fieldsToCheck!![i] != null) {
				if (reflectionCheck.fieldsToCheck!![i]!!.status == SignedResourceStatus.ERROR) {
					reflectionCheck.exceptionsEncountered!![i] = -5
				}
				if (reflectionCheck.fieldsToCheck!![i]!!.status == SignedResourceStatus.IDLE) {
					pending = true
				}
			}
			if (reflectionCheck.methodsToCheck!![i] != null) {
				if (reflectionCheck.methodsToCheck!![i]!!.status == SignedResourceStatus.ERROR) {
					reflectionCheck.exceptionsEncountered!![i] = -6
				}
				if (reflectionCheck.methodsToCheck!![i]!!.status == SignedResourceStatus.IDLE) {
					pending = true
				}
			}
			i++
		}
		if (pending) {
			return
		}
		@Pc(92) val currentPosition: Int = bitPacket!!.currentPosition
		bitPacket.writeInt(reflectionCheck.id)
		@Pc(99) var j: Int = 0
		while (j < reflectionCheck.operationCount) {
			if (reflectionCheck.exceptionsEncountered!![j] == 0) {
				try {
					@Pc(120) val operation: Int = reflectionCheck.operations!![j]
					@Pc(137) var field: Field?
					@Pc(163) val local163: Int
					if (operation == 0) {
						field = reflectionCheck.fieldsToCheck!![j]!!.result as Field?
						val fieldValue: Int = field!!.getInt(null)
						bitPacket.writeByte(0)
						bitPacket.writeInt(fieldValue)
					} else if (operation == 1) {
						field = reflectionCheck.fieldsToCheck!![j]!!.result as Field?
						field!!.setInt(null, reflectionCheck.fieldInput!![j])
						bitPacket.writeByte(0)
					} else if (operation == 2) {
						field = reflectionCheck.fieldsToCheck!![j]!!.result as Field?
						val modifiers: Int = field!!.getModifiers()
						bitPacket.writeByte(0)
						bitPacket.writeInt(modifiers)
					}
					@Pc(202) var method: Method?
					if (operation == 3) {
						method = reflectionCheck.methodsToCheck!![j]!!.result as Method?
						@Pc(207) val argumentBuffer: Array<ByteArray?> = reflectionCheck.argumentBuffers!![j]!!
						@Pc(211) val objects: Array<Any?> = arrayOfNulls<Any>(argumentBuffer.size)
						for (@Pc(213) k in 0 until argumentBuffer.size) {
							@Pc(225) val objectInputStream: ObjectInputStream = ObjectInputStream(ByteArrayInputStream(argumentBuffer[k]))
							objects[k] = objectInputStream.readObject()
						}
						@Pc(240) val `object`: Any? = method!!.invoke(null, *objects)
						if (`object` == null) {
							bitPacket.writeByte(0)
						} else if (`object` is Number) {
							bitPacket.writeByte(1)
							bitPacket.writeLong(`object`.toLong())
						} else if (`object` is String) {
							bitPacket.writeByte(2)
							bitPacket.writeString(`object`)
						} else {
							bitPacket.writeByte(4)
						}
					} else if (operation == 4) {
						method = reflectionCheck.methodsToCheck!![j]!!.result as Method?
						val modifiers: Int = method!!.getModifiers()
						bitPacket.writeByte(0)
						bitPacket.writeInt(modifiers)
					}
				} catch (@Pc(306) ex: ClassNotFoundException) {
					bitPacket.writeByte(-10)
				} catch (@Pc(312) ex2: InvalidClassException) {
					bitPacket.writeByte(-11)
				} catch (@Pc(318) ex3: StreamCorruptedException) {
					bitPacket.writeByte(-12)
				} catch (@Pc(324) ex4: OptionalDataException) {
					bitPacket.writeByte(-13)
				} catch (@Pc(330) ex5: IllegalAccessException) {
					bitPacket.writeByte(-14)
				} catch (@Pc(336) ex6: IllegalArgumentException) {
					bitPacket.writeByte(-15)
				} catch (@Pc(342) ex7: InvocationTargetException) {
					bitPacket.writeByte(-16)
				} catch (@Pc(348) ex8: SecurityException) {
					bitPacket.writeByte(-17)
				} catch (@Pc(354) ex9: IOException) {
					bitPacket.writeByte(-18)
				} catch (@Pc(360) ex10: NullPointerException) {
					bitPacket.writeByte(-19)
				} catch (@Pc(366) ex11: Exception) {
					bitPacket.writeByte(-20)
				} catch (@Pc(372) ex12: Throwable) {
					bitPacket.writeByte(-21)
				}
			} else {
				bitPacket.writeByte(reflectionCheck.exceptionsEncountered!![j])
			}
			j++
		}
		bitPacket.writeCrc(currentPosition)
		reflectionCheck.unlink()
	}

	@OriginalMember(owner = "client!vk", name = "d", descriptor = "(I)Z")
	@JvmStatic
	fun isCheckReady(): Boolean {
		@Pc(10) val reflectionCheck: ReflectionCheck? = ReflectionCheckManager.PENDING_REFLECTION_CHECKS.first() as ReflectionCheck?
		if (reflectionCheck == null) {
			return false
		}
		@Pc(24) var i: Int = 0
		while (i < reflectionCheck.operationCount) {
			if (reflectionCheck.fieldsToCheck!![i] != null && reflectionCheck.fieldsToCheck!![i]!!.status == SignedResourceStatus.IDLE) {
				return false
			}
			if (reflectionCheck.methodsToCheck!![i] != null && reflectionCheck.methodsToCheck!![i]!!.status == SignedResourceStatus.IDLE) {
				return false
			}
			i++
		}
		return true
	}

	@OriginalMember(owner = "client!qfa", name = "o", descriptor = "(I)V")
	@JvmStatic
	fun setupPendingChecks() {
		ReflectionCheckManager.PENDING_REFLECTION_CHECKS = Deque()
	}

	@OriginalMember(owner = "client!aia", name = "a", descriptor = "(ILjava/lang/String;)Ljava/lang/Class;")
	@JvmStatic
	@Throws(ClassNotFoundException::class)
	fun getTypeClass(@OriginalArg(1) string: String?): Class<*> {
		if (string!!.equals("B")) {
			return lang.BYTE_TYPE
		} else if (string.equals("I")) {
			return lang.INT_TYPE
		} else if (string.equals("S")) {
			return lang.SHORT_TYPE
		} else if (string.equals("J")) {
			return lang.LONG_TYPE
		} else if (string.equals("Z")) {
			return lang.BOOLEAN_TYPE
		} else if (string.equals("F")) {
			return lang.FLOAT_TYPE
		} else if (string.equals("D")) {
			return lang.DOUBLE_TYPE
		} else if (string.equals("C")) {
			return lang.CHAR_TYPE
		} else {
			return lang.forName(string)
		}
	}
}

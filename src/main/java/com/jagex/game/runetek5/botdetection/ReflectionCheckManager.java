package com.jagex.game.runetek5.botdetection;

import com.jagex.game.runetek5.core.datastruct.key.Deque;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.sign.SignLink;
import com.jagex.game.runetek5.sign.SignedResource;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.buffer.BitPacket;
import com.jagex.game.runetek5.sign.SignedResourceStatus;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class ReflectionCheckManager {
	@OriginalMember(owner = "client!ri", name = "r", descriptor = "Lclient!uh;")
	public static Deque PENDING_REFLECTION_CHECKS = new Deque();

	private ReflectionCheckManager() {
		/* empty */
	}
	@OriginalMember(owner = "client!mr", name = "a", descriptor = "(ILclient!cea;Lclient!wea;I)V")
	public static void requestReflectionCheck(@OriginalArg(0) int packetSize, @OriginalArg(1) Packet packet, @OriginalArg(2) SignLink signLink) {
		@Pc(9) ReflectionCheck reflectionCheck = new ReflectionCheck();
		reflectionCheck.operationCount = packet.readUByte();
		reflectionCheck.id = packet.readInt();
		reflectionCheck.operations = new int[reflectionCheck.operationCount];
		reflectionCheck.argumentBuffers = new byte[reflectionCheck.operationCount][][];
		reflectionCheck.methodsToCheck = new SignedResource[reflectionCheck.operationCount];
		reflectionCheck.fieldsToCheck = new SignedResource[reflectionCheck.operationCount];
		reflectionCheck.exceptionsEncountered = new int[reflectionCheck.operationCount];
		reflectionCheck.fieldInput = new int[reflectionCheck.operationCount];
		for (@Pc(55) int i = 0; i < reflectionCheck.operationCount; i++) {
			try {
				@Pc(61) int operation = packet.readUByte();
				@Pc(86) String string;
				@Pc(90) String string2;
				@Pc(94) int local94;
				if (operation == 0 || operation == 1 || operation == 2) {
					string = packet.readString();
					string2 = packet.readString();
					int fieldValue = 0;
					if (operation == 1) {
						fieldValue = packet.readInt();
					}
					reflectionCheck.operations[i] = operation;
					reflectionCheck.fieldInput[i] = fieldValue;
					reflectionCheck.fieldsToCheck[i] = signLink.getDeclaredField(string2, ReflectionCheckManager.getTypeClass(string));
				} else if (operation == 3 || operation == 4) {
					string = packet.readString();
					string2 = packet.readString();
					int argumentCount = packet.readUByte();
					@Pc(97) String[] strings = new String[argumentCount];
					for (@Pc(99) int j = 0; j < argumentCount; j++) {
						strings[j] = packet.readString();
					}
					@Pc(114) byte[][] arguments = new byte[argumentCount][];
					@Pc(125) int local125;
					if (operation == 3) {
						for (@Pc(119) int k = 0; k < argumentCount; k++) {
							int argumentLength = packet.readInt();
							arguments[k] = new byte[argumentLength];
							packet.readBytes(0, argumentLength, arguments[k]);
						}
					}
					reflectionCheck.operations[i] = operation;
					@Pc(150) Class[] classes = new Class[argumentCount];
					for (int argumentIndex = 0; argumentIndex < argumentCount; argumentIndex++) {
						classes[argumentIndex] = ReflectionCheckManager.getTypeClass(strings[argumentIndex]);
					}
					reflectionCheck.methodsToCheck[i] = signLink.getDeclaredMethod(classes, ReflectionCheckManager.getTypeClass(string), string2);
					reflectionCheck.argumentBuffers[i] = arguments;
				}
			} catch (@Pc(231) ClassNotFoundException ex) {
				reflectionCheck.exceptionsEncountered[i] = -1;
			} catch (@Pc(238) SecurityException ex2) {
				reflectionCheck.exceptionsEncountered[i] = -2;
			} catch (@Pc(245) NullPointerException ex3) {
				reflectionCheck.exceptionsEncountered[i] = -3;
			} catch (@Pc(252) Exception ex4) {
				reflectionCheck.exceptionsEncountered[i] = -4;
			} catch (@Pc(259) Throwable ex5) {
				reflectionCheck.exceptionsEncountered[i] = -5;
			}
		}
		ReflectionCheckManager.PENDING_REFLECTION_CHECKS.addLast(reflectionCheck);
	}
	@OriginalMember(owner = "client!raa", name = "a", descriptor = "(BLclient!ua;)V")
	public static void collectResult(@OriginalArg(1) BitPacket bitPacket) {
		@Pc(10) ReflectionCheck reflectionCheck = (ReflectionCheck) ReflectionCheckManager.PENDING_REFLECTION_CHECKS.first();
		if (reflectionCheck == null) {
			return;
		}
		@Pc(15) boolean pending = false;
		for (@Pc(17) int i = 0; i < reflectionCheck.operationCount; i++) {
			if (reflectionCheck.fieldsToCheck[i] != null) {
				if (reflectionCheck.fieldsToCheck[i].status == SignedResourceStatus.ERROR) {
					reflectionCheck.exceptionsEncountered[i] = -5;
				}
				if (reflectionCheck.fieldsToCheck[i].status == SignedResourceStatus.IDLE) {
					pending = true;
				}
			}
			if (reflectionCheck.methodsToCheck[i] != null) {
				if (reflectionCheck.methodsToCheck[i].status == SignedResourceStatus.ERROR) {
					reflectionCheck.exceptionsEncountered[i] = -6;
				}
				if (reflectionCheck.methodsToCheck[i].status == SignedResourceStatus.IDLE) {
					pending = true;
				}
			}
		}
		if (pending) {
			return;
		}
		@Pc(92) int currentPosition = bitPacket.currentPosition;
		bitPacket.writeInt(reflectionCheck.id);
		for (@Pc(99) int j = 0; j < reflectionCheck.operationCount; j++) {
			if (reflectionCheck.exceptionsEncountered[j] == 0) {
				try {
					@Pc(120) int operation = reflectionCheck.operations[j];
					@Pc(137) Field field;
					@Pc(163) int local163;
					if (operation == 0) {
						field = (Field) reflectionCheck.fieldsToCheck[j].result;
						int fieldValue = field.getInt(null);
						bitPacket.writeByte(0);
						bitPacket.writeInt(fieldValue);
					} else if (operation == 1) {
						field = (Field) reflectionCheck.fieldsToCheck[j].result;
						field.setInt(null, reflectionCheck.fieldInput[j]);
						bitPacket.writeByte(0);
					} else if (operation == 2) {
						field = (Field) reflectionCheck.fieldsToCheck[j].result;
						int modifiers = field.getModifiers();
						bitPacket.writeByte(0);
						bitPacket.writeInt(modifiers);
					}
					@Pc(202) Method method;
					if (operation == 3) {
						method = (Method) reflectionCheck.methodsToCheck[j].result;
						@Pc(207) byte[][] argumentBuffer = reflectionCheck.argumentBuffers[j];
						@Pc(211) Object[] objects = new Object[argumentBuffer.length];
						for (@Pc(213) int k = 0; k < argumentBuffer.length; k++) {
							@Pc(225) ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(argumentBuffer[k]));
							objects[k] = objectInputStream.readObject();
						}
						@Pc(240) Object object = method.invoke(null, objects);
						if (object == null) {
							bitPacket.writeByte(0);
						} else if (object instanceof Number) {
							bitPacket.writeByte(1);
							bitPacket.writeLong(((Number) object).longValue());
						} else if (object instanceof String) {
							bitPacket.writeByte(2);
							bitPacket.writeString((String) object);
						} else {
							bitPacket.writeByte(4);
						}
					} else if (operation == 4) {
						method = (Method) reflectionCheck.methodsToCheck[j].result;
						int modifiers = method.getModifiers();
						bitPacket.writeByte(0);
						bitPacket.writeInt(modifiers);
					}
				} catch (@Pc(306) ClassNotFoundException ex) {
					bitPacket.writeByte(-10);
				} catch (@Pc(312) InvalidClassException ex2) {
					bitPacket.writeByte(-11);
				} catch (@Pc(318) StreamCorruptedException ex3) {
					bitPacket.writeByte(-12);
				} catch (@Pc(324) OptionalDataException ex4) {
					bitPacket.writeByte(-13);
				} catch (@Pc(330) IllegalAccessException ex5) {
					bitPacket.writeByte(-14);
				} catch (@Pc(336) IllegalArgumentException ex6) {
					bitPacket.writeByte(-15);
				} catch (@Pc(342) InvocationTargetException ex7) {
					bitPacket.writeByte(-16);
				} catch (@Pc(348) SecurityException ex8) {
					bitPacket.writeByte(-17);
				} catch (@Pc(354) IOException ex9) {
					bitPacket.writeByte(-18);
				} catch (@Pc(360) NullPointerException ex10) {
					bitPacket.writeByte(-19);
				} catch (@Pc(366) Exception ex11) {
					bitPacket.writeByte(-20);
				} catch (@Pc(372) Throwable ex12) {
					bitPacket.writeByte(-21);
				}
			} else {
				bitPacket.writeByte(reflectionCheck.exceptionsEncountered[j]);
			}
		}
		bitPacket.writeCrc(currentPosition);
		reflectionCheck.unlink();
	}

	@OriginalMember(owner = "client!vk", name = "d", descriptor = "(I)Z")
	public static boolean isCheckReady() {
		@Pc(10) ReflectionCheck reflectionCheck = (ReflectionCheck) ReflectionCheckManager.PENDING_REFLECTION_CHECKS.first();
		if (reflectionCheck == null) {
			return false;
		}
		for (@Pc(24) int i = 0; i < reflectionCheck.operationCount; i++) {
			if (reflectionCheck.fieldsToCheck[i] != null && reflectionCheck.fieldsToCheck[i].status == SignedResourceStatus.IDLE) {
				return false;
			}
			if (reflectionCheck.methodsToCheck[i] != null && reflectionCheck.methodsToCheck[i].status == SignedResourceStatus.IDLE) {
				return false;
			}
		}
		return true;
	}
	@OriginalMember(owner = "client!qfa", name = "o", descriptor = "(I)V")
	public static void setupPendingChecks() {
		ReflectionCheckManager.PENDING_REFLECTION_CHECKS = new Deque();
	}
	@OriginalMember(owner = "client!aia", name = "a", descriptor = "(ILjava/lang/String;)Ljava/lang/Class;")
	public static Class getTypeClass(@OriginalArg(1) String string) throws ClassNotFoundException {
		if (string.equals("B")) {
			return Byte.TYPE;
		} else if (string.equals("I")) {
			return Integer.TYPE;
		} else if (string.equals("S")) {
			return Short.TYPE;
		} else if (string.equals("J")) {
			return Long.TYPE;
		} else if (string.equals("Z")) {
			return Boolean.TYPE;
		} else if (string.equals("F")) {
			return Float.TYPE;
		} else if (string.equals("D")) {
			return Double.TYPE;
		} else if (string.equals("C")) {
			return Character.TYPE;
		} else {
			return Class.forName(string);
		}
	}
}

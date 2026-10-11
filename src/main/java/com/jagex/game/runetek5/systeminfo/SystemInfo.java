package com.jagex.game.runetek5.systeminfo;

import com.jagex.game.runetek5.cache.node.Node;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.sign.SignLink;
import com.jagex.game.runetek5.sign.SignLinkEnvironment;
import jaclib.hardware_info.HardwareInfo;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;
import com.jagex.game.runetek5.client.GameShell;
import com.jagex.game.runetek5.utils.Utils;
import com.jagex.game.runetek5.client.ClientState;
import java.io.File;
import java.lang.reflect.Method;

@OriginalClass("client!fa")
public final class SystemInfo extends Node {

	@OriginalMember(owner = "client!fa", name = "o", descriptor = "I")
	private int cpuClock;

	@OriginalMember(owner = "client!fa", name = "p", descriptor = "I")
	private int maxMem;

	@OriginalMember(owner = "client!fa", name = "r", descriptor = "I")
	private int javaVersionType;

	@OriginalMember(owner = "client!fa", name = "s", descriptor = "I")
	private int osVendor;

	@OriginalMember(owner = "client!fa", name = "t", descriptor = "I")
	private int anInt3429;

	@OriginalMember(owner = "client!fa", name = "u", descriptor = "Ljava/lang/String;")
	private String dxLetterVersion;

	@OriginalMember(owner = "client!fa", name = "v", descriptor = "I")
	private int anInt3430;

	@OriginalMember(owner = "client!fa", name = "w", descriptor = "I")
	private int processorCount;

	@OriginalMember(owner = "client!fa", name = "A", descriptor = "I")
	private int anInt3433;

	@OriginalMember(owner = "client!fa", name = "B", descriptor = "Ljava/lang/String;")
	private String gpuModel;

	@OriginalMember(owner = "client!fa", name = "C", descriptor = "I")
	private int javaSubBuild;

	@OriginalMember(owner = "client!fa", name = "D", descriptor = "I")
	private int dxDriverMonth;

	@OriginalMember(owner = "client!fa", name = "E", descriptor = "Z")
	private boolean idk;

	@OriginalMember(owner = "client!fa", name = "F", descriptor = "Z")
	private boolean x64os;

	@OriginalMember(owner = "client!fa", name = "G", descriptor = "I")
	public int ram;

	@OriginalMember(owner = "client!fa", name = "H", descriptor = "Ljava/lang/String;")
	private String dwDirectxVersion;

	@OriginalMember(owner = "client!fa", name = "J", descriptor = "I")
	private int javaUpdate;

	@OriginalMember(owner = "client!fa", name = "K", descriptor = "I")
	private int operatingSystemType;

	@OriginalMember(owner = "client!fa", name = "L", descriptor = "Ljava/lang/String;")
	private String gpuDriverVersion;

	@OriginalMember(owner = "client!fa", name = "N", descriptor = "I")
	private int javaBuild;

	@OriginalMember(owner = "client!fa", name = "O", descriptor = "I")
	private int dxDriverYear;

	@OriginalMember(owner = "client!fa", name = "<init>", descriptor = "()V")
	private SystemInfo() {
	}

	@OriginalMember(owner = "client!fa", name = "<init>", descriptor = "(ZLclient!wea;)V")
	public SystemInfo(@OriginalArg(0) boolean arg0, @OriginalArg(1) SignLink signLink) {
		if (SystemInfo.operatingSystemLowercase.startsWith("win")) {
			this.operatingSystemType = 1;
		} else if (SystemInfo.operatingSystemLowercase.startsWith("mac")) {
			this.operatingSystemType = 2;
		} else if (SystemInfo.operatingSystemLowercase.startsWith("linux")) {
			this.operatingSystemType = 3;
		} else {
			this.operatingSystemType = 4;
		}
		if (SystemInfo.OS_ARCH.startsWith("amd64") || SystemInfo.OS_ARCH.startsWith("x86_64")) {
			this.x64os = true;
		} else {
			this.x64os = false;
		}
		if (this.operatingSystemType == 1) {
			if (SystemInfo.osVersion.indexOf("4.0") != -1) {
				this.osVendor = 1;
			} else if (SystemInfo.osVersion.indexOf("4.1") != -1) {
				this.osVendor = 2;
			} else if (SystemInfo.osVersion.indexOf("4.9") != -1) {
				this.osVendor = 3;
			} else if (SystemInfo.osVersion.indexOf("5.0") != -1) {
				this.osVendor = 4;
			} else if (SystemInfo.osVersion.indexOf("5.1") != -1) {
				this.osVendor = 5;
			} else if (SystemInfo.osVersion.indexOf("6.0") != -1) {
				this.osVendor = 6;
			} else if (SystemInfo.osVersion.indexOf("6.1") != -1) {
				this.osVendor = 7;
			}
		} else if (this.operatingSystemType == 2) {
			if (SystemInfo.osVersion.indexOf("10.4") != -1) {
				this.osVendor = 20;
			} else if (SystemInfo.osVersion.indexOf("10.5") != -1) {
				this.osVendor = 21;
			} else if (SystemInfo.osVersion.indexOf("10.6") != -1) {
				this.osVendor = 22;
			}
		}
		if (PlayerUpdate.javaVendor.toLowerCase().indexOf("sun") != -1) {
			this.javaVersionType = 1;
		} else if (PlayerUpdate.javaVendor.toLowerCase().indexOf("microsoft") != -1) {
			this.javaVersionType = 2;
		} else if (PlayerUpdate.javaVendor.toLowerCase().indexOf("apple") == -1) {
			this.javaVersionType = 4;
		} else {
			this.javaVersionType = 3;
		}
		@Pc(206) int versionPos = 2;
		@Pc(208) int versionPart = 0;
		@Pc(214) char ch;
		try {
			while (versionPos < SystemInfo.javaVersion.length()) {
				ch = SystemInfo.javaVersion.charAt(versionPos);
				if (ch < '0' || ch > '9') {
					break;
				}
				versionPos++;
				versionPart = versionPart * 10 + ch - 48;
			}
		} catch (@Pc(246) Exception ex) {
		}
		this.javaBuild = versionPart;
		versionPos = SystemInfo.javaVersion.indexOf(46, 2) + 1;
		versionPart = 0;
		try {
			while (SystemInfo.javaVersion.length() > versionPos) {
				ch = SystemInfo.javaVersion.charAt(versionPos);
				if (ch < '0' || ch > '9') {
					break;
				}
				versionPart = versionPart * 10 + ch - 48;
				versionPos++;
			}
		} catch (@Pc(292) Exception ex2) {
		}
		this.javaSubBuild = versionPart;
		versionPart = 0;
		versionPos = SystemInfo.javaVersion.indexOf(95, 4) + 1;
		try {
			while (SystemInfo.javaVersion.length() > versionPos) {
				ch = SystemInfo.javaVersion.charAt(versionPos);
				if (ch < '0' || ch > '9') {
					break;
				}
				versionPart = ch + versionPart * 10 - 48;
				versionPos++;
			}
		} catch (@Pc(332) Exception ex3) {
		}
		if (signLink.signed) {
			this.idk = false;
		} else {
			this.idk = true;
		}
		this.javaUpdate = versionPart;
		this.maxMem = GameShell.maxMemory;
		if (this.javaBuild <= 3) {
			this.processorCount = 0;
		} else {
			this.processorCount = GameShell.availableProcessors;
		}
		try {
			@Pc(363) int[] cPUInfo = HardwareInfo.getCPUInfo();
			if (cPUInfo != null && cPUInfo.length == 7) {
				this.anInt3430 = cPUInfo[3];
				this.anInt3429 = cPUInfo[4];
				this.cpuClock = cPUInfo[2];
				this.anInt3433 = cPUInfo[5];
				this.ram = cPUInfo[6];
			}
		} catch (@Pc(396) Throwable ex4) {
			this.ram = 0;
		}
		if (this.dwDirectxVersion == null) {
			this.dwDirectxVersion = "";
		}
		if (this.gpuModel == null) {
			this.gpuModel = "";
		}
		if (this.dxLetterVersion == null) {
			this.dxLetterVersion = "";
		}
		if (this.gpuDriverVersion == null) {
			this.gpuDriverVersion = "";
		}
		this.trim();
	}

	@OriginalMember(owner = "client!fa", name = "a", descriptor = "(I)V")
	private void trim() {
		if (this.gpuModel.length() > 40) {
			this.gpuModel = this.gpuModel.substring(0, 40);
		}
		if (this.gpuDriverVersion.length() > 40) {
			this.gpuDriverVersion = this.gpuDriverVersion.substring(0, 40);
		}
		if (this.dwDirectxVersion.length() > 10) {
			this.dwDirectxVersion = this.dwDirectxVersion.substring(0, 10);
		}
		if (this.dxLetterVersion.length() > 10) {
			this.dxLetterVersion = this.dxLetterVersion.substring(0, 10);
		}
	}

	@OriginalMember(owner = "client!fa", name = "a", descriptor = "(B)I")
	public int size() {
		@Pc(12) int size1 = Utils.getJagStringLength(this.gpuModel) + 23;
		@Pc(19) int size2 = size1 + Utils.getJagStringLength(this.gpuDriverVersion);
		@Pc(26) int size3 = size2 + Utils.getJagStringLength(this.dwDirectxVersion);
		return size3 + Utils.getJagStringLength(this.dxLetterVersion);
	}

	@OriginalMember(owner = "client!fa", name = "a", descriptor = "(ILclient!cea;)V")
	public void writeMachineInformation(@OriginalArg(1) Packet packet) {
		packet.writeByte(5);
		packet.writeByte(this.operatingSystemType);
		packet.writeByte(this.x64os ? 1 : 0);
		packet.writeByte(this.osVendor);
		packet.writeByte(this.javaVersionType);
		packet.writeByte(this.javaBuild);
		packet.writeByte(this.javaSubBuild);
		packet.writeByte(this.javaUpdate);
		packet.writeByte(this.idk ? 1 : 0);
		packet.writeShort(this.maxMem);
		packet.writeByte(this.processorCount);
		packet.writeMedium(this.ram);
		packet.writeShort(this.cpuClock);
		packet.writeByte(this.anInt3430);
		packet.writeByte(this.anInt3429);
		packet.writeByte(this.anInt3433);
		packet.writeJagString(this.gpuModel);
		packet.writeJagString(this.gpuDriverVersion);
		packet.writeJagString(this.dwDirectxVersion);
		packet.writeJagString(this.dxLetterVersion);
		packet.writeByte(this.dxDriverMonth);
		packet.writeShort(this.dxDriverYear);
	}
	@OriginalMember(owner = "client!gu", name = "H", descriptor = "I")
	public static int clientBuild;

	@OriginalMember(owner = "client!wea", name = "B", descriptor = "Ljava/lang/String;")
	public static String operatingSystemLowercase;

	@OriginalMember(owner = "client!rn", name = "L", descriptor = "Lclient!fa;")
	public static SystemInfo SYSTEM_INFO;
	@OriginalMember(owner = "client!wea", name = "e", descriptor = "Ljava/lang/String;")
	public static String OS_ARCH;
	@OriginalMember(owner = "client!wea", name = "u", descriptor = "Ljava/lang/String;")
	public static String javaVersion;

	@OriginalMember(owner = "client!wea", name = "c", descriptor = "Ljava/lang/String;")
	public static String osVersion;
	@OriginalMember(owner = "client!ot", name = "a", descriptor = "(Ljava/io/File;ZZ)V")
	public static void dumpHeap(@OriginalArg(0) File file) {
		if (SystemInfo.hotSpotDiagnosticBean == null) {
			SystemInfo.initHotSpotDiagnosticBean();
		}
		try {
			@Pc(15) Class class2 = Class.forName("com.sun.management.HotSpotDiagnosticMXBean");
			@Pc(36) Method method = class2.getDeclaredMethod("dumpHeap", String.class, Boolean.TYPE);
			method.invoke(SystemInfo.hotSpotDiagnosticBean, file.getAbsolutePath(), Boolean.valueOf(false));
		} catch (@Pc(56) Exception ex) {
			System.out.println("HeapDump error:");
			ex.printStackTrace();
		}
	}

	@OriginalMember(owner = "client!ns", name = "a", descriptor = "(B)V")
	public static synchronized void initHotSpotDiagnosticBean() {
		if (SystemInfo.hotSpotDiagnosticBean != null) {
			return;
		}
		try {
			@Pc(9) Class class2 = Class.forName("java.lang.management.ManagementFactory");
			@Pc(14) Method method = class2.getDeclaredMethod("getPlatformMBeanServer", null);
			@Pc(19) Object object = method.invoke(null, (Object[]) null);
			@Pc(56) Method method2 = class2.getMethod("newPlatformMXBeanProxy", Class.forName("javax.management.MBeanServerConnection"), String.class, Class.class);
			SystemInfo.hotSpotDiagnosticBean = method2.invoke(null, object, "com.sun.management:type=HotSpotDiagnostic", Class.forName("com.sun.management.HotSpotDiagnosticMXBean"));
		} catch (@Pc(77) Exception ex) {
			System.out.println("HeapDump setup error:");
			ex.printStackTrace();
		}
	}

	@OriginalMember(owner = "client!bn", name = "i", descriptor = "Ljava/lang/Object;")
	public static volatile Object hotSpotDiagnosticBean = null;
}

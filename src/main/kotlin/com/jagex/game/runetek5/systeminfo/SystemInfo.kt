package com.jagex.game.runetek5.systeminfo

import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.cache.node.Node
import com.jagex.game.runetek5.client.GameShell
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.sign.SignLink
import com.jagex.game.runetek5.utils.Utils
import jaclib.hardware_info.HardwareInfo
import java.io.File
import java.lang.reflect.Method
import java.util.Locale
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!fa")
class SystemInfo : Node {

	@OriginalMember(owner = "client!fa", name = "o", descriptor = "I")
	private var cpuClock: Int = 0

	@OriginalMember(owner = "client!fa", name = "p", descriptor = "I")
	private var maxMem: Int = 0

	@OriginalMember(owner = "client!fa", name = "r", descriptor = "I")
	private var javaVersionType: Int = 0

	@OriginalMember(owner = "client!fa", name = "s", descriptor = "I")
	private var osVendor: Int = 0

	@OriginalMember(owner = "client!fa", name = "t", descriptor = "I")
	private var anInt3429: Int = 0

	@OriginalMember(owner = "client!fa", name = "u", descriptor = "Ljava/lang/String;")
	private var dxLetterVersion: String? = null

	@OriginalMember(owner = "client!fa", name = "v", descriptor = "I")
	private var anInt3430: Int = 0

	@OriginalMember(owner = "client!fa", name = "w", descriptor = "I")
	private var processorCount: Int = 0

	@OriginalMember(owner = "client!fa", name = "A", descriptor = "I")
	private var anInt3433: Int = 0

	@OriginalMember(owner = "client!fa", name = "B", descriptor = "Ljava/lang/String;")
	private var gpuModel: String? = null

	@OriginalMember(owner = "client!fa", name = "C", descriptor = "I")
	private var javaSubBuild: Int = 0

	@OriginalMember(owner = "client!fa", name = "D", descriptor = "I")
	private var dxDriverMonth: Int = 0

	@OriginalMember(owner = "client!fa", name = "E", descriptor = "Z")
	private var idk: Boolean = false

	@OriginalMember(owner = "client!fa", name = "F", descriptor = "Z")
	private var x64os: Boolean = false

	@OriginalMember(owner = "client!fa", name = "G", descriptor = "I")
	@JvmField
	var ram: Int = 0

	@OriginalMember(owner = "client!fa", name = "H", descriptor = "Ljava/lang/String;")
	private var dwDirectxVersion: String? = null

	@OriginalMember(owner = "client!fa", name = "J", descriptor = "I")
	private var javaUpdate: Int = 0

	@OriginalMember(owner = "client!fa", name = "K", descriptor = "I")
	private var operatingSystemType: Int = 0

	@OriginalMember(owner = "client!fa", name = "L", descriptor = "Ljava/lang/String;")
	private var gpuDriverVersion: String? = null

	@OriginalMember(owner = "client!fa", name = "N", descriptor = "I")
	private var javaBuild: Int = 0

	@OriginalMember(owner = "client!fa", name = "O", descriptor = "I")
	private var dxDriverYear: Int = 0

	@OriginalMember(owner = "client!fa", name = "<init>", descriptor = "()V")
	private constructor() : super() {
	}

	@OriginalMember(owner = "client!fa", name = "<init>", descriptor = "(ZLclient!wea;)V")
	constructor(@OriginalArg(0) arg0: Boolean, @OriginalArg(1) signLink: SignLink?) : super() {
		if (SystemInfo.operatingSystemLowercase!!.startsWith("win")) {
			this.operatingSystemType = 1
		} else if (SystemInfo.operatingSystemLowercase!!.startsWith("mac")) {
			this.operatingSystemType = 2
		} else if (SystemInfo.operatingSystemLowercase!!.startsWith("linux")) {
			this.operatingSystemType = 3
		} else {
			this.operatingSystemType = 4
		}
		if (SystemInfo.OS_ARCH!!.startsWith("amd64") || SystemInfo.OS_ARCH!!.startsWith("x86_64")) {
			this.x64os = true
		} else {
			this.x64os = false
		}
		if (this.operatingSystemType == 1) {
			if (SystemInfo.osVersion!!.indexOf("4.0") != -1) {
				this.osVendor = 1
			} else if (SystemInfo.osVersion!!.indexOf("4.1") != -1) {
				this.osVendor = 2
			} else if (SystemInfo.osVersion!!.indexOf("4.9") != -1) {
				this.osVendor = 3
			} else if (SystemInfo.osVersion!!.indexOf("5.0") != -1) {
				this.osVendor = 4
			} else if (SystemInfo.osVersion!!.indexOf("5.1") != -1) {
				this.osVendor = 5
			} else if (SystemInfo.osVersion!!.indexOf("6.0") != -1) {
				this.osVendor = 6
			} else if (SystemInfo.osVersion!!.indexOf("6.1") != -1) {
				this.osVendor = 7
			}
		} else if (this.operatingSystemType == 2) {
			if (SystemInfo.osVersion!!.indexOf("10.4") != -1) {
				this.osVendor = 20
			} else if (SystemInfo.osVersion!!.indexOf("10.5") != -1) {
				this.osVendor = 21
			} else if (SystemInfo.osVersion!!.indexOf("10.6") != -1) {
				this.osVendor = 22
			}
		}
		if (PlayerUpdate.javaVendor!!.lowercase(Locale.getDefault()).indexOf("sun") != -1) {
			this.javaVersionType = 1
		} else if (PlayerUpdate.javaVendor!!.lowercase(Locale.getDefault()).indexOf("microsoft") != -1) {
			this.javaVersionType = 2
		} else if (PlayerUpdate.javaVendor!!.lowercase(Locale.getDefault()).indexOf("apple") == -1) {
			this.javaVersionType = 4
		} else {
			this.javaVersionType = 3
		}
		@Pc(206) var versionPos: Int = 2
		@Pc(208) var versionPart: Int = 0
		@Pc(214) var ch: Char
		try {
			while (versionPos < SystemInfo.javaVersion!!.length) {
				ch = SystemInfo.javaVersion!![versionPos]
				if (ch < '0' || ch > '9') {
					break
				}
				versionPos++
				versionPart = versionPart * 10 + ch.code - 48
			}
		} catch (@Pc(246) ex: Exception) {
		}
		this.javaBuild = versionPart
		versionPos = SystemInfo.javaVersion!!.indexOf('.', 2) + 1
		versionPart = 0
		try {
			while (SystemInfo.javaVersion!!.length > versionPos) {
				ch = SystemInfo.javaVersion!![versionPos]
				if (ch < '0' || ch > '9') {
					break
				}
				versionPart = versionPart * 10 + ch.code - 48
				versionPos++
			}
		} catch (@Pc(292) ex2: Exception) {
		}
		this.javaSubBuild = versionPart
		versionPart = 0
		versionPos = SystemInfo.javaVersion!!.indexOf('_', 4) + 1
		try {
			while (SystemInfo.javaVersion!!.length > versionPos) {
				ch = SystemInfo.javaVersion!![versionPos]
				if (ch < '0' || ch > '9') {
					break
				}
				versionPart = ch.code + versionPart * 10 - 48
				versionPos++
			}
		} catch (@Pc(332) ex3: Exception) {
		}
		if (signLink!!.signed) {
			this.idk = false
		} else {
			this.idk = true
		}
		this.javaUpdate = versionPart
		this.maxMem = GameShell.maxMemory
		if (this.javaBuild <= 3) {
			this.processorCount = 0
		} else {
			this.processorCount = GameShell.availableProcessors
		}
		try {
			@Pc(363) val cPUInfo: IntArray? = HardwareInfo.getCPUInfo()
			if (cPUInfo != null && cPUInfo.size == 7) {
				this.anInt3430 = cPUInfo[3]
				this.anInt3429 = cPUInfo[4]
				this.cpuClock = cPUInfo[2]
				this.anInt3433 = cPUInfo[5]
				this.ram = cPUInfo[6]
			}
		} catch (@Pc(396) ex4: Throwable) {
			this.ram = 0
		}
		if (this.dwDirectxVersion == null) {
			this.dwDirectxVersion = ""
		}
		if (this.gpuModel == null) {
			this.gpuModel = ""
		}
		if (this.dxLetterVersion == null) {
			this.dxLetterVersion = ""
		}
		if (this.gpuDriverVersion == null) {
			this.gpuDriverVersion = ""
		}
		this.trim()
	}

	@OriginalMember(owner = "client!fa", name = "a", descriptor = "(I)V")
	private fun trim() {
		if (this.gpuModel!!.length > 40) {
			this.gpuModel = this.gpuModel!!.substring(0, 40)
		}
		if (this.gpuDriverVersion!!.length > 40) {
			this.gpuDriverVersion = this.gpuDriverVersion!!.substring(0, 40)
		}
		if (this.dwDirectxVersion!!.length > 10) {
			this.dwDirectxVersion = this.dwDirectxVersion!!.substring(0, 10)
		}
		if (this.dxLetterVersion!!.length > 10) {
			this.dxLetterVersion = this.dxLetterVersion!!.substring(0, 10)
		}
	}

	@OriginalMember(owner = "client!fa", name = "a", descriptor = "(B)I")
	fun size(): Int {
		@Pc(12) val size1: Int = Utils.getJagStringLength(this.gpuModel!!) + 23
		@Pc(19) val size2: Int = size1 + Utils.getJagStringLength(this.gpuDriverVersion!!)
		@Pc(26) val size3: Int = size2 + Utils.getJagStringLength(this.dwDirectxVersion!!)
		return size3 + Utils.getJagStringLength(this.dxLetterVersion!!)
	}

	@OriginalMember(owner = "client!fa", name = "a", descriptor = "(ILclient!cea;)V")
	fun writeMachineInformation(@OriginalArg(1) packet: Packet?) {
		packet!!.writeByte(5)
		packet.writeByte(this.operatingSystemType)
		packet.writeByte(if (this.x64os) 1 else 0)
		packet.writeByte(this.osVendor)
		packet.writeByte(this.javaVersionType)
		packet.writeByte(this.javaBuild)
		packet.writeByte(this.javaSubBuild)
		packet.writeByte(this.javaUpdate)
		packet.writeByte(if (this.idk) 1 else 0)
		packet.writeShort(this.maxMem)
		packet.writeByte(this.processorCount)
		packet.writeMedium(this.ram)
		packet.writeShort(this.cpuClock)
		packet.writeByte(this.anInt3430)
		packet.writeByte(this.anInt3429)
		packet.writeByte(this.anInt3433)
		packet.writeJagString(this.gpuModel)
		packet.writeJagString(this.gpuDriverVersion)
		packet.writeJagString(this.dwDirectxVersion)
		packet.writeJagString(this.dxLetterVersion)
		packet.writeByte(this.dxDriverMonth)
		packet.writeShort(this.dxDriverYear)
	}

	companion object {
		@OriginalMember(owner = "client!gu", name = "H", descriptor = "I")
		@JvmField
		var clientBuild: Int = 0

		@OriginalMember(owner = "client!wea", name = "B", descriptor = "Ljava/lang/String;")
		@JvmField
		var operatingSystemLowercase: String? = null

		@OriginalMember(owner = "client!rn", name = "L", descriptor = "Lclient!fa;")
		@JvmField
		var SYSTEM_INFO: SystemInfo? = null
		@OriginalMember(owner = "client!wea", name = "e", descriptor = "Ljava/lang/String;")
		@JvmField
		var OS_ARCH: String? = null
		@OriginalMember(owner = "client!wea", name = "u", descriptor = "Ljava/lang/String;")
		@JvmField
		var javaVersion: String? = null

		@OriginalMember(owner = "client!wea", name = "c", descriptor = "Ljava/lang/String;")
		@JvmField
		var osVersion: String? = null

		@OriginalMember(owner = "client!ot", name = "a", descriptor = "(Ljava/io/File;ZZ)V")
		@JvmStatic
		fun dumpHeap(@OriginalArg(0) file: File?) {
			if (SystemInfo.hotSpotDiagnosticBean == null) {
				SystemInfo.initHotSpotDiagnosticBean()
			}
			try {
				@Pc(15) val class2: Class<*> = Class.forName("com.sun.management.HotSpotDiagnosticMXBean")
				@Pc(36) val method: Method = class2.getDeclaredMethod("dumpHeap", String::class.java, java.lang.Boolean.TYPE)
				method.invoke(SystemInfo.hotSpotDiagnosticBean, file!!.getAbsolutePath(), java.lang.Boolean.valueOf(false))
			} catch (@Pc(56) ex: Exception) {
				System.out.println("HeapDump error:")
				ex.printStackTrace()
			}
		}

		@OriginalMember(owner = "client!ns", name = "a", descriptor = "(B)V")
		@JvmStatic
		@Synchronized
		fun initHotSpotDiagnosticBean() {
			if (SystemInfo.hotSpotDiagnosticBean != null) {
				return
			}
			try {
				@Pc(9) val class2: Class<*> = Class.forName("java.lang.management.ManagementFactory")
				@Pc(14) val method: Method = class2.getDeclaredMethod("getPlatformMBeanServer", *arrayOfNulls<Class<*>>(0))
				@Pc(19) val `object`: Any? = method.invoke(null, *arrayOfNulls<Any>(0))
				@Pc(56) val method2: Method = class2.getMethod("newPlatformMXBeanProxy", Class.forName("javax.management.MBeanServerConnection"), String::class.java, Class::class.java)
				SystemInfo.hotSpotDiagnosticBean = method2.invoke(null, `object`, "com.sun.management:type=HotSpotDiagnostic", Class.forName("com.sun.management.HotSpotDiagnosticMXBean"))
			} catch (@Pc(77) ex: Exception) {
				System.out.println("HeapDump setup error:")
				ex.printStackTrace()
			}
		}

		@OriginalMember(owner = "client!bn", name = "i", descriptor = "Ljava/lang/Object;")
		@JvmField
		@Volatile
		var hotSpotDiagnosticBean: Any? = null
	}
}

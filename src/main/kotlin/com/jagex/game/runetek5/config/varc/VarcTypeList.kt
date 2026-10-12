package com.jagex.game.runetek5.config.varc

import com.jagex.game.runetek5.config.varc.VarcType
import com.jagex.game.runetek5.client.Game
import com.jagex.game.runetek5.cache.node.lru.LRUCache
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.cache.JFS32
import com.jagex.game.runetek5.sign.SignLinkEnvironment
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.client.GameShell
import com.jagex.game.runetek5.core.util.TimeUtils
import com.jagex.game.runetek5.sign.SignedResource
import com.jagex.game.runetek5.sign.SignedResourceStatus
import com.jagex.game.runetek5.utils.Utils
import java.io.IOException

@OriginalClass("client!uea")
class VarcTypeList {

	@OriginalMember(owner = "client!uea", name = "e", descriptor = "Lclient!jr;")
	private val recentUse: LRUCache = LRUCache(64)

	@OriginalMember(owner = "client!uea", name = "a", descriptor = "Lclient!in;")
	private val configClient: Js5ResourceProvider?

	@OriginalMember(owner = "client!uea", name = "g", descriptor = "I")
	@JvmField
	val num: Int

	@OriginalMember(owner = "client!uea", name = "<init>", descriptor = "(Lclient!sj;ILclient!in;)V")
	constructor(@OriginalArg(0) modeGame: Game?, @OriginalArg(1) languageId: Int, @OriginalArg(2) configClient: Js5ResourceProvider?) {
		this.configClient = configClient
		this.num = this.configClient!!.getGroupCapacity(19)
	}

	@OriginalMember(owner = "client!uea", name = "a", descriptor = "(II)Lclient!pa;")
	fun list(@OriginalArg(0) id: Int): VarcType? {
		@Pc(6) val referenceCache: LRUCache = this.recentUse
		@Pc(16) var varcType: VarcType?
		synchronized(this.recentUse) {
			varcType = this.recentUse.get(id.toLong()) as VarcType?
		}
		if (varcType != null) {
			return varcType
		}
		@Pc(37) val configClient: Js5ResourceProvider? = this.configClient
		@Pc(46) var data: ByteArray?
		synchronized(this.configClient!!) {
			data = this.configClient.getFile(19, id)
		}
		varcType = VarcType()
		if (data != null) {
			varcType!!.decode(Packet(data))
		}
		@Pc(68) val referenceCache2: LRUCache = this.recentUse
		synchronized(this.recentUse) {
			this.recentUse.put(varcType, id.toLong())
			return varcType
		}
	}

	companion object {
		@OriginalMember(owner = "client!tia", name = "m", descriptor = "[Z")
		@JvmField
		var VARC_PERSISTENT: BooleanArray? = null

		@OriginalMember(owner = "client!q", name = "t", descriptor = "[Ljava/lang/String;")
		@JvmField
		var VARC_STRING: Array<String?>? = null

		@OriginalMember(owner = "client!ku", name = "o", descriptor = "[I")
		@JvmField
		var VARC_INT: IntArray? = null

		@OriginalMember(owner = "client!fha", name = "a", descriptor = "(I)V")
		@JvmStatic
		fun savePersistentVarcs() {
			@Pc(7) var fileOnDisk: JFS32? = null
			try {
				fileOnDisk = SignLinkEnvironment.openGamePreferencesFile("2")
				@Pc(20) val packet: Packet = Packet(VarcTypeList.persistentVarcCount * 6 + 3)
				packet.writeByte(1)
				packet.writeShort(VarcTypeList.persistentVarcCount)
				@Pc(38) var i: Int = 0
				while (i < VarcTypeList.VARC_INT!!.size) {
					if (VarcTypeList.VARC_PERSISTENT!![i]) {
						packet.writeShort(i)
						packet.writeInt(VarcTypeList.VARC_INT!![i])
					}
					i++
				}
				fileOnDisk!!.write(0, packet.currentPosition, packet.buffer)
			} catch (@Pc(73) ex: Exception) {
			}
			try {
				if (fileOnDisk != null) {
					fileOnDisk.close()
				}
			} catch (@Pc(80) ex2: Exception) {
			}
			VarcTypeList.exitTime = Utils.currentTimeMillis()
			VarcTypeList.clientIsActive = false
		}

		@OriginalMember(owner = "client!pi", name = "a", descriptor = "(Lclient!cea;B)V")
		@JvmStatic
		fun decodePersistentVarcs(@OriginalArg(0) packet: Packet?) {
			if (packet!!.buffer!!.size - packet.currentPosition < 1) {
				return
			}
			@Pc(20) val version: Int = packet.readUByte()
			if (version < 0 || version > 1 || packet.buffer!!.size - packet.currentPosition < 2) {
				return
			}
			@Pc(55) val count: Int = packet.readUShort()
			if (count * 6 > packet.buffer!!.size - packet.currentPosition) {
				return
			}
			@Pc(69) var i: Int = 0
			while (i < count) {
				@Pc(75) val varcId: Int = packet.readUShort()
				@Pc(79) val value: Int = packet.readInt()
				if (varcId < VarcTypeList.VARC_INT!!.size && VarcTypeList.VARC_PERSISTENT!![varcId] && (TypeLists.aClass338_1!!.list(varcId)!!.typeChar != '1' || value >= -1 && value <= 1)) {
					VarcTypeList.VARC_INT!![varcId] = value
				}
				i++
			}
		}

		@OriginalMember(owner = "client!qea", name = "b", descriptor = "Z")
		@JvmField
		var clientIsActive: Boolean = false

		@OriginalMember(owner = "client!vj", name = "K", descriptor = "I")
		@JvmField
		var varcStrUpdateCycle: Int = 0

		@OriginalMember(owner = "client!vu", name = "s", descriptor = "[I")
		@JvmField
		val recentVarcStrUpdates: IntArray = IntArray(32)

		@OriginalMember(owner = "client!ic", name = "u", descriptor = "J")
		@JvmField
		var exitTime: Long = -1L

		@OriginalMember(owner = "client!nb", name = "a", descriptor = "(I)V")
		@JvmStatic
		fun loadPersistentVarcs() {
			@Pc(12) var fileOnDisk: JFS32? = null
			try {
				@Pc(18) val signedResource: SignedResource = GameShell.signLink!!.openCacheFile("2")
				while (signedResource.status == SignedResourceStatus.IDLE) {
					TimeUtils.sleep(1L)
				}
				if (signedResource.status == SignedResourceStatus.SUCCESS) {
					fileOnDisk = signedResource.result as JFS32?
					@Pc(45) val data: ByteArray = ByteArray(fileOnDisk!!.length().toInt())
					@Pc(60) var bytesRead: Int
					@Pc(47) var i: Int = 0
					while (i < data.size) {
						bytesRead = fileOnDisk.read(data, i, data.size - i)
						if (bytesRead == -1) {
							throw IOException("EOF")
						}
						i += bytesRead
					}
					VarcTypeList.decodePersistentVarcs(Packet(data))
				}
			} catch (@Pc(89) ex: Exception) {
			}
			try {
				if (fileOnDisk != null) {
					fileOnDisk.close()
				}
			} catch (@Pc(96) ex2: Exception) {
			}
		}

		@OriginalMember(owner = "client!ce", name = "I", descriptor = "I")
		@JvmField
		var varcUpdateCycle: Int = 0

		@OriginalMember(owner = "client!ds", name = "c", descriptor = "I")
		@JvmField
		var persistentVarcCount: Int = 0
	}
}

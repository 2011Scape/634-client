package com.jagex.game.runetek5.js5.request

import com.jagex.game.runetek5.buffer.Packet
import java.math.BigInteger
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.cache.Decompressor
import com.jagex.game.runetek5.js5.request.local.LocalRequester
import com.jagex.game.runetek5.js5.request.remote.Js5NetQueueRequest
import com.jagex.game.runetek5.js5.request.remote.RemoteFileWorker
import com.jagex.game.runetek5.js5.Js5WorkerThread
import com.jagex.game.runetek5.js5.Whirlpool

@OriginalClass("client!eb")
class RequestManager {

	@OriginalMember(owner = "client!eb", name = "d", descriptor = "Lclient!cea;")
	private var masterIndexBuffer: Packet? = null

	@OriginalMember(owner = "client!eb", name = "e", descriptor = "[Lclient!bja;")
	private var remoteFileWorkers: Array<RemoteFileWorker?>? = null

	@OriginalMember(owner = "client!eb", name = "f", descriptor = "Lclient!aca;")
	private val localRequester: LocalRequester?

	@OriginalMember(owner = "client!eb", name = "g", descriptor = "Ljava/math/BigInteger;")
	private val updateServerModulus: BigInteger?

	@OriginalMember(owner = "client!eb", name = "b", descriptor = "Ljava/math/BigInteger;")
	private val updateServerExponent: BigInteger?

	@OriginalMember(owner = "client!eb", name = "a", descriptor = "Lclient!tha;")
	private val remoteRequester: Js5WorkerThread?

	@OriginalMember(owner = "client!eb", name = "i", descriptor = "Lclient!mba;")
	private var masterIndexRequest: Js5NetQueueRequest? = null

	@OriginalMember(owner = "client!eb", name = "<init>", descriptor = "(Lclient!tha;Lclient!aca;Ljava/math/BigInteger;Ljava/math/BigInteger;)V")
	constructor(@OriginalArg(0) js5WorkerThread: Js5WorkerThread?, @OriginalArg(1) cachedResourceWorker: LocalRequester?, @OriginalArg(2) bigInteger: BigInteger?, @OriginalArg(3) bigInteger2: BigInteger?) {
		this.localRequester = cachedResourceWorker
		this.updateServerModulus = bigInteger2
		this.updateServerExponent = bigInteger
		this.remoteRequester = js5WorkerThread
		if (!this.remoteRequester!!.isUrgentRequestQueueFull()) {
			this.masterIndexRequest = this.remoteRequester.request(0.toByte(), true, 255, 255)
		}
	}

	@OriginalMember(owner = "client!eb", name = "a", descriptor = "(I)V")
	fun pulse() {
		if (this.remoteFileWorkers == null) {
			return
		}
		@Pc(11) var i: Int = 0
		while (i < this.remoteFileWorkers!!.size) {
			if (this.remoteFileWorkers!![i] != null) {
				this.remoteFileWorkers!![i]!!.processPrefetchQueue()
			}
			i++
		}
		@Pc(36) var j: Int = 0
		while (j < this.remoteFileWorkers!!.size) {
			if (this.remoteFileWorkers!![j] != null) {
				this.remoteFileWorkers!![j]!!.loop()
			}
			j++
		}
	}

	@OriginalMember(owner = "client!eb", name = "a", descriptor = "(Lclient!nw;ILclient!nw;I)Lclient!bja;")
	fun getWorker(@OriginalArg(0) fileSystem_Client: Decompressor?, @OriginalArg(1) archive: Int, @OriginalArg(2) fileSystem_Client2: Decompressor?): RemoteFileWorker? {
		return this.getRemoteFileWorker(archive, fileSystem_Client, fileSystem_Client2)
	}

	@OriginalMember(owner = "client!eb", name = "c", descriptor = "(I)Z")
	fun initialize(): Boolean {
		if (this.masterIndexBuffer != null) {
			return true
		}
		if (this.masterIndexRequest == null) {
			if (this.remoteRequester!!.isUrgentRequestQueueFull()) {
				return false
			}
			this.masterIndexRequest = this.remoteRequester.request(0.toByte(), true, 255, 255)
		}
		if (this.masterIndexRequest!!.waiting) {
			return false
		}
		@Pc(46) val packet: Packet = Packet(this.masterIndexRequest!!.getData())
		packet.currentPosition = 5
		@Pc(55) val archiveCount: Int = packet.readUByte()
		packet.currentPosition += archiveCount * 72
		@Pc(71) val rsaEncrypted: ByteArray = ByteArray(packet.buffer!!.size - packet.currentPosition)
		packet.readBytes(0, rsaEncrypted.size, rsaEncrypted)
		@Pc(105) val rsaDecrypted: ByteArray
		if (this.updateServerExponent == null || this.updateServerModulus == null) {
			rsaDecrypted = rsaEncrypted
		} else {
			@Pc(95) val bigInteger: BigInteger = BigInteger(rsaEncrypted)
			@Pc(102) val bigInteger2: BigInteger = bigInteger.modPow(this.updateServerExponent, this.updateServerModulus)
			rsaDecrypted = bigInteger2.toByteArray()
		}
		if (rsaDecrypted.size != 65) {
			throw RuntimeException()
		}
		@Pc(132) val digest: ByteArray = Whirlpool.digest(5, packet.buffer, packet.currentPosition - rsaEncrypted.size - 5)
		@Pc(134) var i: Int = 0
		while (i < 64) {
			if (rsaDecrypted[i + 1] != digest[i]) {
				throw RuntimeException()
			}
			i++
		}
		this.masterIndexBuffer = packet
		this.remoteFileWorkers = arrayOfNulls<RemoteFileWorker>(archiveCount)
		return true
	}

	@OriginalMember(owner = "client!eb", name = "a", descriptor = "(BZILclient!nw;Lclient!nw;)Lclient!bja;")
	private fun getRemoteFileWorker(@OriginalArg(2) archive: Int, @OriginalArg(3) fileSystem_Client: Decompressor?, @OriginalArg(4) fileSystem_Client2: Decompressor?): RemoteFileWorker? {
		if (this.masterIndexBuffer == null) {
			throw RuntimeException()
		} else if (archive < 0 || archive >= this.remoteFileWorkers!!.size) {
			throw RuntimeException()
		} else if (this.remoteFileWorkers!![archive] == null) {
			this.masterIndexBuffer!!.currentPosition = archive * 72 + 6
			@Pc(47) val tableCrc: Int = this.masterIndexBuffer!!.readInt()
			@Pc(52) val tableRevision: Int = this.masterIndexBuffer!!.readInt()
			@Pc(55) val tableDigest: ByteArray = ByteArray(64)
			this.masterIndexBuffer!!.readBytes(0, 64, tableDigest)
			@Pc(82) val js5ResourceProvider: RemoteFileWorker = RemoteFileWorker(archive, fileSystem_Client, fileSystem_Client2, this.remoteRequester, this.localRequester, tableCrc, tableDigest, tableRevision, true)
			this.remoteFileWorkers!![archive] = js5ResourceProvider
			return js5ResourceProvider
		} else {
			return this.remoteFileWorkers!![archive]
		}
	}

	companion object {
		@OriginalMember(owner = "client!pq", name = "r", descriptor = "Lclient!eb;")
		@JvmField
		var requestManager: RequestManager? = null

		@OriginalMember(owner = "client!et", name = "H", descriptor = "Ljava/math/BigInteger;")
		@JvmField
		var js5Modulus: BigInteger? = BigInteger("d6808be939bbfd2ec4e96b1581ce3e1144b526e7643a72e3c64fbb902724fbfcf14ab601da6d6f8dbb57d1c369d080d9fc392abeb7886e0076d07f2aea5810e540d2817fd1967e35b39cc95cf7c9170b5fb55f5bf95524b60e938f0d64614bc365b87d66963a8cc8664e32875366099ef297180d01c7c3842162865e11d92299", 16)

		@OriginalMember(owner = "client!jca", name = "c", descriptor = "Ljava/math/BigInteger;")
		@JvmField
		var js5RsaExponent: BigInteger? = BigInteger("10001", 16)
	}
}

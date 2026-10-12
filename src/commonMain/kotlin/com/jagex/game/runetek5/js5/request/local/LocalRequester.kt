package com.jagex.game.runetek5.js5.request.local

import com.jagex.game.runetek5.cache.node.Queue
import com.jagex.game.runetek5.ExplicitRuntimeException
import com.jagex.game.runetek5.sign.SignLink
import com.jagex.game.runetek5.sign.SignedResource
import com.jagex.game.runetek5.sign.SignedResourceStatus
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.cache.Decompressor
import com.jagex.game.runetek5.js5.request.Js5ResourceRequest
import com.jagex.game.runetek5.js5.Js5ResourceRequestConstants
import com.jagex.game.runetek5.core.util.TimeUtils
import kotlin.jvm.JvmField
import lang.InterruptedException
import lang.Runnable
import lang.Thread
import lockNotifyAll
import lockWait
import withLock
import canBlock
import runLoop

@OriginalClass("client!aca")
class LocalRequester : Runnable {

	@OriginalMember(owner = "client!aca", name = "e", descriptor = "Lclient!mn;")
	private val queue: Queue = Queue()

	@OriginalMember(owner = "client!aca", name = "m", descriptor = "Z")
	private var shutdown: Boolean = false

	@OriginalMember(owner = "client!aca", name = "i", descriptor = "I")
	@JvmField
	var requestCount: Int = 0

	@OriginalMember(owner = "client!aca", name = "h", descriptor = "Ljava/lang/Thread;")
	private var executingThread: Thread? = null

	@OriginalMember(owner = "client!aca", name = "<init>", descriptor = "(Lclient!wea;)V")
	constructor(@OriginalArg(0) signLink: SignLink?) {
		@Pc(20) val signedResource: SignedResource = signLink!!.startThread(this, 5)
		while (signedResource.status == SignedResourceStatus.IDLE) {
			TimeUtils.sleep(10L)
		}
		if (signedResource.status == SignedResourceStatus.ERROR) {
			throw RuntimeException()
		}
		this.executingThread = signedResource.result as Thread?
	}

	@OriginalMember(owner = "client!aca", name = "a", descriptor = "([BLclient!nw;BI)Lclient!qh;")
	fun createStoreRequest(@OriginalArg(0) data: ByteArray?, @OriginalArg(1) fileSystem_Client: Decompressor?, @OriginalArg(3) indexReference: Int): Js5ResourceRequest {
		@Pc(13) val js5ResourceRequest: Js5ResourceRequest = Js5ResourceRequest()
		js5ResourceRequest.data = data
		js5ResourceRequest.highPriority = false
		js5ResourceRequest.fileSystem = fileSystem_Client
		js5ResourceRequest.indexReference = indexReference.toLong()
		js5ResourceRequest.type = 2
		this.addRequest(js5ResourceRequest)
		return js5ResourceRequest
	}

	@OriginalMember(owner = "client!aca", name = "a", descriptor = "(Lclient!qh;I)V")
	private fun addRequest(@OriginalArg(0) js5ResourceRequest: Js5ResourceRequest) {
		@Pc(6) val queue: Queue = this.queue
		withLock(this.queue) {
			this.queue.insert(js5ResourceRequest)
			this.requestCount++
			lockNotifyAll(this.queue)
		}
	}

	@OriginalMember(owner = "client!aca", name = "a", descriptor = "(Z)V")
	fun close() {
		this.shutdown = true
		@Pc(9) val queue: Queue = this.queue
		withLock(this.queue) {
			lockNotifyAll(this.queue)
		}
		try {
			this.executingThread!!.join()
		} catch (@Pc(25) ignored: InterruptedException) {
		}
		this.executingThread = null
	}

	@OriginalMember(owner = "client!aca", name = "a", descriptor = "(Lclient!nw;IB)Lclient!qh;")
	fun createNormalRequest(@OriginalArg(0) fileSystem_Client: Decompressor?, @OriginalArg(1) indexReference: Int): Js5ResourceRequest {
		@Pc(7) val js5ResourceRequest: Js5ResourceRequest = Js5ResourceRequest()
		js5ResourceRequest.highPriority = false
		js5ResourceRequest.type = 3
		js5ResourceRequest.indexReference = indexReference.toLong()
		js5ResourceRequest.fileSystem = fileSystem_Client
		this.addRequest(js5ResourceRequest)
		return js5ResourceRequest
	}

	@OriginalMember(owner = "client!aca", name = "run", descriptor = "()V")
	override fun run() {
		// Not part of the original client: the loop goes through runLoop (PlatformLoop), the original
		// blocking loop on the JVM and an event-loop chain where there are no threads; runStep is one
		// turn of the original while, which ends its turn instead of waiting where it cannot block.
		runLoop(
			prelude = { true },
			step = { this.runStep() },
			onError = { throw it },
			onFinally = { },
		)
	}

	/* Not part of the original client: one turn of run's loop, false when the loop ends. */
	private fun runStep(): Boolean {
		if (this.shutdown) {
			return false
		}
		@Pc(6) val queue: Queue = this.queue
		@Pc(14) var js5ResourceRequest: Js5ResourceRequest?
		withLock(this.queue) {
			js5ResourceRequest = this.queue.removeFirst() as Js5ResourceRequest?
			if (js5ResourceRequest == null) {
				if (!canBlock) {
					return true
				}
				try {
					lockWait(this.queue)
				} catch (@Pc(29) ex: InterruptedException) {
				}
				return true
			}
			this.requestCount--
		}
		try {
			if (js5ResourceRequest!!.type == Js5ResourceRequestConstants.TYPE_WRITE) {
				js5ResourceRequest!!.fileSystem!!.put(js5ResourceRequest!!.data!!.size, js5ResourceRequest!!.indexReference.toInt(), js5ResourceRequest!!.data)
			} else if (js5ResourceRequest!!.type == Js5ResourceRequestConstants.TYPE_VERIFY) {
				js5ResourceRequest!!.data = js5ResourceRequest!!.fileSystem!!.read(js5ResourceRequest!!.indexReference.toInt())
			}
		} catch (@Pc(76) ex2: Exception) {
			ExplicitRuntimeException.reportError(null, ex2)
		}
		js5ResourceRequest!!.waiting = false
		return true
	}

	@OriginalMember(owner = "client!aca", name = "b", descriptor = "(Lclient!nw;IB)Lclient!qh;")
	fun createPriorityRequest(@OriginalArg(0) fileSystem_Client: Decompressor?, @OriginalArg(1) indexReference: Int): Js5ResourceRequest {
		@Pc(14) val js5ResourceRequest: Js5ResourceRequest = Js5ResourceRequest()
		js5ResourceRequest.type = Js5ResourceRequestConstants.TYPE_READ_URGENT
		@Pc(20) val queue: Queue = this.queue
		withLock(this.queue) {
			@Pc(28) var js5ResourceRequest2: Js5ResourceRequest? = this.queue.first() as Js5ResourceRequest?
			while (true) {
				if (js5ResourceRequest2 == null) {
					break
				}
				if (js5ResourceRequest2.indexReference == indexReference.toLong() && js5ResourceRequest2.fileSystem === fileSystem_Client && js5ResourceRequest2.type == Js5ResourceRequestConstants.TYPE_WRITE) {
					js5ResourceRequest.data = js5ResourceRequest2.data
					js5ResourceRequest.waiting = false
					return js5ResourceRequest
				}
				js5ResourceRequest2 = this.queue.next() as Js5ResourceRequest?
			}
		}
		js5ResourceRequest.data = fileSystem_Client!!.read(indexReference)
		js5ResourceRequest.highPriority = true
		js5ResourceRequest.waiting = false
		return js5ResourceRequest
	}

	companion object {
		@OriginalMember(owner = "client!ifa", name = "g", descriptor = "Lclient!aca;")
		@JvmField
		var aClass5_3: LocalRequester? = null
	}
}

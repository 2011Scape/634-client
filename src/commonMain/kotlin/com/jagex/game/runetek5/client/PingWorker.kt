package com.jagex.game.runetek5.client

import com.jagex.game.runetek5.cache.node.PingRequest
import com.jagex.game.runetek5.core.datastruct.key.Deque
import com.jagex.game.runetek5.cache.node.Node
import jagex3.jagmisc.jagmisc
import net.InetAddress
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField
import lang.InterruptedException
import lang.Runnable
import lang.Thread
import lockNotify
import lockWait
import withLock
import canBlock
import runLoop

@OriginalClass("client!pha")
class PingWorker @OriginalMember(owner = "client!pha", name = "<init>", descriptor = "()V") constructor() : Runnable {

	@OriginalMember(owner = "client!pha", name = "b", descriptor = "Lclient!uh;")
	private val requestQueue: Deque = Deque()

	@OriginalMember(owner = "client!pha", name = "c", descriptor = "Ljava/lang/Thread;")
	private var thread: Thread? = Thread(this)

	init {
		this.thread!!.setDaemon(true)
		this.thread!!.start()
	}

	@OriginalMember(owner = "client!pha", name = "run", descriptor = "()V")
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
		@Pc(8) val deque: Deque = this.requestQueue
		@Pc(38) var pingRequest: PingRequest
		withLock(this.requestQueue) {
			@Pc(15) var node: Node?
			node = this.requestQueue.removeFirst()
			while (node == null) {
				if (!canBlock) {
					return true
				}
				try {
					lockWait(this.requestQueue)
				} catch (@Pc(22) ex: InterruptedException) {
				}
				node = this.requestQueue.removeFirst()
			}
			if (node !is PingRequest) {
				return false
			}
			pingRequest = node
		}
		@Pc(64) var pingTime: Int
		try {
			@Pc(49) val address: ByteArray = net.getByName(pingRequest.address).getAddress()
			pingTime = jagmisc.ping(address[0], address[1], address[2], address[3], 1000L)
		} catch (@Pc(66) ex2: Throwable) {
			pingTime = 1000
		}
		pingRequest.ping = pingTime
		return true
	}

	@OriginalMember(owner = "client!pha", name = "a", descriptor = "(ILjava/lang/String;)Lclient!mj;")
	fun requestPing(@OriginalArg(1) string: String?): PingRequest? {
		if (this.thread == null) {
			throw IllegalStateException("")
		} else if (string == null) {
			throw IllegalArgumentException("")
		} else {
			@Pc(29) val pingRequest: PingRequest = PingRequest(string)
			this.enqueue(pingRequest)
			return pingRequest
		}
	}

	@OriginalMember(owner = "client!pha", name = "b", descriptor = "(B)V")
	fun close() {
		if (this.thread == null) {
			return
		}
		this.enqueue(Node())
		try {
			this.thread!!.join()
		} catch (@Pc(18) ignored: InterruptedException) {
		}
		this.thread = null
	}

	@OriginalMember(owner = "client!pha", name = "a", descriptor = "(ILclient!eh;)V")
	private fun enqueue(@OriginalArg(1) node: Node?) {
		@Pc(8) val deque: Deque = this.requestQueue
		withLock(this.requestQueue) {
			this.requestQueue.addLast(node)
			lockNotify(this.requestQueue)
		}
	}

	companion object {
		@OriginalMember(owner = "client!kba", name = "x", descriptor = "Lclient!mj;")
		@JvmField
		var aClass2_Sub36_1: PingRequest? = null
		@OriginalMember(owner = "client!ks", name = "i", descriptor = "Lclient!pha;")
		@JvmField
		var aClass262_1: PingWorker? = PingWorker()
	}
}

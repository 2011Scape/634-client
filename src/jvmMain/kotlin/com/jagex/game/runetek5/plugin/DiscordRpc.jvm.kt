package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.DiscordActivity
import com.jagex.game.runetek5.client.features.DiscordJson
import java.io.Closeable
import java.io.FileNotFoundException
import java.io.IOException
import java.io.RandomAccessFile
import java.net.StandardProtocolFamily
import java.net.UnixDomainSocketAddress
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.SocketChannel
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicReference

/*
 * Not part of the original client: the JVM side of DiscordRpc, a client of Discord's local RPC
 * socket written on java.nio alone (no native discord-rpc, no JNA). The wire format is Discord's:
 * frames of a little-endian opcode and length then JSON; opcode 0 is the handshake, 1 a command
 * or an event, 2 a close, 3 a ping answered with a 4. The thread connects to the first
 * `discord-ipc-N` that answers, sends the handshake, waits for the READY event, then sends every
 * activity set until the socket drops, and tries again after a wait that doubles up to a minute,
 * as the discord-rpc library did. The pid in the command is this JVM's; the nonce a counter.
 */
actual object DiscordRpc {

	private const val OP_HANDSHAKE: Int = 0

	private const val OP_FRAME: Int = 1

	private const val OP_CLOSE: Int = 2

	private const val OP_PING: Int = 3

	private const val OP_PONG: Int = 4

	private const val HEADER_SIZE: Int = 8

	private const val MAX_FRAME_SIZE: Int = 64 * 1024

	private const val MIN_BACKOFF_MILLIS: Long = 500

	private const val MAX_BACKOFF_MILLIS: Long = 60_000

	private const val HANDSHAKE_TIMEOUT_MILLIS: Long = 5000

	private const val POLL_MILLIS: Long = 50

	private const val SLEEP_STEP_MILLIS: Long = 100

	private const val SOCKET_COUNT: Int = 10

	private const val STOP_JOIN_MILLIS: Long = 2000

	/** Marks "nothing new to send" in [pending], since null there means "clear the presence". */
	private val nothing: Any = Any()

	private val pending: AtomicReference<Any?> = AtomicReference(nothing)

	/** What the plugin last set; sent again by each new connection. */
	@Volatile
	private var current: DiscordActivity? = null

	@Volatile
	private var worker: Thread? = null

	@Volatile
	private var running: Boolean = false

	@Volatile
	private var connected: Boolean = false

	@Volatile
	private var statusLine: String = "not started"

	@Volatile
	private var lastMessage: String? = null

	private var applicationId: String = ""

	private var listener: DiscordRpcListener? = null

	/** The last activity Discord confirmed, for [status]. */
	@Volatile
	private var lastAccepted: String? = null

	/** Thread state: the command counter. */
	private var nonce: Long = 0

	actual fun start(applicationId: String, listener: DiscordRpcListener) {
		synchronized(this) {
			val old: Thread? = worker
			if (old != null && old.isAlive) {
				if (running) {
					return
				}
				old.join(STOP_JOIN_MILLIS) // the thread of a stop() a moment ago, clearing the presence
			}
			this.applicationId = applicationId
			this.listener = listener
			running = true
			connected = false
			lastMessage = null
			statusLine = "connecting"
			val thread: Thread = Thread({ run() }, "discord-rpc")
			thread.isDaemon = true
			worker = thread
			thread.start()
		}
	}

	actual fun setActivity(activity: DiscordActivity?) {
		current = activity
		pending.set(activity)
	}

	actual fun stop() {
		synchronized(this) {
			running = false
			worker = null
			statusLine = "stopped"
		}
	}

	actual fun isConnected(): Boolean {
		return connected
	}

	actual fun status(): String {
		val accepted: String? = lastAccepted
		return statusLine + (if (accepted != null) "; last accepted: " + accepted else "")
	}

	/** Tells the listener [message], unless it is what was last said: a Discord that is not running is said once, not every minute. */
	private fun say(message: String) {
		if (message == lastMessage) {
			return
		}
		lastMessage = message
		listener?.event(message)
	}

	@Suppress("TooGenericExceptionCaught") // whatever the connection throws, the thread ends quietly and the plugin keeps running
	private fun run() {
		var backoff: Long = MIN_BACKOFF_MILLIS
		try {
			while (running) {
				val connection: Connection? = open()
				if (connection == null) {
					statusLine = "Discord not found; trying again"
					say("Discord is not running (no RPC socket found); will keep trying")
					sleepWhileRunning(backoff)
					backoff = minOf(backoff * 2, MAX_BACKOFF_MILLIS)
					continue
				}
				val handshook: Boolean = session(connection)
				if (handshook) {
					backoff = MIN_BACKOFF_MILLIS
				}
				if (running) {
					sleepWhileRunning(backoff)
					backoff = minOf(backoff * 2, MAX_BACKOFF_MILLIS)
				}
			}
		} catch (error: Throwable) {
			statusLine = "failed: " + error
			say("Discord connection thread failed: " + error.stackTraceToString())
		} finally {
			connected = false
		}
	}

	/** One connection: the handshake, then the activities, until Discord or [stop] ends it. True when the handshake got through. */
	private fun session(connection: Connection): Boolean {
		var handshook: Boolean = false
		try {
			connection.use {
				statusLine = "handshake with " + connection.name
				handshook = handshake(connection)
				if (!handshook) {
					return false
				}
				pending.set(current)
				while (running) {
					val next: Any? = pending.getAndSet(nothing)
					if (next !== nothing) {
						connection.write(frame(OP_FRAME, DiscordJson.setActivity(ProcessHandle.current().pid(), (++nonce).toString(), next as DiscordActivity?)))
					}
					if (!connection.readFrames(false)) {
						Thread.sleep(POLL_MILLIS)
					}
				}
				if (current != null) {
					connection.write(frame(OP_FRAME, DiscordJson.setActivity(ProcessHandle.current().pid(), (++nonce).toString(), null)))
				}
			}
		} catch (exception: IOException) {
			statusLine = "disconnected: " + exception.message
			say("Discord connection lost: " + exception.message)
		} finally {
			connected = false
		}
		return handshook
	}

	/** Sends the handshake and waits for READY; false when Discord refused, closed, or kept quiet. */
	private fun handshake(connection: Connection): Boolean {
		connection.write(frame(OP_HANDSHAKE, DiscordJson.handshake(applicationId)))
		val deadline: Long = System.currentTimeMillis() + HANDSHAKE_TIMEOUT_MILLIS
		while (running && !connected) {
			if (!connection.readFrames(true) && System.currentTimeMillis() > deadline) {
				statusLine = "no answer to the handshake from " + connection.name
				say("Discord did not answer the handshake")
				return false
			}
			if (!connected) {
				Thread.sleep(POLL_MILLIS)
			}
		}
		return connected
	}

	/** A frame from Discord. */
	private fun received(connection: Connection, opcode: Int, payload: String) {
		when (opcode) {
			OP_PING -> connection.write(frame(OP_PONG, payload))
			OP_CLOSE -> throw IOException("closed by Discord: " + payload)
			OP_FRAME -> event(connection, payload)
			else -> throw IOException("unexpected opcode " + opcode)
		}
	}

	private fun event(connection: Connection, payload: String) {
		val message: Any? = DiscordJson.parse(payload)
		val evt: String? = DiscordJson.string(message, "evt")
		val data: Map<*, *>? = DiscordJson.obj(message, "data")
		when {
			evt == "READY" -> {
				val username: String = DiscordJson.string(DiscordJson.obj(data, "user"), "username") ?: "?"
				connected = true
				statusLine = "connected as " + username + " via " + connection.name
				say("connected to Discord as " + username)
			}
			evt == "ERROR" -> {
				val text: String = (DiscordJson.string(data, "message") ?: "?") + " (code " + (DiscordJson.long(data, "code") ?: 0) + ")"
				statusLine = "refused: " + text
				say("Discord refused: " + text)
				if (!connected) {
					throw IOException("refused: " + text)
				}
			}
			DiscordJson.string(message, "cmd") == "SET_ACTIVITY" && DiscordJson.string(message, "nonce") != null -> {
				val details: String? = DiscordJson.string(data, "details")
				val state: String? = DiscordJson.string(data, "state")
				val accepted: String = if (data == null) "cleared" else (details ?: "") + " / " + (state ?: "")
				lastAccepted = accepted
				say("presence accepted: " + accepted)
			}
		}
	}

	private fun frame(opcode: Int, payload: String): ByteArray {
		val body: ByteArray = payload.encodeToByteArray()
		val buffer: ByteBuffer = ByteBuffer.allocate(HEADER_SIZE + body.size).order(ByteOrder.LITTLE_ENDIAN)
		buffer.putInt(opcode)
		buffer.putInt(body.size)
		buffer.put(body)
		return buffer.array()
	}

	private fun sleepWhileRunning(millis: Long) {
		var left: Long = millis
		while (running && left > 0) {
			val step: Long = minOf(left, SLEEP_STEP_MILLIS)
			Thread.sleep(step)
			left -= step
		}
	}

	/** The first socket that takes a connection, or null when none does. */
	private fun open(): Connection? {
		val windows: Boolean = System.getProperty("os.name", "").lowercase().contains("windows")
		for (i in 0 until SOCKET_COUNT) {
			val name: String = "discord-ipc-" + i
			if (windows) {
				val pipe: Connection? = openPipe("\\\\.\\pipe\\" + name)
				if (pipe != null) {
					return pipe
				}
				continue
			}
			for (dir in socketFolders()) {
				val path: Path = Paths.get(dir, name)
				if (Files.exists(path)) {
					val socket: Connection? = openSocket(path)
					if (socket != null) {
						return socket
					}
				}
			}
		}
		return null
	}

	/** Where Discord makes its socket: the session's runtime folder, the sandboxed app's folders under it, then the temp folder. */
	private fun socketFolders(): List<String> {
		val folders: ArrayList<String> = ArrayList()
		val runtime: String? = System.getenv("XDG_RUNTIME_DIR") ?: runUserFolder()
		if (runtime != null) {
			folders.add(runtime)
			folders.add(runtime + "/snap.discord")
			folders.add(runtime + "/app/com.discordapp.Discord")
			folders.add(runtime + "/app/com.discordapp.DiscordCanary")
		}
		for (variable in arrayOf("TMPDIR", "TMP", "TEMP")) {
			val value: String? = System.getenv(variable)
			if (value != null && !folders.contains(value)) {
				folders.add(value)
			}
		}
		folders.add("/tmp")
		return folders
	}

	@Suppress("SwallowedException") // no uid to be had: the folder is just not tried
	private fun runUserFolder(): String? {
		if (!FileSystems.getDefault().supportedFileAttributeViews().contains("unix")) {
			return null
		}
		try {
			val uid: Any? = Files.getAttribute(Paths.get(System.getProperty("user.home")), "unix:uid")
			return if (uid is Int) "/run/user/" + uid else null
		} catch (exception: IOException) {
			return null
		}
	}

	@Suppress("SwallowedException") // a socket nobody listens on: the next one is tried
	private fun openSocket(path: Path): Connection? {
		try {
			val channel: SocketChannel = SocketChannel.open(StandardProtocolFamily.UNIX)
			channel.connect(UnixDomainSocketAddress.of(path))
			channel.configureBlocking(false)
			return SocketConnection(path.toString(), channel)
		} catch (exception: IOException) {
			return null
		}
	}

	@Suppress("SwallowedException") // no such pipe: the next one is tried
	private fun openPipe(path: String): Connection? {
		try {
			return PipeConnection(path, RandomAccessFile(path, "rw"))
		} catch (exception: FileNotFoundException) {
			return null
		}
	}

	/** A socket to Discord: whole frames out, frames in parsed as they arrive. */
	private abstract class Connection(@JvmField val name: String) : Closeable {

		private val input: ByteArray = ByteArray(HEADER_SIZE + MAX_FRAME_SIZE)

		private var filled: Int = 0

		abstract fun write(bytes: ByteArray)

		/** Reads what has arrived into [buffer] from [offset]: the count, 0 for nothing yet, -1 at the end. */
		protected abstract fun read(buffer: ByteArray, offset: Int, length: Int): Int

		/** Handles every whole frame that has arrived; true when any had. [handshaking] is only for the status. */
		fun readFrames(handshaking: Boolean): Boolean {
			val count: Int = read(input, filled, input.size - filled)
			if (count < 0) {
				throw IOException(if (handshaking) "closed during the handshake" else "closed")
			}
			if (count == 0) {
				return false
			}
			filled += count
			var any: Boolean = false
			while (filled >= HEADER_SIZE) {
				val opcode: Int = littleEndian(input, 0)
				val length: Int = littleEndian(input, 4)
				if (length < 0 || length > MAX_FRAME_SIZE) {
					throw IOException("bad frame length " + length)
				}
				if (filled < HEADER_SIZE + length) {
					break
				}
				val payload: String = input.decodeToString(HEADER_SIZE, HEADER_SIZE + length)
				val rest: Int = filled - HEADER_SIZE - length
				input.copyInto(input, 0, HEADER_SIZE + length, filled)
				filled = rest
				any = true
				received(this, opcode, payload)
			}
			return any
		}

		private fun littleEndian(bytes: ByteArray, at: Int): Int {
			return (bytes[at].toInt() and 0xFF) or ((bytes[at + 1].toInt() and 0xFF) shl 8) or ((bytes[at + 2].toInt() and 0xFF) shl 16) or ((bytes[at + 3].toInt() and 0xFF) shl 24)
		}
	}

	/** Linux and macOS: a unix domain socket, read without blocking. */
	private class SocketConnection(name: String, private val channel: SocketChannel) : Connection(name) {

		override fun write(bytes: ByteArray) {
			val buffer: ByteBuffer = ByteBuffer.wrap(bytes)
			while (buffer.hasRemaining()) {
				if (channel.write(buffer) == 0) {
					Thread.sleep(1)
				}
			}
		}

		override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
			return channel.read(ByteBuffer.wrap(buffer, offset, length))
		}

		override fun close() {
			channel.close()
		}
	}

	/** Windows: a named pipe; its reads block, so a thread of its own does them and queues what came. */
	private class PipeConnection(name: String, private val file: RandomAccessFile) : Connection(name) {

		private val chunks: LinkedBlockingQueue<ByteArray> = LinkedBlockingQueue()

		private var chunk: ByteArray? = null

		private var chunkAt: Int = 0

		@Volatile
		private var closed: Boolean = false

		init {
			val reader: Thread = Thread({ pump() }, "discord-rpc-reader")
			reader.isDaemon = true
			reader.start()
		}

		@Suppress("SwallowedException") // the pipe ended or broke: an empty chunk tells the reader
		private fun pump() {
			val buffer: ByteArray = ByteArray(MAX_FRAME_SIZE)
			try {
				while (!closed) {
					val count: Int = file.read(buffer)
					if (count < 0) {
						break
					}
					if (count > 0) {
						chunks.add(buffer.copyOf(count))
					}
				}
			} catch (exception: IOException) {
				// fallthrough to the end marker
			}
			chunks.add(ByteArray(0))
		}

		override fun write(bytes: ByteArray) {
			file.write(bytes)
		}

		override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
			var current: ByteArray? = chunk
			if (current == null) {
				current = chunks.poll() ?: return 0
				if (current.isEmpty()) {
					chunks.add(current) // keep saying "ended"
					return -1
				}
				chunk = current
				chunkAt = 0
			}
			val count: Int = minOf(length, current.size - chunkAt)
			current.copyInto(buffer, offset, chunkAt, chunkAt + count)
			chunkAt += count
			if (chunkAt >= current.size) {
				chunk = null
			}
			return count
		}

		override fun close() {
			closed = true
			file.close()
		}
	}
}

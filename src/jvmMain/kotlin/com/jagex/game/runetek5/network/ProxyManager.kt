// The warnings of the original (deobfuscated) code, left as it was: the build treats warnings as errors.
@file:Suppress("DEPRECATION")

package com.jagex.game.runetek5.network

import com.jagex.game.runetek5.core.constants.Ports
import com.jagex.game.runetek5.network.SocketManager
import com.jagex.game.runetek5.core.io.socket.ProxyAuthenticationException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStream
import java.lang.reflect.Method
import java.net.InetSocketAddress
import java.net.Proxy.Type
import java.net.Proxy
import java.net.ProxySelector
import java.net.Socket
import java.net.SocketAddress
import java.net.URI
import java.net.URISyntaxException
import java.net.URL
import java.nio.charset.Charset
import java.util.Locale
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!ip")
class ProxyManager : SocketManager() {

	@OriginalMember(owner = "client!ip", name = "h", descriptor = "Ljava/net/ProxySelector;")
	private val proxySelector: ProxySelector? = ProxySelector.getDefault()

	@OriginalMember(owner = "client!ip", name = "httpProxyConnect", descriptor = "(Ljava/lang/String;ILjava/lang/String;)Ljava/net/Socket;")
	@Throws(IOException::class)
	private fun httpProxyConnect(@OriginalArg(0) string: String?, @OriginalArg(1) port: Int, @OriginalArg(2) string2: String?): Socket? {
		@Pc(5) val socket: Socket = Socket(string, port)
		socket.setSoTimeout(10000)
		@Pc(11) val outputStream: OutputStream = socket.getOutputStream()
		if (string2 == null) {
			outputStream.write(("CONNECT " + this.siteHostName + ":" + this.sitePort + " HTTP/1.0\n\n").toByteArray(Charset.forName("ISO-8859-1")))
		} else {
			outputStream.write(("CONNECT " + this.siteHostName + ":" + this.sitePort + " HTTP/1.0\n" + string2 + "\n\n").toByteArray(Charset.forName("ISO-8859-1")))
		}
		outputStream.flush()
		@Pc(71) val bufferedReader: BufferedReader = BufferedReader(InputStreamReader(socket.getInputStream()))
		@Pc(74) var string3: String? = bufferedReader.readLine()
		if (string3 != null) {
			label55@ do {
				if (!string3!!.startsWith("HTTP/1.0 200") && !string3.startsWith("HTTP/1.1 200")) {
					if (!string3.startsWith("HTTP/1.0 407") && !string3.startsWith("HTTP/1.1 407")) {
						break@label55
					}
					@Pc(98) var lineCount: Int = 0
					string3 = bufferedReader.readLine()
					@Pc(103) val string4: String = "proxy-authenticate: "
					while (string3 != null && lineCount < 50) {
						if (string3.lowercase(Locale.getDefault()).startsWith(string4)) {
							string3 = string3.substring(string4.length).trim { it <= ' ' }
							@Pc(125) val spaceIndex: Int = string3.indexOf(32.toChar())
							if (spaceIndex != -1) {
								string3 = string3.substring(0, spaceIndex)
							}
							throw ProxyAuthenticationException(string3)
						}
						lineCount++
						string3 = bufferedReader.readLine()
					}
					throw ProxyAuthenticationException("")
				}
				return socket
			} while (false)
		}
		outputStream.close()
		bufferedReader.close()
		socket.close()
		return null
	}

	@OriginalMember(owner = "client!ip", name = "c", descriptor = "(I)Ljava/net/Socket;")
	@Throws(IOException::class)
	override fun open(): Socket? {
		@Pc(3) val useSystemProxies: Boolean = java.lang.Boolean.parseBoolean(System.getProperty("java.net.useSystemProxies"))
		if (!useSystemProxies) {
			System.setProperty("java.net.useSystemProxies", "true")
		}
		@Pc(20) val secureConnection: Boolean = this.sitePort == Ports.HTTPS
		@Pc(48) val list: MutableList<Proxy>
		@Pc(70) val list2: MutableList<Proxy>
		try {
			list = this.proxySelector!!.select(URI((if (secureConnection) "https" else "http") + "://" + this.siteHostName))
			list2 = this.proxySelector.select(URI((if (secureConnection) "http" else "https") + "://" + this.siteHostName))
		} catch (@Pc(72) ex: URISyntaxException) {
			return this.createSocket()
		}
		list.addAll(list2)
		@Pc(83) val objects: Array<Any?> = list.toTypedArray<Any?>()
		@Pc(85) var proxyAuthenticationException: ProxyAuthenticationException? = null
		@Pc(89) var i: Int = 0
		while (i < objects.size) {
			@Pc(97) val obj: Any? = objects[i]
			@Pc(100) val proxy: Proxy? = obj as Proxy?
			try {
				@Pc(105) val socket: Socket? = this.getProxySocket(proxy)
				if (socket != null) {
					return socket
				}
			} catch (@Pc(111) ex2: ProxyAuthenticationException) {
				proxyAuthenticationException = ex2
			} catch (@Pc(115) ex3: IOException) {
			}
			i++
		}
		if (proxyAuthenticationException != null) {
			throw proxyAuthenticationException
		}
		return this.createSocket()
	}

	@OriginalMember(owner = "client!ip", name = "a", descriptor = "(Ljava/net/Proxy;B)Ljava/net/Socket;")
	@Throws(IOException::class)
	private fun getProxySocket(@OriginalArg(0) proxy: Proxy?): Socket? {
		if (proxy!!.type() == Type.DIRECT) {
			return this.createSocket()
		}
		@Pc(10) val socketAddress: SocketAddress? = proxy.address()
		if (socketAddress !is InetSocketAddress) {
			return null
		}
		@Pc(18) val inetSocketAddress: InetSocketAddress = socketAddress
		if (proxy.type() == Type.HTTP) {
			@Pc(51) var string: String? = null
			try {
				@Pc(54) val class2: Class<*> = Class.forName("sun.net.www.protocol.http.AuthenticationInfo")
				@Pc(75) val method: Method = class2.getDeclaredMethod("getProxyAuth", String::class.java, Integer.TYPE)
				method.setAccessible(true)
				@Pc(97) val obj: Any? = method.invoke(null, inetSocketAddress.getHostName(), Integer.valueOf(inetSocketAddress.getPort()))
				if (obj != null) {
					@Pc(107) val supportsPreemptiveAuth: Method = class2.getDeclaredMethod("supportsPreemptiveAuthorization")
					supportsPreemptiveAuth.setAccessible(true)
					if (supportsPreemptiveAuth.invoke(obj) as Boolean) {
						@Pc(124) val getHeaderName: Method = class2.getDeclaredMethod("getHeaderName")
						getHeaderName.setAccessible(true)
						@Pc(155) val getHeaderValue: Method = class2.getDeclaredMethod("getHeaderValue", URL::class.java, String::class.java)
						getHeaderValue.setAccessible(true)
						@Pc(165) val string2: String? = getHeaderName.invoke(obj) as String?
						@Pc(193) val string3: String? = getHeaderValue.invoke(obj, URL("https://" + this.siteHostName + "/"), "https") as String?
						string = string2 + ": " + string3
					}
				}
			} catch (@Pc(206) ex: Exception) {
			}
			return this.httpProxyConnect(inetSocketAddress.getHostName(), inetSocketAddress.getPort(), string)
		} else if (proxy.type() == Type.SOCKS) {
			@Pc(38) val socket: Socket = Socket(proxy)
			socket.connect(InetSocketAddress(this.siteHostName, this.sitePort))
			return socket
		} else {
			return null
		}
	}
}

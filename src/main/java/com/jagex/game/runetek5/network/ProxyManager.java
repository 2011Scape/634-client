package com.jagex.game.runetek5.network;

import com.jagex.game.runetek5.core.constants.Ports;
import com.jagex.game.runetek5.network.SocketManager;
import com.jagex.game.runetek5.core.io.socket.ProxyAuthenticationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Proxy.Type;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!ip")
public final class ProxyManager extends SocketManager {

	@OriginalMember(owner = "client!ip", name = "h", descriptor = "Ljava/net/ProxySelector;")
	private final ProxySelector proxySelector = ProxySelector.getDefault();

	@OriginalMember(owner = "client!ip", name = "httpProxyConnect", descriptor = "(Ljava/lang/String;ILjava/lang/String;)Ljava/net/Socket;")
	private Socket httpProxyConnect(@OriginalArg(0) String string, @OriginalArg(1) int port, @OriginalArg(2) String string2) throws IOException {
		@Pc(5) Socket socket = new Socket(string, port);
		socket.setSoTimeout(10000);
		@Pc(11) OutputStream outputStream = socket.getOutputStream();
		if (string2 == null) {
			outputStream.write(("CONNECT " + this.siteHostName + ":" + this.sitePort + " HTTP/1.0\n\n").getBytes(Charset.forName("ISO-8859-1")));
		} else {
			outputStream.write(("CONNECT " + this.siteHostName + ":" + this.sitePort + " HTTP/1.0\n" + string2 + "\n\n").getBytes(Charset.forName("ISO-8859-1")));
		}
		outputStream.flush();
		@Pc(71) BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		@Pc(74) String string3 = bufferedReader.readLine();
		if (string3 != null) {
			label55: {
				if (!string3.startsWith("HTTP/1.0 200") && !string3.startsWith("HTTP/1.1 200")) {
					if (!string3.startsWith("HTTP/1.0 407") && !string3.startsWith("HTTP/1.1 407")) {
						break label55;
					}
					@Pc(98) int lineCount = 0;
					string3 = bufferedReader.readLine();
					@Pc(103) String string4 = "proxy-authenticate: ";
					while (string3 != null && lineCount < 50) {
						if (string3.toLowerCase().startsWith(string4)) {
							string3 = string3.substring(string4.length()).trim();
							@Pc(125) int spaceIndex = string3.indexOf(32);
							if (spaceIndex != -1) {
								string3 = string3.substring(0, spaceIndex);
							}
							throw new ProxyAuthenticationException(string3);
						}
						lineCount++;
						string3 = bufferedReader.readLine();
					}
					throw new ProxyAuthenticationException("");
				}
				return socket;
			}
		}
		outputStream.close();
		bufferedReader.close();
		socket.close();
		return null;
	}

	@OriginalMember(owner = "client!ip", name = "c", descriptor = "(I)Ljava/net/Socket;")
	@Override
	public Socket open() throws IOException {
		@Pc(3) boolean useSystemProxies = Boolean.parseBoolean(System.getProperty("java.net.useSystemProxies"));
		if (!useSystemProxies) {
			System.setProperty("java.net.useSystemProxies", "true");
		}
		@Pc(20) boolean secureConnection = this.sitePort == Ports.HTTPS;
		@Pc(48) List list;
		@Pc(70) List list2;
		try {
			list = this.proxySelector.select(new URI((secureConnection ? "https" : "http") + "://" + this.siteHostName));
			list2 = this.proxySelector.select(new URI((secureConnection ? "http" : "https") + "://" + this.siteHostName));
		} catch (@Pc(72) URISyntaxException ex) {
			return this.createSocket();
		}
		list.addAll(list2);
		@Pc(83) Object[] objects = list.toArray();
		@Pc(85) ProxyAuthenticationException proxyAuthenticationException = null;
		for (@Pc(89) int i = 0; i < objects.length; i++) {
			@Pc(97) Object object = objects[i];
			@Pc(100) Proxy proxy = (Proxy) object;
			try {
				@Pc(105) Socket socket = this.getProxySocket(proxy);
				if (socket != null) {
					return socket;
				}
			} catch (@Pc(111) ProxyAuthenticationException ex2) {
				proxyAuthenticationException = ex2;
			} catch (@Pc(115) IOException ex3) {
			}
		}
		if (proxyAuthenticationException != null) {
			throw proxyAuthenticationException;
		}
		return this.createSocket();
	}

	@OriginalMember(owner = "client!ip", name = "a", descriptor = "(Ljava/net/Proxy;B)Ljava/net/Socket;")
	private Socket getProxySocket(@OriginalArg(0) Proxy proxy) throws IOException {
		if (proxy.type() == Type.DIRECT) {
			return this.createSocket();
		}
		@Pc(10) SocketAddress socketAddress = proxy.address();
		if (!(socketAddress instanceof InetSocketAddress)) {
			return null;
		}
		@Pc(18) InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
		if (proxy.type() == Type.HTTP) {
			@Pc(51) String string = null;
			try {
				@Pc(54) Class class2 = Class.forName("sun.net.www.protocol.http.AuthenticationInfo");
				@Pc(75) Method method = class2.getDeclaredMethod("getProxyAuth", String.class, Integer.TYPE);
				method.setAccessible(true);
				@Pc(97) Object object = method.invoke(null, inetSocketAddress.getHostName(), Integer.valueOf(inetSocketAddress.getPort()));
				if (object != null) {
					@Pc(107) Method supportsPreemptiveAuth = class2.getDeclaredMethod("supportsPreemptiveAuthorization");
					supportsPreemptiveAuth.setAccessible(true);
					if ((Boolean) supportsPreemptiveAuth.invoke(object)) {
						@Pc(124) Method getHeaderName = class2.getDeclaredMethod("getHeaderName");
						getHeaderName.setAccessible(true);
						@Pc(155) Method getHeaderValue = class2.getDeclaredMethod("getHeaderValue", URL.class, String.class);
						getHeaderValue.setAccessible(true);
						@Pc(165) String string2 = (String) getHeaderName.invoke(object);
						@Pc(193) String string3 = (String) getHeaderValue.invoke(object, new URL("https://" + this.siteHostName + "/"), "https");
						string = string2 + ": " + string3;
					}
				}
			} catch (@Pc(206) Exception ex) {
			}
			return this.httpProxyConnect(inetSocketAddress.getHostName(), inetSocketAddress.getPort(), string);
		} else if (proxy.type() == Type.SOCKS) {
			@Pc(38) Socket socket = new Socket(proxy);
			socket.connect(new InetSocketAddress(this.siteHostName, this.sitePort));
			return socket;
		} else {
			return null;
		}
	}
}

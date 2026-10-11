package com.jagex.game.runetek5.client;

import com.jagex.game.runetek5.cache.node.PingRequest;
import com.jagex.game.runetek5.core.datastruct.key.Deque;
import com.jagex.game.runetek5.cache.node.Node;
import jagex3.jagmisc.jagmisc;
import java.net.InetAddress;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!pha")
public final class PingWorker implements Runnable {

	@OriginalMember(owner = "client!pha", name = "b", descriptor = "Lclient!uh;")
	private final Deque requestQueue = new Deque();

	@OriginalMember(owner = "client!pha", name = "c", descriptor = "Ljava/lang/Thread;")
	private Thread thread = new Thread(this);

	@OriginalMember(owner = "client!pha", name = "<init>", descriptor = "()V")
	public PingWorker() {
		this.thread.setDaemon(true);
		this.thread.start();
	}

	@OriginalMember(owner = "client!pha", name = "run", descriptor = "()V")
	@Override
	public void run() {
		while (true) {
			@Pc(8) Deque deque = this.requestQueue;
			@Pc(38) PingRequest pingRequest;
			synchronized (this.requestQueue) {
				@Pc(15) Node node;
				for (node = this.requestQueue.removeFirst(); node == null; node = this.requestQueue.removeFirst()) {
					try {
						this.requestQueue.wait();
					} catch (@Pc(22) InterruptedException ex) {
					}
				}
				if (!(node instanceof PingRequest)) {
					return;
				}
				pingRequest = (PingRequest) node;
			}
			@Pc(64) int pingTime;
			try {
				@Pc(49) byte[] address = InetAddress.getByName(pingRequest.address).getAddress();
				pingTime = jagmisc.ping(address[0], address[1], address[2], address[3], 1000L);
			} catch (@Pc(66) Throwable ex2) {
				pingTime = 1000;
			}
			pingRequest.ping = pingTime;
		}
	}

	@OriginalMember(owner = "client!pha", name = "a", descriptor = "(ILjava/lang/String;)Lclient!mj;")
	public PingRequest requestPing(@OriginalArg(1) String string) {
		if (this.thread == null) {
			throw new IllegalStateException("");
		} else if (string == null) {
			throw new IllegalArgumentException("");
		} else {
			@Pc(29) PingRequest pingRequest = new PingRequest(string);
			this.enqueue(pingRequest);
			return pingRequest;
		}
	}

	@OriginalMember(owner = "client!pha", name = "b", descriptor = "(B)V")
	public void close() {
		if (this.thread == null) {
			return;
		}
		this.enqueue(new Node());
		try {
			this.thread.join();
		} catch (@Pc(18) InterruptedException ignored) {
		}
		this.thread = null;
	}

	@OriginalMember(owner = "client!pha", name = "a", descriptor = "(ILclient!eh;)V")
	private void enqueue(@OriginalArg(1) Node node) {
		@Pc(8) Deque deque = this.requestQueue;
		synchronized (this.requestQueue) {
			this.requestQueue.addLast(node);
			this.requestQueue.notify();
		}
	}
	@OriginalMember(owner = "client!kba", name = "x", descriptor = "Lclient!mj;")
	public static PingRequest aClass2_Sub36_1;
	@OriginalMember(owner = "client!ks", name = "i", descriptor = "Lclient!pha;")
	public static PingWorker aClass262_1 = new PingWorker();
}

package com.jagex.game.runetek5.js5.request.local;

import com.jagex.game.runetek5.cache.node.Queue;
import com.jagex.game.runetek5.ExplicitRuntimeException;
import com.jagex.game.runetek5.sign.SignLink;
import com.jagex.game.runetek5.sign.SignedResource;
import com.jagex.game.runetek5.sign.SignedResourceStatus;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.cache.Decompressor;
import com.jagex.game.runetek5.js5.request.Js5ResourceRequest;
import com.jagex.game.runetek5.js5.Js5ResourceRequestConstants;
import com.jagex.game.runetek5.core.util.TimeUtils;

@OriginalClass("client!aca")
public final class LocalRequester implements Runnable {

	@OriginalMember(owner = "client!aca", name = "e", descriptor = "Lclient!mn;")
	private final Queue queue = new Queue();

	@OriginalMember(owner = "client!aca", name = "m", descriptor = "Z")
	private boolean shutdown = false;

	@OriginalMember(owner = "client!aca", name = "i", descriptor = "I")
	public int requestCount = 0;

	@OriginalMember(owner = "client!aca", name = "h", descriptor = "Ljava/lang/Thread;")
	private Thread executingThread;

	@OriginalMember(owner = "client!aca", name = "<init>", descriptor = "(Lclient!wea;)V")
	public LocalRequester(@OriginalArg(0) SignLink signLink) {
		@Pc(20) SignedResource signedResource = signLink.startThread(this, 5);
		while (signedResource.status == SignedResourceStatus.IDLE) {
			TimeUtils.sleep(10L);
		}
		if (signedResource.status == SignedResourceStatus.ERROR) {
			throw new RuntimeException();
		}
		this.executingThread = (Thread) signedResource.result;
	}

	@OriginalMember(owner = "client!aca", name = "a", descriptor = "([BLclient!nw;BI)Lclient!qh;")
	public Js5ResourceRequest createStoreRequest(@OriginalArg(0) byte[] data, @OriginalArg(1) Decompressor fileSystem_Client, @OriginalArg(3) int indexReference) {
		@Pc(13) Js5ResourceRequest js5ResourceRequest = new Js5ResourceRequest();
		js5ResourceRequest.data = data;
		js5ResourceRequest.highPriority = false;
		js5ResourceRequest.fileSystem = fileSystem_Client;
		js5ResourceRequest.indexReference = indexReference;
		js5ResourceRequest.type = 2;
		this.addRequest(js5ResourceRequest);
		return js5ResourceRequest;
	}

	@OriginalMember(owner = "client!aca", name = "a", descriptor = "(Lclient!qh;I)V")
	private void addRequest(@OriginalArg(0) Js5ResourceRequest js5ResourceRequest) {
		@Pc(6) Queue queue = this.queue;
		synchronized (this.queue) {
			this.queue.insert(js5ResourceRequest);
			this.requestCount++;
			this.queue.notifyAll();
		}
	}

	@OriginalMember(owner = "client!aca", name = "a", descriptor = "(Z)V")
	public void close() {
		this.shutdown = true;
		@Pc(9) Queue queue = this.queue;
		synchronized (this.queue) {
			this.queue.notifyAll();
		}
		try {
			this.executingThread.join();
		} catch (@Pc(25) InterruptedException ignored) {
		}
		this.executingThread = null;
	}

	@OriginalMember(owner = "client!aca", name = "a", descriptor = "(Lclient!nw;IB)Lclient!qh;")
	public Js5ResourceRequest createNormalRequest(@OriginalArg(0) Decompressor fileSystem_Client, @OriginalArg(1) int indexReference) {
		@Pc(7) Js5ResourceRequest js5ResourceRequest = new Js5ResourceRequest();
		js5ResourceRequest.highPriority = false;
		js5ResourceRequest.type = 3;
		js5ResourceRequest.indexReference = indexReference;
		js5ResourceRequest.fileSystem = fileSystem_Client;
		this.addRequest(js5ResourceRequest);
		return js5ResourceRequest;
	}

	@OriginalMember(owner = "client!aca", name = "run", descriptor = "()V")
	@Override
	public void run() {
		while (!this.shutdown) {
			@Pc(6) Queue queue = this.queue;
			@Pc(14) Js5ResourceRequest js5ResourceRequest;
			synchronized (this.queue) {
				js5ResourceRequest = (Js5ResourceRequest) this.queue.removeFirst();
				if (js5ResourceRequest == null) {
					try {
						this.queue.wait();
					} catch (@Pc(29) InterruptedException ex) {
					}
					continue;
				}
				this.requestCount--;
			}
			try {
				if (js5ResourceRequest.type == Js5ResourceRequestConstants.TYPE_WRITE) {
					js5ResourceRequest.fileSystem.put(js5ResourceRequest.data.length, (int) js5ResourceRequest.indexReference, js5ResourceRequest.data);
				} else if (js5ResourceRequest.type == Js5ResourceRequestConstants.TYPE_VERIFY) {
					js5ResourceRequest.data = js5ResourceRequest.fileSystem.read((int) js5ResourceRequest.indexReference);
				}
			} catch (@Pc(76) Exception ex2) {
				ExplicitRuntimeException.reportError(null, ex2);
			}
			js5ResourceRequest.waiting = false;
		}
	}

	@OriginalMember(owner = "client!aca", name = "b", descriptor = "(Lclient!nw;IB)Lclient!qh;")
	public Js5ResourceRequest createPriorityRequest(@OriginalArg(0) Decompressor fileSystem_Client, @OriginalArg(1) int indexReference) {
		@Pc(14) Js5ResourceRequest js5ResourceRequest = new Js5ResourceRequest();
		js5ResourceRequest.type = Js5ResourceRequestConstants.TYPE_READ_URGENT;
		@Pc(20) Queue queue = this.queue;
		synchronized (this.queue) {
			@Pc(28) Js5ResourceRequest js5ResourceRequest2 = (Js5ResourceRequest) this.queue.first();
			while (true) {
				if (js5ResourceRequest2 == null) {
					break;
				}
				if (js5ResourceRequest2.indexReference == (long) indexReference && js5ResourceRequest2.fileSystem == fileSystem_Client && js5ResourceRequest2.type == Js5ResourceRequestConstants.TYPE_WRITE) {
					js5ResourceRequest.data = js5ResourceRequest2.data;
					js5ResourceRequest.waiting = false;
					return js5ResourceRequest;
				}
				js5ResourceRequest2 = (Js5ResourceRequest) this.queue.next();
			}
		}
		js5ResourceRequest.data = fileSystem_Client.read(indexReference);
		js5ResourceRequest.highPriority = true;
		js5ResourceRequest.waiting = false;
		return js5ResourceRequest;
	}
	@OriginalMember(owner = "client!ifa", name = "g", descriptor = "Lclient!aca;")
	public static LocalRequester aClass5_3;
}

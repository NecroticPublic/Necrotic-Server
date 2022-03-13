package com.ruse.mvh;

public final class Vote {
	private final String ip;
	private final long timestamp;
	private final boolean fulfilled;
	private final boolean ready;
	private final String auth;
	private final boolean redeemed;
	
	public Vote(String auth, String ip, long timestamp, boolean ready, boolean fulfilled, boolean redeemed) {
		this.auth = auth;
		this.ip = ip;
		this.timestamp = timestamp;
		this.ready = ready;
		this.fulfilled = fulfilled;
		this.redeemed = redeemed;
	}
	
	public boolean redeemed() {
		return redeemed;
	}
	
	public boolean ready() {
		return ready;
	}
	
	public String address() {
		return ip;
	}
	
	public String auth() {
		return auth;
	}
	
	public long callbackDate() {
		return timestamp;
	}
	
	public boolean fulfilled() {
		return fulfilled;
	}
}

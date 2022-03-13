package com.ruse.mvh;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class AuthService {
	protected final ExecutorService executor = Executors.newCachedThreadPool();
	
	public abstract void shutdown();
	public abstract Future<Vote> info(String authcode);
	public abstract Future<Boolean> redeem(String authcode);
	
	public Future<Boolean> redeem(final Vote vote) {
		return redeem(vote.auth());
	}
	
	public Vote infoNow(String authcode) {
		try {
			return info(authcode).get();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public boolean redeemNow(Vote vote) {
		try {
			return redeem(vote).get();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public boolean redeemNow(String authcode) {
		try {
			return redeem(authcode).get();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	// allowing for static referencing if people are so inclined to do it this way
	private static AuthService provider;
	
	public static void setProvider(AuthService provider) {
		AuthService.provider = provider;
	}
	
	public static AuthService provider() {
		return provider;
	}
}

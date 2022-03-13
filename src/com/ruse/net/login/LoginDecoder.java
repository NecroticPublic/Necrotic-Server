package com.ruse.net.login;

import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.security.SecureRandom;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import com.ruse.GameSettings;
import com.ruse.net.PlayerSession;
import com.ruse.net.packet.PacketBuilder;
import com.ruse.net.packet.codec.PacketDecoder;
import com.ruse.net.packet.codec.PacketEncoder;
import com.ruse.net.security.IsaacRandom;
import com.ruse.util.Misc;
import com.ruse.util.NameUtils;
import com.ruse.world.World;
import com.ruse.world.entity.impl.player.Player;

/**
 * A {@link org.niobe.net.StatefulFrameDecoder} which decodes
 * the login requests.
 *
 * @author Gabriel Hannason
 */
public final class LoginDecoder extends FrameDecoder {
	
	private static final int CONNECTED = 0;
	private static final int LOGGING_IN = 1;
	private int state = CONNECTED;
	private long seed;

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {
		if(!channel.isConnected()) {
			return null;
		}
		switch (state) {
		case CONNECTED:
			if (buffer.readableBytes() < 1) {
				return null;
			}
			int request = buffer.readUnsignedByte();
			if (request != 14) {
				System.out.println("Invalid login request: " + request);
				channel.close();
				return null;
			}
			seed = new SecureRandom().nextLong();
			channel.write(new PacketBuilder().put((byte) 0).putLong(seed).toPacket());
			state = LOGGING_IN;
			return null;
		case LOGGING_IN:
			if (buffer.readableBytes() < 2) {
				System.out.println("no readable bytes");
				return null;
			}
			int loginType = buffer.readByte();
			if (loginType != 16 && loginType != 18) {
				System.out.println("Invalid login type: " + loginType);
				channel.close();
				return null;
			}			
			int blockLength = buffer.readByte() & 0xff;
			if (buffer.readableBytes() < blockLength) {
				channel.close();
				return null;
			}
			int magicId = buffer.readUnsignedByte();
			if(magicId != 0xFF) {
				System.out.println("Invalid magic id! magicId: "+magicId);
				channel.close();
				return null;
			}
			int clientVersion = buffer.readShort();
			int memory =  buffer.readByte();
			if (memory != 0 && memory != 1) {
				System.out.println("Unhandled memory byte value");
				channel.close();
				return null;
			}
			/*int[] archiveCrcs = new int[9];
			for (int i = 0; i < 9; i++) {
				archiveCrcs[i] = buffer.readInt();
			}*/
			int length = buffer.readUnsignedByte();
			/**
			 * Our RSA components. 
			 */
			ChannelBuffer rsaBuffer = buffer.readBytes(length);
			BigInteger bigInteger = new BigInteger(rsaBuffer.array());
			bigInteger = bigInteger.modPow(GameSettings.RSA_EXPONENT, GameSettings.RSA_MODULUS);
			rsaBuffer = ChannelBuffers.wrappedBuffer(bigInteger.toByteArray());
			
			int securityId = rsaBuffer.readByte();
			if(securityId != 10) {
				System.out.println("securityId id is not 10. It is "+securityId);
				channel.close();
				return null;
			}
			long clientSeed = rsaBuffer.readLong();
			long seedReceived = rsaBuffer.readLong();
			if (seedReceived != seed) {
				System.out.println("Unhandled seed read: [seed, seedReceived] : [" + seed + ", " + seedReceived + "");
				channel.close();
				return null;
			}
			int[] seed = new int[4];
			seed[0] = (int) (clientSeed >> 32);
			seed[1] = (int) clientSeed;
			seed[2] = (int) (this.seed >> 32);
			seed[3] = (int) this.seed;
			IsaacRandom decodingRandom = new IsaacRandom(seed);
			for (int i = 0; i < seed.length; i++) {
				seed[i] += 50;
			}
			int uid = rsaBuffer.readInt();
			String username = Misc.readString(rsaBuffer);
			String password = Misc.readString(rsaBuffer);
			String mac = Misc.readString(rsaBuffer);
			String uuid = Misc.readString(rsaBuffer);
			//String serial = Misc.readString(rsaBuffer);
			if (username.length() > 12 || password.length() > 20) {
				System.out.println("Username or password length too long");
				return null;
			}
			username = Misc.formatText(username.toLowerCase());
			channel.getPipeline().replace("encoder", "encoder", new PacketEncoder(new IsaacRandom(seed)));
			channel.getPipeline().replace("decoder", "decoder", new PacketDecoder(decodingRandom));
			return login(channel, new LoginDetailsMessage(username, password, ((InetSocketAddress) channel.getRemoteAddress()).getAddress().getHostAddress(), mac, uuid, clientVersion, uid));
		}
		return null;
	}

	public Player login(Channel channel, LoginDetailsMessage msg) {
		
		PlayerSession session = new PlayerSession(channel);
		
		Player player = new Player(session).setUsername(msg.getUsername())
		.setLongUsername(NameUtils.stringToLong(msg.getUsername()))
		.setPassword(msg.getPassword())
		.setHostAddress(msg.getHost())
		.setMac(msg.getMac())
		.setUUID(msg.getUUID());

		session.setPlayer(player);
		
		int response = LoginResponses.getResponse(player, msg);

		final boolean newAccount = response == LoginResponses.NEW_ACCOUNT;
		if(newAccount) {
			player.setNewPlayer(true);
			response = LoginResponses.LOGIN_SUCCESSFUL;
		}
		
		//System.out.println(msg.getMac());
		//System.out.println(msg.getUUID());
		
		if (response == LoginResponses.LOGIN_SUCCESSFUL) {
			channel.write(new PacketBuilder().put((byte)2).put((byte)player.getRights().ordinal()).put((byte)0).toPacket());
			
			if(!World.getLoginQueue().contains(player)) {
				World.getLoginQueue().add(player);
			}
			
			return player;
		} else {
			sendReturnCode(channel, response);
			return null;
		}
	}

	public static void sendReturnCode(final Channel channel, final int code) {
		channel.write(new PacketBuilder().put((byte) code).toPacket()).addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(final ChannelFuture arg0) throws Exception {
				arg0.getChannel().close();
			}
		});
	}

}

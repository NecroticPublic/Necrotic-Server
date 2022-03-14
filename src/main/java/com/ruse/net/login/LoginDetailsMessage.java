package com.ruse.net.login;

import java.nio.channels.Channel;

import org.jboss.netty.channel.ChannelHandlerContext;

import com.ruse.net.packet.Packet;

/**
 * The {@link Packet} implementation that contains data used for the final
 * portion of the login protocol.
 * 
 * @author lare96 <http://github.org/lare96>
 */
public final class LoginDetailsMessage {

    /**
     * The username of the player.
     */
    private final String username;

    /**
     * The password of the player.
     */
    private final String password;
    
    /**
     * The player's host address
     */
    private final String host;
    
    
    /**
     * The player's client version.
     */
    private final int clientVersion;

    /**
     * The player's client uid.
     */
    private final int uid;
    
    private final String mac;
    
    private final String uuid;
    
    /**
     * Creates a new {@link LoginDetailsMessage}.
     *
     * @param ctx
     *            the {@link ChannelHandlerContext} that holds our
     *            {@link Channel} instance.
     * @param username
     *            the username of the player.
     * @param password
     *            the password of the player.
     * @param encryptor
     *            the encryptor for encrypting messages.
     * @param decryptor
     *            the decryptor for decrypting messages.
     */
    public LoginDetailsMessage(String username, String password, String host, String mac, String uuid, int clientVersion, int uid) {
        this.username = username;
        this.password = password;
        this.host = host;
        this.mac = mac;
        this.uuid = uuid;
        this.clientVersion = clientVersion;
        this.uid = uid;
    }

    /**
     * Gets the username of the player.
     * 
     * @return the username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the password of the player.
     * 
     * @return the password.
     */
    public String getPassword() {
        return password;
    }
    
    /**
     * Gets the password of the player.
     * 
     * @return the password.
     */
    public String getHost() {
    	return host;
    }
    
    
    /**
     * Gets the player's client version.
     * 
     * @return the client version.
     */
    public int getClientVersion() {
    	return clientVersion;
    }
    
    /**
     * Gets the player's client uid.
     * @return the client's uid.
     */
    public int getUid() {
    	return uid;
    }
    
    public String getMac() {
    	return mac;
    }
    
    public String getUUID() {
    	return uuid;
    }
}

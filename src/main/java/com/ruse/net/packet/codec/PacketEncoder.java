package com.ruse.net.packet.codec;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import com.ruse.net.packet.Packet;
import com.ruse.net.packet.Packet.PacketType;
import com.ruse.net.security.IsaacRandom;

/**
 * An implementation of netty's {@link OneToOneEncoder} to
 * encode outgoing packets.
 *
 * @author relex lawl
 */
public final class PacketEncoder extends OneToOneEncoder {
	
	/**
	 * The GamePacketEncoder constructor.
	 * @param encoder	The encoder used for the packets.
	 */
	public PacketEncoder(IsaacRandom encoder) {
		this.encoder = encoder;
	}
	
	/**
	 * The encoder used for incoming packets.
	 */
	private final IsaacRandom encoder;

	@Override
	protected Object encode(ChannelHandlerContext context, Channel channel,
			Object message) throws Exception {
		Packet packet = (Packet) message;
		PacketType packetType = packet.getType();
		int headerLength = 1;
		int packetLength = packet.getSize();
		if (packet.getOpcode() == -1) {
			return packet.getBuffer();
		}
		if (packetType == PacketType.BYTE) {
			headerLength += 1;
			if (packetLength >= 256) {
				throw new Exception("Packet length is too long for a sized packet.");
			}
		} else if (packetType == PacketType.SHORT) {
			headerLength += 2;
			if (packetLength >= 65536) {
				throw new Exception("Packet length is too long for a short packet.");
			}
		}
		ChannelBuffer buffer = ChannelBuffers.buffer(headerLength + packetLength);
		buffer.writeByte((packet.getOpcode() + encoder.nextInt()) & 0xFF);
		if (packetType == PacketType.BYTE) {
			buffer.writeByte(packetLength);
		} else if (packetType == PacketType.SHORT) {
			buffer.writeShort(packetLength);
		}
		buffer.writeBytes(packet.getBuffer());
		return buffer;
	}

}

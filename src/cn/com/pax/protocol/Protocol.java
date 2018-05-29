package cn.com.pax.protocol;

//stx cmd datalen data etx lrc
public class Protocol {
	public final static byte BYTE_STX = 0x02;
	public final static byte BYTE_ETX = 0x03;
	public enum ProtocolStatus
	{
		PROTOCOL_ERRNO_OK,
		PROTOCOL_ERRNO_TIMEOUT,
		PROTOCOL_ERRNO_LRC,
	};
	
	public static byte[] compositeProcotol(byte cmd, byte[] data, int len) {
		byte[] buffer = new byte[6 + len];
		buffer[0] = (byte)BYTE_STX;
		buffer[1] = cmd;
		buffer[2] = (byte)(len >> 8 & 0xff);
		buffer[3] = (byte)(len & 0xff);
		for(int i=0; i<len; i++)
			buffer[4+i] = data[i];
		buffer[4+len] = (byte)BYTE_ETX;
		byte lrc = calcLRC(buffer, (byte)0xff, 5 + len);
		buffer[5+len] = lrc;
		return buffer;
	}
	
	public static byte calcLRC(byte[] bytes, byte lrc, int len) {
		for(int i = 0; i < len; i++) {
			lrc ^= bytes[i];
		}
		return lrc;
	}
	
	public static void main(String[] args) throws Exception {
		SocketToC4 c4 = new SocketToC4("192.168.200.103", 8888);
		c4.openConn();
		String string = "code39_25mil_4bytes.png";
		byte[] sendBuf;
		//mathod(c4,string);
		
		
		Thread.sleep(2000);
		
		
		sendBuf = Protocol.compositeProcotol((byte)0x15, null, 0);
		
		c4.writeReadOneLine(sendBuf);
		
		Thread.sleep(2000);
		
		c4.closeConn();
				
	}

	public static byte[] sendCmd2D220(SocketToC4 c4,String string) {
		byte []receive = new byte [50];
		if (string == null ) {
			byte[] sendBuf = Protocol.compositeProcotol((byte)0x15, null, 0);
			receive = c4.writeReadOneLine(sendBuf);
		}else{
			byte[] sendBuf = Protocol.compositeProcotol((byte)0x16, string.getBytes(), string.length());
			receive = c4.writeReadOneLine(sendBuf);
		}
		return receive;
	}

	public static byte[] sendCmd2D220(SocketToC4 c4,byte data) {
		byte[] b1 = {data};
		byte[] sendBuf = Protocol.compositeProcotol((byte)0x17, b1, b1.length);
		byte[] receive = c4.writeReadOneLine(sendBuf);
		return receive;
	}
}

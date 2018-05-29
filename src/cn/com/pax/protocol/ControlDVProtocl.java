package cn.com.pax.protocol;

import cn.com.pax.sericomm.SerialConnection;



public class ControlDVProtocl {
	
	private static SerialConnection connection = null;
	
	public static void setConnection(SerialConnection connection) {
		ControlDVProtocl.connection = connection;
	}
	
	//AA 55 02 F3 00 F5  AA 55 04 F6 v0 v1 XX XX
	public static boolean readProtocol(byte[] tt) {
		
		byte[] readBytes = new byte[2];
		int readNums = 0;
		
		while(true) {
			readNums = connection.serialRead(readBytes, 100);
			if(readNums == 2 && readBytes[0] == ((byte)0xAA) && readBytes[1] == 0x55) {
				break;
			}
			if(readNums <= 0)
				return false;
		}
		
		readNums = connection.serialRead(readBytes, 100);
		if(!(readNums == 2 && readBytes[0] == 0x04 && readBytes[1] == ((byte)0xF6))) {
			return false;
		}
		readNums = connection.serialRead(readBytes, 100);
		if(readNums == 2) {
			tt[0] = readBytes[0];
			tt[1] = readBytes[1];
			return true;
		}
		return false;	
	}	
}

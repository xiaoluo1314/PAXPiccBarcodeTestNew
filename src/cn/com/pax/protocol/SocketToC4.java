package cn.com.pax.protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.com.pax.display.mainFrame;
import cn.com.pax.protocol.Protocol.ProtocolStatus;

public class SocketToC4 {
	private String host;
	private int port;
	
	public SocketToC4(String host, int port) {
		this.host = host;
		this.port = port;
		this.connFlag = false;
		sock = null;
	}

	@Override
	public String toString() {
		return "SocketToC4{" +
				"host='" + host + '\'' +
				", port=" + port +
				", connFlag=" + connFlag +
				'}';
	}

	public boolean openConn1() {
		return true;
	}
	public boolean openConn() {
		try {
			connFlag = false;
			sock = new Socket(host, port);
			connFlag = true;
			return true;
		}
		catch(UnknownHostException e) {
			e.printStackTrace();
			return false;
		}
		catch(ConnectException e) {
			e.printStackTrace();
			return false;
		}
		catch(IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void closeConn() {
		if(sock != null) {
			try {
				connFlag = false;
				sock.close();
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
	}
	
	public String writeReadOneLine(String xString) {
		int cnt = 3;
		
//		if(!connFlag) {
//			System.out.println("[writeReadOneLine String] Please first connect socket!");
//			return "error connect";
//		}
		
		while(cnt > 0) {
			try {
				OutputStream outputStream = sock.getOutputStream();
				PrintWriter writer = new PrintWriter(outputStream, true);
				writer.println(xString);
				InputStream inputStream = sock.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
				String result = reader.readLine();
				//System.out.println("result: " + result);
				if(result == null) {
					result = "error null";
				}
				return result;
				//return "OK";
			}
			catch(SocketException e) {
				e.printStackTrace();
				openConn();
				System.out.println("cnt:" + cnt);
				cnt--;
			}
			catch(IOException e) {
				e.printStackTrace();
				return "error readwrite";
			} 
			catch(Throwable e) {
				e.printStackTrace();
				return "other error readwrite";
			}
		}
		if(cnt <= 0) {
			System.out.println("retry 3 times failed! Network problem!");
		}
		return "error retry";
	}
	
	public String writeReadOneLineForLog(String xString) {
		String result ="";
		try {
			OutputStream outputStream = sock.getOutputStream();
			PrintWriter writer = new PrintWriter(outputStream, true);
			writer.println(xString);
			InputStream inputStream = sock.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			result = reader.readLine();
			if(result == null) {
				result = "error null";
			}
			//return "OK";
		}
		catch(SocketException e) {
			e.printStackTrace();
			result =  "error " + e.getMessage();
		}
		catch(IOException e) {
			e.printStackTrace();
			result =  "error readwrite";
		} 
		catch(Throwable e) {
			e.printStackTrace();
			result =  "other error readwrite";
		}
		mainFrame.logger.info("cmd: " + xString + " resp: " + result);
		return result;
	}
	
	public  byte[] writeReadOneLine(byte[] xPos) {
		int cnt = 3;
		
//		if(!connFlag) {
//			System.out.println("[writeReadOneLine byte[]] Please first connect socket!");
//			return null;
//		}
		
		while(cnt > 0) {
			try {
				OutputStream outputStream = sock.getOutputStream();
				outputStream.write(xPos);
				outputStream.flush();
				InputStream inputStream = sock.getInputStream();
				byte[] retCode = new byte[1];
				
				if(protocol_read_frame(inputStream, retCode) != ProtocolStatus.PROTOCOL_ERRNO_OK) {
					//System.out.println(protocol_read_frame(inputStream, retCode));
					retCode[0] = 0x03;
				} 
				return retCode;
			}
			catch(SocketException e) {
				e.printStackTrace();
				openConn();
				cnt--;
			}
			catch(IOException e) {
				e.printStackTrace();
				return null;
			}
			catch(Throwable e) {
				e.printStackTrace();
				return null;
			}
		}
		if(cnt <= 0) {
			System.out.println("[writeReadOneLine] retry 3 times failed! Network problem!");
		}
		return null;
	}
	
	public  ProtocolStatus protocol_read_frame(InputStream inputStream, byte[] errCode) throws IOException {
		byte[] readBytes;
		byte lrc = (byte) 0xff;
		int tryCnt = 0;
		int ret = 0;
		int timeOut = 300;

		while(true) {
			ret = inputStream.read();
			if ((byte)ret == Protocol.BYTE_STX){
				break;
			} else {
				tryCnt++;
				if (tryCnt > timeOut/100){
					return ProtocolStatus.PROTOCOL_ERRNO_TIMEOUT;
				}	
			}
		}
		lrc ^= (byte)ret;
		
		//cmd
		ret = inputStream.read();
		lrc ^= (byte)ret;
		
		//datalen
		readBytes = new byte[2];
		ret = inputStream.read(readBytes);
		if (ret != 2 ){
			return ProtocolStatus.PROTOCOL_ERRNO_TIMEOUT;
		}

		try {
			lrc ^= readBytes[0];
			lrc ^= readBytes[1];
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return ProtocolStatus.PROTOCOL_ERRNO_TIMEOUT;
		}
		
		//data
		int len = 0;
		int dataLen = ((readBytes[0] & 0x0ff)<<8) | (readBytes[1] & 0x0ff);
		tryCnt = 0;
		readBytes = new byte[dataLen];
		while(len < dataLen) {
			ret = inputStream.read(readBytes, len, dataLen);
			if(ret > 0) {
				lrc = Protocol.calcLRC(readBytes, lrc, ret);
				len += ret;
			} else {
				tryCnt++;
				if(tryCnt > 2 + dataLen/100){
					return ProtocolStatus.PROTOCOL_ERRNO_TIMEOUT;
				}
			}
		}
		
		if(len != dataLen){
			return ProtocolStatus.PROTOCOL_ERRNO_TIMEOUT;
		}
		
		// etx
		ret = inputStream.read();
		//readBytes = connection.serialReadAutoTime(1, 100);
		if ((byte)ret != Protocol.BYTE_ETX){
			return ProtocolStatus.PROTOCOL_ERRNO_TIMEOUT;
		}
			
		lrc ^= (byte)ret;
		
		//lrc
		ret = inputStream.read();
		if((byte)ret != lrc){
			return ProtocolStatus.PROTOCOL_ERRNO_LRC;
		}
		
		//协议正确，获取返回码
		errCode[0] = readBytes[0];
		
		return ProtocolStatus.PROTOCOL_ERRNO_OK;
	}
	
	public String readOneLine() {
		int cnt = 3;
		while(cnt > 0) {
			try {
				InputStream inputStream = sock.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
				String result = reader.readLine();
				if(result == null) {
					result = "error null";
				}
				return result;
			}
			catch(SocketException e) {
				e.printStackTrace();
				openConn();
				System.out.println("cnt:" + cnt);
				cnt--;
			}
			catch(IOException e) {
				e.printStackTrace();
				return "error read";
			} 
			catch(Throwable e) {
				e.printStackTrace();
				return "other error read";
				}
			}
			if(cnt <= 0) {
				System.out.println("retry 3 times failed! Network problem!");
			}
			return "error retry";
		
	}	
	
	public boolean writeOneLine(String data) {
		try {
			OutputStream outputStream = sock.getOutputStream();
			PrintWriter writer = new PrintWriter(outputStream, true);
			writer.println(data);
			return true;
		}
		catch(IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	
	public static void main(String[] args) {
//		SocketToC4 socket = new SocketToC4("127.0.0.1", 6000);
//		socket.openConn();
//		//socket.writeOneLine("123");
//		//String string = socket.readOneLine();
//		String string = socket.writeReadOneLine("123");
//		System.out.println(string);
		 Pattern pattern = Pattern.compile("Suc.*?(\\d+)\\s*Los.*?(\\d+)", Pattern.DOTALL);
		 
		 Matcher matcher = pattern.matcher("Suc:0 Los:54");
		if( matcher.find() ){
			 System.out.println(matcher.group(1) + " " + matcher.group(2));
		 }
	}
	
	private Socket sock;
	private boolean connFlag;

	public boolean isConnFlag() {
		return connFlag;
	}

	public void setConnFlag(boolean connFlag) {
		this.connFlag = connFlag;
	}
}

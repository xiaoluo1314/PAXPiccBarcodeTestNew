package cn.com.pax.sericomm;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.List;

import dk.thibaut.serial.SerialChannel;
import dk.thibaut.serial.SerialPort;

public class SerialConnection  {

	private SerialParameters parameters;
	private SerialChannel channel;
	private OutputStream outputStream;

	private  SerialPort sPort;

	private boolean open;

	public SerialConnection(SerialParameters parameters) {
		this.parameters = parameters;
		open = false;
	}

	public void openConnection() {
		try {
			sPort = SerialPort.open(parameters.getPortName());
			setConnectionParameters(parameters);
			channel = sPort.getChannel();
			outputStream = sPort.getOutputStream();
			open = true;
		} catch (IOException e) {
			e.printStackTrace();
			open = false;
		}	
	}

	public static List<String> getCommInfo() {
		try {
			List<String>comList = SerialPort.getAvailablePortsNames();
			return comList;
		} catch (Exception e) {
			return null;
		}	
	}
	public void setConnectionParameters(SerialParameters parameters)  {
		try {
		
			sPort.setConfig(parameters.getBaudRate(), parameters.getParity(), parameters.getStopbits(), parameters.getDatabits());
			this.parameters = parameters;	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public SerialParameters getParameters() {
		return parameters;
	}

	public void closeConnection() {

		if (!open) {
			return;
		}

		if (sPort != null) {
			try {
				sPort.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		open = false;
	}

	public boolean isOpen() {
		return open;
	}

	public void enableReadTimeout(int timeout){
		try {
			sPort.setTimeout(timeout);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void clearSendBeforeData(){
		try {
			channel.flush(true, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void readExtraData(int timeoutMs)
	{
		int num=0;
		ByteBuffer buffer = ByteBuffer.allocate(16);
		enableReadTimeout(timeoutMs);
		while(true) {
			try {
				buffer.clear();
				num = channel.read(buffer);
				if (num <= 0) {
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
				break;
			} catch (Throwable e) {
				e.printStackTrace();
				break;
			}
		}
	}
	
	public void readExtraData(int timeoutMs, int maxTime)
	{
		int num=0;
		ByteBuffer buffer = ByteBuffer.allocate(16);
		enableReadTimeout(timeoutMs);
		long startTime = System.currentTimeMillis(), endTime = 0;
		while(true) {
			try {
				buffer.clear();
				num = channel.read(buffer);
				if (num <= 0) {
					break;
				}
				endTime = System.currentTimeMillis();
				if((endTime - startTime) > maxTime)
					return;
			} catch (IOException e) {
				e.printStackTrace();
				break;
			} catch (Throwable e) {
				e.printStackTrace();
				break;
			}
		}
	}
	
	
    public int serialRead(byte[] recvBuf, int timeoutMs)
	{
		int num=0, i = 0;
		ByteBuffer buffer = ByteBuffer.allocate(recvBuf.length);
		enableReadTimeout(timeoutMs);
		while(true) {
			try {
				num = channel.read(buffer);
				if (num <= 0) {
					break;
				}
				for(int j=0; j<num; j++) {
					recvBuf[i+j] = buffer.get(i+j);
				}
				i += num;
				if(i >= recvBuf.length)
					break;
			} catch (IOException e) {
				e.printStackTrace();
				i = -2;
				break;
			} catch (Throwable e) {
				e.printStackTrace();
				i = -3;
				break;
			}
		}
		return i;	
	}
	
	public int serailWrite(byte []buf) {
		try {
			ByteBuffer buffer = ByteBuffer.allocate(buf.length);
			buffer.put(buf);
			buffer.flip();
			channel.write(buffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
		return buf.length;
	}
	
	public void serialReset() {
		try {
			channel.flush(true, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception {
		byte[] buffer = new byte[128];
		
        SerialParameters parameters = new SerialParameters();
        parameters.setPortName("COM6");
        
        SerialConnection connection = new SerialConnection(parameters);
        connection.openConnection();
        boolean suc = connection.isOpen();
        
        
        connection.serailWrite("POLL".getBytes());
        
        System.out.println(System.currentTimeMillis());
        int sum = connection.serialRead(buffer, 3000);
        System.out.println(System.currentTimeMillis());
        System.out.println(new String(buffer));
        
        connection.closeConnection();
    }

}

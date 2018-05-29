package cn.com.pax.comm1;

import gnu.io.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.TooManyListenersException;

/*import javax.comm.CommPortIdentifier;
import javax.comm.CommPortOwnershipListener;
import javax.comm.NoSuchPortException;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.SerialPortEvent;
import javax.comm.SerialPortEventListener;
import javax.comm.UnsupportedCommOperationException;*/

import cn.pax.com.utils.DataMatchUtils;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;
public class SerialConnection implements SerialPortEventListener,
		CommPortOwnershipListener {

	private SerialParameters parameters;
	private OutputStream os;
	private InputStream is;

	private CommPortIdentifier portId;
	private SerialPort sPort;

	private boolean open;
	private boolean dataAvaiable = false;

	public SerialConnection(SerialParameters parameters) {
		this.parameters = parameters;
		open = false;
		dataAvaiable = false;
	}

	public void openConnection() throws Exception {
		try {
			portId = CommPortIdentifier.getPortIdentifier(parameters
					.getPortName());
		} catch (NoSuchPortException e) {
			e.printStackTrace();
		}

		try {
			sPort = (SerialPort) portId.open("PAX", 3000);
		} catch (PortInUseException e) {
			throw e;
		}

		try {
			setConnectionParameters(parameters);
		} catch (SerialConnectionException e) {
			sPort.close();
			throw e;
		}

		try {
			os = sPort.getOutputStream();
			is = sPort.getInputStream();
		} catch (IOException e) {
			sPort.close();
			throw new SerialConnectionException("Error opening i/o streams");
		}

		try {
			sPort.addEventListener(this);
		} catch (TooManyListenersException e) {
			sPort.close();
			throw new SerialConnectionException("too many listeners added");
		}

		sPort.notifyOnDataAvailable(true);

		sPort.notifyOnBreakInterrupt(true);
		
//		sPort.notifyOnOutputEmpty(true);
//		
//		sPort.notifyOnCarrierDetect(true);
//		
//		sPort.notifyOnCTS(true);
//		
//		sPort.notifyOnDSR(true);
//		
//		sPort.notifyOnFramingError(true);
//		
//		sPort.notifyOnOutputEmpty(true);
//		
//		sPort.notifyOnParityError(true);
//		
//		sPort.notifyOnRingIndicator(true);

		try {
			sPort.enableReceiveTimeout(30);
		} catch (UnsupportedCommOperationException e) {
		}

		portId.addPortOwnershipListener(this);

		open = true;
	}

	public void setConnectionParameters(SerialParameters parameters) throws SerialConnectionException {

		// Save state of parameters before trying a set.
		int oldBaudRate = sPort.getBaudRate();
		int oldDatabits = sPort.getDataBits();
		int oldStopbits = sPort.getStopBits();
		int oldParity = sPort.getParity();
		//int oldFlowControl = sPort.getFlowControlMode();
		
		try {
			sPort.setSerialPortParams(parameters.getBaudRate(),
					parameters.getDatabits(), parameters.getStopbits(),
					parameters.getParity());
			this.parameters = parameters;
			
			System.out.println(this.parameters);
			
		} catch (UnsupportedCommOperationException e) {
			parameters.setBaudRate(oldBaudRate);
			parameters.setDatabits(oldDatabits);
			parameters.setStopbits(oldStopbits);
			parameters.setParity(oldParity);
			throw new SerialConnectionException("Unsupported parameter");
		}

		try {
			sPort.setFlowControlMode(parameters.getFlowControlIn()
					| parameters.getFlowControlOut());
		} catch (UnsupportedCommOperationException e) {
			throw new SerialConnectionException("Unsupported flow control");
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
				os.close();
				is.close();
			} catch (IOException e) {
				System.err.println(e);
			}

			sPort.close();

			portId.removePortOwnershipListener(this);
		}

		open = false;
	}

	public void sendBreak() {
		sPort.sendBreak(1000);
	}

	public boolean isOpen() {
		return open;
	}

	//閹貉冨煑娑撴彃褰涢惃鍕殶閹诡喗甯撮弨锟�
	public OutputStream getOs() {
		return os;
	}

	public InputStream getIs() {
		return is;
	}

	//设置串口超时时间
	public void enableReadTimeout(int timeout){
		try {
			sPort.enableReceiveTimeout(timeout);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	//清空发送之前的数据
	public void clearSendBeforeData(){
		enableReadTimeout(10);
		InputStream inStream = getIs();
		int jj = 0, ii=0;
		while(true){
			try {
				if(inStream.read() == -1){
					jj++;
				} 
				else {
					ii++;
				}
				if(ii >= 8192) break;
				if(jj > 5) break;
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
		disableReadTimeout();
	}
	
	public boolean isExistComm(){
		try {
			CommPortIdentifier.getPortIdentifier(parameters.getPortName());
			return true;
		} catch (NoSuchPortException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	public void disableReadTimeout(){
		sPort.disableReceiveTimeout();
	}
	
	public void serialEvent(SerialPortEvent e) {
		
		switch (e.getEventType()) {
		case SerialPortEvent.DATA_AVAILABLE:
			//System.out.println("--------DATA_AVAILABLE");
			//Tester.logger.info("Terminal Test serialEvent before-dataAvaiable-{}", dataAvaiable);
			setDataAvaiable(true);
			synchronized (this) {
				notifyAll();
			}
			//Tester.logger.info("Terminal Test serialEvent after-dataAvaiable-{}", dataAvaiable);
			break;
		case SerialPortEvent.BI:
			System.out.println("--------BI");
			break;
//		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
//			System.out.println("--------OUTPUT_BUFFER_EMPTY");
//			break;
//		case SerialPortEvent.DSR:
//			System.out.println("--------DSR");
//			break;
//		case SerialPortEvent.CTS:
//			System.out.println("--------CTS");
//			break;
//		case SerialPortEvent.CD:
//			System.out.println("--------CD");
//			break;
//		case SerialPortEvent.FE:
//			System.out.println("--------FE");
//			break;
//		case SerialPortEvent.OE:
//			System.out.println("--------OE");
//			break;
//		case SerialPortEvent.PE:
//			System.out.println("--------PE");
//			break;
//		case SerialPortEvent.RI:
//			System.out.println("--------RI");
//			break;
		}
	}

	@Override
	public void ownershipChange(int arg0) {
		// TODO Auto-generated method stub
		if(arg0 == CommPortOwnershipListener.PORT_OWNERSHIP_REQUESTED){
			System.out.println("close port");
			sPort.close();
		}
	}
	public static void getCommInfo(java.util.List<String>comList ) {
		CommPortIdentifier portId;
		Enumeration<CommPortIdentifier> en = CommPortIdentifier.getPortIdentifiers();
		if(! en.hasMoreElements()) 
			comList.add("none");
		while (en.hasMoreElements()) {
			portId = (CommPortIdentifier) en.nextElement();
			
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				comList.add(portId.getName());
			}
		}
	}
	public static final ArrayList<String> findPort() {
        Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();	
        ArrayList<String> portNameList =  new ArrayList<String>();
        while (portList.hasMoreElements()) {
            String portName = portList.nextElement().getName();
            portNameList.add(portName);
        }
        return portNameList;
    }
	
	public byte[] serialRead(int len, int timeOut) {
		byte bytes[] = new byte[len];
		int newData = 0;
		int tryCnt = 1;
		int i = 0;
		
		bytes[0] = 0;
		enableReadTimeout((int)(timeOut*1000));
		while(true) {
			try {
				newData = is.read();
				//System.out.println(newData);
				if (newData == -1) {
					tryCnt--;
					if(tryCnt == 0) continue;
					else break;
				}
				bytes[i++] = ((byte)newData);
				if(i >= len )
					break;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		disableReadTimeout();
		return bytes;
	}
	
	public int serialRead(byte[] recvBuf, int timeoutMs)
	{
		int newData = 0, i = 0;
		
		enableReadTimeout(timeoutMs);
		while(true) {
			try {
				newData = is.read();
				//System.out.println(newData);
				if (newData == -1) {
					break;
				}
				recvBuf[i++] = ((byte)newData);
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
		disableReadTimeout();
		return i;	
	}
	
	
	
	
	
//	public byte[] serialReadAutoTime(int len, int timeOut) {
//		byte bytes[] = new byte[len];
//		int newData = 0;
//		int tryCnt = timeOut/100 + 1;
//		int i = 0;
//		enableReadTimeout(timeOut/10);
//		while(tryCnt > 0) {
//			try {
//				newData = is.read();
//				if (newData != -1) {
//					bytes[i++] = ((byte)newData);
//				}
//				if(i >= len)
//					break;
//				tryCnt--;
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		disableReadTimeout();
//		return bytes;
//	}
	
	public int serailWrite(byte []buf) {
		try {
			os.write(buf);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//閹垫挸绱戦崪灞藉彠闂傤厺瑕嗛崣锟芥径鍕倞USB娑撴彃褰涚悮顐ｅ珗閹哄鍟�幓鎺戝弳閻ㄥ嫭鍎忛崘锟�		closeConnection();
			try {
				openConnection();
			} catch (Exception e2) {
		
				e2.printStackTrace();
			}
			return 0;
		}
		return buf.length;
	}
	
	public void serialReset() {
		while(true) {
			try {
				int total = is.available();
				is.skip(total);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	public synchronized boolean isDataAvaiable() {
		return dataAvaiable;
	}

	public synchronized void setDataAvaiable(boolean dataAvaiable) {
		this.dataAvaiable = dataAvaiable;
	}

	
	public static void main(String[] args) throws Exception {
		byte[] recvBuf = new byte[32];
		
		
		SerialParameters parameters = new SerialParameters();
		parameters.setPortName("COM15");
		
		SerialConnection connection = new SerialConnection(parameters);
		connection.openConnection();
		boolean suc = connection.isOpen();
		
		System.out.println(System.currentTimeMillis());
	//	System.out.println(DataMatchUtils.getResultMatch(connection, "Built in May 23, 2017, " +
				//"Nanshan District science and Technology Park, Shenzhen, Guangdong Province, building 4", recvBuf, 1000));
		System.out.println(System.currentTimeMillis());
//		System.out.println(connection.serialRead(recvBuf, 1000));
//		System.out.println(System.currentTimeMillis());
//		System.out.println(connection.serialRead(recvBuf, 1000));
//		System.out.println(System.currentTimeMillis());
		
		connection.closeConnection();
	}
	
}

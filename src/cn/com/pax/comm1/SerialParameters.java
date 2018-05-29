package cn.com.pax.comm1;

//import javax.comm.*;
import gnu.io.*;

public class SerialParameters {

	private String portName;
	private int baudRate;
	private int flowControlIn;
	private int flowControlOut;
	private int databits;
	private int stopbits;
	private int parity;

	public SerialParameters() {
		this("", 115200, SerialPort.FLOWCONTROL_NONE,
				SerialPort.FLOWCONTROL_NONE, SerialPort.DATABITS_8,
				SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

	}

	public SerialParameters(String portName, int baudRate, int flowControlIn,
			int flowControlOut, int databits, int stopbits, int parity) {

		this.portName = portName;
		this.baudRate = baudRate;
		this.flowControlIn = flowControlIn;
		this.flowControlOut = flowControlOut;
		this.databits = databits;
		this.stopbits = stopbits;
		this.parity = parity;
	}

	public void setPortName(String portName) {
		this.portName = portName;
	}

	public String getPortName() {
		return portName;
	}

	public void setBaudRate(int baudRate) {
		this.baudRate = baudRate;
	}

	public void setBaudRate(String baudRate) {
		this.baudRate = Integer.parseInt(baudRate);
	}

	public int getBaudRate() {
		return baudRate;
	}

	public String getBaudRateString() {
		return Integer.toString(baudRate);
	}

	public void setFlowControlIn(int flowControlIn) {
		this.flowControlIn = flowControlIn;
	}

	public void setFlowControlIn(String flowControlIn) {
		this.flowControlIn = stringToFlow(flowControlIn);
	}

	public int getFlowControlIn() {
		return flowControlIn;
	}

	public String getFlowControlInString() {
		return flowToString(flowControlIn);
	}

	public void setFlowControlOut(int flowControlOut) {
		this.flowControlOut = flowControlOut;
	}

	public void setFlowControlOut(String flowControlOut) {
		this.flowControlOut = stringToFlow(flowControlOut);
	}

	public int getFlowControlOut() {
		return flowControlOut;
	}

	public String getFlowControlOutString() {
		return flowToString(flowControlOut);
	}

	public void setDatabits(int databits) {
		this.databits = databits;
	}

	public void setDatabits(String databits) {
		if (databits.equals("5")) {
			this.databits = SerialPort.DATABITS_5;
		}
		if (databits.equals("6")) {
			this.databits = SerialPort.DATABITS_6;
		}
		if (databits.equals("7")) {
			this.databits = SerialPort.DATABITS_7;
		}
		if (databits.equals("8")) {
			this.databits = SerialPort.DATABITS_8;
		}
	}

	public int getDatabits() {
		return databits;
	}

	public String getDatabitsString() {
		switch (databits) {
		case SerialPort.DATABITS_5:
			return "5";
		case SerialPort.DATABITS_6:
			return "6";
		case SerialPort.DATABITS_7:
			return "7";
		case SerialPort.DATABITS_8:
			return "8";
		default:
			return "8";
		}
	}

	public void setStopbits(int stopbits) {
		this.stopbits = stopbits;
	}

	public void setStopbits(String stopbits) {
		if (stopbits.equals("1")) {
			this.stopbits = SerialPort.STOPBITS_1;
		}
		if (stopbits.equals("1.5")) {
			this.stopbits = SerialPort.STOPBITS_1_5;
		}
		if (stopbits.equals("2")) {
			this.stopbits = SerialPort.STOPBITS_2;
		}
	}

	public int getStopbits() {
		return stopbits;
	}

	public String getStopbitsString() {
		switch (stopbits) {
		case SerialPort.STOPBITS_1:
			return "1";
		case SerialPort.STOPBITS_1_5:
			return "1.5";
		case SerialPort.STOPBITS_2:
			return "2";
		default:
			return "1";
		}
	}

	public void setParity(int parity) {
		this.parity = parity;
	}

	public void setParity(String parity) {
		if (parity.equals("None")) {
			this.parity = SerialPort.PARITY_NONE;
		}
		if (parity.equals("Even")) {
			this.parity = SerialPort.PARITY_EVEN;
		}
		if (parity.equals("Odd")) {
			this.parity = SerialPort.PARITY_ODD;
		}
	}

	public int getParity() {
		return parity;
	}

	public String getParityString() {
		switch (parity) {
		case SerialPort.PARITY_NONE:
			return "None";
		case SerialPort.PARITY_EVEN:
			return "Even";
		case SerialPort.PARITY_ODD:
			return "Odd";
		default:
			return "None";
		}
	}

	private int stringToFlow(String flowControl) {
		if (flowControl.equals("None")) {
			return SerialPort.FLOWCONTROL_NONE;
		}
		if (flowControl.equals("Xon/Xoff Out")) {
			return SerialPort.FLOWCONTROL_XONXOFF_OUT;
		}
		if (flowControl.equals("Xon/Xoff In")) {
			return SerialPort.FLOWCONTROL_XONXOFF_IN;
		}
		if (flowControl.equals("RTS/CTS In")) {
			return SerialPort.FLOWCONTROL_RTSCTS_IN;
		}
		if (flowControl.equals("RTS/CTS Out")) {
			return SerialPort.FLOWCONTROL_RTSCTS_OUT;
		}
		return SerialPort.FLOWCONTROL_NONE;
	}

	String flowToString(int flowControl) {
		switch (flowControl) {
		case SerialPort.FLOWCONTROL_NONE:
			return "None";
		case SerialPort.FLOWCONTROL_XONXOFF_OUT:
			return "Xon/Xoff Out";
		case SerialPort.FLOWCONTROL_XONXOFF_IN:
			return "Xon/Xoff In";
		case SerialPort.FLOWCONTROL_RTSCTS_IN:
			return "RTS/CTS In";
		case SerialPort.FLOWCONTROL_RTSCTS_OUT:
			return "RTS/CTS Out";
		default:
			return "None";
		}
	}

	@Override
	public String toString() {
		return "SerialParameters [portName=" + portName + ", baudRate="
				+ baudRate + ", flowControlIn=" + flowControlIn
				+ ", flowControlOut=" + flowControlOut + ", databits="
				+ databits + ", stopbits=" + stopbits + ", parity=" + parity
				+ "]";
	}
	
	
}

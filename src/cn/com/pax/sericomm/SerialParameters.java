package cn.com.pax.sericomm;

import dk.thibaut.serial.enums.BaudRate;
import dk.thibaut.serial.enums.DataBits;
import dk.thibaut.serial.enums.Parity;
import dk.thibaut.serial.enums.StopBits;

public class SerialParameters {

	private String portName;
	private BaudRate baudRate;
	private DataBits databits;
	private StopBits stopbits;
	private Parity parity;

	public SerialParameters() {
		this("", BaudRate.B115200, DataBits.D8, StopBits.ONE, Parity.NONE);
	}

	public SerialParameters(String portName, BaudRate baudRate, DataBits databits, StopBits stopbits, Parity parity) {

		this.portName = portName;
		this.baudRate = baudRate;
		this.databits = databits;
		this.stopbits = stopbits;
		this.parity = parity;
	}

	public String getPortName() {
		return portName;
	}

	public void setPortName(String portName) {
		this.portName = portName;
	}

	public BaudRate getBaudRate() {
		return baudRate;
	}

	public void setBaudRate(BaudRate baudRate) {
		this.baudRate = baudRate;
	}

	public DataBits getDatabits() {
		return databits;
	}

	public void setDatabits(DataBits databits) {
		this.databits = databits;
	}

	public StopBits getStopbits() {
		return stopbits;
	}

	public void setStopbits(StopBits stopbits) {
		this.stopbits = stopbits;
	}

	public Parity getParity() {
		return parity;
	}

	public void setParity(Parity parity) {
		this.parity = parity;
	}

	@Override
	public String toString() {
		return "SerialParameters [portName=" + portName + ", baudRate="
				+ baudRate  + ", databits="
				+ databits + ", stopbits=" + stopbits + ", parity=" + parity
				+ "]";
	}
	
	
}

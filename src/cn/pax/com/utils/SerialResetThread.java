package cn.pax.com.utils;

import cn.com.pax.comm1.SerialConnection;

public class SerialResetThread implements Runnable {
	private boolean running;
	private SerialConnection connection;
	
	
	public SerialResetThread(SerialConnection connection, boolean running) {
		super();
		this.connection = connection;
		this.running = running;
	}


	public synchronized boolean isRunning() {
		return running;
	}


	public synchronized void setRunning(boolean running) {
		this.running = running;
	}


	@Override
	public void run() {
		while(running) {
			connection.clearSendBeforeData();
		}
	}

}

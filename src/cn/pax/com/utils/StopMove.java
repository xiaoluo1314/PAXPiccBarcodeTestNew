package cn.pax.com.utils;

public class StopMove {
	private  static boolean isRunning ;
	private static boolean isExit;
	private static boolean codeRunning;
	
	
	public static boolean isExit() {
		return isExit;
	}

	public static void setExit(boolean isExit) {
		StopMove.isExit = isExit;
	}

	public static synchronized boolean isRunning() {
		return isRunning;
	}

	public static synchronized void setRunning(boolean isRunning) {
		StopMove.isRunning = isRunning;
	}

	public static synchronized boolean isCodeRunning() {
		return codeRunning;
	}

	public static synchronized void setCodeRunning(boolean codeRunning) {
		StopMove.codeRunning = codeRunning;
	}
	
}

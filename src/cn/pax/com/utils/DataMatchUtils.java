package cn.pax.com.utils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.com.pax.display.mainFrame;
import cn.com.pax.sericomm.SerialConnection;



public class DataMatchUtils {
	
	public static boolean getMoveResultMatch(SerialConnection connection, String content, byte[] recvBuf, int timeoutMs) {
		StringBuilder builder = new StringBuilder();
		Pattern pattern = Pattern.compile("SCANST(.+?)[\r]?[\n]?SCANED");
		int recvNum = 0;
		int totalNum = timeoutMs / 100 + 1;
		int count = 0;
		
		while (true) {
			recvNum = connection.serialRead(recvBuf, 100);
			if(recvNum > 0) {
				//System.out.println("---->"+recvNum);
				builder.append(new String(recvBuf, 0, recvNum));
				mainFrame.logger.info("contents: " +  builder.toString());

				//System.out.println("---" + builder.toString());
				//System.out.println("--------->"+content);
				Matcher matcher = pattern.matcher(builder.toString());
				//匹配则需要考虑完整匹配
				if(matcher.find()) {
					mainFrame.logger.info("传入内容:" + content +"匹配内容:" + matcher.group(1));
					//System.out.println("++++" + matcher.group(1));
					if(matcher.group(1).equalsIgnoreCase(content)) {
						return true;
					}
					else {
						return false;
					}
				}
				else {
					//如果字符串超过接收缓冲的4倍则不再接收报失败
					if(builder.toString().length() > recvBuf.length * 18)
						return false;
				}
			}
			//timeout or exception is failed
			else {
				if(recvNum == 0) {
					count++;
					if(count > totalNum) {
						//System.out.println("------------");
						return false;
					}
				}
				else {
					return false;
				}
			}
		}
	}
	
	
	public static boolean getResultMatch(SerialConnection connection, String content, byte[] recvBuf, int timeoutMs) {
		StringBuilder builder = new StringBuilder();
		Pattern pattern = Pattern.compile("SCANST(.+?)[\r]?[\n]?SCANED");
		int recvNum = 0;
		int totalNum = timeoutMs / 500 + 1;
		int count = 0;
		
		while (true) {
			recvNum = connection.serialRead(recvBuf, 500);
			if(recvNum > 0) {
				//System.out.println("---->"+recvNum);
				builder.append(new String(recvBuf, 0, recvNum));
				mainFrame.logger.info("contents: " +  builder.toString());
				//System.out.println("---" + builder.toString());
				//System.out.println("--------->"+content);
				Matcher matcher = pattern.matcher(builder.toString());
				//匹配则需要考虑完整匹配
				if(matcher.find()) {
					mainFrame.logger.info("传入内容:" + content +"匹配内容:" + matcher.group(1));
					//System.out.println("++++" + matcher.group(1));
					if(matcher.group(1).equalsIgnoreCase(content)) {
						return true;
					}
					else {
						return false;
					}
				}
				else {
					//如果字符串超过接收缓冲的4倍则不再接收报失败
					if(builder.toString().length() > recvBuf.length * 18)
						return false;
				}
			}
			//timeout or exception is failed
			else {
				if(recvNum == 0) {
					count++;
					if(count > totalNum) {
						//System.out.println("------------");
						return false;
					}
				}
				else {
					return false;
				}
			}
		}
	}
	
	
	public static String getFeiJMatch(SerialConnection connection, byte[] recvBuf, int timeoutMs) {
		StringBuilder builder = new StringBuilder();
		Pattern pattern = Pattern.compile("Suc.*?(\\d+)\\s*Los.*?(\\d+)");
		int recvNum = 0;
		int totalNum = timeoutMs / 500 + 1;
		int count = 0;
		
		while (true) {
			recvNum = connection.serialRead(recvBuf, 500);
			if(recvNum > 0) {
				//System.out.println("---->"+recvNum);
				builder.append(new String(recvBuf, 0, recvNum));
				mainFrame.logger.info("sources: " +  builder.toString());
				
				Matcher matcher = pattern.matcher(builder.toString());
				//匹配则需要考虑完整匹配
				if(matcher.find()) {
					return matcher.group(1) + ":" + matcher.group(2);
				}
				else {
					//如果字符串超过接收缓冲的4倍则不再接收报失败
					if(builder.toString().length() > recvBuf.length * 4)
						return null;
				}
			}
			//timeout or exception is failed
			else {
				if(recvNum == 0) {
					count++;
					if(count > totalNum) {
						//System.out.println("------------");
						return null;
					}
				}
				else {
					return null;
				}
			}
		}
	}
}

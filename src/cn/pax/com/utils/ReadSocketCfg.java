package cn.pax.com.utils;

import cn.com.pax.protocol.SocketToC4;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

public class ReadSocketCfg {
	private  String  ip= "";
	private  int  port = 0;

	public ReadSocketCfg() {
		ip = "";
		port = 0;
	}

	public ReadSocketCfg(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	public  String getIP() {
		return ip;
	}
	public  int  getPort() {
		return port;
	}

	public static SocketToC4 getSocket(String path) {
		ReadSocketCfg cfg = new ReadSocketCfg();
		return cfg.readSocketConfig(path);
	}

	public  SocketToC4 readSocketConfig(String path) {
		Properties properties = new Properties();
		File file = new File(path);
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(file);
			properties.load(fileReader);
			ip = properties.getProperty("IP").trim();
			port = Integer.parseInt(properties.getProperty("PORT").trim());
//			if (ip == null || ip.length()<1) {
//				throw new IllegalArgumentException("IP地址不合法");
//			}
//			if(port ==0){
//				throw new IllegalArgumentException("端口不合法");
//			}
			return new SocketToC4(ip, port);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			if (fileReader != null) {
				try {
					fileReader.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}


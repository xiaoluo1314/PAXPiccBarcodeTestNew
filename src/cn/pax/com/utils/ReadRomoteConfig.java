package cn.pax.com.utils;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
public class ReadRomoteConfig {
	public static final String CONFIG_NAME = "./config/romoteCfg.properties";
	public static String ip ;
	public static String port;
	
	static{
		
			Properties commConfig = new Properties();
			try {
				commConfig.load(new FileReader(CONFIG_NAME));
			} catch (IOException e) {
				e.printStackTrace();
				
			}
			ip = commConfig.getProperty("IP");
			port = commConfig.getProperty("PORT");
	}

	public static String getIp() {
		return ip;
	}

	public static String getPort() {
		return port;
	}
	
}

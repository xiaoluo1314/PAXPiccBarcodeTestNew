package cn.pax.com.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import javax.jws.Oneway;

import org.slf4j.impl.StaticLoggerBinder;

public class ReadProperties {
	public static final String CONFIG_NAME = "./config/Display.properties";
	private static final String TL_CFG = "./config/scanDepth.properties";

	private	String one_Dim = "";
	private	String two_Dim = "";
	private String oneone_Depth = "";
	private String twotwo_Depth = "";
	private String twoone_Depth = "";
	private String brightOne;
	private String brightTwo;
	private String rateOne;
	private String rateTwo;

	public String getOne_Dim() {
		return one_Dim;
	}
	public String getTwo_Dim() {
		return two_Dim;
	}
	public  String getOneone_Depth() {
		return oneone_Depth;
	}
	public  String getTwotwo_Depth() {
		return twotwo_Depth;
	}
	public  String getTwoone_Depth() {
		return twoone_Depth;
	}
	public String getBrightOne() {
		return brightOne;
	}
	public String getBrightTwo() {
		return brightTwo;
	}
	public String getRateOne() {
		return rateOne;
	}
	public String getRateTwo() {
		return rateTwo;
	}

	public ReadProperties(){
		Properties commConfig = new Properties();
		try {
			FileReader reader = new FileReader(CONFIG_NAME);
			commConfig.load(reader);
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		one_Dim = commConfig.getProperty("One");
		two_Dim = commConfig.getProperty("Two");
		brightOne = commConfig.getProperty("BrightOne");
		brightTwo = commConfig.getProperty("BrightTwo");
		rateOne = commConfig.getProperty("RateOne");
		rateTwo = commConfig.getProperty("RateTwo");

		ReadDepthProperties();
	}

	private void ReadDepthProperties(){
		File scanDepthConfig = new File(TL_CFG);
		if (!scanDepthConfig.exists()) {
			try {
				scanDepthConfig.createNewFile();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} else {
			try {
				FileInputStream fin = new FileInputStream(scanDepthConfig);
				Properties prop = new Properties();
				prop.load(fin);
				oneone_Depth = prop.getProperty("oneone");
				twoone_Depth = prop.getProperty("twoone");
				twotwo_Depth = prop.getProperty("twotwo");
				fin.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
}

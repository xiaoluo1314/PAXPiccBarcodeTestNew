package cn.pax.com.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReadCardInfo {


	private static final String CONFIGINFO = "./config/pickCardCfg.txt";
	private static  InputStreamReader read = null;
	private static  BufferedReader bufferedReader = null;
	public static List<CardInfo>cardInfo = new ArrayList<CardInfo>();
	
	public static List<CardInfo> getCardInfo() {
		return cardInfo;
	}

	public static HashMap<String, List<Integer>>brInfoMap = new HashMap<String, List<Integer>>();
		
	static
	{
		File file = new File(CONFIGINFO);
		try {
			read = new InputStreamReader(new FileInputStream(file), "utf-8");
			bufferedReader = new BufferedReader(read);
			String s = null;
			int i=0;
			while ((s = bufferedReader.readLine()) != null) {
				String[] str = s.split("@{3}");
				CardInfo binfo = new CardInfo(str[0], str[1], str[2],str[3],str[4]);
				cardInfo.add(binfo);
				String key = str[0] + str[1];
				if(brInfoMap.containsKey(key)) {
					List<Integer> vaList = brInfoMap.get(key);
					vaList.add(i);
				} else {
					List<Integer> tmpList = new ArrayList<Integer>();
					tmpList.add(i);
					brInfoMap.put(key, tmpList);
				}
				i++;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (bufferedReader!=null) {
				try {
					bufferedReader.close();
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					if (read != null){
						try {
							read.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	public static HashMap<String, List<Integer>> getBrInfoMap() {
		return brInfoMap;
	}
	
	
	
}

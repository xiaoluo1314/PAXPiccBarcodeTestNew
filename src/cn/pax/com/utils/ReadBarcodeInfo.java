package cn.pax.com.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadBarcodeInfo {
	private static final String CONFIGINFO = "./config/D220displaycfg.txt";
	private static  InputStreamReader read = null;
	private static  BufferedReader bufferedReader = null;
	public static List<BarcodeInfo> barcodeInfoList = new ArrayList<BarcodeInfo>();
	public static HashMap<String, List<Integer>> brInfoMap = new HashMap<String, List<Integer>>();
	public static HashMap<String, Integer> typeNumMap = new HashMap<>();

	static
	{
		File file = new File(CONFIGINFO);
		try {
			read = new InputStreamReader(new FileInputStream(file), "utf-8");
			bufferedReader = new BufferedReader(read);
			String s = null;
			int i = 0;
			while ((s = bufferedReader.readLine()) != null) {
				//String[] str = s.split(",\\s*");
				String[] str = s.split("@{3}");
				BarcodeInfo binfo = new BarcodeInfo(str[0], str[1], str[2], str[3],str[4]);
				barcodeInfoList.add(binfo);
				String key = str[0] + str[1];
				if(typeNumMap.containsKey(key))
					typeNumMap.put(key, typeNumMap.get(key) + 1);
				else
					typeNumMap.put(key, 1);
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
		}	catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (bufferedReader!=null) {
				try {
					bufferedReader.close();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}finally{
					if (read != null){
						try {
							read.close();
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					}
				}
			}
		}
		//System.out.println(brInfoMap.size()+"\n");
		//System.out.println(brInfoMap);
	}
	
	public static List<BarcodeInfo> getList() {
		return barcodeInfoList;
	}
	public static  HashMap<String, List<Integer>> getBrInfoMap() {
		return brInfoMap;
	}
	
/*	public static void readConfigInfo(){
		File file = new File(CONFIGINFO);
		 try {
			InputStreamReader read = new InputStreamReader(
			         new FileInputStream(file),"utf-8");
			 BufferedReader bufferedReader = new BufferedReader(read);
			 String s = null;
	            while((s =bufferedReader.readLine())!=null){//使用readLine方法，一次读一行
	                String []str = s.split(",\\s*");
	                //System.out.println(str+ "---->" +str.length);
	                BarcodeInfo binfo = new BarcodeInfo(str[0], str[1], str[2], str[3],str[4]);
	                barcodeInfoList.add(binfo);
	            }
			
	           //System.out.println(barcodeInfoList);
		
		} catch(Exception e) {
			
			e.printStackTrace();
		}
	}*/
	public static void main(String[] args) {
		//ReadBarcodeInfo.getBrInfoMap();
		for(String key: typeNumMap.keySet())
			System.out.println(key + ": " + typeNumMap.get(key));
//		ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
//		byte ch = 'c';
//		byteBuffer.put(ch);
//		ch = 'a';
//		byteBuffer.put(ch);
//		System.out.println(new String( byteBuffer.array()));
//		
//		Pattern pattern = Pattern.compile("SCANST\\w+SCANED");
//		Matcher matcher = pattern.matcher("SCANST12-3PSCANED\r\nSCANST12-3PSCANED");
//		System.out.println(matcher.find());
//		
//		String  string  = "send "+ 1+" "+ 10;
//		
//		System.out.println(Integer.valueOf("FF",16).toString());
//		System.out.println( string);
		//readConfigInfo();
	}
}

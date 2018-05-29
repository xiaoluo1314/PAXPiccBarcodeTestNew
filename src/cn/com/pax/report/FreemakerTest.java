package cn.com.pax.report;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FreemakerTest {
	public static boolean reportGenerate(String reportName, TestInfo info, List<TiaoMaInfo> allList) {
		DocumentHandler dHandler = new DocumentHandler();
		
		//System.out.println(Arrays.deepToString(allList.toArray()));
		
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("testinfo", info);
		
		ProcessResult results = new ProcessResult();
		
		results.addResult(allList);
		
		List<TiaoMaInfo> jinShenList = results.getTypeList("扫描景深");
		dataMap.put("jinShenList", jinShenList);
		dataMap.put("jinShenResult", results.getTotalResult(results.getKeyList(jinShenList)));

		String dimen = "一维";
		if(jinShenList.size() > 0)
			dimen = jinShenList.get(0).getDimen();
		dataMap.put("dimen", dimen);



		//List<TiaoMaInfo> angelOneList = results.getTypeList("扫描角度/一维");
		List<TiaoMaInfo> angelTwoList = results.getTypeList("扫描角度");
		
		String ccString = "-";
//		if(angelOneList.size() > 0)
//			ccString = angelOneList.get(0).getCodeName();
//		dataMap.put("scanAngelOneName", ccString);
//		dataMap.put("angelOneList", angelOneList);
//		dataMap.put("angelOneResult", results.getTotalResult(results.getKeyList(angelOneList)));
//		ccString = "-";
		if(angelTwoList.size() > 0)
			ccString = angelTwoList.get(0).getCodeName();
		dataMap.put("scanAngelTwoName", ccString);
		dataMap.put("angelTwoList", angelTwoList);
		dataMap.put("angelTwoResult", results.getTotalResult(results.getKeyList(angelTwoList)));
		
		//排序问题要解决
		//List<TiaoMaInfo> rotate360One = results.getTypeList("360旋转角度/一维");
		//paiXu1(rotate360One);
		List<TiaoMaInfo> rotate360Two = results.getTypeList("360旋转角度");
		paiXu1(rotate360Two);
//		ccString = "-";
//		if(rotate360One.size() > 0)
//			ccString = rotate360One.get(0).getCodeName();
//		dataMap.put("rt360OneCodeName", ccString);
//		dataMap.put("rt360OneList", rotate360One);
//		dataMap.put("rt360OneResult", results.getTotalResult(results.getKeyList(rotate360One)));
		ccString = "-";
		if(rotate360Two.size() > 0)
			ccString = rotate360Two.get(0).getCodeName();
		dataMap.put("rt360TwoCodeName", ccString);
		dataMap.put("rt360TwoList", rotate360Two);
		dataMap.put("rt360TwoResult", results.getTotalResult(results.getKeyList(rotate360Two)));
		
		
//		List<TiaoMaInfo> scanRateOne = results.getTypeList("扫描速度/一维");
//		paiXu2(scanRateOne);
		List<TiaoMaInfo> scanRateTwo = results.getTypeList("扫描速度");
		paiXu2(scanRateTwo);   
//		ccString = "-";
//		if(scanRateOne.size() > 0)
//			ccString = scanRateOne.get(0).getCodeName();
//		dataMap.put("scanRateOneCodeName", ccString);
//		dataMap.put("scanRateOneList", scanRateOne);
//		dataMap.put("scanRateOneResult", results.getTotalResult(results.getKeyList(scanRateOne)));
		ccString = "-";
		if(scanRateTwo.size() > 0)
			ccString = scanRateTwo.get(0).getCodeName();
		dataMap.put("scanRateTwoCodeName", ccString);
		dataMap.put("scanRateTwoList", scanRateTwo);
		dataMap.put("scanRateTwoResult", results.getTotalResult(results.getKeyList(scanRateTwo)));

		List<TiaoMaInfo> codeTypeList = results.getTypeList("码制类型");
		List<String> supportList = new ArrayList<String>();
		List<String> notSuppList = new ArrayList<String>();
		for(TiaoMaInfo info1: codeTypeList) {
			if(info1.getResult().equalsIgnoreCase("PASS")) {
				supportList.add(info1.getCodeName());
			}
			else {
				notSuppList.add(info1.getCodeName());
			}
		}
		dataMap.put("supportList", supportList);
		dataMap.put("notSuppList", notSuppList);
		dataMap.put("codeTypeResult", results.getTotalResult(results.getKeyList(codeTypeList)));

		List<TiaoMaInfo> colorList = results.getTypeList("彩色条码");
		dataMap.put("colorList", colorList);
		dataMap.put("colorResult", results.getTotalResult(results.getKeyList(colorList)));
		
		List<TiaoMaInfo> cmpRatioList = results.getTypeList("对比度");
		dataMap.put("cmpRatioList", cmpRatioList);
		dataMap.put("cmpRatioResult", results.getTotalResult(results.getKeyList(cmpRatioList)));
		
		List<TiaoMaInfo> wusunList = results.getTypeList("污损案例");
		dataMap.put("wusunList", wusunList);
		dataMap.put("wusunResult", results.getTotalResult(results.getKeyList(wusunList)));

		List<TiaoMaInfo> bigDataList = results.getTypeList("大数据条码");
		dataMap.put("bigDataList", bigDataList);
		dataMap.put("bigDataResult", results.getTotalResult(results.getKeyList(bigDataList)));

		List<TiaoMaInfo> diffCharList = results.getTypeList("不同字符解码");
		dataMap.put("diffCharList", diffCharList);
		dataMap.put("diffCharResult", results.getTotalResult(results.getKeyList(diffCharList)));

		List<TiaoMaInfo> fatThinList = results.getTypeList("胖瘦畸变");
		dataMap.put("fatThinList", fatThinList);
		dataMap.put("fatThinResult", results.getTotalResult(results.getKeyList(fatThinList)));

		List<TiaoMaInfo> mirrorList = results.getTypeList("镜像码");
		dataMap.put("mirrorList", mirrorList);
		dataMap.put("mirrorResult", results.getTotalResult(results.getKeyList(mirrorList)));

		List<TiaoMaInfo> curveList = results.getTypeList("曲面码");
		dataMap.put("curveList", curveList);
		dataMap.put("curveResult", results.getTotalResult(results.getKeyList(curveList)));

		List<TiaoMaInfo> jianBianList = results.getTypeList("渐变码");
		dataMap.put("jianBianList", jianBianList);
		dataMap.put("jianBianResult", results.getTotalResult(results.getKeyList(jianBianList)));

		List<TiaoMaInfo> jingDuList = results.getTypeList("识别精度");
		dataMap.put("jingDuList", jingDuList);
		dataMap.put("jingDuResult", results.getTotalResult(results.getKeyList(jingDuList)));

		List<TiaoMaInfo> brightList = results.getTypeList("手机背光亮度");
		//dataMap.put("brightList", brightList);
		dataMap.put("brightCode", "-");
		dataMap.put("bright", 0);
		dataMap.put("brightR1", "-");
		if(brightList.size() > 0) {
			dataMap.put("brightCode", brightList.get(0).getCodeName());
			dataMap.put("bright", brightList.get(0).getIndicator());
			dataMap.put("brightR1", brightList.get(0).getResult());
		}
		dataMap.put("brightResult", results.getTotalResult(results.getKeyList(brightList)));

		List<TiaoMaInfo> jiezhiList = results.getTypeList("不同介质卡片");
		dataMap.put("jiezhiList", jiezhiList);
		dataMap.put("jiezhiResult", results.getTotalResult(results.getKeyList(jiezhiList)));
		
		return dHandler.createDoc(".", "./reportmodel/条码扫描测试报告模板.xml", reportName, dataMap);
	}

	private static void paiXu1(List<TiaoMaInfo> list) {
		Collections.sort(list, new Comparator<TiaoMaInfo>(){  
			  
	            public int compare(TiaoMaInfo o1, TiaoMaInfo o2) {  
	              
	                if(Integer.parseInt(o1.getIndicator().trim())>Integer.parseInt(o2.getIndicator().trim())){  
	                    return 1;  
	                }  
	                if(o1.getIndicator().equals( o2.getIndicator())){  
	                    return 0;  
	                }  
	                return -1;  
	            }  
	        });
	}
	
	private static void paiXu2(List<TiaoMaInfo> list) {
		Collections.sort(list, new Comparator<TiaoMaInfo>(){
	            public int compare(TiaoMaInfo o1, TiaoMaInfo o2) {  
	              
	                if(Integer.parseInt(o1.getIndName().trim())>Integer.parseInt(o2.getIndName().trim())){  
	                    return 1;  
	                }  
	                if(o1.getIndName().equals( o2.getIndName())){  
	                    return 0;  
	                }  
	                return -1;  
	            }  
	        });
	}
	
	
	
	public static boolean reportGenerate(String reportName, RFTestInfo info, String[] aPoint25, String[] bPoint25, int pointNums) {
		DocumentHandler dHandler = new DocumentHandler();
		
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("testinfo", info);
		
		String aResult = "FAIL";
		String bResult = "FAIL";
		
		List<RFDetail> aDetails = new ArrayList<RFDetail>();
		List<RFDetail> bDetails = new ArrayList<RFDetail>();
		getDataList(aPoint25, aDetails, pointNums);
		getDataList(bPoint25, bDetails, pointNums);
		
		aResult = analysisData(aPoint25, 0, (pointNums * 2 - 1)) == (pointNums * 2) ? "PASS":"FAIL";
		bResult = analysisData(bPoint25, 0, (pointNums * 2 - 1)) == (pointNums * 2) ? "PASS":"FAIL";
		
		dataMap.put("aResult", aResult);
		dataMap.put("bResult", bResult);
		dataMap.put("aDetails", aDetails);
		dataMap.put("bDetails", bDetails);
		
		return dHandler.createDoc(".", "./reportmodel/非接读卡测试报告模板.xml", reportName, dataMap);
	}
	
	public static boolean reportGenerate(String reportName, RFTestInfo info, double[] voltages, int pNums) {
		DocumentHandler dHandler = new DocumentHandler();
		
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("testinfo", info);
		
		String[] results = new String[voltages.length];
		for(int i=0; i<results.length;i++) {
			results[i] = "";
		}
		for(int i=0; i<pNums;i++) {
			results[i] = (voltages[i] > 2.500 && voltages[i] < 8.100)? "PASS":"FAIL";
		}
		dataMap.put("results", results);
		dataMap.put("voltages", voltages);
		
		return dHandler.createDoc(".", "./reportmodel/场强测试报告模板.xml", reportName, dataMap);
	}
	
	public static void getDataList(String[] point25, List<RFDetail> details, int pNums) {
		int total = 0, total_1 = 0;
		for(int i=0; i<pNums * 2; i+=10) {
			int a1 = analysisData_1(point25, i, i+9);
			total += a1;
			int b1 = analysisData(point25, i, i+9);
			total_1 += b1;
			details.add(new RFDetail(i / 10  + "cm", 5, a1, b1));
		}
		details.add(new RFDetail("Total", pNums, total, total_1 ));
	}
	
	public static int analysisData(String[] point25, int start, int end) {
		int num = 0;
		for(int i=start; i<=end; i+=1) {
			if(point25[i].equalsIgnoreCase("PASS"))
				num++;
		}
		return num;
	}
	public static int analysisData_1(String[] point25, int start, int end) {
		int num = 0;
		for(int i=start; i<=end; i+=2) {
			if(point25[i].equalsIgnoreCase("PASS") && point25[i+1].equalsIgnoreCase("PASS"))
				num++;
		}
		return num;
	}
}



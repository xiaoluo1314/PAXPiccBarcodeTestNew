package cn.com.pax.report;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JOptionPane;

public class TestMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		TestInfo info1 = new TestInfo("李三", "天津", "常温常湿", "A920", "", "", "8974561234", "");
		
		List<TiaoMaInfo> allList = new ArrayList<TiaoMaInfo>();
		//TiaoMaInfo tiao1 = new TiaoMaInfo("扫描景深", "一维", "Code 128_15mil_18bytes", "", "景深范围 > 100mm", "PASS", "90~170");
		TiaoMaInfo tiao2 = new TiaoMaInfo("扫描景深", "二维", "QR Code_40mil_18bytes", "", "景深范围 > 100mm", "FAIL", "60~180"); 
		//allList.add(tiao1);
		allList.add(tiao2);

		System.out.println(allList.size());;
		TiaoMaInfo you1 = new TiaoMaInfo("扫描角度", "一维", "Code 128_15mil_18bytes", "倾斜角度（右）", "0~45°", "PASS", ""); 
		TiaoMaInfo zuo1 = new TiaoMaInfo("扫描角度", "一维", "Code 128_15mil_18bytes", "倾斜角度（左）", "0~45°", "FAIL", "20,30"); 
		TiaoMaInfo shang1 = new TiaoMaInfo("扫描角度", "一维", "Code 128_15mil_18bytes", "偏转角度（上）", "0~45°", "PASS", ""); 
		TiaoMaInfo xia1 = new TiaoMaInfo("扫描角度", "一维", "Code 128_15mil_18bytes", "偏转角度（下）", "0~45°", "FAIL", "10,25"); 
		
		TiaoMaInfo you2 = new TiaoMaInfo("扫描角度", "二维", "QR Code_40mil_18bytes", "倾斜角度（右）", "0~45°", "FAIL", "0,30"); 
		TiaoMaInfo zuo2 = new TiaoMaInfo("扫描角度", "二维", "QR Code_40mil_18bytes", "倾斜角度（左）", "0~45°", "PASS", ""); 
		TiaoMaInfo shang2 = new TiaoMaInfo("扫描角度", "二维", "QR Code_40mil_18bytes", "偏转角度（上）", "0~45°", "FAIL", "40,45"); 
		TiaoMaInfo xia2 = new TiaoMaInfo("扫描角度", "二维", "QR Code_40mil_18bytes", "偏转角度（下）", "0~45°", "PASS", ""); 
		allList.add(you1);allList.add(zuo1);allList.add(shang1);allList.add(xia1);
		allList.add(you2);allList.add(zuo2);allList.add(shang2);allList.add(xia2);
		
		TiaoMaInfo r3601 = new TiaoMaInfo("360旋转角度", "一维", "Code 128_15mil_18bytes", "", "30", "PASS", ""); 
		TiaoMaInfo r3602 = new TiaoMaInfo("360旋转角度", "一维", "Code 128_15mil_18bytes", "", "60", "FAIL", ""); 
		
		TiaoMaInfo r3603 = new TiaoMaInfo("360旋转角度", "二维", "QR Code_40mil_18bytes", "", "60", "PASS", ""); 
		TiaoMaInfo r3604 = new TiaoMaInfo("360旋转角度", "二维", "QR Code_40mil_18bytes", "", "180", "PASS", ""); 
		allList.add(r3601);allList.add(r3602);allList.add(r3603);allList.add(r3604);
		
		TiaoMaInfo dis1 = new TiaoMaInfo("扫描速度", "一维", "Code 128_15mil_18bytes", "6", "10", "PASS", "");
		TiaoMaInfo dis2 = new TiaoMaInfo("扫描速度", "一维", "Code 128_15mil_18bytes", "9", "30", "PASS", "");
		
		TiaoMaInfo dis3 = new TiaoMaInfo("扫描速度", "二维", "QR Code_40mil_18bytes", "9", "10", "PASS", "");
		TiaoMaInfo dis4 = new TiaoMaInfo("扫描速度", "二维", "QR Code_40mil_18bytes", "12", "30", "PASS", "");
		allList.add(dis1);allList.add(dis2);allList.add(dis3);allList.add(dis4);

		TiaoMaInfo code1 = new TiaoMaInfo("码制类型", "一维", "Data Matrix-40mil-103bytes", "", "", "FAIL", "");
		TiaoMaInfo code11 = new TiaoMaInfo("码制类型", "一维", "Data 45mil-103bytes", "", "", "FAIL", "");
		TiaoMaInfo code111 = new TiaoMaInfo("码制类型", "一维", "Data Matrix-40mil-10bytes", "", "", "PASS", "");
		TiaoMaInfo code2 = new TiaoMaInfo("码制类型", "二维", "QR-H-30mil-103bytes", "", "", "FAIL", "");
		allList.add(code1);
		allList.add(code11);
		allList.add(code111);
		allList.add(code2);

		TiaoMaInfo jiezhi1 = new TiaoMaInfo("不同介质卡片", "一维", "QR Code_40mil_18bytes", "普通纸质一维码", "", "PASS", "");
		TiaoMaInfo jiezhi2 = new TiaoMaInfo("不同介质卡片", "二维", "QR Code_40mil_18bytes", "普通纸质二维码", "", "PASS", "");
		TiaoMaInfo jiezhi3 = new TiaoMaInfo("不同介质卡片", "一维", "QR Code_40mil_18bytes", "硬皮纸质一维码", "", "PASS", "");
		TiaoMaInfo jiezhi4 = new TiaoMaInfo("不同介质卡片", "二维", "QR Code_40mil_18bytes", "硬皮纸质二维码", "", "PASS", "");
		//TiaoMaInfo jiezhi5 = new TiaoMaInfo("不同介质", "一维", "QR Code_40mil_18bytes", "薄膜塑料一维码", "", "PASS", "");
		//TiaoMaInfo jiezhi6 = new TiaoMaInfo("不同介质", "二维", "QR Code_40mil_18bytes", "薄膜塑料二维码", "", "PASS", "");
		//TiaoMaInfo jiezhi7 = new TiaoMaInfo("不同介质", "一维", "QR Code_40mil_18bytes", "铁皮介质一维码", "", "PASS", "");
		//TiaoMaInfo jiezhi8 = new TiaoMaInfo("不同介质", "二维", "QR Code_40mil_18bytes", "铁皮介质二维码", "", "FAIL", "");
		allList.add(jiezhi1);allList.add(jiezhi2);allList.add(jiezhi3);allList.add(jiezhi4);
		//allList.add(jiezhi5);allList.add(jiezhi6);allList.add(jiezhi7);allList.add(jiezhi8);
		
		TiaoMaInfo color1 = new TiaoMaInfo("彩色条码", "一维", "QR Code_40mil_18bytes", "彩色条码1", "", "PASS", ""); 
		TiaoMaInfo color11 = new TiaoMaInfo("彩色条码", "一维", "Code_40mil_18bytes", "彩色条码2", "", "PASS", ""); 
		TiaoMaInfo color2 = new TiaoMaInfo("彩色条码", "二维", "QR Code_40mil_18bytes", "彩色条码3", "", "FAIL", ""); 
		allList.add(color1);	allList.add(color11);allList.add(color2);
		
		TiaoMaInfo cmpDu1 = new TiaoMaInfo("对比度", "一维", "QR Code_40mil_18bytes", "一维码对比度测试-30%", "", "PASS", ""); 
		TiaoMaInfo cmpDu2 = new TiaoMaInfo("对比度", "二维", "QR Code_40mil_18bytes", "二维码对比度测试-30%", "", "FAIL", ""); 
		allList.add(cmpDu1);allList.add(cmpDu2);
		
		TiaoMaInfo wusun1 = new TiaoMaInfo("污损案例", "一维", "QR Code_40mil_18bytes", "最大污损率_QR", "", "PASS", "");
		TiaoMaInfo wusun11 = new TiaoMaInfo("污损案例", "一维", "Code_40mil_18bytes", "最大污损率_QR1", "", "PASS", "");
		TiaoMaInfo wusun2 = new TiaoMaInfo("污损案例", "二维", "QR Code_40mil_18bytes", "右上位置探测图形+分隔符+格式信息+版本信息污损_QR", "", "FAIL", "");
		allList.add(wusun1);
		allList.add(wusun11);
		allList.add(wusun2);

		FreemakerTest.reportGenerate("scancode.doc", info1, allList);
		try {
			Desktop myDesktop = Desktop.getDesktop();
			myDesktop.open(new File("scancode.doc"));
		} catch (IOException e1) {
			e1.printStackTrace();
			//JOptionPane.showMessageDialog(,"自动打开Word失败，请手动打开！", "打开报告",JOptionPane.WARNING_MESSAGE);
		}
		
//		RFTestInfo info = new RFTestInfo("百富S90", "pax tester", "PAX", "P3H0710642");
//
//		String[] aPoint25 = new String[25];
//		String[] bPoint25 = new String[25];
//
//		/*for(int i=0; i<aPoint25.length; i++) {
//			if (i % 3 == 0) {
//				aPoint25[i] = "FAIL";
//				bPoint25[i] = "FAIL";
//			} else {
//				aPoint25[i] = "PASS";
//				bPoint25[i] = "PASS";
//			}
//		}*/
//
//		FreemakerTest.reportGenerate("./reportmodel/", info, aPoint25, bPoint25);
//
//		double[] volages = new double[5];
//		for(int i=0; i<volages.length; i++)
//			volages[i] = new Random().nextDouble();
//
//		FreemakerTest.reportGenerate("cq.doc", info, volages);
	}

}

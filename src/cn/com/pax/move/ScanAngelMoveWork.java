package cn.com.pax.move;

import java.util.ArrayList;
import java.util.List;

import cn.com.pax.display.BarcodePanel;
import cn.com.pax.display.mainFrame;
import cn.com.pax.protocol.Protocol;
import cn.com.pax.protocol.SocketToC4;
import cn.com.pax.report.TiaoMaInfo;
import cn.com.pax.sericomm.SerialConnection;
import cn.pax.com.utils.*;

public class ScanAngelMoveWork  {

	private  SerialConnection connection;
	private String ip ;
	private int port ;
	
	private String D220ip ;
	private int D220port ;
	
	private static final String RL_ROTATE ="rl45 ";
	private static final String UD_ROTATE ="ud45 ";
	
	private String path = "./config/ConSocket.properties";
	private String path1 = "./config/D220CfgSocket.properties";
	//public static final Logger logger = LoggerFactory.getLogger(ScanAngelMoveWork.class);
	private static final String MINSTR ="40";
	private static final String MAXSTR ="130";
	private int initDis, connTimeout;
	
	public ScanAngelMoveWork(SerialConnection connection) {
		this.connection = connection;
		initData();
	}
	
	public void initData() {
		String initString = BarcodePanel.initDisField.getText().trim();
		if(initString.length() > 0) {
			initDis = Integer.parseInt(initString);
		} else {
			initDis = 0;
		}
		initString = BarcodePanel.serialPortScanTimeField.getText().trim();
		if(initString.length() > 0) {
			connTimeout = Integer.parseInt(initString) + 3000;
		} else {
			connTimeout = 5000;
		} 
	}
	
	public boolean angelWorkFlowingTwo(boolean isTwoWei, String strType){
		SocketToC4 socket_C4 = ReadSocketCfg.getSocket(path);
		SocketToC4 socket_D220 = ReadSocketCfg.getSocket(path1);//D220
		//boolean sockC4 = socket_C4.openConn();
		if(socket_C4 == null || !socket_C4.openConn()) {
			mainFrame.logger.error("连接机器人失败，请检查配置！!");
			return false;
		}
		if(socket_D220 == null || !socket_D220.openConn()) {
			mainFrame.logger.error("连接手机失败，请检查配置！!");
			socket_C4.closeConn();
			return false;
		}

		boolean flag = false;
		String sendStr = null;
		int i = 0;
		int rotateAngel = 0;
		//int timeOut = 5000;
		ReadProperties readProperties = new ReadProperties();
		//readProperties.ReadDepthProperties();
		String str1 = readProperties.getOne_Dim();
		if(isTwoWei)
			str1 = readProperties.getTwo_Dim();
		String []string1 = str1.split(",");
		//String one_Dim = string1[2];
		
		List<Integer> testResult_LRFailed = new ArrayList<Integer>();
		List<Integer> testResult_UDFailed = new ArrayList<Integer>();
		testResult_LRFailed.clear();
		testResult_UDFailed.clear();
	    byte []receive = new byte[2];
		boolean testResult = true;

		UtilsTool.updateView(BarcodePanel.excutingField, "1");
		UtilsTool.updateView(BarcodePanel.currentTestItemField, "扫描角度");
		UtilsTool.updateView(BarcodePanel.cardNumField, "1");
		UtilsTool.updateView(BarcodePanel.barcodeNameField, string1[1]);
		UtilsTool.updateView(BarcodePanel.barcodePre_contentArea, string1[0]);

		try {
			connection.openConnection();
			connection.readExtraData(100, connTimeout);
			connection.clearSendBeforeData();
		} catch (Exception e1) {
			socket_C4.closeConn();
			socket_D220.closeConn();
			connection.closeConnection();
			e1.printStackTrace();
			return false;
		}

		labelDo:do {
			testResult_LRFailed.clear();
			testResult_UDFailed.clear();
			StringBuilder leftBuilder = new StringBuilder();
			StringBuilder rightBuilder = new StringBuilder();
			while (true && StopMove.isCodeRunning()) {
				if (!flag) {
					String dis = readProperties.getOneone_Depth();
					if(isTwoWei)
						dis	= readProperties.getTwotwo_Depth();
					if(Integer.parseInt(dis)<=Integer.parseInt(MINSTR)){
						dis = MINSTR;
					}else if(Integer.parseInt(dis)>=Integer.parseInt(MAXSTR)) {
						dis = MAXSTR;
					}
					sendStr = RL_ROTATE + i + " " + dis;
					UtilsTool.updateView(BarcodePanel.scanDisField, dis + "");
					flag = true;
				} else {
					sendStr = RL_ROTATE + i + " " + rotateAngel;
				}
				if (i > 18) {
					String string = updateResult(testResult_LRFailed, "Left_Right_Rotate");
					mainFrame.logger.info("Left_Right_Failed: " + string);
					//BarcodePanel.barcodeDecodeContentArea.setText(string);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					break;
				}
				//休眠一下
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				if (rotateAngel<= 45) {
					//左角度45 -5
					UtilsTool.updateView(BarcodePanel.obliquAngleField, (45 - rotateAngel) + "");
				}else {
					//右角度 45 -0
					UtilsTool.updateView(BarcodePanel.obliquAngleField, (rotateAngel - 45) + "");
				}

				//清屏
				receive = Protocol.sendCmd2D220(socket_D220, null);
				if (receive[0]!=0x00) { break labelDo; }
				connection.readExtraData(100, connTimeout);
				connection.clearSendBeforeData();

				String string = socket_C4.writeReadOneLine(sendStr);
				mainFrame.logger.info("cmd: " + sendStr + " result: " + string);
				if (string.contains("null")) {
					break labelDo;
				}else if (string.contains("ok")){
					//显示
					receive = Protocol.sendCmd2D220(socket_D220, string1[2]);
					if (receive[0]!=0x00) { break labelDo; }
					byte[] recvBuf = new byte[32];
					boolean isTestPass = DataMatchUtils.getResultMatch(connection, string1[0], recvBuf, connTimeout);
					if (!isTestPass) {
						if (socket_C4.writeReadOneLine("shakeout 10 0").contains("ok")) {
							isTestPass = DataMatchUtils.getResultMatch(connection, string1[0], recvBuf, connTimeout);
						}
					}
					if (!isTestPass) {
						if (rotateAngel<= 45) {
							//左角度45 -5
							testResult_LRFailed.add(45 - (rotateAngel));
							leftBuilder.append(String.valueOf(45 - (rotateAngel)));
							leftBuilder.append(" ");
						}else {
							//右角度 45 -0
							testResult_LRFailed.add((rotateAngel) - 45 );
							rightBuilder.append(String.valueOf(rotateAngel - 45));
							rightBuilder.append(" ");
						}
					}
					else {
						UtilsTool.updateView(BarcodePanel.barcodeDecodeContentArea, string1[0]);
					}
					i++;
					rotateAngel += 5;
				}else {
					break labelDo;
				}
			}//end while

			if(!StopMove.isCodeRunning()) {
				socket_C4.closeConn();
				socket_D220.closeConn();
				connection.closeConnection();
				return false;
			}

			StringBuilder left = new StringBuilder();
			if (leftBuilder.length() >0) {
				 String [] str= leftBuilder.toString().trim().replace(" ", ",").split(",");
				 for (int k = 0; k < str.length; k++) {
					 left.append(str[str.length-k-1]);
					 left.append(" ");
				}
			}else {
				left.append("");
			}

			TiaoMaInfo you1 = new TiaoMaInfo("扫描角度", strType, string1[1], "倾斜角度（右）", "0~45°", leftBuilder.length() <=0 ? "PASS":"FAIL", left.toString().trim().replace(" ", ","));
			BarcodePanel.allList.add(you1);
			TiaoMaInfo zuo1 = new TiaoMaInfo("扫描角度", strType, string1[1], "倾斜角度（左）", "0~45°", rightBuilder.length() <=0 ? "PASS":"FAIL", rightBuilder.toString().trim().replace(" ", ","));
			BarcodePanel.allList.add(zuo1);
			mainFrame.logger.info("Right_Rotate: " + you1);
			mainFrame.logger.info("Left_Rotate: " + zuo1);

			if(leftBuilder.length() > 0 || rightBuilder.length() > 0)
				testResult = false;
			StringBuilder upBuilder = new StringBuilder();
			StringBuilder downBuilder = new StringBuilder();
			flag = false;
			i = 0;
			rotateAngel = 0;

			while (true && StopMove.isCodeRunning()) {
				if (!flag) {
					String dis = readProperties.getOneone_Depth();
					if(isTwoWei)
						dis = readProperties.getTwotwo_Depth();
					if(Integer.parseInt(dis)<= Integer.parseInt(MINSTR)){
						dis = MINSTR;
					}else if(Integer.parseInt(dis)>=Integer.parseInt(MAXSTR)) {
						dis = MAXSTR;
					}
					UtilsTool.updateView(BarcodePanel.scanDisField, dis + "");
					sendStr = UD_ROTATE + i +" "+ dis;
					flag = true;
				}else {
					sendStr = UD_ROTATE + i +" "+ rotateAngel;
				}
				if (i > 18) {
					// 更新结果并关闭socket
					String string = updateResult(testResult_UDFailed, "Up_Down_Rotate");
					mainFrame.logger.info("Up_Down_Failed: " + string);
					//BarcodePanel.barcodeDecodeContentArea.setText(string);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					break;
				}
				//休眠一下
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				if (rotateAngel <= 45) {
					UtilsTool.updateView(BarcodePanel.arcAngletField, (45 - (rotateAngel)) + "");
				}else {
					UtilsTool.updateView(BarcodePanel.arcAngletField, (rotateAngel -45) + "");
				}

				//清屏{
				receive = Protocol.sendCmd2D220(socket_D220, null);
				if (receive[0]!=0x00) { break labelDo; }
				connection.readExtraData(100);
				connection.clearSendBeforeData();
				String string = socket_C4.writeReadOneLine(sendStr);
				mainFrame.logger.info("cmd: " + sendStr + " result: " + string);
				if (string.contains("null")) {
					break labelDo;
				}else if (string.contains("ok")){
					//显示
					receive = Protocol.sendCmd2D220(socket_D220, string1[2]);
					if (receive[0]!=0x00) {
						break labelDo;
					}
					byte[] recvBuf = new byte[32];
					boolean isTestPass = DataMatchUtils.getResultMatch(connection, string1[0], recvBuf, connTimeout);
					if (!isTestPass) {
						if (socket_C4.writeReadOneLine("shakeout 10 1").contains("ok")) {
							isTestPass = DataMatchUtils.getResultMatch(connection, string1[0], recvBuf, connTimeout);
						}
					}
					if (!isTestPass) {
						if (rotateAngel <= 45) {
							testResult_UDFailed.add(45 - (rotateAngel));
							upBuilder.append(String.valueOf(45 - (rotateAngel)));
							upBuilder.append(" ");
						}else {
							testResult_UDFailed.add(rotateAngel - 45);
							downBuilder.append(String.valueOf(rotateAngel -45));
							downBuilder.append(" ");
						}
					}
					else {
						UtilsTool.updateView(BarcodePanel.barcodeDecodeContentArea, string1[0]);
					}
					i++;
					rotateAngel += 5;
				}else {
					break labelDo;
				}
			}//end while

			if(!StopMove.isCodeRunning()) {
				socket_C4.closeConn();
				socket_D220.closeConn();
				connection.closeConnection();
				return false;
			}

			StringBuilder up = new StringBuilder();
			if (upBuilder.length() >0) {
				 String [] str= upBuilder.toString().trim().replace(" ", ",").split(",");
				 for (int k = 0; k < str.length; k++) {
					 up.append(str[str.length-k-1]);
					 up.append(" ");
				}
			}else {
				up.append("");
			}	
			TiaoMaInfo shang1 = new TiaoMaInfo("扫描角度", strType, string1[1], "偏转角度（上）", "0~45°", upBuilder.length() <=0 ?"PASS":"FAIL", up.toString().trim().replace(" ", ","));
			BarcodePanel.allList.add(shang1);
			TiaoMaInfo xia1 = new TiaoMaInfo("扫描角度", strType, string1[1], "偏转角度（下）", "0~45°", downBuilder.length() <=0 ?"PASS":"FAIL", downBuilder.toString().trim().replace(" ", ","));
			BarcodePanel.allList.add(xia1);
			mainFrame.logger.info("Up_Rotate: " + shang1);
			mainFrame.logger.info("Down_Rotate: " + xia1);

			if(leftBuilder.length() > 0 || rightBuilder.length() > 0)
				testResult = false;

			String rlt = testResult ? "PASS" : "FAIL";
			UtilsTool.updateArea(BarcodePanel.testResultArea, "扫描角度", rlt);

			try {
				socket_C4.closeConn();
				socket_D220.closeConn();
				connection.closeConnection();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		} while (false);
		
		try {
			socket_C4.closeConn();
			socket_D220.closeConn();
			connection.closeConnection();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
//	public boolean angelWorkFlowingOne(){
//		SocketToC4 socket_C4 = ReadSocketCfg.getSocket(path);
//		SocketToC4 socket_D220 = ReadSocketCfg.getSocket(path1);//D220
//		//boolean sockC4 = socket_C4.openConn();
//		if(socket_C4 == null || !socket_C4.openConn()) {
//			mainFrame.logger.error("连接机器人失败，请检查配置！!");
//			return false;
//		}
//		if(socket_D220 == null || !socket_D220.openConn()) {
//			mainFrame.logger.error("连接手机失败，请检查配置！!");
//			socket_C4.closeConn();
//			return false;
//		}
//
//		boolean flag = false;
//		String sendStr = null;
//		int i = 0;
//		int rotateAngel = 0;
//		//int timeOut = 5000;
//		ReadProperties readProperties = new ReadProperties();
//		readProperties.ReadDepthProperties();
//		String str1 = readProperties.getOne_Dim();
//		String []string1 = str1.split(",");
//		//String one_Dim = string1[2];
//		boolean testResult = true;
//
//		List<Integer> testResult_LRFailed = new ArrayList<Integer>();
//		List<Integer> testResult_UDFailed = new ArrayList<Integer>();
//		testResult_LRFailed.clear();
//		testResult_UDFailed.clear();
//	    byte []receive = new byte[2];
//
//		BarcodePanel.scanDisField.setText("");
//		//BarcodePanel.rotation_angleField.setText("");
//		BarcodePanel.barcodeDecodeContentArea.setText("");
//		BarcodePanel.barcodePre_contentArea.setText("");
//		BarcodePanel.rotation_speedField.setText("");
//
//		BarcodePanel.excutingField.setText("1");
//		BarcodePanel.currentTestItemField.setText("扫描角度");
//		BarcodePanel.cardNumField.setText("1");
//		BarcodePanel.barcodeNameField.setText(string1[1]);
//		BarcodePanel.barcodePre_contentArea.setText(string1[0]);
//		try {
//			connection.openConnection();
//			connection.readExtraData(100, connTimeout);
//			connection.clearSendBeforeData();
//		} catch (Exception e1) {
//			BarcodePanel.moveButton.setEnabled(true);
//			socket_C4.closeConn();
//			socket_D220.closeConn();
//			connection.closeConnection();
//			e1.printStackTrace();
//			return false;
//		}
//		labelDo:do {
//			testResult_LRFailed.clear();
//			testResult_UDFailed.clear();
//			StringBuilder leftBuilder = new StringBuilder();
//			StringBuilder rightBuilder = new StringBuilder();
//			while (true && StopMove.isCodeRunning()) {
//				if (!flag) {
//					String dis = readProperties.getOneone_Depth();
//					if(Integer.parseInt(dis)<=Integer.parseInt(MINSTR)){
//						dis = MINSTR;
//					}else if(Integer.parseInt(dis)>=Integer.parseInt(MAXSTR)) {
//						dis = MAXSTR;
//					}
//					sendStr = RL_ROTATE + i + " " + dis;
//					flag = true;
//				} else {
//					sendStr = RL_ROTATE + i + " " + rotateAngel;
//				}
//				if (i > 18) {
//					String string = updateResult(testResult_LRFailed, "Left_Right_Rotate");
//					mainFrame.logger.info("Left_Right_Failed: " + string);
//					BarcodePanel.barcodeDecodeContentArea.setText(string);
//					try {
//						Thread.sleep(1000);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//					break;
//				}
//				//休眠一下
//				try {
//					Thread.sleep(100);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				//清屏
//				receive = Protocol.sendCmd2D220(socket_D220, null);
//				if (receive[0]!=0x00) {
//					break labelDo;
//				}
//				connection.readExtraData(100);
//				connection.clearSendBeforeData();
//				String string = socket_C4.writeReadOneLine(sendStr);
//				mainFrame.logger.info("cmd: " + sendStr + " result: " + string);
//				if (string .contains("null")) {
//					break labelDo;
//				}else if (string.contains("ok")){
//					//显示
//					receive = Protocol.sendCmd2D220(socket_D220, string1[2]);
//					if (receive[0]!=0x00) {
//						break labelDo;
//					}
//					byte[] recvBuf = new byte[32];
//					boolean isTestPass = DataMatchUtils.getResultMatch(connection, string1[0], recvBuf, connTimeout);
//					if (!isTestPass) {
//						if (socket_C4.writeReadOneLine("shakeout 10 0").contains("ok")) {
//							isTestPass = DataMatchUtils.getResultMatch(connection, string1[0], recvBuf, connTimeout);
//						}
//					}
//					if (!isTestPass) {
//						if (rotateAngel<= 45) {
//							//左角度45 -5
//							testResult_LRFailed.add(45 - (rotateAngel));
//							leftBuilder.append(String.valueOf(45 - (rotateAngel)));
//							leftBuilder.append(" ");
//						}else {
//							//右角度 45 -0
//							testResult_LRFailed.add((rotateAngel) - 45 );
//							rightBuilder.append(String.valueOf(rotateAngel - 45));
//							rightBuilder.append(" ");
//						}
//					}
//					i++;
//					rotateAngel += 5;
//				}else {
//					break labelDo;
//				}
//			}//end while
//
//			if(!StopMove.isCodeRunning()) {
//				socket_C4.closeConn();
//				socket_D220.closeConn();
//				connection.closeConnection();
//				return false;
//			}
//
//			StringBuilder left = new StringBuilder();
//			if (leftBuilder.length() >0) {
//				 String [] str= leftBuilder.toString().trim().replace(" ", ",").split(",");
//				 for (int k = 0; k < str.length; k++) {
//					 left.append(str[str.length-k-1]);
//					 left.append(" ");
//				}
//			}else {
//				left.append("");
//			}
//
//			TiaoMaInfo you1 = new TiaoMaInfo("扫描角度", "一维", string1[1], "倾斜角度（右）", "0~45°", leftBuilder.length() <=0 ? "PASS":"FAIL", left.toString().trim().replace(" ", ","));
//			BarcodePanel.allList.add(you1);
//			TiaoMaInfo zuo1 = new TiaoMaInfo("扫描角度", "一维", string1[1], "倾斜角度（左）", "0~45°", rightBuilder.length() <=0 ? "PASS":"FAIL", rightBuilder.toString().trim().replace(" ", ","));
//			BarcodePanel.allList.add(zuo1);
//			mainFrame.logger.info("Right_Rotate: " + you1);
//			mainFrame.logger.info("Left_Rotate: " + zuo1);
//
//			if(leftBuilder.length() > 0 || rightBuilder.length() > 0)
//				testResult = false;
//
//			StringBuilder upBuilder = new StringBuilder();
//			StringBuilder downBuilder = new StringBuilder();
//			flag = false;
//			i = 0;
//			rotateAngel = 0;
//			BarcodePanel.barcodeDecodeContentArea.setText("");
//			while (true && StopMove.isCodeRunning()) {
//				if (!flag) {
//					String dis = readProperties.getOneone_Depth();
//					if(Integer.parseInt(dis)<=Integer.parseInt(MINSTR)){
//						dis = MINSTR;
//					}else if(Integer.parseInt(dis)>=Integer.parseInt(MAXSTR)) {
//						dis = MAXSTR;
//					}
//					sendStr = UD_ROTATE + i +" "+ dis;
//					flag = true;
//				}else {
//					sendStr = UD_ROTATE + i +" "+ rotateAngel;
//				}
//				if (i > 18) {
//					// 更新结果并关闭socket
//					String string = updateResult(testResult_UDFailed, "Up_Down_Rotate");
//					mainFrame.logger.info("Up_Down_Failed: " + string);
//					BarcodePanel.barcodeDecodeContentArea.setText(string);
//					try {
//						Thread.sleep(1000);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//					break;
//				}
//				//休眠一下
//				try {
//					Thread.sleep(100);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				//清屏
//				receive = Protocol.sendCmd2D220(socket_D220, null);
//				if (receive[0]!=0x00) {
//					break labelDo;
//				}
//				connection.readExtraData(100);
//				connection.clearSendBeforeData();
//				String string = socket_C4.writeReadOneLine(sendStr);
//				mainFrame.logger.info("cmd: " + sendStr + " result: " + string);
//				if (string .contains("null")) {
//					break labelDo;
//				}else if (string.contains("ok")){
//					//显示
//					receive = Protocol.sendCmd2D220(socket_D220, string1[2]);
//					if (receive[0]!=0x00) {
//						break labelDo;
//					}
//					byte[] recvBuf = new byte[32];
//					boolean isTestPass = DataMatchUtils.getResultMatch(connection, string1[0], recvBuf, connTimeout);
//					if (!isTestPass) {
//						if (socket_C4.writeReadOneLine("shakeout 10 1").contains("ok")) {
//							isTestPass = DataMatchUtils.getResultMatch(connection, string1[0], recvBuf, connTimeout);
//						}
//					}
//					if (!isTestPass) {
//						if (rotateAngel <= 45) {
//							testResult_UDFailed.add(45 - (rotateAngel));
//							upBuilder.append(String.valueOf(45 - (rotateAngel)));
//							upBuilder.append(" ");
//						}else {
//							testResult_UDFailed.add(rotateAngel - 45);
//							downBuilder.append(String.valueOf(rotateAngel -45));
//							downBuilder.append(" ");
//						}
//					}
//					i++;
//					rotateAngel += 5;
//				}else {
//					break labelDo;
//				}
//			}//end while
//
//			if(!StopMove.isCodeRunning()) {
//				socket_C4.closeConn();
//				socket_D220.closeConn();
//				connection.closeConnection();
//				return false;
//			}
//
//			StringBuilder up = new StringBuilder();
//			if (upBuilder.length() >0) {
//				 String [] str= upBuilder.toString().trim().replace(" ", ",").split(",");
//				 for (int k = 0; k < str.length; k++) {
//					 up.append(str[str.length-k-1]);
//					 up.append(" ");
//				}
//			}else {
//				up.append("");
//			}
//
//			TiaoMaInfo shang1 = new TiaoMaInfo("扫描角度", "一维", string1[1], "偏转角度（上）", "0~45°", upBuilder.length() <=0 ?"PASS":"FAIL", up.toString().trim().replace(" ", ","));
//			BarcodePanel.allList.add(shang1);
//			TiaoMaInfo xia1 = new TiaoMaInfo("扫描角度", "一维", string1[1], "偏转角度（下）", "0~45°", downBuilder.length() <=0 ?"PASS":"FAIL", downBuilder.toString().trim().replace(" ", ","));
//			BarcodePanel.allList.add(xia1);
//			mainFrame.logger.info("Up_Rotate: " + shang1);
//			mainFrame.logger.info("Down_Rotate: " + xia1);
//
//			if(leftBuilder.length() > 0 || rightBuilder.length() > 0)
//				testResult = false;
//
//			if(testResult) {
//				BarcodePanel.testResultArea.append("扫描角度：PASS\n");
//			}
//			else {
//				BarcodePanel.testResultArea.append("扫描角度：FAIL\n");
//			}
//
//			try {
//				socket_C4.closeConn();
//				socket_D220.closeConn();
//				connection.closeConnection();
//				return true;
//			} catch (Exception e) {
//				e.printStackTrace();
//				return false;
//			}
//		} while (false);
//
//		try {
//			socket_C4.closeConn();
//			socket_D220.closeConn();
//			connection.closeConnection();
//			return false;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return false;
//		}
//	}
	@SuppressWarnings("rawtypes")
	private  String updateResult(List<Integer> testResult, String str) {
		StringBuilder builder = new StringBuilder();
		if (testResult.size()>0) {
			
			for (int j = 0; j < testResult.size(); j++) {
				String testResultString = testResult.get(j).toString();
				if(j == testResult.size() - 1)
					builder.append(testResultString + " ");
				else
					builder.append(testResultString + ",");
			}
			builder.append(str+" Failed" + "\n");
			//GUIFrame.barcodeDecodeContentArea.setText(builder.toString() + "Failed");
		}else {
			//GUIFrame.barcodeDecodeContentArea.setText("Success");
			builder.append(str+" Success"+ "\n");
		}
		return builder.toString();
	}

	
	public static void main(String[] args) {
		StringBuilder builder = new StringBuilder();
		/*builder.append("45");
		builder.append(" ");
		builder.append("25");
		builder.append(" ");
		builder.append("15");
		builder.append(" ");*/
		System.out.println(builder.length());
		System.out.println(builder.toString().trim().replace(" ", ","));
	}
}

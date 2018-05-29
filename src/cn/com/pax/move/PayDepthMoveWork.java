package cn.com.pax.move;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JFrame;

import cn.com.pax.display.BarcodePanel;
import cn.com.pax.display.mainFrame;
import cn.com.pax.protocol.Protocol;
import cn.com.pax.protocol.SocketToC4;
import cn.com.pax.report.TiaoMaInfo;
import cn.com.pax.sericomm.SerialConnection;
import cn.pax.com.utils.*;

public class PayDepthMoveWork {
	private String ip ;
	private int port ;
	private String D220ip;
	private int D220port;
	private static SerialConnection connection;
	private String path = "./config/ConSocket.properties";
	private String path1 = "./config/D220CfgSocket.properties";
	private static final String TL_CFG = "./config/scanDepth.properties";
	private static Properties prop = new Properties();
	private static final String PAYDEPTH = "dofone ";
	private int initDis, connTimeout; 
	
	public PayDepthMoveWork(SerialConnection connection) {
		PayDepthMoveWork.connection = connection;
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
			connTimeout = Integer.parseInt(initString) + 1000;
		} else {
			connTimeout = 3000;
		} 
	}
	
//	public  boolean  depthWorkFlowingTwo(){
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
//		//String initDis = BarcodePanel.initDisField.getText().trim();
//		String sendStr = null;
//		int moveDis = 0;
//		int minDis = 0;
//		int maxDis =0;
//		int scanDis =0;
//		int i = 0;
//		ReadProperties readProperties = new ReadProperties();
//		//String str1 = readProperties.getOne_Dim();
//		//String []string1 = str1.split(",");
//		//String one_Dim = string1[2];
//		String fileName = null;
//
//		String str2 = readProperties.getTwo_Dim();
//		String []string2 = str2.split(",");
//		//String two_Dim = string2[2];
//
//		String contend = null;
////		boolean writeFlag = false;
//		//boolean reportFlag = false;
//		boolean flag = false;
//		//boolean isOneOrTwo  = true;
//
//		byte[]receive = new byte[2];
//
//		BarcodePanel.scanDisField.setText("");
//		BarcodePanel.rotation_angleField.setText("");
//		BarcodePanel.barcodeDecodeContentArea.setText("");
//		BarcodePanel.barcodePre_contentArea.setText("");
//		BarcodePanel.rotation_speedField.setText("");
//		BarcodePanel.currentTestItemField.setText("扫描景深");
//		BarcodePanel.cardNumField.setText("1");
//
//		TiaoMaInfo depth = null;
//		try {
//			connection.openConnection();
//			connection.readExtraData(100, connTimeout);
//			connection.clearSendBeforeData();
//		} catch (Exception e) {
//			return false;
//		}
//
//		//二维扫描头只测试二维景深
//		labelDo: do {
//			//labelFor: for (int j = 0; j < 2; j++) {
//			labelFor: for (int j = 0; j < 1; j++) {
//				// 清零变量
//				moveDis = 0;
//				i = 0;
//				maxDis = 0;
//				minDis = 0;
//				scanDis = 0;
//				//BarcodePanel.scanDisField.setText("");
//				//BarcodePanel.barcodeDecodeContentArea.setText("");
//				BarcodePanel.barcodeNameField.setText("");
//				BarcodePanel.excutingField.setText((j + 1) + "");
////				if (!isOneOrTwo) {
////					// 发送一维图片
////					BarcodePanel.barcodePre_contentArea.setText(string1[0]);
////					BarcodePanel.barcodeNameField.setText(string1[1]);
////					contend = string1[0];
////					fileName = string1[2];
////					isOneOrTwo = true;
////				} else {
//				// 发送二维图片
//				BarcodePanel.barcodePre_contentArea.setText(string2[0]);
//				BarcodePanel.barcodeNameField.setText(string2[1]);
//				contend = string2[0];
//				fileName = string2[2];
//				//isOneOrTwo = false;
//				flag = false;
//				//}
//
//				while (true & StopMove.isCodeRunning()) {
//
//					BarcodePanel.scanDisField.setText(moveDis + initDis + "");
//
//					if (!flag) {
//						sendStr = PAYDEPTH + i +" "+ 0;
//						flag = true;
//					}else {
//						sendStr = PAYDEPTH + i +" "+ moveDis;
//					}
//					//清屏
//					receive = Protocol.sendCmd2D220(socket_D220, null);
//					//System.out.println("11111");
//					if (receive[0]!=0x00) { break labelDo;}
//					connection.readExtraData(100);
//					connection.clearSendBeforeData();
//					String string = socket_C4.writeReadOneLine(sendStr);
//					mainFrame.logger.info("cmd: " + sendStr + " result: " + string);
//					if (string.contains("null")) {
//						break labelDo;
//					}else if(string.contains("ok")){
//						//显示条码
//						receive = Protocol.sendCmd2D220(socket_D220, fileName);
//						if (receive[0]!=0x00) { break labelDo;}
//						byte[] recvBuf = new byte[32];
//						boolean isTestPass = DataMatchUtils.getResultMatch(connection, contend, recvBuf, connTimeout);
//						if (isTestPass) {
//							minDis = moveDis + initDis;
//							break;
//						}else {
//							if (moveDis > 200) {
//								//写入报告
////								if (!reportFlag) {
////									depth = new TiaoMaInfo("扫描景深", "一维", string1[1], "", "景深范围 > 100mm", "FAIL", "景深> 200mm,一直扫描不到");
////									BarcodePanel.allList.add(depth);
////									reportFlag = true;
////								}else{
//								depth = new TiaoMaInfo("扫描景深", "二维", string2[1], "", "景深范围 > 100mm", "FAIL", "景深> 200mm,一直扫描不到");
//								BarcodePanel.allList.add(depth);
//									//reportFlag = false;
//								//}
//								updateUi1(moveDis + initDis);
//								mainFrame.logger.info("#paydepth:" + depth.toString());
//								mainFrame.logger.info("#paydepth:" + (moveDis + initDis) + " " + "FAIL#");
//								continue labelFor;
//							}
//							i++;
//							moveDis += 10;
//						}
//					}else {
//						break labelDo;
//					}
//				}//end while
//				i++;
//				moveDis += 10;
//				while (true && StopMove.isCodeRunning()) {
//
//					BarcodePanel.scanDisField.setText(moveDis + initDis + "");
//
//					//清屏
//					receive = Protocol.sendCmd2D220(socket_D220, null);
//					if (receive[0]!=0x00) {break labelDo;}
//					connection.readExtraData(100);
//					connection.clearSendBeforeData();
//					sendStr = PAYDEPTH + i +" "+ moveDis;
//					String string = socket_C4.writeReadOneLine(sendStr);
//					mainFrame.logger.info("cmd: " + sendStr + " result: " + string);
//					if (string.contains("null")) {
//						break labelDo;
//					}else if (string.contains("ok")) {
//						//显示条码
//						receive = Protocol.sendCmd2D220(socket_D220, fileName);
//						if (receive[0]!=0x00) { break labelDo;}
//
//						byte[] recvBuf = new byte[32];
//						boolean isTestPass = DataMatchUtils.getResultMatch(connection, contend, recvBuf, connTimeout);
//						if(isTestPass){
//							maxDis = moveDis + initDis;
//							scanDis = maxDis - minDis;
//							if (scanDis>200) {
//
////								if (!reportFlag) {
////									depth = new TiaoMaInfo("扫描景深", "一维", string1[1], "", "景深范围 > 100mm", "PASS","" + minDis + "~" + maxDis + "" );
////									BarcodePanel.allList.add(depth);
////									reportFlag = true;
////								}else{
//								depth = new TiaoMaInfo("扫描景深", "二维", string2[1], "", "景深范围 > 100mm", "PASS","" + minDis + "~" + maxDis + "");
//								BarcodePanel.allList.add(depth);
//
//								updateUi2(minDis, maxDis, scanDis);
//								mainFrame.logger.info("#paydepth:" + depth.toString());
//								mainFrame.logger.info("#paydepth:" + ""+minDis+"~"+maxDis+""+"="+scanDis+""+" PASS");
//									//reportFlag = false;
//								//}
//								break;
//							}
//							i++;
//							moveDis += 10;
//							continue;
//						}else {
//							//读不到扫描结果的时候，继续走？还是再次读?
//							i++;
//							moveDis += 10;
//							//清屏
//							receive = Protocol.sendCmd2D220(socket_D220, null);
//							if (receive[0]!=0x00) {break labelDo;}
//							connection.readExtraData(100);
//							connection.clearSendBeforeData();
//							sendStr = PAYDEPTH + i +" "+ moveDis;
//							string = socket_C4.writeReadOneLine(sendStr);
//							mainFrame.logger.info("cmd: " + sendStr + " result: " + string);
//							if (string .contains("null")) {
//								break labelDo;
//							}else if (string.contains("ok")){
//								//显示条码
//								receive = Protocol.sendCmd2D220(socket_D220, fileName);
//								if (receive[0]!=0x00) {break labelDo;}
//								byte[] recvBuf1 = new byte[32];
//								isTestPass = DataMatchUtils.getResultMatch(connection, contend, recvBuf1, connTimeout);
//								if (isTestPass) {
//									 maxDis = moveDis + initDis;
//									 scanDis = maxDis - minDis;//Integer.parseInt(initDis);
//									 if (scanDis>200) {
////										 if (!reportFlag) {
////											depth = new TiaoMaInfo("扫描景深", "一维", string1[1], "", "景深范围 > 100mm", "PASS","" + minDis + "~" + maxDis + "" );
////											BarcodePanel.allList.add(depth);
////											reportFlag = true;
////										 }else{
//										depth = new TiaoMaInfo("扫描景深", "二维", string2[1], "", "景深范围 > 100mm", "PASS","" + minDis + "~" + maxDis + "");
//										BarcodePanel.allList.add(depth);
//										updateUi2(minDis, maxDis, scanDis);
//										mainFrame.logger.info("#paydepth:" + depth.toString());
//										mainFrame.logger.info("#paydepth:" + ""+minDis+"~"+maxDis+""+"="+scanDis+""+" PASS");
//											//reportFlag = false;
//										 //}
//										 break;
//									}
//									i++;
//									moveDis += 10;
//									continue;
//								}else {//表示两次都读不到情况下
//									moveDis -= 20;
//									maxDis = moveDis + initDis;
//									scanDis = maxDis - minDis ;
//									//处理极端的情况下
////									if (scanDis >200) {
////										//记录报告在
//////										if (!reportFlag) {
//////											depth = new TiaoMaInfo("扫描景深", "一维", string1[1], "", "景深范围 > 100mm", "PASS","" + minDis + "~" + maxDis + "" );
//////											BarcodePanel.allList.add(depth);
//////											reportFlag = true;
//////										}else{
////											depth = new TiaoMaInfo("扫描景深", "二维", string2[1], "", "景深范围 > 100mm", "PASS","" + minDis + "~" + maxDis + "");
////											BarcodePanel.allList.add(depth);
////											updateUi2(minDis, maxDis, scanDis);
////											mainFrame.logger.info("#paydepth:" + depth.toString());
////											mainFrame.logger.info("#paydepth:" + ""+minDis+"~"+maxDis+""+"="+scanDis+""+" PASS");
////											//reportFlag = false;
////										//}
////										break;
////									}else {
//									String res11 = "";
//									if (scanDis>100) {
//										res11 = "PASS";
//									}else {
//										res11 = "FAIL";
//									}
//
//									depth = new TiaoMaInfo("扫描景深", "二维", string2[1], "", "景深范围 > 100mm", res11,"" + minDis + "~" + maxDis + "");
//									BarcodePanel.allList.add(depth);
//									updateUi2(minDis, maxDis, scanDis);
//									mainFrame.logger.info("#paydepth:" + depth.toString());
//									mainFrame.logger.info("#paydepth:" + ""+minDis+"~"+maxDis+""+"="+scanDis+" "+ res11);
//									break;
//									//}
//								}
//							}else {
//								break labelDo;
//							}
//						}
//					}else {
//						break labelDo;
//					}
//				}//end while
//
//				if(!StopMove.isCodeRunning()) break labelDo;
//              //写文件
//
//				BarcodePanel.scanDisField.setText(maxDis + "");
////				if (!writeFlag) {
////					writeDepthToFile("twoone", String.valueOf((maxDis+minDis)/2));
////					writeFlag = true;
////				}else {
//				writeDepthToFile("twotwo", String.valueOf((maxDis+minDis)/2));
//					//writeFlag = false;
//				//}
//				if(maxDis - minDis > 100) {
//					BarcodePanel.testResultArea.append("扫描景深：PASS\n");
//				}
//				else {
//					BarcodePanel.testResultArea.append("扫描景深：FAIL\n");
//				}
//				try {
//					Thread.sleep(2000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}// end for
//
//			//关闭资源
//			try {
//				connection.closeConnection();
//				socket_C4.closeConn();
//				socket_D220.closeConn();
//				return true;
//			} catch (Exception e) {
//				return false;
//			}
//		} while (false);
//
//		try {
//			connection.closeConnection();
//			socket_C4.closeConn();
//			socket_D220.closeConn();
//			return false;
//		} catch (Exception e) {
//			return false;
//		}
//	}
	
	public boolean depthWorkFlowingOne(boolean isTwoWei, String strType){
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
		
		//int timeOut = 3000;
		//String initDis = BarcodePanel.initDisField.getText().trim();
		String sendStr = null;
		int moveDis = 0;
		int minDis = 0;
		int maxDis = 0;
		int scanDis = 0;
		int i = 0;
		ReadProperties readProperties = new ReadProperties();
		String str = readProperties.getOne_Dim();
		if(isTwoWei)
			str = readProperties.getTwo_Dim();
		String []string1 = str.split(",");
		String filenName = string1[2];

		boolean flag = false;
		byte[]receive = new byte[2];

		UtilsTool.updateView(BarcodePanel.currentTestItemField,"扫描景深");
		UtilsTool.updateView(BarcodePanel.barcodeNameField, string1[1]);
		UtilsTool.updateView(BarcodePanel.excutingField, "1");
		UtilsTool.updateView(BarcodePanel.cardNumField, "1");
		UtilsTool.updateView(BarcodePanel.barcodePre_contentArea, string1[0]);
		String contend = string1[0];
		TiaoMaInfo depth = null;
		try {
			connection.openConnection();
			connection.readExtraData(100, connTimeout);
			connection.clearSendBeforeData();
		} catch (Exception e) {
			BarcodePanel.moveButton.setEnabled(true);
			return false;
		}
		boolean testover = false;

		labelDo: do {
			while (true && StopMove.isCodeRunning()) {
				UtilsTool.updateView(BarcodePanel.scanDisField, moveDis + initDis + "");

				if (!flag) {
					sendStr = PAYDEPTH + i +" "+ 0;
					flag = true;
				}else {
					sendStr = PAYDEPTH + i +" "+ moveDis;
				}
				//清屏
				receive = Protocol.sendCmd2D220(socket_D220, null);
				if (receive[0]!=0x00) {
					mainFrame.logger.error(receive[0] + "");
					break labelDo;
				}
			
				connection.readExtraData(100);
				connection.clearSendBeforeData();
				
				String string = socket_C4.writeReadOneLine(sendStr);
				mainFrame.logger.info("cmd: " + sendStr + " result: " + string);
				if (string .contains("null")) { break labelDo; }
				else if(string.contains("ok")){
					//显示条码
					receive = Protocol.sendCmd2D220(socket_D220, filenName);
					if (receive[0]!=0x00) {
						mainFrame.logger.error(receive[0] + "");
						break labelDo;
					}
					byte[] recvBuf = new byte[32];
					boolean isTestPass = DataMatchUtils.getResultMatch(connection, contend, recvBuf, connTimeout);
					if (isTestPass) {
						UtilsTool.updateView(BarcodePanel.barcodeDecodeContentArea, contend);
						minDis = moveDis + initDis;
						break;
					}else {
						if (moveDis > 200) {
							//写入报告
							depth = new TiaoMaInfo("扫描景深", strType, string1[1], "", "景深范围 >= 100mm", "FAIL", "景深> 200mm,一直扫描不到");
							BarcodePanel.allList.add(depth);
							UtilsTool.updateView(BarcodePanel.barcodeDecodeContentArea, "FAIL");
							//updateUi1(moveDis);
							mainFrame.logger.info("#paydepth:" + depth.toString());
							mainFrame.logger.info("#paydepth:" + (moveDis + initDis) + " " + "FAIL#");
							testover = true;
							break;
						}
						i++;
						moveDis += 10;
					}
				}else{
					break labelDo;
				}	
			}//end while
			if (!testover) {
				i++;
				moveDis += 10;
				while (true && StopMove.isCodeRunning()) {

					UtilsTool.updateView(BarcodePanel.scanDisField, moveDis + initDis + "");

					//清屏
					receive = Protocol.sendCmd2D220(socket_D220, null);
					if (receive[0]!=0x00) {
						mainFrame.logger.error(receive[0] + "");
						break labelDo;
					}

					connection.readExtraData(100, connTimeout);
					connection.clearSendBeforeData();
					sendStr = PAYDEPTH + i +" "+ moveDis;
					String string = socket_C4.writeReadOneLine(sendStr);
					mainFrame.logger.info("cmd: " + sendStr + " result: " + string);
					if (string .contains("null")) {
						break labelDo;
					}else if (string.contains("ok")) {
						//显示条码
						receive = Protocol.sendCmd2D220(socket_D220, filenName);
						if (receive[0]!=0x00) {
							mainFrame.logger.error(receive[0] + "");
							break labelDo;
						}
						byte[] recvBuf = new byte[32];
						boolean isTestPass = DataMatchUtils.getResultMatch(connection, contend, recvBuf, connTimeout);
						if(isTestPass){
							UtilsTool.updateView(BarcodePanel.barcodeDecodeContentArea, contend);
							maxDis = moveDis + initDis;
							scanDis = maxDis - minDis; 
							if (scanDis > 200) {
								depth = new TiaoMaInfo("扫描景深", strType, string1[1], "", "景深范围 >= 100mm", "PASS","" + minDis + "~" + maxDis + "" );
								BarcodePanel.allList.add(depth);
								//updateUi2(minDis, maxDis, scanDis);
								mainFrame.logger.info("#paydepth:" + depth.toString());
								mainFrame.logger.info("#paydepth:" + ""+minDis+"~"+maxDis+""+"="+scanDis+""+" PASS");
								break;
							}
							i++;
							moveDis += 10;
							continue;
						}else {
							//读不到扫描结果的时候，继续走？还是再次读? 
							i++;
							moveDis += 10;
							//清屏
							receive = Protocol.sendCmd2D220(socket_D220, null);
							if (receive[0]!=0x00) {
								mainFrame.logger.error(receive[0] + "");
								break labelDo;
							}

							connection.readExtraData(100, connTimeout);
							connection.clearSendBeforeData();
							sendStr = PAYDEPTH + i +" "+ moveDis;
							string = socket_C4.writeReadOneLine(sendStr);
							mainFrame.logger.info("cmd: " + sendStr + " result: " + string);
							if (string .contains("null")) {
								break labelDo;
							}else if (string.contains("ok")){
								//显示条码
								receive = Protocol.sendCmd2D220(socket_D220, filenName);
								if (receive[0]!=0x00) {
									mainFrame.logger.error(receive[0] + "");
									break labelDo;
								}
								byte[] recvBuf1 = new byte[32];
								isTestPass = DataMatchUtils.getResultMatch(connection, contend, recvBuf1, connTimeout);
								if (isTestPass) {
									UtilsTool.updateView(BarcodePanel.barcodeDecodeContentArea, contend);
									maxDis = moveDis + initDis;
									scanDis = maxDis - minDis;//Integer.parseInt(initDis);
									if (scanDis>200) {
										//updateUi2(minDis, maxDis, scanDis);
										mainFrame.logger.info("#paydepth:" + depth.toString());
										mainFrame.logger.info("#paydepth:" + ""+minDis+"~"+maxDis+""+"="+scanDis+""+" PASS");
										depth = new TiaoMaInfo("扫描景深", strType, string1[1], "", "景深范围 >= 100mm", "PASS","" + minDis + "~" + maxDis + "" );
										BarcodePanel.allList.add(depth);
										break;
									}
									i++;
									moveDis += 10;
									continue;	
								}else {//表示两次都读不到情况下
									moveDis -= 20;
									maxDis = moveDis + initDis;
									scanDis = maxDis - minDis ;
									//处理极端的情况下
//									if (scanDis >200) {
//										//记录报告在
//										depth = new TiaoMaInfo("扫描景深", "一维", string1[1], "", "景深范围 > 100mm", "PASS","" + minDis + "~" + maxDis + "" );
//										BarcodePanel.allList.add(depth);
//										updateUi2(minDis, maxDis, scanDis);
//										mainFrame.logger.info("#paydepth:" + depth.toString());
//										mainFrame.logger.info("#paydepth:" + ""+minDis+"~"+maxDis+""+"="+scanDis+""+" PASS");
//										break;
//									}else {
									String res11 = "";
									if (scanDis>=100) {
										res11 = "PASS";
									}else {
										res11 = "FAIL";
									}
									depth = new TiaoMaInfo("扫描景深", strType, string1[1], "", "景深范围 >= 100mm", res11,"" + minDis + "~" + maxDis + "" );
									BarcodePanel.allList.add(depth);
									//updateUi2(minDis, maxDis, scanDis);
									mainFrame.logger.info("#paydepth:" + depth.toString());
									mainFrame.logger.info("#paydepth:" + ""+minDis+"~"+maxDis+""+"="+scanDis+""+ res11);
									break;
									//}
								}
							}else {
								break labelDo;
							}
						}
					}else {
						break;
					}
				}//end while
			}//end if

			if(!StopMove.isCodeRunning()) break labelDo;

			UtilsTool.updateView(BarcodePanel.scanDisField, maxDis + "");

          //写文件
			if (!testover){
				if(isTwoWei)
					writeDepthToFile("twotwo", String.valueOf((maxDis+minDis)/2));
				else
					writeDepthToFile("oneone", String.valueOf((maxDis+minDis)/2));
			}

			String rlt = maxDis - minDis > 100 ? "PASS" : "FAIL";
			UtilsTool.updateArea(BarcodePanel.testResultArea, "扫描景深", rlt);
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//最后清屏
			/*if (socketD220) {
				receive = Protocol.sendCmd2D220(D220socket, null);
				if (receive[0]!=0x00) {
					break labelDo;
				}
			}else {
				break labelDo;
			}	*/
			//关闭资源
			try {
				connection.closeConnection();
				socket_C4.closeConn();
				socket_D220.closeConn();
				return true;
			} catch (Exception e) {
				return false;
			}
		} while (false);
			
		try {
			connection.closeConnection();
			socket_C4.closeConn();
			socket_D220.closeConn();
			return false;
		} catch (Exception e) {
			return false;
		}
		
	}

	private  void updateUi2(final int minDis, final int maxDis, final int scanDis) {
		try {
			EventQueue.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					if (scanDis>100) {
						//BarcodePanel.scanDisField.setText(scanDis+"");
						BarcodePanel.barcodeDecodeContentArea.setText(""+minDis+"~"+maxDis+""+"="+scanDis+""+" PASS");
					}else {
						//BarcodePanel.scanDisField.setText(scanDis+"");
						BarcodePanel.barcodeDecodeContentArea.setText(""+minDis+"~"+maxDis+""+"="+scanDis+""+" FAIL");
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private  void updateUi1(final int moveDis) {
		try {
			EventQueue.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					//BarcodePanel.scanDisField.setText(moveDis+"");
					BarcodePanel.barcodeDecodeContentArea.setText(moveDis+""+"FAIL");
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static  void writeDepthToFile(String dim,String dis){
		File scanDepthConfig = new File(TL_CFG);
		if (!scanDepthConfig.exists()) {
			try {
				scanDepthConfig.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} 
		
		try {
			 //prop.load(new FileInputStream(scanDepthConfig)); 
			FileInputStream fin = new FileInputStream(scanDepthConfig);
			prop.load(fin);
			fin.close();
			System.out.println(prop);
			prop.setProperty(dim, dis);
			FileOutputStream fout = new FileOutputStream(scanDepthConfig);
			prop.store(fout, "scan depth mid_distance");
			fout.close();	
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		PayDepthMoveWork .writeDepthToFile("twoone", String.valueOf((180+20)/2));
	}
}

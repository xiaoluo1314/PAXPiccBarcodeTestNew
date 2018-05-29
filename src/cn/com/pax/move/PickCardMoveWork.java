package cn.com.pax.move;

import java.awt.EventQueue;
import java.awt.Label;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;

import cn.pax.com.utils.*;
import sun.security.action.PutAllAction;

import cn.com.pax.display.BarcodePanel;
import cn.com.pax.display.mainFrame;
import cn.com.pax.protocol.SocketToC4;
import cn.com.pax.report.TiaoMaInfo;
import cn.com.pax.sericomm.SerialConnection;

public class PickCardMoveWork implements Runnable{
	private boolean isRunning;
	List<Integer>moveList = new ArrayList<Integer>();
	private   String ip ;
	private   int port ;
	private  JFrame jFrame;
	private SerialConnection connection;//串口连接对象
	private String path = "./config/ConSocket.properties";
	private static final String PICKCARD = "pickcard 0";
	private static final String CMD = "dofone ";
	//private String string;
	private int initDis, connTimeout; 
	
	//private static int timeOut = 10;
	//private static int timeOut = 2000;
	public PickCardMoveWork(List<Integer>moveList, SerialConnection connection, JFrame jf){
		this.moveList = moveList;
		this.connection = connection;
		jFrame = jf;
		isRunning = true;
		initData();
	}
//	public PickCardMoveWork(SerialConnection connection, JFrame jf){
//		this.connection = connection;
//		jFrame = jf;
//		isRunning = true;
//		initData();
//	}
	
	public void initData() {
		String initString = BarcodePanel.initDisField.getText().trim();
		if(initString.length() > 0) {
			initDis = Integer.parseInt(initString);
		} else {
			initDis = 0;
		}
		initString = BarcodePanel.serialPortScanTimeField.getText().trim();
		if(initString.length() > 0) {
			connTimeout = Integer.parseInt(initString);
		} else {
			connTimeout = 2000;
		} 
	}
	
	public void run() {
		SocketToC4 socket_C4 = ReadSocketCfg.getSocket(path);
		//boolean sockC4 = socket_C4.openConn();
		if(socket_C4 == null || !socket_C4.openConn()) {
			BarcodePanel.moveButton.setEnabled(true);
			mainFrame.logger.error("连接机器人失败，请检查配置！!");
			JOptionPane.showMessageDialog(jFrame, "连接机器人失败，请检查配置！", "不同介质卡片", JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		cardMoveWorkOne(socket_C4);
		if(!StopMove.isCodeRunning()) {
			JOptionPane.showMessageDialog(jFrame, "用户停止测试，请调整卡片顺序后再测试！", "不同介质卡片", JOptionPane.WARNING_MESSAGE);
		}
		else {
			JOptionPane.showMessageDialog(jFrame, "不同介质卡片测试完成，请更换工装继续测试或生成报告！", "不同介质卡片", JOptionPane.WARNING_MESSAGE);
		}
		BarcodePanel.moveButton.setEnabled(true);
//		if ("二维".equals(BarcodePanel.scanTypeBox.getSelectedItem())) {
//			cardMoveWorkTwo(socket);
//		}else {
//			if ("一维".equals(BarcodePanel.scanTypeBox.getSelectedItem())) {
//				cardMoveWorkOne(socket);
//			}
//		}	
	}
//	private void cardMoveWorkTwo(SocketToC4 socket){
//		int k = 0;	
//		List<CardInfo>cardInfos = ReadCardInfo.getCardInfo();
//		BarcodePanel.cardNumField.setText(String.valueOf(cardInfos.size()));
//		String sendString = null;
//		BarcodePanel.scanDisField.setText("");
//		BarcodePanel.rotation_angleField.setText("");
//		BarcodePanel.barcodeDecodeContentArea.setText("");
//		BarcodePanel.barcodePre_contentArea.setText("");
//		BarcodePanel.rotation_speedField.setText("");
//		//String initDis = BarcodePanel.initDisField.getText().trim();
//		//ReadProperties readProperties = new ReadProperties();
//		//readProperties.ReadDepthProperties();
//		int i = 0;
//		//要考虑初始距离
//		
//		int dis = 0;
//		try {
//			connection.openConnection();
//			connection.readExtraData(100, connTimeout);
//			connection.clearSendBeforeData();
//		} catch (Exception e) {
//			connection.closeConnection();
//			e.printStackTrace();
//			JOptionPane.showMessageDialog(jFrame, "串口打开失败", "不同介质", JOptionPane.WARNING_MESSAGE);
//			return;
//		}
//		if (socket.openConn()) {
//			for (int j = 0; j < cardInfos.size(); j++) { //总卡数
//				i = 0;
//				if(initDis > 30)
//					dis = 0;
//				else {
//					dis = 30 - initDis;
//				}
////				BarcodePanel.barcodeDecodeContentArea.setText("");
//				BarcodePanel.barcodeDecodeContentArea.setText("");
//				k++;
//				CardInfo barcodeInfo = cardInfos.get(j);
//				BarcodePanel.barcodePre_contentArea.setText(barcodeInfo.getBarcodeContent());
//				BarcodePanel.currentTestItemField.setText(barcodeInfo.getBarcodeType());
//				BarcodePanel.barcodeNameField.setText(barcodeInfo.getBarcodeName());
//				BarcodePanel.excutingField.setText(k+"");
//				BarcodePanel.scanDisField.setText((initDis + dis) + "");
//				
//				sendString = PICKCARD ;
//				String string = socket.writeReadOneLine(sendString);
//				if (string.contains("ok")) {
//					connection.readExtraData(100, connTimeout);
//					connection.clearSendBeforeData();
//					while (true) {
//						sendString = CMD + i +" "+ dis;
//						String str = socket.writeReadOneLine(sendString);
//						if (str.contains("ok")) {
//							byte[] recvBuf = new byte[32];
//							boolean isTestPass = DataMatchUtils.getResultMatch(connection,barcodeInfo.getBarcodeContent(), recvBuf, connTimeout);
//							if (isTestPass) {
//								TestResult(barcodeInfo, isTestPass);
//								TiaoMaInfo jiezhi = new TiaoMaInfo("不同介质卡片", barcodeInfo.getScanType(), barcodeInfo.getBarcodeName(), barcodeInfo.getCardType(), "", isTestPass ==true? "PASS":"FAIL", (initDis + dis) + "");
//								BarcodePanel.allList.add(jiezhi);
//								mainFrame.logger.info("#pickcard1:" +"条码名称:" + barcodeInfo.getBarcodeName()+":" +"条码类型:" + barcodeInfo.getCardType() + ":" +"距离:"+ ((initDis + dis) +"") + (isTestPass== true ? "PASS":"FAIL")+"#" );
//								//放卡
//								if (socket.writeReadOneLine("putcard").contains("ok")) {
//									
//								} else {
//									//BarcodePanel.moveButton.setEnabled(true);	
//									socket.closeConn();
//									connection.closeConnection();
//									JOptionPane.showMessageDialog(jFrame, "放置卡片失败，测试中止！", "不同介质", JOptionPane.WARNING_MESSAGE);
//									return;
//								}
//								break;
//							}else {
//								if (dis >200) {
//									TestResult(barcodeInfo, isTestPass);
//									TiaoMaInfo jiezhi = new TiaoMaInfo("不同介质卡片", barcodeInfo.getScanType(), barcodeInfo.getBarcodeName(), barcodeInfo.getCardType(), "", isTestPass ==true? "PASS":"FAIL", (initDis + dis) + "");
//									BarcodePanel.allList.add(jiezhi);
//									mainFrame.logger.info("#pickcard1:" +"条码名称:" + barcodeInfo.getBarcodeName()+":" +"条码类型:" + barcodeInfo.getCardType() + ":" +"距离:"+ ((initDis + dis) +"") + (isTestPass== true ? "PASS":"FAIL")+"#" );
//									//放卡
//									if (socket.writeReadOneLine("putcard").contains("ok")) {
//										
//									} else {
//										//BarcodePanel.moveButton.setEnabled(true);	
//										socket.closeConn();
//										connection.closeConnection();
//										JOptionPane.showMessageDialog(jFrame, "放置卡片失败，测试中止！", "不同介质", JOptionPane.WARNING_MESSAGE);
//										return;
//									}
//									break;
//								}
//								i++;
//								dis +=10;
//							}
//						}else {
//							//BarcodePanel.moveButton.setEnabled(true);	
//							socket.closeConn();
//							connection.closeConnection();
//							JOptionPane.showMessageDialog(jFrame, "放置卡片失败，测试中止！", "不同介质", JOptionPane.WARNING_MESSAGE);
//							return;
//						}	
//					}
//				}else {
//					//BarcodePanel.moveButton.setEnabled(true);	
//					socket.closeConn();
//					connection.closeConnection();
//					JOptionPane.showMessageDialog(jFrame, "机器人移动失败，测试中止！", "不同介质", JOptionPane.WARNING_MESSAGE);
//					return;
//				}
//			}//end for
//			try {
//				//BarcodePanel.moveButton.setEnabled(true);	
//				socket.closeConn();
//				connection.closeConnection();
//			} catch (Exception e) {
//				e.printStackTrace();
//				//connection.closeConnection();
//				//BarcodePanel.moveButton.setEnabled(true);
//				//return;
//			}	
//		}else {
//			//BarcodePanel.moveButton.setEnabled(true);	
//			connection.closeConnection();
//			JOptionPane.showMessageDialog(jFrame, "和机器人通信失败，请检查！", "不同介质", JOptionPane.WARNING_MESSAGE);
//			return;
//		}		
//	}

	/*private void cardMoveWorkTwo1(SocketToC4 socket) {
	
		int k = 0;
		BarcodePanel.moveButton.setEnabled(false);		
		List<CardInfo>cardInfos = ReadCardInfo.getCardInfo();
		BarcodePanel.cardNumField.setText(String.valueOf(cardInfos.size()));
		String sendString;
		BarcodePanel.scanDisField.setText("");
		BarcodePanel.rotation_angleField.setText("");
		BarcodePanel.barcodeDecodeContentArea.setText("");
		BarcodePanel.barcodePre_contentArea.setText("");
		//String initDis = BarcodePanel.initDisField.getText().trim();
		ReadProperties readProperties = new ReadProperties();
		readProperties.ReadDepthProperties();
		if (socket.openConn()) {
			for (int j = 0; j < cardInfos.size(); j++) { //总卡数
				
				BarcodePanel.barcodeDecodeContentArea.setText("");
				BarcodePanel.barcodeDecodeContentArea.setText("");
				k++;
				CardInfo barcodeInfo = cardInfos.get(j);
				BarcodePanel.barcodePre_contentArea.setText(barcodeInfo.getBarcodeContent());
				BarcodePanel.currentTestItemField.setText(barcodeInfo.getBarcodeType());
				BarcodePanel.barcodeNameField.setText(barcodeInfo.getBarcodeName());
				BarcodePanel.excutingField.setText(k+"");
				if ("一维".equals(barcodeInfo.getScanType())) {
					 sendString = PICKCARD +" "+ readProperties.getTwoone_Depth();//二维扫描头一维景深
				}else {
					 sendString = PICKCARD +" "+ readProperties.getTwotwo_Depth();//二维扫描头二维景深
				}
				if ("一维".equals(barcodeInfo.getCardType())) {
					System.out.println("1111"+readProperties.getTwoone_Depth());
					 sendString = PICKCARD + " " + readProperties.getTwoone_Depth();
					 
				}else {
					System.out.println("2222"+readProperties.getTwotwo_Depth());
					sendString = PICKCARD + " " + readProperties.getTwotwo_Depth();
				}
				
				connection.openConnection();
				connection.clearSendBeforeData();
				String string = socket.writeReadOneLine(sendString);
				if (string.contains("ok")) {
					if (connection.isOpen()) {
						connection.clearSendBeforeData();
						long startTime = System.currentTimeMillis();
						byte[] recvBuf = new byte[32];
						boolean isTestPass = DataMatchUtils.getResultMatch(connection,barcodeInfo.getBarcodeContent(), recvBuf, timeOut);
						connection.closeConnection();
						//boolean isTestPass = serialRead(connection, datalen, barcodeInfo.getBarcodeContent(),timeOut);
						TestResult(barcodeInfo, startTime, isTestPass);
						TiaoMaInfo jiezhi = new TiaoMaInfo("不同介质卡片", barcodeInfo.getScanType(), barcodeInfo.getBarcodeName(), barcodeInfo.getCardType(), "", isTestPass ==true? "PASS":"FAIL", "");
						BarcodePanel.allList.add(jiezhi);
						mainFrame.logger.info("#pickcard1:" + barcodeInfo.getBarcodeName()+":" + barcodeInfo.getCardType() + ":" + (isTestPass== true ? "PASS":"FAIL")+"#" );
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
							
						}
						
					}else {
						BarcodePanel.moveButton.setEnabled(true);	
						socket.closeConn();
						connection.closeConnection();
						JOptionPane.showMessageDialog(jFrame, " Serial Open Failed", "Serial Open", JOptionPane.WARNING_MESSAGE);
						return;
					}
					
				}else {
					BarcodePanel.moveButton.setEnabled(true);	
					socket.closeConn();
					JOptionPane.showMessageDialog(jFrame, " Socket Receive Failed", "Socket Receive", JOptionPane.WARNING_MESSAGE);
					return;
				}
					
				
			}//end for
			
			BarcodePanel.moveButton.setEnabled(true);	
			socket.closeConn();
			connection.closeConnection();
			
		}else {
			BarcodePanel.moveButton.setEnabled(true);	
			socket.closeConn();
			connection.closeConnection();
			JOptionPane.showMessageDialog(jFrame, " Socket Open Failed", "Socket Connect", JOptionPane.WARNING_MESSAGE);
			return;
		}
}
	*/

	private void cardMoveWorkOne(SocketToC4 socket) {
		int k = 0;
		int i = 0;
		int dis = 30;

		List<CardInfo>cardInfos = ReadCardInfo.getCardInfo();

		UtilsTool.updateView(BarcodePanel.cardNumField, String.valueOf(moveList.size()));

		try {
			connection.openConnection();
			connection.readExtraData(100, connTimeout);
			connection.clearSendBeforeData();
		} catch (Exception e1) {
			connection.closeConnection();
			e1.printStackTrace();
			JOptionPane.showMessageDialog(jFrame, "串口打开失败", "不同介质卡片", JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		int testResult = 1;

		//停止后，卡片顺序错误，慎用停止
		for (int j = 0; j < cardInfos.size() && StopMove.isCodeRunning(); j++) { //总卡数
			i = 0;
			if(initDis > 30)
				dis = 0;
			else {
				dis = 30 - initDis;
			}
			//BarcodePanel.barcodeDecodeContentArea.setText("");
			//BarcodePanel.barcodeDecodeContentArea.setText("");
			if (moveList.contains(j)) {
				//包含则运动 扫描
				k++;
				CardInfo barcodeInfo = cardInfos.get(j);
				UtilsTool.updateView(BarcodePanel.barcodePre_contentArea, barcodeInfo.getBarcodeContent());
				UtilsTool.updateView(BarcodePanel.currentTestItemField, barcodeInfo.getBarcodeType());
				UtilsTool.updateView(BarcodePanel.barcodeNameField, barcodeInfo.getBarcodeName());
				UtilsTool.updateView(BarcodePanel.excutingField, k+"");
				UtilsTool.updateView(BarcodePanel.scanDisField, (initDis + dis) + "");

				if (socket.writeReadOneLine("pickcard 0").contains("ok")) {
					connection.readExtraData(100, connTimeout);
					connection.clearSendBeforeData();
					while (true) {
						UtilsTool.updateView(BarcodePanel.scanDisField, (initDis + dis) + "");

						String sendString = CMD + i +" "+ dis;
						String str = socket.writeReadOneLine(sendString);
						mainFrame.logger.info("cmd: " + sendString + " result: " + str);
						if (str.contains("ok")) {
							byte[] recvBuf = new byte[32];
							boolean isTestPass = DataMatchUtils.getResultMatch(connection,barcodeInfo.getBarcodeContent(), recvBuf, connTimeout);
							if (isTestPass) {
								TestResult(barcodeInfo, isTestPass);
								TiaoMaInfo jiezhi = new TiaoMaInfo("不同介质卡片", barcodeInfo.getScanType(), barcodeInfo.getBarcodeName(), barcodeInfo.getCardType(), "", isTestPass ==true? "PASS":"FAIL", (initDis + dis) + "");
								BarcodePanel.allList.add(jiezhi);
								mainFrame.logger.info("#pickcard1:" +"条码名称:" + barcodeInfo.getBarcodeName()+":" +"条码类型:" + barcodeInfo.getCardType() + ":" +"距离:"+ ((initDis + dis) +"") + (isTestPass== true ? "PASS":"FAIL")+"#" );
								//放卡
								if (socket.writeReadOneLine("putcard").contains("ok")) {
								} else {
									socket.closeConn();
									connection.closeConnection();
									JOptionPane.showMessageDialog(jFrame, "放置卡片失败，测试中止！", "不同介质卡片", JOptionPane.WARNING_MESSAGE);
									return;
								}
								break;
							}else {
								if (dis >200) {
									TestResult(barcodeInfo, isTestPass);
									TiaoMaInfo jiezhi = new TiaoMaInfo("不同介质卡片", barcodeInfo.getScanType(), barcodeInfo.getBarcodeName(), barcodeInfo.getCardType(), "", isTestPass ==true? "PASS":"FAIL", (initDis + dis) +"");
									BarcodePanel.allList.add(jiezhi);
									mainFrame.logger.info("#pickcard1:" +"条码名称:" + barcodeInfo.getBarcodeName()+":" +"条码类型:" + barcodeInfo.getCardType() + ":" +"距离:"+ ((initDis + dis) +"") + (isTestPass== true ? "PASS":"FAIL")+"#" );

									testResult = 0;
									//放卡
									if (socket.writeReadOneLine("putcard").contains("ok")) {
									} else {
										socket.closeConn();
										connection.closeConnection();
										JOptionPane.showMessageDialog(jFrame, "放置卡片失败，测试中止！", "不同介质卡片", JOptionPane.WARNING_MESSAGE);
										return;
									}
									break;
								}
								i++;
								dis +=10;
							}
						}else {
							//BarcodePanel.moveButton.setEnabled(true);
							socket.closeConn();
							connection.closeConnection();
							JOptionPane.showMessageDialog(jFrame, "放置卡片失败，测试中止！", "不同介质卡片", JOptionPane.WARNING_MESSAGE);
							return;
						}
					}
				}else {
					//BarcodePanel.moveButton.setEnabled(true);
					socket.closeConn();
					connection.closeConnection();
					JOptionPane.showMessageDialog(jFrame, "机器人移动失败，测试中止！", "不同介质卡片", JOptionPane.WARNING_MESSAGE);
					return;
				}
			}else {
				//处理二维，直接放卡
				String sendString = "pickcard -1";
				String string = socket.writeReadOneLine(sendString);
				if (string.contains("ok")) {
					continue;
				}else {
					socket.closeConn();
					connection.closeConnection();
					JOptionPane.showMessageDialog(jFrame, "和机器人通信失败，请检查！", "不同介质卡片", JOptionPane.WARNING_MESSAGE);
					return;
				}
			}
		}

		String rlt = testResult == 1 ? "PASS" : "FAIL";
		UtilsTool.updateArea(BarcodePanel.testResultArea, "不同介质卡片", rlt);

		socket.closeConn();
		connection.closeConnection();
	}
	
	
//	private void cardMoveWorkOne1(SocketToC4 socket) {
//			int k = 0;
//			BarcodePanel.moveButton.setEnabled(false);		
//			BarcodePanel.cardNumField.setText(String.valueOf(moveList.size()));
//			List<CardInfo>cardInfos = ReadCardInfo.getCardInfo();
//			byte[] recvBuf = new byte[32];
//			BarcodePanel.scanDisField.setText("");
//			BarcodePanel.rotation_angleField.setText("");
//			BarcodePanel.barcodeDecodeContentArea.setText("");
//			BarcodePanel.barcodePre_contentArea.setText("");
//			ReadProperties readProperties = new ReadProperties();
//			readProperties.ReadDepthProperties();
//			//String initDis = BarcodePanel.initDisField.getText().trim();
//			if (socket.openConn()) {
//				for (int j = 0; j < cardInfos.size(); j++) { //总卡数
//					BarcodePanel.barcodeDecodeContentArea.setText("");
//					BarcodePanel.barcodeDecodeContentArea.setText("");
//					if (moveList.contains(j)) {
//						//包含则运动 扫描
//						k++;
//						CardInfo barcodeInfo = cardInfos.get(j);
//						BarcodePanel.barcodePre_contentArea.setText(barcodeInfo.getBarcodeContent());
//						BarcodePanel.currentTestItemField.setText(barcodeInfo.getBarcodeType());
//						BarcodePanel.barcodeNameField.setText(barcodeInfo.getBarcodeName());
//						BarcodePanel.excutingField.setText(k+"");
//						
//						String sendString = PICKCARD +" "+ readProperties.getOneone_Depth();
//						connection.openConnection();
//						connection.clearSendBeforeData();
//						String string = socket.writeReadOneLine(sendString);
//						if (string.contains("ok")) {
//							if (connection.isOpen()) {
//								connection.clearSendBeforeData();
//								long startTime = System.currentTimeMillis();
//								boolean isTestPass = DataMatchUtils.getResultMatch(connection, barcodeInfo.getBarcodeContent(), recvBuf, connTimeout);
//								connection.closeConnection();
//								TestResult(barcodeInfo, startTime, isTestPass);
//								TiaoMaInfo jiezhi = new TiaoMaInfo("不同介质卡片", barcodeInfo.getScanType(), barcodeInfo.getBarcodeName(), barcodeInfo.getCardType(), "", isTestPass ==true? "PASS":"FAIL", "");
//								BarcodePanel.allList.add(jiezhi);
//								mainFrame.logger.info("#pickcard1:" + barcodeInfo.getBarcodeName()+":" + barcodeInfo.getCardType() + ":" + (isTestPass== true ? "PASS":"FAIL")+"#" );
//								try {
//									Thread.sleep(1000);
//								} catch (InterruptedException e) {
//									e.printStackTrace();
//									
//								}
//								
//							}else {
//								BarcodePanel.moveButton.setEnabled(true);	
//								socket.closeConn();
//								connection.closeConnection();
//								JOptionPane.showMessageDialog(jFrame, " Serial Open Failed", "Serial Open", JOptionPane.WARNING_MESSAGE);
//								return;
//							}
//							
//						}else {
//							BarcodePanel.moveButton.setEnabled(true);	
//							socket.closeConn();
//							JOptionPane.showMessageDialog(jFrame, " Socket Receive Failed", "Socket Receive", JOptionPane.WARNING_MESSAGE);
//							return;
//						}
//						
//					}else {
//						//处理二维，直接放卡
//						String sendString = PICKCARD + "-1";
//						String string = socket.writeReadOneLine(sendString);
//						if (string.contains("ok")) {
//							try {
//								Thread.sleep(3000);
//							} catch (InterruptedException e) {
//								e.printStackTrace();
//							}
//							continue;
//						}else {
//							BarcodePanel.moveButton.setEnabled(true);	
//							socket.closeConn();
//							JOptionPane.showMessageDialog(jFrame, " Socket Receive Pickup Failed", "Socket Receive Pickup", JOptionPane.WARNING_MESSAGE);
//							return;
//						}
//					}
//				}//end for
//				
//				BarcodePanel.moveButton.setEnabled(true);	
//				socket.closeConn();
//				connection.closeConnection();
//				
//			}else {
//				BarcodePanel.moveButton.setEnabled(true);	
//				socket.closeConn();
//				connection.closeConnection();
//				JOptionPane.showMessageDialog(jFrame, " Socket Open Failed", "Socket Connect", JOptionPane.WARNING_MESSAGE);
//				return;
//			}
//	}
	
	public static void TestResult(final CardInfo barcodeInfo, boolean isTestPass) {
		try {
			if (isTestPass) {
				EventQueue.invokeAndWait(new Runnable() {
				//EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						BarcodePanel.barcodeDecodeContentArea.setText(barcodeInfo.getBarcodeContent());
					}
				});
			}
			else{
				EventQueue.invokeLater(new Runnable() {
				//EventQueue.invokeAndWait(new Runnable() {
					public void run() {
						BarcodePanel.barcodeDecodeContentArea.setText("FAILED");
					}
				});	
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
	
	}
	
	
//	public static void TestResult(final CardInfo barcodeInfo, long startTime, boolean isTestPass) {
//		try {
//			if (isTestPass) {
//				//EventQueue.invokeAndWait(new Runnable() {
//				EventQueue.invokeLater(new Runnable() {
//					@Override
//					public void run() {
//						// TODO Auto-generated method stub
//						BarcodePanel.barcodeDecodeContentArea.setText(barcodeInfo.getBarcodeContent());
//						
//					}
//				});
//			}
//			else{
//				EventQueue.invokeLater(new Runnable() {
//				//EventQueue.invokeAndWait(new Runnable() {
//					public void run() {
//						BarcodePanel.barcodeDecodeContentArea.setText("FAILED");
//					}
//				});	
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		long endTime = System.currentTimeMillis();
//		//System.out.println("" + endTime);
//		
//		if (endTime - startTime < timeOut) {
//			try {
//				Thread.sleep(timeOut -(endTime - startTime));
//			} catch (InterruptedException e) {
//				
//				e.printStackTrace();
//			}
//		}
//	}
/*	public static boolean serialRead (SerialConnection connection,int dataLen, String str, int timeOut){
		
	
		String receiveContend = null ;
		boolean ret = false;
		Pattern pattern = Pattern.compile("SCANST"+str+"SCANED");
		for(int i=0; i<timeOut; i++) {
			byte []b = connection.serialRead(dataLen,1);
			if(b[0] != 0) {
				receiveContend = new String(b);
				//System.out.println(receiveContend);
				Matcher matcher = pattern.matcher(receiveContend);
				boolean b1 = matcher.find();
				if (b1) {
					ret = true;
					break;
				}
				
				
			}
		}
		//SCANST010720SCANED
		connection.closeConnection();
		return ret;
	}*/
	
	public synchronized boolean isRunning() {
		return isRunning;
	}

	public synchronized void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}
}

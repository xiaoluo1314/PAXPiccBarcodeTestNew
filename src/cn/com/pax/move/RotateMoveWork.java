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

public class RotateMoveWork  {
	private SerialConnection connection;
	private String ip ;
	private int port ;
	private String D220ip;
	private int D220port;
	public static final String CIRCOMMAND ="rotate360 ";
	private String path = "./config/ConSocket.properties";
	private String path1 = "./config/D220CfgSocket.properties";
	
	public RotateMoveWork(SerialConnection connection) {
		this.connection = connection;
	}
	
	public boolean rotateWorkFlowingTwo(){
		SocketToC4 socket_C4 = ReadSocketCfg.getSocket(path);
		SocketToC4 socket_D220 = ReadSocketCfg.getSocket(path1);//D220
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
		int timeOut = 3000;
		List<Integer> testResultFailed = new ArrayList<Integer>();
		testResultFailed.clear();
		byte[] recvBuf = new byte[32];
		
		ReadProperties readProperties = new ReadProperties();
		String str1 = readProperties.getTwo_Dim();
		String []string1 = str1.split(",");
		byte[]receive = new byte[2] ;

		UtilsTool.updateView(BarcodePanel.currentTestItemField,"360旋转角度");
		UtilsTool.updateView(BarcodePanel.cardNumField,"1");
		UtilsTool.updateView(BarcodePanel.excutingField, "1");

		try {
			connection.openConnection();
			connection.clearSendBeforeData();
		} catch (Exception e1) {
			e1.printStackTrace();
			connection.closeConnection();
			socket_C4.closeConn();
			socket_D220.closeConn();
			return false;
		}
		boolean testResult = true;

		labelDo:do {
			UtilsTool.updateView(BarcodePanel.barcodeNameField, string1[1]);
			UtilsTool.updateView(BarcodePanel.barcodePre_contentArea, string1[0]);

			while (true && StopMove.isCodeRunning()) {
				if (!flag) {
					String twoDis = readProperties.getTwotwo_Depth();
					sendStr = CIRCOMMAND + i + " " + twoDis;
					flag = true;
					UtilsTool.updateView(BarcodePanel.scanDisField, twoDis);
				} else {
					sendStr = CIRCOMMAND + i + " " + rotateAngel;
					if (i <= 12) {
						UtilsTool.updateView(BarcodePanel.rotation_angleField, String.valueOf(rotateAngel));
					}
				}
				if (i > 12) {
					updateResult(testResultFailed);
					break;
				}
				//清屏
				receive = Protocol.sendCmd2D220(socket_D220, null);
				if (receive[0]!=0x00) {
					mainFrame.logger.error(receive[0] + "");
					break labelDo;
				}

				connection.readExtraData(100);
				connection.clearSendBeforeData();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				String string = socket_C4.writeReadOneLine(sendStr);
				mainFrame.logger.info("cmd: " + sendStr + " result: " + string);
				if (string.contains("null")) {
					break labelDo;
				}else if(string.contains("ok")){
					//显示图片
					receive = Protocol.sendCmd2D220(socket_D220, string1[2]);
					if (receive[0]!=0x00) {
						mainFrame.logger.error(receive[0] + "");
						break labelDo;
					}
				}else {
					break labelDo;
				}
				boolean isTestPass = DataMatchUtils.getResultMatch(connection, string1[0], recvBuf, timeOut);
			    if (!isTestPass) {
			    	if (socket_C4.writeReadOneLine("shakeout 5 1").contains("ok")) {
			    		isTestPass = DataMatchUtils.getResultMatch(connection, string1[0], recvBuf, timeOut);
					}
				}
				TiaoMaInfo r3601 = new TiaoMaInfo("360旋转角度", "二维", string1[1], "", rotateAngel +"", isTestPass== true ? "PASS":"FAIL", ""); 
				BarcodePanel.allList.add(r3601);
				if (!isTestPass) {
					testResult = false;
					testResultFailed.add(rotateAngel);
				} else {
					UtilsTool.updateView(BarcodePanel.barcodeDecodeContentArea, string1[0]);
				}
				i++;
				rotateAngel += 30;
			}//end while

			if(!StopMove.isCodeRunning()) break;

			String rlt = testResult ? "PASS" : "FAIL";
			UtilsTool.updateArea(BarcodePanel.testResultArea, "360旋转角度", rlt);

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
	
	/*
	public boolean rotateWorkFlowingTwo(){
		ReadSocketCfg.readSocketConfig(path);
		ip = ReadSocketCfg.getIP();
		port = ReadSocketCfg.getPort();
		SocketToC4 socket = new SocketToC4(ip, port);
		
		ReadSocketCfg.readSocketConfig(path1);
		D220ip = ReadSocketCfg.getIP();
		D220port = ReadSocketCfg.getPort();
		SocketToC4 D220socket = new SocketToC4(D220ip, D220port);
		
		boolean flag = false;
		//String initDis = BarcodePanel.initDisField.getText().trim();
		String sendStr = null;
		int i = 0;
		int rotateAngel = 0;
		int timeOut = 3000;
		List testResultFailed = new ArrayList ();
		
		ReadProperties readProperties = new ReadProperties();
		readProperties.ReadDepthProperties();
		String str1 = readProperties.getOne_Dim();
		String []string1 = str1.split(",");
		
		String str2 = readProperties.getTwo_Dim();
		String []string2 = str2.split(",");
		String fileName = null;
		String contend = null;
		boolean sock = socket.openConn();
		boolean sockD220 = D220socket.openConn();
		boolean isOneOrTwo  = false;
		boolean reportFlag = false;
		byte[]receive = new byte[2] ;
		
		String dis; 

		BarcodePanel.scanDisField.setText("");
		BarcodePanel.rotation_angleField.setText("");
		BarcodePanel.barcodeDecodeContentArea.setText("");
		BarcodePanel.barcodePre_contentArea.setText("");
		
		BarcodePanel.currentTestItemField.setText("360旋转角度");
		BarcodePanel.cardNumField.setText("2");
		try {
			connection.openConnection();
			connection.clearSendBeforeData();
		} catch (Exception e1) {
			e1.printStackTrace();
			BarcodePanel.moveButton.setEnabled(true);
			connection.closeConnection();
			socket.closeConn();
			D220socket.closeConn();
			return false;
		}
		labelDo:do {
			for (int j = 0; j < 2; j++) {
				BarcodePanel.scanDisField.setText("");
				BarcodePanel.barcodeDecodeContentArea.setText("");
				BarcodePanel.barcodeNameField.setText("");
				BarcodePanel.excutingField.setText((j+1)+"");
				BarcodePanel.rotation_angleField.setText("");
				testResultFailed.clear();
				rotateAngel = 0;
				i = 0;
				if (!isOneOrTwo) {
					BarcodePanel.barcodePre_contentArea.setText(string1[0]);
					BarcodePanel.barcodeNameField.setText(string1[1]);
					fileName = string1[2];
					dis = readProperties.getTwoone_Depth();
					contend = string1[0];
					isOneOrTwo = true;
				}else {
					BarcodePanel.barcodePre_contentArea.setText(string2[0]);
					BarcodePanel.barcodeNameField.setText(string2[1]);
					fileName = string2[2];
					dis = readProperties.getTwotwo_Depth();
					contend = string2[0];
					isOneOrTwo = false;
					flag = false;
				}
				if (sock) {
					while (true) {
						if (!flag) {
							sendStr = CIRCOMMAND + i + " " + dis;
							flag = true;
						} else {
							sendStr = CIRCOMMAND + i + " " + rotateAngel;
							if (i <= 12) {
								BarcodePanel.rotation_angleField.setText(String.valueOf(rotateAngel));
							}
						}
						if (i > 12) {//72
							updateResult(socket, testResultFailed);
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							break;
						}
						//清屏
						if (sockD220) {
							receive = Protocol.sendCmd2D220(D220socket, null);
							if (receive[0]!=0x00) {
								break labelDo;
							}
						}else {
							break labelDo;
						}
						connection.readExtraData(100);
						connection.clearSendBeforeData();
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						String string = socket.writeReadOneLine(sendStr);
						if (string == null) {
							break labelDo;	
						}else if(string.contains("ok")){
							//显示图片
							if (sockD220) {
								receive = Protocol.sendCmd2D220(D220socket, fileName);
								if (receive[0]!=0x00) {
									break labelDo;
								}
							}else {
								break labelDo;
							}
							byte[] recvBuf = new byte[32];
							boolean isTestPass = DataMatchUtils.getResultMatch(connection, contend, recvBuf, timeOut);
							  if (!isTestPass) {
							    	if (socket.writeReadOneLine("shakeout 5").contains("ok")) {
							    		isTestPass = DataMatchUtils.getResultMatch(connection, string1[0], recvBuf, timeOut);
									}
								}
							if (!reportFlag) {
								mainFrame.logger.info(rotateAngel + "");
								TiaoMaInfo r3601 = new TiaoMaInfo("360旋转角度", "一维", string1[1], "", rotateAngel+"", isTestPass== true ? "PASS":"FAIL", ""); 
								BarcodePanel.allList.add(r3601);
								reportFlag = true;
							}else{
								mainFrame.logger.info(rotateAngel + "");
								TiaoMaInfo r3602 = new TiaoMaInfo("360旋转角度", "二维", string2[1], "", rotateAngel+"", isTestPass== true ? "PASS":"FAIL", "");  
								BarcodePanel.allList.add(r3602);
								reportFlag = false;
							}
								
							
							if (!isTestPass) {
								testResultFailed.add(rotateAngel);
							}
							
							i++;
							rotateAngel += 30;
						}
					}//end while
					
				}else {
					break labelDo;
				}
				
			}//end for
			
			try {
				BarcodePanel.moveButton.setEnabled(true);
				connection.closeConnection();
				socket.closeConn();
				D220socket.closeConn();
				return true;
			} catch (Exception e) {
				BarcodePanel.moveButton.setEnabled(true);
				connection.closeConnection();
				socket.closeConn();
				D220socket.closeConn();
				e.printStackTrace();
				return false;
			}
		} while (false);
		try {
			BarcodePanel.moveButton.setEnabled(true);
			connection.closeConnection();
			socket.closeConn();
			D220socket.closeConn();
			return false;
		} catch (Exception e) {
			BarcodePanel.moveButton.setEnabled(true);
			connection.closeConnection();
			socket.closeConn();
			D220socket.closeConn();
			e.printStackTrace();
			return false;
		}
	}*/
	
//	public boolean rotateWorkFlowingOne(){
//		ReadSocketCfg.readSocketConfig(path);
//		ip = ReadSocketCfg.getIP();
//		port = ReadSocketCfg.getPort();
//		SocketToC4 socket = new SocketToC4(ip, port);
//		
//		ReadSocketCfg.readSocketConfig(path1);
//		D220ip = ReadSocketCfg.getIP();
//		D220port = ReadSocketCfg.getPort();
//		SocketToC4 D220socket = new SocketToC4(D220ip, D220port);
//		
//		boolean flag = false;
//		//String initDis = BarcodePanel.initDisField.getText().trim();
//		String sendStr = null;
//		int i = 0;
//		int rotateAngel = 0;
//		int timeOut = 3000;
//		List testResultFailed = new ArrayList ();
//		testResultFailed.clear();
//		byte[] recvBuf = new byte[32];
//		
//		ReadProperties readProperties = new ReadProperties();
//		readProperties.ReadDepthProperties();
//		String str1 = readProperties.getOne_Dim();
//		String []string1 = str1.split(",");
//		boolean sock = socket.openConn();
//		boolean sockD220 = D220socket.openConn();
//		byte[]receive = new byte[2] ;
//		
//		BarcodePanel.scanDisField.setText("");
//		BarcodePanel.rotation_angleField.setText("");
//		BarcodePanel.barcodeDecodeContentArea.setText("");
//		BarcodePanel.barcodePre_contentArea.setText("");
//		BarcodePanel.rotation_speedField.setText("");
//		
//		BarcodePanel.currentTestItemField.setText("360旋转角度");
//		BarcodePanel.cardNumField.setText("1");
//		BarcodePanel.excutingField.setText("1");
//		try {
//			connection.openConnection();
//			connection.clearSendBeforeData();
//		} catch (Exception e1) {
//			e1.printStackTrace();
//			BarcodePanel.moveButton.setEnabled(true);
//			connection.closeConnection();
//			socket.closeConn();
//			D220socket.closeConn();
//			return false;
//		}
//		labelDo:do {
//			BarcodePanel.barcodeNameField.setText(string1[1]);
//			BarcodePanel.barcodePre_contentArea.setText(string1[0]);
//			if (sock) {
//				while (true) {
//					if (!flag) {
//						sendStr = CIRCOMMAND + i + " " + readProperties.getOneone_Depth();
//						flag = true;
//					} else {
//						sendStr = CIRCOMMAND + i + " " + rotateAngel;
//						if (i <= 12) {
//							BarcodePanel.rotation_angleField.setText(String.valueOf(rotateAngel));
//						}
//					}
//					if (i > 12) {
//						updateResult(socket, testResultFailed);
//						break;
//					}
//					//清屏
//					if (sockD220) {
//						receive = Protocol.sendCmd2D220(D220socket, null);
//						if (receive[0]!=0x00) {
//							break labelDo;
//						}
//					}else {
//						break labelDo;
//					}
//					connection.readExtraData(100);
//					connection.clearSendBeforeData();
//					try {
//						Thread.sleep(100);
//					} catch (InterruptedException e1) {
//						e1.printStackTrace();
//					}
//					String string = socket.writeReadOneLine(sendStr);
//					if (string.contains("null")) {
//						break labelDo;	
//					}else if(string.contains("ok")){
//						//显示图片
//						if (sockD220) {
//							receive = Protocol.sendCmd2D220(D220socket, string1[2]);
//							if (receive[0]!=0x00) {
//								break labelDo;
//							}
//						}else {
//							break labelDo;
//						}
//					}else {
//						break labelDo;
//					}
//					boolean isTestPass = DataMatchUtils.getResultMatch(connection, string1[0], recvBuf, timeOut);
//				    if (!isTestPass) {
//				    	if (socket.writeReadOneLine("shakeout 5 1").contains("ok")) {
//				    		isTestPass = DataMatchUtils.getResultMatch(connection, string1[0], recvBuf, timeOut);
//						}
//					}
//					TiaoMaInfo r3601 = new TiaoMaInfo("360旋转角度", "一维", string1[1], "", rotateAngel +"", isTestPass== true ? "PASS":"FAIL", ""); 
//					BarcodePanel.allList.add(r3601);
//					if (!isTestPass) {;
//						testResultFailed.add(rotateAngel);
//						
//					}
//					i++;
//					rotateAngel += 30;
//					
//				}//end while
//			}else {
//				break labelDo;
//			}
//			try {
//				BarcodePanel.moveButton.setEnabled(true);
//				connection.closeConnection();
//				socket.closeConn();
//				D220socket.closeConn();
//				return true;
//			} catch (Exception e) {
//				BarcodePanel.moveButton.setEnabled(true);
//				connection.closeConnection();
//				socket.closeConn();
//				D220socket.closeConn();
//				e.printStackTrace();
//				return false;
//			}
//		} while (false);
//		try {
//			BarcodePanel.moveButton.setEnabled(true);
//			connection.closeConnection();
//			socket.closeConn();
//			D220socket.closeConn();
//			return false;
//		} catch (Exception e) {
//			BarcodePanel.moveButton.setEnabled(true);
//			connection.closeConnection();
//			socket.closeConn();
//			D220socket.closeConn();
//			e.printStackTrace();
//			return false;
//		}
//	}
	@SuppressWarnings("rawtypes")
	private void updateResult(List<Integer> testResult) {
		if (testResult.size()>0) {
			StringBuilder builder = new StringBuilder();
			for (int j = 0; j < testResult.size(); j++) {
				String testResultString =testResult.get(j).toString();
				if(j == testResult.size() - 1)
					builder.append(testResultString + " ");
				else
					builder.append(testResultString + ",");
			}
			
			//BarcodePanel.barcodeDecodeContentArea.setText(builder.toString() + "FAIL");
			mainFrame.logger.info("#rotateangel:"+ (builder.toString() + "FAIL"));
		}else {
			//BarcodePanel.barcodeDecodeContentArea.setText("PASS");
			mainFrame.logger.info("#rotateangel:"+ " PASS");
		}
		
	}

}

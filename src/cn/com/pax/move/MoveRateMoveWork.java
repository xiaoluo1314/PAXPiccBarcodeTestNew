package cn.com.pax.move;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import cn.pax.com.utils.*;
import com.sun.istack.internal.FinalArrayList;

import cn.com.pax.display.BarcodePanel;
import cn.com.pax.display.mainFrame;
import cn.com.pax.protocol.Protocol;
import cn.com.pax.protocol.SocketToC4;
import cn.com.pax.report.TiaoMaInfo;
import cn.com.pax.sericomm.SerialConnection;
import javafx.scene.paint.Stop;

public class MoveRateMoveWork  {

	private SerialConnection connection;
	private String ip ;
	private int port ;
	
	private String D220ip ;
	private int D220port ;
	
	public static final String MRCOMMAND ="moverate ";
	private String path = "./config/ConSocket.properties";
	private String path1 = "./config/D220CfgSocket.properties"; 
	
	public MoveRateMoveWork(SerialConnection connection) {
		this.connection = connection;
		//initData();
	}
	
	public boolean  moverateWorkFlowingTwo(boolean isTwoWei, String strType) {
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
		
		ReadProperties readProperties = new ReadProperties();
		//readProperties.ReadDepthProperties();
		
		int nextDis = Integer.parseInt(readProperties.getOneone_Depth());
		String str1 = readProperties.getRateOne();
		if(isTwoWei) {
			nextDis = Integer.parseInt(readProperties.getTwotwo_Depth());
			str1 = readProperties.getRateTwo();
		}

		String sendStr = null;
		int rate = 200; 
		int connTimeout  = 3000;

		String []string1 = str1.split(",");
		byte[]receive = new byte[2] ;

		UtilsTool.updateView(BarcodePanel.currentTestItemField,"扫描速度");
		UtilsTool.updateView(BarcodePanel.cardNumField,"1");
		UtilsTool.updateView(BarcodePanel.excutingField,"1");

		try {
			connection.openConnection();
			connection.readExtraData(100, connTimeout);
			connection.clearSendBeforeData();
		} catch (Exception e) {
			e.printStackTrace();
			socket_C4.closeConn();
			socket_D220.closeConn();
			connection.closeConnection();
			return false;
		}

		labelDo:do {
			UtilsTool.updateView(BarcodePanel.barcodePre_contentArea, string1[0]);
			UtilsTool.updateView(BarcodePanel.barcodeNameField, string1[1]);

			if(socket_C4.writeReadOneLine("initaction "+ ""+nextDis).contains("ok")){ }

			labelWhile: while(true && StopMove.isCodeRunning()) {
				connTimeout =(int)((12*3.142*10/rate)*1000);
				sendStr = MRCOMMAND + rate +" "+ nextDis;

				UtilsTool.updateView(BarcodePanel.rotation_speedField, rate+"");

				//清屏
				receive = Protocol.sendCmd2D220(socket_D220, null);
				if (receive[0]!=0x00) { break labelDo; }

				connection.readExtraData(100);
				connection.clearSendBeforeData();
				boolean isTestPass = false;
				for(int i=0;i<3 && StopMove.isCodeRunning();i++){
					String string = socket_C4.writeReadOneLine(sendStr);
					mainFrame.logger.info("cmd: " + sendStr + " result: " + string);
					if(string.contains("null") ) {
						break labelDo;
					}else if (string.contains("ok")){
						receive = Protocol.sendCmd2D220(socket_D220, string1[2]);
						if(receive[0]!=0x00) { break labelDo;}
						byte[]recvBuf = new byte[32];
						long startTime = System.currentTimeMillis();
						isTestPass = DataMatchUtils.getMoveResultMatch(connection, string1[0], recvBuf, connTimeout);
						long endTime = System.currentTimeMillis();
						//System.out.println("connTimeout=" + connTimeout);
						if(isTestPass) {
							//updateUI(rate);
							UtilsTool.updateView(BarcodePanel.barcodeDecodeContentArea, string1[0]);
							mainFrame.logger.info("moverate:"+rate+""+nextDis+"");
							//rate += 50;
							try {
								System.out.println("timeOut = " +connTimeout);
								System.out.println("(endTime-startTime) = "+(endTime-startTime));
								if(connTimeout -(endTime-startTime)>0){
									Thread.sleep(connTimeout -(endTime-startTime));
								}
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							break labelWhile;
						}
					}else {
						break labelDo;
					}
				}//end for
				
				rate -= 20;
				if(rate < 1) break;
			}//end while

			if(!StopMove.isCodeRunning()) {
				connection.closeConnection();
				socket_C4.closeConn();
				socket_D220.closeConn();
				return false;
			}

			String rlt = rate >= 200 ? "PASS" : "FAIL";
			UtilsTool.updateArea(BarcodePanel.testResultArea, "扫描速度", rlt);
			TiaoMaInfo dis1 = new TiaoMaInfo("扫描速度", strType, string1[1], nextDis+"", rate +" ", rlt, "");
			BarcodePanel.allList.add(dis1);
			try {
				connection.closeConnection();
				socket_C4.closeConn();
				socket_D220.closeConn();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}	
		} while (false);
		
		try {
			connection.closeConnection();
			socket_C4.closeConn();
			socket_D220.closeConn();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}		
	}
	
//private void updateUI(final int rate ){
//	try {
//		EventQueue.invokeAndWait(new Runnable() {
//			public void run() {
//				//BarcodePanel.barcodeDecodeContentArea.setText("FAIL");
//				BarcodePanel.rotation_speedField.setText(rate+"");
//			}
//		});
//	} catch (Exception e) {
//		e.printStackTrace();
//	}
//}
//public boolean  moverateWorkFlowingOne() {
//	SocketToC4 socket_C4 = ReadSocketCfg.getSocket(path);
//	SocketToC4 socket_D220 = ReadSocketCfg.getSocket(path1);//D220
//	//boolean sockC4 = socket_C4.openConn();
//	if(socket_C4 == null || !socket_C4.openConn()) {
//		mainFrame.logger.error("连接机器人失败，请检查配置！!");
//		return false;
//	}
//	if(socket_D220 == null || !socket_D220.openConn()) {
//		mainFrame.logger.error("连接手机失败，请检查配置！!");
//		socket_C4.closeConn();
//		return false;
//	}
//
//	ReadProperties readProperties = new ReadProperties();
//	readProperties.ReadDepthProperties();
//	int nextDis = Integer.parseInt(readProperties.getOneone_Depth());
//
//	String sendStr = null;
//	int rate = 200;
//	int timeOut  = 3000;
//
//	String str1 = readProperties.getOne_Dim();
//	String []string1 = str1.split(",");
//	byte[]receive = new byte[2] ;
//	BarcodePanel.scanDisField.setText("");
//	BarcodePanel.rotation_angleField.setText("");
//	BarcodePanel.barcodeDecodeContentArea.setText("");
//	BarcodePanel.barcodePre_contentArea.setText("");
//	BarcodePanel.rotation_speedField.setText("");
//
//	BarcodePanel.currentTestItemField.setText("移动速度");
//	BarcodePanel.cardNumField.setText("1");
//	BarcodePanel.excutingField.setText("1");
//	try {
//		connection.openConnection();
//		connection.readExtraData(100, timeOut);
//		connection.clearSendBeforeData();
//	} catch (Exception e) {
//		e.printStackTrace();
//		socket_C4.closeConn();
//		socket_D220.closeConn();
//		connection.closeConnection();
//		return false;
//	}
//
//	labelDo:do {
//		BarcodePanel.barcodePre_contentArea.setText(string1[0]);
//		BarcodePanel.barcodeNameField.setText(string1[1]);
//		if(socket_C4.writeReadOneLine("initaction "+ ""+nextDis).contains("ok")){
//		}
//		labelWhile: while(true && StopMove.isCodeRunning()){
//			timeOut =(int)((12*3.142*10/rate)*1000);
//			sendStr = MRCOMMAND + rate +" "+ nextDis;
//			//清屏
//			receive = Protocol.sendCmd2D220(socket_D220, null);
//			if (receive[0]!=0x00) {
//				break labelDo;
//			}
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			connection.readExtraData(100);
//			connection.clearSendBeforeData();
//			boolean isTestPass = false ;
//			for(int i=0;i<3 && StopMove.isCodeRunning();i++){
//				String string = socket_C4.writeReadOneLine(sendStr);
//				if (string .contains("null") ) {
//					break labelDo;
//				}else if (string.contains("ok")){
//					receive = Protocol.sendCmd2D220(socket_D220, string1[2]);
//					if (receive[0]!=0x00) {
//						break labelDo;
//					}
//					byte[]recvBuf = new byte[32];
//					long startTime = System.currentTimeMillis();
//					isTestPass = DataMatchUtils.getResultMatch(connection, string1[0], recvBuf, timeOut);
//					long endTime = System.currentTimeMillis();
//					if (isTestPass) {
//						updateUI(rate);
//						mainFrame.logger.info("moverate:"+rate+""+nextDis+"");
//						//rate += 50;
//						try {
//							System.out.println("timeOut = " +timeOut);
//							System.out.println("(endTime-startTime) = "+(endTime-startTime));
//							if(timeOut -(endTime-startTime)>0){
//								Thread.sleep(timeOut -(endTime-startTime));
//							}
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
//						break labelWhile;
//					}
//				}else {
//					break labelDo;
//				}
//
//			}//end for
//			rate -= 20;
//			if(rate < 1) break;
//		}//end while
//
//		if(!StopMove.isCodeRunning()) {
//			connection.closeConnection();
//			socket_C4.closeConn();
//			socket_D220.closeConn();
//			return false;
//		}
//
//		TiaoMaInfo dis1 = null;
//		if(rate >= 200) {
//			BarcodePanel.testResultArea.append("扫描速度：PASS\n");
//			dis1 = new TiaoMaInfo("扫描速度", "一维", string1[1], nextDis+"", (rate)+" ",  "PASS", "");
//		} else {
//			BarcodePanel.testResultArea.append("扫描速度：FAIL\n");
//			dis1 = new TiaoMaInfo("扫描速度", "一维", string1[1], nextDis+"", (rate)+" ",  "PASS", "");
//		}
//		BarcodePanel.allList.add(dis1);
//
//		try {
//			connection.closeConnection();
//			socket_C4.closeConn();
//			socket_D220.closeConn();
//			return true;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return false;
//		}
//	} while (false);
//
//	try {
//		connection.closeConnection();
//		socket_C4.closeConn();
//		socket_D220.closeConn();
//		return false;
//	} catch (Exception e) {
//		e.printStackTrace();
//		return false;
//	}
//}
	
	/*
	public boolean moverateWorkFlowingTwo() {
		ReadSocketCfg.readSocketConfig(path);
		ip = ReadSocketCfg.getIP();
		port = ReadSocketCfg.getPort();
		SocketToC4 socket = new SocketToC4(ip, port);
		
		ReadSocketCfg.readSocketConfig(path1);
		D220ip = ReadSocketCfg.getIP();
		D220port = ReadSocketCfg.getPort();
		SocketToC4 D220socket = new SocketToC4(D220ip, D220port);
		ReadProperties readProperties = new ReadProperties();
		readProperties.ReadDepthProperties();
		//String initDis = BarcodePanel.initDisField.getText().trim();
		int nextDis = Integer.parseInt(readProperties.getTwotwo_Depth());
		String sendStr = null;
		int rate = 100; 
		int timeOut  = 7000;
		List testResultFailed = new ArrayList ();
		testResultFailed.clear();
		
		String str1 = readProperties.getTwo_Dim();
		String []string1 = str1.split(",");
		
		boolean sock = socket.openConn();
		boolean sockD220 = D220socket.openConn();
		byte[]receive = new byte[2] ;
		BarcodePanel.scanDisField.setText("");
		BarcodePanel.rotation_angleField.setText("");
		BarcodePanel.barcodeDecodeContentArea.setText("");
		BarcodePanel.barcodePre_contentArea.setText("");
		BarcodePanel.currentTestItemField.setText("移动速度");
		BarcodePanel.cardNumField.setText("1");
		BarcodePanel.excutingField.setText("1");
		try {
			connection.openConnection();
			connection.clearSendBeforeData();
		} catch (Exception e) {
			e.printStackTrace();
			BarcodePanel.moveButton.setEnabled(true);
			socket.closeConn();
			D220socket.closeConn();
			connection.closeConnection();
			return false;
		}
		labelDo:do {
			if (sock) {
				BarcodePanel.barcodePre_contentArea.setText(string1[0]);
				BarcodePanel.barcodeNameField.setText(string1[1]);
				if( socket.writeReadOneLine("initaction "+ ""+nextDis).contains("ok")){
					
				}
				for(int k=0; k<3;k++){
					for(int l=200; l<=200; l+=100){
						rate = l;
						sendStr = MRCOMMAND + rate +" "+ nextDis;
						
						//清屏
						if (sockD220) {
							receive = Protocol.sendCmd2D220(D220socket, null);
							if (receive[0]!=0x00) {
								break labelDo;
							}
						}else {
							break labelDo;
						}
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						connection.readExtraData(100);
						connection.clearSendBeforeData();
						if (!(l == 100 && k == 0)) {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}
							if (sockD220) {
								receive = Protocol.sendCmd2D220(D220socket, string1[2]);
								if (receive[0]!=0x00) {
									break labelDo;
								}
							}else {
								break labelDo;
							}
						}
						String string = socket.writeReadOneLine(sendStr);
						
						if (string == null) {
							break labelDo;	
						}else if(string.contains("ok")){
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							if (l == 100 && k == 0) {
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e1) {
									e1.printStackTrace();
								}
								if (sockD220) {
									receive = Protocol.sendCmd2D220(D220socket, string1[2]);
									if (receive[0]!=0x00) {
										break labelDo;
									}
								}else {
									break labelDo;
								}
							}
							
							byte[]recvBuf = new byte[32];
							boolean isTestPass = DataMatchUtils.getResultMatch(connection, string1[0], recvBuf, timeOut);
							TiaoMaInfo dis1 = new TiaoMaInfo("扫描速度", "二维", string1[1], nextDis+"", rate+" ", isTestPass ? "PASS":"FAIL", ""); 
							BarcodePanel.allList.add(dis1);
								
							if (!isTestPass) {
								testResultFailed.add(rate+" "+ nextDis +"");
							}
							if (isTestPass) {
								
								try {
									Thread.sleep(4000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
					}//end for 
					nextDis += 10;
				}//end for
			}else{
				break labelDo;
			}
			updateResult(socket, testResultFailed);
			
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
	}
	*/
	

	/*public boolean moverateWorkFlowingOne() {
		ReadSocketCfg.readSocketConfig(path);
		ip = ReadSocketCfg.getIP();
		port = ReadSocketCfg.getPort();
		SocketToC4 socket = new SocketToC4(ip, port);
		
		ReadSocketCfg.readSocketConfig(path1);
		D220ip = ReadSocketCfg.getIP();
		D220port = ReadSocketCfg.getPort();
		SocketToC4 D220socket = new SocketToC4(D220ip, D220port);
		ReadProperties readProperties = new ReadProperties();
		readProperties.ReadDepthProperties();
		boolean flag = false;
		String initDis = BarcodePanel.initDisField.getText().trim();
		int nextDis = Integer.parseInt(readProperties.getOneone_Depth());
		
		String sendStr = null;
		int rate = 100; 
		int timeOut  = 7000;
		List testResultFailed = new ArrayList ();
		testResultFailed.clear();
		
		String str1 = readProperties.getOne_Dim();
		String []string1 = str1.split(",");
		
		boolean sock = socket.openConn();
		boolean sockD220 = D220socket.openConn();
		byte[]receive = new byte[2] ;
		BarcodePanel.scanDisField.setText("");
		BarcodePanel.rotation_angleField.setText("");
		BarcodePanel.barcodeDecodeContentArea.setText("");
		BarcodePanel.barcodePre_contentArea.setText("");
		BarcodePanel.currentTestItemField.setText("移动速度");
		BarcodePanel.cardNumField.setText("1");
		BarcodePanel.excutingField.setText("1");
		try {
			connection.openConnection();
			connection.clearSendBeforeData();
		} catch (Exception e) {
			e.printStackTrace();
			BarcodePanel.moveButton.setEnabled(true);
			socket.closeConn();
			D220socket.closeConn();
			connection.closeConnection();
			return false;
		}
		labelDo:do {
			if (sock) {
				BarcodePanel.barcodePre_contentArea.setText(string1[0]);
				BarcodePanel.barcodeNameField.setText(string1[1]);
				if( socket.writeReadOneLine("initaction "+ ""+nextDis).contains("ok")){
					
				}
				for(int k=0; k<3;k++){
					for(int l=200; l<=200; l+=100){
						rate = l;
						sendStr = MRCOMMAND + rate +" "+ nextDis;
						
						//清屏
						if (sockD220) {
							receive = Protocol.sendCmd2D220(D220socket, null);
							if (receive[0]!=0x00) {
								break labelDo;
							}
						}else {
							break labelDo;
						}
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						connection.readExtraData(100);
						connection.clearSendBeforeData();
						if (!(l == 100 && k == 0)) {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}
							if (sockD220) {
								receive = Protocol.sendCmd2D220(D220socket, string1[2]);
								if (receive[0]!=0x00) {
									break labelDo;
								}
							}else {
								break labelDo;
							}
						}
						String string = socket.writeReadOneLine(sendStr);
						
						if (string == null) {
							break labelDo;	
						}else if(string.contains("ok")){
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							if (l == 100 && k == 0) {
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e1) {
									e1.printStackTrace();
								}
								if (sockD220) {
									receive = Protocol.sendCmd2D220(D220socket, string1[2]);
									if (receive[0]!=0x00) {
										break labelDo;
									}
								}else {
									break labelDo;
								}
							}
							
							byte[]recvBuf = new byte[32];
							boolean isTestPass = DataMatchUtils.getResultMatch(connection, string1[0], recvBuf, timeOut);
							TiaoMaInfo dis1 = new TiaoMaInfo("扫描速度", "一维", string1[1], nextDis+"", rate+" ", isTestPass ? "PASS":"FAIL", ""); 
							BarcodePanel.allList.add(dis1);
								
							if (!isTestPass) {
								testResultFailed.add(rate+" "+ nextDis +"");
							}
							if (isTestPass) {
								
								try {
									Thread.sleep(4000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
					}//end for 
					nextDis += 10;
				}//end for
			}else{
				break labelDo;
			}
			updateResult(socket, testResultFailed);
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
//	private void updateResult(SocketToC4 socket, List testResult) {
//		if (testResult.size()>0) {
//			StringBuilder builder = new StringBuilder();
//			for (int j = 0; j < testResult.size(); j++) {
//				String testResultString =testResult.get(j).toString();
//				if(j == testResult.size() - 1)
//					builder.append(testResultString + " ");
//				else
//					builder.append(testResultString + ",");
//			}
//			BarcodePanel.barcodeDecodeContentArea.setText("FAIL");
//			mainFrame.logger.info("#moveRate: "+builder.toString()+" FAIL");
//
//		}else {
//			BarcodePanel.barcodeDecodeContentArea.setText("PASS");
//			mainFrame.logger.info("#moveRate: "+"PASS");
//
//
//		}
//	}

}

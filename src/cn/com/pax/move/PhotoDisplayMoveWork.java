package cn.com.pax.move;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import cn.pax.com.utils.*;
import sun.reflect.generics.tree.Tree;

import com.sun.org.apache.bcel.internal.util.ByteSequence;

import cn.com.pax.display.BarcodePanel;
import cn.com.pax.display.mainFrame;
import cn.com.pax.protocol.Protocol;
import cn.com.pax.protocol.SocketToC4;
import cn.com.pax.report.TiaoMaInfo;
import cn.com.pax.sericomm.SerialConnection;

public class PhotoDisplayMoveWork {
	private JFrame jFrame;
	private SerialConnection connection;
	private String ip_C4 ;
	private int port_C4 ;
	private String ip_D220 ;
	private int port_D220 ;
	private String path = "./config/ConSocket.properties";
	private String path1 = "./config/D220CfgSocket.properties";
	List<Integer>moveList = new ArrayList<Integer>();
	private static final String CODETYPECMD = "dofone ";
	//private static int timeOut = 3;
	//private static int timeOut = 2000;
	private int initDis, connTimeout; 
	
	public PhotoDisplayMoveWork(List<Integer>moveList,SerialConnection connection, JFrame jf ){	
		this.moveList = moveList;
		this.connection = connection;
		jFrame = jf;
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
			connTimeout = Integer.parseInt(initString);
		} else {
			connTimeout = 2000;
		} 
	}
	
	public boolean runWork(boolean isTwoWei, String typeMa, boolean isOnly){
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

		List<BarcodeInfo> codeInfo = ReadBarcodeInfo.getList();
		int ll = 1, j = 0, k = 0;

		Map<String, Integer> totalNum = new HashMap<>();
		boolean testResult = true;
		String string = null;
		//距离
		int  dis = 30;//默认30
		int m =0;
		String sendStr = null;
		byte[]receive = new byte[2];
		try {
			connection.openConnection();
			connection.clearSendBeforeData();
		} catch (Exception e) {
			e.printStackTrace();
			socket_C4.closeConn();
			socket_D220.closeConn();
			connection.closeConnection();
			return false;
		}

		UtilsTool.updateView(BarcodePanel.cardNumField, String.valueOf(moveList.size()));

		labelDo:do{
			for (int i = 0; i < moveList.size() && StopMove.isCodeRunning(); i++) {
				m = 0 ;
				if(initDis > 30)
					dis = 0;
				else {
					dis = 30 - initDis;
				}
				//获取配置文件中的位置
				j = moveList.get(i).intValue();
				//得到配置文件相应行记录的文件名
				BarcodeInfo barcodeTypeInfo = codeInfo.get(j);
				k++;
				//BarcodePanel.barcodeDecodeContentArea.setText("");//清空解码内容
				UtilsTool.updateView(BarcodePanel.barcodePre_contentArea, barcodeTypeInfo.getBarcodeContent());//设置预设内容
				UtilsTool.updateView(BarcodePanel.currentTestItemField, barcodeTypeInfo.getBarcodeType());//当前测试项
				UtilsTool.updateView(BarcodePanel.barcodeNameField, barcodeTypeInfo.getBarcodeName());//当前条码的名称
				UtilsTool.updateView(BarcodePanel.excutingField, k+"");//设置当前执行条目数
				UtilsTool.updateView(BarcodePanel.scanDisField, (initDis + dis) + "");

				String currKey = barcodeTypeInfo.getBarcodeType() + barcodeTypeInfo.getScanType();
				addTypeMap(totalNum, currKey);
				
				while (true && StopMove.isCodeRunning()) {
					sendStr = CODETYPECMD + m +" "+ dis;
					if (m == 0) {
						//清屏
						receive = Protocol.sendCmd2D220(socket_D220, null);
						if (receive[0]!=0x00) {
							mainFrame.logger.error(receive[0] + "");
							break labelDo;
						}
						connection.readExtraData(200);
						connection.clearSendBeforeData();
					}		
					
					string = socket_C4.writeReadOneLine(sendStr);
					mainFrame.logger.info("cmd: " + sendStr + " result: " + string);
					if (string.contains("null")) { break labelDo;}
					else if(string.contains("ok")){
						if (m == 0) {
							//显示条码
							receive = Protocol.sendCmd2D220(socket_D220, barcodeTypeInfo.getFileName());
							if (receive[0]!=0x00) {
								mainFrame.logger.error(receive[0] + "");
								break labelDo;
							}
						}
						byte[] recvBuf = new byte[32];
						boolean isTestPass = DataMatchUtils.getResultMatch(connection, barcodeTypeInfo.getBarcodeContent(), recvBuf, connTimeout);
						if (isTestPass) {
							//TestResult(barcodeTypeInfo, isTestPass);
							UtilsTool.updateView(BarcodePanel.barcodeDecodeContentArea, barcodeTypeInfo.getBarcodeContent());
							ll = writeReport(ll, barcodeTypeInfo, isTestPass, initDis + dis, typeMa);
							mainFrame.logger.info("条码类型:"+ barcodeTypeInfo.getBarcodeType() +"条码名称:"+barcodeTypeInfo.getBarcodeName() +"距离:"+ (initDis + dis));
							break;
						}else{
							if (dis > 200) {
								testResult = false;
								//TestResult(barcodeTypeInfo, isTestPass);
								UtilsTool.updateView(BarcodePanel.barcodeDecodeContentArea, "FAIL");
								ll = writeReport(ll, barcodeTypeInfo, isTestPass,initDis + dis, typeMa);
								break;
							}
							m++;
							dis += 10;
						}
					}else {
						break labelDo;
					}	
				}//end while

				if(!isTwoWei || isOnly) {
					if(ReadBarcodeInfo.typeNumMap.get(currKey) == totalNum.get(currKey)) {
						String rlt = testResult ? "PASS" : "FAIL";
						UtilsTool.updateArea(BarcodePanel.testResultArea, barcodeTypeInfo.getBarcodeType(), rlt);
						testResult = true;
					}
				}
				else {
					String key2 = barcodeTypeInfo.getBarcodeType() + "二维";
					String key1 = barcodeTypeInfo.getBarcodeType() + "一维";
					int t1 = totalNum.get(key1) == null ? 0 : totalNum.get(key1);
					int t2 = totalNum.get(key2) == null ? 0 : totalNum.get(key2);
					int m1 = ReadBarcodeInfo.typeNumMap.get(key1) == null ? 0 : ReadBarcodeInfo.typeNumMap.get(key1);
					int m2 = ReadBarcodeInfo.typeNumMap.get(key2) == null ? 0 : ReadBarcodeInfo.typeNumMap.get(key2);
					if((m1 + m2) == (t1 + t2)) {
						String rlt = testResult ? "PASS" : "FAIL";
						UtilsTool.updateArea(BarcodePanel.testResultArea, barcodeTypeInfo.getBarcodeType(), rlt);
						testResult = true;
					}
				}
			}//end for
			try {
				connection.closeConnection();
				socket_C4.closeConn();
				socket_D220.closeConn();
				if(!StopMove.isCodeRunning()) return false;
				else return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}while(false);
		//关闭资源
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

	public void addTypeMap(Map<String, Integer> typeNumMap, String key) {
		if(typeNumMap.containsKey(key))
			typeNumMap.put(key, typeNumMap.get(key) + 1);
		else
			typeNumMap.put(key, 1);
	}


	private int writeReport(int l, BarcodeInfo barcodeTypeInfo, boolean isTestPass, int dis, String typeMa) {
		TiaoMaInfo maInfo = null;

		if ("彩色条码".equals(barcodeTypeInfo.getBarcodeType())) {
			maInfo = new TiaoMaInfo("彩色条码", typeMa, barcodeTypeInfo.getBarcodeName(), "彩色条码"+l, "", isTestPass== true ? "PASS":"FAIL", dis + "");
			BarcodePanel.allList.add(maInfo);
			l++;
		} else {
			maInfo = new TiaoMaInfo(barcodeTypeInfo.getBarcodeType(), typeMa, barcodeTypeInfo.getBarcodeName(), "", "", isTestPass== true ? "PASS":"FAIL", dis + "");
			BarcodePanel.allList.add(maInfo);
		}
		mainFrame.logger.info("#codeType:" + barcodeTypeInfo.getBarcodeName() +":" + (isTestPass== true ? "PASS":"FAIL")+"#" );
		return l;
	}
	
	/*public boolean runWorkOne(){
		
		ReadSocketCfg.readSocketConfig(path);
		ip_C4 = ReadSocketCfg.getIP();
		port_C4 = ReadSocketCfg.getPort();
		
		ReadSocketCfg.readSocketConfig(path1);
		ip_D220 = ReadSocketCfg.getIP();
		port_D220 = ReadSocketCfg.getPort();
		ReadProperties readProperties = new ReadProperties();
		readProperties.ReadDepthProperties();
		SocketToC4 socket_C4 = new SocketToC4(ip_C4, port_C4);//C4
		SocketToC4 socket_D220 = new SocketToC4(ip_D220, port_D220);//D220
		BarcodePanel.moveButton.setEnabled(false);
		List<BarcodeInfo>codeInfo = ReadBarcodeInfo.getList();
		int l = 1;
		int j = 0;
		int k = 0;
		BarcodePanel.cardNumField.setText(String.valueOf(moveList.size()));
		String initDis = BarcodePanel.initDisField.getText().trim();
		boolean sockC4 = socket_C4.openConn();
		boolean sockD220 = socket_D220.openConn();
		String sendString = null;
		
		BarcodePanel.scanDisField.setText("");
		BarcodePanel.rotation_angleField.setText("");
		BarcodePanel.barcodeDecodeContentArea.setText("");
		BarcodePanel.barcodePre_contentArea.setText("");
		//打开串口
		connection.openConnection();
		
		for (int i = 0; i < moveList.size(); i++) {
			//清屏
			if (sockD220) {
				byte[] buf = Protocol.compositeProcotol((byte)0x15, null, 0);
				byte[] retBuf = socket_D220.writeReadOneLine(buf);
				if (retBuf[0] == 0x00) {
					if (sockC4) { //连接C4
						//获取配置文件中的位置
						j = moveList.get(i).intValue();
						//得到配置文件相应行记录的文件名
						BarcodeInfo barcodeTypeInfo = codeInfo.get(j);
						k++;
						BarcodePanel.barcodeDecodeContentArea.setText("");//清空解码内容
						BarcodePanel.barcodePre_contentArea.setText(barcodeTypeInfo.getBarcodeContent());//设置预设内容
						BarcodePanel.currentTestItemField.setText(barcodeTypeInfo.getBarcodeType());//当前测试项
						BarcodePanel.barcodeNameField.setText(barcodeTypeInfo.getBarcodeName());//当前条码的名称
						BarcodePanel.excutingField.setText(k+"");//设置当前执行条目数
						System.out.println(barcodeTypeInfo.getScanType());
						sendString = CODETYPECMD +" "+ readProperties.getOneone_Depth();//一维扫描头一维景深
						connection.readExtraData(500);
						connection.clearSendBeforeData();
						//尝试3次写读
						String string = socket_C4.writeReadOneLine(sendString);//发送给C4
						if (string.contains("ok")) {
							if (sockD220) {
								String fileName = barcodeTypeInfo.getFileName();
								byte[]fileNameByte = fileName.getBytes();
								int fileLength = fileName.length();
								byte[] sendBuf = Protocol.compositeProcotol((byte)0x16, fileNameByte, fileLength);
								byte[] receiveBuf = socket_D220.writeReadOneLine(sendBuf); 
								if (receiveBuf[0] == 0x00) {
									long startTime = System.currentTimeMillis();
									//boolean isTestPass = PickCardMoveWork.serialRead(connection,datalen, barcodeTypeInfo.getBarcodeContent(), timeOut);
									byte[] recvBuf = new byte[32];
									boolean isTestPass = DataMatchUtils.getResultMatch(connection, barcodeTypeInfo.getBarcodeContent(), recvBuf, timeOut);
									
									TestResult(barcodeTypeInfo, startTime, isTestPass);
									l = writeReport(l, barcodeTypeInfo, isTestPass);
								    try {
										Thread.sleep(2000);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}else {
									BarcodePanel.moveButton.setEnabled(true);
									socket_C4.closeConn();
									socket_D220.closeConn();
									connection.closeConnection();
									JOptionPane.showMessageDialog(jFrame, "socket_D220 Display Failed", "socket_C4 Display", JOptionPane.WARNING_MESSAGE);
									return false;
								}
							}else {
								BarcodePanel.moveButton.setEnabled(true);	
								socket_C4.closeConn();
								socket_D220.closeConn();
								connection.closeConnection();
								JOptionPane.showMessageDialog(jFrame, "socket_D220 Open Failed", "socket_D220 Open", JOptionPane.WARNING_MESSAGE);
								return false;
							}
							
						}else {
							BarcodePanel.moveButton.setEnabled(true);	
							socket_C4.closeConn();
							socket_D220.closeConn();
							connection.closeConnection();
							JOptionPane.showMessageDialog(jFrame, "socket_C4 received data is ERROR", "socket_C4 Receiver", JOptionPane.WARNING_MESSAGE);
							return false;
						}
						
					}else {
						BarcodePanel.moveButton.setEnabled(true);
						connection.closeConnection();
						socket_C4.closeConn();
						socket_D220.closeConn();
						JOptionPane.showMessageDialog(jFrame, "socket_C4 Open Failed", "socket_C4 Open", JOptionPane.WARNING_MESSAGE);
						return false;
					}
					
				}else {
					BarcodePanel.moveButton.setEnabled(true);
					socket_C4.closeConn();
					socket_D220.closeConn();
					connection.closeConnection();
					JOptionPane.showMessageDialog(jFrame, "socket_D220 Display Failed", "socket_C4 Display", JOptionPane.WARNING_MESSAGE);
					return false;
				}
				
			}else {
				BarcodePanel.moveButton.setEnabled(true);	
				socket_C4.closeConn();
				socket_D220.closeConn();
				connection.closeConnection();
				JOptionPane.showMessageDialog(jFrame, "socket_D220 Open Failed", "socket_D220 Open", JOptionPane.WARNING_MESSAGE);
				return false;
			}
		}//end for
		
		byte[] buf1 = Protocol.compositeProcotol((byte)0x15, null, 0);
	    socket_D220.writeReadOneLine(buf1);
	    try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		BarcodePanel.moveButton.setEnabled(true);	
		connection.closeConnection();
		socket_C4.closeConn();
		socket_D220.closeConn();
		return true;
	}*/

	
	/*public boolean runwork() {
		
		ReadSocketCfg.readSocketConfig(path);
		ip_C4 = ReadSocketCfg.getIP();
		port_C4 = ReadSocketCfg.getPort();
		
		ReadSocketCfg.readSocketConfig(path1);
		ip_D220 = ReadSocketCfg.getIP();
		port_D220 = ReadSocketCfg.getPort();
		
		SocketToC4 socket_C4 = new SocketToC4(ip_C4, port_C4);//C4
		SocketToC4 socket_D220 = new SocketToC4(ip_D220, port_D220);//D220
		BarcodePanel.moveButton.setEnabled(false);
		List<BarcodeInfo>codeInfo = ReadBarcodeInfo.getList();
		int l = 1;
		int j = 0;
		int k = 0;
		BarcodePanel.cardNumField.setText(String.valueOf(moveList.size()));
		String initDis = BarcodePanel.initDisField.getText().trim();
		boolean sock = socket_C4.openConn();
		String sendString = null;
		
		BarcodePanel.scanDisField.setText("");
		BarcodePanel.rotation_angleField.setText("");
		BarcodePanel.barcodeDecodeContentArea.setText("");
		BarcodePanel.barcodePre_contentArea.setText("");
		
		
		if (socket_D220.openConn()) {//链接D220
			for (int i = 0; i < moveList.size(); i++) {
				//清屏
				byte[] buf = Protocol.compositeProcotol((byte)0x15, null, 0);
				byte[] retBuf = socket_C4.writeReadOneLine(buf);
				
				//获取配置文件中的位置
				j = moveList.get(i).intValue();
				//得到配置文件相应行记录的文件名
				BarcodeInfo barcodeTypeInfo = codeInfo.get(j);
				String fileName = barcodeTypeInfo.getFileName();
				byte[]fileNameByte = fileName.getBytes();
				int fileLength = fileName.length();
				byte[] sendBuf = Protocol.compositeProcotol((byte)0x16, fileNameByte, fileLength);
				byte[] receiveBuf = socket_D220.writeReadOneLine(sendBuf); 
				
				//System.out.println(receiveBuf[0]);
				if (receiveBuf[0] == 0x00) {
					if (sock) {	//连接C4
						k++;
						BarcodePanel.barcodeDecodeContentArea.setText("");//清空解码内容
						BarcodePanel.barcodePre_contentArea.setText(barcodeTypeInfo.getBarcodeContent());//设置预设内容
						BarcodePanel.currentTestItemField.setText(barcodeTypeInfo.getBarcodeType());//当前测试项
						BarcodePanel.barcodeNameField.setText(barcodeTypeInfo.getBarcodeName());//当前条码的名称
						BarcodePanel.excutingField.setText(k+"");//设置当前执行条目数
						
						if ("一维".equals(barcodeTypeInfo.getScanType())&&"二维".equals((String)BarcodePanel.scanTypeBox.getSelectedItem())) {
							
							 sendString = CODETYPECMD +" "+ ReadProperties.getTwoone_Depth();//二维扫描头一维景深
						}if ("一维".equals(barcodeTypeInfo.getScanType())&&"一维".equals((String)BarcodePanel.scanTypeBox.getSelectedItem())) {
							
							 sendString = CODETYPECMD +" "+ ReadProperties.getOneone_Depth();//一维扫描头一维景深
						}else {
							 sendString = CODETYPECMD +" "+ ReadProperties.getTwotwo_Depth();
						}
						
						 connection.openConnection();
						 connection.clearSendBeforeData();
						 
						//尝试3次写读
						String string = socket_C4.writeReadOneLine(sendString);//发送给C4
						
						if (string.contains("ok")) {
							if (connection.isOpen()) {
								 connection.clearSendBeforeData();
								long startTime = System.currentTimeMillis();
								//boolean isTestPass = PickCardMoveWork.serialRead(connection,datalen, barcodeTypeInfo.getBarcodeContent(), timeOut);
								byte[] recvBuf = new byte[32];
								boolean isTestPass = DataMatchUtils.getResultMatch(connection, barcodeTypeInfo.getBarcodeContent(), recvBuf, timeOut);
								connection.closeConnection();
								TestResult(barcodeTypeInfo, startTime, isTestPass);
								
								if ("一维".equals(barcodeTypeInfo.getScanType())&&"码制类型".equals(barcodeTypeInfo.getBarcodeType())) {
									TiaoMaInfo codeType = new TiaoMaInfo("码制类型", "一维", barcodeTypeInfo.getBarcodeName(), "", "", isTestPass== true ? "PASS":"FAIL", "");
									BarcodePanel.allList.add(codeType);
								}else if ("二维".equals(barcodeTypeInfo.getScanType())&&"码制类型".equals(barcodeTypeInfo.getBarcodeType())) {
									TiaoMaInfo codeType = new TiaoMaInfo("码制类型", "二维", barcodeTypeInfo.getBarcodeName(), "", "", isTestPass== true ? "PASS":"FAIL", "");
									BarcodePanel.allList.add(codeType);
								} 
								if ("一维".equals(barcodeTypeInfo.getScanType())&&"对比度".equals(barcodeTypeInfo.getBarcodeType())) {
									TiaoMaInfo cmpDu1 = new TiaoMaInfo("对比度", "一维", barcodeTypeInfo.getBarcodeName(), "一维码对比度测试-30%", "", isTestPass== true ? "PASS":"FAIL", ""); 
									BarcodePanel.allList.add(cmpDu1);
								}else if ("二维".equals(barcodeTypeInfo.getScanType())&&"对比度".equals(barcodeTypeInfo.getBarcodeType())) {
									TiaoMaInfo cmpDu2 = new TiaoMaInfo("码制类型", "二维", barcodeTypeInfo.getBarcodeName(), "二维码对比度测试-30%", "", isTestPass== true ? "PASS":"FAIL", "");
									BarcodePanel.allList.add(cmpDu2);
								} 
								if ("一维".equals(barcodeTypeInfo.getScanType())&&"彩色条码".equals(barcodeTypeInfo.getBarcodeType())) {
									TiaoMaInfo color1 = new TiaoMaInfo("彩色条码", "一维", barcodeTypeInfo.getBarcodeName(), "彩色条码"+l, "", isTestPass== true ? "PASS":"FAIL", "");
									BarcodePanel.allList.add(color1);
									l++;
								}else if ("二维".equals(barcodeTypeInfo.getScanType())&&"彩色条码".equals(barcodeTypeInfo.getBarcodeType())) {
									TiaoMaInfo color2 = new TiaoMaInfo("彩色条码", "二维", barcodeTypeInfo.getBarcodeName(),  "彩色条码"+l, "", isTestPass== true ? "PASS":"FAIL", "");
									BarcodePanel.allList.add(color2);
									l++;
								} 
								
								if ("一维".equals(barcodeTypeInfo.getScanType())&&"污损案例".equals(barcodeTypeInfo.getBarcodeType())) {
									TiaoMaInfo wusun1 = new TiaoMaInfo("污损案例", "一维", "", barcodeTypeInfo.getBarcodeName(), "", isTestPass == true ? "PASS":"FAIL", "");
									BarcodePanel.allList.add(wusun1);
								}else if ("二维".equals(barcodeTypeInfo.getScanType())&&"污损案例".equals(barcodeTypeInfo.getBarcodeType())) {
									TiaoMaInfo wusun2 = new TiaoMaInfo("污损案例", "二维","", barcodeTypeInfo.getBarcodeName(), "", isTestPass == true ? "PASS":"FAIL", "");
									BarcodePanel.allList.add(wusun2);
								} 
								
								mainFrame.logger.info("#codeType:" + barcodeTypeInfo.getBarcodeName() +":" + (isTestPass== true ? "PASS":"FAIL")+"#" );
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									
									e.printStackTrace();
								}
							}else
							{
								socket_C4.closeConn();
								socket_D220.closeConn();
								connection.closeConnection();
								BarcodePanel.moveButton.setEnabled(true);	
								JOptionPane.showMessageDialog(jFrame, "Serial Open Failed", "Serial Open", JOptionPane.WARNING_MESSAGE);
								return false;
							}
							
							
						}else {
							BarcodePanel.moveButton.setEnabled(true);	
							socket_C4.closeConn();
							socket_D220.closeConn();
							connection.closeConnection();
							JOptionPane.showMessageDialog(jFrame, "socket_C4 received data is ERROR", "socket_C4 Receiver", JOptionPane.WARNING_MESSAGE);
							return false;
						}
						
					}else {
						BarcodePanel.moveButton.setEnabled(true);
						socket_C4.closeConn();
						socket_D220.closeConn();
						JOptionPane.showMessageDialog(jFrame, "socket_C4 Open Failed", "socket_C4 Open", JOptionPane.WARNING_MESSAGE);
						return false;
					}
					
				}else {
					
					BarcodePanel.moveButton.setEnabled(true);
					socket_C4.closeConn();
					socket_D220.closeConn();
					JOptionPane.showMessageDialog(jFrame, "Socket_D220 Display Failed", "Socket_D220 Display", JOptionPane.WARNING_MESSAGE);
					return false;

				}
				
				
			}
			
			
			BarcodePanel.moveButton.setEnabled(true);
			socket_C4.closeConn();
			socket_D220.closeConn();
			return true;
		}else{
			
			BarcodePanel.moveButton.setEnabled(true);
			socket_C4.closeConn();
			socket_D220.closeConn();
			JOptionPane.showMessageDialog(jFrame, "Socket_D220 Open Failed", "Socket_D220 Open", JOptionPane.WARNING_MESSAGE);
			return false;
			
		}
		
	}
	
	*/

//	public static void TestResult( final BarcodeInfo barcodeInfo, boolean isTestPass) {
//		try {
//			if (isTestPass) {
//				EventQueue.invokeAndWait(new Runnable() {
//					@Override
//					public void run() {
//						// TODO Auto-generated method stub
//						BarcodePanel.barcodeDecodeContentArea.setText(barcodeInfo.getBarcodeContent());
//					}
//				});
//			}
//			else{
//				EventQueue.invokeAndWait(new Runnable() {
//					public void run() {
//						BarcodePanel.barcodeDecodeContentArea.setText("FAILED");
//					}
//				});
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//
//				e.printStackTrace();
//			}
//	}

//	public static void TestResult( final BarcodeInfo barcodeInfo, long startTime,boolean isTestPass) {
//		try {
//			if (isTestPass) {
//				EventQueue.invokeAndWait(new Runnable() {
//					
//					@Override
//					public void run() {
//						// TODO Auto-generated method stub
//						BarcodePanel.barcodeDecodeContentArea.setText(barcodeInfo.getBarcodeContent());
//					}
//				});
//			}
//			else{
//				EventQueue.invokeAndWait(new Runnable() {
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
//		if (endTime - startTime < ) {
//			try {
//				Thread.sleep(timeOut -(endTime - startTime));
//			} catch (InterruptedException e) {
//				
//				e.printStackTrace();
//			}
//		}
//	}
	
	
	
}

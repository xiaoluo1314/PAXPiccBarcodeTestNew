package cn.com.pax.display;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import cn.com.pax.protocol.ControlDV;
import cn.com.pax.protocol.SocketToC4;
import cn.com.pax.report.FreemakerTest;
import cn.com.pax.report.RFTestInfo;
import cn.com.pax.sericomm.SerialConnection;
import cn.com.pax.sericomm.SerialParameters;
import cn.pax.com.utils.ReadSocketCfg;

public class ChangqPanel extends JPanel{
	
	private static final long serialVersionUID = -1063287693960551097L;
	
	private String path = "./config/ConSocket.properties";
	private String ip_C4 ;
	private int port_C4 ;
	public  SocketToC4 socket ;

	JFrame jFrame;
	public  JTextField testerField;
	public  JTextField productField;
	public  JTextField snField;
	public  JTextField posField;
	private JComboBox<String> posBox,comboBox,comboBox1;
	//private JRadioButton untestButton,passButton,failButton;
	Thread thread = null;
	CQResultPanel rfResultPanel;
	double[] volages = new double[5];
	public ChangqPanel(final JFrame jf) {
		/*String ipString ="192.168.200.105";
		int portString = 6000;*/
		socket = ReadSocketCfg.getSocket(path);
		
		this.jFrame = jf;
		setBackground(Color.gray);
		setLayout( new BorderLayout(5, 5));
		
		JPanel eastPanel = new JPanel();
		JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
		centerPanel.setBackground(Color.gray);
		
		add(eastPanel, BorderLayout.EAST);
		add(centerPanel, BorderLayout.CENTER);
		
		makeCenterPanel(centerPanel);
		makeEastPanel(eastPanel);
		
	}
	private void makeCenterPanel(JPanel centerPanel) {
		
		centerPanel.setLayout(new BorderLayout(5, 5));
		JPanel upPanel = new JPanel(new GridLayout(1,2));
		JPanel downPanel = new JPanel(new GridBagLayout());
		
		downPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		downPanel.setBackground(Color.gray);
		
		JPanel up_LeftJPanel = new JPanel(new BorderLayout(5 ,5));
		up_LeftJPanel.setBackground(Color.gray);
		
		JPanel westJPanel = new JPanel(new GridLayout(2,1));//西边
		JPanel upJPanel = new JPanel(new GridBagLayout());
		JPanel dnJPanel = new JPanel(new GridBagLayout());
		upJPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		upJPanel.setBackground(Color.gray);
		westJPanel.add(upJPanel);
		westJPanel.add(dnJPanel);
		
		rfResultPanel = new CQResultPanel();
		
		JLabel posJLabel = new JLabel("一:POS平台选择");
		posBox = new JComboBox<String>();
		posBox.addItem("mPos");
		posBox.addItem("非mPos");
		posBox.setPreferredSize(new Dimension(posJLabel.getPreferredSize().width, posBox.getPreferredSize().height));
		
		JLabel comJLabel = new JLabel("1:选择场强串口");
		comboBox = new JComboBox<String>();
		List<String> comList= SerialConnection.getCommInfo();
		if(comList != null) {
			for(Iterator<String> iterator = comList.iterator();iterator.hasNext();){ 
	        	comboBox.addItem(iterator.next());
	        }
		}
        
		comboBox.setEditable(true);
		comboBox.setPreferredSize(new Dimension(comJLabel.getPreferredSize().width, comJLabel.getPreferredSize().height));
		
	/*	comboBox.addItemListener(new ItemListener() {	
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getSource() == comboBox) {
					CommPortIdentifier portId; 
					Enumeration<CommPortIdentifier> en = CommPortIdentifier.getPortIdentifiers();
					while (en.hasMoreElements()) {
						portId = (CommPortIdentifier) en.nextElement();
						
						if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
							int i;
							for (i = 0; i < comboBox.getItemCount(); i++) {
								if (portId.getName().equalsIgnoreCase(String.valueOf(comboBox.getItemAt(i)))) {
									break;
								}
							}
							if(i == comboBox.getItemCount())
								comboBox.addItem(portId.getName());
						}
					}	
				} 
			}
		});*/
		
		
		JLabel comJLabel1 = new JLabel("2:选择POS串口");
		comboBox1 = new JComboBox<String>();
		List<String> comList1 = SerialConnection.getCommInfo();
		if(comList != null) {
			for(Iterator<String> iterator = comList1.iterator();iterator.hasNext();){ 
	        	comboBox1.addItem(iterator.next());
	        }
		}
        
		comboBox1.setEditable(true);
		comboBox1.setPreferredSize(new Dimension(comJLabel1.getPreferredSize().width, comJLabel1.getPreferredSize().height));
		
		/*comboBox1.addItemListener(new ItemListener() {	
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getSource() == comboBox1) {
					CommPortIdentifier portId; 
					Enumeration<CommPortIdentifier> en = CommPortIdentifier.getPortIdentifiers();
					while (en.hasMoreElements()) {
						portId = (CommPortIdentifier) en.nextElement();
						
						if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
							int i;
							for (i = 0; i < comboBox1.getItemCount(); i++) {
								if (portId.getName().equalsIgnoreCase(String.valueOf(comboBox1.getItemAt(i)))) {
									break;
								}
							}
							if(i == comboBox1.getItemCount())
								comboBox1.addItem(portId.getName());
						}
					}	
				} 
			}
		});*/
		
		
		
		JLabel findJLabel = new JLabel("二:寻找非接中心");
		final JButton findButton = new JButton("开始寻找");
		findButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//String send = "pickabcard c";
				//socket.openConn();
				//if (socket.writeReadOneLine(send).contains("ok")) {
					//findButton.setEnabled(true);
					//socket.closeConn();
					TestInfoInput  testInfoInput  = new TestInfoInput(jFrame, ChangqPanel.this, "changqiang");
			    	testInfoInput.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			    	testInfoInput.setVisible(true);
			    	
//				}else{
//					findButton.setEnabled(true);
//					socket.closeConn();
//					JOptionPane.showMessageDialog(jFrame, "Socket Open Failed", "Socket Open", JOptionPane.WARNING_MESSAGE);
//					return;
//				}
				
				
			}
		});
		
		upJPanel.add(posJLabel, new GBC(0, 0).setInsets(5).setWeight(100, 100).setAnchor(GBC.WEST));
		upJPanel.add(posBox, new GBC(0, 1).setInsets(5).setWeight(100, 100).setAnchor(GBC.WEST));
		
		upJPanel.add(comJLabel, new GBC(0, 2).setInsets(1).setWeight(100, 100).setAnchor(GBC.WEST));
		upJPanel.add(comboBox, new GBC(0, 3).setInsets(1).setWeight(100, 100).setAnchor(GBC.WEST));
		
		upJPanel.add(comJLabel1, new GBC(0, 4).setInsets(1).setWeight(100, 100).setAnchor(GBC.WEST));
		upJPanel.add(comboBox1, new GBC(0, 5).setInsets(1).setWeight(100, 100).setAnchor(GBC.WEST));
		
		upJPanel.add(findJLabel, new GBC(0, 6).setInsets(5).setWeight(100, 100).setAnchor(GBC.WEST));
		upJPanel.add(findButton, new GBC(0, 7).setInsets(5).setWeight(100, 100).setAnchor(GBC.WEST));
		
		JLabel imageUntest = new JLabel("未进行测试", new ImageIcon("resources/gray.jpg"), JLabel.LEFT);
		JLabel imagePass= new JLabel("测试通过", new ImageIcon("resources/green.jpg"), JLabel.LEFT);
		JLabel imageFail = new JLabel("测试失败", new ImageIcon("resources/red.jpg"), JLabel.LEFT);

		dnJPanel.add(imageUntest, new GBC(0, 0).setInsets(5).setWeight(100, 100).setAnchor(GBC.WEST));
		dnJPanel.add(imagePass, new GBC(0, 1).setInsets(5).setWeight(100, 100).setAnchor(GBC.WEST));
		dnJPanel.add(imageFail, new GBC(0, 2).setInsets(5).setWeight(100, 100).setAnchor(GBC.WEST));

		JPanel up_RightJPanel = new JPanel(new BorderLayout(5,5));//右边面板
		JPanel rightUpJPanel = new JPanel(new GridBagLayout());
		JPanel rightDnJPanel = new JPanel(new GridBagLayout());
		
		rightUpJPanel.setBackground(Color.gray);
		rightDnJPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		rightDnJPanel.setBackground(Color.gray);
		
		up_RightJPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		up_RightJPanel.setBackground(Color.gray);
		
		JLabel testInfoLabel = new JLabel("测试信息");
		testInfoLabel.setFont(new Font("宋体", Font.BOLD, 14));
		JLabel testerLabel = new JLabel("测试人员");
		testerField = new JTextField(20);
		JLabel productLabel = new JLabel("产品型号");
		productField = new JTextField(20);
		JLabel snLabel = new JLabel("S/N序列号");
		snField = new JTextField(20);
		JLabel posLabel = new JLabel("POS制造商");
		posField = new JTextField(20);
		
		JLabel leftImageJLabel = new JLabel();
		leftImageJLabel.setIcon(new ImageIcon("resources/pass.png"));
		
		JLabel rightImageJLabel = new JLabel();
		rightImageJLabel.setIcon(new ImageIcon("resources/card.png"));
		
		downPanel.add(leftImageJLabel, new GBC(0, 0).setInsets(5,200,5,0).setWeight(100, 100).setAnchor(GBC.WEST));
		downPanel.add(rightImageJLabel, new GBC(1, 0).setInsets(5,0,5,200).setWeight(100, 100).setAnchor(GBC.EAST));
		
		rightUpJPanel.add(testInfoLabel, new GBC(0, 0).setInsets(5).setWeight(100, 100).setAnchor(GBC.CENTER));
		
		rightDnJPanel.add(testerLabel, new GBC(0, 1).setInsets(5).setWeight(100, 100).setAnchor(GBC.WEST));
		rightDnJPanel.add(testerField, new GBC(1, 1).setInsets(5).setWeight(100, 100).setAnchor(GBC.WEST).setFill(GBC.HORIZONTAL));
	
		rightDnJPanel.add(productLabel, new GBC(0, 2).setInsets(5).setWeight(100, 100).setAnchor(GBC.WEST));
		rightDnJPanel.add(productField, new GBC(1, 2).setInsets(5).setWeight(100, 100).setAnchor(GBC.WEST).setFill(GBC.HORIZONTAL));
		
		rightDnJPanel.add(snLabel, new GBC(0, 3).setInsets(5).setWeight(100, 100).setAnchor(GBC.WEST));
		rightDnJPanel.add(snField, new GBC(1, 3).setInsets(5).setWeight(100, 100).setAnchor(GBC.WEST).setFill(GBC.HORIZONTAL));
		
		rightDnJPanel.add(posLabel, new GBC(0, 4).setInsets(5).setWeight(100, 100).setAnchor(GBC.WEST));
		rightDnJPanel.add(posField, new GBC(1, 4).setInsets(5).setWeight(100, 100).setAnchor(GBC.WEST).setFill(GBC.HORIZONTAL));
		
		up_LeftJPanel.add(westJPanel, BorderLayout.WEST);
		up_LeftJPanel.add(rfResultPanel, BorderLayout.CENTER);
		
		up_RightJPanel.add(rightUpJPanel,BorderLayout.NORTH);
		up_RightJPanel.add(rightDnJPanel,BorderLayout.CENTER);
		
		upPanel.add(up_LeftJPanel);
		upPanel.add(up_RightJPanel);
		
		centerPanel.add(upPanel, BorderLayout.CENTER);
		centerPanel.add(downPanel, BorderLayout.SOUTH);
	}
	
	
	
	
	
	
	
	
	
	private void makeEastPanel(JPanel eastPanel) {
		
		eastPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		eastPanel.setBackground(Color.gray);
		
		eastPanel.setLayout(new GridBagLayout());
		final JButton testButton = new JButton("开始测试");
		testButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				String portName = (String) comboBox.getSelectedItem();
				SerialParameters serialParameters = new SerialParameters();
				serialParameters.setPortName(portName);
				SerialConnection conn = new SerialConnection(serialParameters);
				
				String portName1 = (String) comboBox1.getSelectedItem();
				SerialParameters serialParameters1 = new SerialParameters();
				serialParameters1.setPortName(portName1);
				SerialConnection connPOS = new SerialConnection(serialParameters1);
				
				//System.out.println(portName +" "+portName1);
				
				testButton.setEnabled(false);
				String serialSend ="POLL";
				final byte []buf = serialSend.getBytes();
				if ("mPos".equalsIgnoreCase((String) posBox.getSelectedItem())) {
					pubFun(testButton, buf,conn, connPOS);
				}else {
					pubFun(testButton, buf,conn ,connPOS);
				}
				
			}
			
			private void pubFun(final JButton testButton, final byte[] buf, final SerialConnection conn,final SerialConnection connPOS) {
				new Thread(new Runnable() {
					public void run() {
						if (socket != null && socket.openConn()) {
						    String send ="pickabcard c";
							if (socket.writeReadOneLine(send).contains("ok")) {
								int clearPos = 0;
								for (int i = 0; i < 5; i++) {
									rfResultPanel.drawResultPoint(clearPos, 0);
									clearPos += 5;
								}
								int pos = 0;
								try {
									conn.openConnection();//打开与电压通信串口
									conn.clearSendBeforeData();
									connPOS.openConnection();//打开与POS通信
									connPOS.clearSendBeforeData();
								} catch (Exception e) {
									testButton.setEnabled(true);
									socket.closeConn();
									e.printStackTrace();
									return;
								}
								for (int i = 0; i < 5; i++) {
									//rf5 -999 0 0	 rf5 -999 5 0
									ControlDV controlDV = new ControlDV(conn,jFrame);
									String sendString = "rf5 -999 " + pos + " 0";
									String receive = socket.writeReadOneLine(sendString);
									if (receive.contains("ok")) {
										//打开电压通信串口
										if (conn.isOpen()) {
											
											conn.readExtraData(500);
											conn.clearSendBeforeData();
											controlDV.setRunning(true);
											//conn.serailWrite(startCmd);
											thread = new Thread(controlDV);
											thread.start();
											
										}else {
											testButton.setEnabled(true);
											socket.closeConn();
											conn.closeConnection();
											JOptionPane.showMessageDialog(jFrame, "Serial Open Failed", "Serial Open1", JOptionPane.WARNING_MESSAGE);
											return;
										}
										
										if (connPOS.isOpen()) {
											//connPOS.clearSendBeforeData();
											connPOS.serailWrite(buf);
											//connPOS.serialRead(64,5);
											try {
												Thread.sleep(3000);
												controlDV.setRunning(false);
												thread.join();
											} catch (InterruptedException e) {
												e.printStackTrace();
											}
											//System.out.println("1111");
											double result = controlDV.getResult();
											updateResult(pos, result ,i);
										}else {
											
											testButton.setEnabled(true);
											socket.closeConn();
											connPOS.closeConnection();
											conn.closeConnection();
											JOptionPane.showMessageDialog(jFrame, "Serial Open Failed", "Serial Open2", JOptionPane.WARNING_MESSAGE);
											return ;
										}
											
									}else {
										testButton.setEnabled(true);
										socket.closeConn();
										JOptionPane.showMessageDialog(jFrame, "Socket Open Failed", "Socket Open", JOptionPane.WARNING_MESSAGE);
										return ;
									}
									pos += 5;
								}
								
								String sendPutCard ="putabcard c";
								if (socket.writeReadOneLine(sendPutCard).contains("ok")) {
									
								}else {
									testButton.setEnabled(true);
									socket.closeConn();
									JOptionPane.showMessageDialog(jFrame, "PutCard ABC Failed", "PutCard", JOptionPane.WARNING_MESSAGE);
									return;
								}
								
								testButton.setEnabled(true);
								socket.closeConn();
								connPOS.closeConnection();
								conn.closeConnection();
								JOptionPane.showMessageDialog(jFrame, "请更换产品测试", "提示", JOptionPane.WARNING_MESSAGE);
								
							}else {
								testButton.setEnabled(true);
								socket.closeConn();
								JOptionPane.showMessageDialog(jFrame, "Socket Pickup Failed", "Socket Pickup", JOptionPane.WARNING_MESSAGE);
								return ;
							}
						}else {
							testButton.setEnabled(true);
							socket.closeConn();
							JOptionPane.showMessageDialog(jFrame, "Socket Open Failed", "Socket Open", JOptionPane.WARNING_MESSAGE);
							return;
						}
				}

				private void updateResult(final int pos, final double result,final int i) {
					EventQueue.invokeLater(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							System.out.println(result);
							if (result>2.5&&result<8.1) {
								mainFrame.logger.info(("changqiang:" + result));
								rfResultPanel.drawResultPoint(pos, 1);
								volages[i] = result;
							}else {
								mainFrame.logger.info(("changqiang:" + result));
								rfResultPanel.drawResultPoint(pos, -1);
								volages[i] = result;
							}
						}
					});
				}

			}).start();
		}
	});
		
		JButton configButton = new JButton("测试配置");
		JButton reportButton = new JButton("生成报告");
		reportButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				RFTestInfo info = new RFTestInfo(posField.getText(), testerField.getText(), productField.getText(), snField.getText());
				if(FreemakerTest.reportGenerate("cq.doc", info, volages, 5)){
				try {
					Desktop myDesktop = Desktop.getDesktop();
					myDesktop.open(new File("cq.doc"));
					} catch (IOException e1) {
						e1.printStackTrace();
						JOptionPane.showMessageDialog(jFrame,"自动打开Word失败，请手动打开！", "打开报告",JOptionPane.WARNING_MESSAGE);
						return;
					}
				} else {
					JOptionPane.showMessageDialog(jFrame, "生成报告失败，请检查！","生成报告", JOptionPane.WARNING_MESSAGE);
					return;
				}				
			}
		});
		JButton exitButton = new JButton("退出程序");
		
		eastPanel.add(testButton, new GBC(0, 0).setInsets(1).setWeight(100, 100));
		eastPanel.add(configButton, new GBC(0, 1).setInsets(1).setWeight(100, 100));
		eastPanel.add(reportButton, new GBC(0, 2).setInsets(1).setWeight(100, 100));
		eastPanel.add(exitButton, new GBC(0, 3).setInsets(1).setWeight(100, 100));
	}
}

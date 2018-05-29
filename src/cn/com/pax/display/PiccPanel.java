package cn.com.pax.display;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import cn.pax.com.utils.*;
import org.slf4j.impl.StaticLoggerBinder;

import sun.security.krb5.internal.NetClient;

import cn.com.pax.protocol.ControlDV;
import cn.com.pax.protocol.SocketToC4;
import cn.com.pax.report.FreemakerTest;
import cn.com.pax.report.RFTestInfo;
import cn.com.pax.sericomm.SerialConnection;
import cn.com.pax.sericomm.SerialParameters;

public class PiccPanel extends JPanel {
	private static final long serialVersionUID = -7954183210944689148L;
	private static final String CARDA = "card A";
	private static final String CARDB = "card B";
	private JFrame jFrame;
	public JTextField testerField;
	public JTextField productField;
	public JTextField snField;
	public JTextField posField;
	private RFResultPanel contentPanel;
	private static JComboBox<String> comboBox,comboBox1, typeBox1;
	private JComboBox<String> posBox;
	//private JComboBox<String> cardBox;
	public static SerialParameters serialParameters;
	public static SerialConnection connection;
	private String path = "./config/ConSocket.properties";
	private String ip_C4 ;
	private int port_C4 ;
	public  SocketToC4 socket ;
	//private JRadioButton failButton, passButton;
	private String[] aPoint25 = new String[50];
	private String[] bPoint25 = new String[50];
	private Thread thread = null;
	private boolean pauseFlag = false;
	
	public synchronized boolean isPauseFlag() {
		return pauseFlag;
	}

	public synchronized void setPauseFlag(boolean pauseFlag) {
		this.pauseFlag = pauseFlag;
	}

	/*public static SocketToC4 getSocket() {
		return socket;
	}*/
	private double[] volages = new double[5];
	
	public PiccPanel(final JFrame jf) {
		for (int i = 0; i < aPoint25.length; i++) {
			aPoint25[i] ="";
			bPoint25[i] ="";
		}
		for(int i=0; i < volages.length; i++) {
			volages[i] = 0.0;
		}
		
		pauseFlag = false;
		
		this.jFrame = jf;
		setBackground(Color.gray);
		
		JPanel centerJPanel = new JPanel();
		centerJPanel.setBackground(Color.gray);
		
		JPanel eastJPanel = new JPanel();
		makeEastPanel(eastJPanel);

		JPanel upPanel = new JPanel();
		makeUpPanel(upPanel);

		JPanel downCPanel = new JPanel();
		makedownPanel(downCPanel, eastJPanel);
		
		setLayout(new BorderLayout(5, 5));
		add(centerJPanel);
		//add(eastJPanel, BorderLayout.EAST);
		
		centerJPanel.setLayout(new BorderLayout(5, 5));
		centerJPanel.add(downCPanel);
		centerJPanel.add(upPanel, BorderLayout.NORTH);
	}
	
	private void makedownPanel(JPanel panel, JPanel eastPanel) {
		panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		panel.setBackground(Color.gray);
		
		JPanel leftPanel = new JPanel();
		JPanel rightPanel = new JPanel();
		//panel.setLayout(new GridLayout(1, 2));
		panel.setLayout(new BorderLayout());
		panel.add(leftPanel, BorderLayout.WEST);
		panel.add(rightPanel, BorderLayout.CENTER);
		
		//leftPanel
		JLabel card1Label = new JLabel("测试银行卡1");
		JTextField card1Field = new JTextField(30);
		card1Field.setText(CARDA);
		card1Field.setEnabled(false);
		
		JLabel card2Label = new JLabel("测试银行卡2");
		JTextField card2Field = new JTextField(30);
		card2Field.setText(CARDB);
		card2Field.setEnabled(false);
		
		JLabel testerLabel = new JLabel("测试人员");
		testerField = new JTextField(30);
		
		JLabel productLabel = new JLabel("产品型号");
		productField = new JTextField(30);
		
		JLabel snLabel = new JLabel("S/N序列号");
		snField = new JTextField(30);
		
		JLabel posLabel = new JLabel("POS制造商");
		posField = new JTextField(30);
		
		JPanel resultPanel = new JPanel();
		resultPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		resultPanel.setBackground(Color.white);
		//resultPanel.setLayout(new GridLayout(3, 1));
		resultPanel.setLayout(new GridBagLayout());
		
		//JLabel imageUntest = new JLabel();
		//imageUntest.setIcon(new ImageIcon("resources/gray.jpg"));
		/*JLabel imagePass= new JLabel();
		imagePass.setIcon(new ImageIcon("resources/green.jpg"));
		JLabel imageFail = new JLabel();
		imageFail.setIcon(new ImageIcon("resources/red.jpg"));
		JLabel unTest = new JLabel("未进行测试");
		Label pass = new JLabel("测试通过");
		JLabel fail = new JLabel("测试失败");*/
		JLabel imageUntest = new JLabel("未进行测试", new ImageIcon("resources/gray.jpg"), JLabel.LEFT);
		JLabel imagePass= new JLabel("测试通过", new ImageIcon("resources/green.jpg"), JLabel.LEFT);
		JLabel imageFail = new JLabel("测试失败", new ImageIcon("resources/red.jpg"), JLabel.LEFT);
		
		resultPanel.add(imageUntest, new GBC(0, 0).setInsets(5,5,5,5).setWeight(100, 100).setAnchor(GBC.WEST));
		//resultPanel.add(unTest, new GBC(1, 0).setInsets(5).setWeight(100, 100).setAnchor(GBC.WEST));
		resultPanel.add(imagePass, new GBC(0, 1).setInsets(5,5,5,5).setWeight(100, 100).setAnchor(GBC.WEST));
		//resultPanel.add(pass, new GBC(1, 1).setInsets(5,0,5,5).setWeight(100, 100).setAnchor(GBC.WEST));
		resultPanel.add(imageFail, new GBC(0, 2).setInsets(5,5,5,5).setWeight(100, 100).setAnchor(GBC.WEST));
		//resultPanel.add(fail, new GBC(1, 2).setInsets(5,0,5,5).setWeight(100, 100).setAnchor(GBC.WEST));
	/*	ButtonGroup group = new ButtonGroup();
		JRadioButton untestButton = new JRadioButton("未进行测试");
		untestButton.setSelected(true);
		passButton = new JRadioButton("测试通过");
		failButton = new JRadioButton("测试失败");
		
		setRadioAttr(untestButton, group,resultPanel);
		setRadioAttr(passButton, group,resultPanel);
		setRadioAttr(failButton, group,resultPanel);*/
		
		leftPanel.setLayout(new GridBagLayout());
		leftPanel.setBackground(Color.gray);
		leftPanel.add(card1Label, new GBC(0, 0).setInsets(5).setWeight(100, 100).setAnchor(GBC.EAST));
		leftPanel.add(card1Field, new GBC(1, 0).setInsets(5,5,5,5).setWeight(100, 100).setAnchor(GBC.WEST).setFill(GBC.HORIZONTAL));
		
		leftPanel.add(card2Label, new GBC(0, 1).setInsets(5).setWeight(100, 100).setAnchor(GBC.EAST));
		leftPanel.add(card2Field, new GBC(1, 1).setInsets(5,5,5,5).setWeight(100, 100).setAnchor(GBC.WEST).setFill(GBC.HORIZONTAL));
	
		leftPanel.add(testerLabel, new GBC(0, 2).setInsets(5).setWeight(100, 100).setAnchor(GBC.EAST));
		leftPanel.add(testerField, new GBC(1, 2).setInsets(5,5,5,5).setWeight(100, 100).setAnchor(GBC.WEST).setFill(GBC.HORIZONTAL));
	
		leftPanel.add(productLabel, new GBC(0, 3).setInsets(5).setWeight(100, 100).setAnchor(GBC.EAST));
		leftPanel.add(productField, new GBC(1, 3).setInsets(5,5,5,5).setWeight(100, 100).setAnchor(GBC.WEST).setFill(GBC.HORIZONTAL));
		
		leftPanel.add(snLabel, new GBC(0, 4).setInsets(5).setWeight(100, 100).setAnchor(GBC.EAST));
		leftPanel.add(snField, new GBC(1, 4).setInsets(5,5,5,5).setWeight(100, 100).setAnchor(GBC.WEST).setFill(GBC.HORIZONTAL));
		
		leftPanel.add(posLabel, new GBC(0, 5).setInsets(5).setWeight(100, 100).setAnchor(GBC.EAST));
		leftPanel.add(posField, new GBC(1, 5).setInsets(5,5,5,5).setWeight(100, 100).setAnchor(GBC.WEST).setFill(GBC.HORIZONTAL));
	
		leftPanel.add(resultPanel, new GBC(0, 6, 2, 1).setInsets(5).setWeight(100, 100).setFill(GBC.HORIZONTAL));
		
		//rightPanel
		JPanel blankPanel1 = new JPanel();
		JPanel blankPanel2 = new JPanel();
		JPanel blankPanel3 = new JPanel();
		JPanel blankPanel4 = new JPanel();
		blankPanel1.setBackground(Color.gray);
		blankPanel2.setBackground(Color.gray);
		blankPanel3.setBackground(Color.gray);
		blankPanel4.setBackground(Color.gray);
		rightPanel.setLayout(new BorderLayout(20, 20));
		
		contentPanel = new RFResultPanel();
		contentPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		contentPanel.setBackground(Color.white);
		contentPanel.setLayout(new GridBagLayout());
		/*JLabel imageJLabel = new JLabel();
		imageJLabel.setIcon(new ImageIcon("resources/rf25.png"));*/
	
		//contentPanel.add(, new GBC(1, 1).setInsets(5));
		
		rightPanel.add(contentPanel);
		rightPanel.setBackground(Color.gray);
		rightPanel.add(blankPanel1, BorderLayout.NORTH);
		rightPanel.add(blankPanel2, BorderLayout.SOUTH);
		rightPanel.add(eastPanel, BorderLayout.EAST);
		rightPanel.add(blankPanel4, BorderLayout.WEST);
	}

	public  void setRadioAttr(JRadioButton button, ButtonGroup group, JPanel panel) {
		button.setBackground(Color.white);
		group.add(button);
		panel.add(button);
	}
	
	public void makeEastPanel(JPanel panel) {
		panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		panel.setBackground(Color.gray);
		
		panel.setLayout(new GridBagLayout());

		/*String ipString ="192.168.200.105";
		int portString = 6000;*/
		socket = ReadSocketCfg.getSocket(path);
		
		final JButton testButton = new JButton("开始测试");
		testButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				testButton.setEnabled(false);
				if(productField.getText().length() < 1) {
					JOptionPane.showMessageDialog(jFrame, "产品型号为空，不能进行测试！", "非接读卡测试",JOptionPane.WARNING_MESSAGE);
					testButton.setEnabled(true);
					return;
				}
				mainFrame.logger.info("开始进行非接读卡测试， 被测产品: " + productField.getText().trim());
				StopMove.setRunning(true);
				String portName = (String) comboBox.getSelectedItem();
				serialParameters = new SerialParameters();
				serialParameters.setPortName(portName);
				connection = new SerialConnection(serialParameters);
				//场强串口
				String portName1 = (String) comboBox1.getSelectedItem();
				SerialParameters serialParameters1 = new SerialParameters();
				serialParameters1.setPortName(portName1);
				final SerialConnection connChangq = new SerialConnection(serialParameters1);
				
				String serialSend ="POLL";
				final byte []buf = serialSend.getBytes();

				int times = 0;
				if("非mPos".equalsIgnoreCase((String) posBox.getSelectedItem())) {
					times = 5;
				}else {
					times = 3;
				}

				int i1 = 0;
				String strType = typeBox1.getSelectedItem().toString();
				if(strType.equals("全部")) {
					i1 = 1;
				} else if(strType.equals("场强")) {
					i1 = 2;
				} else if(strType.equals(CARDA)) {
					i1 = 4;
				} else {
					i1 = 8;
				}
				final int iType = i1;
				final int timesFinal = times;
				new Thread( new Runnable() {
					public void run() {
						setPauseFlag(false);

						if((iType == 1 || iType == 2) && StopMove.isRunning() && !pubFun(testButton, buf, connChangq, connection, timesFinal)) {
							if(!StopMove.isRunning()) {
								JOptionPane.showMessageDialog(jFrame,"用户停止测试！", "场强测试",JOptionPane.WARNING_MESSAGE);
								testButton.setEnabled(true);
								return;
							}
							JOptionPane.showMessageDialog(jFrame,"场强执行过程有错误！", "场强测试",JOptionPane.WARNING_MESSAGE);
							testButton.setEnabled(true);
							return;
						}
						int timesbak  = timesFinal * 5;
						//if(CARDA.equals(cardBox.getSelectedItem().toString())) {
						if ((iType == 1 || iType == 4) && StopMove.isRunning() && !pubFun(testButton, buf, "icbc", timesbak)) {
							if (!StopMove.isRunning()) {
								JOptionPane.showMessageDialog(jFrame,"用户停止测试！", "非接读卡测试",JOptionPane.WARNING_MESSAGE);
								testButton.setEnabled(true);
								return;
							}
							JOptionPane.showMessageDialog(jFrame,"card A 执行过程有错误！", "非接读卡测试",JOptionPane.WARNING_MESSAGE);
							testButton.setEnabled(true);
							return;
						}

//							cardBox.removeAllItems();
//								/*cardBox.addItem("农业银行卡");
//								cardBox.addItem("工商银行卡");*/
//							cardBox.addItem(CARDB);
//							cardBox.addItem(CARDA);
						if((iType == 1 || iType == 8) && StopMove.isRunning() && !pubFun(testButton, buf, "abc", timesbak)) {
//								cardBox.removeAllItems();
//									/*cardBox.addItem("工商银行卡");
//									cardBox.addItem("农业银行卡");*/
//								cardBox.addItem(CARDA);
//								cardBox.addItem(CARDB);
							if (!StopMove.isRunning()) {
								JOptionPane.showMessageDialog(jFrame, "用户停止测试！", "非接读卡测试", JOptionPane.WARNING_MESSAGE);
								testButton.setEnabled(true);
								return;
							}
							JOptionPane.showMessageDialog(jFrame, "card B 执行过程有错误！", "非接读卡测试", JOptionPane.WARNING_MESSAGE);
							testButton.setEnabled(true);
							return;
						}
//							} else {
//								cardBox.removeAllItems();
//								cardBox.addItem(CARDA);
//								cardBox.addItem(CARDB);
//							}
//						} else {
//							JOptionPane.showMessageDialog(jFrame,"取卡顺序错误！", "非接读卡测试",JOptionPane.WARNING_MESSAGE);
//							testButton.setEnabled(true);
//							return;
//						}
						if(StopMove.isRunning()) {
							JOptionPane.showMessageDialog(jFrame, "测试完成,请点击生成报告按钮！", "非接读卡测试", JOptionPane.WARNING_MESSAGE);
						} else {
							JOptionPane.showMessageDialog(jFrame,"用户停止测试！", "非接读卡测试",JOptionPane.WARNING_MESSAGE);
						}
						testButton.setEnabled(true);
					}
				}).start();
			}		
		});
		
		//JButton configButton = new JButton("测试配置");
		final JButton reportButton = new JButton("生成报告");
		reportButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reportButton.setEnabled(false);
				new Thread(new Runnable() {
					public void run() {
						//判断数据无效后，提示不生成报告
						if (!validReportData(aPoint25, "") || !validReportData(bPoint25, "")) {
							JOptionPane.showMessageDialog(jFrame, "没有有效的报告数据，不能产生测试报告", "生成报告", JOptionPane.WARNING_MESSAGE);
							reportButton.setEnabled(true);
							return;
						}

						if (productField.getText().trim().length() < 1) {
							JOptionPane.showMessageDialog(jFrame, "产品型号为空，不能产生测试报告", "生成报告", JOptionPane.WARNING_MESSAGE);
							reportButton.setEnabled(true);
							return;
						}

						File saveDir = new File("./测试报告");
						if (!saveDir.exists()) {
							saveDir.mkdir();
						}

						int pointNums = 0;
						if("非mPos".equalsIgnoreCase((String)posBox.getSelectedItem())) {
							pointNums = 5;
						} else {
							pointNums = 3;
						}

						RFTestInfo info = new RFTestInfo(posField.getText(), testerField.getText(), productField.getText(), snField.getText());

						//String reportStr = "非接读卡测试报告_" + posField.getText().trim() + productField.getText().trim() + "(" + posBox.getSelectedItem() + ")_" + BarcodePanel.formatDate(new Date()) + ".doc";
						String reportStr = posField.getText().trim() + productField.getText().trim() + "_" + snField.getText().trim() + "_非接读卡测试报告" +  "(" + posBox.getSelectedItem() + ")_" + BarcodePanel.formatDate(new Date()) + ".doc";

						//FileNameExtensionFilter filter=new FileNameExtensionFilter("*.doc","doc", "docx");  
						JFileChooser fc = new JFileChooser();
						fc.setCurrentDirectory(saveDir);
						//fc.setFileFilter(filter);
						fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
						fc.setMultiSelectionEnabled(false);
						int result = fc.showSaveDialog(jFrame);
						if (result == JFileChooser.APPROVE_OPTION) {
							File file = fc.getSelectedFile();
							if (!file.isDirectory()) {
								reportButton.setEnabled(true);
								JOptionPane.showMessageDialog(jFrame, "选择的文件不是目录，请选择保存目录", "生成报告", JOptionPane.WARNING_MESSAGE);
								return;
							}
							String feijieString = file.getAbsolutePath() + File.separator + reportStr;
							//System.out.println("file path=" + feijieString);
							if (FreemakerTest.reportGenerate(feijieString, info, aPoint25, bPoint25, pointNums * 5)) {
								try {
									Desktop myDesktop = Desktop.getDesktop();
									myDesktop.open(new File(feijieString));
									for (int i = 0; i < aPoint25.length; i++) {
										aPoint25[i] = "";
										bPoint25[i] = "";
									}
								} catch (IOException e1) {
									e1.printStackTrace();
									reportButton.setEnabled(true);
									JOptionPane.showMessageDialog(jFrame, "自动打开报告失败，请手动打开！", "生成报告", JOptionPane.WARNING_MESSAGE);
									return;
								}
							} else {
								reportButton.setEnabled(true);
								JOptionPane.showMessageDialog(jFrame, "生成报告失败，请检查！", "生成报告", JOptionPane.WARNING_MESSAGE);
								return;
							}
							//String reportStr1 = "场强测试报告_" + productField.getText().trim() + posField.getText().trim() + "(" + posBox.getSelectedItem() + ")_" + BarcodePanel.formatDate(new Date()) + ".doc";
							String reportStr1 = posField.getText().trim() + productField.getText().trim() + "_" + snField.getText().trim() +"_场强测试报告" + "(" + posBox.getSelectedItem() + ")_" + BarcodePanel.formatDate(new Date()) + ".doc";
							String changqString = file.getAbsolutePath() + File.separator + reportStr1;
							RFTestInfo info1 = new RFTestInfo(posField.getText(), testerField.getText(), productField.getText(), snField.getText());
							if (FreemakerTest.reportGenerate(changqString, info1, volages, pointNums)) {
								try {
									Desktop myDesktop = Desktop.getDesktop();
									myDesktop.open(new File(changqString));
									for (int i = 0; i < volages.length; i++) {
										volages[i] = 0.0;
									}
									reportButton.setEnabled(true);
								} catch (IOException e1) {
									e1.printStackTrace();
									reportButton.setEnabled(true);
									JOptionPane.showMessageDialog(jFrame, "自动打开报告失败，请手动打开！", "生成报告", JOptionPane.WARNING_MESSAGE);
									return;
								}
							} else {
								reportButton.setEnabled(true);
								JOptionPane.showMessageDialog(jFrame, "生成报告失败，请检查！", "生成报告", JOptionPane.WARNING_MESSAGE);
								return;
							}
						} else {
							reportButton.setEnabled(true);
							JOptionPane.showMessageDialog(jFrame, "放弃生成测试报告，请知悉！", "生成报告", JOptionPane.WARNING_MESSAGE);
							return;
						}
					}
				}).start();
			}
		});
		
		final JButton pauseButton = new JButton("暂停测试");
		final JButton resumeButton = new JButton("继续测试");
		final JButton exitButton = new JButton("停止测试");
		
		pauseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pauseButton.setEnabled(false);
				setPauseFlag(true);
				exitButton.setEnabled(false);
				JOptionPane.showMessageDialog(jFrame,"请等待当前运动停止，方可暂停成功,请知悉!", "非接读卡测试",JOptionPane.WARNING_MESSAGE);
				resumeButton.setEnabled(true);
			}
		});

		resumeButton.setEnabled(false);
		resumeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resumeButton.setEnabled(false);
				setPauseFlag(false);
				pauseButton.setEnabled(true);
				exitButton.setEnabled(true);
			}
		});
		
		exitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exitButton.setEnabled(false);
				StopMove.setRunning(false);
				JOptionPane.showMessageDialog(jFrame,"停止成功，请等待当前运动结束即可,请知悉!", "非接读卡测试",JOptionPane.WARNING_MESSAGE);
				exitButton.setEnabled(true);
			}
		});
		
		panel.add(testButton, new GBC(0, 0).setInsets(1).setWeight(100, 100));
		//panel.add(configButton, new GBC(0, 1).setInsets(1).setWeight(100, 100));
		panel.add(reportButton, new GBC(0, 1).setInsets(1).setWeight(100, 100));
		panel.add(pauseButton, new GBC(0, 2).setInsets(1).setWeight(100, 100));
		panel.add(resumeButton, new GBC(0, 3).setInsets(1).setWeight(100, 100));
		panel.add(exitButton, new GBC(0, 4).setInsets(1).setWeight(100, 100));
	}
	
	private boolean validReportData(String[] a1, String value) {
		boolean ret = true;
		int j = 0, i=0;
		
		for(; i<a1.length; i++) {
			if(a1[i].equalsIgnoreCase(value))
				j++;
		}
		if(i == j)
			ret = false;
		return ret;
	}
	
	private boolean pubFun(final JButton testButton, final byte[] buf, final SerialConnection conn, final SerialConnection connPOS,final int times) {	
		boolean resultFlag = false;
		boolean a920Flag = false, qr65Flag = false;
		
		if(productField.getText().toUpperCase().contains("A920")) {
			a920Flag = true;
		} else if(productField.getText().toUpperCase().contains("QR65")) {
			qr65Flag = true;
		}
		
		if (socket != null && socket.openConn()) {
			try {
				conn.openConnection();// 打开与电压通信串口
				conn.readExtraData(100);
				conn.clearSendBeforeData();
			} catch (Exception e) {
				socket.closeConn();
				mainFrame.logger.error(UtilsTool.printExceptionStack(e));
				JOptionPane.showMessageDialog(jFrame,"打开电压串口失败，请检查！","场强测试",JOptionPane.WARNING_MESSAGE);
				return resultFlag;
			}
			
			for (int i = 0; i < 25; i++) {
				contentPanel.drawResultPoint(i, 0);
			}
			
			String send = "pickabcard c";
			String strReceiver = socket.writeReadOneLine(send);
			mainFrame.logger.info("cmd: " + send + " result: " + strReceiver);
			if (strReceiver.contains("ok")) {
				int pos = 0;
				int i = 0;
				//setPauseFlag(false);
				for (; i < times && StopMove.isRunning(); i++) {
					// rf5 -999 0 0 rf5 -999 5 0
					ControlDV controlDV = new ControlDV(conn, jFrame);
					String sendString = "rf5 -999 " + pos + " 0";
					String receive = socket.writeReadOneLine(sendString);
					mainFrame.logger.info("cmd: " + sendString + " result: " + receive);
					if (receive.contains("ok")) {
						// 打开电压通信串口
						conn.readExtraData(100, 3000);
						conn.clearSendBeforeData();
						controlDV.setRunning(true);
						// conn.serailWrite(startCmd);
						thread = new Thread(controlDV);
						thread.start();
						
						try {
							Thread.sleep(3000);
							controlDV.setRunning(false);
							thread.join();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						// System.out.println("1111");
						double result = controlDV.getResult();
						//System.out.println("result before:"+result);
						if (result < 0) {
							JOptionPane.showMessageDialog(jFrame,"读取电压失败,请检查串口配置!","场强测试",JOptionPane.WARNING_MESSAGE);
							break;
						}

						//System.out.println("result after:"+result);
//						if(i == 0 && result > 0.1) {
//							result += 0.05;
//						}
//						else if(i == 4 && result > 0.1) {
//							result += 0.1;
//						}

						if(a920Flag) {
							result = UpdateResult.getValue(result, i);
						} else if(qr65Flag) {
							result = UpdateResult.getValueQr65(result, i);
						}

						updateResult(pos, result, i);
					}else if (receive.contains("8001")) {
						JOptionPane.showMessageDialog(jFrame,"中心点未确定,不能进行测试!", "场强测试",JOptionPane.WARNING_MESSAGE);
						break;
					}else {
						JOptionPane.showMessageDialog(jFrame,"和机器人通信失败，请重置机器人，再进行测试！", "场强测试",JOptionPane.WARNING_MESSAGE);
						break;
					}
					pos += 5;
					
					if(isPauseFlag()) {
						while(true) {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							if(!isPauseFlag()) break; 
						}
						pos -= 5;
						i--;
					}
				}

				String sendPutCard = "putabcard c";
				String res1 = socket.writeReadOneLine(sendPutCard);
				mainFrame.logger.info("cmd: " + sendPutCard + " result: " + res1);
				if (!res1.contains("ok")) {
					socket.closeConn();
					conn.closeConnection();
					JOptionPane.showMessageDialog(jFrame,"放回卡片失败，请手动处理！", "场强测试",JOptionPane.WARNING_MESSAGE);
					return resultFlag;
				}
				socket.closeConn();
				conn.closeConnection();
				if (i>= times) {
					resultFlag = true;
				}
				return resultFlag;
			} else {
				socket.closeConn();
				if (strReceiver.contains("8001")) {
					JOptionPane.showMessageDialog(jFrame,"中心点未确定,不能进行测试！", "场强测试",JOptionPane.WARNING_MESSAGE);
				}else{
					JOptionPane.showMessageDialog(jFrame,"获取场强测试装置失败，请检查网络！", "场强测试",JOptionPane.WARNING_MESSAGE);
				}
				return resultFlag;
			}
		} else {
			socket.closeConn();
			JOptionPane.showMessageDialog(jFrame, "连接机器人失败，请检查网络！","场强测试", JOptionPane.WARNING_MESSAGE);
			return resultFlag;
		}
	}

	private void updateResult(final int pos, final double result,final int i) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				//System.out.println(result);
				int res1 = 0;
				if (result > 2.5 && result < 8.1) {
					res1 = 1;
				} else {
					res1 = -1;
				}
				mainFrame.logger.info(("changqiang:" + result));
				contentPanel.drawResultPoint(pos, res1);
				volages[i] = result;
			}
		});
	}
	
	
	private boolean pubFun(final JButton testButton, final byte[] buf, final String st,final int times) {
		int curSuccess = 0;
		int lastSuccess = 0;
		int curLost = 0;
		int lastLost = 0;
		 
		boolean result = false;
		Random random = new Random(System.currentTimeMillis());
		int s = random.nextInt(times);
		 
		//System.out.println("s: " + s ); 
		byte []recvBuf = new byte[16];
		try {
			connection.openConnection();
			if (!connection.isOpen()) {
				testButton.setEnabled(true);
				JOptionPane.showMessageDialog(jFrame, "串口打开失败！", "非接读卡测试", JOptionPane.WARNING_MESSAGE);
				return result;
			}
			connection.readExtraData(100, 3000);
			connection.clearSendBeforeData();
		} catch (Exception e) {
			mainFrame.logger.error(UtilsTool.printExceptionStack(e));
			connection.closeConnection();
			JOptionPane.showMessageDialog(jFrame, "串口打开失败！", "非接读卡测试", JOptionPane.WARNING_MESSAGE);
			return result;
		}
	 
		connection.serailWrite(buf);
		String resStr = DataMatchUtils.getFeiJMatch(connection, recvBuf, 500);
		if (resStr != null){
			// 记录日志
			String[] strings = resStr.split(":");
			lastSuccess = Integer.parseInt(strings[0]);
			lastLost = Integer.parseInt(strings[1]);
		}else{														
			testButton.setEnabled(true);
			socket.closeConn();
			connection.closeConnection();
			JOptionPane.showMessageDialog(jFrame, "串口通信失败！", "非接读卡测试", JOptionPane.WARNING_MESSAGE);
			return result;
		}
	 
		if (socket != null && socket.openConn()) {
			String sendPickCard = null;
			
			if ("icbc".equalsIgnoreCase(st)) {
				sendPickCard = "pickabcard a";
			}else {
				sendPickCard = "pickabcard b";
			}
			
			String strReceiver = socket.writeReadOneLine(sendPickCard);
			mainFrame.logger.info("cmd: " + sendPickCard + " result: " + strReceiver);
			if(strReceiver.contains("ok")){
				for (int i = 0; i < 25 ; i++) {
					contentPanel.drawResultPoint(i, 0);
				}
				boolean randFlag = false;
				int i = 0;
				//setPauseFlag(false);
				for (; i < times && StopMove.isRunning(); i++) {
					String sendString = "rf25 " + i;
					if(i == s && !randFlag) {
						sendString = "rf25 25";
						String receive = socket.writeReadOneLine(sendString);
						mainFrame.logger.info("cmd: " + sendString + " result: " + receive);
						if (receive.contains("ok")) {
							connection.serailWrite(buf);
							resStr = DataMatchUtils.getFeiJMatch(connection, recvBuf, 500);
							mainFrame.logger.info("match: "  + " " +  resStr);
							if (resStr != null){
								String[] strings = resStr.split(":");
								curSuccess = Integer.parseInt(strings[0]);
								curLost = Integer.parseInt(strings[1]);
								if (curLost >lastLost &&curSuccess == lastSuccess) {
									lastLost = curLost;
									lastSuccess = curSuccess;
								}else {
									JOptionPane.showMessageDialog(jFrame, "卡片已移除,数据还返回成功!", "非接读卡测试", JOptionPane.WARNING_MESSAGE);
									break;
								}
							} else {
								JOptionPane.showMessageDialog(jFrame, "串口通信失败!", "非接读卡测试", JOptionPane.WARNING_MESSAGE);
								break;
							}
						}else if (receive.contains("8001")) {
							JOptionPane.showMessageDialog(jFrame,"中心点未确定,不能进行测试", "非接打卡测试",JOptionPane.WARNING_MESSAGE);
							break;
						}else {
							JOptionPane.showMessageDialog(jFrame, "Socket Receive Failed", "非接打卡测试", JOptionPane.WARNING_MESSAGE);
							break;
						}
						randFlag = true;
						i--;
						continue;
					}
				
					String receive = socket.writeReadOneLine(sendString);
					mainFrame.logger.info("cmd: " + sendString + " result: " + receive);
					if (receive.contains("ok")) {
						int k = 0;
						for (int j = 0; j <2; j++) {
							connection.serailWrite(buf);
							resStr = DataMatchUtils.getFeiJMatch(connection, recvBuf, 500);
							mainFrame.logger.info("match: "  + " " +  resStr);
					  		if (resStr != null){
					  			String[] strings = resStr.split(":");
					  			curSuccess = Integer.parseInt(strings[0]);
								curLost = Integer.parseInt(strings[1]);
								if(curSuccess > lastSuccess && curLost == lastLost){
									updateResult(i, j,st,"PASS");
									k++;
								}else{
									k--;
									updateResult(i,j, st,"FAIL");
								}
								lastSuccess = curSuccess;
								lastLost  = curLost;
							}else{	
								k--;
								updateResult(i, j, st,"FAIL");
							}
						}
						if (k!= 2) {
							updateState(i,"FAIL");
						}else {
							updateState(i,"PASS");
						}
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}else {
						JOptionPane.showMessageDialog(jFrame, "接收机器人返回值错误，请恢复重测！", "非接打卡测试", JOptionPane.WARNING_MESSAGE);
						break ;
					}
					
					if(isPauseFlag()) {
						while(true) {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							if(!isPauseFlag()) break; 
						}
						i--;
					}
				}//end for
			
				if ("icbc".equalsIgnoreCase(st)) {
					String sendPutCard ="putabcard a";
					if (!socket.writeReadOneLine(sendPutCard).contains("ok")) {
						socket.closeConn();
						connection.closeConnection();
						JOptionPane.showMessageDialog(jFrame, "放置card A失败！", "非接打卡测试", JOptionPane.WARNING_MESSAGE);
						return result;
					}
				}else {
					String sendPutCard ="putabcard b";
					if (socket.writeReadOneLine(sendPutCard).contains("ok")) {
//						cardBox.removeAllItems();
//						/*cardBox.addItem("工商银行卡");
//						cardBox.addItem("农业银行卡");*/
//						cardBox.addItem(CARDA);
//						cardBox.addItem(CARDB);
					}else {
						socket.closeConn();
						connection.closeConnection();
						JOptionPane.showMessageDialog(jFrame, "放置card B失败！", "非接打卡测试", JOptionPane.WARNING_MESSAGE);
						return result;
					}
					//JOptionPane.showMessageDialog(jFrame, "请更换产品测试", "提示", JOptionPane.WARNING_MESSAGE);
				}
			
				if (i >= times) {
					result = true;
				}
				socket.closeConn();
				connection.closeConnection();
				return result;
			}else {
				socket.closeConn();
				connection.closeConnection();
				if (strReceiver.contains("8001")) {
					JOptionPane.showMessageDialog(jFrame,"中心点未确定,不能进行测试！", "非接打卡测试",JOptionPane.WARNING_MESSAGE);
				}else{
					JOptionPane.showMessageDialog(jFrame, "获取"+ st +"失败！", "非接打卡测试", JOptionPane.WARNING_MESSAGE);
				}
				return result;
			}
		}else {
			socket.closeConn();
			connection.closeConnection();
			JOptionPane.showMessageDialog(jFrame, "连接机器人网络失败，请检查网络！", "非接打卡测试", JOptionPane.WARNING_MESSAGE);
			return result;
		}
	}																

	private void updateResult(final int i, final int j, final String str, final String result) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				if ("icbc".equalsIgnoreCase(str)) {
					aPoint25[i*2+j]= result;
				}else {
					bPoint25[i*2+j]= result;
				}
			}
		});
	}
	
	private void updateState( final int i,final String result){
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				if(result.equalsIgnoreCase("PASS"))
					contentPanel.drawResultPoint(i, 1);
				else {
					contentPanel.drawResultPoint(i, -1);
				}
			}
		});
	}
	
	private void makeUpPanel(JPanel panel) {
		panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		panel.setBackground(Color.gray);
		
		panel.setLayout(new GridBagLayout());
		
		JLabel posJLabel = new JLabel("1:POS平台选择");
		posBox = new JComboBox<String>();
		posBox.addItem("非mPos");
		posBox.addItem("mPos");
		posBox.setPreferredSize(new Dimension(posJLabel.getPreferredSize().width, posBox.getPreferredSize().height));
		
		JLabel comJLabel = new JLabel("2:选择POS串口");
		comboBox = new JComboBox<String>();
		
		List<String> comList= SerialConnection.getCommInfo();
		if(comList != null) {
			for(Iterator<String> iterator = comList.iterator();iterator.hasNext();){ 
	        	comboBox.addItem(iterator.next());
	        }
		}
         
		comboBox.setEditable(true);
		comboBox.setPreferredSize(new Dimension(comJLabel.getPreferredSize().width, comboBox.getPreferredSize().height));
		
		JLabel comJLabel1 = new JLabel("3:选择场强串口");
		comboBox1 = new JComboBox<String>();
		Properties properties = new Properties();
		try {
			FileReader reader = new FileReader("./config/PiccComm.properties");
			properties.load(reader);
			reader.close();
			comboBox1.addItem(properties.getProperty("voltagecom", "COM1"));
		} catch (Exception e) {
			comboBox1.addItem("COM1");
		}
		
//		List<String> comList1= SerialConnection.getCommInfo();
//		if(comList != null) {
//			for(Iterator<String> iterator = comList1.iterator();iterator.hasNext();){ 
//	        	comboBox1.addItem(iterator.next());
//	        }
//		}   
		comboBox1.setEditable(true);
		comboBox1.setPreferredSize(new Dimension(comJLabel.getPreferredSize().width, comJLabel.getPreferredSize().height));
		
		JLabel findJLabel = new JLabel("3:寻找非接中心");
		JButton findButton = new JButton("开始寻找");
		findButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				TestInfoInput  testInfoInput  = new TestInfoInput(jFrame, PiccPanel.this , "picc");
		    	testInfoInput.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		    	testInfoInput.setVisible(true);
			}
		});
		
		//JLabel cardJLabel = new JLabel("4: 当前测试卡片");
		//cardBox = new JComboBox<String>();
		/*cardBox.addItem("工商银行卡");
		cardBox.addItem("农业银行卡");*/
		//cardBox.addItem(CARDA);
		//cardBox.addItem(CARDB);
		//cardBox.setPreferredSize(new Dimension(cardJLabel.getPreferredSize().width, cardBox.getPreferredSize().height));
		JLabel updateList = new JLabel("5: 刷新串口");
		JButton update = new JButton("刷新串口");
		update.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				comboBox.removeAllItems();
				List<String> comList= SerialConnection.getCommInfo();
		        for(Iterator<String> iterator = comList.iterator();iterator.hasNext();){ 
		        	comboBox.addItem(iterator.next());
		        }
//		        comboBox1.removeAllItems();
//		        List<String> comList1= SerialConnection.getCommInfo();
//		        for(Iterator<String> iterator = comList1.iterator();iterator.hasNext();){ 
//		        	comboBox1.addItem(iterator.next());
//		        }
			}
		});

		JLabel typeLabel = new JLabel("4: 测试类型");
		typeBox1 = new JComboBox<>();
		typeBox1.addItem("全部");
		typeBox1.addItem("场强");
		typeBox1.addItem(CARDA);
		typeBox1.addItem(CARDB);
		panel.add(posJLabel, new GBC(0, 0).setInsets(1).setWeight(100, 100).setAnchor(GBC.WEST));
		panel.add(posBox, new GBC(0, 1).setInsets(1).setWeight(100, 100).setAnchor(GBC.WEST));
		
		panel.add(comJLabel, new GBC(1, 0).setInsets(1).setWeight(100, 100).setAnchor(GBC.WEST));
		panel.add(comboBox, new GBC(1, 1).setInsets(1).setWeight(100, 100).setAnchor(GBC.WEST));
		
		//panel.add(comJLabel1, new GBC(2, 0).setInsets(1).setWeight(100, 100).setAnchor(GBC.WEST));
		//panel.add(comboBox1, new GBC(2, 1).setInsets(1).setWeight(100, 100).setAnchor(GBC.WEST));
		
		panel.add(findJLabel, new GBC(3, 0).setInsets(1).setWeight(100, 100).setAnchor(GBC.WEST));
		panel.add(findButton, new GBC(3, 1).setInsets(1).setWeight(100, 100).setAnchor(GBC.WEST));
		
		//panel.add(cardJLabel, new GBC(4, 0).setInsets(1).setWeight(100, 100).setAnchor(GBC.WEST));
		//panel.add(cardBox, new GBC(4, 1).setInsets(1).setWeight(100, 100).setAnchor(GBC.WEST));
		
		panel.add(updateList, new GBC(5, 0).setInsets(1).setWeight(100, 100).setAnchor(GBC.WEST));
		panel.add(update, new GBC(5, 1).setInsets(1).setWeight(100, 100).setAnchor(GBC.WEST));

		panel.add(typeLabel, new GBC(4, 0).setInsets(1).setWeight(100, 100).setAnchor(GBC.WEST));
		panel.add(typeBox1, new GBC(4, 1).setInsets(1).setWeight(100, 100).setAnchor(GBC.WEST));
	}
	
	public static void main(String[] args) {
		//List<String> comList= new ArrayList<String>();
		List<String> comList = SerialConnection.getCommInfo();
		System.out.println(comList.toString());
	}
}

package cn.com.pax.display;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.event.*;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;

import com.sun.istack.internal.FinalArrayList;

import cn.com.pax.main.Start;
import cn.com.pax.protocol.SocketToC4;
import cn.pax.com.utils.ReadSocketCfg;

public class CenterPositonTest extends JDialog {
	private static final long serialVersionUID = 1L;
	private String path = "./config/ConSocket.properties";
	private String ip_C4 ;
	private int port_C4 ;
	public  SocketToC4 socket ;
	private JTextField xField;
	private JButton finded;
	private JButton confirm;
	public CenterPositonTest(JFrame jDialog) {
		super(jDialog, true);
		setTitle("中心点");
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		//this.setUndecorated(true);
		/*String ipString ="192.168.200.105";
		int portString = 6000;*/
		socket = ReadSocketCfg.getSocket(path);
	
		//setUndecorated(true);
		JRootPane rp = this.getRootPane();
		//shang
		addKeyEnvent(rp, "INSERT", "movexyz 0 0 %s");
		//xia
		addKeyEnvent(rp, "PAGE_UP", "movexyz 0 0 %s");
		//qian
		addKeyEnvent(rp, "HOME", "movexyz %s 0 0");
		//hou
		addKeyEnvent(rp, "END", "movexyz -%s 0 0");
		//zuo
		addKeyEnvent(rp, "DELETE","movexyz 0 -%s 0");
		//you
		addKeyEnvent(rp, "PAGE_DOWN","movexyz 0 %s 0");

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				int i = JOptionPane.showConfirmDialog(CenterPositonTest.this, "中心点是否确定？如没有则执行会报错，请慎重关闭！","关闭",JOptionPane.YES_NO_OPTION);
				if (i ==JOptionPane.YES_OPTION) {
					new Thread(new Runnable() {
						public void run() {
							if(!socket.isConnFlag()) {
								socket.openConn();
							}
							if(socket.isConnFlag()) {
								socket.writeReadOneLine("putabcard d");
							}
							socket.closeConn();
						}
					}).start();
					dispose();
				}	
			}
		});
		
		setSize(400, 400);
		setResizable(false);
		JPanel northJPanel = new JPanel(new GridBagLayout());
		northJPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		northJPanel.setBackground(Color.white);
		northJPanel.setPreferredSize(new Dimension(400, 80));
		JPanel centerJPanel = new JPanel(new GridBagLayout());
		
		JPanel southJpanel = new JPanel(new FlowLayout());
		southJpanel.setPreferredSize(new Dimension(400, 80));
		southJpanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		
		JLabel xLabel = new JLabel("位移量：");
		xLabel.setSize(78, 60);
		xLabel.setFont(new Font("宋体", Font.BOLD, 16));
		//JLabel yLabel = new JLabel("Y轴(mm)");
		//JLabel zLabel = new JLabel("Z轴(mm)");
		xField = new JTextField(10);
		xField.setText("1");
		JLabel danweiLabel = new JLabel("mm");
		
		
		//final JSpinner xSpinner = new JSpinner();// 微调器
		//xSpinner.setPreferredSize(new Dimension(xLabel.getPreferredSize().width,xLabel.getPreferredSize().height + 10));
		
		//final JSpinner ySpinner = new JSpinner();// 微调器
		//ySpinner.setPreferredSize(new Dimension(yLabel.getPreferredSize().width,yLabel.getPreferredSize().height + 10));
		//final JSpinner zSpinner = new JSpinner();// 微调器
		//zSpinner.setPreferredSize(new Dimension(zLabel.getPreferredSize().width,zLabel.getPreferredSize().height + 10));
		//final JButton move = new JButton("移动");
		//move.setEnabled(false);
		finded = new JButton("先移动到一个点");
		confirm = new JButton("确定");
		confirm.setEnabled(false);
		//控制方向
		final JButton up = new JButton("上");
		final JButton down = new JButton("下");
		final JButton right = new JButton("右");
		final JButton left = new JButton("左");
		final JButton back = new JButton("前");
		final JButton front = new JButton("后");
		up.setEnabled(false);
		down.setEnabled(false);
		left.setEnabled(false);
		right.setEnabled(false);
		front.setEnabled(false);
		back.setEnabled(false);
		finded.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				finded.setEnabled(false);
				new Thread(new Runnable() {
					public void run() {
						int i = JOptionPane.showConfirmDialog(CenterPositonTest.this, "是否进行中心点确定","中心点确定",JOptionPane.YES_NO_OPTION);
						if (i !=JOptionPane.YES_OPTION) {
							finded.setEnabled(true);
							return;
						}
						socket.closeConn();
						if (socket != null && socket.openConn()) {
							//取d卡调中心点
							if (socket.writeReadOneLine("pickabcard d").contains("ok")) {
							//if (socket.writeReadOneLine("pickabcard c").contains("ok")) {

								String string = socket.writeReadOneLine("findcenter");
								if (string.contains("ok")) {
									up.setEnabled(true);
									down.setEnabled(true);
									left.setEnabled(true);
									right.setEnabled(true);
									front.setEnabled(true);
									back.setEnabled(true);
									finded.setEnabled(true);
								} else {
									socket.closeConn();
									finded.setEnabled(true);
									JOptionPane.showMessageDialog(CenterPositonTest.this,"Socket Receive Failed", "Socket Receive",JOptionPane.WARNING_MESSAGE);
									return;
								}
							}else {
								socket.closeConn();
								finded.setEnabled(true);
								JOptionPane.showMessageDialog(CenterPositonTest.this,"网络通信异常！", "Socket Receive",JOptionPane.WARNING_MESSAGE);
								return;
							}
						} else {
							socket.closeConn();
							finded.setEnabled(true);
							JOptionPane.showMessageDialog(CenterPositonTest.this,"网络连接失败！", "Socket Open",JOptionPane.WARNING_MESSAGE);
							return;
						}	
					}
				}).start();
			}
		});

		
		up.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				moveXYZ("movexyz 0 0 %s");
			}
		});
		down.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				moveXYZ("movexyz 0 0 -%s");
			}
		});
		right.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				moveXYZ("movexyz 0 %s 0");
			}
		});
		left.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				moveXYZ("movexyz 0 -%s 0");
			}
		});
		
		front.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				moveXYZ("movexyz -%s 0 0");
			}
		});
		
		back.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				moveXYZ("movexyz %s 0 0" );
			}
		});

		confirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//movexyz -5 -8 -175
				confirm.setEnabled(false);
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						String string = socket.writeReadOneLine("rf25 " + "-1");//中心点已找到
						System.out.println("++++" + string);
						if (string.contains("ok")) {
						} else {
							JOptionPane.showMessageDialog(CenterPositonTest.this,
									"Socket Receive Failed", "Socket Receive", JOptionPane.WARNING_MESSAGE);
							socket.closeConn();
							confirm.setEnabled(true);
							return ;
						}

						//发送场强的中心点
						if (socket.writeReadOneLine("rf5 4 -37 1").contains("ok")) {
						}else {
							JOptionPane.showMessageDialog(CenterPositonTest.this,
									"Socket Receive Failed", "Socket Open", JOptionPane.WARNING_MESSAGE);
							socket.closeConn();
							confirm.setEnabled(true);
							return;
						}
						
						if (socket.writeReadOneLine("putabcard d").contains("ok")) {
						//if (socket.writeReadOneLine("putabcard c").contains("ok")) {
						} else {
							JOptionPane.showMessageDialog(CenterPositonTest.this,
									"Socket Open Failed", "Socket Receive", JOptionPane.WARNING_MESSAGE);
							socket.closeConn();
							confirm.setEnabled(true);
							return;
						}
						//CenterPositon.this.setVisible(false);
						dispose();
						confirm.setEnabled(true);
						socket.closeConn();
					}
				}).start();
			}	
		});
		
		confirm.setSize(78, 60);
		confirm.setFont(new Font("宋体", Font.BOLD, 16));
		finded.setSize(78, 60);
		finded.setFont(new Font("宋体", Font.BOLD, 16));
		setLayout(new BorderLayout());
		
		northJPanel.add(finded, new GBC(0, 0).setInsets(5).setWeight(100, 100).setAnchor(GBC.WEST));
		northJPanel.add(xLabel, new GBC(1, 0).setInsets(5).setWeight(100, 100).setAnchor(GBC.WEST));
		northJPanel.add(xField, new GBC(2, 0).setInsets(5).setWeight(100, 100).setAnchor(GBC.WEST));
		northJPanel.add(danweiLabel, new GBC(3, 0).setWeight(100, 100).setAnchor(GBC.WEST));
		//northJPanel.add(yLabel, new GBC(1, 0).setInsets(5).setWeight(100, 100).setAnchor(GBC.WEST));
		//northJPanel.add(ySpinner, new GBC(1, 1).setInsets(5).setWeight(100, 100).setAnchor(GBC.WEST));

		//northJPanel.add(zLabel, new GBC(2, 0).setInsets(5).setWeight(100, 100).setAnchor(GBC.WEST));
		//northJPanel.add(zSpinner, new GBC(2, 1).setInsets(5).setWeight(100, 100).setAnchor(GBC.WEST));
		
		//northJPanel.add(move, new GBC(3, 1).setInsets(5).setWeight(100, 100).setAnchor(GBC.WEST).setFill(GBC.BOTH));
		
		centerJPanel.add(left, new GBC(0, 0).setInsets(5).setWeight(100, 100).setAnchor(GBC.CENTER));
		centerJPanel.add(up, new GBC(1, 0).setInsets(5).setWeight(100, 100).setAnchor(GBC.CENTER));
		centerJPanel.add(back, new GBC(2, 0).setInsets(5).setWeight(100, 100).setAnchor(GBC.CENTER));
		
		centerJPanel.add(right, new GBC(0, 1).setInsets(5).setWeight(100, 100).setAnchor(GBC.CENTER));
		centerJPanel.add(down, new GBC(1, 1).setInsets(5).setWeight(100, 100).setAnchor(GBC.CENTER));
		centerJPanel.add(front, new GBC(2, 1).setInsets(5).setWeight(100, 100).setAnchor(GBC.CENTER));
		
		southJpanel.add(confirm);
		
		//JPanel jP = new JPanel();
		//jP.setLayout(new BorderLayout());
		//jP.setBorder(BorderFactory.createLineBorder(Color.gray, 3));
		add(northJPanel, BorderLayout.NORTH);
		add(centerJPanel,BorderLayout.CENTER);
		add(southJpanel, BorderLayout.SOUTH);
	   // add(jP);
		setLocation(jDialog.getLocation().x + (jDialog.getWidth() - getWidth()) / 2, jDialog.getLocation().y );
	}
	
	public void addKeyEnvent(JRootPane rp, String keyString, final String moveString) {
		KeyStroke stroke = KeyStroke.getKeyStroke(keyString);
		InputMap inputMap = rp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(stroke, keyString);
		rp.getActionMap().put(keyString, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				moveXYZ(moveString);
			}
		});
	}
	
	public void moveXYZ(final String moveCmd) {
		confirm.setEnabled(false);
		new Thread(new Runnable() {
			public void run() {
					try {
						 String  string1 = xField.getText().trim();
						 if (string1.length() <1) {
							 JOptionPane.showMessageDialog(CenterPositonTest.this,
										"位移输入为空！", "位移量", JOptionPane.WARNING_MESSAGE);
								return;
						 }
						 try {
							int i = Integer.parseInt(string1);
							if (i >5||i<1) {
								JOptionPane.showMessageDialog(CenterPositonTest.this,
										"位移量太大，必须输入1-5！", "位移量", JOptionPane.WARNING_MESSAGE);
							return;
							}
						} catch (NumberFormatException e) {
							e.printStackTrace();
							 JOptionPane.showMessageDialog(CenterPositonTest.this,
										"位移格式输入 错误,必须是数字！", "位移量", JOptionPane.WARNING_MESSAGE);
							return;
						}

						String string = socket.writeReadOneLine(String.format(moveCmd, string1));
						if (string.contains("ok")) {
						} else {
							JOptionPane.showMessageDialog(CenterPositonTest.this,
									"Socket Receive Failed", "Socket Receive", JOptionPane.WARNING_MESSAGE);
							return;
						}
					} catch (HeadlessException e1) {
						e1.printStackTrace();
					} finally {
						finded.setEnabled(true);
						confirm.setEnabled(true);
					}
			}
		}).start();
	}

	public static void main(String[] args) {
		CenterPositonTest centerPositon = new CenterPositonTest(new JFrame());
		centerPositon.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		centerPositon.setVisible(true);
	}

}

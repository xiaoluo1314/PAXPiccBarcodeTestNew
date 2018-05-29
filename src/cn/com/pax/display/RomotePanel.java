package cn.com.pax.display;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import com.sun.xml.internal.ws.api.pipe.Tube;

import sun.util.logging.resources.logging;

import cn.com.pax.protocol.SocketToC4;
import cn.pax.com.utils.ReadRomoteConfig;
import cn.pax.com.utils.ReadSocketCfg;
import cn.pax.com.utils.StopMove;

public class RomotePanel extends JPanel  {
	private SocketToC4 socket;
	private boolean socketFlag = false;
	
	public  RomotePanel(final JFrame jf) {
		setBackground(Color.gray);
		setLayout(new BorderLayout(5,5));
		JPanel westPanel = new JPanel(new BorderLayout(5,5));
		JPanel centerPanel = new JPanel();//没用
		centerPanel.setBackground(Color.gray);
		
		westPanel.setBackground(Color.gray);
		westPanel.setPreferredSize((new Dimension(mainFrame.WIDTH/3, mainFrame.HEIGHT)));
		westPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		
		JLabel serverAddress = new JLabel("服务器地址:");
		JLabel serverPort = new JLabel("端口:");
		final JTextField serverField = new JTextField(20);
		serverField.setText(ReadRomoteConfig.getIp());
		final JTextField portField = new JTextField(20);
		portField.setText(ReadRomoteConfig.getPort());
		StopMove.setExit(false);
		
		final JButton loginButton = new JButton("登   录");
		
		final JButton exitbuButton = new JButton("退   出");
		exitbuButton.setEnabled(false);
		
		final JButton startButton = new JButton("开   始");
		startButton.setEnabled(false);
		
		final JButton machOnButton = new JButton("打开机器人");
		machOnButton.setEnabled(false);
		
		final JButton machOffButton = new JButton("关闭机器人");
		machOffButton.setEnabled(false);
		
		final JButton resetButton = new JButton("重   置");
		resetButton.setEnabled(false);
		
		final JButton stopButton = new JButton("停   止");
		stopButton.setEnabled(false);
		
		final JButton continueButton = new JButton("继   续");
		continueButton.setEnabled(false);
		
		final JButton pauseButton = new JButton("暂   停");
		pauseButton.setEnabled(false);
		
		final JButton sendCardIO = new JButton("控制发卡");
		sendCardIO.setEnabled(false);
		sendCardIO.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				sendCardIO.setEnabled(false);
				if (socketFlag) {
					if ("#SetIO,0".equals(socket.writeReadOneLineForLog("$SetIO,5,0"))) {
					}else {
						JOptionPane.showMessageDialog(jf,"恢复发卡失败,请检查！", "远程控制",JOptionPane.WARNING_MESSAGE);
						exitbuButton.setEnabled(true);
						return;
					}
					if ("#SetIO,0".equals(socket.writeReadOneLineForLog("$SetIO,5,1"))) {
						JOptionPane.showMessageDialog(jf,"发卡成功！", "远程控制",JOptionPane.WARNING_MESSAGE);
						sendCardIO.setEnabled(true);
					}else {
						JOptionPane.showMessageDialog(jf,"发卡失败,请检查！", "远程控制",JOptionPane.WARNING_MESSAGE);
						sendCardIO.setEnabled(true);
						return;
					}	
				}else {
					JOptionPane.showMessageDialog(jf,"远程连接没有打开,请检查！", "远程控制",JOptionPane.WARNING_MESSAGE);
					sendCardIO.setEnabled(true);
					return;
				}
			}
		});
		
		final JButton xitouButton = new JButton("控制吸头");
		xitouButton.setEnabled(false);
		xitouButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				xitouButton.setEnabled(false);
				if (socketFlag) {
					if ("#SetIO,0".equals(socket.writeReadOneLineForLog("$SetIO,4,0"))) {
					}else {
						JOptionPane.showMessageDialog(jf,"恢复吸头失败,请检查！", "远程控制",JOptionPane.WARNING_MESSAGE);
						xitouButton.setEnabled(true);
						return;
					}
					if ("#SetIO,0".equals(socket.writeReadOneLineForLog("$SetIO,4,1"))) {
						JOptionPane.showMessageDialog(jf,"开启吸头成功！", "远程控制",JOptionPane.WARNING_MESSAGE);
						xitouButton.setEnabled(true);
					}else {
						JOptionPane.showMessageDialog(jf,"开启吸头失败,请检查！", "远程控制",JOptionPane.WARNING_MESSAGE);
						xitouButton.setEnabled(true);
						return;
					}	
				}else {
					JOptionPane.showMessageDialog(jf,"远程连接没有打开,请检查！", "远程控制",JOptionPane.WARNING_MESSAGE);
					xitouButton.setEnabled(true);
					return;
				}
			}
		});
		
		loginButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				loginButton.setEnabled(false);
				if (serverField.getText().length()>0 && portField.getText().length()>0 ) {
					socket = new SocketToC4(serverField.getText().trim(), Integer.parseInt(portField.getText().trim()));
					if (socket.openConn()) {
						if ("#Login,0".equals(socket.writeReadOneLineForLog("$Login,"))) {
							socketFlag = true;
							startButton.setEnabled(true);
							resetButton.setEnabled(true);
							machOnButton.setEnabled(true);
							machOffButton.setEnabled(false);
							pauseButton.setEnabled(false);
							continueButton.setEnabled(false);
							stopButton.setEnabled(false);
							exitbuButton.setEnabled(true);
							sendCardIO.setEnabled(true);
							xitouButton.setEnabled(true);
							JOptionPane.showMessageDialog(jf,"远程登录成功！", "远程控制",JOptionPane.WARNING_MESSAGE);
						}else {
							socket.closeConn();
							JOptionPane.showMessageDialog(jf,"远程登录失败,请检查！", "远程控制",JOptionPane.WARNING_MESSAGE);
							loginButton.setEnabled(true);
							return;
						}
					}else {
						JOptionPane.showMessageDialog(jf,"远程控制建立连接失败,请检查！", "远程控制",JOptionPane.WARNING_MESSAGE);
						loginButton.setEnabled(true);
						return;
					}
				}else {
					JOptionPane.showMessageDialog(jf,"IP地址和端口不能为空，请检查！", "远程控制",JOptionPane.WARNING_MESSAGE);
					loginButton.setEnabled(true);
					return;
				}
			}
		});
		
		exitbuButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				exitbuButton.setEnabled(false);
				if (socketFlag) {
					if ("#Logout,0".equals(socket.writeReadOneLineForLog("$Logout"))) {
						socketFlag = false;
						startButton.setEnabled(false);
						resetButton.setEnabled(false);
						machOffButton.setEnabled(false);
						machOnButton.setEnabled(false);
						pauseButton.setEnabled(false);
						continueButton.setEnabled(false);
						stopButton.setEnabled(false);
						loginButton.setEnabled(true);
						sendCardIO.setEnabled(false);
						xitouButton.setEnabled(false);
						StopMove.setExit(false);
						socket.closeConn();
						JOptionPane.showMessageDialog(jf,"远程控制退出成功！", "远程控制",JOptionPane.WARNING_MESSAGE);
					}else {
						//socket.closeConn();
						JOptionPane.showMessageDialog(jf,"远程控制退出失败,请检查！", "远程控制",JOptionPane.WARNING_MESSAGE);
						exitbuButton.setEnabled(true);
						return;
					}
				}else {
					JOptionPane.showMessageDialog(jf,"远程连接没有打开,请检查！", "远程控制",JOptionPane.WARNING_MESSAGE);
					exitbuButton.setEnabled(true);
					return;
				}
				
			}
		});
		
		startButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				startButton.setEnabled(false);
				if (socketFlag) {
					if ("#Start,0".equals(socket.writeReadOneLineForLog("$Start,0"))) {
						//startButton.setEnabled(false);
						resetButton.setEnabled(false);
						machOffButton.setEnabled(false);
						machOnButton.setEnabled(false);
						pauseButton.setEnabled(true);
						continueButton.setEnabled(false);
						stopButton.setEnabled(true);
						StopMove.setExit(true);
						exitbuButton.setEnabled(false);
						sendCardIO.setEnabled(false);
						xitouButton.setEnabled(false);
						JOptionPane.showMessageDialog(jf,"程序开始成功！", "远程控制",JOptionPane.WARNING_MESSAGE);
					}else {
						//socket.closeConn();
						JOptionPane.showMessageDialog(jf,"程序开始失败,请检查！", "远程控制",JOptionPane.WARNING_MESSAGE);
						startButton.setEnabled(true);
						return;
					}
				}else {
					JOptionPane.showMessageDialog(jf,"远程连接没有打开,请检查！", "远程控制",JOptionPane.WARNING_MESSAGE);
					startButton.setEnabled(true);
					return;
				}
			}
		});
		
		
		machOnButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				machOnButton.setEnabled(false);
				if (socketFlag) {
					if ("#SetMotorsOn,0".equals(socket.writeReadOneLineForLog("$SetMotorsOn,0"))) {
						startButton.setEnabled(true);
						resetButton.setEnabled(true);
						machOffButton.setEnabled(true);
						pauseButton.setEnabled(false);
						continueButton.setEnabled(false);
						stopButton.setEnabled(false);
						JOptionPane.showMessageDialog(jf,"机器人打开成功！", "远程控制",JOptionPane.WARNING_MESSAGE);
					}else {
						//socket.closeConn();
						JOptionPane.showMessageDialog(jf,"机器人打开失败,请检查！", "远程控制",JOptionPane.WARNING_MESSAGE);
						machOnButton.setEnabled(true);
						return;
					}
				}else {
					JOptionPane.showMessageDialog(jf,"远程连接没有打开,请检查！", "远程控制",JOptionPane.WARNING_MESSAGE);
					machOnButton.setEnabled(true);
					return;
				}
			}
		});
		
		machOffButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				machOffButton.setEnabled(false);
				if (socketFlag) {
					if ("#SetMotorsOff,0".equals(socket.writeReadOneLineForLog("$SetMotorsOff,0"))) {
						startButton.setEnabled(true);
						resetButton.setEnabled(true);
						machOffButton.setEnabled(false);
						machOnButton.setEnabled(true);
						pauseButton.setEnabled(false);
						continueButton.setEnabled(false);
						stopButton.setEnabled(false);
						JOptionPane.showMessageDialog(jf,"机器人关闭成功！", "远程控制",JOptionPane.WARNING_MESSAGE);
					}else {
						//socket.closeConn();
						JOptionPane.showMessageDialog(jf,"机器人关闭失败,请检查！", "远程控制",JOptionPane.WARNING_MESSAGE);
						machOffButton.setEnabled(true);
						return;
					}
				}else {
					JOptionPane.showMessageDialog(jf,"远程连接没有打开,请检查！", "远程控制",JOptionPane.WARNING_MESSAGE);
					machOffButton.setEnabled(true);
					return;
				}
			}
		});
		
		resetButton.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				resetButton.setEnabled(false);
				if (socketFlag) {
					if ("#Reset,0".equals(socket.writeReadOneLineForLog("$Reset"))) {
						JOptionPane.showMessageDialog(jf,"机器人重置成功!", "远程控制",JOptionPane.WARNING_MESSAGE);
					}else {
						//socket.closeConn();
						JOptionPane.showMessageDialog(jf,"机器人重置失败,请检查！", "远程控制",JOptionPane.WARNING_MESSAGE);
						resetButton.setEnabled(true);
						return;
					}
				}else {
					JOptionPane.showMessageDialog(jf,"远程连接没有打开,请检查！", "远程控制",JOptionPane.WARNING_MESSAGE);
					resetButton.setEnabled(true);
					return;
				}
			}
		});
		
		stopButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				stopButton.setEnabled(false);
				if (socketFlag) {
					if ("#Stop,0".equals(socket.writeReadOneLineForLog("$Stop"))) {
						startButton.setEnabled(true);
						resetButton.setEnabled(true);
						machOffButton.setEnabled(true);
						machOnButton.setEnabled(false);
						pauseButton.setEnabled(false);
						continueButton.setEnabled(false);
						stopButton.setEnabled(false);
						exitbuButton.setEnabled(true);
						sendCardIO.setEnabled(true);
						xitouButton.setEnabled(true);
						JOptionPane.showMessageDialog(jf,"机器人停止成功！", "机器人停止",JOptionPane.WARNING_MESSAGE);
					}else {
						//socket.closeConn();
						JOptionPane.showMessageDialog(jf,"机器人停止失败,请检查！", "机器人停止",JOptionPane.WARNING_MESSAGE);
						stopButton.setEnabled(true);
						return;
					}
				}else {
					JOptionPane.showMessageDialog(jf,"远程连接没有打开,请检查！", "机器人停止",JOptionPane.WARNING_MESSAGE);
					stopButton.setEnabled(true);
					return;
				}
			}
		});
		
		continueButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				continueButton.setEnabled(false);
				if (socketFlag) {
					if ("#Continue,0".equals(socket.writeReadOneLineForLog("$Continue"))) {
						startButton.setEnabled(false);
						resetButton.setEnabled(false);
						
						machOffButton.setEnabled(false);
						machOnButton.setEnabled(false);
						
						pauseButton.setEnabled(true);
						continueButton.setEnabled(false);
						stopButton.setEnabled(true);
						JOptionPane.showMessageDialog(jf,"机器人继续成功,请检查！", "远程控制",JOptionPane.WARNING_MESSAGE);
					}else {
						//socket.closeConn();
						JOptionPane.showMessageDialog(jf,"机器人继续失败,请检查！", "远程控制",JOptionPane.WARNING_MESSAGE);
						continueButton.setEnabled(true);
						return;
					}
				}else {
					JOptionPane.showMessageDialog(jf,"远程连接没有打开,请检查！", "远程控制",JOptionPane.WARNING_MESSAGE);
					continueButton.setEnabled(true);
					return;
				}
			}
		});
	
		pauseButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				pauseButton.setEnabled(false);
				if (socketFlag) {
					if ("#Pause,0".equals(socket.writeReadOneLineForLog("$Pause"))) {
						startButton.setEnabled(false);
						resetButton.setEnabled(false);
						machOffButton.setEnabled(false);
						machOnButton.setEnabled(false);
						pauseButton.setEnabled(false);
						continueButton.setEnabled(true);
						stopButton.setEnabled(true);
						JOptionPane.showMessageDialog(jf,"机器人暂停成功,请检查！", "远程控制",JOptionPane.WARNING_MESSAGE);
					}else {
						//socket.closeConn();
						JOptionPane.showMessageDialog(jf,"机器人暂停失败,请检查！", "远程控制",JOptionPane.WARNING_MESSAGE);
						pauseButton.setEnabled(true);
						return;
					}
				}else {
					JOptionPane.showMessageDialog(jf,"远程连接没有打开,请检查！", "远程控制",JOptionPane.WARNING_MESSAGE);
					pauseButton.setEnabled(true);
					return;
				}
			}
		});
		JPanel north = new JPanel(new GridBagLayout());
		north.setBackground(Color.gray);
		north.setPreferredSize((new Dimension(mainFrame.WIDTH/3, mainFrame.HEIGHT/4 - 20)));
		north.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		north.add(serverAddress, new GBC(0, 0).setInsets(5).setWeight(100, 100).setAnchor(GBC.EAST));
		north.add(serverField, new GBC(1, 0).setInsets(5).setWeight(100, 100).setAnchor(GBC.WEST));
		north.add(serverPort, new GBC(0, 1).setInsets(5).setWeight(100, 100).setAnchor(GBC.EAST));
		north.add(portField, new GBC(1, 1).setInsets(5).setWeight(100, 100).setAnchor(GBC.WEST));
		
		JPanel center = new JPanel(new GridBagLayout());
		center.setBackground(Color.gray);
		center.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		
		JPanel south = new JPanel(new GridBagLayout());
		south.setBackground(Color.gray);
		south.setPreferredSize((new Dimension(mainFrame.WIDTH/3, mainFrame.HEIGHT/4 +20)));
		south.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		
		center.add(loginButton, new GBC(0, 0).setInsets(5).setWeight(100, 100).setAnchor(GBC.WEST));
		center.add(exitbuButton, new GBC(1, 0).setInsets(5).setWeight(100, 100).setAnchor(GBC.WEST));
		center.add(machOnButton, new GBC(0, 1).setInsets(5).setWeight(100, 100).setAnchor(GBC.WEST));
		center.add(machOffButton, new GBC(1, 1).setInsets(5).setWeight(100, 100).setAnchor(GBC.WEST));
		center.add(sendCardIO, new GBC(0, 2).setInsets(5).setWeight(100, 100).setAnchor(GBC.WEST));
		center.add(xitouButton, new GBC(1, 2).setInsets(5).setWeight(100, 100).setAnchor(GBC.WEST));
		
		
		south.add(startButton, new GBC(0, 0).setInsets(5, 100, 5, 5).setWeight(100, 100).setAnchor(GBC.CENTER));
		south.add(resetButton, new GBC(0, 1).setInsets(5).setWeight(100, 100).setAnchor(GBC.WEST));
		south.add(stopButton, new GBC(1, 1).setInsets(5).setWeight(100, 100).setAnchor(GBC.WEST));
		south.add(pauseButton, new GBC(0, 2).setInsets(5).setWeight(100, 100).setAnchor(GBC.WEST));
		south.add(continueButton, new GBC(1, 2).setInsets(5).setWeight(100, 100).setAnchor(GBC.WEST));
		
		//south.add(resetButton);
		//south.add(stopButton);
		//south.add(pauseButton);
		//south.add(continueButton);
		westPanel.add(north,BorderLayout.NORTH);
		westPanel.add(center,BorderLayout.CENTER);
		westPanel.add(south,BorderLayout.SOUTH);
		add(westPanel,BorderLayout.WEST);
		add(centerPanel,BorderLayout.CENTER);	
	}
}

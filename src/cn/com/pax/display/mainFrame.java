package cn.com.pax.display;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.pax.com.utils.StopMove;

public class mainFrame extends JFrame {
	private static final long serialVersionUID = 8217562265600480580L; 
	public static JPanel getCenterPanel() {
		return centerPanel;
	}
	
	public mainFrame() {
		super();
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				if(StopMove.isExit()){
					JOptionPane.showMessageDialog(mainFrame.this,"远程控制在连接，不能退出，如果要退出，请先点停止,再点退出！", "远程控制", JOptionPane.WARNING_MESSAGE);
				}else{
					System.exit(0);
				}
			}
			
		});

		setTitle("C4控制软件");
		setSize(WIDTH, HEIGHT);
		getContentPane().setBackground(Color.gray);
		setResizable(false);
		setIconImage(getToolkit().getImage("resources/logo.png"));
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		this.setLocation((this.getToolkit().getScreenSize().width - WIDTH)/2, (this
				.getToolkit().getScreenSize().height - HEIGHT )/2 );
		
		/*JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu toolsMenu = new JMenu("关于");
		menuBar.add(toolsMenu);
		*/
		
		//interface layout
		setLayout(new BorderLayout(5, 5));
		
		JPanel norPanel = new JPanel();
		centerPanel = new JPanel();
		cardLayout = new CardLayout(5, 5);
		centerPanel.setLayout(cardLayout);
		
		barcodePanel = new BarcodePanel(mainFrame.this);
		piccPanel = new PiccPanel(mainFrame.this);
		//changqPanel = new ChangqPanel(mainFrame.this);
		
		romotePanel = new RomotePanel(mainFrame.this);
		
		centerPanel.add(barcodePanel,"tiaoma");
		centerPanel.add(piccPanel, "shepin");
		//centerPanel.add(changqPanel, "changqiang");
		centerPanel.add(romotePanel,"yuancheng");

		norPanel.setBackground(Color.gray);
		norPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		centerPanel.setBackground(Color.gray);
		centerPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		
		comNorthPanel(norPanel, cardLayout, centerPanel);
		
		add(norPanel, BorderLayout.NORTH);
		add(centerPanel);
	}
	
	private static void comNorthPanel(JPanel northPanel, final CardLayout cardLayout, final JPanel cenPanel) {
		JButton barcodeButton = new JButton("条码扫描测试");
		JButton piccButton = new JButton("非接读卡测试");
		//JButton changButton = new JButton("场强测试");
		JButton remoteButton = new JButton("远程控制");
		
		northPanel.setLayout(new FlowLayout());
		northPanel.add(barcodeButton);
		northPanel.add(piccButton);
		//northPanel.add(changButton);
		northPanel.add(remoteButton);
		
		barcodeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cardLayout.show(cenPanel, "tiaoma");
			}
		});
		piccButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				cardLayout.show(cenPanel, "shepin");
			}
		});
		
		/*changButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				cardLayout.show(cenPanel, "changqiang");
			}
		});*/
		remoteButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				cardLayout.show(cenPanel, "yuancheng");
			}
		});
		
	}
	
	public static  CardLayout getCardLayout() {
		return cardLayout;
	}
	public static JPanel centerPanel;
	public PiccPanel piccPanel;
	//public ChangqPanel changqPanel;
	public BarcodePanel barcodePanel;
	public RomotePanel romotePanel;
	private static CardLayout cardLayout;
	public static final int WIDTH = 1366;
	public static final int HEIGHT = 820;
	public static final Logger logger = LoggerFactory.getLogger(mainFrame.class);
}

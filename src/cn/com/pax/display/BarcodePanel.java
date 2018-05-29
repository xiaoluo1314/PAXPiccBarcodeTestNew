package cn.com.pax.display;

//import gnu.io.CommPortIdentifier;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EtchedBorder;

import cn.com.pax.move.*;
import cn.com.pax.protocol.SocketToC4;
import cn.com.pax.report.FreemakerTest;
import cn.com.pax.report.TestInfo;
import cn.com.pax.report.TiaoMaInfo;
import cn.com.pax.sericomm.SerialConnection;
import cn.com.pax.sericomm.SerialParameters;
import cn.pax.com.utils.*;

public class BarcodePanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private JPanel testInfoPanel;
    private JPanel testItemPanel;
    private JPanel codeInfoAndMoveJPanel;

    private static final int HEIGHT = (int) 800;
    private static final int WIDTH = (int) 1000;
    private static final int SUBHEIGHT = (int) 80;
    private static final int SUBWIDTH = (int) 160;

    private static final String[] TOOLSELECTION = {"手机工装", "取卡工装"};//-请选择使用的工装-
    private static final String[] SANCTYPE = {"一维", "二维"};//"-请输入扫码模块组类型-"
    //private static final String[] PAYBARCODE = {"微信支付一维码","手机微信支付条码"};
    private final String path = "./config/ConSocket.properties";
    //private static final String[] INITPOS = {"复位位置坐标","手机工装初位置坐标","取卡工装初位置坐标"};//-请选择初始位置-

    //public static final Logger logger = LoggerFactory.getLogger(GUIFrame.class);

    public static JComboBox<String> scanTypeBox;
    private JComboBox<String> toolSelectionBox;
    public static JComboBox<String> DUTSerialPortBox;
    public static JButton moveButton;
    private JCheckBox codeTypeChbx;//类型
    private JCheckBox scanDepthChbx;//景深
    private JCheckBox scanAngleChbx;//扫描角度
    private JCheckBox contrastChbx;//对比度
    private JCheckBox colorCodeChbx;//彩色条码
    private JCheckBox stainedCaseChbx;//污损
    private JCheckBox dif_mediaCardChbx;//不同介质卡片

    //new add item
    private JCheckBox brightChbx;
    private JCheckBox accuracyChbx;
    private JCheckBox bigDataChbx;
    private JCheckBox diffCharChbx;
    private JCheckBox fatThinChbx;
    private JCheckBox mirrorChbx;
    private JCheckBox curveChbx;
    private JCheckBox gradientChbx;

    public static JTextField initDisField;
    public static JTextField serialPortScanTimeField;
    public static JTextField barcodeContentField;
    //public static JComboBox<?> payBarcodeBox;
    private JLabel codeTypeLabel;
    private JLabel scanDepthLabel;
    private JLabel scanAngleLabel;
    private JLabel contrastLabel;
    private JLabel colorCodeLabel;
    private JLabel stainedCaseLabel;
    private JLabel dif_mediaCardLabel;
    private JCheckBox move_rateChbox;
    private JCheckBox rotation_angleChbox;

    private JLabel bigDataLabel;
    private JLabel diffCharLabel;
    private JLabel fatThinLabel;
    private JLabel mirrorLabel;
    private JLabel curveLabel;
    private JLabel gradientLabel;
    private JLabel accuracyLabel;
    private JLabel brightLabel;
    private boolean firstTimes;

    //barcodeinfo
    public static JTextField cardNumField;
    //public static JTextField phoneBarcodeProjectNumField;
    public static JTextField excutingField;
    public static JTextField currentTestItemField;
    public static JTextField barcodeNameField;
    public static JTextField obliquAngleField;
    public static JTextField arcAngletField;
    public static JTextField rotation_angleField;
    public static JTextField scanDisField;
    public static JTextField rotation_speedField;
    public static JTextField bright_Field;
    public static JTextArea barcodePre_contentArea;
    public static JTextArea barcodeDecodeContentArea;
    public static JTextArea testResultArea;

    public JButton reportModelButton;

    private Thread newThread;

    private JTextField testerField;
    private JTextField testLocField;
    private JTextField testEnvField;
    private JTextField PosVerField;
    private JTextField HWVerField;
    private JTextField SWVerField;
    private JTextField DUTIdField;
    private JTextField remarkField;

    public static List<TiaoMaInfo> allList = new ArrayList<TiaoMaInfo>();

    private SerialConnection connection;//串口连接对象
    private SerialParameters parameters;
    private JFrame jFrame;

    public BarcodePanel(JFrame jFrame) {
        this.setBackground(Color.gray);
        this.jFrame = jFrame;
        this.setLayout(new BorderLayout(10, 10));
        firstTimes = true;
        createTestInfoPanel();
        this.add(testInfoPanel, BorderLayout.WEST);

        createTestItemPanel();
        this.add(testItemPanel, BorderLayout.CENTER);

        createCodeInfoAndMoveJPanel();
        this.add(codeInfoAndMoveJPanel, BorderLayout.EAST);

        //update toolSelectionBox
        updateCheckStatus(true, false);
        //update scanTypeBox
        rotation_angleChbox.setSelected(false);
        rotation_angleChbox.setEnabled(false);
    }

    public static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        return sdf.format(date);
    }

    private void createTestInfoPanel() {
        testInfoPanel = new JPanel(new BorderLayout(10, 5));

        testInfoPanel.setBackground(Color.gray);

        //testInfoPanel.setPreferredSize(new Dimension(WIDTH / 2 - SUBWIDTH + 30, HEIGHT));

        JPanel testInfomationPanel = new JPanel(new BorderLayout(5, 5));

        testInfomationPanel.setBackground(Color.gray);

        testInfomationPanel.setPreferredSize(new Dimension(WIDTH / 2 - SUBWIDTH - 50, HEIGHT / 2 - SUBHEIGHT));
        testInfomationPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        testInfoPanel.add(testInfomationPanel, BorderLayout.NORTH);

        JPanel scanSettingPanel = new JPanel(new BorderLayout(5, 5));

        scanSettingPanel.setBackground(Color.gray);

        scanSettingPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        testInfoPanel.add(scanSettingPanel, BorderLayout.CENTER);

        JLabel testInfoLabel = new JLabel("测试信息", JLabel.CENTER);
        testInfoLabel.setFont(new Font("宋体", Font.BOLD, 24));

        JLabel testerLabel = new JLabel("测试员", JLabel.LEFT);
        JLabel testLocLabel = new JLabel("测试地点", JLabel.LEFT);
        JLabel testEnvLabel = new JLabel("测试环境", JLabel.LEFT);
        JLabel PosModeLable = new JLabel("POS机型", JLabel.LEFT);
        JLabel HWVersionLabel = new JLabel("硬件版本", JLabel.LEFT);
        JLabel SWVersionLabel = new JLabel("软件版本", JLabel.LEFT);
        JLabel DUTIdLabel = new JLabel("DUT编号", JLabel.LEFT);
        JLabel remarkLabel = new JLabel("备注", JLabel.LEFT);

        testerField = new JTextField(15);
        testLocField = new JTextField(15);
        testEnvField = new JTextField(15);
        PosVerField = new JTextField(15);
        HWVerField = new JTextField(15);
        SWVerField = new JTextField(15);
        DUTIdField = new JTextField(15);
        remarkField = new JTextField(15);

        //System.out.println(111+testerField.getText());
        //System.out.println(testerField.getText() == null);
        //System.out.println("".equals(testerField.getText()));

        JPanel northPanel1 = new JPanel();
        northPanel1.setBackground(Color.gray);

        northPanel1.add(testInfoLabel);
        JPanel centerPanel1 = new JPanel(new GridBagLayout());

        centerPanel1.setBackground(Color.gray);

        centerPanel1.add(testerLabel, new GBC(0, 1).setWeight(100, 100).setAnchor(GBC.EAST).setInsets(5));
        centerPanel1.add(testerField, new GBC(1, 1).setWeight(100, 100).setFill(GBC.HORIZONTAL).setInsets(5,10,5,20));

        centerPanel1.add(testLocLabel, new GBC(0, 2).setWeight(100, 100).setAnchor(GBC.EAST).setInsets(5));
        centerPanel1.add(testLocField, new GBC(1, 2).setWeight(100, 100).setFill(GBC.HORIZONTAL).setInsets(5,10,5,20));

        centerPanel1.add(testEnvLabel, new GBC(0, 3).setWeight(100, 100).setAnchor(GBC.EAST).setInsets(5));
        centerPanel1.add(testEnvField, new GBC(1, 3).setWeight(100, 100).setFill(GBC.HORIZONTAL).setInsets(5,10,5,20));

        centerPanel1.add(PosModeLable, new GBC(0, 4).setWeight(100, 100).setAnchor(GBC.EAST).setInsets(5));
        centerPanel1.add(PosVerField, new GBC(1, 4).setWeight(100, 100).setFill(GBC.HORIZONTAL).setInsets(5,10,5,20));

        centerPanel1.add(HWVersionLabel, new GBC(0, 5).setWeight(100, 100).setAnchor(GBC.EAST).setInsets(5));
        centerPanel1.add(HWVerField, new GBC(1, 5).setWeight(100, 100).setFill(GBC.HORIZONTAL).setInsets(5,10,5,20));

        centerPanel1.add(SWVersionLabel, new GBC(0, 6).setWeight(100, 100).setAnchor(GBC.EAST).setInsets(5));
        centerPanel1.add(SWVerField, new GBC(1, 6).setWeight(100, 100).setFill(GBC.HORIZONTAL).setInsets(5,10,5,20));

        centerPanel1.add(DUTIdLabel, new GBC(0, 7).setWeight(100, 100).setAnchor(GBC.EAST).setInsets(5));
        centerPanel1.add(DUTIdField, new GBC(1, 7).setWeight(100, 100).setFill(GBC.HORIZONTAL).setInsets(5,10,5,20));

        centerPanel1.add(remarkLabel, new GBC(0, 8).setWeight(100, 100).setAnchor(GBC.EAST).setInsets(5));
        centerPanel1.add(remarkField, new GBC(1, 8).setWeight(100, 100).setFill(GBC.HORIZONTAL).setInsets(5,10,5,20));
        testInfomationPanel.add(northPanel1, BorderLayout.NORTH);
        testInfomationPanel.add(centerPanel1, BorderLayout.CENTER);

        JLabel scanSettingLabel = new JLabel("扫码设置", JLabel.CENTER);
        scanSettingLabel.setFont(new Font("宋体", Font.BOLD, 24));

        JLabel toolSelectionLabel = new JLabel("工装选择", JLabel.LEFT);
        toolSelectionBox = new JComboBox<String>();
        initComboBox(toolSelectionBox, TOOLSELECTION);
        //updateCheckStatus(true, false);
        toolSelectionBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (toolSelectionBox.getSelectedItem().toString().contains("手机工装")) {
                    updateCheckStatus(true, false);
                    if(scanTypeBox.getSelectedItem().toString().contains("一维")) {
                        rotation_angleChbox.setSelected(false);
                        rotation_angleChbox.setEnabled(false);
                    }
                } else {
                    updateCheckStatus(false, true);
                }
            }
        });

        JLabel scanTypeLabel = new JLabel("扫码类型", JLabel.LEFT);
        scanTypeBox = new JComboBox<String>();
        initComboBox(scanTypeBox, SANCTYPE);
        scanTypeBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (scanTypeBox.getSelectedItem().toString().contains("二维")) {
                    if(toolSelectionBox.getSelectedItem().toString().contains("取卡工装")) {
                        rotation_angleChbox.setSelected(false);
                        rotation_angleChbox.setEnabled(false);
                    } else {
                        rotation_angleChbox.setSelected(true);
                        rotation_angleChbox.setEnabled(true);
                    }
                } else {
                    rotation_angleChbox.setSelected(false);
                    rotation_angleChbox.setEnabled(false);
                }
            }
        });

        JLabel DUTSerialPortLabel = new JLabel("DUT串口", JLabel.LEFT);
        DUTSerialPortBox = new JComboBox<String>();
        List<String> comlist = SerialConnection.getCommInfo();
        if (comlist != null) {
            for (Iterator<String> iterator = comlist.iterator(); iterator.hasNext(); ) {
                DUTSerialPortBox.addItem(iterator.next());
            }
        }

        //setDUTName(DUTSerialPortBox);
        /*DUTSerialPortBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getSource() == DUTSerialPortBox) {
					CommPortIdentifier portId; 
					Enumeration<CommPortIdentifier> en = CommPortIdentifier.getPortIdentifiers();
					while (en.hasMoreElements()) {
						portId = (CommPortIdentifier) en.nextElement();
						
						if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
							int i;
							for (i = 0; i < DUTSerialPortBox.getItemCount(); i++) {
								if (portId.getName().equalsIgnoreCase(String.valueOf(DUTSerialPortBox.getItemAt(i)))) {
									break;
								}
							}
							if(i == DUTSerialPortBox.getItemCount())
								DUTSerialPortBox.addItem(portId.getName());
						}
					}	
				}
				if (connection!= null && connection.isOpen()) {
					if (e.getItemSelectable() == DUTSerialPortBox) {
						JOptionPane.showMessageDialog(jFrame, "You have opend a serial port, please close before switching", "Open Comm", JOptionPane.WARNING_MESSAGE);
						return;
					}
					
				} 
			}
		});*/

        //JLabel payBarcodeLabel = new JLabel("支付条码   ",JLabel.LEFT);
        //payBarcodeBox = new JComboBox();
        //initComboBox(payBarcodeBox, PAYBARCODE);

        //JLabel barcodeContentLabel = new JLabel("条码内容   ",JLabel.LEFT);
        //barcodeContentField = new JTextField(20);
        //barcodeContentField.setText("021250P");
        JLabel initDisJLabel = new JLabel("初始距离");
        initDisField = new JTextField(6);
        JLabel unitDisLabel = new JLabel("mm", JLabel.LEFT);
        initDisField.setText("0");
        JLabel serialPortScanTimeJLabel = new JLabel("串口扫描时间");
        serialPortScanTimeField = new JTextField(6);
        serialPortScanTimeField.setText("2000");
        JLabel unitMilsecLabel = new JLabel("ms", JLabel.LEFT);

        JPanel northPanel2 = new JPanel();
        northPanel2.setBackground(Color.gray);

        northPanel2.add(scanSettingLabel);
        JPanel centerPanel2 = new JPanel(new GridBagLayout());
        centerPanel2.setBackground(Color.gray);

        JPanel southPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        southPanel2.setBackground(Color.gray);

        centerPanel2.add(toolSelectionLabel, new GBC(0, 0).setWeight(100, 100).setAnchor(GBC.EAST).setInsets(5));
        centerPanel2.add(toolSelectionBox, new GBC(1, 0).setWeight(100, 100).setFill(GBC.HORIZONTAL).setInsets(5,10,5,20));

        centerPanel2.add(scanTypeLabel, new GBC(0, 1).setWeight(100, 100).setAnchor(GBC.EAST).setInsets(5));
        centerPanel2.add(scanTypeBox, new GBC(1, 1).setWeight(100, 100).setFill(GBC.HORIZONTAL).setInsets(5,10,5,20));

        centerPanel2.add(DUTSerialPortLabel, new GBC(0, 2).setWeight(100, 100).setAnchor(GBC.EAST).setInsets(5));
        centerPanel2.add(DUTSerialPortBox, new GBC(1, 2).setWeight(100, 100).setFill(GBC.HORIZONTAL).setInsets(5,10,5,20));

        //centerPanel2.add(payBarcodeLabel, new GBC(0,3).setWeight(100, 100).setAnchor(GBC.EAST).setInsets(5));
        //centerPanel2.add(payBarcodeBox, new GBC(1, 3).setWeight(100, 100).setFill(GBC.HORIZONTAL).setInsets(5));

        //centerPanel2.add(barcodeContentLabel, new GBC(0, 4).setWeight(100, 100).setAnchor(GBC.EAST).setInsets(5));
        //centerPanel2.add(barcodeContentField, new GBC(1, 4).setWeight(100, 100).setFill(GBC.HORIZONTAL).setInsets(5));

        centerPanel2.add(initDisJLabel, new GBC(0, 3).setWeight(100, 100).setAnchor(GBC.EAST).setInsets(5));
        centerPanel2.add(initDisField, new GBC(1, 3).setFill(GBC.HORIZONTAL).setInsets(5,10,5,5));
        centerPanel2.add(unitDisLabel, new GBC(2, 3).setWeight(100, 100).setAnchor(GBC.WEST).setInsets(0));

        centerPanel2.add(serialPortScanTimeJLabel, new GBC(0, 4).setWeight(100, 100).setAnchor(GBC.EAST).setInsets(5));
        centerPanel2.add(serialPortScanTimeField, new GBC(1, 4).setFill(GBC.HORIZONTAL).setInsets(5,10,5,5));
        centerPanel2.add(unitMilsecLabel, new GBC(2, 4).setWeight(100, 100).setAnchor(GBC.WEST).setInsets(0));

        //JLabel openDataFramemode = new JLabel("启用数据帧模式",JLabel.LEFT);
        //openDataFramemode.setEnabled(false);
        //JCheckBox openDataFramemodeBox = new JCheckBox("true");
        //openDataFramemodeBox.setEnabled(false);
        //southPanel2.add(openDataFramemode);
        //southPanel2.add(openDataFramemodeBox);

        scanSettingPanel.add(northPanel2, BorderLayout.NORTH);
        scanSettingPanel.add(centerPanel2, BorderLayout.CENTER);
        scanSettingPanel.add(southPanel2, BorderLayout.SOUTH);
    }

    private void updateCheckStatus(boolean b1, boolean b2) {
        codeTypeChbx.setSelected(b1);
        scanDepthChbx.setSelected(b1);
        move_rateChbox.setSelected(b1);
        rotation_angleChbox.setSelected(b1);
        scanAngleChbx.setSelected(b1);
        contrastChbx.setSelected(b1);
        colorCodeChbx.setSelected(b1);
        stainedCaseChbx.setSelected(b1);
        bigDataChbx.setSelected(b1);
        diffCharChbx.setSelected(b1);
        fatThinChbx.setSelected(b1);
        mirrorChbx.setSelected(b1);
        curveChbx.setSelected(b1);
        gradientChbx.setSelected(b1);
        accuracyChbx.setSelected(b1);
        brightChbx.setSelected(b1);

        dif_mediaCardChbx.setSelected(b2);
    }

    private void initComboBox(JComboBox<String> box, String[] str) {
        for (int i = 0; i < str.length; i++) {
            box.addItem(str[i]);
        }
    }

    /*private void setDUTName(JComboBox box){
        CommPortIdentifier portId;
        Enumeration<CommPortIdentifier> en = CommPortIdentifier.getPortIdentifiers();

        if(! en.hasMoreElements())
            box.addItem("None");
        while (en.hasMoreElements()) {
            portId = (CommPortIdentifier) en.nextElement();

            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                box.addItem(portId.getName());
            }
        }
    }*/
    private void createTestItemPanel() {
        testItemPanel = new JPanel(new BorderLayout(5, 5));
        testItemPanel.setBackground(Color.gray);
        testItemPanel.setPreferredSize(new Dimension(SUBWIDTH + 30, HEIGHT));
        //testItemPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        JPanel northPanel = new JPanel(new GridBagLayout());
        northPanel.setBackground(Color.gray);

        northPanel.setPreferredSize(new Dimension(SUBWIDTH + 30, HEIGHT - 750));
        northPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        JPanel centerPanel = new JPanel(new BorderLayout());
        //centerPanel.setPreferredSize(new Dimension(SUBWIDTH +50, HEIGHT));
        //centerPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        centerPanel.setBackground(Color.gray);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setPreferredSize(new Dimension(SUBWIDTH + 30, HEIGHT - 650));
        //southPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        southPanel.setBackground(Color.gray);
        /**
         * 第一块
         */
        JLabel testItemLabel = new JLabel("测试项", JLabel.CENTER);
        testItemLabel.setFont(new Font("宋体", Font.BOLD, 24));

//		JButton SelectCodeBt = new JButton("选择条码");
//		SelectCodeBt.setFont(new Font("黑体",Font.BOLD, 14));
//		SelectCodeBt.setEnabled(false);
//		SelectCodeBt.setSize(100, 100);
//		SelectCodeBt.setBackground(new Color(0,0,255));
		/*northPanel.add(testItemLabel, BorderLayout.NORTH);
		northPanel.add(SelectCodeBt, BorderLayout.CENTER);*/
        northPanel.add(testItemLabel, new GBC(0, 0).setWeight(100, 100).setAnchor(GBC.CENTER).setInsets(5));
        //northPanel.add(SelectCodeBt, new GBC(0, 1 ).setWeight(100, 100).setFill(GBC.BOTH).setAnchor(GBC.CENTER).setInsets(5));

        /**
         * 第二块
         */
        scanDepthChbx = new JCheckBox();
        scanDepthLabel = new JLabel("扫码景深");

        scanAngleChbx = new JCheckBox();
        scanAngleLabel = new JLabel("扫描角度");

        move_rateChbox = new JCheckBox();
        JLabel move_rateLabel = new JLabel("扫描速度");

        rotation_angleChbox = new JCheckBox();
        JLabel rotation_angleLabel = new JLabel("360旋转角度");

        JPanel twoPanel = new JPanel(new BorderLayout());
        twoPanel.setBackground(Color.gray);
        JLabel phoneToolLabel = new JLabel("手机工装", JLabel.CENTER);//phoneToolLabel
        phoneToolLabel.setFont(new Font("宋体", Font.BOLD, 18));

//        JPanel norJPanel = new JPanel(new GridBagLayout());
//        norJPanel.setBackground(Color.gray);
//        norJPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
//        norJPanel.setPreferredSize(new Dimension(SUBWIDTH + 50, HEIGHT - 660));

        JPanel centJPanel = new JPanel(new GridBagLayout());
        centJPanel.setBackground(Color.gray);
        centJPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        codeTypeChbx = new JCheckBox();
        codeTypeLabel = new JLabel("码制类型");
        contrastChbx = new JCheckBox();
        contrastLabel = new JLabel("对比度");
        colorCodeChbx = new JCheckBox();
        colorCodeLabel = new JLabel("彩色条码");
        stainedCaseChbx = new JCheckBox();
        stainedCaseLabel = new JLabel("污损案例");
        bigDataChbx = new JCheckBox();
        bigDataLabel = new JLabel("大数据条码");
        diffCharChbx = new JCheckBox();
        diffCharLabel = new JLabel("不同字符解码");
        fatThinChbx = new JCheckBox();
        fatThinLabel = new JLabel("胖瘦畸变");
        mirrorChbx = new JCheckBox();
        mirrorLabel = new JLabel("镜像码");
        curveChbx = new JCheckBox();
        curveLabel = new JLabel("曲面码");
        gradientChbx = new JCheckBox();
        gradientLabel = new JLabel("渐变码");
        accuracyChbx = new JCheckBox();
        accuracyLabel = new JLabel("识别精度");
        brightChbx = new JCheckBox();
        brightLabel = new JLabel("手机背光亮度");

        centJPanel.add(scanDepthChbx, new GBC(0, 0).setWeight(100, 100).setAnchor(GBC.EAST).setInsets(5));
        centJPanel.add(scanDepthLabel, new GBC(1, 0).setWeight(100, 100).setFill(GBC.HORIZONTAL).setInsets(5));
        centJPanel.add(scanAngleChbx, new GBC(2, 0).setWeight(100, 100).setAnchor(GBC.EAST).setInsets(5));
        centJPanel.add(scanAngleLabel, new GBC(3, 0).setWeight(100, 100).setFill(GBC.HORIZONTAL).setInsets(5));

        centJPanel.add(move_rateChbox, new GBC(0, 1).setWeight(100, 100).setAnchor(GBC.EAST).setInsets(5));
        centJPanel.add(move_rateLabel, new GBC(1, 1).setWeight(100, 100).setFill(GBC.HORIZONTAL).setInsets(5));
        centJPanel.add(rotation_angleChbox, new GBC(2, 1).setWeight(100, 100).setAnchor(GBC.EAST).setInsets(5));
        centJPanel.add(rotation_angleLabel, new GBC(3, 1).setWeight(100, 100).setFill(GBC.HORIZONTAL).setInsets(5));

        JLabel blk2Label = new JLabel("");
        blk2Label.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        blk2Label.setBackground(Color.gray);
        centJPanel.add(blk2Label, new GBC(0, 2, 4, 1).setWeight(100, 100).setFill(GBC.HORIZONTAL));


        centJPanel.add(codeTypeChbx, new GBC(0, 3).setWeight(100, 100).setAnchor(GBC.EAST).setInsets(5));
        centJPanel.add(codeTypeLabel, new GBC(1, 3).setWeight(100, 100).setFill(GBC.HORIZONTAL).setInsets(5));
        centJPanel.add(contrastChbx, new GBC(2, 3).setWeight(100, 100).setAnchor(GBC.EAST).setInsets(5));
        centJPanel.add(contrastLabel, new GBC(3, 3).setWeight(100, 100).setFill(GBC.HORIZONTAL).setInsets(5));

        centJPanel.add(colorCodeChbx, new GBC(0, 4).setWeight(100, 100).setAnchor(GBC.EAST).setInsets(5));
        centJPanel.add(colorCodeLabel, new GBC(1, 4).setWeight(100, 100).setFill(GBC.HORIZONTAL).setInsets(5));
        centJPanel.add(stainedCaseChbx, new GBC(2, 4).setWeight(100, 100).setAnchor(GBC.EAST).setInsets(5));
        centJPanel.add(stainedCaseLabel, new GBC(3, 4).setWeight(100, 100).setFill(GBC.HORIZONTAL).setInsets(5));

        JLabel blkLabel1 = new JLabel("");
        blkLabel1.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        blkLabel1.setBackground(Color.gray);
        centJPanel.add(blkLabel1, new GBC(0, 5, 4, 1).setWeight(100, 100).setFill(GBC.HORIZONTAL));

        centJPanel.add(bigDataChbx, new GBC(0, 6).setWeight(100, 100).setAnchor(GBC.EAST).setInsets(5));
        centJPanel.add(bigDataLabel, new GBC(1, 6).setWeight(100, 100).setFill(GBC.HORIZONTAL).setInsets(5));
        centJPanel.add(diffCharChbx, new GBC(2, 6).setWeight(100, 100).setAnchor(GBC.EAST).setInsets(5));
        centJPanel.add(diffCharLabel, new GBC(3, 6).setWeight(100, 100).setFill(GBC.HORIZONTAL).setInsets(5));

        centJPanel.add(fatThinChbx, new GBC(0, 7).setWeight(100, 100).setAnchor(GBC.EAST).setInsets(5));
        centJPanel.add(fatThinLabel, new GBC(1, 7).setWeight(100, 100).setFill(GBC.HORIZONTAL).setInsets(5));
        centJPanel.add(mirrorChbx, new GBC(2, 7).setWeight(100, 100).setAnchor(GBC.EAST).setInsets(5));
        centJPanel.add(mirrorLabel, new GBC(3, 7).setWeight(100, 100).setFill(GBC.HORIZONTAL).setInsets(5));

        centJPanel.add(curveChbx, new GBC(0, 8).setWeight(100, 100).setAnchor(GBC.EAST).setInsets(5));
        centJPanel.add(curveLabel, new GBC(1, 8).setWeight(100, 100).setFill(GBC.HORIZONTAL).setInsets(5));
        centJPanel.add(gradientChbx, new GBC(2, 8).setWeight(100, 100).setAnchor(GBC.EAST).setInsets(5));
        centJPanel.add(gradientLabel, new GBC(3, 8).setWeight(100, 100).setFill(GBC.HORIZONTAL).setInsets(5));

        centJPanel.add(accuracyChbx, new GBC(0, 9).setWeight(100, 100).setAnchor(GBC.EAST).setInsets(5));
        centJPanel.add(accuracyLabel, new GBC(1, 9).setWeight(100, 100).setFill(GBC.HORIZONTAL).setInsets(5));


        JLabel blkLabel2 = new JLabel("");
        blkLabel2.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        blkLabel2.setBackground(Color.gray);
        centJPanel.add(blkLabel2, new GBC(0, 10, 4, 1).setWeight(100, 100).setFill(GBC.HORIZONTAL));

        centJPanel.add(brightChbx, new GBC(0, 11).setWeight(100, 100).setAnchor(GBC.EAST).setInsets(5));
        centJPanel.add(brightLabel, new GBC(1, 11).setWeight(100, 100).setFill(GBC.HORIZONTAL).setInsets(5));

        centerPanel.add(phoneToolLabel, BorderLayout.NORTH);

        //twoPanel.add(norJPanel, BorderLayout.NORTH);
        twoPanel.add(centJPanel, BorderLayout.CENTER);
        centerPanel.add(twoPanel, BorderLayout.CENTER);

        /**
         * 第三块
         */
        JPanel threepPanel = new JPanel(new GridBagLayout());
        threepPanel.setBackground(Color.gray);
        threepPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        JLabel takeCardLabel = new JLabel("取卡工装", JLabel.CENTER);
        takeCardLabel.setFont(new Font("宋体", Font.BOLD, 18));

        dif_mediaCardChbx = new JCheckBox();
        dif_mediaCardLabel = new JLabel("不同介质卡片");

        threepPanel.add(dif_mediaCardChbx, new GBC(0, 0).setWeight(100, 100).setAnchor(GBC.EAST).setInsets(5));
        threepPanel.add(dif_mediaCardLabel, new GBC(1, 0, 1, 1).setWeight(100, 100).setFill(GBC.HORIZONTAL).setInsets(5));
//        JLabel label1 = new JLabel("      "), label2 = new JLabel();
//        threepPanel.add(label1, new GBC(2, 0).setWeight(100, 100).setFill(GBC.EAST).setInsets(5));
//        threepPanel.add(label2, new GBC(3, 0).setWeight(100, 100).setFill(GBC.HORIZONTAL).setInsets(5));

        southPanel.add(takeCardLabel, BorderLayout.NORTH);
        southPanel.add(threepPanel, BorderLayout.CENTER);

        testItemPanel.add(northPanel, BorderLayout.NORTH);
        testItemPanel.add(centerPanel, BorderLayout.CENTER);
        testItemPanel.add(southPanel, BorderLayout.SOUTH);
    }

    private void createCodeInfoAndMoveJPanel() {
        codeInfoAndMoveJPanel = new JPanel(new BorderLayout(10, 10));
        codeInfoAndMoveJPanel.setBackground(Color.gray);
        JPanel codeInfoPanel = new JPanel(new BorderLayout(10, 10));
        codeInfoPanel.setBackground(Color.gray);
        codeInfoPanel.setPreferredSize(new Dimension(WIDTH / 2 - 130, HEIGHT));
        codeInfoPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        JPanel moveSetPanel = new JPanel(new GridBagLayout());
        moveSetPanel.setBackground(Color.gray);
        moveSetPanel.setPreferredSize(new Dimension(SUBWIDTH, HEIGHT));
        moveSetPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        JPanel centPanel = new JPanel(new GridBagLayout());
        centPanel.setBackground(Color.gray);
        JLabel barcodeInfoLabel = new JLabel("条码信息", JLabel.CENTER);
        barcodeInfoLabel.setFont(new Font("宋体", Font.BOLD, 24));
        JLabel cardNumLabel = new JLabel("卡片张数", JLabel.LEFT);
        cardNumField = new JTextField(10);
        //cardNumField.setText("0");
        //JLabel phoneBarcodeProjectNumLabel = new JLabel("手机条码项目数",JLabel.LEFT); phoneBarcodeProjectNumField = new JTextField(10);
        //phoneBarcodeProjectNumField.setText("");
        JLabel excutingLabel = new JLabel("正在执行", JLabel.LEFT);
        excutingField = new JTextField(10);
        JLabel currentTestItemLabel = new JLabel("当前测试项", JLabel.LEFT);
        currentTestItemField = new JTextField(33);
        JLabel barcodeNameLabel = new JLabel("条码名称", JLabel.LEFT);
        barcodeNameField = new JTextField(18);
        JLabel obliquAngleLabel = new JLabel("倾斜角度(左右)", JLabel.LEFT);
        obliquAngleField = new JTextField(10);
        JLabel obliquAngleUnitLabel = new JLabel("度", JLabel.LEFT);

        JLabel arcAngleLabel = new JLabel("偏转角度(上下)", JLabel.LEFT);
        arcAngletField = new JTextField(10);
        JLabel arcAngleUnitLabel = new JLabel("度", JLabel.LEFT);
        JLabel rotation_angleLabel = new JLabel("360旋转角度", JLabel.LEFT);
        rotation_angleField = new JTextField(10);
        JLabel rotation_angleUnitLabel = new JLabel("度", JLabel.LEFT);

        JLabel scanDisLabel = new JLabel("扫描距离", JLabel.LEFT);
        scanDisField = new JTextField(10);
        JLabel scanDisUnitLabel = new JLabel("mm", JLabel.LEFT);
        JLabel rotation_speedLabel = new JLabel("扫描速度", JLabel.LEFT);
        rotation_speedField = new JTextField(10);
        JLabel rotation_speedUnitLabel = new JLabel("mm/s", JLabel.LEFT);
        JLabel brightLabel = new JLabel("手机背光亮度", JLabel.LEFT);
        bright_Field = new JTextField(10);


        JLabel barcodePre_contentLabel = new JLabel("条码预设内容", JLabel.LEFT);
        barcodePre_contentArea = new JTextArea(3, 1);
        barcodePre_contentArea.setLineWrap(true);
        barcodePre_contentArea.setWrapStyleWord(true);
        barcodePre_contentArea.setEditable(true);
        JScrollPane barcodePre_contentJsPanel = new JScrollPane(barcodePre_contentArea);
        JLabel barcodeDecodeContentLabel = new JLabel("条码解码内容", JLabel.LEFT);
        barcodeDecodeContentArea = new JTextArea(3, 1);
        barcodeDecodeContentArea.setLineWrap(true);
        barcodeDecodeContentArea.setWrapStyleWord(true);
        barcodeDecodeContentArea.setEditable(true);
        JScrollPane barcodeDecodeContentJsPane = new JScrollPane(barcodeDecodeContentArea);
        JLabel testResultLabel = new JLabel("测试结果");
        testResultArea = new JTextArea(10, 1);
        JScrollPane resultPane = new JScrollPane(testResultArea);

        centPanel.add(cardNumLabel, new GBC(0, 0).setWeight(100, 100).setAnchor(GBC.EAST).setInsets(5));
        centPanel.add(cardNumField, new GBC(1, 0).setWeight(100, 100).setFill(GBC.HORIZONTAL).setInsets(5));

        //centPanel.add(phoneBarcodeProjectNumLabel, new GBC(0, 1).setWeight(100, 100).setAnchor(GBC.EAST).setInsets(5));
        //centPanel.add(phoneBarcodeProjectNumField, new GBC(1, 1).setWeight(100, 100).setFill(GBC.HORIZONTAL).setInsets(5));

        centPanel.add(excutingLabel, new GBC(0, 2).setWeight(100, 100).setAnchor(GBC.EAST).setInsets(5));
        centPanel.add(excutingField, new GBC(1, 2).setWeight(100, 100).setFill(GBC.HORIZONTAL).setInsets(5));

        centPanel.add(currentTestItemLabel, new GBC(0, 3).setWeight(100, 100).setAnchor(GBC.EAST).setInsets(5));
        centPanel.add(currentTestItemField, new GBC(1, 3).setWeight(100, 100).setFill(GBC.HORIZONTAL).setInsets(5));

        centPanel.add(barcodeNameLabel, new GBC(0, 4).setWeight(100, 100).setAnchor(GBC.EAST).setInsets(5));
        centPanel.add(barcodeNameField, new GBC(1, 4).setWeight(100, 100).setFill(GBC.HORIZONTAL).setInsets(5));

        centPanel.add(obliquAngleLabel, new GBC(0, 5).setWeight(100, 100).setAnchor(GBC.EAST).setInsets(5));
        centPanel.add(obliquAngleField, new GBC(1, 5).setWeight(100, 100).setFill(GBC.HORIZONTAL).setInsets(5));
        centPanel.add(obliquAngleUnitLabel, new GBC(2, 5).setWeight(100, 100).setAnchor(GBC.WEST).setInsets(0));

        centPanel.add(arcAngleLabel, new GBC(0, 6).setWeight(100, 100).setAnchor(GBC.EAST).setInsets(5));
        centPanel.add(arcAngletField, new GBC(1, 6).setWeight(100, 100).setFill(GBC.HORIZONTAL).setInsets(5));
        centPanel.add(arcAngleUnitLabel, new GBC(2, 6).setAnchor(GBC.WEST).setWeight(100, 100).setInsets(0));


        centPanel.add(rotation_angleLabel, new GBC(0, 7).setWeight(100, 100).setAnchor(GBC.EAST).setInsets(5));
        centPanel.add(rotation_angleField, new GBC(1, 7).setWeight(100, 100).setFill(GBC.HORIZONTAL).setInsets(5));
        centPanel.add(rotation_angleUnitLabel, new GBC(2, 7).setWeight(100, 100).setAnchor(GBC.WEST).setInsets(0));

        centPanel.add(scanDisLabel, new GBC(0, 8).setWeight(100, 100).setAnchor(GBC.EAST).setInsets(5));
        centPanel.add(scanDisField, new GBC(1, 8).setWeight(100, 100).setFill(GBC.HORIZONTAL).setInsets(5));
        centPanel.add(scanDisUnitLabel, new GBC(2, 8).setWeight(100, 100).setAnchor(GBC.WEST).setInsets(0));

        centPanel.add(rotation_speedLabel, new GBC(0, 9).setWeight(100, 100).setAnchor(GBC.EAST).setInsets(5));
        centPanel.add(rotation_speedField, new GBC(1, 9).setWeight(100, 100).setFill(GBC.HORIZONTAL).setInsets(5));
        centPanel.add(rotation_speedUnitLabel, new GBC(2, 9).setWeight(100, 100).setAnchor(GBC.WEST).setInsets(0));

        centPanel.add(brightLabel, new GBC(0, 10).setWeight(100, 100).setAnchor(GBC.EAST).setInsets(5));
        centPanel.add(bright_Field, new GBC(1, 10).setWeight(100, 100).setFill(GBC.HORIZONTAL).setInsets(5));

        centPanel.add(barcodePre_contentLabel, new GBC(0, 11).setWeight(100, 100).setAnchor(GBC.EAST).setInsets(5));
        centPanel.add(barcodePre_contentJsPanel, new GBC(1, 11).setWeight(100, 100).setFill(GBC.HORIZONTAL).setInsets(5));

        centPanel.add(barcodeDecodeContentLabel, new GBC(0, 12).setWeight(100, 100).setAnchor(GBC.EAST).setInsets(5));
        centPanel.add(barcodeDecodeContentJsPane, new GBC(1, 12).setWeight(100, 100).setFill(GBC.HORIZONTAL).setInsets(5));

        centPanel.add(testResultLabel, new GBC(0, 13).setWeight(100, 100).setAnchor(GBC.EAST).setInsets(5));
        centPanel.add(resultPane, new GBC(1, 13).setWeight(100, 100).setFill(GBC.HORIZONTAL).setInsets(5));

        //moveSettingPanel = new MoveSettingPanel(moveSetPanel);
        //JLabel startMoveLabel = new JLabel("开始移动", JLabel.CENTER);
        //startMoveLabel.setFont(new Font("宋体", Font.BOLD, 24));

        //ImageIcon moveIcon  = new ImageIcon("./PAXCodeTestSoft/data/red.png");
        moveButton = new JButton("开始移动");
        moveButton.setSize(new Dimension(80, 50));
        moveButton.setFont(new Font("宋体", Font.BOLD, 24));
        moveButton.setBackground(new Color(0, 0, 255));

        //JLabel initPosition = new JLabel("初始化",JLabel.CENTER);
        //initPosition.setFont(new Font("宋体", Font.BOLD, 24));

        JButton initButton = new JButton("初 始 化");
        initButton.setBackground(new Color(0, 0, 255));
        initButton.setSize(new Dimension(80, 50));
        initButton.setFont(new Font("宋体", Font.BOLD, 24));

        initButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                SocketToC4 socket = ReadSocketCfg.getSocket(path);

                if (socket != null && socket.openConn()) {
                    String str = "init";
                    if (socket.writeReadOneLine(str).contains("ok")) {
                        socket.closeConn();
                    } else {
                        socket.closeConn();
                        JOptionPane.showMessageDialog(jFrame, "和机器人通信失败，请检查网络！", "条码扫描", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(jFrame, "连接机器人失败，请检查配置！", "条码扫描", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        //JLabel gzPosition = new JLabel("换工装", JLabel.CENTER);
        //gzPosition.setFont(new Font("宋体", Font.BOLD, 24));
        JButton changeGzButton = new JButton("换 工 装");
        changeGzButton.setBackground(new Color(0, 0, 255));
        changeGzButton.setSize(new Dimension(80, 50));
        changeGzButton.setFont(new Font("宋体", Font.BOLD, 24));

        changeGzButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                SocketToC4 socket = ReadSocketCfg.getSocket(path);
                if (socket != null && socket.openConn()) {
                    String str = "changegz";
                    if (socket.writeReadOneLine(str).contains("ok")) {
                        socket.closeConn();
                    } else {
                        socket.closeConn();
                        JOptionPane.showMessageDialog(jFrame, "和机器人通信失败，请检查网络！", "条码扫描", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(jFrame, "连接机器人失败，请检查配置！", "条码扫描", JOptionPane.WARNING_MESSAGE);
                }

            }
        });

        //JLabel generateReportLabel = new JLabel("生成报告",JLabel.CENTER);
        //generateReportLabel.setFont(new Font("宋体", Font.BOLD, 24));

        reportModelButton = new JButton("生成报告");
        reportModelButton.setBackground(new Color(0, 0, 255));
        reportModelButton.setSize(new Dimension(80, 50));
        reportModelButton.setFont(new Font("宋体", Font.BOLD, 24));

        //JLabel exitSysLabel = new JLabel("停止运动" ,JLabel.CENTER);
        //exitSysLabel.setFont(new Font("宋体", Font.BOLD, 24));
        final JButton exitSysButton = new JButton("停止运动");
        exitSysButton.setBackground(new Color(0, 0, 255));
        exitSysButton.setFont(new Font("宋体", Font.BOLD, 24));
        exitSysButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exitSysButton.setEnabled(false);
                StopMove.setCodeRunning(false);
                JOptionPane.showMessageDialog(jFrame,"停止成功，请等待当前运动结束即可,请知悉!", "条码扫描测试",JOptionPane.WARNING_MESSAGE);
                exitSysButton.setEnabled(true);
            }
        });
       // exitSysButton.setEnabled(false);

        //JPanel northPanel = new JPanel(new GridBagLayout());
        //northPanel.setBackground(Color.gray);
        //JPanel centerPanel = new JPanel(new GridBagLayout());
        //centerPanel.setBackground(Color.gray);
        //JPanel southPanel = new JPanel(new GridBagLayout());
        //southPanel.setBackground(Color.gray);
        //moveSetPanel.add(northPanel, BorderLayout.NORTH);
        //moveSetPanel.add(centerPanel, BorderLayout.CENTER);
        //moveSetPanel.add(southPanel, BorderLayout.SOUTH);

        //northPanel.setPreferredSize(new Dimension(SUBWIDTH ,mainFrame.HEIGHT / 3 -100));

        //southPanel.setPreferredSize(new Dimension(SUBWIDTH ,mainFrame.HEIGHT / 3 -100));

        //northPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        //centerPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        //southPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        reportModelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //截断日志文件，并保留备份
				/*try {
					
					File file = new File(System.getProperty("user.dir")+"/info.log");
					FileReader fileReader = new FileReader(file);
					BufferedReader bufferedReader = new BufferedReader(fileReader);
					BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(System.getProperty("user.dir")+"/"
							+report.getCurrentDateString()+".log")));
					String string = null;
					while ((string=bufferedReader.readLine())!= null) {
						bufferedWriter.write(string);
						bufferedWriter.newLine();
						bufferedWriter.flush();
						
					}
				
					FileWriter fileWriter = new FileWriter(file);
					fileWriter.write("");
					fileWriter.close();
					bufferedReader.close();
					bufferedWriter.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}*/
                reportModelButton.setEnabled(false);

                if(allList.size() < 1) {
                    JOptionPane.showMessageDialog(jFrame, "没有有效的测试数据，请执行测试后，再点击测试报告！","生成报告", JOptionPane.WARNING_MESSAGE);
                    reportModelButton.setEnabled(true);
                    return;
                }

                if(PosVerField.getText().trim().length() < 1) {
                    JOptionPane.showMessageDialog(jFrame, "产品型号为空，不能产生测试报告！","生成报告", JOptionPane.WARNING_MESSAGE);
                    reportModelButton.setEnabled(true);
                    return;
                }

                File saveDir = new File("./测试报告");
                if(!saveDir.exists()) {
                    saveDir.mkdir();
                }

                TestInfo testInfo = new TestInfo(testerField.getText(), testLocField.getText(), testEnvField.getText(), PosVerField.getText(), HWVerField.getText(),
                        SWVerField.getText(), DUTIdField.getText(), remarkField.getText());
                JFileChooser fc=new JFileChooser();
                fc.setCurrentDirectory(saveDir);
                //fc.setFileFilter(filter);
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fc.setMultiSelectionEnabled(false);
                int result=fc.showSaveDialog(jFrame);
                if (result==JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    if (!file.isDirectory()) {
                        reportModelButton.setEnabled(true);
                        JOptionPane.showMessageDialog(jFrame, "选择的文件不是目录，请选择保存目录", "生成报告", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    //String reportStr = "条码扫描报告_" + PosVerField.getText() + "_" + scanTypeBox.getSelectedItem().toString() + "_" + formatDate(new Date()) + ".doc";
                    String reportStr = PosVerField.getText() + "_" + DUTIdField.getText().trim() + "_条码扫描报告_" +  scanTypeBox.getSelectedItem().toString() + "_" + formatDate(new Date()) + ".doc";
                    String codeReportString = file.getAbsolutePath() + File.separator + reportStr;
                    try {
                        if (FreemakerTest.reportGenerate(codeReportString, testInfo, allList)) {
                            Desktop myDesktop = Desktop.getDesktop();
                            myDesktop.open(new File(codeReportString));
                            allList.clear();
                        } else {
                            JOptionPane.showMessageDialog(jFrame, "生成报告失败，请检查！", "生成报告", JOptionPane.WARNING_MESSAGE);
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                        JOptionPane.showMessageDialog(jFrame, "自动打开Word失败，请手动打开！", "打开报告", JOptionPane.WARNING_MESSAGE);
                    }
                    reportModelButton.setEnabled(true);
                }
                else {
                    reportModelButton.setEnabled(true);
                    JOptionPane.showMessageDialog(jFrame, "放弃生成测试报告，请知悉！", "生成报告", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
        });


        moveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                moveButton.setEnabled(false);
                if(PosVerField.getText().trim().length() < 1) {
                    JOptionPane.showMessageDialog(jFrame, "产品型号为空，不能进行测试！","条码扫描", JOptionPane.WARNING_MESSAGE);
                    moveButton.setEnabled(true);
                    return;
                }
                if(firstTimes) {
                    initResultArea();
                    firstTimes = false;
                }

                mainFrame.logger.info("开机进行条码扫描测试， 被测产品: " + PosVerField.getText().trim() + " 扫描类型: " + scanTypeBox.getSelectedItem());

                String portName = (String) DUTSerialPortBox.getSelectedItem();
                parameters = new SerialParameters();
                parameters.setPortName(portName);
                final SerialConnection connection = new SerialConnection(parameters);
                cardNumField.setText("");
                barcodePre_contentArea.setText("");
                currentTestItemField.setText("");
                barcodeNameField.setText("");
                excutingField.setText("");
                scanDisField.setText("");
                barcodeDecodeContentArea.setText("");

                StopMove.setCodeRunning(true);
               // updateResultArea(toolSelectionBox.getSelectedItem().toString());
                if ("取卡工装".equals(toolSelectionBox.getSelectedItem())) {
                    //initDis();
                    UtilsTool.updateArea(BarcodePanel.testResultArea, "不同介质卡片", "N/A");
                    takeCardMove(connection);
                } else if ("手机工装".equals((String) toolSelectionBox.getSelectedItem())) {
                    new Thread(new Runnable() {
                        public void run() {

                            //设置亮度值为最大
                            BrightMoveWork.setBrightValue((byte)255);

                            List<Integer> moveList = new ArrayList<Integer>();
                            boolean isTwoWei = "二维".equals((String) scanTypeBox.getSelectedItem());
                            String  str = "一维";
                            if(isTwoWei) { str = "二维"; }

                            //扫描景深
                            if (scanDepthChbx.isSelected()) {
                                //moveButton.setEnabled(false);
                                UtilsTool.updateArea(BarcodePanel.testResultArea, "扫描景深", "N/A");
                                PayDepthMoveWork payDepthMoveWork = new PayDepthMoveWork(connection);
                                //PayDepthMoveWork payDepthMoveWork = new PayDepthMoveWork(jFrame, connection);
                                if (!payDepthMoveWork.depthWorkFlowingOne(isTwoWei, str)) {
                                    moveButton.setEnabled(true);
                                    if(!StopMove.isCodeRunning()) {
                                        JOptionPane.showMessageDialog(jFrame, "用户停止测试，请知悉！", "扫描景深", JOptionPane.WARNING_MESSAGE);
                                    }
                                    else {
                                        JOptionPane.showMessageDialog(jFrame, "扫描景深" + str + "测试过程中出现异常，请检查！", "扫描景深", JOptionPane.WARNING_MESSAGE);
                                    }
                                    return;
                                }
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                            //扫描角度
                            if (scanAngleChbx.isSelected()) {
                                UtilsTool.updateArea(BarcodePanel.testResultArea, "扫描角度", "N/A");
                                ScanAngelMoveWork payAngelMoveWork = new ScanAngelMoveWork(connection);
                                if (!payAngelMoveWork.angelWorkFlowingTwo(isTwoWei, str)) {
                                    moveButton.setEnabled(true);
                                    if(!StopMove.isCodeRunning()) {
                                        JOptionPane.showMessageDialog(jFrame, "用户停止测试，请知悉！", "扫描角度", JOptionPane.WARNING_MESSAGE);
                                    }
                                    else {
                                        JOptionPane.showMessageDialog(jFrame, "扫描角度" + str + "测试过程中出现异常，请检查！", "扫描角度", JOptionPane.WARNING_MESSAGE);
                                    }
                                    return;
                                }
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                            //扫描速度
                            if (move_rateChbox.isSelected()) {
                                UtilsTool.updateArea(BarcodePanel.testResultArea, "扫描速度", "N/A");
                                MoveRateMoveWork moveRateMoveWork = new MoveRateMoveWork(connection);
                                if (!moveRateMoveWork.moverateWorkFlowingTwo(isTwoWei, str)) {
                                    moveButton.setEnabled(true);
                                    if(!StopMove.isCodeRunning()) {
                                        JOptionPane.showMessageDialog(jFrame, "用户停止测试，请知悉！", "扫描速度", JOptionPane.WARNING_MESSAGE);
                                    }
                                    else {
                                        JOptionPane.showMessageDialog(jFrame, "扫描速度" + str + "测试过程中出现异常，请检查！", "扫描速度", JOptionPane.WARNING_MESSAGE);
                                    }
                                    return;
                                }
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                            //360旋转角度
                            if (isTwoWei && rotation_angleChbox.isSelected()) {
                                UtilsTool.updateArea(BarcodePanel.testResultArea, "360旋转角度", "N/A");
                                RotateMoveWork rotateMoveWork = new RotateMoveWork(connection);
                                if (!rotateMoveWork.rotateWorkFlowingTwo()) {
                                    moveButton.setEnabled(true);
                                    if(!StopMove.isCodeRunning()) {
                                        JOptionPane.showMessageDialog(jFrame, "用户停止测试，请知悉！", "360旋转角度", JOptionPane.WARNING_MESSAGE);
                                    }
                                    else {
                                        JOptionPane.showMessageDialog(jFrame, "360旋转角度二维测试过程中出现异常，请检查！", "360旋转角度", JOptionPane.WARNING_MESSAGE);
                                    }
                                    return;
                                }
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                            moveList.clear();
                            String labelText= "";
                            //码制类型
                            if (codeTypeChbx.isSelected()) {
                                UtilsTool.updateArea(BarcodePanel.testResultArea, "码制类型", "N/A");
                                labelText = codeTypeLabel.getText();
                                if (ReadBarcodeInfo.getBrInfoMap().get(labelText + "一维") == null) {
                                    JOptionPane.showMessageDialog(jFrame, "码制类型的记录不合法，请检查配置文件", "码制类型", JOptionPane.WARNING_MESSAGE);
                                    moveButton.setEnabled(true);
                                    return;
                                }
                                moveList.addAll(ReadBarcodeInfo.getBrInfoMap().get(labelText + "一维"));
                                if (isTwoWei)
                                    moveList.addAll(ReadBarcodeInfo.getBrInfoMap().get(labelText + "二维"));
                                //System.out.println(moveList);
                            }
                            //对比度
                            if (contrastChbx.isSelected()) {
                                UtilsTool.updateArea(BarcodePanel.testResultArea, "对比度", "N/A");
                                labelText = contrastLabel.getText();
                                if (ReadBarcodeInfo.getBrInfoMap().get(labelText + "一维") == null) {
                                    JOptionPane.showMessageDialog(jFrame, "对比度的记录不合法，请检查配置文件", "对比度", JOptionPane.WARNING_MESSAGE);
                                    moveButton.setEnabled(true);
                                    return;
                                }
                                moveList.addAll(ReadBarcodeInfo.getBrInfoMap().get(labelText + "一维"));
                                if (isTwoWei)
                                    moveList.addAll(ReadBarcodeInfo.getBrInfoMap().get(labelText + "二维"));
                            }
                            //彩色条码
                            if (colorCodeChbx.isSelected()) {
                                UtilsTool.updateArea(BarcodePanel.testResultArea, "彩色条码", "N/A");
                                labelText = colorCodeLabel.getText();
                                if (ReadBarcodeInfo.getBrInfoMap().get(labelText + "一维") == null) {
                                    JOptionPane.showMessageDialog(jFrame, "彩色条码的记录不合法，请检查配置文件", "彩色条码", JOptionPane.WARNING_MESSAGE);
                                    moveButton.setEnabled(true);
                                    return;
                                }
                                moveList.addAll(ReadBarcodeInfo.getBrInfoMap().get(labelText + "一维"));
                                if (isTwoWei)
                                    moveList.addAll(ReadBarcodeInfo.getBrInfoMap().get(labelText + "二维"));
                            }
                            //污损案例
                            if (stainedCaseChbx.isSelected()) {
                                UtilsTool.updateArea(BarcodePanel.testResultArea, "污损案例", "N/A");
                                labelText = stainedCaseLabel.getText();
                                if (ReadBarcodeInfo.getBrInfoMap().get(labelText + "一维") == null) {
                                    JOptionPane.showMessageDialog(jFrame, "污损案例的记录不合法，请检查配置文件", "污损案例", JOptionPane.WARNING_MESSAGE);
                                    moveButton.setEnabled(true);
                                    return;
                                }
                                moveList.addAll(ReadBarcodeInfo.getBrInfoMap().get(labelText + "一维"));
                                if (isTwoWei)
                                    moveList.addAll(ReadBarcodeInfo.getBrInfoMap().get(labelText + "二维"));
                            }

                            if (moveList.size() > 0) {
                                PhotoDisplayMoveWork photoDisplayMoveWork = new PhotoDisplayMoveWork(moveList, connection, jFrame);
                                if (!photoDisplayMoveWork.runWork(isTwoWei, str, false)) {
                                    moveButton.setEnabled(true);
                                    if(!StopMove.isCodeRunning()) {
                                        JOptionPane.showMessageDialog(jFrame, "用户停止测试，请知悉！", "条码扫描测试", JOptionPane.WARNING_MESSAGE);
                                    }
                                    else {
                                        JOptionPane.showMessageDialog(jFrame, "条码扫描测试过程中出现异常，请检查！", "条码扫描测试", JOptionPane.WARNING_MESSAGE);
                                    }
                                    return;
                                }
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
//                            else {
//                                JOptionPane.showMessageDialog(jFrame, "请选择待测试的项目！", "条码扫描测试", JOptionPane.WARNING_MESSAGE);
//                                moveButton.setEnabled(true);
//                            }

                            moveList.clear();
                            //大数据条码
                            if(bigDataChbx.isSelected()) {
                                UtilsTool.updateArea(BarcodePanel.testResultArea, "大数据条码", "N/A");
                                labelText = bigDataLabel.getText();
                                if (ReadBarcodeInfo.getBrInfoMap().get(labelText + "一维") == null) {
                                    JOptionPane.showMessageDialog(jFrame, "大数据条码的记录不合法，请检查配置文件", "大数据条码", JOptionPane.WARNING_MESSAGE);
                                    moveButton.setEnabled(true);
                                    return;
                                }
                                if (isTwoWei)
                                    moveList.addAll(ReadBarcodeInfo.getBrInfoMap().get(labelText + "二维"));
                                else
                                    moveList.addAll(ReadBarcodeInfo.getBrInfoMap().get(labelText + "一维"));
                            }
                            //不同字符解码
                            if(diffCharChbx.isSelected()) {
                                UtilsTool.updateArea(BarcodePanel.testResultArea, "不同字符解码", "N/A");
                                labelText = diffCharLabel.getText();
                                if (ReadBarcodeInfo.getBrInfoMap().get(labelText + "一维") == null) {
                                    JOptionPane.showMessageDialog(jFrame, "不同字符解码的记录不合法，请检查配置文件", "不同字符解码", JOptionPane.WARNING_MESSAGE);
                                    moveButton.setEnabled(true);
                                    return;
                                }

                                if (isTwoWei)
                                    moveList.addAll(ReadBarcodeInfo.getBrInfoMap().get(labelText + "二维"));
                                else
                                    moveList.addAll(ReadBarcodeInfo.getBrInfoMap().get(labelText + "一维"));
                            }
                            //胖瘦畸变
                            if(fatThinChbx.isSelected()) {
                                UtilsTool.updateArea(BarcodePanel.testResultArea, "胖瘦畸变", "N/A");
                                labelText = fatThinLabel.getText();
                                if (ReadBarcodeInfo.getBrInfoMap().get(labelText + "一维") == null) {
                                    JOptionPane.showMessageDialog(jFrame, "胖瘦畸变的记录不合法，请检查配置文件", "胖瘦畸变", JOptionPane.WARNING_MESSAGE);
                                    moveButton.setEnabled(true);
                                    return;
                                }

                                if (isTwoWei)
                                    moveList.addAll(ReadBarcodeInfo.getBrInfoMap().get(labelText + "二维"));
                                else
                                    moveList.addAll(ReadBarcodeInfo.getBrInfoMap().get(labelText + "一维"));
                            }
                            //镜像码
                            if(mirrorChbx.isSelected()) {
                                UtilsTool.updateArea(BarcodePanel.testResultArea, "镜像码", "N/A");
                                labelText = mirrorLabel.getText();
                                if (ReadBarcodeInfo.getBrInfoMap().get(labelText + "一维") == null) {
                                    JOptionPane.showMessageDialog(jFrame, "镜像码的记录不合法，请检查配置文件", "镜像码", JOptionPane.WARNING_MESSAGE);
                                    moveButton.setEnabled(true);
                                    return;
                                }
                                if (isTwoWei)
                                    moveList.addAll(ReadBarcodeInfo.getBrInfoMap().get(labelText + "二维"));
                                else
                                    moveList.addAll(ReadBarcodeInfo.getBrInfoMap().get(labelText + "一维"));
                            }
                            //曲面码
                            if(curveChbx.isSelected()) {
                                UtilsTool.updateArea(BarcodePanel.testResultArea, "曲面码", "N/A");
                                labelText = curveLabel.getText();
                                if (ReadBarcodeInfo.getBrInfoMap().get(labelText + "一维") == null) {
                                    JOptionPane.showMessageDialog(jFrame, "曲面码的记录不合法，请检查配置文件", "曲面码", JOptionPane.WARNING_MESSAGE);
                                    moveButton.setEnabled(true);
                                    return;
                                }

                                if (isTwoWei)
                                    moveList.addAll(ReadBarcodeInfo.getBrInfoMap().get(labelText + "二维"));
                                else
                                    moveList.addAll(ReadBarcodeInfo.getBrInfoMap().get(labelText + "一维"));
                            }
                            //渐变码
                            if(gradientChbx.isSelected()) {
                                UtilsTool.updateArea(BarcodePanel.testResultArea, "渐变码", "N/A");
                                labelText = gradientLabel.getText();
                                if (ReadBarcodeInfo.getBrInfoMap().get(labelText + "一维") == null) {
                                    JOptionPane.showMessageDialog(jFrame, "渐变码的记录不合法，请检查配置文件", "渐变码", JOptionPane.WARNING_MESSAGE);
                                    moveButton.setEnabled(true);
                                    return;
                                }

                                if (isTwoWei)
                                    moveList.addAll(ReadBarcodeInfo.getBrInfoMap().get(labelText + "二维"));
                                else
                                    moveList.addAll(ReadBarcodeInfo.getBrInfoMap().get(labelText + "一维"));
                            }

                            //识别精度
                            if(accuracyChbx.isSelected()) {
                                UtilsTool.updateArea(BarcodePanel.testResultArea, "识别精度", "N/A");
                                labelText = accuracyLabel.getText();
                                if (ReadBarcodeInfo.getBrInfoMap().get(labelText + "一维") == null) {
                                    JOptionPane.showMessageDialog(jFrame, "识别精度的记录不合法，请检查配置文件", "识别精度", JOptionPane.WARNING_MESSAGE);
                                    moveButton.setEnabled(true);
                                    return;
                                }

                                if (isTwoWei)
                                    moveList.addAll(ReadBarcodeInfo.getBrInfoMap().get(labelText + "二维"));
                                else
                                    moveList.addAll(ReadBarcodeInfo.getBrInfoMap().get(labelText + "一维"));
                            }

                            if (moveList.size() > 0) {
                                PhotoDisplayMoveWork photoDisplayMoveWork = new PhotoDisplayMoveWork(moveList, connection, jFrame);
                                if (!photoDisplayMoveWork.runWork(isTwoWei, str, true)) {
                                    moveButton.setEnabled(true);
                                    if(!StopMove.isCodeRunning()) {
                                        JOptionPane.showMessageDialog(jFrame, "用户停止测试，请知悉！", "条码扫描测试", JOptionPane.WARNING_MESSAGE);
                                    }
                                    else {
                                        JOptionPane.showMessageDialog(jFrame, "条码扫描测试过程中出现异常，请检查！", "条码扫描测试", JOptionPane.WARNING_MESSAGE);
                                    }
                                    return;
                                }
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                            //手机背光亮度
                            if (brightChbx.isSelected()) {
                                UtilsTool.updateArea(BarcodePanel.testResultArea, "手机背光亮度", "N/A");
                                BrightMoveWork brightMoveWork = new BrightMoveWork(connection);
                                if (!brightMoveWork.brightWorkTest(isTwoWei, str)) {
                                    moveButton.setEnabled(true);
                                    if(!StopMove.isCodeRunning()) {
                                        JOptionPane.showMessageDialog(jFrame, "用户停止测试，请知悉！", "手机背光亮度", JOptionPane.WARNING_MESSAGE);
                                    }
                                    else {
                                        JOptionPane.showMessageDialog(jFrame, "手机背光亮度" + str + "测试过程中出现异常，请检查！", "手机背光亮度", JOptionPane.WARNING_MESSAGE);
                                    }
                                    return;
                                }
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            JOptionPane.showMessageDialog(jFrame, "手机工装测试完毕，请更换工装继续测试或生成报告！", "条码扫描测试", JOptionPane.WARNING_MESSAGE);
                            moveButton.setEnabled(true);
                        }
                    }).start();
                }
            }
        });

        JButton updateButton = new JButton("刷新串口");
        updateButton.setSize(new Dimension(80, 50));
        updateButton.setFont(new Font("宋体", Font.BOLD, 24));
        updateButton.setBackground(new Color(0, 0, 255));
        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DUTSerialPortBox.removeAllItems();
                List<String> comlist = SerialConnection.getCommInfo();
                if (comlist != null) {
                    for (Iterator<String> iterator = comlist.iterator(); iterator.hasNext(); ) {
                        DUTSerialPortBox.addItem(iterator.next());
                    }
                }
            }
        });
        //northPanel.add(startMoveLabel, new GBC(0, 0).setWeight(100, 100).setAnchor(GBC.CENTER).setInsets(5) );
        moveSetPanel.add(moveButton, new GBC(0, 0).setWeight(100, 100).setInsets(5));

        moveSetPanel.add(updateButton, new GBC(0, 1).setWeight(100, 100).setInsets(5));
        //centerPanel.add(initPosition, new GBC(0, 0).setWeight(100, 100).setAnchor(GBC.CENTER).setInsets(5) );
        moveSetPanel.add(initButton, new GBC(0, 2).setWeight(100, 100).setInsets(5));

        //centerPanel.add(gzPosition, new GBC(0, 2).setWeight(100, 100).setAnchor(GBC.CENTER).setInsets(5) );
        moveSetPanel.add(changeGzButton, new GBC(0, 3).setWeight(100, 100).setInsets(5));

        //centerPanel.add(generateReportLabel, new GBC(0, 4).setWeight(100, 100).setAnchor(GBC.CENTER).setInsets(5) );
        moveSetPanel.add(reportModelButton, new GBC(0, 4).setWeight(100, 100).setInsets(5));

        //southPanel.add(exitSysLabel, new GBC(0, 0).setWeight(100, 100).setAnchor(GBC.CENTER).setInsets(5) );
        moveSetPanel.add(exitSysButton, new GBC(0, 5).setWeight(100, 100).setInsets(5));

        codeInfoPanel.add(barcodeInfoLabel, BorderLayout.NORTH);
        codeInfoPanel.add(centPanel, BorderLayout.CENTER);
        codeInfoAndMoveJPanel.add(codeInfoPanel, BorderLayout.WEST);
        codeInfoAndMoveJPanel.add(moveSetPanel, BorderLayout.CENTER);
    }

    public void initResultArea() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                UtilsTool.updateView(BarcodePanel.testResultArea, "扫描景深：N/A\n" +
                        "扫描角度：N/A\n扫描速度：N/A\n360旋转角度：N/A\n码制类型：N/A\n" +
                        "对比度：N/A\n彩色条码：N/A\n污损案例：N/A\n大数据条码：N/A\n" +
                        "不同字符解码：N/A\n胖瘦畸变：N/A\n镜像码：N/A\n" +
                        "曲面码：N/A\n渐变码：N/A\n识别精度：N/A\n" +
                        "手机背光亮度：N/A\n不同介质卡片：N/A\n");
            }
        });
    }

//    public void updateResultArea(String gz) {
//        if(gz.equalsIgnoreCase("手机工装")) {
//            String[] sj = {"扫描景深", "扫描角度", "扫描速度", "360旋转角度", "码制类型", "对比度", "彩色条码",
//                    "污损案例", "大数据条码", "不同字符解码", "胖瘦畸变", "镜像码", "曲面码", "渐变码",
//                    "识别精度", "手机背光亮度"};
//            for(int i=0; i<sj.length; i++) {
//                UtilsTool.updateArea(BarcodePanel.testResultArea, sj[i], "N/A");
//            }
//        } else {
//            UtilsTool.updateArea(BarcodePanel.testResultArea, "不同介质卡片", "N/A");
//        }
//    }

    private void takeCardMove(SerialConnection connection) {
        List<Integer> moveList = new ArrayList<Integer>();
        String s1 = (String) scanTypeBox.getSelectedItem();

        //if ("一维".equals(s1)) {
        if (dif_mediaCardChbx.isSelected()) {
            List<Integer> cardlist = ReadCardInfo.getBrInfoMap().get(dif_mediaCardLabel.getText() + s1);
            if (cardlist == null) {
                JOptionPane.showMessageDialog(jFrame, "不同介质卡片配置非法，请检查配置文件!", "不同介质卡片", JOptionPane.WARNING_MESSAGE);
                moveButton.setEnabled(true);
                return;
            }

//            if ("二维".equals(s1)) {
//                moveList.addAll(ReadCardInfo.getBrInfoMap().get(dif_mediaCardLabel.getText() + "一维"));
//            }
            moveList.addAll(cardlist);

            newThread = null;
            if (moveList.size() > 0) {
                PickCardMoveWork moveWork = new PickCardMoveWork(moveList, connection, jFrame);
                newThread = new Thread(moveWork);
                newThread.start();
            } else {
                JOptionPane.showMessageDialog(jFrame, "没有可以测试的项，程序配置有问题，请检查！", "不同介质卡片", JOptionPane.WARNING_MESSAGE);
                moveButton.setEnabled(true);
            }
        } else {
            JOptionPane.showMessageDialog(jFrame, "没有可以测试的项，请选中要测试的项目！", "不同介质卡片", JOptionPane.WARNING_MESSAGE);
            moveButton.setEnabled(true);
            return;
        }
//		}else {
//			if (dif_mediaCardChbx.isSelected()) {
//				List<Integer> cardlist = ReadCardInfo.getBrInfoMap().get(dif_mediaCardLabel.getText() + s1);
//				if (cardlist == null) {
//					JOptionPane.showMessageDialog(jFrame,"不同介质卡片配置非法，请检查配置文件", "不同介质",JOptionPane.WARNING_MESSAGE);
//					//moveButton.setEnabled(true);
//					return;
//				}
//				newThread = null;
//				PickCardMoveWork moveWork = new PickCardMoveWork(connection, jFrame);
//				newThread = new Thread(moveWork);
//				newThread.start();
//			}
//			else {
//				JOptionPane.showMessageDialog(jFrame,"没有可以测试的项，请选中要测试的项目！", "不同介质",JOptionPane.WARNING_MESSAGE);
//				//moveButton.setEnabled(true);
//				return;
//			}
//		}	
    }

}

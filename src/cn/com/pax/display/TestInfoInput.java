package cn.com.pax.display;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;

public class TestInfoInput extends JDialog{
	private static final long serialVersionUID = 980634448588715271L;
	private JPanel parentPanel;
	private JTextField productField1,snField1,testerField1,posField1;
	
    public TestInfoInput(final JFrame jFrame, JPanel panel, final String pString){
    	//super(jFrame, true);
    	parentPanel = panel;
    	setTitle("测试信息输入");
    	
    	try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
    	
    	setResizable(false);
    	setSize(450,450);
    	setLayout(new BorderLayout(20, 20));
    	
    	JLabel inputInfo = new JLabel("测试信息输入");
    	inputInfo.setFont(new Font("宋体",Font.BOLD, 18 ));
		JLabel productLabel = new JLabel("产品型号");
		productLabel.setFont(new Font("宋体",Font.BOLD, 18 ));
		productField1 = new JTextField(50);
		
		JLabel snLabel = new JLabel("S/N序列号");
		snLabel.setFont(new Font("宋体",Font.BOLD, 18 ));
		snField1 = new JTextField(50);
		
		JLabel testerLabel = new JLabel("测试人员");
		testerLabel.setFont(new Font("宋体",Font.BOLD, 18 ));
		testerField1 = new JTextField(50);
		
		JLabel posLabel = new JLabel("POS制造商");
		posLabel.setFont(new Font("宋体",Font.BOLD, 18 ));
		posField1 = new JTextField(50); 

		JButton inputOver = new JButton("输入完成");
		inputOver.setFont(new Font("宋体",Font.BOLD, 18 ));
		inputOver.setSize(20, 20);
		
		inputOver.addActionListener( new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				if(productField1.getText().trim().length() < 1) {
					JOptionPane.showMessageDialog(jFrame,"产品型号为空，请输入正确的数值！", "测试信息输入",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				if ("picc".equalsIgnoreCase(pString)) {
					((PiccPanel)parentPanel).testerField.setText(testerField1.getText());
					((PiccPanel)parentPanel).snField.setText(snField1.getText());
					((PiccPanel)parentPanel).productField.setText(productField1.getText());
					((PiccPanel)parentPanel).posField.setText(posField1.getText());
					
					CenterPositon centerPositon = new CenterPositon(jFrame);
					centerPositon.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
					centerPositon.setVisible(true);
					TestInfoInput.this.setVisible(false);
				}else{
					
					((ChangqPanel)parentPanel).testerField.setText(testerField1.getText());
					((ChangqPanel)parentPanel).snField.setText(snField1.getText());
					((ChangqPanel)parentPanel).productField.setText(productField1.getText());
					((ChangqPanel)parentPanel).posField.setText(posField1.getText());
					CenterPositon centerPositon = new CenterPositon(jFrame);
					centerPositon.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
					centerPositon.setVisible(true);
					TestInfoInput.this.setVisible(false);
				}
			/*	CenterPositon centerPositon = new CenterPositon(TestInfoInput.this);
				centerPositon.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				centerPositon.setVisible(true);
				TestInfoInput.this.setVisible(false);*/
				/*CenterPositon centerPositon = new CenterPositon(TestInfoInput.this);
				centerPositon.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				centerPositon.setVisible(true);
				TestInfoInput.this.setVisible(false);*/
			}
		});
		JPanel centerJPanel = new JPanel(new GridBagLayout());
	
		centerJPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		JPanel southJPanel = new JPanel(new FlowLayout());
		
		centerJPanel.add(inputInfo, new GBC(0, 0).setInsets(5).setWeight(100, 100).setAnchor(GBC.WEST));
		centerJPanel.add(testerLabel, new GBC(0, 1).setInsets(5, 25, 5, 5).setWeight(100, 100).setAnchor(GBC.WEST));
		centerJPanel.add(testerField1, new GBC(0, 2).setInsets(5, 25, 5, 5).setWeight(100, 100).setAnchor(GBC.WEST).setFill(GBC.CENTER));

		centerJPanel.add(productLabel, new GBC(0, 3).setInsets(5, 25, 5, 5).setWeight(100, 100).setAnchor(GBC.WEST));
		centerJPanel.add(productField1, new GBC(0, 4).setInsets(5, 25, 5, 5).setWeight(100, 100).setAnchor(GBC.WEST).setFill(GBC.CENTER));

		centerJPanel.add(snLabel, new GBC(0, 5).setInsets(5, 25, 5, 5).setWeight(100, 100).setAnchor(GBC.WEST));
		centerJPanel.add(snField1, new GBC(0, 6).setInsets(5, 25, 5, 5).setWeight(100, 100).setAnchor(GBC.WEST).setFill(GBC.CENTER));
		
		centerJPanel.add(posLabel, new GBC(0, 7).setInsets(5, 25, 5, 5).setWeight(100, 100).setAnchor(GBC.WEST));
		centerJPanel.add(posField1, new GBC(0, 8).setInsets(5, 25, 5, 5).setWeight(100, 100).setAnchor(GBC.WEST).setFill(GBC.CENTER));
		southJPanel.add(inputOver);
		add(centerJPanel, BorderLayout.CENTER);
		add(southJPanel, BorderLayout.SOUTH);
		setLocation(jFrame.getLocation().x + (jFrame.getWidth() - getWidth()) / 2, jFrame.getLocation().y);
		
    }
	
    public static void main(String[] args) {
    	
    	//TestInfoInput  testInfoInput  = new TestInfoInput(new JFrame());
    	//testInfoInput.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    	//testInfoInput.setVisible(true);
	}
	

}

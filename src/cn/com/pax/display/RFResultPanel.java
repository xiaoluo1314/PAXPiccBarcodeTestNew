package cn.com.pax.display;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;


public class RFResultPanel extends JPanel {
	private static final long serialVersionUID = -7319832862889668426L;
	private List<Point2D> point25;
	private List<Integer> result25;
	private boolean pointInit;
	
	public RFResultPanel() {
		point25 = new ArrayList<Point2D>();
		result25 = new ArrayList<Integer>();
		for(int i=0; i<25; i++) {
			result25.add(0);
		}
		pointInit = false;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		String []strs ={"000","010","013","016","019",
						"100","120","123","126","129",
						"200","220","223","226","229",
						"300","320","323","326","329",
						"400","410","413","416","419"};
		Graphics2D g2 = (Graphics2D) g;
		
		int orginX = getWidth() / 2 ;
		int orginY = getHeight() / 2  + 220;
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		drawAL(orginX, orginY, orginX, orginY - 500, g2);
		drawAL(orginX, orginY, orginX + 300, orginY, g2);
		int smallOvalWidth = 300;
		int smallOvalHeight = 50;
		int largeOvalWidth = 500;
		int largeOvalHeight = 100;
		int cWidth = 30;
		int cHeight = 10;
		
		g2.drawOval(orginX - smallOvalWidth/2, orginY - smallOvalHeight/2, smallOvalWidth, smallOvalHeight);
		g2.drawLine(orginX - smallOvalWidth/2, orginY, orginX + smallOvalWidth/2, orginY);
		g2.drawLine(orginX - smallOvalHeight/2, orginY + smallOvalHeight/2, orginX + smallOvalHeight/2, orginY - smallOvalHeight/2);
		
		g2.drawOval(orginX - largeOvalWidth/2, orginY - largeOvalHeight/2 - 90, largeOvalWidth, largeOvalHeight);
		g2.drawLine(orginX - largeOvalWidth/2, orginY - 90, orginX + largeOvalWidth/2, orginY - 90);
		g2.drawLine(orginX - largeOvalHeight/2, orginY + largeOvalHeight/2 - 90, orginX + largeOvalHeight/2, orginY - largeOvalHeight/2 - 90);
	
		g2.drawOval(orginX - largeOvalWidth/2, orginY - largeOvalHeight/2 - 200, largeOvalWidth, largeOvalHeight);
		g2.drawLine(orginX - largeOvalWidth/2, orginY - 200, orginX + largeOvalWidth/2, orginY - 200);
		g2.drawLine(orginX - largeOvalHeight/2, orginY + largeOvalHeight/2 - 200, orginX + largeOvalHeight/2, orginY - largeOvalHeight/2 - 200);
		
		g2.drawOval(orginX - largeOvalWidth/2, orginY - largeOvalHeight/2 - 310, largeOvalWidth, largeOvalHeight);
		g2.drawLine(orginX - largeOvalWidth/2, orginY - 310, orginX + largeOvalWidth/2, orginY - 310);
		g2.drawLine(orginX - largeOvalHeight/2, orginY + largeOvalHeight/2 - 310, orginX + largeOvalHeight/2, orginY - largeOvalHeight/2 - 310);
		
		g2.drawOval(orginX - smallOvalWidth/2, orginY - smallOvalHeight/2 - 400, smallOvalWidth, smallOvalHeight);
		g2.drawLine(orginX - smallOvalWidth/2, orginY - 400, orginX + smallOvalWidth/2, orginY - 400);
		g2.drawLine(orginX - smallOvalHeight/2, orginY + smallOvalHeight/2-400, orginX + smallOvalHeight/2, orginY - smallOvalHeight/2-400);
	
		if(!pointInit) {
			point25.add(new Point2D.Double(orginX, orginY));
			point25.add(new Point2D.Double(orginX + smallOvalWidth/2, orginY));
			point25.add(new Point2D.Double(orginX + smallOvalHeight/2, orginY - smallOvalHeight/2));
			point25.add(new Point2D.Double(orginX - smallOvalWidth/2, orginY));
			point25.add(new Point2D.Double(orginX - smallOvalHeight/2, orginY + smallOvalHeight/2));
			
			int[] c123 = {90,200,310};
			for(int i=0; i<c123.length; i++) {
				point25.add(new Point2D.Double(orginX, orginY-c123[i]));
				point25.add(new Point2D.Double(orginX + largeOvalWidth/2, orginY-c123[i]));
				point25.add(new Point2D.Double(orginX + largeOvalHeight/2, orginY - largeOvalHeight/2-c123[i]));
				point25.add(new Point2D.Double(orginX - largeOvalWidth/2, orginY-c123[i]));
				point25.add(new Point2D.Double(orginX - largeOvalHeight/2, orginY + largeOvalHeight/2-c123[i]));
			}
			
			point25.add(new Point2D.Double(orginX, orginY-400));
			point25.add(new Point2D.Double(orginX + smallOvalWidth/2, orginY-400));
			point25.add(new Point2D.Double(orginX + smallOvalHeight/2, orginY - smallOvalHeight/2-400));
			point25.add(new Point2D.Double(orginX - smallOvalWidth/2, orginY-400));
			point25.add(new Point2D.Double(orginX - smallOvalHeight/2, orginY + smallOvalHeight/2-400));
			
			pointInit = true;
		}
		
		g2.drawLine((int)point25.get(1).getX(), (int)point25.get(1).getY(), (int)point25.get(6).getX(),(int)point25.get(6).getY());
		g2.drawLine((int)point25.get(6).getX(), (int)point25.get(6).getY(), (int)point25.get(16).getX(),(int)point25.get(16).getY());
		g2.drawLine((int)point25.get(16).getX(), (int)point25.get(16).getY(), (int)point25.get(21).getX(),(int)point25.get(21).getY());
		g2.drawLine((int)point25.get(3).getX(), (int)point25.get(3).getY(), (int)point25.get(8).getX(),(int)point25.get(8).getY());
		g2.drawLine((int)point25.get(8).getX(), (int)point25.get(8).getY(), (int)point25.get(18).getX(),(int)point25.get(18).getY());
		g2.drawLine((int)point25.get(18).getX(), (int)point25.get(18).getY(), (int)point25.get(23).getX(),(int)point25.get(23).getY());
		Paint paint = g2.getPaint();
		
		Font sansbold14 = new Font("SansSerif", Font.BOLD, 14);
		g2.setFont(sansbold14);
		for(int i=0; i<result25.size(); i++) {
			int r1 = result25.get(i);
			if(r1 == -1) {
				g2.setPaint(Color.red);
			}
			else if(r1 == 1) {
				g2.setPaint(Color.green);
			}
			else {
				g2.setPaint(Color.gray);
			}
			g2.fillOval((int)(point25.get(i).getX())-cWidth/2, (int)(point25.get(i).getY())-cHeight/2, cWidth, cHeight);
			g2.setPaint(paint);
			g2.drawString(strs[i], (int)(point25.get(i).getX()),  (int)(point25.get(i).getY()) + 20);
		}
		//0~4 point
//		g2.fillOval(orginX-cWidth/2, orginY-cHeight/2, cWidth, cHeight);
//		g2.fillOval(orginX + smallOvalWidth/2-cWidth/2, orginY-cHeight/2, cWidth, cHeight);
//		g2.fillOval(orginX + smallOvalHeight/2-cWidth/2, orginY - smallOvalHeight/2-cHeight/2, cWidth, cHeight);
//		g2.fillOval(orginX - smallOvalWidth/2-cWidth/2, orginY-cHeight/2, cWidth, cHeight);
//		g2.fillOval(orginX - smallOvalHeight/2-cWidth/2, orginY + smallOvalHeight/2-cHeight/2, cWidth, cHeight);
//		
//		//5~9 point
//		g2.fillOval(orginX-cWidth/2, orginY-cHeight/2-40, cWidth, cHeight);
//		g2.fillOval(orginX + largeOvalWidth/2-cWidth/2, orginY-cHeight/2-40, cWidth, cHeight);
//		g2.fillOval(orginX + largeOvalHeight/2-cWidth/2, orginY - largeOvalHeight/2-cHeight/2 - 40, cWidth, cHeight);
//		g2.fillOval(orginX - largeOvalWidth/2-cWidth/2, orginY-cHeight/2- 40, cWidth, cHeight);
//		g2.fillOval(orginX - largeOvalHeight/2-cWidth/2, orginY + largeOvalHeight/2 -cHeight/2- 40, cWidth, cHeight);
//	
//		//10~14 point
//		g2.fillOval(orginX-cWidth/2, orginY-cHeight/2-85, cWidth, cHeight);
//		g2.fillOval(orginX + largeOvalWidth/2-cWidth/2, orginY-cHeight/2-85, cWidth, cHeight);
//		g2.fillOval(orginX + largeOvalHeight/2-cWidth/2, orginY - largeOvalHeight/2 - 85 -cHeight/2, cWidth, cHeight);
//		g2.fillOval(orginX - largeOvalWidth/2-cWidth/2, orginY - 85 -cHeight/2, cWidth, cHeight);
//		g2.fillOval(orginX - largeOvalHeight/2-cWidth/2, orginY + largeOvalHeight/2 - 85 -cHeight/2, cWidth, cHeight);
//		
//		//15~19 point
//		g2.fillOval(orginX-cWidth/2, orginY-cHeight/2-130, cWidth, cHeight);
//		g2.fillOval(orginX + largeOvalWidth/2-cWidth/2, orginY-cHeight/2-130, cWidth, cHeight);
//		g2.fillOval(orginX + largeOvalHeight/2-cWidth/2, orginY - largeOvalHeight/2-cHeight/2-130, cWidth, cHeight);
//		g2.fillOval(orginX - largeOvalWidth/2-cWidth/2, orginY-cHeight/2-130, cWidth, cHeight);
//		g2.fillOval(orginX - largeOvalHeight/2-cWidth/2, orginY + largeOvalHeight/2-cHeight/2-130, cWidth, cHeight);
//	
//		//20~24 point
//		g2.fillOval(orginX-cWidth/2, orginY-cHeight/2-170, cWidth, cHeight);
//		g2.fillOval(orginX + smallOvalWidth/2-cWidth/2, orginY-cHeight/2-170, cWidth, cHeight);
//		g2.fillOval(orginX + smallOvalHeight/2-cWidth/2, orginY - smallOvalHeight/2-cHeight/2-170, cWidth, cHeight);
//		g2.fillOval(orginX - smallOvalWidth/2-cWidth/2, orginY-cHeight/2-170, cWidth, cHeight);
//		g2.fillOval(orginX - smallOvalHeight/2-cWidth/2, orginY + smallOvalHeight/2-cHeight/2-170, cWidth, cHeight);
		
		g2.setPaint(paint);
	}
	
	public void drawResultPoint(int i, int result) {
		result25.set(i, result);
		repaint();
	}
	
	public static void drawAL(int sx, int sy, int ex, int ey, Graphics2D g2)  
    {   
        double H = 10; // ��ͷ�߶�  
        double L = 4; // �ױߵ�һ��  
        int x3 = 0;  
        int y3 = 0;  
        int x4 = 0;  
        int y4 = 0;  
        double awrad = Math.atan(L / H); // ��ͷ�Ƕ�  
        double arraow_len = Math.sqrt(L * L + H * H); // ��ͷ�ĳ���  
        double[] arrXY_1 = rotateVec(ex - sx, ey - sy, awrad, true, arraow_len);  
        double[] arrXY_2 = rotateVec(ex - sx, ey - sy, -awrad, true, arraow_len);  
        double x_3 = ex - arrXY_1[0]; // (x3,y3)�ǵ�һ�˵�  
        double y_3 = ey - arrXY_1[1];  
        double x_4 = ex - arrXY_2[0]; // (x4,y4)�ǵڶ��˵�  
        double y_4 = ey - arrXY_2[1];  
  
        java.lang.Double X3 = new Double(x_3);  
        x3 = X3.intValue();  
        Double Y3 = new Double(y_3);  
        y3 = Y3.intValue();  
        Double X4 = new Double(x_4);  
        x4 = X4.intValue();  
        Double Y4 = new Double(y_4);  
        y4 = Y4.intValue();  
        // ����  
        g2.drawLine(sx, sy, ex, ey);  
        //  
        GeneralPath triangle = new GeneralPath();  
        triangle.moveTo(ex, ey);  
        triangle.lineTo(x3, y3);  
        triangle.lineTo(x4, y4);  
        triangle.closePath();  
        //ʵ�ļ�ͷ  
        g2.fill(triangle);  
        //��ʵ�ļ�ͷ  
        //g2.draw(triangle);  
  
    }
	
	public static double[] rotateVec(int px, int py, double ang,  
            boolean isChLen, double newLen) {  
  
        double mathstr[] = new double[2];  
        // ʸ����ת���������ֱ���x������y��������ת�ǡ��Ƿ�ı䳤�ȡ��³���  
        double vx = px * Math.cos(ang) - py * Math.sin(ang);  
        double vy = px * Math.sin(ang) + py * Math.cos(ang);  
        if (isChLen) {  
            double d = Math.sqrt(vx * vx + vy * vy);  
            vx = vx / d * newLen;  
            vy = vy / d * newLen;  
            mathstr[0] = vx;  
            mathstr[1] = vy;  
        }  
        return mathstr;  
    } 
	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setSize(500,600);
		RFResultPanel  resultPanel = new RFResultPanel();
		frame.setLayout(new BorderLayout());
		resultPanel.drawResultPoint(5, -1);
		resultPanel.drawResultPoint(8, 1);
		frame.add(resultPanel,BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}

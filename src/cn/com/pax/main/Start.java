package cn.com.pax.main;

import java.awt.EventQueue;

import javax.swing.JFrame;

import cn.com.pax.display.mainFrame;
import cn.pax.com.utils.StopMove;

public class Start {
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				StopMove.setRunning(true);
				JFrame frame = new mainFrame();
				frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				frame.setVisible(true);
			}
		});
	}
}

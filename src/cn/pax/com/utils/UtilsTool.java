package cn.pax.com.utils;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilsTool {
	public static String printExceptionStack(Throwable e) {
		StringWriter writer = new StringWriter();
		PrintWriter pWriter = new PrintWriter(writer, true);
		e.printStackTrace(pWriter);
		pWriter.close();
		return writer.toString();
	}

	public static  void updateView(final JTextField textField, final String string) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				textField.setText(string);
			}
		});
	}

	public static void updateView(final JTextArea area, final String string) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				area.setText(string);
			}
		});
	}

	public static void updateView(final JTextArea area, final String string, final boolean isAppend) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				if(isAppend)
					area.append(string);
				else
					area.setText(string);
			}
		});
	}

	public static void updateArea(final JTextArea area, final String key, final String result) {
		EventQueue.invokeLater(new Runnable() {
		   @Override
		   public void run() {
			   String content = area.getText();
			   //System.out.println(content.replace("\n", ""));
			   String replaceContent = content.replaceFirst("(" + key + "\\W+?)([a-zA-Z/]+)", "$1" + result);
			   area.setText(replaceContent);
		   }
	   });

	}

//	public static void updateArea(String content, final String key, final String result) {
//		String replaceContent = content.replaceFirst("(" + key + "\\W+?)([a-zA-Z/]+)", "$1" + result);
//		System.out.println(replaceContent);
//	}

	public static void main(String[] args) {
//		String string = "扫描速度：PASS\n360旋转角度：N/A\n扫描景深：FAIL\n";
//		updateArea(string, "360旋转角度", "PASS");
//		updateArea(string, "扫描景深", "PASS");
//		try {
//			throw new RuntimeException("ssss");
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//			System.out.println("-----------------------");
//			System.out.println(printExceptionStack(e));
//		}
	}
}

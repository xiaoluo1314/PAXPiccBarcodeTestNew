package cn.com.pax.protocol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.swing.JFrame;

import cn.com.pax.display.mainFrame;
import org.omg.PortableServer.ServantLocatorPackage.CookieHolder;


import cn.com.pax.sericomm.SerialConnection;

public class ControlDV implements Runnable {
	//AA 55 02 F1 00 F3
	byte []startCmd =  new byte[]{(byte) 0xAA, 0x55, 0x02,(byte) 0xF1,0x00,(byte) 0xF3};//连续读
	//AA 55 02 FE 01 00
	byte []receive = new byte[]{(byte) 0xAA, 0x55, 0x02, (byte) 0xFE, 0x01, 0x00};//适合一次读
	//AA 55 02 F2 00 F4
	byte []endCmd = new byte[]{(byte) 0xAA, 0x55, 0x02, (byte) 0xF2, 0x00, (byte) 0xF4};//结束读
	List<Integer>list = new ArrayList<Integer>();
	private double result = 0;
	private boolean isRunning;
	SerialConnection conn;
	JFrame jf;
	public synchronized double getResult() {
		return result;
	}

	public synchronized void setResult(double result) {
		this.result = result;
	}

	public ControlDV ( SerialConnection conn,JFrame jf) {
		this.conn = conn;
		this.jf = jf;
		isRunning = true;
	}
	
	public void run() {
		
		byte []buf = new byte[2];
		
		ControlDVProtocl.setConnection(conn);
		conn.readExtraData(100);
		conn.clearSendBeforeData();
		conn.serailWrite(startCmd);
		//buf = conn.serialRead(6, 3);
		
	//	if ( buf[2]==0x02 && buf[3]==(byte)0xF3) {
		
		while (isRunning) {
			//AA 55 02 F3 00 F5  AA 55 04 F6 v0 v1 XX XX
			if(ControlDVProtocl.readProtocol(buf)) {
				int tmp = getShort(buf[0],buf[1]);
				if (tmp>=0) {
					list.add(tmp);
				}
			}
		}
		conn.serailWrite(endCmd);
		conn.readExtraData(100);
		conn.clearSendBeforeData();
		
		//System.out.println(Arrays.deepToString(list.toArray()));

		
		/*if (list.size()>0) {
			res1 = list.get(list.size()-1);
		}else {
			res1 =1;
		}
		setResult(res1/1000);*/
		//setResult(res1/((list.size() > 0 ? list.size() : 1) * 1000));
		 if (list.size() > 0 ) {
			 //mainFrame.logger.info("voltages: " + Arrays.deepToString(list.toArray()));
			 getConvertValue(list);
			 List<Integer>list_1 = getValue(list);
			 double res1 =0;
			 int count = 0;
			 for (Integer integer :list_1) {
			 	if(integer > 0) {
					res1 += integer;
					count++;
				}
			 }
			 if(count == 0) count=1;
			 setResult(res1/(count*1000));
			 //setResult(java.util.Collections.max(list)/1000.0);
		 }else {
			 setResult(-1);
		}
	}
		
	//}
	//AA 55 02 F3 00 F5 AA 55 04 F6 00 00 00 FA AA 55 04 F6 00 00 00 FA
	//转换成short类型 v1*256+v0
	public static short getShort(byte b, byte a) {  
        return (short) (((a << 8) | b & 0xff));  
    }

    public static void getConvertValue(List<Integer> tList) {
		List<String> tmpList = new ArrayList<>();
		for(int i : tList) {
			tmpList.add("(" + (i/128) + "," + (i%128 * 2) + ")");
		}
		mainFrame.logger.info("voltages: " + Arrays.deepToString(tmpList.toArray()));
	}

	public synchronized boolean isRunning() {
		return isRunning;
	}

	public synchronized void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public static List<Integer> getValue(List <Integer>list){
		if(list.size()<2){
			return list;
		}
		List<Integer>list_1 = new  ArrayList<Integer>();
		for (int i = 0; i < list.size(); i++) {
			if ((i+1)< list.size()) {
				if ((Math.abs(list.get(i+1)-list.get(i))<=10)) {
					list_1.add(list.get(i));
				}
			}
			
		}
		//加入最后一个值
		list_1.add(list.get(list.size()-1));
		if (list_1.size()<list.size()/2) {
			return list;
		}
		return list_1;
		
	}
	public static void main(String[] args) {
		List<Integer>list = new ArrayList<Integer>();
		list.add(4985);
		list.add(4289);
		list.add(4290);
		list.add(4291);list.add(4291);list.add(4291);list.add(4291);list.add(4291);list.add(4292);
		List<Integer>list2 = ControlDV.getValue(list);
		System.out.println(Arrays.deepToString(list2.toArray()));
	}

}

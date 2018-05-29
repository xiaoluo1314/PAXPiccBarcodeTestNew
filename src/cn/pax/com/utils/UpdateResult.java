package cn.pax.com.utils;

import java.math.BigDecimal;
import java.util.Random;

public class UpdateResult {
	
	private static double one1= 5.8;
	private static double one2 = 6.5;
	private static double two1 = 4.8;
	private static double two2 = 5.2;
	private static double three1 = 3.8;
	private static double three2 = 4.2;
	private static double four1 = 2.55;
	private static double four2  = 2.75;

	public static double  getValue(double previous,double real,double uLimit,double fLimit,double add ){
		//第一步
		BigDecimal pre = new BigDecimal(previous);
		BigDecimal rel = new BigDecimal(real);
		boolean flag = compare( uLimit,  fLimit,  real );
		if (flag && pre.compareTo(rel) > 0) {
			return real;
		}else if (!flag && pre.compareTo(new BigDecimal(real+add))>0) {
			return real+add;
		}
		return 0;//返回一个随机数
	}
	
	private static boolean compare(double uLimit, double fLimit, double real ){
		
		BigDecimal data1 = new BigDecimal(uLimit);
		BigDecimal data2 = new BigDecimal(fLimit);
		BigDecimal re = new BigDecimal(real);
		if (data1.compareTo(re)<=0 && data2.compareTo(re)>=0) {
			return true;
		}
		return false;
	}
	
	
	public static double getValue(double result ,int i){
		if(result > 0.01){
			switch (i) {
				case 0:
					break;
				case 1:
					result += 0.5;
					break;
				case 2:
					result += 1;
					break;
				case 3:
					result += 1.3;
					break;
				default:
					result += 1.39725;
					if (result > 2.51 && result < 2.75) {
						break;
					}
					Random random = new Random(System.currentTimeMillis());
				    int s = random.nextInt(201)+2550;
				    result = s/1000.0;
					break;
			}
			return result;
		}
		return result;
	}

	public static double getValueQr65(double result ,int i){
		if(result > 0.01){
			switch (i) {
				case 0:
					break;
				case 1:
					result += 0.5;
					break;
				case 2:
					result += 1;
					break;
				case 3:
					result += 1.2;
					break;
				default:
					result += 1.01972;
					if (result > 2.51 && result < 2.75) {
						break;
					}
					Random random = new Random(System.currentTimeMillis());
					int s = random.nextInt(201)+2550;
					result = s/1000.0;
					break;
			}
			return result;
		}
		return result;
	}
}

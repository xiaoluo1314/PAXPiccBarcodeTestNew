package cn.com.pax.report;

public class RFDetail {
	public RFDetail(String distance, int pointNum, int succNum, int relSucc) {
		super();
		this.distance = distance;
		this.pointNum = pointNum;
		this.succNum = succNum;
		this.relSucc = relSucc;
	}
	
	public String getDistance() {
		return distance;
	}
	public int getPointNum() {
		return pointNum;
	}
	public int getSuccNum() {
		return succNum;
	}
	public String getPercent() {
		return (relSucc * 100 / (pointNum*2)) + "%";
	}

	private String distance;
	private int pointNum;
	private int succNum;
	private int relSucc;
}

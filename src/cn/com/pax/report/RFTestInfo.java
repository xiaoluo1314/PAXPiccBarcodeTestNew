package cn.com.pax.report;

import java.util.Date;

public class RFTestInfo {
	
	public RFTestInfo(String ut, String tester, String model, String sn) {
		super();
		this.reportDate = new Date();;
		this.ut = ut;
		this.tester = tester;
		this.model = model;
		this.sn = sn;
	}
	
	public Date getReportDate() {
		return reportDate;
	}
	public String getUt() {
		return ut;
	}
	public String getTester() {
		return tester;
	}
	public String getModel() {
		return model;
	}
	public String getSn() {
		return sn;
	}

	private Date reportDate;
	private String ut;
	private String tester;
	private String model;
	private String sn;
}

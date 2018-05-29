package cn.com.pax.report;
import java.util.Date;


public class TestInfo {
	public TestInfo(String tester, String address, String environment,
			String model, String hwver, String softver, String dutnum, String comment) {
		super();
		this.reportDate = new Date();
		this.tester = tester;
		this.address = address;
		this.environment = environment;
		this.model = model;
		this.hwver = hwver;
		this.softver = softver;
		this.dutnum = dutnum;
		this.comment = comment;
	}
	
	public Date getReportDate() {
		return reportDate;
	}
	public String getTester() {
		return tester;
	}
	public String getAddress() {
		return address;
	}
	public String getEnvironment() {
		return environment;
	}
	public String getModel() {
		return model;
	}
	public String getHwver() {
		return hwver;
	}
	public String getSoftver() {
		return softver;
	}
	public String getDutnum() {
		return dutnum;
	}
	public String getComment() {
		return comment;
	}
	
	private Date reportDate;
	private String tester;
	private String address;
	private String environment;
	private String model;
	private String hwver;
	private String softver;
	private String dutnum;
	private String comment;
}

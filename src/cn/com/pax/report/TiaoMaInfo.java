package cn.com.pax.report;

public class TiaoMaInfo {
	public TiaoMaInfo(String type, String dimen, String codeName, String indName, String indicator, 
			String result, String detail) {
		super();
		id++;
		this.type = type;
		this.dimen = dimen;
		this.codeName = codeName;
		this.indName = indName;
		this.indicator = indicator;
		this.result = result;
		this.detail = detail;
	}
	
	public static int getId() {
		return id;
	}
	public String getType() {
		return type;
	}
	public String getDimen() {
		return dimen;
	}
	public String getCodeName() {
		return codeName;
	}
	public String getIndName() {
		return indName;
	}
	public String getIndicator() {
		return indicator;
	}
	public String getResult() {
		return result;
	}
	public String getDetail() {
		return detail;
	}
	
//	@Override
//	public String toString() {
//		// TODO Auto-generated method stub
//		return type + ":" + dimen + ":" + getCodeName() + ":" + getResult();
//	}
	@Override
	public String toString() {
		return "TiaoMaInfo [type=" + type + ", dimen=" + dimen + ", codeName="
				+ codeName + ", indName=" + indName + ", indicator="
				+ indicator + ", result=" + result + ", detail=" + detail + "]";
	}

	private static int id = 0;
	private String type;
	private String dimen;
	private String codeName;
	private String indName;
	private String indicator;
	private String result;
	private String detail;
}

package cn.pax.com.utils;

public class BarcodeInfo {
	private String barcodeType;
	private String scanType;
	private String barcodeName;
	private String barcodeContent;
	private String fileName;

	public BarcodeInfo(String barcodeType, String scanType, String barcodeName,
			String barcodeContent, String fileName) {
		super();
		this.barcodeType = barcodeType;
		this.scanType = scanType;
		this.barcodeName = barcodeName;
		this.barcodeContent = barcodeContent;
		this.fileName = fileName;
	}


	public String getBarcodeType() {
		return barcodeType;
	}


	public String getScanType() {
		return scanType;
	}


	public String getBarcodeName() {
		return barcodeName;
	}


	public String getBarcodeContent() {
		return barcodeContent;
	}


	public String getFileName() {
		return fileName;
	}


	@Override
	public String toString() {
		return "BarcodeInfo [barcodeType=" + barcodeType + ", scanType="
				+ scanType + ", barcodeName=" + barcodeName
				+ ", barcodeContent=" + barcodeContent + ", fileName="
				+ fileName + "]";
	}
}

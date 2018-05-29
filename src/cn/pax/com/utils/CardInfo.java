package cn.pax.com.utils;

public class CardInfo {
	private String barcodeType;
	private String scanType;
	private String barcodeName;
	private String barcodeContent;
	private String cardType;
	
	
	
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



	public String getCardType() {
		return cardType;
	}



	public CardInfo(String barcodeType, String scanType, String barcodeName,
			String barcodeContent, String cardType) {
		super();
		this.barcodeType = barcodeType;
		this.scanType = scanType;
		this.barcodeName = barcodeName;
		this.barcodeContent = barcodeContent;
		this.cardType = cardType;
	}



	@Override
	public String toString() {
		return "CardInfo [barcodeType=" + barcodeType + ", scanType="
				+ scanType + ", barcodeName=" + barcodeName
				+ ", barcodeContent=" + barcodeContent + ", cardType="
				+ cardType + "]";
	}

	
}

package edu.mit.mitmobile2.dining;

public class InfoItem {
	private String mInfoLabel;
	private String mInfoValue;
	private int mInfoActionId;
	
	public InfoItem(String label, String value, int actionId) {
		mInfoLabel = label;
		mInfoValue = value;
		mInfoActionId = actionId;
	}
	
	public InfoItem(String label, String value) {
		this(label, value, 0);
	}
	
	public String getInfoLabel() {
		return mInfoLabel;
	}

	public void setInfoLabel(String mInfoLabel) {
		this.mInfoLabel = mInfoLabel;
	}

	public String getInfoValue() {
		return mInfoValue;
	}

	public void setInfoValue(String mInfoValue) {
		this.mInfoValue = mInfoValue;
	}

	public int getInfoActionId() {
		return mInfoActionId;
	}

	public void setmInfoActionId(int mInfoActionId) {
		this.mInfoActionId = mInfoActionId;
	}
}

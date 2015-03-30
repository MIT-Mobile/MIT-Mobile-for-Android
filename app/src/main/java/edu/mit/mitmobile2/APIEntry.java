package edu.mit.mitmobile2;

public class APIEntry {

	private String dev;
	private String test;
	private String prod;
	private String manager;

	public String getBaseUrl(String environment) {
		if (environment == MITAPIClient.DEV) {
			return this.getDev();
		}
		
		if (environment == MITAPIClient.TEST) {
			return this.getTest();
		}
		
		
		if (environment == MITAPIClient.PROD) {
			return this.getProd();
		}

		return null;
	}
	
	public String getDev() {
		return dev;
	}
	public void setDev(String dev) {
		this.dev = dev;
	}
	public String getTest() {
		return test;
	}
	public void setTest(String test) {
		this.test = test;
	}
	public String getProd() {
		return prod;
	}
	public void setProd(String prod) {
		this.prod = prod;
	}
	public String getManager() {
		return manager;
	}
	public void setManager(String manager) {
		this.manager = manager;
	}
}

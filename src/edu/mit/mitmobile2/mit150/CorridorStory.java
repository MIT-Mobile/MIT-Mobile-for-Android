package edu.mit.mitmobile2.mit150;

import java.util.Date;

public class CorridorStory {

	private String mTitle;
	private String mFirstname;
	private String mLastname;
	private String mAffiliation;
	private Date mDatePosted;
	private Image mImage;
	private String mBody;
	private String mPlainText;

	public CorridorStory(String title, String firstname, String lastname, String affiliation, Date datePosted, Image image, String body, String plainText) {
		mTitle = title;
		mFirstname = firstname;
		mLastname = lastname;
		mAffiliation = affiliation;
		mDatePosted = datePosted;
		mImage = image;
		mBody = body;
		mPlainText = plainText;
	}
	
	public String getTitle() {
		return mTitle;
	}
	
	public String getFirstname() {
		return mFirstname;
	}
	
	public String getLastname() {
		return mLastname;
	}
	
	public String getAffiliation() {
		return mAffiliation;
	}
	
	public Date getDatePosted() {
		return mDatePosted;
	}
	
	public Image getImage() {
		return mImage;
	}
	
	public String getBody() {
		return mBody;
	}
	
	public String getPlainText() {
		return mPlainText;
	}
	
	public static class Image {
		private String mUrl;
		private int mWidth;
		private int mHeight;

		public Image(String url, int width, int height) {
			mUrl = url;
			mWidth = width;
			mHeight = height;
		}
		
		public String getUrl() {
			return mUrl;
		}
		
		public int getWidth() {
			return mWidth;
		}
		
		public int getHeight() {
			return mHeight;
		}
	}
}

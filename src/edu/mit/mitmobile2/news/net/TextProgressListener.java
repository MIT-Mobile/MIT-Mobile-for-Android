package edu.mit.mitmobile2.news.net;

public interface TextProgressListener {
	void onProgressUpdateText(String... text);
	void onPostExecuteText(Long textDownloaded);
}

package edu.mit.mitmobile;


public class StyledContentHTML {

	public static String html(String bodyHTML) {
		return       "<html>" +
						"<head>" +
							"<style>" +
								"body {" +
								"	font-style: normal;" +
								"   font-size: 11pt;" +
								"   color: #202020; " +
								"	margin-left: 10px; " +
								"   margin-right: 10px; " +
								"}" +
								"a {" +
								"   color: #990000;" +
								"}" +
							"</style>" +
						"</head>" +
						"<body>" +
						bodyHTML +
						"</body>" +
					"</html>";
	}
	
	public static String imageHtml(String url) {
		return "<html>" +
				"<body>" +
					"<img src=\"" + url + "\"  width=\"100%\"/>" +
				"</body>" +
				"</html>";
	}
}

package edu.mit.mitmobile.objs;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class NewsItem {

	public NewsItem() {
		img = null;
		otherImgs = new ArrayList<Image>();
	}
	
	public String title = "";
	public String author = "";
	public HashSet<Integer> categories = new HashSet<Integer>();
	public String link = "";
	public int story_id = -1;
	public boolean featured;
	public String description = "";
	public Date postDate = null;
	public String body = "";

	
	public String thumbURL = null;
	public Image img;
	
	public ArrayList<Image> otherImgs;
	
	public boolean isRead = false;
	
	public static class Image {
		public String smallURL = "";
		public String fullURL  = "";
		public String imageCaption = "";
		public String imageCredits  = "";
	}
	
	public List<Image> getAllImages() {
		ArrayList<Image> imagesArray = new ArrayList<Image>();
		if(img != null) {
			imagesArray.add(img);
		}
		
		imagesArray.addAll(otherImgs);
		
		return imagesArray;
	}
}

/*
http://mobile-dev.mit.edu/api/newsoffice/

	science (which happens to be channel_id=2):
	http://mobile-dev.mit.edu/api/newsoffice/?channel_id=2
	
	to get more results for the category
	http://mobile-dev.mit.edu/api/newsoffice/?channel_id=2&story_id=15391
	where story_id means return results older than this story_id, each
	story has a unique Id

	a news search:
	http://web.mit.edu/newsoffice/index.php?option=com_search&view=isearch&searchword=fun&ordering=newest&limit=10&start=0

	to get more search results:
	http://web.mit.edu/newsoffice/index.php?option=com_search&view=isearch&searchword=fun&ordering=newest&limit=10&start=10
		
		
/*
<?xml version="1.0" encoding="utf-8"?>
<rss version="2.0">
<channel>
<title/>
<link>http://web.mit.edu/newsoffice</link>
<description/>

<item>
<title><![CDATA[Researchers seek to put the squeeze on cancer]]></title>
<author><![CDATA[Anne Trafton, MIT News Office]]></author>
<category>1</category>
<link>http://web.mit.edu/newsoffice/2010/angiogenesis-06152010.html</link>
<story_id>15438</story_id>
<featured>1</featured>
<description><![CDATA[Cell contractions may be key to initiating new blood-vessel growth near tumors.]]></description>
<postDate>Tue, 15 Jun 2010 04:00:00 EDT </postDate>

<image>
<thumbURL>http://web.mit.edu/newsoffice/images/article_images/w76/20100611145614-1.png</thumbURL>
<smallURL width="140" height="105">http://web.mit.edu/newsoffice/images/article_images/w140/20100611145614-1.jpg</smallURL>
<fullURL width="368" height="276">http://web.mit.edu/newsoffice/images/article_images/20100611145614-1.jpg</fullURL>
<imageCaption><![CDATA[MIT and Tufts researchers have shown that mechanical forces from cells that surround small blood vessels may control the growth of new vessels.]]>
</imageCaption>
</image>

<body><![CDATA[Cancer researchers have been s
/>]]></body>
</item></channel></rss>
*/

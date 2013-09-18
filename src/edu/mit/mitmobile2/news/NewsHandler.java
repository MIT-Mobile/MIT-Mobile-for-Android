package edu.mit.mitmobile2.news;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.mit.mitmobile2.DLog;
import edu.mit.mitmobile2.objs.NewsItem;
import edu.mit.mitmobile2.objs.NewsItem.Image;

public class NewsHandler extends DefaultHandler{

		static final  String ITEMS = "items";
		
	    static final  String PUB_DATE = "postDate";
	    static final  String DESCRIPTION = "description";
	    static final  String LINK = "link";
	    static final  String TITLE = "title";
	    static final  String BODY = "body";
	    static final  String ITEM = "item";
	    
	    static final  String story_id = "story_id";
	    static final  String author = "author";
	    static final  String category = "category";
	    
	    // image related
	    static final  String IMAGE = "image";
	    static final  String OTHER_IMAGES = "otherImages";
	    static final  String THUMB_URL = "thumb152";
	    static final  String SMALL_URL = "smallURL";
	    static final  String FULL_URL = "fullURL";
	    static final  String IMAGE_CAPTION = "imageCaption";
	    static final  String IMAGE_CREDITS = "imageCredits";
	    
	    static final SimpleDateFormat sDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);

	    Image img;
	    boolean otherImagesFlag = false;
		
	    private List<NewsItem> NewsItems;
	    private NewsItem curItem;
	    private StringBuilder builder;
	    
	    private Integer mTotalResults;
	    
	    public List<NewsItem> getNewsItems(){
	        return this.NewsItems;
	    }
	    
	    public List<NewsItem> setNewsItems(List<NewsItem> news){
	    	return this.NewsItems = news;
	    }
	    
	    public int totalResults() {
	    	return mTotalResults;
	    }
	    
	    public void settotalResults(int total) {
	    	mTotalResults = total;
	    }
	    
	    @Override
	    public void characters(char[] ch, int start, int length)
	            throws SAXException {
	        super.characters(ch, start, length);
	        builder.append(ch, start, length);
	    }

	    @Override
	    public void endElement(String uri, String localName, String name)
	            throws SAXException {
	    	
	        super.endElement(uri, localName, name);
	        
	        String text = builder.toString().trim();

	    	DLog.v("NewsHandler","end: name="+name + " localName=" + localName);
	    	
	        if (this.curItem != null){
	            if (localName.equalsIgnoreCase(TITLE)){
	                curItem.title = text;
	            } else if (localName.equalsIgnoreCase(LINK)){
	                curItem.link = text;
	            } else if (localName.equalsIgnoreCase(DESCRIPTION)){
	                curItem.description = text;
	            } else if (localName.equalsIgnoreCase(PUB_DATE)){
	                try {
						curItem.postDate = sDateFormat.parse(text);
					} catch (ParseException e) {
						e.printStackTrace();
						throw new RuntimeException("Failed at parsing date: " + text);
					}                
	            } else if (localName.equalsIgnoreCase(THUMB_URL)){
	                curItem.thumbURL = text;
	            } else if (localName.equalsIgnoreCase(FULL_URL)){
	            	img.fullURL = text;
	            } else if (localName.equalsIgnoreCase(SMALL_URL)){
	            	img.smallURL = text;
	            	
	            } else if (localName.equalsIgnoreCase(story_id)){
	            	curItem.story_id = Integer.parseInt(text);
	            } else if (localName.equalsIgnoreCase(category)){
	                curItem.categories.add(Integer.parseInt(text));
	            } else if (localName.equalsIgnoreCase(author)){
	                curItem.author = text;
	                
	            } else if (localName.equalsIgnoreCase(BODY)){
	                curItem.body = text;
	            } else if (localName.equalsIgnoreCase(IMAGE_CAPTION)){
	            	img.imageCaption = text;
	            } else if (localName.equalsIgnoreCase(IMAGE_CREDITS)){
	            	img.imageCredits = text;
	            } else if (localName.equalsIgnoreCase(OTHER_IMAGES)){
	            	otherImagesFlag = false;
	            } else if (localName.equalsIgnoreCase(IMAGE)){
	            	if (otherImagesFlag) curItem.otherImgs.add(img);
	            	else curItem.img = img;
	            } else if (localName.equalsIgnoreCase(ITEM)){
	                NewsItems.add(curItem);
	            }  
	        }
            builder.setLength(0);  
	    }

	    @Override
	    public void startDocument() throws SAXException {
	        super.startDocument();
	        NewsItems = new ArrayList<NewsItem>();
	        builder = new StringBuilder();
	    }

	    @Override
	    public void startElement(String uri, String localName, String name,
	            Attributes attributes) throws SAXException {
	    	
	    	DLog.v("NewsHandler","start:"+name);
	    	
	        super.startElement(uri, localName, name, attributes);
	        
	        if(localName.equalsIgnoreCase(ITEMS)) {
	        	String totalResults = attributes.getValue(1);
	        	if(totalResults != null) {
	        		mTotalResults = Integer.parseInt(totalResults);
	        	}
	    	} else if (localName.equalsIgnoreCase(IMAGE)){
	        	img = new Image();
	        } else if (localName.equalsIgnoreCase(FULL_URL)){
	        	// TODO use the width and height attributes
	        	attributes.getValue("width");
	        	attributes.getValue("height");
	        } else if (localName.equalsIgnoreCase(OTHER_IMAGES)){
	        	otherImagesFlag = true;
	        	img = new Image();
	        } else if (localName.equalsIgnoreCase(ITEM)){
	            this.curItem = new NewsItem();
	        }
	    }
	
}

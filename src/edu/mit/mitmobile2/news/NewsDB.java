package edu.mit.mitmobile2.news;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

import edu.mit.mitmobile2.objs.NewsItem;
import edu.mit.mitmobile2.objs.NewsItem.Image;

public class NewsDB {
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "news.db";
	private static final String STORIES_TABLE = "stories";
	private static final String CATEGORIES_TABLE = "categories";
	private static final String IMAGE_URLS_TABLE = "image_urls";

	
	// story table field names
	private static final String STORY_ID = "_id";
	private static final String TITLE = "title";
	private static final String BODY = "body";
	private static final String AUTHOR = "author";
	private static final String FEATURED = "featured";
	private static final String DESCRIPTION = "description";
	private static final String POST_DATE = "post_date";
	private static final String LINK = "link";
	private static final String THUMBNAIL = "thumbnail";
	private static final String THUMB_URL = "thumb_url";
	private static final String MAIN_IMAGE_ID = "main_image_id";
	private static final String BOOKMARKED = "bookmarked";
	
	private static final String STORY_ID_WHERE = STORY_ID + "=?";
	
	// category table field names
	private static final String CATEGORY = "category";
	
	// image table field names
	private static final String IMAGE_STORY_ID = "story_id";
	private static final String PHOTO_ID = "photo_id";
	private static final String SMALL_URL = "small_url";
	private static final String FULL_URL = "full_url";
	private static final String IMAGE_CAPTION = "image_caption";
	private static final String IMAGE_CREDITS = "image_credits";
	private static final String IS_MAIN = "is_main";
	
	SQLiteOpenHelper mNewsDBHelper;
	
	private static NewsDB newsDBInstance = null;
	
	public static NewsDB getInstance(Context context) {
		if(newsDBInstance == null) {
			newsDBInstance = new NewsDB(context);
			return newsDBInstance;
		} else {
			return newsDBInstance;
		}
	}
	private NewsDB(Context context) {
		mNewsDBHelper = new NewsDatabaseHelper(context); 
	}
	
	private String[] whereArgs(NewsItem newsItem) {
		return new String[] {Integer.toString(newsItem.story_id)};
	}
	
	synchronized void clearAllStories() {
		SQLiteDatabase db = mNewsDBHelper.getWritableDatabase();
		db.delete(STORIES_TABLE, BOOKMARKED + "=0", null);
		db.delete(IMAGE_URLS_TABLE, null, null);
		db.delete(CATEGORIES_TABLE, null, null);
	}
	
	synchronized void clearCategory(int categoryId) {
		SQLiteDatabase db = mNewsDBHelper.getWritableDatabase();
		db.delete(CATEGORIES_TABLE, CATEGORY + "=?", new String[] {Integer.toString(categoryId)});
	}
	
	synchronized void clearAllBookmarks() {
		SQLiteDatabase db = mNewsDBHelper.getWritableDatabase();
		ContentValues newsValues = new ContentValues();
		newsValues.put(BOOKMARKED, false);
		db.update(STORIES_TABLE, newsValues, null, null);
	}
	
	void startTransaction() {
		mNewsDBHelper.getWritableDatabase().beginTransaction();
	}
	
	void endTransaction() {
		mNewsDBHelper.getWritableDatabase().setTransactionSuccessful();
		mNewsDBHelper.getWritableDatabase().endTransaction();
	}
	
	synchronized void updateBookmarkStatus(NewsItem story, boolean bookmarkedStatus) {
		SQLiteDatabase db = mNewsDBHelper.getWritableDatabase();
		
		ContentValues newsValues = new ContentValues();
		newsValues.put(STORY_ID, story.story_id);
		newsValues.put(BOOKMARKED, bookmarkedStatus);
		db.update(STORIES_TABLE, newsValues, STORY_ID_WHERE, whereArgs(story));
	}
	
	synchronized void saveNewsItem(NewsItem story, boolean saveCategories) {
		SQLiteDatabase db = mNewsDBHelper.getWritableDatabase();
		ContentValues newsValues = new ContentValues();
		newsValues.put(STORY_ID, story.story_id);
		newsValues.put(TITLE, story.title);
		newsValues.put(BODY, story.body);
		newsValues.put(AUTHOR, story.author);
		newsValues.put(FEATURED, story.featured);
		newsValues.put(DESCRIPTION, story.description);
		newsValues.put(POST_DATE, story.postDate.getTime());
		newsValues.put(LINK, story.link);
		newsValues.put(THUMB_URL, story.thumbURL);
		
		if(storyExists(story.story_id)) {
			db.update(STORIES_TABLE, newsValues, STORY_ID_WHERE, whereArgs(story));
			
			// delete old images will re-save them
			db.delete(IMAGE_URLS_TABLE, IMAGE_STORY_ID + "=?", whereArgs(story));
		} else {
			db.insert(STORIES_TABLE, BODY, newsValues);
		}
		
		if(saveCategories) {
			for(Integer categoryId : story.categories) {
				ContentValues categoryValues = new ContentValues();
				categoryValues.put(STORY_ID, story.story_id);
				categoryValues.put(CATEGORY, categoryId);
				db.insert(CATEGORIES_TABLE, CATEGORY, categoryValues);
			}
		}
		
		// insert images
		int photoId = 0;
		if(story.img != null) {
			long main_image_id = insertImage(story.story_id, story.img, photoId, true);
			ContentValues mainImageValue = new ContentValues();
			mainImageValue.put(MAIN_IMAGE_ID, main_image_id);
			db.update(STORIES_TABLE, mainImageValue, STORY_ID_WHERE, whereArgs(story));
		}
		
		for(Image image : story.otherImgs) {
			photoId++;
			insertImage(story.story_id, image, photoId, false);
		}
	}
	
	private long insertImage(int storyId, Image image, int photoId, boolean isMain) {
		SQLiteDatabase db = mNewsDBHelper.getWritableDatabase();
		ContentValues imageValues = new ContentValues();
		imageValues.put(IMAGE_STORY_ID, storyId);
		imageValues.put(PHOTO_ID, photoId);
		imageValues.put(SMALL_URL, image.smallURL);
		imageValues.put(FULL_URL, image.fullURL);
		imageValues.put(IMAGE_CAPTION, image.imageCaption);
		imageValues.put(IMAGE_CREDITS, image.imageCredits);
		imageValues.put(IS_MAIN, isMain ? 1 : 0);
		return db.insert(IMAGE_URLS_TABLE, FULL_URL, imageValues);
	}
	
	public synchronized void saveThumbnail(NewsItem newsItem, byte[] thumbnailBytes) {
		SQLiteDatabase db = mNewsDBHelper.getWritableDatabase();
		ContentValues thumbValue = new ContentValues();
		thumbValue.put(THUMBNAIL, thumbnailBytes);
		db.update(STORIES_TABLE, thumbValue, STORY_ID_WHERE, whereArgs(newsItem));
	}
	
	public void markAsRead(NewsItem newsItem) {
		//TODO implement this method!
	}
	
	public List<NewsItem> getTopTen() {
		Cursor topTenCursor = getNewsCursor(NewsModel.TOP_NEWS, "10");
		ArrayList<NewsItem> newsItems = new ArrayList<NewsItem>();
		if(topTenCursor.moveToFirst()) {
			while(!topTenCursor.isAfterLast()) {
				newsItems.add(retrieveNewsItem(topTenCursor));
				topTenCursor.moveToNext();
			}
		}
		topTenCursor.close();
		return newsItems;
	}
	
	public Cursor getNewsCursor(int category) {
		return getNewsCursor(category, null);
	}
	
	public Cursor getNewsCursor(int category, String limit) {
		SQLiteDatabase db = mNewsDBHelper.getReadableDatabase();
		String[] fields = new String[] {STORIES_TABLE + "." + STORY_ID, TITLE, BODY, AUTHOR, FEATURED, DESCRIPTION, POST_DATE, LINK, THUMB_URL};
		
        String joinQuery = SQLiteQueryBuilder.buildQueryString(false,
                STORIES_TABLE +", " + CATEGORIES_TABLE,
                fields,
                CATEGORY + "=" + Integer.toString(category) + " AND " + STORIES_TABLE + "." + STORY_ID + "=" + CATEGORIES_TABLE + "." + STORY_ID,
                null,
                null,
                POST_DATE + " DESC, " + STORIES_TABLE + "." + STORY_ID + " DESC ",
                limit);

        return db.rawQuery(joinQuery, null);
	}
	
	public Cursor getBookmarksCursor() {
		SQLiteDatabase db = mNewsDBHelper.getReadableDatabase();
		String[] fields = new String[] {STORY_ID, TITLE, BODY, AUTHOR, FEATURED, DESCRIPTION, POST_DATE, LINK, THUMB_URL};
		Cursor cursor = db.query(STORIES_TABLE, fields, BOOKMARKED + "=1", null, null, null, STORY_ID + " DESC", null);
		return cursor;
	}
	
	public synchronized byte[] retrieveThumbnailBytes(NewsItem newsItem) {
		SQLiteDatabase db = mNewsDBHelper.getReadableDatabase();
		Cursor cursor = db.query(STORIES_TABLE, new String[] {THUMBNAIL} , STORY_ID + "=" + Integer.toString(newsItem.story_id), null, null, null, null, "1");
		
		byte[] imageBytes = null;
		int thumbnail_index = cursor.getColumnIndex(THUMBNAIL);
		if(cursor.moveToFirst()) {
			imageBytes = cursor.getBlob(thumbnail_index);
		}
		cursor.close();
		
		return imageBytes;
	}	
	static NewsItem retrieveNewsItem(Cursor cursor) {
		int story_id_index = cursor.getColumnIndex(STORY_ID);
		int title_index = cursor.getColumnIndex(TITLE);
		int body_index = cursor.getColumnIndex(BODY);
		int author_index = cursor.getColumnIndex(AUTHOR);
		int featured_index = cursor.getColumnIndex(FEATURED);
		int description_index = cursor.getColumnIndex(DESCRIPTION);
		int post_date_index = cursor.getColumnIndex(POST_DATE);
		int link_index = cursor.getColumnIndex(LINK);		
		int thumb_url_index = cursor.getColumnIndex(THUMB_URL);
		
		NewsItem item = new NewsItem();
		item.story_id = cursor.getInt(story_id_index);
		item.title = cursor.getString(title_index);
		item.body = cursor.getString(body_index);
		item.author = cursor.getString(author_index);
		item.featured = (cursor.getInt(featured_index) > 0);
		item.description = cursor.getString(description_index);
		item.postDate = new Date(cursor.getLong(post_date_index));
		item.link = cursor.getString(link_index);
		item.thumbURL = cursor.getString(thumb_url_index);
		
		return item;
	}
	
	static Image retrieveImage(Cursor cursor) {
		int small_url_index = cursor.getColumnIndex(SMALL_URL);
		int full_url_index = cursor.getColumnIndex(FULL_URL);
		int image_caption_index = cursor.getColumnIndex(IMAGE_CAPTION);
		int image_credit_index = cursor.getColumnIndex(IMAGE_CREDITS);
		
		Image image = new Image();
		image.smallURL = cursor.getString(small_url_index);
		image.fullURL = cursor.getString(full_url_index);
		image.imageCaption = cursor.getString(image_caption_index);
		image.imageCredits = cursor.getString(image_credit_index);
		
		return image;
		
	}
	
	public NewsItem retrieveNewsItem(int storyId) {
		SQLiteDatabase db = mNewsDBHelper.getReadableDatabase();
		
		Cursor cursor = db.query(
			STORIES_TABLE, null, STORY_ID_WHERE, new String[]{Integer.toString(storyId)}, 
			null, null, null
		);
		
		cursor.moveToFirst();
		NewsItem newsItem = retrieveNewsItem(cursor);
		cursor.close();
		populateImages(newsItem);
		
		return newsItem;
	}
	
	public synchronized boolean isBookmarked(int storyId) {
		SQLiteDatabase db = mNewsDBHelper.getReadableDatabase();
		
		Cursor cursor = db.query(
			STORIES_TABLE, new String[] {BOOKMARKED}, STORY_ID_WHERE, new String[]{Integer.toString(storyId)}, 
			null, null, null
		);
		
		boolean isBookmarked = false;
		int bookmarked_index = cursor.getColumnIndex(BOOKMARKED);
		if(cursor.moveToFirst()) {
			isBookmarked = (cursor.getInt(bookmarked_index) == 1);
		}
		cursor.close();
	
		return isBookmarked;
	}
	
	void populateImages(NewsItem newsItem) {
		SQLiteDatabase db = mNewsDBHelper.getReadableDatabase();
		
		Cursor cursor = db.query(IMAGE_URLS_TABLE, null, IMAGE_STORY_ID + "=?", whereArgs(newsItem), null, null, PHOTO_ID + " ASC");
		int is_main_index = cursor.getColumnIndex(IS_MAIN);
		
		if(cursor.moveToFirst()) {
			while(!cursor.isAfterLast()) {
				boolean isMain = (cursor.getInt(is_main_index) == 1);
				Image image = retrieveImage(cursor);
				if(isMain) {
					newsItem.img = image;
				} else {
					newsItem.otherImgs.add(image);
				}
				cursor.moveToNext();
			}
		}		
		cursor.close();
	}
	
	private boolean storyExists(int storyId) {
		SQLiteDatabase db = mNewsDBHelper.getReadableDatabase();
		
		Cursor result = db.query(
			STORIES_TABLE, 
			new String[] {STORY_ID}, 
			STORY_ID_WHERE,
			new String[] {Integer.toString(storyId)},
			null, null, null);
	
		boolean storyExists = (result.getCount() > 0);
		result.close();
		return storyExists;
	}

	private static class NewsDatabaseHelper extends SQLiteOpenHelper {
		
		NewsDatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + STORIES_TABLE + " ("
					+ STORY_ID + " INTEGER,"
					+ TITLE + " TEXT,"
					+ BODY + " TEXT,"
					+ AUTHOR + " TEXT,"
					+ FEATURED + " INTEGER,"
					+ DESCRIPTION + " TEXT,"
					+ POST_DATE + " INTEGER,"
					+ LINK + " TEXT,"
					+ THUMB_URL + " TEXT,"
					+ MAIN_IMAGE_ID + " INTEGER,"
					+ THUMBNAIL + " BLOB,"
					+ BOOKMARKED + " BOOLEAN DEFAULT 0 NOT NULL"
				+ ");");
				
			db.execSQL("CREATE INDEX STORY_INDEX ON " + STORIES_TABLE + "(" + STORY_ID + ")");
			
			db.execSQL("CREATE TABLE " + CATEGORIES_TABLE + " ("
					+ STORY_ID + " INTEGER,"
					+ CATEGORY + " INTEGER"
				+ ");");
					
					
			db.execSQL("CREATE TABLE " + IMAGE_URLS_TABLE + " ("
					+ PHOTO_ID + " INTEGER,"
				    + IMAGE_STORY_ID + " INTEGER,"
				    + SMALL_URL + " TEXT,"
				    + FULL_URL + " TEXT,"
				    + IMAGE_CAPTION + " TEXT,"
				    + IMAGE_CREDITS + " TEXT,"
				    + IS_MAIN + " INTEGER"
				+ ");");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// no old versions exists
		}
	}
}

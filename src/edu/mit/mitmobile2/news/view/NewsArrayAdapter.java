package edu.mit.mitmobile2.news.view;

import java.util.List;

import com.squareup.picasso.Picasso;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.news.beans.NewsImage;
import edu.mit.mitmobile2.news.beans.NewsStory;
import edu.mit.mitmobile2.news.net.NewsDownloader;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsArrayAdapter extends ArrayAdapter<NewsStory>{

	NewsDownloader np;
	public NewsArrayAdapter(Context context, int textViewResourceId){
		super(context, textViewResourceId);
		np = NewsDownloader.getInstance(context);
	}
	public NewsArrayAdapter(Context context, int textViewResourceId, List<NewsStory> news) {
		super(context, textViewResourceId, news);
		np = NewsDownloader.getInstance(context);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View view = convertView;
		
		if(this.getCount()>0){
			NewsStory story = (NewsStory) getItem(position);
			if(story!=null){
				TextView ttView = null;
				TextView deckTtView = null;
				if(story.getId()!=null && story.getId().equals("header")){
					view = LayoutInflater.from(getContext()).inflate(R.layout.news_row_header, null);
					TextView ttv = (TextView) view.findViewById(R.id.newsRowMoreTV);
					ttv.setText(story.getDekText());
					ttv.requestLayout();
					return view;
						
				}else if(story.getId()!=null && story.getId().equals("more")){
					view = LayoutInflater.from(getContext()).inflate(R.layout.news_row_more, null);
					TextView ttv = (TextView) view.findViewById(R.id.newsRowMoreTV);
					ttv.setText(story.getDekText());
					ttv.requestLayout();
					return view;
						
				}else if(story.getCategory()!=null && story.getCategory().getId().equals("in_the_media")){
					view = LayoutInflater.from(getContext()).inflate(R.layout.news_row_in_media, null);
					deckTtView = (TextView) view.findViewById(R.id.newsRowDeckInMediaTV);
					NewsImage ni = story.getCoverImage();
					if(ni!=null && !ni.getRepresentations().isEmpty()){
						ImageView imageView = (ImageView) view.findViewById(R.id.newsRowIV);
						DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
						int maxwidth = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 34, metrics);
						int maxheight = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 175, metrics);
						Picasso.with(getContext()).load(ni.getRepresentationBestFitByWidthOrHeight(maxwidth,maxheight).getUrl()).into(imageView);
					}
				}else{
					view = LayoutInflater.from(getContext()).inflate(R.layout.news_row, null);
					
					ttView = (TextView) view.findViewById(R.id.newsRowTV);
					ttView.setText(story.getTitleText());
					ttView.requestLayout();
					deckTtView = (TextView) view.findViewById(R.id.newsRowDeckTV);
					NewsImage ni = story.getCoverImage();
					if(ni!=null && !ni.getRepresentations().isEmpty()){
						ImageView imageView = (ImageView) view.findViewById(R.id.newsRowIV);
						Picasso.with(getContext()).load(ni.getSmallestRepresentationsByDiagonal().getUrl()).into(imageView);
					}
				}
				view.setTag(Integer.valueOf(story.getId()));
				
				deckTtView.setText(story.getDekText());
				deckTtView.requestLayout();
				
			}
		}
		if(view==null){
			view = LayoutInflater.from(getContext()).inflate(R.layout.news_row, null);
		}
		return view;
	}
}

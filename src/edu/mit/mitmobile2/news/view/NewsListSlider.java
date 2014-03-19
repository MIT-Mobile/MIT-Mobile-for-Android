package edu.mit.mitmobile2.news.view;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.OnRefreshListener;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import edu.mit.mitmobile2.LoaderBar;
import edu.mit.mitmobile2.LockingScrollView;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SliderInterface;
import edu.mit.mitmobile2.news.beans.NewsStory;

public class NewsListSlider extends NewsCategoryLoader implements SliderInterface, LoadingScreenListener, OnRefreshListener{

	private View mView;
	private ListView mNewsListView;
	private LoaderBar mLoaderBar;
	private Context context;
	private NewsArrayAdapter newsAdapter;
	private PullToRefreshAttacher mRefreshAttacher;
	private String category_id;
	private  LayoutInflater inflater;
	public NewsListSlider(Context ctx, String category_id){
		super(ctx);
		this.context = ctx;
		this.mLoadingScreenListener = this;
		newsAdapter = new NewsArrayAdapter(ctx,0);
		
		mRefreshAttacher = ((NewsCategoryListActivity)ctx).createPullToRefreshAttacher();
		mRefreshAttacher.setEnabled(false);
		this.refreshData = false;
		this.category_id = category_id;
		
		inflater = LayoutInflater.from(context);
		mView = inflater.inflate(R.layout.news, null);
		
		mLoaderBar = (LoaderBar) mView.findViewById(R.id.newsLoaderBar);
		mLoaderBar.setFailedMessage("Error loading news headlines.");
		mNewsListView = (ListView) mView.findViewById(R.id.newsCategoryLV);
		
		loadStories(category_id,"category",0,20);
	}
	@Override
	public void updateView() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public View getView() {

		
		final Context c = this.context;
		mNewsListView.setOnItemClickListener(
			new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
					NewsStory newsCursor = (NewsStory) listView.getItemAtPosition(position);
					if(newsCursor.getId().equals("more")){
						//removeItem(position);
						setLoadMore(true);
						mLoaderBar.setVisibility(View.VISIBLE);
						loadStories(category_id,"category",position,20);
					}else if(newsCursor.getCategory()!=null && newsCursor.getCategory().getId().equals("in_the_media")){
						nd.showOpenInBrowserDialog(c,newsCursor.getSourceUrl());
						return;
					}else{
						Intent i = new Intent(context, NewsDetailsActivity.class);
						i.putExtra(NewsDetailsActivity.STORY_ID_KEY, newsCursor.getId());
					
						if(newsCursor.getCategory()!=null){
							i.putExtra(NewsDetailsActivity.CATEGORY_ID_KEY, newsCursor.getCategory().getId());
						}
						context.startActivity(i);
					}
				}
			}
		);
		mNewsListView.setAdapter(newsAdapter);
		
		
		mLoaderBar.enableAnimation();
		
		
		mRefreshAttacher.setRefreshableView(mNewsListView, this);
		mRefreshAttacher.setEnabled(true);
		
		return mView;
	}

	@Override
	public void onSelected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public LockingScrollView getVerticalScrollView() {
		return null;
	}
	@Override
	public void onStoriesLoaded() {
		newsAdapter.clear();
		for(int i=0;(i < list.size()); i++){
			newsAdapter.add(list.get(i));
		}
		
		NewsStory nMore = new NewsStory();
		nMore.setId("more");
		nMore.setDek("Load More...");
		newsAdapter.add(nMore);
		
		//mLoaderBar.endLoading();
		//mLoaderBar.getParent().removeView(mLoaderBar);
		//mView.removeView(mLoaderBar);
		mLoaderBar.setVisibility(View.INVISIBLE);
		//((LinearLayout)mLoaderBar.getParent()).removeView(mLoaderBar);
		mRefreshAttacher.setRefreshComplete();
	}
	@Override
	public void onRefreshStarted(View view) {
		this.refreshData = true;
		loadStories(this.category_id,"category",0,20);
	}
}

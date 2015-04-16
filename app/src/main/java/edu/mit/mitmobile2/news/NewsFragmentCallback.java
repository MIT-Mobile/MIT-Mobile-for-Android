package edu.mit.mitmobile2.news;

import edu.mit.mitmobile2.news.models.MITNewsStory;

public interface NewsFragmentCallback {
    void itemClicked(MITNewsStory story);
}

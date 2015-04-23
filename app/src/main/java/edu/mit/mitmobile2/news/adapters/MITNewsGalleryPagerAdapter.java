package edu.mit.mitmobile2.news.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import java.util.List;

import edu.mit.mitmobile2.news.fragments.MITNewsGalleryFragment;
import edu.mit.mitmobile2.news.models.MITNewsGalleryImage;

public class MITNewsGalleryPagerAdapter extends FragmentStatePagerAdapter {

    private int count;
    private MITNewsGalleryFragment[] fragments;
    private List<MITNewsGalleryImage> galleryImages;

    public MITNewsGalleryPagerAdapter(FragmentManager fm, List<MITNewsGalleryImage> galleryImages) {
        super(fm);
        this.count = galleryImages.size();
        this.fragments = new MITNewsGalleryFragment[count];
        this.galleryImages = galleryImages;
    }

    @Override
    public Fragment getItem(int position) {
        if (fragments[position] == null) {
            MITNewsGalleryFragment fragment = MITNewsGalleryFragment.newInstance(galleryImages.get(position));
            fragments[position] = fragment;
            return fragment;
        } else {
            return fragments[position];
        }
    }

    @Override
    public int getCount() {
        return count;
    }
}

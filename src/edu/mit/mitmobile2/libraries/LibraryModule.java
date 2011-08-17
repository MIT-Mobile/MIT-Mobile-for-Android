package edu.mit.mitmobile2.libraries;

import android.app.Activity;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.R;

public class LibraryModule extends Module {

    @Override
    public int getHomeIconResourceId() {
        return R.drawable.home_people;
    }

    @Override
    public String getLongName() {
        return "Library";
    }

    @Override
    public int getMenuIconResourceId() {
        return R.drawable.menu_directory;
    }

    @Override
    public Class<? extends Activity> getModuleHomeActivity() {
        return LibraryActivity.class;
    }

    @Override
    public String getShortName() {
        return "Library";
    }

}

package edu.mit.mitmobile2.id;

import java.util.List;

import android.app.Activity;
import edu.mit.mitmobile2.MITMenuItem;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.R;

public class OpenIDConnectModule extends NewModule {

	@Override
	public List<MITMenuItem> getPrimaryOptions() {
		return null;
	}

	@Override
	public List<MITMenuItem> getSecondaryOptions() {
		return null;
	}

	@Override
	public boolean onItemSelected(Activity activity, String id) {
		return false;
	}

	@Override
	public String getLongName() {
		return "OpenID Connect Test Application";
	}

	@Override
	public String getShortName() {
		return "OpenID Connect";
	}

	@Override
	public Class<? extends Activity> getModuleHomeActivity() {
		return OpenIDConnectActivity.class;
	}

	@Override
	public int getMenuIconResourceId() {
		return R.drawable.home_openidconnect;
	}

	@Override
	public int getHomeIconResourceId() {
		return R.drawable.home_openidconnect;
	}
	

}

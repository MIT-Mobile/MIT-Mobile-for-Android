package edu.mit.mitmobile2.id;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;

public class OpenIDConnectActivity extends NewModuleActivity implements
		OnSharedPreferenceChangeListener {

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
	}

	@Override
	protected NewModule getNewModule() {
		return new OpenIDConnectModule();
	}

	@Override
	protected boolean isScrollable() {
		return true;
	}

	@Override
	protected void onOptionSelected(String optionId) {
	}

	@Override
	protected boolean isModuleHomeActivity() {
		return true;
	}

}

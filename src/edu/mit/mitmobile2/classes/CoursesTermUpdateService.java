package edu.mit.mitmobile2.classes;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class CoursesTermUpdateService extends Service {

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		CoursesDataModel.updateFavoritesForTerm(this);
		return 0;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	

}

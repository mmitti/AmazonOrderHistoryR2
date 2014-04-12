package jp.mmitti.jphistory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

public class InitActivity extends Activity implements ServiceConnection{

	private ListDownloadService mMainService;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.init_main);
		startService(new Intent(this, ListDownloadService.class));
		if(mMainService == null) bindService(new Intent(this, ListDownloadService.class), this, BIND_AUTO_CREATE);
		
	}


	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		mMainService = ((ListDownloadService.Binder)service).getService();
		mMainService.updateList();
		Toast.makeText(getApplication(), "サービスを起動しました", Toast.LENGTH_LONG).show();
	}



	@Override
	public void onServiceDisconnected(ComponentName name) {
		mMainService = null;
	}
	


}

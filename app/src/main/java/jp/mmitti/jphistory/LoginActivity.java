package jp.mmitti.jphistory;

import android.content.ComponentName;import android.content.Intent;import android.content.ServiceConnection;import android.os.IBinder;import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class LoginActivity extends ActionBarActivity  implements ServiceConnection{
    private  AmazonLogin login;

	private ListDownloadService mMainService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);
        		startService(new Intent(this, ListDownloadService.class));
		if(mMainService == null) bindService(new Intent(this, ListDownloadService.class), this, BIND_AUTO_CREATE);

        login = new AmazonLogin((MyWebView)findViewById(R.id.web));
        login.OnLogin = new Runnable() {
            public void run(){
                mMainService.setAuthInfo(login.email, login.pass);
                mMainService.updateList();
                finish();
            }
        };
        login.UserLogin();
    }
    @Override
    	public void onServiceConnected(ComponentName name, IBinder service) {
		mMainService = ((ListDownloadService.Binder)service).getService();

	}



	@Override
	public void onServiceDisconnected(ComponentName name) {
		mMainService = null;
	}



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

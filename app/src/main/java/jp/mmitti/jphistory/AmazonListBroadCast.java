package jp.mmitti.jphistory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AmazonListBroadCast extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		Intent i  = new Intent(arg0, ListActivity.class);
		i.putExtra("AMAZONDAT", arg1.getExtras().getString("DAT"));
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		arg0.startActivity(i);
	}

}

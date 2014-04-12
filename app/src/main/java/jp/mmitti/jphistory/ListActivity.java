package jp.mmitti.jphistory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import jp.mmitti.jphistory.ListDownloadService.Item;
import jp.mmitti.jphistory.ListDownloadService.Data;
import net.arnx.jsonic.JSON;


import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ListActivity extends Activity{

	private ProgressBar progress;
	private LinearLayout mList;
	private ListDownloadService mMainService;
	private InitData mInitData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_main);
		mList = (LinearLayout)findViewById(R.id.list);
		progress = (ProgressBar)findViewById(R.id.progressBar1);
		mInitData = new InitData(new Handler());
		mInitData.start();
		
	}
	
	

	

	
	public void updateList(List<Data> list){
		Toast.makeText(getApplication(), "LOADED", Toast.LENGTH_LONG).show();
		for(Data d : list){
			for(Item i : d.items){
				ViewGroup vg = (ViewGroup) ViewGroup.inflate(getApplication(), R.layout.list_item, null);
				((TextView)vg.findViewById(R.id.textitem)).setText(i.Name);
				((TextView)vg.findViewById(R.id.textdate)).setText(i.OrderDate);
				((ImageView)vg.findViewById(R.id.image)).setImageBitmap(i.Img);
				
				mList.addView(vg);
				TextView tv = new TextView(getApplication());
				tv.setText(i.Name);
				mList.addView(tv);
			}
		}

		progress.setVisibility(View.INVISIBLE);
	}

	

	private class InitData extends MyAsyncTask{

		public InitData(Handler handler) {
			super(handler);
			// TODO 自動生成されたコンストラクター・スタブ
		}
		List<Data> mList;
		@Override
		protected void doBackGround() throws InterruptedException {
			String str = getIntent().getStringExtra("AMAZONDAT");
			Data[] list = JSON.decode(str, Data[].class);
			mList = new LinkedList<ListDownloadService.Data>();
			for(Data d : list){
				mList.add(d);
				for(Item i : d.items){
					try {
						URL img = new URL(i.ImgUrl);
						InputStream is;
						is = img.openStream();
						i.Img = BitmapFactory.decodeStream(is);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
		
		protected void onFinishOnUI(){
			updateList(mList);
		}
	}
	


}

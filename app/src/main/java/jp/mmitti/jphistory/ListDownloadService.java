package jp.mmitti.jphistory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;








import net.arnx.jsonic.JSON;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

public class ListDownloadService extends Service {

	/**
	 * Binder<BR>
	 * サービスをActivityに渡すため使用
	 * @author Masashi
	 */
	public static class Binder extends android.os.Binder{
		private ListDownloadService mService;

		public Binder(final ListDownloadService sokutanService){
			mService = sokutanService;
		}

		public ListDownloadService getService(){
			return mService;
		}

	}
	
	private Binder mBinder;
	@Override
	public IBinder onBind(final Intent arg0){
		return mBinder;
	}

	private MyWebView mWeb;
	@Override
	public void onCreate(){
		super.onCreate();
		mBinder = new Binder(this);
		mDatList = new LinkedList<Data>();
		mWeb = new MyWebView(getApplication());
		mOrderListLoader = new OrderListLoader(new Handler());
	}
	 

	@Override
	public int onStartCommand(final Intent intent, final int flags, final int startId){
		super.onStartCommand(intent, flags, startId);
		return Service.START_STICKY;
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
	}
	
	String mMail = null;
	String mPass = null;

	public void setAuthInfo(String email, String pass){
	    mMail = email;
	    mPass = pass;
    }
	
	private OrderListLoader mOrderListLoader;
	
	public void updateList(){
		mDatList.clear();

		mWeb.onPageLoaded.add( new OnPageLoaded() {
			
			@Override
			public void OnPageLoaded(MyWebView view, String url) {
				if(url.startsWith("https://www.amazon.co.jp/ap/signin")){
						if(mMail == null){
                            Intent i  = new Intent(ListDownloadService.this, LoginActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            return;
                        }
		    		 view.doJS("document.getElementById(\"ap_email\").value=\""+mMail+"\";");
		    		 view.doJS("document.getElementById(\"ap_password\").value=\""+mPass+"\";");
		    		 view.doJS("document.getElementById(\"signInSubmit-input\").click();");
				}
				if(url.startsWith("https://www.amazon.co.jp/gp/css/order-history")){
					if(!mOrderListLoader.isRunning())mOrderListLoader.start();
				}
			}
		});
		mWeb.loadUrl("https://www.amazon.co.jp/gp/css/order-history");
	}
	
	private class OrderListLoader extends MyAsyncTask{

		public OrderListLoader(Handler handler) {
			super(handler);
		}

		@Override
		protected void doBackGround() throws InterruptedException {
			for(int i = 2013; i>2010; i--)getOrderList(i);
			Intent i = new Intent("jp.mmitti.amazonlist.done");
			i.putExtra("DAT", JSON.encode(mDatList));
			sendBroadcast(i);
		}

		
		private void getOrderList(int year){
			String html = mWeb.getHTML("https://www.amazon.co.jp/gp/css/order-history?orderFilter=year-"+year);
			Data dat = new Data();
			dat.items = new LinkedList<Item>();
			dat.year = year;
			Document doc = Jsoup.parse(html);
			Element cso = doc.getElementById("cs-orders");
			Elements elm = cso.getElementsByClass("action-box");
			for(Element e : elm){
				Item i = new Item();
				i.Name = e.getElementsByClass("item-title").get(0).text();
				Elements orderLv = e.getElementsByClass("order-level");
				i.OrderDate = orderLv.get(0).getElementsByTag("h2").get(0).text();
				i.ImgUrl = e.getElementsByClass("image-box").get(0).getElementsByTag("img").attr("src");
				/*try {
					URL img = new URL(e.getElementsByClass("image-box").get(0).getElementsByTag("img").attr("src"));
					InputStream is;
					is = img.openStream();
					i.Img = BitmapFactory.decodeStream(is);
				} catch (IOException e1) {
					// TODO 自動生成された catch ブロック
					e1.printStackTrace();
				}*/
				dat.items.add(i);
			
			}
			mDatList.add(dat);
		}
		
	}
	
	public List<Data> getAmazonItemList(String id, String pass){
		return mDatList;
	}
	
	private List<Data> mDatList;
	

	public class Data{
		public int year;
		public List<Item> items;

	}
	
	public class Item{
		public String ImgUrl;
		public Bitmap Img;
		public String OrderDate;
		public String Name;
	}
	

}

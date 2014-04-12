package jp.mmitti.jphistory;

import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MyWebView extends WebView{
	

	
	public List<OnPageLoaded> onPageLoaded;
	public List<OnHTMLLoaded> onHTMLLoaded;
	public List<OnAlert> onAlert;
	private final static String HTML_ALERT_PREFIX = "html:";
    public MyWebView(Context context) {
		super(context);
        init();
	}
	public MyWebView(Context context, AttributeSet s) {
		super(context, s);
        init();
	}

	private void init(){
	    		onPageLoaded = new LinkedList<OnPageLoaded>();
		onHTMLLoaded = new LinkedList<OnHTMLLoaded>();
		onAlert = new LinkedList<OnAlert>();
		getSettings().setSavePassword(false);
		setWebViewClient(new WebViewClient(){
		      public void onPageFinished (WebView view, String url){
		    	  doJS("alert(\"" + HTML_ALERT_PREFIX + "\"+document.documentElement.outerHTML);");
		    	  for(OnPageLoaded e : onPageLoaded)e.OnPageLoaded((MyWebView)view, url);
		    	/*  if(url.startsWith("https://www.amazon.co.jp/ap/signin")){
		    		  doJS(view,"document.getElementById(\"ap_email\").value=\"masashi-asuka@tempo.ocn.ne.jp\";");
		    	  }
		    	  if(url.startsWith("https://www.amazon.co.jp/gp/css/order-history")){
		    		 if(!isSignin){
		    			 view.setVisibility(View.GONE );
		    			 progress.setVisibility(View.VISIBLE);
		    			 mWeb.loadUrl("https://www.amazon.co.jp/gp/css/order-history?orderFilter=year-2013");
		    			 Toast.makeText(getApplication(), "ログイン完了", Toast.LENGTH_LONG).show();
		    			 isSignin  =true;
		    		 }
		    	  }*/
		      }
		    });
		setWebChromeClient( new WebChromeClient(){
			public boolean onJsAlert(WebView view, String url, String message, JsResult result){
				if(message.startsWith(HTML_ALERT_PREFIX)){
					String html = message.substring(HTML_ALERT_PREFIX.length());
					for(OnHTMLLoaded e : onHTMLLoaded)e.OnHTMLLoaded((MyWebView)view,html, url);
				}
				else for(OnAlert e : onAlert)e.OnAlert((MyWebView)view,message, url);
				result.confirm();
				 return true;
			}
		});
		getSettings().setJavaScriptEnabled(true);
	}
	
	public String getHTML(String url){
		final String ret = "";
		_HTMLReceiver event = new _HTMLReceiver(url);
		onHTMLLoaded.add(event);
		loadUrl(url);//TODO　Handlerでも使ってUIスレで
		while(!event.isLoaded){
			try{Thread.sleep(100);}catch (InterruptedException e){}
		}
		onHTMLLoaded.remove(event);
		return event.html;
	}
	
	private class _HTMLReceiver implements OnHTMLLoaded{
		public _HTMLReceiver(String url){
			mTarget = url;
		}
		public String html;
		private String mTarget;
		public boolean isLoaded = false;
		@Override
		public void OnHTMLLoaded(MyWebView view, String html, String url) {
			if(!mTarget.equals(url))return;
			this.html = html;
			isLoaded = true;
		}
		
	}
	/**
	 * 必ずUIスレッドで呼ぶこと
	 * @param v
	 * @param script
	 */
	@SuppressLint("NewApi")
	public void doJS( String script){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			this.evaluateJavascript(script, null);
		}
		else this.loadUrl("javascript:"+script);
	}

}

package jp.mmitti.jphistory;

import android.webkit.WebView; /**
 * Created by mmitti on 14/04/13.
 */
public class AmazonLogin implements OnPageLoaded, OnAlert{
    private MyWebView mWebView;
    public String email;
    public String pass;
    private boolean isLogin;
    public Runnable OnLogin;
    public AmazonLogin(MyWebView wv){
        mWebView = wv;
        isLogin = false;
    }

    public void UserLogin(){
        if(isLogin)return;
        mWebView.onPageLoaded.add(this);
        mWebView.onAlert.add(this);
        mWebView.loadUrl("https://www.amazon.co.jp/gp/css/order-history");
    }



    public void OnPageLoaded(MyWebView view, String url){
        if(url.startsWith("https://www.amazon.co.jp/ap/signin")){
            mWebView.doJS("document.getElementById(\"signInSubmit-input\").setAttribute('onClick','alert(\"login:id:\"+document.getElementById(\"ap_email\").value+\":pass:\"+document.getElementById(\"ap_password\").value+\":\");');");
        }
        if(url.startsWith("https://www.amazon.co.jp/gp/css/order-history")){
            //LOGIN DONE
            isLogin = true;
            OnLogin.run();
        }
    }

    public void OnAlert(MyWebView view, String msg, String url){
        if(url.startsWith("https://www.amazon.co.jp/ap/signin")){
            if(msg.startsWith("login:")){
                String[] texts = msg.split(":");
                email = texts[2];
                pass = texts[4];
            }
        }
    }
}

package com.example.tscheitrum.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.*;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.HttpResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.Console;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.ConsoleHandler;

public class MainActivity extends AppCompatActivity {

    public WebView mWebView;
    public DhcpInfo d;
    public WifiManager wifii;
    private Button button;
    private Button btn_login;
    private AlertDialog alert;
    private EditText txt_username;
    private EditText txt_password;
    private CheckBox ckbx_auto_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWebView = (WebView) findViewById(R.id.activity_main_webview);
        mWebView.setWebViewClient(new WebViewClient());
        // Enable Javascript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);

        DhcpInfo d;
        final WifiManager wifii;
        wifii = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        d = wifii.getDhcpInfo();
        int gatewayip = d.gateway;

        //mWebView.loadUrl("http://" + String.valueOf(intToIpAddress(wifii.getDhcpInfo().gateway)));
        //mWebView.loadUrl("http://stackoverflow.com");

        final AlertDialog.Builder alert = new AlertDialog.Builder(this)
                .setTitle("DHCP INFO")
                .setMessage(String.valueOf(intToIpAddress(wifii.getDhcpInfo().gateway)))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert);
        alert.show();

        //new loadData().execute(mWebView);
        button = (Button) findViewById(R.id.btn_do_it);
        btn_login = (Button) findViewById(R.id.btn_login);
        txt_username = (EditText) findViewById(R.id.editText);
        txt_password = (EditText) findViewById(R.id.editText2);
        ckbx_auto_login = (CheckBox) findViewById(R.id.ckbx_auto_login);

        final String url = String.valueOf(intToIpAddress(wifii.getDhcpInfo().gateway));
        alert.setMessage("button Clicked!");

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String username = "";
                String password = "";

                String[] credentials = {"admin","admin", "admin", "password", "username", "admin", "username", "password","root2","115wolympicpl"};

                String webpage = "http://" + username + ":" + password + "@" + url;


                String test = "test";


                if (!ckbx_auto_login.isChecked()) {
                    username = txt_username.getEditableText().toString();
                    password = txt_password.getText().toString();
                    webpage = "http://" + username + ":" + password + "@" + url;
                    tryLogin(webpage);
                }else{
                    for(int i=0;i<credentials.length;i=i+2){
                        username = credentials[i];
                        password = credentials[i+1];
                        webpage = "http://" + username + ":" + password + "@" + url;
                        if (tryLogin(webpage)){
                            break;
                        }
                    }
                }
                try {
                    test = new RetrieveFeedTask().execute(webpage).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                alert.setMessage(test);
                alert.show();
                mWebView.loadUrl(test);

            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alert.show();

                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    Document doc = null;
                    String data = "";
                    try {
                        doc = Jsoup.connect("http://stackoverflow.com/questions/10695350/androi-and-jsoup").get();
                        Elements elements = doc.getElementsByClass("post-tag");
                        for (Element element : elements) {
                            data += element.outerHtml();
                            data += "<br/>";
                        }

                        mWebView.loadData(data, "text/html", "UTF-8");


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


            }
        });
    }


    public boolean tryLogin(String url){
            // TODO Auto-generated method stub
            int iHTTPStatus;

            // Making HTTP request
            try {
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpRequest = new HttpGet(url);

                HttpResponse httpResponse = httpClient.execute(httpRequest);
                iHTTPStatus = httpResponse.getStatusLine().getStatusCode();
                if (iHTTPStatus != 200) {
                    // Serve a local page instead...
                    return false;
                } else {

                    mWebView.loadUrl(url);     // Status = 200 so we can loard our desired URL
                }
            }catch(Exception e) {}

        return true;

    }
    public String intToIpAddress(int ipAddress) {
        return ((ipAddress & 0xFF) + "." +
                ((ipAddress >>>= 8) & 0xFF) + "." +
                ((ipAddress >>>= 8) & 0xFF) + "." +
                ((ipAddress >>>= 8) & 0xFF));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}

class WebClient extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }

    @Override
    public void onPageFinished(WebView view, String url)
    {
        // Obvious next step is: document.forms[0].submit()
        view.loadUrl("javascript:alert('hey');");
    }
}

class RetrieveFeedTask extends AsyncTask<String, Void, String> {

    private Exception exception;

    protected String doInBackground(String... webpage) {
        Document doc = null;
        String data = "";
        try {
            doc = Jsoup.connect(webpage[0]).get();
            Elements elements = doc.select("a");
            for (Element element : elements) {
                if (element.attr("href").contains("Wireless")){
                    return element.attr("href");
                }
            }

            //mWebView.loadData(data, "text/html", "UTF-8");


        } catch (IOException e) {
            e.printStackTrace();
        }
        return "failed.";
    }

    protected void onPostExecute(String feed) {
        // TODO: check this.exception
        // TODO: do something with the feed
    }
}
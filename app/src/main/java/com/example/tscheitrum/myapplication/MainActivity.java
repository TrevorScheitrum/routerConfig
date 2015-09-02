package com.example.tscheitrum.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private WebView mWebView;
    public DhcpInfo d;
    public WifiManager wifii;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWebView = (WebView) findViewById(R.id.activity_main_webview);
        // Enable Javascript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        DhcpInfo d;
        WifiManager wifii;
        wifii= (WifiManager) getSystemService(Context.WIFI_SERVICE);
        d = wifii.getDhcpInfo();
        int gatewayip = d.gateway;

        mWebView.loadUrl("http://" + String.valueOf(intToIpAddress(wifii.getDhcpInfo().gateway)));
        // Force links and redirects to open in the WebView instead of in a browser
        mWebView.setWebViewClient(new WebViewClient());

        new AlertDialog.Builder(this)
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
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

        new loadData().execute(mWebView);



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


public class loadData extends AsyncTask<WebView> {
    @Override
    protected Object doInBackground(Object[] params) {
        String data = "";
        Document doc = null;
        try {
            doc = Jsoup.connect("http://stackoverflow.com/questions/10695350/androi-and-jsoup").get();
            Elements elements = doc.getElementsByClass("post-tag");
            for(Element element : elements) {
                data += element.outerHtml();
                data += "<br/>";
            }
            webView.loadData(data, "text/html", "UTF-8");
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected void onProgressUpdate(Integer... progress) {
    }

    protected void onPostExecute(Long result) {
    }
}
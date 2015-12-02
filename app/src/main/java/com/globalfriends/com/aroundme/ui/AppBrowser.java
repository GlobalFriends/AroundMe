package com.globalfriends.com.aroundme.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.globalfriends.com.aroundme.R;

public class AppBrowser extends AppCompatActivity {
    private WebView webView;
    private String mUrl = "www.google.com";
    private ProgressDialog progressBar;
    private Context mContext;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        mContext = this;
        webView = (WebView) findViewById(R.id.webView1);
        webView.getSettings().setJavaScriptEnabled(true);
        progressBar = ProgressDialog.show(this, null, getResources().getString(R.string.please_wait_progress));

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                if (progressBar.isShowing()) {
                    progressBar.dismiss();
                }
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                AlertDialog alertDialog = new AlertDialog.Builder(mContext).setTitle("Error")
                        .setMessage(description)
                        .setPositiveButton(getResources().getString(R.string.action_ok),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        return;
                                    }
                                }).show();
            }
        });

        if (savedInstanceState != null) {
            mUrl = savedInstanceState.getString("URL");
            webView.loadUrl(mUrl);
            return;
        }

        Intent intent = getIntent();
        if (intent != null) {
            mUrl = intent.getStringExtra("URL");
            webView.loadUrl(mUrl);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("URL", mUrl);
        super.onSaveInstanceState(outState);
    }
}

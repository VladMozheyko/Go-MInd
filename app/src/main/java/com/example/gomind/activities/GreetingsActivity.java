package com.example.gomind.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gomind.R;

public class GreetingsActivity extends AppCompatActivity {
    private boolean isPageLoaded = false;
    private final Handler handler = new Handler();
    private static final int CHECK_DELAY = 5000; // Задержка для проверки (в миллисекундах)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greetings);

        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        String pdfUrl = "http://31.129.102.70:8081/authentication/file-system-pdf/user_agreement.pdf";
        String fullUrl = "https://drive.google.com/viewerng/viewer?embedded=true&url=" + Uri.encode(pdfUrl);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                isPageLoaded = false; // Устанавливаем флаг в false при начале загрузки
                Log.d("WebView", "Началась загрузка URL: " + url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                isPageLoaded = true; // Устанавливаем флаг в true, если загрузка завершена
                Log.d("WebView", "Загрузка завершена URL: " + url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.e("WebView Error", "Ошибка загрузки ресурса: " + error.getDescription());
            }
        });

        webView.loadUrl(fullUrl);

        // Проверяем через CHECK_DELAY, загрузилась ли страница
        handler.postDelayed(() -> {
            if (!isPageLoaded) {
                Log.w("WebView", "Страница не загрузилась. Повторная попытка...");
                webView.loadUrl(fullUrl); // Повторная загрузка
            } else {
                Log.d("WebView", "Страница успешно загружена.");
            }
        }, CHECK_DELAY);
    }

    public void onClick(View view) {
        Intent intent = new Intent(this, SplashScreen.class);
        startActivity(intent);
        finish();
    }
}
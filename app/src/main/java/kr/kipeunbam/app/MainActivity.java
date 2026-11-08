package kr.kipeunbam.app;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class MainActivity extends Activity {
    private WebView web;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        web = new WebView(this);
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        web.setBackgroundColor(0xFF0E1322);
        web.addJavascriptInterface(new Bridge(), "KipeunbamApp");
        setContentView(web);
        if (state == null) web.loadUrl("file:///android_asset/index.html");
        else web.restoreState(state);
        if (Build.VERSION.SDK_INT >= 33) requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
    }

    @Override protected void onSaveInstanceState(Bundle out) { super.onSaveInstanceState(out); web.saveState(out); }

    // 화면이 꺼지거나 다른 앱으로 가도 소리가 멈추지 않도록 웹뷰를 일시정지하지 않음
    @Override protected void onPause() { super.onPause(); }

    @Override
    public void onBackPressed() { moveTaskToBack(true); }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PlayService.class));
        web.destroy();
        super.onDestroy();
    }

    class Bridge {
        @JavascriptInterface
        public void playing(boolean on) {
            Intent i = new Intent(MainActivity.this, PlayService.class);
            if (on) {
                if (Build.VERSION.SDK_INT >= 26) startForegroundService(i); else startService(i);
            } else {
                stopService(i);
            }
        }
    }
}

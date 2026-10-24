
package id.kpbi.barongsay;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.ValueCallback;
import android.view.ViewGroup;
import android.view.View;
import android.widget.FrameLayout;

public class MainActivity extends Activity {

    private WebView webView;
    private ValueCallback<Uri[]> fileCallback;
    private static final int FILE_REQUEST = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout container = new FrameLayout(this);
        webView = new WebView(this);

        container.addView(webView,
            new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        );

        setContentView(container);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        webView.setWebViewClient(new WebViewClient());

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(
                WebView view,
                ValueCallback<Uri[]> callback,
                FileChooserParams params
            ) {
                if (fileCallback != null) {
                    fileCallback.onReceiveValue(null);
                }

                fileCallback = callback;

                Intent intent = new Intent(
                    Intent.ACTION_GET_CONTENT
                );
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");

                try {
                    startActivityForResult(
                        Intent.createChooser(
                            intent, "Pilih berkas"
                        ),
                        FILE_REQUEST
                    );
                } catch (Exception e) {
                    fileCallback = null;
                    return false;
                }

                return true;
            }
        });

        webView.loadUrl(
            "https://barongsaisurabaya.github.io/Pendaftaran/"
        );
    }

    @Override
    protected void onActivityResult(
        int requestCode,
        int resultCode,
        Intent data
    ) {
        super.onActivityResult(
            requestCode, resultCode, data
        );

        if (requestCode == FILE_REQUEST
            && fileCallback != null) {

            Uri[] results = null;

            if (resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {
                results = new Uri[] { data.getData() };
            }

            fileCallback.onReceiveValue(results);
            fileCallback = null;
        }
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.stopLoading();
            webView.setWebChromeClient(null);
            webView.setWebViewClient(null);
            webView.destroy();
        }
        super.onDestroy();
    }
}

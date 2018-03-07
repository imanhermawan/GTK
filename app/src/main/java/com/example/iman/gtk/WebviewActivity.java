package com.example.iman.gtk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.iman.gtk.util.AdMob;
import com.example.iman.gtk.util.NetworkHandler;
import com.google.android.gms.ads.AdView;

public class WebviewActivity extends AppCompatActivity {

    private WebView browser = null;
    private ProgressBar loading;
    private AdView mAdView;
    private AdMob admob;
    private static final String TAG = "WebviewActivity";
    private String requestedURL = null;
    private ConnectionTimeoutHandler timeoutHandler = null;
    private static int PAGE_LOAD_PROGRESS = 0;
    public static final String KEY_REQUESTED_URL = "requested_url";
    public static final String CALLBACK_URL = "success";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        initBrowser(savedInstanceState);

        if (savedInstanceState != null) {
            initAdMob();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    initAdMob();
                }
            }, 5000);
        }
    }

    private void initBrowser(Bundle savedInstanceState) {
        loading = findViewById(R.id.progressBar);
        browser = findViewById(R.id.webview);
        WebSettings webSettings = browser.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setDomStorageEnabled(true);
        /*webSettings.setUseWideViewPort(true);*/
        webSettings.setUserAgentString("Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0");
        browser.setWebViewClient(new MyWebViewClient());
        browser.setWebChromeClient(new MyWebChromeClient());

        if (Build.VERSION.SDK_INT >= 19) {
            browser.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT >= 15 && Build.VERSION.SDK_INT < 19) {
            browser.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        if (!TextUtils.isEmpty(getString(R.string.zoom))) {
            browser.getSettings().setSupportZoom(true);
            browser.getSettings().setBuiltInZoomControls(true);
            browser.getSettings().setDisplayZoomControls(false);
        }

        browser.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        requestedURL = getIntent().getStringExtra(KEY_REQUESTED_URL);

        if (savedInstanceState != null && requestedURL != null) {
            browser.restoreState(savedInstanceState);
        } else {
            browser.loadUrl(requestedURL);
        }
    }

    private void initAdMob() {
        mAdView = (AdView) findViewById(R.id.adView);
        admob = new AdMob(this, mAdView);
        admob.requestAdMob();

    }

    @Override
    protected void onResume() {
        super.onResume();
        App.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        App.activityPaused();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        browser.saveState(outState);
    }

    //Custom web view client
    public class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.i(TAG, "Loading url : " + url);

            if (url.contains(CALLBACK_URL)) {
                Log.i(TAG, "Callback url matched... Handling the result");
                Intent intent = new Intent();
                Uri uri = Uri.parse(url);
                intent.setData(uri);
                setResult(RESULT_OK, intent);
                finish();
                return true;
            }
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            timeoutHandler = new ConnectionTimeoutHandler(WebviewActivity.this, view);
            timeoutHandler.execute();
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            super.onPageFinished(view, url);
            loading.setVisibility(View.GONE);

            if (timeoutHandler != null)
                timeoutHandler.cancel(true);

            Log.i(TAG, "Loading url : " + url);
            // Do all your result processing here
            if (url.contains(CALLBACK_URL)) {
                Log.i(TAG, "Callback url matched... Handle result");
                Intent mIntent = new Intent();
                Uri uri = Uri.parse(url);
                mIntent.setData(uri);
                setResult(RESULT_OK, mIntent);
                finish();
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {

            Log.i(TAG, "GOT Page error : code : " + errorCode + " Desc : " + description);
            if (!NetworkHandler.isNetworkAvailable(view.getContext())) {
                Toast.makeText(WebviewActivity.this,
                        getString(R.string.no_internet),
                        Toast.LENGTH_SHORT).show();
                view.loadUrl("file:///android_asset/NoInternet.html");
            }
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
            if (!NetworkHandler.isNetworkAvailable(view.getContext())) {
                Toast.makeText(WebviewActivity.this,
                        getString(R.string.no_internet),
                        Toast.LENGTH_SHORT).show();
                view.loadUrl("file:///android_asset/NoInternet.html");
            }
        }
    }

    //Custom web chrome client
    public class MyWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            PAGE_LOAD_PROGRESS = newProgress;
            //Log.i(TAG, "Page progress [" + PAGE_LOAD_PROGRESS + "%]");
            if (newProgress == 100) {
                loading.setVisibility(View.GONE);
            }
            super.onProgressChanged(view, newProgress);
        }
    }

    private void showError(Context mContext, int errorCode) {
        //Prepare message
        String message = null;
        String title = null;
        if (errorCode == WebViewClient.ERROR_AUTHENTICATION) {
            message = "User authentication failed on server";
            title = "Auth Error";
        } else if (errorCode == WebViewClient.ERROR_TIMEOUT) {
            message = "The server is taking too much time to communicate. Try again later.";
            title = "Connection Timeout";
        } else if (errorCode == WebViewClient.ERROR_TOO_MANY_REQUESTS) {
            message = "Too many requests during this load";
            title = "Too Many Requests";
        } else if (errorCode == WebViewClient.ERROR_UNKNOWN) {
            message = "Generic error";
            title = "Unknown Error";
        } else if (errorCode == WebViewClient.ERROR_BAD_URL) {
            message = "Check entered URL..";
            title = "Malformed URL";
        } else if (errorCode == WebViewClient.ERROR_CONNECT) {
            message = "Failed to connect to the server";
            title = "Connection";
        } else if (errorCode == WebViewClient.ERROR_FAILED_SSL_HANDSHAKE) {
            message = "Failed to perform SSL handshake";
            title = "SSL Handshake Failed";
        } else if (errorCode == WebViewClient.ERROR_HOST_LOOKUP) {
            message = "Server or proxy hostname lookup failed";
            title = "Host Lookup Error";
        } else if (errorCode == WebViewClient.ERROR_PROXY_AUTHENTICATION) {
            message = "User authentication failed on proxy";
            title = "Proxy Auth Error";
        } else if (errorCode == WebViewClient.ERROR_REDIRECT_LOOP) {
            message = "Too many redirects";
            title = "Redirect Loop Error";
        } else if (errorCode == WebViewClient.ERROR_UNSUPPORTED_AUTH_SCHEME) {
            message = "Unsupported authentication scheme (not basic or digest)";
            title = "Auth Scheme Error";
        } else if (errorCode == WebViewClient.ERROR_UNSUPPORTED_SCHEME) {
            message = "Unsupported URI scheme";
            title = "URI Scheme Error";
        } else if (errorCode == WebViewClient.ERROR_FILE) {
            message = "Generic file error";
            title = "File";
        } else if (errorCode == WebViewClient.ERROR_FILE_NOT_FOUND) {
            message = "File not found";
            title = "File";
        } else if (errorCode == WebViewClient.ERROR_IO) {
            message = "The server failed to communicate. Try again later.";
            title = "IO Error";
        }

        if (message != null) {
            new AlertDialog.Builder(mContext)
                    .setMessage(message)
                    .setTitle(title)
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    setResult(RESULT_CANCELED);
                                    //finish();
                                    dialog.dismiss();
                                }
                            }).show();
        }
    }

    public class ConnectionTimeoutHandler extends AsyncTask<Void, Void, String> {

        private static final String PAGE_LOADED = "PAGE_LOADED";
        private static final String CONNECTION_TIMEOUT = "CONNECTION_TIMEOUT";
        private static final long CONNECTION_TIMEOUT_UNIT = 30000L; //1 minute

        private Context mContext = null;
        private WebView webView;
        private Time startTime = new Time();
        private Time currentTime = new Time();
        private Boolean loaded = false;

        public ConnectionTimeoutHandler(Context mContext, WebView webView) {
            this.mContext = mContext;
            this.webView = webView;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.startTime.setToNow();
            WebviewActivity.PAGE_LOAD_PROGRESS = 0;
        }

        @Override
        protected void onPostExecute(String result) {

            if (CONNECTION_TIMEOUT.equalsIgnoreCase(result)) {
                showError(this.mContext, WebViewClient.ERROR_TIMEOUT);

                this.webView.stopLoading();
            } else if (PAGE_LOADED.equalsIgnoreCase(result)) {
                //Toast.makeText(this.mContext, "Page load success", Toast.LENGTH_LONG).show();
            } else {
                //Handle unknown events here
            }
        }

        @Override
        protected String doInBackground(Void... params) {

            while (!loaded) {
                currentTime.setToNow();
                if (WebviewActivity.PAGE_LOAD_PROGRESS != 100
                        && (currentTime.toMillis(true) - startTime.toMillis(true)) > CONNECTION_TIMEOUT_UNIT) {
                    return CONNECTION_TIMEOUT;
                } else if (WebviewActivity.PAGE_LOAD_PROGRESS == 100) {
                    loaded = true;
                }
            }
            return PAGE_LOADED;
        }
    }

    public static void callWebview(Activity activity, String requestedURL, int requestCode) {

        Intent intent = new Intent(activity, WebviewActivity.class);
        intent.putExtra(WebviewActivity.KEY_REQUESTED_URL, requestedURL);
        activity.startActivityForResult(intent, requestCode);
    }
}
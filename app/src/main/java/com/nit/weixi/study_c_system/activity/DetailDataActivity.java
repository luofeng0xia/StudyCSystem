package com.nit.weixi.study_c_system.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.text.TextUtilsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nit.weixi.study_c_system.R;
import com.nit.weixi.study_c_system.tools.DownUtils;
import com.nit.weixi.study_c_system.tools.Tool;

import java.io.File;
import java.io.IOException;

import cz.msebera.android.httpclient.Header;


/**
 * 文章详情activity
 */
@SuppressLint("SetJavaScriptEnabled")
public class DetailDataActivity extends MyBackActivity
        implements SwipeRefreshLayout.OnRefreshListener {
    public static final String EXTRA_LINK = "link";
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_TAG="tag";

    Toolbar mToolbar;

    SwipeRefreshLayout swipeRefreshLayout;
    WebView webview;
    String link;// 接受2种URL,一种是url,另一种文件路径path
    String title;
    String tag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        Tool.setMyTheme(this,tag);
        setContentView(R.layout.acty_detail);
        swipeRefreshLayout= (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        webview= (WebView) findViewById(R.id.webview);

        initLayout();
    }

    protected void initData() {
        link = getIntent().getStringExtra(EXTRA_LINK);
        title = getIntent().getStringExtra(EXTRA_TITLE);
        tag=getIntent().getStringExtra(EXTRA_TAG);
        if (link == null || title == null|| tag == null ) {
            finish();
        }
    }

    protected void initLayout() {
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_blue_light,
                android.R.color.holo_blue_bright,
                android.R.color.holo_blue_light);

        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setAppCacheEnabled(true);
        webview.getSettings().setDatabaseEnabled(true);
        webview.getSettings().setGeolocationEnabled(true);
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        webview.setWebChromeClient(new MyWebChromeClient());
        webview.setWebViewClient(new MyWebViewClient());

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setBackActionBar(title,mToolbar); // 设置返回ActionBar相关
        onRefresh();

    }

    /**
     * 刷新前
     */
    private void onPreRefresh() {
        Tool.setRefreshing(swipeRefreshLayout, true);
    }

    /**
     * 加载界面
     */
    @Override public void onRefresh() {
        if (DownUtils.isURL(link)) {
            //如果是一个url,则发起网络请求
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(link, new AsyncHttpResponseHandler() {
                @Override public void onStart() {
                    //开启刷新
                    onPreRefresh();
                }

                @Override public void onSuccess(int statusCode,
                                                Header[] headers, byte[] data) {
                    //如果成功则把data数据显示
                    load(new String(data));
                }

                @Override public void onFailure(int statusCode,
                                                Header[] headers, byte[] data, Throwable arg3) {
                    //请求数据失败，显示Assets下的404.md
                    try {
                        load(DownUtils.readFile(getAssets().open("404.md")));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override public void onFinish() {
                    //加载完成，去掉加载显示
                    onPostRefresh();
                }

            });
        } else {
            // 如果是路径则开启一个本地异步任务
            new ReadFileTask(link).execute();
        }

    }

    /**
     * 网络加载完毕
     */
    private void onPostRefresh() {
        Tool.setRefreshing(swipeRefreshLayout, false);
    }

    /**
     * 加载网络请求读到的内容
     * @param content 内容
     */
    private void load(String content) {
        try {
            String tpl = DownUtils.readFile(getAssets().open("preview.html"));
            webview.loadDataWithBaseURL("about:blank",
                    tpl.replace("{markdown}", TextUtilsCompat.htmlEncode(content)), "text/html", "UTF-8",
                    null);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 重写WebChromeClient
     */
    class MyWebChromeClient extends WebChromeClient {
        @Override public void onProgressChanged(WebView view, int newProgress) {
        }

        @Override public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            return true;
        }
    }

    /**
     * 重写WebViewClient
     */
    class MyWebViewClient extends WebViewClient {

        @Override public void onLoadResource(WebView view, String url) {
        }

        @Override public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @Override public void onPageStarted(WebView view, String url,
                                            Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override public boolean shouldOverrideKeyEvent(WebView view,
                                                        KeyEvent event) {
            return super.shouldOverrideKeyEvent(view, event);
        }

        @Override public boolean shouldOverrideUrlLoading(WebView view,
                                                          String url) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            return true;
        }

        @Override public void onReceivedError(WebView view, int errorCode,
                                              String description, String failingUrl) {
            // toast("Error Code--->"+errorCode+"   failingUrl--> "+failingUrl);
            // view.loadUrl("file:///android_asset/404.html");
        }
    }

    /**
     * 读取本地文本的异步任务，并且加载页面
     */
    class ReadFileTask extends AsyncTask<Void, Void, String> {
        private String path; //本地文件的路径

        public ReadFileTask(String path) {
            this.path = path;
        }

        /**
         * 准备加载界面，显示刷新样式——转圈
         */
        @Override protected void onPreExecute() {
            onPreRefresh();
        }

        /**
         * 读文件操作
         * @param params params
         * @return 读到的内容
         */
        @Override protected String doInBackground(Void... params) {
            if(new File(this.path).exists()){
                // 文件存在就读内容
                return DownUtils.readFile(this.path);
            }else{
                //不存在就读404
                try {
                    return DownUtils.readFile(getAssets().open("404.md"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }


        /**
         * 加载数据
         * @param result doInBackground 返回的结果
         */
        @Override protected void onPostExecute(String result) {
            load(result);
            onPostRefresh(); //停止加载转圈
        }

    }

}

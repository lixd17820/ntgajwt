package com.ntga.jwt;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;

import com.ntga.dao.GlobalConstant;
import com.ntga.dao.GlobalData;
import com.ydjw.web.RestfulDao;
import com.ydjw.web.RestfulDaoFactory;

public class ZhcxWebActivity extends ActionBarActivity {
    private final String DEBUG_TAG = "ZhcxWebActivity";
    private WebView mWebView;
    final Activity activity = this;
    private String initUrl;
    private String initHost;

    class JsObject {
        @JavascriptInterface
        public String toString() {
            return "injectedObject";
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.zhcx_web);
        mWebView = (WebView) findViewById(R.id.webView1);

        WebSettings webSettings = mWebView.getSettings();// 设置支持JavaScript脚本
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);// 设置可以访问文件
        webSettings.setBuiltInZoomControls(false);// 设置支持缩放
        mWebView.addJavascriptInterface(new JsObject(), "injectedObject");
        RestfulDao rd = RestfulDaoFactory.getDao();
        initHost = rd.getUrl();
        initUrl = initHost + "/ydjw/zhcx/index.jsp";
        String jh = GlobalData.grxx.get(GlobalConstant.JH);
        mWebView.loadUrl(initUrl + "?meid=" + GlobalData.serialNumber
                + "&jybh=" + jh);

        mWebView.setWebViewClient(new WebViewClient() {
            // 设置WebViewClient来辅助WebView处理一些事件
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e(DEBUG_TAG, url);
                Uri uri = Uri.parse(url);
                String u = initHost
                        + "/"
                        + uri.getPath()
                        + (TextUtils.isEmpty(uri.getQuery()) ? "" : "?"
                        + uri.getQuery());
                Log.e(DEBUG_TAG, uri.getHost() + uri.getPath() + uri.getQuery());
                // Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                // startActivity(intent);
                view.loadUrl(u);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            // 设置WebChromeClient来辅助WebView处理JavaScripte对话框
            public boolean onJsAlert(WebView view, String url, String message,
                                     final JsResult result) {
                // 处理javascript中的alert构建一个Builder来显示网页中的对话框
                Builder builder = new Builder(activity);
                builder.setTitle("提示对话框");
                builder.setMessage(message);
                builder.setPositiveButton(android.R.string.ok,
                        new AlertDialog.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // 点击确定按钮之后,继续执行网页中的操作。通过confirm和cancel方法将我们的操作传递给Javascript处理
                                result.confirm();
                            }
                        }
                );
                builder.setCancelable(false);
                builder.create();
                builder.show();
                return true;
            }

            ;

            public boolean onJsConfirm(WebView view, String url,
                                       String message, final JsResult result) {
                // 处理javascript中的confirm
                Builder builder = new Builder(activity);
                builder.setTitle("带选择的对话框");
                builder.setMessage(message);
                builder.setPositiveButton(android.R.string.ok,
                        new AlertDialog.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                result.confirm();// 通过confirm和cancel方法将我们的操作传递给Javascript处理
                            }
                        }
                );
                builder.setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                result.cancel();// 通过confirm和cancel方法将我们的操作传递给Javascript处理
                            }
                        }
                );
                builder.setCancelable(false);
                builder.create();
                builder.show();
                return true;
            }

            ;

            public boolean onJsPrompt(WebView view, String url, String message,
                                      String defaultValue, final JsPromptResult result) {
                // 处理javascript中的prompt，message为网页中对话框的提示内容，
                // defaultValue在没有输入时，默认显示的内容
                final LayoutInflater factory = LayoutInflater.from(activity);
                // 自定义一个带输入的对话框由TextView和EditText构成
                final View dialogview = factory.inflate(
                        R.layout.zhcx_prom_dialog, null);
                ((TextView) dialogview.findViewById(R.id.tv_prom))
                        .setText(message);// 设置TextView对应网页中的提示信息
                ((EditText) dialogview.findViewById(R.id.edit_prom))
                        .setText(defaultValue);// 设置EditText对应网页中的输入框

                Builder builder = new Builder(activity);
                builder.setTitle("带输入的对话框");
                builder.setView(dialogview);
                builder.setPositiveButton(android.R.string.ok,
                        new AlertDialog.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                String value = ((EditText) dialogview
                                        .findViewById(R.id.edit_prom))
                                        .getText().toString();
                                // 点击确定之后，取得输入的值，传给网页处理
                                result.confirm(value);
                                // 通过confirm和cancel方法将我们的操作传递给Javascript处理
                            }
                        }
                );
                builder.setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                result.cancel();
                                // 通过confirm和cancel方法将我们的操作传递给Javascript处理
                            }
                        }
                );
                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        result.cancel();
                        // 通过confirm和cancel方法将我们的操作传递给Javascript处理
                    }
                });
                builder.show();
                return true;
            }

            ;

            public void onProgressChanged(WebView view, int newProgress) {
                // 设置网页加载的进度条
                activity.setProgress(newProgress * 1000);

                // newProgress * 100);
                // super.onProgressChanged(view, newProgress);
            }

            public void onReceivedTitle(WebView view, String title) {
                // 设置应用程序的标题title
                activity.setTitle(title);
                super.onReceivedTitle(view, title);
            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            // 当我们按返回键时 可以通过goBack和goForward方法来设置其前进和后退，但在使用之前我们通过
            // canGoBack和canGoForward方法来检测是否能够后退和前进。
            // mWebView.loadUrl(initUrl);
            mWebView.goBack();// 返回前一个页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.exit_zhcx) {
            finish();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.zhcx_web_menu, menu);
        return true;
    }

}

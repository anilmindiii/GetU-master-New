package getu.app.com.getu.freelancer_side.activity;

        import android.app.Dialog;
        import android.app.ProgressDialog;
        import android.graphics.Bitmap;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.View;
        import android.webkit.WebSettings;
        import android.webkit.WebView;
        import android.webkit.WebViewClient;
        import android.widget.ImageView;
        import android.widget.TextView;

        import getu.app.com.getu.R;
        import getu.app.com.getu.user_side_package.acrivity.TandCActivity;
        import getu.app.com.getu.util.Constant;

public class TandCFreelancerActivity extends AppCompatActivity {
    private WebView webView;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tand_cfreelancer);
        toolbar();

        webView = (WebView) findViewById(R.id.webView);
        dialog = new ProgressDialog(TandCFreelancerActivity.this);
        dialog.setMessage("Loading please wait.....");
        initWebView("http://gnmtechnology.com/themes/documents/TC.pdf");
    }

    public void initWebView(String url) {
        webView.setVisibility(View.VISIBLE);
        webView.setWebViewClient(new TandCFreelancerActivity.MyWebViewClient());
        webView.loadUrl(url);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setAllowContentAccess(true);
        webView.loadUrl("https://docs.google.com/gview?embedded=true&url="+url);
    }

    private class MyWebViewClient extends WebViewClient {

        private MyWebViewClient() {
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            dialog.dismiss();
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            dialog.show();
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageCommitVisible(WebView view, String url) {
            super.onPageCommitVisible(view, url);
        }
    }

    private void toolbar(){
        ImageView iv_for_back = findViewById(R.id.iv_for_back);
        TextView tv_for_tittle = findViewById(R.id.tv_for_tittle);

        iv_for_back.setVisibility(View.VISIBLE);
        tv_for_tittle.setText(R.string.term_and_conditions);

        iv_for_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

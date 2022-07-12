package com.groupphoto.app.presentation

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import com.groupphoto.app.R
import com.groupphoto.app.databinding.ActivityWebViewBinding
import com.groupphoto.app.presentation.base.BaseActivity

class WebViewActivity : BaseActivity<ActivityWebViewBinding>(ActivityWebViewBinding::inflate) {

    var strUrl : String = ""
    var strTitle : String = ""

    override fun init(savedInstanceState: Bundle?) {

        //val toolbar : Toolbar = findViewById(R.id.toolbar);

        /*Objects.requireNonNull(supportActionBar)!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)*/

        strUrl = intent?.getStringExtra("URL").toString()
        strTitle = intent?.getStringExtra("Title").toString()

        val txvTitle : TextView = findViewById(R.id.txv_title);
        // set toolbar as support action bar
        setSupportActionBar(binding.toolbar)

        supportActionBar?.apply {
            txvTitle.setText(strTitle)
            // show back button on toolbar
            // on back button press, it will navigate to parent activity
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setDisplayShowTitleEnabled(false)

        }

        // WebViewClient allows you to handle
        // onPageFinished and override Url loading.
        val webView : WebView = findViewById(R.id.webView)


        webView.webViewClient = WebViewClient()
        webView.loadUrl(strUrl)
        webView.settings.javaScriptEnabled = true

        webView.settings.setSupportZoom(true)
    }

    // if you press Back button this code will work
    override fun onBackPressed() {
        // if your webview can go back it will go back
        if (binding.webView.canGoBack())
            binding.webView.goBack()
        // if your webview cannot go back
        // it will exit the application
        else
            super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
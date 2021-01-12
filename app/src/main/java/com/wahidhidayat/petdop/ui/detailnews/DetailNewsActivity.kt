package com.wahidhidayat.petdop.ui.detailnews

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.wahidhidayat.petdop.R
import com.wahidhidayat.petdop.data.News
import com.wahidhidayat.petdop.data.NewsData
import kotlinx.android.synthetic.main.activity_adoption.toolbar
import kotlinx.android.synthetic.main.activity_detail_news.*
import kotlinx.android.synthetic.main.activity_detail_news.view.*


class DetailNewsActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_NEWS = "extra_news"
    }

    private lateinit var news: News

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_news)

        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val news = intent.getParcelableExtra(EXTRA_NEWS) as NewsData

        pb_news.visibility = View.VISIBLE

        web_view.settings.domStorageEnabled = true
        web_view.settings.loadsImagesAutomatically = true
        web_view.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        web_view.webViewClient = WebViewArticle()
        web_view.loadUrl(news.url)
    }

    class WebViewArticle : WebViewClient() {
        override fun shouldOverrideUrlLoading(
                view: WebView,
                url: String
        ): Boolean {
            view.loadUrl(url)
            return true
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            if (view.pb_news != null) {
                view.pb_news.visibility = View.GONE
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
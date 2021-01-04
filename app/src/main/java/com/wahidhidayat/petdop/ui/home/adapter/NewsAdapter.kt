package com.wahidhidayat.petdop.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wahidhidayat.petdop.R
import com.wahidhidayat.petdop.data.NewsData
import kotlinx.android.synthetic.main.item_news.view.*
import org.ocpsoft.prettytime.PrettyTime
import java.text.SimpleDateFormat
import java.util.*

class NewsAdapter(
    private val listNews: List<NewsData>,
    private val context: Context?
) : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(news: NewsData) {
            with(itemView) {
                Glide.with(itemView.context)
                    .load(news.image)
                    .into(image_news)

                text_news_title.text = news.title
                text_source.text = news.source?.name
                text_news_date.text = "\u2022" + dateTime(news.date)
            }
        }

        private fun dateTime(t: String): String? {
            val prettyTime = PrettyTime(Locale.ENGLISH)
            var time: String? = null
            try {
                val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:", Locale.ENGLISH)
                val date = simpleDateFormat.parse(t)
                time = prettyTime.format(date)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return time
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val news: NewsData = listNews[position]
        holder.bind(news)
    }

    override fun getItemCount(): Int {
        return listNews.size
    }
}
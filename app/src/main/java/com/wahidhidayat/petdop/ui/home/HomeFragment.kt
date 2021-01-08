package com.wahidhidayat.petdop.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.wahidhidayat.petdop.BuildConfig
import com.wahidhidayat.petdop.R
import com.wahidhidayat.petdop.data.News
import com.wahidhidayat.petdop.data.NewsData
import com.wahidhidayat.petdop.data.Post
import com.wahidhidayat.petdop.network.ApiEndpoints
import com.wahidhidayat.petdop.network.NewsApiService
import com.wahidhidayat.petdop.ui.home.adapter.NearbyAdapter
import com.wahidhidayat.petdop.ui.home.adapter.NewsAdapter
import com.wahidhidayat.petdop.ui.home.adapter.PopularAdapter
import com.wahidhidayat.petdop.ui.notification.NotificationActivity
import kotlinx.android.synthetic.main.fragment_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeFragment : Fragment() {
    companion object {
        const val TAG = "HomeFragment"
    }

    private val mDb = FirebaseFirestore.getInstance()
    private val postList: MutableList<Post?> = mutableListOf()
    private val newsList: MutableList<NewsData> = mutableListOf()

    private val nearbyAdapter = NearbyAdapter(postList, context, mDb)
    private val popularAdapter = PopularAdapter(postList, context, mDb)
    private val newsAdapter = NewsAdapter(newsList, context)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadPosts()
        loadNews()

        rv_near_you.apply {
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            adapter = nearbyAdapter
        }

        rv_popular.apply {
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            adapter = popularAdapter
        }

        rv_news.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            adapter = newsAdapter
        }

        image_notification.setOnClickListener {
            startActivity(Intent(activity, NotificationActivity::class.java))
        }
    }

    private fun loadPosts() {
        mDb.collection("posts")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (pb_home != null) {
                        pb_home.visibility = View.GONE
                    }

                    for (doc in task.result!!) {
                        val post: Post = doc.toObject(Post::class.java)
                        postList.add(post)
                        nearbyAdapter.notifyDataSetChanged()
                        popularAdapter.notifyDataSetChanged()
                    }

                } else {
                    Toast.makeText(
                        activity,
                        "Error getting documents: ${task.exception}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(activity, "Error getting documents: $it", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadNews() {
        val service = NewsApiService.buildService(ApiEndpoints::class.java)
        val call = service.getNews("kucing-anjing", BuildConfig.NEWS_API_KEY)
        call.enqueue(object : Callback<News> {
            override fun onResponse(call: Call<News>, response: Response<News>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        newsList.addAll(body.articles)
                        newsAdapter.notifyDataSetChanged()
                    }
                } else {
                    Toast.makeText(
                        activity,
                        "Something went wrong, try again later",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<News>, t: Throwable?) {
                Toast.makeText(activity, "Error getting news: $t", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
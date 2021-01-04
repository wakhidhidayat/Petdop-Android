package com.wahidhidayat.petdop.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.wahidhidayat.petdop.R
import com.wahidhidayat.petdop.data.Post
import com.wahidhidayat.petdop.ui.home.adapter.NearbyAdapter
import com.wahidhidayat.petdop.ui.home.adapter.PopularAdapter
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {
    companion object {
        const val TAG = "HomeFragment"
    }
    private val mDb = FirebaseFirestore.getInstance()
    private val postList: MutableList<Post?> = mutableListOf()

    private val nearbyAdapter = NearbyAdapter(postList, context, mDb)
    private val popularAdapter = PopularAdapter(postList, context, mDb)

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
    }

    private fun loadPosts() {
        mDb.collection("posts")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if(pb_home != null) {
                        pb_home.visibility = View.GONE
                    }

                    for (doc in task.result!!) {
                        val post: Post = doc.toObject(Post::class.java)
                        postList.add(post)
                        nearbyAdapter.notifyDataSetChanged()
                        popularAdapter.notifyDataSetChanged()
                    }

                } else {
                    Toast.makeText(activity, "Error getting documents: ${task.exception}", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(activity, "Error getting documents: $it", Toast.LENGTH_SHORT).show()
            }
    }
}
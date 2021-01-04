package com.wahidhidayat.petdop.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.wahidhidayat.petdop.R
import com.wahidhidayat.petdop.data.Post
import com.wahidhidayat.petdop.ui.home.adapter.NearbyAdapter
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {
    private val mDb = FirebaseFirestore.getInstance()
    private lateinit var nearbyAdapter: NearbyAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadNearbyList()
    }

    private fun loadNearbyList() {
        mDb.collection("posts")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val postList: MutableList<Post?> = ArrayList()
                    for (doc in task.result!!) {
                        val post: Post = doc.toObject(Post::class.java)
                        postList.add(post)
                    }
                    nearbyAdapter = NearbyAdapter(
                        postList,
                        context,
                        mDb
                    )
                    rv_near_you.apply {
                        setHasFixedSize(true)
                        layoutManager =
                            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                        adapter = nearbyAdapter
                    }
                } else {
                    Log.d("HomeFragment", "Error getting documents: ", task.exception)
                }
            }
    }
}
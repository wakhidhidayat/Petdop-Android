package com.wahidhidayat.petdop.ui.notification

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.wahidhidayat.petdop.R
import com.wahidhidayat.petdop.data.Adoption
import kotlinx.android.synthetic.main.activity_notification.*

class NotificationActivity : AppCompatActivity() {

    private val mUser = FirebaseAuth.getInstance().currentUser
    private val mAdoptionReference = FirebaseFirestore.getInstance().collection("adoptions")
    private val mUserEmail = mUser!!.email

    private val adoptionSentList: MutableList<Adoption?> = mutableListOf()
    private val adoptionReceivedList: MutableList<Adoption?> = mutableListOf()

    private val mSentAdapter =
        AdoptionSentAdapter(adoptionSentList, this, FirebaseFirestore.getInstance())
    private val mReceivedAdapter =
        AdoptionReceivedAdapter(adoptionReceivedList, this, FirebaseFirestore.getInstance())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        loadAdoptions()

        swipe_notification.setOnRefreshListener {
            adoptionReceivedList.clear()
            adoptionSentList.clear()
            loadAdoptions()
        }

        rv_adoption_sent.apply {
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(this@NotificationActivity, LinearLayoutManager.VERTICAL, false)
            adapter = mSentAdapter
        }

        rv_adoption_received.apply {
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(this@NotificationActivity, LinearLayoutManager.VERTICAL, false)
            adapter = mReceivedAdapter
        }
    }

    private fun loadAdoptions() {
        swipe_notification.isRefreshing = true
        mAdoptionReference.get()
            .addOnCompleteListener {
                swipe_notification.isRefreshing = false
                for (document in it.result!!) {
                    val adoption: Adoption = document.toObject(Adoption::class.java)
                    if (adoption.user.email == mUserEmail.toString()) {
                        adoptionSentList.add(adoption)
                        mSentAdapter.notifyDataSetChanged()
                    }

                    if (adoption.post.author == mUserEmail.toString()) {
                        adoptionReceivedList.add(adoption)
                        mReceivedAdapter.notifyDataSetChanged()
                    }
                }
                Log.d("adoptions", adoptionReceivedList.toString())
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
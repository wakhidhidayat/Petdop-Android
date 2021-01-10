package com.wahidhidayat.petdop.ui.notification

import android.os.Bundle
import android.util.Log
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

    private val mSentAdapter = AdoptionSentAdapter(adoptionSentList, this, FirebaseFirestore.getInstance())
    private val mReceivedAdapter = AdoptionReceivedAdapter(adoptionReceivedList, this, FirebaseFirestore.getInstance())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        setSupportActionBar(toolbar)

        loadAdoptions()

        rv_adoption_sent.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@NotificationActivity, LinearLayoutManager.VERTICAL, false)
            adapter = mSentAdapter
        }

        rv_adoption_received.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@NotificationActivity, LinearLayoutManager.VERTICAL, false)
            adapter = mReceivedAdapter
        }
    }

    private fun loadAdoptions() {
        mAdoptionReference.get()
                .addOnCompleteListener {
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
}
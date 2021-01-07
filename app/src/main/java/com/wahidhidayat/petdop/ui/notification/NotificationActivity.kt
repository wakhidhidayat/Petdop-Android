package com.wahidhidayat.petdop.ui.notification

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.wahidhidayat.petdop.R
import com.wahidhidayat.petdop.data.Adoption
import com.wahidhidayat.petdop.data.User

class NotificationActivity : AppCompatActivity() {

    private val mUser = FirebaseAuth.getInstance().currentUser
    private val mUserReference = FirebaseFirestore.getInstance().collection("users")
    private val mAdoptionReference = FirebaseFirestore.getInstance().collection("adoptions")
    private val mUserEmail = mUser!!.email

    private val adoptionSentList: MutableList<Adoption> = mutableListOf()
    private val adoptionReceivedList: MutableList<Adoption> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        mAdoptionReference.get()
                .addOnCompleteListener {
                    for(document in it.result!!) {
                        val adoption: Adoption = document.toObject(Adoption::class.java)
                        if(adoption.user.email == mUserEmail.toString()) {
                            adoptionSentList.add(adoption)
                        } else if(adoption.post.author == mUserEmail.toString()) {
                            adoptionReceivedList.add(adoption)
                        }
                    }
                    Log.d("adoptions", adoptionReceivedList.toString())
                }
    }
}
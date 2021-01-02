package com.wahidhidayat.petdop.ui.account

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.wahidhidayat.petdop.R
import com.wahidhidayat.petdop.ui.login.LoginActivity.Companion.TAG
import kotlinx.android.synthetic.main.fragment_account.*

private val mUser = FirebaseAuth.getInstance().currentUser
private val mDb = FirebaseFirestore.getInstance()

class AccountFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userRef = mDb.document("users/${mUser?.email.toString()}")
        userRef.get()
            .addOnSuccessListener { documentSnapshot ->
                getUser(
                    documentSnapshot.getString("name"),
                    documentSnapshot.getString("email"),
                    documentSnapshot.getString("phone"),
                    documentSnapshot.getString("address"),
                    documentSnapshot.getString("avatar")
                )
                pb_account.visibility = View.GONE
                Log.d("AccountFragment", "DocumentSnapshot data: ${documentSnapshot.getString("name")}.")
            }
            .addOnFailureListener { exception ->
                Log.d("AccountFragment", "get failed with ", exception)
            }
    }

    private fun getUser(name: String?, email: String?, phone: String?, address: String?, avatar: String?) {
        et_name.setText(name)
        et_address.setText(address)
        et_phone.setText(phone)
        et_email.setText(email)

        if (avatar != null) {
            Glide.with(this)
                .load(avatar)
                .into(image_avatar)
        }
    }
}
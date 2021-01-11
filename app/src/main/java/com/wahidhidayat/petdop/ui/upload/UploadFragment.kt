package com.wahidhidayat.petdop.ui.upload

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.wahidhidayat.petdop.R
import com.wahidhidayat.petdop.data.Post
import kotlinx.android.synthetic.main.fragment_upload.*

class UploadFragment : Fragment() {
    private val mPostRef = FirebaseFirestore.getInstance().collection("posts")
    private val mUserRef = FirebaseFirestore.getInstance().collection("users")
    private val mUser = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upload, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = mPostRef.document().id

        btn_upload.setOnClickListener {
            val categoryId: Int = radioGroup1.checkedRadioButtonId
            val tervaksinId: Int = radioGroup2.checkedRadioButtonId
            val genderId: Int = radioGroup.checkedRadioButtonId

            if (genderId != -1 && categoryId != -1 && tervaksinId != -1) {
                val gender = view.findViewById(genderId) as RadioButton
                val genderName = gender.text.toString()

                val category = view.findViewById(categoryId) as RadioButton
                val categoryName = category.text.toString()

                var tervaksinValue = false
                val tervaksin = view.findViewById(tervaksinId) as RadioButton
                val tervaksinString = tervaksin.text.toString()

                if (tervaksinString == "Tervaksin") {
                    tervaksinValue = true
                }

                mUserRef.document(mUser?.email.toString()).get()
                        .addOnSuccessListener {
                            val address = it.getString("address").toString()
                            val phone = it.getString("phone").toString()
                            val post = Post(id, address, phone, et_age.text.toString().toInt(), mUser?.email.toString(), categoryName, et_description.text.toString(), genderName, et_name.text.toString(), listOf(), et_reason.text.toString(), "Menunggu", tervaksinValue, et_weight.text.toString().toDouble())
                            Log.d("postData", post.toString())
                            mPostRef.document(id).set(post)
                        }
            } else {
                Toast.makeText(activity, "Mohon isi form dengan lengkap!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
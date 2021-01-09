package com.wahidhidayat.petdop.ui.adoption

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.wahidhidayat.petdop.R
import com.wahidhidayat.petdop.data.Adoption
import com.wahidhidayat.petdop.data.Post
import com.wahidhidayat.petdop.data.User
import com.wahidhidayat.petdop.ui.detailpost.DetailPostActivity
import kotlinx.android.synthetic.main.activity_adoption.*

class AdoptionActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_POST = "extra_post"
    }

    private val mUser = FirebaseAuth.getInstance().currentUser
    private val mUserReference = FirebaseFirestore.getInstance().collection("users")
    private val mAdoptionReference = FirebaseFirestore.getInstance().collection("adoptions")
    private val mUserEmail = mUser!!.email


    private lateinit var post: Post

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adoption)

        post = intent.getParcelableExtra(DetailPostActivity.EXTRA_POST) as Post

        getUser()

        btn_ajukan.setOnClickListener {
            val user = User(
                mUserEmail.toString(),
                et_name.text.toString(),
                et_phone.text.toString(),
                et_address.text.toString(),
                ""
            )
            val adoption =
                Adoption(et_cage.text.toString(), et_home.text.toString(), "Menunggu", post, user)
            mAdoptionReference.document().set(adoption)
        }
    }

    private fun getUser() {
        mUserReference.document(mUserEmail!!).get()
            .addOnSuccessListener {
                et_address.setText(it.getString("address"))
                et_phone.setText(it.getString("phone"))
                et_name.setText(it.getString("name"))
            }
    }
}
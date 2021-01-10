package com.wahidhidayat.petdop.ui.detailadoption

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.wahidhidayat.petdop.R
import com.wahidhidayat.petdop.data.Adoption
import kotlinx.android.synthetic.main.activity_adoption_sent.*

class AdoptionSentActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_ADOPTION = "extra_adoption"
    }

    private val mAdoptionRef = FirebaseFirestore.getInstance().collection("adoptions")
    private val mUserRef = FirebaseFirestore.getInstance().collection("users")
    private lateinit var adoption: Adoption

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adoption_sent)

        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        adoption = intent.getParcelableExtra(EXTRA_ADOPTION) as Adoption

        text_name.text = adoption.user.name
        text_pet_name.text = adoption.post.name
        text_address.text = adoption.post.address

        mUserRef.document(adoption.post.author.toString()).get()
                .addOnSuccessListener {
                    text_phone.text = it.getString("phone")
                }

        btn_cancel.setOnClickListener {
            mAdoptionRef.document(adoption.id).update("status", "Dibatalkan")
            Toast.makeText(this, "Berhasil membatalkan pengajuan adopsi!", Toast.LENGTH_SHORT).show()
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
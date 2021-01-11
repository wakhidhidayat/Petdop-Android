package com.wahidhidayat.petdop.ui.detailadoption

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.wahidhidayat.petdop.R
import com.wahidhidayat.petdop.data.Adoption
import kotlinx.android.synthetic.main.activity_adoption_received.*
import kotlinx.android.synthetic.main.activity_adoption_received.text_address
import kotlinx.android.synthetic.main.activity_adoption_received.text_name
import kotlinx.android.synthetic.main.activity_adoption_received.toolbar
import kotlinx.android.synthetic.main.activity_detail_post.*

class AdoptionReceivedActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_ADOPTION = "extra_adoption"
    }

    private val mAdoptionRef = FirebaseFirestore.getInstance().collection("adoptions")
    private lateinit var adoption: Adoption

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adoption_received)

        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        adoption = intent.getParcelableExtra(EXTRA_ADOPTION) as Adoption

        text_address.text = adoption.user.address
        text_cage.text = adoption.cage
        text_home.text = adoption.homeSpecification
        text_name.text = adoption.user.name
        text_pet_name.text = adoption.post.name
        text_phone.text = adoption.user.phone

        btn_approve.setOnClickListener {
            mAdoptionRef.document(adoption.id).update("status", "Diterima")
            Toast.makeText(this, "Berhasil menerima pengajuan adopsi!", Toast.LENGTH_SHORT).show()
        }

        btn_reject.setOnClickListener {
            mAdoptionRef.document(adoption.id).update("status", "Ditolak")
            Toast.makeText(this, "Berhasil menolak pengajuan adopsi!", Toast.LENGTH_SHORT).show()
        }

        image_message.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://wa.me/${adoption.user.phone}")))
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
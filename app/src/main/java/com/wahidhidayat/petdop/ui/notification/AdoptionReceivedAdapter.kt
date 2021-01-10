package com.wahidhidayat.petdop.ui.notification

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.wahidhidayat.petdop.R
import com.wahidhidayat.petdop.data.Adoption
import com.wahidhidayat.petdop.ui.detailadoption.AdoptionReceivedActivity
import kotlinx.android.synthetic.main.item_adoption_received.view.*

class AdoptionReceivedAdapter(
        private val mList: MutableList<Adoption?>,
        private val mContext: Context?,
        private val mDb: FirebaseFirestore
) : RecyclerView.Adapter<AdoptionReceivedAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(adoption: Adoption) {
            itemView.text_name.text = adoption.user.name
            itemView.text_adoption.text = "Mengajukan permohonan mengadopsi ${adoption.post.name}"
            itemView.text_status.text = adoption.status
            itemView.cv_notification.setOnClickListener {
                val intent = Intent(itemView.context, AdoptionReceivedActivity::class.java)
                intent.putExtra(AdoptionReceivedActivity.EXTRA_ADOPTION, adoption)
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_adoption_received, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val adoption: Adoption? = mList[position]
        if (adoption != null) {
            holder.bind(adoption)
        }
    }
}
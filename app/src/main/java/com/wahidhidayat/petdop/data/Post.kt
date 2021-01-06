package com.wahidhidayat.petdop.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Post(
        val id: String,
        val address: String,
        val age: Int,
        val author: String?,
        val category: String?,
        val description: String,
        val gender: String,
        val name: String,
        val photos: List<String>,
        val reason: String,
        val status: String,
        val tervaksin: Boolean,
        val weight: Double
) : Parcelable {
    constructor() : this("", "", 0, "", "", "", "", "", listOf<String>(), "", "", true, 0.0)
}
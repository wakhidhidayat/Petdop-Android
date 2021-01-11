package com.wahidhidayat.petdop.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val email: String,
    val name: String,
    val phone: String?,
    val address: String?,
    val avatar: String
) : Parcelable {
    constructor() : this("", "", "", "", "")
}
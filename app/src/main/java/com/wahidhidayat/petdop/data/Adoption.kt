package com.wahidhidayat.petdop.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Adoption(
        val id: String,
        val cage: String,
        val homeSpecification: String,
        val status: String,
        val post: Post,
        val user: User
) : Parcelable {
    constructor() : this("", "", "", "", Post(), User())
}
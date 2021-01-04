package com.wahidhidayat.petdop.data

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class News(
    @SerializedName("articles")
    @Expose
    val articles: List<NewsData>
) : Parcelable {
    constructor() : this(mutableListOf())
}

@Parcelize
data class NewsData(
    @SerializedName("title")
    @Expose
    val title: String,

    @SerializedName("urlToImage")
    @Expose
    val image: String,

    @SerializedName("publishedAt")
    @Expose
    val date: String,

    @SerializedName("url")
    @Expose
    val url: String,

    @SerializedName("source")
    @Expose
    val source: Source?
) : Parcelable {
    constructor() : this("", "", "", "", null)
}

@Parcelize
data class Source(
    @SerializedName("name")
    @Expose
    val name: String
) : Parcelable {
    constructor() : this("")
}
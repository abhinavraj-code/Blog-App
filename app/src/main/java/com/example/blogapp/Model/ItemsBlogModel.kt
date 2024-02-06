package com.example.blogapp.Model

import android.os.Parcel
import android.os.Parcelable


data class ItemsBlogModel(
    val heading: String? ="null",
    val userName: String? ="null",
    val date: String? ="null",
    val post: String? ="null",
    var likeCount: Int =0,
    val profileImage: String? ="null",
    var isSaved: Boolean= false,
    var postId: String?="null",
    val likeBy: MutableList<String>?=null
) :Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readByte()!=0.toByte(),
       parcel.readString()?: "null"
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(heading)
        parcel.writeString(userName)
        parcel.writeString(date)
        parcel.writeString(post)
        parcel.writeInt(likeCount)
        parcel.writeString(profileImage)
        parcel.writeString(postId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ItemsBlogModel> {
        override fun createFromParcel(parcel: Parcel): ItemsBlogModel {
            return ItemsBlogModel(parcel)
        }

        override fun newArray(size: Int): Array<ItemsBlogModel?> {
            return arrayOfNulls(size)
        }
    }

}
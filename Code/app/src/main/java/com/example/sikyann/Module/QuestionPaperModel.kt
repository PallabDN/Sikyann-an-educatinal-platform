package com.example.sikyann.Module

import android.os.Parcel
import android.os.Parcelable

data class QuestionPaperModel (
    var title:String? = "",
    var subtitle:String? = "",
    var uniqueId:String? = "",
    var imageList: List<String>? = emptyList(),
    var totalMark:String? = "",
    var date:String? = ""
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(subtitle)
        parcel.writeString(uniqueId)
        parcel.writeStringList(imageList)
        parcel.writeString(date)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<QuestionPaperModel> {
        override fun createFromParcel(parcel: Parcel): QuestionPaperModel {
            return QuestionPaperModel(parcel)
        }

        override fun newArray(size: Int): Array<QuestionPaperModel?> {
            return arrayOfNulls(size)
        }
    }
}
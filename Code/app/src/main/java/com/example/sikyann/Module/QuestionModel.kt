package com.example.sikyann.Module

import android.os.Parcel
import android.os.Parcelable

data class QuestionModel (
    var question:String? = "",
    var mark:String? = "",
    var option: List<String>? = emptyList(),
    var image: String? = "",
    var answer:String? = ""
) :Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(question)
        parcel.writeString(mark)
        parcel.writeStringList(option)
        parcel.writeString(image)
        parcel.writeString(answer)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<QuestionModel> {
        override fun createFromParcel(parcel: Parcel): QuestionModel {
            return QuestionModel(parcel)
        }

        override fun newArray(size: Int): Array<QuestionModel?> {
            return arrayOfNulls(size)
        }
    }
}
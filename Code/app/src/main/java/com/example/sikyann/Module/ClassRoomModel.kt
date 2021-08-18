package com.example.sikyann.Module

import android.os.Parcel
import android.os.Parcelable

data class ClassRoomModel (
    var title:String? = "",
    var subtitle:String? = "",
    var description:String? = "",
    var uniqueId:String? = "",
    var date:String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(subtitle)
        parcel.writeString(description)
        parcel.writeString(uniqueId)
        parcel.writeString(date)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ClassRoomModel> {
        override fun createFromParcel(parcel: Parcel): ClassRoomModel {
            return ClassRoomModel(parcel)
        }

        override fun newArray(size: Int): Array<ClassRoomModel?> {
            return arrayOfNulls(size)
        }
    }
}
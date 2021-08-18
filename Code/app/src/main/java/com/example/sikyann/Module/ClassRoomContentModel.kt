package com.example.sikyann.Module

import android.icu.text.CaseMap
import android.os.Parcel
import android.os.Parcelable

data class ClassRoomContentModel (
    val title: String? = "",
    val description:String? = "",
    val link: String? ="",
    val resource: String? = "",
    val resourceType: String? = "",
    val date: String? = "",
    val time:String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(link)
        parcel.writeString(resource)
        parcel.writeString(resourceType)
        parcel.writeString(date)
        parcel.writeString(time)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ClassRoomContentModel> {
        override fun createFromParcel(parcel: Parcel): ClassRoomContentModel {
            return ClassRoomContentModel(parcel)
        }

        override fun newArray(size: Int): Array<ClassRoomContentModel?> {
            return arrayOfNulls(size)
        }
    }
}
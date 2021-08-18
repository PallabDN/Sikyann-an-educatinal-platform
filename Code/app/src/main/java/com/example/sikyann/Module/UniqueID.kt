package com.example.sikyann.Module

import android.os.Parcel
import android.os.Parcelable

data class UniqueID(
    var id: String? = "",
    var result: List<String>? = emptyList(),
    var date:String? = "",
    var time: String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.createStringArrayList(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeStringList(result)
        parcel.writeString(date)
        parcel.writeString(time)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UniqueID> {
        override fun createFromParcel(parcel: Parcel): UniqueID {
            return UniqueID(parcel)
        }

        override fun newArray(size: Int): Array<UniqueID?> {
            return arrayOfNulls(size)
        }
    }
}
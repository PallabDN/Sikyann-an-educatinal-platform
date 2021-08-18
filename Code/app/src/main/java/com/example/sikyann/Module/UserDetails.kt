package com.example.sikyann.Module

import android.os.Parcel
import android.os.Parcelable

data class UserDetails(
    var name: String? = "",
    var father:String? = "",
    var gmail: String? = "",
    var address: String? = "",
    var mobile: String? = "",
    var occupation: String? = ""
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(father)
        parcel.writeString(gmail)
        parcel.writeString(address)
        parcel.writeString(mobile)
        parcel.writeString(occupation)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserDetails> {
        override fun createFromParcel(parcel: Parcel): UserDetails {
            return UserDetails(parcel)
        }

        override fun newArray(size: Int): Array<UserDetails?> {
            return arrayOfNulls(size)
        }
    }
}
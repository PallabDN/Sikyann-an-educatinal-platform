package com.example.sikyann.Module

import android.os.Parcel
import android.os.Parcelable

data class StudentClassRoomDataModel (
    val date:String? = "",
    val studetID:String? = "",
    val classRoomPath:String? = ""
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(date)
        parcel.writeString(studetID)
        parcel.writeString(classRoomPath)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<StudentClassRoomDataModel> {
        override fun createFromParcel(parcel: Parcel): StudentClassRoomDataModel {
            return StudentClassRoomDataModel(parcel)
        }

        override fun newArray(size: Int): Array<StudentClassRoomDataModel?> {
            return arrayOfNulls(size)
        }
    }
}
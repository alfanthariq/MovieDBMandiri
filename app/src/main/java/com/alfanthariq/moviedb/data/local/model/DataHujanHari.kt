package com.alfanthariq.moviedb.data.local.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class DataHujanHari(@SerializedName("nilai")
                         var nilai: String? = null,
                         @SerializedName("jam")
                         var jam: Int = 0,
                         @SerializedName("nama_pos")
                         var namaPos: String? = null) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!
    )

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(nilai)
        dest?.writeInt(jam)
        dest?.writeString(namaPos)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DataHujanHari> {
        override fun createFromParcel(parcel: Parcel): DataHujanHari {
            return DataHujanHari(parcel)
        }

        override fun newArray(size: Int): Array<DataHujanHari?> {
            return arrayOfNulls(size)
        }
    }
}
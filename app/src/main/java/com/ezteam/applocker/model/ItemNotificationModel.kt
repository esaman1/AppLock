package com.ezteam.applocker.model

import android.app.PendingIntent
import android.os.Parcel
import android.os.Parcelable

data class ItemNotificationModel(
    val resId: Int = 0,
    val title: String? = "",
    var message: String? = "",
    val time: Long = 0,
    val packageName: String? = "",
    val key: String? = "",
    val notificationId: Int = 0,
    val contentIntent: PendingIntent? = null,
    val isClearable: Boolean = false,
    var isParent: Boolean = true,
    var isAds: Boolean = false,
    var isMore: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readParcelable(PendingIntent::class.java.classLoader),
        parcel.readByte()!=0.toByte(),
        parcel.readByte()!=0.toByte(),
        parcel.readByte()!=0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(resId)
        parcel.writeString(title)
        parcel.writeString(message)
        parcel.writeLong(time)
        parcel.writeString(packageName)
        parcel.writeString(key)
        parcel.writeInt(notificationId)
        parcel.writeParcelable(contentIntent, flags)
        parcel.writeByte(if (isClearable) 1 else 0)
        parcel.writeByte(if (isParent) 1 else 0)
        parcel.writeByte(if (isAds) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ItemNotificationModel> {
        override fun createFromParcel(parcel: Parcel): ItemNotificationModel {
            return ItemNotificationModel(parcel)
        }

        override fun newArray(size: Int): Array<ItemNotificationModel?> {
            return arrayOfNulls(size)
        }
    }
}





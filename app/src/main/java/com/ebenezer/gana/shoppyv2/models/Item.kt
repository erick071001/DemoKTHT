package com.ebenezer.gana.shoppyv2.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Item(
    val id: Long = 0,
    val productID: Long = 0,
    val orderID: Long = 0,
    val cartID: Long = 0,
    val quantity: Long = 0
) : Parcelable
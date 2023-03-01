package com.ebenezer.gana.shoppyv2.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Cart(
    val product: Product,
    val item: Item
) : Parcelable
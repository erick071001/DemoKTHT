package com.ebenezer.gana.shoppyv2.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Products(
    val user_id:String = "1",
    val user_name:String = "PeLong2k5",
    val product_name: String = "",
    val price:String = "",
    val description:String = "",
    val totalQuantity:String = "",
    val imageURL:String = "",
    var productId:String = "",
    val category:String = "1"

):Parcelable
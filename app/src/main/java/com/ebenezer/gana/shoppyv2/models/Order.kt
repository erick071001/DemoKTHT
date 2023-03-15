package com.ebenezer.gana.shoppyv2.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    var id:Long = 0L,
    val order_status:String = "",
    val total_price: Float,
    val user_id:String = "",
    val payment_method:String = "",
    val order_date:String = "",
):Parcelable
package com.ebenezer.gana.shoppyv2.firestore

import android.app.Activity
import android.content.Context
import android.util.Log
import com.ebenezer.gana.shoppyv2.models.*
import com.ebenezer.gana.shoppyv2.ui.activities.*
import com.ebenezer.gana.shoppyv2.ui.fragments.DashboardFragment
import com.ebenezer.gana.shoppyv2.ui.fragments.OrdersFragment
import com.ebenezer.gana.shoppyv2.utils.Constants

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import okhttp3.*
import java.io.IOException
import java.lang.reflect.Type


/**
 * A custom class where we will add the operation performed for the FireStore database.
 */
class FirestoreClass {

    var listproduct: ArrayList<Product> = ArrayList()
    fun getCartList(activity: Activity) {

        var cartList: ArrayList<Cart> = ArrayList()
        val client = OkHttpClient()
        val moshi = Moshi.Builder().build()
        val usersType: Type = Types.newParameterizedType(
            MutableList::class.java,
            Cart::class.java
        )
        val jsonAdapter: JsonAdapter<ArrayList<Cart>> =
            moshi.adapter<kotlin.collections.ArrayList<Cart>>(usersType)


        // Tạo request lên server.
        val request: Request = Request.Builder()
            .url("http://10.0.2.2:8000/cart/")
            .build()


        Log.e("Request ", request.toString())
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("Error Network Error", e.toString())
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call?, response: Response) {

                // Lấy thông tin JSON trả về. Bạn có thể log lại biến json này để xem nó như thế nào.
                val json: String = response.body()!!.string()
                val users: kotlin.collections.ArrayList<Cart>? = jsonAdapter.fromJson(json)

                cartList = ArrayList<Cart>(users)
                Log.e("asssssssssss1", json.toString())
                Log.e("asssssssssss1", cartList.toString())
                when (activity) {
                    is CartListActivity -> {
                        activity.successCartItemsList(cartList)
                    }
                }
            }
        })

    }

    fun removedItemFromCart(context: Context, cart_id: String) {

    }
    var s : String ="["
    fun createNewOrder(cart : ArrayList<Cart>,tt : Double) {
        val client = OkHttpClient()
        Log.e("createNewOrder: ", cart.toString() )

        for (i in cart) {
            s+=i.product.productId
            s+=","
        }
        s=s.dropLast(1)
        s+="]"

        val json = "{\"product_ids\": "+s+",\"user_id\": \"1\",\"payment_method\": \"momo\",\"total_price\": ${tt}}"
        Log.e("createNewOrder: ", json )
        val body = RequestBody.create(
            MediaType.parse("application/json"), json
        )
        val request: Request = Request.Builder()
            .url("http://10.0.2.2:8000/order/create")
            .post(body)
            .build()
        Thread {
            val call = client.newCall(request)
            val response = call.execute()
            response.code()
            Log.e("order", response.toString() )
        }.start()

    }

    fun addCartItems(activity: ProductDetailsActivity, addToCart: Cart) {

        val client = OkHttpClient()
        val json = "{\"id\":${addToCart.product.productId}}"
        val body = RequestBody.create(
            MediaType.parse("application/json"), json
        )
        val request: Request = Request.Builder()
            .url("http://10.0.2.2:8000/cart/add")
            .post(body)
            .build()
        Thread {
            val call = client.newCall(request)
            val response = call.execute()
            response.code()
            Log.e("ddddddd", response.toString() )
        }.start()
        activity.addToCartSuccess()
    }

    fun getProductDetails(activity: ProductDetailsActivity, product: Product) {

        activity.productDetailsSuccess(product)

    }

    fun checkIfItemExistsInCart(activity: ProductDetailsActivity, productId: String) {

    }

    fun getAllProductsList(activity: Activity) {
        when (activity) {
            is CartListActivity -> {
                activity.successProductsListFromFireStore(listproduct)
            }
        }
    }
    var ordersList: ArrayList<Order> = ArrayList()
    fun getMyOrdersList(fragment: OrdersFragment) {
        val client = OkHttpClient()
        val moshi = Moshi.Builder().build()
        val usersType: Type = Types.newParameterizedType(
            MutableList::class.java,
            Order::class.java
        )
        val jsonAdapter: JsonAdapter<ArrayList<Order>> =
            moshi.adapter<kotlin.collections.ArrayList<Order>>(usersType)


        // Tạo request lên server.
        val request: Request = Request.Builder()
            .url("http://10.0.2.2:8000/order/")
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("Error Network Error", e.toString())
            }

            override fun onResponse(call: Call?, response: Response) {
                // Lấy thông tin JSON trả về. Bạn có thể log lại biến json này để xem nó như thế nào.
                val json: String = response.body()!!.string()
                val users: kotlin.collections.ArrayList<Order>? = jsonAdapter.fromJson(json)
                ordersList = ArrayList(users)

                Log.e("asssssssssss1", json.toString())
                Log.e("asssssssssss2", ordersList.toString())
                fragment.populateOrdersListInUI(ordersList)

            }
        })



    }




    fun deleteAllOrders(fragment: OrdersFragment, userId: String) {


    }

    fun getDashboardItemsList(fragment: DashboardFragment) {

        val client = OkHttpClient()
        val moshi = Moshi.Builder().build()
        val usersType: Type = Types.newParameterizedType(
            MutableList::class.java,
            Product::class.java
        )
        val jsonAdapter: JsonAdapter<List<Product>> =
            moshi.adapter<kotlin.collections.List<Product>>(usersType)


        // Tạo request lên server.
        val request: Request = Request.Builder()
            .url("http://10.0.2.2:8000/product/")
            .build()

        Log.e("Request ", request.toString())
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("Error Network Error", e.toString())
            }


            override fun onResponse(call: Call?, response: Response) {

                // Lấy thông tin JSON trả về. Bạn có thể log lại biến json này để xem nó như thế nào.
                val json: String = response.body()!!.string()
                val users: kotlin.collections.List<Product>? = jsonAdapter.fromJson(json)

                listproduct = ArrayList<Product>(users)
                Log.e("asssssssssss1", listproduct.toString())
                fragment.successDashboardItemsList(listproduct)
            }
        })




    }

}
package com.ebenezer.gana.shoppyv2.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import com.ebenezer.gana.shoppyv2.models.*
import com.ebenezer.gana.shoppyv2.ui.activities.*
import com.ebenezer.gana.shoppyv2.ui.fragments.DashboardFragment
import com.ebenezer.gana.shoppyv2.ui.fragments.OrdersFragment
import com.ebenezer.gana.shoppyv2.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
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

    // Access a Cloud Firestore instance.
    private val mFirestore = FirebaseFirestore.getInstance()



    fun getCartList(activity: Activity) {


        mFirestore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, "1")
            .get()
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, it.documents.toString())
                val cartList: ArrayList<CartItem> = ArrayList()

                for (items in it.documents) {
                    val cartItem = items.toObject(CartItem::class.java)!!
                    cartItem.id = items.id

                    cartList.add(cartItem)

                }

                when (activity) {
                    is CartListActivity -> {
                        activity.successCartItemsList(cartList)

                    }

                }

            }
            .addOnFailureListener {
                when (activity) {
                    is CartListActivity -> {
                        activity.hideProgressDialog()

                    }

                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while trying to get cart list from firestore",
                    it
                )
            }
    }

    fun removedItemFromCart(context: Context, cart_id: String) {
        mFirestore.collection(Constants.CART_ITEMS)
            .document(cart_id)
            .delete()
            .addOnSuccessListener {
                when (context) {
                    is CartListActivity -> {
                        context.itemRemovedSuccess()
                    }
                }
            }
            .addOnFailureListener {

                when (context) {
                    is CartListActivity -> {
                        context.hideProgressDialog()
                    }
                }
                Log.e(
                    context.javaClass.simpleName,
                    "Error while removing item from the cart list",
                    it
                )

            }
    }

    fun updateMyCart(context: Context, cart_id: String, itemHashMap: HashMap<String, Any>) {
        mFirestore.collection(Constants.CART_ITEMS)
            .document(cart_id)
            .update(itemHashMap)
            .addOnSuccessListener {
                when (context) {
                    is CartListActivity -> {
                        context.itemUpdateSuccess()
                    }
                }

            }
            .addOnFailureListener {
                when (context) {
                    is CartListActivity -> {
                        context.hideProgressDialog()
                    }
                }
                Log.e(
                    context.javaClass.simpleName,
                    "Error while updating item from the cart list",
                    it
                )

            }


    }

    fun addCartItems(activity: ProductDetailsActivity, addToCart: CartItem) {
        mFirestore.collection(Constants.CART_ITEMS)
            .document(Constants.USER_ID+"1"+Constants.PRODUCT_ID+ addToCart.product_id)
            .set(addToCart, SetOptions.merge())
            .addOnSuccessListener {
                activity.addToCartSuccess()
            }

            .addOnFailureListener {
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while deleting the product",
                    it
                )
            }

    }

    fun getProductDetails(activity: ProductDetailsActivity, product: Products) {

                    activity.productDetailsSuccess(product)

    }

    fun checkIfItemExistsInCart(activity: ProductDetailsActivity, productId: String) {
        mFirestore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, "1")
            .whereEqualTo(Constants.PRODUCT_ID, productId)
            .get()
            .addOnSuccessListener {
                if (it.documents.size > 0) {
                    activity.productExistInCart()
                } else {
                    activity.hideProgressDialog()
                }

            }
            .addOnFailureListener {
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while checking if the item exist",
                    it
                )

            }
    }

    fun getAllProductsList(activity: Activity) {
        mFirestore.collection(Constants.PRODUCTS)
            .get()
            .addOnSuccessListener {
                Log.e("Products List", it.documents.toString())
                val allProductsList = ArrayList<Products>()
                for (items in it.documents) {
                    val product = items.toObject(Products::class.java)!!
                    product.productId = items.id

                    allProductsList.add(product)
                }
                when (activity) {
                    is CartListActivity -> {
                        activity.successProductsListFromFireStore(allProductsList)

                    }
                }


            }
            .addOnFailureListener {
                when (activity) {
                    is CartListActivity -> {
                        activity.hideProgressDialog()

                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the user details",
                    it
                )
            }
    }

    fun getMyOrdersList(fragment: OrdersFragment) {
        mFirestore.collection(Constants.ORDERS)
            .whereEqualTo(Constants.USER_ID, "")
            .get()
            .addOnSuccessListener {
                val ordersList: ArrayList<Order> = ArrayList()


                for (items in it.documents) {
                    val orderItem = items.toObject(Order::class.java)!!
                    orderItem.id = items.id
                    ordersList.add(orderItem)
                }

                fragment.populateOrdersListInUI(ordersList)

            }
            .addOnFailureListener {
                fragment.hideProgressDialog()
                Log.e(
                    fragment.javaClass.simpleName,
                    "Error while placing an order", it
                )
            }


    }

    fun deleteAllOrders(fragment: OrdersFragment, userId: String) {
        mFirestore.collection(Constants.ORDERS)
            .document(userId)
            .delete()
            .addOnSuccessListener {
                fragment.successDeleteAllOrders()
            }
            .addOnFailureListener {
                fragment.hideProgressDialog()
                Log.e(
                    fragment.javaClass.simpleName,
                    "Error while deleting all orders", it
                )
            }

    }


    fun getDashboardItemsList(fragment: DashboardFragment) {
        var listproduct : ArrayList<Products> = ArrayList()
        val client = OkHttpClient()
        val moshi = Moshi.Builder().build()
        val usersType: Type = Types.newParameterizedType(
            MutableList::class.java,
            Products::class.java
        )
        val jsonAdapter: JsonAdapter<List<Products>> =
            moshi.adapter<kotlin.collections.List<Products>>(usersType)


        // Tạo request lên server.
        val request: Request = Request.Builder()
            .url("http://10.0.2.2:8000/product/")
            .build()


        Log.e("Request " , request.toString() )
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("Error Network Error", e.toString())
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call?, response: Response) {

                // Lấy thông tin JSON trả về. Bạn có thể log lại biến json này để xem nó như thế nào.
                val json: String = response.body()!!.string()
                val users: kotlin.collections.List<Products>? = jsonAdapter.fromJson(json)

                listproduct = ArrayList<Products>(users)
                Log.e("asssssssssss1", listproduct.toString() )
                fragment.successDashboardItemsList(listproduct)
            }
        })




    }

}
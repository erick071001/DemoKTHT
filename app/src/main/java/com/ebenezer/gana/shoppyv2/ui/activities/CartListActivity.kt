package com.ebenezer.gana.shoppyv2.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.ebenezer.gana.shoppyv2.R
import com.ebenezer.gana.shoppyv2.databinding.ActivityCartListBinding
import com.ebenezer.gana.shoppyv2.firestore.FirestoreClass
import com.ebenezer.gana.shoppyv2.models.Cart
import com.ebenezer.gana.shoppyv2.models.Product
import com.ebenezer.gana.shoppyv2.ui.adapters.CartListAdapter
import com.ebenezer.gana.shoppyv2.utils.Constants
import java.util.stream.IntStream.range

class CartListActivity : BaseActivity() {

    private var mProductList: ArrayList<Product> = ArrayList<Product>()
    private var mCartListItems: ArrayList<Cart> = ArrayList<Cart>()
    private var cartListItems: ArrayList<Cart> = ArrayList<Cart>()

    lateinit var binding: ActivityCartListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_cart_list)
        binding = ActivityCartListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupActionBar()
        binding.btnCheckout.setOnClickListener {
            FirestoreClass().createNewOrder(cartListItems,total)
            onBackPressed()

        }

    }


    override fun onResume() {
        super.onResume()
        getCartItemsList()
        getProductList()
    }

    fun itemUpdateSuccess() {
        getCartItemsList()
    }


    fun itemRemovedSuccess() {
        Toast.makeText(
            this@CartListActivity,
            resources.getString(R.string.msg_item_removed_successfully),
            Toast.LENGTH_SHORT
        ).show()

        getCartItemsList()

    }



    private fun getCartItemsList() {
        //show progress dialog, hide when successCartItemList is called
        //showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getCartList(this@CartListActivity)



    }


    private fun getProductList() {
        //show progress dialog, hide when successCartItemList is called
        FirestoreClass().getAllProductsList(this@CartListActivity)
    }

    fun successProductsListFromFireStore(productList: ArrayList<Product>) {
        mProductList = productList



    }
    var total : Double = 0.0
    fun successCartItemsList(cartList: ArrayList<Cart>) {
        //hide progress dialog, shown when getCartItemsList is called



        for (product in mProductList) {
            for (cartItem in cartList) {
                if (product.productId == cartItem.item.productID.toString()) {

                    cartItem.item.quantity= product.totalQuantity.toLong()
                    if (product.totalQuantity.toInt() == 0) {
                        cartItem.item.quantity = product.totalQuantity.toLong()
                    }
                }

            }
        }

        Log.e("sssasasa", cartList.toString() )
        mCartListItems = cartList
        cartListItems = ArrayList(cartList)
        for (i in 0..mCartListItems.size-1) {
            for (j in mCartListItems.size-1 downTo i+1) {
                if (mCartListItems[i].item.productID == mCartListItems[j].item.productID ){
                    mCartListItems[i].item.quantity+=1
                    mCartListItems.removeAt(j)
                }
            }
        }
        Log.e("TAG", mCartListItems.toString() )

        Handler(Looper.getMainLooper()).post {
            if (mCartListItems.size > 0) {
                binding.rvCartItemsList.visibility = View.VISIBLE
                binding.tvNoCartItemFound.visibility = View.GONE
                binding.llCheckout.visibility = View.VISIBLE

                with(binding.rvCartItemsList) {
                    layoutManager = LinearLayoutManager(this@CartListActivity)
                    setHasFixedSize(true)
                    val cartListAdapter = CartListAdapter(
                        this@CartListActivity,
                        cartListItems = mCartListItems, true
                    )
                    adapter = cartListAdapter
                }


                var subTotal: Double = 0.0
                var shippingCharge = 0

                for (item in mCartListItems) {
                    val availableQuantity = item.product.totalQuantity.toInt()
                    if (availableQuantity > 0) {
                        val price = (item.product.price.toDouble() * item.item.quantity).toDouble()
                        val quantity = item.item.quantity.toInt()
                        shippingCharge = 15
                        subTotal += (price)
                    }

                }

                binding.tvSubTotal.text = "$subTotal$"
                binding.tvShippingCharge.text = "$shippingCharge$"

                if (subTotal > 0) {
                    binding.llCheckout.visibility = View.VISIBLE
                    total = subTotal + shippingCharge

                    binding.tvTotalAmount.text = "$total$"
                } else {

                    binding.llCheckout.visibility = View.GONE


                }

            } else {
                binding.rvCartItemsList.visibility = View.GONE
                binding.tvNoCartItemFound.visibility = View.VISIBLE
                binding.llCheckout.visibility = View.GONE

            }
        }




    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarCartListActivity)
        val actionbar = supportActionBar
        actionbar?.let {
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarCartListActivity.setNavigationOnClickListener { finish() }


    }
}
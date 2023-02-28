package com.ebenezer.gana.shoppyv2.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.ebenezer.gana.shoppyv2.R
import com.ebenezer.gana.shoppyv2.databinding.ActivityCartListBinding
import com.ebenezer.gana.shoppyv2.firestore.FirestoreClass
import com.ebenezer.gana.shoppyv2.models.CartItem
import com.ebenezer.gana.shoppyv2.models.Products
import com.ebenezer.gana.shoppyv2.ui.adapters.CartListAdapter
import com.ebenezer.gana.shoppyv2.utils.Constants

class CartListActivity : BaseActivity() {

    private lateinit var mProductsList: ArrayList<Products>
    private lateinit var mCartListItems: ArrayList<CartItem>


    lateinit var binding: ActivityCartListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_cart_list)
        binding = ActivityCartListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupActionBar()


    }


    override fun onResume() {
        super.onResume()
        //getCartItemsList()
        getProductList()
    }

    fun itemUpdateSuccess() {
        hideProgressDialog()
        getCartItemsList()
    }


    fun itemRemovedSuccess() {
        hideProgressDialog()
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
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAllProductsList(this@CartListActivity)
    }

    fun successProductsListFromFireStore(productsList: ArrayList<Products>) {
        hideProgressDialog()
        mProductsList = productsList
        getCartItemsList()


    }

    fun successCartItemsList(cartList: ArrayList<CartItem>) {
        //hide progress dialog, shown when getCartItemsList is called

        hideProgressDialog()

        for (product in mProductsList) {
            for (cartItem in cartList) {
                if (product.productId == cartItem.product_id) {

                    cartItem.stock_quantity = product.totalQuantity

                    if (product.totalQuantity.toInt() == 0) {
                        cartItem.cart_quantity = product.totalQuantity
                    }
                }

            }
        }

        mCartListItems = cartList

        /*for (items in cartList){
            Log.i("Cart Item Title", items.title)
        }*/

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
                val availableQuantity = item.stock_quantity.toInt()
                if (availableQuantity > 0) {
                    val price = item.price.toDouble()
                    val quantity = item.cart_quantity.toInt()
                    shippingCharge = item.product_shipping_charge.toInt()
                    subTotal += (price * quantity)
                }

            }

            binding.tvSubTotal.text = "$subTotal$"
            binding.tvShippingCharge.text = "$shippingCharge$"

            if (subTotal > 0) {
                binding.llCheckout.visibility = View.VISIBLE
                val total = subTotal + shippingCharge

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

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarCartListActivity)
        val actionbar = supportActionBar
        actionbar?.let {
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarCartListActivity.setNavigationOnClickListener { onBackPressed() }

    }
}
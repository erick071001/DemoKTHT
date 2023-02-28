package com.ebenezer.gana.shoppyv2.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.ebenezer.gana.shoppyv2.R
import com.ebenezer.gana.shoppyv2.databinding.ActivityProductDetailsBinding
import com.ebenezer.gana.shoppyv2.firestore.FirestoreClass
import com.ebenezer.gana.shoppyv2.models.CartItem
import com.ebenezer.gana.shoppyv2.models.Products
import com.ebenezer.gana.shoppyv2.utils.Constants
import com.ebenezer.gana.shoppyv2.utils.GlideLoader

class ProductDetailsActivity : BaseActivity(), View.OnClickListener {
    lateinit var binding: ActivityProductDetailsBinding

    private var mProductId: String = "1"

    private lateinit var mProductDetails: Products
    private var mProductOwnerId: String = "1"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_product_details)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()

        mProductDetails = intent.getParcelableExtra<Products>("Product")!!


        // Now we have the product owner id so if the product which is added by owner himself should not see the button Add To Cart.
        binding.btnAddToCart.visibility = View.VISIBLE

        getProductDetails()
        binding.btnAddToCart.setOnClickListener(this)
        binding.btnGoToCart.setOnClickListener(this)
    }

    private fun getProductDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getProductDetails(this, mProductDetails)
    }

    fun productExistInCart() {
        hideProgressDialog()
        // if the product already exist in the cart, hide the add to cart button
        // and make go to cart button visible
        binding.btnAddToCart.visibility = View.GONE
        binding.btnGoToCart.visibility = View.VISIBLE

    }


    fun productDetailsSuccess(product: Products) {
        mProductDetails = product
        //hideProgressDialog()
        GlideLoader(this@ProductDetailsActivity).loadProductPicture(
            product.imageURL,
            binding.ivProductDetailImage
        )
        binding.tvProductDetailsTitle.text = product.product_name
        binding.tvProductDetailsPrice.text = "${product.price}$"
        binding.tvProductDetailsDescription.text = product.description
        binding.tvProductDetailsAvailableQuantity.text = product.totalQuantity
        binding.tvProductDetailsShippingCharge.text = "15$"

        if (product.totalQuantity.toInt() == 0) {
            hideProgressDialog()
            binding.btnAddToCart.visibility = View.GONE
            binding.tvProductDetailsAvailableQuantity.setTextColor(
                ContextCompat.getColor(
                    this@ProductDetailsActivity,
                    R.color.colorSnackBarError
                )

            )
            binding.tvProductDetailsAvailableQuantity.text =
                resources.getString(R.string.lb_out_of_stock)


        } else {


            FirestoreClass().checkIfItemExistsInCart(this, mProductId)

        }


    }


    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarProductDetailsActivity)
        val actionbar = supportActionBar
        actionbar?.let {
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarProductDetailsActivity.setNavigationOnClickListener { onBackPressed() }

    }

    /**
     * A function to prepare the cart item to add it to the cart in cloud firestore.
     */

    private fun addToCart() {
        val cartItem = CartItem(
            "1",
            mProductOwnerId,
            mProductDetails.productId,
            mProductDetails.product_name,
            mProductDetails.price,
            mProductDetails.imageURL,
            Constants.DEFAULT_CART_QUANTITY,
            mProductDetails.totalQuantity, "1", "15"
        )

        //show progress dialog
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addCartItems(this, cartItem)
    }

    //  Notify the success result of item added to the to cart.
    fun addToCartSuccess() {
        //hide progress dialog, first show when addToCart() is called
        hideProgressDialog()
        Toast.makeText(
            this@ProductDetailsActivity,
            resources.getString(R.string.success_message_item_added_to_cart),
            Toast.LENGTH_SHORT
        ).show()

        // After adding to cart, make the button invisible and
        // make go to cart button visible
        binding.btnAddToCart.visibility = View.GONE
        binding.btnGoToCart.visibility = View.VISIBLE


    }


    override fun onClick(v: View?) {
        v.let {
            when (it!!.id) {
                R.id.btn_add_to_cart -> {
                    addToCart()
                }

                R.id.btn_go_to_cart -> {
                    startActivity(Intent(this@ProductDetailsActivity, CartListActivity::class.java))
                }
            }
        }
    }
}


